package daos.activity.shake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.activity.shake.entity.t_shake_set;

/**
 * 摇一摇活动设置DAO
 * 
 * @author niu
 * @create 2017-12-08
 */
public class ShakeSetDao extends BaseDao<t_shake_set> {
	
	/**
	 * 保存摇一摇活动设置
	 * 
	 * @param type			设置类型
	 * @param activityId	活动Id
	 * @param amount		奖项大小
	 * @param number		奖项数量
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-09
	 */
	public boolean saveShakeSet(int type, long activityId, int amount, String prize_name,int number, double use_rule) {
		
		t_shake_set shakeSet = new t_shake_set();
		
		shakeSet.activity_id = activityId;
		shakeSet.type = type;
		shakeSet.amount = amount;
		shakeSet.prize_name = prize_name;
		shakeSet.number = number;
		shakeSet.use_rule = use_rule;
		
		return shakeSet.save() == null ? false : true;
	}
	
	/**
	 * 通过活动设置ID删除活动设置
	 * 
	 * @param shakeSetId
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-09
	 */
	public boolean deleteShakeSet(long shakeSetId) {
		t_shake_set shakeSet = findByID(shakeSetId);
		
		return shakeSet.delete() == null ? false : true;
	}
	
	
	/**
	 * 通过类型和活动Id查询活动设置
	 * 
	 * @param type		    奖项类型
	 * @param activityId 活动ID
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-09
	 */
	public List<t_shake_set> listOfShakeSet(int type, long activityId) {
		
		StringBuffer sql = new StringBuffer("SELECT * FROM t_shake_set t WHERE t.activity_id = :activityId");
		
		//String sql = "SELECT * FROM t_shake_set t WHERE t.activity_id = :activityId AND t.type = :type";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("activityId", activityId);
		
		if (type > 0) {
			sql.append(" AND t.type = :type");
			
			condition.put("type", type);
		}
		
		
		return findListBySQL(sql.toString(), condition);
	}
	
	/**
	 * 查询活动设置的奖项
	 * 
	 * @param type
	 * @param activityId
	 * @return
	 */
	public t_shake_set getShakeSet(int type, long activityId) {
		
		String sql = "SELECT * FROM t_shake_set ss WHERE ss.activity_id = :activityId AND ss.type = :type";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("activityId", activityId);
		condition.put("type", type);
		
		return findBySQL(sql, condition);
	}
	
}
