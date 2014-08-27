package com.weibocrawl.prehandler;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.BasicCookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibocrawl.memory.MemoryCookie;
import com.weibocrawl.modle.CookieBean;
import com.weibocrawl.modle.CrawlPolicyBean;
import com.weibocrawl.utils.CookieUtil;

/**
 * 预先处理器，在该连接负责处理cookie以及ip，以及任务的选择
 * @author Pery
 *
 */
public class PreHandler {
	public static final Logger logger = LoggerFactory.getLogger(PreHandler.class);
	public MemoryCookie memoryCookie;
	public MemoryCookie getMemoryCookie() {
		return memoryCookie;
	}
	public void setMemoryCookie(MemoryCookie memoryCookie) {
		this.memoryCookie = memoryCookie;
	}


	public CrawlPolicyBean selectAndIntialOnePolicy(){
		CrawlPolicyBean policy = new CrawlPolicyBean();
		CookieBean cookieBean = memoryCookie.getNextCookie();
		logger.info("PreHandler selectAndIntialOnePolicy -"+Thread.currentThread().getName()+
					"当前cookie:"+cookieBean.getUser()+"；已经使用次数:"+cookieBean.getUsedCount()+
						";剩余使用次数"+(150-cookieBean.getUsedCount()));
		if(cookieBean!=null){
		policy.setCookieBean(cookieBean); 
		}
		//policy.setProxy(new HttpHost("211.142.236.132", 80));
		//代理ip选择
		return policy;
	}
	
	public CrawlPolicyBean selectDefaultPolicy(){
		return new CrawlPolicyBean();
	}
}
