package controllers.back.spread;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_event_supervisor.Event;
import models.ext.experience.entity.t_experience_bid;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import service.ext.experiencebid.ExperienceBidService;
import service.ext.experiencebid.ExperienceBidSettingService;

import com.shove.Convert;

import common.constants.Constants;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;

/**
 * 后台-推广-体验标
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年2月16日
 */
public class ExperienceBidCtrl extends BackBaseController{
	
	protected static ExperienceBidSettingService experienceBidSettingServiceService = Factory.getService(ExperienceBidSettingService.class);
	
	protected static ExperienceBidService experienceBidService = Factory.getService(ExperienceBidService.class);
	
	/**
	 * 后台-推广-体验标-体验标规则
	 *  
	 * @rightID 702001
	 *
	 * @author yaoyi
	 * @createDate 2016年2月16日
	 */
	public static void showExperienceBidPre(){
		
		Map<String, Object> map = experienceBidSettingServiceService.queryExperienceBidInfo();
		
		render(map);
	}
	
	/**
	 * 后台-推广-体验标-保存体验金发放规则
	 *  
	 * @rightID 702002
	 * 
	 * @param opportunityCount 机会次数
	 * @param everyGrant 每次发放
	 *
	 * @author yaoyi
	 * @createDate 2016年2月16日
	 */
	public static void saveExperienceGold(int opportunityCount, double everyGrant){
		ResultInfo result = new ResultInfo();
		
		if (opportunityCount < 0 || opportunityCount > 10) {
			result.code = -1;
			result.msg = "机会次数只能在[0~10]之间!";
			
			renderJSON(result);
		}
		
		if (everyGrant < 1000 || everyGrant%1000!=0 || everyGrant > 100000000) {
			result.code = -1;
			result.msg = "每次发放金额只能位于1000~1亿之间，且必须为1000的正整数倍!";
			
			renderJSON(result);
		}
		
		result = experienceBidSettingServiceService.saveExperienceGold(opportunityCount, everyGrant);
		if (result.code < 1) {
			LoggerUtil.error(true, "保存体验金发放规则失败!");
		}
		
		/* 添加管理员事件 */
		long supervisor_id = getCurrentSupervisorId();
		
		supervisorService.addSupervisorEvent(supervisor_id, Event.SPREAD_EDIT_EXPERIENCEGOLD, null);
		
		renderJSON(result);
	}
	
	/**
	 * 后台-推广-体验标-体验项目发布规则
	 *
	 * @rightID 702003
	 * 
	 * @param raiseTime 筹款时间(天数)
	 * @param period 体验标期限(天数)
	 * @param apr 年利率
	 *
	 * @author yaoyi
	 * @createDate 2016年2月16日
	 */
	public static void saveExperienceBid(int raiseTime, int period, double apr){
		ResultInfo result = new ResultInfo();
		
		if (raiseTime < 0 || raiseTime > 100) {
			result.code = -1;
			result.msg = "筹款时间[0~100]之间!";
			
			renderJSON(result);
		}
		
		if (period<0 || period>30) {
			result.code = -1;
			result.msg = "期限只能在[0~30]之间!";
			
			renderJSON(result);
		}
		
		if (apr<0 || period>100) {
			result.code = -1;
			result.msg = "年利率[0%~100%]之间!";
			
			renderJSON(result);
		}
		
		result = experienceBidSettingServiceService.saveExperienceBid(raiseTime, period, apr);
		if (result.code < 1) {
			LoggerUtil.error(true, "保存体验项目发布规则");
		}
		
		/* 添加管理员事件 */
		long supervisor_id = getCurrentSupervisorId();
		
		supervisorService.addSupervisorEvent(supervisor_id, Event.SPREAD_EDIT_EXPERIENCEBID, null);
		
		renderJSON(result);
	}
	
