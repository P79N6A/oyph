package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.common.bean.ShowUserInfo;
import models.common.bean.UserDetail;
import models.common.bean.UserFundInfo;
import models.common.bean.UserFundStatistic;
import models.common.entity.t_deal_user;
import models.common.entity.t_user;
import models.common.entity.t_user_info;

import org.apache.commons.lang.StringUtils;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;

/**
 * 用户注册、登录dao实现
 *
 * @description
 * 
 * @author ChenZhipeng
 * @createDate 2015年12月15日
 */
public class UserDao extends BaseDao<t_user> {

	protected UserDao() {
	}


	/**
	 * 根据id修改用户信息
	 * 
	 * @param id
	 * @param passwordContinueFails 密码连续输入错误次数
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月16日
	 *
	 */
	public int updateLockStatus(long id, int pwdContinueFails) {
		String sql = "UPDATE t_user SET  password_continue_fails =:pwdContinueFails, is_password_locked=1, password_locked_time =:date WHERE id =:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("pwdContinueFails", pwdContinueFails);
		condition.put("date", new Date());
		condition.put("id", id);

		return updateBySQL(sql, condition);
	}
	
	/**
	 * 密码登录错误次数加1
	 *
	 * @param id 用户id
	 * @return 自行成功返回true，反之返回false
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年5月20日
	 */
	public boolean addPwdContinueFails(long id){
		String sql = "UPDATE t_user SET  password_continue_fails = password_continue_fails+1 WHERE id =:id";
		Map<String, Object> condition = new HashMap<String, Object>();

		condition.put("id", id);

		return updateBySQL(sql, condition)==1;
	}
	/**
	 * 用户修改密码
	 * 
	 * @param id 用户id
	 * @param password密码(已加密)
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 *
	 */
	public int updatePassowordById(long id, String password) {
		String sql = "UPDATE t_user SET  password =:password WHERE id =:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		condition.put("password", password);
		
		return updateBySQL(sql, condition);
	}
	
	/**
	 * 修改用户是否允许登录
	 * 
	 * @param id
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月24日
	 *
	 */
	public int updateUserIsAllowLogin(long id) {
		String sql = "UPDATE t_user SET is_allow_login = (CASE is_allow_login WHEN 0 THEN 1 WHEN 1 THEN 0 END),lock_time = (CASE is_allow_login WHEN 1 THEN NULL WHEN 0 THEN :date END) WHERE id =:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("date", new Date());
		condition.put("id", id);

		return updateBySQL(sql, condition);
	}
	

	
	/**
	 * 修改会员最后一次登录信息
	 * @param id 用户id
	 * @param client 上次登录入口 
	 * @param ip 上次登录IP
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月28日
	 *
	 */
	public int updateUserLastLoginInfo(long id, int client, String ip) {
		String sql = "UPDATE t_user SET last_login_time=:time,last_login_client =:client,last_login_ip=:ip WHERE id =:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("time", new Date());
		condition.put("ip", ip);
		condition.put("client", client);
		condition.put("id", id);

		return updateBySQL(sql, condition);
	}
	
	/**
	 * 修改用户手机号码
	 * 
	 * @param id 用户id
	 * @param mobile 手机号码
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 *
	 */
	public int updateUserMobile(long id, String mobile) {
		String sql = "UPDATE t_user SET mobile =:mobile WHERE id =:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("mobile", mobile);
		condition.put("id", id);

		return updateBySQL(sql, condition);
	}

	/**
	 * 修改用户头像
	 * 
	 * @param id 用户id
	 * @param photo 头像路径
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月25日
	 *
	 */
	public int updateUserPhoto(long id, String photo) {
		String sql = "UPDATE t_user_info SET photo =:photo WHERE user_id =:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("photo", photo);
		condition.put("id", id);

		return updateBySQL(sql, condition);
	}
	
	
	/**
	 * 根据手机号码查询用户信息
	 * 
	 * @param mobile 手机号码
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月16日
	 *
	 */
	public t_user findByMobile(String mobile) {
		String sql = "SELECT * FROM t_user WHERE mobile =:mobile";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("mobile", mobile);

		return findBySQL(sql, condition);
	}

