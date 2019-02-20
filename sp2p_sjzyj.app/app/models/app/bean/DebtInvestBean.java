package models.app.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

/**
 * 债权投资相关的信息
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
@Entity
public class DebtInvestBean implements Serializable {

	@Id
	public long id;
	
	@Transient
	public Long bid_id;
	
	/** 标的编号:前缀（J）+id */
	@Transient
	public String bid_no;
	public String getBid_no(){
		return NoUtil.getBidNo(this.bid_id);
	}
	
	public Long user_id;
	
	/** 原标记标题 */
	public String title;
	
	/** 债权总额:select sum(receive_corpus + receive_interest) from t_bill_invests where invest_id = ? */
	public double debt_amount;
	
	/** 债权本金 */
	public double debt_principal;
	
	/** 债权利息 */
	public double debt_interest;
	
	/** 债权剩余期数 */
	public int period;
	
	@Transient
	public String sign;
	
	public String getSign() {
		String signID = Security.addSign(id, Constants.INVEST_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		
		return signID;
	}
	
}
