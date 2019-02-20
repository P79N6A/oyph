package controllers.thread;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

import common.utils.Factory;
import daos.ext.redpacket.RedpacketAccountDao;
import daos.ext.redpacket.RedpacketUserDao;
import models.ext.redpacket.entity.t_red_packet_account;
import models.ext.redpacket.entity.t_red_packet_user;
import play.db.jpa.JPA;


/**
 * 多线程给所有用户发放红包
 * 
 * 
 * @author LiuPengwei
 * @createDate  2018年5月14日17:43:40
 *
 */

public class RedPacketUserAllThread extends Thread{
	

	protected static RedpacketUserDao redpacketUserDao = Factory.getDao(RedpacketUserDao.class);
	
	protected static RedpacketAccountDao redpacketAccountDao = Factory.getDao(RedpacketAccountDao.class);
	
	private int limitDay;
	private long userId;
	private double redpacketAm;
	private double useRule;
	
	

	
	public RedPacketUserAllThread() {
		super();
	}

	public RedPacketUserAllThread(int limitDay, long userId, double redpacketAm, double useRule) {
		this.limitDay = limitDay;
		this.userId = userId;
		this.redpacketAm = redpacketAm;
		this.useRule = useRule;
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
        
        Date now = new Date();
		
		/**使用期限*/
		Calendar calendar = new GregorianCalendar(); 
		calendar.setTime(now); 
		calendar.add(calendar.DATE, limitDay);
		Date limit_time=calendar.getTime();
		
		t_red_packet_user redpacket_user = new t_red_packet_user();
		redpacket_user.time = now;
		redpacket_user._key = "batch_give";
		redpacket_user.red_packet_name = "理财红包";
		redpacket_user.user_id = userId;
		redpacket_user.amount = redpacketAm;
		redpacket_user.use_rule = useRule;
		redpacket_user.limit_day = limitDay;
		redpacket_user.limit_time = limit_time;
		redpacket_user.invest_id = 0;
		redpacket_user.setStatus(t_red_packet_user.RedpacketStatus.RECEIVED);
		redpacket_user.channel = "批量发放" ;
		
		redpacket_user.save();
		
		t_red_packet_account userRedPacket = redpacketAccountDao.findByColumn("user_id = ?", userId);
		
		if (userRedPacket == null){
			t_red_packet_account red_packet_account = new t_red_packet_account();
			red_packet_account.time = now;
			red_packet_account.user_id = userId;
			red_packet_account.balance = 0;
			
			red_packet_account.save();
		}
		
        JPA.em().getTransaction().commit();
	}
	
}
