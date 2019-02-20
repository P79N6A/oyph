package services.activity.shake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.sun.org.apache.bcel.internal.generic.RETURN;

import common.utils.Factory;
import daos.activity.shake.ShakeActivityDao;
import daos.activity.shake.ShakeSetDao;
import models.activity.shake.entity.t_shake_activity;
import models.activity.shake.entity.t_shake_set;
import services.base.BaseService;

/**
 * 摇一摇活动Service
 * 
 * @author niu
 * @create 2017-12-09
 */
public class ShakeSetService extends BaseService<t_shake_set> {

	protected ShakeActivityDao shakeActivityDao = Factory.getDao(ShakeActivityDao.class);
	
	protected ShakeActivityService shakeActivityService = Factory.getService(ShakeActivityService.class);
	
	protected ShakeSetDao shakeSetDao;
	
	protected ShakeSetService() {
		shakeSetDao = Factory.getDao(ShakeSetDao.class);
		this.basedao = shakeSetDao;
	}
	
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
		
		/*boolean saveSuccess = shakeSetDao.saveShakeSet(type, activityId, amount, number);
		
		if (type == 4 && saveSuccess && shakeActivityService.saveShakeActivityWinRate(activityId, number, amount)) {
			return true;
		} else {
			
		}*/
		t_shake_activity activity = shakeActivityDao.findByID(activityId);
		
		if (activity.status != 1) {
			return false;
		}
		
		return shakeSetDao.saveShakeSet(type, activityId, amount, prize_name,number, use_rule);
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
	public boolean deleteShakeSet(long shakeSetId, long activityId) {
		
		t_shake_activity activity = shakeActivityDao.findByID(activityId);
		
		if (activity.status != 1) {
			return false;
		}
		
		return shakeSetDao.deleteShakeSet(shakeSetId);
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
		
		return shakeSetDao.listOfShakeSet(type, activityId);
	}
	
	/**
	 * 随机中的奖项
	 * 
	 * @param type
	 * @param activityId
	 * @return
	 */
	public t_shake_set getShakeSet(long activityId) {
		
		Random random = new Random();
		//随机生成一个[0,3)之间的数+1之后是 1,2,3
		int randomNum = random.nextInt(3) + 1;
		t_shake_set set = null;
		
		switch (randomNum) {
		//当随机数为1时
		case 1:
			
			set = shakeSetDao.getShakeSet(8, activityId);
			//若红包奖项有,返回
			if (set != null && set.number > 0) {
				return set;
			}
			//如果没有1,2,3
			randomNum = random.nextInt(2) + 1;
			
			if (randomNum == 1) {
				
				set = shakeSetDao.getShakeSet(6, activityId);
				
				if (set != null && set.number > 0) {
					return set;
				}
				set = shakeSetDao.getShakeSet(7, activityId);
				
				if (set != null && set.number > 0) {
					return set;
				}
				
				return shakeSetDao.getShakeSet(5, activityId);
			} else  {
				set = shakeSetDao.getShakeSet(7, activityId);
				if (set != null && set.number > 0) {
					return set;
				}
				
				set = shakeSetDao.getShakeSet(6, activityId);
				if (set != null && set.number > 0) {
					return set;
				}
				set = shakeSetDao.getShakeSet(5, activityId);

				
			}
			
			break;
		case 2:
			set = shakeSetDao.getShakeSet(7, activityId);
		
			if (set != null && set.number > 0) {
				return set;
			}
			
			randomNum = random.nextInt(2) + 1;
			
			if (randomNum == 1) {
				
				set = shakeSetDao.getShakeSet(8, activityId);
			
				if (set != null && set.number > 0) {
					return set;
				}
				set = shakeSetDao.getShakeSet(6, activityId);
				if (set != null && set.number > 0) {
					return set;
				}
				
				return shakeSetDao.getShakeSet(5, activityId);
			} else {
				set = shakeSetDao.getShakeSet(6, activityId);
				
				if (set != null && set.number > 0) {
					return set;
				}
				set = shakeSetDao.getShakeSet(8, activityId);
				
				if (set != null && set.number > 0) {
					return set;
				}
				set = shakeSetDao.getShakeSet(5, activityId);
				
				
			}
			
			break;
		case 3:
			set = shakeSetDao.getShakeSet(6, activityId);
			
			if (set != null && set.number > 0) {
				return set;
			}
			
			randomNum = random.nextInt(2) + 1;
			
			if (randomNum == 1) {
				set = shakeSetDao.getShakeSet(7, activityId);
				
				if (set != null && set.number > 0) {
					return set;
				}
				set = shakeSetDao.getShakeSet(8, activityId);
				
				if (set != null && set.number > 0) {
					return set;
				}
				
				return shakeSetDao.getShakeSet(5, activityId);
			} else {
				set = shakeSetDao.getShakeSet(8, activityId);
				
				if (set != null && set.number > 0) {
					return set;
				}
				
				set = shakeSetDao.getShakeSet(7, activityId);
				
				if (set != null && set.number > 0) {
					return set;
				}
				set = shakeSetDao.getShakeSet(5, activityId);
				
				
			}
			break;
		default:
			
			break;
		}
		
		
		return set;
	}
	
	
	
	
}

























