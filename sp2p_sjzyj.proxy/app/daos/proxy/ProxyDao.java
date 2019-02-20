package daos.proxy;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.proxy.entity.t_proxy;
import models.proxy.entity.t_proxy_salesman;

/**
 * 代理商
 * @author Niu Dongfeng
 */
public class ProxyDao extends BaseDao<t_proxy> {

	protected ProxyDao() {} 
	
	/**
	 * 代理商查询
	 * @author Niu Dongfeng
	 */
	public PageBean<t_proxy_salesman> pageOfProxy(int currPage, int pageSize, int proxyStatus, String proxyName, String proxyMobile) {
		
		StringBuffer querySql = new StringBuffer("SELECT * FROM t_proxy_salesman t WHERE t.type = 2 ");
		StringBuffer countSql = new StringBuffer("SELECT COUNT(t.id) FROM t_proxy_salesman t WHERE t.type = 2 ");
		
		//查询条件
		Map<String, Object> condition = new HashMap<String, Object>();
		
		if (proxyStatus == 1 || proxyStatus == 2) {
			querySql.append(" AND t.sale_status = :proxyStatus ");
			countSql.append(" AND t.sale_status = :proxyStatus ");
			
			condition.put("proxyStatus", proxyStatus);
		}
		
		if (proxyName != null && !"".equals(proxyName)) {
			querySql.append(" AND t.sale_name LIKE :proxyName ");
			countSql.append(" AND t.sale_name LIKE :proxyName ");
			
			condition.put("proxyName", "%" + proxyName + "%");
		}
		
		if (proxyMobile != null && !"".equals(proxyMobile)) {
			querySql.append(" AND t.sale_mobile = :proxyMobile ");
			countSql.append(" AND t.sale_mobile = :proxyMobile ");
			
			condition.put("proxyMobile", proxyMobile);
		}
			
		return this.pageOfBeanBySQL(currPage, pageSize, countSql.toString(), querySql.toString(), t_proxy_salesman.class, condition);
	}
	
	/**
	 * 添加代理商
	 * @author Niu Dongfeng
	 */
	public t_proxy addProxy() {
		
		t_proxy proxy = new t_proxy();
		
		proxy.sale_count = 1;
		proxy.time = new Date();
		proxy.profit_rule = "[{minAmount:0,maxAmount:10,amount:13.0},{minAmount:10,maxAmount:20,amount:20.0},{minAmount:20,maxAmount:30,amount:20.0},{minAmount:0,maxAmount:10,amount:13.0},{minAmount:10,maxAmount:20,amount:20.0},{minAmount:20,maxAmount:30,amount:20.0}]";

		return proxy.save();
	}

	/**
	 * 代理商规则修改
	 * 
	 * @author GuoShijie
	 * @createDate 2018.01.29
	 * */
	public t_proxy updateProxyProfitRule(long proxyId, String profitRule) {
		t_proxy proxy = findByID(proxyId);
		proxy.profit_rule = profitRule;
		return proxy.save();
	}

}
