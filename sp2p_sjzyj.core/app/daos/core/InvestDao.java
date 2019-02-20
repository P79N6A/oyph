package daos.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import models.core.bean.BidInvestRecord;
import models.core.bean.UserInvestRecord;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import play.db.jpa.JPA;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.PageBean;
import daos.base.BaseDao;

/**
 * 投资 接口实现类
 *
 * @description 
 *
 * @author liudong
 * @createDate 2015年12月15日
 */
public class InvestDao extends BaseDao<t_invest> {

	protected InvestDao() {}
	
	public double findTotalInvest(Long userId) {
		String querySQL = " SELECT IFNULL(SUM(i.amount),0) FROM t_invest i  where i.user_id=:userId  AND i.debt_id = 0 ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return findSingleDoubleBySQL(querySQL, 0, condition);
	}
	
	/**
	 * 查询最新的理财信息
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月29日
	 *
	 */
	public double findBidInvestFee(long bidId) {
		
		String sql = "select sum(invest_fee) from t_invest where bid_id=:bid_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bid_id", bidId);
		
		return super.findSingleDoubleBySQL(sql, 0.00, params);
	}
	
	/**
	 * 获取投资金额
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月25日
	 *
	 */
	public double findTotalInvestByDate(String startTime, String endTime,
			int type){
		String sql = null;
		String hour = null;
		Map<String, Object> condition = new HashMap<String, Object>();
		if (type == Constants.YESTERDAY) {
			sql = "SELECT SUM(amount) AS invest_money FROM t_invest WHERE TO_DAYS(:nowTime) - TO_DAYS(time) = 1 AND HOUR(time) <:hour AND debt_id=0";
			if (endTime.contains(":")) {
				hour = endTime.substring(0, endTime.indexOf(":"));
				if("00".equals(hour)){
					hour = "24";
				}
			}
			
			condition.put("nowTime", new Date());
			condition.put("hour", hour);
		}else{
			sql="SELECT SUM(amount) AS invest_money FROM t_invest WHERE time>=:startTime AND time <=:endTime AND debt_id=0";
			
			condition.put("startTime", startTime);
			condition.put("endTime", endTime);
		}
		
		return findSingleDoubleBySQL(sql, 0.00, condition);
	}

	
	/**
	 * 查询一个借款标的所有投资记录
	 *
	 * @param Id
	 * @return
	 *
	 * @author likai
	 * @createDate 2018年11月15日 
	 */
	public BidInvestRecord listOfBidInvestRecords(long id) {
		//String sql="SELECT ti.id AS id, ti.bid_id AS bid_id, tu.name AS name, ti.client AS client, ti.time AS time, ti.amount AS amount, ti.invest_type FROM t_user tu, t_invest ti WHERE tu.id = ti.user_id AND ti.bid_id =:bidId ";
		String sql="SELECT ti.id AS id, ti.bid_id AS bid_id, tu.name AS name, ti.client AS client, ti.time AS time, ti.amount AS amount, ti.invest_type FROM t_user tu, t_invest ti WHERE tu.id = ti.user_id AND ti.id =:id ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		return super.findBeanBySQL(sql, BidInvestRecord.class, condition);
	}
	
	/**
	 * 查询用户每周投资信息(剔除已转让的账单)
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月29日
	 *
	 */
	public List<Map<String, Object>> queryNewInvestList() {
		String sql="SELECT ti.time, tu.photo, ti.amount, tb.id FROM t_user_info tu, t_invest ti, t_bid tb WHERE tu.user_id = ti.user_id AND ti.bid_id = tb.id AND ti.debt_id=0 ORDER BY ti.time DESC LIMIT 5";
		
		return this.findListMapBySQL(sql, null);
	}

	/**
	 * 周排行榜
	 *
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年4月27日
	 */
	public List<Map<String, Object>> queryWeekInvestList() {
		Map<String, Object> condition = new HashMap<String, Object>();
		String endTime = DateUtil.getPreviousSunday();//本周结束时间
		String startTime = DateUtil.getCurrentMonday();//本周开始时间
		String sql = "SELECT tu.name,sum(ti.amount) AS amount FROM t_user tu,t_invest ti WHERE tu.id = ti.user_id AND ti.time BETWEEN :startTime AND :endTime and debt_id = 0 GROUP BY tu.id ORDER BY amount DESC LIMIT 7";
		condition.put("startTime", startTime);
		condition.put("endTime", endTime);
		return this.findListMapBySQL(sql, condition);
	}
	
	/**
	 * 月排行榜
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年1月6日
	 */
	public List<Map<String, Object>> queryMouthInvestList() {
		Map<String, Object> condition = new HashMap<String, Object>();
		String endTime = DateUtil.getMonthLastDay();//本月结束时间
		String startTime = DateUtil.getMonthFirstDay();//本月开始时间
		String sql = "SELECT tu.name,sum(ti.amount) AS amount FROM t_user tu,t_invest ti WHERE tu.id = ti.user_id AND ti.time BETWEEN :startTime AND :endTime and debt_id = 0 GROUP BY tu.id ORDER BY amount DESC LIMIT 10";
		condition.put("startTime", startTime);
		condition.put("endTime", endTime);
		return this.findListMapBySQL(sql, condition);
	}
	
	/**
	 * 投资记录查询，针对一个标的投资情况[标的详情的下拉列表]
	 *
	 * @author yaoyi
	 * @createDate 2015年12月16日
	 * 
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param bidId 标的Id
	 * 
	 * @return PageBean
	 * 
	 */
	public PageBean<BidInvestRecord> pageOfBidInvestDetail(int currPage, int pageSize, long bidId){
		
		String querySQL = "SELECT invest.id, invest.bid_id, user.name, invest.client, invest.time, invest.amount, invest.invest_type FROM t_invest invest LEFT JOIN t_user user ON invest.user_id=user.id WHERE invest.bid_id=:bidId AND invest.debt_id=0 ORDER BY invest.id DESC";
		String countSQL = "SELECT COUNT(1) FROM t_invest WHERE bid_id=:bidId AND debt_id=0";
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("bidId", bidId);
		
		return super.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, BidInvestRecord.class, conditionArgs);
	}

	/**
	 * 投资记录查询，针对某一个用户的投资情况
	 * 
	 * @description 已转让的状态也当做完成状态
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param userId 用户id
	 *
	 * @author liudong
	 * @createDate 2015年12月16日
	 */
	public PageBean<UserInvestRecord> pageOfUserInvestRecord(int currPage, int pageSize, long userId){
		/**
		SELECT
			ti.id AS id,
			ti.user_id AS user_id,
			ti.bid_id AS bid_id,
			ti.amount AS amount,
			tb.title AS title,
			tb.apr AS apr,
			tb.period AS period,
			tb.period_unit AS period_unit,
			tb.repayment_type AS repayment_type,
			tb.release_time AS release_time,
			tb.status AS status,
			(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.status IN (:status) AND tbi.user_id =ti.user_id) AS alreadRepay,
			(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.user_id =ti.user_id ) AS totalPay
		FROM
			t_bid tb,
			t_invest ti
		WHERE
			tb.id = ti.bid_id
		AND ti.user_id =:userId;
		 */
		String sql="SELECT ti.id AS id,ti.time as time, ti.user_id AS user_id,ti.bid_id AS bid_id,ti.amount AS amount,tb.title AS title,tb.apr AS apr,tb.period AS period,tb.period_unit AS period_unit,tb.repayment_type AS repayment_type,tb.release_time AS release_time,tb.status AS status,(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.status IN ("+t_bill_invest.Status.RECEIVED.code+","+t_bill_invest.Status.TRANSFERED.code+") AND tbi.user_id =ti.user_id) AS alreadRepay,(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.user_id =ti.user_id ) AS totalPay FROM t_bid tb,t_invest ti WHERE tb.id = ti.bid_id AND ti.user_id =:userId ORDER BY ti.id DESC";
		
		String sqlCount="SELECT COUNT(1) FROM t_bid tb,t_invest ti WHERE tb.id = ti.bid_id AND ti.user_id =:userId";
	
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
			
		return pageOfBeanBySQL(currPage, pageSize, sqlCount, sql, UserInvestRecord.class, condition);
	}
	public PageBean<UserInvestRecord> pageOfUserInvestRecord1(int currPage, int pageSize, long userId){
		/**
		SELECT
			ti.id AS id,
			ti.user_id AS user_id,
			ti.bid_id AS bid_id,
			ti.amount AS amount,
			tb.title AS title,
			tb.apr AS apr,
			tb.period AS period,
			tb.period_unit AS period_unit,
			tb.repayment_type AS repayment_type,
			tb.release_time AS release_time,
			tb.status AS status,
			(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.status IN (:status) AND tbi.user_id =ti.user_id) AS alreadRepay,
			(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.user_id =ti.user_id ) AS totalPay
		FROM
			t_bid tb,
			t_invest ti
		WHERE
			tb.id = ti.bid_id
		AND ti.user_id =:userId;
		 */
		String sql="SELECT ti.id AS id,ti.time as time, ti.user_id AS user_id,ti.bid_id AS bid_id,ti.amount AS amount,tb.title AS title,tb.apr AS apr,tb.period AS period,tb.period_unit AS period_unit,tb.repayment_type AS repayment_type,tb.release_time AS release_time,tb.status AS status,tb.contract_id AS contract_id,(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.status IN ("+t_bill_invest.Status.RECEIVED.code+","+t_bill_invest.Status.TRANSFERED.code+") AND tbi.user_id =ti.user_id) AS alreadRepay,(SELECT COUNT(1) FROM t_bill_invest tbi WHERE tbi.invest_id = ti.id AND tbi.user_id =ti.user_id ) AS totalPay,(SELECT t_pact.contract_id FROM t_pact WHERE pid = ti.bid_id AND t_pact.user_id = ti.user_id) AS contract FROM t_bid tb,t_invest ti WHERE tb.id = ti.bid_id AND ti.user_id =:userId AND ti.debt_id = 0 and ti.transfer_status=0 ORDER BY ti.id DESC";
		
		String sqlCount="SELECT COUNT(1) FROM t_bid tb,t_invest ti WHERE tb.id = ti.bid_id AND ti.user_id =:userId and transfer_status=0";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
			
		return pageOfBeanBySQL(currPage, pageSize, sqlCount, sql, UserInvestRecord.class, condition);
	}
	
	public double findZhi(long userId, Date sdate, Date sdates) {
		
		String sql=" select sum(v.amount) from t_invest v INNER JOIN t_bid b ON b.id=v.bid_id where b.status>3 and v.transfer_status=0 and v.user_id=:userId and b.release_time>=:sdate and b.release_time<=:sdates ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		condition.put("sdate", sdate);
		condition.put("sdates", sdates);
		
		return super.findSingleDoubleBySQL(sql, 0, condition);
	}
	
	
	/**
	 * 红包活动排行榜
	 *
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年7月14日
	 */
	public List<Map<String, Object>> queryActivityInvestList() {
		Map<String, Object> condition = new HashMap<String, Object>();
		String endTime = "2017-09-01 00:00:00";//活动结束时间
		String startTime = "2017-07-20 00:00:00";//活动开始时间
		String sql = "SELECT tu.name,sum(ti.amount) AS amount FROM t_user tu,t_invest ti WHERE tu.id = ti.user_id AND ti.time BETWEEN :startTime AND :endTime and debt_id = 0 GROUP BY tu.id ORDER BY amount DESC LIMIT 5";
		condition.put("startTime", startTime);
		condition.put("endTime", endTime);
		return this.findListMapBySQL(sql, condition);
	}
	
	/**
	 * 查询一个标的所有投资记录
	 * 
	 * @author niu
	 * @create 2017.09.28
	 */
	public List<t_invest> findAllInvest(long bidId) {
		
		String sql = "SELECT * FROM t_invest t WHERE t.bid_id =:bidId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", "bidId");
		
		return this.findListBySQL(sql, condition);
	}
	
	/**
	 * 查询累计成交人次
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月20日
	 *
	 */
	public int findCountOfInvests() {
		String sql = "SELECT COUNT(1) FROM t_invest";
		
		return findSingleIntBySQL(sql, 0, null);
	}
	
	/**
	 * 查询上月交易笔数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月20日
	 *
	 */
	public int findMonCountByMonth(String beginTime, String endTime) {
		
		String sql = "SELECT COUNT(t.amount) FROM t_invest t WHERE t.time BETWEEN :beginTime AND :endTime";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		
		return findSingleIntBySQL(sql, 0, params);
	}
	
	/**
	 * 上月投资前十大人
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月20日
	 */
	public List<Map<String, Object>> queryMouthInvestListByMonth(String beginTime, String endTime) {
		Map<String, Object> condition = new HashMap<String, Object>();

		String sql = "SELECT tu.name,sum(ti.amount) AS amount FROM t_user tu,t_invest ti,t_bid bi WHERE tu.id = ti.user_id AND ti.bid_id = bi.id AND bi.status=4 AND ti.time BETWEEN :beginTime AND :endTime and debt_id = 0 GROUP BY tu.id ORDER BY amount DESC LIMIT 10";
		condition.put("beginTime", beginTime);
		condition.put("endTime", endTime);
		
		return this.findListMapBySQL(sql, condition);
	}
	
	/**
	 * 查询月借款人数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月20日
	 *
	 */
	public int findBorrowerCountByMonth(String beginTime, String endTime) {
		String sql = "SELECT COUNT(distinct user_id) FROM t_invest where time >=:beginTime AND time <=:endTime";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		
		return findSingleIntBySQL(sql, 0, params);
	}
	
	/**
	 * 查询某个人待还总额
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年01月23日
	 *
	 */
	public double findTotalAmounts(long userId) {
		String sql = "select SUM(tbi.receive_corpus + tbi.receive_interest) from t_invest i INNER JOIN t_bill_invest tbi on i.id=tbi.invest_id INNER JOIN t_bid b on b.id = i.bid_id where b.status>=0 AND b.status<=4 AND i.user_id=:userId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		
		return super.findSingleDoubleBySQL(sql, 0.00, params);
	}
	
	/**
	 * 查询某个人今日投资总额
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年01月23日
	 *
	 */
	public double findTodayAmounts(long userId, String startTimes) {
		String sql = "select sum(i.amount) from t_invest i where i.user_id=:userId and i.time>=:startTimes";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("startTimes", startTimes);
		
		return super.findSingleDoubleBySQL(sql, 0.00, params);
	}
	
	
	/**
	 * 查询标的所有投资人(去重)
	 * 
	 * @param bidId
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月29日 09:53:22
	 */
	public List<Long> findBidInvests(long bidId){
		
		String sql="select distinct user_id from t_invest where bid_id = ?";
		Query qry=JPA.em().createQuery(sql);
		qry.setParameter(1, bidId);
		List<Long> years=qry.getResultList();
		
		return years;
	}

	public List<t_invest> findListByBid(long bidId) {
		String sql="SELECT id,time,user_id,bid_id,SUM(amount) as amount,SUM(correct_amount) AS correct_amount,SUM(correct_interest) AS correct_interest,SUM(loan_service_fee_divide) AS loan_service_fee_divide,client,transfer_status,debt_id,service_order_no,trust_order_no,invest_type,red_packet_amount,red_packet_id,rate_ticket_id FROM t_invest WHERE bid_id = :bidId GROUP BY user_id ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bidId", bidId);
		return findListBySQL(sql, params);
	}
		
}