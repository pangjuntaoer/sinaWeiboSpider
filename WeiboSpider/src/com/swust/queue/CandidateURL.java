package com.swust.queue;

import org.apache.commons.codec.digest.DigestUtils;

public class CandidateURL {
	private String url;
	private String fingerprint;
	private String urlMd5;
	private String content;
	private int status;//抓取状态码
	private String reference;
	private boolean isSeed=false;
	private int fetchCount=0;
	public CandidateURL(String url,boolean isSeed){
		this.url=url;
		this.isSeed=isSeed;
		this.urlMd5 = DigestUtils.md5Hex(this.url);
	}
	public CandidateURL(String url){
		this.url = url;
		this.urlMd5 = DigestUtils.md5Hex(this.url);
	}
	public CandidateURL(String reference, String urlinks) {
		this.reference = reference;
		this.url=urlinks;
		this.urlMd5 = DigestUtils.md5Hex(this.url);
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFingerprint() {
		return fingerprint;
	}
	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
		//暂时不用加密
		//this.fingerprint=DigestUtils.shaHex(content);
	}
	public boolean isSeed() {
		return isSeed;
	}
	public void setSeed(boolean isSeed) {
		this.isSeed = isSeed;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getFetchCount() {
		return fetchCount;
	}
	public void setFetchCount(int fetchCount) {
		this.fetchCount = fetchCount;
	}
	public void addFetchCount() {
		this.fetchCount++;
	}
	public String getUrlMd5() {
		return urlMd5;
	}
	public void setUrlMd5(String urlMd5) {
		this.urlMd5 = urlMd5;
	}
	
}
