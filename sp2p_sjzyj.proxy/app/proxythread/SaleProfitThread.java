package proxythread;

import javax.persistence.EntityManager;

import common.proxy.utils.CalculationUtils;
import common.utils.Factory;
import daos.proxy.SalesManDao;
import models.proxy.entity.t_proxy;
import models.proxy.entity.t_proxy_salesman;
import play.db.jpa.JPA;

public class SaleProfitThread extends Thread{
	
	protected static final CalculationUtils calculationUtils = Factory.getDao(CalculationUtils.class);
	
	protected static final SalesManDao salesManDao = Factory.getDao(SalesManDao.class);
	
	private long saleId;
	
	public SaleProfitThread(long saleId){
		
		this.saleId = saleId;
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
		
			t_proxy_salesman sale = salesManDao.findByID(saleId);
			
			if(sale!=null){
				
				sale.cur_profit = calculationUtils.salesmanMonthCommission(saleId , 0);
				
				sale.total_profit = calculationUtils.saleTotalProfit(saleId , 0) + sale.cur_profit;
				
				sale.save();
			}
		
		JPA.em().getTransaction().commit();
		
	}	
}
