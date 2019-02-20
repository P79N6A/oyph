package daos.common;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_ssq_user;


public class ssqUserDao extends BaseDao<t_ssq_user>{

	
	/**
	 * 通过 userId 查找成功的用户信息
	 * 
	 * @param userId
	 * @return
	 */
	public t_ssq_user findByUserId(long userId) {
		
		String sql = "SELECT * FROM t_ssq_user tui WHERE tui.user_id = :userId and application_status =1";
		Map<String, Object> condition  = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return findBySQL(sql, condition);
				
	}
	
	/**
	 * 通过 userId 查找用户信息
	 * 
	 * @param userId
	 * @return
	 */
	public t_ssq_user findssqByUserId(long userId) {
		
		String sql = "SELECT * FROM t_ssq_user tui WHERE tui.user_id = :userId";
		Map<String, Object> condition  = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return findBySQL(sql, condition);
				
	}
	
	
	/**
	 * 通过 userId 查找用户信息
	 * 
	 * @param userId
	 * @return
	 */
	public t_ssq_user findByAccount(String  account) {
		
		String sql = "SELECT * FROM t_ssq_user tui WHERE tui.account = :account";
		Map<String, Object> condition  = new HashMap<String, Object>();
		condition.put("account", account);
		
		return findBySQL(sql, condition);
				
	}
}
