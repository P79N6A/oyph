package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

@Entity
public class BillInfo implements Serializable{
	
	@Id
	public long billId;
	
	/** 生成时间 */
	public Date time;
	
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
	
	/** 期数 */
	public int period;
	
	/** 到期时间 */
	public Date repaymentTime;
	public Long getRepaymentTime() {
		return this.repaymentTime == null ? null : repaymentTime.getTime();
	}
	
	/** 实际还款时间 */
	public Date realRepaymentTime;
	public Long getRealRepaymentTime() {
		return this.realRepaymentTime == null ? null : realRepaymentTime.getTime();
	}
	
	/** 状态 */
	public int status;
	
	/**	id加密串	*/	
	@Transient
	public String  billIdSign;
	
	public String getBillIdSign() {
		return Security.addSign(this.billId, Constants.BILL_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
}
