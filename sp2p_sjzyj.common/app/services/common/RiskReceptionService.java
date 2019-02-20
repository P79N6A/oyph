package services.common;

import common.utils.Factory;


import daos.common.RiskReceptionDao;
import models.common.entity.t_risk_reception;
import services.base.BaseService;

public class RiskReceptionService extends BaseService<t_risk_reception>{
	
	protected static RiskReceptionDao riskReceptionDao = Factory.getDao(RiskReceptionDao.class);
	
	protected RiskReceptionService(){
		super.basedao = riskReceptionDao;
	}
}
