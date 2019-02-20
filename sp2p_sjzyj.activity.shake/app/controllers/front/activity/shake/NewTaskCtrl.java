package controllers.front.activity.shake;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.shove.Convert;

import common.annotation.LoginCheck;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import controllers.common.BackBaseController;
import controllers.common.FrontBaseController;
import models.common.entity.t_jdecard;
import models.common.entity.t_user;

import services.common.JdecardService;
import services.common.UserService;
/**
 * 
 *
 * @ClassName: NewTaskCtrl
 *
 * @description 新手任务前台控制器
 *
 * @author LiuHangjing
 * @createDate 2019年2月19日-上午11:10:59
 */
public class NewTaskCtrl extends FrontBaseController {
	
	private static JdecardService jdecardService = Factory.getService(JdecardService.class);
	
	/**
	 * 
	 * @Title: newTaskListPre
	 *
	 * @description 分页显示所有个人中奖信息
	 *
	 * @param @param currPage
	 * @param @param pageSize 
	 * 
	 * @return void    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年2月18日
	 */
	@LoginCheck
	public static void newTaskListPre(int currPage,int pageSize) {
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 5;
		}
		/** 获取当前登陆人id */
		Long userId = getCurrUser().id;
		PageBean<t_jdecard> pageBean = jdecardService.pageOfNewTaskAll(userId,currPage,pageSize);
		
		render(pageBean);
	}
	/**
	 * 
	 * @Title: showNewTaskList
	 * 
	 * @description OPT=1316,app显示新手任务中奖记录
	 * @param parameters
	 * @return String    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年2月18日-下午6:02:24
	 */
	public static String showNewTaskList(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		
		if(!StrUtil.isNumeric(currentPageStr)||!StrUtil.isNumeric(pageNumStr)){
			 json.put("code",-1);
			 json.put("msg", "分页参数不正确");
			 
			 return json.toString();
		}
		int currPage = Convert.strToInt(currentPageStr, 1);
		int pageSize = Convert.strToInt(pageNumStr, 10);
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		long userId = Long.parseLong(result.obj.toString());
		PageBean<t_jdecard> pageBean = jdecardService.pageOfNewTaskAll(userId, currPage, pageSize);

		if (pageBean==null) {
			json.put("code", -1);
			json.put("msg", "查询失败");
			json.put("newTask", null);
			
			return json.toString();
		}
		json.put("code",1);
		json.put("msg", "查询成功");
		json.put("newTask", pageBean.page);
		
		return json.toString();
	}

}
