package models.common.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 信息披露年龄统计表
 * 
 * @description
 *
 * @author liuyang
 * @createDate 2017年12月15日
 */

@Entity
public class t_disclosure_age extends Model {

	/** 信息披露外键 */
	public long disclosure_id;
	
	/** 90后借款人 */
	public int nine_borrower;
	
	/** 80后借款人 */
	public int eight_borrower;
	
	/** 70后借款人 */
	public int seven_borrower;
	
	/** 60后借款人 */
	public int six_borrower;
	
	/** 90后出借人 */
	public int nice_lender;
	
	/** 80后出借人 */
	public int eight_lender;
	
	/** 70后出借人 */
	public int seven_lender;
	
	/** 60后出借人 */
	public int six_lender;
	
	/** 添加时间 */
	public int time;
}
