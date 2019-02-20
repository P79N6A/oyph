package proxy.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shove.Convert;

import common.constants.SettingKey;
import common.proxy.utils.CalculationUtils;
import common.utils.Factory;
import daos.proxy.ProfitDao;
import daos.proxy.ProxyDao;
import daos.proxy.SalesManDao;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import models.proxy.entity.t_proxy;
import models.proxy.entity.t_proxy_profit;
import models.proxy.entity.t_proxy_salesman;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import services.common.SettingService;
import services.proxy.ProfitService;

/**
 * 添加收益记录
 * @author GuoShijie
 * @createDate 2018.01.25
 * */
public class HistoryProfit {

	protected static final CalculationUtils calculationUtils = Factory.getDao(CalculationUtils.class);
	
	protected static final ProxyDao proxyDao = Factory.getDao(ProxyDao.class);
	
	protected static final SalesManDao salesManDao =  Factory.getDao(SalesManDao.class);
	
	protected static final ProfitDao profitDao = Factory.getDao(ProfitDao.class);
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	public void doJob() {
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------计算代理商上个月提成,开始---------------------");
		}
		
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		
		int year = 0;
		int month = 0;
		if((ca.get(Calendar.MONTH)+1) == 1) {
			month = 12;
			year = ca.get(Calendar.YEAR) - 1;
		}else{
			month = ca.get(Calendar.MONTH);
			year = ca.get(Calendar.YEAR);
		}
		String date = month > 9 ? year+"-"+month : year+"-0"+month;
		
		//--业务员收益
		
		List<t_proxy_salesman> salesman = salesManDao.findAll();
		
		if (salesman != null && salesman.size() > 0) {
			for (t_proxy_salesman sales : salesman) {
				t_proxy_profit profitHistory = getProxyProfit(1, date, sales.id);
				if (profitHistory == null) {
					t_proxy_profit profit = new t_proxy_profit();
					profit.profit_time = date;
					profit.invest_amount = calculationUtils.monthTotalMoney(sales.id,1);
					profit.year_convert = calculationUtils.annualConversion(sales.id, 1).convert;
					profit.profit = calculationUtils.salesmanMonthCommission(sales.id, 1);
					profit.sale_id = sales.id;
					profit.type = 1;
					profit.save();
				}
			}
		}
		
		/**--代理商收益*/
		
		List<t_proxy> proxyman = proxyDao.findAll();
		
		if (proxyman != null && proxyman.size() > 0) {
			for (t_proxy proxy : proxyman) {
				t_proxy_profit profitHistory = getProxyProfit(2, date, proxy.id);
				if (profitHistory == null) {
					t_proxy_profit profit = new t_proxy_profit();
					profit.profit_time = date;
					profit.invest_amount = calculationUtils.proxyMonthTotalMoney(proxy.id,1);
					profit.year_convert = calculationUtils.proxyAnnualConversion(proxy.id, 1);
					profit.profit = calculationUtils.proxyMonthCommission(proxy.id, 1);
					profit.sale_id = proxy.id;
					profit.type = 2;
					profit.save();
				}
			}
		}
		//CalculationUtils.deleteMoreProxyProfit();
		//--end
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------计算代理商上个月提成,结束---------------------");
		}
		
	}
	
	public t_proxy_profit getProxyProfit(int type,String date ,long id) {
		String sql = "select * from t_proxy_profit where type = :t AND profit_time = :htime AND sale_id = :sid ";
		Map<String , Object> condition = new HashMap<String,Object>();
		condition.put("t", type);
		condition.put("htime", date);
		condition.put("sid", id);
		return profitDao.findBeanBySQL(sql, t_proxy_profit.class, condition);
	}
}
