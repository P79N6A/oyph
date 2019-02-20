package services.common;

import common.utils.Factory;
import daos.common.DisclosureInvestDao;
import models.common.entity.t_disclosure_invest;
import services.base.BaseService;

/**
 * 信息投资service实现
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2017年12月15日
 */
public class DisclosureInvestService extends BaseService<t_disclosure_invest> {

	private DisclosureInvestDao disclosureInvestDao = Factory.getDao(DisclosureInvestDao.class);
	
	protected DisclosureInvestService() {
		super.basedao = this.disclosureInvestDao;
	}
}
