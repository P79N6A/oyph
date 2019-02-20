package models.finance.entity;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 网贷机构验收进度明细统计表
 * @createDate 2018.10.22
 * */
@Entity
public class t_internet_agency_detail_statistic extends Model{

	/**企业名称*/
	public String agency_name;
	
	/**属地*/
	public String place;
	
	/**自然人借款余额(万元)*/
	public BigDecimal personal_loan_balance;
	
	/**法人及其他组织借款余额(万元)*/
	public BigDecimal organization_loan_balance;
	
	/**借款余额总计(万元)*/
	public BigDecimal loan_balance_total;
	
	/**自然人借款人数(人)*/
	public Integer personal_loan_count;
	
	/**法人及其他组织借款人数(人)*/
	public Integer organization_loan_count;
	
	/**借款人数总计（人）*/
	public Integer loan_count_total;
	
	/**自然人投资人数（人）*/
	public Integer personal_invest_count;
	
	/**法人及其他组织投资人数*/
	public Integer organization_invest_count;
	
	/**投资人数合计（人）*/
	public Integer invest_count;
	
	/**自然人平均借款额度（万元）*/
	public BigDecimal average_loan_quota_personal;
	
	/**法人及其他组织平均借款额度(万元)*/
	public BigDecimal average_loan_quota_organization;
	
	/**平均借款利率（%）*/
	public BigDecimal average_loan_apr;
	
	/**逾期金额（万元）*/
	public BigDecimal overdue_amount;
	
	/**存管银行名称*/
	public String deposit_tube_bank;
	
	/**存管状态-----1已上线*/
	public Integer deposit_tube_status;
	
	/**创建时间*/
	public Date time;
}
