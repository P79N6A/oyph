package controllers.app.wealth;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.app.bean.DebtInvestBean;
import models.app.bean.InOutDebt;
import models.app.bean.MyInvestRecordBean;
import models.common.entity.t_loan_profession;
import models.common.entity.t_pact;
import models.common.entity.t_user_info;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import models.main.bean.LoanContract;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.DataFormatRecord;

import payment.impl.PaymentProxy;
import play.Logger;
import service.AccountAppService;
import service.DebtAppService;
import service.LoanAppService;
import services.common.LoanProfessionService;
import services.common.PactService;
import services.common.SettingService;
import services.common.UserInfoService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.InvestService;
import yb.YbUtils;
import yb.enums.EntrustFlag;
import yb.enums.ServiceType;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.SettingKey;
import common.enums.DeviceType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.TimeUtil;
import common.utils.number.Arith;
import controllers.common.BaseController;
import controllers.front.account.MyFundCtrl;
import controllers.front.seal.ElectronicSealCtrl;

/**
 * 账户中心-财富管理
 *
 * @description 包括我的投资，我的借款，我的受让/转让，转让申请等
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
public class MyFundAction {
	
	private static AccountAppService accountAppService = Factory.getService(AccountAppService.class);
	
	private static LoanAppService loanAppService = Factory.getService(LoanAppService.class);
	
	private static BillService billService = Factory.getService(BillService.class);
	
	private static DebtAppService debtAppService = Factory.getService(DebtAppService.class);
	
	private static PactService pactService = Factory.getService(PactService.class);
	
	private static BillInvestService billInvestService = Factory.getService(BillInvestService.class); //lihuijun
	
	protected static BidService bidService = Factory.getService(BidService.class); //lihuijun
	
	protected static LoanProfessionService loanProfessionService = Factory.getService(LoanProfessionService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	/***
	 * 
	 * 我的投资（OPT=231）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-5
	 */
	public static String pageOfMyInvest (Map<String, String> parameters) throws Exception{
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
		PageBean<MyInvestRecordBean> page = accountAppService.pageOfMyInvestRecord(currPage, pageSize, userId);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(new String[] {// 只要设置这个数组，指定过滤哪些字段。
				"investOriId",
				"id"
		});
		  
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("records", page.page);
        //JSONObject.fromObject(object, jsonConfig)
		return JSONObject.fromObject(map,jsonConfig).toString();
	}
	
	/**
	 * 
	 * @Title: pageOfMyInvestNew
	 *
	 * @description 我的投资接口——我的出借（OPT=231）
	 *
	 * @param @param parameters
	 * @param @return
	 * @param @throws Exception 
	 * 
	 * @return String    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年12月28日
	 */
	public static String pageOfMyInvestNew (Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		String debtOfStr = parameters.get("debtOf");
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
		int debtOf = -1;
		if (!StrUtil.isNumeric(debtOfStr)) {
			json.put("code", -1);
			json.put("msg", "债权类型有误");

			return json.toString();
		} else {
			debtOf = Convert.strToInt(debtOfStr, -1);
			if (debtOf != 1 && debtOf != 0) {
				json.put("code", -1);
				json.put("msg", "债权类型有误");

				return json.toString();
			}
		}
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
        long userId = Long.parseLong(result.obj.toString());
		
		PageBean<MyInvestRecordBean> page = null;
		if(debtOf == 0){
			/** debtOf=0出借未结束 */
			page = accountAppService.pageOfMyInvestRecordNewUnfinished(currPage, pageSize, userId);//未结束
		} else {
			/** debtOf=1出借已结束 */
			page = accountAppService.pageOfMyInvestRecordNewEnd(currPage, pageSize, userId);//结束
		}
		
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(new String[] {// 只要设置这个数组，指定过滤哪些字段。
				"investOriId",
				"id"
		});
		  
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("records", page.page);
        //JSONObject.fromObject(object, jsonConfig)
		
		return JSONObject.fromObject(map,jsonConfig).toString();
	}
	
	/***
	 * 
	 * 投资账单接口（OPT=232）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-5
	 */
	public static String listOfInvestBill (Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		String signUsersId = parameters.get("userId");
		String signInvestId = parameters.get("investId");
		ResultInfo result = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}

		long userId = Long.parseLong(result.obj.toString());
		
		result = Security.decodeSign(signInvestId, Constants.INVEST_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 
			 return json.toString();
		}
		long investId = Long.parseLong(result.obj.toString());

		return accountAppService.listOfInvestBillRecord(userId,investId);
	}
	
	/**
	 * 我的借款（OPT=233）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月1日
	 */
	public static String pageOfMyLoan(Map<String, String> parameters){
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
		int currPage = Convert.strToInt(currentPageStr, 1);
		int pageSize = Convert.strToInt(pageNumStr, 10);
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		PageBean pageBean = accountAppService.pageOfMyLoan(Long.parseLong(result.obj.toString()), currPage, pageSize);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("records", pageBean.page);
		
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(new String[] {// 只要设置这个数组，指定过滤哪些字段。
				"id"
		});
		
		return JSONObject.fromObject(map, jsonConfig).toString();
	}
	
	
	/**
	 * opt=234 借款账单
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月14日
	 */
	public static String listOfLoanBill(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signUserId = parameters.get("userId");
		String signBidId = parameters.get("bidId");
		if (StringUtils.isBlank(signUserId)) {
			json.put("code", -1);
			json.put("msg", "参数userId不能为空");
			
			return json.toString();
		}
		if (StringUtils.isBlank(signBidId)) {
			json.put("code", -1);
			json.put("msg", "参数bidId不能为空");
			
			return json.toString();
		}
		
		ResultInfo result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		ResultInfo result2 = Security.decodeSign(signBidId, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", -1);
			 json.put("msg", result.msg);
			 
			 return json.toString();
		}
		long userId = Long.parseLong(result.obj.toString());
		long bidId = Long.parseLong(result2.obj.toString());
		
		return loanAppService.listOfLoanBill(userId, bidId);
	}
	
	/****
	 * 还款请求（Opt=235）
	 *
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-11
	 */
	public static ResultInfo repayment(Map<String, String> parameters, String businessSeqNo) {
		
		ResultInfo result = new ResultInfo();
		JSONObject json = new JSONObject();
		
		String billIdSign = parameters.get("billIdSign");
		String userIdSign = parameters.get("userId");
		String deviceType = parameters.get("deviceType");
		
		result = Security.decodeSign(billIdSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return result;
		}
		
		
		ResultInfo userResult = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (userResult.code < 1) {
			 json.put("code", -1);
			 json.put("msg", result.msg);
			 
			 return result;
		}
		
		
		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return result;
		}

		long billId = Long.parseLong(result.obj.toString());
		long userId = Long.parseLong(userResult.obj.toString());
		
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			 json.put("code", -1);
			 json.put("msg", "服务器繁忙");
			 
			 return result;
		}
		
		/** 正常还款 */
		
		result = billService.normalRepayment(userId, bill);
		if (result.code < 1) {
			 json.put("code", -1);
			 json.put("msg", result.msg);
			 
			 return result;
		}
		
		t_bid bid = bidService.findByID(bill.bid_id);
		
		List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
		accountList.add(YbUtils.getAccountMap("0", "", "", bid.user_id + "", bid.object_acc_no, YbUtils.formatAmount(bill.repayment_corpus), "T04"));
		accountList.add(YbUtils.getAccountMap("1", "", "", bid.user_id + "", bid.object_acc_no, YbUtils.formatAmount(Arith.add(bill.repayment_interest, bill.overdue_fine)), "T12"));

		
		//资金托管 - 还款
		if (ConfConst.IS_TRUST) {
			
			result = PaymentProxy.getInstance().fundTrade(OrderNoUtil.getClientSn(), userId, businessSeqNo, bid, ServiceType.REPAYMENT, EntrustFlag.UNENTRUST, accountList, YbUtils.getContractList());
			if (result.code < 0) {
				LoggerUtil.info(true, result.msg);
				
				return result;
			} else {
				
				result = billService.doRepaymentOne(businessSeqNo, bill, bill.overdue_fine, bid);
				if (result.code < 0) {
					LoggerUtil.info(true, result.msg);
					return result;
				} else {
					if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
						Logger.info("融资人台账  -> 标的台账  -> 还款成功");
					}
					
					//正常还款时改变贷款业务信息表中的数据
					t_loan_profession loans = loanProfessionService.queryLoanByBidId(bill.bid_id);
					t_bill bills = billService.findByID(billId);
					if(loans != null) {
						loans.last_repayment_date = new Date();
						Double zhis = Double.parseDouble(loans.agreement_repayment_amount);
						loans.actual_amount = bills.repayment_corpus + zhis;
						loans.time = new Date();
						Integer zhi = Integer.parseInt(loans.resudue_months);
						loans.resudue_months = (zhi - 1)+"";
						loans.save();
					}
					
					 json.put("code", 1);
					 json.put("msg", "还款成功,请核对账单");
					
					 return result;
				}
			}
		}
		
		 json.put("code", -1);
		 json.put("msg", "还款失败");
		
		 return result;
	}
	
	/****
	 * 还款请求（Opt=2350）
	 *
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-11
	 */
	public static String repaymentAll (Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String billIdSign = parameters.get("billIdSign");
		String userIdSign = parameters.get("userId");
		String deviceType = parameters.get("deviceType");
		
		ResultInfo result = Security.decodeSign(billIdSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		
		ResultInfo userResult = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (userResult.code < 1) {
			 json.put("code", -1);
			 json.put("msg", result.msg);
			 
			 return json.toString();
		}
		
		
		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}

		long billId = Long.parseLong(result.obj.toString());
		long userId = Long.parseLong(userResult.obj.toString());
		
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			 json.put("code", -1);
			 json.put("msg", "服务器繁忙");
			 
			 return json.toString();
		}
		
		List<t_bill> billList=billService.findBillByBidIdAndStatus(bill.bid_id); 
		List<t_bill_invest> theBillInvests=billInvestService.billInvestListByBidIdAndPeriod(bill.bid_id,bill.period);
		List<t_bill_invest> billInvestList=billInvestService.getBillInvestListByBidIdAndStatus(bill.bid_id);
		List<t_bill_invest> billInvestOverdueList = billInvestService.getInvestOverduelist(bill.bid_id);
		
		/** 正常还款 */
		if (t_bill.Status.NORMAL_NO_REPAYMENT.equals(bill.getStatus())) {
			result = billService.normalRepaymentAll(userId, bill);
			if (result.code < 1) {
				 json.put("code", -1);
				 json.put("msg", result.msg);
				 
				 return json.toString();
			}
			
			List<Map<String, Double>> billInvestFeeList = (List<Map<String, Double>>) result.obj;
			
			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_FAILED);
			if (ConfConst.IS_TRUST) {
				result = PaymentProxy.getInstance().repaymentAll(Convert.strToInt(deviceType, DeviceType.DEVICE_ANDROID.code), serviceOrderNo, bill, billInvestFeeList);

				if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
					 LoggerUtil.info(true, result.msg);
					 json.put("code", -1);
					 json.put("msg", result.msg);	 
				} else {
					double surplusCorpus=0;
					double surplusInterest=0;
					double surplusFine=0;
					for (t_bill_invest  billIn:billInvestList) {
						surplusCorpus += billIn.receive_corpus;
					}
					if(billInvestOverdueList!=null && billInvestOverdueList.size()>0){
						for(t_bill_invest interest:billInvestOverdueList){
							surplusFine+=interest.overdue_fine;
							surplusInterest+=interest.receive_interest;
						}
					}
					billService.updateThisBill(bill.id,surplusCorpus,surplusInterest,surplusFine); //更新本期借款账单
					
					if(billList!=null && billList.size()>0){
						for(t_bill bil:billList){
							if(!bil.id.equals(bill.id)){
								billService.updateBill(bil.id);
							}
						}
					}
	
					if(billInvestList!=null && billInvestList.size()>0){  
						double corpus=0;
						double interest=0;
						double fine=0;
						for(t_bill_invest bine:theBillInvests){
							corpus=0;
							interest=0;
							fine=0;
							for(t_bill_invest bin:billInvestList){
								if(bine.invest_id==bin.invest_id ){
									corpus+=bin.receive_corpus;
								}
							}
							
							if(billInvestOverdueList!=null && billInvestOverdueList.size()>0){
								for(t_bill_invest binOver:billInvestOverdueList){
									if(bine.invest_id==binOver.invest_id){
										interest+=binOver.receive_interest;
										fine+=binOver.overdue_fine;
									}
								}
							}
							billInvestService.updateTheBillInvest(bine.id, corpus, interest, fine);
						}					
						for(t_bill_invest bin:billInvestList){
							boolean flag=false;
							for(t_bill_invest bine:theBillInvests){
								if(bine.id.equals(bin.id)){
									flag=true;
									break;
								}
							}
							if(!flag){
								billInvestService.updateThisBillInvest(bin.id);
							}
						}
					}
					
					if (billService.isEndPayment(bill.bid_id)) {
						bidService.bidEnd(bill.bid_id);
					}
					 json.put("code", 1);
					 json.put("msg", "还款成功,请核对账单");
				}
				
				 return json.toString();
			}
			
			result = billService.doRepayment(billId, billInvestFeeList, serviceOrderNo);
			if (result.code < 1) {
				 LoggerUtil.info(true, result.msg);
				 json.put("code", -1);
				 json.put("msg", result.msg);
				 
				 return json.toString();
			}
			
			 json.put("code", 1);
			 json.put("msg", "还款成功,请核对账单");
			
			 return json.toString();
		}
		
		/** 本息垫付后还款 */
		if (t_bill.Status.ADVANCE_PRINCIIPAL_NO_REPAYMENT.equals(bill.getStatus())){
			
			result = billService.advanceRepayment(userId, bill);
			if (result.code < 1) {
				 LoggerUtil.info(true, result.msg);
				 json.put("code", -1);
				 json.put("msg", result.msg);
				 
				 return json.toString();
			}
			
			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_MODIFY);
			if (ConfConst.IS_TRUST) {
				result = PaymentProxy.getInstance().advanceRepayment(Convert.strToInt(deviceType, DeviceType.DEVICE_ANDROID.code), serviceOrderNo, bill);
				if (result.code < 1 && result.code!=ResultInfo.ALREADY_RUN) {
					 LoggerUtil.info(true, result.msg);
					 json.put("code", -1);
					 json.put("msg", result.msg);
					 
					 return json.toString();
				}
				
				json.put("code", 1);
			    json.put("msg", "还款成功,请核对账单");		 	 
				
				return json.toString();
			}
			
			result = billService.doAdvanceRepayment(serviceOrderNo, userId,billId, bill.overdue_fine);
			if (result.code < 1) {
				 LoggerUtil.info(true, result.msg);
				 json.put("code", -1);
				 json.put("msg", result.msg);
				 
				 return json.toString();
			}
			
			json.put("code", 1);
			json.put("msg", "还款成功,请核对账单");
			
			return json.toString();
		}
		
		 json.put("code", -1);
		 json.put("msg", "账单已还款");
		 
		 return json.toString();
	}
	
	
	

	/***
	 * 
	 * 查看合同（OPT=236）
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @throws ParseException 
	 * @createDate 2016-4-12
	 */
	public static String showBidPact(Map<String, String> parameters) throws ParseException{
		System.out.println("236我走的是，我的借款");
		JSONObject json = new JSONObject();
		String bidIdSign = parameters.get("bidId");
		String time  = parameters.get("time");
		String user_Id = parameters.get("userId");
		Long userId = null;
		ResultInfo result;
		if (user_Id.length()>8) {
			result = Security.decodeSign(user_Id, Constants.USER_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_APP_KEY_DES);
			userId = Long.parseLong(result.obj.toString());
			
		}else {
			userId =Long.parseLong(user_Id);
			
		}
		result = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_APP_KEY_DES);
		Long bidId = Long.parseLong(result.obj.toString());
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 
			 return json.toString();
		}
		String PactResult = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//正式环境需要把时间改为2018-12-14 18:00:00
		Date date1 = dateFormat.parse("2018-12-14 18:00:00");
