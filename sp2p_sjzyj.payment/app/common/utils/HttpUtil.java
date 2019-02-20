package common.utils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.shove.Convert;

import java.security.cert.*;

import common.constants.SettingKey;
import net.sf.json.JSONObject;
import play.Logger;
import services.common.SettingService;
import yb.YbConsts;

/**
 * 客户端工具类
 * 
 * @author niu
 * @create 2017.08.31
 */
public class HttpUtil {
	
	private static SettingService settingService = Factory.getService(SettingService.class);
	
	public static String getMethod(JSONObject params) {
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("请求报文体参数：" + params.toString());
		}
		 
		HttpEntity entity = null;
		StringBuffer buff = new StringBuffer();
		// 创建线程安全的httpClient
		CloseableHttpClient httpClient = HttpClients.createDefault();
		//HttpClient  httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());
		httpClient = (CloseableHttpClient) SSLSocketFactoryEx.getNewHttpClient();
		HttpPost httpPost = new HttpPost("http://192.168.0.188:9000/payment/yibincallback/asyn");
		try {				
			if (params != null) {
				StringEntity se = new StringEntity(params.toString(), YbConsts.URL_ENCODED);
				se.setContentType("application/json");//发送json数据需要设置ContentType
				httpPost.setEntity(se);
			}
							
			HttpResponse response = httpClient.execute(httpPost);
			
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				
				entity = response.getEntity();
				buff.append(EntityUtils.toString(entity,"UTF-8")); 
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			releaseSource(null, httpPost, httpClient);
		}
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("响应报文体参数：" + buff.toString());
		}
		
		
		return buff.toString();
		
	}
	
	 /**
	  * POST方法
	  * 
	  * @author niu
	  * @create 2017.09.01
	  */
	 public static String postMethod(JSONObject params){   
		 
		/* String test = "{\"inBody\":{\"accNo\":\"Lb2ZL7KnzYXp2k6ozEWQDQ==\",\"businessSeqNo\":\"A2017090508143055\",\"dealStatus\":\"1\","
		 		+ "\"note\":\"\",\"oldTxnType\":\"T00001\",\"oldbusinessSeqNo\":\"20170905153828000039\",\"respCode\":\"P2P0000\",\"respMsg\":\"处理成功\","
		 		+ "\"secBankaccNo\":\"\"},\"reqHeader\":{\"clientDate\":\"20170905\",\"clientSn\":\"A2017090508143054\",\"clientTime\":\"235743\","
		 		+ "\"fileName\":\"\",\"merchantCode\":\"zsjr\",\"signTime\":\"1504626961734\",\"signature\":\"GJ3PBnilY3QYUiEGFNlAnWXLWVdyD3W5s1e/uP4Rb91A/HubXA9XSpTpM9ljc/8/ij0ZNqlzRDSSk1XHPINI6VgpmrxS9LkrUae+UMGQuJq6rCjpmGD0JqkrzX0KLNHegXRYujglybAFOIK67275d1xy8kTS0vzxav+NXYiPpL8=\",\"txnType\":\"R00001\",\"version\":\"1.0\"}}";
		 
		 
		 
		 params = JSONObject.fromObject(test);*/
		 
	    if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
	    	Logger.info("请求报文体参数：" + params.toString());
		} 
		 
		HttpEntity entity = null;
		StringBuffer buff = new StringBuffer();
		// 创建线程安全的httpClient
		CloseableHttpClient httpClient = (CloseableHttpClient) wrapClient();
		//HttpClient  httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());
		//httpClient = (CloseableHttpClient) wrapClient();
		//HttpPost httpPost = new HttpPost("http://192.168.0.188:9003/payment/yibincallback/asyn");
		HttpPost httpPost = new HttpPost(YbConsts.YIBIN_URL);
		try {				
			if (params != null) {
				StringEntity se = new StringEntity(params.toString(), YbConsts.URL_ENCODED);
				se.setContentType("application/json");//发送json数据需要设置ContentType
				httpPost.setEntity(se);
			}
							
			CloseableHttpResponse response = httpClient.execute(httpPost);
			
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				
				entity = response.getEntity();
				buff.append(EntityUtils.toString(entity,"UTF-8")); 
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			releaseSource(null, httpPost, httpClient);
		}
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("响应报文体参数：" + buff.toString());
		} 
		
		
		return buff.toString();
	}
	 
	 /**
	  * get方法
	  * 
	  * @author liuyang
	  * @create 2017.11.08
	  */
	 public static String getsMethod(String nu){   
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("请求物流参数：" + nu);
		}
		 
		HttpEntity entity = null;
		StringBuffer buff = new StringBuffer();
		StringBuffer buffs = new StringBuffer();
		// 创建线程安全的httpClient
		CloseableHttpClient httpClient = (CloseableHttpClient) wrapClient();
		//HttpClient  httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());
		//httpClient = (CloseableHttpClient) wrapClient();
		
		String ids = settingService.findSettingValueByKey(SettingKey.EXPRESS_KEY);
		
		buffs.append("?id="+ids+"&nu="+nu);
		HttpGet httpGet = new HttpGet(YbConsts.LOGISTICS_URL+buffs.toString());
		try {				
			if (nu != null) {
				httpGet.setHeader("Accept", "application/json");
				httpGet.setHeader("Accept-Encoding", YbConsts.URL_ENCODED);
			}
							
			CloseableHttpResponse response = httpClient.execute(httpGet);
			
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				
				entity = response.getEntity();
				buff.append(EntityUtils.toString(entity,"UTF-8")); 
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			releaseSource(httpGet, null, httpClient);
		}
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("响应报文体参数：" + buff.toString());
		}
		
		return buff.toString();
	}
	 
	 
	 /**
	  * 释放资源
	  * 
	  * @author niu
	  * @create 2017.09.01
	  */
	 public static void releaseSource(HttpGet httpGet, HttpPost httpPost, CloseableHttpClient httpClient) {
			if (httpGet != null) {
				httpGet.abort();
			}
			if (httpPost != null) {
				httpPost.abort();
			}
			if (httpClient != null) {
				
				try {
					httpClient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
	public static HttpClient wrapClient() {  
	        try {  
	            SSLContext ctx = SSLContext.getInstance("TLS");  
	            X509TrustManager tm = new X509TrustManager() {  
	                public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
	                    return null;  
	                }  
	                
	                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}  
	                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
	                
					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
							throws java.security.cert.CertificateException {
						// TODO Auto-generated method stub
						
					}
					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
							throws java.security.cert.CertificateException {
						// TODO Auto-generated method stub
						
					}  
	                 
	            };  
	            /*ctx.init(null, new TrustManager[] { tm }, null);  
	            SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  
	            SchemeRegistry registry = new SchemeRegistry();  
	            registry.register(new Scheme("https", 443, ssf));  
	            registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));  
	            ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);
	            
	            return new DefaultHttpClient(mgr, base.getParams()); */ 
	            
	            ctx.init(null, new TrustManager[] { tm }, null);  
	            SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(  
	                    ctx, NoopHostnameVerifier.INSTANCE);  
	            CloseableHttpClient httpclient = HttpClients.custom()  
	                    .setSSLSocketFactory(ssf).build(); 
	            return httpclient;
	            
	        } catch (Exception ex) {  
	            ex.printStackTrace();  
	            return HttpClients.createDefault();  
	            
	        }  
	    }
	
}
