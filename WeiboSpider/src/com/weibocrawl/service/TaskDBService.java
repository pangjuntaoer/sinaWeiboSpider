package com.weibocrawl.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.weibocrawl.extractor.Extractor;
import com.weibocrawl.memory.TaskOfMemory;
import com.weibocrawl.memory.TaskQueue;
import com.weibocrawl.modle.MemoryOfTaskBean;
import com.weibocrawl.modle.TaskBean;
import com.weibocrawl.modle.WeiboBean;
import com.weibocrawl.utils.DBUtil;
import com.weibocrawl.utils.DateUtil;

@Component
public class TaskDBService {
	public static final Logger logger = LoggerFactory.getLogger(TaskDBService.class);	
	@Autowired
	private DataSource dataSource;
	private DBUtil db;
	private String taskFilePath;
	private String loadCount;
	@Value("10")
	public void setLoadCount(String loadCount) {
		this.loadCount = loadCount;
	}

	public void setTaskFilePath(String taskFilePath) {
		this.taskFilePath = taskFilePath;
	}

	public DBUtil getDb() {
		return db;
	}

	public void setDb(DBUtil db) {
		this.db = db;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<TaskBean> loadTasks() {
		List<TaskBean> taskList = new ArrayList<TaskBean>();
		Connection connection = getConnection();
		String sql = "SELECT * From crawl_task WHERE cra_status=2 LIMIT "+loadCount;
		if (db.execCount(sql, getConnection())<=0) {
			updateTaskReadStatus();
		}
		Statement st = null;
		ResultSet rs = null;
		try {
			 st = connection.createStatement();
			 rs = st.executeQuery(sql);
			 if(!rs.wasNull()){
				 resultSetParser(rs,taskList);
			 }
		} catch (SQLException e) {
			logger.info("TaskDBService loadTasks-DB Handler fail；"+e.getMessage());
			return null;
		}finally{
			try {
				rs.close();
				st.close();
				connection.close();
			} catch (SQLException e) {
				logger.info("TaskDBService loadTasks-DB Handler fail；"+e.getMessage());
			}
		}
		return taskList;
	}
	private void resultSetParser(ResultSet rs,List<TaskBean> taskList) throws SQLException{
		while (rs.next()) {
			TaskBean task = new TaskBean();
			boolean isNeedUpdateTime = false;
			task.setTaskId(rs.getInt("cra_id"));
			task.setKeywordStr(rs.getString("cra_keyword"));
			task.setOriginTime(rs.getDate("cra_orig_time"));
			task.setHop(rs.getInt("cra_gap"));
			Date lastCrawlTime = rs.getDate("cra_lastcrawl_time");
			Date startTime = rs.getDate("start_time");
			Date endTime = rs.getDate("end_time");
			Date fistWbTime = rs.getDate("first_wb_time");
			task.setFirstWbTime(fistWbTime);
			int currentPage = rs.getInt("next_page");
			if (lastCrawlTime == null) { // 第一轮，
				if (currentPage == 1 && startTime == null && endTime == null) { // 更新时间需要
					startTime = task.getOriginTime();
					endTime = new Date();
					// 更新DB时间参数
					isNeedUpdateTime = true;
				}// 否则时间都不为空则可能是是第一轮抓取过程，超过50页时间
			} else { // 非第一轮
				if (currentPage == 1) {
					startTime = (fistWbTime==null)?lastCrawlTime:fistWbTime;
					endTime = new Date();
					isNeedUpdateTime = true;
				}
			}

			task.setLastCrawlTime(lastCrawlTime);
			task.setIsEndEveryWheel(rs.getInt("cra_is_end"));
			task.setStartTime(startTime);
			task.setEndTime(endTime);
			task.setNextPage(currentPage);
			task.setLastWeiboTimeOfPage50(rs.getDate("last_weibo_time"));
			task.setTempTime(rs.getDate("temp_time"));
			
			updateTaskTime(isNeedUpdateTime, task);
			taskList.add(task);
		}
	}
	
	private void updateTaskReadStatus() {
		String sql = "UPDATE crawl_task set cra_status=2 WHERE cra_status=3";
		db.executeUpdate(sql, getConnection());
	}

	private void updateTaskTime(boolean isNeedUpdateTime, TaskBean task) {
		String sql = "";
		if (!isNeedUpdateTime) {
			sql = "UPDATE crawl_task SET cra_status=3 WHERE cra_id=" + task.getTaskId();
		} else {
			sql = "UPDATE crawl_task SET cra_status=3, start_time='" + DateUtil.formatDateToString(task.getStartTime())
					+ "',temp_time='" + DateUtil.formatDateToString(task.getEndTime()) + "',end_time='"
					+ DateUtil.formatDateToString(task.getEndTime()) + "' WHERE cra_id=" + task.getTaskId();
		}
		db.executeUpdate(sql, getConnection());
	}

	private Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void updateTimeParameters(TaskBean task, WeiboBean weibo) throws Exception {
		String sql = "UPDATE crawl_task SET next_page=1, end_time='" + weibo.getWeiboTime() + "' WHERE cra_id=" + task.getTaskId();
		logger.info("TaskDBService updateTimeParameters-超过50页码翻页了，task:"+task.getTaskId()+" begin to process page 50!");
		db.executeUpdate(sql, getConnection());
	}
	/**
	 * 结束一轮任务
	 * @param task
	 * @param resultStatus 
	 */
	public void endOneTask (TaskBean task, int pageCount) throws Exception  {
		MemoryOfTaskBean taskMemory = TaskOfMemory.memoryTask.get(task.getTaskId());
		String lastTime = DateUtil.formatDateToString(taskMemory.getLastUpdateTime());
		String thisFirstWbTime = taskMemory.getFirstPageFirstWbTime();
		if(lastTime==null||lastTime.equals("")){
			lastTime = "temp_time";
		}else{
			lastTime = "'"+lastTime+"'";
		}
		
		if(thisFirstWbTime==null||thisFirstWbTime.equals("")){
			thisFirstWbTime = "temp_time";
		}else{
			thisFirstWbTime = "'"+thisFirstWbTime+"'";
		}
		int gap = calculateCrawlPeriod(pageCount);
		String sql = "UPDATE crawl_task SET cra_lastcrawl_time="+lastTime+",start_time="+lastTime+
				",next_page=1, cra_gap="+gap+", cra_is_end=cra_is_end+1, cra_status=2,first_wb_time="+thisFirstWbTime+"  WHERE"
				+ " cra_id="+task.getTaskId();
		db.executeUpdate(sql, getConnection());
	}

	/**
	 * 根据抓取总页数计算抓取周期
	 * @return
	 */
	private int calculateCrawlPeriod(int pageCount){
		if(pageCount<=5){
			return 1440*10;//10天一次
		}else if(pageCount<=10){
			return 1440*5;//5天一次
		}else if(pageCount<=30){
			return 1440;
		}else if(pageCount<=50){
			return 720;
		}else
			return 20;
	}
	
	public void updateNextPage(TaskBean task) {
		String sql = "UPDATE crawl_task SET next_page=next_page+1 WHERE"
				+ " cra_id="+task.getTaskId();
		db.executeUpdate(sql, getConnection());
	}
	
	
	public void insertTask(){
		File file = new File(taskFilePath);
		if(file!=null){
			 try {
				   BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
				   String temp =null; 
				   int i=0;
				   String sqlValue="";
				   while((temp=br.readLine())!=null){
					   i++;
					   String t = new String(temp.trim().getBytes("utf-8"), "utf-8");
					   sqlValue +="('"+temp.trim()+"'),";
					   if(i%100==0){
						   insertInto(sqlValue.substring(0, sqlValue.length()-1));
						   i=0;
						   sqlValue = "";
					   }
				   }
				  } catch (FileNotFoundException e) {
				   e.printStackTrace();
				  } catch (IOException e) {
				   e.printStackTrace();
				  }
		}
	}
	private void insertInto(String value){
		String sql="INSERT into crawl_task(cra_keyword) VALUE "+value+";";
/*		File fileName = new File("D:\\sql.sql");
		try {
			   FileWriter fw = new FileWriter(fileName, true);
			   BufferedWriter bw = new BufferedWriter(fw);
			   bw.write(sql+"\n");
			   bw.flush();
			   bw.close();
			   fw.close();
			  } catch (IOException e) {
			   e.printStackTrace();
			  }*/
		db.executeUpdate(sql, getConnection());
	}
}
