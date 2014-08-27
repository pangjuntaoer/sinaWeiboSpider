package com.weibocrawl.utils;

public class TestCMCC {
	public static void main(String []arg){
		String url1 = "http://211.137.84.198/pams2/m/s.do?j=l&p=52&c=33976";
		String url2 = "";
		String cookie="sc_cmnet_code=%1F%C2%8B%08%00%00%00%00%00%00%00345%C2%B20%C2%B04%C2%B5405%01%00%C2%A9lL%C2%92%0B%00%00%00|%1F%C2%8B%08%00%00%00%00%00%00%00%0B%C3%8F%08%C2%8E%C3%B0%C3%8B%C3%8At%0A%2B*%2F%C2%B5%C3%88%C2%B2%C3%B0%0C%291t%0CN6%0A%284N%C3%B3%29-4%09%C3%8B%C3%88%C3%8A%C2%AE%C3%8A.5%C2%A9%04%00%C2%9B%C2%83r%C3%A6%28%00%00%00; expires=Fri, 26 Jul 2013 06:25:36 GMT; path=/; domain=211.137.84.198"
+"sccmnet=0n8025ZEYaoeFLPLZuj9OLBQnvfYJPqd; expires=Fri, 26 Jul 2013 06:25:37 GMT; path=/; domain=211.137.84.198"
+"JSESSIONID=412FE778222EB93DA42F43A9B6C7D234; path=/pams2/; domain=211.137.84.198; HttpOnly"
+"sc_cmnet_code=%1F%C2%8B%08%00%00%00%00%00%00%00345%C2%B20%C2%B04%C2%B5405%01%00%C2%A9lL%C2%92%0B%00%00%00|%1F%C2%8B%08%00%00%00%00%00%00%00%0B%C3%8F%08%C2%8E%C3%B0%C3%8B%C3%8At%0A%2B*%2F%C2%B5%C3%88%C2%B2%C3%B0%0C%291t%0CN6%0A%284N%C3%B3%29-4%09%C3%8B%C3%88%C3%8A%C2%AE%C3%8A.5%C2%A9%02%00%21%C3%92%7B%7F%28%00%00%00; expires=Mon, 05 Aug 2013 06:05:37 GMT; path=/; domain=wap.scmcc.com.cn";
		int i = 0;
		while(i<100){
			String source = HtmlUtil.getPageContent(url1, 2000, cookie);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(source!=null){
				System.out.println("中奖了:"+source);
			}
		}
	}
}
