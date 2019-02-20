package services.activity.shake;

import java.util.Date;
import java.util.List;
import java.util.Random;

import common.utils.Factory;
import common.utils.PageBean;
import daos.activity.shake.ShakeActivityDao;
import daos.activity.shake.ShakeSetDao;
import models.activity.shake.entity.t_shake_activity;
import models.activity.shake.entity.t_shake_set;
import services.base.BaseService;

/**
 * 摇一摇活动Service
 * 
 * @author niu
 * @create 2017-12-08
 */
public class ShakeActivityService extends BaseService<t_shake_activity> {

	protected ShakeSetDao shakeSetDao = Factory.getDao(ShakeSetDao.class);
	
	protected ShakeActivityDao shakeActivityDao;
	
	protected ShakeActivityService() {
		shakeActivityDao = Factory.getDao(ShakeActivityDao.class);
		super.basedao = shakeActivityDao;
	}
	
	/**
	 * 保存摇一摇活动
	 * 
	 * @param name  活动名称
	 * @param ctime 活动时间
	 * @return 添加成功返回true,添加失败返回flase.
	 * 
	 * @author niu
	 * @create 2017-12-08
	 */
	public boolean saveShakeActivity(String name, int ctime) {
		
		return shakeActivityDao.saveShakeActivity(name, ctime);
	}
	
	/**
	 * 分页查询摇一摇活动
	 * 
	 * @param currPage 当前页码
	 * @param pageSize 显示条数
	 * @return 
	 * 
	 * @author niu
	 * @create 2017-12-08
	 */
	public PageBean<t_shake_activity> listOfShakeActivity(int currPage, int pageSize) {
		
		return shakeActivityDao.listOfShakeActivity(currPage, pageSize);
	}
	
	/**
	 * 保存活动概率
	 * 
	 * @param activityId 活动Id
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-11
	 */
	public boolean saveShakeActivityWinRate(long activityId, int number, int shakeTime) {
		
		t_shake_activity activity = shakeActivityDao.findByID(activityId);
		if (activity == null || activity.status != 1) {
			return false;
		}
		
		List<t_shake_set> setList = shakeSetDao.listOfShakeSet(0, activityId);
		
		int prizeNum = 0;
		
		if (setList != null && setList.size() > 0) {
			for (t_shake_set set : setList) {
				prizeNum += set.number;
			}
			
		}
		
		
		int winRate = (activity.ctime * 60 / shakeTime * number) / prizeNum;
		
		activity.winrate = winRate;
		activity.number = number;
		activity.shake_time = shakeTime;
		activity.prize_count = prizeNum;
	
		Random random = new Random();
		activity.win_num = random.nextInt(winRate);
		
		return activity.save() == null ? false : true;
	}
	
	/**
	 * 更改活动状态
	 * 
	 * @param activityId
	 * @param status
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-11
	 */
	public boolean updateActivityStatus(long activityId, int status) {
		
		t_shake_activity activity = shakeActivityDao.findByID(activityId);
		if (activity == null) {
			return false;
		}
		
		activity.status = status;
		
		return activity.save() == null ? false : true;
	}
	
	/**
	 * 活动开始
	 * 
	 * @param activityId
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-11
	 */
	public boolean startActivity(long activityId) {
		
		t_shake_activity activity2 = shakeActivityDao.getOngoingActivity();
		if (activity2 != null) {
			return false;
		}
		
		t_shake_activity activity = shakeActivityDao.findByID(activityId);
		if (activity == null) {
			return false;
		}
		
		if (activity.status != 2) {
			return false;
		}
		
		activity.status = 3;
		activity.stime = new Date();
		
		return activity.save() == null ? false : true;
	}
	
	/**
	 * 活动结束
	 * 
	 * @param activityId
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-11
	 */
	public boolean endActivity(long activityId) {
		
		t_shake_activity activity = shakeActivityDao.findByID(activityId);
		if (activity == null) {
			return false;
		}
		
		if (activity.status != 3) {
			return false;
		}
		
		activity.status = 4;
		activity.stime = new Date();
		
		return activity.save() == null ? false : true;
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
