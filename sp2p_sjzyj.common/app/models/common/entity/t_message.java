package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 实体:系统消息表
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月18日
 */
@Entity
public class t_message extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 管理员id，如果为0表示为系统自动发送的站内信 */
	public Long supervisor_id;
	
	/** 标题 */
	public String title;
	
	/** 消息内容 */
	public String content;

	/** 对id进行验签加密后的字符串 */
	@Transient
	public String sign;
	
	public String getSign() {
		
		return Security.addSign(id, Constants.MSG_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}

	@Override
	public String toString() {
		return "t_message [time=" + time + ", supervisor_id=" + supervisor_id
				+ ", title=" + title + ", content=" + content + ", id=" + id
				+ "]";
	}
	
}
