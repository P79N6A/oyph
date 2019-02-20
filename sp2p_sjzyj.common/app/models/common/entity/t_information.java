package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.IsUse;
import common.enums.PactType;
import common.utils.Security;

/**
 * 实体 资讯类
 *
 * @author liudong
 * @createDate 2015年12月28日
 */
@Entity
public class t_information extends Model {

	/**官方公告模板id*/
	public long template_official_notice_id;
	
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
	
	/** 栏目key 名称 */
	@Transient
	public String column_name;

	@Transient
	public String sign;

	public String getSign() {
		String signID = Security.addSign(id, Constants.INFORMATION_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
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

}
