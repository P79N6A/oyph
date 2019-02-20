package controllers.back.team;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.h2.engine.User;

import com.google.gson.JsonArray;
import com.shove.Convert;
import com.shove.security.Encrypt;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import play.Play;
import services.TeamsService;
import services.TpService;
import services.common.SupervisorService;
import services.common.UserService;
import services.core.InvestService;
import models.common.bean.ShowUserInfo;
import models.common.entity.t_setting_platform;
import models.common.entity.t_supervisor;
import models.common.entity.t_supervisor.LockStatus;
import models.common.entity.t_user;
import models.common.entity.t_event_supervisor.Event;
import models.core.bean.UserInvestRecord;
import models.core.entity.t_invest;
import models.entity.t_teams;
import models.main.bean.DiscountRate;
import models.main.bean.TeamRule;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.TimeUtil;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.back.BackLoginCtrl;
import controllers.common.BackBaseController;
import daos.common.SettingDao;

public class TeamCtrl extends BackBaseController{
	
	protected static final TeamsService teamsService = Factory.getService(TeamsService.class);
	
	protected static final InvestService investService = Factory.getService(InvestService.class);
	
	protected static final UserService userService = Factory.getService(UserService.class);
	
	protected static final SupervisorService supervisorService = Factory.getService(SupervisorService.class);
	
    protected static final SettingDao settingDao = Factory.getDao(SettingDao.class); 
    
	/**
	 * 业务经理列表
	 * @param currPage
	 * @param pageSize
	 * @param type
	 */
	public static void accountsManagerListsPre(int currPage,int pageSize,int type,String name,Integer month){
		
		/*if(month==null){
			month=0;
		}
		
		String zhis = params.get("zhis");
		Date times = TimeUtil.strToDate_yyyyMM(zhis);
		if(times != null) {
			t_teams.getT().setMonths(times.getMonth()+1);
			t_teams.getT().setYear(times.getYear()+1900);
		}
		t_teams.getT().setMonth(month);
		
		int exports = Convert.strToInt(params.get("exports"), 0);		
		
		PageBean<t_teams> page = teamsService.findTypeList(currPage, pageSize, type,name,exports);
            //导出
		if(exports == Constants.EXPORT){
			List<t_teams> list =page.page;
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			File file = ExcelUtils.export("客户经理",
					arrList,
					new String[] {
						"编号", "职位", "推广码", "真实姓名", "推广会员人数", "理财会员人数", "当月推广理财金额", "本月业绩提成", "推广理财总金额"
					},
					new String[] {
					"id","position", "code", "realName", "count", "investCount", "sumMoney", "deduct", "total_money"
					}
				);
			   
			renderBinary(file, "客户经理"+".xls");
		}*/
		render(month, type);
	}
	
	
	/**
	 * 客户经理详情
	 */
	public static void accountManagerInfoPre(int currPage,int pageSize){
		/*t_user.getT().setMonths(0);
		long supervisorId = getCurrentSupervisorId();
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		if(supervisorId<=0){
			BackLoginCtrl.toBackLoginPre();
		}
		t_supervisor supervisors = supervisorService.findByID(supervisorId);
		
		t_teams team  = teamService.findByColumn("supervisor_id=?", supervisorId);
		
		PageBean<t_user> page = userService.queryBySupervisorList(currPage, pageSize, supervisorId,exports);
		
		if(exports == Constants.EXPORT){
			List<t_user> list =page.page;
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			File file = ExcelUtils.export("推广会员",
					arrList,
					new String[] {
						"编号", "职位", "会员名", "真实姓名", "推广时间", "手机号码", "本月理财金额", "推广理财总金额"
					},
					new String[] {
					"id","position", "name", "realName", "time", "mobile", "monthAmount", "investAmount"
					}
				);
			   
			renderBinary(file, "推广会员"+".xls");
		}
		String codeSign = Encrypt.encrypt3DES(supervisors.extension, ConfConst.ENCRYPTION_KEY_DES);
		String baseUrl = Play.configuration.getProperty("extension_base_path");
		
		String extensionPath = baseUrl + "loginAndRegiste/register.html?extensionCode=" + codeSign;*/
		render();
	}
	/**
	 * lihuijun 2017-1-17
	 */
	public static void accountManagerInfooPre(int currPage,int pageSize, long supervisorId, int month){
		/*t_user.getT().setMonths(month);
		
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		if(supervisorId<=0){
			BackLoginCtrl.toBackLoginPre();
		}
		
		PageBean<t_user> page = userService.queryBySupervisorList(currPage, pageSize, supervisorId,exports);*/
			
		render(supervisorId,month);
	}
	/**
	 * 
	 */
	
