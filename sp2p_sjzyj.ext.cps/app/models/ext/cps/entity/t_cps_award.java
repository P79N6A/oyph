package models.ext.cps.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:CPS奖励设置表
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年06月12日
 */
@Entity
public class t_cps_award extends Model {

	public long t_cps_id;
	
	public String name;
	
	public int num;
	
	public double amount;
	
	public String prize;
	
	public Date time;

	public long getT_cps_id() {
		return t_cps_id;
	}

	public void setT_cps_id(long t_cps_id) {
		this.t_cps_id = t_cps_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getPrize() {
		return prize;
	}

	public void setPrize(String prize) {
		this.prize = prize;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	
}
