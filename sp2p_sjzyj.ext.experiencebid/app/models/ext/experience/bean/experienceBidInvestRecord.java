package models.ext.experience.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import common.enums.Client;

@Entity
public class experienceBidInvestRecord implements Serializable{

	@Id
	public long id;
	
	/** 投资人姓名 */
	public String user_name;
	
	/** 投资金额 */
	public double invest_amount;
	
	/** 投标时间 */
	public Date invest_time;
	
	/** 投资入口*/
	private int client;

	public Client getClient() {
		Client client = Client.getEnum(this.client);
		
		return client;
	}

	public void setClient(Client client) {
		this.client = client.code;
	}
}
