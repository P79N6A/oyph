package controllers.back.setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.bean.CurrSupervisor;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_event_supervisor.Event;
import models.common.entity.t_right_supervisor;
import models.common.entity.t_supervisor;
import models.common.entity.t_supervisor.LockStatus;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import play.mvc.With;

import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;

import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;

/**
 * 后台-设置-权限管理控制器
 *
 * @description
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
@With(SubmitRepeat.class)
public class RightMngCtrl extends BackBaseController {
	
	/**
	 * 后台-设置-权限管理
	 * 
	 * @rightID 804001
	 *
	 * @param currPage
	 * @param pageSize
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月24日
	 */
	public static void showAllSupervisorsPre(int currPage, int pageSize, int status) {
		
		String userName = params.get("userName");
		
		PageBean<t_supervisor> page = supervisorService.pageOfAllSupervisors( currPage, pageSize, status, userName);

		render(page, status, userName);
	}

	/**
	 * 后台-设置-权限管理-添加管理员-(1.进入添加管理员页面)
	 *
	 * @rightID 804002
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月24日
	 */
	@SubmitCheck
	public static void toAddSupervisorPre() {

		render();
	}

	/**
	 * 后台-设置-权限管理-分页查询所有的管理员-添加管理员
	 *
	 * @rightID 804002
	 * 
	 * @param supervisor 管理员用户名/真实姓名/手机/密码
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月24日
	 */
	@SubmitOnly
	public static void addSupervisor(t_supervisor supervisor,int type) {
		checkAuthenticity();
		
		if (!valid_addSupervisor(supervisor)) {
			// 验证失败
			toAddSupervisorPre();
		}
		
		supervisor.password = com.shove.security.Encrypt.MD5(supervisor.password + ConfConst.ENCRYPTION_KEY_MD5);
		ResultInfo result = supervisorService.addSupervisor(supervisor);
		if (result.code < 1) {
			flash.error(result.msg);
			
			//添加管理员失败
			toAddSupervisorPre();
		}
		//业务经理为0,添加推广码
		t_supervisor supervisors = (t_supervisor) result.obj;
		if(type==0 || type == 4){
			String code ="T"+supervisors.id ;
			 
			result =supervisorService.updateSupervisorExtension(supervisors.id, code);
			
			if (result.code < 1) {
					flash.error(result.msg);
					
					//添加管理员失败
					toAddSupervisorPre();
				}
		}
		
		//4为普通管理员,不用插进团队表
		if(type!=3){
			if(teamService.save(supervisors.id,0, type)<0){
				flash.error("添加管理员失败!");
				//添加管理员失败
				toAddSupervisorPre();
			}
		}
		
		long supervisorId = getCurrentSupervisorId();

		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("manager_id", supervisor.id.toString());
		supervisorEventParam.put("manager_name", supervisor.reality_name);

		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.RIGHT_ADD_SUPERVISOR, supervisorEventParam);
		
		flash.success("添加管理员成功");
		
		showAllSupervisorsPre(0, 10, 0);
	}	
	
	/**
	 * 后台-设置-权限管理-编辑管理员-(1.进入编辑页面)
	 *
	 * @rightID 804003
	 * 
	 * @param sign
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	public static void toEditSupervisorPre(String sign) {
		ResultInfo result = Security.decodeSign(sign, Constants.SUPERVISOR_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error(result.msg);

			showAllSupervisorsPre(0, 10, 0);
		}

		long supervisorId = Long.parseLong((String) result.obj);
		t_supervisor supervisor = supervisorService.findByID(supervisorId);
		if (supervisor == null) {
			flash.error("用户不存在!");
			showAllSupervisorsPre(0, 10, 0);
		}

		render(supervisor, sign);
	}

	/**
	 * 设置-权限管理-编辑管理员-(2.编辑管理员手机号)
	 *
	 * @rightID 804003
	 * 
	 * @param sign
	 * @param mobile 管理员更新后的手机号码
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	public static void editSupervisorMobile(String sign, String mobile) {

		ResultInfo result = Security.decodeSign(sign, Constants.SUPERVISOR_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			flash.error(result.msg);
			showAllSupervisorsPre(0, 10, 0);
		}

		long supervisorId = Long.parseLong((String) result.obj);
		result = supervisorService.updateSupervisorMobile(supervisorId, mobile);
		if (result.code < 1) {

			flash.error("手机号码没有更新成功，请稍后重试");
			toEditSupervisorPre(sign);
		}

		long loginSupervisorId = getCurrentSupervisorId();
		t_supervisor supervisor = supervisorService.findByID(supervisorId);

		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("manager_id", supervisor.id.toString());
		supervisorEventParam.put("manager_name", supervisor.name);

		supervisorService.addSupervisorEvent(loginSupervisorId, t_event_supervisor.Event.RIGHT_EDIT_SUPERVISOR, supervisorEventParam);

		flash.success("编辑管理员成功");
		
		showAllSupervisorsPre(0, 10, 0);
	}

	/**
	 * 后台-设置-权限管理-编辑管理员-(3.编辑管理员登录密码)
	 * 
	 * @rightID 804003
	 *
	 * @param sign 
	 * @param newPassword 管理员新的密码
	 * @param rePassword 重复密码
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	public static void editSupervisorPassword(String sign, String newPassword, String rePassword) {
		ResultInfo result = Security.decodeSign(sign, Constants.SUPERVISOR_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			flash.error(result.msg);
			showAllSupervisorsPre(0, 10, 0);;
		}
		
		if (!valid_editSupervisorPassword(newPassword, rePassword)) {

			toEditSupervisorPre(sign);
		}
		
		long supervisorId = Long.parseLong((String) result.obj);
		result = supervisorService.updateSupervisorPassword( supervisorId, com.shove.security.Encrypt.MD5(newPassword + ConfConst.ENCRYPTION_KEY_MD5));
		if (result.code < 1) {
		
			flash.error(result.msg);
			
			toEditSupervisorPre(sign);
		}
		
		long loginSupervisorId = getCurrentSupervisorId();
		t_supervisor supervisor = supervisorService.findByID(supervisorId);

		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("manager_id", supervisor.id.toString());
		supervisorEventParam.put("manager_name", supervisor.name);

		supervisorService.addSupervisorEvent(loginSupervisorId, t_event_supervisor.Event.RIGHT_EDIT_SUPERVISOR, supervisorEventParam);

		flash.success("编辑管理员成功");
		
		showAllSupervisorsPre(0, 10, 0);
	}
	
	/**
	 * 后台-设置-权限管理-解锁/锁定管理员
	 * 
	 * @rightID 804004
	 *
	 * @param sign
	 * @param flag true:解锁;false:锁定
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月25日
	 */
	public static void lockAUnlockSupervisor(String sign, boolean flag) {
		ResultInfo result = Security.decodeSign(sign, Constants.SUPERVISOR_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		
		long supervisorId = Long.parseLong((String) result.obj);
		long loginSupervisorId = getCurrentSupervisorId();
		if (supervisorId == loginSupervisorId) {
			result.code = 0;
			result.msg = "请勿对自己执行该操作!";

			renderJSON(result);
		}
		if (flag) {
			boolean isUnlock = supervisorService.unlockSupervisor(supervisorId);
			if (!isUnlock) {
				result.code = -1;
				result.msg = "解锁管理员失败";

				renderJSON(result);
			}

			result.code = 1;
			result.msg = "解锁管理员成功";
		} else {
			boolean isLock = supervisorService.lockSupervisor(supervisorId);

			CurrSupervisor currSupervisor = (CurrSupervisor) Cache.get(CacheKey.LOGIN_SUPERVISOR + supervisorId);
			if(currSupervisor != null){
				currSupervisor.setLock_status(LockStatus.STATUS_2_LOCKEDBYSYS);
				Cache.safeSet(CacheKey.LOGIN_SUPERVISOR + currSupervisor.id, currSupervisor, Constants.CACHE_TIME_MINUS_30);
			}
			
			if (!isLock) {
				result.code = -1;
				result.msg = "锁定管理员失败";

				renderJSON(result);
			}

			result.code = 1;
			result.msg = "锁定管理员成功";
		}

		t_supervisor supervisor = supervisorService.findByID(supervisorId);
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("manager_id", supervisor.id.toString());
		supervisorEventParam.put("manager_name", supervisor.name);

		t_event_supervisor.Event event = null;
		if (flag) {
			event = t_event_supervisor.Event.RIGHT_UNLOCK_SUPERVISOR;
		} else {
			event = t_event_supervisor.Event.RIGHT_LOCK_SUPERVISOR;
		}
		supervisorService.addSupervisorEvent(loginSupervisorId, event,supervisorEventParam);

		renderJSON(result);
	}

	
	/**
	 * 设置-权限管理-权限分配-(1.进入分配界面)
	 *
	 * @rightID 804005
	 * 
	 * @param supervisorSign
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月24日
	 */
	public static void toRightMngPre(String supervisorSign){
		ResultInfo result = Security.decodeSign(supervisorSign, Constants.SUPERVISOR_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error(result.msg);
			
			showAllSupervisorsPre(0, 10, 0);
		}
		
		long supervisorId = Long.parseLong((String) result.obj);
		Map<Long,t_right_supervisor> rights = rightService.queryRightsOfUser(supervisorId);
		
		t_supervisor supervisor = supervisorService.findByID(supervisorId);
		
		render(rights,supervisor);
	}
	
	/**
	 * 后台-设置-权限管理-权限分配-(2.权限分配)
	 * 
	 * @rightID 804005
	 *
	 * @param supervisorSign
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月8日
	 */
	public static void rightAssignment(String supervisorSign){
		ResultInfo result = Security.decodeSign(supervisorSign, Constants.SUPERVISOR_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error(result.msg);
			
			showAllSupervisorsPre(0, 10, 0);
		}
		long supervisorId = Long.parseLong((String) result.obj);
		Map<Long,t_right_supervisor> formerRights = rightService.queryRightsOfUser(supervisorId);//原来的权限
		
		String[] newRights =  params.getAll("rights");//最终的权限
		
		Map<Long, Boolean> finalRights = new HashMap<Long, Boolean>();//原来有的，现在没有的需要删掉。现在有的，原来没有的需要添加。现在有的，原来也有不做改动
		
		if (formerRights != null && formerRights.size() > 0) {
			for (Long actionId : formerRights.keySet()) {
				finalRights.put(actionId, false);
			}
		}		
		
		if (newRights != null && newRights.length > 0) {
			for (String actionId : newRights) {
				finalRights.put(Long.valueOf(actionId), true);
			}
		}
		
		if (!rightService.updateRightsOfUser(supervisorId, finalRights)) {
			LoggerUtil.error(true, "权限分配时出错，数据回滚");

			flash.error("权限分配失败");
			toRightMngPre(supervisorSign);
		}
		
		CurrSupervisor loginSupervisor = (CurrSupervisor) Cache.get(CacheKey.LOGIN_SUPERVISOR + supervisorId);
		if(loginSupervisor != null){
			List<Long> rights = rightService.queryRightsOfSupervisor(supervisorId);
			loginSupervisor.rights = rights;
			Cache.safeSet(CacheKey.LOGIN_SUPERVISOR + loginSupervisor.id, loginSupervisor, Constants.CACHE_TIME_MINUS_30);
		}
		
		CurrSupervisor currSupervisor = getCurrSupervisor();
		//管理员事件
		Map<String, String> eventParam = new HashMap<String, String>();
		eventParam.put("manager_id", supervisorId+"");
		t_supervisor manager = supervisorService.findByID(supervisorId);
		eventParam.put("manager_name", manager.reality_name);
		supervisorService.addSupervisorEvent(currSupervisor.id, Event.RIGHT_ASSIGN_SUPERVISOR, eventParam);
		
		flash.success("权限分配成功!");
		showAllSupervisorsPre(0, 10, 0);
	}
	
	/** addSupervisor()的数据校验方法 */
	private static boolean valid_addSupervisor(t_supervisor supervisor){
		if (!StrUtil.isValidUsername(supervisor.name, 3, 10)) {
			flash.error("用户名输入有误");

			return false;
		}
		
		if (supervisorService.isNameExists(supervisor.name)) {
			flash.error("该用户名已经存在，请重新输入!");

			return false;
		}
		
		if (StringUtils.isBlank(supervisor.reality_name) || supervisor.reality_name.length()<2 || supervisor.reality_name.length()>15) {
			flash.error("真实姓名输入有误");

			return false;
		}
		
		if (!StrUtil.isMobileNum(supervisor.mobile)) {
			flash.error("手机号码输入有误!");

			return false;
		}
		
		if (supervisorService.isMobileExists(supervisor.mobile)) {
			flash.error("该手机号码已经被使用，请重新输入!");

			return false;
		}
		
		if (!StrUtil.isValidPassword(supervisor.password, 6, 15)) {
			flash.error("密码输入有误!");

			return false;
		}

		return true;
	}
	

	/** editSupervisorPassword()的数据校验方法 */
	private static boolean valid_editSupervisorPassword( String newPassword, String rePassword){

		if (!StrUtil.isValidPassword(newPassword, 6, 15)) {
			flash.error("新密码密码输入有误!");

			return false;
		}
		
		if (!newPassword.equals(rePassword)) {
			flash.error("两次输入的密码不一致!");

			return false;
		}
		
		return true;
	}
}
