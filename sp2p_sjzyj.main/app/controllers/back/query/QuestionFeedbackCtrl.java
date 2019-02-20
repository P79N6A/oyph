package controllers.back.query;

import java.util.Date;

import common.utils.Factory;
import common.utils.PageBean;
import controllers.common.BackBaseController;
import models.app.bean.QuestionDetailBean;
import models.app.bean.QuestionListBean;
import models.common.bean.CurrSupervisor;
import models.common.entity.t_question;
import models.common.entity.t_supervisor;
import models.common.entity.t_user_info;
import services.common.QuesitonService;
import services.common.SupervisorService;
import services.common.UserInfoService;

/**
 * 
 * 控制类 - 问题反馈
 * 
 * @author niu
 * @create 2017-11-16
 * 
 */
public class QuestionFeedbackCtrl extends BackBaseController {
	
	protected static QuesitonService     quesitonService = Factory.getService(QuesitonService.class);
	protected static UserInfoService     userInfoService = Factory.getService(UserInfoService.class);
	protected static SupervisorService supervisorService = Factory.getService(SupervisorService.class);
	
	/**
	 * 后台 - 查询 - 问题反馈列表
	 * 
	 * @author niu
	 * @create 2017-11-16
	 */
	public static void questionListPre(int currPage,int pageSize, int status) {
		
		renderArgs.put("status", status);
		PageBean<QuestionListBean> page = quesitonService.pageOfQuesiton(currPage, pageSize, status, 0);
		
		render(page);
	}
	
	/**
	 * 后台 - 查询 - 问题反馈 - 问题详情
	 * 
	 * @author niu
	 * @create 2017-11-16
	 */
	public static void questionDetailsPre(long questionId) {

		if (questionId <= 0) {
			questionListPre(1, 10, 0);
		}
	
		//查询问题
		t_question question = quesitonService.findByID(questionId);
		
		t_user_info userInfo = null;
		t_supervisor supervisor = null;
		
		if (question != null) {
			
			if (question.status == 1) {
				//修改问题状态：已提交 -> 已查看
				quesitonService.updateQuestion(questionId, "", 2, null, 0);
			}
			
			//查询用户信息
			userInfo = userInfoService.findUserInfoByUserId(question.userid);
			//查询管理员信息
			supervisor = supervisorService.findByID(question.superid);
		}
		
		render(question, userInfo, supervisor);
	}
	
	/**
	 * 后台 - 查询 - 问题反馈 - 问题详情 - 问题修改
	 * 
	 * @author niu
	 * @create 2017-11-16
	 */
	public static void updateQuestion(long questionId, String reason) {
		
		if (questionId <= 0) {
			questionListPre(1, 10, 2);
		}
		
		CurrSupervisor supervisor = getCurrSupervisor();
		
		if (supervisor == null) {
			flash.error("获取管理员信息失败");
			questionDetailsPre(questionId);
		}
		
		if (reason == null || reason.trim().equals("")) {
			flash.error("请输入问题原因");
			questionDetailsPre(questionId);
		}
		
		boolean updateSuccess = quesitonService.updateQuestion(questionId, reason, 3, new Date(), supervisor.id);
		
		if (updateSuccess) {
			flash.success("问题已解决");
			questionDetailsPre(questionId);
		}
		
		flash.success("问题修改失败");
		questionDetailsPre(questionId);
	}
	
}
