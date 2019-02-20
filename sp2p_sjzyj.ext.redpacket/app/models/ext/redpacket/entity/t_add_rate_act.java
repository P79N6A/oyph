package models.ext.redpacket.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

/**
 * 实体：加息券活动
 * 
 * @author niu
 * @create 2017.10.27
 */
@Entity
public class t_add_rate_act extends Model {

	
	/** 开始时间  */
	public Date stime;
	
	/** 结束时间  */
	public Date etime;
	
	/** 活动状态  */
	public int status;
	
	/** 活动名称  */
	public String name;
	
	@Transient
	public String sdate;
	
	@Transient
	public String edate;
	
	public String getSdate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(stime);
	}
	
	public String getEdate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(etime);
	}
}









