package models.common.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_transfer_notice extends Model{

	/**转让通知人姓名 */
	public String notice_name;
	
	/**转让通知人电话 */
	public String notice_mobile;
	
	/**是否通知0：通知，1：不通知 */
	public Integer notice_type;
	
	/**通知类型 0，转让通知，1，还款通知*/
	public Integer notice_temp;
	
}