//		Date date2 = dateFormat.parse("2019-1-13 18:00:00");
		Date release_time = TimeUtil.strToDate(time);
		if (release_time.before(date1)) {
			PactResult = accountAppService.findBidPact(bidId);
			
		} else {
			PactResult = accountAppService.findBidAndUserIdByPact(bidId,userId);
		}
			
//		} else{
//			long uid = userId;
//			PactResult = ElectronicSealCtrl.previewContract(bidId, userId, time, uid);
//			json.put("PactResult", PactResult);
//			json.put("code",1);
//			json.put("msg", result.msg);
//			System.out.println("这儿");
//			return json.toString();
//		}
	   return PactResult;
	}
	
	/***
	 * 理财账单详情 (OPT=237)
	 *
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-14
	 */
	public static String investBillInfo(Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String billInvestId = parameters.get("billInvestId");
		String bidId = parameters.get("bidId");
		if(!StrUtil.isNumeric(billInvestId)){
			 json.put("code", -1);
			 json.put("msg", "账单id非法");
			 
			 return json.toString();
		}
		if(!StrUtil.isNumeric(bidId)){
			 json.put("code", -1);
			 json.put("msg", "标id非法");
			 
			 return json.toString();
		}
		
		return accountAppService.findInvestBillRecord(Convert.strToLong(billInvestId,0),Convert.strToLong( bidId,0));
	}
	
	/**
	 * opt=238 借款账单详情
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月14日
	 */
	public static String findLoanBill(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signUserId = parameters.get("userId");
		String billIdSign = parameters.get("billIdSign");
		String bidIdSign = parameters.get("bidId");
		if (StringUtils.isBlank(signUserId)) {
			json.put("code", -1);
			json.put("msg", "参数userId不能为空");
			
			return json.toString();
		}
		if (StringUtils.isBlank(billIdSign)) {
			json.put("code", -1);
			json.put("msg", "参数billIdSign不能为空");
			
			return json.toString();
		}
		if (StringUtils.isBlank(bidIdSign)) {
			json.put("code", -1);
			json.put("msg", "参数bidId不能为空");
			
			return json.toString();
		}
		
		ResultInfo result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			
			return json.toString();
		}
		ResultInfo result2 = Security.decodeSign(billIdSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", -1);
			json.put("msg", result.msg);
			
			return json.toString();
		}
		ResultInfo result3 = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", -1);
			json.put("msg", result.msg);
			
			return json.toString();
		}
		long userId = Long.parseLong(result.obj.toString());
		long billId = Long.parseLong(result2.obj.toString());
		long bidId = Long.parseLong(result3.obj.toString());
		
		return loanAppService.findLoanBill(userId, billId, bidId);
	}
	
	/**
	 * 我的受让/我的转让(OPT=239)
	 *
	 * @param parameters
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月30日
	 */
	public static String pageOfDebt(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		String debtOfStr = parameters.get("debtOf");
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		
		if (!StrUtil.isNumeric(currentPageStr) || !StrUtil.isNumeric(pageNumStr)) {
			json.put("code", -1);
			json.put("msg", "分页参数不正确");

			return json.toString();
		}
		int currPage = Convert.strToInt(currentPageStr, 1);
		int pageSize = Convert.strToInt(pageNumStr, 5);
		
		int debtOf = -1;
		if (!StrUtil.isNumeric(debtOfStr)) {
			json.put("code", -1);
			json.put("msg", "债权类型有误");

			return json.toString();
		} else {
			debtOf = Convert.strToInt(debtOfStr, -1);
			if (debtOf != 1 && debtOf != 0) {
				json.put("code", -1);
				json.put("msg", "债权类型有误");

				return json.toString();
			}
		}
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		long userId = Long.valueOf(result.obj+"");
		
		PageBean<InOutDebt> page = null;
		if(debtOf == 0){
			page = debtAppService.pageOfAppDebtByUser(currPage, pageSize, null, userId);//0受让，1转让
		} else {
			page = debtAppService.pageOfAppDebtByUser(currPage, pageSize, userId, null);//0受让，1转让
		}
		
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(new String[] {// 只要设置这个数组，指定过滤哪些字段。
				"id"
		});
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("records", page.page);
		return JSONObject.fromObject(map,jsonConfig).toString();
	}

	/**
	 * 
	 * @Title: pageOfDebtNew
	 *
	 * @description 我的受让(根据标的状态分为已结束和未结束)-OPT=2390
	 *
	 * @param @param parameters
	 * @param @return 
	 * 
	 * @return String    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年1月3日
	 */
	public static String pageOfDebtNew(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		String signId = parameters.get("transactionUserId");
		String debtOfStr = parameters.get("debtOf");
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		
		if (!StrUtil.isNumeric(currentPageStr) || !StrUtil.isNumeric(pageNumStr)) {
			json.put("code", -1);
			json.put("msg", "分页参数不正确");

			return json.toString();
		}
		int currPage = Convert.strToInt(currentPageStr, 1);
		int pageSize = Convert.strToInt(pageNumStr, 5);
		
		int debtOf = -1;
		if (!StrUtil.isNumeric(debtOfStr)) {
			json.put("code", -1);
			json.put("msg", "债权类型有误");

			return json.toString();
		} else {
			debtOf = Convert.strToInt(debtOfStr, -1);
			if (debtOf != 1 && debtOf != 0) {
				json.put("code", -1);
				json.put("msg", "债权类型有误");

				return json.toString();
			}
		}
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		/** transactionUserId受让人Id */
		long transactionUserId = Long.valueOf(result.obj+"");
		PageBean<InOutDebt> page = null;
		if(debtOf == 0){
			//debtOf == 0我的受让(标已结束)
			page = debtAppService.pageOfAppDebtByUserNewEnd(currPage, pageSize,transactionUserId);
		} else {
			//debtOf == 1我的受让(标未结束)
			page = debtAppService.pageOfAppDebtByUserNewUnfinished(currPage, pageSize,transactionUserId);
		}
		
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(new String[] {// 只要设置这个数组，指定过滤哪些字段。
				"id"
		});
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("records", page.page);
		
		return JSONObject.fromObject(map,jsonConfig).toString();
	}
	

	/**
	 * 查看合同（OPT=2312）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年7月6日
	 */
	public static String showDebtPact(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		String debtIdSign = parameters.get("debtId");
		
		ResultInfo result = Security.decodeSign(debtIdSign, Constants.DEBT_TRANSFER_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);

			return json.toString();
		}
		
		long debtId = Long.parseLong((String)result.obj);
		
		t_pact pact = pactService.findByDebtId(debtId);
		if (pact == null) {
			json.put("code", -1);
			json.put("msg", "协议不存在");

			return json.toString();
		}
		json.put("html", pact.content);
		json.put("code", 1);
		json.put("msg", "加载成功");
		
		return json.toString();
	}
	
	/**
	 * 我的财富-资产管理-我的理财-进入债权申请页面(OPT=2313)
	 *
	 * @param parameters
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月30日
	 */
	public static String applyDebtPre(Map<String, String> parameters){
		
		JSONObject json = new JSONObject();
		String signId = parameters.get("investId");
		
		ResultInfo result = Security.decodeSign(signId, Constants.INVEST_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		
		long investId = Long.parseLong((String)result.obj);
		result = debtAppService.isInvestCanbeTransfered(investId);
		if (result.code < 1) {
			
			json.put("code", -1);
			json.put("msg", result.code);
			return json.toString();
		}
		
		DebtInvestBean debtInvest = debtAppService.findDebtInvestByInvestId(investId);
		if (debtInvest == null) {

			json.put("code", -1);
			json.put("msg", "债权不存在");
			return json.toString();
		}
		
		json.put("code", 1);
		json.put("msg", "投资剩余债权信息查询成功");
		json.put("investId", debtInvest.getSign());//投资id加密串
		json.put("debtAmount", debtInvest.debt_amount);//债权总额
		json.put("debtPrincipal", debtInvest.debt_principal);//待收本金
		json.put("debtInterest", debtInvest.debt_interest);//待收利息
		json.put("period", debtInvest.period);//待转让期数
		
		
		return json.toString();
	}
	
	/**
	 * 我的财富-资产管理-我的理财-债权申请(OPT=2314)
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public static String applyDebtTransfer(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String title = parameters.get("title");
		String periodStr = parameters.get("period");//转让时间
		int period = 0;
		String transferPriceStr = parameters.get("transferPrice");
		int transferPrice = 0;
		String investIdSign = parameters.get("investId");
		long investId = 0L;
		
		
		if (StringUtils.isBlank(title) || title.length() > 20 || title.length() < 2) {
			json.put("code", -2);
			json.put("msg", "转让标题长度为2~20位字符");
			return json.toString();
		}
		if (StringUtils.isBlank(periodStr)) {
			json.put("code", -2);
			json.put("msg", "转让有效期为1~7天");
			return json.toString();

		} else {
			period = Integer.parseInt(periodStr);
			if (period < 1 || period > 7) {
				json.put("code", -2);
				json.put("msg", "转让有效期为1~7天");
				return json.toString();
			}
		}
		
		if (StringUtils.isBlank(transferPriceStr)) {
			json.put("code", -2);
			json.put("msg", "转让价格不能为空");
			return json.toString();
		}
		transferPrice = Integer.parseInt(transferPriceStr);
		
		ResultInfo result = Security.decodeSign(investIdSign, Constants.INVEST_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		investId = Long.parseLong((String)result.obj);
		
		t_invest invest = investService.findByID(investId);
		t_bid bids = bidService.findByID(invest.bid_id);
		Date dates = new Date();
		
		//判断标是否在锁定期
		if(bids.lock_deadline != 0) {
			
			if(bids.lock_time.getTime()>dates.getTime()) {
				json.put("code", -2);
				json.put("msg", "标在锁定期，无法进行转让");
				return json.toString();
			}
		}
		
		result = debtAppService.applyDebtTransfer(investId, title, period, transferPrice);
		if (result.code < 1) {
			json.put("code", -1);
			json.put("msg", result.msg);
			return json.toString();
		}
		
		json.put("code", 1);
		json.put("msg", "债权转让申请成功，请等待审核结果");
		
		return json.toString();
	}
	
	/**
	 * 还款校验交易密码
	 * 
	 * @author niu
	 * @create 2017.09.23
	 */
	public static String repaymentCheckPass(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		//获取userId
		String userIdSign = parameters.get("userId");
		
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (userIdSignDecode.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			
			return json.toString();
		}
		
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		
		//获取设备类型
		String deviceType = parameters.get("deviceType");//设备类型
		int client = Integer.parseInt(deviceType);

		//验密请求参数
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.REPAYMENT);
		
		ResultInfo result = new ResultInfo();
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		if (userInfo == null) {
			json.put("code", -1);
			json.put("msg", "查询用户信息失败");
			json.put("flag", "");
			json.put("html", "");
		}
		
		result = Security.decodeSign(parameters.get("billIdSign"), Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 json.put("flag", "");
			 json.put("html", "");
			 
			 return json.toString();
		}
		
		long billId = Long.parseLong(result.obj.toString());
		
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			 json.put("code", -1);
			 json.put("msg", "服务器繁忙");
			 json.put("flag", "");
			 json.put("html", "");
			 
			 return json.toString();
		}
		
		t_bid tb = bidService.findByID(bill.bid_id);
		
		//服务费  借款金额 * 年化服务费率% / 12
		double serviceAmount = tb.amount * tb.service_charge/100/12;
		
		BigDecimal bg = new BigDecimal(serviceAmount);  
		serviceAmount = bg.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		
		double amount = bill.repayment_corpus + bill.repayment_interest + serviceAmount;
		
		result = PaymentProxy.getInstance().checkTradePass(client, businessSeqNo, userId, BaseController.getBaseURL() + "payment/yibincallback/repaymentCheckPass", userInfo.reality_name, ServiceType.REPAYMENT, amount, parameters, ServiceType.REPAYMENT_CHECK_TRADE_PASS, 2, "", "", "");
		json.put("code", result.code);
		json.put("msg", result.msg);
		json.put("flag", "1");
		json.put("html", result.obj.toString());
		
		return json.toString();
		
		
		
	}

	/**
	 * 
	 * @Title: showBidPactAll
	 * @description 合同下拉（OPT=2360）
	 * @param parameters
	 * @return
	 * @throws ParseException
	 * String
	 * @author likai
	 * @createDate 2018年12月26日 下午8:51:54
	 */
	public static String showBidPactAll(Map<String, String> parameters) throws ParseException {
		JSONObject json = new JSONObject();
		String bidIdSign = parameters.get("bidId");
		String time  = parameters.get("time");
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		ResultInfo result = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 
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
		
		long bidId = Long.parseLong((String)result.obj);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//正式环境需要把时间改为2018-12-14 18:00:00
		Date date = dateFormat.parse("2018-12-14 18:00:00");
		Date release_time = TimeUtil.strToDate(time);
		if (release_time.before(date)) {
			PageBean<LoanContract> page = pactService.PageFindLoanContract(bidId,currPage,pageSize);
			for (LoanContract loanContract : page.page) {
				loanContract.date = loanContract.time.getTime();
			}
			json.put("result", page.page);
		}else {
			PageBean<LoanContract> page = pactService.PageFindListLoanContract(bidId,currPage,pageSize);
			for (LoanContract loanContract : page.page) {
				loanContract.date = loanContract.time.getTime();
			}
			json.put("result", page.page);
		}
		json.put("code",1);
		json.put("msg", "加载成功");
		return json.toString();
	}
	/**
	 * 
	 * @Title: showBidPactSSQ
	 * 
	 * @description 移动端显示出借上上签合同
	 * @param @param parameters
	 * @throws ParseException
	 * @return String    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月16日-下午2:47:07
	 */
	public static String showBidPactSSQ(Map<String, String> parameters) throws ParseException {
		
		JSONObject json = new JSONObject();
		String bidIdSign = parameters.get("bidId");
		String time  = parameters.get("time");
		//登录用户id
		String user_Id = parameters.get("userId");
		//出借人id
		String uidSign = parameters.get("user_id");
		Long userId = null;
		ResultInfo result;
		ResultInfo uuuid;
		if (user_Id.length()>8) {
			result = Security.decodeSign(user_Id, Constants.USER_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_APP_KEY_DES);
			userId = Long.parseLong(result.obj.toString());
		
		}else {
			userId =Long.parseLong(user_Id);
		
		}
		result = Security.decodeSign(bidIdSign, Constants.BID_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_APP_KEY_DES);
		if (uidSign!=null) {
			uuuid = Security.decodeSign(uidSign, Constants.USER_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_APP_KEY_DES);
		}else{
			uuuid = null;
		}
		
		Long bidId = Long.parseLong(result.obj.toString());
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 
			 return json.toString();
		}
		
		String PactResult = null;
		//出借人用户id
		Long uuid = null;
		
		if (uuuid == null) {
			uuid = userId;
		}else{
			uuid = Long.parseLong(uuuid.obj.toString());
		}
		
		PactResult = ElectronicSealCtrl.previewContract(bidId, userId, time, uuid);
		
		if (PactResult == null) {
			//查看本地合同
			PactResult = accountAppService.findBidAndUserIdByPact(bidId,userId);
			
			return PactResult;	
		}
		
		json.put("PactResult", PactResult);
		//状态为1 新合同
		json.put("status", 1);
		json.put("code",1);
		json.put("msg", result.msg);
		
		return json.toString();	

	}
	/***
	 * 
	 * @Title: showDebtPactSSQ
	 * 
	 * @description 我的转让我的受让查看电子签章合同接口 OTP-2362
	 * @param parameters
	 * @throws ParseException
	 * @return String    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月23日-下午2:56:29
	 */
	public static String showDebtPactSSQ(Map<String, String> parameters) throws ParseException {
		JSONObject json = new JSONObject();
		String debtIdSign = parameters.get("debtId");
		//登录用户id
		String user_Id = parameters.get("userId");
		Long userId = null;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
		ResultInfo result;
		
		if (user_Id.length()>8) {
			result = Security.decodeSign(user_Id, Constants.USER_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_APP_KEY_DES);
			userId = Long.parseLong(result.obj.toString());
			
		}else {
			userId =Long.parseLong(user_Id);
		}
		result = Security.decodeSign(debtIdSign, Constants.DEBT_TRANSFER_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_APP_KEY_DES);
		
		Long debtId = Long.parseLong(result.obj.toString());
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 
			 return json.toString();
		}
		String PactResult = null;
		PactResult = ElectronicSealCtrl.previewDebtContract(debtId, userId);
		if (PactResult == null) {
			//协议为空时,查看本地协议
			t_pact pact = pactService.findByDebtId(debtId);
			json.put("html", pact.content);
			json.put("status", 0);
			json.put("code",1);
			json.put("msg", result.msg);
			return json.toString();	
		}
		json.put("PactResult", PactResult);
		json.put("status", 1);
		json.put("code",1);
		json.put("msg", result.msg);
		
		return json.toString();	
	}
	
}
