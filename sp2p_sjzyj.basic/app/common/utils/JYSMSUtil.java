package common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.shove.Convert;

import java.util.Map.Entry;

import common.constants.Constants;
import common.constants.SettingKey;
import net.sf.json.JSONObject;
import play.Logger;
import play.cache.Cache;
import services.common.SettingService;

/**
 * 焦云发送短息Util
 * @author guoShiJie
 * @createDate 2018.7.2
 * */
public class JYSMSUtil {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);

	/**
	 * 发送验证码
	 * @author guoShiJie
	 * @param mobile 手机号
	 * @param scene 为tpl_id模板id
	 * @param effectiveTime 缓存时间
	 * @param isSend 能否发送验证码短息
	 * */
	public static boolean sendCode (String mobile,String scene,String effectiveTime,boolean isSend) {
		int randomCode = (new Random()).nextInt(899999) + 100000;// 最大值位999999
		
		/* 判断验证码在有效期内是否存在于缓存中 */
		Object cache = Cache.get(mobile + scene);
		
		Map<String,String> map = new HashMap<String,String>();
    	if(cache != null){
			map.put("code", cache.toString());
    	}else {
			map.put("code", randomCode+"");
    		Cache.set(mobile + scene, randomCode + "", effectiveTime);
		}
    	map.put("time", "30");
    	Logger.info("平台验证码:"+map.get("code"));
    	/* 是否发送短信 */
		if(isSend){
			return JYSMSUtil.sendMessage(mobile, scene, map);
		}
		
		return false;
	}
	
	/**
	 * 发送验证码
	 * @author guoShiJie
	 * @param mobile 手机号
	 * @param scene 场景
	 * @param effectiveTime 缓存时间
	 * @param isSend 是否发送短息
	 * @param tpl_id 模板id
	 * */
	public static boolean sendCode (String mobile,String scene,String effectiveTime,boolean isSend, String tpl_id) {
		int randomCode = (new Random()).nextInt(899999) + 100000;// 最大值位999999
		
		/* 判断验证码在有效期内是否存在于缓存中 */
		Object cache = Cache.get(mobile + scene);
		
		Map<String,String> map = new HashMap<String,String>();
    	if(cache != null){
			map.put("code", cache.toString());
    	}else {
			map.put("code", randomCode+"");
    		Cache.set(mobile + scene, randomCode + "", effectiveTime);
		}
    	map.put("time", "30");
    	Logger.info("手机号:"+mobile+" 平台验证码:"+map.get("code"));
    	/* 是否发送短信 */
    	if(isSend){
			return JYSMSUtil.sendMessage(mobile, tpl_id, map);
		}
		
		return false;
	}
	
	/**
	 * 发送短信
	 * @author guoShiJie
	 * @createDate 2018.7.2
	 * @param phone 手机号
	 * @param tpl_id 模板id
	 * @param tpl_value 发送参数值
	 * */
	public static boolean sendMessage (String phone, String tpl_id, Map<String,String> tpl_value) {
		if (phone == null || tpl_id == null) {
			LoggerUtil.info(false, "无手机号，和模板id，不能发送短息");
			return false;
		}
		HashMap<String,Object> map = new HashMap<String, Object>();
		map.put("action", "verification");
		map.put("phone", phone);
		map.put("tpl_id", tpl_id);
		map.put("tpl_value", JYSMSUtil.createTpl_value(tpl_value));
		
		String result = JYSMSUtil.sendPost(map, "http://121.201.72.233:8081/sms.php");
		Logger.info("%s", result);
		JSONObject jsonStr = JSONObject.fromObject(result);
		String code = jsonStr.getString("code");
		if (code.equals("100")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取accessToken值
	 * @author guoShiJie
	 * @createDate 2018.7.10
	 * */
	/*public static String sendAccessToken () {
		Object access = Cache.get("accessToken");
		if (access != null) {
			return access.toString();
		}
		
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("grant_type", "client_credentials");
		map.put("client_id", "e14987773b8848d748881718df5361fa");
		map.put("client_secret", "03741585e5d2be0f5990b151319a22ec");
		
		String result = JYSMSUtil.sendPost(map, "http://oauth.cloudfoci.com/token.php");
		JSONObject jsonStr = JSONObject.fromObject(result);
		Cache.set("accessToken", jsonStr.get("access_token"), "1mn");
		return jsonStr.getString("access_token");
		
	}*/
	
	/**
	 * createTpl_value 获取tpl_value格式值(将map类型的值转换为指定格式字符串，并用urlencoder编码)
	 * @author guoShiJie
	 * @createDate 2018.7.10
	 * **/
	public static String createTpl_value (Map<String,String> tpl_value) {
		StringBuffer params  = new StringBuffer();
		if (tpl_value != null && tpl_value.size() > 0) {
			Iterator<Entry<String, String>> it = tpl_value.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				params.append("#"+entry.getKey()+"#="+entry.getValue()+"&");
			}
			String result = params.substring(0, params.length()-1);
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("tpl_value:"+result);
			}
			
			return URLEncoder.encode(result) ;
		}
		return null;
	}
	
	/**
	 * post方式发送短息
	 * @param params 发送参数
	 * @param jyUrl 发送地址
	 * @author guoShiJie
	 * @createDate 2018.7.10
	 * */
	public static String sendPost (HashMap<String,Object> params,String jyUrl) {
		
		StringBuffer resultBuffer = null;
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> e : params.entrySet()) {
				sbParams.append(e.getKey());
				sbParams.append("=");
				//注意这些需要编码
				sbParams.append(URLEncoder.encode(e.getValue()+""));
				sbParams.append("&");
			}
		}
		String param = sbParams.substring(0, sbParams.length() - 1);
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("params:%s",param);
		}
		
		HttpURLConnection con = null;
        OutputStreamWriter osw = null;
        BufferedReader br = null;
        // 发送请求
        try {
        	URL url = new URL(jyUrl);
        	con = (HttpURLConnection) url.openConnection();
        	con.setRequestMethod("POST");
        	con.setRequestProperty("Authorization", JYAPIUtil.Authorization(params));

        	con.setDoOutput(true);
        	con.setDoInput(true);
        	con.setUseCaches(false);
            //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        	if (param != null && param.length() > 0) {
        		osw = new OutputStreamWriter(con.getOutputStream(), "utf-8");
        		osw.write(param);
        		osw.flush();
        	}
            // 读取返回内容
        	resultBuffer = new StringBuffer();

        	br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
        	String temp;
        	while ((temp = br.readLine()) != null) {
        		resultBuffer.append(temp);
        	}

        } catch (Exception e) {

        	throw new RuntimeException(e);
        } finally {
        	if (osw != null) {
        		try {
        			osw.close();
        		} catch (IOException e) {
        			osw = null;
        			throw new RuntimeException(e);
        		} finally {
        			if (con != null) {
        				con.disconnect();
        				con = null;
        			}
        		}
        	}
        	if (br != null) {
        		try {
        			br.close();
        		} catch (IOException e) {
        			br = null;
        			throw new RuntimeException(e);
        		} finally {
        			if (con != null) {
        				con.disconnect();
        				con = null;
        			}
        		}
        	}
        }

        return resultBuffer.toString();
	}
	
	/**
	 * 获取短息发送内容的字符串
	 * @param phone 手机号
	 * @param tpl_id 模板id
	 * @param tpl_value 请求参数
	 * @author  createDate 2018.7.10
	 * */
	public static String getMessageVaule (String phone, String tpl_id, Map<String,String> tpl_value) {
		if (phone == null || tpl_id == null) {
			LoggerUtil.info(false, "无手机号，和模板id，不能发送短息");
			return null;
		}
		Map<String,String> map = new HashMap<String, String>();
		map.put("action", "verification");
		map.put("phone", phone);
		map.put("tpl_id", tpl_id);
		map.put("tpl_value", JYSMSUtil.createTpl_value(tpl_value));
		
		return JYSMSUtil.mapToString(map);
	}
	
	public static String getMessageVauleJson (String phone, String tpl_id, Map<String,String> tpl_value) {
		if (phone == null || tpl_id == null) {
			LoggerUtil.info(false, "无手机号，和模板id，不能发送短息");
			return null;
		}
		Map<String,String> map = new HashMap<String, String>();
		map.put("action", "verification");
		map.put("phone", phone);
		map.put("tpl_id", tpl_id);
		map.put("tpl_value", JYSMSUtil.createTpl_value(tpl_value));
		
		return JSONObject.fromObject(map).toString();
	}
	
	/**
	 * 构建请求参数(格式转换maptoString)
	 * */
	public static String mapToString (Map<String,String> map) {
		 // 构建请求参数
		 StringBuffer sbParams = new StringBuffer();
		 if (map != null && map.size() > 0) {
			 for (Entry<String, String> e : map.entrySet()) {
				 sbParams.append(e.getKey());
				 sbParams.append("=");
				 //注意这些需要编码
				 sbParams.append(URLEncoder.encode(e.getValue()+""));
				 sbParams.append("&");
			 }
			 String param = sbParams.substring(0, sbParams.length() - 1);
			 return param;
		 }
		 return null;
	}
	
	/**
	 * 发送短息
	 * @param sbParams 字符串格式参数
	 * @param jyUrl 发送地址
	 * @author guoShiJie
	 * */
	public static String sendPost (String sbParams,String jyUrl,String authorization) {
		StringBuffer resultBuffer = null;
		 
		HttpURLConnection con = null;
		OutputStreamWriter osw = null;
		BufferedReader br = null;
		// 发送请求
		try {
           URL url = new URL(jyUrl);
           con = (HttpURLConnection) url.openConnection();
           con.setRequestMethod("POST");
           
           HashMap<String,Object> param = new HashMap<String,Object>();
           if (authorization != null) {
        	   HashMap<String,Object> mapType = JSON.parseObject(authorization, HashMap.class);
               for (String element : mapType.keySet()) {
    				param.put(element, mapType.get(element));
               }
           }
           
           
           con.setRequestProperty("Authorization", JYAPIUtil.Authorization(param));
           con.setDoOutput(true);
           con.setDoInput(true);
           con.setUseCaches(false);
           //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
           if (sbParams != null && sbParams.length() > 0) {
           	osw = new OutputStreamWriter(con.getOutputStream(), "utf-8");
           	osw.write(sbParams);
           	osw.flush();
           }
           // 读取返回内容
           resultBuffer = new StringBuffer();

           br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
           String temp;
           while ((temp = br.readLine()) != null) {
           	resultBuffer.append(temp);
           }

       } catch (Exception e) {

       	throw new RuntimeException(e);
       } finally {
       	if (osw != null) {
       		try {
       			osw.close();
       		} catch (IOException e) {
       			osw = null;
       			throw new RuntimeException(e);
       		} finally {
       			if (con != null) {
       				con.disconnect();
       				con = null;
       			}
       		}
       	}
       	if (br != null) {
       		try {
       			br.close();
       		} catch (IOException e) {
       			br = null;
       			throw new RuntimeException(e);
       		} finally {
       			if (con != null) {
       				con.disconnect();
       				con = null;
       			}
       		}
       	}
       }
		
		return resultBuffer.toString();
	}
	
}
