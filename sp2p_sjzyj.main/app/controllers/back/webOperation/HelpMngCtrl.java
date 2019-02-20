package controllers.back.webOperation;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_event_supervisor;
import models.common.entity.t_help_center;
import models.common.entity.t_help_center.Column;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import play.mvc.With;
import services.common.HelpCenterService;

import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;

import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;

/**
 * 后台-运维-帮助中心控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With(SubmitRepeat.class)
public class HelpMngCtrl extends BackBaseController {
	
	/** 注入帮助中心Service */
	protected static HelpCenterService helpCenterService = Factory.getService(HelpCenterService.class);

	/**
	 * 后台-运维-帮助中心 
	 * 帮助中心 列表查询
	 * @rightID 206001
	 * 
	 * @param showType 栏目
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param helpTitle 问题标题
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void showHelpsPre(int showType,int currPage,int pageSize){
		/* 搜索条件 */
		String helpTitle = params.get("helpTitle");
		
		PageBean<t_help_center> page = helpCenterService.pageOfHelpCenterBack(currPage, pageSize, Column.getEnum(showType), helpTitle);

		render(page,showType,helpTitle);
	}
	
	/**
	 * 后台-运维-帮助中心-添加帮助
	 *  进入添加页面 
	 * @rightID 206002
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	@SubmitCheck
	public static void toAddHelpPre(){
		
		//回显数据用到的处理方式
		t_help_center help = (t_help_center) Cache.get(CacheKey.HELP_+session.getId());
		if (help != null) {
			renderArgs.put("help", help);
			Cache.delete(CacheKey.HELP_+session.getId());
		}
		
		render();
	}
	
	
	/**
	 * 添加确认
	 * @rightID 206002
	 *
	 * @param columnNo 栏目
	 * @param help 帮助中心
	 * @param orderTime 排序时间 
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2016年1月9日
	 */
	@SubmitOnly
	public static void addHelp(int columnNo,t_help_center help,String orderTime){
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		/** 参数校验  */
		result=isCheckHelp(columnNo, help, orderTime);
		if(result.code < 1){
			flash.error(result.msg);
			
			addCacheFlash(columnNo, help, orderTime);
			toAddHelpPre();
		}
			
		help.order_time = DateUtil.strToDate(orderTime,  Constants.DATE_PLUGIN);
		help.setColumn(Column.getEnum(columnNo));
		
		boolean addFlag = helpCenterService.addHelpCenter(help);
		if(!addFlag){
			flash.error("添加失败！");
			toAddHelpPre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("help_id", help.id.toString());  
		supervisorEventParam.put("help_name", help.title);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.HELP_ADD, supervisorEventParam);
		
		flash.success("添加成功");
		showHelpsPre(0, 1, 10);
	}
	
	/**
	 * 后台-运维-帮助中心-编辑帮助
	 * 进入编辑页面
	 * @rightID 206003
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void toEditHelpPre(long id){
		t_help_center help = helpCenterService.findByID(id);
		if(help==null){
			
			error404();
		}
		render(help);
	}
	
	/**
	 * 编辑确认
	 * @rightID 206003
	 *
	 * @param sign 帮助中心加密id
	 * @param columnNo 栏目 
	 * @param title 标题
	 * @param content 内容答案
	 * @param orderTime 排序时间
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月9日
	 */							
	public static void editHelp(String sign,int columnNo,t_help_center help,String orderTime){
		ResultInfo result = new ResultInfo();
		
		result = Security.decodeSign(sign, Constants.HELPCENTER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error404();
		}
		long helpId = Long.parseLong((String) result.obj);
		
		result = isCheckHelp(columnNo, help, orderTime);
		if(result.code < 1){
			flash.error("栏目设置错误！");
			toEditHelpPre(helpId);
		}
		
		boolean editFlag = helpCenterService.upadteHelpCenter(helpId, t_help_center.Column.getEnum(columnNo),
				help.title, help.content, DateUtil.strToDate(orderTime, Constants.DATE_PLUGIN));
		if(!editFlag){
			flash.error("编辑失败");
			toEditHelpPre(helpId);
		}
		
		long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("help_id", helpId+"");  
		supervisorEventParam.put("help_name", help.title);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.HELP_EDIT, supervisorEventParam);

		flash.success("编辑成功");
		showHelpsPre(0, 1, 10);
	}
	
	/**
	 * 上下架
	 * @rightID 206004
	 *
	 * @param sign 帮助中心加密id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月9日
	 */
	public static void helpCenterIsUse(String sign){
		ResultInfo result = Security.decodeSign(sign, Constants.HELPCENTER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		long helpId = Long.parseLong((String) result.obj);
		t_help_center help = helpCenterService.findByID(helpId);
		boolean isUseFlag = helpCenterService.upadteHelpCenterIsUse(helpId, !help.getIs_use().code);
		if(!isUseFlag){
			result.code=-1;
			result.msg="上下架操作失败";
			renderJSON(result);
		}
		
		/** 添加管理员事件 */
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("help_id", help.id.toString());  
		supervisorEventParam.put("help_name", help.title);	
		if(help.getIs_use().code){
			supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.HELP_DISABLED, supervisorEventParam);
		}else{
			supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.HELP_ENABLE, supervisorEventParam);

		}
		
		result.code=1;
		result.msg="上下架操作成功";
		result.obj=help.getIs_use().code;
		
		renderJSON(result);
	}
	
	/**
	 * 参数校验 
	 * 
	 * @param help 内容
	 * @param columnNo 栏目
	 * @param showTime 显示时间
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2016年3月21日
	 */
	public static ResultInfo isCheckHelp(int columnNo,t_help_center help,String orderTime){
		ResultInfo result = new ResultInfo();
		
		if(t_help_center.Column.getEnum(columnNo) == null){
			result.code=-1;
			result.msg="栏目设置错误";
			
			return result;
		}
		
		if(StringUtils.isBlank(help.title)){
			result.code=-1;
			result.msg="标题不能为空";
			
			return result;
		}
		
		if(StringUtils.isBlank(help.content)){
			result.code=-1;
			result.msg="答案不能为空";
			
			return result;
		}
		
		if(StringUtils.isBlank(orderTime)){
			result.code=-1;
			result.msg="排序时间不能为空";
			
			return result;
		}
		
		result.code=1;
		result.msg="";
		
		return result;
	}
	
	/**
	 * 将内容添加到 flash中
	 * 
	 * @param help 内容
	 * @param columnNo 栏目
	 * @param showTime 显示时间
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2016年3月21日
	 */
	private static void addCacheFlash(int columnNo,t_help_center help,String orderTime){
		Cache.set(CacheKey.HELP_+session.getId(), help, Constants.CACHE_TIME_SECOND_60);
		flash.put("columnNo", columnNo);
		flash.put("orderTime", orderTime);
	}
}
