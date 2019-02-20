package services.common;

import java.util.List;

import common.utils.Factory;
import daos.common.DisclosureMonthDao;
import models.common.entity.t_disclosure_month;
import services.base.BaseService;

public class DisclosureMonthService extends BaseService<t_disclosure_month> {

	private DisclosureMonthDao disclosureMonthDao = Factory.getDao(DisclosureMonthDao.class);
	
	protected DisclosureMonthService() {
		super.basedao = this.disclosureMonthDao;
	}
	
	/**
	 * 查询十二个月的月信息数据
	 * @param disclosureId
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2017年12月21日
	 */
	public List<t_disclosure_month> queryListById(long disclosureId) {
		
		return disclosureMonthDao.queryListById(disclosureId);
	}
}
