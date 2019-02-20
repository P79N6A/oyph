package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 
 * 资料审核库
 * @description 
 *
 * @author Songjia
 * @createDate 2016年3月31日
 */
@Entity
public class t_audit_item_library extends Model{
	
	/** 创建时间 */
	public Date time = new Date();
	
	/** 图片名称 */
	public String name;
	
	/** 图片路径 */
	public String url;
	
	/** 用户ID */
	public long user_id;
	
}
