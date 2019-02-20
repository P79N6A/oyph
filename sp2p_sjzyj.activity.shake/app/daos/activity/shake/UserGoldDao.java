package daos.activity.shake;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.activity.shake.entity.t_user_gold;
import models.common.entity.t_user;

public class UserGoldDao extends BaseDao<t_user_gold> {
	

	/**
	 * 
	 *
	 * @Title: saveUserGold
	 * 
	 * @description: 新用户注册成功后给一个金币
	 *
	 * @param user_id
	 * @param gold
	 * @param share_num
	 * @param Time
	 * @return
	 * 
	 * @return Boolean
	 *
	 * @author HanMeng
	 * @createDate 2018年10月30日-上午11:08:39
	 */
	public Boolean saveUserGold(long user_id, int gold, int share_num, Date Time) {
		t_user_gold usergold = new t_user_gold();
		usergold.user_id = user_id;
		usergold.gold = 1;
		usergold.share_num = share_num;
		usergold.Time = new Date();

		return this.save(usergold);
	}
	/**
	 * 
	 *
	 * @Title: getByUserId
	
	 * @description: 根据id查询用户有几个金币
	 *
	 * @param userid
	 * @return 
	   
	 * @return t_user_gold   
	 *
	 * @author HanMeng
	 * @createDate 2018年10月30日-上午11:52:27
	 */
	public t_user_gold getByUserId(Long userid) {
		String sql = "select * from t_user_gold where user_id = :userid";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userid", userid);
		return findBySQL(sql, condition);
	}
	/**
	 * 
	 *
	 * @Title: updateUserGold
	
	 * @description:新用户开户7天内首投再送1金币 
	 *
	 * @param user_id
	 * @return 
	   
	 * @return int   
	 *
	 * @author HanMeng
	 * @createDate 2018年10月31日-下午3:13:56
	 */
	public int updateUserGold(long  user_id){
		String sql="UPDATE t_user_gold SET gold=gold+1 where user_id = :user_id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", user_id);	
		return this.updateBySQL(sql, condition);
		
	}
	/**
	 * 
	 *	
	 * @Title: updateShareNum
	
	 * @description: 每天00:01定时将分享次数归0
	 *
	 * @return 
	   
	 * @return int   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月1日-上午11:35:23
	 */
	public int updateShareNum(){
		String sql = "update t_user_gold set share_num =0";
		return this.updateBySQL(sql, null);
	}
	/**
	 * 
	 *
	 * @Title: alterShareNum
	
	 * @description: 记录分享次数
	 *
	 * @param user_id
	 * @return 
	   
	 * @return int   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月1日-下午1:53:38
	 */
	public int alterShareNum(long  user_id){
		String sql="UPDATE t_user_gold SET share_num=share_num+1 where user_id = :user_id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", user_id);		
		return this.updateBySQL(sql, condition);
	}
	
}
