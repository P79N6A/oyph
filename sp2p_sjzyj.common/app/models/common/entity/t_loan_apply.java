package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.enums.ApplicationType;
import daos.common.UserDao;
import daos.common.UserInfoDao;
import play.db.jpa.Model;

/**
 * 贷款申请信息表
 *  
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月27日
 */

@Entity
public class t_loan_apply extends Model {

	/** 用户ID(取t_users表的ID) */
	public long user_id;
	
	/** 标的id */
	public long bid_id;
	
	/** 贷款申请类型 */
	public int type;
	
	public ApplicationType getType() {
		ApplicationType type = ApplicationType.getEnum(this.type);
		return type;
	}
	
	public void setType(ApplicationType type) {
		this.type = type.code;
	}
	
	/** 贷款申请金额 */
	public double amount;
	
	/** 贷款申请月数 */
	public double period;
	
	/** 贷款申请时间 */
	public Date time;
	
	/** 贷款申请状态 */
	public int status;
	
	/** 业务订单号 */
	public String service_order_no;
	
	/** 发生地点 */
	public int site;
	
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
