package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import models.activity.shake.entity.t_user_gold;
import models.app.bean.BillInvestInfo;
import models.app.bean.BillInvestListBean;
import models.app.bean.DealRecordsBean;
import models.app.bean.MyInvestRecordBean;
import models.app.bean.MyLoanRecordBean;
import models.app.bean.RechargeBean;
import models.app.bean.ReturnMoneyPlanBean;
import models.app.bean.UserBankCard;
import models.app.bean.WithdrawBean;
import models.common.bean.UserFundInfo;
import models.common.entity.t_bank_card_user;
import models.common.entity.t_bank_quota;
import models.common.entity.t_pact;
import models.common.entity.t_template_notice;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.bean.InvestReceive;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import models.ext.cps.entity.t_cps_activity;
import models.ext.cps.entity.t_cps_user;
import models.ext.redpacket.bean.AddRateTicketBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import play.Logger;
import play.cache.Cache;
import play.mvc.Scope.Session;
import services.activity.shake.UserGoldService;
import services.common.BankQuotaService;
import services.common.PactService;
import services.common.SendCodeRecordsService;
import services.common.SettingService;
import services.common.SmsJyCountService;
import services.common.UserFundService;
import services.common.UserService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.InvestService;
import services.ext.cps.CpsActivityService;
import services.ext.redpacket.AddRateTicketService;
import yb.YbPaymentCallBackService;
import yb.YbUtils;
import yb.enums.ServiceType;
import yb.enums.YbPayType;

import com.shove.Convert;
import com.shove.security.Encrypt;

import common.FeeCalculateUtil;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.ModuleConst;
import common.constants.SettingKey;
import common.enums.AnnualIncome;
import common.enums.Car;
import common.enums.Client;
import common.enums.Education;
import common.enums.House;
import common.enums.JYSMSModel;
import common.enums.Marital;
import common.enums.NetAssets;
import common.enums.NoticeScene;
import common.enums.Relationship;
import common.enums.WorkExperience;
import common.ext.cps.utils.RewardGrantUtils;
import common.utils.EnumUtil;
import common.utils.Factory;
import common.utils.JYSMSUtil;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.SMSUtil;
import common.utils.Security;
import controllers.back.activity.shake.UserGoldCtrl;
import controllers.common.BaseController;
import controllers.front.account.MyAccountCtrl;
import dao.AccountAppDao;
import daos.ext.cps.CpsUserDao;

public class AccountAppService extends UserService {

	private static SettingService settingService = Factory.getService(SettingService.class);
	private static SendCodeRecordsService sendCodeRecordsService = Factory.getService(SendCodeRecordsService.class);
	private static UserFundService userFundService = Factory.getService(UserFundService.class);
	private static PactService pactService = Factory.getService(PactService.class);
	private static BillService billService = Factory.getService(BillService.class);
	private static YbPaymentCallBackService ybPaymentCallBackService = Factory
			.getSimpleService(YbPaymentCallBackService.class);
	private static DebtAppService debtAppService = Factory.getService(DebtAppService.class);
	private static UserGoldService userGoldService = Factory.getService(UserGoldService.class);
	protected static BankQuotaService bankQuotaService = Factory.getService(BankQuotaService.class);
	protected static CpsUserDao cpsUserDao = Factory.getDao(CpsUserDao.class);
	protected static CpsActivityService cpsActivityService = Factory.getService(CpsActivityService.class);
	protected static SmsJyCountService smsJyCountService = Factory.getService(SmsJyCountService.class);
	protected static AddRateTicketService addRateTicketService = Factory.getService(AddRateTicketService.class);

