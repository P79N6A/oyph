package yb;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import net.sf.json.JSONArray;

import com.google.gson.JsonArray;
import com.shove.Convert;

import services.core.InvestService;
import common.FeeCalculateUtil;
import common.constants.ConfConst;
import common.constants.RemarkPramKey;
import common.constants.SettingKey;
import common.enums.Client;
import yb.enums.EntrustFlag;
import yb.enums.PayType;
import yb.enums.ProjectStatus;
import yb.enums.ServiceType;
import common.utils.DateUtil;
import common.utils.EncryptUtil;
import common.utils.Factory;
import common.utils.HttpUtil;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import controllers.front.account.MyAccountCtrl;
import controllers.front.account.MySecurityCtrl;
import controllers.payment.yb.YbPaymentCallBackCtrl;
import controllers.payment.yb.YbPaymentRequestCtrl;
import daos.core.AutoInvestSettingDao;
import models.common.entity.t_bank_card_user;
import models.common.entity.t_conversion_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.entity.t_auto_invest_setting;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import net.sf.json.JSONObject;
import payment.IPayment;
import payment.impl.PaymentProxy;
import play.Logger;
import services.common.BankCardUserService;
import services.common.SettingService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.DebtService;
import services.core.InvestService;
import yb.enums.TradeType;
import yb.enums.YbPayType;

/**
 * 接口实现类
 * 
 * @author niu
 * @create 2017.08.23
 */
public class YbPayment implements IPayment {
	
	private static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	private static YbPaymentService ybPaymentService = Factory.getSimpleService(YbPaymentService.class);
	private static BillService billService = Factory.getService(BillService.class);
	private static BidService bidService = Factory.getService(BidService.class);
	private static UserFundService userFundService = Factory.getService(UserFundService.class);
	private static InvestService investService = Factory.getService(InvestService.class);
	private static DebtService debtService = Factory.getService(DebtService.class);
	protected AutoInvestSettingDao autoInvestSettingDao = Factory.getDao(AutoInvestSettingDao.class);

	private static YbPaymentCallBackService callBackService = Factory.getSimpleService(YbPaymentCallBackService.class);
	
