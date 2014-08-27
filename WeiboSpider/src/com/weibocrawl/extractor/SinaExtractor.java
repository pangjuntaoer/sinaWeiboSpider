package com.weibocrawl.extractor;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.weibo.controller.SynTaskController;
import com.weibocrawl.memory.TaskOfMemory;
import com.weibocrawl.memory.TaskQueue;
import com.weibocrawl.modle.CrawResult;
import com.weibocrawl.modle.MemoryOfTaskBean;
import com.weibocrawl.modle.TaskBean;
import com.weibocrawl.modle.WeiboBean;
import com.weibocrawl.service.TaskDBService;
import com.weibocrawl.utils.DateUtil;
import com.weibocrawl.writter.Writter;

public class SinaExtractor implements Extractor{
	public static final Logger logger = LoggerFactory.getLogger(TaskQueue.class);	
	final static int MAX_TURN_PAGE = 50;
	private TaskDBService taskDBservice;
	public void setTaskDBservice(TaskDBService taskDBservice) {
		this.taskDBservice = taskDBservice;
	}
	private Writter writter; //持久化模块
	public void setWritter(Writter writter) {
		this.writter = writter;
	}
	private final static String blockRegex = "<script>STK\\s&&\\sSTK\\.pageletM\\s&&\\sSTK\\.pageletM\\.view\\(.*\\)";
	private final static String NO_RESULT_CLASS_NAME = "pl_noresult";
	private final static String PAGE_TURN_CLASS_NAME="search_page clearfix"; //分页class
	private final static String CONTENT_KEY_NAME="node-type";
	private static Pattern pattern = Pattern.compile(blockRegex);
	private SynTaskController synTaskControll;
	public void setSynTaskControll(SynTaskController synTaskControll) {
		this.synTaskControll = synTaskControll;
	}
	public String isShouleExtract(String source, CrawResult content) {
		if(source==null ||source==""){
			return "null";
		}
		// 匹配文本块
		Matcher m = null;
		try{
		 m = pattern.matcher(source);
		}catch(Exception e){
			e.printStackTrace();
			return "no";
		}
		boolean hasGetContent = false;
		boolean hasGetCount = false;
		while(m.find()){
			String jsonStr = m.group();
			jsonStr = jsonStr.substring(jsonStr.indexOf("{"), jsonStr.lastIndexOf(")"));
			// 解析json,转换为实体类
			SinaWeiboBlock block = JSON.parseObject(jsonStr, SinaWeiboBlock.class);
			if(block.getPid().equals("pl_weibo_feedlist")){
				 //没有搜索到结果
				if(block.getHtml().startsWith(" <div class=\"pl_noresult\">")){
					logger.info("SinaExtractor isShouleExtract- Can't search result!begin to end this task!");
					return "end";
				}
				Document doc = Jsoup.parse(block.getHtml());
				Elements searchPage = doc.getElementsByClass(PAGE_TURN_CLASS_NAME);
				if(searchPage!=null){ //出现了分页（数据正确）
					content.setResultHtml(doc); //正文
					hasGetContent = true;
				}else{
					content.setResultHtml(doc); //正文 (抓取只有一页的数据，此情况没有分页)
					hasGetContent = false;
				}			
			}
			if(block.getPid().equals("pl_common_totalshow")){
				Pattern pt = Pattern.compile(".*找到 (.*) 条结果.*");
				Matcher mt = pt.matcher(block.getHtml());
				if(mt.find()){
					String t = mt.group(1).replace(",", "");
					long s = Long.valueOf(t);
					if(s==0){
						return "end";
					}
					content.setResultCount(s);
					hasGetCount = true;
					if(s<=20){//只有一页，则也算结束
						hasGetContent = true;
					}
				}
			}
			if(block.getPid().equals("pl_common_sassfilter")){
				logger.error(" SinaExtractor isShouleExtract- Cookie失效，需要输入验证码了");
				return "wrong";
			}
			if(hasGetContent&&hasGetCount){
				logger.info("SinaExtractor isShouleExtract- this whell crawl,totalCount:"+content.getResultCount()+";totalPage:"+
						content.getTotalPage());
				return "yes";
			}
		}
		return "no";
	}
	/**
	 * 微博抽取
	 */
	public List<WeiboBean> innerExtractor(CrawResult content, TaskBean task) {
		MemoryOfTaskBean taskMemory = TaskOfMemory.memoryTask.get(task.getTaskId());
		Elements  childs = content.getResultHtml().getElementsByClass("feed_list");
		Iterator<Element> it = childs.iterator();
		List<String []> weiboArrayList = new ArrayList<String[]>();
		int i = 0;
		while(it.hasNext()){
			i ++;
			WeiboBean weibo = new WeiboBean();
			Element node = it.next();
			weibo.setWeiboId(node.attr("mid")); //微博id
			Element face_node = node.child(0); 
			extractFace(face_node,weibo); //作者相关
			Element content_node = node.child(1);
			extractContent(content_node,weibo); //微博正文
			
			weibo.setBookId(task.getTaskId()); //任务id
			weibo.setBookTitle(task.getKeywordStr());
			if(taskMemory!=null&&task.getNextPage()==1&&i==1){ //本轮第一页则记录第一条微博时间
				taskMemory.setFirstPageFirstWbTime(weibo.getWeiboTime());
			}
			if(task.getFirstWbTime()!=null){//当前微博时间与上一轮第一页则记录第一条微博时间比较
				if(checkOverWeiboTime(weibo.getWeiboTime(),task.getFirstWbTime())){
					weiboArrayList.add(weibo.toArrayString());
				}else{//结束了
					synTaskControll.endOneTask(task, content.getTotalPage());
					break;
				}
			}
			weiboArrayList.add(weibo.toArrayString());
			//exceed50PageProcess(content,task,weibo);
		}
		writter.writterToDBbyPrepare(weiboArrayList);
		return null;
	}
/**
 * 微博时间比较	
 * @param weiboTime
 * @param firstWbTime
 * @return
 */
	private boolean checkOverWeiboTime(String weiboTime, Date firstWbTime) {
		Date weibo = DateUtil.fomatStringToDate(weiboTime);
		if(weibo!=null&&weibo.compareTo(firstWbTime)>0){
			return true;
		}
		return false;
	}
	/**
	 * 抽取微博作者信息
	 * @param face_node
	 * @param weibo
	 */
	private void extractFace(Element face_node, WeiboBean weibo) {
		Element a = face_node.child(0);
		weibo.setDomain(a.attr("href"));
		weibo.setUserName(a.attr("title"));
		weibo.setUserImage(a.child(0).attr("src"));
	}
	
