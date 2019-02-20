package services.common;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_deal_platform;
import models.common.entity.t_deal_user;
import models.common.entity.t_sms_jy_count;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import play.Logger;

import org.apache.commons.lang.StringUtils;

import services.base.BaseService;
import services.ext.redpacket.AddRateTicketService;

import com.shove.Convert;
import com.shove.security.Encrypt;
import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.SettingKey;
import common.enums.Client;
import common.enums.JYSMSModel;
import common.enums.NoticeScene;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ReentrantLockUtil;
import common.utils.ResultInfo;
import daos.common.SmsSendingDao;
import daos.common.UserFundDao;
import daos.common.UserInfoDao;

public class UserFundService extends BaseService<t_user_fund> {
	
	protected static UserInfoDao userInfoDao = Factory.getDao(UserInfoDao.class);
	
	protected static UserFundDao userFundDao = Factory.getDao(UserFundDao.class);
	
	protected static RechargeUserService rechargeUserService = Factory.getService(RechargeUserService.class);
	
	protected static WithdrawalUserService withdrawalUserService = Factory.getService(WithdrawalUserService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	protected static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);
	
	protected static DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static AddRateTicketService addRateTicketService = Factory.getService(AddRateTicketService.class);
	
	protected static SmsJyCountService smsJyCountService = Factory.getService(SmsJyCountService.class);
	
	protected static SmsSendingDao smsSendingDao = Factory.getDao(SmsSendingDao.class);
	protected UserFundService(){
		super.basedao = userFundDao;
	}
	
	@Override
	public t_user_fund findByID(long id) {
		//禁用此方法
		throw new RuntimeException("UserFundService中的findByID()禁止使用 ");

	}
	
	/**
	 * 添加用户资金信息
	 * 
	 * @param userId 用户id
	 * @param name 用户名
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月19日
	 */
	public boolean addUserFundInfo(long userId, String name) {
		t_user_fund userFund = new t_user_fund();
		userFund.user_id = userId;
		userFund.name = name;
		userFund.payment_account = "";
		userFund.fund_sign = "";
		
		return userFundDao.save(userFund);
	}
	
	/**
	 * 资金托管个人开户（执行）
	 *
	 * @param userId
	 * @param paymentAccount
	 * @param realName
	 * @param idNumber
	 * @param mobile
	 * @param email
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年9月15日
	 */
	public ResultInfo doCreateAccount(long userId, String paymentAccount,
			String realName, String idNumber) {
		ResultInfo result = new ResultInfo();
		
		//保存托管账户
		int row = userFundDao.updatePaymentAccount(userId, paymentAccount);
		if (row < 1) {
			result.code = -1;
			result.msg = "保存第三方托管账户失败";
			
			return result;
		}
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		if (StringUtils.isNotBlank(realName)) {
			userInfo.reality_name = realName;
		}
		
		if (StringUtils.isNotBlank(idNumber)) {
			userInfo.id_number = idNumber;
		}
		
		result = userInfoService.updateUserInfo(userInfo);
		if (result.code < 0) {
			result.code = -1;
			result.msg = "保存用户信息失败";
			
			return result;
		}

		/** 发送通知  */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", userInfo.name);
		noticeService.sendSysNotice(userInfo.user_id, NoticeScene.ACCOUTN_OPENING_SUCC, param);
		
		//刷新用户缓存信息
		userService.flushUserCache(userId);
		
		result.code = 1;
		result.msg = "个人开户成功";
		
		return result;
	}

	
	
	/**
	 * 资金托管企业开户（执行）
	 *
	 * @param userId
	 * @param paymentAccount
	 * @param realName
	 * @param idNumber
	 * @param mobile
	 * @param email
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年9月15日
	 */
	public ResultInfo doCreateAccount(long userId, String paymentAccount, String realName,
			String companyName, String uniSocCreCode, String bankNo, String idCard) {
		ResultInfo result = new ResultInfo();
		
		//保存托管账户
		int row = userFundDao.updatePaymentAccount(userId, paymentAccount);
		if (row < 1) {
			result.code = -1;
			result.msg = "保存第三方托管账户失败";
			
			return result;
		}
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		if (StringUtils.isNotBlank(realName)) {
			userInfo.reality_name = realName;
		}
		
		if (StringUtils.isNotBlank(idCard)) {
			userInfo.id_number = idCard;
		}
		
		if (StringUtils.isNotBlank(companyName)) {
			userInfo.enterprise_name = companyName;
		}
		
		if (StringUtils.isNotBlank(uniSocCreCode)) {
			userInfo.enterprise_credit = uniSocCreCode;
		}
		
		if (StringUtils.isNotBlank(bankNo)) {
			userInfo.enterprise_bank_no = bankNo;
		}
		
		
		result = userInfoService.updateUserInfo(userInfo);
		if (result.code < 0) {
			result.code = -1;
			result.msg = "保存用户信息失败";
			
			return result;
		}

		/** 发送通知  */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", userInfo.name);
		noticeService.sendSysNotice(userInfo.user_id, NoticeScene.ACCOUTN_OPENING_SUCC, param);
		
		//刷新用户缓存信息
		userService.flushUserCache(userId);
		
		result.code = 1;
		result.msg = "企业开户成功";
		
		return result;
	}
	
