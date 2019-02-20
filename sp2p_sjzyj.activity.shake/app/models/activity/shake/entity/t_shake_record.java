package models.activity.shake.entity;

import java.util.Date;

import javax.persistence.Entity;
import play.db.jpa.Model;

@Entity
public class t_shake_record extends Model {
	
	/** 奖项类型：1、红包 2、加息卷 3、积分 ,5、一等奖,6、二等奖，7、三等奖，8、纪念奖*/
	public int type;		
	
	/** 奖项大小 */
	public int amount;
	
	/** 奖品名称*/
	public String prize_name;
	
	/** 用户id */
	public long user_id;	
	
	/** 中奖号码 */
	public int number;		
	
	/** 活动Id */
	public long activity_id; 
	
	/** 中奖人 */
	public String user_name; 
	
	/** 中奖人手机号 */
	public String user_mobile;
	
	/** 使用条件 */
	public double use_rule; 
	
	/** 获奖时间 */
	public Date create_time ;
	
	/** 分享次数 */
	public int share_count; 
	
	/** 获取次数 */
	public int gain_count; 
	
	/** 获得分享红包的用户Id */
	public String gain_user;

	@Override
	public String toString() {
		return "t_shake_record [type=" + type + ", amount=" + amount + ", prize_name=" + prize_name + ", user_id="
				+ user_id + ", number=" + number + ", activity_id=" + activity_id + ", user_name=" + user_name
				+ ", user_mobile=" + user_mobile + ", use_rule=" + use_rule + ", create_time=" + create_time
				+ ", share_count=" + share_count + ", gain_count=" + gain_count + ", gain_user=" + gain_user + "]";
	} 

	

	
	
}
