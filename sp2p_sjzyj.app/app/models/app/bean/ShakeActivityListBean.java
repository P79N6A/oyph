package models.app.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ShakeActivityListBean implements Serializable {
	
	@Id
	public long activityId;		//活动Id
	
	public String title;			//活动标题
		
	public String description;		//活动描述
	
	public int status;				//活动状态
	
	public int maxNum;				//最大随机
	
}