	/**
	 * 根据手机号码和密码获得用户信息
	 * 
	 * @param mobile 手机号码
	 * @param password 登录密码
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月16日
	 *
	 */
	public t_user findByMobileAndPWD(String mobile, String password) {
		String sql = "SELECT * FROM t_user WHERE mobile =:mobile AND password =:password";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("mobile", mobile);
		condition.put("password", password);

		return findBySQL(sql, condition);
	}
	
	/**
	 * 查询用户数目
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月8日
	 *
	 */
	public int findCountOfUsers() {
		String sql = "SELECT COUNT(1) FROM t_user";
		
		return findSingleIntBySQL(sql, 0, null);
	}
	
	/**
	 * 查询用户信用情况
	 *
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月5日
	 *
	 */
	public UserDetail findCreditSituation(long userId) {
		/**
		 SQL:
			SELECT 
				t.id, 
				((SELECT IFNULL((tuf.balance + tuf.freeze + tuf.visual_balance),0) FROM t_user_fund tuf WHERE tuf.user_id = t.id) - 
				(SELECT IFNULL(SUM(tb.repayment_corpus + tb.repayment_interest),0) FROM t_bill tb WHERE t.id = tb.user_id AND tb.status IN (0, 1)) + 
				(SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) FROM t_bill_invest tbi,t_bid tb WHERE tb.id = tbi.bid_id AND t.id = tbi.user_id AND tbi.status IN (0))) AS total_assets, 
				(IFNULL((SELECT SUM(tbi.receive_interest) FROM t_bill_invest tbi WHERE tbi.user_id = t.id AND tbi.status = 1),0) - 
				IFNULL((SELECT SUM(tdu.amount) FROM t_deal_user tdu WHERE tdu.operation_type = 206 AND tdu.user_id = t.id),0) + 
				IFNULL((SELECT SUM(tdu.amount) FROM t_deal_user tdu WHERE tdu.operation_type = 106 AND tdu.user_id = t.id),0)) AS total_income, 
				(SELECT COUNT(tb.id) FROM t_bid tb WHERE tb.user_id = t.id) AS apply_bid_count,
				(SELECT COUNT(tb.id) FROM t_bid tb WHERE tb.user_id = t.id AND tb.status IN (4,5)) AS complete_borrow_count,
				(SELECT COUNT(tb.id) FROM t_bid tb WHERE tb.user_id = t.id AND tb.status = 5) AS complete_repayment_count,
				(SELECT COUNT(tb.id) FROM t_bill tb WHERE tb.user_id = t.id AND tb.is_overdue = 1) AS overdue_count,
				(SELECT IFNULL(SUM(tb.overdue_fine),0) FROM t_bill tb WHERE tb.user_id = t.id AND tb.is_overdue = 1) AS overdue_fine,
				(SELECT IFNULL(SUM(tb.repayment_corpus+tb.repayment_interest),0) FROM t_bill tb WHERE tb.user_id = t.id AND tb.status IN(0,1)) AS no_repayment_interest,
				(SELECT IFNULL(SUM(tb.amount), 0) FROM t_bid tb WHERE t.id = tb.user_id AND tb.status IN (4, 5)) AS total_borrow,
				(SELECT IFNULL(COUNT(tbcu.id),0) FROM t_bank_card_user tbcu WHERE t.id = tbcu.user_id) AS bank_card_count 
			FROM 
				t_user t 
			WHERE 
				t.id =:userId
		 */
		String sql = "SELECT t.id, ((SELECT IFNULL((tuf.balance + tuf.freeze + tuf.visual_balance),0) FROM t_user_fund tuf WHERE tuf.user_id = t.id) - (SELECT IFNULL(SUM(tb.repayment_corpus + tb.repayment_interest),0) FROM t_bill tb WHERE t.id = tb.user_id AND tb.status IN (0, 1))+ (SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) FROM t_bill_invest tbi,t_bid tb WHERE tb.id = tbi.bid_id AND t.id = tbi.user_id AND tbi.status IN (0))) AS total_assets, (IFNULL((SELECT SUM(tbi.receive_interest) FROM t_bill_invest tbi WHERE tbi.user_id = t.id AND tbi.status = 1),0) - IFNULL((SELECT SUM(tdu.amount) FROM t_deal_user tdu WHERE tdu.operation_type = 206 AND tdu.user_id = t.id),0) + IFNULL((SELECT SUM(tdu.amount) FROM t_deal_user tdu WHERE tdu.operation_type = 106 AND tdu.user_id = t.id),0)) AS total_income, (SELECT COUNT(tb.id) FROM t_bid tb WHERE tb.user_id = t.id) AS apply_bid_count,(SELECT COUNT(tb.id) FROM t_bid tb WHERE tb.user_id = t.id AND tb.status IN (4,5)) AS complete_borrow_count,(SELECT COUNT(tb.id) FROM t_bid tb WHERE tb.user_id = t.id AND tb.status = 5) AS complete_repayment_count,(SELECT COUNT(tb.id) FROM t_bill tb WHERE tb.user_id = t.id AND tb.is_overdue = 1) AS overdue_count,(SELECT IFNULL(SUM(tb.overdue_fine),0) FROM t_bill tb WHERE tb.user_id = t.id AND tb.is_overdue = 1) AS overdue_fine,(SELECT IFNULL(SUM(tb.repayment_corpus+tb.repayment_interest),0) FROM t_bill tb WHERE tb.user_id = t.id AND tb.status IN(0,1)) AS no_repayment_interest,(SELECT IFNULL(SUM(tb.amount), 0) FROM t_bid tb WHERE t.id = tb.user_id AND tb.status IN (4, 5)) AS total_borrow,(SELECT IFNULL(COUNT(tbcu.id),0) FROM t_bank_card_user tbcu WHERE t.id = tbcu.user_id) AS bank_card_count FROM t_user t WHERE t.id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return findBeanBySQL(sql, UserDetail.class, condition);
	}

