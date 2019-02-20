package controllers.front.account;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.lang.StringUtils;

import models.app.bean.ReturnMoneyPlanBean;
import models.common.bean.CurrUser;
import models.common.bean.UserFundInfo;
import models.common.bean.UserMessage;
import models.common.entity.t_bank_card_user;
import models.common.entity.t_bank_quota;
import models.common.entity.t_cost;
import models.common.entity.t_notice_setting_user;
import models.common.entity.t_ssq_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.common.entity.t_user_vip_grade;
import models.core.bean.InvestReceive;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import models.ext.redpacket.bean.AddRateTicketBean;
import net.sf.json.JSONObject;
import payment.impl.PaymentProxy;
import play.db.jpa.JPA;
import play.mvc.With;
import services.common.BankCardUserService;
import services.common.BankQuotaService;
import services.common.CreditLevelService;
import services.common.NoticeService;
import services.common.RechargeUserService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.common.UserVIPGradeService;
import services.common.WithdrawalUserService;
import services.common.ssqUserService;
import services.core.BillInvestService;
import services.core.InvestService;
import services.ext.redpacket.AddRateTicketService;
import yb.YbConsts;
import yb.YbPayment;
import yb.YbUtils;
import yb.enums.ServiceType;

import com.alibaba.fastjson.JSON;
import com.shove.Convert;

import common.FeeCalculateUtil;
import common.annotation.PaymentAccountCheck;
import common.annotation.RealNameCheck;
import common.annotation.SimulatedCheck;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.SettingKey;
import common.enums.Client;
import common.enums.NoticeScene;
import common.enums.NoticeType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.file.FileUtil;
import controllers.common.FrontBaseController;
import controllers.common.SubmitRepeat;
import controllers.common.interceptor.AccountInterceptor;
import controllers.common.interceptor.SimulatedInterceptor;
import controllers.front.FrontHomeCtrl;
import controllers.front.LoginAndRegisteCtrl;
import controllers.front.seal.ElectronicSealCtrl;

