package com.weibocrawl.extractor;

import java.util.List;

import com.weibocrawl.modle.CrawResult;
import com.weibocrawl.modle.TaskBean;
import com.weibocrawl.modle.WeiboBean;

public interface Extractor {
	static String COOKIE_WRONG = "wrong";//cookie失效
	static String CONTENT_NULL = "null";//抓取失败为null
	static String CAN_EXTRACT  = "yes";
	static String WRONG_CONTENT = "no";//不正确的内容
	static String END_TASK = "end";//结束任务
	public String isShouleExtract(String source, CrawResult content);
	public List<WeiboBean> innerExtractor(CrawResult content, TaskBean task);
}
