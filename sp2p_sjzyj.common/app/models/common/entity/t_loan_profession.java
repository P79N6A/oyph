package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.enums.ApplicationType;
import common.enums.GuarantyStyle;
import daos.common.UserDao;
import daos.common.UserInfoDao;
import play.db.jpa.Model;

/**
 * 贷款业务信息表
 *  
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月27日
 */

@Entity
public class t_loan_profession extends Model {

	/** 用户ID(取t_users表的ID) */
	public long user_id;
	
	/** 标的id */
	public long bid_id;
	
	/** 贷款类型 */
	public int loan_type;
	
	public ApplicationType getLoan_type() {
		ApplicationType type = ApplicationType.getEnum(this.loan_type);
		return type;
	}
	
	public void setLoan_type(ApplicationType type) {
		this.loan_type = type.code;
	}
	
	/** 标的编号(贷款合同号码) */
	public String mer_bid_no;
	
	/** 业务订单号 */
	public String service_order_no;
	
	/** 发生地点 */
	public int site;
	
	/** 开户日期 */
	public Date account_time;
	
	/** 到期日期 */
	public Date expire_time;
	
	/** 币种 */
	public String currency;
	
	/** 授信额度 */
	public double credit_line;
	
	/** 共享授信额度 */
	public double share_credit_line;
	
	/** 最大负债额 */
	public double max_liskills;
	
	/** 担保方式 */
	public int guaranty_style;
	
	public GuarantyStyle getGuaranty_style() {
		GuarantyStyle styles = GuarantyStyle.getEnum(this.guaranty_style);
		return styles;
	}
	
	public void setGuaranty_style(GuarantyStyle styles) {
		this.guaranty_style = styles.code;
	}
	
	/** 还款频率 */
	public int repayment_frequency;
	
	/** 还款月数 */
	public String repayment_months;
	
	/** 剩余还款月数 */
	public String resudue_months;
	
	/** 协定还款期数 */
	public String agreement_repayment_period;
	
	/** 协定期还款额 */
	public String agreement_repayment_amount;
	
	/** 结算/应还款日期 */
	public Date repayment_date;
	
	/** 最近一次实际还款日期 */
	public Date last_repayment_date;
	
	/** 本月应还款金额 */
	public double current_amount;
	
	/** 本月实际还款金额 */
	public double actual_amount;
	
	/** 余额 */
	public double balance;
	
	/** 当前逾期期数 */
	public int current_overdue_periods;
	
	/** 当前逾期总额*/
	public double current_overdue_amount;
	
	/** 逾期31-60天未归还贷款本金 */
	public double overdue_one;
	
	/** 逾期61-90天未归还贷款本金 */
	public double overdue_two;
	
	/** 逾期91-180天未归还贷款本金 */
	public double overdue_three;
	
	/** 逾期180天未归还贷款本金 */
	public double overdue_four;
	
	/** 累计逾期期数 */
	public int total_overdue_periods;
	
	/** 最高逾期期数 */
	public int max_overdue_periods;
	
	/** 五级分类状态 */
	public int classify_status;
	
	/** 账户状态 */
	public int account_status;
	
	/** 24月（账户）还款状态 */
	public String repayment_status;
	
	/** 账户拥有者信息提示 */
	public int owner_message_hint;
	
	/** 添加时间 */
	public Date time;
	
	@Transient
	public String realityName;
	
	public String getRealityName() {
		UserInfoDao userInfoDao = common.utils.Factory.getDao(UserInfoDao.class);
		t_user_info userInfos = userInfoDao.findByColumn("user_id=?", this.user_id);
		if(userInfos != null) {
			return userInfos.reality_name;
		}else {
			return null;
		}
	}
	
	@Transient
	public String idNumber;
	
	public String getIdNumber() {
		UserInfoDao userInfoDao = common.utils.Factory.getDao(UserInfoDao.class);
		t_user_info userInfos = userInfoDao.findByColumn("user_id=?", this.user_id);
		if(userInfos != null) {
			return userInfos.id_number;
		}else {
			return null;
		}
	}
	
	@Transient
	public String papersType;
	
	public String getPapersType() {
		UserInfoDao userInfoDao = common.utils.Factory.getDao(UserInfoDao.class);
		t_user_info userInfos = userInfoDao.findByColumn("user_id=?", this.user_id);
		if(userInfos != null) {
			return userInfos.papers_type+"";
		}else {
			return null;
		}
	}
	
}
