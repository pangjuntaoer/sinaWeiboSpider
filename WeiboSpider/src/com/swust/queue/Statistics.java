package com.swust.queue;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.util.concurrent.AtomicDouble;

public class Statistics {

	private Lock lock = new ReentrantLock(false);

	public Statistics() {
		this.beginTime = Calendar.getInstance();
		this.lastRequestTime = this.beginTime;
	}

	/**
	 * 开始时间
	 */
	private Calendar beginTime;
	/**
	 * 请求总次数
	 */
	private AtomicInteger reqeustCount = new AtomicInteger(0);

	private AtomicDouble requestTotalTime = new AtomicDouble(0);
	/**
	 * 成功请求总次数
	 */
	private AtomicInteger successReqeustCount = new AtomicInteger(0);
	/**
	 * 请求频率(每分钟/次)
	 */
	private int requestSeed;
	/**
	 * 上次请求时间
	 */
	private Calendar lastRequestTime;
	/**
	 * 当前每次请求平均时间
	 */
	private long currentGetAverTime;

	/**
	 * 当前平均处理时间
	 */
	private long currentHandleAveTime;

	public void statisticInfo(long currentHandleAveTime) {
		lock.lock();
		try {
			this.successReqeustCount.addAndGet(1);
			this.currentHandleAveTime = (this.currentHandleAveTime + currentHandleAveTime)
					/ this.successReqeustCount.intValue();
			printStatisticInfo();
		} finally {
			lock.unlock();
		}
	}

	public void statisticInfoAfterRequest(long requestTime) {
		lock.lock();
		try {
			this.reqeustCount.addAndGet(1);
			this.currentGetAverTime = (this.currentGetAverTime + requestTime)
					/ this.reqeustCount.intValue();
			if (lastRequestTime == null) {
				lastRequestTime = Calendar.getInstance();
			} else {
				Calendar now = Calendar.getInstance();
				requestTotalTime.addAndGet(requestTime);
				lastRequestTime = now;
				requestSeed = (int) (reqeustCount.intValue()/(requestTotalTime.doubleValue()/1000));
			}
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void getStatisticInfo() {
		lock.lock();
		try {
			printStatisticInfo();
		} finally {
			lock.unlock();
		}
	}

	private void printStatisticInfo(){
		System.out.println("当前请求总数:"+this.reqeustCount+"\n"+
				"当前请求速率:"+this.requestSeed+" 次/秒\n"+
				"当前成功处理（And Rquest OK）次数："+this.successReqeustCount+"\n"+
				"当前平均每次处理耗时："+this.currentHandleAveTime+"ms\n"+
				"当前平均请求耗时："+this.currentGetAverTime+"ms\n");
	}
}
