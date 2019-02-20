package models.app.bean;

import java.io.Serializable;
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
public class InOutDebt implements Serializable{

	@Id
	public Long id;
	
	@Transient
	public String debtId;
	
	/** 债权转让项目的编号:前缀（Q）+id */
	@Transient
	public String debt_transfer_no;

	/** 对应的投资id */
	public Long invest_id;
	
	/** 转让人 user_id */
	public long user_id;
	
	/** 受让人 user_id */
	public Long transaction_user_id;
	
	/** 转让标题 */
	public String title;
	
	/** 债权金额 */
	public double debt_amount;//剩余本金+利息(罚息去掉)
	
	/** 债权本金 */
	public double debt_principal;
	
	/** 转让底价 */
	public double transfer_price;
	
	/** 债权剩余期数 */
	public int transfer_period;
	
	/** 转让状态(0审核中，1为竞拍中，2成交 -1审核不通过,-2 过期，-3失效) */
	private int status;
	
	/** 状态名称 */
	@Transient
	private String statusStr;
	
	/** 成交时间 */
	public Date transaction_time;
	
	/**合同编号*/
	public String contract_id;
	
	public int getStatus() {
		return status;
	}
	public String getDebt_transfer_no() {
		return NoUtil.getDebtTransferNo(this.id);
	}
	
	public String getDebtId() {
		return Security.addSign(this.id, Constants.DEBT_TRANSFER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
	public String getStatusStr() {
		return  Status.getEnum(this.status).value;
	}
	public String getTransaction_time() {
		if(transaction_time != null){
			return transaction_time.getTime()+"";
		}
		
		return null;
	}
	
}
