package common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import com.alibaba.fastjson.JSONObject;

import models.finance.entity.t_return_status;
import models.finance.entity.t_return_status.ReportType;
import play.Logger;
import services.finance.ReturnStatusService;

/**
 * 金融办发送 https 工具类
 * @createDate 2108.10.16
 * */
public class JRBHttpsUtils {
	
	protected static ReturnStatusService returnStatusService = Factory.getService(ReturnStatusService.class);
		
	/**向金融办发送xml文件
	 * @param file 发送的xml文件
	 * @param reportType 按年份分报送类型（天，月，季）
	 * @author guoShiJie
	 * @createDate 2018.10.16
	 * */
	public static String post( File file , ReportType reportType) {
		try {
			//https://121.28.252.136/dcp/xmlparsing/upload   测试网址
			String url = "https://121.28.252.136/dcp_sc/xmlparsing/upload";
			String fileRquestParam = "file";
			Map<String, String> dataMap = new HashMap<String, String>();
			dataMap.put("md5", JRBMD5Utils.getFileMD5String(file));
			Response response = doPostFileRequest(url, dataMap, file, fileRquestParam);
			Logger.info(response.body());
			JSONObject json = JSONObject.parseObject(response.body());
			returnStatusService.add(file.getName(), reportType, json);
			return json.get("result").toString(); 
		} catch (Exception e) { 
			e.printStackTrace();
			String result = "0005";
			return result;
		}
	}

	/**
	 * @param url              请求的Url
	 * @param paramMap         参数
	 * @param file             文件
	 * @param fileRequestParam form表单对应的文件name属性名 使用Jsoup的方式实现
	 * @return
	 * @throws Exception
	 */
	public static Response doPostFileRequest(String url, Map<String, String> paramMap, File file, String fileRequestParam) throws Exception {
	    if (StringUtils.isBlank(url)) {
	        throw new Exception("The request URL is blank.");
	    }
	    // Https请求
	    if (StringUtils.startsWith(url, "https")) {
	        trustEveryone();
	    }
	    Connection connection = Jsoup.connect(url);
	    connection.method(Connection.Method.POST);
	    connection.timeout(12000);
	    connection.header("Content-Type", "multipart/form-data");
	    connection.ignoreHttpErrors(true);
	    connection.ignoreContentType(true);
	    if (paramMap != null && !paramMap.isEmpty()) {
	        connection.data(paramMap);
	    }
	    try {
	        FileInputStream fis = new FileInputStream(file);
	        connection.data(fileRequestParam, file.getName(), fis);
	        
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    try {
	        Response response = connection.execute();
	        if (response.statusCode() != 200) {
	            throw new Exception("http请求响应码:" + response.statusCode() + "");
	        }
	        return response;
	    } catch (IOException e) {
	        e.printStackTrace();	    
	    }
	    return null;
	}

	/**
	 * 解决Https请求,返回404错误
	 */
	private static void trustEveryone() {
	    try {
	        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        });
	        SSLContext context = SSLContext.getInstance("TLS");
	        context.init(null, new X509TrustManager[]{new X509TrustManager() {

	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub
					
				}

				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub
					
				}

				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}
	        }}, new SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
