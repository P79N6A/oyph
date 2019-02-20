package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import models.common.entity.t_withdrawal_user.Status;

import org.omg.CORBA.PUBLIC_MEMBER;

import play.db.jpa.Model;
import common.enums.Client;

/**
 * 用户充值记录表
 * 
 * @description
 *
 * @author ChenZhipeng
 * @createDate 2015年12月21日
 */
@Entity
public class t_recharge_user extends Model {
	
	/** 交易时间 */
	public Date time;

	/** 用户ID */
	public long user_id;

	/** 业务订单号 */
	public String order_no;
	
	/** 充值金额 */
	public double amount;

	/** 充值入口:1-pc,2-app,3-wechat */
	private int client;

	public Client getClient() {
		Client client = Client.getEnum(this.client);
		return client;
	}

	public void setClient(Client client) {
		this.client = client.code;
	}

	/** 充值状态:-1-失败,0-处理中,1-成功 */
	private int status;

	public Status getStatus() {
		Status status = Status.getEnum(this.status);
		return status;
	}

	public void setStatus(Status status) {
		this.status = status.code;
	}
	
	/** 完成时间  */
	public String completed_time;
	
	/** 备注 */
	public String summary;
	
	
	public String bankName;
	
	public String bankAccount;
		
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
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
