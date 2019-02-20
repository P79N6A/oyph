package models.ext.experience.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ext.ExperienceBidConstants;

import models.ext.experience.entity.t_experience_bid.Status;

@Entity
public class experienceUserInvestRecord implements Serializable{

	@Id
	public long id;
	
	/** 体验标id */
	public String bid_id;
	
	/** 体验标名称 */
	public String title;
	
	/** 投资金额 */
	public double invest_amount;
	
	/** 预计收益 */
	public double income;
	
	/** 投资时间 */
	public Date invest_time;
	
	/** 还款时间 */
	public Date repayment_time;
	
	/** 体验标状态 */
	public int status;
	
	public void setStatus(Status status) {
		this.status = status.code;
	}
	
	public Status getStatus() {
		Status status = Status.getEnum(this.status);
		
		return status;
	}
	
	/** 体验标编号 */
	@Transient
	public String bidNo;
	public String getBidNo() {
		return ExperienceBidConstants.PREFIX_EXPERIENCEBID+this.bid_id;
	}
	
}
