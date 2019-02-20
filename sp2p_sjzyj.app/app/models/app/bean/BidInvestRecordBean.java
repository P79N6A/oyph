package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import common.enums.Client;
import common.utils.NameAsteriskUtil;

/***
 * 投标记录实体
 *
 * @description 
 *
 * @author luzhiwei
 * @createDate 2016-4-7
 */

@Entity
public class BidInvestRecordBean implements Serializable{
	
	@Id
	public long id;
	
	
	/** 用户名 */
	public String name;
	
	/** 投标金额 */
	public double amount;
	
	/** 投标端 */
	public int client;
	
	/** 投标时间 */
	public Date time;

	public String getClient() {
		Client client = Client.getEnum(this.client);
		
		return client == null ? null : client.value;
	}
	
	public Long getTime(){
		if(time==null){
			return null;
		}
		
		return time.getTime();
	}

	
	public String getName() {
		
		return NameAsteriskUtil.asterisk(this.name, 2, 4, 2);
	}
	
	
	
}
