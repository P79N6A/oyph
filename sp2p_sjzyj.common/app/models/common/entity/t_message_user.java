package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:用户接收系统消息表
 *
 * @description 这张表只记录用户接收的状态,具体的消息内容需要关联到t_message表进行查看
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月18日
 */
@Entity
public class t_message_user extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 用户的id */
	public Long user_id;
	
	/** 消息的id */
	public Long message_id;
	
	/** 是否已读*/
	public boolean is_read;

}
