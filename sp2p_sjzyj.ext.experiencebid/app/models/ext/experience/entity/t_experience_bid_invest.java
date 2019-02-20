package models.ext.experience.entity;

import java.util.Date;

import javax.persistence.Entity;

import common.enums.Client;

import play.db.jpa.Model;

@Entity
public class t_experience_bid_invest extends Model{
	
	/** 投标时间 */
	public Date time;
	
	/** 用户ID */
	public long user_id;
	
	/** 体验标ID */
	public long bid_id;
	
	/** 投标金额 */
	public double amount;
	
	/** 预收益 */
	public double income;
	
	/** 投资登记入口 */
	private int client;
	
	public Client getClient() {
		Client client = Client.getEnum(this.client);
		
		return client;
	}

	public void setClient(Client client) {
		this.client = client.code;
	}
}
