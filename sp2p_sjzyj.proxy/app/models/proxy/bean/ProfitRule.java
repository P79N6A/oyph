package models.proxy.bean;

import javax.persistence.Transient;

/**
 * 业务员提成规则
 * 
 * @author GuoShijie
 * @createDate 2018.01.23
 * */
public class ProfitRule {
	
	@Transient
	public int minAmount;
	
	@Transient
	public int maxAmount;
	
	@Transient
	public double amount;

}
