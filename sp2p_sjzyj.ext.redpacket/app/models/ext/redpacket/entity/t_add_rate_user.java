package models.ext.redpacket.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体：加息券记录
 * 
 * @author niu
 * @create 2017.11.02
 */
@Entity
public class t_add_rate_user extends Model {

	/** 开始时间  */
	public Date stime;
	
	/** 结束时间  */
	public Date etime;
	
	/** 用户ID  */
	public long user_id;
	
	/** 加息券ID  */
	public long ticket_id;
	
	/** 加息券状态  */
	public int status;
	
	/** 投资ID  */
	public long invest_id;
	
	/** 发放渠道 */
	public String channel;
	
	/** 创造投资 */
	public double create_invest;
	
	/** 标的期限 */
	public int bid_period;
	
}
