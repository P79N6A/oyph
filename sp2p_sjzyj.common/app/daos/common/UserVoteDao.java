package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import play.db.jpa.JPA;
import models.common.entity.t_user_vote;
import daos.base.BaseDao;

/**
 * 用户追踪dao
 * @author liuyang
 *
 */
public class UserVoteDao extends BaseDao<t_user_vote> {

	protected UserVoteDao() {}
	
	/**
	 * 查询参加活动的投票总数
	 * @param activityId
	 * @return
	 */
	public int queryTotalUserVotes(long activityId){
		String sql="SELECT COUNT(u.id) FROM t_user_vote u INNER JOIN t_participation p ON u.participation_id = p.id WHERE p.activity_id =:activityId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("activityId", activityId);
		
		return this.findSingleIntBySQL(sql, 0, condition);
	}
	
	/**
	 * 查询投票活动列表
	 * @param userId
	 * @return
	 */
	public List<t_user_vote> queryVoteByUserId(long userId) {
		String sql="SELECT * FROM t_user_vote u WHERE u.user_id =:userId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return this.findListBySQL(sql, condition);
	}
	
	/**
	 * 根据时间查询实体
	 * @return
	 */
	public t_user_vote queryVoteByTime(long partId) {
		String sql="SELECT * FROM t_user_vote u where u.activity_id =:partId order by u.time desc";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("partId", partId);
		return this.findBySQL(sql, condition);
	}
	
}
