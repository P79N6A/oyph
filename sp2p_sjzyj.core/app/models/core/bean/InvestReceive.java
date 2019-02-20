package models.core.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_bid;
import models.core.entity.t_bill_invest;
import models.core.entity.t_bill_invest.Status;
import common.FeeCalculateUtil;
import common.utils.Factory;
import common.utils.NoUtil;
import daos.core.BidDao;
import daos.core.BillInvestDao;

/**
 * 投资回款
 *
 * @author liudong
 * @createDate 2016年2月25日
 */

@Entity
public class InvestReceive implements Serializable{

	@Id
	public long id;
	
	/** 时间 */
	public Date time;
	
	/** 投资人id */
	public long user_id;
	
	/** 期数 */
	public int period;
	
	/** 总期数 */
	public int totalPeriod;
	
	/** 投资ID */
	public long invest_id;
	
	/** 应收本息 */
	public double corpus_interest;
	
	/** 本金 */
	public double receive_corpus;

	/** 利息 */
	public double receive_interest;
	
	@Transient 
	public double coupon_interest;
		
	/** 到期时间 */
	public Date receive_time;
	
	/** 实际回款时间*/
	public Date real_receive_time;

	/** 状态 */
	public int status;
	public Status getStatus() {
		Status stat = Status.getEnum(this.status);
		
		return stat;
	}
	
	/** 服务费规则 */
	public String service_fee_rule;
	
	/** 账单编号 */
	@Transient
	public String bill_invest_no;
	public String getBill_invest_no () {
		return NoUtil.getBillInvestNo(this.id, this.time);
	}	
	
	/** 服务费 */
	@Transient
	public double loan_fee;
	public double getLoan_fee() {
		
		return FeeCalculateUtil.getOriginalInvestManagerFee(this.receive_interest, this.service_fee_rule);
	}
	
	/** 预计到账 */
	@Transient
	public double expected_arrival;
	public double getExpected_arrival() {
		
		return this.coupon_interest + this.receive_corpus - this.loan_fee;
	}
	
	/** 标的编号:前缀（J）+id */
	@Transient
	public String bid_no;
	public String getBid_no(){
		BillInvestDao billDao = Factory.getDao(BillInvestDao.class);
		t_bill_invest bill = billDao.findByID(id);
		return NoUtil.getBidNo(bill.bid_id);
	}
	


}
