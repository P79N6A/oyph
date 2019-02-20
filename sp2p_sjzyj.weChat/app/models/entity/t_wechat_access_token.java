package models.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_wechat_access_token extends Model {
	
	public String token;
	
	public int expire_in;
	
	public Date time;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getExpire_in() {
		return expire_in;
	}

	public void setExpire_in(int expire_in) {
		this.expire_in = expire_in;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	
}
