package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 信息披露投资表
 * 
 * @description
 *
 * @author liuyang
 * @createDate 2017年12月15日
 */

@Entity
public class t_disclosure_invest extends Model {

	/** 信息披露外键 */
	public long disclosure_id;
	
	/** 用户名 */
	public String name;
	
	/** 投资金额 */
	public String invest_amount;
	
	/** 比例 */
	public double proportion;
	
	/** 添加时间 */
	public Date time;
}
