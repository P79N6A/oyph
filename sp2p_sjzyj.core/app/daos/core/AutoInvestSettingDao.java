package daos.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.core.entity.t_auto_invest_setting;
import daos.base.BaseDao;

/** 
 * 自动投标设置接口实现类
 * 
 * @description 
 * 
 * @author ZhouYuanZeng 
 * @createDate 2016年3月29日 上午9:30:13  
 */
public class AutoInvestSettingDao extends BaseDao<t_auto_invest_setting> {
	protected AutoInvestSettingDao() {}
	
	/**
	 * 根据userId查询用户自动投标设置
	 *  
	 * @param userId 用户id
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年3月24日
	 *
	 */
	public t_auto_invest_setting findAutoInvestOptionByUserId(long userId){
		String sql="SELECT * FROM t_auto_invest_setting WHERE user_id =:user_id";
		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("user_id", userId);
		
		return super.findBySQL(sql, condition);
	}
	
	/**
	 * 更新自动投标设置状态
	 *  
	 * @param userId 用户id
	 * @param status
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年3月24日
	 *
	 */
	public int updateAutoInvestStatus(long userId, int status) {
		
		String updSql = "UPDATE t_auto_invest_setting SET status=:status WHERE user_id=:userId ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("userId", userId);
		
		return super.updateBySQL(updSql, params);
	}
	
	/**
	 * 查出所有开启自动投标的用户
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年3月24日
	 *
	 */
	public List<Object> queryAllAutoUser(){
		String sql = "select user_id from t_auto_invest_setting where status = 1 ";
		
		return super.findListSingleBySQL(sql, null);
	}
	
	/**
	 * 将该用户排到队尾
	 *  
	 * @param userId
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年3月24日
	 *
	 */
	public int updateUserAutoBidTime(long userId) {
		String sql = "update t_auto_invest_setting set wait_time = :waitTime where user_id = :userId";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("waitTime", new Date());
		params.put("userId", userId);
		
		return super.updateBySQL(sql, params);
	}
	
	/**
	 * 标的是否符合用户设置的条件
	 *  
	 * @param autoOptions
	 * @param unit
	 * @param bidId
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年3月24日
	 *
	 */
	public long findBidByParam(t_auto_invest_setting autoOptions, int unit, long bidId) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id from t_bid where id =:bid_id");
		sql.append(" and apr >= :min_apr");
		sql.append(" and min_invest_amount <= :amount");
		//月标
		if(unit == 2 ){
			sql.append(" and period <= :max_period");
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bid_id", bidId);
		params.put("min_apr", autoOptions.min_apr);
		params.put("amount", autoOptions.amount);
		if(unit == 2 ){
			params.put("max_period", autoOptions.max_period);
		}
		
		return super.findSingleLongBySQL(sql.toString(), 0, params);
	}

}
