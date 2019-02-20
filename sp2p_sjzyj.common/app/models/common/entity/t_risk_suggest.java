package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;
@Entity
public class t_risk_suggest extends Model{
	
	/**风控报告的id*/
	public long risk_id;
	
	/**经理意见*/
	public String suggest;
	
	/**审核时间*/
	public Date time;
	
	/**意见类型 1 风控专员意见 2 风控经意见*/
	public int type;
	
	/**意见人的Id*/
	public Long supervisorId;
	
	/**意见人的真实姓名*/
	@Transient 
	public String realName;
	public String getRealName(){
		if(this.supervisorId!=null){
			t_supervisor supervisor = t_supervisor.findById(this.supervisorId);
			if(supervisor==null){
				return null;
			}else{
				return supervisor.reality_name;
			}
		}else{
			return null;
		}
	}
}
