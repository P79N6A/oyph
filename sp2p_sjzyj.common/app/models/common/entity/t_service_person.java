package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

/**
 * 客服表
 * @author liuyang
 *
 */
@Entity
public class t_service_person extends Model {

	public long supervisor_id;
	
	public Date time;

	public long getSupervisor_id() {
		return supervisor_id;
	}

	public void setSupervisor_id(long supervisor_id) {
		this.supervisor_id = supervisor_id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	@Transient
	public String realityName;
	
	public String getRealityName() {
		t_supervisor sup = t_supervisor.findById(this.supervisor_id);
		if(sup != null) {
			return sup.reality_name;
		}
		return null;
	}
	
	
}
