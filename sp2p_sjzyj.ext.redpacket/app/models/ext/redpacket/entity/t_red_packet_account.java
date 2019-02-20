package models.ext.redpacket.entity;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

/**
 * 用户的红包账号
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月14日
 */
@Entity
public class t_red_packet_account extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 用户ID */
	public Long user_id;
	
	/** 红包金额 */
	public double balance;
	
	public t_red_packet_account() {}
	
	public t_red_packet_account (Date time,long user_id,double balance) {
		this.time = time;
		this.user_id = user_id;
		this.balance = balance;
	}
	
	public t_red_packet_account (long id,Date time,long user_id,double balance) {
		this.id = id;
		this.time = time;
		this.user_id = user_id;
		this.balance = balance;
	}
	
	
	
}
