package models.app.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.common.entity.t_bank_card_user.Status;
/****
 * 
 *
 * 银行卡
 *
 * @author luzhiwei
 * @createDate 2016-4-7
 */
@Entity
public class UserBankCard implements Serializable {
    @Id
	public long id;
	
	/** 银行名称 */
	public String bankName;
	
	/** 银行卡账号 */
	public String account;
	
	/** 状态描述 */
	@Transient
	public String statusStr;
	public String getStatusStr() {
		Status status = Status.getEnum(this.status);
		return status == null ? null : status.value;
	}
	
	/** 状态 */
	public int status;
	
}
