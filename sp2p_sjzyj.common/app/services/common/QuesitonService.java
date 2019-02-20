package services.common;

import java.sql.Timestamp;
import java.util.Date;

import common.utils.Factory;
import common.utils.Page;
import common.utils.PageBean;
import daos.common.QuesitonDao;
import models.app.bean.QuestionDetailBean;
import models.app.bean.QuestionListBean;
import models.common.entity.t_question;
import services.base.BaseService;

/**
 * 问题反馈业务类
 * 
 * @author niu
 * @createDate 2017.11.09
 */
public class QuesitonService extends BaseService<t_question> {

	protected QuesitonDao quesitonDao = Factory.getDao(QuesitonDao.class);
	
	protected QuesitonService() {
		super.basedao = quesitonDao;
	}
	
	/**
	 * 保存提问的问题
	 * 
	 * @param quesitonType  问题类型
	 * @param question      问题详情
	 * @param questionImage 问题图片路径
	 * 
	 * @return 保存成功返回true, 保存失败返回false.
	 * 
	 * @author niu
	 * @create 2017-11-14
	 */
	public boolean saveQuestion(int quesitonType, String questionIntroduce, String questionImagePath, long userId) {
		
		t_question question = quesitonDao.saveQuesiton(new Date(), questionIntroduce, questionImagePath, quesitonType, 1, userId);
		
		if (question != null) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 分页查询问题
	 * 
	 * @param currPage  当前页
	 * @param pageSize  显示条数
	 * @param status    问题状态
	 * 
	 * @return 返回一页问题列表
	 * 
	 * @author niu
	 * @create 2017-11-14
	 */
	public PageBean<QuestionListBean> pageOfQuesiton(int currPage, int pageSize, int status, long userId) {
		
		return quesitonDao.pageOfQuestions(currPage, pageSize, status, userId);
	}
	
	/**
	 * 查询问题详情（根据问题ID）
	 * 
	 * @param questionId  问题ID
	 * @return
	 * 
	 * @author niu
	 * @create 2017-11-14
	 */
	public QuestionDetailBean getQuestionDetails(long questionId) {
		
		return quesitonDao.findQuestionById(questionId);
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
	public boolean updateQuestion(long questionId, String reason, int status, Date etime, long superid) {
		
		t_question question = quesitonDao.updateQuesiton(questionId, reason, status, etime, superid);
		if (question == null) {
			return false;
		}
		
		return true;
	}
	
	
	
	
	
	
}
