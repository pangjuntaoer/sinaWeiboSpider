package com.weibo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.moniter.MailModel;
import com.weibo.moniter.MailSender;
import com.weibocrawl.extractor.Extractor;
import com.weibocrawl.extractor.SinaExtractor;
import com.weibocrawl.fetcher.FetcherCore;
import com.weibocrawl.memory.TaskQueue;
import com.weibocrawl.modle.CrawlPolicyBean;
import com.weibocrawl.modle.CrawResult;
import com.weibocrawl.modle.TaskBean;
import com.weibocrawl.prehandler.PreHandler;
import com.weibocrawl.urlutil.SinaRequestURIFormat;

public class SinaCrawlController implements Runnable {
	public static final Logger logger = LoggerFactory
			.getLogger(SinaCrawlController.class);
	private TaskQueue taskQueue; // 任务队列
	private PreHandler preHandler; // 预先处理模块
	private FetcherCore fetcher; // 抓取控制模块<!--不能通过注入-->
	private Extractor extractor;// 抽取以及存储控制模块
	private SynTaskController synTaskControll;

	private MailSender mailSend;

	public void setMailSend(MailSender mailSend) {
		this.mailSend = mailSend;
	}

	public void setSynTaskControll(SynTaskController synTaskControll) {
		this.synTaskControll = synTaskControll;
	}

	public void setTaskQueue(TaskQueue taskQueue) {
		this.taskQueue = taskQueue;
	}

	public void setPreHandler(PreHandler preHandler) {
		this.preHandler = preHandler;
	}

	public void setExtractor(SinaExtractor extractor) {
		this.extractor = extractor;
	}

	@Override
	public void run() {
		while (true) {
			long startTime = System.currentTimeMillis();

			TaskBean task = taskQueue.pollOneTask(); // 获得任务
			logger.info("CrawlController run-"
					+ Thread.currentThread().getName() + "取得任务"
					+ task.toString());
			if (task != null) {
				CrawlPolicyBean policy = preHandler.selectAndIntialOnePolicy(); // cookie选择
				if (policy.getCookieBean() == null) {
					continue;
				}
				fetcher = new FetcherCore(policy); // 构造抓取器

				SinaRequestURIFormat requestURI = new SinaRequestURIFormat(task);
				try {
					if (!synTaskControll.synTaskBeforeCrawl(task)) {
						continue;
					}
					String source = fetcher.getResource(requestURI.getUrl(),
							requestURI.getParamsList());
					CrawResult content = new CrawResult(); // 解析抓取结果为内容和数量
					String resultStatus = extractor.isShouleExtract(source,
							content);

					synTaskControll.synTaskAfterCrawl(task, content,
							resultStatus); // 抓取后同步
					if (!resultStatus.equals(Extractor.COOKIE_WRONG)) {
						// 释放cookie重新进入队列
						preHandler.memoryCookie.reenQueue(policy
								.getCookieBean());
					}
					if (resultStatus.equals(Extractor.CAN_EXTRACT)) {
						extractor.innerExtractor(content, task); // 抽取内容并存储
						synTaskControll.sysTaskAfterExtractor(content, task); // 抽取后同步
					} else if (resultStatus.equals(Extractor.COOKIE_WRONG)) {
						// cookie失效，验证码出现了（cookie不能进入队列了）
						MailModel mail = new MailModel(policy);
						mailSend.send(mail);
						logger.error("CrawlController run- cookie"
								+ policy.getCookieBean().getUser()
								+ " is forbidded");
					} else if (resultStatus.equals(Extractor.END_TASK)) { // 没有搜索到结果
						synTaskControll.endOneTask(task, 0);
					}
					long costTime = System.currentTimeMillis() - startTime;
					logger.info("CrawlController run-task:" + task.toString()
							+ "；process of this time cost time:" + costTime
							+ " ms");
					if (costTime <= 3000) { // 操作低于消耗3s
						Thread.sleep(2000);// 休停2s
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
