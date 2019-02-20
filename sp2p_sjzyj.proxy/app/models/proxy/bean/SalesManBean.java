package models.proxy.bean;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SalesManBean {
	
	@Id
	public long id;
	
	public String sale_name;			/** 业务员姓名  */
	
	public String sale_mobile;			/** 业务员手机号  */
	
	
}
