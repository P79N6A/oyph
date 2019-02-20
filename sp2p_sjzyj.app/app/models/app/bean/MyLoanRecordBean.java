package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_bid;
import models.core.entity.t_bid.Status;
import models.core.entity.t_product.PeriodUnit;
import models.core.entity.t_product.RepaymentType;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

@Entity
public class MyLoanRecordBean implements Serializable{
	
	@Id
	public long id;
	
	/** 标的id */
	@Transient
	public long bidId;
	public String getBidId() {
		return Security.addSign(this.id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
	
	/** 标题 */
	public String title;
	
	@Transient
	public String bidNo;
	public String getBidNo() {
		return NoUtil.getBidNo(this.id);
	}
	
	/** 金额 */
	public double amount;
	
	/** 年利率 */
	public double apr;
	
	/** 借款期限 */
	public int period;
	
	/** 借款期限单位 */
	public int periodUnit;
	public String getPeriodUnit(){
		PeriodUnit  period = PeriodUnit.getEnum(this.periodUnit);

		return period == null ? null : period.getShowValue() ;
	}
	
	/** 还款方式
	 * 1-先息后本
	 * 2-等本等息
	 * 3-一次性还款 */
	public int repaymentType;
	public String getRepaymentType() {
		RepaymentType repaymentType = RepaymentType.getEnum(this.repaymentType);
		
		return repaymentType == null ? null : repaymentType.value;
	}
	
	/** 放款时间 */
	public Date releaseTime;
	public Long getReleaseTime() {
		if (releaseTime == null) {
			return null;
		}
		return releaseTime.getTime();
	}
	
	/** 标的状态 */
	public int status;
	
	@Transient
	public String statusStr;
	public String getStatusStr(){
		Status status = Status.getEnum(this.status);
		
		return status == null ? null : status.value ;
	}

	/** 标的已还账单数 */
	public int has_repayment_bill;
	
	/** 标的总的账单数 */
	public int total_repayment_bill;
	
}
