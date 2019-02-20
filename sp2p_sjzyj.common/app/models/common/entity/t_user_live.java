package models.common.entity;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.enums.DwellingCondition;
import daos.common.UserDao;
import daos.common.UserInfoDao;
import play.db.jpa.Model;

/**
 * 用户表(居住信息)
 *  
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月27日
 */

@Entity
public class t_user_live extends Model {

	/** 用户ID(取t_users表的ID) */
	public long user_id;
	
	/** 居住地址  */
	public String residential_address;
	
	/** 居住地址邮政编码  */
	public String residential_postal_code;
	
	/** 居住状况 */
	public int residential_condition;
	
	public DwellingCondition getResidential_condition() {
		DwellingCondition dc = DwellingCondition.getEnum(this.residential_condition);
		return dc;
	}
	
	public void setResidential_condition(DwellingCondition dc) {
		this.residential_condition = dc.code;
	}
	
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
