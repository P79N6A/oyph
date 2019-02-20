package services.common;

import java.util.List;

import common.utils.Factory;
import daos.common.ActivityPrizeDao;
import models.common.entity.t_activity_prize;
import services.base.BaseService;

/**
 * 投票活动奖品规则service
 * @author Administrator
 *
 */
public class ActivityPrizeService extends BaseService<t_activity_prize>{

	protected ActivityPrizeDao activityPrizeDao = Factory.getDao(ActivityPrizeDao.class);
	
	protected ActivityPrizeService(){
		this.basedao=activityPrizeDao;
	}
	
	/**
	 * 通过活动的id查询奖品列表
	 * @param activityId
	 * @return
	 */
	public List<t_activity_prize> queryActivityPrize(long activityId) {
		
		return activityPrizeDao.queryActivityPrize(activityId);
	}
}
