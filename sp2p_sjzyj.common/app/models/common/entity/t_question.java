package models.common.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

/**
 * 问题反馈实体类
 * 
 * @author niu
 * @createDate 2017.11.09
 */
@Entity
public class t_question extends Model {

	/** 提问时间 */
	public Date stime;
	
	/** 问题 */
	public String question;
	
	/** 图片路径 */
	public String image_path;
	
	/** 问题原因 */
	public String reason;
	
	/** 问题类型 */
	public int type;
	
	/** 问题状态 */
	public int status;
	
	/** 问题解决时间 */
	public Date etime;
	
	/** 问题解决人 */
	public long superid;
	
	/** 问题提问人 */
	public long userid;
	
	
	@Transient
	public List<String> imageList;

	public List<String> getImageList() {
		
		List<String> list = new ArrayList<String>();
		if (image_path == null || image_path.trim().equals("")) {
			return null;
		}
		
		String[] strArr = image_path.split(",");
		
		if (strArr.length <= 0) {
			return null;
		}
		
		for (String s : strArr) {
			if (s != null && !s.trim().equals("")) {

				list.add(s);
			}
		}
		
		if (list.size() <= 0) {
			return null;
		}

		return list;
	}
	
	
	
}
