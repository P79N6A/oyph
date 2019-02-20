package models.ext.cps.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import models.common.entity.t_user_info;
import play.db.jpa.Model;

/**
 * 实体:CPS活动中奖记录表
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年06月12日
 */
@Entity
public class t_cps_award_record extends Model {

	public long cps_award_id;
	
	public long user_id;
	
	public Date time;
	
	public long cps_activity_id;
	
	@Transient
	public int num;
	
	public int getNum() {
		t_cps_award award = t_cps_award.findById(this.cps_award_id);
		
		if(award!=null){
			return award.num;
		}else{
			return 0;
		}
	}
	
	@Transient
	public String activityName;
	
	public String getActivityName() {
		t_cps_activity act = t_cps_activity.findById(cps_activity_id);
		if(act!=null){
			return act.title;
		}else{
			return "";
		}
	}
	
	@Transient
	public String awardName;
	
	public String getAwardName() {
		t_cps_award award = t_cps_award.findById(this.cps_award_id);
		
		if(award!=null){
			return award.prize;
		}else{
			return "";
		}
	}
	
	@Transient
	public t_user_info userInfo;
	
	public t_user_info getUserInfo() {
		
		t_user_info info = t_user_info.find("user_id=?", this.user_id).first();
		
		if(info!=null){
			return info;
		}else{
			return null;
		}
	}

	public long getCps_activity_id() {
		return cps_activity_id;
	}

	public void setCps_activity_id(long cps_activity_id) {
		this.cps_activity_id = cps_activity_id;
	}

	public long getCps_award_id() {
		return cps_award_id;
	}

	public void setCps_award_id(long cps_award_id) {
		this.cps_award_id = cps_award_id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	
}
