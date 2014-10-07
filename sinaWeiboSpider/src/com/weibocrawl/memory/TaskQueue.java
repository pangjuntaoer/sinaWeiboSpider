package com.weibocrawl.memory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibocrawl.modle.TaskBean;
import com.weibocrawl.service.TaskDBService;

public class TaskQueue {
	public static final Logger logger = LoggerFactory.getLogger(TaskQueue.class);	
	/**
	 * 最大任务数量
	 */
	public final static int QUEUE_MAX_SIZE = 20;
	public final static int QUEUE_MIN_DEFAULT = 0;
	//LinkedBlockingQueue是线程安全的
	public static  BlockingQueue<TaskBean> queue = new  LinkedBlockingQueue<TaskBean>(QUEUE_MAX_SIZE);
	
	/**
	 * 任务队列还没有满
	 * 
	 * @return
	 */
	public  boolean isQueueFull() {
		if (queue.size() >= QUEUE_MAX_SIZE) {
			return true;
		}
		return false;
	}

	/**
	 * 弹出一个任务
	 * @return
	 */
	public  TaskBean pollOneTask() {
		TaskBean oneTask = null;
		try {
			if (queue.isEmpty()) {
				logger.warn("TaskQueue pollOneTask- 任务队列中为空了，等待中");
			}
			oneTask = queue.take();
			logger.info("TaskQueue pollOneTask-SUCCESS poll Task! surplus task is="+queue.size());
		} catch (IllegalMonitorStateException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		return oneTask;
	}
	public TaskBean TestGetOneTask(){
		TaskBean task = new TaskBean();
		task.setKeywordStr("四川");
		task.setStartTime(new Date());
		task.setEndTime(new Date());
		task.setNextPage(1);
		return task;
	}
	
	/**
	 * 返回任务队列任务数量
	 * @return
	 */
	public int getTaskCountOfQueue(){
		return queue.size();
	}
	
}
