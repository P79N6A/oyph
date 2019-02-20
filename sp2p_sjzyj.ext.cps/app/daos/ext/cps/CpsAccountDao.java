package daos.ext.cps;

import java.util.HashMap;
import java.util.Map;

import models.ext.cps.entity.t_cps_account;
import daos.base.BaseDao;

/**
 * CPS账号具体实现dao
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月15日
 */
public class CpsAccountDao extends BaseDao<t_cps_account> {

	protected CpsAccountDao() {

	}
	
	/**
	 * 用户CPS账户资金减少
	 *
	 * @param spreaderId 用户id
	 * @param amount 减少的资金
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月16日
	 */
	public boolean cpsAccountMinus(long spreaderId,double amount) {
		String sql = "UPDATE t_cps_account SET  balance = balance-:amount WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", spreaderId);
		condition.put("amount", amount);
		
		return (updateBySQL(sql,condition)==1);
	}
	
	/**
	 * CPS账户金额增加
	 *
	 * @param userId 用户的ID 
	 * @param amount 增加的金额
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月16日
	 */
	public int cpsAccountFundAdd(long userId, double amount) {
		String sql = "UPDATE t_cps_account SET balance = balance + :amount WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amount", amount);
		condition.put("userId", userId);
		
		return updateBySQL(sql,condition);
	}
	
}
