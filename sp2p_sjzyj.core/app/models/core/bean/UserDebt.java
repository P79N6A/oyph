package models.core.bean;

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

/**
 * 用户的债权(包括受让和转让)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月30日
 */
@Entity
public class UserDebt implements Serializable {

	@Id
	public Long id;
	
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
	
	/** 开始时间 */
	public Date start_time;
	
	/** 结束时间 */
	public Date end_time;
	
	/** 成交时间 */
	public Date transaction_time;
	
	/** 转让和受让协议合同id*/
	public String contract_id;
	
	@Transient
	public String sign;
	public String getSign () {
		
		return Security.addSign(this.id, Constants.DEBT_TRANSFER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	public Status getStatus() {
		return Status.getEnum(this.status);
	}
	
}
