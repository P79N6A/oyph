package daos.common;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.finance.entity.t_industry_sort;

public class IndustrySortDao extends BaseDao<t_industry_sort> {

	protected IndustrySortDao() {}
	
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
		String sql = " SELECT s.id,s.code_name,s.code,s.code_desc FROM t_industry_sort s INNER JOIN t_user_profession u ON u.company_trade=s.code_desc INNER JOIN t_bid b ON b.user_id=u.user_id WHERE b.id=:bidId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		
		return this.findBySQL(sql, condition);
	}
}
