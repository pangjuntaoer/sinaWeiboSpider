package com.swust.httputil;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.util.EntityUtils;

import com.swust.queue.CandidateURL;

/**
 * @author Pery
 * 自定义参数的Httpclient。<br>
 * 提供httpGet，httpPost两种传送消息的方式<br>
 * 提供httpPost上传文件的方式
 */
public class QHttpClient {

    // SDK默认参数设置
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int CON_TIME_OUT_MS = 10000;
    public static final int SO_TIME_OUT_MS = 10000;
    public static final int MAX_CONNECTIONS_PER_HOST = 20;
    public static final int MAX_TOTAL_CONNECTIONS = 200;
    
    private static String COOKIE="_hc.v='\"10293932-04f4-4798-982b-9fa3468f702f.1406359230\"';"
    		+ "abtest='48,124\\|52,133\\|47,122\\|44,106\\|45,115'; tc=8; PHOENIX_ID=0a010444-148ab7d9962-2989d5; s_ViewType=10; JSESSIONID=0EAEDF27D96560BB8DEF6ED5DBDADFA5; aburl=1; cy=8; cye=chengdu; __utma=1.871512914.1411199946.1411559439.1411626401.5; __utmb=1.19.10.1411626401; __utmc=1; __utmz=1.1411626401.5.3.utmcsr=baidu|utmccn=(organic)|utmcmd=organic|utmctr=%E5%A4%A7%E4%BC%97%E7%82%B9%E8%AF%84%E7%BD%91";
    // 日志输出
    private static Log log = LogFactory.getLog(QHttpClient.class);

    private DefaultHttpClient httpClient;
    public QHttpClient(){
    	 this(MAX_CONNECTIONS_PER_HOST, MAX_TOTAL_CONNECTIONS, CON_TIME_OUT_MS, SO_TIME_OUT_MS);
    }
    /**
     * 个性化配置连接管理器
     * @param maxConnectionsPerHost 设置默认的连接到每个主机的最大连接数
     * @param maxTotalConnections 设置整个管理连接器的最大连接数
     * @param conTimeOutMs  连接超时
     * @param soTimeOutMs socket超时
     * @param routeCfgList 特殊路由配置列表，若无请填null
     * @param crawlPolicy 抓取策略，包括了代理ip以及cookie信息
     */
    public QHttpClient(int maxConnectionsPerHost, int maxTotalConnections, int conTimeOutMs, int soTimeOutMs) {

        // 使用默认的 socket factories 注册 "http" & "https" protocol scheme
        SchemeRegistry supportedSchemes = new SchemeRegistry();
        supportedSchemes.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        supportedSchemes.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
        ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(supportedSchemes);

        // 参数设置
        HttpParams httpParams = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

        httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, conTimeOutMs);
        httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeOutMs);
        //与之前两行作用相同
