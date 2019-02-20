package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import models.common.entity.t_supervisor.LockStatus;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Target;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 合作伙伴
 *
 * @author liudong
 * @createDate 2015年12月28日
 */
@Entity
public class t_partner extends Model {
	
	/** 添加时间 */
	public Date time;
	
	/** 名称 */
	public String name;
	
	/** 排序时间 */
	public Date order_time;
	
	/** 图片路径 */
	public String image_url;
	
	/** 图片分辨率 */
	public String image_resolution;
	
	/** 文件大小 */
	public String image_size;
	
	/** 文件格式 */
	public String image_format;
	
	/** 合作伙伴链接 */
	public String url;
	
	/** 链接打开方式  1-_self 2-_blank*/
	private int target;
	
	@Transient
	public String sign;

	public String getSign() {
		String signID = Security.addSign(id, Constants.PARTNER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
	public Target getTargets() {
		Target target = Target.getEnum(this.target);
		
		return target;
	}

	public void setTargets(Target target) {
		this.target = target.code;
	}

}
