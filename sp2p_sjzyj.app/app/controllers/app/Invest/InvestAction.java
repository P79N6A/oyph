package controllers.app.Invest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;
import payment.impl.PaymentProxy;
import play.mvc.Scope.Session;
import service.DebtAppService;
import service.InvestAppService;
import services.common.QuotaService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.ssqUserService;
import services.core.BidItemSupervisorService;
import services.core.BidItemUserService;
import services.core.BillInvestService;
import services.core.InvestService;
import services.ext.cps.CpsUserService;
import services.finance.TradeInfoService;
import yb.YbUtils;
import yb.enums.ServiceType;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Client;
import common.enums.DeviceType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.UserValidataUtil;
import controllers.app.wealth.MyFundAction;
import controllers.common.BaseController;
import models.common.bean.CurrUser;
import models.common.entity.t_quota;
import models.common.entity.t_ssq_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.ext.cps.entity.t_cps_user;
import models.ext.redpacket.entity.t_red_packet_user;
import models.finance.entity.t_trade_info.TradeType;

/**
 * 散标投资
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月30日
 */
public class InvestAction {
	private static InvestAppService investAppService = Factory.getService(InvestAppService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static UserFundService uesrFundService = Factory.getService(UserFundService.class);
	
	protected static BidItemSupervisorService bidItemSupervisorService = Factory.getService(BidItemSupervisorService.class);
	
	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);
	
