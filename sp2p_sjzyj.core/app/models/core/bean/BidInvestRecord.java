package models.core.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import models.core.entity.t_invest.InvestType;

import common.enums.Client;
import common.utils.DateUtil;

@Entity
public class BidInvestRecord implements Serializable{

	@Id
	/** t_invest的id */
	public long id;
	
	/** 对应的标的id */
	public long bid_id;
	
	/** 投资人昵称*/
	public String name;
	
	/** 登记投资入口 */
	public int client;
	
	/** 投资时间 */
	public Date time;
	
	public String getTime() {
		return DateUtil.getTimeLine(this.time);
	}
	
	public Client getClient() {
		Client client = Client.getEnum(this.client);
		
		return client;
	}

	public void setClient(Client client) {
		this.client = client.code;
	}
	
	/** 投资金额  */
	public double amount;
	
	/** 投标方式 */
	public int invest_type;
	
	public InvestType getInvest_type() {
		InvestType invest_type = InvestType.getEnum(this.invest_type);
		
		return invest_type;
	}

	public void setInvest_type(InvestType invest_type) {
		this.invest_type = invest_type.code;
	}
	
}
