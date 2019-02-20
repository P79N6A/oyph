package controllers.payment.yb;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shove.Convert;
import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;

import common.constants.Constants;
import common.constants.RemarkPramKey;
import common.constants.SettingKey;
import common.constants.WxPageType;
import common.enums.Client;
import common.enums.JYSMSModel;
import common.ext.cps.utils.RewardGrantUtils;
import common.utils.EncryptUtil;
import common.utils.Factory;
import common.utils.JYSMSUtil;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.TimeUtil;
import controllers.payment.PaymentBaseCtrl;
import daos.PaymentRequstDao;
import daos.ext.cps.CpsUserDao;
import models.common.bean.UserFundInfo;
import models.common.entity.t_bank_card_user;
import models.common.entity.t_recharge_user;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.entity.t_bid;
import models.core.entity.t_debt_transfer;
import models.core.entity.t_invest;
import models.core.entity.t_invest_entrust;
import models.entity.t_payment_call_back;
import models.entity.t_payment_request;
import models.entity.t_payment_request.Status;
import models.ext.cps.entity.t_cps_activity;
import models.ext.cps.entity.t_cps_user;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import payment.impl.PaymentProxy;
import play.Logger;
import play.mvc.Scope.Params;
import service.AccountAppService;
import services.PaymentRequstService;
import services.PaymentService;
import services.common.BankCardUserService;
import services.common.RechargeUserService;
import services.common.SettingService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.common.WithdrawalUserService;
import services.core.BidService;
import services.core.DebtService;
import services.core.InvestService;
import services.ext.cps.CpsActivityService;
import yb.YbConsts;
import yb.YbPaymentCallBackService;
import yb.YbPaymentService;
import yb.YbUtils;
import yb.enums.ServiceType;
import yb.enums.TradeType;
import yb.enums.YbPayType;
import controllers.app.Invest.InvestAction;
import controllers.app.wealth.MyFundAction;
import controllers.back.finance.BillMngCtrl;
import controllers.front.account.MyAccountCtrl;
import controllers.front.account.MyFundCtrl;
import controllers.front.account.MySecurityCtrl;

/**
 * 托管回调控制器
 * 
 * @author niu
 * @create 2017.08.29
 */
public class YbPaymentCallBackCtrl extends PaymentBaseCtrl {
	
	private static YbPaymentCallBackService ybPaymentCallBackService = Factory.getSimpleService(YbPaymentCallBackService.class);
	
	private static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	private static BidService bidService = Factory.getService(BidService.class);
	
	private static PaymentRequstService paymentRequestService = Factory.getService(PaymentRequstService.class);
	
	private static RechargeUserService rechargeUserService = Factory.getService(RechargeUserService.class);
	
	private static WithdrawalUserService withdrawalUserService = Factory.getService(WithdrawalUserService.class);
	
	private static YbPaymentCallBackService callBackService = Factory.getSimpleService(YbPaymentCallBackService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected static CpsUserDao cpsUserDao = Factory.getDao(CpsUserDao.class);
	
	protected static CpsActivityService cpsActivityService = Factory.getService(CpsActivityService.class);
	
	protected static DebtService debtService = Factory.getService(DebtService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	private static YbPaymentCallBackCtrl instance = null;
	
	
	private YbPaymentCallBackCtrl() {
		
	}
	

	public static YbPaymentCallBackCtrl getInstance() {
		
		if (instance == null) {
			synchronized (YbPaymentCallBackCtrl.class) {
				if (instance == null) {
					instance = new YbPaymentCallBackCtrl();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * 个人开户重定向
	 * 
	 * @param response
	 * @author LiuPengwei
	 * @create 2017年9月18日
	 */
	public static void userRegisterSyn (String response,String userName, String certNo, long userId, String businessSeqNo){
		ResultInfo result = null;
		//验签,解析参数
		TreeMap<String, String> map2 = YbUtils.getRespHeader(response,ServiceType.PERSON_CUSTOMER_REGIST,null);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackDataS("个人开户同步回调", map2, ServiceType.PERSON_CUSTOMER_REGIST , TradeType.CUSTOMER_INFO_SYNC);
				
		
		if (map2.get("accNo") == null ||map2.get("accNo").length()==0){
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info(map2.get("respMsg"));
			}
			
			flash.error(map2.get("respMsg"));
			//失败 实名认证清空
			UserInfoService.updateUser(userId);
			redirect("front.account.MySecurityCtrl.securityPre");
			
		}else {
			result = ybPaymentCallBackService.userRegister(userId, map2,userName, certNo,YbPayType.USERREGISTER,businessSeqNo);
			if (result.code > 0 ) {
				
				ResultInfo error = new ResultInfo();
				
				t_cps_activity cpsActivityGoing = null;
				t_user user = t_user.findById(userId);
				t_cps_user cpsUser = cpsUserDao.findByUserId(userId);
				if (cpsUser != null) {
					t_cps_activity cpsActivity = cpsActivityService.queryCpsActivity(cpsUser.activity_id);
					if (cpsActivity != null) {
						cpsActivityGoing  = cpsActivityService.queryGoingCpsActivityById(cpsActivity.id, 1);
					}
				}
				RewardGrantUtils.registerAndRealName(cpsActivityGoing, user, error);
				
				flash.success("资金存管开户成功");
			} else {
				flash.error(result.msg);
			}
			
			
			
			redirect("front.account.MySecurityCtrl.securityPre");
				
		}
		
	}
	
	/**
	 * 企业开户重定向
	 * 
	 * @param response
	 * @author LiuPengwei
	 * @create 2017年9月20日
	 */

	public static void companyRegisterSyn(String response,String userName,String companyName, String uniSocCreCode,
			long userId,String businessSeqNo, String bankNo, String idCard) {
		ResultInfo result = null;
		//验签,解析参数
		TreeMap<String, String> map2 = YbUtils.getRespHeader(response,ServiceType.ENTERPRISE_CUSTOMER_REGIST,null);
		
		if (map2.get("accNo") == null ||map2.get("accNo").length()==0){
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info(map2.get("respMsg"));
			}
			flash.error(map2.get("respMsg"));
			redirect("back.BackHomeCtrl.createAccountsPre");
		}else {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("验签成功！");
			}
			
			result = ybPaymentCallBackService.enterpriseRegister(userId, map2, userName,companyName,
					uniSocCreCode, YbPayType.USERREGISTER, businessSeqNo, bankNo, idCard);
			if (result.code > 0 ) {
				flash.success("资金托管企业开户成功");
			} else {
				flash.error(result.msg);
			}
			redirect("back.BackHomeCtrl.createAccountsPre");
		}
	}
		
	
	/**
	 * 充值功能校验交易密码同步回调
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月16日
	 */
	public static void rechargeCheckPasswordSyn() {
		
		//获取回调参数
		Map<String, String> cbParams = ybPaymentCallBackService.getRespParams(params);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("校验交易密码回调", cbParams, ServiceType.FORGET_PASSWORD , TradeType.CUSTOMER_INFO_SYNC);
		
		if (cbParams == null) {
			flash.error("校验交易密码失败，请重试"); 
			
			redirect("front.account.MySecurityCtrl.securityPre");
		}
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParams(cbParams.get("businessSeqNo"));
		
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");
			redirect("front.account.MySecurityCtrl.securityPre");
		}
		
		String businessSeqNo = cbParams.get("businessSeqNo");
		double rechargeAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.RECHARGE_AMT));
		String bankNo = remarkParams.get(RemarkPramKey.Bank_No);
		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		int client =Integer.parseInt(remarkParams.get(RemarkPramKey.CLIENT));
		
	
		String flag = cbParams.get("flag");
		if(flag.equals("2")) {
			flash.error("校验交易密码失败，请重试"); 
			
			redirect("front.account.MySecurityCtrl.securityPre");
		}else{
			
			flash.success("校验交易密码成功");
		
			rechargeUserService.updateSummaryS(businessSeqNo, "账号直充");
			
			//校验成功进行充值操作
			PaymentProxy.getInstance().recharge(client, businessSeqNo, userId, rechargeAmt,bankNo);
			
		}
		
	}
	
