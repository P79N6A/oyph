package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;

/**
 * 平台前台主题
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月18日
 */
@Entity
public class t_theme extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 主题名称 */
	public String name;
	
	/** 主题文件夹名称 */
	public String folder;
	
	/** 主色系 */
	public String main_color;
	
	/** 辅色系 */
	public String aux_color;
	
	/** 第1张样例图片 */
	public String img1;
	
	/** 第2张样例图片 */
	public String img2;
	
	/** 第3张样例图片 */
	public String img3;
	
	/** 第4张样例图片 */
	public String img4;
	
	/** 是否含有首页小部件 */
	public boolean isWidget;
	
	/** 首页小部件名称(要求是js。如sd.js。则该值为sd。没有则不填) */
	public String widget;
	
	/** 对id进行验签加密后的字符串 */
	@Transient
	public String sign;
	
	public String getSign() {
		
		String signID = Security.addSign(id, Constants.THEME_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}

}
