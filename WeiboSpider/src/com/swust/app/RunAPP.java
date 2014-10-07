package com.swust.app;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.swust.dao.DbDao;
import com.swust.extractor.SimpleExtractor;
import com.swust.httputil.QHttpClient;
import com.swust.queue.CandidateURL;
import com.swust.queue.Statistics;
import com.swust.queue.URLQueue;

public class RunAPP {
private static int THREAD_COUNT=10;	
private static String ERROR_FILE="/home/pery/github/error.txt";
	public static void main(String[] args) throws Exception {
		String xmlPath[]=new String[]{"beans.xml"};
		ApplicationContext ctx = new ClassPathXmlApplicationContext(xmlPath);
		DbDao db = ctx.getBean(DbDao.class);
		URLQueue<CandidateURL> urlQueue = new 
				URLQueue<CandidateURL>();
		Statistics statistics = new Statistics();
		//String testURL="http://www.dianping.com/shop/2649025/review_all?pageno=1";
		String seedURL = "http://www.dianping.com/search/category/8/10/";
		CandidateURL seed = new CandidateURL(seedURL, true);
		urlQueue.putOneURL(seed);
		//3个线程
		ExecutorService execu = Executors.newFixedThreadPool(THREAD_COUNT);
		for (int i = 0; i < THREAD_COUNT; i++) {
			execu.execute(new ThreadRun(urlQueue,statistics,db));
		}
	}
	
	public static void appendErrorContent(String content) {  
        try {  
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
            FileWriter writer = new FileWriter(ERROR_FILE, true);  
            writer.write(content+"\n");
            writer.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
	
	public static void appendContent2File(String fileName,String content) {  
        try {  
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
            FileWriter writer = new FileWriter(fileName, true);  
            writer.write(content+"\n");
            writer.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}
