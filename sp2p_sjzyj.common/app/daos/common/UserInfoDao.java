package daos.common;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import common.constants.Constants;
import common.enums.NoticeScene;
import common.utils.PageBean;
import models.common.bean.UserInfoServices;
import models.common.entity.t_company_style;
import models.common.entity.t_deal_user;
import models.common.entity.t_user_info;
import models.core.entity.t_bid;
import daos.base.BaseDao;

/**
 * 用户基本信息dao实现
 * 
 * @description
 *
 * @author ChenZhipeng
 * @createDate 2015年12月21日
 */
public class UserInfoDao extends BaseDao<t_user_info> {
	protected UserInfoDao() {
	}
	
	/**
	 * 查询相应时间理财会员数目的数据
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
		String sql = null;
		String hour = null;
		Map<String, Object> condition = new HashMap<String, Object>();
		if (type == Constants.YESTERDAY) {
			sql = "SELECT COUNT(1) FROM t_user t, t_user_info tui WHERE t.id = tui.user_id AND (tui.member_type =:memberType1 OR tui.member_type =:memberType2) AND TO_DAYS(:nowTime) - TO_DAYS(tui.invest_member_time) = 1 AND HOUR(tui.invest_member_time) <:hour";
			if (endTime.contains(":")) {
				hour = endTime.substring(0, endTime.indexOf(":"));
				if("00".equals(hour)){
					hour = "24";
				}
			}
			
			condition.put("memberType1", t_user_info.MemberType.FINANCIAL_MEMBER.code);
			condition.put("memberType2", t_user_info.MemberType.COMPOSITE_MEMBERS.code);
			condition.put("nowTime", new Date());
			condition.put("hour", hour);
		}else{ 
			sql="SELECT COUNT(1) FROM t_user t, t_user_info tui WHERE t.id = tui.user_id AND (tui.member_type =:memberType1 OR tui.member_type =:memberType2) AND tui.invest_member_time BETWEEN :startTime AND :endTime ";
			condition.put("memberType1", t_user_info.MemberType.FINANCIAL_MEMBER.code);
			condition.put("memberType2", t_user_info.MemberType.COMPOSITE_MEMBERS.code);
			condition.put("startTime", startTime);
			condition.put("endTime", endTime);
		}
		
		return findSingleIntBySQL(sql, 0, condition);
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
		String sql = "SELECT add_base_info_schedule FROM t_user_info WHERE user_id = :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		int schedule = Convert.strToInt(findSingleBySQL(sql, condition).toString(), 0);
		return schedule;
	}
	
	/**
	 * 获取用户信用额度
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public double findUserCreditLine(long userId) {
		
		String sql = "SELECT credit_line FROM t_user_info WHERE user_id=:user_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", userId);
		
		return super.findSingleDoubleBySQL(sql, 0.00, params);
	}

	/**
	 * 根据邮箱查询用户基本信息
	 * 
	 * @param email email地址
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 *
	 */
	public t_user_info findUserInfoByEmail(String email) {
		String sql = "SELECT * FROM t_user_info WHERE email =:email";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("email", email);

		return findBySQL(sql, condition);
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
	 *
	 */
	public int updateEmergencyContact(long userId, String name, int type, String mobile) {
		String sql = "UPDATE t_user_info SET emergency_contact_type =:type, emergency_contact_name =:name, emergency_contact_mobile =:mobile WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		condition.put("name", name);
		condition.put("mobile", mobile);
		condition.put("userId", userId);
		
		return updateBySQL(sql, condition);
	}
	

	/**
	 * 修改用户资产信息
	 * 
	 * @param userId 用户Id
	 * @param workExperience 工作经验
	 * @param annualIncome 年收入
	 * @param netAsset 资产估值
	 * @param car 车产
	 * @param house 房产
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 *
	 */
	public int updateUserAssetsInfo(long userId, int workExperience,
			int annualIncome, int netAsset, int car, int house) {
		String sql = "UPDATE t_user_info SET work_experience =:workExperience, annual_income=:annualIncome,net_asset=:netAsset,car=:car,house=:house WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("workExperience", workExperience);
		condition.put("annualIncome", annualIncome);
		condition.put("netAsset", netAsset);
		condition.put("car", car);
		condition.put("house", house);
		condition.put("userId", userId);

		return updateBySQL(sql, condition);
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
	 *
	 */
	public int updateUserBasicInfo(long userId, int education, int marital) {
		String sql = "UPDATE t_user_info SET education =:education, marital=:marital WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("education", education);
		condition.put("marital", marital);
		condition.put("userId", userId);

		return updateBySQL(sql, condition);
	}
	
	/**
	 * 修改邮箱地址
	 * 
	 * @param userId 用户id
	 * @param email email地址
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 *
	 */
	public int updateUserEmail(long userId, String email) {
		String sql = "UPDATE t_user_info SET email =:email, is_email_verified =:emailVerified WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("email", email);
		condition.put("emailVerified", true);
		condition.put("userId", userId);

		return updateBySQL(sql, condition);
	}
	
	/**
	 * 修改会员信息的手机号码
	 * @param userId
	 * @param mobile
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月7日
	 *
	 */
	public int updateUserInfoMobile(long userId, String mobile) {
		String sql = "UPDATE t_user_info SET mobile =:mobile WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("mobile", mobile);
		condition.put("userId", userId);

		return updateBySQL(sql, condition);
	}
	
	/**
	 * 更新用户信息完成进度
	 * @param userId
	 * @param schedule
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月29日
	 *
	 */
	public int updateUserInfoSchedule(long userId, int schedule){
		String sql = "UPDATE t_user_info SET add_base_info_schedule =:schedule WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("schedule", schedule);
		condition.put("userId", userId);
		
		return updateBySQL(sql, condition);
	}
	

	/**
	 * 更新会员类型
	 *
	 * @param userId 用户ID
	 * @param newMemberType 新类型
	 * @param oldMemberType 旧类型
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public boolean updateUserMemberType(long userId, t_user_info.MemberType newMemberType, t_user_info.MemberType oldMemberType) {
		if (!t_user_info.MemberType.FINANCIAL_MEMBER.equals(newMemberType)
				&& !t_user_info.MemberType.BORROW_MEMBER.equals(newMemberType)) {
			throw new InvalidParameterException("新的会员类型必须是借款会员或者理财会员");
		}
		
		StringBuffer sql = new StringBuffer("UPDATE t_user_info SET member_type = :newMemberType ");
		
		switch (newMemberType) {
		case FINANCIAL_MEMBER:{
			if (t_user_info.MemberType.NEW_MEMBER.equals(oldMemberType)) {  //新会员-》理财会员
				sql.append(", invest_member_time = :time ");
				break;
			}
			if (t_user_info.MemberType.FINANCIAL_MEMBER.equals(oldMemberType) 
					|| t_user_info.MemberType.COMPOSITE_MEMBERS.equals(oldMemberType)) { //已经是理财会员或复合会员
				
				return true;
			} 
			if (t_user_info.MemberType.BORROW_MEMBER.equals(oldMemberType)) { //借款会员-》复合会员
				newMemberType = t_user_info.MemberType.COMPOSITE_MEMBERS;
				sql.append(", invest_member_time = :time ");
				break;
			}
			
			return false;
		}
		case BORROW_MEMBER:{
			if (t_user_info.MemberType.NEW_MEMBER.equals(oldMemberType)) {  //新会员-》借款会员
				sql.append(", loan_member_time = :time ");
				break;
			}
			if (t_user_info.MemberType.BORROW_MEMBER.equals(oldMemberType) 
					|| t_user_info.MemberType.COMPOSITE_MEMBERS.equals(oldMemberType)) { //已经是借款会员或复合会员
				
				return true;
			} 
			if (t_user_info.MemberType.FINANCIAL_MEMBER.equals(oldMemberType)) { //理财会员-》复合会员
				newMemberType = t_user_info.MemberType.COMPOSITE_MEMBERS;
				sql.append(", loan_member_time = :time ");
				break;
			}
			
			return false;
		}
		default:
			return false;
		}
		
		sql.append(" WHERE user_id = :userId");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("newMemberType", newMemberType.code);
		params.put("time", new Date());
		params.put("userId", userId);
		
		int row = updateBySQL(sql.toString(), params);
		
		if (row < 1) {
			
			return false;
		}
		
		return true;
	}

	
	/**
	 * 更新会员信息
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
	 * @param userId
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年3月31日
	 */
	public int updateUserInformation(long userId, int education, int marital, int workExperience, int annualIncome, int netAsset, int car,
			int house, int emergencyContactType, String emergencyContactName, String emergencyContactMobile) {
		
		String sql = "UPDATE t_user_info SET education =:education, marital=:marital, work_experience =:workExperience, annual_income=:annualIncome, net_asset=:netAsset, car=:car, house=:house, emergency_contact_type =:emergencyContactType, emergency_contact_name =:emergencyContactName, emergency_contact_mobile =:emergencyContactMobile WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("education", education);
		condition.put("marital", marital);
		condition.put("workExperience", workExperience);
		condition.put("annualIncome", annualIncome);
		condition.put("netAsset", netAsset);
		condition.put("car", car);
		condition.put("house", house);
		condition.put("emergencyContactType", emergencyContactType);
		condition.put("emergencyContactName", emergencyContactName);
		condition.put("emergencyContactMobile", emergencyContactMobile);
		condition.put("userId", userId);
		

		return updateBySQL(sql, condition);
	}

	
	/**
	 * 查询接受系统消息的所有用户的user_id
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
		
		if (scene.maskable) { //用户是否可设置个该场景是否接收
			/**
				 SELECT 
				 	tui.user_id,
				 	IFNULL(( SELECT nsu.msg FROM t_notice_setting_user nsu WHERE nsu.user_id = tui.user_id AND nsu.msg=true AND nsu.scene =:scene), 1) AS msg 
				 FROM 
				 	t_user_info tui 
				 WHERE 1=1
			 */
			StringBuffer sql = new StringBuffer("SELECT tui.user_id,IFNULL(( SELECT nsu.msg FROM t_notice_setting_user nsu WHERE nsu.user_id = tui.user_id AND nsu.msg=true AND nsu.scene =:scene), 1) AS msg FROM t_user_info tui WHERE 1=1 ");			
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("scene", scene.code);
			if(memberType > -1 && memberType < 3){
				sql.append(" AND member_type =:member_type ");
				condition.put("member_type", memberType);
			}
			
			return super.findListMapBySQL(sql.toString(), condition);
		}else{
			StringBuffer sql = new StringBuffer("SELECT user_id, 1 AS msg FROM t_user_info WHERE 1=1 ");
			Map<String, Object> condition = new HashMap<String, Object>();
			if(memberType > -1 && memberType < 3){
				sql.append(" AND member_type =:member_type ");
				condition.put("member_type", memberType);
			}
			
			return super.findListMapBySQL(sql.toString(), condition);
		}
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
	public List<Map<String, Object>> queryUserNoticeSmsSetting(int memberType,NoticeScene scene) {
		
		if (scene.maskable) { //用户是否可设置个该场景是否接收
			/**
			SELECT 
				tui.mobile,
				IFNULL(( SELECT nsu.sms FROM t_notice_setting_user nsu WHERE nsu.user_id = tui.user_id AND nsu.sms=true AND nsu.scene =68), 1) AS sms 
			FROM 
				t_user_info tui 
			WHERE 
				tui.mobile<>''
			 */
			StringBuffer sql = new StringBuffer("SELECT tui.mobile,IFNULL(( SELECT nsu.sms FROM t_notice_setting_user nsu WHERE nsu.user_id = tui.user_id AND nsu.sms=true AND nsu.scene =:scene), 1) AS sms FROM t_user_info tui WHERE tui.mobile<>''  ");			
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("scene", scene.code);
			if(memberType > -1 && memberType < 3){
				sql.append(" AND member_type =:member_type ");
				condition.put("member_type", memberType);
			}
			
			return super.findListMapBySQL(sql.toString(), condition);
		}else{
			StringBuffer sql = new StringBuffer("SELECT mobile, 1 AS sms FROM t_user_info WHERE 1=1 ");
			Map<String, Object> condition = new HashMap<String, Object>();
			if(memberType > -1 && memberType < 3){
				sql.append(" AND member_type =:member_type ");
				condition.put("member_type", memberType);
			}
			
			return super.findListMapBySQL(sql.toString(), condition);
		}
	}
	
	/**
	 * 查询接受短信的所有用户的邮件地址
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
		if (scene.maskable) { //用户是否可设置个该场景是否接收
			/**
			 SELECT 
				tui.email,
				IFNULL(( SELECT nsu.email FROM t_notice_setting_user nsu WHERE nsu.user_id = tui.user_id AND nsu.email=true AND nsu.scene =:scene), 1) AS setting 
			 FROM 
				t_user_info tui 
			 WHERE tui.email<>''  		
			 */
			StringBuffer sql = new StringBuffer("SELECT tui.email,IFNULL(( SELECT nsu.email FROM t_notice_setting_user nsu WHERE nsu.user_id = tui.user_id AND nsu.email=true AND nsu.scene =:scene), 1) AS setting FROM t_user_info tui WHERE tui.email<>''  ");			
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("scene", scene.code);
			if(memberType > -1 && memberType < 3){
				sql.append(" AND member_type =:member_type ");
				condition.put("member_type", memberType);
			}
			
			return super.findListMapBySQL(sql.toString(), condition);
		}else{
			StringBuffer sql = new StringBuffer("SELECT email, 1 AS setting FROM t_user_info WHERE email<>'' ");
			Map<String, Object> condition = new HashMap<String, Object>();
			if(memberType > -1 && memberType < 3){
				sql.append(" AND member_type =:member_type ");
				condition.put("member_type", memberType);
			}
			
			return super.findListMapBySQL(sql.toString(), condition);
		}
	}
	
	/**
	 * 月初修改客户信息为老客户
	 * @return
	 */
	public int updateByAccount() {
		String sql = "UPDATE t_user_info SET is_account =:account WHERE is_account =1";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("account", 0);
		
		return updateBySQL(sql, condition);
	}
	
	/**
	 * 查询用户信息表
	 * @return
	 */
	public List<t_user_info> queryUserInfo() {
		
		String sql="SELECT * FROM t_user_info p WHERE p.is_account =:account";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("account", 1);
		
		return this.findListBySQL(sql, condition);
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
	 * @createDate 2018年10月8日-下午12:55:54
	 */
	public List<t_user_info> queryUserInfoAll() {
		String sql="SELECT * FROM t_user_info";
		Map<String, Object> condition = new HashMap<String, Object>();
		
		return this.findListBySQL(sql, condition);
	}
	
	/**
	 * 修改用户信息中首次理财情况
	 * @param userId
	 * @param first_money
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2016年1月7日
	 *
	 */
	public int updateUserInfoFirstMoney(long userId, long first_money) {
		String sql = "insert into t_user_info (first_money) values (?) WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("?", first_money);
		condition.put("userId", userId);

		return updateBySQL(sql, condition);
	}
	
	/**
	 * 注销开户信息
	 * 
	 * @author niu	
	 * @create 2017.10.17
	 */
	public int updCustInfo(long userId) {
		String sql = "UPDATE t_user_info SET reality_name = NULL, id_number = NULL, sex = 0, birthday = NULL, enterprise_name = NULL, enterprise_credit = NULL, enterprise_bank_no = NULL WHERE user_id = :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);

		return updateBySQL(sql, condition);
	}
	
	/**
	 * 反查失败 清除开户信息
	 * @author LiuPengwei
	 * @return
	 */
	public int updateUser(long userId) {
		String sql = "UPDATE t_user_info SET reality_name =:reality_name,id_number=:id_number,enterprise_name=:enterprise_name,"
				+ "enterprise_credit=:enterprise_credit,enterprise_bank_no=:enterprise_bank_no WHERE user_id =:user_id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("user_id", userId);
		condition.put("reality_name",null);
		condition.put("id_number",null);
		condition.put("enterprise_name",null);
		condition.put("enterprise_credit",null);
		condition.put("enterprise_bank_no",null);

		return updateBySQL(sql, condition);
	}
	
	/**
	 * 个人开户信息保存
	 * 
	 * @author LiuPengwei
	 * @return
	 */
	public int updateUserInfos(long userId,String reality_name, String id_number) {
		String sql = "UPDATE t_user_info SET reality_name =:reality_name,id_number=:id_number WHERE user_id =:user_id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		
		condition.put("reality_name",reality_name);
		condition.put("id_number",id_number);
		condition.put("user_id", userId);

		return updateBySQL(sql, condition);
	}
	
	/**
	 * 个人开户台账
	 * 
	 * @author LiuPengwei
	 * @return
	 */
	public int updateUsers(long userId) {
		String sql = "UPDATE t_user_info SET user_id = :user_id WHERE user_id =:user_id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("user_id", userId);
	


		return updateBySQL(sql, condition);
	}
	
	/**
	 * 判断是否为企业用户
	 * 
	 * @author niu
	 * @create 2017.10.20
	 */
	public boolean isEnterprise(long userId) {
		
		String sql = "SELECT * FROM t_user_info t WHERE (t.enterprise_name IS NOT NULL AND t.enterprise_name <> '') AND (t.enterprise_credit IS NOT NULL AND t.enterprise_name <> '') AND (t.enterprise_bank_no IS NOT NULL AND t.enterprise_name <> '') AND t.user_id = :userId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("userId", userId);
		
		return findBySQL(sql, condition) == null ? false : true;
	}
	
	/**
	 * 通过 userId 查找用户信息
	 * 
	 * @param userId
	 * @return
	 */
	public t_user_info findUserInfoByUserId(long userId) {
		
		String sql = "SELECT * FROM t_user_info tui WHERE tui.user_id = :userId";
		Map<String, Object> condition  = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return findBySQL(sql, condition);
				
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
		StringBuffer querySQL = new StringBuffer("SELECT s.id AS id,s.name AS name,s.mobile AS mobile,s.time as time,u.reality_name AS reality_name,u.service_type AS service_type,(SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) FROM t_bill_invest tbi WHERE tbi.user_id = u.user_id AND tbi.status = 0) as collectMoney FROM t_user_info u INNER JOIN t_user s ON u.user_id = s.id where 1=1");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(s.id) FROM t_user_info u INNER JOIN t_user s ON u.user_id = s.id where 1=1");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		switch (showType) {
			case 0:{//所有
				break;
			}
			case 1:{//开户
				querySQL.append("  AND u.reality_name is not null ");
				countSQL.append("  AND u.reality_name is not null ");
				break;
			}
			case 2:{//未开户
				querySQL.append("  AND u.reality_name is null ");
				countSQL.append("  AND u.reality_name is null ");
				break;
			}
		}
		
		//按电话查询
		if(StringUtils.isNotBlank(mobile)){
			querySQL.append(" AND s.mobile =:mobile ");
			countSQL.append(" AND s.mobile =:mobile ");
			condition.put("mobile", mobile);
		}
		
		querySQL.append(" AND s.supervisor_id =0 AND s.proxy_salesMan_id =0 AND u.service_type=0 order by s.time desc,u.service_type desc ");
		countSQL.append(" AND s.supervisor_id =0 AND s.proxy_salesMan_id =0 AND u.service_type=0 ");
		
		return this.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), UserInfoServices.class, condition);
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
		StringBuffer querySQL = new StringBuffer("SELECT s.id AS id,s.name AS name,s.mobile AS mobile,s.time as time,u.reality_name AS reality_name,u.service_type AS service_type,(SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) FROM t_bill_invest tbi WHERE tbi.user_id = u.user_id AND tbi.status = 0) as collectMoney FROM t_user_info u INNER JOIN t_user s ON u.user_id = s.id inner join t_service_user_relevance p on p.user_id = u.user_id where 1=1");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(s.id) FROM t_user_info u INNER JOIN t_user s ON u.user_id = s.id inner join t_service_user_relevance p on p.user_id = u.user_id where 1=1");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		switch (showType) {
			case 0:{//所有
				break;
			}
			case 1:{//开户
				querySQL.append("  AND u.reality_name is not null ");
				countSQL.append("  AND u.reality_name is not null ");
				break;
			}
			case 2:{//未开户
				querySQL.append("  AND u.reality_name is null ");
				countSQL.append("  AND u.reality_name is null ");
				break;
			}
		}
		
		//按电话查询
		if(StringUtils.isNotBlank(mobile)){
			querySQL.append(" AND s.mobile =:mobile ");
			countSQL.append(" AND s.mobile =:mobile ");
			condition.put("mobile", mobile);
		}
		
		if(serviceId != 0) {
			querySQL.append(" AND p.service_id =:serviceId ");
			countSQL.append(" AND p.service_id =:serviceId ");
			condition.put("serviceId", serviceId);
		}
		
		querySQL.append(" AND s.supervisor_id =0 AND s.proxy_salesMan_id =0 order by s.time desc,u.service_type desc ");
		countSQL.append(" AND s.supervisor_id =0 AND s.proxy_salesMan_id =0 ");
		
		return this.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), UserInfoServices.class, condition);
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
		String sql = "SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) FROM t_bill_invest tbi WHERE tbi.user_id = :userId AND tbi.status = 0 ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return findSingleDoubleBySQL(sql, 0.00, condition);
	}
	/**
	 * 
	 * @Title: getReality_nameByuserId
	 * @description 根据userID查询
	 * @param userid
	 * @return
	 * String
	 * @author likai
	 * @createDate 2018年10月29日 上午10:04:41
	 */
	public t_user_info getReality_nameByuserId(Long userid) {
		String sql = "SELECT * FROM t_user_info where user_id = :userid";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userid", userid);
		return this.findBySQL(sql, condition);
	}
	/**
	 * 
	 * @Title: getUserInfo
	 * 
	 * @description 根据真实姓名和身份证号码查询，判断该用户是否实名
	 * @param  reality_name
	 * @param id_number
	 * @return t_user_info    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月28日-下午6:43:32
	 */
	public t_user_info getUserInfo(String reality_name, String id_number) {
		String sql = "SELECT * FROM t_user_info where reality_name =:reality_name and id_number =:id_number and add_base_info_schedule=3";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("reality_name", reality_name);
		condition.put("id_number", id_number);
		return this.findBySQL(sql, condition);
	}
	
}
