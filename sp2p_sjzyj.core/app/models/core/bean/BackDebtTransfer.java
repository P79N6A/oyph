package models.core.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_debt_transfer;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

/**
 * 债权转让列表
 * 
 * @author liudong
 * @createDate 2016年1月11日
 */
@Entity
public class BackDebtTransfer {
	
	@Id
	public long id;
	
	/** 债权转让项目的编号:前缀（Q）+id */
	@Transient
	public String debt_transfer_no;
	public String getDebt_transfer_no() {
		return NoUtil.getDebtTransferNo(this.id);
	}
	
	/** 转让人昵称*/
	public String transfer_name;
	
	/** 转让标题 */
	public String title;
	
	/** 债权金额 */
	public double debt_amount;
	
	/** 转让期数 */
	public double transfer_period;
	
	/** 转让价格 */
	public double transfer_price;
	
	/** 转让状态(0审核中，1为竞拍中，2成交 -1审核不通过,-2 过期，-3失效) */
	public int status;
	public t_debt_transfer.Status getStatus() {
		return t_debt_transfer.Status.getEnum(this.status);
	}
	
	/** 受让人昵称 */
	public String transaction_name;
	
	/** 成交时间 */
	public Date transaction_time;
	
	/** 合同编号*/
	public String contract_id;
	
	@Transient
	public String sign;
	public String getSign () {
		
		return Security.addSign(this.id, Constants.DEBT_TRANSFER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}

}
