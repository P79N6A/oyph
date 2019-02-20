package services.finance;

import common.utils.Factory;
import daos.finance.IndustrySortDao;
import models.finance.entity.t_industry_sort;
import services.base.BaseService;

public class IndustrySortService extends BaseService<t_industry_sort>{

	protected static IndustrySortDao industrySortDao = Factory.getDao(IndustrySortDao.class);
	
	protected IndustrySortService () {
		super.basedao = this.industrySortDao;
	}
	
}
