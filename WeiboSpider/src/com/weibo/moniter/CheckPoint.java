package com.weibo.moniter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.weibocrawl.memory.TaskOfMemory;
import com.weibocrawl.memory.TaskQueue;
import com.weibocrawl.modle.MemoryOfTaskBean;
import com.weibocrawl.modle.TaskBean;

public class CheckPoint {
static final Logger logger = LoggerFactory.getLogger(CheckPoint.class);	
static  BlockingQueue<TaskBean> taskQueue =  TaskQueue.queue;
static  ConcurrentMap<Integer,MemoryOfTaskBean> memoryTask=TaskOfMemory.memoryTask;
private String backPath; 
@Value("./backup.bat") 
public void setBackPath(String backPath) {
	this.backPath = backPath;
}
/**
 * 备份数据
 */
public synchronized void BackUpData(){
	ObjectOutputStream os = null;
	try {
		 os = new ObjectOutputStream(new FileOutputStream(backPath));
		os.writeObject(taskQueue);
		os.writeObject(memoryTask);
		logger.info("CheckPoint--BackUpData,备份内存数据成功");
	} catch (FileNotFoundException e) {
		logger.info("CheckPoint--BackUpData 文件不存在！"+e.getMessage());
	} catch (IOException e) {
		e.printStackTrace();
	}finally{
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
public synchronized void recoverBackUp(){
	ObjectInputStream oi = null;
	try {
		 oi = new ObjectInputStream(new FileInputStream(backPath));
		BlockingQueue<TaskBean> tasks = (BlockingQueue)oi.readObject();
		Iterator<TaskBean> it = tasks.iterator();
		while(it.hasNext()){
			try {
				taskQueue.put(it.next());
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.info("CheckPoint- recoverBackUp-恢复数据失败");
			}
		}
	
		ConcurrentMap<Integer,MemoryOfTaskBean> tasksMemory = (ConcurrentMap)oi.readObject();
		Iterator<Integer> keyIt =  tasksMemory.keySet().iterator();
		while(keyIt.hasNext()){
			MemoryOfTaskBean m = tasksMemory.get(keyIt.next());
			memoryTask.put(m.getId(), m);
		}
		
	} catch (FileNotFoundException e) {
		logger.info("CheckPoint--RecoverbackUp 文件不存在！"+e.getMessage());
		return;
	} catch (IOException e) {
		e.printStackTrace();
		return;
	} catch (ClassNotFoundException e) {
		logger.info("CheckPoint--RecoverbackUp 备份中不存在当前数据！"+e.getMessage());
		return;
	}finally{
		try {
			if(oi!=null)
			oi.close();
		} catch (IOException e) {
			return;
		}
	}
}	

public static void main(String[] args) {
	TaskBean t1 = new TaskBean();
	t1.setTaskId(1);
	t1.setKeywordStr("四川cookie");
	TaskBean t2 = new TaskBean();
	t2.setTaskId(2);
	t2.setKeywordStr("天府d");
	taskQueue.add(t1);
	taskQueue.add(t2);
	
	MemoryOfTaskBean m1 = new MemoryOfTaskBean();
	m1.setId(3);
	MemoryOfTaskBean m2 = new MemoryOfTaskBean();
	m2.setId(4);
	memoryTask.put(m1.getId(), m1);
	memoryTask.put(m2.getId(), m2);
	CheckPoint c = new CheckPoint();
//	c.BackUpData();
	c.recoverBackUp();
}
}
