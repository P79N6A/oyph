package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_bill_invest;
import models.core.entity.t_bill_invest.Status;
import common.FeeCalculateUtil;
import common.utils.Factory;
import common.utils.NoUtil;
import common.utils.number.Arith;
import daos.core.BillInvestDao;

/****
 * 理财账单详情
 *
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-4-1
 */
@Entity
public class BillInvestInfo implements Serializable{
    @Id
	public long billInvestNo;
	
	/** 时间 */
	public Date time;
	
	/** 投资人id */
	public long userId;
	
	/** 期数 */
	public String period;
	
	/** 本金 */
	public double receiveCorpus;

	/** 利息 */
	public double receiveInterest;
	
	@Transient 
	public double coupon_interest;
	
	public long bid_no;
	public String getBid_no(){
		BillInvestDao billDao = Factory.getDao(BillInvestDao.class);
		t_bill_invest bill = billDao.findByID(bid_no);
		return NoUtil.getBidNo(bill.bid_id);
	}
		
	/** 到期时间 */
	public Date receiveTime;
	public Long getReceiveTime() {
		if (receiveTime==null) {
			return null;
		}
		return this.receiveTime.getTime();
	}
	/** 回款时间 */
	public Date realReceiveTime;
	public Long getRealReceiveTime() {
		if (realReceiveTime==null) {
			return null;
		}
		return this.realReceiveTime.getTime();
	}
	
	/** 服务费规则 */
	public String serviceFeeRule;
	
	/** 状态 */
	public int status;
	@Transient
	public String statusStr;
	public String getStatusStr() {
		Status status = Status.getEnum(this.status);
	
		return status == null ? null : status.value ;
	}
	
	
	
	/** 账单编号 */
	public String getBillInvestNo() {
		return NoUtil.getBillInvestNo(this.billInvestNo, this.time);
	}
	
	@Transient
	public double loanFee;
	/** 服务费 */
	public double getLoanFee() {
		return FeeCalculateUtil.getInvestManagerFee(this.receiveInterest, serviceFeeRule, this.userId);
	}

	/** 预计到账 */
	@Transient
	public double expectedArrival;
	public double getExpectedArrival() {
		return Arith.sub(Arith.add(this.coupon_interest, this.receiveCorpus) , this.getLoanFee());
	}
	

}
