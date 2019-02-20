package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import common.enums.IsUse;

import play.db.jpa.Model;

/**
 * 分公司风采
 *
 * @author liuyang
 * @createDate 2017年4月20日
 */
@Entity
public class t_company_style extends Model {

	/** 名称 */
	public String city_name;
	
	/** 企业风貌图片路径 */
	public String enter_pic;
	
	/** 员工风采图片路径 */
	public String employee_pic;
	
	/** 创建时间 */
	public Date create_time;
	
	/** 0.下架  1上架 */
	public int status;
	
}
