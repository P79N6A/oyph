package models.ext.experience.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ext.ExperienceBidConstants;
import common.utils.DateUtil;
import play.db.jpa.Model;

@Entity
public class t_experience_bid extends Model{
	
	/** 发布时间 */
	public Date time;
	
	@Transient
	public String bid_no;
	
	/** 体验标名称 */
	public String title;
	
	/** 年利率 */
	public double apr;
	
	/** 借款期限(单位：天) */
	public int period;
	
	/** 投标期限(单位：天) */
	public int invest_period;
	
	/** 起投金额 */
	public double min_invest_amount;
	
	/** 还款方式 */
	public String repayment_type;
	
	/** 状态:0-借款中 1-还款中 2-已结束 */
	public int status;
	
	/** 已投金额 */
	public double has_invest_amount;
	
	/** 放款时间 */
	public Date release_time;
	
	/** 还款时间 */
	public Date repayment_time;
	
	/** 实际还款时间 */
	public Date real_repayment_time;
	
	/** 加入人次 */
	public int invest_count;
	
	public void setStatus(Status status) {
		this.status = status.code;
	}
	
	public Status getStatus() {
		Status status = Status.getEnum(this.status);
		
		return status;
	}
	
	public String getBid_no(){
		return ExperienceBidConstants.PREFIX_EXPERIENCEBID+this.id;
	}
	
	public enum Status{
		
		/** 0-借款中 */
		FUNDRAISING(0, "借款中"),
		
		/** 1-还款中 */
		REPAYING(1, "还款中"),
		
		/** 2-已结束 */
		END(2, "已结束");
		
		/** 状态码 */
		public int code;
		
		/** 状态描述 */
		public String value;
		
		private Status(int code, String value){
			this.code = code;
			this.value=value;
		}
		
		public static Status getEnum(int code){
			Status[] bidstatus = Status.values();
			for(Status income:bidstatus){
				if(income.code == code){
					
					return income;
				}
			}
			
			return null;
		}
	}
	
	/** 投标期限是否到期 */
	@Transient
	public boolean is_overdue;
	public boolean getIs_overdue() {
		Date deadLineDate = DateUtil.addDay(this.time, this.invest_period);
		if(new Date().compareTo(deadLineDate)==1){
			
			return true;
		}
		
		return false;
	}
}
