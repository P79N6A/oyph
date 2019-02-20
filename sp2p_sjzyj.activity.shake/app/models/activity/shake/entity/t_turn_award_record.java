package models.activity.shake.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 
 * @ClassName: t_turn_award_record
 *
 * @description 获奖记录表-实体类
 *
 * @author yaozijun
 *
 * @createDate 2018年10月25日
 */
@Entity
public class t_turn_award_record extends Model {
	/** 用户id */
	public Long user_id;
	/** 用户姓名 */
	public String user_name;
	/** 奖品id */
	public Long award_id;
	/** 0.5元话费数量 */
	public int onetel_count;
	
	/** 电话 */
	public String tel;
    /** 实物获奖者收货地址 */
	public String ship_address;
	/** 兑换状态(1,已领取，0未领取) */
	public Integer status;
	/** 到期状态(0正常未到期，1到期) */
	public Integer end_status;
	/** 是否发放  */
	public Integer is_grant;
	/** 时间获取的应该是中奖时的时间 */
	public Date time;
	/** 到期时间 */
	public Date end_time;
	
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public Long getAward_id() {
		return award_id;
	}
	public void setAward_id(Long award_id) {
		this.award_id = award_id;
	}
	public int getOnetel_count() {
		return onetel_count;
	}
	public void setOnetel_count(int onetel_count) {
		this.onetel_count = onetel_count;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getShip_address() {
		return ship_address;
	}
	public void setShip_address(String ship_address) {
		this.ship_address = ship_address;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getEnd_status() {
		return end_status;
	}
	public void setEnd_status(Integer end_status) {
		this.end_status = end_status;
	}
	public Integer getIs_grant() {
		return is_grant;
	}
	public void setIs_grant(Integer is_grant) {
		this.is_grant = is_grant;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Date getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}
	
	
}
