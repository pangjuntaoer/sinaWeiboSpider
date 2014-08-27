package com.weibocrawl.modle;

import java.util.Date;

import com.weibocrawl.utils.DateUtil;

public class WeiboBean {
private String weiboId;
private String userName;
private String domain;
private String userImage;

private String innerHtml=""; //正文html包含评论统计，以及图片
private String context; //仅仅正文文字，

private String weiboUrl;
private String weiboTime;
private int repostCount; //暂时不启用
private int commentCount; //暂时不启用
private String weiboFrom;

private int bookId;//图书ｉｄ
private String bookTitle;//图书标题
/**
 * 将对象转换为('','',''.....)
 */
@Override
public String toString(){
	if(this.weiboTime==null){
		this.weiboTime=DateUtil.formatDateToString(new Date());
	}
	String s="('";
	s+= 	this.weiboId+"','"+
			this.userName+"','"+
			this.userImage+"','"+
			this.domain+"','"+
			this.innerHtml+"','"+
			this.context+"','"+
			this.weiboUrl+"','"+
			this.weiboTime+"','"+
			this.weiboFrom+"',"+
			this.bookId;
	return s+")";
}
/**
 * 将对象转换为字符串数组
 * @return
 */
public String [] toArrayString(){
	String [] values = new String[11];
	values[0] = this.weiboId;
	values[1] = this.userName;
	values[2] = this.userImage;
	values[3] = this.domain;
	values[4] = this.innerHtml;
	values[5] = this.context;
	values[6] = this.weiboUrl;
	values[7] = this.weiboTime;
	values[8] = this.weiboFrom;
	values[9] = String.valueOf(this.bookId);
	values[10] = this.bookTitle;
	return values;
}
public String getWeiboId() {
	return weiboId;
}
public void setWeiboId(String weiboId) {
	this.weiboId = weiboId;
}
public String getDomain() {
	return domain;
}
public void setDomain(String domain) {
	this.domain = domain;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public String getUserImage() {
	return userImage;
}
public void setUserImage(String userImage) {
	this.userImage = userImage;
}
public String getContext() {
	return context;
}
public void setContext(String context) {
	this.context = context;
}
public String getWeiboUrl() {
	return weiboUrl;
}
public void setWeiboUrl(String weiboUrl) {
	this.weiboUrl = weiboUrl;
}
public String getWeiboTime() {
	return weiboTime;
}
public void setWeiboTime(String weiboTime) {
	this.weiboTime = weiboTime;
}
public int getRepostCount() {
	return repostCount;
}
public void setRepostCount(int repostCount) {
	this.repostCount = repostCount;
}
public int getCommentCount() {
	return commentCount;
}
public void setCommentCount(int commentCount) {
	this.commentCount = commentCount;
}
public String getWeiboFrom() {
	return weiboFrom;
}
public void setWeiboFrom(String weiboFrom) {
	this.weiboFrom = weiboFrom;
}
public String getInnerHtml() {
	return innerHtml;
}
public void setInnerHtml(String innerHtml) {
	this.innerHtml = innerHtml;
}
public int getBookId() {
	return bookId;
}
public void setBookId(int bookId) {
	this.bookId = bookId;
}
public String getBookTitle() {
	return bookTitle;
}
public void setBookTitle(String bookTitle) {
	this.bookTitle = bookTitle;
}
}
