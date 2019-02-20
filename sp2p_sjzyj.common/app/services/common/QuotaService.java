package services.common;


import java.util.List;

import common.utils.Factory;
import daos.common.QuotaDao;
import models.common.entity.t_quota;
import services.base.BaseService;

/**
 * 投资限额Service
 *
 * @description 
 *
 * @author Liuyang
 * @createDate 2018年01月23日
 */
public class QuotaService extends BaseService<t_quota> {

	protected static QuotaDao quotaDao = Factory.getDao(QuotaDao.class);
	
	protected QuotaService() {
		
		super.basedao = this.quotaDao;
	}
	
	public List<t_quota> queryQuotaesByType(int showType) {
		
		return quotaDao.queryQuotaesByType(showType);
	}
}
