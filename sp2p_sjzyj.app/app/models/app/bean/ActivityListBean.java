package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class ActivityListBean implements Serializable{
	/** id*/
	@Id	
	public long id;
	
	/** 活动名称 */
	public String name;
	
	public Date start_time;
	
	@Transient
	public long time;   //得到秒数，Date类型的getTime()返回毫秒数
	/** 状态 2.未开始,3.进行中,4. 已结束*/
	public int status;
	/** 活动类型 1.大转盘,2.摇一摇*/
	public int type;
	
	public int maxNum;				//最大随机
}
