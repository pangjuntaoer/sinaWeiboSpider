package com.weibocrawl.utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;







import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

public class HtmlUtil {
	
	public static String getPageContent(String url, int timeOut,String cookieValue) {
		DefaultHttpClient  client = new DefaultHttpClient();
//		client.getConnectionManager().getSchemeRegistry().register(ch);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		HttpGet httpGet = new HttpGet(url);  
		HttpClientParams.setCookiePolicy(client.getParams(), CookiePolicy.BROWSER_COMPATIBILITY); 
		httpGet.setHeader("Cookie", cookieValue);
     //   client.setCookieStore(CookieUtil.strToCookie(cookieValue));
		String pageString = null;
		try {
			  HttpResponse response = client.execute(httpGet);  
			  if (HttpStatus.SC_OK==response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				if(entity!=null){
					pageString = EntityUtils.toString(entity, "utf-8");
				}
				//读取cookie并保存文件  
		        List<Cookie> cookies = ((AbstractHttpClient)client).getCookieStore().getCookies();  
                if(cookies !=null && cookies.size()>0){
                    String cook=cookies.get(0).getValue();
                    for (int i = 1; i < cookies.size(); i++) {
                        cook += "; " + cookies.get(i).getName() + "=" + cookies.get(i).getValue();
                    }
                    System.out.println("cookie\n"+cook);
                }
            }
		} catch (IOException e) {
			return null;
		} finally {
			client.getConnectionManager().shutdown();
		}
		 return pageString;
	}

	
	
	private static String checkBom(String content) {
		char[] strAry = content.toCharArray();
		int index = 0;
		for (int i = 0; i < strAry.length; i++) {
			if (strAry[i] == '{' || strAry[i] == '[') {
				index = i;
				break;
			}
		}
		return content.substring(index);
	}

	/**
	 * 将InputStream信息转换为String信息�?
	 * 
	 * @param in
	 * @return
	 * @throws UnsupportedEncodingException
	 * @since 1.0
	 */
	public static String convertStreamToString(InputStream in)
			throws UnsupportedEncodingException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(in,
				"utf-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String clearHtml(String str) {
		String result = str;
		result = Pattern.compile("<script.*?>.*?</script>",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(result)
				.replaceAll("");
		result = Pattern.compile("<style.*?>.*?</style>",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(result)
				.replaceAll("");
		result = result.replaceAll("<[^>]+?>", "");

		return result;
	}

	public static String writeToFile(String path, String content,
			boolean isCompress) {
		BufferedWriter bw = null;
		try {
			File file = new File(path);
			if (!file.getParentFile().exists()) {
				if (!file.getParentFile().mkdirs()) {
					return "创建路径未成功";
				}
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			bw = new BufferedWriter(new FileWriter(file));
			if (isCompress) {
				// TODO 添加压缩逻辑
			}
			bw.write(content);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return path;
	}

	// 通过正则在html中提取信息 如果正则为空，没有匹配， 返回空串
	public static String getStringInHtml(String html, String regex,boolean judge) {
		if (regex == null)
			return "[提取不到信息]";
		try{
			Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			Matcher matcher = pattern.matcher(html);
			while (matcher.find()) {
				if(judge) return deleteHtmlTagAndSpaces(matcher.group(1));
				else return matcher.group(1);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "[提取不到信息]";
	}
	public static String getStringInHtml(String html, String regex) {
		if (regex == null)
			return "[提取不到信息]";
		try{
			Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {
			return matcher.group(1);
		}
		}catch(Exception e){
		}
		return "[提取不到信息]";
	}
	public static String deleteHtmlTagAndSpaces(String html){
		html = html.replaceAll("<.*?>", "");
		html = html.replaceAll("\\s*", "");
		html = html.replaceAll("　*", "");
		return html;
	}
	public static void main(String[] args) {
		String uri ="http://42.120.22.162:8080/weiboajax/statuses/search.json?keyword=%B8%B4%%20B5%A9%B4%F3%D1%A7&province=31&starttime=2013-04-21+16%3A32%3A32.0&endTime=2013-0%205-05+18%3A12%3A49.0&page=22";
		String url ="http://42.120.22.162:8080/weiboajax/statuses/search.json?keyword=%C9%CF%BA%A3&province=31&starttime=2013-04-01%2011:50:18&endtime=2013-04-18%2011:50:18&page=1";
		System.out.print(getPageContent(url, 3000,null));
	}
}