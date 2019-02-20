package models.app.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProxyAppBean implements Serializable {

	@Id
	public long id;
	
	public int saleManCount;
	
	public int investUserCount;
	
	public double curTotalInvest;
	
	public double curYearConvert;
	
	public double curProfit;
	
}
