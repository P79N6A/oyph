package controllers.front;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import play.mvc.Controller;
import services.common.SupervisorService;
import services.common.UserInfoService;
import services.common.UserService;
import services.proxy.SalesManService;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import models.proxy.entity.t_proxy_salesman;

/**
 * 前台异步验证控制器
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2016年1月9日
 */
public class FrontAjaxValideCtrl extends Controller  {

	protected static UserService userService = Factory.getService(UserService.class);
	protected static SupervisorService supervisorService = Factory.getService(SupervisorService.class);
	protected static SalesManService salesManService = Factory.getService(SalesManService.class);
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	/**
	 * 手机号码校验
	 * @param mobile 手机号码
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月9日
	 *
	 */
	public static void checkUserMobile(String mobile){
		if (StringUtils.isBlank(mobile)) {
			renderJSON(false);
		}
		
		/* 判断手机号码是否有效 */
		if (!StrUtil.isMobileNum(mobile)) {
			renderJSON(false);
		}
		
		/* 判断手机号码是否存在 */
		if (userService.isMobileExists(mobile)) {
			renderJSON(false);
		}
		
		renderJSON(true);
	}
	
	/**
	 * 判断邀请码是否存在
	 * @param recommendCode 邀请码(推广码,目前是会员的id)
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月9日
	 *
	 */
	public static void checkRecommendCode(String recommendCode){
		if (StringUtils.isBlank(recommendCode)) {
			renderJSON(false);
		}
		boolean flag = false;
		if(StrUtil.isMobileNum(recommendCode)){
			/* 判断手机号码是否存在 */
			flag = userService.isMobileExists(recommendCode);
		} else {
			if (common.constants.ModuleConst.EXT_WEALTHCIRCLE) {
				service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory.getService(service.ext.wealthcircle.WealthCircleService.class);
				ResultInfo result = wealthCircleService.isWealthCircleCodeUseful(recommendCode);
				if(result.code == 1){
					flag = true;
				}
			}
		}
		
		renderJSON(flag);
	}
	
	
	public static void checkeExtensionCode(String recommendCode){
		if (StringUtils.isBlank(recommendCode)) {
			renderJSON(false);
		}
		boolean flag = false;
		long supervisorId =0;
		if(StringUtils.isNotBlank(recommendCode)){
			/* 判断手机号码是否存在 */
			if (recommendCode.indexOf("T") == 0) {
				supervisorId = supervisorService.findSupervisorByExtension(recommendCode);
				if(supervisorId<=0){
					flash.error("业务邀请码不存在");
					renderJSON(false);
				}else{
					 flag = true;
				}
			}else if (recommendCode.indexOf("D") == 0) {
				t_proxy_salesman sales = null;
				boolean flagCode = false;
				try {
					Long.parseLong(recommendCode.substring(1));
					flagCode = true;
				} catch(Exception e) {
				}
				if (flagCode) {
					long number = Long.parseLong(recommendCode.substring(1));
					sales = salesManService.findByID(number);
				}
				if (sales == null) {
					flash.error("业务邀请码不存在");
					renderJSON(false);
				}else{
					flag = true;
				}
			}else {
				flash.error("业务邀请码不存在");
				renderJSON(false);
			}
				
		}else {
			if (common.constants.ModuleConst.EXT_WEALTHCIRCLE) {
				service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory.getService(service.ext.wealthcircle.WealthCircleService.class);
				ResultInfo result = wealthCircleService.isWealthCircleCodeUseful(recommendCode);
				if(result.code == 1){
					flag = true;
				}
			}
		}
		
		renderJSON(flag);
	}
	
	/**
	 * 判断手机号码是否存在
	 * @param mobile
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月11日
	 *
	 */
	public static void userMobileExists(String mobile){
		if (StringUtils.isBlank(mobile)) {

			renderJSON(false);
		}
		
		/* 判断手机号码是否存在 */
		if (!userService.isMobileExists(mobile)) {
			renderJSON(false);
		}
		
		renderJSON(true);
	}
	
	
	/**
	 * 用户密码校验
	 * @param password 密码
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月9日
	 *
	 */
	public static void checkUserPassword(String password){
		ResultInfo result = new ResultInfo();
		
		if (!StrUtil.isValidPassword(password, 6, 20)) {
			result.code = -1;
			result.msg="您输入的密码不符合规范，请重新输入";
			
			renderJSON(result);
		}
		result.code = 1;
		
		renderJSON(result);
	}
	
	/**
	 * 验证手机验证码
	 * @param mobile 手机号码
	 * @param smsCode 短信验证码
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月11日
	 *
	 */
	public static void checkSmsCode(String mobile, String smsCode){
        String codec = (String) Cache.get(mobile);
        if (!smsCode.equals(codec)) {
        	
        	renderJSON(false);
  		}
        
    	renderJSON(true);
	}
	
	/**
	 * 校验邮箱
	 * @param email
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月15日
	 *
	 */
	public static void checkEmail(String email){
		if (userInfoService.isEailExists(email)) {
			
			renderJSON(false);
		}
        
    	renderJSON(true);
	}
	
}
