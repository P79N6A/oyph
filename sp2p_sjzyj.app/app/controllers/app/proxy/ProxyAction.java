package controllers.app.proxy;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;
import com.shove.security.Encrypt;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.SettingKey;
import common.constants.SmsScene;
import common.enums.DeviceType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import controllers.common.BaseController;
import controllers.front.proxy.LoginCtrl;
import models.common.entity.t_send_code;
import models.proxy.bean.CurrSaleMan;
import models.proxy.entity.t_proxy_salesman;
import net.sf.json.JSONObject;
import play.cache.Cache;
import play.mvc.Scope.Session;
import service.AccountAppService;
import service.ProxyAppService;
import services.common.SendCodeRecordsService;
import services.common.SettingService;
import services.proxy.SalesManService;
import yb.YbUtils;

/**
 * 代理商APP控制器
 * 
 * @author 
 */
public class ProxyAction {

	private static ProxyAppService proxyAppService = Factory.getService(ProxyAppService.class);
	private static SalesManService salesManService = Factory.getService(SalesManService.class);
	private static SendCodeRecordsService sendCodeRecordsService = Factory.getService(SendCodeRecordsService.class);
	private static AccountAppService userAppService = Factory.getService(AccountAppService.class);
	private static SettingService settingService = Factory.getService(SettingService.class);
	
	/**
	 * 代理商或者业务员登录
	 * @return
	 */
	public static String proxyOrSaleManLogining(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		String mobile   = parameters.get("mobile");
		String password = parameters.get("password");
		
		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg" , "请输入正确的用户名");
			
			return json.toString();
		}
		if (StringUtils.isBlank(password)) {
			json.put("code", -1);
			json.put("msg" , "请输入密码");
			
			return json.toString();
		}
		
		password = Encrypt.decrypt3DES(password, ConfConst.ENCRYPTION_APP_KEY_DES);//密码解密
		
