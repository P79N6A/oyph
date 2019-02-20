package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

/**
 * 客服月业绩表
 * @author liuyang
 *
 */
@Entity
public class t_service_month extends Model {

	public long service_id;
	
	public int year;
	
	public int month;
	
	public double collect_money;
	
	public Date time;
	
	public double add_money;
	
	public int service_num;
	
	public int open_num;
	
	@Transient
	public t_service_person person;
	
	public t_service_person getPerson() {
		t_service_person per = t_service_person.findById(this.service_id);
		if(per == null) {
			return null;
		}
		
		return per;
	}

	public long getService_id() {
		return service_id;
	}

	public void setService_id(long service_id) {
		this.service_id = service_id;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public double getCollect_money() {
		return collect_money;
	}

	public void setCollect_money(double collect_money) {
		this.collect_money = collect_money;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public double getAdd_money() {
		return add_money;
	}

	public void setAdd_money(double add_money) {
		this.add_money = add_money;
	}

	public int getService_num() {
		return service_num;
	}

	public void setService_num(int service_num) {
		this.service_num = service_num;
	}

	public int getOpen_num() {
		return open_num;
	}

	public void setOpen_num(int open_num) {
		this.open_num = open_num;
	}
	
	
	
	
}
