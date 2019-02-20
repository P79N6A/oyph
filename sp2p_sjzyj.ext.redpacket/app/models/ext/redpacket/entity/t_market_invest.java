package models.ext.redpacket.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 理财产品营销规则表
 * 
 * @author LiuPenmgwei
 * @createDate 2018年5月16日16:35:13
 *
 */

@Entity
public class t_market_invest extends Model{

	/** 创建时间 */
	public Date time ;
	
	/** 借款产品类型id */
	public long invest_product;
	
	/** 理财金额 */
	public int invest_total;
	
	/**金额 或利息*/
	public double amount_apr;
	
	/** 使用规则 */
	public int use_rule;
	
	/** 有限时间 */
	public int limit_day;
	
	/** 标的期限*/
	public int use_bid_limit;
	
	/** 状态 */
	public boolean is_use;
	
	/** 类型  1.红包；2.加息券 */
	public int type ;
}