	/**
	 * 根据手机号码获取用户ID
	 * 
	 * @param mobile 手机号码
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月16日
	 *
	 */
	public long findIdByMobile(String mobile) {
		String sql = "SELECT id FROM t_user WHERE mobile =:mobile";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("mobile", mobile);

		return findSingleLongBySQL(sql, -1, condition);
	}

	/**
	 * 根据用户的userId查询用户信息
	 * 
	 * @param id
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月26日
	 *
	 */
	public t_user findUserById(long id) {
		String sql = "SELECT * FROM t_user WHERE id =:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);

		return findBySQL(sql, condition);
	}
	
	/**
	 * 获取ShowUserInfo
	 *
	 * @param userId
	 * @param showType 用户类型
	 * @param userName 用户名
	 * 
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月7日
	 *
	 */
	public UserFundStatistic findUserFundStatistic(long userId, int showType, String userName) {
		/**
		  SQL:
	 		SELECT
				t.id,
				IFNULL(SUM(tuf.balance), 0) AS balance_total,
				IFNULL(SUM(tuf.freeze), 0) AS freeze_total,
				IFNULL(SUM((SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest), 0) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status IN (0))),0) AS no_receive_total,
				IFNULL(SUM((SELECT IFNULL(SUM(tb.repayment_corpus + tb.repayment_interest), 0) FROM t_bill tb WHERE t.id = tb.user_id AND tb.status IN (0, 1))),0) AS no_repayment_total
			FROM
				t_user t,
				t_user_info tui,
				t_user_fund tuf
			WHERE
				t.id = tuf.user_id
			AND t.id = tui.user_id 
		 */
		
		StringBuffer strSql =new StringBuffer("SELECT t.id,IFNULL(SUM(tuf.balance),0) AS balance_total,IFNULL(SUM(tuf.freeze),0) AS freeze_total,IFNULL(SUM(( SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest), 0) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status IN (0))),0) AS no_receive_total,IFNULL(SUM(( SELECT IFNULL(SUM(tb.repayment_corpus + tb.repayment_interest), 0) FROM t_bill tb WHERE t.id = tb.user_id AND tb.status IN (0, 1))),0) AS no_repayment_total FROM t_user t,t_user_info tui,t_user_fund tuf WHERE t.id = tuf.user_id AND t.id = tui.user_id ");
		Map<String, Object> condition = new HashMap<String, Object>();
		
		if (showType == 1) {  	
			/* 借款会员 */
			strSql.append(" AND (tui.member_type =:memberType1 OR tui.member_type =:memberType2) AND tui.loan_member_time IS NOT NULL ");
			condition.put("memberType1", t_user_info.MemberType.BORROW_MEMBER.code);
			condition.put("memberType2", t_user_info.MemberType.COMPOSITE_MEMBERS.code);
			
		}else if (showType == 2) {  	
			/* 理财会员 */
			strSql.append(" AND  (tui.member_type =:memberType1 OR tui.member_type =:memberType2) AND tui.invest_member_time IS NOT NULL");
			condition.put("memberType1", t_user_info.MemberType.FINANCIAL_MEMBER.code);
			condition.put("memberType2", t_user_info.MemberType.COMPOSITE_MEMBERS.code);
			
		}else if (showType == 3) {  	
			/* 新用户 */
			strSql.append(" AND tui.member_type =:memberType");
			condition.put("memberType", t_user_info.MemberType.NEW_MEMBER.code);
			
		}else if (showType == 4) {
			/* 复合会员 */
			strSql.append(" AND tui.member_type =:memberType");
			condition.put("memberType", t_user_info.MemberType.COMPOSITE_MEMBERS.code);
			
		}else if (showType == 5) {
			/* 正常登录会员 */
			strSql.append(" AND t.is_allow_login =:isAllowLogin");
			condition.put("isAllowLogin", true);
			
		}else if (showType == 6) {
			/* 锁定会员 */
			strSql.append(" AND t.is_allow_login =:isAllowLogin");
			condition.put("isAllowLogin", false);
			
		}
		
		if (StringUtils.isNotBlank(userName)) {
			strSql.append(" AND t.name like :name");
			condition.put("name", "%"+userName+"%");
		}
		
		return findBeanBySQL(strSql.toString(), UserFundStatistic.class, condition);
	}

