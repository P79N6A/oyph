package com.softkey;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import net.sf.json.JSONObject;

public class JsonUtil {
	/**
	 * 
	 * @Title: loadJson
	 * @description: 获取json
	 *
	 * @param url
	 * @return    
	 * @return String   
	 *
	 * @author HanMeng
	 * @createDate 2018年12月4日-上午10:29:06
	 */
	public static  String loadJson(String url) {
		StringBuilder json = new StringBuilder();
		try {
			URL urlObject = new URL(url);
			URLConnection uc = urlObject.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				json.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json.toString();
	}

}
