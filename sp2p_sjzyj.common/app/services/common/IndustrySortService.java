package services.common;

import common.utils.Factory;
import daos.common.IndustrySortDao;
import models.finance.entity.t_industry_sort;
import services.base.BaseService;

public class IndustrySortService extends BaseService<t_industry_sort> {

	protected static IndustrySortDao industrySortDao = Factory.getDao(IndustrySortDao.class);
	
	protected IndustrySortService() {
		this.basedao = industrySortDao;
	}
	
	/**
	 * 
	 * @Title: getByCompanyTrade
	 *
	 * @description 根据bidId查询标人的单位所属行业(CompanyTrade)
	 *
	 * @param @param bidId
	 * @param @return 
	 * 
	 * @return t_user_profession    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年12月10日
	 */
	public t_industry_sort getByCompanyTrade (long bidId) {
		
		return industrySortDao.getByCompanyTrade(bidId);
	}
}
