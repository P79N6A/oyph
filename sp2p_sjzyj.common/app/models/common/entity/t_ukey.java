package models.common.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_ukey extends Model {
	
	//加密锁编号
	public String ukey_sn;
	
	//加密锁ID
	public String ukey_id;
	
	//增强算法密钥
	public String ukey_ckey;
	
	//加密锁绑定管理员
	public String ukey_user;
	
	//加密锁状态：0 正常，1 挂失
	public int ukey_stat;

}
