package models.proxy.bean;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SalesManUserBean {
	
	
	@Id
	public long id;				/** 主键  */
	
	public String userName;			/** 用户名  */
	
	public String userMobile;		/** 手机号  */
	
	public String realName;			/** 真实姓名  */
	
	public double balance;			/** 账户余额  */	
	
	public String extTime;			/** 推广时间  */
	
	public double monthAmount;		/** 当月理财总金额  */
	
	public double totalAmount;		/** 理财总金额  */
	
}
