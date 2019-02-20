package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.enums.AnnualIncome;
import common.enums.Duty;
import common.enums.Professional;
import daos.common.UserDao;
import daos.common.UserInfoDao;
import play.db.jpa.Model;

/**
 * 用户表(职业信息)
 *  
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月27日
 */

@Entity
public class t_user_profession extends Model {

	/** 用户ID(取t_users表的ID) */
	public long user_id;
	
	/** 职业 */
	public String profession;
	
	/** 单位所属行业 */
	public String company_trade;
	
	/** 单位地址 */
	public String company_address;
	
	/** 单位地址邮政编码 */
	public String company_postal_code;
	
	/** 本单位工作起始年份 */
	public String start_work_time;
	
	/** 职务 */
	public int duty;
	
	public Duty getDuty() {
		Duty duty = Duty.getEnum(this.duty);
		return duty;
	}
	
	public void setDuty(Duty duty) {
		this.duty = duty.code;
	}
	
	/** 职称 */
	public int professional;
	
	public Professional getProfessional() {
		Professional pro = Professional.getEnum(this.professional);
		return pro;
	}
	
	public void setProfessional(Professional pro) {
		this.professional = pro.code;
	}
	
	/** 年收入 */
	public double annual_income;
	
	/** 单位名称 */
	public String company_name;
	
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
