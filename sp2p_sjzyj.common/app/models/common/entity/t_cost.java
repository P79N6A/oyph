package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:费用账户明细
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年03月14日
 */
@Entity
public class t_cost extends Model {
	
	/** 标号 */
	public long bid_id;
	
	/** 0充值 1提现 2提现手续费 3服务费 */
	public int sort;
	
	/** 0收入 1支出 */
	public int type;
	
	/** 金额 */
	public double amount;
	
	/** 余额 */
	public double balance;
	
	/** 创建时间 */
	public Date time;
	
	/** 备注 */
	public String remark;
	
}
