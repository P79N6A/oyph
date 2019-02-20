package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
@Entity
public class t_risk_pic extends Model{
	
	/**风控报告的id*/
	public long risk_id;
	
	/**图片路径*/
	public String pic_path;
	
	/**图片类型 1.报告图片 2.征信图片 3.经营状况图片 4.现地址居住图片*/
	public Integer type;
	
	/**图片上传时间*/
	public Date time;

	public long getRisk_id() {
		return risk_id;
	}

	public void setRisk_id(long risk_id) {
		this.risk_id = risk_id;
	}

	public String getPic_path() {
		return pic_path;
	}

	public void setPic_path(String pic_path) {
		this.pic_path = pic_path;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "t_risk_pic [risk_id=" + risk_id + ", pic_path=" + pic_path + ", type=" + type + ", time=" + time + "]";
	}
	
	
}
