package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 实体： 优选照片主题
 *
 * @author LiuPengwei
 * @createDate 2018年1月3日
 */
@Entity
public class t_select_theme extends Model {
	
	/** 创建时间 */
	public Date time;
	
	/** 排序时间轴 */
	public Date order_time;
	
	/** 主题名称 */
	public String name;
	
	/** 照片路径 */
	public String image_url;
	
	/** 照片分辨率 */
	public String image_resolution;
	
	/** 照片大小  */
	public String image_size;
	
	/** 照片格式 */
	public String image_format;
	
	/** 浏览次数 */
	public int read_count;
	
	/** 横图路径 */
	public String code_url;
	
	/** 横图分辨率 */
	public String code_resolution;
	
	/** 横图大小  */
	public String code_size;
	
	/** 横图格式 */
	public String code_format;
	
		
}
