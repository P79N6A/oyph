package models.proxy.bean;

import javax.persistence.Transient;

/**
 * 代理商/业务员收益
 * 
 * @author GuoShijie
 * @createDate 2018.01.23
 * */
public class Income {

	/**年化折算*/
	@Transient
	public Double convert;
	
	/**totalMoney = 投资金额 * 期限*/
	@Transient
	public Double totalMoney;
	
	/**投资总金额*/
	@Transient
	public Double sumMoney;
	
	/**推广理财总金额*/
	@Transient
	public Double total_Money;
	
	/**当月推广理财总金额*/
	@Transient
	public Double month_total_Money;
	
	/**提成*/
	@Transient
	public Double commission;
}
