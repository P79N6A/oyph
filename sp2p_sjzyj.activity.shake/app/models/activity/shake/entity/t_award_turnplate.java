package models.activity.shake.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_award_turnplate extends Model{
	/**	商品名称 */
	private String award_name;
	/**	中奖概率(百分比) */
	private BigDecimal prob;
	/**	时间 */
	private Date time;
	public String getAward_name() {
		return award_name;
	}
	public void setAward_name(String award_name) {
		this.award_name = award_name;
	}
	public BigDecimal getProb() {
		return prob;
	}
	public void setProb(BigDecimal prob) {
		this.prob = prob;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	
}
