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
* @ClassName: SaleAnnualHistoryThread 
* @Description: TODO(多线程处理上个月业务员年化) 
* @author lihuijun 
* @date 2018年4月26日 下午3:01:20 
*
 */
public class SaleAnnualHistoryThread extends Thread{
	
	protected static final CalculationUtils calculationUtils = Factory.getDao(CalculationUtils.class);
	
	protected static final ProfitDao profitDao = Factory.getDao(ProfitDao.class);
	
	private t_proxy_salesman sales;
	
	private String date;
	
	public SaleAnnualHistoryThread(t_proxy_salesman sales, String date){
		this.sales = sales;
		this.date = date;
	};
	
	@Override
	public void run() {
		
		if (JPA.local.get() == null) {
			
            EntityManager em = JPA.newEntityManager();
            
            final JPA jpa = new JPA();
            
            jpa.entityManager = em;
            
            JPA.local.set(jpa);
        }

        JPA.em().getTransaction().begin();
        
        	t_proxy_profit profitHistory = profitDao.findByColumn("type =? and profit_time=? and sale_id =?", 1,date,sales.id);
        	
        	t_proxy_profit profit;
        	
        	if(profitHistory==null){
        		
				profit = new t_proxy_profit();
				
				
        	}else{
        		
        		profit = profitHistory;
        	}
        	
        	profit.profit_time = date;
			
			profit.invest_amount = calculationUtils.monthTotalMoney(sales.id,1);
			
			profit.year_convert = calculationUtils.annualConversion(sales.id, 1).convert;
			
			profit.sale_id = sales.id;
			
			profit.type = 1;
			
			profit.save();
			
		JPA.em().getTransaction().commit();
		
	}
}
