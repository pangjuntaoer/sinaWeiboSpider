package com.weibocrawl.memory;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.weibocrawl.modle.MemoryOfTaskBean;
import com.weibocrawl.modle.TaskBean;

public class TaskOfMemory {
public static ConcurrentMap<Integer,MemoryOfTaskBean> memoryTask = new ConcurrentHashMap<Integer,MemoryOfTaskBean>(); 
 public static void addOneTask(TaskBean task){
	 MemoryOfTaskBean taskMemory = new MemoryOfTaskBean();
	 taskMemory.setLastUpdateTime(new Date());
	 taskMemory.setId(task.getTaskId());
	 taskMemory.getHadCrawledMap().put(task.toMd5String(),MemoryOfTaskBean.BEING_CRAWL); //当前信息
	 memoryTask.put(task.getTaskId(), taskMemory);
 }

}
