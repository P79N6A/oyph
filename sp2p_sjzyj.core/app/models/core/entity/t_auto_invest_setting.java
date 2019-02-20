package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/** 
 * 自动投标设置
 * 
 * @description 
 * 
 * @author ZhouYuanZeng 
 * @createDate 2016年3月29日 上午9:26:50  
 */
@Entity
public class t_auto_invest_setting extends Model {
	
	/** 添加时间 */
	public Date time;
	
	/** 用户Id(投资人) */
	public long user_id;
	
	/** 状态 0 关闭 (协议开启) 1 开启  2 关闭(协议关闭)*/
	public int status;
	
	/** 最低年利率*/
	public double min_apr;
	
	/** 最长投资期限(月) */
	public int max_period;
	
	/** 每笔次投资金额  */
	public double amount;
	
	/** 排队等待时间 */
	public Date wait_time;
	
	/**委托协议号*/
	public String protocol_no;
	
	/** 自动投标有效时长(天)*/
	public int valid_day;

}
