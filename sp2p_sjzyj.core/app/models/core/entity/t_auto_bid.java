package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/** 
 * 自动投标记录
 * 
 * @description 
 * 
 * @author ZhouYuanZeng 
 * @createDate 2016年3月25日 上午11:00:49  
 */
@Entity
public class t_auto_bid extends Model{

	/** 添加时间 */
	public Date time;
	
	/** 用户Id(投资人) */
	public long user_id;
	
	/** 借款标ID */
	public long bid_id;
}
