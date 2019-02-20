package controllers.back;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.bean.ShowUserInfo;
import models.common.bean.UserDetail;
import models.common.bean.UserFundStatistic;
import models.common.entity.t_event_supervisor.Event;
import models.common.entity.t_notice_setting_user;
import models.common.entity.t_supervisor;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.proxy.entity.t_proxy_salesman;
import models.proxy.entity.t_proxy_user;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import play.mvc.Scope.Session;
import services.common.NoticeService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.proxy.SalesManService;
import services.proxy.SalesManUserService;

import com.shove.Convert;

import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.NoticeScene;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;
import controllers.front.FrontHomeCtrl;

/**
 * 后台-会员管理控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
public class UserMngCtrl extends BackBaseController {
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static SalesManService salesManService = Factory.getService(SalesManService.class);
	
	protected static SalesManUserService salesManUserService = Factory.getService(SalesManUserService.class);
	
	
	/**
	 * 后台-会员管理-会员列表<br>
	 * 
	 * @rightID 301001
	 * 
	 * @param showType 筛选类型  0-所有;1-借款会员;2-理财会员;3-新用户;4-复合会员;5-正常会员;6-锁定
	 * @param currPage
	 * @param pageSize
	 * @param exports 1:导出    default：不导出
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月30日
	 *
	 */
	public static void showUsersPre(int showType, int currPage, int pageSize) {
		/* 搜索条件 */
		String userName = params.get("userName");
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		if (showType < 0 || showType > 6) {
			showType = 0;
		}
		
		/* 排序栏目 */
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);
		if(orderType < 0 || orderType > 9){
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);
		
		/* 排序方式 */
		String orderValueStr = params.get("orderValue");
		int orderValue = Convert.strToInt(orderValueStr, 0);//0,降序，1,升序
		if(orderValue<0 || orderValue>1){
			orderValue = 0;
		}
		renderArgs.put("orderValue", orderValue);
		
		PageBean<ShowUserInfo> pageBean = userService.pageOfUserInfo(showType, currPage, pageSize , orderType, orderValue, userName, exports);
		
		//导出
		if(exports == Constants.EXPORT){
			List<ShowUserInfo> list = pageBean.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject userInfo = (JSONObject)obj;	
				
				if(userInfo.getString("is_allow_login") == "true"){
					userInfo.put("is_allow_login", "未锁定");
				}else{
					userInfo.put("is_allow_login", "锁定");
				}
			}
			
			String fileName="平台会员列表";
			switch (showType) {
				case 1: 
					fileName = "平台借款会员列表";
					break;
				case 2:
					fileName = "平台理财会员列表";
					break;
				case 3:
					fileName = "平台新用户列表";
					break;
				case 4:
					fileName = "平台复合会员列表";
					break;
				case 5:
					fileName = "平台正常会员列表";
					break;
				case 6:
					fileName = "平台锁定会员列表";
					break;
				default:
					fileName = "平台会员列表";
					break;
			}
						
			File file = ExcelUtils.export(fileName,
					arrList,
					new String[] {
						"编号", "会员", "可用", "冻结", "待收", "待还", "累计借款", "累计投资", "登录次数", "最近登录时间", "状态"
					},
					new String[] {
					"id","name", "balance", "freeze", "no_receive", "no_repayment", "borrow_total", "invest_total", "login_count", "last_login_time", "is_allow_login"
					}
				);
			   
			renderBinary(file, fileName+".xls");
		}
		
		UserFundStatistic fundStatistic = userService.findUserFundStatistic(0, showType, userName);
		
		render(pageBean, fundStatistic, showType, userName);
	}
	
	/**
	 * 后台-会员管理-会员列表-会员详情<br>
	 * 
	 * @rightID 301002
	 * 
	 * @param sign
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月30日
	 *
	 */
	public static void showUserDetailPre(String sign) {
		ResultInfo result = new ResultInfo();
		result = Security.decodeSign(sign, Constants.USER_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			return;
		}
		long userId = Long.parseLong((String) result.obj);
		t_user user = userService.findUserById(userId);
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		UserDetail userDetail = userService.findCreditSituation(userId);
		render(user, userInfo, userFund, userDetail);
	}

	/**
	 * 后台-会员管理控制器-会员列表-进入发送短信给用户的界面<br>
	 * 
	 * @rightID 301003
	 *
	 * @param sign 用户ID的加密验签字符串
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月23日
	 */
	public static void toSendSMSPre(String sign) {

		ResultInfo result = Security.decodeSign(sign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error(result.msg);
			
			showUsersPre(0, 0, 0);
		}
		
		long userId = Long.parseLong((String) result.obj);
		t_user user = userService.findByID(userId);
		String mobile = user.mobile;
		
		List<NoticeScene> scenes = NoticeScene.getScenesNullTemplate();
		
		render(mobile,sign, scenes);
	}
	
	/**
	 * 后台-会员管理控制器-会员列表-进入发送短信给用户的界面-发送短信给用户<br>
	 * 
	 * @rightID 301003
	 *
	 * @param sign
	 * @param content 短信内容
	 * @param notice_scene 短信场景
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月23日
	 */
	public static void sendSMS(String sign, String content,String notice_scene) {
		ResultInfo result = Security.decodeSign(sign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error(result.msg);
			
			showUsersPre(0, 0, 0);
		}
		
		long userId = Long.parseLong((String) result.obj);
		t_user user = userService.findByID(userId);
		if (user == null) { 
			flash.error("用户不存在!");
			
			toSendSMSPre(sign);
		}
		
		if (StringUtils.isBlank(content) || content.length() < 1 || content.length() > 200) {
			flash.error("你输入的内容长度超出限制!");
			
			toSendSMSPre(sign);
		}
		
		NoticeScene scene = NoticeScene.getEnum(Convert.strToInt(notice_scene, -1));
		if( (!scene.equals(NoticeScene.PLATEFORM_EMAIL)) && (!scene.equals(NoticeScene.PLATEFORM_AD)) ){
			flash.error("你输入的含有非法的参数");
			
			toSendSMSPre(sign);
		}
		
		t_notice_setting_user user_setting = noticeService.findUserNoticeSetting(userId, scene);
		if ( !user_setting.sms ) {
			flash.error("该用户设置了不接收%s类型的短信",scene.value);
			
			flash.put("content", content);
			
			toSendSMSPre(sign);
		}
		
		
		long supervisor_id = getCurrentSupervisorId();
		boolean flag = noticeService.sendSMS(user.id, supervisor_id, content);
		if (!flag) {
			
			flash.error("短信发送失败");
			toSendSMSPre(sign);
		}
		
		//管理员事件
		Map<String, String> map = new HashMap<String, String>();
		map.put("user_id", user.id.toString());
		map.put("user_name", user.name);
		supervisorService.addSupervisorEvent(supervisor_id, Event.MEMBER_SEND_SMS, map);
		
		flash.success("短信发送成功");
		showUsersPre(0, 1, 10);
	}
	
	/**
	 * 后台-会员管理控制器-会员列表-进入发送email给用户的界面<br>
	 * 
	 * @rightID 301004
	 * 
	 * @param sign 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月23日
	 */
	public static void toSendEmailPre(String sign) {
		ResultInfo result = Security.decodeSign(sign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error(result.msg);
			
			showUsersPre(0, 0, 0);
		}
		
		long userId = Long.parseLong((String) result.obj);
		
		t_user_info user_info = userInfoService.findUserInfoByUserId(userId);
		if(user_info == null || StringUtils.isBlank(user_info.email)){
			flash.error("该用户没有绑定邮箱");
			
			showUsersPre(0, 0, 0);
		}
		
		List<NoticeScene> scenes = NoticeScene.getScenesNullTemplate();

		String email = user_info.email;
		render(sign,email, scenes);

	}
	
	/**
	 * 后台-会员管理控制器-会员列表-进入发送邮件给用户的界面-发送邮件给用户<br>
	 * 
	 * @rightID 301004
	 * 
	 * @param sign
	 * @param title 邮件标题
	 * @param content 邮件内容
	 * @param notice_scene 邮件场景
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月23日
	 */
	public static void sendEmail(String sign,String title, String content,String notice_scene) {

		ResultInfo result = Security.decodeSign(sign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error(result.msg);
			
			showUsersPre(0, 0, 0);
		}
		
		long userId = Long.parseLong((String) result.obj);
		t_user user = userService.findByID(userId);
		if (user == null) {
			flash.error("用户不存在");
			
			toSendEmailPre(sign);
		}
		
		if (StringUtils.isBlank(title) || title.length() < 1 || title.length() > 20) {
			flash.error("标题长度超出限制");
			
			toSendEmailPre(sign);
		}
		
		if (StringUtils.isBlank(content)) {
			flash.error("内容不为空");
			
			toSendEmailPre(sign);
		}
		
		NoticeScene scene = NoticeScene.getEnum(Convert.strToInt(notice_scene, -1));
		if( (!scene.equals(NoticeScene.PLATEFORM_EMAIL)) && (!scene.equals(NoticeScene.PLATEFORM_AD)) ){
			flash.error("你输入的含有非法的参数");
			
			toSendEmailPre(sign);
		} 
		
		t_notice_setting_user user_setting = noticeService.findUserNoticeSetting(userId, scene);
		if ( !user_setting.email ) {
			flash.error("该用户设置了不接收%s类型的邮件",scene.value);
			
			toSendEmailPre(sign);
		}
		
		long supervisor_id = getCurrentSupervisorId();
		result = noticeService.sendEmail(user.id, supervisor_id, title, content);
		
		if (result.code < 1) {
			flash.error(result.msg);
			toSendEmailPre(sign);
			
		} 
		
		//管理员事件
		Map<String, String> map = new HashMap<String, String>();
		map.put("user_id", user.id.toString());
		map.put("user_name", user.name);
		supervisorService.addSupervisorEvent(supervisor_id, Event.MEMBER_SEND_EMAIL, map);
		
		flash.success("邮件发送成功");
		showUsersPre(0, 1, 10);
	}
	
	/**
	 * 后台-会员管理控制器-会员列表-进入发送消息给用户的界面<br>
	 * 
	 * @rightID 301005
	 *
	 * @param sign
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月23日
	 */
	public static void toSendMSGPre(String sign) {

		ResultInfo result = Security.decodeSign(sign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error(result.msg);
			
			showUsersPre(0, 0, 0);
		}
		
		long userId = Long.parseLong((String) result.obj);
		t_user user = userService.findByID(userId);
		if ( user == null ) {
			flash.error("用户不存在!");
			
			toSendSMSPre(sign);
		}
		
		String userName = user.name;
		
		List<NoticeScene> scenes = NoticeScene.getScenesNullTemplate();

		render(userName,sign, scenes);

	}
	
	/**
	 * 后台-会员管理控制器-会员列表-进入发送消息给用户的界面-发送消息给用户<br>
	 * 
	 * @rightID 301005
	 * 
	 * @param sign
	 * @param title 消息标题
	 * @param content 消息内容
	 * @param notice_scene 消息场景
	 * 
	 * @author DaiZhengmiao
	 * @createDate 2015年12月23日
	 */
	public static void sendMSG(String sign,String title, String content,String notice_scene) {
		ResultInfo result = Security.decodeSign(sign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error(result.msg);
			
			showUsersPre(0, 0, 0);
		}
		
		long userId = Long.parseLong((String) result.obj);
		t_user user = userService.findByID(userId);
		if (user == null) { 
			flash.error("用户不存在");
			
			toSendSMSPre(sign);
		}
		
		if (StringUtils.isBlank(title) || title.length() < 1 || title.length() > 20) {
			flash.error("标题长度超出限制");
			
			toSendMSGPre(sign);
		}
		
		if (StringUtils.isBlank(content) || content.length() < 1 || content.length() > 200) {
			flash.error("内容超出限制");
			
			toSendMSGPre(sign);
		}
		
		NoticeScene scene = NoticeScene.getEnum(Convert.strToInt(notice_scene, -1));
		if( (!scene.equals(NoticeScene.PLATEFORM_EMAIL)) && (!scene.equals(NoticeScene.PLATEFORM_AD)) ){
			flash.error( "你输入的含有非法的参数");
			
			toSendMSGPre(sign);
		} 
		
		t_notice_setting_user user_setting = noticeService.findUserNoticeSetting(userId, scene);
		if ( !user_setting.msg ) {
			flash.error("该用户设置了不接收%s类型的站内信",scene.value);
			
			flash.put("title", title);
			flash.put("content", content);
			
			toSendMSGPre(sign);
		}
		
		Long supervisor_id = getCurrentSupervisorId();
		boolean flag = noticeService.sendMsg(user.id, supervisor_id, title, content);
		if (!flag) {
			
			flash.error("消息发送失败");
			toSendMSGPre(sign);
		} 
		
		//管理员事件
		Map<String, String> map = new HashMap<String, String>();
		map.put("user_id", user.id.toString());
		map.put("user_name", user.name);
		supervisorService.addSupervisorEvent(supervisor_id, Event.MEMBER_SEND_MSG, map);
		
		flash.success("站内消息发送成功");
		showUsersPre(0, 1, 10);
	}
	
	/**
	 * 后台-会员管理控制器-会员列表-管理员解锁/锁定用户<br>
	 * 
	 * @rightID 301006
	 * 
	 * @param sign 用户id
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月31日
	 *
	 */
	public static void supervisorLockUser(String sign){
		ResultInfo result = new ResultInfo();
		/* 获取userId */
		result = Security.decodeSign(sign, Constants.USER_ID_SIGN,Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 0) {
			
			renderJSON(result);
		}
		long userId =Convert.strToLong(result.obj.toString(), 0);
		t_user user = userService.findUserById(userId);
		
		result = userService.updateIsAllowLogin(userId);
		if (result.code < 0) {
			
			renderJSON(result);
		}
		long supervisorId = BackBaseController.getCurrentSupervisorId();
		Map<String, String> map = new HashMap<String, String>();
		map.put("user_id", userId+"");
		map.put("user_name", user.name);
		if (user.is_allow_login) {
			result.msg = "锁定会员成功!";
			result.obj = true;
			supervisorService.addSupervisorEvent(supervisorId, Event.MEMBER_LOCK, map);
			
			//移出登陆用户
			if (Cache.get(CacheKey.USER_ + userId) != null) {
				userService.removeUser(userId+"");
			};
			
		}else {
			result.obj = false;
			result.msg = "解锁会员成功!";
			supervisorService.addSupervisorEvent(supervisorId, Event.MEMBER_UNLOCK, map);
		}
		
		renderJSON(result);
	}
	
	/**
	 * 后台-会员管理-会员列表-编辑会员-进入编辑会员界面<br>
	 * 
	 * @rightID 301007
	 * 
	 * @param sign
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月30日
	 *
	 */
	public static void toEditUserPre(String sign) {
		ResultInfo result = new ResultInfo();
		result = Security.decodeSign(sign, Constants.USER_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			return;
		}
		long userId = Long.parseLong((String) result.obj);
		t_user user = userService.findUserById(userId);
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		UserDetail userDetail = userService.findCreditSituation(userId);
		render(user, userInfo, userFund, userDetail);
	}
	
	/**
	 * 后台-会员管理-会员列表-编辑会员-修改会员手机<br>
	 * 
	 * @rightID 301007
	 * 
	 * @param sign
	 * @param mobile 修改后的手机
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月7日
	 *
	 */
	public static void editUserMobile(String sign, String mobile){
		ResultInfo result = new ResultInfo();
		result = Security.decodeSign(sign, Constants.USER_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		
		/* 校验手机号码是否正确 */
		if (!StrUtil.isMobileNum(mobile)) {
			result.code = -1;
			result.msg = "您输入的手机号码有误";
			
			renderJSON(result);
		}
		
		long userId = Long.parseLong((String) result.obj);
		t_user user = userService.findUserById(userId);
		result = userService.updateUserMobile(userId, mobile);
		
		/** 添加管理员事件 */
		long supervisorId = BackBaseController.getCurrentSupervisorId();
		Map<String, String> map = new HashMap<String, String>();
		map.put("user_id", userId+"");
		map.put("user_name", user.name);
		if (result.code==1) {
			supervisorService.addSupervisorEvent(supervisorId, Event.MEMBER_EDIT, map);
		}
		
		renderJSON(result);
	}
	
	/**
	 * 后台-会员管理控制器-会员列表-更新会员签名<br>
	 * 
	 * @rightID 301008
	 * 
	 * @param sign 用户id
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月31日
	 *
	 */
	public static void updateFundSign(String sign){
		ResultInfo result = new ResultInfo();
		/* 获取userId */
		result = Security.decodeSign(sign, Constants.USER_ID_SIGN,Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 0) {
			
			renderJSON(result);
		}
		
		long userId =Convert.strToLong(result.obj.toString(), 0);

		result = userFundService.userFundSignUpdate(userId);

		renderJSON(result);
	}
	
	/**
	 * 会员 - 会员列表 - 进入群发短信页
	 * 
	 * @rightID 301009
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月30日
	 *
	 */
	public static void toMassSMSPre(){
		
		List<NoticeScene> scenes = NoticeScene.getScenesNullTemplate();
		render(scenes);
	}
	
	/**
	 * 会员 - 会员列表 - 群发短信
	 * @rightID 301009
	 * 
	 * @param member_type 会员类型
	 * @param content 内容
	 * @param notice_scene 场景
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月30日
	 *
	 */
	public static void massSMS(int member_type,String content,String notice_scene){
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		
		//参数校验
		result = isCheckMassSMS(member_type, content, notice_scene);
		if(result.code < 1){
			flash.error(result.msg);
			
			//缓存
			flash.put("member_type", member_type);
			flash.put("content", content);
			flash.put("notice_scene", notice_scene);
			
			toMassSMSPre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		
		//群发短信
		result = noticeService.sendMassSMS(member_type, content, NoticeScene.getEnum(Convert.strToInt(notice_scene, -1)),supervisorId);
		if(result.code < 1){
			flash.error(result.msg);
			
			//缓存
			flash.put("member_type", member_type);
			flash.put("content", content);
			flash.put("notice_scene", notice_scene);
			
			toMassSMSPre();
		}
		
		//管理员事件
		supervisorService.addSupervisorEvent(supervisorId, Event.MASS_SEND_SMS, null);
		
		showUsersPre(0, 1, 10);
	}
	
	/**
	 * 会员 - 会员列表 - 进入群发邮件页
	 * @rightID 301010
	 * 
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月30日
	 *
	 */
	public static void toMassEmailPre(){
		
		List<NoticeScene> scenes = NoticeScene.getScenesNullTemplate();
		render(scenes);
	}
	
	/**
	 * 会员 - 会员列表 - 群发邮件
	 * @rightID 301010
	 * 
	 * @param member_type 会员类型
	 * @param title 标题
	 * @param content 内容
	 * @param notice_scene 场景
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月30日
	 *
	 */
	public static void massEmail(int member_type,String title, String content,String notice_scene){
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();

		//参数校验
		result = isCheckMassEmail(member_type, title, content, notice_scene);
		if(result.code < 1){
			flash.error(result.msg);
			
			flash.put("member_type", member_type);
			flash.put("title", title);
			flash.put("content", content);
			flash.put("notice_scene", notice_scene);
			
			toMassEmailPre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		
		//群发邮件
		result = noticeService.sendMassEamil(member_type, title, content, NoticeScene.getEnum(Convert.strToInt(notice_scene, -1)),supervisorId);
		if(result.code < 1){
			flash.error(result.msg);
			
			flash.put("member_type", member_type);
			flash.put("title", title);
			flash.put("content", content);
			flash.put("notice_scene", notice_scene);
			
			toMassEmailPre();
		}
		
		//管理员事件
		supervisorService.addSupervisorEvent(supervisorId, Event.MASS_SEND_EMAIL, null);
		
		showUsersPre(0, 1, 10);
	}
	
	/**
	 * 会员 - 会员列表 - 进入群发消息页
	 * @rightID 301011
	 *  
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月30日
	 *
	 */
	public static void toMassMSGPre(){
		
		List<NoticeScene> scenes = NoticeScene.getScenesNullTemplate();
		render(scenes);
	}
	
	/**
	 * 会员 - 会员列表 - 群发消息
	 * @rightID 301011
	 * 
	 * @param memberType 会员类型   -1：全部会员    0：新会员   1：理财会员    2：借款会员
	 * @param title 标题
	 * @param content 内容
	 * @param notice_scene 类型
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月30日
	 *
	 */
	public static void massMSG(int member_type,String title, String content,String notice_scene){
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		
		//参数校验
		result = isCheckMassMsg(member_type,title,content,notice_scene);
		if(result.code < 1){
			flash.error(result.msg);
			
			flash.put("member_type", member_type);
			flash.put("title", title);
			flash.put("content", content);
			flash.put("notice_scene", notice_scene);
			
			toMassMSGPre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		//群发消息
		result = noticeService.sendMassMsg(member_type, supervisorId, title, content,NoticeScene.getEnum(Convert.strToInt(notice_scene, -1)));
		if(result.code < 1){
			
			flash.error(result.msg);
			toMassMSGPre();
		}
		
		//管理员事件
		supervisorService.addSupervisorEvent(supervisorId, Event.MASS_SEND_MSG, null);
		
		showUsersPre(0, 1, 10);
	}
	
	/**
	 * 群发短信校验
	 * 
	 * @param memberType 会员类型   -1：全部会员    0：新会员   1：理财会员    2：借款会员
	 * @param content 内容
	 * @param notice_scene 类型
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月1日
	 *
	 */
	public static ResultInfo isCheckMassSMS(int member_type,String content,String notice_scene){
		ResultInfo result = new ResultInfo();
		
		if(member_type < -1 || member_type > 3){
			result.code= -1;
			result.msg="请选择会员类型";
			
			return result;
		}
		
		if (StringUtils.isBlank(content) || content.length() < 1 || content.length() > 100) {
			result.code= -1;
			result.msg="内容1-100个字符";
			
			return result;
		}
		
		//参数非法校验
		NoticeScene scene = NoticeScene.getEnum(Convert.strToInt(notice_scene, -1));
		if( (!scene.equals(NoticeScene.PLATEFORM_EMAIL)) && (!scene.equals(NoticeScene.PLATEFORM_AD)) ){
			result.code= -1;
			result.msg="你输入的含有非法的参数";
			
			return result;
		}
		
		result.code = 1;
		result.msg="";
		
		return result;
	}
	
	/**
	 * 群发消息校验
	 * 
	 * @param memberType 会员类型   -1：全部会员    0：新会员   1：理财会员    2：借款会员
	 * @param title 标题
	 * @param content 内容
	 * @param notice_scene 类型
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月1日
	 *
	 */
	public static ResultInfo isCheckMassMsg(int member_type,String title, String content,String notice_scene){
		ResultInfo result = new ResultInfo();
		
		if(member_type < -1 || member_type > 3){
			result.code= -1;
			result.msg="请选择会员类型";
			
			return result;
		}
		
		if (StringUtils.isBlank(title) || title.length() < 1 || title.length() > 20) {
			result.code= -1;
			result.msg="标题长度超出限制";
			
			return result;
		}
		
		if (StringUtils.isBlank(content) || content.length() < 1 || content.length() > 200) {
			result.code= -1;
			result.msg="内容超出限制";
			
			return result;
		}
		
		//参数非法校验
		NoticeScene scene = NoticeScene.getEnum(Convert.strToInt(notice_scene, -1));
		if( (!scene.equals(NoticeScene.PLATEFORM_EMAIL)) && (!scene.equals(NoticeScene.PLATEFORM_AD)) ){
			result.code= -1;
			result.msg="你输入的含有非法的参数";
			
			return result;
		}
		
		result.code = 1;
		result.msg="";
		
		return result;
	}
	
	/**
	 * 群发邮件校验
	 * 
	 * @param memberType 会员类型   -1：全部会员    0：新会员   1：理财会员    2：借款会员
	 * @param title 标题
	 * @param content 内容
	 * @param notice_scene 类型
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月1日
	 *
	 */
	public static ResultInfo isCheckMassEmail(int member_type,String title, String content,String notice_scene){
		ResultInfo result = new ResultInfo();
		
		if(member_type < -1 || member_type > 3){
			result.code= -1;
			result.msg="请选择会员类型";
			
			return result;
		}
		
		if (StringUtils.isBlank(title) || title.length() < 1 || title.length() > 20) {
			result.code= -1;
			result.msg="标题长度超出限制";
			
			return result;
		}
		
		if (StringUtils.isBlank(content)) {
			result.code= -1;
			result.msg="内容不能为空";
			
			return result;
		}
		
		//参数非法校验
		NoticeScene scene = NoticeScene.getEnum(Convert.strToInt(notice_scene, -1));
		if( (!scene.equals(NoticeScene.PLATEFORM_EMAIL)) && (!scene.equals(NoticeScene.PLATEFORM_AD)) ){
			result.code= -1;
			result.msg="你输入的含有非法的参数";
			
			return result;
		}
		
		result.code = 1;
		result.msg="";
		
		return result;
	}
	
	/**
	 * 进入批量发送发送短信给用户的界面
	 * @rightID 301012
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public static void toSendBatchSMSPre(String users) {
		if(StringUtils.isBlank(users)){
			flash.error("请选择要发送的用户");
			
			showUsersPre(0, 1, 10);
		}
		
		List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
		String[] user = users.split(";");
		
		for(int i=0;i<user.length;i++){
			Map<String, Object> map = new HashMap<String, Object>();
				
			//拼接用户 昵称 及 手机号
			long userId = Long.parseLong(user[i]);
			t_user tuser = userService.findByID(userId);
			if(user != null && tuser.mobile != null){
				map.put("name", tuser.name);
				map.put("mobile", tuser.mobile);
					
				userList.add(map);
			}
		}
		
		if(userList == null || userList.size() == 0){
			flash.error("请选择要发送的用户");
			
			showUsersPre(0, 1, 10);
		}
		
		List<NoticeScene> scenes = NoticeScene.getScenesNullTemplate();
		
		render(scenes,userList,users);
	}
	
	/**
	 * 批量发送短信给用户<br>
	 * @rightID 301012
	 * 
	 * @param users 选择的用户id
	 * @param content 内容
	 * @param notice_scene 场景
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public static void sendBatchSMS(String users, String content,String notice_scene) {
		checkAuthenticity();
		
		if (StringUtils.isBlank(content) || content.length() < 1 || content.length() > 200) {
			flash.error("你输入的内容长度超出限制!");
			
			flash.put("content", content);
			flash.put("notice_scene", notice_scene);
			
			toSendBatchSMSPre(users);
		}
		
		NoticeScene scene = NoticeScene.getEnum(Convert.strToInt(notice_scene, -1));
		if( (!scene.equals(NoticeScene.PLATEFORM_EMAIL)) && (!scene.equals(NoticeScene.PLATEFORM_AD)) ){
			flash.error("你输入的含有非法的参数");
			
			flash.put("content", content);
			flash.put("notice_scene", notice_scene);
			
			toSendBatchSMSPre(users);
		}
		//获取管理员id
		long supervisor_id = getCurrentSupervisorId();
		
		String[] user = users.split(";");
		String str="";
		for(int i=0;i<user.length;i++){				
			long userId = Long.parseLong(user[i]);
			t_notice_setting_user user_setting = noticeService.findUserNoticeSetting(userId, scene);
			if (user_setting != null && user_setting.sms) {
				boolean flag = noticeService.sendSMS(userId, supervisor_id, content);
				if(!flag){
					str = str+userId+";";
				}
			}else{
				str = str+userId+";";
			}
		}
		
		//判断群发结果
		if(StringUtils.isNotBlank(str)){
			flash.error("编号为："+ str +" 的用户短信发送失败");
			
			showUsersPre(0, 1, 10);
		}
		
		//管理员事件
		supervisorService.addSupervisorEvent(supervisor_id, Event.BATCH_SEND_SMS, null);
		
		flash.success("短信发送成功");
		showUsersPre(0, 1, 10);
	}
	

	/**
	 * 进入批量发送邮件页面
	 * @rightID 301012
	 * 
	 * @param users选择的用户id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public static void toSendBatchEmailPre(String users) {
		if(StringUtils.isBlank(users)){
			flash.error("请选择要发送的用户");
			
			showUsersPre(0, 1, 10);
		}
		
		List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
		String[] user = users.split(";");
				
		for(int i=0;i<user.length;i++){
			Map<String, Object> map = new HashMap<String, Object>();
						
			//拼接用户 昵称 及 手机号
			long userId = Long.parseLong(user[i]);
			t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
			if(userInfo != null && userInfo.email != null){
				map.put("name", userInfo.name);
				map.put("email", userInfo.email);
				
				userList.add(map);
			}
		}
		
		if(userList ==null || userList.size()==0){
			flash.error("请选择绑定邮箱的用户");
			
			showUsersPre(0, 1, 10);
		}
		
		List<NoticeScene> scenes = NoticeScene.getScenesNullTemplate();

		render(scenes,userList,users);
	}
	
	/**
	 * 批量发送邮件
	 * @rightID 301012
	 * 
	 * @param users 选择的用户id
	 * @param title 标题
	 * @param content 内容
	 * @param notice_scene 场景
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public static void sendBatchEmail(String users,String title, String content,String notice_scene) {
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		if (StringUtils.isBlank(title) || title.length() < 1 || title.length() > 20) {
			flash.error("标题长度超出限制");
			
			toSendBatchEmailPre(users);
		}
		
		if (StringUtils.isBlank(content)) {
			flash.error("内容不为空");
			
			toSendBatchEmailPre(users);
		}
		
		NoticeScene scene = NoticeScene.getEnum(Convert.strToInt(notice_scene, -1));
		if( (!scene.equals(NoticeScene.PLATEFORM_EMAIL)) && (!scene.equals(NoticeScene.PLATEFORM_AD)) ){
			flash.error("你输入的含有非法的参数");
			
			toSendBatchEmailPre(users);
		}

		//获取管理员id
		long supervisor_id = getCurrentSupervisorId();
		
		//用户
		String[] user = users.split(";");
		String str = "";
		for(int i=0;i<user.length;i++){				
			long userId = Long.parseLong(user[i]);
			t_notice_setting_user user_setting = noticeService.findUserNoticeSetting(userId, scene);
			if (user_setting !=null  && user_setting.email) {
				result = noticeService.sendEmail(userId, supervisor_id, title, content);
				if(result.code < 1){
					str=str+userId+";";
				}
			}else{
				str=str+userId+";";
			}
		}
		
		//判断批量发送结果
		if(StringUtils.isNotBlank(str)){
			flash.error("编号为："+ str +" 的用户邮件发送失败");
					
			showUsersPre(0, 1, 10);
		}
		
		//管理员事件
		supervisorService.addSupervisorEvent(supervisor_id, Event.BATCH_SEND_EMAIL, null);
		
		flash.success("邮件发送成功");
		showUsersPre(0, 1, 10);
	}
	
	/**
	 * 进入批量发送消息的页面
	 * @rightID 301012
	 * 
	 * @param users选择的用户
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public static void toSendBatchMSGPre(String users) {
		if(StringUtils.isBlank(users)){
			flash.error("请选择要发送的用户");
			
			showUsersPre(0, 1, 10);
		}
		
		List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
		String[] user = users.split(";");
				
		for(int i=0;i<user.length;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			
			//拼接用户 昵称 及 手机号
			long userId = Long.parseLong(user[i]);
			t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
			if(userInfo != null && userInfo.name != null){	
				map.put("name", userInfo.name);
				userList.add(map);
			}
		}
		
		if(userList == null || userList.size()==0){
			flash.error("请选择用户");
			
			showUsersPre(0, 1, 10);
		}
		
		List<NoticeScene> scenes = NoticeScene.getScenesNullTemplate();

		render(userList,users, scenes);
	}
	
	/**
	 * 批量发送消息
	 * @rightID 301012
	 * 
	 * @param users选择的用户id
	 * @param title 标题
	 * @param content 内容
	 * @param notice_scene 场景
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年4月5日
	 *
	 */
	public static void sendBatchMSG(String users,String title, String content,String notice_scene) {
		
		if (StringUtils.isBlank(title) || title.length() < 1 || title.length() > 20) {
			flash.error("标题长度超出限制");
			
			toSendBatchMSGPre(users);
		}
		
		if (StringUtils.isBlank(content) || content.length() < 1 || content.length() > 200) {
			flash.error("内容超出限制");
			
			toSendBatchMSGPre(users);
		}
		
		NoticeScene scene = NoticeScene.getEnum(Convert.strToInt(notice_scene, -1));
		if( (!scene.equals(NoticeScene.PLATEFORM_EMAIL)) && (!scene.equals(NoticeScene.PLATEFORM_AD)) ){
			flash.error( "你输入的含有非法的参数");
			
			toSendBatchMSGPre(users);
		} 

		//获取管理员id
		long supervisor_id = getCurrentSupervisorId();

		String[] user = users.split(";");
		String str = "";
		for(int i=0;i<user.length;i++){				
			long userId = Long.parseLong(user[i]);
			t_notice_setting_user user_setting = noticeService.findUserNoticeSetting(userId, scene);
			if (user_setting !=null  && user_setting.msg) {
				boolean flag =noticeService.sendMsg(userId, supervisor_id, title, content);
				if(!flag){
					str = str + userId +";";
				}
			}
			else{
				str = str + userId +";";
			}
		}
		
		//判断批量发送结果
		if(StringUtils.isNotBlank(str)){
			flash.error("编号为："+ str +" 的用户消息发送失败");
							
			showUsersPre(0, 1, 10);
		}
		
		//管理员事件
		supervisorService.addSupervisorEvent(supervisor_id, Event.BATCH_SEND_MSG, null);
		
		flash.success("消息发送成功");
		showUsersPre(0, 1, 10);
	}
	
	/**
	 * 后台-会员管理-会员列表-模拟登陆<br>
	 * 
	 * @rightID 301013
	 * 
	 * @param sign
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年5月30日
	 *
	 */
	public static void simuldatedLoginPre(String sign) {
		
		ResultInfo result = new ResultInfo();
		result = Security.decodeSign(sign, Constants.USER_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			return;
		}
		long userId = Long.parseLong((String) result.obj);
		String sessionId = Session.current().getId();
		/* 设置用户凭证 */
		Cache.set(CacheKey.FRONT_ + sessionId, userId, Constants.CACHE_TIME_MINUS_30);
		userService.flushUserCache(userId);
		Cache.set(CacheKey.SIMULATED_ + sessionId, true, Constants.CACHE_TIME_MINUS_30);
		
		long supervisorId = BackBaseController.getCurrentSupervisorId();
		t_user_info user = userInfoService.findUserInfoByUserId(userId);
		Map<String, String> map = new HashMap<String, String>();
		map.put("user_id", userId+"");
		map.put("user_name", user.name);
		supervisorService.addSupervisorEvent(supervisorId, Event.SIMULATED_LOGIN, map);
		
		redirect("front.account.MyAccountCtrl.homePre");
	}
	
	
	/**
	 * 推广详情
	 * @param userId
	 */
	public static void CPSDetail(long userId,int currPage,int pageSize,String name,String beginTime,String endTime){
		
		
		
	}
	
	/**
	 * 后台-会员管理-会员列表-团队长业务员分配<br>
	 * 
	 * @rightID 
	 * 
	 * @param sign
	 *
	 * @author liuyang
	 * @createDate 2018年7月23日
	 *
	 */
	public static void distributionUser(String sign, long userId, String superId){

		Long supervisorId=Long.parseLong(superId);
		t_supervisor supers=supervisorService.findByID(supervisorId);
		
		if(supers!=null && supers.extension!=null){
			t_user user=userService.findByID(userId);
			user.supervisor_id=supervisorId;
			user.save();
		}else{
			flash.error("输入的编号不存在,或不是业务员");
		}
		
		toEditUserPre(sign);
	}
	
	/**
	 * 后台-会员管理-会员列表-代理商业务员分配<br>
	 * 
	 * @rightID 
	 * 
	 * @param sign
	 *
	 * @author liuyang
	 * @createDate 2018年7月23日
	 *
	 */
	public static void distributionProxyUser(String sign, long userId, String superId){

		Long proxyId=Long.parseLong(superId);
		
		t_proxy_salesman pro = salesManService.findByID(proxyId);
		
		if( pro!=null ){
			t_user user=userService.findByID(userId);
			user.proxy_salesMan_id = pro.id;
			user.save();
			
			t_proxy_user pu = salesManUserService.findByColumn("user_id=?", userId);
			
			if (pu == null) {
				t_proxy_user pUser = new t_proxy_user();
				
				pUser.user_id = userId;
				pUser.cur_invest_amount = 0.00;
				pUser.total_invest_amount = 0.00;
				pUser.save();
			}
			
		}else{
			flash.error("输入的编号不存在,或不是业务员");
		}
		
		toEditUserPre(sign);
	}
}
