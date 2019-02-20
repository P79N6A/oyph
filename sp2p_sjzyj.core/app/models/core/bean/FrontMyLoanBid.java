package models.core.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_bid.Status;
import models.core.entity.t_product.PeriodUnit;
import models.core.entity.t_product.RepaymentType;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

@Entity
public class FrontMyLoanBid implements Serializable{
	
	@Id
	public long id;
	
	/** 借款标题 */
	public String title;
	
	/** 借款金额 */
	public double amount;
	
	/** 年利率 */
	public double apr;
	
	/** 借款期限单位
	 *1-天
	 *2-月*/
	public int period_unit;
	
	/** 借款期限 */
	public int period;
	
	/** 标的状态 */
	public int status;
	
	/** 放款时间 */
	public Date release_time;
	
	/** 还款方式 */
	public int repayment_type;
	
	/** 标的已还账单数 */
	public int has_repayment_bill;
	
	/** 标的总的账单数 */
	public int total_repayment_bill;
	
	/** 标的已经上传的审核科目数 */
	public int has_upload_item;
	
	/** 标的总共需要上传的审核科目数 */
	public int total_upload_item;
	
	@Transient
	public String sign;
	
	public String getBid_no(){
		return NoUtil.getBidNo(id);
	}
	
	public PeriodUnit getPeriod_unit(){
		return PeriodUnit.getEnum(this.period_unit);
	}
	
	public Status getStatus(){
		return Status.getEnum(this.status);
	}
	
	public RepaymentType getRepayment_type(){
		return RepaymentType.getEnum(this.repayment_type);
	}
	
	public FrontMyLoanBid(){}

	public String getSign() {
		String signID = Security.addSign(id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
	public String contract_id;
}
