package dao.ext.wealthcircle;

import java.util.HashMap;
import java.util.Map;

import models.ext.wealthcircle.entity.t_wealthcircle_account;
import daos.base.BaseDao;

/**
 * 用户财富圈账户dao
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年4月6日
 */
public class WealthCircleAccountDao extends BaseDao<t_wealthcircle_account> {

	protected WealthCircleAccountDao() {
	}
	
	/**
	 * 更新已经生成邀请码的理财金额
	 *
	 * @param userId
	 * @param amt
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月7日
	 */
	public boolean updateConvertAmount(Long userId, double amt) {
		String excSQL = " UPDATE t_wealthcircle_account wa SET wa.has_convert_amount = wa.has_convert_amount + :amt WHERE wa.user_id = :userId ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amt", amt);
		condition.put("userId", userId);
		
		return updateBySQL(excSQL, condition)==1;
	}
	
	/**
	 * 财富圈账户金额减少
	 *
	 * @param spreaderId 用户id
	 * @param amount 减少的资金
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月7日
	 */
	public boolean wcAccountMinus(long spreaderId,double amount) {
		String sql = "UPDATE t_wealthcircle_account SET  balance = balance - :amount WHERE user_id = :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", spreaderId);
		condition.put("amount", amount);
		
		return (updateBySQL(sql,condition)==1);
	}
	
	/**
	 * 财富圈账户金额增加
	 *
	 * @param userId 用户的ID 
	 * @param amount 增加的金额
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月7日
	 */
	public int wcAccountFundAdd(long userId, double amount) {
		String sql = "UPDATE t_wealthcircle_account SET balance = balance + :amount WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amount", amount);
		condition.put("userId", userId);
		
		return updateBySQL(sql,condition);
	}
}