	/**
	 * 用户充值（准备）
	 *
	 * @param userId 用户ID
	 * @param serviceOrderNo 业务订单号
	 * @param rechargeAmt 充值金额
	 * @param summary 备注
	 * @param 登录端
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public ResultInfo recharge(long userId, String businessSeqNo, double rechargeAmt, String summary,Client client) {
		ResultInfo result = new ResultInfo();
		
		//用户资金签名校验
		result = this.userFundSignCheck(userId);
		if (result.code < 1) {
			
			return result;
		}
		
		//最低充值金额
		String minRechargeAmtStr = settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MIN);
		double minRechargeAmt = Convert.strToDouble(minRechargeAmtStr, 100);
		if (rechargeAmt < minRechargeAmt) {
			result.code = -1;
			result.msg = "充值不能低于"+minRechargeAmt+"元";
			
			return result;
		}
		
		//最高充值金额
		String maxRechargeAmtStr = settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MAX);
		double maxRechargeAmt = Convert.strToDouble(maxRechargeAmtStr, 100);
		if (rechargeAmt > maxRechargeAmt) {
			result.code = -1;
			result.msg = "充值不能高于"+maxRechargeAmt+"元";
			
			return result;
		}
		
		//添加充值记录
		boolean isAdd = rechargeUserService.addUserRecharge(userId, businessSeqNo, rechargeAmt, summary, client);
		if (!isAdd) {
			result.code = -1;
			result.msg = "添加充值记录失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "充值准备完毕";
		
		return result;
	}

	/**
	 * 用户充值（准备）
	 *
	 * @param userId 用户ID
	 * @param serviceOrderNo 业务订单号
	 * @param rechargeAmt 充值金额
	 * @param summary 备注
	 * @param 登录端
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年11月06日
	 */
	public ResultInfo rechargeS(long userId, String businessSeqNo, double rechargeAmt, String bankNames, String bankAccount, String summary,Client client) {
		ResultInfo result = new ResultInfo();
		
		//用户资金签名校验
		result = this.userFundSignCheck(userId);
		if (result.code < 1) {
			
			return result;
		}
		
		//最低充值金额
		String minRechargeAmtStr = settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MIN);
		double minRechargeAmt = Convert.strToDouble(minRechargeAmtStr, 100);
		if (rechargeAmt < minRechargeAmt) {
			result.code = -1;
			result.msg = "充值不能低于"+minRechargeAmt+"元";
			
			return result;
		}
		
		//最高充值金额
		String maxRechargeAmtStr = settingService.findSettingValueByKey(SettingKey.RECHARGE_AMOUNT_MAX);
		double maxRechargeAmt = Convert.strToDouble(maxRechargeAmtStr, 100);
		if (rechargeAmt > maxRechargeAmt) {
			result.code = -1;
			result.msg = "充值不能高于"+maxRechargeAmt+"元";
			
			return result;
		}
		
		//添加充值记录
		boolean isAdd = rechargeUserService.addUserRechargeS(userId, businessSeqNo, rechargeAmt, summary, client, bankNames, bankAccount);
		if (!isAdd) {
			result.code = -1;
			result.msg = "添加充值记录失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "充值准备完毕";
		
		return result;
	}

	
	
