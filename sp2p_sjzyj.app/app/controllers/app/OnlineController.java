package controllers.app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alipay.fc.csplatform.common.crypto.CustomerInfoCryptoUtil;
import com.shove.Convert;
import com.shove.gateway.GeneralRestGateway;
import com.shove.gateway.GeneralRestGatewayInterface;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.SettingKey;
import common.constants.ConfConst;
import common.utils.OnlinePubKeyUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.common.BaseController;
import net.sf.json.JSONObject;
import play.Logger;
import play.mvc.Http;

public class OnlineController extends BaseController {
	
	/**
	 * 客服端工作台访客信息展示接口 (云客服会通过Post的方式调用这个接口来查询访客信息。)
	 * @createDate 2018.7.13
	 * */
	public static String customerInfo(String params ,String key) throws Exception {
		request.contentType = "application/json";
		
		response.setHeader("Content-Type", "application/json");
		response.setHeader("charset", "utf-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        // 响应类型
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, OPTIONS, DELETE");
        // 响应头设置
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with, X-Custom-Header, HaiYi-Access-Token");
        
        JSONObject json = new JSONObject();
        if (params == null || key == null) {
        	json.put("message", "查询失败");
			json.put("success", false);
			json.put("result",null);
			return json.toString();
        }
        
		PublicKey publicKey = OnlinePubKeyUtil.getPubKey();
		String cinfo = CustomerInfoCryptoUtil.decryptByPublicKey(URLEncoder.encode(params, "UTF-8"), URLEncoder.encode(key,"UTF-8"), publicKey);
		JSONObject dataJsonObject = JSONObject.fromObject(cinfo);
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("接收的数据:"+dataJsonObject.toString()+"===========");
		}
		
		JSONObject result = new JSONObject();
		JSONObject properties = new JSONObject();
		JSONObject exinfo = JSONObject.fromObject(dataJsonObject.get("extInfo"));
		JSONObject schema = new JSONObject();
		String userId = dataJsonObject.get("userId").toString();
		JSONObject userName = new JSONObject();
		JSONObject customerName = new JSONObject();
		JSONObject totalIncome = new JSONObject();
		JSONObject totalAssets = new JSONObject();
		JSONObject balance = new JSONObject();
		JSONObject grade = new JSONObject();
		
		/*userId.put("name", "用户Id");
		userId.put("type", "text");*/
		userName.put("name", "真实姓名");
		userName.put("type", "text");
		customerName.put("name", "客户名");
		customerName.put("type", "text");
		totalIncome.put("name", "总收益");
		totalIncome.put("type", "text");
		totalAssets.put("name", "总资产");
		totalAssets.put("type", "text");
		balance.put("name", "可用余额");
		balance.put("type", "text");
		grade.put("name", "风险评估等级");
		grade.put("type", "text");
		
		//properties.put("userId", userId);
		properties.put("realName", userName);
		properties.put("customerName", customerName);
		properties.put("totalIncome", totalIncome);
		properties.put("totalAssets", totalAssets);
		properties.put("balance", balance);
		properties.put("grade", grade);
		schema.put("properties", properties);
		
		result.put("userId", userId);
		result.put("userName", exinfo.get("userName"));
		result.put("customerName", exinfo.get("customerName"));
		result.put("totalIncome", exinfo.get("totalIncome"));
		result.put("totalAssets", exinfo.get("totalAssets"));
		result.put("balance", exinfo.get("balance"));
		result.put("grade", exinfo.get("grade"));
		result.put("schema", schema);
		
		json.put("message", "查询成功");
		json.put("success", true);
		json.put("result", result);
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("返回的数据："+json.toString());
		}
		
		return json.toString();
		
	}

}