	/**
	 * 查询用户资产信息
	 *
	 *  
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月20日
	 *
	 */
	public UserFundInfo findUserFundInfo(long userId) {
		/**
		  SQL:
			SELECT 
				t.id AS id, tuf.balance AS balance,
				(IFNULL((SELECT SUM(tbi.receive_interest) FROM t_bill_invest tbi WHERE tbi.user_id = t.id AND tbi.status = 1),0) - 
				IFNULL((SELECT SUM(tdu.amount) FROM t_deal_user tdu WHERE tdu.operation_type =:invest_service_fee AND tdu.user_id = t.id),0) + 
				IFNULL((SELECT SUM(tdu.amount) FROM t_deal_user tdu WHERE tdu.operation_type =:conversion AND tdu.user_id = t.id),0)) AS total_income,
				tuf.freeze AS freeze,tuf.visual_balance AS reward,
				(SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status = 0) AS no_receive,
				(SELECT IFNULL(SUM(tb.repayment_corpus + tb.repayment_interest),0) FROM t_bill tb WHERE t.id = tb.user_id AND tb.status IN (0, 1)) AS no_repayment,
				(SELECT COUNT(1) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status = 0) AS no_payment_count,
				(SELECT COUNT(1) FROM t_invest ti,t_bid tb WHERE ti.user_id = t.id AND tb.id = ti.bid_id AND tb.status IN (4, 5) AND ti.debt_id=0) AS invest_count 
			FROM 
				t_user t INNER JOIN t_user_fund tuf ON t.id = tuf.user_id
			WHERE 
				t.id =:userId  
		 */
		String sql = "SELECT t.id AS id, tuf.balance AS balance,(IFNULL((SELECT SUM(tbi.receive_interest) FROM t_bill_invest tbi WHERE tbi.user_id = t.id AND tbi.status = 1),0) - IFNULL((SELECT SUM(tdu.amount) FROM t_deal_user tdu WHERE tdu.operation_type =:invest_service_fee AND tdu.user_id = t.id),0) + IFNULL((SELECT SUM(tdu.amount) FROM t_deal_user tdu WHERE tdu.operation_type =:conversion AND tdu.user_id = t.id),0)) AS total_income,tuf.freeze AS freeze,tuf.visual_balance AS reward,(SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status = 0) AS no_receive,(SELECT IFNULL(SUM(tb.repayment_corpus + tb.repayment_interest),0) FROM t_bill tb WHERE t.id = tb.user_id AND tb.status IN (0, 1)) AS no_repayment,(SELECT COUNT(1) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status = 0) AS no_payment_count,(SELECT COUNT(1) FROM t_invest ti, t_bid tb WHERE ti.user_id = t.id AND tb.id = ti.bid_id AND tb.status IN (4, 5) AND ti.debt_id=0) AS invest_count FROM t_user t INNER JOIN t_user_fund tuf ON t.id = tuf.user_id WHERE t.id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("invest_service_fee", t_deal_user.OperationType.INVEST_SERVICE_FEE.code);
		condition.put("conversion", t_deal_user.OperationType.CONVERSION.code);
		condition.put("userId", userId);
		
		return findBeanBySQL(sql, UserFundInfo.class, condition);
	}
	