	/**
	 * 抽取微博正文信息
	 * @param content_node
	 * @param weibo
	 */
	private void extractContent(Element content_node, WeiboBean weibo) {
		Element content = content_node.getElementsByAttribute(CONTENT_KEY_NAME).first();
		weibo.setContext(content.text());
		Element weiboInfo = content_node.getElementsByTag("p").get(1);
		Elements e = weiboInfo.children();
		for (int i = 0; i < e.size(); i++) {
			Element element = e.get(i);
			if(i==1){ //微博时间
				weibo.setWeiboTime(element.attr("title"));
				weibo.setWeiboUrl(element.attr("href"));
			}
			if(element.tagName().equals("span")){//赞，评论等信息
				weibo.setInnerHtml(element.text());
			}
			if(i==2){ //微博客户端
				weibo.setWeiboFrom(element.text());
			}
		}
	}

/**
 * 操过５０页处理
 * @param content
 * @param task
 * @param weibo
 */
	public void exceed50PageProcess(CrawResult content,TaskBean task,WeiboBean weibo){
		if(content.getTotalPage()<=MAX_TURN_PAGE){
			return;
		}
		if(task.getNextPage()==MAX_TURN_PAGE){
			try {
				taskDBservice.updateTimeParameters(task,weibo);
			} catch (Exception e) {
				logger.error("SinaExtractor exceed50PageProcess- 第50页最后一条微博时间处理错误");
				e.printStackTrace();
			}
		}
		return;
	}
}
