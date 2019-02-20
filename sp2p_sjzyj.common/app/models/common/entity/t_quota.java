package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import daos.common.UserDao;
import daos.common.UserInfoDao;
import play.db.jpa.Model;

/**
 * 投资限额实体类
 * 
 * @author liuyang
 * @createDate 2018.01.23
 */
@Entity
public class t_quota extends Model {
	
	/** 用户id */
	public long user_id;
	
	/** 投资限额 */
	public double amount;
	
	/** 累计投资 */
	public double sum_invest;
	
	/** 类型 */
	public int type;
	
	/** 时间 */
	public Date time;
	
	@Transient
	public t_user_info users;
	
	public t_user_info getUsers() {
		
		UserInfoDao userInfoDao = common.utils.Factory.getDao(UserInfoDao.class);
		
		return userInfoDao.findByColumn("user_id=?", user_id);
	}

}
