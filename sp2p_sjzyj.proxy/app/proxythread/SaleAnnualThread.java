package proxythread;

import javax.persistence.EntityManager;

import common.proxy.utils.CalculationUtils;
import common.utils.Factory;
import daos.proxy.SalesManDao;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import models.proxy.entity.t_proxy_salesman;
import play.db.jpa.JPA;

/**
 * 
* @ClassName: SaleAnnualThread 
* @Description: 多线程处理业务员当月年化 
* @author lihuijun 
* @date 2018年4月26日 下午4:12:01 
*
 */
public class SaleAnnualThread extends Thread{
	
	protected static final CalculationUtils calculationUtils = Factory.getDao(CalculationUtils.class);
	
	protected static final SalesManDao salesManDao = Factory.getDao(SalesManDao.class);
	
	private long saleId;
	
	public SaleAnnualThread (long saleId){
		
		this.saleId = saleId;
	}
	
	@Override
	public void run() {
		
		if(JPA.local.get()==null){
			
			EntityManager em = JPA.newEntityManager();
			
			final JPA jpa =new JPA();
			
			jpa.entityManager=em;
			
			JPA.local.set(jpa);
			
		}
		
		JPA.em().getTransaction().begin();
		
			t_proxy_salesman sale = salesManDao.findByID(saleId);
			
			if(sale!=null){
				
				sale.cur_invest_amount = calculationUtils.monthTotalMoney(sale.id,0);
				
				sale.cur_user = calculationUtils.salesManCurUserCount(sale.id , 0);
				
				sale.cur_invest_user = calculationUtils.salesManMonthAmountCount(sale.id , 0);
				
				sale.total_user = (int)t_user.count("proxy_salesMan_id=?", sale.id);
				
				sale.total_invest_user = (int)t_user_info.count(" invest_member_time is not null and user_id in( select id from t_user where proxy_salesMan_id="+sale.id+")");
				
				sale.total_amount = calculationUtils.totalMoney(sale.id);
				
				sale.cur_year_convert = calculationUtils.annualConversion(sale.id , 0).convert;
				
				sale.save();
			}
		
		JPA.em().getTransaction().commit();
		
		
		
	}

}
