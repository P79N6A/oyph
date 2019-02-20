package models.core.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_bill_invest.Status;
import common.enums.IsOverdue;
import common.utils.NoUtil;
import common.utils.number.Arith;

/**
 * 理财账单
 *
 * @author liudong
 * @createDate 2016年1月25日
 */
@Entity
public class BillInvest {
	
	@Id
	public long id;
	
	/** 账单生成时间 */
	public Date time;
	
	/** 理财账单编号 */
	@Transient
	public String bill_invest_no;
	public String getBill_invest_no () {
		return NoUtil.getBillInvestNo(this.id, this.time);
	}
	
	/** 投资ID */
	public long invest_id;
	
	/** 期数 */
	public int period;
	
	/** 借款标标题  */
	public String title;
	
	/** 借款人姓名  */
	public String name;
	
	/** 借款标ID */
	public long bid_id;
	
	/** 收款时间 */
	public Date receive_time;
	
	/** 本期应收款金额 */
	public double receive_corpus;
	
	/** 本期应收利息 */
	public double receive_interest;
	
	/** 本息合计 */
	public double corpus_interest;
	
	/** 是否逾期:false:未逾期；true:逾期 */
	public boolean is_overdue;
	
	/** 逾期天数  */
	public int overdue_days;
	
	/** 收款状态:0-待还；1-已还；2-已转让 */
	public int status;
	
	/** 实际收款时间 */
	public Date real_receive_time;
	
	/** 总共期数  */
	public int totalPeriod;
	
	/** 标的编号:前缀（J）+id */
	@Transient
	public String bid_no;
	public String getBid_no(){
		return NoUtil.getBidNo(this.bid_id);
	}
	
	public IsOverdue getIs_overdue() {
		IsOverdue isOverdue = IsOverdue.getEnum(this.is_overdue);
		
		return isOverdue;
	}
	
	public Status getStatus() {
		Status stat = Status.getEnum(this.status);
		
		return stat;
	}

}
