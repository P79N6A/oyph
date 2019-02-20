package models.ext.cps.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;

/**
 * cps推广记录(前台)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月16日
 */
@Entity
public class CpsUser {

	@Id
	public Long id;
	
	/** 创建时间 */
	public Date time;
	
	/** 会员id(被推广人id，唯一索引) */
	public Long user_id;
	
	/** 会员昵称 */
	public String userName;
	
	/** 推广人id */
	public Long spreader_id;
	
	/** 累计返佣 */
	public double total_rebate;
	
	/** 待领取返佣 */
	public double recivable_rebate;
	
	/** 对id进行验签加密后的字符串 */
	@Transient
	public String sign;
	
	public String getSign() {
		
		String signID = Security.addSign(id, Constants.CPS_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}

}
