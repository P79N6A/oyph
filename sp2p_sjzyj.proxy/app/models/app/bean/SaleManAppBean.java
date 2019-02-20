package models.app.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class SaleManAppBean implements Serializable{
	
	@Id
	public long id;
	
	public int type;
	
	public String realName;
	
	public String mobile;
	
	@Transient
	public String extLink;
	
	public long proxyId;
	
}
