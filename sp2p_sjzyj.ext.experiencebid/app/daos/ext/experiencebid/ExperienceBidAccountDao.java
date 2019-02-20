package daos.ext.experiencebid;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.ext.experience.entity.t_experience_bid_account;
import daos.base.BaseDao;

public class ExperienceBidAccountDao extends BaseDao<t_experience_bid_account>{
	
	public ExperienceBidAccountDao(){}


	/**
	 * 增加体验金账户余额
	 *
	 * @param userId
	 * @param amount
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public int addExperienceAccountIncome(long userId, double amount) {
		String sql = "UPDATE t_experience_bid_account SET balance=(balance+:amount) WHERE user_id=:userId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", amount);
		params.put("userId", userId);
		
		return super.updateBySQL(sql, params);
	}
	
	/**
	 * 获取体验金
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	public int getExperienceGold(long userId) {
		
		String sql = "UPDATE t_experience_bid_account SET amount=(amount+every_grant), last_receive_time=:now, count=(count-1) WHERE user_id=:userId AND count>0";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("now", new Date());
		params.put("userId", userId);
		
		return super.executSQL(sql, params);
	}

	/**
	 * 更新用户体验金账户资产验签
	 *
	 * @param userId
	 * @param sign
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	public int updateUserExperienceGoldSign(long userId, String sign) {
		String sql = "UPDATE t_experience_bid_account SET fund_sign = :sign WHERE user_id = :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("sign", sign);
		condition.put("userId", userId);
		
		return updateBySQL(sql, condition);
	}

	/**
	 * 冻结用户体验账户的体验金
	 *
	 * @param investAmount
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public int FreezeUserExperienceGold(double investAmount, long userId) {
		
		String sql = "UPDATE t_experience_bid_account SET amount=(amount-:investAmount), freeze=(freeze+:investAmount) WHERE user_id=:userId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("investAmount", investAmount);
		params.put("userId", userId);

		return super.updateBySQL(sql, params);
	}

	/**
	 * 扣除用户投资体验账户的体验金
	 *
	 * @param userId
	 * @param amount
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public int experienceUserFundMinusFreezeAmt(long userId, double amount) {
		
		String sql = "UPDATE t_experience_bid_account SET freeze=(freeze-:amount) WHERE user_id=:userId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", amount);
		params.put("userId", userId);

		return super.updateBySQL(sql, params);
	}
	
	/**
	 * 扣除体验金账户余额
	 *
	 * @param userId
	 * @param amount
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public int minusExperienceAccountIncome(long userId, double amount) {
		String sql = "UPDATE t_experience_bid_account SET balance=(balance-:amount) WHERE user_id=:userId AND balance>=:amount";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("amount", amount);
		params.put("userId", userId);
		
		return super.updateBySQL(sql, params);
	}
	
	/**
	 * 根据用户id查询用户体验账户信息
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	public t_experience_bid_account findUserExperienceAccount(long userId) {
		
		String sql = "SELECT * FROM t_experience_bid_account WHERE user_id=:userId";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		
		return super.findBySQL(sql, params);
	}

	/**
	 * 查询用户体验金账户的余额
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	public double findUserExperienceGold(long userId) {
		
		String sql = "SELECT IFNULL(amount, 0.0) FROM t_experience_bid_account WHERE user_id=:userId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		
		return super.findSingleDoubleBySQL(sql, 0.00, params);
	}
}
