package com.weibocrawl.writter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.weibocrawl.modle.WeiboBean;
import com.weibocrawl.utils.DBUtil;
@Component
public class Writter {
	@Autowired
	private DataSource dataSource;
	private DBUtil db;
	public DBUtil getDb() {
		return db;
	}
	public void setDb(DBUtil db) {
		this.db = db;
	}
	public void writteToDB(List<WeiboBean> weiboList) {
		String sql = "REPLACE INTO weibo (weibo_mid,"
				+ "weibo_user_name,weibo_user_image,weibo_domain,weibo_inner_html,"
				+ "weibo_context,weibo_url,weibo_time,weibo_from,book_id) VALUES ";
		for (int i = 0; i < weiboList.size(); i++) {
			sql += weiboList.get(i).toString()+",";
		}
		sql = sql.substring(0, sql.lastIndexOf(","));
		db.executeUpdate(sql, getConnection());
		return;
	}
	
	public void writterToDBbyPrepare(List<String[]> paraments){
		if(paraments.size()<=0){
			return;
		}
		String sql = "REPLACE INTO weibo (weibo_mid,"
				+ "weibo_user_name,weibo_user_image,weibo_domain,weibo_inner_html,"
				+ "weibo_context,weibo_url,weibo_time,weibo_from,book_id,book_title) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		db.prepareExecuteUpdateBatch(getConnection(), sql, paraments);
	}
	private Connection getConnection(){
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
