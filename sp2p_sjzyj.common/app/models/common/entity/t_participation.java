package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import daos.common.ParticipationDao;
import daos.common.UserDao;
import play.db.jpa.Model;
import services.common.ParticipationService;
import services.common.UserService;

/**
 * 活动参与表
 *
 * @author liuyang
 * @createDate 2017年5月17日
 */
@Entity
public class t_participation extends Model {

	/** 名称 */
	public String name;
	
	/** 用户id */
	public long user_id;
	
	/** 口号 */
	public String slogan;
	
	/** 照片描述 */
	public String description;
	
	/** 票数 */
	public int poll_num;
	
	/** 图片路径 */
	public String img_url;
	
	/** 添加时间 */
	public Date time;
	
	/** 审核状态(0.待审核1.通过2.未通过) */
	public int status;
	
	/** 审核人id */
	public long supervisor_id;
	
	/** 审核时间 */
	public Date verifier_time;
	
	/** 审核意见 */
	public String verifier_idea;
	
	/** 活动id */
	public long activity_id;
	
	public Status getStatus(){
	    return Status.getEnum(this.status);
	}
	public enum Status {
		
		CHECK_PENDING(0,"待审核"),
		PASS(1,"通过"),
		NOT_PASS(2,"未通过");
		
		/** 状态 */
		public int code;
		
		/** 描述 */
		public String value;
		
		private Status(int code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static Status getEnum(int code){
			Status[] Statuss = Status.values();
			for(Status sta:Statuss){
				if(sta.code == code){
					
					return sta;
				}
			}
			
			return null;
		}
		
	}
	
	/** 当前排名 */
	@Transient
	public long orderNum;
	public long getOrderNum(){
	    ParticipationService participationDao = common.utils.Factory.getService(ParticipationService.class);
	    
	    return participationDao.queryUserOrder(this.id);
	}
	
	/** 参与人 */
	@Transient
	public t_user user;
	public t_user getUser(){
	    
	    UserService userService = common.utils.Factory.getService(UserService.class);
	    
	    t_user user = userService.findByID(this.user_id);
	    
	    return user;
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

