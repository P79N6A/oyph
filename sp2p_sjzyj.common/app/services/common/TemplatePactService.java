package services.common;

import common.utils.Factory;
import daos.common.TemplatePactDao;
import models.common.entity.t_template_pact;
import services.base.BaseService;

public class TemplatePactService extends BaseService<t_template_pact> {

	protected TemplatePactDao templatePactDao = null;
	
	protected TemplatePactService() {
		this.templatePactDao = Factory.getDao(TemplatePactDao.class);
		
		super.basedao = this.templatePactDao;
	}

	public t_template_pact findTempByType(int type) {
		return templatePactDao.findTempByType(type);
	}
}
