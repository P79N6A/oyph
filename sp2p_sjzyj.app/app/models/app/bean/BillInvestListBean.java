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

@Entity
public class BillInvestListBean implements Serializable{
    @Id
	public long billInvestId;
    
    public long bidId;

    public long billInvestNo;
	
    public Date time;
    
	/** 本金 */
	public double receiveCorpus;

	/** 利息 */
	public double receiveInterest;
	
	@Transient 
	public double coupon_interest;
	
	@Transient
	public long bid_no;
	public String getBid_no(){
		BillInvestDao billDao = Factory.getDao(BillInvestDao.class);
		t_bill_invest bill = billDao.findByID(billInvestId);
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

	/** 账单编号 */
	public String getBillInvestNo() {
		return NoUtil.getBillInvestNo(this.billInvestNo, this.time);
	}
	
}
