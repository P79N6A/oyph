package models.finance.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体：客户基本信息表
 *	
 * @ClassName: t_cust_info

 * @description 
 *
 * @author lujinpeng
 * @createDate 2018年10月5日-下午2:45:53
 */
@Entity
public class t_cust_info extends Model {

	/** 数据日期 */
	public Date data_date;
	
	/** 机构代码 */
	public String org_num;
	
	/** 客户号 */
	public String cust_id;
	
	/** 客户中文名称 */
	public String cust_nam;
	
	/** 证件类型 */
	public String id_type;
	
	/** 证件号码 */
	public String id_no;
	
	/** 通讯地址 */
	public String cust_adde_desc;
	
	/** 营业执照到期日期 */
	public Date exp_date;
	
	/** 营业执照是否有效 */
	public Character sts_flg;

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

	public String getCust_nam() {
		return cust_nam;
	}

	public void setCust_nam(String cust_nam) {
		this.cust_nam = cust_nam;
	}

	public String getId_type() {
		return id_type;
	}

	public void setId_type(String id_type) {
		this.id_type = id_type;
	}

	public String getId_no() {
		return id_no;
	}

	public void setId_no(String id_no) {
		this.id_no = id_no;
	}

	public String getCust_adde_desc() {
		return cust_adde_desc;
	}

	public void setCust_adde_desc(String cust_adde_desc) {
		this.cust_adde_desc = cust_adde_desc;
	}

	public Date getExp_date() {
		return exp_date;
	}

	public void setExp_date(Date exp_date) {
		this.exp_date = exp_date;
	}

	public Character getSts_flg() {
		return sts_flg;
	}

	public void setSts_flg(Character sts_flg) {
		this.sts_flg = sts_flg;
	}
	
}
