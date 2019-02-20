package models.common.entity;

import javax.persistence.Entity;
import javax.persistence.Transient;

import daos.common.UserFundDao;
import daos.common.UserInfoDao;
import play.db.jpa.Model;

/**
 * 担保人表
 *
 * @author liuyang
 * @createDate 2018年1月5日
 */
@Entity
public class t_guarantee extends Model {
	
	/** 用户id */
	public long user_id;
	
	/** 用户名称 */
	public String name;
	
	/** 代偿余额 */
	public double compensate_amount;
	
	@Transient
	public double amount;
	public double getAmount(){
		
		UserFundDao userFundDao = common.utils.Factory.getDao(UserFundDao.class);
		
		t_user_fund userFund = userFundDao.findByColumn("user_id=?", this.user_id);
		
		return userFund.balance;
		
	}
	
	@Transient
	public String mobile;
	public String getMobile(){
		
		UserInfoDao userInfoDao = common.utils.Factory.getDao(UserInfoDao.class);
		
		t_user_info userInfo = userInfoDao.findByColumn("user_id=?", this.user_id);
		
		return userInfo.mobile;
		
	}
	
}
