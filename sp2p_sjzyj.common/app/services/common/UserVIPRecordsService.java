package services.common;

import java.util.Date;

import common.utils.Factory;
import daos.common.UserVIPRecordsDao;
import models.common.entity.t_user_vip_records;
import services.base.BaseService;

public class UserVIPRecordsService extends BaseService<t_user_vip_records>{

	protected final UserVIPRecordsDao userVIPRecordsDao = Factory.getDao(UserVIPRecordsDao.class);
	
	protected UserVIPRecordsService () {
		super.basedao = this.userVIPRecordsDao;
	}
	
	/**
	 * 用户VIP等级更新记录添加
	 * @author guoShiJie
	 * @createDate 2018.11.9
	 * */
	public Boolean addUserVIPRecord (Long user_id,Long vipId, Date time) {
		return this.queryUserVIPRecordsByUserIdAndDate(user_id, time) == null ? userVIPRecordsDao.addUserVIPRecord(user_id, vipId, time):false;
	}
	
	/**
	 * 记录查询通过user_id 和 time
	 * @author guoShiJie
	 * @createDate 2018.11.9
	 * */
	public t_user_vip_records queryUserVIPRecordsByUserIdAndDate (Long user_id,Date date) {
		return userVIPRecordsDao.queryUserVIPRecordsByUserIdAndDate(user_id, date);
	}
	
	
}
