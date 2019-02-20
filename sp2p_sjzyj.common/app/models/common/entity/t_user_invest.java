package models.common.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 用户资金当月待收表
 * @createDate 2018.11.5
 * */
@Entity
public class t_user_invest extends Model{
	
	/**user表id*/
	public Long user_id;
	
	/**当月待收金额*/
	public BigDecimal curr_total_invest_amounts;
	
	/**创建时间*/
	public Date time;
	
	/**待收时间(几月份的待收)*/
	public String invest_time;

}
