package services.common;


import common.utils.Factory;
import common.utils.PageBean;
import daos.common.CompanyStyleDao;
import models.common.entity.t_company_style;
import services.base.BaseService;

public class CompanyStyleService extends BaseService<t_company_style> {

	protected static CompanyStyleDao companyStyleDao = Factory.getDao(CompanyStyleDao.class);
	
	protected CompanyStyleService() {
		super.basedao = this.companyStyleDao;
	}
	
	/**
	 *  分页查询，分公司列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2017年4月21日
	 */
	public PageBean<t_company_style> pageOfCompanyStyleBack(int currPage, int pageSize) {
		
		return companyStyleDao.pageOfCompanyStyleBack(currPage, pageSize);
	}
}
