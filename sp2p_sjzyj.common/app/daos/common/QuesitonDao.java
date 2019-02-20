package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.app.bean.QuestionDetailBean;
import models.app.bean.QuestionListBean;
import models.common.entity.t_question;

/**
 * 问题反馈Dao
 * 
 * @author niu
 * @createDate 2017.11.09
 */
public class QuesitonDao extends BaseDao<t_question> { /** ***************************************************************************** */

	protected QuesitonDao() {}
	
	/**
	 * 分页查询问题-根据问题状态
	 * 
	 * @param currPage    当前页码
	 * @param pageSize    显示条数
	 * @param status      问题状态（0、所有 1、待解决 2、已受理 3、已解决）
	 * @return
	 * 
	 * @author niu
	 * @createDate 2017.11.09
	 */
	public PageBean<QuestionListBean> pageOfQuestions(int currPage, int pageSize, int status, long userId) {
		
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(t.id) FROM t_question t WHERE 1 = 1 ");
		StringBuffer querySQL = new StringBuffer("SELECT t.id AS questionId, CASE t.type WHEN 1 THEN '功能异常：功能故障或不可用' WHEN 2 THEN '产品建议：用的不爽，我有建议' ELSE '其他问题' END AS questionType, t.question AS questionDescription, CASE t.status WHEN 1 THEN '已提交' WHEN 2 THEN '已查看' WHEN 3 THEN '已解决' END AS questionStatus FROM t_question t WHERE 1 = 1 ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		if (status > 0) {
			countSQL.append("AND t.status = :status ");
			querySQL.append("AND t.status = :status ");
			
			condition.put("status", status);
		}
		
		if (userId > 0) {
			countSQL.append("AND t.userid = :userId ");
			querySQL.append("AND t.userid = :userId ");
			
			condition.put("userId", userId);
		}

		querySQL.append("ORDER BY t.stime DESC");
		
		return this.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), QuestionListBean.class, condition);
	}
	
	/**
	 * 查询某一个问题 - 根据问题Id
	 * 
	 * @param quesitonId    问题Id
	 * @return
	 * 
	 * @author niu
	 * @createDate 2017.11.09
	 */
	public QuestionDetailBean findQuestionById(long questionId) {
		String sql = "SELECT t.id AS questionId, CASE t.type WHEN 1 THEN '功能异常：功能故障或不可用' WHEN 2 THEN '产品建议：用的不爽，我有建议' ELSE '其他问题' END AS questionType, t.question AS questionDescription, CASE t.status WHEN 1 THEN '已提交' WHEN 2 THEN '已查看' WHEN 3 THEN '已解决' END AS questionStatus, DATE_FORMAT(t.stime,'%Y/%m/%d') AS quizTime, DATE_FORMAT(t.etime,'%Y/%m/%d') AS solveTime, t.reason AS questionReason, t.image_path AS questionImage FROM t_question t WHERE t.id = :quesitonId";
		
		Map<String, Object> conditon = new HashMap<String, Object>();
		conditon.put("quesitonId", questionId);
		
		return this.findBeanBySQL(sql, QuestionDetailBean.class, conditon);
	}
	
	/**
	 * 保存问题
	 * 
	 * @param stime      问题提问时间
	 * @param question   问题
	 * @param image_path 问题图片路径
	 * @param type       问题类型
	 * @param status     问题状态
	 * @return
	 * 
	 * @author niu
	 * @createDate 2017.11.09
	 */
	public t_question saveQuesiton(Date stime, String question, String image_path, int type, int status, long userId) {
		
		t_question question1 = new t_question();
		
		question1.stime = stime;
		question1.question = question;
		question1.image_path = image_path;
		question1.type = type;
		question1.status = status;
		question1.userid = userId;
		
		return question1.save();
	}
	
	/**
	 * 修改问题
	 * 
	 * @param questionId  问题ID
	 * @param reason      问题原因
	 * @param status      问题状态
	 * @param etime       问题解决时间
	 * @param superid     问题解决人
	 * @return
	 * 
	 * @author niu
	 * @createDate 2017.11.09
	 */
	public t_question updateQuesiton(long questionId, String reason, int status, Date etime, long superid) {
		
		t_question quesiton = this.findByID(questionId);
		if (quesiton == null) {
			return null;
		}
		
		if (reason != null && !reason.equals("")) {
			quesiton.reason = reason;
		}
		
		if (status > 0) {
			quesiton.status = status;
		}
		
		if (etime != null) {
			quesiton.etime = etime;
		}
		
		if (superid > 0) {
			quesiton.superid = superid;
		}
		
		return quesiton.save();
	}
	
	
	
	
	
	
	
	
	
	
}

