	protected static QuotaService quotaService = Factory.getService(QuotaService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected static ssqUserService ssquserService = Factory.getService(ssqUserService.class);
	
	protected static CpsUserService cpsUserService = Factory.getService(CpsUserService.class);
	
	protected static TradeInfoService tradeInfoService = Factory.getService(TradeInfoService.class);
	
	/***
	 * 理财产品列表接口（OPT=311）
	 *
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public static String pageOfInvestBids(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
	
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
		
		return investAppService.pageOfInvestBids(currPage, pageSize);
	}
	
	/***
	 * 借款标详情（opt=312）
	 *
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-13
	 */
	public static String investBidInformation(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String bidIdSign = parameters.get("bidIdSign");
		String userIds = parameters.get("userId");
		
		ResultInfo result = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 return json.toString();
		}
		
		long bidId = Long.parseLong(result.obj.toString());
		
		t_user_fund userFund = new t_user_fund();
		if(userIds == null || userIds.equals("")) {
			return investAppService.findInvestBidInformation(bidId, userFund);
		}
		ResultInfo userIdSignDecode = Security.decodeSign(userIds, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		/*if (userIdSignDecode.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			
			return json.toString();
		}*/
		
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
    	
		userFund = uesrFundService.queryUserFundByUserId(userId);
		
		
		return investAppService.findInvestBidInformation(bidId, userFund);
	}
	
	/***
	 * 借款人详情接口（OPT=322）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public static String investBidDeatils(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String bidIdSign = parameters.get("bidIdSign");
		
		ResultInfo result = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 return json.toString();
		}
    	
		long bidId = Long.parseLong(result.obj.toString());
		
		return investAppService.findInvestBidDeatils(bidId);
	}
		
	/***
	 * 回款计划接口（OPT=323）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public static String listOfRepaymentBill(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String bidIdSign = parameters.get("bidIdSign");
		
		ResultInfo result = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 return json.toString();
		}
    	
		long bidId = Long.parseLong(result.obj.toString());
		
		return investAppService.listOfRepaymentBill(bidId);
	}
	
	/***
	 * 投标记录接口（OPT=324）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public static String investBidsRecord(Map<String, String> parameters) throws Exception{
		
		JSONObject json = new JSONObject();
		
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		String bidIdSign = parameters.get("bidIdSign");
		
		ResultInfo result = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 return json.toString();
		}
    	
		long bidId = Long.parseLong(result.obj.toString());
		
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
			
		return investAppService.pageOfInvestBidsRecord(currPage, pageSize,bidId);
	}
	
	
	/**
	 * 查询借款标审核详情图片（ OPT=335 ）
	 * 
	 * @param parameters 问题参数
	 * @return 
	 * 
	 * @author liuyang
	 * @create 2017-12-1
	 */
	public static String auditImgDeatils(Map<String, String> parameters) throws Exception {
		JSONObject json = new JSONObject();
		
		//定义返回结果
		Map<String, Object> result = new HashMap<String, Object>();
		
		//获取参数
		String bidIdSign = parameters.get("bidIdSign");
		
		ResultInfo results = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (results.code < 1) {
			 json.put("code",-1);
			 json.put("msg", results.msg);
			 return json.toString();
		}
    	
		long bidId = Long.parseLong(results.obj.toString());
		
		if (bidId <= 0) {
			result.put("code", -1);
			result.put( "msg", "标的Id不存在");
			
			return JSONObject.fromObject(result).toString();
		}
		
		String auditIds = parameters.get("auditId");
		
		if (auditIds == null || auditIds.equals("")) {
			result.put("code", -1);
			result.put( "msg", "审核详情的Id获取失败");
			
			return JSONObject.fromObject(result).toString();
		}
		
		//
		long auditId = Convert.strToLong(auditIds, 0);
		
		if (auditId <= 0) {
			result.put("code", -1);
			result.put( "msg", "审核详情的Id不存在");
			
			return JSONObject.fromObject(result).toString();
		}
		
		return bidItemSupervisorService.listOfAuditItems(bidId, auditId);
	}
	
	
	public static String investCheckPass(Map<String, String> parameters) throws Exception{
		
		JSONObject json = new JSONObject();
		
		String userIdSign = parameters.get("userId");
		String investAmt = parameters.get("investAmt");//投标金额或者投标份数
		String bidIdSign = parameters.get("bidIdSign");
		String deviceType = parameters.get("deviceType");//设备类型
		String redPacketIdStr = parameters.get("redPacketId");
		String couponIdStr = parameters.get("couponId");
		
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (userIdSignDecode.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			
			return json.toString();
		}
		ResultInfo bidIdSignDecode = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (bidIdSignDecode.code < 1) {
			json.put("code", -1);
			json.put("msg", bidIdSignDecode.msg);
			
			return json.toString();
		}
		if (!StrUtil.isNumericPositiveInt(investAmt)) {
			json.put("code", -1);
			json.put("msg", "请输入正确的投标金额!");
			
			return json.toString();
		}
		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		long bidId = Long.parseLong(bidIdSignDecode.obj.toString());
		long redPacketId = 0;
		if(redPacketIdStr != null){
			 redPacketId = Long.parseLong(redPacketIdStr);
		}
		
			
		if (!UserValidataUtil.checkPaymentAccount(userId)) {
			json.put("code", ResultInfo.NOT_PAYMENT_ACCOUNT);
			json.put("msg", "请先开通资金托管");
			
			 return json.toString();
		}
		
		if (!UserValidataUtil.checkRealName(userId)) {
			json.put("code", ResultInfo.NOT_REAL_NAME);
			json.put("msg", "请实名认证");
			
			 return json.toString();
		}
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		if (userInfo == null) {
			json.put("code", -1);
			json.put("msg", "用户基本信息查询失败");
			
			return json.toString();
		}
		
		t_ssq_user ssqUser = ssquserService.findByUserId(userId);
		
		// 10. 电子签章准备
		if (ssqUser == null ){
			
			json.put("code", -1);
			json.put("msg", "电子签章用户校验失败");
			return json.toString();
		}
		//企业用户
		if (userInfo.enterprise_name != null || !(StringUtils.isBlank(userInfo.enterprise_name)) ){
			json.put("code", -1);
			json.put("msg", "企业用户权限仅支持借款");
			return json.toString();
		}
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.TENDER);
		
		double amount = Convert.strToDouble(investAmt, 0.0);
		
		// 11. 判断投资待还金额和每日限额是否超过限制
		
		Date dates = new Date();
		StringBuffer startTime = new StringBuffer();
		startTime.append(DateUtil.getYear(dates)+"-").append(DateUtil.getMonth(dates)+"-").append(DateUtil.getDay(dates)+" 00:00:00");
		
		//用户投资待还金额
		double amounts = billInvestService.findTotalAmounts(userId)+amount;
		
		//用户今日投资总额
		double dayMount = investService.findTodayAmounts(userId, startTime.toString())+amount;
		
		t_quota quota = quotaService.findByColumn("user_id=?", userId);
		long creditId = userInfo.credit_id;
		
		if(quota == null) {
			if(creditId==3) {
				if(amounts>4000000) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，点击向平台提出申请");
					
					t_quota quo = new t_quota();
					quo.user_id = userId;
					quo.sum_invest = amounts-amount;
					quo.time = new Date();
					quo.save();
					
					return json.toString();
				}else if(dayMount>1500000) {
					json.put("code", -1);
					json.put("msg", "用户今日投资金额超过限额");
					
					return json.toString();
				}
				
			} else if(creditId==4) {
				if(amounts>5000000) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，点击向平台提出申请");
					
					t_quota quo = new t_quota();
					quo.user_id = userId;
					quo.sum_invest = amounts-amount;
					quo.time = new Date();
					quo.save();
					
					return json.toString();
				}else if(dayMount>2000000) {
					json.put("code", -1);
					json.put("msg", "用户今日投资金额超过限额");
					
					return json.toString();
				}
			}else if(creditId==5) {
				if(amounts>10000000) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，点击向平台提出申请");
					
					t_quota quo = new t_quota();
					quo.user_id = userId;
					quo.sum_invest = amounts-amount;
					quo.time = new Date();
					quo.save();
					
					return json.toString();
				}else if(dayMount>3000000) {
					json.put("code", -1);
					json.put("msg", "用户今日投资金额超过限额");
					
					return json.toString();
				}
			}else if(creditId==1) {
				if(amounts>2000000) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，点击向平台提出申请");
					
					t_quota quo = new t_quota();
					quo.user_id = userId;
					quo.sum_invest = amounts-amount;
					quo.time = new Date();
					quo.save();
					
					return json.toString();
				}else if(dayMount>500000) {
					json.put("code", -1);
					json.put("msg", "用户今日投资金额超过限额");
					
					return json.toString();
				}
			}else if(creditId==2) {
				if(amounts>3000000) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，点击向平台提出申请");
					
					t_quota quo = new t_quota();
					quo.user_id = userId;
					quo.sum_invest = amounts-amount;
					quo.time = new Date();
					quo.save();
					
					return json.toString();
				}else if(dayMount>1000000) {
					json.put("code", -1);
					json.put("msg", "用户今日投资金额超过限额");
					
					return json.toString();
				}
			}
		}else {
			if(creditId==3) {
				if(quota.type == 0) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，已经提出申请，请耐心等待");
					
					return json.toString();
				}else if(dayMount>1500000) {
					json.put("code", -1);
					json.put("msg", "用户今日投资金额超过限额");
					
					return json.toString();
				}else if(quota.amount<amounts) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，点击向平台提出申请");
					quota.time = new Date();
					quota.sum_invest = amounts-amount;
					quota.type = 0;
					quota.save();
					return json.toString();
				}
				
			} else if(creditId==4) {
				if(quota.type == 0) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，已经提出申请，请耐心等待");
					
					return json.toString();
				}else if(dayMount>2000000) {
					json.put("code", -1);
					json.put("msg", "用户今日投资金额超过限额");
					
					return json.toString();
				}else if(quota.amount<amounts) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，点击向平台提出申请");
					quota.time = new Date();
					quota.sum_invest = amounts-amount;
					quota.type = 0;
					quota.save();
					return json.toString();
				}
			} else if(creditId==5) {
				if(quota.type == 0) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，已经提出申请，请耐心等待");
					
					return json.toString();
				}else if(dayMount>3000000) {
					json.put("code", -1);
					json.put("msg", "用户今日投资金额超过限额");
					
					return json.toString();
				}else if(quota.amount<amounts) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，点击向平台提出申请");
					quota.time = new Date();
					quota.sum_invest = amounts-amount;
					quota.type = 0;
					quota.save();
					return json.toString();
				}
			} else if(creditId==1) {
				if(quota.type == 0) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，已经提出申请，请耐心等待");
					
					return json.toString();
				}else if(dayMount>500000) {
					json.put("code", -1);
					json.put("msg", "用户今日投资金额超过限额");
					
					return json.toString();
				}else if(quota.amount<amounts) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，点击向平台提出申请");
					quota.time = new Date();
					quota.sum_invest = amounts-amount;
					quota.type = 0;
					quota.save();
					return json.toString();
				}
			} else if(creditId==2) {
				if(quota.type == 0) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，已经提出申请，请耐心等待");
					
					return json.toString();
				}else if(dayMount>1000000) {
					json.put("code", -1);
					json.put("msg", "用户今日投资金额超过限额");
					
					return json.toString();
				}else if(quota.amount<amounts) {
					json.put("code", -1);
					json.put("msg", "用户总投资金额超过限制，点击向平台提出申请");
					quota.time = new Date();
					quota.sum_invest = amounts-amount;
					quota.type = 0;
					quota.save();
					return json.toString();
				}
			}
		}
		
		
		Map<String, String> serviceReqParams = new HashMap<String, String>();
		serviceReqParams.put("businessSeqNo", businessSeqNo);
		serviceReqParams.put("userId", userId + "");
		serviceReqParams.put("amount", amount + "");
		serviceReqParams.put("bidId", bidId + "");
		serviceReqParams.put("couponId", couponIdStr);
		serviceReqParams.put("redPacketId", redPacketId + "");
		serviceReqParams.put("deviceType", deviceType);
		

		ResultInfo result = null;
		//ResultInfo result = PaymentProxy.getInstance().checkTradePass(Integer.parseInt(deviceType), businessSeqNo, userId, BaseController.getBaseURL() + "payment/yibincallback/checkPassAI", userInfo.reality_name, ServiceType.TENDER, amount, serviceReqParams, ServiceType.TENDER_CHECK_TRADE_PASS, 2, "", "", "");

		t_cps_user cpsUser = cpsUserService.findByUserId(userId);
		
		if (cpsUser == null) {
			result = PaymentProxy.getInstance().checkTradePass(Integer.parseInt(deviceType), businessSeqNo, userId, BaseController.getBaseURL() + "payment/yibincallback/checkPassAI", userInfo.reality_name, ServiceType.TENDER, amount, serviceReqParams, ServiceType.TENDER_CHECK_TRADE_PASS, 2, "", "", "");
		} else {
			result = PaymentProxy.getInstance().checkTradePass(Integer.parseInt(deviceType), businessSeqNo, userId, BaseController.getBaseURL() + "payment/yibincallback/checkPassAI2?amount="+amount+"&bidId="+bidId+"&memberType="+userInfo.getMember_type().code, userInfo.reality_name, ServiceType.TENDER, amount, serviceReqParams, ServiceType.TENDER_CHECK_TRADE_PASS, 2, "", "", "");
		}
		json.put("code", result.code);
		json.put("msg", result.msg);
		json.put("flag", "1");
		json.put("grade", userInfo.credit_id);
		json.put("html", result.obj.toString());
		
		/** 交易信息表（t_trade_info）*/
		tradeInfoService.saveTradeInfo(new Date(), businessSeqNo, TradeType.INVEST, userId, "CNY", new BigDecimal(investAmt), bidId);
		
		return json.toString();

	}
	
	
	public static ResultInfo invest(Map<String, String> parameters) {
		
		long userId = Convert.strToLong(parameters.get("userId"), -1);
		long bidId = Convert.strToLong(parameters.get("bidId"), -1);
		String investAmt = parameters.get("amount");
		int client = Convert.strToInt(parameters.get("deviceType"), -1);
		String businessSeqNo = parameters.get("businessSeqNo");
		long redPacketId = Convert.strToLong(parameters.get("redPacketId"), -1);
		String couponIdStr = parameters.get("couponId");
		
		return investAppService.appInvest(userId, bidId, investAmt, Client.getEnum(client), businessSeqNo, redPacketId, couponIdStr);
	}
	
}
