package models.app.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 
 * 问题列表Bean
 * 
 * @author niu
 * @create 2017-11-14
 * 
 */
@Entity
public class QuestionListBean implements Serializable {

	//问题主键
	@Id
	public long questionId;
	
	//问题类型
	public String questionType;
	
	//问题描述
	public String questionDescription;
	
	//问题状态
	public String questionStatus;
	
}
