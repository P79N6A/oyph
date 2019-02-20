package proxy.jobs;



import java.util.ArrayList;
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
import models.proxy.entity.t_proxy_salesman;
import models.proxy.entity.t_proxy_user;
import proxythread.ProxyThread;
import proxythread.SaleAnnualThread;
import proxythread.SaleProfitThread;
import proxythread.UserThread;


/**
 * 
* @ClassName: ProxyDeduct 
* @Description: 有关代理商资金信息定时更新类
* @author lihuijun 
* @date 2018年4月20日 下午2:44:15 
*
 */
public class ProxyDeduct {
	
	protected static final SalesManUserDao salesManUserDao = Factory.getDao(SalesManUserDao.class);
	
	protected static final UserDao userDao = Factory.getDao(UserDao.class);
	
	protected static final SalesManDao salesManDao = Factory.getDao(SalesManDao.class);
	
	protected static final CalculationUtils calculationUtils = Factory.getDao(CalculationUtils.class);
	
	protected static final ProxyDao proxyDao = Factory.getDao(ProxyDao.class);
	 
	/**
	 * 
	 * @Title: doJobUpdateUser   
	 * @Description: 业务员名下会员更新资金信息
	 * @param     
	 * @return: void 
	 * @author lihuijun 
	 * @date 2018年4月20日 下午2:49:51    
	 * @throws
	 */
	public void doJobUpdateUser(){
		
		List<t_proxy_user> saleUserList = salesManUserDao.findAll();
			
		if(saleUserList!=null && saleUserList.size()>0){
			
			for (t_proxy_user saleUser:saleUserList) {	
				
				Thread thread = new UserThread(saleUser.user_id);
				
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
	 * @Title: doJobUpdateSaleAnnual   
	 * @Description: 更新业务员信息1:先更新年化
	 * @param     
	 * @return: void 
	 * @author lihuijun 
	 * @date 2018年4月20日 下午5:36:26    
	 * @throws
	 */
	public void doJobUpdateSaleAnnual(){
		
		List<t_proxy_salesman> salesmanList =  salesManDao.findAll();
		
		if(salesmanList!=null && salesmanList.size()>0){
			
			for(t_proxy_salesman sale : salesmanList){

				Thread thread =new SaleAnnualThread(sale.id);
				
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
	 * @Title: doJobUpdateSaleProfit   
	 * @Description: 更新业务员信息1:更新提成
	 * @param     
	 * @return: void 
	 * @author lihuijun 
	 * @date 2018年4月23日 上午9:57:40    
	 * @throws
	 */
	public void doJobUpdateSaleProfit(){
		
		List<t_proxy_salesman> salesmanList =  salesManDao.findAll();
		
		for(t_proxy_salesman sale:salesmanList){
			
			Thread thread=new SaleProfitThread(sale.id);
			
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
	
	/**
	 * 
	 * @Title: doJobUpdateProxyAnnual   
	 * @Description: 更新代理商年化 与 提成
	 * @param     
	 * @return: void 
	 * @author lihuijun 
	 * @date 2018年4月23日 上午11:02:42    
	 * @throws
	 */
	public void doJobUpdateProxy(){
		
		List<t_proxy> proxyList = proxyDao.findAll();
		
		if(proxyList!=null && proxyList.size()>0){
			
			for(t_proxy proxy:proxyList){
				
				Thread thread = new ProxyThread(proxy.id);
				
				thread.start();

			}
		}
	}
	
}
