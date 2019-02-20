package models.core.entity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

/**
 * 债权转让实体
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月25日
 */
@Entity
public class t_debt_transfer extends Model {

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
	
	/** 转让标题 */
	public String title;
	
	/** 债权金额 */
	public double debt_amount;//剩余本金+利息(罚息去掉)
	
	/** 债权本金 */
	public double debt_principal;//底价、管理费
	
	/** 转让底价 */
	public double transfer_price;
	
	/** 债权剩余期数 */
	public int transfer_period;
	
	/** 期限(单位天) */
	public int period;
	
	/** 转让状态(0审核中，1为竞拍中，2成交 -1审核不通过,-2 过期，-3 失效) */
	private int status;
	
	/** 审核管理员id */
	public Long audit_supervisor_id;
	
	/** 开始时间 */
	public Date start_time;
	
	/** 结束时间 */
	public Date end_time;
	
	/** 成交者ID */
	public Long transaction_user_id;
	
	/** 成交时间 */
	public Date transaction_time;
	
	/** 成交价 */
	public double transaction_price;
	
	/** 业务订单号 */
	public String service_order_no;
	
	/** 交易订单号(第三方返回) */
	public String trust_order_no; 
	
	
	@Transient
	public String sign;
	public String getSign () {
		
		return Security.addSign(this.id, Constants.DEBT_TRANSFER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
	public Status getStatus() {
		return Status.getEnum(this.status);
	}

	public void setStatus(Status status) {
		this.status = status.code;
	}




	/**
	 * 债权转让状态枚举
	 *
	 * @description 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public enum Status {
		
		/** 待审核:0 */
		PREAUDITING(0,"待审核"),
		
		/** 转让中:1 */
		AUCTING(1,"转让中"),
		
		/** 成功:2 */
		SUCC(2,"成功"),
		
		/** 审核不通过:-1 */
		AUDIT_NOT_THROUGH(-1,"审核不通过"),
		
		/** 过期:-2 */
		FAILED(-2,"过期"),
		
		/** 失效:-3 */
		INVALID(-3,"失效"),
		
		;
		
		
		public int code;
		public String value;
		private Status(int code, String value) {
			this.code = code;
			this.value = value;
		}
		
		/** 转让流程未完成(0待审核，1为转让中 ) */
		public static final List<Integer> NO_TRANSFER_FINISH = Arrays.asList(
				PREAUDITING.code,
				AUCTING.code
				); 
		
		/**
		 * 根据code取得客户端类型,没有找到返回null
		 *
		 * @param code
		 * @return
		 *
		 * @author DaiZhengmiao
		 * @createDate 2015年12月8日
		 */
		public static Status getEnum(int code){
			Status[] clients = Status.values();
			for (Status cli : clients) {
				if (cli.code == code) {

					return cli;
				}
			}
			
			return null;
		}
	}
}
