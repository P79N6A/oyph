package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

import common.enums.NoticeScene;

/**
 * 实体:用户的消息设置
 *
 * @description 默认用户接收所有场景的所有消息，只有在用户首次取消时才回生成某条不接收的数据。<br>
 *              查询某个用户是否接收某个场景的消息时，只有在数据库中能找到对应场景，且值为false的情况下，才认为该用户不接收消息
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月18日
 */
@Entity
public class t_notice_setting_user extends Model {

	/** 创建时间 */
	public Date time ;
	
	/** 用户id */
	public Long user_id;
	
	/** 应用场景 */
	private int scene;

	/** 是否接收短信  */
	public boolean sms = true;
	
	/** 是否接收消息 */
	public boolean msg = true;
	
	/** 是否接收邮件 */
	public boolean email = true;

	public NoticeScene getScene() {
		NoticeScene se = NoticeScene.getEnum(this.scene);
		return se;
	}

	public void setScene(NoticeScene scene) {
		this.scene = scene.code;
	}

	@Override
	public String toString() {
		return "t_user_notice_setting [time=" + time + ", user_id=" + user_id
				+ ", scene=" + scene + ", sms=" + sms + ", msg=" + msg
				+ ", email=" + email + ", id=" + id + "]";
	}
	
}
