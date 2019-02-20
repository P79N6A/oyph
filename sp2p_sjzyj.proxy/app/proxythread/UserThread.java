package proxythread;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.persistence.EntityManager;



import common.utils.Factory;
import daos.common.UserDao;
import daos.proxy.SalesManUserDao;
import models.common.entity.t_user;

import models.proxy.entity.t_proxy_user;
import play.Play;
import play.db.jpa.JPA;


public class UserThread extends Thread {
	
	protected static final UserDao userDao = Factory.getDao(UserDao.class);
	
	protected static final SalesManUserDao salesManUserDao = Factory.getDao(SalesManUserDao.class);
	
	private long userId;
	
	public UserThread(long userId){
		
		this.userId = userId;

	}
	
	@Override
	public void run() {
		

		if(JPA.local.get()==null){
			
			EntityManager em = JPA.newEntityManager();
			
			final JPA jpa = new JPA();
			
			jpa.entityManager = em;
			
			JPA.local.set(jpa);
			
		}
		
		JPA.em().getTransaction().begin();
		
			t_user user = userDao.findByID(userId);
			
			t_proxy_user saleUser = salesManUserDao.findByColumn("user_id = ?", userId);
			
			t_user.getT().setMonths(0);
			
			if(user != null && saleUser != null){
				
				saleUser.cur_invest_amount = user.monthAmount;  // 当月的投资金额
				
				saleUser.total_invest_amount = user.investAmount; //总投资金额
				
				saleUser.save();
				
			}
		
		JPA.em().getTransaction().commit();	
		

	}

}
