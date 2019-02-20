package services.activity.shake;

import java.util.Date;
import java.util.List;

import common.utils.Factory;
import daos.activity.shake.UserGoldDao;
import daos.common.UserDao;
import daos.common.UserInfoDao;
import models.activity.shake.entity.t_user_gold;
import models.common.entity.t_user_info;
import services.base.BaseService;

public class UserGoldService extends BaseService<t_user_gold> {

	protected static UserGoldDao userGoldDao = Factory.getDao(UserGoldDao.class);
	protected static final UserInfoDao userInfoDao = Factory.getDao(UserInfoDao.class);
	protected static final UserDao userDao = Factory.getDao(UserDao.class);

	protected UserGoldService() {

	}

	/**
	 * 
	 *
	 * @Title: saveUserGold
	 * 
	 * @description: 注册成功后金币表插入数据
	 *
	 * @param user_id
	 * @param gold
	 * @param share_num
	 * @param Time
	 * 
	 * @return void
	 *
	 * @author HanMeng
	 * @createDate 2018年10月25日-下午5:32:38
	 */

	public boolean saveUserGold(long user_id, int gold, int share_num, Date Time) {
		Date time = new Date();
		// List<t_user_gold> usergold = userGoldDao.findAll();
		t_user_info userInfo = userInfoDao.findByColumn(" user_id = ? ", user_id);
		if (userInfo != null) {
			user_id = userInfo.user_id;
		}
		t_user_gold usergold = new t_user_gold();
		usergold.gold = 1;
		usergold.share_num = share_num;
		return userGoldDao.saveUserGold(user_id, gold, share_num, time);
	}

	/**
	 * 
	 *
	 * @Title: saveUserGold
	 * 
	 * @description: 注册成功后金币表插入数据
	 *
	 * @param gold
	 * @param share_num
	 * @param Time
	 * 
	 * @return void
	 *
	 * @author HanMeng
	 * @createDate 2018年10月25日-下午5:32:38
	 */
	public static t_user_gold saveUserGold(long user_id) {
		t_user_gold usergold = new t_user_gold();
		usergold.user_id = user_id;
		usergold.gold++;
		usergold.Time = new Date();

		return usergold.save();

	}

	/**
	 * 
	 *
	 * @Title: getByUserId
	 * 
	 * @description: 根据id查询用户金币数量
	 *
	 * @param userid
	 * @return
	 * 
	 * @return t_user_gold
	 *
	 * @author HanMeng
	 * @createDate 2018年10月30日-下午1:46:52
	 */
	public t_user_gold getByUserId(Long userid) {
		return userGoldDao.getByUserId(userid);
	}

	/**
	 * 
	 *
	 * @Title: updateUserGold
	 * 
	 * @description: 加一个金币
	 *
	 * @param user_id
	 * @return
	 * 
	 * @return int
	 *
	 * @author HanMeng
	 * @createDate 2018年11月1日-上午9:11:52
	 */
	public int updateUserGold(long user_id) {
		return userGoldDao.updateUserGold(user_id);

	}

	/**
	 * 每天00:01定时将分享次数归0
	 *
	 * @Title: updateShareNum
	 * 
	 * @description:
	 *
	 * @return
	 * 
	 * @return int
	 *
	 * @author HanMeng
	 * @createDate 2018年11月1日-上午11:38:12
	 */
	public int updateShareNum() {
		return userGoldDao.updateShareNum();
	}

	/**
	 * 
	 * @Title: alterShareNum
	 * @description: 记录分享次数
	 *
	 * @param user_id
	 * @return
	 * @return int
	 *
	 * @author HanMeng
	 * @createDate 2018年11月1日-下午2:14:17
	 */
	public int alterShareNum(long user_id) {
		return userGoldDao.alterShareNum(user_id);
	}

}
