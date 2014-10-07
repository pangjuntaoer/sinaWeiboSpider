package com.weibo.moniter;

import java.util.Date;

import com.weibocrawl.modle.CrawlPolicyBean;
import com.weibocrawl.utils.DateUtil;

public class MailModel {
private String subject;
private String content;

public MailModel(){
this.content="test......调用了无参数构造函数";
this.subject="test";
}

public MailModel(CrawlPolicyBean policy){
	this.subject="当前cookie失效，需要更新";
	this.content="需要更新的cookie是："+policy.getCookieBean().getUser()+"\n"
	+"\n**********************\n"+DateUtil.formatDateToString(new Date())
	+"\n该邮件由线程"+Thread.currentThread().getName()+"发送";
}
public MailModel(String content,String title){
	this.subject=title;
	this.content=content
	+"\n**********************\n"+DateUtil.formatDateToString(new Date())
	+"\n该邮件由线程"+Thread.currentThread().getName()+"发送";
}
public String getSubject() {
	return subject;
}
public void setSubject(String subject) {
	this.subject = subject;
}
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}

}
