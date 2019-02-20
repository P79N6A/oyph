package models.app.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_debt_transfer.Status;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

@Entity
public class DebtTransferDetailBean {

	@Id
	public Long id;
	
	@Transient
	public String debtId;
	
	/** 创建时间 */
	public Date time;

	/** 债权转让项目的编号:前缀（Q）+id */
	@Transient
	public String debt_transfer_no;

	public String getDebt_transfer_no() {
		return NoUtil.getDebtTransferNo(this.id);
	}

	/** 对应的投资id */
	public Long invest_id;

	/** 标记id */
	public long bid_id;

	/** 转让人 user_id */
	public long user_id;

	/** 转让标题 */
	public String title;

	/** 债权金额 */
	public double debt_amount;

	/** 债权本金 */
	public double debt_principal;

	/** 转让底价 */
	public double transfer_price;

	/** 期限(单位天) */
	public int period;

	/** 转让状态(0审核中，1为竞拍中，2成交 -1审核不通过,-2 过期，-3 失效) */
	public int status;

	@Transient
	public String statusStr;
	
	/** 最近还款时间 */
	public Date receive_time;

	/** 竞价结束时间 */
	public Date end_time;


	public String getDebtId() {

		return Security.addSign(this.id, Constants.DEBT_TRANSFER_ID_SIGN,ConfConst.ENCRYPTION_APP_KEY_DES);
	}

	public Status getStatusStr() {
		return Status.getEnum(this.status);
	}

	public long getTime() {
		if(time != null){
			return time.getTime();
		}
		return 0;
	}

	public long getReceive_time() {
		if(receive_time != null){
			return receive_time.getTime();
		}
		return 0;
	}

	public long getEnd_time() {
		if(end_time != null){
			return end_time.getTime();
		}
		return 0;
	}
	
}
