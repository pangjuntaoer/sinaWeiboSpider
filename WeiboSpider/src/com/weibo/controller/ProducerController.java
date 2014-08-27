package com.weibo.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.weibo.moniter.CheckPoint;
import com.weibocrawl.memory.TaskOfMemory;
import com.weibocrawl.memory.TaskQueue;
import com.weibocrawl.modle.MemoryOfTaskBean;
import com.weibocrawl.modle.TaskBean;
import com.weibocrawl.service.TaskDBService;
/**
 * 生产者。主要负责向队列中添加任务
 * @author Pery
 *
 */
public class ProducerController implements Runnable {
public static final Logger logger = LoggerFactory.getLogger(ProducerController.class);	
private static final int MIN_TASK_WHEN_ADD = 3;//小于3个任务时候开始添加任务
private  BlockingQueue<TaskBean> 	queue = TaskQueue.queue; //将任务队列引用传过来 非注入
private static Date lastBackTime;//上次备份时间
private TaskDBService taskDBService; 
//备份频率，，几秒钟备份一次
private CheckPoint checkPoint;
private int amount;
@Value("30")
public void setAmount(int amount) {
	this.amount = amount;
}
public void setCheckPoint(CheckPoint checkPoint) {
	this.checkPoint = checkPoint;
}
public void setTaskDBService(TaskDBService taskDBService) {
	this.taskDBService = taskDBService;
}
public ProducerController(){
}
	@Override
	public void run() {
		while(true){ 
			if(lastBackTime==null){//第一次载入
				checkPoint.recoverBackUp();
				lastBackTime=new Date();
			}
			if(toReachBacktime()){
				pointTimeToBack();//整点备份内存数据
				lastBackTime = new Date();
			}
			//作为生产者永远向队列中添加任务
			if(queue.size()<=MIN_TASK_WHEN_ADD){//只有任务中数量少于3个时候才开始添加任务
				taskToTaskQueue();
			}else{
			//logger.info("队列中还有"+queue.size()+"个任务，还不能载入任务");
			}
		}
	}
	/**
	 * 定时备份内存数据
	 */
	private void pointTimeToBack() {
		synchronized (queue) {
				checkPoint.BackUpData();
		}
	}
	/**
	 * 是否到达备份时间
	 * 精确度为秒
	 * @return
	 */
	private boolean toReachBacktime() {
		if(lastBackTime!=null){
			Calendar cl = Calendar.getInstance();
			cl.setTime(lastBackTime);
			cl.add(Calendar.SECOND, amount);
			Calendar e = Calendar.getInstance();
			e.setTime(new Date());
			if(e.after(cl)){
				return true;
			}
			return false;
		}
		lastBackTime = new Date();//刚启动程序，只用记录时间，不用备份
		return false;
	}
	/**
	 * 载入任务到任务队列
	 */
	private  void taskToTaskQueue() {
	List<TaskBean> taskBeans = taskDBService.loadTasks();
	if(taskBeans==null||taskBeans.size()<=0){
		logger.warn("ProducerController taskToTaskQueue-Can't find legal task from DB!Task of queue  size="+queue.size());
		return ;
	}
	for(TaskBean task:taskBeans){
		if(checkTask(task)){
			try {
				queue.put(task);
				logger.info("ProducerController taskToTaskQueue-SUCCESS ADD Task! surplus task is="+queue.size());
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.warn("ProducerController taskToTaskQueue-任务队列满了，生产线程阻塞中");
			}
		}
	}
}
	/**
	 * 检查任务，首先检查时间，然后检查与内存任务同步
	 * @param task
	 * @return
	 */
	private  boolean checkTask(TaskBean task) {
		if(!isToCrawTime(task.getLastCrawlTime(),task.getHop())){//没到抓取时间，则跳过
			logger.info("ProducerController checkTask-task "+task.getTaskId()+",can't be added to queue for not reach  to crawl time");
			return false;
		}
		//内存任务同步检测
		MemoryOfTaskBean memoryTask = TaskOfMemory.memoryTask.get(task.getTaskId());
		if(memoryTask==null){ //内存不存在该任务,表示为新的一轮
			TaskOfMemory.addOneTask(task);
			logger.info("ProducerController checkTask-task "+task.getTaskId()+",first bulild successfully in memory!");
			return true;
		}
		//当前参数url还没有被抓取过，则加入
		if(!memoryTask.getHadCrawledMap().containsKey(task.toMd5String())){
			memoryTask.getHadCrawledMap().put(task.toMd5String(), MemoryOfTaskBean.BEING_CRAWL);
			logger.info("ProducerController checkTask-当前任务url从未添加过，need add! ");
			return true;
		}else{//当前参数已经被抓取过了，则判断是否需要重试,目前忽略重试
			//return false;
			if(memoryTask.getHadCrawledMap().get(task.toMd5String())==MemoryOfTaskBean.HAD_CRAWLED){
				logger.info("ProducerController checkTask-当前任务url已经抓取过了,任务队列还有+"+queue.size());
				return false;
			}else{
				logger.info("ProducerController checkTask-该任务失败抓取了，开始重试抓取 ");
				return true;
			}
		}
	}
	/**
	 * 判断当前任务十分到达了可抓取的时间
	 * @param lastUpdateTime
	 * @return
	 */
		public static boolean isToCrawTime(Date lastUpdateTime,int interval) {
			if(lastUpdateTime==null){
				return true;
			}
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			start.setTime(lastUpdateTime);
			start.add(Calendar.MINUTE, interval);
			end.setTime(new Date());
			return end.after(start);
		}
		
		public static void main(String[] args) {
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			start.setTime(new Date());
			start.add(Calendar.SECOND, 3);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			end.setTime(new Date());
			System.out.println( end.after(start));
		}
}
