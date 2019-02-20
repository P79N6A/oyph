package models.app.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.IsUse;
import common.utils.Security;

@Entity
public class InformationBean implements Serializable{

	@Id
	public Long id;
	
	/** 添加时间 */
	public Date time;
	
	/** 栏目key(取自栏目表) */
	public String column_key;
	
	/** 标题 */
	public String title;
	
	/** 来源  */
	public String source_from;
	
	/** 关键字  */
	public String keywords;
	
	/** 内容 */
	public String content;
	
	/** 排序时间 */
	public Date order_time;
	
	/** 查看次数  */
	public int read_count;
	
	/** 点赞次数  */
	public int support_count;
	
	/** 图片路径 */
	public String image_url;
	
	/** 图片分辨率 */
	public String image_resolution;
	
	/** 文件大小 */
	public String image_size;
	
	/** 文件格式  */
	public String image_format;
	
	/** 发布时间  */
	public Date show_time;
	
	/** is_use '0-下架\r\n1-上架'  */
	private boolean is_use;
	
	
	@Transient
	public String informationIdSign;
	public String getInformationIdSign() {
		
		return Security.addSign(this.id, Constants.INFORMATION_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
	}
	
	public IsUse getIs_use() {
		IsUse isUse = IsUse.getEnum(this.is_use);
		
		return isUse;
	}

	public void setIs_use(IsUse isUse) {
		this.is_use = isUse.code;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "InformationBean [id=" + id + ", time=" + time + ", column_key=" + column_key + ", title=" + title
				+ ", source_from=" + source_from + ", keywords=" + keywords + ", content=" + content + ", order_time="
				+ order_time + ", read_count=" + read_count + ", support_count=" + support_count + ", image_url="
				+ image_url + ", image_resolution=" + image_resolution + ", image_size=" + image_size
				+ ", image_format=" + image_format + ", show_time=" + show_time + ", is_use=" + is_use
				+ ", informationIdSign=" + informationIdSign + "]";
	}
	
}
