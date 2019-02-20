package models.main.bean;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.PactType;
import common.utils.Security;
import common.utils.TimeUtil;
import play.db.jpa.Model;

@Entity
public class LoanContract extends Model{
	/** 理财人账号 */
	public String name;
	/** 借款金额*/
	public BigDecimal amount;

	/** 合同生成时间 */
	public Date time;
	
	@Transient
	public Long date;
	
	/** 主体的id(如债权转让合同对应的是债权id，散标投资对应的是bid) */
	public Long bidId;
	/** 用户ID */
	public Long user_id;
	
	/** 上上签合同编号*/
	public String contract_id;
	
	/** 上上签合同编号(旧)*/
	public String contract;
	/** 标的状态 */
	public int status;
}
