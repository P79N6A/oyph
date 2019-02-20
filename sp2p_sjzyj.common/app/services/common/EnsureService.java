package services.common;

import common.utils.Factory;

import daos.common.EnsureDao;
import models.common.entity.t_ensure;
import services.base.BaseService;

public class EnsureService extends BaseService<t_ensure> {

	protected EnsureDao ensureDao = Factory.getDao(EnsureDao.class);
	
	protected EnsureService() {
		this.basedao = ensureDao;
	}
}