	private static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);
	protected static UserService userService = Factory.getService(UserService.class);
	private static YbPaymentCallBackService ybPaymentCallBackService = Factory.getSimpleService(YbPaymentCallBackService.class);
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	/**
	 * 个人开户
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月18日
	 */
	@Override
	public ResultInfo personCustomerRegist(int client, String businessSeqNo, long userId, String username, String certNo,
			String startTime, String endTime,String jobType,String job, String national,
			String postcode, String address, Object... objects) {
		
		ResultInfo result = new ResultInfo();
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//交易订单号
		String orderNo = "";  //开户无需订单号
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.personCustomerRegist(businessSeqNo, userInfo.mobile, userId, requestMark, certNo, username, 
				startTime, endTime, jobType, job, national, postcode, address);
			
		//打印，保存请求参数
		ybPaymentService.printRequestParams(requestMark, userId, businessSeqNo, orderNo, reqParams, ServiceType.PERSON_CUSTOMER_REGIST, TradeType.CUSTOMER_INFO_SYNC);
	
		//提交获得响应体
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		
		if (response == null) {
			result.code = -1;
			result.msg = "未获得响应体";
			
			return result;
		}

		 //app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = response;
			return result;
		}
		
		YbPaymentCallBackCtrl.getInstance().userRegisterSyn(response, username, certNo, userId,businessSeqNo);
	
		
		return result;
	}
	
	/**
	 * 企业开户
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月18日
	 */
	@Override
	public ResultInfo enterpriseCustomerRegist(int client, String businessSeqNo, long userId, String companyName,
			String uniSocCreCode, String entType, String establish, String companyId,
			String licenseAddress, String bankNo, String bankName, String userName,String idCard ,Object... objects) {

		ResultInfo result = new ResultInfo();
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//交易订单号
		String orderNo = "";  //开户无需订单号
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.companyCustomerRegist(businessSeqNo, userInfo.mobile, userId, requestMark,
				companyName, uniSocCreCode, entType, establish, companyId, licenseAddress, bankNo, bankName,userName, idCard);
		
		//打印，保存请求参数
		ybPaymentService.printRequestParams(requestMark, userId, businessSeqNo, orderNo, reqParams, ServiceType.ENTERPRISE_CUSTOMER_REGIST, TradeType.CUSTOMER_INFO_SYNC);
			
		//提交获得响应体
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		
		if (response == null) {
			result.code = -1;
			result.msg = "未获得响应体";
			
			return result;
		}

		YbPaymentCallBackCtrl.getInstance().companyRegisterSyn(response,userName,companyName, uniSocCreCode, userId, businessSeqNo,companyId,idCard);
	
		return result;
	}
	
	/**
	 * 充值校验交易密码
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月18日
	 */
	public ResultInfo rechargeCheckPassword(int client, String businessSeqNo, long userId, double rechargeAmt,String bankNo, Object... objects){
		
		ResultInfo result = new ResultInfo();
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		HashMap<String, String> reqParams = ybPaymentService.checkPassword(userId, businessSeqNo,ServiceType.CUSTOMER_RECHARGE,rechargeAmt);
		
		//生成表单
		String html = "";
		if (Client.isAppEnum(client)) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5_STANDARD_URL + "checkPassword.html");
		} else {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5URL+"checkPassword.html");
		}
		
		Map<String, String> remarkParams = new HashMap<String, String>();
		//备注参数（全部以“r_”开头）
		remarkParams.put(RemarkPramKey.USER_ID, userId+"");
		remarkParams.put(RemarkPramKey.CLIENT, client+"");
		remarkParams.put(RemarkPramKey.RECHARGE_AMT, rechargeAmt+"");
		remarkParams.put(RemarkPramKey.Bank_No, bankNo+"");
		remarkParams.put(RemarkPramKey.SERVICE_ORDER_NO,businessSeqNo+"");
		
		
		ybPaymentService.printRequestData(requestMark, userId, businessSeqNo, null, ServiceType.FORGET_PASSWORD, TradeType.CUSTOMER_INFO_SYNC, reqParams,remarkParams);
	    
		//app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	
	
		
	/**客户充值
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月18日
	 */
	@Override
	public ResultInfo recharge(int client, String businessSeqNo, long userId, double rechargeAmt,String bankNo, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userInfo = userFundService.queryUserFundByUserId(userId);   
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		
		String paymentAccount = userInfo.payment_account;

		//订单流水号
		String orderNo = businessSeqNo;  //业务单一，直接使用业务订单号
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//接口参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.customerRecharge(businessSeqNo,requestMark, orderNo,PayType.BANK_PAYMENT.code, bankNo,paymentAccount, rechargeAmt);
		
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));  
		
		Map<String, String> remarkParams = new HashMap<String, String>();
		//备注参数（全部以“r_”开头）
		remarkParams.put(RemarkPramKey.MARK,requestMark+"");
		
		//打印，保存请求参数
		ybPaymentService.printRequestDataS(requestMark, userId, businessSeqNo, orderNo, ServiceType.CUSTOMER_RECHARGE, TradeType.CUSTOMER_RECHARGE_WITHDRAW, reqParams,remarkParams);
	    					
		YbPaymentCallBackCtrl.getInstance().rechargeUserSyn(response, businessSeqNo, client);
		
		
		return result;
	}
	
	
	/**
	 * 提现校验交易密码
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月20日
	 */
	public ResultInfo withdrawalCheckPassword(int client, String businessSeqNo, long userId, double withdrawalAmt,String bankAccount,
			ServiceType serviceType, double withdrawalFee, Object... objects){
		
		ResultInfo result = new ResultInfo();
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		HashMap<String, String> reqParams = ybPaymentService.checkPassword(userId, businessSeqNo, serviceType, withdrawalAmt);
		
		//生成表单
		String html = "";
		if (Client.isAppEnum(client)) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5_STANDARD_URL + "checkPassword.html");
		} else {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5URL+"checkPassword.html");
		}
		Map<String, String> remarkParams = new HashMap<String, String>();
		
		//备注参数（全部以“r_”开头）
		remarkParams.put(RemarkPramKey.USER_ID, userId+"");
		remarkParams.put(RemarkPramKey.CLIENT, client+"");
		remarkParams.put(RemarkPramKey.WITHDRAWAL_AMT, withdrawalAmt+"");
		remarkParams.put(RemarkPramKey.Bank_No, bankAccount+"");
		remarkParams.put(RemarkPramKey.SERVICE_ORDER_NO,businessSeqNo+"");
		remarkParams.put(RemarkPramKey.WITHDRAWAL_FEE,withdrawalFee+"");
		
		ybPaymentService.printRequestData(requestMark, userId, businessSeqNo, null, ServiceType.FORGET_PASSWORD, TradeType.CUSTOMER_INFO_SYNC, reqParams,remarkParams);
	    
		//app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	
	
	/**
	 * 客户提现
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月20日
	 */
	@Override
	public ResultInfo withdrawal(int client, String businessSeqNo, long userId, double withdrawalAmt, String bankAccount,double withdrawalFee, Object... obj) {
		
		ResultInfo result = new ResultInfo();
		
		t_user_fund userInfo = userFundService.queryUserFundByUserId(userId);   
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		String paymentAccount = userInfo.payment_account;

		//订单流水号
		String orderNo = businessSeqNo;  //业务单一，直接使用业务订单号
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//接口参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.customerWithdraw(businessSeqNo, requestMark, orderNo, PayType.BANK_PAYMENT, bankAccount,paymentAccount, withdrawalAmt,withdrawalFee);
		
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		

		Map<String, String> remarkParams = new HashMap<String, String>();
		//备注参数（全部以“r_”开头）
		remarkParams.put(RemarkPramKey.MARK,requestMark+"");
		
		//打印，保存请求参数
		ybPaymentService.printRequestDataS(requestMark, userId, businessSeqNo, orderNo, ServiceType.CUSTOMER_WITHDRAW, TradeType.CUSTOMER_RECHARGE_WITHDRAW, reqParams,remarkParams);
		
		YbPaymentCallBackCtrl.getInstance().withdrawalUserSyn(response,businessSeqNo,client);
		
		return result;
		
	}
	
	/**
	 * 企业提现
	 * 
	 * @author LiuPengwei
	 * @create 2017年10月18日
	 */
	@Override
	public ResultInfo enterpriseWithdrawal(int client, String businessSeqNo, long userId, double withdrawalAmt, String bankAccount,double withdrawalFee, Object... obj) {
		
		ResultInfo result = new ResultInfo();
		
		t_user_fund userInfo = userFundService.queryUserFundByUserId(userId);   
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		String paymentAccount = userInfo.payment_account;

		//订单流水号
		String orderNo = businessSeqNo;  //业务单一，直接使用业务订单号
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
	
		//接口参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.enterpriseWithdraw(businessSeqNo, requestMark, orderNo, PayType.BANK_PAYMENT, bankAccount,paymentAccount, withdrawalAmt,withdrawalFee);
		
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		
		Map<String, String> remarkParams = new HashMap<String, String>();
		
		//备注参数（全部以“r_”开头）
		remarkParams.put(RemarkPramKey.MARK,requestMark+"");
		
		//打印，保存请求参数
		ybPaymentService.printRequestDataS(requestMark, userId, businessSeqNo, orderNo, ServiceType.ENTERPRISE_WITHDRAW, TradeType.CUSTOMER_RECHARGE_WITHDRAW, reqParams,remarkParams);
		
		YbPaymentCallBackCtrl.getInstance().withdrawalUsersSyn(response,businessSeqNo);
		
		return result;
		
	}
	
	
	/**
	 * 正常放款
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月21日
	 */
	
	@Override
	public ResultInfo bidSuditSucc(int client, String serviceOrderNo, long releaseSupervisorId,  t_bid bid, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		if (bid == null) {
			throw new RuntimeException("bid is null");
		}
		
		List<t_invest> invests = investService.queryBidInvest(bid.id);
		if (invests == null || invests.size() == 0) {
			result.code = -1;
			result.msg = "查询投资列表失败";

			return result;
		}
		
		//借款人第三方唯一标示
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bid.user_id);
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "查询借款人资金信息失败";
			
			return result;
		}

		t_user_fund investUserFund = null;
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		double loanServiceFee = 0;
		double investorFee = 0;
		
		for (t_invest invest : invests) {
			investUserFund = userFundService.queryUserFundByUserId(invest.user_id);   
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "查询投资人资金信息失败";
				
				return result;
			}
			//投资人投资金额
			double amount = invest.amount;
			//计算总投资金额
			investorFee += amount;
			
			//手续费
			double fee = invest.loan_service_fee_divide;
			//计算借款服务费
			loanServiceFee += fee;
			
			
		}
				
		//借款服务费纠偏
		bidService.updateLoanServiceFee(bid.id, loanServiceFee);
		
		String debitAccountNo = bid.object_acc_no ;//借方台账号(标的台账账号)
		String financierAccountNo = bid.user_id+"";//贷方台账号（融资方台账账号）
		String bidNo = bid.mer_bid_no;//标的编号
		double financierFee = investorFee - loanServiceFee;//融资人
		//参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.bidAuditSucc(serviceOrderNo, requestMark, debitAccountNo, financierAccountNo, bidNo, loanServiceFee, financierFee);
		
		//打印，保存请求
		ybPaymentService.printRequestParams(requestMark, bid.user_id, serviceOrderNo, serviceOrderNo, reqParams, ServiceType.LOAN, TradeType.FUND_TRADE);
		
		//请求第三方
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));  
		
		//转换为map
		TreeMap<String, String> responseMap = YbUtils.getRespHeader(response, ServiceType.LOAN, null);
		
		if (!responseMap.get("respCode").equals("P2P0000")){
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("验签失败！");
			}
			
			result.code = -1;
			result.msg = "放款失败";
			
			return result;
		}
		
		// 业务流水号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_MODIFY);
		
		/** 生成借款账单 */
		bidService.createBidBill(bid,result);
		
		result = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), businessSeqNo, bid, ServiceType.BID_MODIFY, ProjectStatus.LOAN, billService.findReturnLists(bid.id));
		//result = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), businessSeqNo, bid, ServiceType.BID_MODIFY, ProjectStatus.LOAN, billService.findReturnList());
		if (result.code < 1) {
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("验签失败！");
			}
			result.code = -1;
			result.msg = "放款失败";
			return result;
		}
				
		
		result = bidService.doRelease(bid.id, releaseSupervisorId, serviceOrderNo);
		
		if (result.code < 1) {
			
			return result;
		}
		
		result.code = 1;
		result.msg = "放款成功";
		
		return result;
	}
	
	/**
	 * 债权转让
	 * 
	 * @author LiuPengwei
	 * @create 2017年10月13日
	 */
	@Override
	public ResultInfo debtTransfer(int client, String serviceOrderNo, Long debtId, Long userId) {
		ResultInfo result = debtService.findTransferInfo(debtId, userId);
		if (result.code < 1) {

			return result;
		}
		
		Map<String, Object> map = (Map<String, Object>) result.obj;
		t_bid bid = (t_bid) map.get("bid");
		
		t_user_fund sellCustUser = userFundService.queryUserFundByUserId(Long.valueOf(map.get("fromUserId")+""));
		t_user_fund buyCustUser = userFundService.queryUserFundByUserId(userId);
		
		String sellCustId = sellCustUser.payment_account;//转让者资金托管账号
		double creditDealAmt = Convert.strToDouble(map.get("pPayAmt").toString(), 0.00);//成交金额
		double fee = Convert.strToDouble(map.get("managefee").toString(), 0.00) ;//转让方手续费
		String borrowerCustId = buyCustUser.payment_account;//受让者资金托管账号
		String bidNo = bid.mer_bid_no;  //借款标编号
		
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
	
		//参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.debtTransfer(serviceOrderNo, requestMark, borrowerCustId, sellCustId, bidNo, fee, creditDealAmt);
		
		Map<String, String> remarkParams = new HashMap<String, String>();
		

		//备注参数（全部以“r_”开头）
		remarkParams.put(RemarkPramKey.BOND_FEE, fee+"");
		
		//打印，保存请求
		ybPaymentService.printRequestDataS(requestMark, userId, serviceOrderNo, null, ServiceType.FARM_IN, TradeType.FUND_TRADE, reqParams,remarkParams);
		
		//请求第三方
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));  
		
		 //app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = response;
			return result;
		}
		
		
		YbPaymentCallBackCtrl.getInstance().debtTransferSyn(response, serviceOrderNo, debtId, userId, fee,client);
		
		result.code = 1;
		result.msg ="申请通过，等待审核人员进行处理";
		return result;
	}
	
	

	/**
	 * 设置交易密码
	 * 
	 * @author liu
	 * @create 2017.09.07
	 */
	public ResultInfo setUserPassword(int client, String businessSeqNo, long userId, Object... objects){
		
		ResultInfo result = new ResultInfo();
		
		//获取用户基本信息
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数拼接
		HashMap<String, String> reqParams = ybPaymentService.setUserPassword(userId, businessSeqNo);
		
		if(reqParams == null) {
			result.code = -1;
			result.msg = "回调地址格式有误";
			
			return result;
		}
		
		//生成表单
		String html = null;
		if(client == 1) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5URL+"enterPassword.html");
		}else if(client == 2) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5_STANDARD_URL+"enterPassword.html");
		}else if(client == 3) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5_STANDARD_URL+"enterPassword.html");
		}
		
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		//设置交易密码请求参数的保存
		ybPaymentService.printRequestData(requestMark, userId, businessSeqNo, null, ServiceType.SET_USER_PASSWORD, TradeType.CUSTOMER_INFO_SYNC, reqParams,reqParams);
	    
		//app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	/**
	 * 修改交易密码
	 * 
	 * @author liu
	 * @create 2017.09.07
	 */
	public ResultInfo amendUserPassword(int client, String businessSeqNo, long userId, Object... objects){
		
		ResultInfo result = new ResultInfo();
		
		//获取用户基本信息
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数拼接
		HashMap<String, String> reqParams = ybPaymentService.amendUserPassword(userId, businessSeqNo);
		
		//生成表单
		String html = null;
		if(client == 1) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5URL+"changePassword.html");
		}else if(client == 2 || client == 3) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5_STANDARD_URL+"changePassword.html");
		}
		
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		//修改交易密码请求参数的打印和保存
		ybPaymentService.printRequestData(requestMark, userId, businessSeqNo, null, ServiceType.AMEND_USER_PASSWORD, TradeType.CUSTOMER_INFO_SYNC, reqParams,reqParams);
	    
		//app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	/**
	 * 重置交易密码
	 * 
	 * @author liu
	 * @create 2017.09.07
	 */
	public ResultInfo resetUserPassword(int client, String businessSeqNo, long userId, Object... objects){
		
		ResultInfo result = new ResultInfo();
		
		//获取用户基本信息
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数的群拼接
		HashMap<String, String> reqParams = ybPaymentService.resetUserPassword(userId, businessSeqNo);
		
		//生成表单
		String html = null;
		if(client == 1) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5URL+"forgetPassword.html");
		}else if(client == 2 || client == 3) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5_STANDARD_URL+"forgetPassword.html");
		}
		
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		//重置交易密码的请求参数的打印和保存
		ybPaymentService.printRequestData(requestMark, userId, businessSeqNo, null, ServiceType.RESET_USER_PASSWORD, TradeType.CUSTOMER_INFO_SYNC, reqParams,reqParams);
	    
		//app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	/**
	 * 校验交易密码
	 * 
	 * @author liu
	 * @create 2017.09.13
	 */
	public ResultInfo checkPassword(int client, String businessSeqNo, long userId, Object... objects){
		
		ResultInfo result = new ResultInfo();
		
		//获取基本信息
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数的拼接
		HashMap<String, String> reqParams = ybPaymentService.checkPassword(userId, businessSeqNo,ServiceType.CUSTOMER_BIND_CARD,0);
		
		//生成表单
		String html = null;
		if(client == 1) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5URL+"checkPassword.html");
		}else if(client == 2 || client == 3) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5_STANDARD_URL+"checkPassword.html");
		}
		
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		//校验交易密码的请求参数的打印和保存
		ybPaymentService.printRequestData(requestMark, userId, businessSeqNo, null, ServiceType.FORGET_PASSWORD, TradeType.CUSTOMER_INFO_SYNC, reqParams,reqParams);
	    
		//app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	/**
	 * 校验交易密码
	 * 
	 * @author liu
	 * @create 2018.1.3
	 */
	public ResultInfo bindCardCheckPassword(int client, String businessSeqNo, long userId){

		ResultInfo result = new ResultInfo();
		
		//获取基本信息
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数的拼接
		HashMap<String, String> reqParams = ybPaymentService.bindCardCheckPassword(userId, businessSeqNo, ServiceType.CUSTOMER_BIND_CARD);
		
		//生成表单
		String html = null;
		if(client == 1) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5URL+"checkPassword.html");
		}else if(client == 2 || client == 3) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5_STANDARD_URL+"checkPassword.html");
		}
		
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		//校验交易密码的请求参数的打印和保存
		ybPaymentService.printRequestData(requestMark, userId, businessSeqNo, null, ServiceType.FORGET_PASSWORD, TradeType.CUSTOMER_INFO_SYNC, reqParams,reqParams);
	    
		//app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	/**
	 * 校验交易密码(担保人还款)
	 * 
	 * @author liuyang
	 * @create 2018年1月3日
	 */
	public ResultInfo guaranteeCheckPassword(int client, String businessSeqNo, long userId, String userName, double amount, String backUrl, String billSign){
		
		ResultInfo result = new ResultInfo();
		
		//获取基本信息
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数的拼接
		HashMap<String, String> reqParams = ybPaymentService.guaranteeCheckPassword(userId, businessSeqNo, ServiceType.CUSTOMER_BIND_CARD, userName, amount, backUrl);
		
		//生成表单
		String html  = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5URL+"checkPassword.html");
		
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		reqParams.put(RemarkPramKey.RECHARGE_AMT, amount+"");
		reqParams.put(RemarkPramKey.BILL_SIGN, billSign);
		
		//校验交易密码的请求参数的打印和保存
		ybPaymentService.printRequestData(requestMark, userId, businessSeqNo, null, ServiceType.FORGET_PASSWORD, TradeType.CUSTOMER_INFO_SYNC, reqParams,reqParams);
	    
		// 表单提交
		YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	

	
	
	
	@Override
	public String getInterfaceDes(int code) {
		TradeType ybp = TradeType.getEnum(code);
		
		return ybp.value;
	}

	@Override
	public int getInterfaceType(Enum interfaceType) {
		TradeType hpt = (TradeType) interfaceType;
		
		return hpt.code;
	}



	/**
	 * 用户绑卡
	 * 
	 * @author liu
	 * @create 2017.09.13
	 */
	public ResultInfo userBindCard(int client, String serviceOrderNo, long userId, String name, String idNumber, String mobile, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户资金信息失败";
			
			return result;
		}
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		HashMap<String, String> reqParams = ybPaymentService.userBindCard(userId, serviceOrderNo, name, idNumber, mobile);
		
		//生成表单
		String html = null;
		if(client == 1) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5URL+"cardBind2.html");
		}else if(client == 2 || client == 3) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5_STANDARD_URL+"cardBind2.html");
		}
		
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		ybPaymentService.printRequestData(requestMark, userId, serviceOrderNo, null, ServiceType.CUSTOMER_BIND_CARD, TradeType.CUSTOMER_INFO_SYNC, reqParams,reqParams);
	    
		//app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
		
	}


	/**
	 * 查询用户绑卡信息
	 * 
	 * @author liu
	 * @create 2017.09.10
	 */
	public ResultInfo queryBindedBankCard(int client, long userId, String serviceOrderNo,int serviceType, Object... obj) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户资金信息失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String clientSn = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.queryBindedBankCard(userId, clientSn);
	
		//提交获得响应体
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		
		if(response == null) {
			result.code = -1;
			result.msg = "未获得响应体";
			
			return result;
		}
		
		if(client == 1) {
			
			//验签，解析参数
			TreeMap<String, String> map2 = YbUtils.getRespHeader(response, ServiceType.QUERY_BINDED_BANK,"cardList");
			
			//判断参数是否为空
			if(map2 == null || map2.isEmpty() || map2.size()<=0) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info("查询用户绑定的银行卡失败");
				}
				
				MySecurityCtrl.securityPre();
			} 
			
			switch (serviceType) {
			case 1:	{
					if (!map2.get("accountNo").equals("")){
						//用户台账号添加
						userFundService.updateUserStatus(userId,map2.get("accountNo"));
						//刷新用户缓存信息
						userService.flushUserCache(userId);
					}else {
						
						userFundService.updateUserStatus(userId,"");
						UserInfoService.updateUser(userId);
					}
					MyAccountCtrl.homePre();
					break;
				}
			case 20:{	
					//数据库中存的银行卡
					List<t_bank_card_user> cardList = bankCardUserService.queryCardByUserId(userId);
					
					if(cardList.size()>0) {
						//删除数据库中存的银行卡列表
						for (int i = 0; i < cardList.size(); i++) {
							cardList.get(i).delete();
						}
					}
					
					//循环保存银行返回参数中的银行卡信息
					for (int i = 0; i < 50; i++) {
						String cardNo = map2.get("tiedAccno"+i);
						if(cardNo != null) {
							ybPaymentCallBackService.userBindCard(userId+"", map2.get("tiedAccno"+i));
						}else{
							break;
						}

					}
					
					MySecurityCtrl.securityPre();
				}
			}
			
		}else {
					
			//验签，解析参数
			TreeMap<String, String> map2 = YbUtils.getRespHeader(response, ServiceType.QUERY_BINDED_BANK,"cardList");
			
			//判断参数是否为空
			if(map2 == null || map2.isEmpty()) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info("查询用户绑定的银行卡失败");
				}
				result.code = -1;
				result.msg = "查询用户绑定的银行卡失败";
				return result;
			} 
			
			//数据库中存的银行卡
			List<t_bank_card_user> cardList = bankCardUserService.queryCardByUserId(userId);
			
			if(cardList.size()>0) {
				//删除数据库中存的银行卡列表
				for (int i = 0; i < cardList.size(); i++) {
					cardList.get(i).delete();
				}
			}
			
			//循环保存银行返回参数中的银行卡信息
			for (int i = 0; i < 50; i++) {
				String cardNo = map2.get("tiedAccno"+i);
				if(cardNo != null) {
					ybPaymentCallBackService.userBindCard(userId+"", map2.get("tiedAccno"+i));
				}else{
					break;
				}
			}
			
			result.code = 1;
			result.msg = "";
			return result;
		}
		
		result.code = 1;
		return result;
	}
	
	/**
	 * 托管账户资金信息查询
	 * 
	 * @author liu
	 * @create 2017.09.14
	 */
	public ResultInfo queryFundInfo(int client, String account, String serviceOrderNo) {
		
		ResultInfo result = new ResultInfo();
		
		t_user_fund userFund = userFundService.findByColumn("payment_account=?", account);
		
		if(userFund == null) {
			result.code = -1;
			result.msg = "获取用户台账信息失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String clientSn = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.queryFundInfo(userFund.user_id, clientSn);
		
		//打印，保存请求参数
		//ybPaymentService.printRequestParams(clientSn, userFund.user_id, serviceOrderNo, orderNo, reqParams, ServiceType.QUERY_MESSAGE, TradeType.CUSTOMER_INFO_QUERY);
		
		//提交获得响应体
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		
		//打印，保存响应参数
		//ybPaymentService.printResponseParams("查询绑卡信息", clientSn, response, ServiceType.QUERY_MESSAGE, TradeType.CUSTOMER_INFO_QUERY);
		
		if(response == null) {
			result.code = -1;
			result.msg = "未获得响应体";
			
			return result;
		} 
		
		//验签，解析参数
		TreeMap<String, String> map2 = YbUtils.getRespHeader(response, ServiceType.QUERY_MESSAGE, "cardList");
		
		if(map2 == null || map2.isEmpty()) {
			result.code = -1;
			result.msg = "获取用户台账信息失败";
			
			return result;
		}
		
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("pBlance", map2.get("availableamount").replace(",", ""));  //用户可用余额
	
		result.code = 1;
		result.obj = maps;
		
		return result;
	}
	
	/**
	 * 代偿账户资金信息查询
	 *
	 * @param client 客户端
	 * @param businessSeqNo 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	public ResultInfo queryCompensatoryBalance(int client, String businessSeqNo){
		
		ResultInfo result = new ResultInfo();
		
		//托管请求唯一标识
		String clientSn = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.queryCompensatoryBalance(clientSn);
		
		//打印，保存请求参数
		//ybPaymentService.printRequestParams(clientSn, 0, businessSeqNo, orderNo, reqParams, ServiceType.QUERY_MESSAGE, TradeType.CUSTOMER_INFO_QUERY);
		
		//提交获得响应体
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		
		//打印，保存响应参数
		//ybPaymentService.printResponseParams("查询代偿账户资金信息", clientSn, response, ServiceType.QUERY_MESSAGE, TradeType.CUSTOMER_INFO_QUERY);
		
		if(response == null) {
			result.code = -1;
			result.msg = "未获得响应体";
			
			return result;
		} 
		
		//验签，解析参数
		TreeMap<String, String> map2 = YbUtils.getRespHeader(response, ServiceType.QUERY_MESSAGE, "cardList");
		
		if(map2 == null || map2.isEmpty()) {
			result.code = -1;
			result.msg = "获取用户台账信息失败";
			
			return result;
		}
		
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("pBlance", map2.get("availableamount").replace(",", ""));  //用户可用余额
	
		result.code = 1;
		result.obj = maps;
		
		return result;
	}
	
	/**
	 * 担保账户资金信息查询
	 *
	 * @param client 客户端
	 * @param businessSeqNo 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	public ResultInfo queryGuaranteeBalance(int client, String businessSeqNo, long paymentAccount){
		
		ResultInfo result = new ResultInfo();
		
		//托管请求唯一标识
		String clientSn = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.queryGuaranteeBalance(clientSn, paymentAccount);
		
		//打印，保存请求参数
		//ybPaymentService.printRequestParams(clientSn, 0, businessSeqNo, orderNo, reqParams, ServiceType.QUERY_MESSAGE, TradeType.CUSTOMER_INFO_QUERY);
		
		//提交获得响应体
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		
		//打印，保存响应参数
		//ybPaymentService.printResponseParams("查询代偿账户资金信息", clientSn, response, ServiceType.QUERY_MESSAGE, TradeType.CUSTOMER_INFO_QUERY);
		
		if(response == null) {
			result.code = -1;
			result.msg = "未获得响应体";
			
			return result;
		} 
		
		//验签，解析参数
		TreeMap<String, String> map2 = YbUtils.getRespHeader(response, ServiceType.QUERY_MESSAGE, "cardList");
		
		if(map2 == null || map2.isEmpty()) {
			result.code = -1;
			result.msg = "获取用户台账信息失败";
			
			return result;
		}
		
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("pBlance", map2.get("availableamount").replace(",", ""));  //用户可用余额
	
		result.code = 1;
		result.obj = maps;
		
		return result;
	}
	
	/**
	 * 营销账户资金信息查询
	 *
	 * @param client 客户端
	 * @param businessSeqNo 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	public ResultInfo queryMarketBalance(int client, String businessSeqNo){

		ResultInfo result = new ResultInfo();
		
		//托管请求唯一标识
		String clientSn = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.queryMarketBalance(clientSn);
		
		//打印，保存请求参数
		//ybPaymentService.printRequestParams(clientSn, 0, businessSeqNo, orderNo, reqParams, ServiceType.QUERY_MESSAGE, TradeType.CUSTOMER_INFO_QUERY);
		
		//提交获得响应体
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		
		//打印，保存响应参数
		//ybPaymentService.printResponseParams("查询营销账户资金信息", clientSn, response, ServiceType.QUERY_MESSAGE, TradeType.CUSTOMER_INFO_QUERY);
		
		if(response == null) {
			result.code = -1;
			result.msg = "未获得响应体";
			
			return result;
		} 
		
		//验签，解析参数
		TreeMap<String, String> map2 = YbUtils.getRespHeader(response, ServiceType.QUERY_MESSAGE, "cardList");
		
		if(map2 == null || map2.isEmpty()) {
			result.code = -1;
			result.msg = "获取用户台账信息失败";
			
			return result;
		}
		
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("pBlance", map2.get("availableamount").replace(",", ""));  //用户可用余额
	
		result.code = 1;
		result.obj = maps;
		
		return result;
	}
	
	/**
	 * 费用账户资金信息查询
	 *
	 * @param client 客户端
	 * @param businessSeqNo 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	public ResultInfo queryCostBalance(int client, String businessSeqNo){

		ResultInfo result = new ResultInfo();
		
		//托管请求唯一标识
		String clientSn = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.queryCostBalance(clientSn);
		
		//打印，保存请求参数
		//ybPaymentService.printRequestParams(clientSn, 0, businessSeqNo, orderNo, reqParams, ServiceType.QUERY_MESSAGE, TradeType.CUSTOMER_INFO_QUERY);
		
		//提交获得响应体
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		
		//打印，保存响应参数
		//ybPaymentService.printResponseParams("查询费用账户资金信息", clientSn, response, ServiceType.QUERY_MESSAGE, TradeType.CUSTOMER_INFO_QUERY);
		
		if(response == null) {
			result.code = -1;
			result.msg = "未获得响应体";
			
			return result;
		} 
		
		//验签，解析参数
		TreeMap<String, String> map2 = YbUtils.getRespHeader(response, ServiceType.QUERY_MESSAGE, "cardList");
		
		if(map2 == null || map2.isEmpty()) {
			result.code = -1;
			result.msg = "获取用户台账信息失败";
			
			return result;
		}
		
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("pBlance", map2.get("availableamount").replace(",", ""));  //用户可用余额
	
		result.code = 1;
		result.obj = maps;
		
		return result;
	}
	
	/**
	 * 台账平台充值
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 
	 * @param rechargeAmt 
	 * @param flag 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	public ResultInfo merchantRecharge(int client, String serviceOrderNo, double rechargeAmt, int flag){
		
		ResultInfo result = new ResultInfo();
		
		//订单流水号
		String orderNo = serviceOrderNo;  //业务单一，直接使用业务订单号
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//根据flag判断接口参数拼装
		if(flag == 1) {
			HashMap<String, JSONObject> reqParams = ybPaymentService.compensatoryRecharge(serviceOrderNo, requestMark, orderNo, PayType.BANK_PAYMENT.code, rechargeAmt);
			
			String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));  
						
			//打印，保存请求参数
			ybPaymentService.printRequestParams(requestMark, 0, serviceOrderNo, orderNo, reqParams, ServiceType.COMPENSATORY_RECHARGE, TradeType.CUSTOMER_RECHARGE_WITHDRAW);
			
			YbPaymentCallBackCtrl.getInstance().compensatoryRechargeSyn(response);
			
		}else if(flag == 2) {
			HashMap<String, JSONObject> reqParams = ybPaymentService.marketRecharge(serviceOrderNo,requestMark, orderNo,PayType.BANK_PAYMENT.code, rechargeAmt);
			
			String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));  
						
			//打印，保存请求参数
			ybPaymentService.printRequestParams(requestMark, 0, serviceOrderNo, orderNo, reqParams, ServiceType.MARKET_RECHARGE, TradeType.CUSTOMER_RECHARGE_WITHDRAW);
			
			YbPaymentCallBackCtrl.getInstance().marketRechargeSyn(response);
		}else {
			
			HashMap<String, JSONObject> reqParams = ybPaymentService.costRecharge(serviceOrderNo, requestMark, orderNo, PayType.BANK_PAYMENT.code, rechargeAmt);
			
			String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));  
				
			//打印，保存请求参数
			ybPaymentService.printRequestParams(requestMark, 0, serviceOrderNo, orderNo, reqParams, ServiceType.COST_RECHARGE, TradeType.CUSTOMER_RECHARGE_WITHDRAW);
			
			YbPaymentCallBackCtrl.getInstance().costRechargeSyn(response);
		}
		
		return result;
	}
	
	/**
	 * 台账平台提现
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 
	 * @param rechargeAmt 
	 * @param flag 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月21日
	 */
	public ResultInfo merchantWithdrawal(int client, String serviceOrderNo, double rechargeAmt, int flag) {

		ResultInfo result = new ResultInfo();
		
		//订单流水号
		String orderNo = serviceOrderNo;  //业务单一，直接使用业务订单号
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//根据flag判断接口参数拼装
		if(flag == 1) {
			HashMap<String, JSONObject> reqParams = ybPaymentService.compensatoryWithdrawal(serviceOrderNo, requestMark, orderNo, PayType.BANK_PAYMENT.code, rechargeAmt);
			
			String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));  
						
			//打印，保存请求参数
			ybPaymentService.printRequestParams(requestMark, 0, serviceOrderNo, orderNo, reqParams, ServiceType.COMPENSATORY_WITHDRAW, TradeType.CUSTOMER_RECHARGE_WITHDRAW);
			
			YbPaymentCallBackCtrl.getInstance().compensatoryWithdrawalSyn(response);
			
		}else if(flag == 2) {
			HashMap<String, JSONObject> reqParams = ybPaymentService.marketWithdrawal(serviceOrderNo,requestMark, orderNo,PayType.BANK_PAYMENT.code, rechargeAmt);
			
			String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));  
						
			//打印，保存请求参数
			ybPaymentService.printRequestParams(requestMark, 0, serviceOrderNo, orderNo, reqParams, ServiceType.MARKET_WITHDRAW, TradeType.CUSTOMER_RECHARGE_WITHDRAW);
			
			YbPaymentCallBackCtrl.getInstance().marketWithdrawalSyn(response);
		}else {
			HashMap<String, JSONObject> reqParams = ybPaymentService.costWithdrawal(serviceOrderNo, requestMark, orderNo, PayType.BANK_PAYMENT.code, rechargeAmt);
			
			String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));  
						
			//打印，保存请求参数
			ybPaymentService.printRequestParams(requestMark, 0, serviceOrderNo, orderNo, reqParams, ServiceType.COST_WITHDRAW, TradeType.CUSTOMER_RECHARGE_WITHDRAW);
			
			YbPaymentCallBackCtrl.getInstance().costWithdrawalSyn(response);
		}
		
		return result;
	}


	/**
	 * 后台代偿线下收款
	 * 
	 * @param clientSn 客户端
	 * @param businessSeqNo
	 * @param bid
	 * @param serviceType
	 * @param entrustFlag
	 * @param accountList
	 * @param contractList
	 * @param objects
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2017年10月12日
	 */
	public ResultInfo offlineReceive(String clientSn, String businessSeqNo, t_bid bid, ServiceType serviceType, EntrustFlag entrustFlag, List<Map<String, Object>> accountList, List<Map<String, Object>> contractList, Object... objects) {

		ResultInfo result = new ResultInfo();
		
		//组织请求报文
		HashMap<String, JSONObject> request = ybPaymentService.offlineReceive(clientSn, businessSeqNo, bid, serviceType, entrustFlag, accountList, contractList);
		
		//打印，保存请求报文
		ybPaymentService.printRequestParams(clientSn, bid.user_id, businessSeqNo, "", request, serviceType, TradeType.FUND_TRADE);
		
		//请求第三方，得到响应报文
		String response = HttpUtil.postMethod(JSONObject.fromObject(request));
		
		//打印，保存响应报文
		ybPaymentService.printResponseParams(serviceType.note, clientSn, response, serviceType, TradeType.FUND_TRADE);
		
		//响应报文处理
		TreeMap<String, String> respMap = YbUtils.getRespHeader(response, serviceType ,null);
		
		if (respMap != null) {
			if (respMap.get("respCode").equals(YbConsts.RESP_SUC_CODE)) {
				result.code = 1;
				result.msg = respMap.get("respMsg");
				
				return result;
			}
			if (!respMap.get("respCode").equals(YbConsts.RESP_SUC_CODE)) {
				result.code = -1;
				result.msg = respMap.get("respMsg");
				
				return result;
			}
		}		
		
		result.code = -1;
		result.msg = "托管第三方响应失败";
		
		return result;
	}
	
	/**
	 * 校验交易密码(网银充值)
	 * 
	 * @author liuyang
	 * @create 2017年10月23日
	 */
	public ResultInfo ebankRechargeCheckPassword(int client, String businessSeqNo, long userId, double rechargeAmt, int flags){

		ResultInfo result = new ResultInfo();
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		HashMap<String, String> reqParams = ybPaymentService.ebankRechargeCheckPassword(userId, businessSeqNo,rechargeAmt,flags);
		
		//生成表单
		String html = "";
		if (Client.isAppEnum(client)) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5_STANDARD_URL + "checkPassword.html");
		} else {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5URL+"checkPassword.html");
		}
		
		Map<String, String> remarkParams = new HashMap<String, String>();
		//备注参数（全部以“r_”开头）
		remarkParams.put(RemarkPramKey.USER_ID, userId+"");
		remarkParams.put(RemarkPramKey.CLIENT, client+"");
		remarkParams.put(RemarkPramKey.RECHARGE_AMT, rechargeAmt+"");
		remarkParams.put(RemarkPramKey.SERVICE_ORDER_NO,businessSeqNo+"");
		remarkParams.put(RemarkPramKey.BANK_FLAG, flags+"");
		
		ybPaymentService.printRequestData(requestMark, userId, businessSeqNo, null, ServiceType.FORGET_PASSWORD, TradeType.CUSTOMER_RECHARGE_WITHDRAW, reqParams,remarkParams);
	    
		//app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	/**客户网银充值
	 * 
	 * @author liuyang
	 * @create 2017年10月23日
	 */
	@Override
	public ResultInfo ebankRecharge(int client, String businessSeqNo, long userId, double rechargeAmt) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userInfo = userFundService.queryUserFundByUserId(userId);   
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		String paymentAccount = userInfo.payment_account;

		//订单流水号
		String orderNo = businessSeqNo;  //业务单一，直接使用业务订单号
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//接口参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.customerInternetbankRecharge(businessSeqNo,requestMark, orderNo,PayType.BANK_PAYMENT.code,paymentAccount, rechargeAmt);
		
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));  
					
		Map<String, String> remarkParams = new HashMap<String, String>();
		//备注参数（全部以“r_”开头）
		remarkParams.put(RemarkPramKey.MARK,requestMark+"");
		
		//打印，保存请求参数
		ybPaymentService.printRequestDataS(requestMark, userId, businessSeqNo, orderNo, ServiceType.CUSTOMER_INTERNATBANK_RECHARGE, TradeType.CUSTOMER_RECHARGE_WITHDRAW, reqParams,remarkParams);
		
		YbPaymentCallBackCtrl.getInstance().ebankRechargeUserSyn(response, businessSeqNo, client);
		
		return result;
	}
	
	/**企业网银充值
	 * 
	 * @author liuyang
	 * @create 2017年12月18日
	 */
	@Override
	public ResultInfo enterpriseEbankRecharge(int client, String businessSeqNo, long userId, double rechargeAmt, int flags) {
		ResultInfo result = new ResultInfo();
		
		t_user_fund userInfo = userFundService.queryUserFundByUserId(userId);   
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		String paymentAccount = userInfo.payment_account;

		//订单流水号
		String orderNo = businessSeqNo;  //业务单一，直接使用业务订单号
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//接口参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.enterpriseInternetbankRecharge(businessSeqNo, requestMark, orderNo,PayType.BANK_PAYMENT.code, paymentAccount, rechargeAmt);
		
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));  
					
		Map<String, String> remarkParams = new HashMap<String, String>();
		//备注参数（全部以“r_”开头）
		remarkParams.put(RemarkPramKey.MARK, requestMark+"");
		
		//打印，保存请求参数
		ybPaymentService.printRequestDataS(requestMark, userId, businessSeqNo, orderNo, ServiceType.CUSTOMER_INTERNATBANK_RECHARGE, TradeType.CUSTOMER_RECHARGE_WITHDRAW, reqParams,remarkParams);
		
		YbPaymentCallBackCtrl.getInstance().enterpriseEbankRechargeUserSyn(response, businessSeqNo, client, userId, rechargeAmt, flags);
		
		return result;
	}
	
	@Override
	public ResultInfo regist(int client, String serviceOrderNo, long userId, Object... obj) {
		ResultInfo resultInfo = new ResultInfo();
		resultInfo.code = 1;
		
		return resultInfo;
	}


	@Override
	public ResultInfo invest(int client, int investType, String serviceOrderNo, long userId, t_bid bid,
			double investAmt, Object... obj) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ResultInfo repayment(int client, String serviceOrderNo, t_bill bill,
			List<Map<String, Double>> billInvestFeeList, Object... obj) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ResultInfo advanceRepayment(int client, String serviceOrderNo, t_bill bill, Object... obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultInfo login(int client, long userId, Object... obj) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ResultInfo autoInvest(int client, int investType, String serviceOrderNo, long userId, t_bid bid,
			double investAmt, Object... obj) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public ResultInfo bidCreate(int client, String serviceOrderNo, t_bid bid, int bidCreateFrom, Object... obj) {
		// TODO Auto-generated method stub
		return null;
	}





	@Override
	public ResultInfo queryMerchantBalance(int client, Object... obj) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ResultInfo conversion(int client, String serviceOrderNo, t_conversion_user conversion, Object... obj) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ResultInfo advance(int client, String serviceOrderNo, t_bill bill,
			List<Map<String, Double>> billInvestFeeList, Object... obj) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ResultInfo advanceReceive(int client, String serviceOrderNo, t_bill bill,
			List<Map<String, Double>> billInvestFeeList, Object... obj) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ResultInfo repaymentAll(int client, String serviceOrderNo, t_bill bill,
			List<Map<String, Double>> billInvestFeeList, Object... obj) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 
	* 
	* @Description: TODO(开启委托严密) 
	* @author lihuijun 
	* @date 2017年9月26日 上午10:20:30 
	*
	 */
	@Override
	public ResultInfo autoCheckPassword(int client, String businessSeqNo, long userId, ServiceType serviceType,double min_apr, int max_period, double invest_amt,String valid_time, int valid_day, Object... objects){
		
		ResultInfo result = new ResultInfo();
		
		//获取基本信息
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数的拼接
		HashMap<String, String> reqParams = ybPaymentService.autoCheckPassword(userId, businessSeqNo,serviceType, valid_time, invest_amt);
		
		String html="";
		//生成表单
		if(Client.PC.code==client){
			
			 html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5URL+"checkPassword.html");
		}
		if(Client.ANDROID.code==client || Client.IOS.code==client){
			
			 html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5_STANDARD_URL+"checkPassword.html");
		}
		
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.MIN_APR, min_apr+"");
		reqParams.put(RemarkPramKey.MAX_PERIOD,max_period+"");
		reqParams.put(RemarkPramKey.INVEST_AMT, invest_amt+"");
		reqParams.put(RemarkPramKey.VALID_TIME, valid_time+"");
		reqParams.put(RemarkPramKey.VALID_DAY, valid_day+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		
		
		//校验交易密码的请求参数的打印和保存
		ybPaymentService.printRequestData(requestMark, userId, businessSeqNo, null, ServiceType.FORGET_PASSWORD, TradeType.CUSTOMER_ENTRUST_PROTOCOL, reqParams,reqParams);
	    
		//app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
		
		
	}
	
	/**
	 * 签署自动投标协议
	 * 
	 */
	@Override
	public ResultInfo autoInvestSignature(int client, String businessSeqNo, long userId, ServiceType serviceType,
			double invest_amt, String valid_time,  Object... obj) {
		ResultInfo result= new ResultInfo();
		
		String clientSn = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		//委托协议号（签署委托协议唯一，取消委托协议应使用签署协议号，如数据库为空，用取消协议流水号）
		String protocolNo = businessSeqNo ;
		
		if (serviceType.code == 9){
			t_auto_invest_setting autoInvestOption = autoInvestSettingDao.findByColumn("user_id = ?", userId);
			if (!autoInvestOption.protocol_no.equals("")){
				protocolNo=autoInvestOption.protocol_no;
			}
		}		
		
		HashMap<String,JSONObject> request = ybPaymentService.autoInvestSignature(clientSn, businessSeqNo,
				userId,serviceType, protocolNo, invest_amt, valid_time);
		ybPaymentService.printRequestParams(clientSn, userId, businessSeqNo, "", request, serviceType, TradeType.CUSTOMER_ENTRUST_PROTOCOL);
		
		//请求第三方，得到响应报文
		String response = HttpUtil.postMethod(JSONObject.fromObject(request));
				
		//打印，保存响应报文
		ybPaymentService.printResponseParams(serviceType.note, clientSn, response, serviceType, TradeType.CUSTOMER_ENTRUST_PROTOCOL);
				
		//响应报文处理
		TreeMap<String, String> respMap = YbUtils.getRespHeader(response, serviceType ,null);

		if (respMap != null) {
			if (respMap.get("respCode").equals(YbConsts.RESP_SUC_CODE)) {
				result = callBackService.updateReqStatus(clientSn, serviceType);
				if (result.code < 1){
					return result;
				}
				result.code = 1;
				result.msg = respMap.get("respMsg");
				result.obj = protocolNo;
				
				return result;
			}
			if (!respMap.get("respCode").equals(YbConsts.RESP_SUC_CODE)) {
				result.code = -1;
				result.msg = respMap.get("respMsg");
				
				return result;
			}
		}		
		result.code = -1;
		result.msg = "托管第三方响应失败";
		
		return result;
	}
	
	/******************************************************************* niu *************************************************************************/

	@Override
	public ResultInfo bidInfoAysn(String clientSn, String businessSeqNo, t_bid bid, ServiceType serviceType, ProjectStatus projectStatus, List<Map<String, Object>> returnInfoList, Object... objects) {
		
		ResultInfo result = new ResultInfo();
		
		//请求报文
		HashMap<String, JSONObject> request = ybPaymentService.bidInfoAysn(clientSn, businessSeqNo, bid, serviceType, projectStatus, returnInfoList);
		
		//打印，保存请求报文
		ybPaymentService.printRequestParams(clientSn, bid.user_id, businessSeqNo, "", request, serviceType, TradeType.BID_INFO_SYNC);
		
		//请求第三方，得到响应报文
		String response = HttpUtil.postMethod(JSONObject.fromObject(request));
		
		//打印，保存响应报文
		ybPaymentService.printResponseParams(serviceType.note, clientSn, response, serviceType, TradeType.BID_INFO_SYNC);
	
		//响应报文处理
		TreeMap<String, String> respMap = YbUtils.getRespHeader(response, serviceType ,null);
	
		if (respMap != null) {
			if (respMap.get("respCode").equals(YbConsts.RESP_SUC_CODE)) {
				result.code = 1;
				result.obj = respMap.get("objectaccNo");
				result.msg = respMap.get("respMsg");
				
				return result;
			}
			if (!respMap.get("respCode").equals(YbConsts.RESP_SUC_CODE)) {
				result.code = -1;
				result.msg = respMap.get("respMsg");
				
				return result;
			}
		}		
		
		result.code = -1;
		result.msg = "托管第三方响应失败";
		
		return result;
	}


	@Override
	public ResultInfo fundTrade(String clientSn, long userId, String businessSeqNo, t_bid bid, ServiceType serviceType, EntrustFlag entrustFlag, List<Map<String, Object>> accountList, List<Map<String, Object>> contractList, Object... objects) {
		
		ResultInfo result = new ResultInfo();
		//组织请求报文
		HashMap<String, JSONObject> request = ybPaymentService.fundTrade(clientSn, businessSeqNo, bid, serviceType, entrustFlag, accountList, contractList);
		//打印，保存请求报文
		ybPaymentService.printRequestParams(clientSn, userId, businessSeqNo, "", request, serviceType, TradeType.BID_INFO_SYNC);
		
		//请求第三方，得到响应报文
		String response = HttpUtil.postMethod(JSONObject.fromObject(request));
		//打印，保存响应报文
		ybPaymentService.printResponseParams(serviceType.note, clientSn, response, serviceType, TradeType.BID_INFO_SYNC);
		//响应报文处理
		TreeMap<String, String> respMap = YbUtils.getRespHeader(response, serviceType ,null);
		
		if (respMap != null) {
			if (respMap.get("respCode").equals(YbConsts.RESP_SUC_CODE)) {		
				return callBackService.updateReqStatus(clientSn, serviceType);
			}
			if (!respMap.get("respCode").equals(YbConsts.RESP_SUC_CODE)) {
				result.code = -1;
				result.msg = respMap.get("respMsg");

				return result;
			}
		}		
		
		result.code = -1;
		result.msg = "托管第三方响应失败";
		
		return result;
	}

	@Override
	public ResultInfo checkTradePass(int client, String businessSeqNo, long userId, String backURL, String userName, ServiceType serviceType, double amount, Map<String, String> serviceRequestParams, ServiceType checkPassType, int type, String cardNo, String authtime, String authority, Object... objects) {
		
		//组织请求报文参数
		HashMap<String, String> reqParams = ybPaymentService.checkTradePass(businessSeqNo, userId, backURL, userName, serviceType, amount, type, cardNo, authtime, authority);
		
		//生成表单
		String html = "";
		if (Client.isAppEnum(client)) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5_STANDARD_URL + "checkPassword.html");
		} else {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5URL+"checkPassword.html");
		}

		//打印、保存请求参数
		ybPaymentService.printCheckPassReqParams(null, userId, businessSeqNo, null, checkPassType, TradeType.H5_CHECK_TRADE_PASS, backURL, reqParams, serviceRequestParams);
		
		ResultInfo res = new ResultInfo();
		
		if (Client.isAppEnum(client)) {
			res.code = 1;
			res.msg = "";
			res.obj = html;
			
			return res;
		}

		//表单提交
		YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return res;
	}

	@Override
	public ResultInfo queryTradeStatus(String clientSn, String businessSeqNo, String oldbusinessSeqNo, String operType, Object... objects) {
		
		//组织请求报文参数
		HashMap<String, JSONObject> reqParams = ybPaymentService.queryTradeStatus(clientSn, businessSeqNo, oldbusinessSeqNo, operType);
		
		//打印、保存请求报文参数
		//ybPaymentService.printRequestParams(clientSn, 0, oldbusinessSeqNo, userId, reqParams, ServiceType.TRADE_STATUS_QUERY, TradeType.TRADE_STATUS_QUERY);
		
		//请求第三方、得到响应报文
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		
		//打印，保存响应报文
		//ybPaymentService.printResponseParams(ServiceType.TRADE_STATUS_QUERY.note, clientSn, response, ServiceType.TRADE_STATUS_QUERY, TradeType.TRADE_STATUS_QUERY);
		
		//响应报文处理
		TreeMap<String, String> respMap = YbUtils.getRespHeader(response, ServiceType.TRADE_STATUS_QUERY ,null);
		
		ResultInfo result = new ResultInfo();
		
		if (respMap == null) {
			result.code = -1;
			result.msg = "交易状态查询失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "查询成功";
		result.msgs = respMap;
		
		return result;
	}

	@Override
	public ResultInfo dataInfoQuery(String clientSn, String customerId, String accountNo, ServiceType serviceType, Object... objects) {
		
		//组织请求报文参数
		HashMap<String, JSONObject> reqParams = ybPaymentService.dataInfoQuery(clientSn, customerId, accountNo, serviceType);
		
		//打印、保存请求报文参数
		ybPaymentService.printRequestParams(clientSn, 0, null, null, reqParams, serviceType, TradeType.DATA_INFO_QUERY);

		//请求第三方、得到响应报文
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		
		//打印，保存响应报文
		ybPaymentService.printResponseParams(serviceType.note, clientSn, response, serviceType, TradeType.DATA_INFO_QUERY);

		//响应报文处理
		TreeMap<String, String> respMap = YbUtils.getRespHeader(response, serviceType ,"cardList");
		
		ResultInfo result = new ResultInfo();
		if (respMap != null) {
			if (respMap.get("respCode").equals(YbConsts.RESP_SUC_CODE)) {
				result.code = 1;
				result.msg = respMap.get("respMsg");
				result.msgs = respMap;
				return result;
			}
			if (!respMap.get("respCode").equals(YbConsts.RESP_SUC_CODE)) {
				result.code = -1;
				result.msg = respMap.get("respMsg");
				
				return result;
			}
		}		
		
		result.code = -1;
		result.msg = "托管第三方响应失败";
		
		
		return result;
	}
	
	
	@Override
	public ResultInfo custInfoAysn(Map<String, String> inBody, String clientSn, ServiceType serviceType, Object... objects) {
		
	
		//组织请求报文参数
		HashMap<String, JSONObject> reqParams = ybPaymentService.custInfoAysn(inBody, clientSn, serviceType);
		
		//打印、保存请求报文参数
		ybPaymentService.printRequestParams(clientSn, Convert.strToLong(inBody.get("customerId"), 0), inBody.get("businessSeqNo"), "", reqParams, serviceType, TradeType.CUSTOMER_INFO_SYNC);
		
		//请求第三方、得到响应报文
		String response = HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		
		ybPaymentService.printResponseParams(serviceType.note, clientSn, response, serviceType, TradeType.CUSTOMER_INFO_SYNC);
		//响应报文处理
		TreeMap<String, String> respMap = YbUtils.getRespHeader(response, serviceType ,"cardList");
		
		ResultInfo result = new ResultInfo();
		
		if (respMap != null) {
			if (respMap.get("respCode").equals(YbConsts.RESP_SUC_CODE)) {
				result.code = 1;
				result.msg = respMap.get("respMsg");
				
				result.msgs.put("accNo", respMap.get("accNo"));
				result.msgs.put("secBankaccNo", respMap.get("secBankaccNo"));
				
				return result;
			}
			if (!respMap.get("respCode").equals(YbConsts.RESP_SUC_CODE)) {
				result.code = -1;
				result.msg = respMap.get("respMsg");
				
				return result;
			}
		}		
		
		result.code = -1;
		result.msg = "托管第三方响应失败";
		
		return result;
	}
	
	/******************************************************************* niu *************************************************************************/
	/**
	 * 绑定银行卡(银行卡预留手机号)
	 * 
	 * @author guoShiJie
	 * @create 2018.04.16
	 */
	@Override
	public ResultInfo bindCardCheckPassword1(int client, String businessSeqNo, long userId,String mobilePhone){
		ResultInfo result = new ResultInfo();
		
		//获取基本信息
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		//托管请求唯一标识
		String requestMark = YbConsts.MER_CODE + "_" + UUID.randomUUID().toString();
		
		//参数的拼接
		HashMap<String, String> reqParams = ybPaymentService.bindCardCheckPassword1(userId, businessSeqNo, ServiceType.CUSTOMER_BIND_CARD, mobilePhone);
		//生成表单
		String html = null;
		if(client == 1) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5URL+"checkPassword.html");
		}else if(client == 2 || client == 3) {
			html = YbUtils.createFormHtml(reqParams, YbConsts.YIBIN_H5_STANDARD_URL+"checkPassword.html");
		}
		
		//备注参数（全部以“r_”开头）
		reqParams.put(RemarkPramKey.USER_ID, userId+"");
		reqParams.put(RemarkPramKey.CLIENT, client+"");
		
		//校验交易密码的请求参数的打印和保存
		ybPaymentService.printRequestData(requestMark, userId, businessSeqNo, null, ServiceType.FORGET_PASSWORD, TradeType.CUSTOMER_INFO_SYNC, reqParams,reqParams);
	    
		//app 接口处理
		if(Client.isAppEnum(client)){
			result.code = 1;
			result.msg = "";
			
			result.obj = html;
			return result;
		}
		// 表单提交
		YbPaymentRequestCtrl.getInstance().submitForm(html, client);
		
		return result;
	}
	
	/**
	 * 受让冲正数据拼接
	 * 
	 * @param businessSeqNo 业务流水号
	 * @param clientSn 托管请求唯一标识
	 * @param debitAccountNo 受让方台账账号
	 * @param financierAccountNo 转让方台账账号
	 * @param financierFee 交易金额
	 * @param oldbusinessSeqNo 原业务流水号
	 *
	 * @author liuyang
	 * @createDate 2018年8月9日
	 */
	public ResultInfo reversalTransfer(String businessSeqNo, String clientSn, String debitAccountNo,
			String financierAccountNo, double financierFee, String oldbusinessSeqNo, String bidNo){
		
		ResultInfo result = new ResultInfo();
		
		//参数拼装
		HashMap<String, JSONObject> reqParams = ybPaymentService.reversalTransfer(businessSeqNo, clientSn, debitAccountNo,
				financierAccountNo, financierFee, oldbusinessSeqNo, bidNo);
		
		Map<String, String> remarkParams = new HashMap<String, String>();
		
		//备注参数（全部以“r_”开头）
		remarkParams.put(RemarkPramKey.BOND_FEE, "");
		
		//打印，保存请求
		ybPaymentService.printRequestDataS(clientSn, 0, businessSeqNo, null, ServiceType.WASHED, TradeType.FUND_TRADE, reqParams, remarkParams);
		
		//请求第三方,得到响应报文
		HttpUtil.postMethod(JSONObject.fromObject(reqParams));
		
		callBackService.updateReqStatus(clientSn, ServiceType.WASHED);
		
		return result;
		
	}
}





