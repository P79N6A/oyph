package proxythread;

import javax.persistence.EntityManager;

import common.proxy.utils.CalculationUtils;
import common.utils.Factory;
import daos.proxy.ProxyDao;
import models.proxy.entity.t_proxy;
import models.proxy.entity.t_proxy_salesman;
import play.db.jpa.JPA;

public class ProxyThread extends Thread{
	
	protected static final CalculationUtils calculationUtils = Factory.getDao(CalculationUtils.class);
	
	protected static final ProxyDao proxyDao = Factory.getDao(ProxyDao.class);
	
	private long proxyId;
	
	public ProxyThread(long proxyId){
		
		this.proxyId = proxyId;
	}
	
	@Override
	public void run() {
		
		if(JPA.local.get() == null){
			
			EntityManager em = JPA.newEntityManager();
			
			final JPA jpa = new JPA();
			
			jpa.entityManager = em;
			
			JPA.local.set(jpa);

		}
		
		JPA.em().getTransaction().begin();
			
			t_proxy proxy = proxyDao.findByID(proxyId);
			
			if(proxy!=null){
				
				proxy.cur_total_invest = calculationUtils.proxyCurrTotalMoney(proxyId , 0); 
				
				proxy.sale_count = (int)t_proxy_salesman.count("proxy_id = ?" , proxyId);
				
				proxy.invest_user_count = calculationUtils.proxyAmUserCount(proxyId);
				
				proxy.total_user_count = calculationUtils.proxyTotUser(proxyId);
				
				proxy.total_amount = calculationUtils.proxyTotMoney(proxyId);
				
				proxy.cur_year_convert = calculationUtils.proxyAnnConversion(proxyId, 0);
				
				proxy.cur_profit = calculationUtils.proxyCurrCommission(proxyId, 0);
				
				proxy.total_profit = calculationUtils.proxyTotProfit(proxy.id , 0) + proxy.cur_profit;
				
				proxy.save();
				
			}
			
		JPA.em().getTransaction().commit();
		
	}
	
}
