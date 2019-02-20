package models.common.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import services.common.UserInfoService;

/**
 * 理财客户查询列表
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2018年07月10日
 */
@Entity
public class UserInfoServices {
	
	@Id
	public long id;
	
	public String name;
	
	public String mobile;
	
	public Date time;
	
	public String reality_name;
	
	public int service_type;
	
	public double collectMoney;
	
}
