package daos.activity.shake;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.core.joran.conditional.Condition;

import java.util.List;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.activity.shake.entity.t_shake_activity;
import models.activity.shake.entity.t_shake_record;
import models.activity.shake.entity.t_shake_set;

/**
 * 摇一摇活动中奖记录DAO
 * 
 * @author niu
 * @create 2017-12-08
 */
public class ShakeRecordDao extends BaseDao<t_shake_record> {
	
	
	
	/**
	 * 保存奖项
	 * 
	 * @param type			奖项类型
	 * @param amount		奖项大小
	 * @param number		中奖号码
	 * @param activityId	活动Id
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-11
	 */
	public boolean saveShakeRecord(int type, int amount,String prize_name, int number, long activityId, double useRule) {
		
		String sql = "INSERT IGNORE INTO t_shake_record (type, amount, prize_name,number, activity_id, use_rule) VALUES (:type, :amount, :prize_name :number, :activityId, :useRule)";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		condition.put("amount", amount);
		condition.put("prize_name", prize_name);
		condition.put("number", number);
		condition.put("activityId", activityId);
		condition.put("useRule", useRule);
		
		return updateBySQL(sql, condition) == 1 ? true : false;
		
		
		
		
		
		/*t_shake_record record = new t_shake_record();
		
		record.type = type;
		record.amount = amount;
		record.activity_id = activityId;
		record.number = number;
		
		return record.save() == null ? false : true;
		*/
	}
	
	/**
	 * 删除活动中奖记录
	 * 
	 * @param activityId 活动Id
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-08
	 */
	public boolean deleteShakeRecord(long activityId) {
		
		String sql = "DELETE FROM t_shake_record  WHERE activity_id = :activityId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("activityId", activityId);
		
		return deleteBySQL(sql, condition) > 0 ? true : false;
	}
	
	/**
	 * 中奖记录
	 * 
	 * @param activityId	活动Id
	 * @param winner		用户Id不为0
	 * @param currPage		当前页码
	 * @param pageSize		显示条数
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-08
	 */
	public PageBean<t_shake_record> pageOfShakeRecord(long activityId, int winner, int currPage, int pageSize) {
		
		StringBuffer querySQL = new StringBuffer("SELECT * FROM t_shake_record t WHERE t.activity_id = :activityId");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(t.id) FROM t_shake_record t WHERE t.activity_id = :activityId");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("activityId", activityId);
		
		if (winner > 0) {
			querySQL.append(" AND t.user_id <> 0");
			countSQL.append(" AND t.user_id <> 0");
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), t_shake_record.class, condition);	
	}
	
	/**
	 * 中奖次数
	 * @param activityId
	 * @return
	 */
	public int winCount(long activityId) {
		String sql = "SELECT COUNT(t.id) FROM t_shake_record t WHERE t.activity_id = :activityId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("activityId", activityId);
		
		return findSingleIntBySQL(sql, 0, condition);
	}
	
	
	/**
	 * 是否中奖
	 * 
	 * @param activityId
	 * @param winningNumber
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-12
	 */
	public t_shake_record getShakeRecord(long activityId, int winningNumber) {
		String sql = "SELECT * FROM t_shake_record t WHERE t.activity_id = :activityId AND t.number = :winningNumber";
		
		Map<String, Object> condition =new HashMap<String, Object>();
		
		condition.put("activityId", activityId);
		condition.put("winningNumber", winningNumber);
		
		return findBySQL(sql, condition);
	}
	
	/**
	 * pc页面中奖纪录
	 * @param activityId
	 * @return
	 * 
	 * @author LiuPengwei
	 * @create 2017年12月13日
	 */
	public List<t_shake_record> queryUserShakeRecord (long activityId){
		String sql = "SELECT * FROM t_shake_record  where activity_id =:activityId and user_id <>0 ORDER BY create_time asc";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("activityId", activityId);
		
		return this.findListBySQL(sql, condition);		
	}
	
	
	/**
	 * pc页面中奖纪录
	 * @param activityId
	 * @return
	 * 
	 * @author LiuPengwei
	 * @create 2017年12月13日
	 */
	public PageBean<t_shake_record> pageOfUserShakeRecord (long activityId , int currPage, int pageSize, Date current_time){
		String querySQL = "SELECT * FROM t_shake_record  where activity_id =:activityId and user_id <>0 and create_time > :current_time ORDER BY create_time asc";
		String countSQL = "SELECT COUNT(id) FROM t_shake_record  where activity_id =:activityId and user_id <>0 and create_time > :current_time ORDER BY create_time asc";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("activityId", activityId);
		condition.put("current_time", current_time);
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), t_shake_record.class, condition);
		
	}
	/**
	 * 
	 * @Title: pageOfMyShakeRecord
	 * 
	 * @description 显示年会摇一摇个人中奖信息
	 * @param  userId
	 * @return List<t_shake_record>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月29日-下午2:12:42
	 */
	public t_shake_record findMyShakeRecord(long userId) {
		String querySql = "SELECT * FROM t_shake_record where type > 4 and user_id =:userId ORDER BY create_time DESC";
		
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return findBySQL(querySql, condition);
	}
	/**
	 * 
	 * @Title: findRecoredByUserId
	 * 
	 * @description 通过userId列查询中奖纪录 ,判断该用户是否摇到过奖
	 * @param  userId
	 * @return t_shake_record    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月2日-下午2:01:21
	 */
	public t_shake_record findRecoredByUserId(long userId) {
		String sql = "SELECT * FROM t_shake_record WHERE user_id =:userId and type > 4";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return findBySQL(sql, condition);
	}
	
}

















