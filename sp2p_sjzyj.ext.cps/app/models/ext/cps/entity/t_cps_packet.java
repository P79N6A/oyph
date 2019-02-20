package models.ext.cps.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:CPS红包设置表
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年06月12日
 */
@Entity
public class t_cps_packet extends Model {

	public long t_cps_id;
	
	public int type;
	
	public double money;
	
	public int allotte_day;
	
	public double condition_money;
	
	public Date time;
	
	public int status;
	
	public int lock_day;
	
	public int getLock_day() {
		return lock_day;
	}

	public void setLock_day(int lock_day) {
		this.lock_day = lock_day;
	}

	public long getT_cps_id() {
		return t_cps_id;
	}

	public void setT_cps_id(long t_cps_id) {
		this.t_cps_id = t_cps_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public int getAllotte_day() {
		return allotte_day;
	}

	public void setAllotte_day(int allotte_day) {
		this.allotte_day = allotte_day;
	}

	public double getCondition_money() {
		return condition_money;
	}

	public void setCondition_money(double condition_money) {
		this.condition_money = condition_money;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
	
	
}
