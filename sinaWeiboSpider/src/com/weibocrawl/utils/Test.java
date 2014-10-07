package com.weibocrawl.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.mapred.SequenceFileInputFilter.MD5Filter;
import org.jsoup.Jsoup;

import com.alibaba.fastjson.JSON;
import com.weibocrawl.extractor.SinaWeiboBlock;

public class Test {
	private final static String blockRegex = "<script>STK\\s&&\\sSTK\\.pageletM\\s&&\\sSTK\\.pageletM\\.view\\(.*\\)";
	private static Pattern pattern = Pattern.compile(blockRegex);

	public static void main(String args[]) throws IOException{		
		
	String t=	System.getProperty("file.encoding");
	if(t!=null){
		System.out.println(t);
		return;
	}
		
	String cookieValue="	_s_tentry=-; Apache=1412020411931.556.1375261451990; SINAGLOBAL=1412020411931.556.1375261451990; ULV=1375261452002:1:1:1:1412020411931.556.1375261451990:; SUS=SID-1805841815-1375261533-GZ-dxj1w-d8739ee388fea24c922aa80254071452; SUE=es%3D06da69da9feb84aa3c7ba326b0dc313b%26ev%3Dv1%26es2%3D3f21ecf7614668401c2430a908e4ea76%26rs0%3DVwY3WdwVWWyWcNjbtKkp8tOqSPpNO8Q2lils8aP%252Bm2O90FTPdk6Qumo8P6YTaGbmmipFwCiKSEW8mMXJPtzUQ%252BPmkHGsqAyvTOWNhQxwjDN7KanaGgdsbbYTBEOi9nHCrgyNuwLGAP2m83qXsApzuSIeQIwzGopGbSm%252FW%252BBGC%252BI%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1375261533%26et%3D1375347933%26d%3Dc909%26i%3D0a0e%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D2%26st%3D0%26uid%3D1805841815%26name%3Dpangjuntaoer%2540163.com%26nick%3D%25E6%2583%2585%25E9%259D%259E%25E5%25BE%2597%25E5%25B7%25B2%26fmp%3D%26lcp%3D2011-12-30%252019%253A29%253A48; ALF=1377853532; SSOLoginState=1375261533; un=pangjuntaoer@163.com; wvr=5; SWB=usrmd1072; WBStore=9ac9867b6090f537|undefined";
	//"SINAGLOBAL=233511789011.5179.1374487122470; ULV=1374737442297:5:5:5:9209693558412.453.1374737442265:1374734439079; ALF=1377329431; un=pangjuntaoer@163.com; wvr=5; UOR=,,login.sina.com.cn; SUS=SID-1805841815-1374737432-GZ-3db8g-858dacbcb2fc33be67b142688d28dd7a; SUE=es%3D53f62a2357b07e2c9dd3155d09578e7f%26ev%3Dv1%26es2%3D095ccc551c9c6ef06660284cdc13edc6%26rs0%3Ds%252Fl1U8hGRRI9tH%252F9LUnuS%252F9J%252FLzoKaO7GZXi1Quk6i7vjOLdeqT5DBUmRWY9LTuNcQ%252BtnIZMQPptRV8LJZ9re5eJTX8suHqnEV22KMaItbLSPK6gihm7XydEtyyVM%252BDjObaRRqrBJkdD2Z8Ssgdl0P34HuS8KZsCHVRBdaApaW0%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1374737432%26et%3D1374823832%26d%3Dc909%26i%3D2712%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D2%26st%3D0%26uid%3D1805841815%26name%3Dpangjuntaoer%2540163.com%26nick%3D%25E6%2583%2585%25E9%259D%259E%25E5%25BE%2597%25E5%25B7%25B2%26fmp%3D%26lcp%3D2011-12-30%252019%253A29%253A48; SSOLoginState=1374737432; SWB=usrmd1073; _s_tentry=login.sina.com.cn; Apache=9209693558412.453.1374737442265; WBStore=7df5532e3799ef19|undefined";
	String url = "http://s.weibo.com/weibo/msn&Refer=STopic_box?retcode=6102";
	String source =null;
	int i=0;
	while(i<2){
	source = HtmlUtil.getPageContent(url, 2000, cookieValue);
		i++;
	}
	// 匹配文本块
	Matcher m = null;
	try{
	 m = pattern.matcher(source);
	}catch(Exception e){
		e.printStackTrace();
		System.out.println(source);
		return;
	}
	while(m.find()){

		String jsonStr = m.group();
		
		jsonStr = jsonStr.substring(jsonStr.indexOf("{"), jsonStr.lastIndexOf(")"));
		
		// 解析json,转换为实体类
		SinaWeiboBlock block = JSON.parseObject(jsonStr, SinaWeiboBlock.class);
		if(block.getHtml().trim().startsWith("<div class=\"search_feed\">")){
			System.out.println(block.getHtml());
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(block.getHtml().trim().startsWith("<div class=\"topcon_l\">")){
			System.out.println("拿到征文了");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	}
	    public static String getMd5ByFile(File file) {
	        String value = null;
	        FileInputStream in = null;
		try {
		     in = new FileInputStream(file);
		    MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		    MessageDigest md5 = MessageDigest.getInstance("MD5");
		    md5.update(byteBuffer);
		    BigInteger bi = new BigInteger(1, md5.digest());
		    value = bi.toString(16);
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
	            if(null != in) {
		            try {
			        in.close();
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
		    }
		}
		return value;
	    }
}
