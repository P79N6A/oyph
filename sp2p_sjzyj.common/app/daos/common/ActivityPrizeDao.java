package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_activity_prize;
import daos.base.BaseDao;

/**
 * 投票活动奖品规则dao
 * @author Administrator
 *
 */
public class ActivityPrizeDao extends BaseDao<t_activity_prize>{

	protected ActivityPrizeDao(){}
	
	/**
	 * 通过活动的id查询奖品列表
	 * @param activityId
	 * @return
	 */
	public List<t_activity_prize> queryActivityPrize(long activityId) {
		String sql="SELECT * FROM t_activity_prize p WHERE p.activity_id =:activityId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("activityId", activityId);
		
		return this.findListBySQL(sql, condition);
	}
}
