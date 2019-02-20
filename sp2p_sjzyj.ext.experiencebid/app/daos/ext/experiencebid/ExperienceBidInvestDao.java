package daos.ext.experiencebid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ext.experience.bean.experienceBidInvestRecord;
import models.ext.experience.bean.experienceUserInvestRecord;
import models.ext.experience.entity.t_experience_bid_invest;

import common.utils.PageBean;

import daos.base.BaseDao;

public class ExperienceBidInvestDao extends BaseDao<t_experience_bid_invest>{
	
	public ExperienceBidInvestDao(){}

	/**
	 * 体验标的投资列表
	 *
	 * @param experienceBidId
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月20日
	 */
	public PageBean<experienceBidInvestRecord> pageOfExperienceBidInvestRecord(long experienceBidId,int currPage, int pageSize) {
		
		String querySQL = "SELECT tebi.id, tui.name AS user_name, tebi.amount AS invest_amount, tebi.time AS invest_time, tebi.client FROM t_experience_bid_invest tebi LEFT JOIN t_user_info tui ON tebi.user_id=tui.user_id WHERE tebi.bid_id=:bidId ORDER BY tebi.id DESC";
		String countSQL = "SELECT COUNT(1) FROM t_experience_bid_invest WHERE bid_id=:bidId";
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("bidId", experienceBidId);
		
		return super.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, experienceBidInvestRecord.class, conditionArgs);
	}
	
	/**
	 * 用户投资体验标的记录
	 *
	 * @param userId
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public PageBean<experienceUserInvestRecord> pageOfExperienceUserInvestRecord(long userId, int currPage, int pageSize) {
		
		/**
		 SELECT
			tebi.id AS id,
			teb.id AS bid_id,
			teb.title AS title,
			tebi.amount AS invest_amount,
			tebi.time AS invest_time,
			tebi.income,
			teb.repayment_time AS repayment_time,
			teb. STATUS
		FROM
			t_experience_bid_invest tebi
		LEFT JOIN t_experience_bid teb ON tebi.bid_id = teb.id
		WHERE
			tebi.user_id =: userId
		ORDER BY
			tebi.id DESC 
		 
		 */
		String querySQL = "SELECT tebi.id AS id, teb.id AS bid_id, teb.title AS title, tebi.amount AS invest_amount, tebi.time AS invest_time, tebi.income, teb.repayment_time AS repayment_time, teb.status FROM t_experience_bid_invest tebi LEFT JOIN t_experience_bid teb ON tebi.bid_id=teb.id WHERE tebi.user_id=:userId ORDER BY tebi.id DESC";
		String countSQL = "SELECT COUNT(1) FROM t_experience_bid_invest WHERE user_id=:userId";
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("userId", userId);
		
		return super.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, experienceUserInvestRecord.class, conditionArgs);
	}

	/**
	 * 获取这个体验标的所有投标记录
	 *
	 * @param experienceBidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public List<t_experience_bid_invest> queryExperienceBidInvest(long experienceBidId) {
		
		return super.findListByColumn("bid_id=?", experienceBidId);
	}
}
