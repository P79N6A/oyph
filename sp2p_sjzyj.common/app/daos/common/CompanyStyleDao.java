package daos.common;

import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import models.common.entity.t_company_style;
import daos.base.BaseDao;

public class CompanyStyleDao extends BaseDao<t_company_style> {

	protected CompanyStyleDao() {}
	
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
		StringBuffer sql = new StringBuffer("SELECT * FROM t_company_style ORDER BY create_time");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(id) FROM t_company_style");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), condition);
	}
}
