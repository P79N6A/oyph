package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_bill.Status;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

@Entity
public class BillListBean implements Serializable{
	
	@Id
	public long billId;
	
	/** 生成时间 */
	public Date time;
	
	/** 标的id */
	public long bidId;
	
	public String getBidId() {
		return Security.addSign(this.bidId, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
	
	
	/** 账单编号 */
	@Transient
	public int billNo;
	public String getBillNo() {
		return NoUtil.getBillNo(this.billId, this.time);
	}
	
	/** 本金 */
	public double repaymentCorpus;
	
	/** 利息 */
	public double repaymentInterest;
	
	/** 到期时间 */
	public Date repaymentTime;
	public Long getRepaymentTime() {
		return this.repaymentTime == null ? null : repaymentTime.getTime();
	}
	
	/**	id加密串	*/	
	@Transient
	public String  billIdSign;
	public String getBillIdSign() {
		return Security.addSign(this.billId, Constants.BILL_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
	
}
