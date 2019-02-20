package models.common.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import models.common.entity.t_event_supervisor.Item;

/**
 * 管理员操作日志记录(t_event_supervisor)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月24日
 */
@Entity
public class SupervisorEventLog {

	@Id
	public Long id;
	
	/** 操作时间 */
	public Date time; 

	/** 管理员ID */
	public long supervisor_id;
	
	/** ip */
	public String ip;
	
	/** 操作类型 */
	private int item;
	
	/** 描述 */
	public String description;

	/** 管理员的名称 */
	public String supervisor_name;
	
	public Item getItem() {
		
		return Item.getEnum(this.item);
	}
	
}
