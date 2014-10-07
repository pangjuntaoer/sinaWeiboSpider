package com.swust.extractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import com.swust.app.RunAPP;
import com.swust.dao.DbDao;
import com.swust.httputil.QHttpClient;
import com.swust.model.Item;
import com.swust.model.UserItem;
import com.swust.queue.CandidateURL;

public class SimpleExtractor extends AbstractExtractor {
private static Log log = LogFactory.getLog(SimpleExtractor.class);
private static int URLCANDIDAT_QUEUE_SIZE  = 60;
private static String ITEM_REGEX="http://www.dianping.com/search/category/8/10/.*";
private static String USER_ITEM_REGEX="http://www.dianping.com/shop/(\\d+)/review_all\\?.*";
private static String ITEM_COMMOMENT="口味(.*)&nbsp;&nbsp;环境(.*)&nbsp;&nbsp;服务(.*)";
private static String BASE_ITEM_PAGE="http://www.dianping.com";
static Pattern item_Pattern = Pattern.compile(ITEM_REGEX);
static Pattern UserItem_Pattern = Pattern.compile(USER_ITEM_REGEX);
static Pattern item_Comment_Pattern = Pattern.compile(ITEM_COMMOMENT);
private DbDao dbdao ;
	public SimpleExtractor(String baseUrl,CandidateURL url,DbDao dbdao) {
		super(baseUrl,url);
		this.dbdao = dbdao;
	}
	public SimpleExtractor(String baseUrl,String html) {
		super(baseUrl,html);
	}
	public SimpleExtractor(File file,String baseurl) throws IOException {
		super(file,baseurl);
	}
	public SimpleExtractor(String html) {
		super(html);
	}
	@Override
	public Set<String> extractContent() {
		log.info("extracting the url:"+url.getUrl());
		Set<String> candidateUrlList= new HashSet<String>(URLCANDIDAT_QUEUE_SIZE);
		Matcher itemMt = item_Pattern.matcher(url.getUrl());
		Matcher userItemMt = UserItem_Pattern.matcher(url.getUrl());		
		if(itemMt.find()){//如果是item列表页面
			candidateUrlList = processItemPage(candidateUrlList);
		}else if(userItemMt.find()){//item评论页面
			String itemId = userItemMt.group(1);
			candidateUrlList = processUserItemPage(itemId,candidateUrlList);
		}
		return candidateUrlList;
	}

	private Set<String> processUserItemPage(String itemId,Set<String> candidateUrlList) {
		Element comonDiv = doc.body().getElementsByClass("comment-mode").first();
		Element div = comonDiv.select(".comment-list").first();
		Elements comments = div.
				child(0).children();
		List<UserItem> list = new ArrayList<UserItem>(comments.size());
		for (int i = 0; i < comments.size(); i++) {
			Element e = comments.get(i);
			if(e.tagName().equals("li")){
				try {
					UserItem userItem = extractUserItemInfo(e);
					userItem.setItemId(itemId);
					list.add(userItem);
				} catch (Exception e2) {
					e2.printStackTrace();
					log.error(e.html());
				}
			}
		}
		try {
			if(list.size()>0)
			dbdao.insertIntoUserItem(list);
		} catch (Exception e) {
			e.printStackTrace();
			RunAPP.appendErrorContent(list.toString());
		}
		// 评分页面的构造的分页抽取
		Elements pages = comonDiv.select(".PageLink");
		this.itemPageOutLinkExtract(itemId, pages, candidateUrlList);
		return candidateUrlList;
	}
private void itemPageOutLinkExtract(String itemId,Elements pages,Set<String> candidateUrlList){
	for (int i = 0; i < pages.size(); i++) {
		Element ue = pages.get(i);
		String href = ue.attr("href");
		if(href.contains("?pageno")){
			candidateUrlList.add("http://www.dianping.com/shop/"+
					itemId+"/review_all"+ue.attr("href"));
		}
	}
}
	private UserItem extractUserItemInfo(Element e) {
		UserItem userItem= new UserItem();
		//li->pic->a[a]
		Element pic = e.child(0).child(0);
		userItem.setUserId(pic.attr("user-id"));
		//
		Element user_info = e.child(1).child(0);
		String starStr = user_info.child(0).attr("class");
		userItem.setRating(starStr.substring(starStr.length()-2, starStr.length()-1));
		Elements commentRst=user_info.select(".comment-rst").select(".rst");
		String tastStr = commentRst.get(0).ownText();
		String eveStr = commentRst.get(1).ownText();
		String servStr = commentRst.get(2).ownText();
		userItem.setTast(tastStr.substring(tastStr.length()-1));
		userItem.setEnvironment(eveStr.substring(eveStr.length()-1));
		userItem.setService(servStr.substring(servStr.length()-1));
		
		//common_text
		String commen_txt = e.select(".J_brief-cont").first().text();
		userItem.setReview(commen_txt);
		//time
		userItem.setTimes(e.select(".misc-info").get(0).child(0).text());
		return userItem;
	}
	
