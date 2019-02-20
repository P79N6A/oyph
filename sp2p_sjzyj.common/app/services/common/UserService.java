package services.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import models.beans.MallScroeRecord;
import models.common.bean.CurrUser;
import models.common.bean.ShowUserInfo;
import models.common.bean.UserDetail;
import models.common.bean.UserFundInfo;
import models.common.bean.UserFundStatistic;
import models.common.entity.t_bank_card_user;
import models.common.entity.t_credit_level;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.entity.t_mall_scroe_rule;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import play.mvc.Scope.Session;
import services.base.BaseService;
import common.constants.CacheKey;
import common.constants.Constants;
import common.constants.SettingKey;
import common.enums.Client;
import common.enums.NoticeScene;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ReentrantLockUtil;
import common.utils.ResultInfo;
import daos.common.UserDao;

public class UserService extends BaseService<t_user> {
	
	protected UserDao userDao = Factory.getDao(UserDao.class);

	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);
	
	protected static CreditLevelService creditLevelService = Factory.getService(CreditLevelService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	protected UserService(){
		super.basedao = userDao;
	}
	
	/**
	 * 用户注册
	 * 
	 * @param name 用户名
	 * @param mobile 手机号码
	 * @param password 密码
	 * @param client 客户端
	 * @param ip IP地址
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月15日
	 *
	 */
	public ResultInfo registering(String name, String mobile, String password, Client client, String ip,long supervisorId) {
		ResultInfo result = new ResultInfo();
		
		t_user user = new t_user(new Date(), name, mobile, password);
		user.login_count = 1;
		user.last_login_time = new Date();
		user.setLast_login_client(client);
		user.is_allow_login = true;
		user.last_login_ip = ip;
		user.supervisor_id = supervisorId;
		/* 保存用户注册信息 */
		boolean isSaveUser = userDao.save(user);
		if (!isSaveUser) {
			result.code = -1;
			result.msg = "用户注册信息失败";
			
			return result;
		}
		
		/* 初始化用户基本信息 */
		boolean isSaveUInfo = userInfoService.addUserInfo(user.id, client, mobile, name);
		if (!isSaveUInfo) {
			result.code = -1;
			result.msg = "用户基本信息初始化失败";
			
			return result;
		}
		
		/* 初始化用户资金信息 */
		boolean isSaveUFund = userFundService.addUserFundInfo(user.id, name);
		if (!isSaveUFund) {
			result.code = -1;
			result.msg = "用户资金信息初始化失败";
			
			return result;
		}
		
		/* 用户资产签名更新 */
		result = userFundService.userFundSignUpdate(user.id);
		
		if (result.code < 1) {
			
			return result;
		}
		
		
		t_mall_scroe_rule rule = t_mall_scroe_rule.find("type=1").first();
		
		if(rule !=null){
			MallScroeRecord.saveScroeRegistereRecord(user, rule.scroe, result);
		}
		
		result.code = 1;
		result.msg = "用户注册成功";
		result.obj = user;
		
		/** 发送通知 */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		noticeService.sendSysNotice(user.id, NoticeScene.REGISTER_SUCC, param);
		
		/** 注册成功，设置用户为登陆状态 */
		setCurrUser(user.id);
		flushUserCache(user.id);
		
		return result;
	}

	/**
	 * 用户登录
	 *
	 * @param mobile 登录账户
	 * @param password 登录密码
	 * @param client 登录端(1-pc； 2-app；3 wechat)
	 * @param ip 登录IP
	 * @param securityLockTimes 密码连续错误次数时锁定用户
	 * @param securityLockTime 密码连续错误次数时锁定用户时长（分钟）
	 * @return 登录用户对象
	 * 
	 * @author ChenZhipeng
	 * @createDate 2015年12月15日
	 */
	public ResultInfo logining(String mobile, String password, Client client, String ip, int securityLockTimes, int securityLockTime) {
		ResultInfo result = new ResultInfo();
		t_user user = userDao.findByColumn("mobile=?", mobile);
		
		/* 判断账号是否被管理员锁定 */
		if (!user.is_allow_login) {
			result.code = -1;
			result.msg = "该账号被锁定请联系管理员";
			
			return result;
		}
		
		/* 判断账号是否被锁定 */
		if (user.is_password_locked) {
			if(DateUtil.getMinutesDiffFloor(user.password_locked_time, new Date()) < securityLockTime){
				result.code = -1;
				result.msg = "连续错误次数超出请稍后再试";
				
				return result;
			} else {
				user.password_continue_fails = 0;
				user.is_password_locked = false;
				user.password_locked_time = null;
				if (!userDao.save(user)) {
					result.code = ResultInfo.ERROR_SQL;
					result.msg = "系统异常，请联系管理员";
					
					return result;
				}
			}
		}
		
		/* 输入的密码错误 */
		if (!user.password.equals(password)) {
			/* 获取当前错误登录次数 */
			int currLoginFailCount = user.password_continue_fails;
			currLoginFailCount += 1;
			if (currLoginFailCount >= securityLockTimes) {
				/* 用户达到锁定账号条件，执行账号锁定 */
				int i = userDao.updateLockStatus(user.id, securityLockTimes);
				if (i < 0) {
					result.code = ResultInfo.ERROR_SQL;
					result.msg = "系统异常，请联系管理员";
					
					return result;
				}

				result.code = -1;
				result.msg = "连续错误次数超出请稍后再试";
				
				return result;
			}
			
			/* 添加一次输入错误 */
			user.password_continue_fails = currLoginFailCount;
			if (!userDao.addPwdContinueFails(user.id)) {
				result.code = ResultInfo.ERROR_SQL;
				result.msg = "系统异常，请联系管理员";
				
				return result;
			}
			result.code = -1;
			result.msg = "密码不正确";
			
			return result;
		}
		
		/* 登录成功，修改登录信息 */
		user.login_count += 1;
		user.password_continue_fails = 0;
		user.last_login_time = new Date();
		user.setLast_login_client(client);
		user.last_login_ip = ip;
		
		if (!userDao.save(user)) {
			result.code = ResultInfo.ERROR_SQL;
			result.msg = "系统异常，请联系管理员";
			
			return result;
		}
		
		setCurrUser(user.id);
		flushUserCache(user.id);
		
		result.code = 1;
		result.msg = "登录成功";
		result.obj = user;
		
		return result;
	}
	
	/**
	 * 设置用户登陆信息
	 *
	 * @param userId
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月14日
	 */
	private void setCurrUser(long userId) {
		if (Session.current() == null) {
			
			return;
		}
		
		String sessionId = Session.current().getId();
		/* 设置用户凭证 */
		Cache.set(CacheKey.FRONT_ + sessionId, userId, Constants.CACHE_TIME_MINUS_30);

		return;
	}
	
	/**
	 * 刷新用户缓存信息
	 *
	 * @param userId
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月14日
	 */
	public void flushUserCache(long userId){
		CurrUser currUser = new CurrUser();
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		if (userInfo == null) {

			return;
		}
		
		currUser.id = userId;
		currUser.name = userInfo.name; 
		currUser.photo = userInfo.photo; 
		currUser.payment_account = userFund.payment_account;
		currUser.is_email_verified = userInfo.is_email_verified;
		
		
		/* 判断该用户是否实名认证 */
		if (!StringUtils.isBlank(userInfo.reality_name) && !StringUtils.isBlank(userInfo.id_number)) {
			currUser.is_real_name = true;
		}
		
		/* 判断该用户是否绑定手机 */
		if (!StringUtils.isBlank(userInfo.mobile)) {
			currUser.is_mobile = true;
		}
		
		/* 判断该用户是否绑定银行卡 */
		List<t_bank_card_user> cardList = bankCardUserService.queryCardByUserId(userId);
		if (cardList.size() > 0) {
			currUser.is_bank_card = true;	
		}
		/* 获取该用户信用等级信息 */
		t_credit_level level = creditLevelService.queryLevelByCreditScore(userInfo.credit_score);
		currUser.credit_level = level.name;
		currUser.credit_level_picture = level.image_url;
		
		/* 刷新用户缓存信息 */
		Cache.set(CacheKey.USER_ + userId, currUser, Constants.CACHE_TIME_MINUS_30);
		
		return;
	}
	
	/**
	 * 退出时清除cookie和缓存
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月25日
	 */
	public void removeCurrUser() {
		if (Session.current() == null) {
			
			return;
		}
		
		String sessionId = Session.current().getId();
		
		String userId = Cache.get(CacheKey.FRONT_ + sessionId).toString();
		
		//清除登陆凭证
		Cache.delete(CacheKey.FRONT_ + sessionId);  
		
		Object isSimulated = Cache.get(CacheKey.SIMULATED_ + sessionId);	
		if(isSimulated != null){
			Cache.delete(CacheKey.SIMULATED_ + sessionId);
		}
		
		removeUser(userId);
	}
	
	/**
	 * 清除用户缓存
	 *
	 * @author huangyunsong
	 * @createDate 2016年3月3日
	 */
	public void removeUser (String userId) {
		//清除用户缓存信息
//		Cache.delete(CacheKey.USER_ + userId);
				
		ReentrantLockUtil.cleanCacheLock(CacheKey.USER_FUND_LOCK_ + userId);
	}
	
	
    /**
	 * 判断一个手机号是否已经存在，存在则返回true，不存在返回false
	 * 
	 * @param mobile 手机号码
	 * @return 返回Boolean值，存在返回true，不存在false
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月16日
	 */
	public boolean isMobileExists(String mobile) {
		t_user user = userDao.findByColumn("mobile=?",mobile);
		if (user != null) {
			return true;
		}
		return false;
	}

	/**
	 * 判断一个用户名是否已经存在，存在则返回true，不存在返回false
	 * 
	 * @param name 手机号码
	 * @return 返回Boolean值，存在返回true，不存在false
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月16日
	 */
	public boolean isNameExists(String name) {
		t_user user = userDao.findByColumn("name=?",name);
		if (user != null) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 通过手机号码找回用户信息
	 * 
	 * @param mobile 手机号码
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月17日
	 */
	public ResultInfo findUserInfoByMobile(String mobile) {
		
		ResultInfo result = new ResultInfo();
		
		/* 判断手机号码是否注册 */
		if (!isMobileExists(mobile)) {
			result.code = -1;
			result.msg = "手机号码未注册";
			
			return result;
		}
		
		t_user user = userDao.findByColumn("mobile=?",mobile);
		result.obj = user;
		
		return result;
	}

	/**
	 * 查询注册表用户实体信息 通过userId查询
	 * 
	 * @param id 用户id
	 * @return 
	 *
	 * @author liudong
	 * @createDate 2015年12月17日
	 */
	public t_user findUserById(long id) {
		
		return userDao.findUserById(id);
	}
	

	/**
	 * 获取用户信用情况
	 * 
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月6日
	 *
	 */
	public UserDetail findCreditSituation(long userId) {
		
		return userDao.findCreditSituation(userId);
	}
	
	/**
	 * 获取ShowUserInfo统计的字段
	 * 
	 * @param userId
	 * @param showType 用户类型
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月7日
	 *
	 */
	public UserFundStatistic findUserFundStatistic(long userId, int showType, String userName) {
		
		return userDao.findUserFundStatistic(userId, showType, userName);
	}
	
	/**
	 * 用户顶部资产信息
	 * 
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月20日
	 *
	 */
	public UserFundInfo findUserFundInfo(long userId) {
		UserFundInfo userFundInfo = userDao.findUserFundInfo(userId);
		
		/* 计算总资产 */
		userFundInfo.total_assets = userFundInfo.balance + userFundInfo.freeze - 
					userFundInfo.no_repayment + userFundInfo.no_receive + userFundInfo.reward;
		
		return userFundInfo;
	}
	
	/**
	 * 查询相应时间注册会员数目的数据
	 * 
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月23日
	 *
	 */
	public int findUserCount(String startTime, String endTime, int type) {
		
		return userDao.findUserCount(startTime, endTime, type);
	}
	
	/****
	 * 查询用户原始密码
	 * @param userId
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-17
	 */
	public String findUserOldPassWord(long userId){
		
		return userDao.findUserOldPassWord(userId);
	}
	
	/**
	 * 统计用户数
	 * 
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月8日
	 *
	 */
	public int queryTotalRegisterUserCount() {

		return userDao.findCountOfUsers();
	}
	
	/**
	 * 后台发标，获取关联用户
	 *
	 * @param key
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月9日
	 */
	public PageBean<Map<String, Object>> queryCreateBidRefUser(int currPage, int pageSize, String key){
		
		return userDao.queryCreateBidRefUser(currPage, pageSize, key);
	}
	
	/**
	 * 后台发红包，分页查询所有用户
	 *
	 * @return
	 *
	 * @author LiuPengnwei
	 * @createDate 2018年5月21日14:21:20
	 */
	public PageBean<Map<String, Object>> queryredPacketUser(int currPage, int pageSize, String key){
		
		return userDao.queryredPacketUser(currPage, pageSize, key);
	}
	
	/**
	 * 通过手机号码获取用户ID
	 * 
	 * @param mobile
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 */
	public long queryIdByMobile(String mobile) {
		
		return userDao.findIdByMobile(mobile);
	}
	
	/**
	 * 统计用户数
	 * 
	 * @param showType 筛选类型  0-所有;1-借款会员;2-理财会员;3-新用户;4-复合会员;5-正常会员;6-锁定
	 * @param currPage
	 * @param pageSize
	 * @param exports 1:导出    default：不导出
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月8日
	 *
	 */
	public PageBean<ShowUserInfo> pageOfUserInfo(int showType, int currPage, int pageSize, int orderType, int orderValue, String userName, int exports) {
		
		return userDao.pageOfUserInfo(showType, currPage, pageSize, orderType, orderValue, userName, exports);
	}

	/**
	 * 后台发标，获取关联用户
	 *
	 * @param key
	 * @param id 用户userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月9日
	 */
	public ResultInfo updatePassword(long id, String password) {
		ResultInfo result = new ResultInfo();
		int i = userDao.updatePassowordById(id, password);
		if (i < 0) {
			result.code = -1;
			result.msg = "密码修改失败";
			
			return result;
		}
		
		t_user user = userDao.findByID(id);
		
		/** 发送通知 */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		noticeService.sendSysNotice(id, NoticeScene.RESET_PASSWORD_SUCC, param);
		
		result.code = 1;
		result.msg = "密码修改成功";
		
		return result;
	}
	
	/**
	 * 修改用户手机号码
	 * 
	 * @param id 用户user_id
	 * @param mobile 手机号码
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 */
	public ResultInfo updateUserMobile(long id, String mobile) {
		ResultInfo result = new ResultInfo();
		
		/* 判断手机号码是否存在 */
		if (isMobileExists(mobile)) {
			result.code = -1;
			result.msg = "手机号码已存在";
			return result;
		}
		/* 修改用户信息表中的手机号字段 */
		result = userInfoService.updateUserMobile(id, mobile);
		if (result.code < 0) {

			return result;
		}
		/* 修改用户表中的手机号字段 */
		int i = userDao.updateUserMobile(id, mobile);
		if (i < 0) {
			result.code = -1;
			result.msg = "手机号码修改失败";
			return result;
		}
		
		t_user user = userDao.findByID(id);
		
		/** 手机号绑定更改 发送通知  */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		noticeService.sendSysNotice(id, NoticeScene.BIND_MOBILE_SUCC, param);
		
		result.code = 1;
		result.msg = "手机号码修改成功";
		/* 刷新缓存用户信息 */
		flushUserCache(id);
		
		return result;
	}

	/**
	 * 后台管理员锁定、解锁用户
	 * 
	 * @param userId
	 * @return
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 */
	public ResultInfo updateIsAllowLogin(long userId) {
		ResultInfo result = new ResultInfo();
		int i = userDao.updateUserIsAllowLogin(userId);
		if (i < 0) {
			result.code = -1;
			result.msg= "修改用户锁定状态失败";
			
			return result;
		}
		result.code = 1;
		result.msg= "修改用户锁定状态成功";
		
		return result;
	}

	/**
	 * 修改用户头像
	 * 
	 * @param userId 用户Id
	 * @param photo 头像
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月25日
	 */
	public ResultInfo updateUserPhoto(long userId, String photo) {
		ResultInfo result = new ResultInfo();
		if (StringUtils.isBlank(photo)) {
			result.code = -1;
			result.msg = "头像路径为空";
			
			return result;
		}
		
		int i = userDao.updateUserPhoto(userId, photo);
		if (i < 0) {
			result.code = -1;
			result.msg = "头像修改失败";
			
			return result;
		}
		result.code = 1;
		result.msg = "头像修改成功";
		
		return result;
	}

	/**
	 * 修改会员退出信息
	 * 
	 * @param userId 用户id
	 * @param client 上次登录入口
	 * @param ip 
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月28日
	 *
	 */
	public ResultInfo updateUserLoginOutInfo(long userId, int client, String ip) {
		ResultInfo result = new ResultInfo();
		int i = userDao.updateUserLastLoginInfo(userId, client, ip);
		if (i < 0) {
			result.code =-1;
			result.msg = "修改用户登出信息失败";
			
			return result;
		}
		/* 清除缓存 */
		removeCurrUser();
		
		result.code = 1;
		result.msg = "修改用户登出信息成功";
		
		return result;
	}
	
	/**
	 * 随机生成两个英文字母,并与手机号码连接,获得用户名
	 * @description 
	 * 
	 * @param mobile 手机号码
	 * @return
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月16日
	 */
	public String userNameFactory(String mobile){
	    Random random = new Random();
	    StringBuffer word = new StringBuffer();
	    for( int i = 0; i < 2; i ++) {  
	    	/* 取得大写或小写 */   
	        int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
	        word.append((char)(choice + random.nextInt(26)));   
	    }
	    
	    /* 获取系统设置的否定词 */
	    String sensitiveWords = settingService.findSettingValueByKey(SettingKey.SENSITIVE_WORDS);
		if (!StringUtils.isBlank(sensitiveWords)) {
		    if (sensitiveWords.contains(word)) {
				userNameFactory(mobile);
			}
		}
		
		word.append(mobile);
	    
		return word.toString();
	}
	
	
	/**
	 * 查询推广会员
	 * @param currPage
	 * @param pageSize
	 * @param supervisorId
	 * @return
	 */
	public PageBean<t_user> queryBySupervisorList(int currPage,int pageSize,long supervisorId,int exports){
		PageBean<t_user> page = new PageBean<t_user>();
		Map<String, Object> args = new HashMap<String, Object>();
		
		if(currPage<=0){
			currPage=1;
		}
		String sql ="supervisor_id=?";
		
		if(exports==1){
			
			sql ="select * from t_user where supervisor_id=:supervisor_id";
			args.put("supervisor_id", supervisorId);
			
			page.page = userDao.findListBySQL(sql, args);
			return page;
		}
		
		return userDao.pageOfByColumn(currPage, pageSize, sql, supervisorId);
		
		
	}
	
	//liuyang begin 2016-12-30-----------------------------
	/**
	 * 分配离职员工的推广会员
	 * @param id
	 * @param supervisorId
	 * @return
	 */
	public void updateBySupervisorId(long id,long supervisor_id){
		String sql = " update t_user set supervisor_id=:supervisor_id where id=:id ";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("id", id);
		args.put("supervisor_id", supervisor_id);
		userDao.updateBySQL(sql, args);
	}
	//liuyang end 2016-12-30-----------------------------
	
	/**
	 * 根据Mobile 查询用户id
	 * 
	 * @param mobile 电话号
	 * @author liupengwei
	 * @createDate 2017年7月3日
	 * @return
	 */
	public t_user queryByMobile(String mobile){
		
		return userDao.queryByMobile(mobile);
	}
	
	/**
	 * 更新用户代理商推广码
	 * 
	 * @param userId
	 * @param saleManId
	 */
	public boolean updateUserById(long userId, long saleManId) {
		
		String sql = "UPDATE t_user SET proxy_salesMan_id = :saleManId WHERE id = :userId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		condition.put("saleManId", saleManId);
		
		return userDao.updateBySQL(sql, condition) == 1 ? true : false;
	}
	
	public ResultInfo registering(String name, String mobile, String password, Client client, String ip,long supervisorId,long proxyExtensionCode) {
		ResultInfo result = new ResultInfo();
		
		t_user user = new t_user(new Date(), name, mobile, password);
		user.login_count = 1;
		user.last_login_time = new Date();
		user.setLast_login_client(client);
		user.is_allow_login = true;
		user.last_login_ip = ip;
		user.supervisor_id = supervisorId;
		user.proxy_salesMan_id = proxyExtensionCode;
		/* 保存用户注册信息 */
		boolean isSaveUser = userDao.save(user);
		if (!isSaveUser) {
			result.code = -1;
			result.msg = "用户注册信息失败";
			
			return result;
		}
		
		/* 初始化用户基本信息 */
		boolean isSaveUInfo = userInfoService.addUserInfo(user.id, client, mobile, name);
		if (!isSaveUInfo) {
			result.code = -1;
			result.msg = "用户基本信息初始化失败";
			
			return result;
		}
		
		/* 初始化用户资金信息 */
		boolean isSaveUFund = userFundService.addUserFundInfo(user.id, name);
		if (!isSaveUFund) {
			result.code = -1;
			result.msg = "用户资金信息初始化失败";
			
			return result;
		}
		
		/* 用户资产签名更新 */
		result = userFundService.userFundSignUpdate(user.id);
		
		if (result.code < 1) {
			
			return result;
		}
		
		
		t_mall_scroe_rule rule = t_mall_scroe_rule.find("type=1").first();
		
		if(rule !=null){
			MallScroeRecord.saveScroeRegistereRecord(user, rule.scroe, result);
		}
		
		result.code = 1;
		result.msg = "用户注册成功";
		result.obj = user;
		
		/** 发送通知 */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		noticeService.sendSysNotice(user.id, NoticeScene.REGISTER_SUCC, param);
		
		/** 注册成功，设置用户为登陆状态 */
		setCurrUser(user.id);
		flushUserCache(user.id);
		
		return result;
	}
	
	/**
	 * 
	 * @Title: findUserMobile
	 *
	 * @description 登陆时,通过mobile查询id(t_user表中)
	 *
	 * @param mobile登陆人手机号
	 * @param @return 
	 * 
	 * @return t_user    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年11月6日
	 */
	public t_user findUserMobile(String  mobile){
		
		return userDao.findUserMobile(mobile);
	}
	
}
