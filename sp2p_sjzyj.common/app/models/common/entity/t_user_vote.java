package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import daos.common.ParticipationDao;
import daos.common.UserDao;
import play.db.jpa.Model;

/**
 * 用户投票表
 *
 * @author liuyang
 * @createDate 2017年5月17日
 */
@Entity
public class t_user_vote extends Model {

	/** 用户id */
	public long user_id;
	
	/** 时间 */
	public Date time;
	
	/** 活动参与表id */
	public long participation_id;
	
	/** 活动id */
	public long activity_id;
	
	/** 活动参与者 */
	@Transient
	public t_participation part;
	
	public t_participation getPart(){
		ParticipationDao partDao = common.utils.Factory.getDao(ParticipationDao.class);
		t_participation parts = partDao.findByID(this.participation_id);
		if(parts!=null){
			return parts;
		}
		return null;
	}
	
	/** 用户 */
	@Transient
	public t_user users;
	
	public t_user getUsers() {
		UserDao userDao = common.utils.Factory.getDao(UserDao.class);
		t_user users = userDao.findByID(this.user_id);
		if(users!=null){
			return users;
		}
		return null;
	}
	
	
}
