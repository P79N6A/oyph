package models.common.entity;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;

import play.db.jpa.Model;

/**
 * 信用等级表
 * 
 * @description
 *
 * @author ChenZhipeng
 * @createDate 2015年12月23日
 */
@Entity
public class t_credit_level extends Model {

	/** 信用等级名称 */
	public String name;

	/** 等级图标路径 */
	public String image_url;

	/** 图片分辨率 */
	public String image_resolution;

	/** 图片大小 */
	public String image_size;

	/** 文件格式 */
	public String image_format;

	/** 最低信用积分 */
	public int min_credit_score;

	/** 最高信用积分 */
	public int max_credit_score;

	/** 信用等级高低排序号。数字越大等级越高 */
	public int order_no;

	@Transient
	public String sign;

	public String getSign() {
		
		return Security.addSign(this.id, Constants.CREDITLEVEL_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
	}
	
}
