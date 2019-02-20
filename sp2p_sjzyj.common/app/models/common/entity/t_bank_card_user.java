package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 用户银行账户信息
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月23日
 */
@Entity
public class t_bank_card_user extends Model {

	/** 创建时间 */
	public Date time;

	/** 用户ID */
	public long user_id;

	/** 开户行名称 */
	public String bank_name;

	/** 开户行code */
	public String bank_code;

	/** 账号或卡号 */
	public String account;

	/** 状态:1-默认；2-正常 */
	private int status;
		

	public Status getStatus() {
		Status status = Status.getEnum(this.status);
		return status;
	}

	public void setStatus(Status status) {
		this.status = status.code;
	}

	/**
	 * 银行卡状态枚举
	 * 
	 * @description
	 *
	 * @author ChenZhipeng
	 * @createDate 2015年12月23日
	 */
	public enum Status {
		
		/** 默认 */
		TYPE_DEFAULT(1, "默认"),

		/** 正常 */
		TYPE_NORMAL(2, "正常");

		public int code;
		public String value;

		private Status(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static Status getEnum(int code) {
			Status[] status = Status.values();
			for (Status statu : status) {
				if (statu.code == code) {

					return statu;
				}
			}

			return null;
		}
	}
}
