package services.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.Factory;
import common.utils.PageBean;
import daos.proxy.ProfitDao;
import models.proxy.bean.SalesManProfitBean;
import models.proxy.entity.t_proxy_profit;
import services.base.BaseService;

/**
 * 业务员、代理商收益业务
 * 
 * @author Niu Dongfeng
 */
public class ProfitService extends BaseService<t_proxy_profit> {

	protected ProfitDao profitDao = Factory.getDao(ProfitDao.class);
	
	protected ProfitService() {
		super.basedao = profitDao;
	}
	
	/**
	 * 代理商历史三个月提成
	 * 
	 * @param proxyId	代理商Id
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-26
	 */
	public List<t_proxy_profit> listOfProxyProfit(long proxyId) {
		
		return profitDao.listOfProxyProfit(proxyId);
	}

	/**
	 * 业务员历史三个月提成
	 * 
	 * @param proxyId	业务员Id
	 * @return
	 * 
	 * @author Niu Dongfeng
	 * @create 2018-01-26
	 */
	public List<t_proxy_profit> listOfSalesManProfit(long proxyId) {
		
		return profitDao.listOfSalesManProfit(proxyId);
	}
	
	/**
	 * 代理商-业务员提成-查询
	 * @author Niu Dongfeng
	 */
	public PageBean<SalesManProfitBean> pageOfSalesManProfit(long proxyId, int currPage, int pageSize, String userName, String userMobile, String profitTime) {
		
		return profitDao.pageOfSalesManProfit(proxyId, currPage, pageSize, userName, userMobile, profitTime);
	}
	
	
}
