package models.ext.cps.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_red_packet_register extends Model {

	/** 金额 */
	public Double money;
	
	/** 有效期天数 */
	public Integer allotte_day;
	
	/** 最低投资金额 */
	public Double condition_money;
	
	/** 锁定时间天数 */
	public Integer lock_day;
	
	/**新手红包唯一标识符*/
	public String _key;
	
	/** 创建时间 */
	public Date time;
	
}
