package com.weibocrawl.fetcher;

import java.util.List;

import org.apache.http.NameValuePair;

/**
 * 调用Http和Https协议通讯的接口
 */
public interface RequestAPI {
    /**
     * 使用Get方法发送API请求
     * 
     * @param url  远程API请求地址
     * @param paramsList 参数列表
     * @param oAuth OAuth鉴权信息
     * @return  Json 或 XML 格式的资源
     * @throws Exception
     */
    public String getResource(String url, List<NameValuePair> paramsList) throws Exception;
    
    /**
     * 使用Post方法发送API请求
     * 
     * @param url  远程API请求地址
     * @param paramsList 参数列表
     * @param oAuth OAuth鉴权信息
     * @return  Json 或 XML 格式的资源
     * @throws Exception
     */
    public String postContent(String url, List<NameValuePair> paramsList) throws Exception;
    
    /**
     * 使用Post方法发送API请求，并上传文件
     * 
     * @param url  远程API请求地址
     * @param paramsList 参数列表
     * @param files 需要上传的文件列表
     * @param oAuth OAuth鉴权信息
     * @return  Json 或 XML 格式的资源
     * @throws Exception
     */
    public String postFile(String url, List<NameValuePair> paramsList,
            List<NameValuePair> files) throws Exception;
    /**
     * 关闭连接管理器
     */
    public void shutdownConnection();

}
