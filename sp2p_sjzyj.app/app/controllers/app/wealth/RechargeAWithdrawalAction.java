package controllers.app.wealth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import service.AccountAppService;
import services.common.BankCardUserService;
import services.common.BankQuotaService;
import services.common.RechargeUserService;
import services.common.UserFundService;
import services.common.UserInfoService;
import yb.enums.ServiceType;

import com.shove.Convert;

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
import models.common.entity.t_bank_card_user;
import models.common.entity.t_bank_quota;

/**
 * 充值提现
 *
 * @description 充值，提现，充值记录，提现记录
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
public class RechargeAWithdrawalAction {

	private static AccountAppService accountAppService = Factory.getService(AccountAppService.class);
	
	protected static RechargeUserService rechargeUserService = Factory.getService(RechargeUserService.class);
	
	protected static BankQuotaService bankQuotaService = Factory.getService(BankQuotaService.class);
	
	protected static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);
	
	private static UserFundService userFundService = Factory.getService(UserFundService.class);
	/**
	 * APP进入提现页面
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月7日
	 */
	public static String withdrawalPre(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String userIdSign = parameters.get("userId");
		ResultInfo result = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
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
			json.put("code", ResultInfo.NOT_REAL_NAME);
			json.put("msg", "您还没有进行实名认证!");
			 
			return json.toString();
		}
		
		if (!UserValidataUtil.checkEnterpriseAccount(userId)) {
			json.put("code", ResultInfo.NONSUPPORT);
			json.put("msg", "企业用户暂不支持此功能!");
			 
			return json.toString();
		}
		
		if (!UserValidataUtil.checkBankCard(userId)) {
			json.put("code", ResultInfo.NOT_BIND_BANKCARD);
			json.put("msg", "您还没有进行绑卡!");
			 
			return json.toString();
		}
		
		
		
		return accountAppService.withdrawalPre(userId);
	}
	
	/**
	 * 我的财富-提现（OPT=214）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public static String withdrawal(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String userIdSign= parameters.get("userId");
		String amount = parameters.get("amount");
		String bankAccount = parameters.get("bankAccount");
		String deviceType = parameters.get("deviceType");
		
		ResultInfo result = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		if (!StrUtil.isNumericalValue(amount)) {
			json.put("code", -1);
			json.put("msg", "请输入正确的提现金额!");
			
			return json.toString();
		}
		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		double withdrawalAmt = Double.parseDouble(amount);
		
		if (withdrawalAmt <= 0) {
			json.put("code", -1);
			json.put("msg", "提现金额必须大于0!");
			
			return json.toString();
		}	
		if (!UserValidataUtil.checkPaymentAccount(userId)) {
			json.put("code", ResultInfo.NOT_PAYMENT_ACCOUNT);
			json.put("msg", "您还没有进行资金托管开户!");
			 
			return json.toString();
		}
		if (!UserValidataUtil.checkRealName(userId)) {
			json.put("code", ResultInfo.NOT_REAL_NAME);
			json.put("msg", "您还没有进行实名认证!");
			 
			return json.toString();
		}
		if (!UserValidataUtil.checkBankCard(userId)) {
			 json.put("code", ResultInfo.NOT_BIND_BANKCARD);
			 json.put("msg", "请您先绑卡");
			 return json.toString();
		}
		
		result = accountAppService.withdrawal(userId, withdrawalAmt, bankAccount, Integer.parseInt(deviceType));
	   
		if (result.code < 0) {
			LoggerUtil.info(true, result.msg);
			json.put("code", result.code);
			json.put("msg", result.msg);

			return json.toString();
		}

		json.put("code", result.code);
		json.put("msg", result.msg);

		if (ConfConst.IS_TRUST) {
			
			json.put("html", result.obj.toString());
		}
		
		return json.toString();
	}

	/**
	 * 查询提现记录（OPT=215）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public static String pageOfWithdraw(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		String currPage = parameters.get("currPage");
		String pageSize = parameters.get("pageSize");
		if (StringUtils.isBlank(signId)) {
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
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", -1);
			json.put("msg", result.msg);
			
			return json.toString();
		}
		
		return accountAppService.pageOfWithdrawRecord(Long.parseLong(result.obj.toString()), curr, page);
	}
	
	/***
	 * 充值准备（opt=216）
	 *
	 * @param parameters
	 * @description 
	 *
	 * @author luzhiwei
	 * @throws Exception 
	 * @createDate 2016-4-12
	 */	 
	public static String rechargePre(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();

		String signId = parameters.get("userId");

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
			 json.put("code", ResultInfo.NOT_REAL_NAME);
			 json.put("msg", "请实名认证");
			 return json.toString();
		}
		
		if (!UserValidataUtil.checkEnterpriseAccount(userId)) {
			json.put("code", ResultInfo.NONSUPPORT);
			json.put("msg", "企业用户暂不支持此功能!");
			 
			return json.toString();
		}
		
		if (!UserValidataUtil.checkBankCard(userId)) {
			 json.put("code", ResultInfo.NOT_BIND_BANKCARD);
			 json.put("msg", "请您先绑卡");
			 
			 return json.toString();
		}

		
		return accountAppService.rechargePre(userId);    
	}
	
	/***
	 * 充值接口（OPT=211）
	 * @param parameters
	 * @return
	 * @throws Exception
	 *
	 * @author LiuPengwei
	 * @createDate 2017年
	 */
	public static String recharge(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");//用户id
		String amount = parameters.get("amount");//充值金额
		String bankId = parameters.get("bankId");//银行卡号
		String bankAccount = parameters.get("bankAccount");
		String deviceType = parameters.get("deviceType");//设备类型
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
	    
    	if(!StrUtil.isNumeric(amount)){
	   		 json.put("code",-1);
			 json.put("msg", "充值金额不正确");
			 
			 return json.toString();
    	}
    	
		long userId = Long.parseLong(result.obj.toString());
		
		if (!UserValidataUtil.checkPaymentAccount(userId)) {
			json.put("code", ResultInfo.NOT_PAYMENT_ACCOUNT);
			json.put("msg", "您还没有进行资金托管开户!");
			 
			return json.toString();
		}
		
		if (!UserValidataUtil.checkRealName(userId)) {
			 json.put("code", ResultInfo.NOT_REAL_NAME);
			 json.put("msg", "请实名认证");
			 
			 return json.toString();
		}

		if (!UserValidataUtil.checkBankCard(userId)) {
			 json.put("code", ResultInfo.NOT_BIND_BANKCARD);
			 json.put("msg", "请您先绑卡");
			 
			 return json.toString();
		}
		
		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		
		double rechargeAmt = Double.parseDouble(amount);
		if (bankId !=null && bankId.length()!=0){	
			
			/*银行卡信息*/
			t_bank_card_user bankCardUser = bankCardUserService.findByColumn("id =?", Long.parseLong(bankId));	
			
			/* 代扣充值每日限额*/
			Calendar ca = Calendar.getInstance();
		    Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    String dateNowStr = sdf.format(date);  
			ca.setTime(date);
		    ca.add(Calendar.DATE, 1);
			Date dateTomDate = ca.getTime();
			String dateTomStr= sdf.format(dateTomDate);    
			
			/** 查询用户某卡银行当天充值累计金额*/
			double totalRechargeUser = rechargeUserService.findUseridBankNameTotalRechargeByDate(dateNowStr, dateTomStr, userId, bankCardUser.bank_name, bankCardUser.account);
			
			t_bank_quota bank_quota = bankQuotaService.findByColumn("bank_name = ?", bankCardUser.bank_name);
			
						
			if (rechargeAmt > bank_quota.single_quota) {
				
				json.put("code",-1);
				json.put("msg","该卡本次充值超过该银行单笔限额");
				return json.toString();
			}
			
			int day_quota= bank_quota.day_quota;
			
			double sumDayRecharge = rechargeAmt + totalRechargeUser;
			
			if (sumDayRecharge > day_quota) {
				
				json.put("code",-1);
				json.put("msg","该卡当天累计充值金额超过银行每日限额");
				return json.toString();
			}
			
			//业务流水号
			String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.FORGET_PASSWORD);
			
			result = userFundService.rechargeS(userId, businessSeqNo, rechargeAmt, bankCardUser.bank_name, bankCardUser.account, "账户直充", Client.getEnum(Convert.strToInt(deviceType, -99)));
			
			if (result.code < 0) {
				LoggerUtil.info(true, result.msg);
				json.put("code", result.code);
				json.put("msg", result.msg);

				return json.toString();
			}
			
				
			result = accountAppService.recharge(userId, rechargeAmt, Client.getEnum(Convert.strToInt(deviceType, -99)), bankCardUser.account, businessSeqNo);

			if (result.code < 0) {
				LoggerUtil.info(true, result.msg);
				json.put("code", result.code);
				json.put("msg", result.msg);

				return json.toString();
			}

			json.put("code", result.code);
			json.put("msg", result.msg);

			if (ConfConst.IS_TRUST) {

				json.put("html", result.obj.toString());
			}

				return json.toString();
			
		}
		
		//业务流水号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.FORGET_PASSWORD);
		
		result = userFundService.recharge(userId, businessSeqNo, rechargeAmt, "账户直充", Client.getEnum(Convert.strToInt(deviceType, -99)));
		
		if (result.code < 0) {
			LoggerUtil.info(true, result.msg);
			json.put("code", result.code);
			json.put("msg", result.msg);

			return json.toString();
		}
		
		result = accountAppService.recharge(userId, rechargeAmt, Client.getEnum(Convert.strToInt(deviceType, -99)),bankAccount, businessSeqNo);

		if (result.code < 0) {
			LoggerUtil.info(true, result.msg);
			json.put("code", result.code);
			json.put("msg", result.msg);

			return json.toString();
		}

		json.put("code", result.code);
		json.put("msg", result.msg);

		if (ConfConst.IS_TRUST) {

			json.put("html", result.obj.toString());
		}

		return json.toString();
		
	}
	
	/***
	 * 充值记录接口（OPT=212）
	 * @param parameters
	 * @return
	 * @throws Exception
	 *
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public static String pageOfRecharge (Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
	
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}

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
		
		
		long userId = Long.parseLong(result.obj.toString());

		return accountAppService.pageOfRechargeRecord(userId, currPage, pageSize);
	}
}
