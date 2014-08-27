package com.weibo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.weibocrawl.extractor.Extractor;
import com.weibocrawl.memory.TaskOfMemory;
import com.weibocrawl.modle.CrawResult;
import com.weibocrawl.modle.MemoryOfTaskBean;
import com.weibocrawl.modle.TaskBean;
import com.weibocrawl.service.TaskDBService;

/**
 * 当前任务同步模块
 * 
 * @author Pery
 * 
 */
public class SynTaskController {
	public static final Logger logger = LoggerFactory.getLogger(SynTaskController.class);
	private TaskDBService taskDBService;
	private int crawlMaxPage;//抓取最大页码
	@Value("10")
	public void setCrawlMaxPage(int crawlMaxPage) {
		this.crawlMaxPage = crawlMaxPage;
	}
	public void setTaskDBService(TaskDBService taskDBService) {
		this.taskDBService = taskDBService;
	}

	/**
	 * 抓取前与内存任务同步
	 * 
	 * @param task
	 */
	public boolean synTaskBeforeCrawl(TaskBean task) {
		MemoryOfTaskBean taskMemory = TaskOfMemory.memoryTask.get(task.getTaskId());
		if (taskMemory != null) {
			return true;
		} else {
			logger.info("SynTaskController synTaskBeforeCrawl- current task has already ended!");
			return false;
		}
	}

	/**
	 * 抓取后与任务同步 主要同步当前任务在内存中的总页码，任务下一次应该抓取的页码
	 * 
	 * @param task
	 * @param content
	 * @param resultStatus
	 */
	public void synTaskAfterCrawl(TaskBean task, CrawResult content, String resultStatus) {
		MemoryOfTaskBean taskMemory = TaskOfMemory.memoryTask.get(task.getTaskId());
		if (taskMemory == null) {// 任务之前结束了
			return;
		}
		if (resultStatus.equals(Extractor.CONTENT_NULL)) { // 抓取失败，准备重试
			taskMemory.getHadCrawledMap().put(task.toMd5String(), MemoryOfTaskBean.FAIL_CRAWLED);
			return;
		}
		if (resultStatus.equals(Extractor.CAN_EXTRACT)) {//抓取成功可以抽取
			taskMemory.setCrawlMaxPageSize(content.getTotalPage());// 本轮最大翻页数放到改任务的内存中
			taskMemory.getHadCrawledMap().put(task.toMd5String(), MemoryOfTaskBean.HAD_CRAWLED);
			if(task.getNextPage()<content.getTotalPage()){
				taskDBService.updateNextPage(task);
			}
			int page = content.getTotalPage();
			if (page != 0) {
				taskMemory.setCrawlMaxPageSize(page);
			}
		}
	}

	/**
	 * 抽取后同步 主要查看任务是否一轮结束
	 * 
	 * @param content
	 * @param task
	 */
	public void sysTaskAfterExtractor(CrawResult content, TaskBean task) {
		if(content.getTotalPage()<=crawlMaxPage){
			if (task.getNextPage() == content.getTotalPage()) { // 一轮抓取完成了
				endOneTask(task,content.getTotalPage());
			}else{
				return;
			}
		}else{
			if(task.getNextPage()==crawlMaxPage){
				endOneTask(task,content.getTotalPage());
			}
		}
		return;
	}

	/**
	 * 结束某一任务的当前轮
	 * 
	 * @param task
	 * @param resultStatus 
	 */
	public void endOneTask(TaskBean task, int resultStatus) {
		logger.info("SynTaskController-endOneTask:task"+task.toString()+" is end crawl,this totalPage is"+resultStatus);
		try {
			taskDBService.endOneTask(task,resultStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
		TaskOfMemory.memoryTask.remove(task.getTaskId());
	}
}
