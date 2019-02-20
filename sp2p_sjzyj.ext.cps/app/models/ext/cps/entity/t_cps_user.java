package models.ext.cps.entity;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

/**
 * 实体:CPS推广记录
 *
 * @description 这个表不仅维护被推广人和推广人之间的关系，也维护着该被推广人和推广人之间的返佣以及理财折扣等相关信息
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月15日
 */
@Entity
public class t_cps_user extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 会员id(被推广人id，唯一索引) */
	public Long user_id;
	
	/** 推广人id */
	public Long spreader_id;
	
	/** 累计理财 */
	public double total_invest;
	
	/** 累计返佣 */
	public double total_rebate;
	
	/** 待领取返佣 */
	public double recivable_rebate;
	
	public long activity_id;
}
