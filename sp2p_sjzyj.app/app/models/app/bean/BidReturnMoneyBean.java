package models.app.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_bill.Status;


import common.FeeCalculateUtil;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;
import common.utils.number.Arith;

/****
 * 
 * 回款计划
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-4-7
 */
@Entity
public class BidReturnMoneyBean implements Serializable {
   
	/** billId */
	@Id
	public long id;
	

	/** 回款时间 */
	public Date repaymentTime;  
	
	
	/** 本息 */
	public double principalInterest; 
	
   /** 状态  */
    public  int status;
    
    /**期数 */
    public int period;
    
    /** 总期数 */
    public int totalPeriod;
    
	public String getStatus() {
		Status status = Status.getEnum(this.status);
		
		return status == null ? null : status.value;
	}
	
	public Long getRepaymentTime() {
		if (repaymentTime == null) {
			return null;
		}
		return repaymentTime.getTime();
	}

}
