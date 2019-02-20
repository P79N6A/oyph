package models.common.entity;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;
import services.common.VoteActivityService;

/**
 * 投票活动奖品规则表
 * @author Administrator
 *
 */
@Entity
public class t_bank_quota extends Model{

	/**  银行名称*/
	public String bank_name;
	
	/**  银行单笔限额*/
	public int single_quota;
	
	/**  银行单日限额*/
	public int day_quota;

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	public int getSingle_quota() {
		return single_quota;
	}

	public void setSingle_quota(int single_quota) {
		this.single_quota = single_quota;
	}

	public int getDay_quota() {
		return day_quota;
	}

	public void setDay_quota(int day_quota) {
		this.day_quota = day_quota;
	}
	
	
	
}
