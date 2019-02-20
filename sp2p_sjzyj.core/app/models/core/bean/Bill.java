package models.core.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;
import models.core.entity.t_bill.Status;

/**
 * 借款账单列表
 * 
 * @author liudong
 * @createDate 2016年1月11日
 */
@Entity
public class Bill{
	
	@Id
	public long id;
	
	/** 账单生成时间 */
	public Date time;
	
	/** 借款账单编号 */
	@Transient
	public String bill_no;
	public String getBill_no() {
		return NoUtil.getBillNo(this.id,this.time);
	}

	/** 借款人Id */
	public long user_id;
	
	/** 标的Id */
	public long bid_id;
	
	/** 借款标标题  */
	public String title;
	
	/** 账单期数 */
	public int period;
	
	public int is_payment;
	
	/** 还款状态:0-正常待还； 1-本息垫付待还  2-正常还款  3-本息垫付还款  4-线下收款  5-本息垫付后线下收款；*/
	public int status;
	
	/** 还款日 */
	public Date repayment_time;
	
	/** 实际还款时间 */
	public Date real_repayment_time;
	
	/** 应还本金 */
	public double repayment_corpus;
	
	/** 应还利息 */
	public double repayment_interest;
		
	/** 是否逾期，false:未逾期；true:逾期 */
	public boolean is_overdue;
	
	/** 逾期罚息 */
	public double overdue_fine;
	
	/** 逾期天数  */
	public int overdue_days;
	
	/** 借款人昵称 */
	public String name;
	
	/** 本息合计 */
	public double corpus_interest;

	/** 总共期数 */
	public int totalPeriod;
	
	/** 标的编号:前缀（J）+id */
	@Transient
	public String bid_no;
	public String getBid_no(){
		return NoUtil.getBidNo(this.bid_id);
	}
	
	/** 提前还款标示（标示单期资金不能执行提前还款）*/
	@Transient
	public boolean adFlag;
	
	//加密借款账单ID
	@Transient
	public String sign;
	public String getSign() {
		return Security.addSign(this.id, Constants.BILL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	public Status getStatus() {
		Status status = Status.getEnum(this.status);
		
		return status;
	}
}
