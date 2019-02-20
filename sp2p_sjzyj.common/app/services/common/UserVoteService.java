package services.common;

import java.util.List;

import common.utils.Factory;
import daos.common.ParticipationDao;
import daos.common.UserVoteDao;
import models.common.entity.t_participation;
import models.common.entity.t_user_vote;
import services.base.BaseService;

public class UserVoteService extends BaseService<t_user_vote> {

	protected static UserVoteDao userVoteDao = Factory.getDao(UserVoteDao.class);
	
	protected UserVoteService() {
		super.basedao = this.userVoteDao;
	}
	
	/**
	 * 查询参加活动的投票总数
	 * @param activityId
	 * @return
	 */
	public int queryTotalUserVotes(long activityId){
		
		return userVoteDao.queryTotalUserVotes(activityId);
	}
	
	/**
	 * 查询投票活动列表
	 * @param userId
	 * @return
	 */
	public List<t_user_vote> queryVoteByUserId(long userId) {
		
		return userVoteDao.queryVoteByUserId(userId);
	}
	
	/**
	 * 根据时间查询实体
	 * @return
	 */
	public t_user_vote queryVoteByTime(long partId) {
		
		return userVoteDao.queryVoteByTime(partId);
	}
}
