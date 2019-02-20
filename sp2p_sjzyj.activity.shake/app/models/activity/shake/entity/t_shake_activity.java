package models.activity.shake.entity;

import java.util.Date;
import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 * 实体类：摇一摇活动
 * 
 * @author niu
 * @create 2017-12-07
 */
@Entity
public class t_shake_activity extends Model {

	/** 活动名称 */
	public String name;		
	
	/** 活动开始时间 */
	public Date stime;		

	/** 活动持续时间，单位：分钟 */
	public int ctime;		
	
	/** 活动状态：1、未设奖项 2、未开始 3、进行中 4、已结束 */
	public int status;		
	
	/** 中奖率 */
	public int winrate;	
	
	/** 预估参与人数 */
	public int number;		
	
	/** 摇一摇时间 */
	public int shake_time;	
	
	/** 奖项总数量 */
	public int prize_count; 
	
	/** 活动描述 */
	public String description;
	
	/** 摇一摇次数 */
	public int shake_count; 
	
	/** 中奖次数 */
	public int win_count;
	
	/**摇一摇最大随机数*/
	public int random_one; 
	
	/**中奖最大随机数*/
	public int random_two;  
	
	/**中奖号码*/
	public int win_num; 
	
}