	/**
	 * 推广-体验标-体验项目
	 * 
	 * @rightID 703001
	 *
	 * @param showType
	 * @param currPage
	 * @param pageSize
	 * @param exports 1:导出 default：不导出
	 *
	 * @author yaoyi
	 * @createDate 2016年2月17日
	 */
	public static void showExperienceBidListPre(int showType, int currPage, int pageSize){
		//导出
		int exports = Convert.strToInt(params.get("exports"), 0);
		
		if (showType < 1) {
			showType = 0;
		}
		
		PageBean<t_experience_bid> pageBean = experienceBidService.pageOfexperienceBid(showType, currPage, pageSize, exports);
		
		//导出
		if ( exports == Constants.EXPORT ) {
			List<t_experience_bid> list = pageBean.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject experienceBid = (JSONObject)obj;
				
				experienceBid.put("apr", experienceBid.get("apr")+"%");
				experienceBid.put("invest_period", experienceBid.get("invest_period")+"天");
				experienceBid.put("invest_count", experienceBid.get("invest_count")+"人次");
				
				if(StringUtils.isNotBlank(experienceBid.getString("status"))){
					experienceBid.put("status", t_experience_bid.Status.valueOf(experienceBid.getString("status")).value);
				}
			}
			
			String fileName="体验项目";
			switch (showType) {
				case 1:
					fileName = "借款中的体验项目";
					break;
				case 2:
					fileName = "还款中的体验项目";
					break;
				case 3:
					fileName = "已结束的体验项目";
					break;
				default:
					fileName = "体验项目";
					break;
			}
			
			File file = ExcelUtils.export(fileName,
			arrList,
			new String[] {
			"项目编号 ", "项目", "年利率", "期限", "项目金额", "加入人次", "发布时间", "放款时间", "状态"},
			new String[] {
			"bid_no","title", "apr", "invest_period", "has_invest_amount", "invest_count", "time", "release_time", "status"});
			   
			renderBinary(file, fileName + ".xls");
		}
		
		/** 查询项目总额 */
		double totalGold = experienceBidService.findTotalGold(showType);
		
		render(pageBean, totalGold, showType);
	}
	
	/**
	 * 推广-体验标-体验项目-发布体验项目-(1.进入发布体验标项目界面)
	 * 
	 * @rightID 703002
	 *
	 * @param title
	 *
	 * @author yaoyi
	 * @createDate 2016年2月17日
	 */
	public static void createExperienceBidPre(String title){
		
		render(title);
	}
	
	/**
	 * 推广-体验标-体验项目-发布体验项目-(2.发布体验项目)
	 * 
	 * @rightID 703002
	 *
	 * @param title
	 *
	 * @author yaoyi
	 * @createDate 2016年2月17日
	 */
	public static void createExperienceBid(String title){
		checkAuthenticity();
		
		if (StringUtils.isBlank(title) || title.length() > 15) {
			flash.error("体验标标题在15字以内!");

			createExperienceBidPre(title);
		}
		
		ResultInfo result = experienceBidService.createExperienceBid(title);
		if (result.code < 1) {
			LoggerUtil.error(true, "创建体验标[%s]失败", title);

			flash.error(result.msg);
			createExperienceBidPre(title);
		}
		
		/* 添加管理员事件 */
		long supervisor_id = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		t_experience_bid experienceBid = (t_experience_bid) result.obj;
		supervisorEventParam.put("bid_no", experienceBid.getBid_no());
		supervisorEventParam.put("bid_name", experienceBid.title);
		supervisorService.addSupervisorEvent(supervisor_id, Event.SPREAD_EXPERIENCEBID_CREAT, supervisorEventParam);
		
		flash.success("发布体验标成功!");
		showExperienceBidListPre(0, 1, 10);
	}

	/**
	 * 推广-体验标-体验项目-体验项目放款
	 * 
	 * @rightID 703003
	 *
	 * @param experienceBidId
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public static void release(long experienceBidId){
		checkAuthenticity();
		
		ResultInfo result = experienceBidService.release(experienceBidId);
		if(result.code < 1){
			flash.error(result.msg);
			LoggerUtil.error(true, "体验标[%s]放款失败", experienceBidId);
			
			showExperienceBidListPre(0,1,10);
		}else if (result.code == 99){

			flash.success(result.msg);
		}else {
			
			flash.success("放款成功!已成功将体验标置为[还款中]");
		}
		
		/* 添加管理员事件 */
		long supervisor_id = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		t_experience_bid experienceBid = (t_experience_bid) result.obj;
		supervisorEventParam.put("bid_no", experienceBid.getBid_no());
		supervisorEventParam.put("bid_name", experienceBid.title);
		supervisorService.addSupervisorEvent(supervisor_id, Event.SPREAD_EXPERIENCEBID_RELEASE, supervisorEventParam);
		
		showExperienceBidListPre(0,1,10);
	}

}
