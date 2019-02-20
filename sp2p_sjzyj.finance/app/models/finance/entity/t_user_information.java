package models.finance.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 
 * @ClassName: t_user_information
 *
 * @description (P04 河北省P2P机构客户信息表)实体类
 *
 * @author yaozijun
 * @createDate 2018年10月5日
 */
@Entity
public class t_user_information extends Model{
	/** 客户编码 */
	public String cust_id;
	
	/** 客户类型 */
	public String cust_type;
	
	/** 客户名称 */
	public String customer_name;
	
	/** 证件类型 */
	public String papers_type;
	
	/** 证件号码 */
	public String id_no;
	
	public String getPapers_type() {
		return papers_type;
	}

	public void setPapers_type(String papers_type) {
		this.papers_type = papers_type;
	}

	/** 是否本机构员工 */
	public String whether_organization_member;
	
	/** 企业规模 */
	public String enterprise_scale;
	
	/** 所属行业 */
	public String industry_involved;
	
	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getCust_type() {
		return cust_type;
	}

	public void setCust_type(String cust_type) {
		this.cust_type = cust_type;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getId_no() {
		return id_no;
	}

	public void setId_no(String id_no) {
		this.id_no = id_no;
	}

	public String getWhether_organization_member() {
		return whether_organization_member;
	}

	public void setWhether_organization_member(String whether_organization_member) {
		this.whether_organization_member = whether_organization_member;
	}

	public String getIndustry_involved() {
		return industry_involved;
	}

	public void setIndustry_involved(String industry_involved) {
		this.industry_involved = industry_involved;
	}

	public String getEnterprise_scale() {
		return enterprise_scale;
	}

	public void setEnterprise_scale(String enterprise_scale) {
		this.enterprise_scale = enterprise_scale;
	}
	
	
	

}
