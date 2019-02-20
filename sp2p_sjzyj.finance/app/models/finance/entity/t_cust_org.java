package models.finance.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体：客户关联方信息表
 *
 * @ClassName: t_cust_org

 * @description 
 *
 * @author lujinpeng
 * @createDate 2018年10月5日-下午2:48:09
 */
@Entity
public class t_cust_org extends Model {

	/** 数据日期 */
	public Date data_date;
	
	/** 机构代码 */
	public String org_num;
	
	/** 客户号 */
	public String cust_id;
	
	/** 关联方代码 */
	public String relation_id;
	
	/** 关联方类型 */
	public String relation_type;
	
	/** 关联方证件类型 */
	public String relation_id_type;
	
	/** 关联方证件号码 */
	public String relation_id_no;
	
	/** 关联方联系电话 */
	public String cust_telephone_no;
	
	/** 关联方通讯地址 */
	public String cust_adde_desc;

	public Date getData_date() {
		return data_date;
	}

	public void setData_date(Date data_date) {
		this.data_date = data_date;
	}

	public String getOrg_num() {
		return org_num;
	}

	public void setOrg_num(String org_num) {
		this.org_num = org_num;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getRelation_id() {
		return relation_id;
	}

	public void setRelation_id(String relation_id) {
		this.relation_id = relation_id;
	}

	public String getRelation_type() {
		return relation_type;
	}

	public void setRelation_type(String relation_type) {
		this.relation_type = relation_type;
	}

	public String getRelation_id_type() {
		return relation_id_type;
	}

	public void setRelation_id_type(String relation_id_type) {
		this.relation_id_type = relation_id_type;
	}

	public String getRelation_id_no() {
		return relation_id_no;
	}

	public void setRelation_id_no(String relation_id_no) {
		this.relation_id_no = relation_id_no;
	}

	public String getCust_telephone_no() {
		return cust_telephone_no;
	}

	public void setCust_telephone_no(String cust_telephone_no) {
		this.cust_telephone_no = cust_telephone_no;
	}

	public String getCust_adde_desc() {
		return cust_adde_desc;
	}

	public void setCust_adde_desc(String cust_adde_desc) {
		this.cust_adde_desc = cust_adde_desc;
	}
	
}
