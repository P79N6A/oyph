package proxythread;

import javax.persistence.EntityManager;

import common.proxy.utils.CalculationUtils;
import common.utils.Factory;
import daos.proxy.ProfitDao;
import models.proxy.entity.t_proxy;
import models.proxy.entity.t_proxy_profit;
import play.db.jpa.JPA;

public class ProxyHistoryThread extends Thread{
	
	protected static final CalculationUtils calculationUtils = Factory.getDao(CalculationUtils.class);
	
	protected static final ProfitDao profitDao = Factory.getDao(ProfitDao.class);
	
	private t_proxy proxy;
	
	private String date;
	
	public ProxyHistoryThread(t_proxy proxy,String date){
		
		this.proxy = proxy;
		
		this.date =date;
	}
	
	@Override
	public void run() {
		
		if (JPA.local.get() == null) {
			
            EntityManager em = JPA.newEntityManager();
            
            final JPA jpa = new JPA();
            
            jpa.entityManager = em;
            
            JPA.local.set(jpa);
        }

        JPA.em().getTransaction().begin();
        
	        t_proxy_profit profitHistory = profitDao.findByColumn("type =? and profit_time=? and sale_id =?", 2,date,proxy.id);
	        
	        t_proxy_profit profit;
	        
	        if(profitHistory==null){
	        	
	        	profit = new t_proxy_profit();
	        	
	        }else{
	        	
	        	profit = profitHistory;
	        	
	        }
		
			profit.profit_time = date;
			
			profit.invest_amount = calculationUtils.proxyHistoryTotalMoney(proxy.id,1).invest_amount;
			
			profit.year_convert = calculationUtils.proxyHistoryTotalMoney(proxy.id, 1).year_convert;
			
			profit.profit = calculationUtils.proxyHistoryCommission(proxy.id, 1);
			
			profit.sale_id = proxy.id;
			
			profit.type = 2;
			
			profit.save();
		
		JPA.em().getTransaction().commit();
	}	
	
	
	
}
