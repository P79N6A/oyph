package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.enums.IsUse;
import daos.common.SupervisorDao;
import play.db.jpa.Model;

/**
 * 投票活动表
 * @author Administrator
 *
 */
@Entity
public class t_vote_activity extends Model{

	/** 活动介绍 */
	public String activity_introduction;
	
	/** 开始时间 */
	public Date begin_time;
	
	/** 结束时间 */
	public Date end_time;
	
	/** 添加人id */
	public long supervisor_id;
	
	/** 活动标题 */
	public String title;
	
	/** is_use '0-下架\r\n1-上架'  */
	private boolean is_use;
	
	/** 顶部图 */
	public String img_url;
	
	/** 背景图 */
	public String background_url;
	
	/** 每天每人最多投票数 */
	public int vote_num;
	
	/** 照片要求 */
	public String pic_need;
	
	/** 访问量 */
	public int visit_num;
	
	/** 添加时间 */
	public Date create_time;
	
	public IsUse getIs_use() {
		IsUse isUse = IsUse.getEnum(this.is_use);
		
		return isUse;
	}

	public void setIs_use(IsUse isUse) {
		this.is_use = isUse.code;
	}
	
	@Transient
	public t_supervisor supervisor;
	public t_supervisor getSupervisor(){
	    
	    SupervisorDao supervisorDao = common.utils.Factory.getDao(SupervisorDao.class);
	    t_supervisor supervisor = supervisorDao.findByID(this.supervisor_id);
	    if(supervisor!=null){
	        return supervisor;
	    }
	    return null;
	}
}
