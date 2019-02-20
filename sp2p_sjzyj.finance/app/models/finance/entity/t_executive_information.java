package models.finance.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 
 *
 * @ClassName: t_executive_information
 *
 * @description 高管信息表
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
@Entity
public class t_executive_information extends Model {
	

	/**  证件类型   */
	public String certificate_type;
	
	/**  证件号码   */
	public String certificate_num;
	
	/**  姓名   */
	public String name;
	
	/**  职务   */
	public String position;
	
	/**  入职时间   */
	public Date entry_time;
	
	/**  学历   */
	public String education;
	
	/**  经验   */
	public String experience;
	
	/**  添加时间   */
	public Date time;
	
}
