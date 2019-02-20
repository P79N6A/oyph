package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
/**
 * 
 *
 * @ClassName: t_loan_apply_info
 *
 * @description 前台借款申请信息实体
 *
 * @author LiuHangjing
 * @createDate 2018年12月18日-下午4:40:51
 */
@Entity
public class t_loan_apply_info extends Model{
	
	/**借款人姓名 */
	public String appli_name;
	
	/**借款人电话 */
	public String appli_phone;
	
	/**借款人性别 */
	public Integer appli_sex;
	
	/**借款人借款金额 */
	public double loan_amounts;
	
	/**借款人所在地*/
	public String appli_location;
	
	/**是否有抵押物*/
	public Integer is_havePawn;
	
	/**抵押物种类*/
	public Integer pawn_kind;
	
	/**状态*/
	public Integer appli_status;
	
	/**申请时间*/
	public Date appli_time;
	
	/**修改备注时间*/
	public Date update_time;
}
