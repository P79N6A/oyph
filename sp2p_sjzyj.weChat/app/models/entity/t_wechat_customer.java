package models.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import models.common.entity.t_supervisor;
import play.db.jpa.Model;

@Entity
public class t_wechat_customer extends Model {

	/**完整客服帐号，格式为：帐号前缀@公众号微信号*/
	public String kf_account;
	
	/**客服昵称*/
	public String kf_nick;
	
	/**客服编号*/
	public Integer kf_id;
	
	/**如果客服帐号已绑定了客服人员微信号， 则此处显示微信号*/
	public String kf_wx;
	
	/**创建时间*/
	public Date time;
	
	/**t_supervisor 表 id*/
	public Long supervisor_id;
	
	/**真实姓名*/
	@Transient
	public String realName;
	public String getRealName() {
		String real = null;
		t_supervisor supervisor = t_supervisor.findById(this.supervisor_id);
		if (supervisor != null) {
			real = supervisor.reality_name;
		}
		return real;
	}
	
	public String getKf_account() {
		return kf_account;
	}

	public void setKf_account(String kf_account) {
		this.kf_account = kf_account;
	}

	public String getKf_nick() {
		return kf_nick;
	}

	public void setKf_nick(String kf_nick) {
		this.kf_nick = kf_nick;
	}

	public Integer getKf_id() {
		return kf_id;
	}

	public void setKf_id(Integer kf_id) {
		this.kf_id = kf_id;
	}

	public String getKf_wx() {
		return kf_wx;
	}

	public void setKf_wx(String kf_wx) {
		this.kf_wx = kf_wx;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public void setSupervisor_id(Long supervisor_id) {
		this.supervisor_id = supervisor_id;
	}
	
	public Long getSupervisor_id() {
		return supervisor_id;
	}
	
	public enum KfStatus {
		WAITING(1,"等待确认","waiting"),
		REJECTED(2,"被拒绝","rejected"),
		EXPIRED(3,"过期","expired");
		
		public Integer code;
		public String des;
		public String dess;
		
		private KfStatus (Integer code,String des,String dess) {
			this.code = code;
			this.des = des;
			this.dess = dess;
		}
		
		public static KfStatus getEnum(Integer code) {
			KfStatus[] kf = KfStatus.values();
			for (KfStatus kfStatus : kf) {
				if (kfStatus.code == code) {
					return kfStatus;
				}
			}
			return null;
		}
		
		public static KfStatus getEnum(String dess) {
			KfStatus[] kf = KfStatus.values();
			for (KfStatus kfStatus : kf) {
				if (kfStatus.dess.equals(dess)) {
					return kfStatus;
				}
			}
			return null;
		}
	}
}
