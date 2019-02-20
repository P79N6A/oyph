package models.core.entity;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.IsOverdue;
import common.utils.NoUtil;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 借款账单
 *
 * @author liudong
 * @createDate 2015年12月18日
 */
@Entity
public class t_bill extends Model {
	
	/** 账单生成时间 */
	public Date time;

	/** 借款账单编号 */
	@Transient
	public String bill_no;
	public String getBill_no(){
		return NoUtil.getBillNo(this.id, this.time);
	}

	/** 借款人Id */
	public long user_id;
	
	/** 标的Id */
	public long bid_id;
	
	/** 借款标标题  */
	public String title;
	
	/** 账单期数 */
	public int period;
	
	/** 还款状态:0-正常待还； 1-本息垫付待还  2-正常还款  3-本息垫付还款  4-线下收款  5-本息垫付后线下收款；*/
	private int status;
	
	/** 还款日 */
	public Date repayment_time;
	
	/** 是否已出款 */
	public int is_payment;
	
	/** 应还本金 */
	public double repayment_corpus;
	
	/** 应还利息 */
	public double repayment_interest;
	
	/** 实际还款时间 */
	public Date real_repayment_time;
	
	/** 逾期标记时间 */
	public Date mark_overdue_time;
	
	/** 是否逾期，false:未逾期；true:逾期 */
	private boolean is_overdue;
	
	/** 逾期罚息 */
	public double overdue_fine;
	
	/** 逾期天数  */
	public int overdue_days;
	
	/** 担保人id  */
	public long guarantee_user_id;
	
	/** 业务流水号 */
	public String busniess_no;
	
	/**加密ID*/
	@Transient
	public String sign;

	public String getSign() {
		return Security.addSign(this.id, Constants.BILL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	public Status getStatus() {
		Status status = Status.getEnum(this.status);
		
		return status;
	}

	public void setStatus(Status status) {
		this.status = status.code;
	}
	
	public IsOverdue getIs_overdue() {
		IsOverdue isOverdue = IsOverdue.getEnum(this.is_overdue);
		
		return isOverdue;
	}

	public void setIs_overdue(IsOverdue isOverdue) {
		this.is_overdue = isOverdue.code;
	}
	
	/**
	 * 枚举：借款账单 ：  还款状态: 0:正常待还；1:本息垫付待还；2:正常还款；3:本息垫付还款；4:线下收款；5:本息垫付后线下收款；
	 *
	 * @author liudong
	 * @createDate 2015年12月21日
	 */
	public enum Status {
		
		/** 0  正常待还  */
		NORMAL_NO_REPAYMENT(0,"正常待还"),
		
		/** 1  本息垫付待还 */
		ADVANCE_PRINCIIPAL_NO_REPAYMENT(1,"本息垫付待还 "),
		
		/** 2  正常还款 */
		NORMAL_REPAYMENT(2,"正常还款"),
		
		/** 3  本息垫付后还款 */
		ADVANCE_PRINCIIPAL_REPAYMENT(3,"本息垫付还款"),
		
		/** 4  线下收款 */
		OUT_LINE_RECEIVE(4,"线下收款"),
		
		/** 5 本息垫付后线下收款 */
		OUT_LINE_PRINCIIPAL_RECEIVE(5,"本息垫付后线下收款"),
		
		/** 6 提前还款*/
		ADVANCE_lINE_RECEIVE(6,"提前还款"),
		
		/** 7 提前还款*/
		ADVANCE_lINE_RECEIVE_YET(7,"已提前还清"),
		
		BILL_STATUS_PAYMENT(8, "已出款");
		
		/** 状态 */
		public int code;
		
		/** 描述 */
		public String value;
		
		/** 未还(包括 正常待还+本息垫付待还 ) */
		public static final List<Integer> NO_REPAYMENT = Arrays.asList(
				NORMAL_NO_REPAYMENT.code,
				ADVANCE_PRINCIIPAL_NO_REPAYMENT.code); 

		/** 已还(正常还款、本息垫付还款 、线下收款、本息垫付后线下收款 ) */
		public static final List<Integer> REPAYED = Arrays.asList( 
				NORMAL_REPAYMENT.code,
				ADVANCE_PRINCIIPAL_REPAYMENT.code,
				OUT_LINE_RECEIVE.code,
				OUT_LINE_PRINCIIPAL_RECEIVE.code);
		
		private Status(int code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static Status getEnum(int code){
			Status[] status = Status.values();
			for(Status s: status){
				if(s.code == code){
					
					return s ;
				}
			}
			
			return null;
		}
	}
	
	/**
	 * 查询借款账单 ：
	 * 0:所有        1:待还(正常待还+逾期待还+本息垫付待还) 2:逾期待还     3:已还(正常还款、逾期还款、本息垫付还款和线下收款)
	 *
	 * @author liudong
	 * @createDate 2016年1月22日
	 */
	public enum BillStatus{
		
		/** 0:借款账单  */
		ALL_BILL(0,"借款账单"),
		
		/** 1:待还借款账单  */
		NO_REPAYMENT(1,"待还借款账单"),
		
		/** 2:逾期待还借款账单 */
		OVERDUE_NO_REPAYMENT(2,"逾期待还借款账单"),
		
		/** 3:已还借款账单 */
		REPAYMENT(3,"已还借款账单");
		
		/** 状态 */
		public int code;
		
		/** 描述 */
		public String value;
		
		private BillStatus(int code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static BillStatus getEnum(int code){
			BillStatus[] billStatus = BillStatus.values();
			for(BillStatus bill_status: billStatus){
				if(bill_status.code == code){
					
					return bill_status ;
				}
			}
			
			return null;
		}
	}
	
	/**lihuijun*/
	public void setStatus(int status) {
		this.status = status;
	}
	
	public void setIs_overdue(boolean is_overdue) {
		this.is_overdue = is_overdue;
	}

}
