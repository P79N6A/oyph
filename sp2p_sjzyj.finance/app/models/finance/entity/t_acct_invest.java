package models.finance.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
@Entity
public class t_acct_invest extends Model{
	/**	数据日期 */
	private Date data_date;
	/**	产品代码 */
	private String product_code;
	/**	产品最终投向 */
	private String invest_type;
	/**	机构代码 */
	private String org_num;
	/**	产品是否多层嵌套  */
	private String whe_more;
	
	public Date getData_date() {
		return data_date;
	}
	public void setData_date(Date data_date) {
		this.data_date = data_date;
	}
	public String getProduct_code() {
		return product_code;
	}
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
	public String getInvest_type() {
		return invest_type;
	}
	public void setInvest_type(String invest_type) {
		this.invest_type = invest_type;
	}
	public String getOrg_num() {
		return org_num;
	}
	public void setOrg_num(String org_num) {
		this.org_num = org_num;
	}
	public String getWhe_more() {
		return whe_more;
	}
	public void setWhe_more(String whe_more) {
		this.whe_more = whe_more;
	}
	
}
