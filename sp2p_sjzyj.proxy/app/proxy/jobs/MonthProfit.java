package proxy.jobs;

import java.util.List;

import common.proxy.utils.CalculationUtils;
import common.utils.Factory;
import daos.common.UserDao;
import daos.proxy.ProxyDao;
import daos.proxy.SalesManDao;
import daos.proxy.SalesManUserDao;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import models.proxy.entity.t_proxy;
import models.proxy.entity.t_proxy_profit;
import models.proxy.entity.t_proxy_salesman;
import models.proxy.entity.t_proxy_user;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.On;
import services.proxy.ProfitService;

/**
 * 更新当月收益信息
 * @author GuoShijie
 * @createDate 2018.01.25
 * */
public class MonthProfit {
	
	protected static final CalculationUtils calculationUtils = Factory.getDao(CalculationUtils.class);
	
	protected static final SalesManDao salesManDao = Factory.getDao(SalesManDao.class);
	
	protected static final ProfitService profitService = Factory.getService(ProfitService.class);
	
	protected static final ProxyDao proxyDao = Factory.getDao(ProxyDao.class);
	
	protected static final UserDao userDao = Factory.getDao(UserDao.class);
	
	protected static final SalesManUserDao salesManUserDao = Factory.getDao(SalesManUserDao.class); 
	
	public void doJob() {
		/** t_proxy_user 表更新 */
		List<t_user> users = userDao.findListByColumn("proxy_salesMan_id != 0");
		List<t_proxy_user> salesUser = salesManUserDao.findAll();
		if (users != null && users.size() > 0) {
			for(t_user u : users){
				int i = 0;
				t_user.getT().setMonths(0);
				if (salesUser != null && salesUser.size() > 0) {
					for (t_proxy_user su : salesUser) {
						if (u.id == su.user_id) {
							su.cur_invest_amount = u.monthAmount;
							su.total_invest_amount = u.investAmount;
							su.save();
							i++;
						} 
					}
				}
				
				if (i == 0) {
					t_proxy_user proxyUsers = new t_proxy_user();
					proxyUsers.cur_invest_amount = u.monthAmount;
					proxyUsers.total_invest_amount = u.investAmount;
					proxyUsers.user_id = u.id;
					proxyUsers.save();
				}
			}
		}
		
		CalculationUtils.deleteMoreProxyUser();
		
		//业务员更新
		List<t_proxy_salesman> sales = salesManDao.findAll();
		if (sales != null && sales.size() > 0) {
			for (t_proxy_salesman salesman : sales) {
				salesman.cur_invest_amount = calculationUtils.monthTotalMoney(salesman.id,0);
				salesman.cur_user = calculationUtils.salesManCurUserCount(salesman.id , 0);
				salesman.cur_invest_user = calculationUtils.salesManMonthAmountCount(salesman.id , 0);
				salesman.cur_profit = calculationUtils.salesmanMonthCommission(salesman.id , 0);
				salesman.cur_year_convert = calculationUtils.annualConversion(salesman.id , 0).convert;
				salesman.total_user = (int)t_user.count("proxy_salesMan_id=?", salesman.id);
				salesman.total_invest_user = (int)t_user_info.count(" invest_member_time is not null and user_id in( select id from t_user where proxy_salesMan_id="+salesman.id+")");
				salesman.total_amount = calculationUtils.totalMoney(salesman.id);
				salesman.total_profit = calculationUtils.salesManTotalProfit(salesman.id , 0);
				salesman.save();
			}
		}
		//代理商更新
		List<t_proxy> proxyman = proxyDao.findAll();
		if (proxyman != null && proxyman.size() > 0) {
			for (t_proxy proxy : proxyman) {
				proxy.cur_profit = calculationUtils.proxyMonthCommission(proxy.id, 0);
				proxy.cur_total_invest = calculationUtils.proxyMonthTotalMoney(proxy.id , 0);
				proxy.cur_year_convert = calculationUtils.proxyAnnualConversion(proxy.id, 0);
				proxy.sale_count = (int)t_proxy_salesman.count("proxy_id = ?" , proxy.id);
				proxy.invest_user_count = calculationUtils.proxyAmountUserCount(proxy.id);
				proxy.total_user_count = calculationUtils.proxyTotalUser(proxy.id);
				proxy.total_amount = calculationUtils.proxyTotalMoney(proxy.id);
				proxy.total_profit = calculationUtils.proxyTotalProfit(proxy.id , 0);
				proxy.save();
			}
		}
		
		
		
	}
}
