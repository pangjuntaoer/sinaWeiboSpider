package com.weibocrawl.modle;

import org.apache.http.impl.client.BasicCookieStore;

public class CookieBean {
	private int id;
	private String user;
	private String password;
	private String strValue;
	private int isRuning; //当前cookie是否在用，0表示闲置 1标志在用
	private int isActive;	//当前cookie是否可用，0表示可用 1表示过期
	private BasicCookieStore cookieStore;
	
	private int usedCount=0;//使用次数
	public int getUsedCount() {
		return usedCount;
	}
	public void setUsedCount(int usedCount) {
		this.usedCount = usedCount;
	}
	public BasicCookieStore getCookieStore() {
		return cookieStore;
	}
	public void setCookieStore(BasicCookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}
	public int getIsRuning() {
		return isRuning;
	}
	public void setIsRuning(int isRuning) {
		this.isRuning = isRuning;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getStrValue() {
		return strValue;
	}
	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}
	
}