/**
 * 前台-账户中心-账户首页控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With({AccountInterceptor.class, SubmitRepeat.class,SimulatedInterceptor.class})
public class MyAccountCtrl extends FrontBaseController {
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);
	
	protected static CreditLevelService creditLevelService = Factory.getService(CreditLevelService.class);
	
	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);
	
	protected static WithdrawalUserService withdrawalUserService = Factory.getService(WithdrawalUserService.class);
	
	protected static BankQuotaService bankQuotaService = Factory.getService(BankQuotaService.class);
	
	protected static YbPayment ybPayment = Factory.getSimpleService(YbPayment.class); 
	
	protected static RechargeUserService rechargeUserService = Factory.getService(RechargeUserService.class);
	
	protected static ssqUserService ssquserService = Factory.getService(ssqUserService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected static AddRateTicketService addRateTicketService = Factory.getService(AddRateTicketService.class);
	
	protected static UserVIPGradeService userVIPGradeService = Factory.getService(UserVIPGradeService.class);
	/**
	 * 我的财富-账户首页
	 * @description 
	 *
	 * @author LiuPengwei
	 * @createDate 2018年3月5日
	 */
	public static void homesPre(){
		
		long userId = getCurrUser().id;		
		
		t_user_info userInfo = userInfoService.findByColumn("user_id = ?", userId);
		if (userInfo.credit_id == 0) {
			
			FrontHomeCtrl.risqueEvaluationPre();
		}
		
		//业务流水号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_BINDED_BANK);	
		
		//实时查询开户信息
		PaymentProxy.getInstance().queryBindedBankCard(Client.PC.code, userId, businessSeqNo,ServiceType.PERSON_CUSTOMER_REGIST.code);
				
		homePre();
	}
	
	
	/**
	 * 进入我的财富页面
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月6日14:14:44
	 */
	public static void homePre(){
		/* 账户首页页面标记 */
		boolean isHome = true;
		
		//登录我的财富,上上签自动注册
		electronicSeal();
		
		render(isHome);
	}
	
	/**
	 * 我的财富-账户首页-回款计划
	 * @param currPage
	 * @param pageSize
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月21日
	 *
	 */
	public static void listOfNoReceiveBillPre(int currPage,int pageSize){
		if (pageSize < 1) {
			pageSize = 5;
		}
		
		long userId = getCurrUser().id;
		PageBean<InvestReceive> investReceive = billInvestService.pageOfInvestReceive(currPage, pageSize, userId);
		
		if(investReceive.page == null) {
			render(investReceive);
		}
		
		//判断标是否使用了加息卷
		for (InvestReceive invest : investReceive.page) {
			
			t_invest invest1 = investService.findByID(invest.invest_id);
			
			AddRateTicketBean couponUser = addRateTicketService.findTicketByUserId(invest.invest_id, userId, 3);
			if (couponUser ==null) {
				//invest.coupon_interest = invest1.correct_interest;
				invest.coupon_interest = invest.receive_interest;
			}else {
				//invest.coupon_interest = invest1.correct_interest + couponUser.apr *invest1.amount /1200 ;
				invest.coupon_interest = invest.receive_interest + couponUser.apr *invest1.amount /1200 ;
			}
		}
		
		render(investReceive);
	}
	
	/**
	 * 我的财富-头部资产信息
	 * 
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月20日
	 *
	 */
	public static void userHeadFundInfo(){
		long userId = getCurrUser().id;
		UserFundInfo userFundInfo = userService.findUserFundInfo(userId);
		
		render(userFundInfo);
	}
	
	/**
	 * 进入消息页面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月8日
	 */
	public static void showNoticePre() {
		
		render();
	}
	
	/**
	 * 前台-账户中心-账户首页-消息-消息
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	public static void listOfNoticsPre(int currPage,int pageSize) {
		long userId = getCurrUser().id;
		
		PageBean<UserMessage> page = noticeService.pageOfAllUserMessages(currPage,pageSize,userId);
		
		int unreadMsgCount = noticeService.countUnreadMsg(userId);//未读消息总数
		render(page, unreadMsgCount);
	}
	
	/**
	 * 标记用户的某个站内信为已读
	 *
	 * @param msgId
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	public static void editUserMsgStatus(String sign){
		ResultInfo result = Security.decodeSign(sign, Constants.MSG_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error();
		}
		
		long msgId = Long.parseLong(result.obj.toString());
		long userId = getCurrUser().id;
		
		boolean isUpdated = noticeService.updateUserMsgStatus(userId, msgId);
		if (!isUpdated) {
			result.code = -1;
			result.msg = "没有更新成功!";
			
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "该条信息已读状态已更新!";
		
		renderJSON(result);
	}
	
	/**
	 * 删除某条站内信
	 *
	 * @param sign
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月18日
	 */
	@SimulatedCheck
	public static void delUserMsg(String sign) {
		ResultInfo result = Security.decodeSign(sign, Constants.MSG_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error();
		}
		
		long msgId = Long.parseLong(result.obj.toString());
		long userId = getCurrUser().id;
		
		result = noticeService.deleteUserMsg(userId, msgId);
		if (result.code < 1) {
			LoggerUtil.error(true, "消息删除失败:【%s】", msgId+"");
			
			renderJSON(result);
		}
		
		renderJSON(result);
	}
	
	
	/**
	 * 前台-账户中心-账户首页-消息-消息设置
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	public static void showUserNoticSettingsPre() {
		
		Long user_id = getCurrUser().id;		
		
		List<t_notice_setting_user> noticSettings = noticeService.queryAllNoticSettingsByUser(user_id);
		
		render(noticSettings);
	}
	
	/**
	 * 前台-账户中心-账户首页-消息-消息设置-更改用户的消息接收设置
	 *
	 * @param sceneCode
	 * @param type
	 * @param flag
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月18日
	 */
	public static void editNoticeSetting(String sceneCode, String noticeType, boolean noticeflag) {
		ResultInfo result = new ResultInfo();
		NoticeScene scene =  NoticeScene.getEnum(Convert.strToInt(sceneCode, -1));
		if (scene == null) {
			result.code = -1;
			result.msg = "你输入的含有非法的参数";

			renderJSON(result);
		}
		
		NoticeType typeOfNotice = NoticeType.getEnum(Convert.strToInt(noticeType, -1));
		if (typeOfNotice == null) {
			result.code = -1;
			result.msg = "你输入的含有非法的参数";

			renderJSON(result);
		}
		
		//获取当前登录用户的id
		Long user_id =getCurrUser().id;		
		
		boolean flagOf = noticeService.saveOrUpdateUserNoticeSetting(user_id, scene, typeOfNotice, noticeflag);
		if ( !flagOf ) {
			result.code = -2;
			result.msg = "设置失败，请稍后再试";
			
			renderJSON(result);
		} else {
			result.code = 1;
			result.msg = scene.value+"设置成功!";
			
			renderJSON(result);
		}
	}
	
	/**
	 * 前台充值页面
	 * 
	 *
	 * @author LiuPengwei
	 * @createDate 2017年10月09日
	 *
	 */
	@PaymentAccountCheck
	@RealNameCheck
	@SubmitCheck
	public static void rechargePre(){
		CurrUser currUser = getCurrUser();
		
		/* 用户Id */
		long userId = getCurrUser().id;
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if(userInfo == null) {
			LoginAndRegisteCtrl.loginPre();
		}
		renderArgs.put("userInfo", userInfo);
		
		if(userInfo.enterprise_name == null || userInfo.enterprise_name.equals("")) {
			List<t_bank_card_user> banks = bankCardUserService.queryCardByUserId(currUser.id);
			
			// 未绑卡
			if (banks == null || banks.size()<=0) {
				redirect("front.account.MyAccountCtrl.bankCardBindGuidePre");
			}
			
			/* 最高充值金额 */
			int rechargeHighest = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MAX), 0);
			
			/* 最低充值金额 */
			int rechargeLowest = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MIN), 0);
		
			renderArgs.put("bankList", banks);
			
			render(rechargeHighest, rechargeLowest);
		}else {
			render();
		}
		
	}
	
	/**
	 * 查询银行充值限额
	 * 
	 *
	 * @author LiuPengwei
	 * @createDate 2017年11月04日
	 *
	 */
	@PaymentAccountCheck
	@RealNameCheck
	@SubmitCheck
	public static void rechargeBankQuota(String bankname){
		JSONObject json = new JSONObject();
		t_bank_quota bank_quota = bankQuotaService.findByColumn("bank_name = ?", bankname);
		json.put("single_quota", bank_quota.single_quota);
		json.put("day_quota", bank_quota.day_quota);
		
		renderJSON(json);
	}
	
	
	/**
	 * 用户充值
	 *
	 * @param rechargeAmt 金额
	 * @param bankNo 银行卡号
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	@PaymentAccountCheck
	@RealNameCheck
	@SimulatedCheck
	public static void recharge(double rechargeAmt, long bankId, int rrecharge, int rrecharges){

		CurrUser currUser = getCurrUser();
		
		/* 用户Id */
		long userId = getCurrUser().id;
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		if(userInfo == null) {
			LoginAndRegisteCtrl.loginPre();
		}
		
		if(userInfo.enterprise_name == null || userInfo.enterprise_name.equals("")) {
			List<t_bank_card_user> banks = bankCardUserService.queryCardByUserId(currUser.id);
			// 未绑卡
			if (banks == null || banks.size()<=0) {
				redirect("front.account.MyAccountCtrl.bankCardBindGuidePre");
			}
		}
		
		/*银行卡信息*/
		t_bank_card_user bankCardUser = bankCardUserService.findByColumn("id =?", bankId);
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.FORGET_PASSWORD);
		
		if(rrecharge == 1) {
			
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
			
			int day_quota= bank_quota.day_quota;
			
			double sumDayRecharge =rechargeAmt + totalRechargeUser;
			
			if (sumDayRecharge > day_quota) {
				
				flash.error("当天累计充值金额超过该银行每日限额");
				rechargePre();
			}
			
			
			//保存充值记录
			ResultInfo result = userFundService.rechargeS(userId, businessSeqNo, rechargeAmt, bankCardUser.bank_name, bankCardUser.account, "账户直充", Client.PC);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				
				rechargePre();
			}
			//先校验交易密码才充值
			if (ConfConst.IS_TRUST) {
				 result = PaymentProxy.getInstance().rechargeCheckPassword(Client.PC.code, businessSeqNo, userId, rechargeAmt, bankCardUser.account);
				 
				if (result.code < 1) {
					LoggerUtil.info(true, result.msg);
					flash.error(result.msg);
					
					rechargePre();
				}
				return ;
			}
			
			flash.error("充值成功");
			homePre();
		}else {
			//保存充值记录
			ResultInfo result = userFundService.recharge(userId, businessSeqNo, rechargeAmt, "账户网银充值", Client.PC);
			
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				
				rechargePre();
			}

			int flags = rrecharges;
			
			//先校验交易密码才网银充值
			if (ConfConst.IS_TRUST) {
				 result = PaymentProxy.getInstance().ebankRechargeCheckPassword(Client.PC.code, businessSeqNo, userId, rechargeAmt, flags);
				
				if (result.code < 1) {
					LoggerUtil.info(true, result.msg);
					flash.error(result.msg);
					
					rechargePre();
				}
				return ;
			}
			
			flash.error("充值成功");
			homePre();
		}
		
		
	}
	
	/**
	 * 前台提现页面
	 * 
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月14日
	 *
	 */
	@PaymentAccountCheck
	@RealNameCheck
	@SubmitCheck
	public static void withdrawPre(){
		CurrUser currUser = getCurrUser();

		List<t_bank_card_user> banks = bankCardUserService.queryCardByUserId(currUser.id);
		t_user_info userInfo = userInfoService.findUserInfoByUserId(currUser.id);
		String enterprise_bank_no = userInfo.enterprise_bank_no;
		
		// 未绑卡
		if (userInfo.enterprise_bank_no==null && (banks == null || banks.size()<=0)) {
			redirect("front.account.MyAccountCtrl.bankCardBindGuidePre");
		}
				
		
		List<t_bank_card_user> bankList = bankCardUserService.queryCardByUserId(currUser.id);
		renderArgs.put("bankList", bankList);
		
		t_user_fund userFundInfo = userFundService.queryUserFundByUserId(currUser.id);
		
		/* 提现手续费起点 */
		String withdrawFeePoint = settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_POINT);

		/* 提现手续费率 */
		String withdrawFeeRate = settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_RATE);
		
		/* 最低提现手续费 */
		String withdrawFeeMin = settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_MIN);
		
		/* 最高提现金额 */
		int withdrawHighest = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.WITHDRAW_AMOUNT_MAX), 0);
		
		/* 最低提现金额 */
		int withdrawLowest = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.WITHDRAW_AMOUNT_MIN), 0);
		
		/* 可用余额 */
		double balance = userFundInfo.balance;
		
		double withdrawMaxRate = Constants.WITHDRAW_MAXRATE;
		
		
		render(bankList, balance ,withdrawMaxRate , withdrawFeePoint, withdrawFeeRate, withdrawFeeMin, enterprise_bank_no,userInfo, withdrawHighest, withdrawLowest);
	}
	
	
	/**
	 * 用户提现
	 *
	 * @param userSign 用户签名
	 * @param withdrawalAmt 提现金额
	 * @param bankAccount 银行卡号
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月9日
	 */
	@SubmitOnly
	@PaymentAccountCheck
	@RealNameCheck
	@SimulatedCheck
	public static void withdrawal(double withdrawalAmt, String bankAccount){
		checkAuthenticity();
		
		CurrUser currUser = getCurrUser();
		
		List<t_bank_card_user> banks = bankCardUserService.queryCardByUserId(currUser.id);
		// 未绑卡
		if (banks == null || banks.size()<=0) {
			redirect("front.account.MyAccountCtrl.bankCardBindGuidePre");
		}
		//业务订单号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.CUSTOMER_WITHDRAW);
		
		ResultInfo result = userFundService.withdrawal(currUser.id, businessSeqNo, withdrawalAmt, bankAccount, Client.PC);
	
		
		if (ConfConst.IS_TRUST) {
			
			//提现手续费
			double withdrawalFee = FeeCalculateUtil.getWithdrawalFee(withdrawalAmt);
			
			result = PaymentProxy.getInstance().withdrawalCheckPassword(Client.PC.code, businessSeqNo, currUser.id,
					withdrawalAmt, bankAccount, ServiceType.CUSTOMER_WITHDRAW, withdrawalFee);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);

				withdrawPre();
			}
			
			// 查询费用账户可用余额
			String businessSeqNo3 = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);
			
			ResultInfo result3 = PaymentProxy.getInstance().queryCostBalance(Client.PC.code, businessSeqNo3);
			if (result3.code < 1) {
				flash.error("查询费用账户可用余额异常");
				withdrawPre();
			}
			
			Map<String, Object> merDetail3 = (Map<String, Object>) result3.obj;
			double cosBalance = Convert.strToDouble(merDetail3.get("pBlance").toString(), 0);
			
			// 保存费用账户明细
			t_cost cost = new t_cost();
			cost.balance = cosBalance;
			cost.amount = withdrawalFee;
			cost.type = 0;
			cost.sort = 2;
			cost.time = new Date();
			cost.save();
			
			return;
		}
		
		flash.error("提现成功");
		
		homePre();
	}

	/**
	 * 企业提现
	 *
	 * @param withdrawalAmt 提现金额
	 * @param bankAccount 银行卡号
	 *
	 * @author LiuPengwei
	 * @createDate 2017年10月15日
	 */
	@SubmitOnly
	@PaymentAccountCheck
	@RealNameCheck
	@SimulatedCheck
	public static void enterpriseWithdrawal(double withdrawalAmt, String bankAccount){
		
		checkAuthenticity();
		
		CurrUser currUser = getCurrUser();
		
		List<t_bank_card_user> banks = bankCardUserService.queryCardByUserId(currUser.id);
		t_user_info userInfo = userInfoService.findUserInfoByUserId(currUser.id);
		// 未绑卡
		if (userInfo.enterprise_bank_no==null && banks == null) {
			redirect("front.account.MyAccountCtrl.bankCardBindGuidePre");
		}
		
		//业务订单号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.CUSTOMER_WITHDRAW);
		
		ResultInfo result = userFundService.withdrawal(currUser.id, businessSeqNo, withdrawalAmt, bankAccount, Client.PC);
	

		if (ConfConst.IS_TRUST) {
			//提现手续费
			double withdrawalFee = FeeCalculateUtil.getWithdrawalFee(withdrawalAmt);
			
			result = PaymentProxy.getInstance().withdrawalCheckPassword(Client.PC.code,businessSeqNo, currUser.id, withdrawalAmt,
					bankAccount, ServiceType.ENTERPRISE_WITHDRAW, withdrawalFee);
			
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);

				withdrawPre();
			}
			
			return;
		}
		
		
		
		flash.error("提现成功");
		
		homePre();
	}


	/**
	 * 上传用户头像
	 * @description 
	 *
	 * @param photoUrl 图片路径
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月25日
	 */
	@SimulatedCheck
	public static void updatePhoto(File imgFile, String fileName){
		
		response.contentType="text/html";
		ResultInfo result = new ResultInfo();
		if (imgFile == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";
			
			renderJSON(result);
		}
		if(StringUtils.isBlank(fileName) || fileName.length() > 32){
			result.code = -1;
			result.msg = "图片名称长度应该位于1~32位之间";
			
			renderJSON(result);
		}
		
		result = FileUtil.uploadImgags(imgFile);
		if (result.code < 0) {

			renderJSON(result);
		}
		
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		fileInfo.put("imgName", fileName);
		String filename = (String) fileInfo.get("staticFileName");
		
		long userId = getCurrUser().id;
		
		result = userService.updateUserPhoto(userId, filename);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		
		userService.flushUserCache(userId);
		result.obj = filename;
		
		renderJSON(result);
	}

	/**
	 * 统计用户的未读消息数
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月27日
	 */
	public static void getNoticeNum(){
		CurrUser user = getCurrUser();
		int num = noticeService.countUserUnreadMSGs(user.id);
		
		ResultInfo result = new ResultInfo();
		result.code = 1;
		result.msg = "查询成功";
		result.obj = num;
		
		renderJSON(result);
	}
	
	/**
	 * 资金托管开户引导页
	 * @author Chenzhipeng
	 * @createDate 2016年1月30日
	 *
	 */
	public static void paymentAccountGuidePre(){
		
		render();
	}
	
	/**
	 * 模拟登录不能执行数据库信息修改操作
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年5月31日
	 */
	public static void simulatedLoginPre(){
		
		render();
	}
	
	/**
	 * 银行卡绑定引导页
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月30日
	 *
	 */
	public static void bankCardBindGuidePre(){
		
		render();
	}
	
	/**
	 * 实名认证引导页
	 * @author Chenzhipeng
	 * @createDate 2016年1月30日
	 *
	 */
	public static void realNameGuidePre(){
		
		render();
	}
	
	
	
	
	/**
	 * 将站内信标记为已读
	 *
	 * @author songjia
	 * @createDate 2016年2月18日
	 */
	public static void markMsgAsRead() {
		ResultInfo result = new ResultInfo();
		
		long userId = getCurrUser().id;
		int row = noticeService.updateUserAllMsgStatus(userId);
		if(row == ResultInfo.ERROR_SQL) {
			result.code = ResultInfo.ERROR_SQL;
			result.msg = "标记站内信为已读不成功!";
			
			renderJSON(result);
		}
		result.code = 1;
		result.msg = "信息已全部标记为已读!";
		
		renderJSON(result);
	}
	
	/**
	 * 汇付登录
	 */
	public static void loginHfPre(){
		long userId = getCurrUser().id;
		PaymentProxy.getInstance().login(Client.PC.code, userId, null);
	}
	
	
	/**
	 * 上上签用户注册
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月6日16:06:08
	 */
	public static ResultInfo electronicSeal (){
		ResultInfo result = new ResultInfo();
		
		String taskId = null;
		//登录用户完成上上签注册
		long userId = getCurrUser().id;
		t_user_info userInfo = userInfoService.findByColumn("user_id = ?", userId);
		t_user_fund userFund = userFundService.findByColumn("user_id = ?", userId);
		//查询用户是否注册上上签成功
		t_ssq_user ssqUser = ssquserService.findssqByUserId(userId);
		//开户成功，实名认证，完成上上签用户自动注册
		
		if (userFund.payment_account != null && !(userFund.payment_account.equals("")) && ssqUser == null ) {
			
			//企业用户注册
			if (userInfo.enterprise_credit != null && !(userFund.payment_account.equals(""))){
				
				taskId = ElectronicSealCtrl.EnterpriseUserReg(userInfo);
				
			} else {
				
				//个人用户注册
				taskId = ElectronicSealCtrl.personalUserReg(userInfo);
			}
		}
				
		return result;
	}
	
	/**
	 * 用户vip设置
	 * @author guoShiJie
	 * @createDate 2018.11.7
	 * */
	public static void getUserVip () {
		
		Long userId = getCurrUser().id;
		t_user_vip_grade vip = userVIPGradeService.findByColumn(" id = ( select vip_grade_id from t_user_info where user_id = ? ) ", userId);
		String vipJson = JSON.toJSONString(vip);
		renderJSON(vipJson);
	}
}