	private Set<String> processItemPage(Set<String> candidateUrlList) {
		Elements div = doc.body().select("#sortBar");
		Elements itemList = div.get(0).child(0).child(1).children(); //getElementsByClass("content");
		System.out.println("共有条目："+itemList.size());
		List<Item> list = new ArrayList<Item>(itemList.size());
		for (int i = 0; i < itemList.size(); i++) {
			Element e = itemList.get(i);
			if(e.tagName().equals("li")){
				try {
					Item item = extractItemInfo(e);
					list.add(item);
					//构造评分页码
					candidateUrlList.add("http://www.dianping.com/shop/"+item.getId()+"/review_all?pageno=1");
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		try {
			if(list.size()>0)
			dbdao.insertIntoItem(list);
		} catch (Exception e) {
			e.printStackTrace();
			RunAPP.appendErrorContent(list.toString());
		}
		Element page = div.select(".page").first();
		this.userItemPageOutLinkExtract(page, candidateUrlList);
		//导航分类抽取
		Element nav = doc.body().select(".nav").first();
		Elements navUrl=nav.select("a[href]");
		for (int i = 0; i < navUrl.size(); i++) {
			Element ue = navUrl.get(i);
			if(ue.attr("href").contains("/search/category/8/10")){
				candidateUrlList.add(BASE_ITEM_PAGE+ue.attr("href"));
			}
		}
		return candidateUrlList;
	}

	public void userItemPageOutLinkExtract(Element page,Set<String> candidateUrlList){
		///分页抽取
				Elements urlsEle = page.select("a[href]");
				for (int i = 0; i < urlsEle.size(); i++) {
					Element ue = urlsEle.get(i);
					if(ue.attr("href").contains("/search/category/8/10")){
						candidateUrlList.add(BASE_ITEM_PAGE+ue.attr("href"));
					}
				}
	}
	private Item extractItemInfo(Element e) {
		Item item = new Item();
		String itemUrl = e.child(0).attr("href");
		item.setId(itemUrl.substring(6));
		Element info = e.child(1);
		item.setName(info.child(0).child(0).child(0).text());
		Element remark = info.child(1);
		String starStr = remark.child(0).attr("class");
		item.setStar(starStr.substring(starStr.length()-2, starStr.length()-1));
		String reviewCountStr = remark.child(1).text(); 
		item.setReviewCount(reviewCountStr.substring(0, reviewCountStr.indexOf("封")));
		Element comment = info.child(2);
		item.setCost(comment.child(0).child(0).text().trim().replace("￥", ""));
		String commenList = comment.child(1).text();
		String [] str = commenList.split("\\s");
		item.setTast(str[0].substring(2));
		item.setEnvironment(str[1].substring(2));
		item.setService(str[2].substring(2));
		return item;
	}
}
