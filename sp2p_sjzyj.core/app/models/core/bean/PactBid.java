package models.core.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.core.entity.t_product.PeriodUnit;
import models.core.entity.t_product.RepaymentType;
import net.sf.json.JSONObject;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.NoUtil;
import common.utils.number.NumberFormat;

/**
 * bean:标记的合同相关内容
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年1月20日
 */
@Entity
public class PactBid {

	@Id
	public long id;

	/** 标的编号:前缀（J）+id */
	@Transient
	public String bid_no;
	public String getBid_no(){
		return NoUtil.getBidNo(this.id);
	}
	
	/** 借款标题(purpose_name) */
	public String title;
	
	/** 借款描述 */
	public String description;
	
	/** 用户名(loan_name) */
	public String name;
	
	/** 借款用户真实姓名(loan_real_name) */
	public String reality_name;
	
	/** 借款用户身份证号(id_number) */
	public String id_number;
	
	/** 借款金额 */
	public double amount;
	
	/** 中文标示 */
	@Transient
	public double cmount;
	
	/** 年利率(apr) */
	public double apr;
	
	/** 借款期限单位period_unit
	 *1-天
	 *2-月*/
	private int period_unit;
	
	/** 借款期限(period_num) */
	public int period;
	
	/** 放款时间(release_date) */
	public Date release_time;
	
	/** 合同到期时间(放款时间加上期数) */
	@Transient
	public Date last_repay_time;
	
	/** 还款方式(repayment_type) */
	private int repayment_type;
	
	/** 借款服务费、理财服务费、逾期罚息规则（以JSON串的形式存储）(数据来自借款产品) */
	public String service_fee_rule;
	
	/** 借款服务费的key：借款金额百分比  */
	@Transient
	private String loan_amount_rate;
	
	/** 借款服务费的key：减去的借款期数 */
	@Transient
	private String sub_period;
	
	/** 借款服务费的key：减去借款期数后，借款金额的百分比 */
	@Transient
	private String sub_loan_amount_rate;
	
	/** 理财服务费的key：百分比 */
	@Transient
	private String invest_amount_rate;
	
	/** 逾期罚息的key：百分比 */
	@Transient
	private String overdue_amount_rate;
	
	public String getLoan_amount_rate() {
		JSONObject json = JSONObject.fromObject(service_fee_rule);
		loan_amount_rate = json.getString(Constants.LOAN_AMOUNT_RATE);
		return loan_amount_rate;
	}
	
	public String getSub_period() {
		JSONObject json = JSONObject.fromObject(service_fee_rule);
		sub_period = json.getString(Constants.SUB_PERIOD);
		return sub_period;
	}

	public String getSub_loan_amount_rate() {
		JSONObject json = JSONObject.fromObject(service_fee_rule);
		sub_loan_amount_rate = json.getString(Constants.SUB_LOAN_AMOUNT_RATE);
		return sub_loan_amount_rate;
	}

	public String getInvest_amount_rate() {
		JSONObject json = JSONObject.fromObject(service_fee_rule);
		invest_amount_rate = json.getString(Constants.INVEST_AMOUNT_RATE);
		return invest_amount_rate;
	}

	public String getOverdue_amount_rate() {
		JSONObject json = JSONObject.fromObject(service_fee_rule);
		overdue_amount_rate = json.getString(Constants.OVERDUE_AMOUNT_RATE);
		return overdue_amount_rate;
	}

	public PeriodUnit getPeriod_unit(){
		return PeriodUnit.getEnum(this.period_unit);
	}

	public String getCmount() {
		String cmounts = NumberFormat.financeCN(amount);
		
		return cmounts;
	}

	public Date getLast_repay_time() {
		PeriodUnit periodUnit = getPeriod_unit();
		if(PeriodUnit.MONTH.equals(periodUnit)){
			last_repay_time = DateUtil.addMonth(release_time, period);
		} else if(PeriodUnit.DAY.equals(periodUnit)){
			last_repay_time = DateUtil.addDay(release_time, period);
		}
		return last_repay_time;
	}

	public RepaymentType getRepayment_type(){
		return RepaymentType.getEnum(this.repayment_type);
	}
	
	//企业
	/** 企业名称 */
	public String enterprise_name;
	
	//上上签担保方账号
	public String guarantee_user;
	
}
