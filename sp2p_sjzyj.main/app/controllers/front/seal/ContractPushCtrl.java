package controllers.front.seal;

import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;
import models.common.entity.t_ssq_user;
import models.common.entity.t_user_info;
import net.sf.json.JSONObject;
import play.Logger;
import play.mvc.Controller;
import play.mvc.results.RenderJson;
import services.common.SettingService;
import services.common.UserInfoService;
import services.common.ssqUserService;
import yb.YbUtils;
import yb.enums.ServiceType;

/**
 * 上上签异步推送信息接收
 * 
 * @author LiuPengwei
 * @createDate 2018年3月29日17:04:01
 */
public class ContractPushCtrl extends Controller{

	private static ssqUserService ssquserService = Factory.getService(ssqUserService.class);
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	public static void contractCallBackAsyn(){
		
		Logger.info("***************上上签异步回调开始***************");
		
		JSONObject.fromObject(params).toString();
		// 1.获取报文
		String jsonStr = params.data.get("body")[0];
		
		if ("".equals(jsonStr) || "[]".equals(jsonStr) || "{}".equals(jsonStr)) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("异步回调报文为空");
			}
			
			return;
		}
		
		JSONObject body = JSONObject.fromObject(jsonStr);
		if (body == null) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("异步回调报文转JSON失败");
			}
			
			return;
		}
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("回调报文：" + body.toString());
		}
		
		
		//异步业务类型
		String action = body.get("action").toString();
		//业务参数
		Object params =body.get("params");

		Map<String, String> inBodyMap = YbUtils.jsonToMap(params.toString());
		
		if ("certApply".equals(action)){
			
			String account = inBodyMap.get("account");//用户账号（电话）
			String status = inBodyMap.get("status");//申请证书状态   5：成功
			String taskId = inBodyMap.get("taskId");//证书编号
			
			t_ssq_user ssqUser = ssquserService.findByAccount(account);
			
			if (ssqUser == null){
				return;
			}
			boolean falg = false;
			
			//成功进行业务处理
			if ("5".equals(status) ) {
				
				//业务处理
				falg = ssquserService.updateUserSealTaskId(ssqUser, taskId, 1);
				
				if (!falg){
					if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
						Logger.info(account +"证书申请失败！");
					}
					
				}
				
			}else {
				
				falg = ssquserService.updateUserSealTaskId(ssqUser, taskId, -1);
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(account +"证书申请失败！");
				}
				
			}
			
		}
		
		Logger.info("***************上上签异步回调结束***************");
	}
	
}
