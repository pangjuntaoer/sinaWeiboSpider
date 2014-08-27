package com.weibocrawl.urlutil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.weibocrawl.httputil.QStrOperate;
import com.weibocrawl.modle.TaskBean;

public class SinaRequestURIFormat {
final static String REAL_TIME="STopic_realtime";//实时	
final static String COMPOSITE="g";//综合
final static String HOT_TOP="STopic_hotTopic";//热门必须有参数 xsort=hot
private String url;
private List<NameValuePair> paramsList;
private SinaRequestURIFormat(){}//禁用无参数构造方法
public SinaRequestURIFormat(TaskBean task){
	url="http://s.weibo.com/weibo/"+QStrOperate.paramEncode(task.getKeywordStr());
	paramsList = new ArrayList<NameValuePair>();
	paramsList.add(new BasicNameValuePair("timescope",fomatTimeScope(task)));
	paramsList.add(new BasicNameValuePair("Refer",COMPOSITE));
	paramsList.add(new BasicNameValuePair("topnav","1"));
	paramsList.add(new BasicNameValuePair("wvr","5"));

	paramsList.add(new BasicNameValuePair("page",String.valueOf(task.getNextPage())));
	paramsList.add(new BasicNameValuePair("retcode","6102?retcode=6102")); //直接跳转
	//paramsList.add(new BasicNameValuePair("xsort","hot"));
	
}
private String fomatTimeScope(TaskBean task){
	SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd-HH");
	String s = sd.format(task.getStartTime());
	String e=sd.format(task.getEndTime());
	if(task.getLastCrawlTime()!=null) //非第一轮抓取
	return "custom:"+s+":"+e; //(时间段)
	else
	return "custom::"+e;//只有结束时间，第一轮抓取
}
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
public List<NameValuePair> getParamsList() {
	return paramsList;
}
public void setParamsList(List<NameValuePair> paramsList) {
	this.paramsList = paramsList;
}
}
