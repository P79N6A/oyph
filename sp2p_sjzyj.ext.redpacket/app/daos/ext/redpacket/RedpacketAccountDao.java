package daos.ext.redpacket;

import java.util.HashMap;
import java.util.Map;

import models.ext.redpacket.entity.t_red_packet_account;
import daos.base.BaseDao;

/**
 * 用户红包账号Dao
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月14日
 */
public class RedpacketAccountDao extends BaseDao<t_red_packet_account> {

	protected RedpacketAccountDao() {
	}
	
	/**
	 * 更新用户红包资产签名
	 *
	 * @param userId 会员ID
	 * @param userFundsign 更新的签名
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月16日
	 */
	public int updateUserVisualFundSign(long userId, String userFundsign) {
		String sql = "UPDATE t_red_packet_account SET fund_sign = :sign WHERE user_id = :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("sign", userFundsign);
		condition.put("userId", userId);
		
		return updateBySQL(sql, condition);
	}
	
	/**
	 * 红包金额余额增加
	 *
	 * @param userId  用户ID
	 * @param amount  增加资金
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月22日
	 */
	public int accountFundAdd(long userId, double amount) {
		String sql = "UPDATE t_red_packet_account SET  balance = balance + :amount WHERE user_id =:userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amount", amount);
		condition.put("userId", userId);
		
		return updateBySQL(sql,condition);
	}

	/**
	 * 红包金额减少
	 *
	 * @param userId  用户ID
	 * @param amount  减少金额
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月22日
	 */
	public int accountFundMinus(long userId, double amount) {
		String sql = "UPDATE t_red_packet_account SET  balance = balance - :amount WHERE user_id =:userId AND balance >= :amount";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amount", amount);
		condition.put("userId", userId);
		
		return updateBySQL(sql,condition);
	} 

}
