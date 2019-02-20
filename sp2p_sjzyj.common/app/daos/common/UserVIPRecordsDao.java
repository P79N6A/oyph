package daos.common;

import java.util.Date;

import common.utils.DateUtil;
import daos.base.BaseDao;
import models.common.entity.t_user_vip_records;

/**
 * 用户VIP等级更新记录Dao
 * @author guoShiJie
 * @createDate 2018.11.9
 * */
public class UserVIPRecordsDao extends BaseDao<t_user_vip_records>{
	
	protected UserVIPRecordsDao () {}
	
	/**
	 * 用户VIP等级更新记录添加
	 * @author guoShiJie
	 * @createDate 2018.11.9
	 * */
	public Boolean addUserVIPRecord (Long user_id,Long vipId, Date time) {
		t_user_vip_records records = new t_user_vip_records();
		records.user_id = user_id;
		records.vip_grade_id= vipId;
		records.time = time;
		
		return this.save(records);
	}
	
	/**
	 * 记录查询通过user_id 和 time
	 * @author guoShiJie
	 * @createDate 2018.11.9
	 * */
	public t_user_vip_records queryUserVIPRecordsByUserIdAndDate (Long user_id,Date date) {
		return this.findByColumn("user_id = ? AND DATE_FORMAT(time,'%Y-%m-%d') = ? ", user_id,DateUtil.dateToString(date, "yyyy-MM-dd"));
	}
}
