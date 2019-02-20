package com.citic.wx.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 
 *
 * @ClassName: WeChatUtils
 *
 * @description 微信公众号工具类
 *
 * @author HanMeng
 * @createDate 2018年12月29日-下午3:47:47
 */
public class WeChatUtils {

	public static String PostSendMsg(JSONObject json, String url) {
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/json");
		post.addHeader("Authorization", "Basic YWRtaW46");
		String result = "";
		try {
			StringEntity s = new StringEntity(json.toString(), "utf-8");
			s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setEntity(s);
			// 发送请求
			HttpResponse httpResponse = HttpClients.createDefault().execute(post);
			// 获取响应输入流
			InputStream inStream = httpResponse.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
			StringBuilder strber = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
				strber.append(line + "\n");
			inStream.close();

			result = strber.toString();
			System.out.println("result"+result);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				System.out.println("请求服务器成功，做相应处理");
			} else {
				System.out.println("请求服务端失败");
			}
		} catch (Exception e) {
			System.out.println("请求异常");
			throw new RuntimeException(e);
		}
		return result;
	}

	/*public static AccessToken getAccessToken(String appid, String appsecret) {
		AccessToken accessToken = null;
		// 获取access_token的接口地址（GET） 限200（次/天）
		String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
		String requestUrl = access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
		System.out.println("requestUrl"+requestUrl);
		JSONObject jsonObject = JSONObject.fromObject(sendGet(requestUrl));
		// 如果请求成功
		if (null != jsonObject) {
			try {
				accessToken = new AccessToken();
				accessToken.setToken(jsonObject.getString("access_token"));
				accessToken.setExpiresIn(jsonObject.getInt("expires_in"));
			} catch (JSONException e) {
				accessToken = null;
				// 获取token失败
				System.out.println("获取token失败 errcode:" + jsonObject.getInt("errcode") + " errmsg:"
						+ jsonObject.getString("errmsg"));
			}
		}
		return accessToken;
	}
*/
	public static String sendGet(String url) {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			// Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			// for (String key : map.keySet()) {
			// System.out.println(key + "--->" + map.get(key));
			// }
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		System.out.println();
		return result;
	}
	/**
	 * 
	 * @Title: getOpenId 
	 * @Description: 根据网页授权获取用户openid
	 * @param @param code appId secret
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	public static String getOpenId(String code,String appId,String secret) {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appId+"&secret="+secret+"&code="
				+ code + "&grant_type=authorization_code";
		String openId = "";
		try {
			URL getUrl = new URL(url);
			HttpURLConnection http = (HttpURLConnection) getUrl.openConnection();
			http.setRequestMethod("GET");
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			http.connect();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] b = new byte[size];
			is.read(b);
			String message = new String(b, "UTF-8");
			JSONObject json = JSONObject.fromObject(message);
			openId = json.getString("openid");
			System.out.println("openId"+openId);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return openId;
	}
}