	/**
	 * 用户充值（执行）
	 *
	 * @param userId 用户ID
	 * @param rechargeAmt 充值金额
	 * @param serviceOrderNo 业务订单号
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public ResultInfo doRecharge(long userId, double rechargeAmt, String serviceOrderNo) {
		ResultInfo result = new ResultInfo();
		
		int row = rechargeUserService.updateRechargeToSuccess(serviceOrderNo);
		
		if (row < 0) {
			result.code = -1;
			result.msg = "更新充值记录失败";
			
			return result;
		}

		boolean isAdd = userFundAdd(userId, rechargeAmt);
		if (!isAdd) {
			result.code = -1;
			result.msg = "增加用户可用余额失败";
			
			return result;
		}
		
		result = userFundSignUpdate(userId);
		if (result.code < 1) {
			result.code = -1;
			result.msg = "更新用户签名失败";
			
			return result;
		}
		
		t_user_fund userFund = queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
	    //充值交易记录
		boolean addDeal = dealUserService.addDealUserInfo(
				serviceOrderNo, 
				userId,
				rechargeAmt,
				userFund.balance,
				userFund.freeze,
				t_deal_user.OperationType.RECHARGE,
				null);
		
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加充值交易记录失败";
			
			return result;
		}
		
		if (ConfConst.IS_ADD_RATE) {
			addRateTicketService.getTicket(userId);
		}
		
		/** 发送通知 */
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", userFund.name);
		sceneParame.put("amount", rechargeAmt);
		sceneParame.put("balance", userFund.balance);
		//noticeService.sendSysNotice(userId, NoticeScene.RECHARGE_SUCC, sceneParame);
		t_user user = t_user.findById(userId);
		Boolean flag = smsJyCountService.judgeIsSend(user.mobile, JYSMSModel.MODEL_RECHARGE_SUCCESS);
		if (flag) {
			noticeService.sendSysNotice(userId,NoticeScene.RECHARGE_SUCC, sceneParame,JYSMSModel.MODEL_RECHARGE_SUCCESS);
		}
		
		result.code = 1;
		result.msg = "充值成功";