	/**
	 * 查询相应时间注册会员数目的数据
	 * 
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param type 查询类型
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月23日
	 *
	 */
	public int findUserCount(String startTime, String endTime, int type) {
		String sql = null;
		String hour = null;
		Map<String, Object> condition = new HashMap<String, Object>();
		if (type == Constants.YESTERDAY) {
			sql = "SELECT COUNT(1) FROM t_user WHERE TO_DAYS(:nowTime) - TO_DAYS(time) = 1 AND HOUR(time) <:hour";
			if (endTime.contains(":")) {
				hour = endTime.substring(0, endTime.indexOf(":"));
				if("00".equals(hour)){
					hour = "24";
				}
			}
			condition.put("nowTime", new Date());
			condition.put("hour", hour);
		}else{
			sql="SELECT COUNT(1) FROM t_user WHERE time>=:startTime AND time <=:endTime ";
			condition.put("startTime", startTime);
			condition.put("endTime", endTime);
		}
		
		return findSingleIntBySQL(sql, 0, condition);
	}
	
	/**
	 * 根据用户Id查询用户原始密码
	 * 
	 * @param userId 手机号码
	 * @return
	 *
	 * @author luzhiwei
	 * @createDate 2016年5月17日
	 *
	 */
	public String findUserOldPassWord(long userId) {
		String sql = "SELECT password FROM t_user WHERE id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);

		return findSingleStringBySQL(sql, "", condition);
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
	public PageBean<Map<String, Object>> queryCreateBidRefUser(int currPage, int pageSize, String key) {
		
		String querySQL = "SELECT tu.id, tu.name, tu.mobile, tui.reality_name, tui.id_number FROM t_user tu, t_user_info tui WHERE tu.id=tui.user_id AND tui.reality_name IS NOT NULL AND tui.id_number IS NOT NULL AND tui.add_base_info_schedule=3";
		String countSQL = "SELECT COUNT(tu.id) FROM t_user tu, t_user_info tui WHERE tu.id=tui.user_id AND tui.reality_name IS NOT NULL AND tui.id_number IS NOT NULL AND tui.add_base_info_schedule=3";
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(key)){
			querySQL += " AND (tu.name LIKE :name OR tu.mobile LIKE :mobile OR tui.reality_name LIKE :realname )";
			countSQL += " AND (tu.name LIKE :name OR tu.mobile LIKE :mobile OR tui.reality_name LIKE :realname )";
			conditionArgs.put("name", "%"+key+"%");
			conditionArgs.put("mobile", "%"+key+"%");
			conditionArgs.put("realname", "%"+key+"%");
		}
		
		return pageOfMapBySQL(currPage, pageSize, countSQL, querySQL, conditionArgs);
	}
	
