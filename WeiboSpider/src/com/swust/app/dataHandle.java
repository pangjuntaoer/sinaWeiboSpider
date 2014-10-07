package com.swust.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.swust.dao.DbDao;
/**
 * 数据集处理
 * @author pery
 *
 */
public class dataHandle {
static Map<Long,Integer> user2Index=new TreeMap<Long,Integer>();
static Map<Long,Integer> item2Index=new TreeMap<Long,Integer>();
static Map<Long,String> item2Name=new TreeMap<Long,String>();
public static void main(String[] args) throws Exception {
	String xmlPath[]=new String[]{"beans.xml"};
	ApplicationContext ctx = new ClassPathXmlApplicationContext(xmlPath);
	DbDao db = ctx.getBean(DbDao.class);
	List<Long> userList = db.selectUsers();
	List<Long> itemList = db.selectItems();
	Collections.sort(userList);
	Collections.sort(itemList);
	List<DataModel> datas = db.selectDatas();
	ResultData []resultData= new ResultData[userList.size()+1];
	for (int i = 0; i < userList.size(); i++) {
		user2Index.put(userList.get(i), (i+1));
	}
	for (int i = 0; i < itemList.size(); i++) {
		item2Index.put(itemList.get(i), (i+1));
	}
	System.out.println("UserSize:"+userList.size());
	System.out.println("itemSize:"+itemList.size());
	System.out.println("resultData:"+resultData.length);
	
	for (int i = 0; i < datas.size(); i++) {
		DataModel d = datas.get(i);
		item2Name.put(d.getItemId(), d.getItemName());
		d.setItemName(null);//节约内存
		int uIndex = user2Index.get(d.getUserId());
		int iIndex = item2Index.get(d.getItemId());
		d.setItemId(iIndex);
		d.setUserId(uIndex);
		List<DataModel> uItems=null;
		try {
			ResultData user= resultData[uIndex];
			if(user==null){
				uItems=new ArrayList<DataModel>();
				user = new ResultData(uItems);
				resultData[uIndex]=user;
			}else{
				 uItems = user.getItems();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("resultSize:"+resultData.length+",uIndex："+uIndex);
		}
		uItems.add(d);
	}
	StringBuffer arrf=new StringBuffer("@RELATION dazong userSize:"+userList.size()
			+" itemSize:"+itemList.size()+"\n@ATTRIBUTE userId NUMERIC\n");
	StringBuffer dateStr=new StringBuffer();
	for (int i = 0; i < itemList.size(); i++) {
		arrf.append("@ATTRIBUTE 'rate for "+item2Name.get(itemList.get(i))+"["+(i+1)+"]' NUMERIC\n");
		dateStr.append("@ATTRIBUTE 'rate for "+item2Name.get(itemList.get(i))+"["+(i+1)+"]' STRING\n");
	}
	StringBuffer data=new StringBuffer("@DATA\n");
	Comparator  sortCompar =new ComparatorItem();
	for (int i = 0; i < userList.size(); i++) {
		int uIndex=(i+1);
		ResultData rd = resultData[uIndex];
		if(rd==null){
			continue;
		}
		List<DataModel> uItems = rd.getItems();
		if(uItems!=null){
			 Collections.sort(uItems, sortCompar);
			 StringBuffer urate = new StringBuffer("{0 ").append(uIndex).append(",");
			 StringBuffer udate = new StringBuffer("");
			 for (int j = 0; j < uItems.size(); j++) {
				 DataModel useritem=uItems.get(j);
				 urate.append(" ").append(useritem.getItemId()).append(" ").append(useritem.getRating())
				 	.append(",");
				 if(useritem.getItemId()<10){
					 udate.append(" 10");
				 }else if(useritem.getItemId()<100){
					 udate.append(" 1");
				 }
				 udate.append(useritem.getItemId()).append(" ")
				 .append(useritem.getDate());
				 if(j<uItems.size()-1){
					 udate.append(","); 
				 }
			}
			 urate.append(udate.toString()).append("}");
			 udate.setLength(0);
			 data.append(urate.toString()).append("\n");
			 urate.setLength(0);
		}
	}
	arrf.append(dateStr.toString()).append(data.toString());
	//写入结果
	RunAPP.appendContent2File("/home/pery/work/prea_eclipse_2_0/PreaRank/dazong.arff", arrf.toString());
}
}
class ComparatorItem implements Comparator<DataModel>{
	@Override
	public int compare(DataModel o1, DataModel o2) {
		return (int) (o1.getItemId()-o2.getItemId());
	}
	
}
class ResultData{
	List<DataModel> items;

	public ResultData(List<DataModel> uItems) {
		this.items = uItems;
	}

	public List<DataModel> getItems() {
		return items;
	}

	public void setItems(List<DataModel> uItems) {
		this.items = uItems;
	}
}

