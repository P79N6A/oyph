package proxythread;

import javax.persistence.EntityManager;

import common.proxy.utils.CalculationUtils;
import common.utils.Factory;
import daos.proxy.ProfitDao;
import models.proxy.entity.t_proxy_profit;
import models.proxy.entity.t_proxy_salesman;
import play.db.jpa.JPA;

/**
 * 
* @ClassName: SaleProfitHistoryThread 
* @Description: 多线程处理上个月业务员提成
* @author lihuijun 
* @date 2018年4月26日 下午3:02:59 
*
 */
public class SaleProfitHistoryThread extends Thread{
	
	protected static final CalculationUtils calculationUtils = Factory.getDao(CalculationUtils.class);
	
	protected static final ProfitDao profitDao = Factory.getDao(ProfitDao.class);
	
	private t_proxy_salesman sale;
	
	private String date;
	
	public SaleProfitHistoryThread(t_proxy_salesman sale, String date){
		this.sale = sale;
		
		this.date = date;
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
        
        t_proxy_profit profitHistory = profitDao.findByColumn("type =? and profit_time=? and sale_id =?", 1,date,sale.id);
        
        	if(profitHistory!=null){
        		
		        profitHistory.profit = calculationUtils.salesmanMonthCommission(sale.id , 1);
				
		        profitHistory.save();
        	}
        	
        JPA.em().getTransaction().commit();
	}
}
