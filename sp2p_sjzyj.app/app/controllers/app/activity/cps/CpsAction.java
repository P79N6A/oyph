package controllers.app.activity.cps;

import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import controllers.common.BaseController;
import models.ext.cps.entity.t_cps_activity;
import models.ext.cps.entity.t_cps_award_record;
import service.CpsAppService;
import services.ext.cps.CpsActivityService;
import services.ext.cps.CpsAwardRecordServcie;

public class CpsAction extends BaseController{

	private static CpsAppService cpsAppService = Factory.getService(CpsAppService.class);
	
	protected static CpsAwardRecordServcie cpsAwardRecordService = Factory.getService(CpsAwardRecordServcie.class);
	
	protected static CpsActivityService cpsActivityService = Factory.getService(CpsActivityService.class);

	/**opt 900
	 * cps推广记录
	 * @author guoShiJie
	 * @createDate 2018.6.19
	 * */
	public static String pageOfCpsSpreadRecord (Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		
		if(!StrUtil.isNumeric(currentPageStr)||!StrUtil.isNumeric(pageNumStr)){
			 json.put("code",-1);
			 json.put("msg", "分页参数不正确");
			 
			 return json.toString();
		}
		
		int currPage = Convert.strToInt(currentPageStr, 1);
		int pageSize = Convert.strToInt(pageNumStr, 10);
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		long userId = Long.parseLong(result.obj.toString());
		return cpsAppService.pageOfCpsRecords(currPage, pageSize, userId);
	}
	
	/**opt 901
	 * 获取cps推广码和url
	 * @author guoShiJie
	 * @createDate 2018.6.19
	 * */
	public static String querySpreadMobileByUserId(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		long userId = Long.parseLong(result.obj.toString());
		return cpsAppService.queryMoblie(userId);
	}
	
	/***
	 * 
	 * cps活动中奖记录（opt=902）
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author liuyang
	 * @createDate 2018-06-22
	 */
	public static String queryAwardRecord(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		long userId = Long.parseLong(result.obj.toString());
		
		t_cps_activity act = cpsActivityService.queryActivity();
		t_cps_activity acts = cpsActivityService.queryGoingActivity();
		t_cps_activity actss = cpsActivityService.queryEndActivity();
		t_cps_activity actIsUse = cpsActivityService.queryActivityIsUse();
		
		if(actIsUse == null) {
			json.put("code",1);
			json.put("type", 0);
			json.put("msg", "活动还未开始");
		}else if(act != null) {
			json.put("code",1);
			json.put("type", 0);
			json.put("msg", "活动还未开始");
		}else if(acts != null) {
			json.put("code",1);
			json.put("type", 2);
			json.put("msg", "活动进行中");
		}else if(actss != null) {
			t_cps_award_record records = cpsAwardRecordService.queryAwardByActivityId(userId, actss.id);
			if(records == null){
				json.put("code",1);
				json.put("type", -1);
				json.put("msg", "此用户未中奖");
			}else{
				json.put("code", 1);
				json.put("type", 1);
				json.put("msg", "此用户已中奖");
				json.put("activityName", records.activityName);
				json.put("awardName", records.awardName);
				json.put("awardNum", records.getNum());
			}
		}
		
		return json.toString();
	}
}
