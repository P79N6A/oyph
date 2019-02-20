package models.ext.cps.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:cps积分设置表
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年06月12日
 */
@Entity
public class t_cps_integral extends Model {

	public long t_cps_id;
	
	public int type;
	
	public int integral;
	
	public Date time;
}
