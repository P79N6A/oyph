package models.ext.redpacket.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AddRateTicketBean {
	
	@Id
	public long id;
	
	/** 利率  */
	public double apr;
	
	/** 有效期  */
	public int day;
	
	/** 使用条件：大于  */
	public double amount;
	
	/** 加息券状态  */
	public int status;
	
	/** 有效期至  */
	public String todate;
}
