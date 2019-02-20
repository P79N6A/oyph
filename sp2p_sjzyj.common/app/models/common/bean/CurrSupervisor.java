package models.common.bean;

import java.io.Serializable;
import java.util.List;

import models.common.entity.t_supervisor.LockStatus;

/**
 * 当前登录管理员的基本信息
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月1日
 */
public class CurrSupervisor implements Serializable {

	/** 用户ID */
	public long id;
	
	/** 用户名称 */
	public String name;
	
	/** 用户真实姓名 */
	public String reality_name;
	
	/** 用户的所有权限,key:权限id */
	public List<Long> rights;
	
	/** 锁定状态 */
	private int lock_status;
	
	public LockStatus getLock_status() {
		LockStatus status = LockStatus.getEnum(this.lock_status);
		
		return status;
	}

	public void setLock_status(LockStatus lock_status) {
		this.lock_status = lock_status.code;
	}
}
