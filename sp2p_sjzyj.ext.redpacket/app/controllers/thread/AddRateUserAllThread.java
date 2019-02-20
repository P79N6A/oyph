package controllers.thread;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

import common.utils.DateUtil;
import common.utils.Factory;
import daos.ext.redpacket.RedpacketAccountDao;
import daos.ext.redpacket.RedpacketUserDao;
import models.ext.redpacket.entity.t_add_rate_ticket;
import models.ext.redpacket.entity.t_add_rate_user;
import models.ext.redpacket.entity.t_red_packet_account;
import models.ext.redpacket.entity.t_red_packet_user;
import play.db.jpa.JPA;
import play.db.jpa.JPABase;


/**
 * 多线程给所有用户发放加息券
 * 
 * 
 * @author LiuPengwei
 * @createDate  2018年5月25日15:59:50
 *
 */
public class AddRateUserAllThread extends Thread{

	
	private double apr;
	private int day;
	private long userId;
	private double useRule;
	private int bid_period;
	
	
	public AddRateUserAllThread() {
		super();
	}

	public AddRateUserAllThread(double apr, int day, long userId, double useRule, int bid_period) {
		super();
		this.apr = apr;
		this.day = day;
		this.userId = userId;
		this.useRule = useRule;
		this.bid_period = bid_period;
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
        
		
    	t_add_rate_ticket ticket = new t_add_rate_ticket();
		
		ticket.apr = apr;
		ticket.day = day;
		ticket.large = 0;
		ticket.small = 0;
		ticket.amount = useRule;
		ticket.act_id = 0;
		ticket.type = 2;
		
		ticket.save();
    	
		Date nowDate = new Date();
		
		Date etime = DateUtil.add(nowDate, Calendar.DAY_OF_YEAR, ticket.day);
		
		t_add_rate_user rateUser = new t_add_rate_user();
		
		rateUser.stime = nowDate;
		rateUser.etime = etime;
		rateUser.ticket_id = ticket.id;
		rateUser.user_id = userId;
		rateUser.status = 1;
		rateUser.channel = "批量发放";
		rateUser.bid_period = bid_period;
		
		rateUser.save();
		
	
        JPA.em().getTransaction().commit();
	}
}