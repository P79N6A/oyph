package models.ext.wealthcircle.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;

/**
 * 我的邀请码(也即邀请记录)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年4月7日
 */
@Entity
public class MyWealthCircleUser implements Serializable {

	@Id
	public Long id;
	
	/** 创建时间 */
	public Date time;
	
	/** 邀请码 */
	public String wc_code;
	
	/** 用户id(被邀请人) */
	public Long user_id;
	
	/** 用户id(被邀请人) */
	public String user_name;
	
	/** 推广人/邀请人id(邀请码持有人) */
	public Long spreader_id;
	
	/** 邀请码激活时间(被使用时间) */
	public Date active_time;
	
	/** 0:未使用，1:已使用 */
	public boolean status;
	
	/** 被邀请人累计理财 */
	public double total_invest;
	
	/** 给邀请人带来累计返佣 */
	public double total_rebate;
	
	/** 可领取返佣 */
	public double recivable_rebate;

	@Transient
	public String sign;

	public String getSign() {
		sign = Security.addSign(id, Constants.WEALTHCIRCLE_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return sign;
	}
}
