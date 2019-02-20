package models.app.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class SaleManInfo implements Serializable {

	@Id
	public long id;
	
	public String realName;
	
	public String mobile;
	
	public int extCount;
	
	public int investCount;
	
	@Transient
	public String extLink;
	
}
