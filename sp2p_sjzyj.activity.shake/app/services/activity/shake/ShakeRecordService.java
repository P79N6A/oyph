package services.activity.shake;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.activity.shake.ShakeActivityDao;

import daos.activity.shake.ShakeRecordDao;
import daos.activity.shake.ShakeSetDao;
import models.activity.shake.entity.t_shake_activity;

import models.activity.shake.entity.t_shake_record;
import models.activity.shake.entity.t_shake_set;
import services.base.BaseService;

public class ShakeRecordService extends BaseService<t_shake_record> {
	
	protected ShakeSetDao shakeSetDao = Factory.getDao(ShakeSetDao.class);
	
	protected ShakeActivityDao shakeActivityDao = Factory.getDao(ShakeActivityDao.class);
	
	protected ShakeRecordDao shakeRecordDao;
	
	protected ShakeRecordService() {
		shakeRecordDao = Factory.getDao(ShakeRecordDao.class);
		this.basedao = shakeRecordDao;
	}
	
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
	public boolean saveShakeRecord(long activityId) {
		
		
		
		t_shake_activity activity = shakeActivityDao.findByID(activityId);
		if (activity == null) {
			return false;
		}

		if (activity.status == 1) {
			shakeRecordDao.deleteShakeRecord(activityId);
			List<t_shake_set> setList = shakeSetDao.listOfShakeSet(0, activityId);
			 
			if (setList == null || setList.size() <= 0) {
				return false;
			}
			Random random = new Random(10);
			int maxValue = activity.ctime * 60 / activity.shake_time * activity.number;
			
			for (t_shake_set shakeSet : setList) {

				for (int i = 0; i < shakeSet.number; i++) {
					int winNumber = random.nextInt(maxValue);
					if (!shakeRecordDao.saveShakeRecord(shakeSet.type, shakeSet.amount, shakeSet.prize_name ,winNumber, activityId, shakeSet.use_rule)) {
						boolean flag = true;
						int k = 0;
						do {
							winNumber = random.nextInt(maxValue);
							if (shakeRecordDao.saveShakeRecord(shakeSet.type, shakeSet.amount,shakeSet.prize_name, winNumber, activityId, shakeSet.use_rule)) {
								flag = false;
							}
							k++;
							
							if (k > 100) {
								break;
							}
						} while (flag);

					}
				}
			}
			return true;
		}

		return false;
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
		
		return shakeRecordDao.pageOfShakeRecord(activityId, winner, currPage, pageSize);
	}
	
	
	/**
	 * pc页面显示中奖纪录
	 * 
	 * @param activityId	活动Id
	 * @return
	 * 
	 * @author LiuPengwei
	 * @create 2017年12月13日
	 */
	public List<t_shake_record> queryUserShakeRecord(long activityId) {
		
		return shakeRecordDao.queryUserShakeRecord(activityId);
	}
	
	
	/**
	 * pc页面显示中奖纪录
	 * 
	 * @param activityId	活动Id
	 * @return
	 * 
	 * @author LiuPengwei
	 * @create 2017年12月13日
	 */
	public PageBean<t_shake_record> pageOfUserShakeRecord(long activityId, int currPage, int pageSize, Date current_time) {
		
		return shakeRecordDao.pageOfUserShakeRecord(activityId, currPage, pageSize, current_time);
	}
	
	/**
	 * 查询活动中奖次数
	 * @param activityId
	 * @return
	 */
	public int winCount(long activityId) {
		return shakeRecordDao.winCount(activityId);
	}
	
	/**
	 * 分享获取红包准备
	 * 
	 * @author niu
	 * @create 2017-12-31
	 */
	 public ResultInfo gainRedPacketPrepare(long redPacketId) {
		 ResultInfo result = new ResultInfo();
		 
		 t_shake_record record = shakeRecordDao.findByID(redPacketId);
		 if (record == null) {
			 
			 result.code = -1;
			 result.msg  = "红包不存在"; 
			
			 return result;
		 }
		
		 if (record.amount > 66 || record.amount <= 0 || record.type != 1) {
			 result.code = -1;
			 result.msg  = "红包金额或者类型不符合"; 
			
			 return result;
		 }
		 
		 if (record.gain_count >= 1 && record.gain_count <= 3) {
			 
			 result.code = 1;
			 result.msg  = "可以获取红包"; 
			 result.obj = record;
				
			 return result;
		}
		 
		 result.code = -1;
		 result.msg  = "红包"; 
		
		 return result;
	 }
	
	 /**
	  * 增加红包获取次数
	  * @param redPacketId
	  * @return
	  * 
	  * @author niu
	  * @create 2017-12-31
	  */
	 public ResultInfo addGainCountOfRedPacket(long redPacketId, long userId) {
		 
		 ResultInfo result = new ResultInfo();
		 
		 t_shake_record record = shakeRecordDao.findByID(redPacketId);
		 if (record == null) {
			 
			 result.code = -1;
			 result.msg  = "红包不存在"; 
			
			 return result;
		 }
		 
		 record.gain_count = record.gain_count + 1;
		 record.gain_user = record.gain_user + "," + userId;
		 
		 t_shake_record record2 = record.save();
		 
		 if (record2 == null) {
			 result.code = -1;
			 result.msg  = "增加红包获取次数失败"; 
			
			 return result;
		}
		 
		 result.code = 1;
		 result.msg  = "增加红包获取次数成功"; 
		
		 return result;
	 }

	
	 
	
}























