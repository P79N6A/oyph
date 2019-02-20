package models.common.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_jdecard extends Model{
	/**用户id*/
	public Long user_id;
	
	/** 用户名 */
	public String user_name;
	
	/** 联系电话 */
	public String mobile;
	
	/** 京东E卡面额 */
	public int denomination;
	
	/** 获奖方式 (0新人任务，1邀请新人)*/
	public int win_type;
	
	/** 京东E卡账号*/
	public String jd_number;
	
	/** 京东E卡密码*/
	public String jd_password;
	
	/** 短信发送状态(0未发送，1已发送) */
	public int grant_status;
	
	/** 中奖时间 */
	public Date time;
	
	/** 发送短信时间 */
	public Date sms_time;

	@Override
	public String toString() {
		return "t_jdecard [user_id=" + user_id + ", user_name=" + user_name + ", mobile=" + mobile + ", denomination="
				+ denomination + ", win_type=" + win_type + ", jd_number=" + jd_number + ", jd_password=" + jd_password
				+ ", grant_status=" + grant_status + ", time=" + time + ", sms_time=" + sms_time + "]";
	}		
}