	/**
	 * 主任列表
	 * @param currPage
	 * @param pageSize
	 * @param type
	 */
	public static void directorListPre(int currPage,int pageSize,int type){
		/*String zhis = params.get("zhis");
		Date times = TimeUtil.strToDate_yyyyMM(zhis);
		if(times != null) {
			t_teams.getT().setMonths(times.getMonth()+1);
			t_teams.getT().setYear(times.getYear()+1900);
		}
		int exports = Convert.strToInt(params.get("exports"), 0);
		int selMonth=Convert.strToInt(params.get("selMonth"), 0);
		PageBean<t_teams> page = teamsService.findTypeList(currPage, pageSize, 1,"",exports);
		List<t_teams> newTeams=new ArrayList<>();
		if (page != null && page.page != null) {
			for(t_teams myteam:page.page){
				myteam=teamsService.getTeamActualMonthTotal(myteam, selMonth);
				newTeams.add(myteam);
			}
		}
		page.page=newTeams;
		if(exports == Constants.EXPORT){
			List<t_teams> list =page.page;
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			File file = ExcelUtils.export("主任列表",
					arrList,
					new String[] {
						"编号", "职位",  "真实姓名","团队人数", "团队推广会员人数", "团队理财会员人数", "团队当月理财总金额","团队月折算理财金额", "团队本月佣金", "团队理财总金额","团队佣金"
					},
					new String[] {
					"id","position", "realName","count", "userCode", "investCount", "total_month_invest","total_month_money", "total_month_commission", "total_money","total_commission"
					}
				);
			renderBinary(file, "主任列表"+".xls");
		}
		
		
		page.selMonth=selMonth;*/
		render();
	}
	
	/**
	 * 主任详情
	 * @param currPage
	 * @param pageSize
	 */
	public static void directorInfoPre(int currPage,int pageSize){
	
		long supervisorId = getCurrentSupervisorId();
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		if(supervisorId<=0){
			BackLoginCtrl.toBackLoginPre();
		}
		t_supervisor supervisors = supervisorService.findByID(supervisorId);
		
		t_teams team  = teamService.findByColumn("supervisor_id=?", supervisorId);
		
		PageBean<t_teams> page = teamService.findDirectorList(currPage, pageSize, 0,supervisorId,exports);
		
		if(exports == Constants.EXPORT){
			List<t_teams> list =page.page;
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			File file = ExcelUtils.export("主任列表我的业务经理团队",
					arrList,
					new String[] {
						"编号", "职位", "推广码", "真实姓名", "推广会员人数", "理财会员人数", "当月推广理财金额", "业绩提成", "推广理财总金额"
					},
					new String[] {
					"id","position", "code", "name", "count", "investCount", "month_money", "total_commission", "total_money"
					}
				);
			renderBinary(file, "主任列表我的业务经理团队"+".xls");
		}
		
		render(supervisors,team,page);
	}
	
	
	/**
	 * 主任经理列表
	 * @param currPage
	 * @param pageSize
	 * @param type
	 */
	public static void directorManagerListPre(int currPage,int pageSize,int type){
		
		
	int exports = Convert.strToInt(params.get("exports"), 0);
		
		PageBean<t_teams> page = teamsService.findTypeList(currPage, pageSize, 2,"",exports);
		if(exports == Constants.EXPORT){
			List<t_teams> list =page.page;
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			File file = ExcelUtils.export("主任经理列表",
					arrList,
					new String[] {
						"编号", "职位","真实姓名", "团队人数", "团队推广会员人数", "团队理财会员人数", "团队当月理财总金额", "团队本月佣金", "团队理财总金额","团队佣金"
					},
					new String[] {
					"id","position","realName", "count", "userCode", "investCount", "total_money", "month_commission", "total_money", "total_commission"
					}
				);
			renderBinary(file, "主任经理列表"+".xls");
		}
		
	
		
		render(page);
	
	}
	
