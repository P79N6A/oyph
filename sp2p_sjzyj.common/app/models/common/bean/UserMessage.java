package models.common.bean;


import play.db.jpa.*;

import javax.persistence.*;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;

import java.util.*;

/**
 * 用户消息bean
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年1月11日
 */
@Entity
public class UserMessage {

	@Id
    @GeneratedValue
    public Long id;
	
	/** 创建时间 */
	public Date time;
	
	/** 管理员id，如果为0表示为系统自动发送的站内信 */
	public Long supervisor_id;
	
	/** 标题 */
	public String title;
	
	/** 消息内容 */
	public String content;

	/** 用户的id */
	public Long user_id;
	
	/** 是否已读*/
	public boolean is_read;
	
	/** 对id进行验签加密后的字符串 */
	@Transient
	public String sign;
	
	public String getSign() {
		
		String signID = Security.addSign(id, Constants.MSG_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
}
