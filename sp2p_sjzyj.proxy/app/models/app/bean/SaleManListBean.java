package models.app.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SaleManListBean implements Serializable {
	
	@Id
	public long id;
	
	public String realName;
	
	public String addTime;
	
	public String mobile;
}