		return proxyAppService.proxyOrSalemanLogining(mobile, password);
	}
	
	/**
	 * 代理商主页数据查询
	 * 
	 * @param parameters
	 * @return
	 */
	public static String proxyHome(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		String saleManIdStr = parameters.get("saleManId");
		if (saleManIdStr == null) {
			json.put("code", -1);
			json.put("msg" , "业务员Id不能为空");
			
			return json.toString();
		}
		
		long saleManId = Convert.strToLong(saleManIdStr, 0);
		
		if (saleManId <= 0) {
			json.put("code", -1);
			json.put("msg" , "业务员Id错误");
			
			return json.toString();
		}
		
		return proxyAppService.proxyHome(saleManId);
	}

	
	/**
	 * 代理商规则查询
	 * 
	 * @param parameters
	 * @return
	 */
	public static String proxyRule(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		String proxyIdStr = parameters.get("proxyId");
		if (proxyIdStr == null) {
			json.put("code", -1);
			json.put("msg" , "代理商Id不能为空");
			
			return json.toString();
		}
		
		long proxyId = Convert.strToLong(proxyIdStr, 0);
		
		if (proxyId <= 0) {
			json.put("code", -1);
			json.put("msg" , "代理商Id错误");
			
			return json.toString();
		}
		
		return proxyAppService.proxyRule(proxyId);
	}
	
	/**
	 * 业务员规则查询
	 * 
	 * @param parameters
	 * @return
	 */
	public static String saleManRule(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		long saleManId = Convert.strToLong(parameters.get("saleManId"), 0);
		if (saleManId <= 0) {
			json.put("code", -1);
			json.put("msg" , "业务员id错误");
			
			return json.toString();
		}
		
		return proxyAppService.saleManRule(saleManId);
	}
	
	/**
	 * 业务员列表查询
	 * @param parameters
	 * @return
	 */
	public static String listOfSaleMan(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		long proxyId = Convert.strToLong(parameters.get("proxyId"), 0);
		if (proxyId <= 0) {
			json.put("code", -1);
			json.put("msg" , "代理商id错误");
			
			return json.toString();
		}
		
		int currPage = Convert.strToInt(parameters.get("currPage"), 1);
		int pageSize = Convert.strToInt(parameters.get("pageSize"), 10);
		
		return proxyAppService.listOfSaleMan(proxyId, currPage, pageSize);
	}
	
	/**
	 * 业务员信息查询
	 * @param parameters
	 * @return
	 */
	public static String saleManInfo(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		long saleManId = Convert.strToLong(parameters.get("saleManId"), 0);
		if (saleManId <= 0) {
			json.put("code", -1);
			json.put("msg" , "代理商id错误");
			
			return json.toString();
		}
		
		return proxyAppService.getSaleManInfo(saleManId);
	}
	
	/**
	 * 名下会员查询
	 * @param parameters
	 * @return
	 */
	public static String saleManUsers(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		long saleManId = Convert.strToLong(parameters.get("saleManId"), 0);
		if (saleManId <= 0) {
			json.put("code", -1);
			json.put("msg" , "业务员id错误");
			
			return json.toString();
		}
		
		int currPage = Convert.strToInt(parameters.get("currPage"), 1);
		int pageSize = Convert.strToInt(parameters.get("pageSize"), 10);
		
		int orderType = Convert.strToInt(parameters.get("orderType"), 4);
		int orderValue = Convert.strToInt(parameters.get("orderValue"), 2);//0,降序，1,升序
		
		return proxyAppService.listOfSaleManUsers(saleManId, currPage, pageSize, orderType, orderValue);
	}
	
	/**
	 * 添加业务员
	 * @param parameters
	 * @return
	 */
	public static String addSaleMan(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		long proxyId = Convert.strToLong(parameters.get("proxyId"), 0);
		if (proxyId <= 0) {
			json.put("code", -1);
			json.put("msg" , "代理商id错误");
			
			return json.toString();
		}
		String mobile = parameters.get("mobile");
		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg" , "手机号格式错误");
			
			return json.toString();
		}
		if (salesManService.isMobileExists(mobile)) {
			json.put("code", -1);
			json.put("msg" , "手机号已存在");
			
			return json.toString();
		}
		
		String realName = parameters.get("realName");
		
		if (StringUtils.isBlank(realName) || realName.length() < 2 || realName.length() > 15) {
			json.put("code", -1);
			json.put("msg" , "名字2~15个字符");
			
			return json.toString();
		}
		
		String password = parameters.get("password");
		
		password = Encrypt.decrypt3DES(password, ConfConst.ENCRYPTION_APP_KEY_DES);//密码解密
		
		if (!StrUtil.isValidPassword(password, 6, 15)) {
			json.put("code", -1);
			json.put("msg" , "密码6~15个字符或数字");
			
			return json.toString();
		}

		ResultInfo result = salesManService.addSalesMan(proxyId, realName, mobile, password);
		
		json.put("code", result.code);
		json.put("msg" , result.msg);
		
		return json.toString();
	}
	
	/**
	 * 更改密码
	 * @param parameters
	 * @return
	 */
	public static String updatePwd(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		String origPwd = parameters.get("origPwd");
		
		if (StringUtils.isBlank(origPwd)) {
			json.put("code", -1);
			json.put("msg" , "原始密码不能为空");
			
			return json.toString();
		}
		
		String newPwd = parameters.get("newPwd");
		if (StringUtils.isBlank(newPwd)) {
			json.put("code", -1);
			json.put("msg" , "新密码不能为空");
			
			return json.toString();
		}
		origPwd = Encrypt.decrypt3DES(origPwd, ConfConst.ENCRYPTION_APP_KEY_DES);//解密密码
		if (!StrUtil.isValidPassword(origPwd, 6, 15)) {
			json.put("code", -1);
			json.put("msg" , "原始密码请输入6~15个字母或数字");
			
			return json.toString();
		}	
		newPwd = Encrypt.decrypt3DES(newPwd, ConfConst.ENCRYPTION_APP_KEY_DES);//解密密码
		if (!StrUtil.isValidPassword(newPwd, 6, 15)) {
			json.put("code", -1);
			json.put("msg" , "新密码请输入6~15个字母或数字");
			
			return json.toString();
		}

		long saleManId = Convert.strToLong(parameters.get("saleManId"), 0);
		if (saleManId <= 0) {
			json.put("code", -1);
			json.put("msg" , "业务员id错误");
			
			return json.toString();
		}
		
		t_proxy_salesman salesman = salesManService.findByID(saleManId);
		
		if (salesman == null) {
			json.put("code", -1);
			json.put("msg" , "查询不到业务员");
			
			return json.toString();
		}
		
		String originalPwdEncrypt = Encrypt.MD5(origPwd + ConfConst.ENCRYPTION_KEY_MD5);
		if (!salesman.sale_pwd.equals(originalPwdEncrypt)) {
			json.put("code", -1);
			json.put("msg" , "原始密码错误");
			
			return json.toString();
		}

		if (salesManService.updatePassWord(saleManId, newPwd)) {
			json.put("code", 1);
			json.put("msg" , "密码更新成功");
			
			return json.toString();
		}
		
		json.put("code", -1);
		json.put("msg" , "密码更新失败");
		
		return json.toString();
		
	}
	
	/**
	 * 业务员提成查询
	 * 
	 * @param parameters
	 * @return
	 */
	public static String saleManProfit(Map<String, String> parameters) {	
		
		JSONObject json = new JSONObject();
		
		long proxyId = Convert.strToLong(parameters.get("proxyId"), 0);
		if (proxyId <= 0) {
			json.put("code", -1);
			json.put("msg" , "代理商id错误");
			
			return json.toString();
		}
		
		t_proxy_salesman salesman = salesManService.findByID(proxyId);
		if (salesman == null) {
			json.put("code", -1);
			json.put("msg" , "代理商id错误");
			
			return json.toString();
		}
		
		
		int currPage = Convert.strToInt(parameters.get("currPage"), 1);
		int pageSize = Convert.strToInt(parameters.get("pageSize"), 10);
		
		String profitTime = parameters.get("profitTime");
		
		if(profitTime.equals("") || profitTime.equals(null)) {
			
			Date date = new Date();
			
			date = DateUtil.minusMonth(date, 1);
			
			if(date.getMonth()<9) {
				profitTime = ""+(date.getYear()+1900)+"-0"+(date.getMonth()+1);
			}else {
				profitTime = ""+(date.getYear()+1900)+"-"+(date.getMonth()+1);
			}
		}
		
		return proxyAppService.listOfSaleManPrift(salesman.proxy_id, currPage, pageSize, profitTime);
	}
	
	/**
	 * 业务员编辑
	 * 
	 * @param parameters
	 * @return
	 */
	public static String saleManEdit(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		long saleManId = Convert.strToLong(parameters.get("saleManId"), 0);
		t_proxy_salesman salesman = salesManService.findByID(saleManId);
		if (salesman == null) {
			json.put("code", -1);
			json.put("msg" , "代理商不存在");
			
			return json.toString();
		}
		
		String salesManName = parameters.get("salesManName");
		if (StringUtils.isBlank(salesManName) || salesManName.length() < 2 || salesManName.length() > 15) {
			json.put("code", -1);
			json.put("msg" , "真实姓名输入有误2-15个字符");
			
			return json.toString();
		}
		
		salesman.sale_name = salesManName;
		
		String salesManMobile = parameters.get("salesManMobile");
		
		if (!StrUtil.isMobileNum(salesManMobile)) {
			json.put("code", -1);
			json.put("msg" , "手机号格式错误");
			
			return json.toString();
		}
		
		if (salesManService.isMobileExists2(salesManMobile, salesman.id)) {
			json.put("code", -1);
			json.put("msg" , "手机号已存在");
			
			return json.toString();
		}
		
		salesman.sale_mobile = salesManMobile;
		
		String salesManPwd = parameters.get("salesManPwd");
		salesManPwd = Encrypt.decrypt3DES(salesManPwd, ConfConst.ENCRYPTION_APP_KEY_DES);//密码解密
		
		if (!StringUtils.isBlank(salesManPwd) && StrUtil.isValidPassword(salesManPwd, 6, 15)) {
			salesman.sale_pwd = Encrypt.MD5(salesManPwd + ConfConst.ENCRYPTION_KEY_MD5);//密码加密	
		}
		
		t_proxy_salesman salesman2 = salesman.save();
		if (salesman2 == null) {
			json.put("code", -1);
			json.put("msg" , "业务员编辑失败");
			
			return json.toString();
		}
		
		json.put("code", 1);
		json.put("msg" , "业务员编辑成功");
		
		return json.toString();
	}
	
	/**
	 * 
	 * 发送验证码
	 * @return
	 * @author 
	 * @createDate 2016-3-31
	 */
	public static String sendCode(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		String mobile = parameters.get("mobile");
        String scene = parameters.get("scene");//场景
		
		/* 验证手机号是否符合规范 */
		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg","手机号不符合规范");
			
			return json.toString();	
		}
	
		if (SmsScene.APP_FORGET_PWD.equals(scene)) {
			if(!salesManService.isMobileExists(mobile)){
				json.put("code", -1);
				json.put("msg","手机号未注册");
				
				return json.toString();	
			}
		} else {
			json.put("code", -1);
			json.put("msg", "场景类型不符合规范");
			
			return json.toString();
		}

		/* 根据手机号码查询短信发送条数 */
    	List<t_send_code> recordByMobile = sendCodeRecordsService.querySendRecordsByMobile(mobile);
    	if(recordByMobile.size() >= ConfConst.SEND_SMS_MAX_MOBILE){
			json.put("code", -1);
			json.put("msg","该手机号码单日短信发送已达上限");
			
			return json.toString();	
		}
    	
    	/* 根据IP查询短信发送条数 */
    	List<t_send_code> recordByIp = sendCodeRecordsService.querySendRecordsByIp(BaseController.getIp());
    	if(recordByIp.size() >= ConfConst.SEND_SMS_MAX_IP){
			json.put("code", -1);
			json.put("msg","该IP单日短信发送已达上限");
			
			return json.toString();	
		}
		
		/* 将手机号码存入缓存，用于判断60S中内同一手机号不允许重复发送验证码 */
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile + encryString + scene);
    	if(null == cache){
			Cache.set(mobile + encryString + scene, mobile, Constants.CACHE_TIME_SECOND_60);
    	}else{
    		String isOldMobile = cache.toString();
    		if (isOldMobile.equals(mobile)) {
    			json.put("code", 1);
    			json.put("msg","短信已发送");

    			return json.toString();	
    		}
    	}
    	
		return userAppService.sendCode(mobile, scene);	
	}
	
	/**
	 * 修改登录密码
	 *
	 * @param parameters
	 * @return
	 *
	 * @author 
	 * @createDate 
	 */
	public static String updateUserPwd(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		String verificationCode = parameters.get("veriCode");
		String mobile = parameters.get("mobile");
		String scene = parameters.get("scene");
		
		if (StringUtils.isBlank(verificationCode)) {
			json.put("code", -1);
			json.put("msg", "验证码不能为空!");
			
			return json.toString();
		}
		if (StringUtils.isBlank(mobile)) {
			json.put("code", -1);
			json.put("msg", "手机号码不能为空!");
			
			return json.toString();
		}
		if (!SmsScene.APP_REGISTER.equals(scene) && !SmsScene.APP_FORGET_PWD.equals(scene)) {
	
			json.put("code", -1);
			json.put("msg", "参数scene错误!");
			
			return json.toString();
		
		}
		
		String verify = userAppService.verificationCode(verificationCode, mobile, scene);
		
		Map<String, String> result = YbUtils.jsonToMap(verify);
		
		if (!result.get("code").equals("1")) {
			return verify;
		}
		
		
		String newPassword = parameters.get("newPwd");
		
		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg", "请输入正确手机号!");
			
			return json.toString();
		}
		if (StringUtils.isBlank(newPassword)) {
			json.put("code", -1);
			json.put("msg", "请输入新密码!");
			
			return json.toString();
		}
		newPassword = Encrypt.decrypt3DES(newPassword, ConfConst.ENCRYPTION_APP_KEY_DES);//解密密码
		if (!StrUtil.isValidPassword(newPassword)) {
			json.put("code", -1);
			json.put("msg", "密码格式错误!");
			
			return json.toString();
		}
		
		t_proxy_salesman saleMan = salesManService.getSaleManByMobile(mobile);
		
		if (saleMan == null) {
			json.put("code", -1);
			json.put("msg", "没有该用户!");
			
			return json.toString();
		}
		/** 密码加密 */
		
		if (!salesManService.updatePassWord(saleMan.id, newPassword)) {
			json.put("code", -1);
			json.put("msg", "密码修改失败");
			
			return json.toString();
		}
		
		json.put("code", 1);
		json.put("msg", "密码修改成功");
		
		return json.toString();
	}
	
	/***
	 * 获取APP(代理商)版本信息
	 *
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author liuyang
	 * @createDate 2018-04-09
	 */
	public static String getPlatformInfo(Map<String, String> parameters) {
        JSONObject json = new JSONObject();
		
		String deviceTypeStr = parameters.get("deviceType");
		
		if (DeviceType.getEnum(Convert.strToInt(deviceTypeStr, -99)) == null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		int deviceType = Integer.parseInt(deviceTypeStr);
		
		/**android(代理商) 版本*/
		String androidVersion = settingService.findSettingValueByKey(SettingKey.ANDROID_NEW_VERSION_DL);
	
		/**android(代理商) 升级类型*/
		String androidType = settingService.findSettingValueByKey(SettingKey.ANDROID_PROMOTION_TYPE_DL);
		boolean androidPromotionType = (StringUtils.isNotBlank(androidType) && androidType.equals("1")) ? true : false;

		json.put("version", deviceType == DeviceType.DEVICE_ANDROID.code ? androidVersion : null);
		json.put("promotionType", deviceType == DeviceType.DEVICE_ANDROID.code ? androidPromotionType : null);
		json.put("code", 1);
		json.put("msg", "查询成功");
		return json.toString();
	}
	
}






















