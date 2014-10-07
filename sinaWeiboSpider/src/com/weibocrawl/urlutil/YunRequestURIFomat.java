package com.weibocrawl.urlutil;

import java.util.List;

import org.apache.http.NameValuePair;

import com.weibocrawl.modle.TaskBean;

public class YunRequestURIFomat {
	private  String url;
	private List<NameValuePair> paramsList;
	private YunRequestURIFomat(){}//禁用无参数构造方法
	public YunRequestURIFomat(TaskBean task){
		this.url = "http://weibo.yunyun.com/Weibo.php?p="+String.valueOf(task.getNextPage())+
		"&q="+task.getKeywordStr();
	}
	public List<NameValuePair> getParamsList() {
		return paramsList;
	}
	public void setParamsList(List<NameValuePair> paramsList) {
		this.paramsList = paramsList;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