	/**
	 * 业务部经理个人中心
	 * @param currPage
	 * @param pageSize
	 */
	public static void directorManagerInfoPre(int currPage,int pageSize){
		long supervisorId = getCurrentSupervisorId();
		
		if(supervisorId<=0){
			BackLoginCtrl.toBackLoginPre();
		}
		int exports = Convert.strToInt(params.get("exports"), 0);
		t_supervisor supervisors = supervisorService.findByID(supervisorId);
		
		t_teams team  = teamService.findByColumn("supervisor_id=?", supervisorId);
		
		PageBean<t_teams> page = teamService.findDirectorList(currPage, pageSize, 1,supervisorId,exports);
		
		if(exports == Constants.EXPORT){
			List<t_teams> list =page.page;
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			File file = ExcelUtils.export("业务部经理我的团队",
					arrList,
					new String[] {
						"编号", "职位", "真实姓名", "推广会员人数", "理财会员人数", "当月推广理财金额", "推广理财总金额", "业绩提成"
					},
					new String[] {
					"id","position", "realName", "count", "investCount", "month_money", "total_money", "total_commission"
					}
				);
			renderBinary(file, "业务部经理我的团队"+".xls");
		}
		
		render(supervisors,team,page);
		
	}

	/**
	 * 团队长规则
	 */
	public static void teamRulePre(){
		
		List<TeamRule> lists = new ArrayList<TeamRule>();
		List<TeamRule> lists1 = new ArrayList<TeamRule>();
		List<TeamRule> lists2 = new ArrayList<TeamRule>();
		List<TeamRule> lists3 = new ArrayList<TeamRule>();
		List<DiscountRate> lists8 = new ArrayList<DiscountRate>();
		List<TeamRule> lists9 = new ArrayList<TeamRule>();
		
		
		t_setting_platform setting = settingDao.findByColumn("_key=?", "customer_commission");
		t_setting_platform setting1 = settingDao.findByColumn("_key=?", "director_commission");
		t_setting_platform setting2 = settingDao.findByColumn("_key=?", "director_manager_commission");
		t_setting_platform setting3 = settingDao.findByColumn("_key=?", "business_manager_commission");
		t_setting_platform setting8 = settingDao.findByColumn("_key=?", "annual_discount_rate");
		t_setting_platform setting9 = settingDao.findByColumn("_key=?", "personal_commission");
		
		JSONArray jsonArray = JSONArray.fromObject(setting._value);
		JSONArray jsonArray1 = JSONArray.fromObject(setting1._value);
		JSONArray jsonArray2 = JSONArray.fromObject(setting2._value);
		JSONArray jsonArray3 = JSONArray.fromObject(setting3._value);
		JSONArray jsonArray8 = JSONArray.fromObject(setting8._value);
		JSONArray jsonArray9 = JSONArray.fromObject(setting9._value);
		
		lists = (List)JSONArray.toList(jsonArray, new TeamRule(), new JsonConfig()); 
		lists1 = (List)JSONArray.toList(jsonArray1, new TeamRule(), new JsonConfig()); 
		lists2 = (List)JSONArray.toList(jsonArray2, new TeamRule(), new JsonConfig()); 
		lists3 = (List)JSONArray.toList(jsonArray3, new TeamRule(), new JsonConfig());   	
      	lists8 = (List)JSONArray.toList(jsonArray8, new DiscountRate(), new JsonConfig());
      	lists9 = (List)JSONArray.toList(jsonArray9, new TeamRule(), new JsonConfig());

		
		render(lists,lists1,lists2,lists3,lists8,lists9);
	}
	
