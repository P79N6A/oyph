package models.ext.redpacket.entity;

import java.util.Date;

import javax.persistence.Entity;
import play.db.jpa.Model;

@Entity
public class t_red_packet_virtual_goods extends Model {
	
	/**创建时间*/
	public Date time;
	
	/**红包唯一标识符*/
	public String _key;
	
	/**红包名称*/
	public String red_packet_name;
	
	/**红包金额*/
	public double amount;
	
	/**红包使用规则(投资大于等于这个金额)*/
	public double use_rule;
	
	/**红包的有效期 单位天*/
	public int limit_day;
	
	/**标的期限*/
	public int bid_period;
	
}
