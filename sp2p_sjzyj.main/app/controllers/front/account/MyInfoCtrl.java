package controllers.front.account;

import models.common.entity.t_user_info;
import play.mvc.With;
import services.common.UserInfoService;
import services.common.UserService;
import common.annotation.SimulatedCheck;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import controllers.common.FrontBaseController;
import controllers.common.interceptor.AccountInterceptor;
import controllers.common.interceptor.SimulatedInterceptor;

/**
 * 前台-账户中心-会员信息控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With({AccountInterceptor.class,SimulatedInterceptor.class})
public class MyInfoCtrl extends FrontBaseController {

	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	/**
	 * 前台-账户中心-进入个人信息
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月7日
	 */
	public static void toUserInfoPre() {
		long userId = getCurrUser().id;
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		render(userInfo);
	}

	/**
	 * 修改基本信息
	 * 
	 * @description 使用editUserInfomation()方法替代
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 * @param education
	 * @param marital
	 */
	@Deprecated
	public static void updateBasicInfo(int education, int marital){
		ResultInfo result = new ResultInfo();
		long userId = getCurrUser().id;
		
		result = userInfoService.updateUserBasicInfo(userId, education, marital);
		if (result.code < 1) {
			result.msg = "基本信息修改失败";
			
			renderJSON(result);
		}
		result.msg = "基本信息修改成功";
		
		renderJSON(result);
	}
	
	/**
	 * 修改资产信息
	 * 
	 * @description 使用editUserInfomation()方法替代
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 * @param workExperience
	 * @param annualIncome
	 * @param netAsset
	 * @param car
	 * @param house
	 */
	@Deprecated
	public static void changeAssetsInfo(int workExperience, int annualIncome, int netAsset, int car, int house){
		ResultInfo result = new ResultInfo();
		long userId = getCurrUser().id;
		
		result = userInfoService.updateUserAssetsInfo(userId, workExperience, annualIncome, netAsset, car, house);
		if (result.code < 1) {
			result.msg = "资产信息修改失败";
			
			renderJSON(result);
		}
		result.msg = "资产信息修改成功";
		
		renderJSON(result);
	}
	
	/**
	 * 修改紧急联系人信息
	 * 
	 * @description 使用editUserInfomation()方法替代
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 * @param EmergencyContactType
	 * @param EmergencyContactName
	 * @param EmergencyContactMobile
	 */
	@Deprecated
	public static void updateEmergencyContact(String userSign, int emergencyContactType, String emergencyContactName, String emergencyContactMobile){
		ResultInfo result = new ResultInfo();
		long userId = getCurrUser().id;

		if(!StrUtil.isMobileNum(emergencyContactMobile)){
			result.code = -1;
			result.msg = "紧急联系人信息修改失败";
			
			renderJSON(result);
		}
		
		result = userInfoService.updateEmergencyContact(userId, emergencyContactName, emergencyContactType, emergencyContactMobile);
		if (result.code < 1) {
			result.code = -1;
			
			renderJSON(result);
		}
		result.code = 1;
		result.msg = "紧急联系人信息修改成功";
		
		renderJSON(result);
	}
	
	
	/**
	 * 修改会员信息
	 * @description
	 * 
	 * @author songjia
	 * @createDate 2016年03月30日
	 * @param education
	 * @param marital
	 * @param workExperience
	 * @param annualIncome
	 * @param netAsset
	 * @param car
	 * @param house
	 * @param userSign
	 * @param emergencyContactType
	 * @param emergencyContactName
	 * @param emergencyContactMobile
	 */
	@SimulatedCheck
	public static void editUserInfomation(int education, int marital, int workExperience, int annualIncome, int netAsset, int car, int house, int emergencyContactType, String emergencyContactName, String emergencyContactMobile) {
		ResultInfo result = new ResultInfo();
		long userId = getCurrUser().id;

		if(!StrUtil.isMobileNum(emergencyContactMobile)){
			result.code = -1;
			result.msg = "紧急联系人手机号码有误";
			
			renderJSON(result);
		}
		
		result = userInfoService.updateUserInformation(userId, education, marital, workExperience, annualIncome, netAsset, car, house, emergencyContactType, emergencyContactName, emergencyContactMobile);
		if (result.code < 1) {
			result.code = -1;
			
			renderJSON(result);
		}
		result.code = 1;
		result.msg = "会员信息修改成功";
		
		renderJSON(result);
	}
	
}
