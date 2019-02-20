package controllers.back.query;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import controllers.common.BackBaseController;
import models.activity.shake.entity.t_user_gold;
import models.common.bean.UserInfoServices;
import models.common.entity.t_service_month;
import models.common.entity.t_service_person;
import models.common.entity.t_service_trace;
import models.common.entity.t_service_user_relevance;
import models.common.entity.t_supervisor;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import services.activity.shake.UserGoldService;
import services.common.ServiceMonthService;
import services.common.ServicePersonService;
import services.common.ServiceTraceService;
import services.common.ServiceUserRelevanceService;
import services.common.SupervisorService;
import services.common.UserInfoService;
import services.common.UserService;

/**
 * 后台-查询-客户追踪
 * 
 * 
 * @author liuyang
 * @create 2018.07.06
 */
public class UserTraceCtrl extends BackBaseController {
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static SupervisorService supervisorService = Factory.getService(SupervisorService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static ServicePersonService servicePersonService = Factory.getService(ServicePersonService.class);
	
	protected static ServiceUserRelevanceService serviceUserRelevanceService = Factory.getService(ServiceUserRelevanceService.class);
	
	protected static ServiceTraceService serviceTraceService = Factory.getService(ServiceTraceService.class);
	
	protected static ServiceMonthService serviceMonthService = Factory.getService(ServiceMonthService.class);
	
	protected static UserGoldService userGoldService = Factory.getService(UserGoldService.class);

	/**
	 * 后台-查询-客户追踪-理财会员管理-未开户客户列表页面
	 * 
	 * @param currPage
	 * @param pageSize
	 * 
	 * @author liuyang
	 * @createDate 2018年07月06日
	 */
	public static void showUserlistPre(int showType, int currPage, int pageSize) {
		
		if (showType < 0 || showType > 2) {
			showType = 0;
		}
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		
		String mobile = params.get("mobile");
		
		PageBean<UserInfoServices> pageBean = userInfoService.pageOfUserList(showType, currPage, pageSize, mobile);
		
		render(pageBean, showType, mobile);
	}
	
	/**
	 * 后台-查询-客户追踪-理财会员管理-到添加客服页面
	 * 
	 * @author liuyang
	 * @createDate 2018年07月10日
	 */
	public static void toAddServicePre() {
		
		render();
	}
	
	/**
	 * 后台-查询-客户追踪-理财会员管理-保存客服
	 * 
	 * @author liuyang
	 * @createDate 2018年07月10日
	 */
	public static void addService() {
		
		t_service_person ser = new t_service_person();
		
		String name = params.get("name");
		
		t_supervisor sup = supervisorService.findByColumn("name=?", name);
		if(sup == null) {
			flash.error("名称错误，请重试");
			toAddServicePre();
		}
		ser.supervisor_id = sup.id;
        
        String orderTime = params.get("order_time");
        ser.time = DateUtil.strToDate(orderTime);
        
        ser.save();
        flash.success("添加成功");
		
		showUserlistPre(0, 1, 10);
	}
	
	/**
	 * 后台-查询-客户追踪-理财会员管理-给指定客服分配客服页面
	 * 
	 * @author liuyang
	 * @createDate 2018年07月10日
	 */
	public static void toAllotUserPre(long userId) {
		
		t_user user = userService.findByID(userId);
		
		if(user == null) {
			flash.error("用户不存在，请重试");
			showUserlistPre(0, 1, 10);
		}
		
		List<t_service_person> persons = servicePersonService.findAll();
		
		if(persons.size()<=0 || persons == null) {
			flash.error("还未添加客服人员，请先添加客服");
			showUserlistPre(0, 1, 10);
		}
		
		render(user, persons);
	}
	
	/**
	 * 后台-查询-客户追踪-给指定客服分配客户
	 * 
	 * @author liuyang
	 * @createDate 2018年07月06日
	 */
	public static void allotUser(long userId, long serviceId) {
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		if(userInfo == null) {
			flash.error("客户信息不存在");
			showUserlistPre(0, 1, 10);
		}
		userInfo.service_type = 1;
		
		
		t_service_user_relevance ser = new t_service_user_relevance();
		
		ser.service_id = serviceId;
		ser.user_id = userId;
		
		String orderTime = params.get("order_time");
        ser.time = DateUtil.strToDate(orderTime);
        
        ser.save();
        userInfo.save();
        
        Date dates = DateUtil.strDateToEndDate("2018-11-13");
        
        t_user_gold gold = userGoldService.getByUserId(userId);
        t_user users = userService.findUserById(userId);
        
        if(users.time.getTime()<dates.getTime()) {
        	if(gold == null) {
            	t_user_gold golds = new t_user_gold();
            	golds.gold = 1;
            	golds.user_id = userId;
            	golds.Time = new Date();
            	golds.save();
            }else {
            	gold.gold++;
            	gold.save();
            }
        }
        
        
        flash.success("分配成功");
        
		showUserlistPre(0, 1, 10);
	}
	
	/**
	 * 后台-查询-客户追踪-理财会员追踪-客户列表页面
	 * 
	 * @param currPage
	 * @param pageSize
	 * 
	 * @author liuyang
	 * @createDate 2018年07月06日
	 */
	public static void showServiceUserlistPre(long serviceId, int showType, int currPage, int pageSize) {
		
		if (showType < 0 || showType > 2) {
			showType = 0;
		}
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		
		List<t_service_person> persons = servicePersonService.findAll();
		
		if(persons.size()<=0 || persons == null) {
			flash.error("还未添加客服人员，请先添加客服");
			showUserlistPre(0, 1, 10);
		}
		
		String mobile = params.get("mobile");
		
		PageBean<UserInfoServices> pageBean = userInfoService.pageOfServiceUserList(showType, currPage, pageSize, mobile, serviceId);
		
		render(pageBean, mobile, showType, persons, serviceId);
		
	}
	
	/**
	 * 后台-查询-客户追踪-理财会员追踪-添加客户追踪信息页面
	 * 
	 * @author liuyang
	 * @createDate 2018年07月06日
	 */
	public static void toAddUserTracePre(long userId) {
		
		t_service_user_relevance rel = serviceUserRelevanceService.findByColumn("user_id=?", userId);
		
		if(rel == null) {
			flash.error("系统错误，请重试");
			showServiceUserlistPre(0, 0, 1, 10);
		}
		
		t_service_person person = servicePersonService.findByID(rel.service_id);
		
		if(person == null) {
			flash.error("系统错误，请重试");
			showServiceUserlistPre(0, 0, 1, 10);
		}
		
		t_user user = userService.findByID(userId);
		
		if(user == null) {
			flash.error("用户信息不存在，请重试");
			showServiceUserlistPre(0, 0, 1, 10);
		}
		
		long supervisor_id = getCurrentSupervisorId();
		
		if(person.supervisor_id != supervisor_id) {
			flash.error("没有权限，请勿操作");
			showServiceUserlistPre(0, 0, 1, 10);
		}
		
		render(user, person);
	}
	
	/**
	 * 后台-查询-客户追踪-理财会员追踪-客户追踪信息列表页面
	 * 
	 * @param userId
	 * @param currPage
	 * @param pageSize
	 * 
	 * @author liuyang
	 * @createDate 2018年07月06日
	 */
	public static void showUserTraceListPre(long userId,int currPage, int pageSize) {
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		
		t_user user = userService.findByID(userId);
		
		if(user == null) {
			flash.error("用户信息不存在，请重试");
			showServiceUserlistPre(0, 0, 1, 10);
		}
		
		PageBean<t_service_trace> pageBean = serviceTraceService.pageOfUserTraceList(userId, currPage, pageSize);
		render(pageBean, userId);
	}
	
	/**
	 * 后台-查询-客户追踪-保存客户追踪信息页面
	 * 
	 * @author liuyang
	 * @createDate 2018年07月06日
	 */
	public static void addUserTrace(String content, long userId, long serviceId) {
		
		if(StringUtils.isBlank(content)){
        	flash.error("跟踪内容不能为空");
        	toAddUserTracePre(userId);
        }
		
		long supervisor_id = getCurrentSupervisorId();
		t_supervisor sup = supervisorService.findByID(supervisor_id);
		if(sup == null) {
			flash.error("登陆已失效，请重新登陆");
        	toAddUserTracePre(userId);
		}
		
		t_service_trace tra = new t_service_trace();
		tra.content = content;
		tra.service_id = serviceId;
		tra.user_id = userId;
		
		String orderTime = params.get("time");
        flash.put("orderTime", orderTime);
        tra.time = DateUtil.strToDate(orderTime);
        
        tra.save();
		
        flash.success("添加成功");
		showUserlistPre(0, 1, 10);
	}
	
	/**
	 * 后台-查询-客户追踪-理财会员统计-客服月统计列表
	 * 
	 * @param currPage
	 * @param pageSize
	 * 
	 * @author liuyang
	 * @createDate 2018年07月13日
	 */
	public static void showServiceMonthListPre(int currPage, int pageSize, int month, int year) {
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		
		PageBean<t_service_month> pageBean = serviceMonthService.pageOfServiceMonthList(currPage, pageSize, year, month);
		
		render(pageBean, month, year);
	}
	
	
}
