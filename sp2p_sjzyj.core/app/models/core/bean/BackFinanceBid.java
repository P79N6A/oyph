package models.core.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_bid.Status;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.NoUtil;
import common.utils.Security;

/**
 * 后台-财务-列表的bean
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年1月25日
 */
@Entity
public class BackFinanceBid implements Serializable{
	
	@Id
	public long id;
	
	/** 借款标题 */
	public String title;
	
	/** 用户名 */
	public String name;
	
	/** 借款金额 */
	public double amount;
	
	/** 标的状态 */
	public int status;
	
	/** 放款时间 */
	public Date release_time;
	
	/** 初审管理员真实姓名 */
	public String pre_audit_supervisor;
	
	/** 复审管理员真实姓名 */
	public String audit_supervisor;
	
	/**	放款管理员真实姓名 */
	public String release_supervisor;
	
	/** 合同编号 */
	public String contract_id;
	
	@Transient
	public String sign;
	
	public String getBid_no(){
		return NoUtil.getBidNo(id);
	}
	
	public Status getStatus(){
		return Status.getEnum(this.status);
	}
	
	public BackFinanceBid(){}

	public String getSign() {
		String signID = Security.addSign(id, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
}
