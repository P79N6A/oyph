package controllers.app.wealth;

import java.util.Map;

import models.common.entity.t_user;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import service.AccountAppService;

import com.shove.Convert;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.AnnualIncome;
import common.enums.Car;
import common.enums.Education;
import common.enums.House;
import common.enums.Marital;
import common.enums.NetAssets;
import common.enums.Relationship;
import common.enums.WorkExperience;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.Md5Utils;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;

/**
 * 个人基本信息接口
 *
 * @description 包括个人信息，消息，保存个人信息，上传头像等等
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
public class MyInfoAction {

	private static AccountAppService accountAppService = Factory.getService(AccountAppService.class);
	
	
	/**
	 * 
	 * 个人基本信息接口（OPT=251）
	 * @param parameters
	 * @return
	 *
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public static String userInfomation(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		
		return accountAppService.findUserInfomation(userId);
	}
	
	/**
	 * 消息列表（OPT=252）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月1日
	 */
	public static String pageOfUserMessage(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		String currPage = parameters.get("currPage");
		String pageSize = parameters.get("pageSize");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		if (!StrUtil.isNumeric(currPage) || !StrUtil.isNumeric(pageSize)) {
			json.put("code", -1);
			json.put("msg", "分页参数错误!");
			
			return json.toString();
		}
		int curr = Integer.parseInt(currPage);
		int page = Integer.parseInt(pageSize);
		if (curr < 1) {
			curr = 1;
		}
		if (page < 1) {
			page = 10;
		}
		
		return accountAppService.pageOfUserMessage(Long.parseLong(result.obj.toString()), curr, page);
	}
	
	/***
	 * 
	 * 客户端获取会员信息接口（OPT=253）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-6
	 */
	public static String toUserInfo (Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		String signUsersId = parameters.get("userId");

		ResultInfo result = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}

		long userId = Long.parseLong(result.obj.toString());
		
	
		return accountAppService.findUserInfo(userId);
	}
	
	/***
	 * 
	 * 客户端获取会员信息接口（OPT=253）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-6
	 */
	public static String toUserInfoTwo (Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		String signUsersId = parameters.get("userId");

		ResultInfo result = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}

		long userId = Long.parseLong(result.obj.toString());
		
	
		return accountAppService.findUserInfoTwo(userId);
	}
	
	/***
	 * 
	 * 客户端保存会员信息接口（OPT=254）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-6
	 */
	public static String updateUserInfo (Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		String signUsersId = parameters.get("userId");
		String education = parameters.get("education");
		String marital = parameters.get("marital");
		String workExperience = parameters.get("workExperience");
		String annualIncome = parameters.get("annualIncome");
		String netAsset = parameters.get("netAsset");
		String car = parameters.get("car");
		String house = parameters.get("house");
		String emergencyContactType = parameters.get("emergencyContactType");
		String emergencyContactName = parameters.get("emergencyContactName");
		String emergencyContactMobile = parameters.get("emergencyContactMobile");
		
		ResultInfo result = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		long userId = Long.parseLong(result.obj.toString());
		
		if(Education.getEnum(Convert.strToInt(education, -99)) == null){
			 json.put("code",-1);
			 json.put("msg", "请填写正确的学历信息");
			 return json.toString();
		}
		if(Marital.getEnum(Convert.strToInt(marital, -99)) == null){
			 json.put("code",-1);
			 json.put("msg", "请填写正确的婚姻信息");
			 return json.toString();
		}
		if(WorkExperience.getEnum(Convert.strToInt(workExperience, -99)) == null){
			 json.put("code",-1);
			 json.put("msg", "请填写正确的工作年限");
			 return json.toString();
		}
		if(AnnualIncome.getEnum(Convert.strToInt(annualIncome, -99)) == null){
			 json.put("code",-1);
			 json.put("msg", "请填写正确的年收入");
			 return json.toString();
		}
		if(NetAssets.getEnum(Convert.strToInt(netAsset, -99)) == null){
			 json.put("code",-1);
			 json.put("msg", "请填写正确的资产估值");
			 return json.toString();
		}
		if(Car.getEnum(Convert.strToInt(car, -99)) == null){
			 json.put("code",-1);
			 json.put("msg", "请填写正确的车产信息");
			 return json.toString();
		}
		if(House.getEnum(Convert.strToInt(house, -99)) == null){
			 json.put("code",-1);
			 json.put("msg", "请填写正确的房产信息");
			 return json.toString();
		}
		if(Relationship.getEnum(Convert.strToInt(emergencyContactType, -99)) == null ){
			 json.put("code",-1);
			 json.put("msg", "请填写正确的联系人关系");
			 return json.toString();
		}
		if(StringUtils.isBlank(emergencyContactName) || emergencyContactName.length() > 20){
			 json.put("code",-1);
			 json.put("msg", "请填写正确的联系人姓名");
			 return json.toString();
		}
		
		if(!StrUtil.isMobileNum(emergencyContactMobile)){
			 json.put("code",-1);
			 json.put("msg", "请填写正确的联系人电话");
			 return json.toString();
		}
		
		/* 判断否是为本人的手机号码 */
		t_user user = accountAppService.findUserById(userId);
		if (user.mobile.equals(emergencyContactMobile)) {
			 json.put("code",-1);
			 json.put("msg", "不能使用本人的手机号码作为紧急联系人手机号");
			 return json.toString();
		}
		result = accountAppService.updateUserInfo(userId , Integer.parseInt(education), Integer.parseInt(marital), Integer.parseInt(workExperience) ,Integer.parseInt(annualIncome) ,Integer.parseInt(netAsset) ,Integer.parseInt(car) ,Integer.parseInt(house) , Integer.parseInt(emergencyContactType) ,emergencyContactName ,emergencyContactMobile );

		if(result.code < 0){
			LoggerUtil.info(true,result.msg);
		}
		
		json.put("code", result.code);
		json.put("msg", result.msg);
		 
		return json.toString();
	}
	
	/***
	 * 保存用户头像 
	 * @param parameters
	 * 
	 * @author luzhiwei
	 * @createDate 2016-4-22
	 */
	public static String updatePhoto(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		
		String signUsersId = parameters.get("userId");
		
		String fileName = Md5Utils.JM(parameters.get("fileName"));
		
		ResultInfo result = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		
		if(StringUtils.isBlank(fileName)){
			 json.put("code", -1);
			 json.put("msg", "请重新上传头像");
			 
			 return json.toString();	
		}
		
		result = accountAppService.updateUserPhoto(userId, fileName);
		if (result.code < 1) {
			 json.put("code", -1);
			 json.put("msg", result.msg);
			 
			 return json.toString();
		}
		
		accountAppService.flushUserCache(userId);
		
		json.put("code", 1);
		json.put("msg", result.msg);
		 
		return json.toString();
	}

}
