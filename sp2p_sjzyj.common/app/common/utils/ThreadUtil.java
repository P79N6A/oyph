package common.utils;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import common.enums.Client;
import models.bean.UserFundCheck;
import models.common.bean.LockBean;
import payment.impl.PaymentProxy;
import play.db.jpa.JPA;
import yb.enums.ServiceType;

public class ThreadUtil extends Thread {
	
	private UserFundCheck ufc;
	
	private LockBean lock;
	
	public ThreadUtil(UserFundCheck ufc, LockBean lock){
		
		this.ufc =ufc;
		this.lock =lock;
		
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
		
		//业务流水号
		String businessSeqNo = "oyph_"+OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);
		
		//捕获异常后将余额设为空
		ResultInfo result = PaymentProxy.getInstance().queryFundInfo(Client.PC.code, ufc.account, businessSeqNo);
		if (result.code < 1) {
			LoggerUtil.error(false, "查询用户资金异常。【userId】", ufc.id);
		}
		Map<String, Object> fundInfo = (Map<String, Object>) result.obj;
		
		if (fundInfo != null){
			ufc.pBlance = Double.valueOf(fundInfo.get("pBlance").toString()); 
		}
		
		lock.fundList.add(ufc);
				
		JPA.em().getTransaction().commit();	
		
		if(lock.fundList.size()==lock.size){
			
			synchronized (lock) {
				lock.notify();
			}
		}
				
	}

}