	protected static InvestService investService = Factory.getService(InvestService.class);

	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);

	private static AccountAppDao accountDao;

	private AccountAppService() {
		accountDao = Factory.getDao(AccountAppDao.class);
		super.basedao = accountDao;
	}

	/**
	 * 用户登录（OPT=123）
	 *
	 * @param mobile
	 *            手机号
	 * @param password
	 *            密码
	 * @param deviceType
	 *            设备类型
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public String logining(String mobile, String password, String deviceType) {
		JSONObject json = new JSONObject();
		ResultInfo result = new ResultInfo();

		result = super.findUserInfoByMobile(mobile);
		t_user user = (t_user) result.obj;
		if (user == null) {
			json.put("code", -1);
			json.put("msg", "该用户不存在!");

			return json.toString();
		}

		int securityLockTimes = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.SECURITY_LOCK_TIMES),
				3);
		int securityLockTime = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.SECURITY_LOCK_TIME),
				30);
		String pwdEncrypt = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		result = super.logining(mobile, pwdEncrypt, Client.getEnum(Integer.valueOf(deviceType)), BaseController.getIp(),
				securityLockTimes, securityLockTime);

		if (result.code < 1) {
			if (result.code == ResultInfo.ERROR_SQL) {
				LoggerUtil.info(true, result.msg);
			}

			json.put("code", -1);
			json.put("msg", result.msg);

			return json.toString();
		}

		result = MyAccountCtrl.electronicSeal();

		json.put("code", 1);
		json.put("msg", "登录成功");
		json.put("userId", user.appSign);
		/** 通过登陆人mobile获取登陆人id */
		t_user userResult = super.findUserMobile(mobile);
		/** 判断登陆人信息是否在t_user_gold表中存在，没有则添加 */
		t_user_gold userGold = userGoldService.getByUserId(userResult.id);
		
		/** 如果表中没有登陆人信息则在t_user_gold表中添加 */
		if (userGold == null) {
			UserGoldCtrl.saveUserInfo(userResult.id);
		}
		
		return json.toString();
	}

	/**
	 * 修改登录密码（OPT=122）
	 * 
	 * @param userId
	 *            用户id
	 * @param mobile
	 *            手机号
	 * @param newPassword
	 *            新密码
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public String updateUserPwd(String mobile, long userId, String newPassword) {
		JSONObject json = new JSONObject();

		ResultInfo result = super.updatePassword(userId, newPassword);
		if (result.code < 1) {
			json.put("code", -1);
			json.put("msg", result.msg);

			return json.toString();
		}

		json.put("code", 1);
		json.put("msg", "更改成功!");

		return json.toString();
	}

	/***
	 * 
	 * 发送验证码（OPT=111）
	 * 
	 * @param mobile
	 *            手机号
	 * @param scene
	 *            场景
	 * @return
	 * 
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public String sendCode(String mobile, String scene) {

		JSONObject json = new JSONObject();

		// 创蓝短信发送
		/* 查询短信验证码模板 */
		/*
		 * List<t_template_notice> noticeList =
		 * noticeService.queryTemplatesByScene(NoticeScene.SECRITY_CODE);
		 * 
		 * if(noticeList == null || noticeList.size() < 1){ json.put("code",
		 * -2); json.put("msg", "短信发送失败");
		 * 
		 * return json.toString(); } String content = noticeList.get(0).content;
		 * String smsAccount =
		 * settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_ACCOUNT);
		 * String smsPassword =
		 * settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_PASSWORD)
		 * ;
		 * 
		 * 发送短信验证码 SMSUtil.sendCode(smsAccount, smsPassword, mobile, content,
		 * Constants.CACHE_TIME_MINUS_30, scene,ConfConst.IS_CHECK_MSG_CODE);
		 */

		// 焦云短信发送
		if (!smsJyCountService.judgeIsSend(mobile, JYSMSModel.MODEL_SEND_CODE)) {
			json.put("code", -2);
			json.put("msg", "短信验证码未发送");
			return json.toString();
		}
		Boolean flag = JYSMSUtil.sendCode(mobile, JYSMSModel.MODEL_SEND_CODE.tplId, Constants.CACHE_TIME_MINUS_30,
				ConfConst.IS_CHECK_MSG_CODE);
		smsJyCountService.updateCount(mobile, JYSMSModel.MODEL_SEND_CODE, flag);
		/* 添加一条短信发送控制记录 */
		sendCodeRecordsService.addSendCodeRecords(mobile, BaseController.getIp());

		json.put("code", 1);
		json.put("msg", "短信验证码发送成功");

		return json.toString();
	}

	/**
	 * 验证验证码（OPT=121）
	 *
	 * @param verificationCode
	 *            校验码
	 * @param mobile
	 *            手机号
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public String verificationCode(String verificationCode, String mobile, String scene) {
		JSONObject json = new JSONObject();

		Object obj = Cache.get(mobile + JYSMSModel.MODEL_SEND_CODE.tplId);
		if (obj == null) {
			json.put("code", -1);
			json.put("msg", "短信验证码已失效!");

			return json.toString();
		}
		String codeStr = obj.toString();
		/** 短信验证码 验证 */
		if (ConfConst.IS_CHECK_MSG_CODE) {
			if (!codeStr.equals(verificationCode)) {
				json.put("code", -1);
				json.put("msg", "短信验证码错误!");

				return json.toString();
			}
		}

		json.put("code", 1);
		json.put("msg", "验证成功!");

		return json.toString();
	}

	/**
	 * 查询提现记录（OPT=215）
	 *
	 * @param userId
	 *            用户id
	 * @param currPage
	 *            当前请求页
	 * @param pageNum
	 *            页大小
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public String pageOfWithdrawRecord(long userId, int currPage, int pageNum) {

		PageBean<WithdrawBean> page = accountDao.pageOfWithdrawRecord(userId, currPage, pageNum);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("records", page.page);

		return JSONObject.fromObject(map).toString();
	}

	/***
	 * 
	 * 个人基本信息(OPT=251)
	 * 
	 * @return
	 * @param userId
	 *            用户Id
	 * @param
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public String findUserInfomation(long userId) {
		JSONObject json = new JSONObject();

		/** 获取用户基本信息 **/
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);

		/** 获取用户资产信息 **/
		UserFundInfo userFundInfo = super.findUserFundInfo(userId);

		/** 获取是否有未读信息 */
		boolean hasUserUnreadMsg = accountDao.hasUserUnreadMsg(userId);

		json.put("name", userInfo.name);
		json.put("photo", userInfo.photo);
		json.put("totalIncome", userFundInfo.total_income);// 总收益
		json.put("totalAssets", userFundInfo.total_assets);// 总资产
		json.put("balance", userFundInfo.balance);// 可用余额
		json.put("no_receive", userFundInfo.no_receive);//待收金额
		json.put("hasUserUnreadMsg", hasUserUnreadMsg);
		json.put("grade", userInfo.credit_id);// 风险评估等级
		json.put("code", 1);
		json.put("msg", "查询成功!");

		return json.toString();
	}

	/****
	 * 充值记录接口（OPT=212）
	 * 
	 * @param userId
	 *            用户id
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public String pageOfRechargeRecord(long userId, int currPage, int pageSize) throws Exception {

		PageBean<RechargeBean> rechargePage = accountDao.pageOfRechargeRecord(userId, currPage, pageSize);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("records", rechargePage.page);

		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 消息列表（OPT=252）
	 *
	 * @param userId
	 *            用户id
	 * @param curr
	 * @param page
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月1日
	 */
	public String pageOfUserMessage(long userId, int currPage, int pageSize) {

		PageBean<Map<String, Object>> rechargePage = accountDao.pageOfUserMessage(userId, currPage, pageSize);

		// 更新消息为已读
		accountDao.editUserMsgStatus(userId, currPage, pageSize);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("records", rechargePage.page);

		return JSONObject.fromObject(map).toString();
	}

	/****
	 * 充值准备（opt=216）
	 *
	 * @return
	 * @description
	 *
	 * @author LiuPengwei
	 * @throws Exception
	 * @createDate 2016-4-12
	 */
	public static String rechargePre(long userId) throws Exception {
		JSONObject json = new JSONObject();

		List<UserBankCard> userBankCard = accountDao.listOfUserBankCard(userId);

		/* 最高充值金额 */
		int rechargeHighest = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MAX), 0);

		/* 最低充值金额 */
		int rechargeLowest = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MIN), 0);

		List<t_bank_quota> bankQuotaAll = bankQuotaService.findAll();

		// 充值限额表
		json.put("bankQuotaMap", bankQuotaAll);
		json.put("rechargeHighest", rechargeHighest);
		json.put("rechargeLowest", rechargeLowest);
		json.put("records", JSONArray.fromObject(userBankCard));
		json.put("code", 1);
		json.put("msg", "查询成功");

		return json.toString();
	}

	/****
	 * 
	 * 充值接口(OPT=211)
	 * 
	 * @param userId
	 *            用户id
	 * @param amount
	 *            金额
	 * @param client
	 *            客户端
	 * @param result
	 * @return
	 * @throws Exception
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-1
	 */
	public ResultInfo recharge(long userId, double amount, Client client, String bankNo, String businessSeqNo)
			throws Exception {
		ResultInfo result = new ResultInfo();

		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().rechargeCheckPassword(client.code, businessSeqNo, userId, amount,
					bankNo);
			if (result.code < 0) {
				result.code = -1;

				return result;
			}
			result.code = 1;
			result.msg = "App充值校验密码";

			return result;
		}

		// 普通网关模式
		result = userFundService.doRecharge(userId, amount, businessSeqNo);
		if (result.code < 1) {
			result.code = -1;

			return result;
		}

		result.code = 1;

		return result;
	}

	/**
	 * 分页查询 回款计划（OPT=242）
	 * 
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 *            用户id
	 * @return
	 *
	 * @author luzhiwei
	 * @createDate 2016年4月1日
	 *
	 */
	public String pageOfInvestReceive(int currPage, int pageSize, long userId) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();

		PageBean<ReturnMoneyPlanBean> page = accountDao.pageOfInvestReceive(userId, currPage, pageSize);

		if (page.page == null) {
			map.put("code", 1);
			map.put("msg", "查询结果为空");
			map.put("records", null);

			return JSONObject.fromObject(map).toString();
		}

		for (ReturnMoneyPlanBean invest : page.page) {

			t_bill_invest billInvest = billInvestService.findByID(invest.billInvestId);

			t_invest invest1 = investService.findByID(billInvest.invest_id);

			AddRateTicketBean couponUser = addRateTicketService.findTicketByUserId(billInvest.invest_id,
					billInvest.user_id, 3);
			if (couponUser == null) {
				invest.coupon_interest = invest.receiveInterest;
			} else {
				invest.coupon_interest = invest.receiveInterest + couponUser.apr * invest1.amount / 1200;
			}
		}

		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("records", page.page);

		return JSONObject.fromObject(map).toString();
	}

	/***
	 * 我的投资接口——我的出借——已完成（OPT=231）
	 * 
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 *            用户id
	 * @return
	 * 
	 * @author luzhiwei
	 * @createDate 2016-4-1
	 */
	public PageBean<MyInvestRecordBean> pageOfMyInvestRecord(int currPage, int pageSize, long userId) throws Exception {
		ResultInfo result = null;

		PageBean<MyInvestRecordBean> page = accountDao.pageOfMyInvestRecord(currPage, pageSize, userId);
		if (page != null && page.page != null && page.page.size() > 0) {
			for (MyInvestRecordBean bean : page.page) {
				result = debtAppService.isInvestCanbeTransfered(Long.valueOf(bean.investOriId));
				if (result.code == 1) {
					bean.canBeTransed = true;
				}
			}
		}
		return page;
	}
	
	/**
	 * 
	 * @Title: pageOfMyInvestRecordNew
	 *
	 * @description 我的投资接口——我的出借——已完成（OPT=231）
	 *
	 * @param @param currPage
	 * @param @param pageSize
	 * @param @param userId
	 * @param @return
	 * @param @throws Exception 
	 * 
	 * @return PageBean<MyInvestRecordBean>    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年12月28日
	 */
	public PageBean<MyInvestRecordBean> pageOfMyInvestRecordNewEnd(int currPage, int pageSize, long userId) throws Exception {
		ResultInfo result = null;
        PageBean<MyInvestRecordBean> page = accountDao.pageOfMyInvestRecordNewEnd(currPage, pageSize, userId);
		if (page != null && page.page != null && page.page.size() > 0) {
			for (MyInvestRecordBean bean : page.page) {
				result = debtAppService.isInvestCanbeTransfered(Long.valueOf(bean.investOriId));
				if (result.code == 1) {
					bean.canBeTransed = true;
				}
			}
		}
		
		return page;
	}
	
	
	/**
	 * 
	 * @Title: pageOfMyUnfinishedInvestRecord
	 *
	 * @description 我的投资接口——我的出借——未完成（OPT=219）
	 *
	 * @param @param currPage
	 * @param @param pageSize
	 * @param @param userId
	 * @param @return
	 * @param @throws Exception 
	 * 
	 * @return PageBean<MyInvestRecordBean>    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年12月26日
	 */
	public PageBean<MyInvestRecordBean> pageOfMyInvestRecordNewUnfinished(int currPage, int pageSize, long userId) throws Exception {
		ResultInfo result = null;
        PageBean<MyInvestRecordBean> page = accountDao.pageOfMyInvestRecordUnfinished(currPage, pageSize, userId);
		if (page != null && page.page != null && page.page.size() > 0) {
			for (MyInvestRecordBean bean : page.page) {
				result = debtAppService.isInvestCanbeTransfered(Long.valueOf(bean.investOriId));
				if (result.code == 1) {
					bean.canBeTransed = true;
				}
			}
		}
		
		return page;
	}
	

	/***
	 * 投资账单接口（OPT=232）
	 * 
	 * @param userId
	 *            用户id
	 * @param investId
	 *            投资id
	 * 
	 * @author luzhiwei
	 * @createDate 2016-4-5
	 */
	public String listOfInvestBillRecord(long userId, long investId) throws Exception {

		t_invest invest1 = investService.findByID(investId);

		List<BillInvestListBean> investBillList = accountDao.listOfInvestBillRecord(userId, investId);

		for (BillInvestListBean invest : investBillList) {
			AddRateTicketBean couponUser = addRateTicketService.findTicketByUserId(investId, userId, 3);
			if (couponUser == null) {
				invest.coupon_interest = invest.receiveInterest;
			} else {
				invest.coupon_interest = invest.receiveInterest + couponUser.apr * invest1.amount / 1200;
			}
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("billList", investBillList);

		return JSONObject.fromObject(map).toString();
	}

	/***
	 * 投资账单详情接口（OPT=237）
	 * 
	 * @param billInvestId
	 *            理财账单id
	 * 
	 * @author luzhiwei
	 * @createDate 2016-4-5
	 */
	public String findInvestBillRecord(long billInvestId, long bidId) throws Exception {

		t_bill_invest billInvest = billInvestService.findByID(billInvestId);

		t_invest invest1 = investService.findByID(billInvest.invest_id);

		BillInvestInfo billInvestInfo = accountDao.findInvestBillRecord(billInvestId);
		int totalPeriod = billService.findBidTotalBillCount(bidId);

		AddRateTicketBean couponUser = addRateTicketService.findTicketByUserId(billInvest.invest_id, billInvest.user_id,
				3);
		if (couponUser == null) {
			billInvestInfo.coupon_interest = billInvestInfo.receiveInterest;
		} else {
			billInvestInfo.coupon_interest = billInvestInfo.receiveInterest + couponUser.apr * invest1.amount / 1200;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalPeriod", totalPeriod);
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("billInvestInfo", billInvestInfo);

		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 我的借款（OPT=233）
	 *
	 * @param userId
	 *            用户id
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月5日
	 */
	public PageBean<MyLoanRecordBean> pageOfMyLoan(long userId, int currPage, int pageSize) {

		PageBean<MyLoanRecordBean> myLoan = accountDao.pageOfMyLoan(userId, currPage, pageSize);

		return myLoan;
	}

	/****
	 * 绑卡接口（OPT=222）
	 *
	 * @param userId
	 *            用户id
	 * @throws Exception
	 * @description
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-5
	 */
	public static String bindCardCheckPassword(long userId, int client) throws Exception {
		ResultInfo result = new ResultInfo();
		JSONObject json = new JSONObject();

		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		if (userInfo == null) {
			json.put("code", -108);
			json.put("msg", "获取用户信息失败");

			return json.toString();
		}

		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (StringUtils.isBlank(userFund.payment_account)) {
			json.put("code", -103);
			json.put("msg", "您还没有进行实名认证");

			return json.toString();
		}

		if (userFund.transaction_password == 0) {
			json.put("code", -107);
			json.put("msg", "您还没有设置交易密码");

			return json.toString();
		}

		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.CUSTOMER_BIND_CARD);

		result = PaymentProxy.getInstance().bindCardCheckPassword(client, businessSeqNo, userId);

		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			return json.toString();
		}

		json.put("html", result.obj.toString());
		json.put("businessSeqNo", businessSeqNo);
		json.put("code", 1);
		json.put("msg", "绑卡验密请求处理中");

		return json.toString();
	}

	/***
	 * 
	 * 交易记录接口（OPT=241）
	 * 
	 * @param userId
	 *            用户id
	 * @param currPage
	 * @param pageSize
	 * @return
	 * @description
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-6
	 */
	public String pageOfUserDealRecords(long userId, int currPage, int pageSize) {

		PageBean<DealRecordsBean> myLoan = accountDao.pageOfUserDealRecords(userId, currPage, pageSize);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("records", myLoan.page);

		return JSONObject.fromObject(map).toString();
	}

	/****
	 * 客户端获取会员信息接口（OPT=253）
	 *
	 * @param userId
	 *            用户id
	 * @throws Exception
	 * @description
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-5
	 */
	public String findUserInfo(long userId) throws Exception {
		JSONObject json = new JSONObject();
		Map<String, Object> userInfo = accountDao.findUserInfo(userId);

		json.put("userInfo", JSONObject.fromObject(userInfo));

		json.put("code", 1);
		json.put("msg", "查询成功");

		return json.toString();
	}

	/****
	 * 客户端获取会员信息接口（OPT=253）
	 *
	 * @param userId
	 *            用户id
	 * @throws Exception
	 * @description
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-5
	 */
	public String findUserInfoTwo(long userId) throws Exception {
		JSONObject json = new JSONObject();
		Map<String, Object> userInfo = accountDao.findUserInfo(userId);

		json.put("userInfo", JSONObject.fromObject(userInfo));
		json.put("education", EnumUtil.parseEnum(Education.values()));
		json.put("marital", EnumUtil.parseEnum(Marital.values()));
		json.put("workExperience", EnumUtil.parseEnum(WorkExperience.values()));
		json.put("annualIncome", EnumUtil.parseEnum(AnnualIncome.values()));
		json.put("netAssets", EnumUtil.parseEnum(NetAssets.values()));
		json.put("car", EnumUtil.parseEnum(Car.values()));
		json.put("house", EnumUtil.parseEnum(House.values()));
		json.put("relationship", EnumUtil.parseEnum(Relationship.values()));
		json.put("code", 1);
		json.put("msg", "查询成功");

		return json.toString();
	}

	/***
	 * 
	 * 客户端保存会员信息接口（OPT=254）
	 * 
	 * @param userId
	 *            用户id
	 * @param education
	 *            学历
	 * @parma mairtal 婚姻
	 * @param workExperience
	 *            工作
	 * @param annualIncome
	 *            年收入
	 * @param netAsset
	 *            资产
	 * @param car
	 *            车
	 * @param house
	 *            房
	 * @param emergencyContactType
	 *            紧急联系人关系
	 * @param emergencyContactName
	 *            紧急联系人姓名
	 * @param emergencyContactMobile
	 *            紧急联系人电话
	 * @return
	 * @throws Exception
	 * @description
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-6
	 */
	public ResultInfo updateUserInfo(long userId, int education, int marital, int workExperience, int annualIncome,
			int netAsset, int car, int house, int emergencyContactType, String emergencyContactName,
			String emergencyContactMobile) throws Exception {
		ResultInfo result = new ResultInfo();
		int rows = accountDao.updateUserInfo(userId, education, marital, workExperience, annualIncome, netAsset, car,
				house, emergencyContactType, emergencyContactName, emergencyContactMobile);
		if (rows < 0) {
			result.code = -1;
			result.msg = "保存会员信息失败";

			return result;
		}

		result.code = 1;
		result.msg = "保存会员信息成功";

		return result;
	}

	/**
	 * 安全中心（opt=261）
	 *
	 * @param userId
	 *            用户id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月6日
	 */
	public String findUserSecurity(long userId) {
		JSONObject json = new JSONObject();

		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		t_user user = super.findByID(userId);
		List<t_bank_card_user> cardList = bankCardUserService.queryCardByUserId(userId);

		json.put("paymentAccount", userFund.payment_account == null ? "" : userFund.payment_account);
		json.put("realityName", userInfo.reality_name == null ? "" : userInfo.reality_name);
		json.put("mobile", user.mobile == null ? "" : userInfo.mobile);
		json.put("email", userInfo.email == null ? "" : userInfo.email);
		json.put("card", cardList == null || cardList.size() == 0 ? "" : cardList.size());

		json.put("code", 1);
		json.put("msg", "查询成功");

		return json.toString();
	}

	/**
	 * 根据原密码修改密码（OPT=262）
	 *
	 * @param userId
	 *            用户id
	 * @param oldPassword
	 *            旧密码
	 * @param password
	 *            新密码
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月6日
	 */
	public ResultInfo userUpdatePwdbyold(long userId, String oldPassword, String password) {
		ResultInfo result = new ResultInfo();

		t_user user = super.findByID(userId);
		if (!oldPassword.equalsIgnoreCase(user.password)) {
			result.code = -1;
			result.msg = "原密码不正确";

			return result;
		}

		result = super.updatePassword(userId, password);
		if (result.code < 1) {
			result.code = -1;

			return result;
		}

		result.code = 1;
		result.msg = "修改成功";

		return result;
	}

	/**
	 * 提现准备（opt=213）
	 *
	 * @param userId
	 *            用户id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月7日
	 */
	public String withdrawalPre(long userId) throws Exception {
		JSONObject json = new JSONObject();

		/* 一个用户最多只能绑定10张银行卡 */
		/*
		 * bankCardUserService.delBank(userId); //实时查询绑定银行卡 ResultInfo result =
		 * PaymentProxy.getInstance().queryBindedBankCard(Client.PC.code,
		 * userId); List<t_bank_card_user> bankList = (List<t_bank_card_user>)
		 * result.obj;
		 * 
		 * for (t_bank_card_user bcu : bankList) {
		 * bankCardUserService.addUserCard(userId, bcu.bank_name, bcu.bank_code,
		 * bcu.account);
		 * 
		 * }
		 */
		List<UserBankCard> userBankCard = accountDao.listOfUserBankCard(userId);

		/** 提现手续费起点 */
		String withdrawFeePoint = settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_POINT);
		/** 提现手续费率 */
		String withdrawFeeRate = settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_RATE);
		/** 最低提现手续费 */
		String withdrawFeeMin = settingService.findSettingValueByKey(SettingKey.WITHDRAW_FEE_MIN);

		t_user_fund userFundInfo = userFundService.queryUserFundByUserId(userId);

		json.put("code", 1);
		json.put("msg", "查询成功");
		json.put("withdrawFeePoint", withdrawFeePoint);
		json.put("withdrawFeeRate", withdrawFeeRate);
		json.put("withdrawFeeMin", withdrawFeeMin);
		json.put("withdrawMaxRate", Constants.WITHDRAW_MAXRATE);
		json.put("balance", userFundInfo.balance);
		json.put("maxWithdraw", 500000); // 最大提现金额 ，由于PC端是固定的，App暂时先固定
		json.put("records", JSONArray.fromObject(userBankCard));

		return json.toString();
	}

	/***
	 * 
	 * 银行卡列表(opt=221)
	 * 
	 * @param userId
	 *            用户id
	 * @return
	 * @throws Exception
	 * @description
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public String listOfUserBankCard(long userId) throws Exception {
		JSONObject json = new JSONObject();
		List<UserBankCard> userBankList = accountDao.listOfUserBankCard(userId);

		json.put("code", 1);
		json.put("msg", "查询成功!");
		json.put("records", userBankList);

		return json.toString();
	}

	/**
	 * 提现（opt=214）
	 *
	 * @param userId
	 * @param withdrawalAmt
	 *            提现金额
	 * @param bankAccount
	 *            卡
	 * @param client
	 *            设备端
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月7日
	 */
	public ResultInfo withdrawal(long userId, double withdrawalAmt, String bankAccount, int client) {
		// 业务订单号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.FORGET_PASSWORD);

		ResultInfo result = userFundService.withdrawal(userId, businessSeqNo, withdrawalAmt, bankAccount,
				Client.getEnum(client));
		if (result.code < 1) {
			result.code = -1;

			return result;
		}

		if (ConfConst.IS_TRUST) {
			// 提现手续费
			double withdrawalFee = FeeCalculateUtil.getWithdrawalFee(withdrawalAmt);

			result = PaymentProxy.getInstance().withdrawalCheckPassword(client, businessSeqNo, userId, withdrawalAmt,
					bankAccount, ServiceType.CUSTOMER_WITHDRAW, withdrawalFee);
			if (result.code < 1) {
				result.code = -1;

				return result;
			}
			result.code = 1;
			result.msg = "App提现提交中...";

			return result;
		} else {
			// 提现手续费
			double withdrawalFee = FeeCalculateUtil.getWithdrawalFee(withdrawalAmt);
			// 普通网关模式
			// result = userFundService.doWithdrawal(userId, withdrawalAmt,
			// withdrawalFee, withdrawalFee, serviceOrderNo, false,0.0);
			if (result.code < 1) {
				result.code = -1;

				return result;
			}

		}

		result.code = 1;
		result.msg = "提现成功";

		return result;
	}

	/**
	 * 资金托管开户
	 *
	 * @param userId
	 *            用户id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月12日
	 */
	public String createAccount(long userId, int deviceType, String realName, String certNo, String beginTime,
			String endTime, String jobType, String job, String national, String postcode, String address) {
		JSONObject json = new JSONObject();

		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			json.put("code", -1);
			json.put("msg", "糟糕！没有获取到该用户信息");

			return json.toString();
		}

		if (StringUtils.isNotBlank(userFund.payment_account)) {
			json.put("code", -1);
			json.put("msg", "你已开通资金存管");

			return json.toString();
		}

		// 业务订单号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.PERSON_CUSTOMER_REGIST);

		ResultInfo result = PaymentProxy.getInstance().personCustomerRegist(deviceType, businessSeqNo, userId, realName,
				certNo, beginTime, endTime, jobType, job, national, postcode, address);

		TreeMap<String, String> map2 = YbUtils.getRespHeader(result.msg, ServiceType.PERSON_CUSTOMER_REGIST, null);

		if (map2.get("accNo") == null || map2.get("accNo").length() == 0) {

			json.put("code", -1);
			json.put("msg", map2.get("respMsg"));

		} else {

			result = ybPaymentCallBackService.userRegister(userId, map2, realName, certNo, YbPayType.USERREGISTER,
					businessSeqNo);
			if (result.code > 0) {

				ResultInfo error = new ResultInfo();
				t_cps_activity cpsActivityGoing = null;
				t_cps_user cpsUser = cpsUserDao.findByUserId(userId);
				t_user user = t_user.findById(userId);
				if (cpsUser != null) {
					t_cps_activity cpsActivity = cpsActivityService.queryCpsActivity(cpsUser.activity_id);
					if (cpsActivity != null) {
						cpsActivityGoing = cpsActivityService.queryGoingCpsActivityById(cpsActivity.id, 1);
					}
				}
				RewardGrantUtils.registerAndRealName(cpsActivityGoing, user, error);

				json.put("code", 1);
				json.put("msg", "资金存管开户成功");

			} else {
				json.put("code", -1);
				json.put("msg", map2.get("respMsg"));

			}
		}

		return json.toString();
	}

	/**
	 * 设置默认银行卡（opt=223）
	 *
	 * @param bankCardId
	 *            银行卡id
	 * @param userId
	 *            用户id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月12日
	 */
	public String setDefaultBankCard(long bankCardId, long userId) {
		JSONObject json = new JSONObject();

		int isFlag = bankCardUserService.updateUserCardStatus(bankCardId, userId);
		if (isFlag < 1) {
			json.put("code", -1);
			json.put("msg", "设置失败!");

			return json.toString();
		}

		json.put("code", 1);
		json.put("msg", "设置成功!");

		return json.toString();
	}

	/***
	 * 查看合同（opt=236）
	 *
	 * @param bidId
	 *            标id
	 * @return
	 * @description
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-12
	 */
	public String findBidPact(long bidId) {
		JSONObject json = new JSONObject();
		t_pact pact = pactService.findByBidid(bidId);
		if (pact == null) {
			json.put("code", -1);
			json.put("msg", "加载失败");

			return json.toString();
		}
		json.put("html", pact.content);
		json.put("code", 1);
		json.put("msg", "加载成功");

		return json.toString();
	}

	/**
	 * 
	 * 注册准备
	 * 
	 * @return
	 * @description
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-14
	 */
	public String registerPre() {
		JSONObject json = new JSONObject();

		String title = accountDao.findRegisterDealTitle();
		json.put("code", 1);
		json.put("msg", "查询成功");
		json.put("title", title);

		return json.toString();
	}

	/***
	 * 获取平台ico logo
	 *
	 * @return
	 * @description
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-14
	 */
	public String findPlatformIconFilename() {
		JSONObject json = new JSONObject();

		json.put("code", 1);
		json.put("msg", "查询成功");
		String platform_icon_filename = settingService.findSettingValueByKey(SettingKey.PLATFORM_ICON_FILENAME);
		json.put("platformIconFileName", platform_icon_filename);

		return json.toString();
	}

	/***
	 * 修改邮箱
	 * 
	 * @param userId
	 *            用户id
	 * @param email
	 *            邮箱
	 * @return
	 * @description
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-14
	 */
	public String updateEmail(long userId, String email) {
		JSONObject json = new JSONObject();

		/** 获取用户基本信息 **/
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);

		/* 用户Id进行加密 */
		String userSign = Security.addSign(userId, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);

		/* 邮箱进行加密 */
		String emailStr = Security.addEmailSign(email, Constants.MSG_EMAIL_SIGN, ConfConst.ENCRYPTION_KEY_DES);

		/* 获取修改邮箱URL */
		String url = BaseController.getBaseURL() + "loginAndRegiste/confirmactiveemail.html?su=" + userSign + "&se="
				+ emailStr;

		/* 发送邮件激活 */
		ResultInfo result = noticeService.sendReBindEmail(email, userInfo.name, url);
		if (result.code < 1) {
			json.put("code", -1);
			json.put("msg", result.msg);

			return json.toString();
		}
		json.put("code", 1);
		json.put("msg", "邮件发送成功,请到邮箱确认");

		return json.toString();
	}

	/***
	 * 设置交易密码
	 * 
	 * @param userId
	 *            用户id
	 * @return
	 * @description
	 *
	 * @author liuyang
	 * @createDate 2017-10-16
	 */
	public String setUserPassword(long userId, int deviceType) {
		JSONObject json = new JSONObject();

		/** 获取用户基本信息 **/
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			json.put("code", -1);
			json.put("msg", "获取用户信息失败");

			return json.toString();
		}

		if (StringUtils.isBlank(userFund.payment_account)) {
			json.put("code", -1);
			json.put("msg", "你还未开户，请先开户");

			return json.toString();
		}

		// 业务流水号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.SET_USER_PASSWORD);

		ResultInfo result = PaymentProxy.getInstance().setUserPassword(deviceType, businessSeqNo, userId);

		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			return json.toString();
		}

		json.put("html", result.obj.toString());
		json.put("code", 1);
		json.put("msg", "设置交易密码请求处理中");
		return json.toString();
	}

	/***
	 * 修改交易密码
	 * 
	 * @param userId
	 *            用户id
	 * @return
	 * @description
	 *
	 * @author liuyang
	 * @createDate 2017-10-16
	 */
	public String amendUserPassword(long userId, int deviceType) {
		JSONObject json = new JSONObject();

		/** 获取用户基本信息 **/
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			json.put("code", -108);
			json.put("msg", "获取用户信息失败");

			return json.toString();
		}

		if (StringUtils.isBlank(userFund.payment_account)) {
			json.put("code", -103);
			json.put("msg", "您还没有进行实名认证");

			return json.toString();
		}

		if (userFund.transaction_password == 0) {
			json.put("code", -107);
			json.put("msg", "您还没有设置交易密码");

			return json.toString();
		}

		// 业务流水号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.SET_USER_PASSWORD);

		ResultInfo result = PaymentProxy.getInstance().amendUserPassword(deviceType, businessSeqNo, userId);

		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			return json.toString();
		}

		json.put("html", result.obj.toString());
		json.put("code", 1);
		json.put("msg", "修改交易密码请求处理中");
		return json.toString();
	}

	/***
	 * 重置交易密码
	 * 
	 * @param userId
	 *            用户id
	 * @return
	 * @description
	 *
	 * @author liuyang
	 * @createDate 2017-10-16
	 */
	public String resetUserPassword(long userId, int deviceType) {
		JSONObject json = new JSONObject();

		/** 获取用户基本信息 **/
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			json.put("code", -108);
			json.put("msg", "获取用户信息失败");

			return json.toString();
		}

		if (StringUtils.isBlank(userFund.payment_account)) {
			json.put("code", -103);
			json.put("msg", "您还没有进行实名认证");

			return json.toString();
		}

		if (userFund.transaction_password == 0) {
			json.put("code", -107);
			json.put("msg", "您还没有设置交易密码");

			return json.toString();
		}

		// 业务流水号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.SET_USER_PASSWORD);

		ResultInfo result = PaymentProxy.getInstance().resetUserPassword(deviceType, businessSeqNo, userId);

		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			return json.toString();
		}

		json.put("html", result.obj.toString());
		json.put("code", 1);
		json.put("msg", "重置交易密码请求处理中");
		return json.toString();
	}

	/***
	 * 验密完成后绑定银行卡
	 * 
	 * @param client
	 *            设备类型
	 * @param userId
	 *            用户的ID
	 * @param businessSeqNo
	 *            业务流水号
	 * @return
	 * @description
	 *
	 * @author liuyang
	 * @createDate 2017-10-17
	 */
	public static String userBindCard(int client, long userId, String businessSeqNo) {
		JSONObject json = new JSONObject();

		/** 获取用户基本信息 **/
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		if (userFund == null) {
			json.put("code", -108);
			json.put("msg", "获取用户信息失败");

			return json.toString();
		}

		if (StringUtils.isBlank(userFund.payment_account)) {
			json.put("code", -103);
			json.put("msg", "您还没有进行实名认证");

			return json.toString();
		}

		if (userFund.transaction_password == 0) {
			json.put("code", -107);
			json.put("msg", "您还没有设置交易密码");

			return json.toString();
		}

		ResultInfo result = PaymentProxy.getInstance().userBindCard(client, businessSeqNo, userId,
				userInfo.reality_name, userInfo.id_number, userInfo.mobile);

		if (result.code < 0) {
			result.code = -1;
			json.put("code", -1);
			json.put("msg", "App绑卡请求错误");

			return json.toString();
		}

		json.put("code", 1);
		json.put("msg", "App绑卡请求提交中");
		json.put("html", result.obj);

		return json.toString();

	}

	/***
	 * 绑卡验密跳转页面
	 * 
	 * @param userId
	 *            用户id
	 * @return
	 * @description
	 *
	 * @author liuyang
	 * @createDate 2017-10-17
	 */
	public static String toCardPage(long userId, int deviceType) {
		JSONObject json = new JSONObject();

		/** 获取用户基本信息 **/
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		if (userInfo == null) {
			json.put("code", -108);
			json.put("msg", "获取用户信息失败");

			return json.toString();
		}

		if (StringUtils.isBlank(userFund.payment_account)) {
			json.put("code", -103);
			json.put("msg", "您还没有进行实名认证");

			return json.toString();
		}

		if (userFund.transaction_password == 0) {
			json.put("code", -107);
			json.put("msg", "您还没有设置交易密码");

			return json.toString();
		}

		json.put("name", userInfo.reality_name);
		json.put("id_number", userInfo.id_number);
		json.put("code", 1);
		json.put("msg", "绑卡页面跳转处理中");

		return json.toString();

	}

	/***
	 * 验密完成后绑定银行卡(新)
	 * 
	 * @param client
	 *            设备类型
	 * @param userId
	 *            用户的ID
	 * @param businessSeqNo
	 *            业务流水号
	 * @return
	 * @description
	 *
	 * @author guoShiJie
	 * @createDate 2018-04-25
	 */
	public static String userBindCard1(int client, long userId, String businessSeqNo, String mobilePhone) {
		JSONObject json = new JSONObject();

		/** 获取用户基本信息 **/
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		if (userFund == null) {
			json.put("code", -108);
			json.put("msg", "获取用户信息失败");

			return json.toString();
		}

		if (StringUtils.isBlank(userFund.payment_account)) {
			json.put("code", -103);
			json.put("msg", "您还没有进行实名认证");

			return json.toString();
		}

		if (userFund.transaction_password == 0) {
			json.put("code", -107);
			json.put("msg", "您还没有设置交易密码");

			return json.toString();
		}
		userInfo.mobile = mobilePhone;
		ResultInfo result = PaymentProxy.getInstance().userBindCard(client, businessSeqNo, userId,
				userInfo.reality_name, userInfo.id_number, userInfo.mobile);

		if (result.code < 0) {
			result.code = -1;
			json.put("code", -1);
			json.put("msg", "App绑卡请求错误");

			return json.toString();
		}

		json.put("code", 1);
		json.put("msg", "App绑卡请求提交中");
		json.put("html", result.obj);

		return json.toString();

	}

	public String findBidAndUserIdByPact(long bidId, long userId) {
		JSONObject json = new JSONObject();
		t_pact pact = pactService.findByCondition(bidId,userId);
		if (pact == null) {
			json.put("code", -1);
			json.put("msg", "加载失败");

			return json.toString();
		}
		json.put("html", pact.content);
		//状态为0时，查看旧合同
		json.put("status", 0);
		json.put("code", 1);
		json.put("msg", "加载成功");

		return json.toString();
	}
}
