package daos.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_user;
import models.proxy.bean.SalesManUserBean;
import models.proxy.entity.t_proxy_user;

/**
 * 用户表（业务员推广的用户）
 * @author Niu Dongfeng
 */
public class SalesManUserDao extends BaseDao<t_proxy_user> {

	protected SalesManUserDao() {}
	
	/**
	 * 分页查询业务员下会员
	 * 
	 * @param salesManId
	 * @param currPage
	 * @param pageSize
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-26
	 */
	public PageBean<SalesManUserBean> pageOfSalesManUsers(long salesManId, int currPage, int pageSize, String userName, String userMobile, int orderType, int orderValue) {
		
		StringBuffer querySql = new StringBuffer("SELECT u.id AS id, u.name AS userName, u.mobile AS userMobile, ui.reality_name AS realName, uf.balance AS balance, DATE_FORMAT(u.time,'%Y-%m-%d') as extTime, pu.cur_invest_amount AS monthAmount, pu.total_invest_amount AS totalAmount FROM t_proxy_user pu INNER JOIN t_user u ON pu.user_id = u.id INNER JOIN t_user_info ui ON u.id = ui.user_id INNER JOIN t_user_fund uf ON ui.user_id = uf.user_id WHERE u.proxy_salesMan_id = :salesManId");
		StringBuffer countSql = new StringBuffer("SELECT COUNT(pu.id) FROM t_proxy_user pu INNER JOIN t_user u ON pu.user_id = u.id INNER JOIN t_user_info ui ON u.id = ui.user_id INNER JOIN t_user_fund uf ON ui.user_id = uf.user_id WHERE u.proxy_salesMan_id = :salesManId");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("salesManId", salesManId);
		
		if (userName != null && !"".equals(userName)) {
			querySql.append(" AND ui.reality_name LIKE :userName ");
			countSql.append(" AND ui.reality_name LIKE :userName ");
			
			condition.put("userName", "%" + userName + "%");
		}
		
		if (userMobile != null && !"".equals(userMobile)) {
			querySql.append(" AND u.mobile LIKE :userMobile ");
			countSql.append(" AND u.mobile LIKE :userMobile ");
			
			condition.put("userMobile", "%" + userMobile + "%");
		}
		
		/* 排序功能 */
		switch (orderType) {
		case 1:
			querySql.append(" ORDER BY uf.balance ");
			if(orderValue == 0){
				querySql.append(" DESC ");
			}
			break;
		case 2:
			querySql.append(" ORDER BY pu.cur_invest_amount ");
			if(orderValue == 0){
				querySql.append(" DESC ");
			}
			break;
		case 3:
			querySql.append(" ORDER BY pu.total_invest_amount ");
			if(orderValue == 0){
				querySql.append(" DESC ");
			}
			break;
		default:
			querySql.append(" ORDER BY u.time DESC ");
			break;
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSql.toString(), querySql.toString(), SalesManUserBean.class, condition);
		
	}
}