	/**
	 * author:lihuijun
	 * date:2017-2-8
	 */
	public static void definedTeamRulePre(){
		List<DiscountRate> lists1 = new ArrayList<DiscountRate>();
		List<DiscountRate> lists2 = new ArrayList<DiscountRate>();
		t_setting_platform setting1 = settingDao.findByColumn("_key=?", "special_commission1");
		t_setting_platform setting2 = settingDao.findByColumn("_key=?", "special_commission2");
		JSONArray jsonArray1 = JSONArray.fromObject(setting1._value);
		JSONArray jsonArray2 = JSONArray.fromObject(setting2._value);
		lists1 = (List)JSONArray.toList(jsonArray1, new DiscountRate(), new JsonConfig());
		lists2 = (List)JSONArray.toList(jsonArray2, new DiscountRate(), new JsonConfig());
		render(lists1,lists2);
	}
	
	
	/**
	 * 修改团队长配置
	 */
	public static void editTeamRule(){
		
		Map<String, String> infos = new HashMap<String, String>();
		if(params.get("customer_commission")!=""&&params.get("customer_commission")!=null){
			infos.put(SettingKey.CUSTOMER_COMMISSION, params.get("customer_commission"));
		}
		if(params.get("financial_fee")!=""&&params.get("financial_fee")!=null){
			infos.put(SettingKey.FINANCIAL_FEE, params.get("financial_fee"));
		}
		if(params.get("director_quota")!=""&&params.get("director_quota")!=null){
			infos.put(SettingKey.DIRECTOR_QUOTA, params.get("director_quota"));
		}
		if(params.get("director_manager_quota")!=""&&params.get("director_manager_quota")!=null){
			infos.put(SettingKey.DIRECTOR_MANAGER_QUOTA, params.get("director_manager_quota"));
		}
		if(params.get("standard_bid")!=""&&params.get("standard_bid")!=null){
			infos.put(SettingKey.STANDARD_BID, params.get("standard_bid"));
		}
		if(params.get("director_commission")!=""&&params.get("director_commission")!=null){
			infos.put(SettingKey.DIRECTOR_COMMISSION, params.get("director_commission"));
		}
		if(params.get("director_manager_commission")!=""&&params.get("director_manager_commission")!=null){
			infos.put(SettingKey.DIRECTOR_MANAGER_COMMISSION, params.get("director_manager_commission"));
		}
		if(params.get("business_manager_commission")!=""&&params.get("business_manager_commission")!=null){
			infos.put(SettingKey.BUSINESS_MANAGER_COMMISSION, params.get("business_manager_commission"));
		}
		if(params.get("discount_rates")!=""&&params.get("discount_rates")!=null){
			infos.put("annual_discount_rate", params.get("discount_rates"));
		}
		if(params.get("personal_commission")!=""&&params.get("personal_commission")!=null){
			infos.put(SettingKey.PERSONAL_COMMISSION, params.get("personal_commission"));
		}
		
		boolean flag = settingService.updateSettings(infos);
		if (!flag) {
			flash.error("团队长规则修改信息失败!");
			teamRulePre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.SETTING_BASIC, null);
		
		flash.success("基本信息保存成功");
		teamRulePre();
	}
	/**
	 * author:lihuijun
	 * date:2017-2-8
	 * 修改自定义提成规则
	 */
	public static void editDeductRule(){
		Map<String, String> infos = new HashMap<String, String>();
		
		if(params.get("discount_rates")!=""&&params.get("discount_rates")!=null){
			infos.put("special_commission1", params.get("discount_rates"));
		}
		
		boolean flag = settingService.updateSettings(infos);
		if (!flag) {
			flash.error("自定义提成规则修改信息失败!");
			definedTeamRulePre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.SETTING_BASIC, null);
		flash.success("基本信息保存成功");
		definedTeamRulePre();
	}
	public static void editDeductRule1(){
		Map<String, String> infos = new HashMap<String, String>();
		
		
		if(params.get("discount_rates")!=""&&params.get("discount_rates")!=null){
			infos.put("special_commission2", params.get("discount_rates"));
		}
		boolean flag = settingService.updateSettings(infos);
		if (!flag) {
			flash.error("自定义提成规则修改信息失败!");
			definedTeamRulePre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.SETTING_BASIC, null);
		flash.success("基本信息保存成功");
		definedTeamRulePre();
	}
	
	/**
	 * 推广详情
	 * @param userId
	 */
	public static void cpsDetailsPre(long userId,int currPage, int pageSize){
		
		ResultInfo resultInfo = new ResultInfo();
		
		PageBean<UserInvestRecord> pageBean = investService.pageOfUserInvestRecord(currPage,pageSize, userId);
		
		render(pageBean,userId);
	}
	
