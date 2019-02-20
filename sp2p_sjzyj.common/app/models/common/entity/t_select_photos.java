package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;
import play.db.jpa.Model;

/**
 * 实体： 优选照片
 *
 * @author LiuPengwei
 * @createDate 2018年1月3日
 */
@Entity
public class t_select_photos extends Model {
	
	/** 创建时间 */
	public Date time;
	
	/** 排序时间 */
	public String order_times;
	
	/** 照片主题Id */
	public long theme_id;
	
	/** 照片路径 */
	public String image_url;
	
	/** 照片分辨率 */
	public String image_resolution;
	
	/** 照片大小  */
	public String image_size;
	
	/** 照片格式 */
	public String image_format;
	
	/** 照片序号 */
	public int orders;

	@Override
	public String toString() {
		return "t_select_photos [time=" + time + ", order_times=" + order_times + ", theme_id=" + theme_id
				+ ", image_url=" + image_url + ", image_resolution=" + image_resolution + ", image_size=" + image_size
				+ ", image_format=" + image_format + ", orders=" + orders + "]";
	}

	

	
		
}
