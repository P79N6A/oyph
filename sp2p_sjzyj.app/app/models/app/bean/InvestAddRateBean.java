package models.app.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class InvestAddRateBean implements Serializable {
	
	@Id
	public long id;
	
	public double apr;
	
	public double amount;
	
}
