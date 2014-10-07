package com.weibocrawl.modle;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.weibocrawl.utils.DateUtil;

public class MemoryOfTaskBean implements Serializable {
	public final static int BEING_CRAWL = 0; //正在或将要被抓取
	public final static int HAD_CRAWLED = 1; //成功抓取
	public final static int FAIL_CRAWLED =2; //失败抓取
	private static final long serialVersionUID = 1L;
	private int id;
	private int crawlMaxPageSize;//本轮搜索最大的翻页数（=总记录数/每页条数）
	private Date lastUpdateTime; //记录本轮开始时间
	//private int nowPage;//当前第几页，防止多线程重复抓取
	private String firstPageFirstWbTime;//第一页第一条微博时间(暂时不用)
	private int allWeiboCount;//累积抓取的微博数
	//private int retryCount;//该任务重试次数(暂时不用)
	//已经抓取过的url（主要有时间参数和页码参数生成，值为-0表示正在抓取为1表示成功抓取，为2表示失败）
	private Map<String,Integer> hadCrawledMap = new HashMap<String,Integer>();

	@Override
	public String toString(){
		return this.id+","+this.lastUpdateTime+","+this.hadCrawledMap.size();
	}
	public int getCrawlMaxPageSize() {
		return crawlMaxPageSize;
	}
	public void setCrawlMaxPageSize(int crawlMaxPageSize) {
		this.crawlMaxPageSize = crawlMaxPageSize;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	public String getFirstPageFirstWbTime() {
		return firstPageFirstWbTime;
	}
	public void setFirstPageFirstWbTime(String firstPageFirstWbTime) {
		if(firstPageFirstWbTime!=null)
		{
			this.firstPageFirstWbTime = firstPageFirstWbTime;
		}
		else{
		this.firstPageFirstWbTime =DateUtil.formatDateToString( new Date());
		}
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAllWeiboCount() {
		return allWeiboCount;
	}
	public void setAllWeiboCount(int allWeiboCount) {
		this.allWeiboCount = allWeiboCount;
	}
	public Map<String, Integer> getHadCrawledMap() {
		return hadCrawledMap;
	}
	public void setHadCrawledMap(Map<String, Integer> hadCrawledMap) {
		this.hadCrawledMap = hadCrawledMap;
	}
}
