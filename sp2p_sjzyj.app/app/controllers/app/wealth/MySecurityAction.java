package controllers.app.wealth;

import java.util.List;
import java.util.Map;

import models.common.entity.t_bank_card_user;
import models.common.entity.t_user_fund;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;

import com.shove.Convert;
import com.shove.security.Encrypt;

import service.AccountAppService;
import services.common.BankCardUserService;
import services.common.UserFundService;
import services.common.UserInfoService;
import yb.enums.ServiceType;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Client;
import common.enums.DeviceType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.UserValidataUtil;

/**
 * 我的财富-安全中心
 *
 * @description 电子邮箱、绑定银行卡、登录密码。注意的是资金托管开户(包括实名认证已经放置到AccountAction中)
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
public class MySecurityAction {

	private static AccountAppService accountAppService = Factory.getService(AccountAppService.class);
	private static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	protected static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);
	
	
	/**
	 * 我的财富-安全中心（opt=261）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月6日
	 */
	public static String userSecurity(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signUserId = parameters.get("userId");
		ResultInfo result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());

		return accountAppService.findUserSecurity(userId);
	}

	/***
	 * 安全中心 修改邮箱
	 *
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-14
	 */
	public static String updateEmail(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		
		String signUsersId = parameters.get("userId");
		String email = parameters.get("email");
		
		ResultInfo result = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		
		/* 判断是否是有效邮箱 */
		if (!StrUtil.isEmail(email)) {
			 json.put("code", -1);
			 json.put("msg", "邮箱格式错误");
			 
			 return json.toString();
		}
		
		/* 判断邮箱是否存在 */
		if (userInfoService.isEailExists(email)) {
			 json.put("code", -1);
			 json.put("msg", "邮箱已存在");
			 
			 return json.toString();
		}
		
		return accountAppService.updateEmail(userId, email);
    }
	
	
	/**
	 * 我的财富-安全中心-修改密码（通过原密码）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月6日
	 */
	public static String userUpdatePwdbyold(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signUserId = parameters.get("userId");
		String oldPassword = parameters.get("oldPassword");
		String password = parameters.get("password");
		ResultInfo result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		if (StringUtils.isBlank(oldPassword)) {
			 json.put("code", -1);
			 json.put("msg", "原密码不能为空");
			 
			 return json.toString();
		}
		if (StringUtils.isBlank(password)) {
			 json.put("code", -1);
			 json.put("msg", "新密码不能为空");
			 
			 return json.toString();
		}
		/** 验证密码是否符合规范 */
		password = Encrypt.decrypt3DES(password, ConfConst.ENCRYPTION_APP_KEY_DES);//解密密码
		if (!StrUtil.isValidPassword(password)) {
			 json.put("code", -1);
			 json.put("msg", "密码不符合规范!");
			 
			 return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		
		oldPassword = Encrypt.decrypt3DES(oldPassword, ConfConst.ENCRYPTION_APP_KEY_DES);//解密密码
		
		
		oldPassword = Encrypt.MD5(oldPassword + ConfConst.ENCRYPTION_KEY_MD5);
		password = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		result = accountAppService.userUpdatePwdbyold(userId, oldPassword, password);
		if(result.code < 0){
			LoggerUtil.info(true, result.msg);			
		}
		json.put("code", result.code);
		json.put("msg", result.msg); 
		
		return json.toString();
	}
	
	/***
	 * 
	 * 用户银行卡列表(opt=221)
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public static String listUserBankCard(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		String signUsersId = parameters.get("userId");
		String deviceTypes = parameters.get("deviceType");//设备类型
		
		ResultInfo result = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		if (DeviceType.getEnum(Convert.strToInt(deviceTypes, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		int deviceType = Integer.parseInt(deviceTypes);
		
		/* 一个用户最多只能绑定10张银行卡 */
		//bankCardUserService.delBank(userId);
		//实时查询绑定银行卡
		/*result = PaymentProxy.getInstance().queryBindedBankCard(Client.PC.code, userId);
		List<t_bank_card_user> bankList = (List<t_bank_card_user>) result.obj;
		
		for (t_bank_card_user bcu : bankList) {
			bankCardUserService.addUserCard(userId, bcu.bank_name, bcu.bank_code, bcu.account);
		}*/
		
		//业务流水号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_BINDED_BANK);
		
		//实时查询绑定银行卡
		PaymentProxy.getInstance().queryBindedBankCard(deviceType, userId, businessSeqNo,ServiceType.QUERY_BINDED_BANK.code);
		
		return accountAppService.listOfUserBankCard(userId);
	}

	/***
	 * 绑卡验密接口（OPT=703）
	 * @param parameters
	 * @return
	 * @throws Exception
	 *
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public static String bindCardCheckPassword(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		String deviceType = parameters.get("deviceType");//设备类型
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
    	
		long userId = Long.parseLong(result.obj.toString());
		
		if (!UserValidataUtil.checkPaymentAccount(userId)) {
			json.put("code", ResultInfo.NOT_PAYMENT_ACCOUNT);
			json.put("msg", "您还没有进行资金托管开户!");
			 
			return json.toString();
		}
		
		if (!UserValidataUtil.checkRealName(userId)) {
			 json.put("code",ResultInfo.NOT_REAL_NAME);
			 json.put("msg", "请实名认证");
			 return json.toString();
		}

		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		return accountAppService.bindCardCheckPassword(userId, Convert.strToInt(deviceType, 1));
		
	}
	
	/**
	 * 设置默认的一行卡（OPT=223）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月29日
	 */
	public static String setDefaultBankCard(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signUsersId = parameters.get("userId");
		String bankCardIdStr = parameters.get("bankCardId");
		
		ResultInfo result = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		if (StringUtils.isBlank(bankCardIdStr)) {
			 json.put("code", -1);
			 json.put("msg", "请选择需要设定的默认卡!");
			 
			 return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		long bankCardId = Long.parseLong(bankCardIdStr);
		
		return accountAppService.setDefaultBankCard(bankCardId, userId);
	}
	
	/**
	 * 设置交易密码（opt=700）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年10月16日
	 */
	public static String setUserPassword(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signUserId = parameters.get("userId");
		String deviceTypes = parameters.get("deviceType");//设备类型
		
		ResultInfo result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		if (DeviceType.getEnum(Convert.strToInt(deviceTypes, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		int deviceType = Integer.parseInt(deviceTypes);

		return accountAppService.setUserPassword(userId,deviceType);
	}
	
	/**
	 * 修改交易密码（opt=701）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年10月16日
	 */
	public static String amendUserPassword(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signUserId = parameters.get("userId");
		String deviceTypes = parameters.get("deviceType");//设备类型
		
		ResultInfo result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		if (DeviceType.getEnum(Convert.strToInt(deviceTypes, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		int deviceType = Integer.parseInt(deviceTypes);
		
		return accountAppService.amendUserPassword(userId,deviceType);
	}
	
	/**
	 * 重置交易密码（opt=702）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年10月16日
	 */
	public static String resetUserPassword(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signUserId = parameters.get("userId");
		String deviceTypes = parameters.get("deviceType");//设备类型
		
		ResultInfo result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		if (DeviceType.getEnum(Convert.strToInt(deviceTypes, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		int deviceType = Integer.parseInt(deviceTypes);

		return accountAppService.resetUserPassword(userId,deviceType);
	}
	
	/**
	 * 绑卡（opt=222）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年10月17日
	 */
	public static String bindCard(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String mobilePhone = parameters.get("mobilePhone");//新
		String signUserId = parameters.get("userId");
		String deviceTypes = parameters.get("deviceType");//设备类型
		String businessSeqNo = parameters.get("businessSeqNo");
		
		ResultInfo result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		if (DeviceType.getEnum(Convert.strToInt(deviceTypes, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		int deviceType = Integer.parseInt(deviceTypes);

		return accountAppService.userBindCard1(deviceType, userId, businessSeqNo,mobilePhone);
	}
	
	/**
	 * 到绑卡页面（opt=703）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年10月17日
	 */
	public static String toCardPage(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signUserId = parameters.get("userId");
		String deviceTypes = parameters.get("deviceType");//设备类型
		
		ResultInfo result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		if (DeviceType.getEnum(Convert.strToInt(deviceTypes, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		int deviceType = Integer.parseInt(deviceTypes);

		return accountAppService.toCardPage(userId,deviceType);
	}

}
