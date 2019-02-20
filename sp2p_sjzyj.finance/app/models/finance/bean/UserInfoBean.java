package models.finance.bean;

import java.math.BigDecimal;

import javax.persistence.Entity;

import play.db.jpa.Model;
/**
 * 
 *
 * @ClassName: UserInfoBean

 * @description t_max_ten_user_information(河北省P2P机构最大十家客户信息表)创建(UserInfoBean)方便查询字段数据
 *
 * @author yaozijun
 * @createDate 2018年10月8日
 */
@Entity
public class UserInfoBean extends Model{
	/** 用户ID(取t_users表的ID) */
	private long user_id;
	/** 真实姓名 */
	private String reality_name;

	/** 身份证号码 */
	private String id_number;
	
	/** 企业名称(企业开户) */
	private String enterprise_name;
	
	/** 企业信用代码(企业开户) */
	private String enterprise_credit;
	
	/** 企业对公账户(企业开户) */
	private String enterprise_bank_no;
	
	/** 证件类型 */
	private int papers_type;
	
	/** 期末余额 */
	private BigDecimal debt;
	
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public String getReality_name() {
		return reality_name;
	}
	public void setReality_name(String reality_name) {
		this.reality_name = reality_name;
	}
	public String getId_number() {
		return id_number;
	}
	public void setId_number(String id_number) {
		this.id_number = id_number;
	}
	public String getEnterprise_name() {
		return enterprise_name;
	}
	public void setEnterprise_name(String enterprise_name) {
		this.enterprise_name = enterprise_name;
	}
	public String getEnterprise_credit() {
		return enterprise_credit;
	}
	public void setEnterprise_credit(String enterprise_credit) {
		this.enterprise_credit = enterprise_credit;
	}
	public String getEnterprise_bank_no() {
		return enterprise_bank_no;
	}
	public void setEnterprise_bank_no(String enterprise_bank_no) {
		this.enterprise_bank_no = enterprise_bank_no;
	}
	public int getPapers_type() {
		return papers_type;
	}
	public void setPapers_type(int papers_type) {
		this.papers_type = papers_type;
	}
	public BigDecimal getDebt() {
		return debt;
	}
	public void setDebt(BigDecimal debt) {
		this.debt = debt;
	}
	
	
}
