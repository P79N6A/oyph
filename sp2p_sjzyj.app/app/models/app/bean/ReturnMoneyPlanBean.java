package models.app.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_bill_invest;
import models.core.entity.t_bill_invest.Status;

import common.FeeCalculateUtil;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.NoUtil;
import common.utils.Security;
import common.utils.number.Arith;
import daos.core.BillInvestDao;

/****
 * 
 * 回款计划
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-4-1
 */
@Entity
public class ReturnMoneyPlanBean implements Serializable {
   
	/** billId */
	@Id
	public long billInvestNo;
	
	/** 用户Id */
	public long userId;
	
	/** 到期时间 */
	public Date receiveTime;  

	
	/** 期数 */
	public int period;
	
	/** 总期数 */
	public int totalPeriod;
	
	
	/** 本金 */
	public double receiveCorpus; 
	
	/** 利息 */
	public double receiveInterest;
	
	/** 时间 */
	public Date time;
	
	/** 服务费规则*/
	public String serviceFeeRule;
	
	@Transient 
	public double coupon_interest;
	
	public long billInvestId;
	
	/** 状态 */
	public int status;
	/** 状态描述 */
	@Transient
	public String statusStr;
	public String getStatusStr() {
		Status stat = Status.getEnum(this.status);
		
		return stat.value;
	}
	
	public long bid_no;
	public String getBid_no(){
		BillInvestDao billDao = Factory.getDao(BillInvestDao.class);
		t_bill_invest bill = billDao.findByID(bid_no);
		return NoUtil.getBidNo(bill.bid_id);
	}
	/** 账单编号 */
	public String getBillInvestNo () {
		return NoUtil.getBillInvestNo(this.billInvestNo, this.time);
	}
	
	
	@Transient
	public double loanFee;
	/** 服务费 */
	public double getLoanFee() {
		
		return FeeCalculateUtil.getOriginalInvestManagerFee(this.receiveInterest, this.serviceFeeRule);
	}

	/** 预计到账 */
	@Transient
	public double principalInterest;
	public double getPrincipalInterest() {
		
		return Arith.sub(Arith.add(this.receiveInterest, this.receiveCorpus) , this.getLoanFee());
	}

	
	public Long getReceiveTime() {
		if (receiveTime == null) {
			return null;
		}
		return receiveTime.getTime();
	}

}
