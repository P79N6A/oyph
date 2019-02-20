package push;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import common.utils.LoggerUtil;

     
public class PushClient {
	
		/**用户代理*/
		protected final String USER_AGENT = "Mozilla/5.0";

		/**This object is used for sending the post request to Umeng*/
		protected CloseableHttpClient client = HttpClients.createDefault();
		
		/**The host*/
		protected static final String host = "http://msg.umeng.com";
		
		/**The upload path*/
		protected static final String uploadPath = "/upload";
		
		/**The post path*/
		protected static final String postPath = "/api/send";

		/**
		 * 推送消息
		 * @param msg 消息
		 * @author guoshijie
		 * @create 2017.12.7
		 * */
		public boolean send(UmengNotification msg) throws Exception {
			
			boolean flag = false;
			
			/**设置发送时间*/
			String timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
			msg.setPredefinedKeyValue("timestamp", timestamp);
			
			/**请求地址*/
	        String url = host + postPath;
	        String postBody = msg.getPostBody();
	        
	        /**生成签名*/
	        String sign = DigestUtils.md5Hex(("POST" + url + postBody + msg.getAppMasterSecret()).getBytes("utf8"));
	        url = url + "?sign=" + sign;
	        LoggerUtil.info(false,url);
	        HttpPost post = new HttpPost(url);
	        post.setHeader("User-Agent", USER_AGENT);
	        StringEntity se = new StringEntity(postBody, "UTF-8");
	        post.setEntity(se);
	        
	        /**向客户端发送请求*/
	        CloseableHttpResponse response = client.execute(post);
	        
	        /**返回请求结果*/
	        int status = response.getStatusLine().getStatusCode();
	        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	        StringBuffer result = new StringBuffer();
	        String line = "";
	        while ((line = rd.readLine()) != null) {
	            result.append(line);
	        }
	        LoggerUtil.info(false, "Response Code : "+status+" result:"+result.toString());
	        if (status == 200) {
	            LoggerUtil.info(false, "Notification sent successfully.");
	            flag = true;
	        } else {
	            LoggerUtil.info(false, "Failed to send the notification!");
	        }
	        return flag;
	    }

		/**Upload file with device_tokens to Umeng
		 * @param appkey 
		 * @param appMasterSecret
		 * @param contents 
		 * @author guoshijie
		 * */
		public String uploadContents(String appkey,String appMasterSecret,String contents) throws Exception {
			
			/**Construct the json string*/
			JSONObject uploadJson = new JSONObject();
			uploadJson.put("appkey", appkey);
			String timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
			uploadJson.put("timestamp", timestamp);
			uploadJson.put("content", contents);
			
			/**Construct the request*/
			String url = host + uploadPath;
			String postBody = uploadJson.toString();
			String sign = DigestUtils.md5Hex(("POST" + url + postBody + appMasterSecret).getBytes("utf8"));
			url = url + "?sign=" + sign;
			HttpPost post = new HttpPost(url);
			post.setHeader("User-Agent", USER_AGENT);
			StringEntity se = new StringEntity(postBody, "UTF-8");
			post.setEntity(se);
			
			/**Send the post request and get the response*/
			CloseableHttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			
			/**Decode response string and get file_id from it*/
			JSONObject respJson = new JSONObject(result.toString());
			String ret = respJson.getString("ret");
			if (!ret.equals("SUCCESS")) {
				throw new Exception("Failed to upload file");
			}
			JSONObject data = respJson.getJSONObject("data");
			String fileId = data.getString("file_id");
			/**Set file_id into rootJson using setPredefinedKeyValue*/
			
			return fileId;
		}
}
