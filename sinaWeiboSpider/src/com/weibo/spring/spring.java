package com.weibo.spring;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.weibo.controller.SinaCrawlController;
import com.weibo.moniter.MailModel;
import com.weibo.moniter.MailSender;
import com.weibocrawl.extractor.SinaExtractor;
import com.weibocrawl.memory.TaskQueue;
import com.weibocrawl.modle.CrawResult;
import com.weibocrawl.modle.TaskBean;
import com.weibocrawl.prehandler.PreHandler;
import com.weibocrawl.service.TaskDBService;
import com.weibocrawl.writter.Writter;

public class spring {
	private static Pattern pattern = Pattern.compile("\\&\\#(\\d+)"); 
	public static String readFile() {
		String result = "";
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileInputStream fis = null;

		try {
			fis = new FileInputStream("D:/1.txt");
			isr = new InputStreamReader(fis, "GBK");
			BufferedReader bf = new BufferedReader(isr);
			String line = null;
			while ((line = bf.readLine()) != null) {
				result += line;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	 public static String tr(String str){  
		 String[] unicode = str.split("\\\\u");  //第一个是空白字符，忽略
		 StringBuffer buf = new StringBuffer(unicode.length - 1);
		 for(int i = 1; i < unicode.length; i++)
		      buf.append((char)Integer.parseInt(unicode[i], 16));
		 System.out.println(buf.toString()); 
		 return buf.toString();
	 }
public static void main(String[] args) {
/*		String source = readFile();
		String regex = "displaySearchResult\\(_yrE\\('searchtabContainer'\\), state,\\s*\\[(.*?\\]\\]),";
		Pattern pt = Pattern.compile(regex);
		Matcher mt = pt.matcher(source);
		String result  =null;
		if(mt.find()){
			result = mt.group(1);
		}
		String s=tr(result);
		System.out.println(s);
	*/
	
		 String xmlPath[]=new String[]{"beans.xml","mail_beans.xml"};
		 ApplicationContext ctx = new ClassPathXmlApplicationContext(xmlPath);
	//下面两行代码负责将图书标题列表文件导入到数据库 
		// TaskDBService td = (TaskDBService) ctx.getBean("task_Service");
		//td.insertTask();
	
		//抓取任务开始
		 
		 CrawlPool start = (CrawlPool) ctx.getBean("threadPool");
		 start.setupAndStartThread();
		 
		 //邮件测试
		//MailSender sd = (MailSender) ctx.getBean("mail_factory");
		//MailModel mail = new MailModel();
		//sd.send(mail);
	}
}
