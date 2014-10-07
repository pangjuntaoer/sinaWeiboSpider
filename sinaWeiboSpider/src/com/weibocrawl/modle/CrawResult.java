package com.weibocrawl.modle;

import org.jsoup.nodes.Document;

public class CrawResult {
final static int PAGE_SIZE=20;
private long resultCount;
private int totalPage;
private Document resultHtml;
public long getResultCount() {
	return resultCount;
}

public int getTotalPage() {
	return totalPage;
}

public void setTotalPage(int totalPage) {
	this.totalPage = totalPage;
}

public void setResultCount(long resultCount) {
	this.resultCount = resultCount;
	totalPage = (int) ((resultCount%PAGE_SIZE==0) ?resultCount/PAGE_SIZE : resultCount/PAGE_SIZE+1);
}
public Document getResultHtml() {
	return resultHtml;
}
public void setResultHtml(Document resultHtml) {
	this.resultHtml = resultHtml;
}

}
