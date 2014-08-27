package com.weibocrawl.fetcher;

import java.util.List;

import org.apache.http.NameValuePair;

import com.weibocrawl.httputil.QHttpClient;
import com.weibocrawl.httputil.QStrOperate;
import com.weibocrawl.modle.CrawlPolicyBean;

public class FetcherCore implements RequestAPI {
    private QHttpClient qHttpClient;//不能通过Spring注入
    //禁用无参数构造函数
    private FetcherCore(){
    }
  /**
   * 使用完毕后，请调用 shutdownConnection() 关闭自动生成的连接管理器
   */
  public FetcherCore(CrawlPolicyBean crawlPolicy){
      qHttpClient = new QHttpClient(crawlPolicy);
  }
	@Override
	public String getResource(String url, List<NameValuePair> paramsList) throws Exception {
		 String queryString = QStrOperate.getQueryString(paramsList);
	     return qHttpClient.httpGet(url, queryString);
	}

	@Override
	public String postContent(String url, List<NameValuePair> paramsList) throws Exception {
		 String queryString = QStrOperate.getQueryString(paramsList);
	     return qHttpClient.httpGet(url, queryString);
	}

	@Override
	public String postFile(String url, List<NameValuePair> paramsList, List<NameValuePair> files) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdownConnection() {
		 qHttpClient.shutdownConnection();
	}

}
