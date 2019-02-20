package controllers.back.risk;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_event_supervisor.Event;
import models.core.entity.t_audit_subject;

import org.apache.commons.lang.StringUtils;

import play.mvc.With;
import services.core.AuditSubjectService;

import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;

import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;

/**
 * 后台-风控-审核科目管理控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月18日
 */
@With(SubmitRepeat.class)
public class AuditSubjectMngCtrl extends BackBaseController {
	
	protected static AuditSubjectService auditSubjectService = Factory.getService(AuditSubjectService.class);
	
	/**
	 * 后台-风控-审核科目-审核科目列表
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void showAuditSubjectsPre(int currPage, int pageSize) {
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		
		PageBean<t_audit_subject> pageBean = auditSubjectService.pageOfAuditSubject(currPage, pageSize);
		
		render(pageBean);
	}
	
	/**
	 * 后台-风控-审核科目-审核科目列表-进入添加审核科目界面
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	@SubmitCheck
	public static void createAuditSubjectPre(String name, String description) {
		
		render(name, description);
	}
	
	/**
	 * 后台-风控-审核科目-审核科目列表-进入编辑审核科目界面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	@SubmitCheck
	public static void toEditAuditSubjectPre(long id) {
		
		if(id < 1){
			
			showAuditSubjectsPre(1, 10);
		}
		
		t_audit_subject tas = auditSubjectService.findAuditSubject(id);
		
		render(tas);
	}
	/**
	 * 保存审核科目
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	@SubmitOnly
	public static void createAuditSubject(String name, String description){
		checkAuthenticity();
		
		checkParams(name, description);
		
		ResultInfo result = auditSubjectService.createAuditSubject(name, description);
		if (result.code < 1) {
			flash.error("审核资料[%s]保存失败", name);
			
			createAuditSubjectPre(name, description);
		}
		t_audit_subject tas = (t_audit_subject) result.obj;
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", getCurrentSupervisorName());
		param.put("subject_id", ""+tas.id);
		param.put("subject_name", name);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.SUBJECT_ADD, param);
		
		if(!flag){
			LoggerUtil.error(true, "保存管理员事件失败");
			flash.error("保存管理员事件失败");
			
			createAuditSubjectPre(name, description);
		}
		
		flash.success("保存成功");
		showAuditSubjectsPre(1, 10);
	}
	
	/**
	 * 编辑审核科目
	 *
	 * @param id
	 * @param name
	 * @param description
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	@SubmitOnly
	public static void editAuditSubject(long id, String name, String description){
		checkAuthenticity();
		
		if (StringUtils.isBlank(description)) {
			flash.error("描述不能为空");
			
			showAuditSubjectsPre(1, 10);
		}
		if (id < 0) {
			
			showAuditSubjectsPre(1, 10);
		}
		
		boolean upd = auditSubjectService.updateAuditSubject(id, description);
		if(!upd){
			LoggerUtil.error(true, "编辑审核科目失败");
			flash.error("编辑审核科目失败");
			
			showAuditSubjectsPre(1, 10);
		}
		
		/** 添加管理员事件 */
		Map<String, String>param = new HashMap<String, String>();
		param.put("supervisor", getCurrentSupervisorName());
		param.put("subject_id", ""+id);
		param.put("subject_name", name);
		boolean flag = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.SUBJECT_EDIT, param);
		
		if(!flag){
			LoggerUtil.error(true, "保存管理员事件失败");
			flash.error("保存管理员事件失败");
			
			showAuditSubjectsPre(1, 10);
		}
		
		flash.success("编辑成功");
		showAuditSubjectsPre(1, 10);
	}
	
	/**
	 * 从createAuditSubject()和editAuditSubject()方法中抽取出来的代码块
	 *
	 * @param name
	 * @param description
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	private static void checkParams(String name, String description){
		
		if(StringUtils.isBlank(name) || name.length() > 10){
			flash.error("科目名称请控制在10位以内且不为空");
			
			createAuditSubjectPre(name, description);
		}
		
		if(StringUtils.isBlank(description) || description.length() > 30){
			flash.error("要求请控制在30位以内且不为空");
			
			createAuditSubjectPre(name, description);
		}
	}
}
