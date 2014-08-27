package com.weibocrawl.modle;

import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

/**
 * 抓取策略参数类，该类负责抓取过程中的cookie设置以及ip代理设置
 * @author Pery
 *
 */
public class CrawlPolicyBean {
private HttpHost proxy;

private CookieBean cookieBean;

public HttpHost getProxy() {
	return proxy;
}

public CookieBean getCookieBean() {
	return cookieBean;
}

public void setCookieBean(CookieBean cookieBean) {
	this.cookieBean = cookieBean;
}

public void setProxy(HttpHost proxy) {
	this.proxy = proxy;
}
}
