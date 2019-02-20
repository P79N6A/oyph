package models.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 
 * 问题详情Bean
 * 
 * @author niu
 * @create 2017-11-14
 * 
 */
@Entity
public class QuestionDetailBean implements Serializable {
	
	@Id
	public long questionId;
	
	//问题类型
	public String questionType;
	
	//问题描述
	public String questionDescription;
	
	//问题状态
	public String questionStatus;
	
	//提问时间
	public String quizTime;
	
	//解决时间
	public String solveTime;
	
	//问题原因
	public String questionReason;	
	
	//图片路径
	public String questionImage;
	
	@Transient
	public List<Map<String, Object>> imageList;

	public List<Map<String, Object>> getImageList() {
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (questionImage == null || questionImage.trim().equals("")) {
			return null;
		}
		
		String[] strArr = questionImage.split(",");
		
		if (strArr.length <= 0) {
			return null;
		}
		
		for (String s : strArr) {
			if (s != null && !s.trim().equals("")) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("image", s);
				list.add(map);
			}
		}
		
		if (list.size() <= 0) {
			return null;
		}

		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
}
