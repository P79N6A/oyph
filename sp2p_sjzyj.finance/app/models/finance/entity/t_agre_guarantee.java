package models.finance.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
@Entity
public class t_agre_guarantee extends Model{
	/** 数据日期 */
	private Date data_date;
	/** 产品代码 */
	private String product_code;
	/** 机构代码 */
	private String org_num;
	/** 收益特征 */
	private String proceeds_character;
	/** 募集起始日期 */
	private Date collect_start_date;
	/** 募集结束日期 */
	private Date collect_end_date;
	/** 产品预计终止日期 */
	private Date intending_end_date;
	/** 产品说明 */
	private String prd_remark;
	/** 担保人证件类型 */
	private String guar_id_typ;
	/** 担保人证件号码 */
	private String guar_id_num;
	
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
	public String getOrg_num() {
		return org_num;
	}
	public void setOrg_num(String org_num) {
		this.org_num = org_num;
	}
	public String getProceeds_character() {
		return proceeds_character;
	}
	public void setProceeds_character(String proceeds_character) {
		this.proceeds_character = proceeds_character;
	}
	public Date getCollect_start_date() {
		return collect_start_date;
	}
	public void setCollect_start_date(Date collect_start_date) {
		this.collect_start_date = collect_start_date;
	}
	public Date getCollect_end_date() {
		return collect_end_date;
	}
	public void setCollect_end_date(Date collect_end_date) {
		this.collect_end_date = collect_end_date;
	}
	public Date getIntending_end_date() {
		return intending_end_date;
	}
	public void setIntending_end_date(Date intending_end_date) {
		this.intending_end_date = intending_end_date;
	}
	public String getPrd_remark() {
		return prd_remark;
	}
	public void setPrd_remark(String prd_remark) {
		this.prd_remark = prd_remark;
	}
	public String getGuar_id_typ() {
		return guar_id_typ;
	}
	public void setGuar_id_typ(String guar_id_typ) {
		this.guar_id_typ = guar_id_typ;
	}
	public String getGuar_id_num() {
		return guar_id_num;
	}
	public void setGuar_id_num(String guar_id_num) {
		this.guar_id_num = guar_id_num;
	}
	
	
}
