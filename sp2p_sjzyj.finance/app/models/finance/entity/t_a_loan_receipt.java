package models.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_a_loan_receipt extends Model{
	/** 数据日期 */
	private Date data_date;
	/**	合同号 */
	private String contract_no;
	/**	机构代码 */
	private String org_num;
	/**	借据号 */
	private String loan_receipt_no;
	/**	客户号 */
	private String customer_id;
	/**	原始起息日 */
	private Date origin_value_date;
	/**	原始到期日 */
	private Date origin_mature_date;
	/**	放款金额 */
	private BigDecimal loan_receipt_amount;
	/**	贷款余额 */
	private BigDecimal loan_receipt_balance;
	/**	币种 */
	private String currency;
	/**	贷款状态 */
	private String acct_typ; 
	/**	贷款利率 */
	private BigDecimal acct_int;
	/**	账号 */
	private String account;
	
	public Date getData_date() {
		return data_date;
	}
	public void setData_date(Date data_date) {
		this.data_date = data_date;
	}
	public String getContract_no() {
		return contract_no;
	}
	public void setContract_no(String contract_no) {
		this.contract_no = contract_no;
	}
	public String getOrg_num() {
		return org_num;
	}
	public void setOrg_num(String org_num) {
		this.org_num = org_num;
	}
	public String getLoan_receipt_no() {
		return loan_receipt_no;
	}
	public void setLoan_receipt_no(String loan_receipt_no) {
		this.loan_receipt_no = loan_receipt_no;
	}
	public String getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	public Date getOrigin_value_date() {
		return origin_value_date;
	}
	public void setOrigin_value_date(Date origin_value_date) {
		this.origin_value_date = origin_value_date;
	}
	public Date getOrigin_mature_date() {
		return origin_mature_date;
	}
	public void setOrigin_mature_date(Date origin_mature_date) {
		this.origin_mature_date = origin_mature_date;
	}
	public BigDecimal getLoan_receipt_amount() {
		return loan_receipt_amount;
	}
	public void setLoan_receipt_amount(BigDecimal loan_receipt_amount) {
		this.loan_receipt_amount = loan_receipt_amount;
	}
	public BigDecimal getLoan_receipt_balance() {
		return loan_receipt_balance;
	}
	public void setLoan_receipt_balance(BigDecimal loan_receipt_balance) {
		this.loan_receipt_balance = loan_receipt_balance;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getAcct_typ() {
		return acct_typ;
	}
	public void setAcct_typ(String acct_typ) {
		this.acct_typ = acct_typ;
	}
	public BigDecimal getAcct_int() {
		return acct_int;
	}
	public void setAcct_int(BigDecimal acct_int) {
		this.acct_int = acct_int;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	
}
