package proxy.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.proxy.utils.CalculationUtils;
import common.utils.Factory;
import daos.proxy.ProfitDao;
import daos.proxy.ProxyDao;
import daos.proxy.SalesManDao;
import models.proxy.entity.t_proxy;
import models.proxy.entity.t_proxy_profit;
import models.proxy.entity.t_proxy_salesman;
import play.Logger;
import proxythread.ProxyHistoryThread;
import proxythread.SaleAnnualHistoryThread;
import proxythread.SaleProfitHistoryThread;

public class HistoryDeduct {

	protected static final CalculationUtils calculationUtils = Factory.getDao(CalculationUtils.class);
	
	protected static final ProxyDao proxyDao = Factory.getDao(ProxyDao.class);
	
	protected static final SalesManDao salesManDao =  Factory.getDao(SalesManDao.class);
	
	protected static final ProfitDao profitDao = Factory.getDao(ProfitDao.class);
	
	
	/**
	 * 
	 * @Title: doJobUpdateSaleHistoryProfit   
	 * @Description: 计算业务员上个月年化
	 * @param     
	 * @return: void 
	 * @author lihuijun 
	 * @date 2018年4月24日 下午2:45:53    
	 * @throws
	 */
	public void doJobUpdateSaleHistoryAnnual(){
			
		List<t_proxy_salesman> salesman = salesManDao.findAll();
		
		if (salesman != null && salesman.size() > 0) {
			
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
			
			for (t_proxy_salesman sales : salesman) {
				
				Thread thread=new SaleAnnualHistoryThread(sales,date);
				
				thread.start();
				
				if(thread.activeCount()>=100){
					
					try {
						
						thread.join();
						
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
				
			}
		}
		
	}
	
	/**
	 * 
	 * @Title: doJobUpdateSaleHistoryProfit   
	 * @Description: 计算业务员上个月提成 
	 * @param     
	 * @return: void 
	 * @author lihuijun 
	 * @date 2018年4月24日 下午2:53:39    
	 * @throws
	 */
	public void doJobUpdateSaleHistoryProfit(){
			

		List<t_proxy_salesman> salesman = salesManDao.findAll();
		
		if (salesman != null && salesman.size() > 0) {
			
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
			
			for (t_proxy_salesman sales : salesman) {
				
				Thread thread = new SaleProfitHistoryThread(sales,date);
				
				thread.start();
				
				if(Thread.activeCount()>=100){
					
					try {
						
						thread.join();
						
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	/**
	 * 
	 * @Title: doJobUpdateProxyHistoty   
	 * @Description: 代理商上个月年化与提成 
	 * @param     
	 * @return: void 
	 * @author lihuijun 
	 * @date 2018年4月24日 下午3:07:26    
	 * @throws
	 */
	public void doJobUpdateProxyHistoty(){
		
		List<t_proxy> proxyman = proxyDao.findAll();
		
		if (proxyman != null && proxyman.size() > 0) {
			
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
			
			for (t_proxy proxy : proxyman) {
				
				Thread thread =	new ProxyHistoryThread(proxy, date);
				
				thread.start();
				
				if(Thread.activeCount()>=100){
					
					try {
						
						thread.join();
						
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
				}
				
			}
		}
		
	}
	
}
