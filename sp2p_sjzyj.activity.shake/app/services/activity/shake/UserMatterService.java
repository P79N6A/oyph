package services.activity.shake;

import common.utils.Factory;
import daos.activity.shake.ShakeActivityDao;
import daos.activity.shake.ShakeRecordDao;
import daos.activity.shake.UserMatterDao;
import models.activity.shake.entity.t_user_matter;
import services.base.BaseService;

public class UserMatterService extends BaseService<t_user_matter>{
	
	protected UserMatterDao userMatterDao = Factory.getDao(UserMatterDao.class);
	
	protected UserMatterService() {
		userMatterDao = Factory.getDao(UserMatterDao.class);
		this.basedao = userMatterDao;
	}
	
	
}
