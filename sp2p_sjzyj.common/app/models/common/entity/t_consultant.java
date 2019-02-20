package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 实体： 理财顾问
 *
 * @author liudong
 * @createDate 2015年12月28日
 */
@Entity
public class t_consultant extends Model {
	
	/** 创建时间 */
	public Date time;
	
	/** 排序时间 */
	public Date order_time;
	
	/** 姓名 */
	public String name;
	
	/** 头像路径 */
	public String image_url;
	
	/** 头像分辨率 */
	public String image_resolution;
	
	/** 头像大小  */
	public String image_size;
	
	/** 头像格式 */
	public String image_format;
	
	/** 二维码路径 */
	public String code_url;
	
	/** 二维码分辨率 */
	public String code_resolution;
	
	/** 二维码大小 */
	public String code_size;
	
	/** 二维码格式 */
	public String code_format;
	
	@Transient
	public String sign;

	public String getSign() {
		String signID = Security.addSign(id, Constants.CONSULTANT_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
}