	/**
	 * 后台发红包，分页查询所有用户
	 *
	 * @param key 搜索关键字
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月21日14:19:26
	 */
	public PageBean<Map<String, Object>> queryredPacketUser(int currPage, int pageSize,String key) {
		/**
		 * SELECT 
		 * 		tu.id,
		 * 		tu.name,
		 * 		tu.mobile,
		 * 		tui.reality_name,
		 * 	    tui.id_number 
		 * FROM 
		 * 		t_user tu 
		 * INNER JOIN 
		 * 		t_user_info tui 
		 * ON
		 * 		tu.id = tui.user_id 
		 */
		
		
		String querySQL = "SELECT tu.id, tu.name, tu.mobile, tui.reality_name, tui.id_number FROM t_user tu INNER JOIN t_user_info tui on tu.id = tui.user_id ";
		String countSQL = "SELECT COUNT(tu.id) FROM t_user tu INNER JOIN t_user_info tui on tu.id = tui.user_id";
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(key)){
			querySQL += " WHERE tu.name LIKE :name OR tu.mobile LIKE :mobile OR tui.reality_name LIKE :realname ";
			countSQL += " WHERE tu.name LIKE :name OR tu.mobile LIKE :mobile OR tui.reality_name LIKE :realname ";
			conditionArgs.put("name", "%"+key+"%");
			conditionArgs.put("mobile", "%"+key+"%");
			conditionArgs.put("realname", "%"+key+"%");
		}
		
