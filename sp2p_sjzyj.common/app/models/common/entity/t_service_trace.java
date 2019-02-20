package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

/**
 * 用户追踪表
 * @author liuyang
 *
 */
@Entity
public class t_service_trace extends Model {

	public long user_id;
	
	public long service_id;
	
	public String content;
	
	public Date time;
	
	public String reality_name;
	
	@Transient
	public String name;
	
	public String getName() {
		t_user user = t_user.findById(this.user_id);
		if(user == null) {
			return null;
		}
		return user.name;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getService_id() {
		return service_id;
	}

	public void setService_id(long service_id) {
		this.service_id = service_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getReality_name() {
		return reality_name;
	}

	public void setReality_name(String reality_name) {
		this.reality_name = reality_name;
	}
	
	
	
}
