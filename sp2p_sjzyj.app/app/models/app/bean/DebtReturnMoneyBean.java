package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import models.core.entity.t_bill.Status;

/**
 * 债权回款计划
 *
 * @description
 *
 * @author DaiZhengmiao
 * @createDate 2016年7月1日
 */
@Entity
public class DebtReturnMoneyBean implements Serializable {

	/** billId */
	@Id
	public long id;

	/** 回款时间 */
	public Date repaymentTime;

	/** 本息 */
	public double principalInterest;

	/** 状态 */
	public int status;

	@Transient
	public String statusStr;

	/** 期数 */
	public int period;

	/** 总期数 */
	public int totalPeriod;

	public String getStatusStr() {
		Status status = Status.getEnum(this.status);

		return status == null ? null : status.value;
	}

	public Long getRepaymentTime() {
		if (repaymentTime == null) {
			return null;
		}
		return repaymentTime.getTime();
	}

}