		return pageOfMapBySQL(currPage, pageSize, countSQL, querySQL, conditionArgs);
	}
	
	
	
	/**
	 * 查询用户信息列表
	 *
	 *			
	 * @param showType 筛选类型  0-所有;1-借款会员;2-理财会员;3-新用户;4-复合会员;5-正常会员;6-锁定
	 * @param currPage 
	 * @param pageSize 
	 * @param orderType 排序类型  0-默认按登录时间 1-编号 2-可用 3-冻结 4-待收 5-待还 6-累计借款 7-累计投资 8-会员
	 * @param orderValue 排序方式 0-倒序;1-顺序
	 * @param userName 搜索条件
	 * @param exports 1:导出    default：不导出
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月30日
	 *
	 */
	public PageBean<ShowUserInfo> pageOfUserInfo(int showType, int currPage, int pageSize, int orderType, int orderValue, String userName, int exports) {
		/**
		  SQL:
		 	SELECT
				t.id AS id,
				t.time AS time,
				t.name AS name,
				tui.mobile AS mobile,
				tui.email AS email,
				tuf.balance AS balance,
				tuf.freeze AS freeze,
				t.login_count AS login_count,
				t.last_login_time AS last_login_time,
				t.is_allow_login AS is_allow_login,
				(SELECT IFNULL(SUM(tb.repayment_corpus + tb.repayment_interest),0) FROM t_bill tb WHERE t.id = tb.user_id AND tb.status IN (0, 1)) AS no_repayment,
				(SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status IN (0)) AS no_receive,
				(SELECT IFNULL(SUM(tb.amount), 0) FROM t_bid tb WHERE t.id = tb.user_id AND tb.status IN (4, 5)) AS borrow_total,
				(SELECT IFNULL(SUM(ti.amount), 0) FROM t_invest ti,t_bid tb WHERE ti.debt_id=0 AND t.id = ti.user_id AND ti.bid_id = tb.id AND tb.status IN (4, 5)) AS invest_total
			FROM
				t_user t,
				t_user_info tui,
				t_user_fund tuf
			WHERE
				t.id = tuf.user_id
			AND t.id = tui.user_id 
		 */
		
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_user t, t_user_info tui, t_user_fund tuf WHERE t.id = tuf.user_id AND t.id = tui.user_id ");
		StringBuffer querySQL = new StringBuffer("SELECT t.id AS id,t.time AS time,t.name AS name,tui.mobile AS mobile,tui.email AS email,tuf.balance AS balance,tuf.freeze AS freeze,t.login_count AS login_count,t.last_login_time AS last_login_time,t.is_allow_login AS is_allow_login,(SELECT IFNULL(SUM(tb.repayment_corpus + tb.repayment_interest),0) FROM  t_bill tb WHERE t.id = tb.user_id AND tb.status IN (0,1)) AS no_repayment,(SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) FROM t_bill_invest tbi WHERE t.id = tbi.user_id AND tbi.status = 0) AS no_receive,(SELECT IFNULL(SUM(tb.amount),0) FROM t_bid tb WHERE t.id = tb.user_id AND tb.status IN (4, 5)) AS borrow_total, (SELECT IFNULL(SUM(ti.amount), 0) FROM t_invest ti,t_bid tb WHERE t.id = ti.user_id AND ti.bid_id=tb.id AND tb.status in (4,5) AND ti.debt_id=0 ) AS invest_total FROM t_user t, t_user_info tui, t_user_fund tuf WHERE t.id = tuf.user_id AND t.id = tui.user_id ");
		Map<String, Object> condition = new HashMap<String, Object>();
		
		switch (showType) {
		case 1:
			/* 借款会员 */
			countSQL.append(" AND (tui.member_type =:memberType1 OR tui.member_type =:memberType2) ");
			querySQL.append(" AND (tui.member_type =:memberType1 OR tui.member_type =:memberType2) ");
			condition.put("memberType1", t_user_info.MemberType.BORROW_MEMBER.code);
			condition.put("memberType2", t_user_info.MemberType.COMPOSITE_MEMBERS.code);
			break;
		case 2:
			/* 理财会员 */
			countSQL.append(" AND (tui.member_type =:memberType1 OR tui.member_type =:memberType2) ");
			querySQL.append(" AND (tui.member_type =:memberType1 OR tui.member_type =:memberType2) ");
			condition.put("memberType1", t_user_info.MemberType.FINANCIAL_MEMBER.code);
			condition.put("memberType2", t_user_info.MemberType.COMPOSITE_MEMBERS.code);
			break;
		case 3:
			/* 新用户 */
			countSQL.append(" AND tui.member_type =:memberType");
			querySQL.append(" AND tui.member_type =:memberType");
			condition.put("memberType", t_user_info.MemberType.NEW_MEMBER.code);
			break;
		case 4:
			/* 复合会员 */
			countSQL.append(" AND tui.member_type =:memberType ");
			querySQL.append(" AND tui.member_type =:memberType ");
			condition.put("memberType", t_user_info.MemberType.COMPOSITE_MEMBERS.code);
			break;
		case 5:
			/* 正常登录会员 */
			countSQL.append(" AND t.is_allow_login =:isAllowLogin");
			querySQL.append(" AND t.is_allow_login =:isAllowLogin");
			condition.put("isAllowLogin", true);
			break;
		case 6:
			/* 锁定会员 */
			countSQL.append(" AND t.is_allow_login =:isAllowLogin");
			querySQL.append(" AND t.is_allow_login =:isAllowLogin");
			condition.put("isAllowLogin", false);
			break;
		}
		
		if (StringUtils.isNotBlank(userName)) {
			/* 按用户名搜索 */
			countSQL.append(" AND t.name like :name");
			querySQL.append(" AND t.name like :name");
			condition.put("name", "%"+userName+"%");
		}
		/* 排序功能 */
		switch (orderType) {
		case 1:
			querySQL.append(" ORDER BY id ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
			break;
		case 2:
			querySQL.append(" ORDER BY balance ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
			break;
		case 3:
			querySQL.append(" ORDER BY freeze ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
			break;
		case 4:
			querySQL.append(" ORDER BY no_receive ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
			break;
		case 5:
			querySQL.append(" ORDER BY no_repayment ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
			break;
		case 6:
			querySQL.append(" ORDER BY borrow_total ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
			break;
		case 7:
			querySQL.append(" ORDER BY invest_total ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
			break;
		case 8:
			querySQL.append(" ORDER BY login_count ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
			break;
		default:
			querySQL.append(" ORDER BY last_login_time DESC ");
			break;
		}
		
		if(exports == Constants.EXPORT){
			PageBean<ShowUserInfo> page = new PageBean<ShowUserInfo>();
			page.page = findListBeanBySQL(querySQL.toString(), ShowUserInfo.class, condition);
			return page;
		}
		
		return super.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), ShowUserInfo.class, condition);
	}
	
	/**
	 * 根据Mobile 查询用户id
	 * 
	 * @param mobile 电话号
	 * @return
	 */
	public t_user queryByMobile(String mobile){
		String sql="SELECT * FROM t_user u WHERE u.mobile =:mobile ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("mobile", mobile);
		return this.findBySQL(sql, condition);
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
		String sql="select id from t_user where mobile  = :mobile";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("mobile", mobile);
		
		return findBySQL(sql, condition);
	}

}
