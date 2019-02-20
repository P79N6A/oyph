package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.app.bean.ProxyAppBean;
import models.app.bean.ProxyProfitBean;
import models.app.bean.SaleManAppBean;
import models.app.bean.SaleManInfo;
import models.app.bean.SaleManListBean;
import models.proxy.entity.t_proxy;
import models.proxy.entity.t_proxy_salesman;

public class ProxyAppDao extends BaseDao<t_proxy> {

	
	/**
	 * 通过业务员Id 查询业务员
	 * @param saleManId
	 * @return
	 */
	public SaleManAppBean getSaleManById(long saleManId) {
		
		String sql = "SELECT ps.id AS id, ps.type AS type, ps.sale_name AS realName, ps.sale_mobile AS mobile, '' AS extLink, ps.proxy_id AS proxyId FROM t_proxy_salesman ps WHERE ps.id = :saleManId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("saleManId", saleManId);
		
		return findBeanBySQL(sql, SaleManAppBean.class, condition);
	}
	
	/**
	 * 通过代理商Id 查询代理商
	 * @param saleManId
	 * @return
	 */
	public ProxyAppBean getProxyById(long proxyId, int type) {
		
		String sql = "";
		
		if (type == 1) {
			sql = "SELECT ps.id AS id, ps.total_user AS saleManCount, ps.total_invest_user AS investUserCount, ps.cur_invest_amount AS curTotalInvest, ps.cur_year_convert AS curYearConvert, ps.cur_profit AS curProfit FROM t_proxy_salesman ps WHERE ps.id = :proxyId";
		} else {
			sql = "SELECT p.id AS id, p.sale_count AS saleManCount, p.invest_user_count AS investUserCount, p.cur_total_invest AS curTotalInvest, p.cur_year_convert AS curYearConvert, p.cur_profit AS curProfit FROM t_proxy p WHERE p.id = :proxyId";

		}

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("proxyId", proxyId);
		
		return findBeanBySQL(sql, ProxyAppBean.class, condition);
	}
	
	/**
	 * 通过代理商（业务员）Id，查询代理商（业务员）最近三个月收益
	 * 
	 * @param id
	 * @return
	 */
	public List<ProxyProfitBean> listOfThreeMonthProfit(long id, int type) {
		
		String sql = "SELECT t.id AS id, t.profit_time AS profitTime, t.invest_amount AS investAmount, t.year_convert AS yearConvert, t.profit AS profit FROM t_proxy_profit t WHERE t.sale_id = :id AND t.type = :type ORDER BY t.id DESC LIMIT 3";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		condition.put("type", type);
		
		return findListBeanBySQL(sql, ProxyProfitBean.class, condition);
	}
	
	/**
	 * 查询代理商规则
	 * 
	 * @param proxyId
	 * @return
	 */
	public Object getProxyRuleById(long proxyId) {
		
		String sql = "SELECT p.profit_rule FROM t_proxy p WHERE p.id = :proxyId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("proxyId", proxyId);
		
		return findSingleBySQL(sql, condition);
	}
	
	/**
	 * 业务员列表查询
	 * @param proxyId
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	public PageBean<SaleManListBean> pageOfSaleMan(long proxyId, int currPage, int pageSize) {
		
		StringBuffer querySql = new StringBuffer("SELECT t.id AS id, t.sale_name AS realName, t.sale_mobile AS mobile, DATE_FORMAT(t.time,'%Y-%m-%d') AS addTime FROM t_proxy_salesman t WHERE t.proxy_id = :proxyId");
		StringBuffer countSql = new StringBuffer("SELECT COUNT(t.id) FROM t_proxy_salesman t WHERE t.proxy_id = :proxyId");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("proxyId", proxyId);
		
		return pageOfBeanBySQL(currPage, pageSize, countSql.toString(), querySql.toString(), SaleManListBean.class, condition);
	}
	
	/**
	 * 业务员信息查询
	 * @param proxyId
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	public SaleManInfo getSaleManInfo(long saleManId) {
		
		String sql = "SELECT t.id AS id, t.sale_name AS realName, t.sale_mobile AS mobile, t.total_user AS extCount, t.total_invest_user AS investCount FROM t_proxy_salesman t WHERE t.id = :saleManId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("saleManId", saleManId);
		
		return findBeanBySQL(sql, SaleManInfo.class, condition);
	}
	
	
	
	
	
	
}
