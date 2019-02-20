package models.ext.cps.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.ext.cps.entity.t_cps_activity;
import models.ext.cps.entity.t_cps_user;

/**
 * cps推广记录
 *
 * @author liudong
 * @createDate 2016年3月16日
 */
@Entity
public class CpsSpreadRecord {
	@Id
	public Long id;
	
	/** 创建时间 */
	public Date time;
	
	/** 会员编号 */
	public long user_id;
	
	/** 会员昵称(被推广人昵称) */
	public String user_name;
	
	/** 推广人昵称 */
	public String spreader_name;
	
	/** 累计理财 */
	public double total_invest;
	
	/** 累计返佣 */
	public double total_rebate;
	
	/** 活动名称 */
	@Transient
	public String activityName;
	
	public String getActivityName () {
		t_cps_user cpsUser = t_cps_user.findById(this.id);
		if (cpsUser != null ) {
			t_cps_activity cpsActivity = t_cps_activity.findById(cpsUser.activity_id);
			if (cpsActivity != null) {
				return cpsActivity.title;
			}
		}
		return null;
	}

}
