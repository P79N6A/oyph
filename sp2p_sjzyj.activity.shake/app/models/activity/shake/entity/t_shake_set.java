package models.activity.shake.entity;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 * 实体类：摇一摇活动奖项以及中奖率设置
 * 
 * @author niu
 * @create 2017-12-07
 */
@Entity
public class t_shake_set extends Model {

	/** 奖项类型：1、红包 2、加息卷 3、积分 4、中奖率 5，一等奖，6，二等奖，7，三等奖，8纪念奖*/
	public int type; 			
	
	/** 奖项大小 */
	public int amount;
	
	/** 奖品名称 */
	public String prize_name; 
	
	/** 奖品数量 */
	public int number; 		
	
	/** 摇一摇活动 id */
	public long activity_id;
	
	/** 奖项使用规则 */
	public double use_rule;	
	
	
}
