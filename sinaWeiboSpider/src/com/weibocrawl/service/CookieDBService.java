package com.weibocrawl.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.weibocrawl.memory.MemoryCookie;
import com.weibocrawl.modle.CookieBean;
import com.weibocrawl.utils.CookieUtil;
import com.weibocrawl.utils.DBUtil;
@Component
public class CookieDBService {
	public static final Logger logger = LoggerFactory.getLogger(CookieDBService.class);
@Autowired
private DataSource dataSource;
public void setDataSource(DataSource dataSource) {
	this.dataSource = dataSource;
}
@Autowired
private DBUtil db;
public void setDb(DBUtil db) {
	this.db = db;
}
public List<CookieBean> loadCookie(){
	Connection conn = getConn();
	if(conn!=null){
		String strSQL="SELECT * FROM cookie";
		Collection<Map<String,String>> rows = db.executeQuery(conn, strSQL);
		Iterator<Map<String,String>> it = rows.iterator();
		List<CookieBean> cookieList=new ArrayList<CookieBean>();
		while(it.hasNext()){
			Map<String,String> map = it.next();
			if(map.get("cookie_value_text")==null||map.get("cookie_value_text").equals("")){
				continue;
			}
			CookieBean cookie = new CookieBean();
			cookie.setId(Integer.parseInt(map.get("cookie_id")));
			cookie.setPassword(map.get("cookie_user_password"));
			cookie.setUser(map.get("cookie_user_name"));
			cookie.setStrValue(map.get("cookie_value_text"));
			try {
				cookie.setCookieStore(CookieUtil.stringToCookie(cookie.getStrValue()));
			} catch (Exception e) {
				logger.error("CookieDBService loadCookie-账号:"+cookie.getUser()+"的cookie值有错误");
			}
			cookieList.add(cookie);
		}
		return cookieList;
	}
	return null;
}
private Connection getConn(){
	try {
		return dataSource.getConnection();
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return null;
}
}
