package models.app.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProxyProfitBean implements Serializable {

	
	@Id
	public long id;
	
	public String profitTime;
	
	public double investAmount;
	
	public double yearConvert;
	
	public double profit;
	
}
