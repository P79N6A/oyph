package models.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 
 *
 * @ClassName: t_shareholder_information
 *
 * @description 股东信息表
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
@Entity
public class t_shareholder_information extends Model {

	/**  股东类型   */
	public String type;
	
	/**  证件类型   */
	public String certificate_type;
	
	/**  证件号码   */
	public String certificate_num;
	
	/**  股东名称   */
	public String name;
	
	/**  持股比例   */
	public BigDecimal shareholding_ratio;
	
	/**  实际出资数额   */
	public BigDecimal contributed_capital;
	
	/**  入股时间   */
	public Date shares_time;
	
	/**  是否有违法记录   */
	public String illegal_record;
	
	/**  添加时间   */
	public Date time;
	
	
}