//        HttpConnectionParams.setConnectionTimeout(httpParams, conTimeOutMs);
//        HttpConnectionParams.setSoTimeout(httpParams, soTimeOutMs);
        
        HttpProtocolParams.setUseExpectContinue(httpParams, false);

        connectionManager.setDefaultMaxPerRoute(maxConnectionsPerHost);
        connectionManager.setMaxTotal(maxTotalConnections);

        HttpClientParams.setCookiePolicy(httpParams, CookiePolicy.BROWSER_COMPATIBILITY);
        // 对特定路由修改最大连接数 
        httpClient = new DefaultHttpClient(connectionManager, httpParams);
    }

    /**
     * Get方法传送消息
     * 
     * @param url  连接的URL
     * @param queryString  请求参数串
     * @return 服务器返回的信息
     * @throws Exception
     */
    public String httpGet(CandidateURL url, String queryString) throws Exception {
        String responseData = null;
        String URI = url.getUrl();
        if (queryString != null && !queryString.equals("")) {
        	URI += "&" + queryString;
        }
        log.info("QHttpClient httpGet [1] url = " + url);
        System.out.println(url);
        HttpGet httpGet = new HttpGet(url.getUrl());
        httpGet.addHeader("Cookie", COOKIE);
        httpGet.getParams().setParameter("Referer", url.getReference());
        httpGet.getParams().setParameter("http.socket.timeout", new Integer(CONNECTION_TIMEOUT));
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.2)");
        //这里需要制定超时连接
        HttpResponse response = httpClient.execute(httpGet);
        try {
            log.info("QHttpClient httpGet [2] StatusLine : " + response.getStatusLine());
            responseData = EntityUtils.toString(response.getEntity());
            url.setStatus(response.getStatusLine().getStatusCode());
            if(responseData!=null){
            	url.setContent(responseData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpGet.abort();
        }
        return responseData;
    }
    
    
    
    /**
     * Post方法传送消息
     * 
     * @param url  连接的URL
     * @param queryString 请求参数串
     * @return 服务器返回的信息
     * @throws Exception
     */
    public String httpPost(String url, String queryString) throws Exception {
        String responseData = null;
        URI tmpUri = new URI(url);
        URI uri = URIUtils.createURI(tmpUri.getScheme(), tmpUri.getHost(), tmpUri.getPort(), tmpUri.getPath(),
                queryString, null);
        log.info("QHttpClient httpPost [1] url = " + uri.toURL());

        HttpPost httpPost = new HttpPost(uri);
        httpPost.getParams().setParameter("http.socket.timeout", new Integer(CONNECTION_TIMEOUT));
        if (queryString != null && !queryString.equals("")) {
            StringEntity reqEntity = new StringEntity(queryString);
            // 设置类型
            reqEntity.setContentType("application/x-www-form-urlencoded");
            // 设置请求的数据
            httpPost.setEntity(reqEntity);
        }
        
        try {
            HttpResponse response = httpClient.execute(httpPost);
            log.info("QHttpClient httpPost [2] StatusLine = " + response.getStatusLine());
            responseData = EntityUtils.toString(response.getEntity());
            log.info("QHttpClient httpPost [3] responseData = " + responseData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpPost.abort();
        }

        return responseData;
    }

    /**
     * Post方法传送消息
     * 
     * @param url  连接的URL
     * @param queryString 请求参数串
     * @return 服务器返回的信息
     * @throws Exception
     */
    public String httpPostWithFile(String url, String queryString, List<NameValuePair> files) throws Exception {

        String responseData = null;

        URI tmpUri = new URI(url);
        URI uri = URIUtils.createURI(tmpUri.getScheme(), tmpUri.getHost(), tmpUri.getPort(), tmpUri.getPath(),
                queryString, null);
        log.info("QHttpClient httpPostWithFile [1]  uri = " + uri.toURL());
        MultipartEntity mpEntity = new MultipartEntity();
        HttpPost httpPost = new HttpPost(uri);
        StringBody stringBody;
        FileBody fileBody;
        File targetFile;
        String filePath;
        FormBodyPart fbp;

        List<NameValuePair> queryParamList = QStrOperate.getQueryParamsList(queryString);
        for (NameValuePair queryParam : queryParamList) {
            stringBody = new StringBody(queryParam.getValue(), Charset.forName("UTF-8"));
            fbp = new FormBodyPart(queryParam.getName(), stringBody);
            mpEntity.addPart(fbp);
            // log.info("------- "+queryParam.getName()+" = "+queryParam.getValue());
        }

        for (NameValuePair param : files) {
            filePath = param.getValue();
            targetFile = new File(filePath);
            log.info("---------- File Path = " + filePath + "\n---------------- MIME Types = "
                    + QHttpUtil.getContentType(targetFile));
            fileBody = new FileBody(targetFile, QHttpUtil.getContentType(targetFile), "UTF-8");
            fbp = new FormBodyPart(param.getName(), fileBody);
            mpEntity.addPart(fbp);

        }

        // log.info("---------- Entity Content Type = "+mpEntity.getContentType());

        httpPost.setEntity(mpEntity);

        try {
            HttpResponse response = httpClient.execute(httpPost);
            log.info("QHttpClient httpPostWithFile [2] StatusLine = " + response.getStatusLine());
            responseData = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpPost.abort();
        }
        log.info("QHttpClient httpPostWithFile [3] responseData = " + responseData);
        return responseData;
    }

    /**
     * 断开QHttpClient的连接
     */
    public void shutdownConnection() {
        try {
            httpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