	/**
	 * 充值重定向
	 * 
	 * @param response
	 * @author LiuPengwei
	 * @create 2017年9月20日
	 */

	public static void rechargeUserSyn(String response,String businessSeqNo,int client) {
		ResultInfo result = new ResultInfo();
		
		TreeMap<String, String> responseMap = YbUtils.getRespHeader(response, ServiceType.CUSTOMER_RECHARGE, null);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackDataS("充值同步回调", responseMap, ServiceType.CUSTOMER_RECHARGE , TradeType.CUSTOMER_RECHARGE_WITHDRAW);
		
		switch (client) {
			case 1:{	
				if (responseMap== null || ! responseMap.get("respCode").equals("P2PS000")){

					if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
						Logger.info("受理失败！");
					}
					
					flash.error(responseMap.get("respMsg"));
					rechargeUserService.updateSummary(businessSeqNo, responseMap.get("respMsg"));

					redirect("front.account.MyAccountCtrl.rechargePre");	
				}else {
					if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
						Logger.info("受理成功！");
					}
					
					flash.error(responseMap.get("respMsg"));
					redirect("front.account.MyAccountCtrl.homePre");	
				}
				break;
			}
			case 2:
			case 3:{
				if (responseMap!= null && responseMap.get("respCode").equals("P2PS000")) {
					result.code = 1;
					result.msg = "受理成功";
					
				} else {
					result.code = -1;
					result.msg = "受理失败";
				}
				redirectApp(result);
			}
		}
		
				
	}
	
	/**
	 * 后台平台代偿充值重定向
	 * 
	 * @param response
	 * @author Liuyang
	 * @create 2017年9月20日
	 */

	public static void compensatoryRechargeSyn(String response) {
		
		TreeMap<String, String> responseMap = YbUtils.getRespHeader(response, ServiceType.COMPENSATORY_RECHARGE, null);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("平台代偿充值回调", responseMap, ServiceType.COMPENSATORY_RECHARGE , TradeType.CUSTOMER_RECHARGE_WITHDRAW);
		
		if (responseMap!= null && responseMap.get("respCode").equals("P2PS000")){
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理成功！");
			}
			flash.error(responseMap.get("respMsg"));
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");			
		}else {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理失败！");
			}
			flash.error(responseMap.get("respMsg"));
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");	
		}
				
	}
	
	/**
	 * 后台平台代偿提现重定向
	 * 
	 * @param response
	 * @author Liuyang
	 * @create 2017年9月21日
	 */

	public static void compensatoryWithdrawalSyn(String response) {
		
		TreeMap<String, String> responseMap = YbUtils.getRespHeader(response, ServiceType.COMPENSATORY_WITHDRAW, null);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("平台代偿提现回调", responseMap, ServiceType.COMPENSATORY_WITHDRAW , TradeType.CUSTOMER_RECHARGE_WITHDRAW);
		
		if (responseMap!= null && responseMap.get("respCode").equals("P2PS000")){
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理成功！");
			}
			
			flash.error(responseMap.get("respMsg"));
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");			
		}else {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理失败！");
			}
			flash.error(responseMap.get("respMsg"));
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");	
		}
				
	}
	
	/**
	 * 后台平台营销充值重定向
	 * 
	 * @param response
	 * @author Liuyang
	 * @create 2017年9月20日
	 */

	public static void marketRechargeSyn(String response) {
		
		TreeMap<String, String> responseMap = YbUtils.getRespHeader(response, ServiceType.MARKET_RECHARGE, null);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("平台营销充值回调", responseMap, ServiceType.MARKET_RECHARGE , TradeType.CUSTOMER_RECHARGE_WITHDRAW);
				
		if (responseMap!= null && responseMap.get("respCode").equals("P2PS000")){
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理成功！");
			}
			flash.error(responseMap.get("respMsg"));
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");			
		}else {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理失败！");
			}
			flash.error(responseMap.get("respMsg"));
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");	
		}
		
				
	}
	
	/**
	 * 后台平台营销提现重定向
	 * 
	 * @param response
	 * @author Liuyang
	 * @create 2017年9月21日
	 */

	public static void marketWithdrawalSyn(String response) {
		
		TreeMap<String, String> responseMap = YbUtils.getRespHeader(response, ServiceType.MARKET_WITHDRAW, null);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("平台营销提现回调", responseMap, ServiceType.MARKET_WITHDRAW , TradeType.CUSTOMER_RECHARGE_WITHDRAW);
		
		if (responseMap!= null && responseMap.get("respCode").equals("P2PS000")){
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理成功！");
			}
			flash.error(responseMap.get("respMsg"));
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");			
		}else {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理失败！");
			}
			flash.error(responseMap.get("respMsg"));
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");	
		}
		
				
	}
	
	/**
	 * 后台平台费用充值重定向
	 * 
	 * @param response
	 * @author Liuyang
	 * @create 2017年9月20日
	 */

	public static void costRechargeSyn(String response) {
		
		TreeMap<String, String> responseMap = YbUtils.getRespHeader(response, ServiceType.COST_RECHARGE, null);

		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("平台费用充值回调", responseMap, ServiceType.COST_RECHARGE , TradeType.CUSTOMER_RECHARGE_WITHDRAW);
		
		if (responseMap!= null && responseMap.get("respCode").equals("P2PS000")){
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理成功！");
			}
			flash.error(responseMap.get("respMsg"));
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");			
		}else {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理失败！");
			}
			flash.error(responseMap.get("respMsg"));
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");	
		}
		
				
	}
	
	/**
	 * 后台平台费用提现重定向
	 * 
	 * @param response
	 * @author Liuyang
	 * @create 2017年9月21日
	 */

	public static void costWithdrawalSyn(String response) {
		
		TreeMap<String, String> responseMap = YbUtils.getRespHeader(response, ServiceType.COST_WITHDRAW, null);

		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("平台费用提现回调", responseMap, ServiceType.COST_WITHDRAW , TradeType.CUSTOMER_RECHARGE_WITHDRAW);
		
		if (responseMap!= null && responseMap.get("respCode").equals("P2PS000")){
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理成功！");
			}
			
			flash.error(responseMap.get("respMsg"));
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");			
		}else {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理失败！");
			}
			flash.error(responseMap.get("respMsg"));
			redirect("back.finance.MerchantMngCtrl.toMerchantPre");	
		}
		
				
	}
	
	/**
	 * 前台校验交易密码同步回调(网银充值)
	 * 
	 * @author liuyang
	 * @create 2017年10月23日
	 */
	public static void ebankRechargeCheckPasswordSyn() {
		
		//获取回调参数
		Map<String, String> cbParams = ybPaymentCallBackService.getRespParams(params);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("校验交易密码回调", cbParams, ServiceType.FORGET_PASSWORD , TradeType.CUSTOMER_RECHARGE_WITHDRAW);
		
		if (cbParams == null) {
			flash.error("校验交易密码失败，请重试"); 
			
			redirect("front.account.MySecurityCtrl.securityPre");
		}
		
		String businessSeqNo = cbParams.get("businessSeqNo");
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParamss(businessSeqNo);
		
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");	
			redirect("front.account.MySecurityCtrl.securityPre");
		}
		
		double rechargeAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.RECHARGE_AMT));
		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		int client = Integer.parseInt(remarkParams.get(RemarkPramKey.CLIENT));
		int flags = Integer.parseInt(remarkParams.get(RemarkPramKey.BANK_FLAG));
	
		String flag = cbParams.get("flag");
		if(flag.equals("2")) {
			flash.error("校验交易密码失败，请重试"); 
			
			redirect("front.account.MySecurityCtrl.securityPre");
		}else{
			
			flash.success("校验交易密码成功");
		
			//校验成功进行充值操作
			if(flags == 2) {
				PaymentProxy.getInstance().ebankRecharge(client, businessSeqNo, userId, rechargeAmt);
			}else {
				PaymentProxy.getInstance().enterpriseEbankRecharge(client, businessSeqNo, userId, rechargeAmt, flags);
			}
			
		}
		
	}
	
	/**
	 * 后台校验交易密码同步回调(网银充值)
	 * 
	 * @author liuyang
	 * @create 2017年10月23日
	 */
	public static void ebankRechargeCheckPasswordsSyn() {
		
		//获取回调参数
		Map<String, String> cbParams = ybPaymentCallBackService.getRespParams(params);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("校验交易密码回调", cbParams, ServiceType.FORGET_PASSWORD , TradeType.CUSTOMER_RECHARGE_WITHDRAW);
		
		if (cbParams == null) {
			flash.error("校验交易密码失败，请重试"); 
			
			redirect("back.finance.GuaranteeCtrl.showGuaranteePre");
		}
		
		String businessSeqNo = cbParams.get("businessSeqNo");
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParamss(businessSeqNo);
		
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");	
			redirect("back.finance.GuaranteeCtrl.showGuaranteePre");
		}
		
		double rechargeAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.RECHARGE_AMT));
		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		int client = Integer.parseInt(remarkParams.get(RemarkPramKey.CLIENT));
		int flags = Integer.parseInt(remarkParams.get(RemarkPramKey.BANK_FLAG));
	
		String flag = cbParams.get("flag");
		if(flag.equals("2")) {
			flash.error("校验交易密码失败，请重试"); 
			
			redirect("back.finance.GuaranteeCtrl.showGuaranteePre");
		}else{
			
			flash.success("校验交易密码成功");
		
			//校验成功进行充值操作
			PaymentProxy.getInstance().enterpriseEbankRecharge(client, businessSeqNo, userId, rechargeAmt, flags);
			
		}
		
	}
	
	/**
	 *提现功能校验交易密码同步回调
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月16日
	 */
	public static void withdrawalCheckPasswordSyn() {
		
		//获取回调参数
		Map<String, String> cbParams = ybPaymentCallBackService.getRespParams(params);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("校验交易密码回调", cbParams, ServiceType.FORGET_PASSWORD , TradeType.CUSTOMER_INFO_SYNC);
		
		if (cbParams == null) {
			flash.error("校验交易密码失败，请重试"); 
			
			redirect("front.account.MySecurityCtrl.securityPre");
		}
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParams(cbParams.get("businessSeqNo"));
		
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");	
			redirect("front.account.MySecurityCtrl.securityPre");
		}
		
		String businessSeqNo = cbParams.get("businessSeqNo");
		double withdrawalAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.WITHDRAWAL_AMT));
		String bankNo = remarkParams.get(RemarkPramKey.Bank_No);
		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		double withdrawalFee = Double.parseDouble(remarkParams.get(RemarkPramKey.WITHDRAWAL_FEE));
		int client =Integer.parseInt(remarkParams.get(RemarkPramKey.CLIENT));
		
	
		String flag = cbParams.get("flag");
		if(flag.equals("2")) {
			flash.error("校验交易密码失败，请重试"); 
			
			redirect("front.account.MySecurityCtrl.securityPre");
		}else{
			
			flash.success("校验交易密码成功");
		
			
			//校验成功进行充值操作
			PaymentProxy.getInstance().withdrawal(client, businessSeqNo, userId, withdrawalAmt, bankNo, withdrawalFee);
			
		}
		
	}
	
	/**
	 *	企业提现功能校验交易密码同步回调
	 * 
	 * @author LiuPengwei
	 * @create 2018年1月10日
	 */
	public static void enterpriseWithdrawalCheckPasswordSyn() {
		
		//获取回调参数
		Map<String, String> cbParams = ybPaymentCallBackService.getRespParams(params);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("校验交易密码回调", cbParams, ServiceType.FORGET_PASSWORD , TradeType.CUSTOMER_INFO_SYNC);
		
		if (cbParams == null) {
			flash.error("校验交易密码失败，请重试"); 
			
			redirect("front.account.MySecurityCtrl.securityPre");
		}
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParams(cbParams.get("businessSeqNo"));
		
		if (remarkParams == null) {
			flash.error("查询托管请求备注参数失败");	
			redirect("front.account.MySecurityCtrl.securityPre");
		}
		
		String businessSeqNo = cbParams.get("businessSeqNo");
		double withdrawalAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.WITHDRAWAL_AMT));
		String bankNo = remarkParams.get(RemarkPramKey.Bank_No);
		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		double withdrawalFee = Double.parseDouble(remarkParams.get(RemarkPramKey.WITHDRAWAL_FEE));
		int client =Integer.parseInt(remarkParams.get(RemarkPramKey.CLIENT));
		
	
		String flag = cbParams.get("flag");
		if(flag.equals("2")) {
			flash.error("校验交易密码失败，请重试"); 
			
			redirect("front.account.MySecurityCtrl.securityPre");
		}else{
			
			flash.success("校验交易密码成功");
		
			//校验成功进行充值操作
			PaymentProxy.getInstance().enterpriseWithdrawal(client, businessSeqNo, userId, withdrawalAmt, bankNo, withdrawalFee);
			
		}
		
	}
	
	
	/**
	 * 提现重定向
	 * 
	 * @param response
	 * @author LiuPengwei
	 * @create 2017年9月20日
	 */

	public static void withdrawalUserSyn(String response, String businessSeqNo, int client) {
		ResultInfo result = new ResultInfo();

		TreeMap<String, String> responseMap = YbUtils.getRespHeader(response, ServiceType.CUSTOMER_WITHDRAW, null);

		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackDataS("客户提现同步回调", responseMap, ServiceType.CUSTOMER_WITHDRAW , TradeType.CUSTOMER_RECHARGE_WITHDRAW);
		
		switch (client) {
		case 1:{	
			if (responseMap!= null && responseMap.get("respCode").equals("P2PS000")){
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info("受理成功！");
				}
				
				flash.error(responseMap.get("respMsg"));
				
				withdrawalUserService.updateSummaryS(businessSeqNo, "用户提现");
				redirect("front.account.MyAccountCtrl.homePre");	

			}else {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info("受理失败！");
				}
				
				flash.error(responseMap.get("respMsg"));
				
				withdrawalUserService.updateSummary(businessSeqNo, responseMap.get("respMsg"));
				redirect("front.account.MyAccountCtrl.rechargePre");	
			}
			break;
		}
		case 2:
		case 3:{
			if (responseMap!= null && responseMap.get("respCode").equals("P2PS000")) {
				
				withdrawalUserService.updateSummary(businessSeqNo, responseMap.get("respMsg"));
				result.code = 1;
				result.msg = "受理成功";
				
			} else {
				
				withdrawalUserService.updateSummaryS(businessSeqNo, "用户提现");
				result.code = -1;
				result.msg = "受理失败";
			}
			redirectApp(result);
		}
	}
	}
	
	/**
	 * 企业提现重定向
	 * 
	 * @param response
	 * @author LiuPengwei
	 * @create 2017年9月20日
	 */

	public static void withdrawalUsersSyn(String response, String businessSeqNo) {
	
		TreeMap<String, String> responseMap = YbUtils.getRespHeader(response, ServiceType.ENTERPRISE_WITHDRAW, null);

		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackDataS("企业客户提现回调", responseMap, ServiceType.ENTERPRISE_WITHDRAW , TradeType.CUSTOMER_RECHARGE_WITHDRAW);
		
		if (responseMap!= null && responseMap.get("respCode").equals("P2PS000")){
			withdrawalUserService.updateSummaryS(businessSeqNo, "企业用户提现");
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理成功！");
			}
			
			flash.error(responseMap.get("respMsg"));
			
			redirect("front.account.MyAccountCtrl.homePre");
		}else {
			
			withdrawalUserService.updateSummary(businessSeqNo, responseMap.get("respMsg"));
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理失败！");
			}
			
			flash.error(responseMap.get("respMsg"));
	
					
			redirect("front.account.MyAccountCtrl.withdrawPre");	
		}
				
	}
	
	

	/**
	 * 债权转让重定向
	 * 
	 * @param response
	 * @author LiuPengwei
	 * @create 2017年10月13日
	 */

	public static void debtTransferSyn(String response ,String serviceOrderNo, Long debtId, Long toUserId, double debtFee,int client) {
		ResultInfo result = new ResultInfo();
		JSONObject json = new JSONObject();
		TreeMap<String, String> responseMap = YbUtils.getRespHeader(response, ServiceType.FARM_IN, null);

		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("客户转受让", responseMap, ServiceType.FARM_IN , TradeType.FUND_TRADE);
		if (responseMap== null || !responseMap.get("respCode").equals("P2P0000")){
			flash.error(responseMap.get("respMsg"));
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info(responseMap.get("respMsg"));
			}
			redirect("front.account.MyFundCtrl.accountManagePre", 2);
			
		}
		
		result = ybPaymentCallBackService.debtTransfer(serviceOrderNo, debtId, toUserId, debtFee);	

		switch (client) {
		case 1:{	
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				
				flash.success("债权已经成交");	
				if (sendTextMessage(debtId)) {
					Logger.info(debtId+"债权转让短信发送成功");
				}else {
					Logger.info(debtId+"债权转让短信发送失败");
				}
				redirect("front.account.MyFundCtrl.accountManagePre", 2);
			} else {
				flash.error(responseMap.get("respMsg"));
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(responseMap.get("respMsg"));
				}
				redirect("controllers.front.invest.InvestCtrl.transferPre", debtId);
			}
			break;
		}
		case 2:
		case 3:{
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				json.put("code",1);
				json.put("msg","债权已经成交!") ;
				
			} else {
				json.put("code",-1);
				json.put("msg","购买失败!") ;
			}
			break;
			}
		default:{//

			break;
		}		
			
	}
			
		
	}
	
	/**
	 * 
	 * @Title: sendTextMessage
	 * @description 债权转让成功发送短信
	 * @param debtId
	 * @return
	 * boolean
	 * @author likai
	 * @createDate 2018年12月17日 下午5:58:16
	 */
	public static boolean sendTextMessage(Long debtId) {
		Map<String, String> map = new HashMap<String,String>();
		t_debt_transfer debt = debtService.findByID(debtId);
		//转让人
		t_user_info alienator = userInfoService.findUserInfoByUserId(debt.user_id);
		//受让人
		t_user_info assignee = userInfoService.findUserInfoByUserId(debt.transaction_user_id);
		t_invest invest = investService.findByID(debt.invest_id);
		map.put("alienator", alienator.name);
		map.put("alienatorName", alienator.reality_name);
		map.put("date", TimeUtil.dateToStrCurr(debt.time));
		map.put("assignee", assignee.name);
		map.put("bid", "J"+invest.bid_id);
		if(JYSMSUtil.sendMessage(alienator.mobile,JYSMSModel.MODEL52.tplId, map)){
			return true;
		}
		return false;
	}


	/**
	 * 宜宾银行异步回调
	 * 
	 * @author niu
	 * @create 2017.09.05
	 */
	public static void yibinCallBackAsyn() {
		
		Logger.info("***************异步回调开始*****");

		// 1.获取报文
		JSONObject.fromObject(params).toString().substring(0, 0);
		
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
		
		TreeMap<String, String> map = YbUtils.getAysnResponse(body.toString());
		
		if (map == null) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("异步响应接收失败");
			}
			
			return;
		}
		
		ServiceType serviceType = ybPaymentCallBackService.getServiceType(map.get("oldbusinessSeqNo"));
		
		if (serviceType == null) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("获取业务类型失败");
			}
			
			return;
		}
		
		//应答参数拼装
		HashMap<String, JSONObject> reqParams =YbUtils.getResaHeader(map,null,serviceType.key,TradeType.CUSTOMER_RECHARGE_WITHDRAW.key,null);			
		
		ResultInfo result = null;
		
		//备注参数调用（交易密码存,不能获得解绑-交易密码备注参数)
		Map<String, String> remarkParams = null;
		//备注参数调用（业务接口存）
		Map<String, String> CallServiceParams = ybPaymentCallBackService.queryServiceRequestParam(map.get("oldbusinessSeqNo"));
		
		//业务调用
		switch (serviceType.code) {
		case 14:
			//异步回调保存
			ybPaymentCallBackService.printOutCallBack("用户充值异步回调", map, ServiceType.CUSTOMER_RECHARGE , TradeType.CUSTOMER_RECHARGE_WITHDRAW, CallServiceParams);
			
			//错误直接返回
			if (!map.get("respCode").equals("P2P0000")) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(map.get("respMsg"));
				}
				
				rechargeUserService.updateSummary(map.get("oldbusinessSeqNo"), map.get("respMsg"));
								
				renderJSON(JSONObject.fromObject(reqParams).toString());
			}
			
			remarkParams = ybPaymentCallBackService.queryRequestParams(map.get("oldbusinessSeqNo"));
			//业务调用
			result = ybPaymentCallBackService.netSave(map,remarkParams);
			
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(result.msg);
				}
			
			} else {
				
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(result.msg);
				}
				rechargeUserService.updateSummary(map.get("oldbusinessSeqNo"), result.msg);
				
			}
			renderJSON(JSONObject.fromObject(reqParams).toString());
			break;
		case 15:
			//异步回调保存
			ybPaymentCallBackService.printOutCallBack("用户提现异步回调", map, ServiceType.CUSTOMER_WITHDRAW , TradeType.CUSTOMER_RECHARGE_WITHDRAW, CallServiceParams);
			
			//错误直接返回
			if (!map.get("respCode").equals("P2P0000")) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(map.get("respMsg"));
				}
				
				
				withdrawalUserService.updateSummary(map.get("oldbusinessSeqNo"), map.get("respMsg"));
				
				renderJSON(JSONObject.fromObject(reqParams).toString());
			}
			
			remarkParams = ybPaymentCallBackService.queryRequestParams(map.get("oldbusinessSeqNo"));
			//业务调用
			result = ybPaymentCallBackService.withdrawal(map, remarkParams);
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info("提现业务处理完成");
				}
				
			} else {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(result.msg);
				}
				
				withdrawalUserService.updateSummary(map.get("oldbusinessSeqNo"), result.msg);
			}
			renderJSON(JSONObject.fromObject(reqParams).toString());
			break;
		case 16:
			//错误直接返回
			if (!map.get("respCode").equals("P2P0000")) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(map.get("respMsg"));
				}
				
				renderJSON(JSONObject.fromObject(reqParams).toString());
			}

			remarkParams = ybPaymentCallBackService.queryRequestParams(map.get("oldbusinessSeqNo"));
			
			if(remarkParams != null) {
				//得到用户编号
				String userId = remarkParams.get("r_user_id");
				if(userId != null) {
					long userIds = Integer.parseInt(userId);
					//修改数据库标识
					t_user_fund userFund = userFundService.findByColumn("user_id=?", userIds);
					userFund.transaction_password = 1;
					userFund.save();
					renderJSON(JSONObject.fromObject(reqParams).toString());
				}
			}
				
			break;	
		case 40:
			//错误直接返回
			if (!map.get("respCode").equals("P2P0000")) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(map.get("respMsg"));
				}
				
				renderJSON(JSONObject.fromObject(reqParams).toString());
			}
			
			paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(map.get("oldbusinessSeqNo"));
			renderJSON(JSONObject.fromObject(reqParams).toString());	
				
			break;
		case 41:
			//错误直接返回
			if (!map.get("respCode").equals("P2P0000")) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(map.get("respMsg"));
				}
				renderJSON(JSONObject.fromObject(reqParams).toString());
			}
			
			paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(map.get("oldbusinessSeqNo"));
			renderJSON(JSONObject.fromObject(reqParams).toString());	
				
			break;
		case 42:
			//错误直接返回
			if (!map.get("respCode").equals("P2P0000")) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(map.get("respMsg"));
				}
				renderJSON(JSONObject.fromObject(reqParams).toString());
			}

			paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(map.get("oldbusinessSeqNo"));
			renderJSON(JSONObject.fromObject(reqParams).toString());	
				
			break;
		case 43:
			//错误直接返回
			if (!map.get("respCode").equals("P2P0000")) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(map.get("respMsg"));
				}
				renderJSON(JSONObject.fromObject(reqParams).toString());
			}

			paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(map.get("oldbusinessSeqNo"));
			renderJSON(JSONObject.fromObject(reqParams).toString());	
			
			break;
		case 44:
			//错误直接返回
			if (!map.get("respCode").equals("P2P0000")) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(map.get("respMsg"));
				}
				renderJSON(JSONObject.fromObject(reqParams).toString());
			}
			
			paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(map.get("oldbusinessSeqNo"));
			renderJSON(JSONObject.fromObject(reqParams).toString());	
				
			break;
		case 45:
			//错误直接返回
			if (!map.get("respCode").equals("P2P0000")) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(map.get("respMsg"));
				}
				renderJSON(JSONObject.fromObject(reqParams).toString());
			}
			
			paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(map.get("oldbusinessSeqNo"));
			renderJSON(JSONObject.fromObject(reqParams).toString());	
				
			break;
		case 46:
			
			//异步回调保存
			ybPaymentCallBackService.printOutCallBack("企业提现异步回调", map, ServiceType.ENTERPRISE_WITHDRAW , TradeType.CUSTOMER_RECHARGE_WITHDRAW, CallServiceParams);
			
			//错误直接返回
			if (!map.get("respCode").equals("P2P0000")) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(map.get("respMsg"));
				}
				
				withdrawalUserService.updateSummary(map.get("oldbusinessSeqNo"), map.get("respMsg"));
				
				renderJSON(JSONObject.fromObject(reqParams).toString());
			}
	
			remarkParams = ybPaymentCallBackService.queryRequestParams(map.get("oldbusinessSeqNo"));
			//业务调用
			result = ybPaymentCallBackService.withdrawal(map, remarkParams);
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info("企业提现业务处理完成");
				}
				
				
			} else {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(result.msg);
				}
				
				withdrawalUserService.updateSummary(map.get("oldbusinessSeqNo"), result.msg);
				
			}
			
			renderJSON(JSONObject.fromObject(reqParams).toString());
			break;
		case 4:
			System.out.println(serviceType.note);
			break;
		case 101:
			
			//异步回调保存
			ybPaymentCallBackService.printOutCallBack("用户网银充值异步回调", map, ServiceType.CUSTOMER_INTERNATBANK_RECHARGE , TradeType.CUSTOMER_RECHARGE_WITHDRAW, CallServiceParams);
			
			HashMap<String, JSONObject> reqParamsRecharge =YbUtils.getResaHeader(map,null,ServiceType.CUSTOMER_INTERNATBANK_RECHARGE.key,TradeType.CUSTOMER_RECHARGE_WITHDRAW.key,null);
			//错误直接返回
			if (!map.get("respCode").equals("P2P0000")) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(map.get("respMsg"));
				}
				
				rechargeUserService.updateSummary(map.get("oldbusinessSeqNo"), map.get("respMsg"));
				
				renderJSON(JSONObject.fromObject(reqParamsRecharge).toString());
			}
			
			remarkParams = ybPaymentCallBackService.queryRequestParams(map.get("oldbusinessSeqNo"));
			//业务调用
			result = ybPaymentCallBackService.internetbankNetSave(map,remarkParams);
			
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(remarkParams.get("businessSeqNo"));
				
			} else {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(result.msg);
				}
				
				rechargeUserService.updateSummary(map.get("oldbusinessSeqNo"), result.msg);	
			}
			renderJSON(JSONObject.fromObject(reqParamsRecharge).toString());
			break;
		case 102:
			//异步回调保存
			ybPaymentCallBackService.printOutCallBack("企业网银充值异步回调", map, ServiceType.ENTERPRISE_INTERNATBANK_RECHARGE , TradeType.CUSTOMER_RECHARGE_WITHDRAW, CallServiceParams);
			
			HashMap<String, JSONObject> reqParamsRecharges =YbUtils.getResaHeader(map,null,ServiceType.ENTERPRISE_INTERNATBANK_RECHARGE.key,TradeType.CUSTOMER_RECHARGE_WITHDRAW.key,null);
			//错误直接返回
			if (!map.get("respCode").equals("P2P0000")) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(map.get("respMsg"));
				}
				
				rechargeUserService.updateSummary(map.get("oldbusinessSeqNo"), map.get("respMsg"));
				renderJSON(JSONObject.fromObject(reqParamsRecharges).toString());
				
			}
			
			t_recharge_user rechargeUser = rechargeUserService.findByColumn("order_no=? and status = 0", map.get("oldbusinessSeqNo"));
			
			if(rechargeUser == null) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(map.get("respMsg"));
				}
				
				renderJSON(JSONObject.fromObject(reqParamsRecharges).toString());
				
				break;
			}
		
			result = userFundService.internetDoRecharge(rechargeUser.user_id, rechargeUser.amount, map.get("oldbusinessSeqNo"));
			
			if (result.code > 0 ) {
				
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(map.get("oldbusinessSeqNo"));
				
			} else {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info(result.msg);
				}
				rechargeUserService.updateSummary(map.get("oldbusinessSeqNo"), result.msg);
				
			}
			renderJSON(JSONObject.fromObject(reqParamsRecharges).toString());
			break;
		default:
			
			renderJSON(JSONObject.fromObject(reqParams).toString());
			break;
		}
		
		Logger.info("***************异步回调结束***************");	
					
	}
	
	/**
	 * 标的发布页面跳转
	 * 
	 * @author niu
	 * @create 2017.09.11
	 */
	public void addBidInfoWS (ResultInfo result, t_bid bid, int bidCreateFrom) {
		
		if(result.code > 0 || result.code == ResultInfo.ALREADY_RUN){
			if (bidCreateFrom == Constants.BID_CREATE_FROM_FRONT) {
				//标的发布成功，数据库中才有标的信息，取出标的id
				long bidId = bidService.findIdByMerBidNo(bid.mer_bid_no, -1L);
				if (bidId == -1) {
					if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
						Logger.info("===========bid.mer_bid_no="+bid.mer_bid_no+";bid.product_id"+ bid.product_id);
					}
					
					flash.success("项目发布成功");
					redirect("front.LoanCtrl.toLoanPre");
				}

				redirect("front.LoanCtrl.uploadBidItemPre", bidId);
			}
			
			if (bidCreateFrom == Constants.BID_CREATE_FROM_BACK) {
				flash.success("项目发布成功");
				redirect("back.risk.LoanMngCtrl.showBidPre");
			}
		} else {
			long productId = bid.product_id;
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);
			
			redirect("front.LoanCtrl.toLoanPre", productId);
		}
	}
	
	
	/**
	 * 宜宾银行设置交易密码同步回调
	 * 
	 * @author liu
	 * @create 2017.09.07
	 */
	public static void setUserPasswordSyn() {
		
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = ybPaymentCallBackService.getRespParams(params);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("设置交易密码回调", cbParams, ServiceType.SET_USER_PASSWORD , TradeType.CUSTOMER_INFO_SYNC);
		
		if (cbParams == null) {
			flash.error("设置交易密码失败，请重试"); 
			
			redirect("front.account.MySecurityCtrl.securityPre");
		}
		
		//得到用户编号
		String userId = cbParams.get("userId");
		long userIds = Integer.parseInt(userId);
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParamss(cbParams.get("businessSeqNo"));
		
		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT),Client.PC.code));
		
		//根据标识判断密码是否设置成功
		String flag = cbParams.get("flag");
		
		switch (client) {
		case PC:{
			if (flag.equals("2")) {
				flash.error("设置交易密码失败，请重试"); 
				
				redirect("front.account.MySecurityCtrl.securityPre");
			} else {
				//修改数据库标识
				t_user_fund userFund = userFundService.findByColumn("user_id=?", userIds);
				userFund.transaction_password = 1;
				userFund.save();
				
				flash.success("设置密码成功");
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				redirect("front.account.MySecurityCtrl.securityPre");	
			}
			break;
		}
		case ANDROID:{
			if (flag.equals("2")) {
				result.code = -1;
				result.msg = "设置交易密码失败";
			} else {
				//修改数据库标识
				t_user_fund userFund = userFundService.findByColumn("user_id=?", userIds);
				userFund.transaction_password = 1;
				userFund.save();
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				result.code = 1;
				result.msg = "设置交易密码成功";
			}
			redirectApp(result);
			break;
		}
		case IOS:{
			if (flag.equals("2")) {
				result.code = -1;
				result.msg = "设置交易密码失败";
			} else {
				//修改数据库标识
				t_user_fund userFund = userFundService.findByColumn("user_id=?", userIds);
				userFund.transaction_password = 1;
				userFund.save();
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				result.code = 1;
				result.msg = "设置交易密码成功";
			}
			redirectApp(result);
			break;
		}
		case WECHAT:{
			
			break;
		}
	
		default:
			break;
		}
	}
	
	/**
	 * 宜宾银行修改交易密码同步回调
	 * 
	 * @author liu
	 * @create 2017.09.08
	 */
	public static void amendUserPasswordSyn() {

		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = ybPaymentCallBackService.getRespParams(params);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("修改交易密码回调", cbParams, ServiceType.AMEND_USER_PASSWORD , TradeType.CUSTOMER_INFO_SYNC);
		
		if (cbParams == null) {
			flash.error("修改交易密码失败，请重试"); 
			
			redirect("front.account.MySecurityCtrl.securityPre");
		}
		
		//获取用户编号
		String userId = cbParams.get("userId");
		long userIds = Integer.parseInt(userId);
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParamss(cbParams.get("businessSeqNo"));
		
		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT),Client.PC.code));
		
		//根据标识判断密码是否设置成功
		String flag = cbParams.get("flag");
		
		switch (client) {
		case PC:{
			if (flag.equals("2")) {
				flash.error("修改交易密码失败，请重试"); 
				
				redirect("front.account.MySecurityCtrl.securityPre");
			} else {
				//修改数据库标识
				t_user_fund userFund = userFundService.findByColumn("user_id=?", userIds);
				userFund.transaction_password = 1;
				userFund.save();
				
				flash.success("修改密码成功");
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				redirect("front.account.MySecurityCtrl.securityPre");	
			}
			break;
		}
		case ANDROID:{
			if (flag.equals("2")) {
				result.code = -1;
				result.msg = "修改交易密码失败";
			} else {
				//修改数据库标识
				t_user_fund userFund = userFundService.findByColumn("user_id=?", userIds);
				userFund.transaction_password = 1;
				userFund.save();
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				result.code = 1;
				result.msg = "修改交易密码成功";
			}
			redirectApp(result);
			break;
		}
		case IOS:{
			if (flag.equals("2")) {
				result.code = -1;
				result.msg = "修改交易密码失败";
			} else {
				//修改数据库标识
				t_user_fund userFund = userFundService.findByColumn("user_id=?", userIds);
				userFund.transaction_password = 1;
				userFund.save();
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				result.code = 1;
				result.msg = "修改交易密码成功";
			}
			redirectApp(result);
			break;
		}
		case WECHAT:{
			
			break;
		}
	
		default:
			break;
		}
	}
	
	/**
	 * 宜宾银行重置交易密码同步回调
	 * 
	 * @author liu
	 * @create 2017.09.08
	 */
	public static void resetUserPasswordSyn() {

		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = ybPaymentCallBackService.getRespParams(params);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("重置交易密码回调", cbParams, ServiceType.RESET_USER_PASSWORD , TradeType.CUSTOMER_INFO_SYNC);
		
		if (cbParams == null) {
			flash.error("重置交易密码失败，请重试"); 
			
			redirect("front.account.MySecurityCtrl.securityPre");
		}
		
		//获取用户编号
		String userId = cbParams.get("userId");
		long userIds = Integer.parseInt(userId);
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParamss(cbParams.get("businessSeqNo"));
		
		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT),Client.PC.code));
		
		//根据标识判断密码是否设置成功
		String flag = cbParams.get("flag");
		
		switch (client) {
		case PC:{
			if (flag.equals("2")) {
				flash.error("重置交易密码失败，请重试"); 
				
				redirect("front.account.MySecurityCtrl.securityPre");
			} else {
				//修改数据库标识
				t_user_fund userFund = userFundService.findByColumn("user_id=?", userIds);
				userFund.transaction_password = 1;
				userFund.save();
				
				flash.success("重置交易密码成功");
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				redirect("front.account.MySecurityCtrl.securityPre");	
			}
			break;
		}
		case ANDROID:{
			if (flag.equals("2")) {
				result.code = -1;
				result.msg = "重置交易密码失败";
			} else {
				//修改数据库标识
				t_user_fund userFund = userFundService.findByColumn("user_id=?", userIds);
				userFund.transaction_password = 1;
				userFund.save();
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				result.code = 1;
				result.msg = "重置交易密码成功";
			}
			redirectApp(result);
			break;
		}
		case IOS:{
			if (flag.equals("2")) {
				result.code = -1;
				result.msg = "重置交易密码失败";
			} else {
				//修改数据库标识
				t_user_fund userFund = userFundService.findByColumn("user_id=?", userIds);
				userFund.transaction_password = 1;
				userFund.save();
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				result.code = 1;
				result.msg = "重置交易密码成功";
			}
			redirectApp(result);
			break;
		}
		case WECHAT:{
			
			break;
		}
	
		default:
			break;
		}
	}
	
	/**
	 * 宜宾银行校验交易密码同步回调
	 * 
	 * @author liu
	 * @create 2017.09.07
	 */
	public static void checkPasswordSyn() {

		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = ybPaymentCallBackService.getRespParams(params);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("校验交易密码回调", cbParams, ServiceType.FORGET_PASSWORD , TradeType.CUSTOMER_INFO_SYNC);
		
		if (cbParams == null) {
			flash.error("校验交易密码失败，请重试"); 
			
			redirect("front.account.MySecurityCtrl.securityPre");
		}
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParamss(cbParams.get("businessSeqNo"));
		
		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT),Client.PC.code));
		
		//根据标识判断密码是否设置成功
		String flag = cbParams.get("flag");
		
		switch (client) {
		case PC:{
			if (flag.equals("2")) {
				flash.error("校验交易密码失败，请重试"); 
				
				redirect("front.account.MySecurityCtrl.securityPre");
			} else {
				
				flash.success("校验交易密码成功");
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				MySecurityCtrl.toBindCards1Pre(remarkParams.get("businessSeqNo"),remarkParams.get("mobilePhone"));
			}
			break;
		}
		case ANDROID:{
			if (flag.equals("2")) {
				result.code = -1;
				result.msg = "校验交易密码失败";
			} else {
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				result.code = 1;
				result.msg = "校验交易密码成功";
			}
			redirectApp(result);
			break;
		}
		case IOS:{
			if (flag.equals("2")) {
				result.code = -1;
				result.msg = "校验交易密码失败";
			} else {
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				result.code = 1;
				result.msg = "校验交易密码成功";
			}
			redirectApp(result);
			break;
		}
		case WECHAT:{
			
			break;
		}
	
		default:
			break;
		}
		
	}

	

	
	/**
	 * 绑定银行卡同步回调
	 * 
	 * @author liu
	 * @create 2017.09.13
	 */
	public static void userBindCardSyn() {
		
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = ybPaymentCallBackService.getRespParams(params);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("绑定银行卡回调", cbParams, ServiceType.CUSTOMER_BIND_CARD , TradeType.CUSTOMER_INFO_SYNC);
		
		if(cbParams == null) {
			flash.error("绑定银行卡失败，请重试");
		}
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParamss(cbParams.get("businessSeqNo"));
		
		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT),Client.PC.code));
		
		//根据标识判断密码是否设置成功
		String flag = cbParams.get("flag");
		
		switch (client) {
		case PC:{
			if (flag.equals("2")) {
				flash.error("绑定银行卡失败，请重试"); 
				
			} else {
				flash.success("绑定银行卡成功");
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
			}
			MySecurityCtrl.securitysPre();
			break;
		}
		case ANDROID:{
			if (flag.equals("2")) {
				result.code = -1;
				result.msg = "绑定银行卡失败";
			} else {
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				result.code = 1;
				result.msg = "绑定银行卡成功";
			}
			redirectApp(result);
			break;
		}
		case IOS:{
			if (flag.equals("2")) {
				result.code = -1;
				result.msg = "绑定银行卡失败";
			} else {
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				result.code = 1;
				result.msg = "绑定银行卡成功";
			}
			redirectApp(result);
			break;
		}
		case WECHAT:{
			
			break;
		}
		
		}
	
	}
	
	/**
	 * 网银充值跳转H5页面
	 * 
	 * @param response
	 * @author liuyang
	 * @create 2017年10月24日
	 */

	public static void ebankRechargeUserSyn(String response,String businessSeqNo,int client) {
		
		TreeMap<String, String> responseMap = YbUtils.getRespHeader(response, ServiceType.CUSTOMER_INTERNATBANK_RECHARGE, null);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("网银充值回调", responseMap, ServiceType.CUSTOMER_INTERNATBANK_RECHARGE , TradeType.CUSTOMER_RECHARGE_WITHDRAW);
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParams(businessSeqNo);
		
		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		
		String rechargeAmt = remarkParams.get(RemarkPramKey.RECHARGE_AMT);
		
		if (responseMap== null || !(responseMap.get("respCode").equals("P2PS000"))){

			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理失败！");
			}
			
			flash.error(responseMap.get("respMsg"));
			rechargeUserService.updateSummary(businessSeqNo, responseMap.get("respMsg"));

			redirect("front.account.MyAccountCtrl.rechargePre");	
		}else {
			rechargeUserService.updateSummaryS(businessSeqNo, "账户网银充值");
			
			HashMap<String, String> reqParams = ybPaymentCallBackService.toRechargerByCpcn(userId, businessSeqNo, rechargeAmt);		
			
			//生成表单
			String html = YbUtils.createFormHtmlPost(reqParams, YbConsts.RECHARGERBYCPCN);
			
			// 表单提交
			YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		}
				
	}
	
	/**
	 * 企业网银充值跳转H5页面
	 * 
	 * @param response
	 * @author liuyang
	 * @create 2017年12月18日
	 */

	public static void enterpriseEbankRechargeUserSyn(String response, String businessSeqNo, int client, long userId, double rechargeAmt, int flags) {
		
		TreeMap<String, String> responseMap = YbUtils.getRespHeader(response, ServiceType.ENTERPRISE_INTERNATBANK_RECHARGE, null);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("网银充值回调", responseMap, ServiceType.ENTERPRISE_INTERNATBANK_RECHARGE , TradeType.CUSTOMER_RECHARGE_WITHDRAW);
		
		if (responseMap== null || !(responseMap.get("respCode").equals("P2PS000"))){

			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("受理失败！");
			}
			flash.error(responseMap.get("respMsg"));
			rechargeUserService.updateSummary(businessSeqNo, responseMap.get("respMsg"));
			
			if(flags>4) {
				redirect("back.finance.GuaranteeCtrl.showGuaranteePre");
			}else {
				redirect("front.account.MyAccountCtrl.rechargePre");
			}

		}else {
			
			rechargeUserService.updateSummaryS(businessSeqNo, "账户网银充值");

			HashMap<String, String> reqParams = ybPaymentCallBackService.toEnterpriseRechargerByCpcn(userId, businessSeqNo, rechargeAmt, flags);		
			
			//生成表单
			String html = YbUtils.createFormHtmlPost(reqParams, YbConsts.RECHARGERBYCPCN);
			
			// 表单提交
			YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		}
				
	}
	
   /**
	* 开启委托协议验密回调
	* 
	* @author LiuPengwei 
	* @date 2018年1月18日
	*
	*/
	public static void autoCheckPasswordSyn() {
		ResultInfo result = new ResultInfo();
		//获取回调参数
		Map<String, String> cbParams = ybPaymentCallBackService.getRespParams(params);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("校验交易密码回调", cbParams, ServiceType.FORGET_PASSWORD , TradeType.CUSTOMER_ENTRUST_PROTOCOL);
		
		if (cbParams == null) {
			flash.error("校验交易密码失败，请重试"); 
			
			redirect("front.account.MyFundCtrl.accountAutoInvestPre");
		}
		
		//获取业务流水号
		String businessSeqNo = cbParams.get("businessSeqNo");
	
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParamss(businessSeqNo);
		
		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		double min_apr=Double.parseDouble(remarkParams.get(RemarkPramKey.MIN_APR));
		int max_period=Integer.parseInt(remarkParams.get(RemarkPramKey.MAX_PERIOD));
		double invest_amt=Double.parseDouble(remarkParams.get(RemarkPramKey.INVEST_AMT));
		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT),Client.PC.code));
		String valid_time = remarkParams.get(RemarkPramKey.VALID_TIME);
		int valid_day = Integer.parseInt(remarkParams.get(RemarkPramKey.VALID_DAY));
		//根据标识判断校验密码是否成功
		String flag = cbParams.get("flag");
		switch (client){
			case PC:{
			
				if(flag.equals("2")) {
					flash.error("校验交易密码失败，请重试"); 
					redirect("front.account.MyFundCtrl.accountAutoInvestPre");
				}else{
					flash.success("校验交易密码成功");
					MyFundCtrl.openEntrustPre(businessSeqNo,userId,ServiceType.ENTRUST_PROTOCOL_AGREE,min_apr,max_period,invest_amt,valid_time,valid_day);
				}
				break;
			}
			case IOS:
			case ANDROID:{
				if(flag.equals("2")){
					result.code = -1;
					result.msg = "校验交易密码失败，请重试";
				}else{
					result.code = 1;
					result.msg = "校验交易密码成功";
					result=PaymentProxy.getInstance().autoInvestSignature(client.code, businessSeqNo, userId,ServiceType.ENTRUST_PROTOCOL_AGREE,invest_amt,valid_time);
					if(result.code < 1){
						result.code = -1;
						result.msg = "委托协议签署失败";
						redirectApp(result);
					}
					//委托协议号
					String protocolNo = result.obj.toString();
					result = investService.newAutoInvest(userId, min_apr, max_period, invest_amt, valid_day, protocolNo);
					
				}
				redirectApp(result);
				break;
			}

			case WECHAT:{
				
				break;
			}
			
		}
	}
	
	/**
	 * 
	* 
	* @Description: TODO(关闭委托回调) 
	* @author lihuijun 
	* @date 2017年9月26日 上午10:28:37 
	*
	 */
	public static void closeAutoCheckPasswordSyn() {
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = ybPaymentCallBackService.getRespParams(params);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("校验交易密码回调", cbParams, ServiceType.FORGET_PASSWORD , TradeType.CUSTOMER_ENTRUST_PROTOCOL);
		
		if (cbParams == null) {
			flash.error("校验交易密码失败，请重试");
			redirect("front.account.MyFundCtrl.accountAutoInvestPre");
		}
		
		//获取业务流水号
		String businessSeqNo = cbParams.get("businessSeqNo");
	
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParamss(businessSeqNo);
		
		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		String valid_time = remarkParams.get(RemarkPramKey.VALID_TIME);
		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT),Client.PC.code));
		
		//根据标识判断校验密码是否成功
		String flag = cbParams.get("flag");
		
		switch (client){
			case PC:{
			
				if(flag.equals("2")) {
					flash.error("校验交易密码失败，请重试"); 
					
					redirect("front.account.MyFundCtrl.accountAutoInvestPre");
				}else{
					flash.success("校验交易密码成功");			
					
					MyFundCtrl.openEntrustPre(businessSeqNo,userId,ServiceType.ENTRUST_PROTOCOL_CANCEL,0,0,0,valid_time,0);
				}
				break;
			}
			
			case IOS:
			case ANDROID:{
				if(flag.equals("2")){
					result.code = -1;
					result.msg = "校验交易密码失败，请重试";
				}else{
					result.code = 1;
					result.msg = "校验交易密码成功";
					result=PaymentProxy.getInstance().autoInvestSignature(client.code, businessSeqNo, userId,ServiceType.ENTRUST_PROTOCOL_CANCEL,0,valid_time);
					if(result.code < 1){
						
						result.code = -1;
						result.msg = "委托协议签署失败";
						redirectApp(result); 
					}
					result = investService.closeAutoInvest(userId);
				}
				redirectApp(result); 
				break;
			}
			
			case WECHAT:{
				
				break;
			}			
		}
	}
	
	/**
	 * APP 投标验密回调
	 * 
	 * 
	 * @author niu
	 * @create 2017.10.13
	 */
	public static void checkPassAI() {
		
		ResultInfo result = new ResultInfo();
		
		result = callBackService.checkTradePassCB(params, ServiceType.TENDER);
		
		if (result.code < 0) {
			redirectApp(result);
		}
		
		Map<String, String> reqParams = result.msgs;
		
		result = InvestAction.invest(reqParams);

		redirectApp(result);
	}
	
	/**
	 * APP 还款验密回调
	 * 
	 * 
	 * @author niu
	 * @create 2017.10.13
	 */
	public static void repaymentCheckPass() {
		
		ResultInfo result = new ResultInfo();
		
		result = callBackService.checkTradePassCB(params, ServiceType.REPAYMENT);
		
		if (result.code < 0) {
			redirectApp(result);
		}
		
		Map<String, String> reqParams = result.msgs;
		String businessSeqNo = result.msg;
		
		result = MyFundAction.repayment(reqParams, businessSeqNo);
		
		redirectApp(result);
	}
	
	/**
	 * 宜宾银行校验交易密码同步回调(银行卡预留手机号)
	 * 
	 * @author guoShiJie
	 * @create 2018.04.16
	 */
	public static void checkPasswordSyn1(String mobilePhone) {
		
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = ybPaymentCallBackService.getRespParams(params);
		
		//打印、保存接口回调参数
		ybPaymentCallBackService.printCallBackData("校验交易密码回调", cbParams, ServiceType.FORGET_PASSWORD , TradeType.CUSTOMER_INFO_SYNC);
		
		if (cbParams == null) {
			flash.error("校验交易密码失败，请重试"); 
			
			redirect("front.account.MySecurityCtrl.securityPre");
		}
		
		//备注参数调用
		Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParamss(cbParams.get("businessSeqNo"));
		
		Client client = Client.getEnum(Convert.strToInt(remarkParams.get(RemarkPramKey.CLIENT),Client.PC.code));
		
		//根据标识判断密码是否设置成功
		String flag = cbParams.get("flag");
		
		switch (client) {
		case PC:{
			if (flag.equals("2")) {
				flash.error("校验交易密码失败，请重试"); 
				
				redirect("front.account.MySecurityCtrl.securityPre");
			} else {
				flash.success("校验交易密码成功");
				
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				MySecurityCtrl.toBindCards1Pre(remarkParams.get("businessSeqNo") , mobilePhone);
			}
			break;
		}
		case ANDROID:{
			if (flag.equals("2")) {
				result.code = -1;
				result.msg = "校验交易密码失败";
			} else {
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				result.code = 1;
				result.msg = "校验交易密码成功";
			}
			redirectApp(result);
			break;
		}
		case IOS:{
			if (flag.equals("2")) {
				result.code = -1;
				result.msg = "校验交易密码失败";
			} else {
				//修改请求参数表中的状态
				paymentRequestService.updateReqStatusToSuccessByBusinessSeqNo(cbParams.get("businessSeqNo"));
				
				result.code = 1;
				result.msg = "校验交易密码成功";
			}
			redirectApp(result);
			break;
		}
		case WECHAT:{
			
			break;
		}
	
		default:
			break;
		}
		
	}
	
	/**
	 * APP 投标验密回调
	 * 
	 * 
	 * @author niu
	 * @create 2017.10.13
	 */
	public static void checkPassAI2(long userId,long amount,long bidId,int memberType) {
		
		ResultInfo result = new ResultInfo();
		
		result = callBackService.checkTradePassCB(params, ServiceType.TENDER);
		
		if (result.code < 0) {
			redirectApp(result);
		}
		
		Map<String, String> reqParams = result.msgs;
		
		result = InvestAction.invest(reqParams);
		
		ResultInfo error = new ResultInfo();
		
		t_cps_activity cpsActivityGoing = null;
		t_cps_activity cpsActivity = null;
		t_cps_user cpsUser = cpsUserDao.findByUserId(userId);
		if (cpsUser != null) {
			cpsActivity = cpsActivityService.queryCpsActivity(cpsUser.activity_id);
			if (cpsActivity != null){
				cpsActivityGoing = cpsActivityService.queryGoingCpsActivityById(cpsActivity.id, 1);
			}
		}
		t_bid bid = t_bid.findById(bidId);
		t_user user = t_user.findById(userId);
		if (memberType == 0) {
			RewardGrantUtils.newCustomerFirst(cpsActivityGoing, user, bid, amount ,error);
		} 
		if (memberType > 0) {
			RewardGrantUtils.newCustomerNotFirst(cpsActivity, user, bid, amount, error);
		}
		
		redirectApp(result);
	}
	
	
	
	
	
	
	
	
	

}