	/**
	 * 添加业务经理业务
	 */
	public static void  addAccountManagerPre(int currPage,int pageSize){
		PageBean<t_teams> page = teamsService.pageOfaccountManager(currPage, pageSize,0);
		render(page); 
	}
	
	/**
	 * 添加管理员的父类Id
	 * @param ids
	 */
	public static void addAccountManager(String ids){
		long supervisorId= getCurrentSupervisorId();
		
		if(supervisorId<=0){
			BackLoginCtrl.toBackLoginPre();
		}
	
		teamService.updateParentsBySupervisor(ids, supervisorId);
		flash.error("添加成功");
		directorInfoPre(1, 10);
	}
	
	
	public static void  addDireManagerPre(int currPage,int pageSize){
		PageBean<t_teams> page = teamsService.pageOfaccountManager(currPage, pageSize,1);
		render(page); 
	}
	
	/**
	 * 添加管理员父类的id
	 * @param ids
	 */
	public static void  addDireManager(String ids){
		long supervisorId= getCurrentSupervisorId();
		
		if(supervisorId<=0){
			BackLoginCtrl.toBackLoginPre();
		}
	
		teamService.updateParentsBySupervisor(ids, supervisorId);
		flash.error("添加成功");
		directorManagerListPre(1, 10, 2);
	}
	
	/**
	 * 分配离职员工的推广会员
	 * @param id,team_id,currPage,pageSize
	 */
	//liuyang begin 2016-12-30-----------------------------
	public static void allotMemberPre(long id,long team_id,int currPage,int pageSize){
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.SETTING_BASIC, null);
		
