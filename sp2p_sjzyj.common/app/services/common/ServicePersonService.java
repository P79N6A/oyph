package services.common;

import common.utils.Factory;
import daos.common.ServicePersonDao;
import models.common.entity.t_service_person;
import services.base.BaseService;

public class ServicePersonService extends BaseService<t_service_person> {

	protected ServicePersonDao servicePersonDao = Factory.getDao(ServicePersonDao.class);
	
	protected ServicePersonService() {
		super.basedao = servicePersonDao;
	}
}
