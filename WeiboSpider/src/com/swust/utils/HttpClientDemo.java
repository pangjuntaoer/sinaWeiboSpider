package com.swust.utils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HttpClientDemo {
	/**
	 * 获取重定向之后的网址信息
	 * @see HttpClient缺省会自动处理客户端重定向
	 * @see 即访问网页A后,假设被重定向到了B网页,那么HttpClient将自动返回B网页的内容
	 * @see 若想取得B网页的地址,就需要借助HttpContext对象,HttpContext实际上是客户端用来在多次请求响应的交互中,保持状态信息的
	 * @see 我们自己也可以利用HttpContext来存放一些我们需要的信息,以便下次请求的时候能够取出这些信息来使用
	 */
	public static void getRedirectInfo(){
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpGet httpGet = new HttpGet("http://book.douban.com/subject/2000000/");
		try {
			//将HttpContext对象作为参数传给execute()方法,则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中
			HttpResponse response = httpClient.execute(httpGet, httpContext);
			//获取重定向之后的主机地址信息,即"http://127.0.0.1:8088"
			HttpHost targetHost = (HttpHost)httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			//获取实际的请求对象的URI,即重定向之后的"/blog/admin/login.jsp"
			HttpUriRequest realRequest = (HttpUriRequest)httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
			System.out.println("主机地址:" + targetHost);
			System.out.println("URI信息:" + realRequest.getURI());
			System.out.println(targetHost.toString()+realRequest.getURI()+"\n"+realRequest.getRequestLine());
			HttpEntity entity = response.getEntity();
			if(null != entity){
				//System.out.println("响应内容:" + EntityUtils.toString(entity, "UTF-8"));
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			httpClient.getConnectionManager().shutdown();
		}
	}
public static void main(String[] args) {
	getRedirectInfo();
}
}
