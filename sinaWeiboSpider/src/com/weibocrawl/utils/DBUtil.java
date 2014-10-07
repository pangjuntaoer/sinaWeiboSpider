package com.weibocrawl.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibocrawl.memory.TaskQueue;

public class DBUtil {
private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);		
public Collection<Map<String,String>> executeQuery(Connection conn,String strSQL){
    Statement statement = null;
    ResultSet resultSet = null;
    Collection<Map<String,String>> collection = new LinkedList<Map<String,String>>();
    try{
        statement = conn.createStatement();
        resultSet=statement.executeQuery(strSQL);
        ResultSetMetaData rsmd=resultSet.getMetaData();
        while(resultSet.next()){                 
            Map<String,String> hasMap = new HashMap<String,String>();
            for(int i=1;i<=rsmd.getColumnCount();i++){
                hasMap.put(rsmd.getColumnLabel(i),resultSet.getString(i));
            }
            collection.add(hasMap);
        }
    }catch (SQLException ex){
        ex.printStackTrace() ;
        return null;
    }finally{
        closeAll(conn,statement,resultSet);
    }
    return collection;
}
/**
 * 执行返回的个数
 * @param sql
 * @return
 */
public int execCount(String sql,Connection conn){
	int count = 0;
	Statement stmt = null;
	ResultSet rs = null; 
	try {
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql);
		if(rs.next()) count = rs.getInt(1);
	} catch (SQLException e) {
		e.printStackTrace();
	}finally {
		closeAll(conn, stmt,null);
    } 
	return count;
}
public int executeUpdate(String sql,Connection con) {
    Statement st = null;
    try {
            st = con.createStatement();
            return st.executeUpdate(sql);
    } catch (SQLException e) {
           logger.error("DBUtil  executeUpdate-" + e.getMessage());
    } finally {
    	closeAll(con, st,null);
    }
    return -1;
}
public void closeAll (Connection conn, Statement stmt, ResultSet rs) {
	try {
		if (rs != null){
			rs.close();
			rs = null;
		}
		if (stmt != null) {
			stmt.close();
			stmt = null;
		}
		if (conn != null){
			conn.close();
			conn = null;
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
}
/**
 * Prepare方式批量更新
 * @param sql
 * @param paramsList
 * @param con
 * @return
 */
public int prepareExecuteUpdateBatch(Connection con,String sql, List<String[]> paramsList) {
    PreparedStatement pst = null;
    int count = 0;
    try {
        pst = con.prepareStatement(sql);
        for (String[] params : paramsList) {
            for (int i = 1; i <= params.length; i++) {
                pst.setString(i, params[i-1]);
             }
               pst.addBatch();
                count ++;
         }
        pst.executeBatch();
        }catch (SQLException e) {
        logger.error("DBUtil prepareExecuteUpdateBatch-" + "sql:" +e.getMessage());
    } finally {
        closeAll(con, pst,null);
    }
    return count;
}

}