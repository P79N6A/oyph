package models.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.IsUse;
import common.utils.Security;
import play.db.jpa.Model;

@Entity
public class t_wechat_group_sent extends Model {

	public String title;
	
	public int send_success;
	
	public int send_failure;
	
	public boolean is_use;
	
	public Date time;
	
	public int send_manner;
	
	public int type;
	
	public String mass_state;
	
	public long classify_id;
	
	@Transient
	public t_wechat_group_classify classify;
	
	public t_wechat_group_classify getClassify() {
		
		if(this.classify == null) {
			this.classify = t_wechat_group_classify.findById(this.classify_id);
		}
		return this.classify;
	}
	
	@Transient
	public String sign;

	public String getSign() {
		String signID = Security.addSign(id, Constants.INFORMATION_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSend_success() {
		return send_success;
	}

	public void setSend_success(int send_success) {
		this.send_success = send_success;
	}

	public int getSend_failure() {
		return send_failure;
	}

	public void setSend_failure(int send_failure) {
		this.send_failure = send_failure;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getSend_manner() {
		return send_manner;
	}

	public void setSend_manner(int send_manner) {
		this.send_manner = send_manner;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public IsUse getIs_use() {
		IsUse isUse = IsUse.getEnum(this.is_use);
		
		return isUse;
	}

	public void setIs_use(IsUse isUse) {
		this.is_use = isUse.code;
	}

	public String getMass_state() {
		return mass_state;
	}

	public void setMass_state(String mass_state) {
		this.mass_state = mass_state;
	}

	public long getClassify_id() {
		return classify_id;
	}

	public void setClassify_id(long classify_id) {
		this.classify_id = classify_id;
	}
	
	
	
}
