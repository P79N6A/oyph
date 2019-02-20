package service;

import common.utils.Factory;
import dao.GroupClassifyDao;
import models.entity.t_wechat_group_classify;
import services.base.BaseService;

public class GroupClassifyService extends BaseService<t_wechat_group_classify> {

	private GroupClassifyDao groupClassifyDao;
	
	protected GroupClassifyService () {
		
		groupClassifyDao = Factory.getDao(GroupClassifyDao.class);
		super.basedao = this.groupClassifyDao;
	}
}
