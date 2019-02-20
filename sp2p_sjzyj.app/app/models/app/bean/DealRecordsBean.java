package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import models.common.entity.t_deal_user.DealType;
/***
 * 交易记录实体
 *
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-4-6
 */
@Entity
public class DealRecordsBean implements Serializable {
	
	@Id
	public long id;
	
	/** 交易类型  */
    public int dealType;
    
    /** 订单订单号 */
    public  String  orderNo;
    
    /** 成交金额  */
    public double amount;
    
    /** 可用余额 */
    public double balance;
    
    /** 冻结金额 */
    public double freeze;
    
    /** 交易时间  */
    public Date time;
    
    /** 交易明细 */
    public String summary;

	public String getDealType() {
		DealType dt = DealType.getEnum(this.dealType);
		
		return dt == null ? null : dt.value;
	}

	
	
	public Long getTime() {
		if(time==null){
			return null;
		}
		
		return time.getTime();
	}
    
    
    
    
}
