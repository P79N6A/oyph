package models.activity.shake.entity;

import java.util.Date;

import javax.persistence.Entity;
import play.db.jpa.Model;
/**
 * 
 *
 * @ClassName: t_user_gold
 *
 * @description 用户金币表
 *
 * @author HanMeng
 * @createDate 2018年10月25日-上午10:41:48
 */
@Entity
public class t_user_gold extends  Model {
	
	/** 关联用户表id */
	public long user_id;
	
	/** 金币数量  */
	public int gold;
	
	/** 朋友圈分享次数  */
	public int share_num;
	
	/** 时间  */
	public Date Time;
}
