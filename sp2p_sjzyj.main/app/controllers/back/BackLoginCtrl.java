package controllers.back;

import java.util.List;

import com.softkey.UkeyUtil;

import models.common.bean.CurrSupervisor;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_supervisor;
import play.Logger;
import play.cache.Cache;
import play.libs.Codec;
import play.mvc.Scope.Session;

import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import common.utils.captcha.CaptchaUtil;
import controllers.common.BackBaseController;

/**
 * 后台-登录控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
public class BackLoginCtrl extends BackBaseController {
	
	/**
	 * 后台进入登录页面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月24日
	 */
	public static void toBackLoginPre() {
		//Logger.info(System.getProperty("java.library.path"));
		Long supervisorId = getCurrentSupervisorId();
		if (supervisorId != null) {
			
			redirect("back.BackHomeCtrl.backHomePre");
		}
		
 		//禁止该页面进行高速缓存
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

		//验证码
		String randomID = Codec.UUID();
		
		int number1=(int)(Math.random()*65535)+1;
	    int number2=(int)(Math.random()*65535)+1;
	    
	    String ranNum = (new   Integer(number1)).toString()+(new   Integer(number2)).toString();
	    
	    int isCheck = 0;
	    
	    if (ConfConst.IS_CHECK_BACK_PASS) {
	    	isCheck = 1;
		}
		
		render(randomID, ranNum, isCheck);
	}
	
	
	/**
	 * 后台-登录
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月24日
	 */
	public static void login(String supervisor_name,String password,String captcha,String randomID, String flag, String ranNum, String return_EncData, String ckeyId) {
		ResultInfo result = new ResultInfo();
		//加密狗验证
		if (ConfConst.IS_CHECK_BACK_PASS) {
			result = UkeyUtil.checkKey(ranNum, return_EncData, ckeyId);
			if (result == null || result.code < 1) {
				flash.error(result.msg);
				toBackLoginPre();
			}
			Logger.info("-------后台账号：" + supervisor_name + "-------" + result.msg);
		}
		
		//验证用户名,密码，验证码输入是否有效
		if(!valid_login(supervisor_name, password, captcha)){
			flash.put("supervisor_name", supervisor_name);
			//进入登录界面
			toBackLoginPre();
		}

		String randCode = CaptchaUtil.getCode(randomID);
		
		//删除缓存中的验证码
		CaptchaUtil.clearCaptcha(randomID); 
		if (randCode == null) {
			flash.put("supervisor_name", supervisor_name);
			flash.error("验证码失效");
			
			toBackLoginPre();//进入登录界面
		}
		
		if (!captcha.equalsIgnoreCase(randCode)) {
			flash.put("supervisor_name", supervisor_name);
			flash.error("验证码错误");
			
			toBackLoginPre();//进入登录界面
		} 
		
		//用户输入 密码加密
		password = com.shove.security.Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		
		//安全云盾校验
		if (ConfConst.IS_CHECK_UKEY) {
			t_supervisor supervisor = supervisorService.findByColumn("name=? and password=?", supervisor_name, password);
			String time = DateUtil.getHours();
			String supervisorSign = com.shove.security.Encrypt.MD5(Long.toString(supervisor.id) + time);

			if(!supervisorSign.equals(params.get("supervisorSign"))){
				
				toBackLoginPre();//进入登录界面
			}
		}
		
		 result = supervisorService.login(supervisor_name, password, getIp());
		if(result.code < 1){
			flash.put("supervisor_name", supervisor_name);
			flash.error(result.msg);
			
			toBackLoginPre();//进入登录界面
		}
		
		
		t_supervisor supervisor = (t_supervisor) result.obj;
		
		//添加管理员事件
		supervisorService.addSupervisorEvent(supervisor.id, t_event_supervisor.Event.LOGIN, null);
		
		List<Long> rights = rightService.queryRightsOfSupervisor(supervisor.id);
		CurrSupervisor currSupervisor = new CurrSupervisor();
		currSupervisor.id = supervisor.id;
		currSupervisor.name = supervisor.name;
		currSupervisor.reality_name = supervisor.reality_name;
		currSupervisor.rights = rights;
		currSupervisor.setLock_status(supervisor.getLock_status());
		//管理员id缓存
		Cache.safeSet(CacheKey.LOGIN_SUPERVISOR_ID+Session.current().getId(), supervisor.id, Constants.CACHE_TIME_MINUS_30);
		//管理员Bean缓存
		Cache.safeSet(CacheKey.LOGIN_SUPERVISOR + currSupervisor.id, currSupervisor, Constants.CACHE_TIME_MINUS_30);
		
		//进入页面首页
		redirect("back.BackHomeCtrl.backHomePre");
	}

	/**
	 * 后台-登出
	 * 
	 * @description 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月31日
	 */
	public static void logoutPre(){
		//退出时可能会遇到各种异常状态(正常退出、时间超出、网络异常等等)导致程序出错，故使用了了一个较大的catch进行异常处理
		try {
			CurrSupervisor supervisor = getCurrSupervisor();
			if(supervisor != null){
				supervisorService.addSupervisorEvent(supervisor.id, t_event_supervisor.Event.LOGOUT, null);
				Cache.safeDelete(CacheKey.LOGIN_SUPERVISOR + supervisor.id);
			}
			
			Cache.safeDelete(CacheKey.LOGIN_SUPERVISOR_ID+Session.current().getId());
		} catch (Exception e) {
			LoggerUtil.info(false,e, "退出时报了一个异常");
		}
		
		toBackLoginPre();
	}
	
	private static boolean valid_login(String supervisor_name,String password,String captcha){
		if (!StrUtil.isValidUsername(supervisor_name, 3, 10)) {
			flash.error("用户名输入有误");
			
			return false;
		}
		
		if (!StrUtil.isValidPassword(password, 6, 15)) {
			flash.error("密码输入有误");

			return false;
		}
		
		if (!StrUtil.isNumeric(captcha)) {
			flash.error("验证码输入有误");

			return false;
		}
		
		return true;
	}
	
}
