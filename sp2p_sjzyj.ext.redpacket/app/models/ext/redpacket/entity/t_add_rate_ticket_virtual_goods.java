package models.ext.redpacket.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_add_rate_ticket_virtual_goods extends Model {
	
	/**创建时间*/
	public Date time;
	
	/**利率*/
	public double apr;
	
	/**加息券有效期*/
	public int day;
	
	/**加息券使用规则（大于该金额）*/
	public double amount;
}
