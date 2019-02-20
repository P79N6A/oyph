package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 上上签用户表
 * 
 * @author LiuPengwei
 * @createDate 2018年4月18日10:21:20
 *
 */

@Entity
public class t_ssq_user extends Model{

	/** 用户id */
	public long user_id;
	
	/** 上上签用户名 */
	public String account;
	
	/** 用户证书编号 */
	public String seal ; 
	
	/** 证书处理状态   0：处理中 ；1：成功 ； -1：失败 */
	public int application_status;
}
