package dao.ext.wealthcircle;

import java.util.HashMap;
import java.util.Map;

import models.ext.wealthcircle.entity.t_wealthcircle_user;

import org.apache.commons.lang.StringUtils;

import daos.base.BaseDao;

/**
 * 财富圈邀请记录dao
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年4月6日
 */
public class WealthCircleUserDao extends BaseDao<t_wealthcircle_user> {

	protected WealthCircleUserDao() {
	}
	
	/**
	 * 将某个邀请记录的可领取返减少0
	 *
	 * @param wcId 邀请记录id
	 * @param recivable_rebate 减少的金额
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月8日
	 */
	public boolean rebateMinus(long wcId,double recivable_rebate) {
		String sql = "UPDATE t_wealthcircle_user SET recivable_rebate = recivable_rebate - :amt WHERE id=:wcId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amt", recivable_rebate);
		condition.put("wcId", wcId);
		
		return (updateBySQL(sql,condition) == 1);
	}

	/**
	 * 被邀请人投标时更新累计理财、累计返佣、可领取返佣
	 *
	 * @param userId 用户Id
	 * @param investAmt 本次理财金额
	 * @param investRebate 本次增加的累计返佣和可领取返佣金额
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月8日
	 */
	public int updateWcUserRecord(long userId, double investAmt, double investRebate) {
		
		String sql = "UPDATE t_wealthcircle_user SET total_invest=total_invest+:investAmt,total_rebate = total_rebate + :investRebate,recivable_rebate=recivable_rebate + :investRebate WHERE user_id= :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("investAmt", investAmt);
		condition.put("investRebate", investRebate);
		condition.put("userId", userId);
		
		return updateBySQL(sql,condition);
	}

	/**
	 * 查询累计投资总额 和累计推广总额
	 *
	 * @param userName 根据会员名称模糊检索
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月8日
	 */
	public Map<String, Object> findTotalWcAmount(String userName) {

		StringBuffer querySQL=new StringBuffer(" SELECT IFNULL(SUM(wu.total_invest), 0) AS total_invest, IFNULL(SUM(wu.total_rebate), 0) AS total_rebate FROM t_wealthcircle_user wu INNER JOIN t_user u ON u.id = wu.user_id WHERE (wu.user_id IS NOT NULL) AND (wu.user_id > 0) ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(userName)) {
			querySQL.append(" AND u.name LIKE :userName ");
			condition.put("userName", "%" + userName + "%");
		}
		
		return  super.findMapBySQL(querySQL.toString(), condition);
	}
}
