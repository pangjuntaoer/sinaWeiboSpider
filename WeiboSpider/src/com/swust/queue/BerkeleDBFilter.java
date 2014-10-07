package com.swust.queue;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.mysql.jdbc.StringUtils;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockConflictException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;

public class BerkeleDBFilter {
	//数据库环境
    private  Environment myDbEnvironment = null;
    //数据库配置
    private  DatabaseConfig dbConfig=null;
//    //数据库游标
//    private  Cursor myCursor = null;
    //数据库对象
    private  Database myDatabase = null;
    //数据库文件名
    private  String fileName = "berkeleyDB";
    //数据库名称
    private  String dbName = "fetcheduri";
    
    public BerkeleDBFilter(){
    	this.openDatabase();
    }
    /*
     * 打开当前数据库
     */
    public  void openDatabase() {
        try{
            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setAllowCreate(true);
            envConfig.setTransactional(true);
            envConfig.setReadOnly(false);
            envConfig.setTxnTimeout(10000, TimeUnit.MILLISECONDS);
            envConfig.setLockTimeout(10000, TimeUnit.MILLISECONDS);
            /*
             *   其他配置 可以进行更改
                EnvironmentMutableConfig envMutableConfig = new EnvironmentMutableConfig();
                envMutableConfig.setCachePercent(50);//设置je的cache占用jvm 内存的百分比。
                envMutableConfig.setCacheSize(123456);//设定缓存的大小为123456Bytes
                envMutableConfig.setTxnNoSync(true);//设定事务提交时是否写更改的数据到磁盘，true不写磁盘。
                //envMutableConfig.setTxnWriteNoSync(false);//设定事务在提交时，是否写缓冲的log到磁盘。如果写磁盘会影响性能，不写会影响事务的安全。随机应变。
             *
             */
            File file = new File(fileName);
            if(!file.exists())
                file.mkdirs();
            myDbEnvironment = new Environment(file,envConfig);
            
            dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            dbConfig.setTransactional(true);
            dbConfig.setReadOnly(false);
            //dbConfig.setSortedDuplicates(false);
            /*
                setBtreeComparator 设置用于B tree比较的比较器，通常是用来排序
                setDuplicateComparator 设置用来比较一个key有两个不同值的时候的大小比较器。
                setSortedDuplicates 设置一个key是否允许存储多个值，true代表允许，默认false.
                setExclusiveCreate 以独占的方式打开，也就是说同一个时间只能有一实例打开这个database。
                setReadOnly 以只读方式打开database,默认是false.
                setTransactional 如果设置为true,则支持事务处理，默认是false，不支持事务。
            */
            if(myDatabase == null)
                myDatabase = myDbEnvironment.openDatabase(null, dbName, dbConfig);
            /*
             *  Database.getDatabaseName()
                取得数据库的名称
                如：String dbName = myDatabase.getDatabaseName();
                
                Database.getEnvironment()
                取得包含这个database的环境信息
                如：Environment theEnv = myDatabase.getEnvironment();
                
                Database.preload()
                预先加载指定bytes的数据到RAM中。
                如：myDatabase.preload(1048576l); // 1024*1024
                
                Environment.getDatabaseNames()
                返回当前环境下的数据库列表
                Environment.removeDatabase()
                删除当前环境中指定的数据库。
                如：
                String dbName = myDatabase.getDatabaseName();
                myDatabase.close();
                myDbEnv.removeDatabase(null, dbName);
                
                Environment.renameDatabase()
                给当前环境下的数据库改名
                如：
                String oldName = myDatabase.getDatabaseName();  
                String newName = new String(oldName + ".new", "UTF-8");
                myDatabase.close();
                myDbEnv.renameDatabase(null, oldName, newName);
                
                Environment.truncateDatabase()
                清空database内的所有数据，返回清空了多少条记录。
                如：
                Int numDiscarded= myEnv.truncate(null,
                myDatabase.getDatabaseName(),true);
                CheckMethods.PrintDebugMessage("一共删除了 " + numDiscarded +" 条记录 从数据库 " + myDatabase.getDatabaseName());
             */
        }
        catch(DatabaseException e){
           e.printStackTrace();
        }
    }
    
    /*
     * 向数据库中写入记录
     * 传入key和value
     */
    public  boolean writeToDatabase(String key,String value,boolean isOverwrite) {
    	try {
              //设置key/value,注意DatabaseEntry内使用的是bytes数组
              DatabaseEntry theKey=new DatabaseEntry(StringUtils.getBytes(key, "UTF-8"));
              DatabaseEntry theData=new DatabaseEntry(value.getBytes("UTF-8"));
              OperationStatus res = null;
              Transaction txn = null;
              try
              {
                  TransactionConfig txConfig = new TransactionConfig();
                  txConfig.setSerializableIsolation(true);
                  txn = myDbEnvironment.beginTransaction(null, txConfig);
                  if(isOverwrite)
                  {
                      res = myDatabase.put(txn, theKey, theData);
                  }
                  else
                  {
                      res = myDatabase.putNoOverwrite(txn, theKey, theData);
                  }
                  txn.commit();
                  if(res == OperationStatus.SUCCESS)
                  {
                      return true;
                  } 
                  else if(res == OperationStatus.KEYEXIST)
                  {
                      return false;
                  }
                  else 
                  {
                      return false;
                  }
              }
              catch(LockConflictException lockConflict)
              {
                  txn.abort();
                  return false;
              }
        }
        catch (Exception e) 
        {
            // 错误处理
        	e.printStackTrace();
            return false;
        }
    }
    