		long userId = params.get("user_id",long.class);
		if(userId==-1){
			promotionMemberPre(id,team_id,currPage,pageSize);
		}else{
			//List<Long> supers = new ArrayList<>();
			//supers.add(params.get("t_user_id", Long.class));
			/*if(supers.size()>0){
			for (Long long1 : supers) {
				if(long1==null){
					continue;
				}
				userService.updateBySupervisorId(long1, userId);
			}
				promotionMemberPre(id,team_id,currPage,pageSize);
			}else{
				promotionMemberPre(id,team_id,currPage,pageSize);
			}*/
			Long[] superssLongs = params.get("t_user_id",Long[].class);
			if(superssLongs==null){
				promotionMemberPre(id,team_id,currPage,pageSize);
			}
			if(superssLongs.length>0){
				for (Long long1 : superssLongs) {
					if(long1==null){
						continue;
					}
					userService.updateBySupervisorId(long1, userId);
				}
				promotionMemberPre(id,team_id,currPage,pageSize);
			}else{
				promotionMemberPre(id,team_id,currPage,pageSize);
			}				
		}
	}
	//liuyang end 2016-12-30-------------------------------
	
	/**
	 * 查看该经理的推广会员
	 * @param id
	 */
	public static void promotionMemberPre(long id,long team_id,int currPage,int pageSize){
		
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		PageBean<t_user> page = userService.queryBySupervisorList(currPage, pageSize, id,0);
		
		//liuyang begin  2016-12-30-----------------------------
		
		//得到当前业务员信息
		t_supervisor sup_current = supervisorService.findByID(id);
		LockStatus LockStatuss = sup_current.getLock_status();
		//得到本部门所有成员信息
		t_teams tea_currrent = teamsService.findByID(team_id);
		List<t_teams> listt = teamsService.queryListByPrentId(tea_currrent.parent_id);
		List<t_supervisor> supers = new ArrayList<t_supervisor>();
		for (t_teams tea : listt) {
			t_supervisor supervisor = supervisorService.findByID(tea.supervisor_id); 
			supers.add(supervisor);
		}
		supers.remove(sup_current);
		supers.add(supervisorService.findByID(tea_currrent.parent_id));
				
		//liuyang end  2016-12-30------------------------------
		
		if(exports == Constants.EXPORT){
			List<t_user> list =page.page;
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			File file = ExcelUtils.export("业务经理我的推广会员",
					arrList,
					new String[] {
						"编号", "职位", "会员名", "真实姓名", "推广时间 ", "手机号码 ", "本月理财金额 ", "推广理财总金额"
					},
					new String[] {
					"id","position", "name", "realName", "time", "mobile", "monthAmount", "investAmount"
					}
				);
			renderBinary(file, "业务经理我的推广会员"+".xls");
		}
		
		render(page,id,LockStatuss,supers,team_id);
	}
	
	/**
	 * 推广主任
	 * @param id
	 * @param currPage
	 * @param pageSize
	 */
	public static void  promotionDirectorPre(long id,int currPage,int pageSize ){
		PageBean<t_teams> page = teamService.findDirectorList(currPage, pageSize, 0,id,0 );
		
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		if(exports == Constants.EXPORT){
			List<t_teams> list =page.page;
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			File file = ExcelUtils.export("业务部经理我的团队",
					arrList,
					new String[] {
						"编号", "职位", "会员名", "真实姓名", "手机号码 ", "本月理财金额 ", "本月佣金", "推广理财总金额 ","总佣金 "
					},
					new String[] {
					"id","position", "name", "realName", "mobile", "month_money", "month_commission", "total_money","total_commission"
					}
				);
			renderBinary(file, "业务部经理我的团队"+".xls");
		}
		
		render(page,id);
	}
	
	/**
	 * 删除业务员
	 * @param id
	 */
	public static void delete(long id, int type){
		ResultInfo resultInfo = new ResultInfo();
		
		long supervisorId =getCurrentSupervisorId();
		
		resultInfo =teamsService.delete(id, type, resultInfo);
		
		flash.error(resultInfo.msg);
		directorInfoPre(1, 10);
	}
	
	/**
	 * 删除业务经理
	 * @param id
	 */
	public static void delectManager(long id ){
		
		ResultInfo resultInfo = new ResultInfo();
		
		long supervisorId =getCurrentSupervisorId();
		
		resultInfo =teamsService.delete(id, 1, resultInfo);
		
		flash.error(resultInfo.msg);
		directorManagerInfoPre(1, 10);
		
	}
	/**
	 * 给部分业务员特殊提成
	 * lvweihuan
	 */
	public static void differentCommissionPre(Integer selMonth,Long comId){
		List<DiscountRate> drList=new ArrayList<DiscountRate>();
		TpService tpService=new TpService();
		
		if(selMonth==null){selMonth=0;}
		if(comId==null){
			
			comId=settingDao.findByColumn("_key=?", "special_commission1").id;
			
		}
		
		List<Long> comIdList=new ArrayList<Long>();
		comIdList.add(settingDao.findByColumn("_key=?", "special_commission1").id);
		comIdList.add(settingDao.findByColumn("_key=?", "special_commission2").id);
		
		
		t_setting_platform setting1=settingDao.findByColumn("id=?",comId);
		JSONArray jsonArray = JSONArray.fromObject(setting1._value);
		drList = (List)JSONArray.toList(jsonArray, new DiscountRate(), new JsonConfig());
		
		t_teams.getT().setMonth(selMonth);
		
		List<t_teams> teamList=teamsService.findListByColumn("curr_commission=?", comId);
		
		if(teamList!=null && teamList.size()>0){
			for(t_teams salesman:teamList){
				//salesman.total_per_commission=tpService.caculateCommission(salesman.supervisor_id, selMonth, drList);
				salesman.actual_month_commission_special=tpService.caculateActualMonthCommissionSpecial(salesman.supervisor_id, selMonth, drList);
			}
		}else{
			teamList=null;
		}
		List<t_teams> allTeams=new ArrayList<t_teams>();
		allTeams=teamsService.findListByColumn("type=? OR type=?", 0, 4);
		
		render(teamList,comId,comIdList,selMonth,allTeams);
	}
	
	
	
	public static void deleDifferentCommissionPre(Integer selMonth,Long comId,Long teamId){
		t_teams teams=teamsService.findByID(teamId);
		teams.curr_commission=null;
		teams.save();
		differentCommissionPre(selMonth,comId);
	}
	public static void addDiffComm(long teamXid,long comId,Integer selMonth){
		t_teams teams=teamsService.findByID(teamXid);
		teams.curr_commission=comId;
		teams.save();
		differentCommissionPre(selMonth,comId);
	}
}
