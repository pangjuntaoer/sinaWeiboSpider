package com.weibocrawl.memory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.impl.client.BasicCookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.moniter.MailModel;
import com.weibo.moniter.MailSender;
import com.weibocrawl.modle.CookieBean;
import com.weibocrawl.modle.TaskBean;
import com.weibocrawl.service.CookieDBService;

/**
 * 保存在内存中的Cookie
 * 
 * @author Pery
 */
public class MemoryCookie {
	public static final Logger logger = LoggerFactory.getLogger(MemoryCookie.class);
	private CookieDBService cookieDB;

	public void setCookieDB(CookieDBService cookieDB) {
		this.cookieDB = cookieDB;
	}
	private MailSender mailSend;
	
	public void setMailSend(MailSender mailSend) {
		this.mailSend = mailSend;
	}

	/** cookie正在被使用 */
	final static int COOKIE_STATUS_IDLE = 1;
	/** cookie闲置 **/
	final static int COOKIE_STATUS_FREE = 0;
	/** 没个cookie每小时使用最大次数 */
	final static int COOKIE_USE_COUNT_HOURLY = 150;
	/** 上次使用的Cookie的id,供轮换时使用 */
	static Integer lastUsedUid;
	/** 上次初始化Cookie的时间，每小时就重新读取一次，同时重新清空cookie统计 */
	static Date lastInitialCookieTime;
	static int failGetCookieCount;
	final static int reLoadHop = 60;//60分钟重新载入一次cookie
	
	final static int IS_ACTIVE = 0;
	private static BlockingQueue<CookieBean> cookieQueue = new LinkedBlockingQueue<CookieBean>();

	public MemoryCookie() {

	}

	/**
	 * 初始化 token队列
	 */
	private void init() {
		boolean isNeedSendEmail = false;
		Date lastTime = null;
		if (cookieQueue == null || cookieQueue.isEmpty()) {
			lastInitialCookieTime = new Date();
			List<CookieBean> cookies = cookieDB.loadCookie();
			if (cookies.isEmpty()) {
				logger.info("MemoryCookie init--cookie list is empty!");
			}
			for (CookieBean cookieBean : cookies) {
				try {
					cookieQueue.put(cookieBean);
				} catch (InterruptedException e) {
					logger.error("MemoryCookie init--载入cookie失败：" + e.getMessage());
				}
			}
		} else {
			if (isToTimePoint()) { // 整点重新读取token列表
				List<CookieBean> cookies = cookieDB.loadCookie();
				if (cookies.isEmpty()) {
					logger.info("MemoryCookie init--cookie list is empty!");
				}
				// 清空cookie
				cookieQueue.clear();
				for (CookieBean cookieBean : cookies) {
					try {
						cookieQueue.put(cookieBean);
					} catch (InterruptedException e) {
						logger.error("MemoryCookie init- 载入cookie失败：" + e.getMessage());
					}
				}
				lastTime = lastInitialCookieTime;
				lastInitialCookieTime = new Date();
				logger.info("MemoryCookie init--整点了，重新载入了所有cookie" + lastInitialCookieTime);
				isNeedSendEmail = true;
			}
		}
		if(isNeedSendEmail){
			loadCookieInform(lastTime);//载入cookie通知
		}
	}
/**
 * 整点载入cookie邮件通知
 * @param lastTime 
 */
	private void loadCookieInform(Date lastTime) {
		String title="cookie整点载入了";
		String content="本次载入cookie时间："+new Date()+";上次载入cookie时间"+lastTime+
				";提示：cookie是每隔一小时重新载入一次，所以当你更新cookie后需要等下次载入时候才会起作用，" +
				"所以过期的cookie最好在一个小时内全部更新完成，否则可能会重复收到cookie邮件更新提醒";
		MailModel mail = new  MailModel(content,title);
		mailSend.send(mail);
	}

	/**
	 * 轮换取得下一个Cookie
	 * 
	 * @return
	 */
	public  synchronized CookieBean getNextCookie() {
		init();
		CookieBean cookieBean = null;
		int count = 0;
		while (true) {
			try {
				cookieBean = cookieQueue.take();
				count++;
				if (count >= cookieQueue.size()) {
					logger.error("MemoryCookie getNextCookie-No one cookie can be used! "
							+ "for every cookie's usedCount is to 150 times!");
					break; //取了20次没有取到的话
				}
				if (cookieBean.getUsedCount() < COOKIE_USE_COUNT_HOURLY) {
					break;
				} else {
					cookieBean.setUsedCount(cookieBean.getUsedCount() -2);//增加一次机会，等待下次使用
					reenQueue(cookieBean);
				}
			} catch (InterruptedException e) {
				logger.error("MemoryCookie getNextCookie-error to take cookie:" +
						e.getMessage());
			}
		}
		if (cookieBean != null) { // 使用次数+1
				cookieBean.setUsedCount(cookieBean.getUsedCount() + 1);
		}
		return cookieBean;
	}

	/**
	 * Cookie重新进入队列
	 * 
	 * @param cookieBean
	 */
	public void reenQueue(CookieBean cookieBean) {
		try {
			cookieQueue.put(cookieBean);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//testShowQueue();//测试队列中的内容
		logger.info("MemoryCookie reenQueue-线程"+Thread.currentThread().getName()+
				"重新进入队列"+cookieBean.getUser());
	}

	/**
	 * 是否到达整点
	 * 有问题了。。。
	 * @return
	 */
	private static boolean isToTimePoint() {
		Date currentTime = new Date();
		if (lastInitialCookieTime == null) {
			return true;
		}
		Calendar l = Calendar.getInstance();
		l.setTime(lastInitialCookieTime);
		l.add(Calendar.MINUTE, reLoadHop);//60分钟
		Calendar e = Calendar.getInstance();
		e.setTime(currentTime);
		return e.after(l)?true:false;
		/*
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		String last_str = sdf.format(lastInitialCookieTime);
		String end_str = sdf.format(currentTime);
		System.out.println(last_str+":"+end_str);
		return !last_str.equals(end_str);*/
	}
	private void testShowQueue(){
		Iterator<CookieBean> it = cookieQueue.iterator();
		while(it.hasNext()){
			System.out.println(it.next().getUser());
		}
	}
	public static void main(String[] args) {
		lastInitialCookieTime = new Date();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(new MemoryCookie().isToTimePoint());
	}
}
