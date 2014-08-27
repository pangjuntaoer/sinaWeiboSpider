package com.weibo.spring;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;

import com.weibo.controller.SinaCrawlController;
import com.weibo.controller.ProducerController;

public class CrawlPool {
private int producerThreadCount;
private int crawlThreadCount;
private int poolThreadCount;

private SinaCrawlController crawlControll;
private ProducerController producerControll;

public void setCrawlControll(SinaCrawlController crawlControll) {
	this.crawlControll = crawlControll;
}

public void setProducerControll(ProducerController producerControll) {
	this.producerControll = producerControll;
}
@Value("1")
public void setProducerThreadCount(int producerThreadCount) {
	this.producerThreadCount = producerThreadCount;
}
@Value("2")
public void setCrawlThreadCount(int crawlThreadCount) {
	this.crawlThreadCount = crawlThreadCount;
}
@Value("3")
public void setPoolThreadCount(int poolThreadCount) {
	this.poolThreadCount = poolThreadCount;
}

public void setupAndStartThread(){
	 ExecutorService service = Executors.newFixedThreadPool(poolThreadCount);
	 for (int i = 0; i <producerThreadCount; i++) {
		 service.execute(producerControll);
	}
	 for (int i = 0; i < crawlThreadCount; i++) {
		service.execute(crawlControll);
	}
}
}
