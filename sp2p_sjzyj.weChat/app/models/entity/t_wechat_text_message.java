package models.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.IsUse;
import common.utils.Security;
import play.db.jpa.Model;

@Entity
public class t_wechat_text_message extends Model {

	public String keywords;
	
	public String content;
	
	public int matchs;
	
	/** is_use '0-下架\r\n1-上架'  */
	private boolean is_use;
	
	public Date time;
	
	@Transient
	public String sign;

	public String getSign() {
		String signID = Security.addSign(id, Constants.INFORMATION_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getMatchs() {
		return matchs;
	}

	public void setMatchs(int matchs) {
		this.matchs = matchs;
	}

	public IsUse getIs_use() {
		IsUse isUse = IsUse.getEnum(this.is_use);
		
		return isUse;
	}

	public void setIs_use(IsUse isUse) {
		this.is_use = isUse.code;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	
}