    /*
     * 关闭当前数据库
     */
    public  void closeDatabase() {
        if(myDatabase != null)
        {
            myDatabase.close();
        }
        if(myDbEnvironment != null)
        {
            myDbEnvironment.cleanLog(); 
            myDbEnvironment.close();
        }
    }
    
    /*
     * 删除数据库中的一条记录
     */
    public  boolean deleteFromDatabase(String key) {
        boolean success = false;
           long sleepMillis = 0;
        for(int i=0;i<3;i++)
        {
            if (sleepMillis != 0) 
            {
                try 
                {
                    Thread.sleep(sleepMillis);
                } 
                catch (InterruptedException e) 
                {
                    e.printStackTrace();
                }
                sleepMillis = 0;
            }
            Transaction txn = null;
            try
            {
                TransactionConfig txConfig = new TransactionConfig();
                txConfig.setSerializableIsolation(true);
                txn = myDbEnvironment.beginTransaction(null, txConfig);
                DatabaseEntry theKey;
                theKey = new DatabaseEntry(StringUtils.getBytes(key,"UTF-8"));
                OperationStatus res = myDatabase.delete(txn, theKey);
                txn.commit();
                if(res == OperationStatus.SUCCESS)
                {
                       success = true; 
                       return success;
                }
                else if(res == OperationStatus.KEYEMPTY)
                {
                     System.out.println("没有从数据库" + dbName +"中找到:"+key+"。无法删除");
                }
                else
                {
                	System.out.println("删除操作失败，由于"+res.toString());
                }
                return false;
            }
            catch (UnsupportedEncodingException e) 
            {
                e.printStackTrace();
                return false;
            }
            catch(LockConflictException lockConflict)
            {
                sleepMillis = 1000;
                
                continue;
            }
            finally 
            {
                 if (!success)
                 {
                      if (txn != null) 
                      {
                          txn.abort();
                      }
                 }
            }
        }
        return false;
    }
    
    /*
     * 从数据库中读出数据
     * 传入key 返回value
     */
    public String readFromDatabase(String key) {
        try {
             DatabaseEntry theKey = new DatabaseEntry(StringUtils.getBytes(key, "UTF-8"));
             DatabaseEntry theData = new DatabaseEntry();
             Transaction txn = null;
             try
             {
                 TransactionConfig txConfig = new TransactionConfig();
                 txConfig.setSerializableIsolation(true);
                 txn = myDbEnvironment.beginTransaction(null, txConfig);
                 OperationStatus res = myDatabase.get(txn, theKey, theData, LockMode.DEFAULT);
                 txn.commit();
                 if(res == OperationStatus.SUCCESS)
                 {
                     byte[] retData = theData.getData();
                     String foundData = new String(retData, "UTF-8");
                     return foundData;
                 }
                 else
                 {
                     return "";
                 }
             }
             catch(LockConflictException lockConflict)
             {
                 txn.abort();
                 return "";
             }
            
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    /*
     * 遍历数据库中的所有记录，返回list
     */
    public  ArrayList<String> getEveryItem() {
         Cursor myCursor = null;
         ArrayList<String> resultList = new ArrayList<String>();
         Transaction txn = null;
         try{
             txn = this.myDbEnvironment.beginTransaction(null, null);
             CursorConfig cc = new CursorConfig();
             cc.setReadCommitted(true);
             if(myCursor==null)
                 myCursor = myDatabase.openCursor(txn, cc);
             DatabaseEntry foundKey = new DatabaseEntry();
             DatabaseEntry foundData = new DatabaseEntry();         
             // 使用cursor.getPrev方法来遍历游标获取数据
             if(myCursor.getFirst(foundKey, foundData, LockMode.DEFAULT)
                     == OperationStatus.SUCCESS)
             {
                 String theKey = new String(foundKey.getData(), "UTF-8");
                 String theData = new String(foundData.getData(), "UTF-8");
                 resultList.add(theKey);
                 while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) 
                           == OperationStatus.SUCCESS) 
                 {
                        theKey = new String(foundKey.getData(), "UTF-8");
                        theData = new String(foundData.getData(), "UTF-8");
                        resultList.add(theKey);
                 }
             }
             myCursor.close();
             txn.commit();
             return resultList;
         } 
         catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();    
            return null;
         }
         catch (Exception e) 
         {
        	 e.printStackTrace();
             txn.abort();
             if (myCursor != null) 
             {
                 myCursor.close();
             }
             return null;
         }
    }
}
