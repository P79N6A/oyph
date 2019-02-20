package models.app.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import models.core.entity.t_bid.Status;
import models.core.entity.t_product.PeriodUnit;
import models.core.entity.t_product.RepaymentType;


import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

/****
 * 
 * 我的投资接口
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-4-1
 */
@Entity
public class MyInvestRecordBean implements Serializable {
    
	@Id
	public long investOriId;
	
    /**investId**/
    @Transient
	public long  investId;
	
	/** bidId **/
	public long id;
	
	/**bidId sign */
	@Transient
	public String bidId;

	@Transient
	public String bidNo;
	public String getBidNo() {
		return NoUtil.getBidNo(this.id);
	}
	
	/** 投标名称 */
	public String title; 
	
	/** 金额 */
	public double amount; 
	
	/** 年利率 */
	public double apr;  
	
	/** 期限 */
	public  long  period;
	
	/** 投标期限单位 */
	public int periodUnit; 
		
	/** 投资方式 */
	public int repaymentType; 
	
	/** 投资状态 */
	public int status; 
	
	/** 放款时间 */
	public Date release_time;  
	
	/** 是否可以进行债券转让 */
	@Transient
	public boolean canBeTransed;
	
	/** 已结束账单 */
	public int alreadRepay;
	
	/** 全部账单 */
	public int totalPay;
	
	public Long getTime() {
		
		return time == null ? null : time.getTime();
	}

	@Transient
	public String statusStr;
	public String getStatusStr() {
		Status status = Status.getEnum(this.status);
		
		return status == null ? null : status.value ;
	}

	public String getInvestId() {
		
		return Security.addSign(this.investOriId, Constants.INVEST_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}

	public String getPeriodUnit() {
		PeriodUnit peroidUnit = PeriodUnit.getEnum((this.periodUnit));
		return peroidUnit == null ? null : peroidUnit.getShowValue();
	
	}

	public String getRepaymentType() {
		RepaymentType repaymentType = RepaymentType.getEnum(this.repaymentType);
		
		return repaymentType == null ? null : repaymentType.value;
	}

	public String getBidId() {
		
		return Security.addSign(this.id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
	//投资时间
	public Date time;
	/**用户id*/
	public long user_id;
	
	/**bid 表合同号*/
	public String contract_id;
	
	/**pact 合同编号*/
	public String contract;
}
