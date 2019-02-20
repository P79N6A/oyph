package models.bean;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class UserFundCheck {
	
	@Id
	public long id;
	
	//用户名
	public String userName;
	
	//托管账户
	public String account;
	
	//本地账户可用金额
	public double systemBlance;
	
	//本地账户冻结金额
	public double systemFreeze;
	
	//第三方账户可用金额
	@Transient
	public double pBlance;
	
	//第三方账户冻结金额
	@Transient
	public double pFreeze;
	
	//状态，0正常，1异常
	@Transient
	public int status;
	
	public int getStatus(){
		if (compareBlance()){
			this.status = 0;
			
		}
		else{
			this.status = 1;
		}
		return this.status;
	}
	
	public boolean compareBlance(){
		if (Double.compare(this.systemBlance, this.pBlance) == 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean compareFreeze(){
		if (Double.compare(this.systemFreeze, this.pFreeze) == 0){
			return true;
		}
		else{
			return false;
		}
	}
}
