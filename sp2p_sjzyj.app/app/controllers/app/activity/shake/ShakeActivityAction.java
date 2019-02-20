package controllers.app.activity.shake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.codehaus.groovy.control.StaticImportVisitor;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.number.Arith;
import controllers.common.BackBaseController;
import models.activity.shake.entity.t_display_status;
import models.activity.shake.entity.t_shake_record;
import models.app.bean.ActivityListBean;
import models.app.bean.ShakeActivityListBean;
import net.sf.json.JSONObject;
import service.ShakeActivityAppService;
import services.activity.shake.BigWheelService;
import services.activity.shake.DisplayStatusService;
import services.activity.shake.ShakeRecordService;
import sun.print.resources.serviceui;


/**
 * 摇一摇活动
 * 
 * @author niu
 * @create 2017-12-12
 */
public class ShakeActivityAction extends BackBaseController {
	
	protected static ShakeActivityAppService shakeActivityAppService = Factory.getService(ShakeActivityAppService.class);
	
	protected static ShakeRecordService shakeRecordService = Factory.getService(ShakeRecordService.class);
	
	protected static DisplayStatusService displayStatusService = Factory.getService(DisplayStatusService.class);
	
	private static BigWheelService bigWheelService = Factory.getService(BigWheelService.class);
	/**
	 * 摇一摇活动列表
	 * 
	 * @param parameters
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-12
	 */
	public static String listOfShakeActivity(Map<String, String> parameters) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
//		List<ShakeActivityListBean> activityList = shakeActivityAppService.listOfShakeActivity();
		List<ActivityListBean> activityList = bigWheelService.findListActivityAppBean();
		result.put("code", 1);
		result.put("msg", "活动列表查询成功");
		
		
		if (activityList.isEmpty()) {
			result.put("activityList", null);
		} else {
			for (ActivityListBean activity:activityList) {
				
				activity.time=activity.start_time.getTime();
			}
			result.put("activityList", activityList);
		}
		
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 摇一摇
	 * 
	 * @param parameters
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-12
	 */
	public static String shake(Map<String, String> parameters) {
		
		String userIdString = parameters.get("userId");
		String randomNumString = parameters.get("randomNum");
		
		ResultInfo userIdSignDecode = Security.decodeSign(userIdString, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);

		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		
		int randomNum = Convert.strToInt(randomNumString, -1);
		
		/*Random random = new Random();
		for (int i = 1; i < 1200; i++) {
			int randomNum1 = random.nextInt(12000);
			shakeActivityAppService.shake(userId, randomNum1);
			System.out.println(randomNum1);
		}*/
		
		return shakeActivityAppService.shake(userId, randomNum);
	}
	/**
	 * 
	 * @Title: pageOfMyShakeRecord
	 * 
	 * @description  app显示年会个人中奖记录
	 * @param parameters
	 * @return String    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月29日-下午2:52:54
	 */
	public static String pageOfMyShakeRecord(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		//显示状态
		t_display_status displayStatus = displayStatusService.findDisplayStatus();
		int display_status = displayStatus.display_status;
		
		
		long userId = Long.parseLong(result.obj.toString());
		
		t_shake_record myShakeList = shakeActivityAppService.findMyShakeRecord(userId);
		
		json.put("code", 1);
		json.put("msg", "查询个人中奖信息成功");
		json.put("myShakeList", myShakeList);
		json.put("display_status", display_status);  //控制个人中奖信息显示状态
		
		//返回列表，返回状态
		return json.toString();
	}
	
}