		return result;
	}
	
	/**
	 * 用户绑卡（执行）
	 *
	 * @param userId 用户ID
	 * @param bankName 银行名称
	 * @param bankCode 银行代号
	 * @param account 银行账户
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月9日
	 */
	public ResultInfo doBindCard(long userId, String  bankName, String bankCode, String account) {
		ResultInfo result = new ResultInfo();
		
		boolean isAdd = bankCardUserService.addUserCard(userId, bankName, bankCode, account);
		if (!isAdd) {
			result.code = -1;
			result.msg = "添加银行卡失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "用户绑卡成功";
		/* 刷新当前用户缓存 */
		userService.flushUserCache(userId);
		
		t_user_info userInfo = userInfoDao.findByID(userId);
		
		/** 发送通知 */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", userInfo.name);
		//添加银行卡成功
		//创蓝接口
		//noticeService.sendSysNotice(userId, NoticeScene.ADD_BANKCARD_SUCC, param);
		//焦云接口
		//noticeService.sendSysNotice(userId, NoticeScene.ADD_BANKCARD_SUCC, param,JYSMSModel.MODEL_ADD_BANKCARD_SUCC);
		
		return result;
		
	}
	
	/**
	 * 提现（准备）
	 *
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月17日
	 */
	public ResultInfo withdrawal(long userId, String serviceOrderNo, double withdrawalAmt, String bankAccount, Client client) {
		ResultInfo result = new ResultInfo();
		t_user_fund userFund = this.queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		//用户资金签名校验
		result = this.userFundSignCheck(userId);
		if (result.code < 1) {
			
			return result;
		}
		
		if (userFund.balance < withdrawalAmt) {
			result.code = -1;
			result.msg = "提现余额不足";
			
			return result;
		}

		//添加提现记录
		boolean isAdd = withdrawalUserService.addUserWithdrawal(userId, serviceOrderNo, withdrawalAmt, bankAccount, "用户提现", client);
		if (!isAdd) {
			result.code = -1;
			result.msg = "添加提现记录失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "提现准备完毕";
		
		return result;
	}

	/**
	 * 用户提现（执行）
	 *
	 * @param userId 关联用户ID
	 * @param withdrawalAmt 提现金额
	 * @param withdrawalFee 提现手续费
	 * @param serviceOrderNo 业务订单号
	 * @param chargeMode 手续费扣除模式：false-内扣
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月11日
	 */
	public ResultInfo doWithdrawal(long userId, double withdrawalAmt, double withdrawalFee, String serviceOrderNo) {
		ResultInfo result = new ResultInfo();
		
		int row = withdrawalUserService.updateWithdrawalToSuccess(serviceOrderNo);
		
		if (row < 0) {
			result.code = -1;
			result.msg = "更新提现记录失败";
			
			return result;
		}
		
		double 	minusAmt = withdrawalAmt ;//内扣模式账号扣除金额为提现金额
		

		boolean isMinus = userFundMinus(userId, minusAmt);
		if (!isMinus) {
			result.code = -1;
			result.msg = "扣除用户可用余额失败";
			
			return result;
		}
		
		result = userFundSignUpdate(userId);
		if (result.code < 1) {
			result.code = -1;
			result.msg = "更新用户签名失败";
			
			return result;
		}
		
		t_user_fund userFund = queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
		//提现交易记录
		double realAmt = 0;  //实际到账金额 
		
		realAmt = withdrawalAmt - withdrawalFee;
		
		boolean addDeal = dealUserService.addDealUserInfo(
				serviceOrderNo, 
				userId,
				realAmt,  //实际到账
				userFund.balance + withdrawalFee,  //此时不计提现手续费
				userFund.freeze, 
				t_deal_user.OperationType.WITHDRAW,
				null);
		
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加提现交易记录失败";
			
			return result;
		}
		
		
		//提现手续费扣除记录
		addDeal = dealUserService.addDealUserInfo(
				serviceOrderNo, 
				userId,
				withdrawalFee,
				userFund.balance,
				userFund.freeze, 
				t_deal_user.OperationType.WITHDRAW_FREE,
				null);
		
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加提现手续费扣除记录失败";
			
			return result;
		}
		
		/** 发送通知 */
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", userFund.name);
		sceneParame.put("withdraw_amount", withdrawalAmt);
		sceneParame.put("fee", withdrawalFee);
		sceneParame.put("actual_amount", realAmt);
		sceneParame.put("balance", userFund.balance);
		//noticeService.sendSysNotice(userId, NoticeScene.WITHDRAW_SUCC, sceneParame);
		t_user user = t_user.findById(userFund.user_id);
		Boolean flag = smsJyCountService.judgeIsSend(user.mobile, JYSMSModel.MODEL_WITHDRAW);
		if (flag) {
			noticeService.sendSysNotice(userId, NoticeScene.WITHDRAW_SUCC, sceneParame,JYSMSModel.MODEL_WITHDRAW);
		}
		
		result.code = 1;
		result.msg = "提现成功";

		return result;
	}
	
	/**
	 * 增加用户可用余额
	 * 
	 * @param userId 用户id 
	 * @param amount 变动金额
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月19日
	 */
	public boolean userFundAdd(long userId, double amount) {
		if (amount < 0) {
			
			throw new InvalidParameterException("amount < 0");
		}
		
		int row = userFundDao.updateUserFundAdd(userId, amount);
		
		if (row < 1) {
			LoggerUtil.info(true, "增加用户可用余额失败，【userId：%s，amount：%s】", userId, amount);

			return false;
		}

		return true;
	}

	/**
	 * 扣除用户可用余额
	 * 
	 * @param userId 用户id
	 * @param amount 变动金额
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月19日
	 */
	public boolean userFundMinus(long userId, double amount) {
		if (amount < 0) {
			
			throw new InvalidParameterException("amount < 0");
		}
		
		t_user_fund userFund = userFundDao.findByColumn("user_id = ?", userId);
		
		if (userFund.balance <  amount){
			
			Logger.info("用户还款金额不足");
			return false;
		}
		
		int row = userFundDao.updateUserFundMinus(userId, amount);
		
		if (row < 1) {
			LoggerUtil.info(true, "扣除用户虚拟资产失败，【userId：%s，amount：%s】", userId, amount);
			
			return false;
		}
		
		return true;
	}

	/**
	 * 扣除用户冻结金额
	 * 
	 * @param userId 用户id
	 * @param amount 变动金额
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月19日
	 */
	public boolean userFundMinusFreezeAmt(long userId, double amount) {
		if (amount <= 0) {
			
			throw new InvalidParameterException("amount <= 0");
		}
		
		int row = userFundDao.updateUserFundMinusFreezeAmt(userId, amount);
		
		if (row < 1) {
			LoggerUtil.info(true, "扣除用户冻结金额失败，【userId：%s，amount：%s】", userId, amount);
			
			return false;
		}
		
		return true;
	}

	/**
	 * 冻结用户资金
	 * 
	 * @param userId 用户id
	 * @param amount 变动金额
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月19日
	 */
	public boolean userFundFreeze(long userId, double amount) {
		if (amount < 0) {
			
			throw new InvalidParameterException("amount < 0");
		}
		
		int row = userFundDao.updateUserFundFreeze(userId, amount);
		
		if (row < 1) {
			LoggerUtil.info(true, "冻结用户资金失败，【userId：%s，amount：%s】", userId, amount);
			
			return false;
		}
		
		return true;
	}

	/**
	 * 解冻用户冻结资金
	 * 
	 * @param userId 用户id
	 * @param amount 变动金额
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月19日
	 */
	public boolean userFundUnFreeze(long userId, double amount) {
		if (amount <= 0) {
			
			throw new InvalidParameterException("amount <= 0");
		}
		
		int row = userFundDao.updateUserFundUnFreeze(userId, amount);
		
		if (row < 1) {
			LoggerUtil.info(true, "解冻用户冻结资金失败，【userId：%s，amount：%s】", userId, amount);
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * 增加用户的虚拟资产
	 *
	 * @param userId
	 * @param amount
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月16日
	 */
	public boolean userVisualFundAdd(long userId,double amount){
		if (amount <= 0) {
			
			throw new InvalidParameterException("amount <= 0");
		}
		
		int row = userFundDao.updateUserVisualFundAdd(userId, amount);
		
		if (row < 1) {
			LoggerUtil.info(true, "增加用户虚拟资产失败，【userId：%s，amount：%s】", userId, amount);

			return false;
		}

		return true;
	}
	
	/**
	 * 扣除用户的虚拟资产
	 *
	 * @param userId
	 * @param amount
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月16日
	 */
	public boolean userVisualFundMinus(long userId,double amount){
		if (amount <= 0) {
			
			throw new InvalidParameterException("amount <= 0");
		}
		
		int row = userFundDao.updateUserVisualFundMinus(userId, amount);
		
		if (row < 1) {
			LoggerUtil.info(true, "减少用户虚拟资产失败，【userId：%s，amount：%s】", userId, amount);
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * 用户资产签名校验
	 *
	 * @param userId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月23日
	 */
	public ResultInfo userFundSignCheck(long userId) {
		ResultInfo result = new ResultInfo();
		result.msg = "尊敬的用户，你的账户资金出现异常变动，请速联系管理员";
		
		t_user_fund userFund = userFundDao.findByColumn("user_id = ?", userId);
		if (userFund == null) {
			result.code = -1;
			LoggerUtil.info(false, "该用户不存在"); 
			
			return result;
		}
		
		if (StringUtils.isBlank(userFund.fund_sign)) {
			result.code = -2;
			LoggerUtil.info(false, "该用户资金签名字段为空"); 
			
			return result;
		}
		
		//参与验签字段：id、balance、freeze、visual_balance
		String sign = Encrypt.MD5(userFund.id + userFund.balance + userFund.freeze + userFund.visual_balance + ConfConst.ENCRYPTION_KEY_MD5);

		if (!sign.equalsIgnoreCase(userFund.fund_sign)) {
			result.code = -3;
			LoggerUtil.info(false, "该用户账户资金出现异常变动"); 
			
			return result;
		}
		
		result.code = 1;
		result.msg = "用户资产签名校验通过";
		
		return result;
	}

	/**
	 * 用户资产签名更新
	 *
	 * @param userId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月23日
	 */
	public ResultInfo userFundSignUpdate(long userId) {
		ResultInfo result = new ResultInfo();
		result.msg = "尊敬的用户，你的账户资金出现异常变动，请速联系管理员";
		
		ReentrantLockUtil.lock(CacheKey.USER_FUND_LOCK_ + userId);
		
		try {
			t_user_fund userFund = userFundDao.findByColumn("user_id = ?", userId);
			if (userFund == null) {
				result.code = -1;
				LoggerUtil.info(false, "该用户不存在"); 
				
				return result;
			}
			
			String userFundsign = Encrypt.MD5(userFund.id + userFund.balance + userFund.freeze + userFund.visual_balance + ConfConst.ENCRYPTION_KEY_MD5);
			
			int row = userFundDao.updateUserFundSign(userId, userFundsign);
			
			if (row <= 0) {
				result.code = -2;
				LoggerUtil.info(false, "更新用户资产签名字段失败");
				
				return result;
			}
		} catch (Exception e) {
			LoggerUtil.error(false, e, "更新用户资产签名字段异常");
		} finally {
			ReentrantLockUtil.unLock(CacheKey.USER_FUND_LOCK_ + userId);
		}

		result.code = 1;
		result.msg = "更新用户资产签名字段成功";
		
		return result;
	}

	/**
	 * 平台浮存金
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月8日
	 *
	 */
	public double queryPlatformFloatAmount() {

		return userFundDao.findPlatformFloatAmount();
	}

	/**
	 * 查询用户可用余额
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public double findUserBalance(long userId) {
		
		return userFundDao.findUserBalance(userId);
	}
	
	/**
	 * 查询用户的虚拟资产
	 *
	 * @param userId 用户id
	 * @return 
	 *
	 * @author yaoyi
	 * @createDate 2016年2月17日
	 */
	public double findUserVisualBalance(long userId){
		return userFundDao.findUserVisualBalance(userId);
	}
	
	/**
	 * 查询某个用户的理财服务费优惠信息
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月8日
	 */
	public double findUserDiscount(Long userId){
		
		return userFundDao.findUserDiscount(userId);
	}
	
	/**
	 * 根据userId获取用户资金信息
	 * 
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 */
	public t_user_fund queryUserFundByUserId(long userId) {
		
		return userFundDao.findUserFundByUserId(userId);
	}
	
	/**
	 * 注销
	 * 
	 * @author niu
	 * @create 2017.10.17
	 */
	public ResultInfo custCancel(long userId) {
		
		ResultInfo result = new ResultInfo();
		
		//保存托管账户
		int row = userFundDao.cancelPaymengAcc(userId);
		if (row < 1) {
			result.code = -1;
			result.msg = "修改本地第三方托管账户失败！";
			
			return result;
		}
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		result = userInfoService.cancelUpdUsrInfo(userId);
		if (result.code < 0) {
			return result;
		}
		
		t_user_fund userFund = userFundService.findByColumn("user_id=?", userId);
		userFund.transaction_password = 0;
		t_user_fund fund = userFund.save();
		if (fund == null) {
			result.code = -1;
			result.msg = "取消交易密码失败！";
			
			return result;
		}

		//刷新用户缓存信息
		userService.flushUserCache(userId);
		
		result.code = 1;
		result.msg = "注销成功！";
			
		return result;
	}
	
	
	/**
	 * 自检更新开户状态
	 * 
	 * @param userId
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年10月17日
	 */
	public int updateUserStatus(long userId,String payment_account) {
		
		return userFundDao.updateUserStatus(userId,payment_account);
	}
	
	/**
	 * 用户网银充值（执行）
	 *
	 * @param userId 用户ID
	 * @param rechargeAmt 充值金额
	 * @param serviceOrderNo 业务订单号
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年10月26日
	 */
	public ResultInfo internetDoRecharge(long userId, double rechargeAmt, String serviceOrderNo) {
		ResultInfo result = new ResultInfo();
		
		int row = rechargeUserService.updateRechargeToSuccesses(serviceOrderNo);
		
		if (row < 0) {
			result.code = -1;
			result.msg = "更新充值记录失败";
			
			return result;
		}

		boolean isAdd = userFundAdd(userId, rechargeAmt);
		if (!isAdd) {
			result.code = -1;
			result.msg = "增加用户可用余额失败";
			
			return result;
		}
		
		result = userFundSignUpdate(userId);
		if (result.code < 1) {
			result.code = -1;
			result.msg = "更新用户签名失败";
			
			return result;
		}
		
		t_user_fund userFund = queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "查询用户资金信息失败";
			
			return result;
		}
		
	    //充值交易记录
		boolean addDeal = dealUserService.addDealUserInfo(
				serviceOrderNo, 
				userId,
				rechargeAmt,
				userFund.balance,
				userFund.freeze,
				t_deal_user.OperationType.INTERNETRECHARGE,
				null);
		
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加充值交易记录失败";
			
			return result;
		}
		
		/** 发送通知 */
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", userFund.name);
		sceneParame.put("amount", rechargeAmt);
		sceneParame.put("balance", userFund.balance);
		noticeService.sendSysNotice(userId, NoticeScene.RECHARGE_SUCC, sceneParame);
		
		result.code = 1;
		result.msg = "充值成功";

		return result;
	}
	
	/**
	 * 获取交易密码transaction_password属性值
	 * 
	 * @author guoShiJie
	 * @createDate 2018.04.25
	 * */
	public int transactionPasswordValue(long userId){
		
		t_user_fund ufd = userFundDao.findByColumn("user_id = ?", userId);
		if (ufd == null) {
			return -1;
		}
		return ufd.transaction_password;
	}
	
}




















