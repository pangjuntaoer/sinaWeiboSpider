package com.weibocrawl.modle;

import java.io.Serializable;
import java.util.Date;

import com.weibocrawl.utils.MD5Util;

public class TaskBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int taskId;
	private String keywordStr;
	private Date originTime;
	private int hop;
	private int loadStatus;
	private Date startTime;
	private Date endTime;
	private int nextPage;
	private Date lastCrawlTime;
	private Date lastWeiboTimeOfPage50;
	private int isEndEveryWheel;
	private Date tempTime;
	private Date firstWbTime;
	
	public Date getFirstWbTime() {
		return firstWbTime;
	}
	public void setFirstWbTime(Date firstWbTime) {
		this.firstWbTime = firstWbTime;
	}
	@Override
	public String toString(){
		return this.keywordStr+","+this.nextPage+","+this.startTime+","+this.endTime;
	}
	public String toMd5String(){
		String md5 = MD5Util.MD5(this.toString());
		System.out.println(this.toString()+":"+md5);
		return md5;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public String getKeywordStr() {
		return keywordStr;
	}
	public void setKeywordStr(String keywordStr) {
		this.keywordStr = keywordStr;
	}
	public Date getOriginTime() {
		return originTime;
	}
	public void setOriginTime(Date originTime) {
		this.originTime = originTime;
	}
	public int getHop() {
		return hop;
	}
	public void setHop(int hop) {
		this.hop = hop;
	}
	public int getLoadStatus() {
		return loadStatus;
	}
	public void setLoadStatus(int loadStatus) {
		this.loadStatus = loadStatus;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public int getNextPage() {
		return nextPage;
	}
	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}
	public Date getLastCrawlTime() {
		return lastCrawlTime;
	}
	public void setLastCrawlTime(Date lastCrawlTime) {
		this.lastCrawlTime = lastCrawlTime;
	}
	public Date getLastWeiboTimeOfPage50() {
		return lastWeiboTimeOfPage50;
	}
	public void setLastWeiboTimeOfPage50(Date lastWeiboTimeOfPage50) {
		this.lastWeiboTimeOfPage50 = lastWeiboTimeOfPage50;
	}
	public int getIsEndEveryWheel() {
		return isEndEveryWheel;
	}
	public void setIsEndEveryWheel(int isEndEveryWheel) {
		this.isEndEveryWheel = isEndEveryWheel;
	}
	public Date getTempTime() {
		return tempTime;
	}
	public void setTempTime(Date tempTime) {
		this.tempTime = tempTime;
	}
	
	
}
