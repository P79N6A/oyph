package services.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.common.bean.UserInfoServices;
import models.common.entity.t_user;
import models.common.entity.t_user_info;

import org.apache.commons.lang.StringUtils;

import services.base.BaseService;

import com.shove.Convert;
import com.sun.swing.internal.plaf.basic.resources.basic;

import common.constants.ConfConst;
import common.enums.Client;
import common.enums.Gender;
import common.enums.NoticeScene;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.common.UserDao;
import daos.common.UserInfoDao;

public class UserInfoService extends BaseService<t_user_info> {
	
	protected static UserInfoDao userInfoDao = Factory.getDao(UserInfoDao.class);
	
	protected static UserDao userDao = Factory.getDao(UserDao.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected UserInfoService(){
		super.basedao = userInfoDao;
	}

	/**
	 * 判断一个邮箱是否已经存在，存在则返回true，不存在返回false
	 * @param email
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月25日
	 */
	public boolean isEailExists(String email) {
		t_user_info userInfo = userInfoDao.findUserInfoByEmail(email);
		if (userInfo != null) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 获取用户信用等级
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public double findUserCreditLine(long userId){
		
		return userInfoDao.findUserCreditLine(userId);
	}
	
	/**
	 * 根据user_id查询t_user_info
	 * 
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 */
	public t_user_info findUserInfoByUserId(long userId) {
		
		return userInfoDao.findByColumn("user_id = ?", userId);
	}
	
	/**
	 * 查询相应时间理财会员数目的数据
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
	public int findFinancialUserCount(String startTime, String endTime, int type) {

		return userInfoDao.findFinancialUserCount(startTime, endTime, type);
	}
	
	/**
	 * 获取用户基本信息编辑进度
	 * 
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月29日
	 *
	 */
	public int findUserBasicSchedule(long userId){
		
		return userInfoDao.findUserBasicSchedule(userId);
	}
	
	/**
	 * 查询接受消息的所有用户的user_id
	 * 
	 * @param memberType 会员类型  -1-全部会员  0-新会员   1-理财会员    2-借款会员
	 * @param scene 通知场景
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public List<Map<String, Object>> queryUserNoticeMsgSetting(int memberType,NoticeScene scene) {	
		
		return userInfoDao.queryUserNoticeMsgSetting(memberType, scene);
	}
	
	/**
	 * 查询接受短信的所有用户的mobile
	 * 
	 * @param memberType 会员类型  -1-全部会员  0-新会员   1-理财会员    2-借款会员
	 * @param scene 通知场景
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public List<Map<String, Object>> queryUserNoticeSmsSetting(int memberType,NoticeScene scene){	
		
		return userInfoDao.queryUserNoticeSmsSetting(memberType, scene);
	}
	
	/**
	 * 查询接受邮件的所有用户的邮件地址
	 * 
	 * @param memberType 会员类型  -1-全部会员  0-新会员   1-理财会员    2-借款会员
	 * @param scene 通知场景
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public List<Map<String, Object>> queryUserNoticeEmailSetting(int memberType,NoticeScene scene) {
		
		return userInfoDao.queryUserNoticeEmailSetting(memberType, scene);
	}
	
	/**
	 * 添加用户基本信息
	 * 
	 * @param userId 用户id
	 * @param client 注册入口
	 * @param mobile 手机号码
	 * @param name 用户名
	 * @return
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月19日
	 */
	public boolean addUserInfo(long userId, Client client, String mobile, String name) {
		t_user_info userInfo = new t_user_info();
		userInfo.user_id = userId;
		userInfo.setClient(client.PC);
		userInfo.mobile = mobile;
		userInfo.name = name;
		userInfo.credit_line = Convert.strToDouble(ConfConst.CREDIT_LINE, 0.00);
		userInfo.credit_score = Convert.strToInt(ConfConst.CREDIT_SCORE, 0);
		userInfo.photo = "/public/common/defaultUser.png";
		userInfo.vip_grade_id = 1l;
		return userInfoDao.save(userInfo);
	}
	
	/**
	 * 修改用户邮箱地址
	 * 
	 * @param userId
	 * @param email
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 */
	public ResultInfo updateUserEmail(long userId, String email) {
		ResultInfo result = new ResultInfo();
		
		t_user_info userInfo = this.findByID(userId);
		if (userInfo == null) {
			result.code = -1;
			result.msg = "查询用户信息失败";
			
			return result;
		}
		
		/* 判断邮箱是否被其他用户绑定 */
		if (isEailExists(email) && email.equals(userInfo.email)) {
			result.code = -1;
			result.msg = "邮箱已被其他用户绑定";
			
			return result;
		}
		
		int i = userInfoDao.updateUserEmail(userId, email);
		if (i < 0) {
			result.code = -1;
			result.msg = "邮箱修改失败";
			return result;
		}
		
		/** 发送通知  */
		Map<String, Object> noticeParams = new HashMap<String, Object>();
		noticeParams.put("user_name", userInfo.name);
		noticeService.sendSysNotice(userId, NoticeScene.BIND_EMAIL_SUCC, noticeParams);
		
		result.code = 1;
		result.msg = "邮箱修改成功";
		/* 刷新用户缓存信息 */
		userService.flushUserCache(userId);
		
		return result;
	}
	
	/**
	 * 修改用户真实姓名、出生日期、性别
	 * （通过判断身份证号码获取性别和出生日期）
	 * 
	 * @param userId 用户Id
	 * @param realityName 真实姓名
	 * @param idNumber 身份证号码
	 * @return
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 */
	public ResultInfo updateUserInfo(t_user_info userInfo) {
		ResultInfo result = new ResultInfo();
		
		if (StringUtils.isBlank(userInfo.reality_name)) {
			result.code = -1;
			result.msg= "真实姓名不能为空";
			
			return result;
		}
		
		if (StringUtils.isBlank(userInfo.id_number)) {
			result.code = -1;
			result.msg= "身份证号码不能为空";
			
			return result;
		}
		/* 通过身份证号码获取出生日期 */
		userInfo.birthday = DateUtil.strToDate(userInfo.id_number.substring(6, 14), "yyyyMMdd");
		/* 通过身份证号码获取性别 */
		int sex = Convert.strToInt(userInfo.id_number.substring(16, 17), 0);
		if(sex % 2 == 0){
			userInfo.setSex(Gender.FEMALE);
		}else{
			userInfo.setSex(Gender.MALE);
		}
		
		boolean isFlag = userInfoDao.save(userInfo);
		if (!isFlag) {
			result.code = -1;
			result.msg= "用户实名认证信息添加失败";
			
			return result;
		}
		result.code = 1;
		result.msg= "用户实名认证信息添加成功";
		
		return result;
	}
	
	/**
	 * 修改用户基本信息
	 * 
	 * @param userId 用户Id
	 * @param education 学历
	 * @param marital  婚姻
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 */
	public ResultInfo updateUserBasicInfo(long userId, int education, int marital) {
		ResultInfo result = new ResultInfo();
		
		int i = userInfoDao.updateUserBasicInfo(userId, education, marital);
		if (i < 1) {
			result.code = -1;
			result.msg = "用户信息修改失败";
			
			return result;
		}
		/* 获取当前用户的信息完成进度 */
		int schedule = checkUserBasicSchedule(userId);
		i = userInfoDao.updateUserInfoSchedule(userId, schedule);
		if (i < 1) {
			result.code = -1;
			result.msg = "用户信息修改失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "用户信息修改成功";
		
		return result;
	}
	
	/**
	 * 修改用户资产信息
	 * 
	 * @param userId 用户Id
	 * @param workExperience 工作经验
	 * @param annualIncome 年收入
	 * @param netAsset 净资产
	 * @param car 车产
	 * @param house 房产
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 */
	public ResultInfo updateUserAssetsInfo(long userId, int workExperience,
			int annualIncome, int netAsset, int car, int house) {
		ResultInfo result = new ResultInfo();

		int i = userInfoDao.updateUserAssetsInfo(userId, workExperience, annualIncome, netAsset, car, house);
		if (i < 0) {
			result.code = -1;
			result.msg = "用户资产信息修改失败";
			
			return result;
		}
		/* 获取当前用户的信息完成进度 */
		int schedule = checkUserBasicSchedule(userId);
		i = userInfoDao.updateUserInfoSchedule(userId, schedule);
		if (i < 0) {
			result.code = -1;
			result.msg = "用户资产信息修改失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "用户资产信息修改成功";
		
		return result;
	}
	
	/**
	 * 修改用户紧急联系人
	 * 
	 * @param userId 用户Id
	 * @param name 紧急联系人姓名
	 * @param type 紧急联系人类型
	 * @param mobile 紧急联系人手机号码
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 */
	public ResultInfo updateEmergencyContact(long userId, String name, int type,
			String mobile) {
		ResultInfo result = new ResultInfo();
		
		/* 判断否是为本人的手机号码 */
		t_user user = userService.findUserById(userId);
		if (user.mobile.equals(mobile)) {
			result.code = -1;
			result.msg = "不能使用本人的手机号码作为紧急联系人手机号";
			
			return result;
		}
		
		int i = userInfoDao.updateEmergencyContact(userId, name, type, mobile);
		if (i < 0) {
			result.code = -1;
			result.msg = "紧急联系人信息修改失败";
			
			return result;
		}
		
		/* 获取当前用户的信息完成进度 */
		int schedule = checkUserBasicSchedule(userId);
		i = userInfoDao.updateUserInfoSchedule(userId, schedule);
		if (i < 0) {
			result.code = -1;
			result.msg = "紧急联系人信息修改失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "紧急联系人信息修改成功";
		
		return result;
	}
	
	/**
	 * 更新会员类型
	 *
	 * @param userId 用户ID
	 * @param memberType 会员类型枚举
	 *
	 * @author yaoyi
	 * @createDate 2015年12月25日
	 */
	public boolean updateUserMemberType(long userId, t_user_info.MemberType memberType) {
		t_user_info userInfo = findUserInfoByUserId(userId);
		if (userInfo == null) {
			LoggerUtil.info(true, "更新会员类型时，用户信息不存在。[userId:%s]", userId);
			
			return false;
		}

		return userInfoDao.updateUserMemberType(userId, memberType, userInfo.getMember_type());
	}

	/**
	 * 修改用户信息中手机号码
	 * 
	 * @param userId
	 * @param mobile
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月7日
	 *
	 */
	public ResultInfo updateUserMobile(long userId, String mobile) {
		ResultInfo result = new ResultInfo();
		int i = userInfoDao.updateUserInfoMobile(userId, mobile);
		if (i < 0) {
			result.code = -1;
			result.msg = "手机号码修改失败";
			return result;
		}
		result.code = 1;
		result.msg ="手机号码修改成功";
		
		return result;
	}
	
	/**
	 * 获取当前用户信息完成进度
	 * 
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月29日
	 *
	 */
	private int checkUserBasicSchedule(long userId){
		t_user_info userInfo = findUserInfoByUserId(userId);
		int schedule = 0;
		
		/* 基本信息进度 */
		if(userInfo.getEducation() != null && userInfo.getMarital() != null){
			schedule += 1;
		}
		
		/* 资产信息进度 */
		if(userInfo.getWork_experience() != null && userInfo.getAnnual_income() != null && userInfo.getNet_asset() != null && userInfo.getCar() != null 
					&& userInfo.getHouse() != null){
			schedule += 1;
		}
		
		/* 紧急联系人信息进度 */
		if(userInfo.getEmergency_contact_type() != null && StringUtils.isNotBlank(userInfo.emergency_contact_name) && StringUtils.isNotBlank(userInfo.emergency_contact_mobile)){
			schedule += 1;
		}
		
		return schedule;
	}
	
	
	/**
	 * 更新用户会员信息
	 *
	 * @param userId
	 * @param education
	 * @param marital
	 * @param workExperience
	 * @param annualIncome
	 * @param netAsset
	 * @param car
	 * @param house
	 * @param userSign
	 * @param emergencyContactType
	 * @param emergencyContactName
	 * @param emergencyContactMobile
	 * @return
	 *
	 * @author songjia
	 * @createDate 2016年3月31日
	 */
	public ResultInfo updateUserInformation(long userId, int education, int marital, int workExperience, int annualIncome, int netAsset,
			int car, int house, int emergencyContactType, String emergencyContactName, String emergencyContactMobile) {
		ResultInfo result = new ResultInfo();
		
		int i = userInfoDao.updateUserInformation(userId, education, marital, workExperience, annualIncome, netAsset, car, house, emergencyContactType, emergencyContactName, emergencyContactMobile);
		if (i < 0) {
			result.code = -1;
			result.msg = "会员信息修改失败";
			
			return result;
		}
		
		/* 获取当前用户的信息完成进度 */
		int schedule = checkUserBasicSchedule(userId);
		i = userInfoDao.updateUserInfoSchedule(userId, schedule);
		if (i < 0) {
			result.code = -1;
			result.msg = "会员信息修改失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "会员信息修改修改成功";
		
		return result;
	}
	
	/**
	 * 月初修改客户信息为老客户
	 * @return
	 */
	public int updateByAccount() {
		
		return userInfoDao.updateByAccount();
	}
	
	/**
	 * 查询用户信息表
	 * @return
	 */
	public List<t_user_info> queryUserInfo() {
		
		return userInfoDao.queryUserInfo();
	}
	
	/**
	 * 查询所有用户信息
	 *
	 * @Title: queryUserInfoAll
	
	 * @description 
	 *
	 * @param @return 
	   
	 * @return List<t_user_info>    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月8日-下午12:58:10
	 */
	public List<t_user_info> queryUserInfoAll() {
		
		return userInfoDao.queryUserInfoAll();
	}
	
	/**
	 * 修改用户信息中首次理财情况
	 * 
	 * @param userId
	 * @param first_money
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年7月5日
	 *
	 */
	public ResultInfo updateUserFirstMoney(long userId, long first_money) {
		ResultInfo result = new ResultInfo();
		int i = userInfoDao.updateUserInfoFirstMoney(userId, first_money);
		if (i < 0) {
			result.code = -1;
			result.msg = "首次理财情况修改失败";
			return result;
		}
		result.code = 1;
		result.msg ="首次理财情况修改成功";
		
		return result;
	}
	
	/**
	 * 注销修改个人信息
	 * 
	 * @author niu
	 * @create 2017.10.17
	 */
	public ResultInfo cancelUpdUsrInfo(long userId) {
		ResultInfo result = new ResultInfo();
		int i = userInfoDao.updCustInfo(userId);
		if (i < 0) {
			result.code = -1;
			result.msg  = "注销修改个人信息失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "注销修改个人信息成功";
		
		return result;
	}
	
	/**
	 * 修改身份证和真是姓名
	 * 
	 * @author LiuPengwei
	 * @return
	 */
	public static int updateUser(long userId) {
		
		return userInfoDao.updateUser(userId);
	}
	
	/**
	 * 先添加记录
	 * 
	 * @author LiuPengwei
	 * @return
	 */
	public static int updateUserInfos(long userId,String reality_name, String id_number) {
		
		return userInfoDao.updateUserInfos(userId, reality_name, id_number);
	}
	
	/**
	 * 是否为企业用户
	 * 
	 * @author niu
	 * @create 2017.10.20
	 */
	public boolean isEnterprise(long userId) {
		
		return userInfoDao.isEnterprise(userId);
	}
	
	/**
	 * 保存用户风险评估登记
	 * 
	 * @param userId
	 * @param grade
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月6日 11:21:18
	 */
	public boolean userCredit(long userId, int grade) {
		
		t_user_info userInfo = userInfoDao.findByColumn("user_id = ?", userId);
		userInfo.credit_id = grade;
		
		return userInfoDao.save(userInfo);
	}
	
	/**
	 *  分页查询，会员列表  
	 *
	 * @param showType 筛选类型    0-所有;1-开户;2-未开户
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param mobile 电话
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年07月10日
	 */
	public PageBean<UserInfoServices> pageOfUserList(int showType, int currPage, int pageSize, String mobile) {
		
		return userInfoDao.pageOfUserList(showType, currPage, pageSize, mobile);
	}
	
	/**
	 *  分页查询，会员列表  
	 *
	 * @param showType 筛选类型    0-所有;1-开户;2-未开户
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param mobile 电话
	 * @param serviceId 客服id
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年07月10日
	 */
	public PageBean<UserInfoServices> pageOfServiceUserList(int showType, int currPage, int pageSize, String mobile, long serviceId) {
		
		return userInfoDao.pageOfServiceUserList(showType, currPage, pageSize, mobile, serviceId);
	}
	
	/**
	 *  查询会员待收金额  
	 *
	 * @param userId 用户id
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年07月15日
	 */
	public double findCollectByUser(long userId) {
		
		return userInfoDao.findCollectByUser(userId);
	}
	/**
	 * 
	 * @Title: getReality_nameByuserId
	 * @description 根据userID查询
	 * @param userid
	 * @return
	 * String
	 * @author likai
	 * @createDate 2018年10月29日 上午10:03:59
	 */
	public t_user_info getReality_nameByuserId(Long userid) {
		// TODO Auto-generated method stub
		return userInfoDao.getReality_nameByuserId(userid);
	}
	
	/**
	 * 
	 * @Title: getUserInfo
	 * 
	 * @description  根据真实姓名和身份证号码查询，判断该用户是否实名
	 * @param reality_name
	 * @param id_number
	 * @return t_user_info    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月28日-下午6:44:25
	 */
	public t_user_info getUserInfo(String reality_name,String id_number){
		
		return userInfoDao.getUserInfo(reality_name,id_number);
	}
	/**
	 * 
	 * @Title: insertUserInfo
	 * 
	 * @description 保存信息返回成功返回true
	 * @param  user_info
	 * @return boolean    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月7日-下午4:34:39
	 */
	public boolean insertUserInfo(t_user_info user_info) {
		
		return userInfoDao.save(user_info);
	}
}















