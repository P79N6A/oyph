package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 贷款跟踪信息表
 *  
 * @description 
 *
 * @author liuyang
 * @createDate 2018年01月26日
 */

@Entity
public class t_loan_track extends Model {

	/** 标的id */
	public long bid_id;
	
	/** 标题 */
	public String title;
	
	/** 内容 */
	public String content;
	
	/** 时间 */
	public Date time;
}
