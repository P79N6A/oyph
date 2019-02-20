package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import models.common.entity.t_withdrawal_user;

/**
 * APP充值记录bean
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年4月5日
 */
@Entity
public class RechargeBean implements Serializable{
	
	@Id
	public long id;
	
	/** 状态 1-失败 0-处理中 1-成功 */
	public int status;
	
	/** 充值时间 */
	public Date time;
	
	/** 充值金额 */
	public double amount;
	
	/** 充值订单号 */
	public String orderNo;
	
	/** 充值备注 */
	public String summary;
	
	public String getStatus() {
		t_withdrawal_user.Status status = t_withdrawal_user.Status.getEnum(this.status);
		return status == null ? null : status.value;
	}
	public Long getTime() {
		if (this.time==null) {
			return null;
		}
		return this.time.getTime();
	}
}
