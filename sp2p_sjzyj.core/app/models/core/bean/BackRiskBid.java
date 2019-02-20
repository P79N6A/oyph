package models.core.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_bid.Status;
import models.core.entity.t_product.PeriodUnit;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

/**
 * 后台-风控-列表的bean
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年1月25日
 */
@Entity
public class BackRiskBid implements Serializable{

	@Id
	public long id;
	
	/** 借款标题 */
	public String title;
	
	/** 用户名 */
	public String name;
	
	/** 借款人姓名*/
	public String reality_name;
	
	/** 借款金额 */
	public double amount;
	
	/** 年利率 */
	public double apr;
	
	/** 借款期限单位
	 *1-天
	 *2-月*/
	public int period_unit;
	
	/** 借款期限 */
	public int period;
	
	/** 借款进度比例 */
	public double loan_schedule;
	
	/** 发售时间 */
	public Date pre_release_time;
	
	/** 标的状态 */
	public int status;
	
	@Transient
	public String sign;
	
	/** 标的编号 */
	@Transient
	public String bid_no;
	public String getBid_no(){
		return NoUtil.getBidNo(id);
	}
	
	public PeriodUnit getPeriod_unit(){
		return PeriodUnit.getEnum(this.period_unit);
	}
	
	public Status getStatus(){
		return Status.getEnum(this.status);
	}
	
	public BackRiskBid(){}

	public String getSign() {
		String signID = Security.addSign(id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
}
