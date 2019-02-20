package daos.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.proxy.bean.SalesManProfitBean;
import models.proxy.entity.t_proxy_profit;

/**
 * 业务员、代理商收益表
 * 
 * @author Niu Dongfeng
 */
public class ProfitDao extends BaseDao<t_proxy_profit> {

	protected ProfitDao() {}
	
	/**
	 * 代理商历史三个月提成
	 * 
	 * @param proxyId	代理商Id
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-26
	 */
	public List<t_proxy_profit> listOfProxyProfit(long salesManId) {
		
		String sql = "SELECT * FROM t_proxy_profit t WHERE t.sale_id = :proxyId AND t.type = 2 ORDER BY t.id DESC LIMIT 3";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("proxyId", salesManId);
		
		return findListBySQL(sql, condition);
	}
	
	/**
	 * 业务员历史三个月提成
	 * 
	 * @param proxyId	代理商Id
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-26
	 */
	public List<t_proxy_profit> listOfSalesManProfit(long salesManId) {
		
		String sql = "SELECT * FROM t_proxy_profit t WHERE t.sale_id = :salesManId AND t.type = 1 ORDER BY t.id DESC LIMIT 3";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("salesManId", salesManId);
		
		return findListBySQL(sql, condition);
	}
	
	/**
	 * 代理商-业务员提成-查询
	 * @author Niu Dongfeng
	 */
	public PageBean<SalesManProfitBean> pageOfSalesManProfit(long proxyId, int currPage, int pageSize, String userName, String userMobile, String profitTime) {
		
		StringBuffer querySql = new StringBuffer("SELECT t.id AS id, t.profit_time AS profitTime, p.sale_name AS realName, t.invest_amount AS investAmount, t.year_convert AS yearConvert, p.total_amount AS totalAmount, t.profit AS monthProfit FROM t_proxy_profit t INNER JOIN t_proxy_salesman p ON t.sale_id = p.id WHERE t.type = 1 AND t.sale_id IN (SELECT ps.id FROM t_proxy_salesman ps WHERE ps.type = 1 AND ps.proxy_id = :proxyId OR (ps.type = 2 AND ps.proxy_id = :proxyId))");
		StringBuffer countSql = new StringBuffer("SELECT COUNT(t.id) FROM t_proxy_profit t INNER JOIN t_proxy_salesman p ON t.sale_id = p.id WHERE t.type = 1 AND t.sale_id IN (SELECT ps.id FROM t_proxy_salesman ps WHERE ps.type = 1 AND ps.proxy_id = :proxyId OR (ps.type = 2 AND ps.proxy_id = :proxyId))");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("proxyId", proxyId);
		
		
		if (userName != null && !"".equals(userName)) {
			querySql.append(" AND p.sale_name LIKE :userName ");
			countSql.append(" AND p.sale_name LIKE :userName ");
			
			condition.put("userName", "%" + userName + "%");
		}
		
		if (userMobile != null && !"".equals(userMobile)) {
			querySql.append(" AND p.sale_mobile = :userMobile ");
			countSql.append(" AND p.sale_mobile = :userMobile ");
			
			condition.put("userMobile", userMobile);
		}
		
		if (profitTime != null && !"".equals(profitTime)) {
			querySql.append(" AND t.profit_time = :profitTime ");
			countSql.append(" AND t.profit_time = :profitTime ");
			
			condition.put("profitTime", profitTime);
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSql.toString(), querySql.toString(), SalesManProfitBean.class, condition);
	}
	
	/**
	 * 修改收益记录
	 * @author GuoShijie
	 * @createDate 2018.04.02
	 * */
	public int updateProxyProfit(t_proxy_profit proxyProfit ) {
		String sql = "update t_proxy_profit set invest_amount = :investAmount, profit= :monthProfit, profit_time= :profitTime, sale_id= :saleId, type= :proxyType, year_convert= :yearConvert where id= :mainId";
		Map<String , Object> condition = new HashMap<String , Object>();
		condition.put("investAmount", proxyProfit.invest_amount);
		condition.put("monthProfit", proxyProfit.profit);
		condition.put("profitTime", proxyProfit.profit_time);
		condition.put("proxyType", proxyProfit.type);
		condition.put("saleId", proxyProfit.sale_id);
		condition.put("yearConvert", proxyProfit.year_convert);
		condition.put("mainId", proxyProfit.id);
		return updateBySQL(sql, condition);
	}
	
	
	
	
}
