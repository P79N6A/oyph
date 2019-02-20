package models.proxy.bean;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SalesManProfitBean {

	@Id
	public long id;				/** 主键 */
	
	public String profitTime;		/** 收益月份 */
	
	public String realName;			/** 真实姓名 */
	
	public double investAmount;	/** 月理财金额 */
	
	public double yearConvert;		/** 年化折算 */
	
	public double totalAmount;		/** 总理财金额 */
	
	public double monthProfit;		/** 月理财收益 */
	
}
