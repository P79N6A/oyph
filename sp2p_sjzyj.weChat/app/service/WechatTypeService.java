package service;

import common.utils.Factory;
import dao.WechatTypeDao;
import models.entity.t_wechat_type;
import services.base.BaseService;

public class WechatTypeService extends BaseService<t_wechat_type> {
	
	private WechatTypeDao wechatTypeDao;
	
	protected WechatTypeService() {
		wechatTypeDao = Factory.getDao(WechatTypeDao.class);
		super.basedao = wechatTypeDao;
	}

}
