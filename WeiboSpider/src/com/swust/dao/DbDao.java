package com.swust.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.swust.app.DataModel;
import com.swust.model.Item;
import com.swust.model.UserItem;

@Repository
public class DbDao {
	@Autowired
	private SqlSession sqlSession;
	public void setSqlSession(SqlSession sqlSession)throws Exception {
		this.sqlSession = sqlSession;
	}
	
	public int insertIntoItem(List<Item> itemList)throws Exception {
		return sqlSession.insert("depaItem.insertIntoItem", itemList);
	}
	public int insertIntoUserItem(List<UserItem> userItemList)throws Exception {
		return sqlSession.insert("depaItem.insertIntoUserItem", userItemList);
	}

	public List<DataModel> selectDatas()throws Exception  {
		return sqlSession.selectList("depaItem.selectTraindata");
	}

	public List<Long> selectUsers() {
		return sqlSession.selectList("depaItem.selectUsers");
	}

	public List<Long> selectItems() {
		return sqlSession.selectList("depaItem.selectItems");
	}
}
