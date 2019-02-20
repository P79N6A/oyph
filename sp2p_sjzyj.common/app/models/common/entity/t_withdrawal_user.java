package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
import common.enums.Client;

/**
 * 用户提现记录表
 * 
 * @description
 *
 * @author ChenZhipeng
 * @createDate 2015年12月21日
 */
@Entity
public class t_withdrawal_user extends Model {

	/** 用户id */
	public long user_id;

	/** 申请时间 */
	public Date time;

	/** 业务订单号 */
	public String order_no;
	
	/** 提现金额 */
	public double amount;

	/** 银行卡号 */
	public String bank_account;

	/** 完成时间 */
	public Date completed_time;

	/** 备注 */
	public String summary;
	
	/** 提现渠道:1-PC、2-APP、3-wechat */
	private int client;

	public Client getClient() {
		Client client = Client.getEnum(this.client);
		return client;
	}

	public void setClient(Client client) {
		this.client = client.code;
	}

	/** 状态:-1-失败、0-处理中、1-成功 */
	private int status;

	public Status getStatus() {
		Status status = Status.getEnum(this.status);
		return status;
	}

	public void setStatus(Status status) {
		this.status = status.code;
	}

	/**
	 * 状态枚举
	 * 
	 * @description
	 *
	 * @author ChenZhipeng
	 * @createDate 2015年12月21日
	 */
	public enum Status {

		/** -1:失败 */
		FAILED(-1, "失败"),

		/** 0-处理中 */
		HANDING(0, "处理中"),
		
		/** 1-成功 */
		SUCCESS(1, "成功");

		public int code;
		public String value;

		private Status(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static Status getEnum(int code) {
			Status[] status = Status.values();
			for (Status stat : status) {
				if (stat.code == code) {

					return stat;
				}
			}

			return null;
		}
	}
}
