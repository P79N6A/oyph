package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.NoticeScene;
import common.enums.NoticeType;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 实体:系统消息模板(只对应sms,msg,email)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月19日
 */
@Entity
public class t_template_notice extends Model {

	public Date time;
	
	/** 
	 * 模板类型
	 * 1-短信模板；
	 * 2-消息模板
	 * 3-邮件模板
	 */
	private int type=1;
	
	/** 应用场景 */
	private int scene;
	
	/** 标题 */
	public String title;
	
	/** 内容 */
	public String content;
	
	@Transient
	public String sign;
	public String getSign () {
		
		return Security.addSign(this.id, Constants.NOTEMP_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}

	public NoticeType getType() {
		NoticeType noticeType = NoticeType.getEnum(this.type);
		return noticeType;
	}

	public NoticeScene getScene() {
		NoticeScene noticeScene = NoticeScene.getEnum(this.scene);
		return noticeScene;
	}

	public void setType(NoticeType type) {
		this.type = type.code;
	}

	public void setScene(NoticeScene scene) {
		this.scene = scene.code;
	}

	@Override
	public String toString() {
		return "t_template_notice [time=" + time + ", type=" + type
				+ ", scene=" + scene + ", title=" + title + ", content="
				+ content + ", id=" + id + "]";
	}
	
}
