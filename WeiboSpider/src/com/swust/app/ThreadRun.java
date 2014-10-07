package com.swust.app;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;

import com.swust.dao.DbDao;
import com.swust.extractor.SimpleExtractor;
import com.swust.httputil.QHttpClient;
import com.swust.queue.BerkeleDBFilter;
import com.swust.queue.CandidateURL;
import com.swust.queue.Statistics;
import com.swust.queue.URLQueue;

public class ThreadRun implements Runnable{
    // 日志输出
private static Log log = LogFactory.getLog(QHttpClient.class);
private URLQueue<CandidateURL> URLQUEUE;
private Statistics statistic;
private DbDao db;
public ThreadRun(URLQueue<CandidateURL> URLQUEUE,Statistics statistic, DbDao db){
	this.URLQUEUE = URLQUEUE;
	this.statistic = statistic;
	this.db = db;
}

@Override
public void run() {
	try {
		QHttpClient httpClient = new QHttpClient();
		while(true){
		//取链接不计算时间
		CandidateURL url = URLQUEUE.getOneURL();
		long time1 = System.currentTimeMillis();
		url.addFetchCount();
		log.info("begin the ["+url.getFetchCount()+"]:"+url.getUrl()+" fetching");
		long time2 = System.currentTimeMillis();
		httpClient.httpGet(url, null);
		//请求统计
		long time3 = System.currentTimeMillis();
		statistic.statisticInfoAfterRequest(time3-time2);
		if(url.getStatus()==HttpStatus.SC_OK){
			SimpleExtractor extractor = new SimpleExtractor(url.getUrl(), url,db);
			Set<String> outLinks = extractor.extractContent();
			log.info("extract "+outLinks.size()+ " outLinks from:"+url.getUrl());
			this.addOutLinks(url,outLinks);
		}else{
			log.warn("["+url.getStatus()+"] Faild,begin retry!:"+url.getUrl());
			this.deleteOneKey(url.getUrlMd5());
			URLQUEUE.putOneURL(url);
		}
		long time4 = System.currentTimeMillis();
		//处理打印统计
		statistic.statisticInfo(time4-time1);
		//暂停1s
		Thread.sleep(2000);
	 }
		
	} catch (InterruptedException e) {
		e.printStackTrace();
	} catch (Exception e) {
		e.printStackTrace();
	}
}

private void deleteOneKey(String urlMd5) {
	BerkeleDBFilter urlMD5= new BerkeleDBFilter();
	try {
		urlMD5.deleteFromDatabase(urlMd5);
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		urlMD5.closeDatabase();
	}
	
	
}

public void addOutLinks(CandidateURL url, Set<String> outLinks) throws InterruptedException {
	BerkeleDBFilter urlMD5= new BerkeleDBFilter();
	//已经完成抓取
	try {
		urlMD5.writeToDatabase(url.getUrlMd5(), "fetched", true);
		Iterator<String> it = outLinks.iterator();
		while(it.hasNext()){
			String link = it.next();
			//去已经抓取重复url
			String value = urlMD5.readFromDatabase(DigestUtils.md5(link).toString());
			if(value!=null&&value.equals("")){
				log.info("add outlink SUCCESS:"+link);
				CandidateURL outURI = new CandidateURL(url.getUrl(),link);
				urlMD5.writeToDatabase(url.getUrlMd5(), "fetching", true);
				URLQUEUE.putOneURL(outURI);
			}
	}
	}finally{
		urlMD5.closeDatabase();
	}
}

}
