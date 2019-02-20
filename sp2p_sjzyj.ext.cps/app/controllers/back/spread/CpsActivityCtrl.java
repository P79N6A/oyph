package controllers.back.spread;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.common.BackBaseController;
import models.common.entity.t_advertisement;
import models.common.entity.t_event_supervisor;
import models.ext.cps.bean.CpsAwardRecord;
import models.ext.cps.entity.t_cps_activity;
import models.ext.cps.entity.t_cps_award;
import models.ext.cps.entity.t_cps_award_record;
import models.ext.cps.entity.t_cps_integral;
import models.ext.cps.entity.t_cps_packet;
import models.ext.cps.entity.t_cps_rate;
import services.ext.cps.CpsActivityService;
import services.ext.cps.CpsAwardRecordServcie;
import services.ext.cps.CpsAwardService;
import services.ext.cps.CpsIntegralService;
import services.ext.cps.CpsPacketService;
import services.ext.cps.CpsRateService;
import services.ext.cps.CpsService;

/**
 * 后台-推广-CPS-CPS活动
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年6月12日
 */
public class CpsActivityCtrl extends BackBaseController {
	
	protected static CpsActivityService cpsActivityService = Factory.getService(CpsActivityService.class);
	
	protected static CpsPacketService cpsPacketService = Factory.getService(CpsPacketService.class);
	
	protected static CpsRateService cpsRateService = Factory.getService(CpsRateService.class);
	
	protected static CpsIntegralService cpsIntegralService = Factory.getService(CpsIntegralService.class);
	
	protected static CpsAwardService cpsAwardService = Factory.getService(CpsAwardService.class);
	
	protected static CpsService cpsService = Factory.getService(CpsService.class);
	
	protected static CpsAwardRecordServcie cpsAwardRecordService = Factory.getService(CpsAwardRecordServcie.class);

	/**
	 * cps活动列表页
	 * 
	 * @param currPage
	 * @param pageSize
	 * @return
	 * @createDate 2018年6月12日
	 * @author liuyang
	 */
	public static void showCpsActivitysPre(int currPage,int pageSize){
		if(currPage<1){
	        currPage=1;
	    }
	    if(pageSize<1){
	        pageSize=10;
	    }
	    
	    String title = params.get("title");
	    
	    PageBean<t_cps_activity> page = cpsActivityService.queryCpsActivity(currPage, pageSize, title);
	    
		render(page, title);
	}
	
	/**
	 * cps活动添加页
	 * 
	 * @return
	 * @createDate 2018年6月12日
	 * @author liuyang
	 */
	public static void toAddCpsActivityPre() {
		
		render();
	}
	
	/**
	 * cps活动保存
	 * 
	 * @param title
	 * @param beginTime
	 * @param endTime
	 * @return
	 * @createDate 2018年6月12日
	 * @author liuyang
	 */
	public static void saveCpsActivity(String title, String beginTime, String endTime) {
		if(getCurrentSupervisorId()==null){
	        flash.error("系统忙，请稍后再试");
	        toAddCpsActivityPre();
	    }
	    
	    if(StringUtils.isBlank(beginTime) || StringUtils.isBlank(endTime)){
	        flash.error("时间不能为空");
	        toAddCpsActivityPre();
	    }
	    
	    Date startTime = DateUtil.strToDate(beginTime,  Constants.DATE_PLUGIN);
	    Date nextTime = DateUtil.strToDate(endTime,  Constants.DATE_PLUGIN);
	    if(!DateUtil.isDateBefore(startTime, nextTime)){
	        flash.error("活动开始时间需小于结束时间！");
	        toAddCpsActivityPre();
	    }
	    
	    t_cps_activity cps = new t_cps_activity();
	    
	    cps.title = title;
	    cps.begin_time = startTime;
	    cps.end_time = nextTime;
	    cps.time = new Date();
	    cps.first_type = 1;
	    cps.register_type = 1;
	    cps.save();
	    
	    flash.success("保存活动成功！");
        
	    showCpsActivitysPre(1, 5);
	}
	
	/**
	 * cps活动编辑页
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年6月12日
	 * @author liuyang
	 */
	public static void toEditCpsActivityPre(long cpsActivityId) {
		
		t_cps_activity cps = cpsActivityService.findByID(cpsActivityId);
		
		if(cps == null) {
			flash.error("未找到相应活动，请重试");
			showCpsActivitysPre(1,5);
		}
		
		render(cps);
	}
	
	/**
	 * cps活动修改保存
	 * 
	 * @param cpsActivityId
	 * @param title
	 * @param beginTime
	 * @param endTime
	 * @return
	 * @createDate 2018年6月12日
	 * @author liuyang
	 */
	public static void editCpsActivity(long cpsActivityId, String title, String beginTime, String endTime) {
		t_cps_activity cps = cpsActivityService.findByID(cpsActivityId);
		
		if(cps == null) {
			flash.error("未找到相应活动，请重试");
			showCpsActivitysPre(1,5);
		}
		
		if(getCurrentSupervisorId()==null){
	        flash.error("系统忙，请稍后再试");
	        toAddCpsActivityPre();
	    }
	    
	    if(StringUtils.isBlank(beginTime) || StringUtils.isBlank(endTime)){
	        flash.error("时间不能为空");
	        toAddCpsActivityPre();
	    }
	    
	    Date startTime = DateUtil.strToDate(beginTime,  Constants.DATE_PLUGIN);
	    Date nextTime = DateUtil.strToDate(endTime,  Constants.DATE_PLUGIN);
	    if(!DateUtil.isDateBefore(startTime, nextTime)){
	        flash.error("活动开始时间需小于结束时间！");
	        toAddCpsActivityPre();
	    }
	    
	    cps.title = title;
	    cps.begin_time = startTime;
	    cps.end_time = nextTime;
	    cps.register_type = 1;
	    cps.first_type = 1;
	    cps.save();
	    
	    flash.success("修改活动成功！");
        
	    showCpsActivitysPre(1, 5);
	}
	
	/**
	 * 广告图片 上下架
	 * @rightID 203004
	 *
	 * @param sign 广告图片加密id
	 * @return
	
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void isUseCps(String sign){
		ResultInfo result = Security.decodeSign(sign, Constants.ADS_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);             
		}
		long cpsId = Long.parseLong((String) result.obj);
		t_cps_activity cpss = cpsActivityService.findByID(cpsId);
		
		if(cpss==null){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
		}
		
		boolean isUseFlag = cpsActivityService.updateVoteIsUse(cpsId, !cpss.getIs_use().code);
		
		if(!isUseFlag){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
		}
		else{
			//添加管理员事件
			long supervisorId = getCurrentSupervisorId();
			Map<String, String> supervisorEventParam = new HashMap<String, String>();  
			supervisorEventParam.put("information_id", "");  
			supervisorEventParam.put("information_name", "cps活动上下架");
			if(cpss.getIs_use().code){
				//下架
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_DISABLED, supervisorEventParam);
			}else{
				//上架
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_ENABLE, supervisorEventParam);			
			}
		}
		
		result.code=1;
		result.msg="上下架操作成功";
		result.obj=cpss.getIs_use().code;
		
		renderJSON(result);
	}
	
	/**
	 * cps活动奖项设置页面
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年6月12日
	 * @author liuyang
	 */
	public static void toSetAwardPre(long cpsActivityId) {
		
		t_cps_activity cps = cpsActivityService.findByID(cpsActivityId);
		
		if(cps == null) {
			flash.error("未找到相应活动，请重试");
			showCpsActivitysPre(1,5);
		}
		
		//新用户注册奖励红包列表
		List<t_cps_packet> registerPackets = cpsPacketService.queryPacketListByNew(cps.id, 0);
		
		//新用户注册奖励老用户红包
		t_cps_packet registerPacket = cpsPacketService.queryPacketByType(cps.id, 0);
		
		//新用户注册并实名时给老用户奖励(1红包 2 加息卷 3积分)
		if(cps.register_type == 1) {
			t_cps_packet registerPacketOld = cpsPacketService.queryPacketByType(cps.id, 1);
			renderArgs.put("registerPacketOld", registerPacketOld);
		}else if(cps.register_type == 2){
			t_cps_rate registerRateOld = cpsRateService.queryRateByType(cps.id, 1);
			renderArgs.put("registerRateOld", registerRateOld);
		}else{
			t_cps_integral registerIntegralOld = cpsIntegralService.queryIntegralByType(cps.id, 0);
			renderArgs.put("registerIntegralOld", registerIntegralOld);
		}
		
		//新用户首投时给老用户奖励(1红包 2 加息卷 3积分)
		if(cps.first_type == 1) {
			t_cps_packet firstPacketOld = null;
			renderArgs.put("firstPacketOld", firstPacketOld);
		}else if(cps.first_type == 2){
			t_cps_rate firstRateOld = cpsRateService.queryRateByType(cps.id, 2);
			renderArgs.put("firstRateOld", firstRateOld);
		}else{
			t_cps_integral firstIntegralOld = cpsIntegralService.queryIntegralByType(cps.id, 1);
			renderArgs.put("firstIntegralOld", firstIntegralOld);
		}
		
		//老用户推广新用户注册并投资奖励红包列表
		List<t_cps_packet> registerAndInvestPackets = cpsPacketService.queryPacketListByType(cps.id, 2);
		
		//奖项结算列表
		List<t_cps_award> awards = cpsAwardService.queryAwardsByActivityId(cps.id);
		
		render(cps, registerPackets, registerPacket, awards, registerAndInvestPackets);
	}
	
	/**
	 * cps活动奖项设置添加新用户注册奖励
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年6月15日
	 * @author liuyang
	 */
	public static void addNewPacket(long cpsActivityId, Double money, Integer allotte_day, Double condition, Integer lock_day) {
		
		t_cps_activity cps = cpsActivityService.findByID(cpsActivityId);
		
		if(cps == null) {
			flash.error("提交失败，请重试");
			showCpsActivitysPre(1,5);
		}
		
		if(money != null && allotte_day != null && condition != null && lock_day != null) {
			t_cps_packet packet = new t_cps_packet();
			packet.t_cps_id = cps.id;
			packet.money = money;
			packet.allotte_day = allotte_day;
			packet.lock_day = lock_day;
			packet.condition_money = condition;
			packet.time = new Date();
			packet.type = 0;
			packet.status = 0;
			packet.save();
		}else{
			flash.error("添加失败，请重试");
			toSetAwardPre(cps.id);
		}
		
		flash.success("保存成功！");
		toSetAwardPre(cps.id);
	}
	
	/**
	 * cps活动奖项设置添加新用户注册奖励
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年9月26日
	 * @author liuyang
	 */
	public static void addRegisterAndInvestPacket(long cpsActivityId, Double money, Integer allotte_day, Double condition, Integer lock_day) {
		
		t_cps_activity cps = cpsActivityService.findByID(cpsActivityId);
		
		if(cps == null) {
			flash.error("提交失败，请重试");
			showCpsActivitysPre(1,5);
		}
		
		if(money != null && allotte_day != null && condition != null && lock_day != null) {
			t_cps_packet packet = new t_cps_packet();
			packet.t_cps_id = cps.id;
			packet.money = money;
			packet.allotte_day = allotte_day;
			packet.lock_day = lock_day;
			packet.condition_money = condition;
			packet.time = new Date();
			packet.type = 2;
			packet.status = 1;
			packet.save();
		}else{
			flash.error("添加失败，请重试");
			toSetAwardPre(cps.id);
		}
		
		flash.success("保存成功！");
		toSetAwardPre(cps.id);
	}
	
	/**
	 * cps活动奖项设置保存老用户推广新用户注册并投资红包奖励
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年9月26日
	 * @author liuyang
	 */
	public static void saveRegisterAndInvestPacket(long cpsActivityId, double[] money, int[] allotte_day, double[] condition, int[] lock_day) {
		
		t_cps_activity cps = cpsActivityService.findByID(cpsActivityId);
		
		if(cps == null) {
			flash.error("提交失败，请重试");
			showCpsActivitysPre(1,5);
		}
		
		//奖励红包列表
		List<t_cps_packet> registerAndInvestPackets = cpsPacketService.queryPacketListByType(cps.id, 2);
		if(registerAndInvestPackets != null && registerAndInvestPackets.size()>0) {
			for (int i = 0; i < registerAndInvestPackets.size(); i++) {
				registerAndInvestPackets.get(i).delete();
			}
		}
		
		int moneyNum = money.length;
		int allotte_day_num = allotte_day.length;
		int conditionNum = condition.length;
		int lock_day_num = lock_day.length;
		
		if(moneyNum != allotte_day_num || moneyNum != conditionNum || moneyNum != lock_day_num) {
			flash.error("提交失败，请重试");
			showCpsActivitysPre(1,5);
		}
		
		//循环保存奖励红包
		if(money != null && money.length>0) {
			for (int i = 0; i < money.length; i++) {
				t_cps_packet packet = new t_cps_packet();
				packet.t_cps_id = cps.id;
				packet.money = money[i];
				packet.allotte_day = allotte_day[i];
				packet.lock_day = lock_day[i];
				packet.condition_money = condition[i];
				packet.time = new Date();
				packet.type = 2;
				packet.status = 1;
				packet.save();
			}
		}
		
		flash.success("保存成功！");
		toSetAwardPre(cps.id);
	}
	
	/**
	 * cps活动奖项设置保存新用户注册奖励
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年6月12日
	 * @author liuyang
	 */
	public static void saveNewPacket(long cpsActivityId, double[] money, int[] allotte_day, double[] condition, int[] lock_day) {
		
		t_cps_activity cps = cpsActivityService.findByID(cpsActivityId);
		
		if(cps == null) {
			flash.error("提交失败，请重试");
			showCpsActivitysPre(1,5);
		}
		
		//新用户注册奖励红包列表
		List<t_cps_packet> registerPackets = cpsPacketService.queryPacketListByNew(cps.id, 0);
		if(registerPackets != null && registerPackets.size()>0) {
			for (int i = 0; i < registerPackets.size(); i++) {
				registerPackets.get(i).delete();
			}
		}
		
		int moneyNum = money.length;
		int allotte_day_num = allotte_day.length;
		int conditionNum = condition.length;
		int lock_day_num = lock_day.length;
		
		if(moneyNum != allotte_day_num || moneyNum != conditionNum || moneyNum != lock_day_num) {
			flash.error("提交失败，请重试");
			showCpsActivitysPre(1,5);
		}
		
		//循环保存奖励红包
		if(money != null && money.length>0) {
			for (int i = 0; i < money.length; i++) {
				t_cps_packet packet = new t_cps_packet();
				packet.t_cps_id = cps.id;
				packet.money = money[i];
				packet.allotte_day = allotte_day[i];
				packet.lock_day = lock_day[i];
				packet.condition_money = condition[i];
				packet.time = new Date();
				packet.type = 0;
				packet.status = 0;
				packet.save();
			}
		}
		
		flash.success("保存成功！");
		toSetAwardPre(cps.id);
	}
	
	/**
	 * cps活动奖项设置保存老客户奖励
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年6月12日
	 * @author liuyang
	 */
	public static void saveOldPacket(long cpsActivityId, int register_type, int first_type, double integral_ratio, int cutoff_time) {
		
		t_cps_activity cps = cpsActivityService.findByID(cpsActivityId);
		
		if(cps == null) {
			flash.error("提交失败，请重试");
			showCpsActivitysPre(1,5);
		}
		
		cpsPacketService.deletePacketByType(cps.id, 0);
		
		//新用户注册奖励老用户红包
		double registerPacketMoney = 0.00;
		int registerPacketAllotte_day = 0;
		double registerPacketCondition_money = 0.00;
		int registerPacketLock_day = 0;
		
		String registerPacketMoneys = params.get("registerPacketMoney");
		if(registerPacketMoneys != null) {
			registerPacketMoney = Double.parseDouble(registerPacketMoneys);
		}
		String registerPacketAllotte_days = params.get("registerPacketAllotte_day");
		if(registerPacketAllotte_days != null) {
			registerPacketAllotte_day = Integer.parseInt(registerPacketAllotte_days);
		}
		String registerPacketCondition_moneys = params.get("registerPacketCondition_money");
		if(registerPacketCondition_moneys != null) {
			registerPacketCondition_money = Double.parseDouble(registerPacketCondition_moneys);
		}
		String registerPacketLock_days = params.get("registerPacketLock_day");
		if(registerPacketLock_days != null) {
			registerPacketLock_day = Integer.parseInt(registerPacketLock_days);
		}
		
		t_cps_packet registerPacket = new t_cps_packet();
		registerPacket.t_cps_id = cps.id;
		registerPacket.status = 1;
		registerPacket.money = registerPacketMoney;
		registerPacket.allotte_day = registerPacketAllotte_day;
		registerPacket.lock_day = registerPacketLock_day;
		registerPacket.condition_money = registerPacketCondition_money;
		registerPacket.type = 0;
		registerPacket.time = new Date();
		registerPacket.save();
		
		//删除新用户注册实名时给老用户的奖励
		cpsPacketService.deletePacketByType(cps.id, 1);
		cpsRateService.deleteRateByType(cps.id, 1);
		cpsIntegralService.deleteIntegralByType(cps.id, 0);
		
		//新用户注册并实名时给老用户奖励(1红包 2 加息卷 3积分)
		if(register_type == 1) {
			double money = 0.00;
			int allotte_day = 0;
			double condition = 0;
			int lock_day = 0;
			String moneys = params.get("registerPacketOldMoney");
			if(moneys != null) {
				money = Double.parseDouble(moneys);
			}
			String allotte_days = params.get("registerPacketOldAllotteDay");
			if(allotte_days != null) {
				allotte_day = Integer.parseInt(allotte_days);
			}
			String lock_days = params.get("registerPacketOldLockDay");
			if(lock_days != null) {
				lock_day = Integer.parseInt(lock_days);
			}
			String conditions = params.get("registerPacketOldCondition");
			if(conditions != null) {
				condition = Double.parseDouble(conditions);
			}
			t_cps_packet registerPacketOld = new t_cps_packet();
			registerPacketOld.t_cps_id = cps.id;
			registerPacketOld.status = 1;
			registerPacketOld.money = money;
			registerPacketOld.allotte_day = allotte_day;
			registerPacketOld.lock_day = lock_day;
			registerPacketOld.condition_money = condition;
			registerPacketOld.type = 1;
			registerPacketOld.time = new Date();
			registerPacketOld.save();
		}else if(register_type == 2){
			double count = 0.00;
			int allotte_day = 0;
			double condition = 0.00;
			int lock_day = 0;
			String counts = params.get("registerRateOldCount");
			if(counts != null) {
				count = Double.parseDouble(counts);
			}
			String allotte_days = params.get("registerRateOldAllotteDay");
			if(allotte_days != null) {
				allotte_day = Integer.parseInt(allotte_days);
			}
			String lock_days = params.get("registerRateOldLockDay");
			if(lock_days != null) {
				lock_day = Integer.parseInt(lock_days);
			}
			String conditions = params.get("registerRateOldCondition");
			if(conditions != null) {
				condition = Double.parseDouble(conditions);
			}
			t_cps_rate registerRateOld = new t_cps_rate();
			registerRateOld.t_cps_id = cps.id;
			registerRateOld.status = 1;
			registerRateOld.count = count;
			registerRateOld.allotte_day = allotte_day;
			registerRateOld.lock_day = lock_day;
			registerRateOld.condition_money = condition;
			registerRateOld.type = 1;
			registerRateOld.time = new Date();
			registerRateOld.save();
		}else if(register_type == 3){
			int integral = 0;
			String integrals = params.get("registerIntegralOldIntegral");
			if(integrals != null) {
				integral = Integer.parseInt(integrals);
			}
			t_cps_integral registerIntegralOld = new t_cps_integral();
			registerIntegralOld.t_cps_id = cps.id;
			registerIntegralOld.type = 0;
			registerIntegralOld.time = new Date();
			registerIntegralOld.integral = integral;
			registerIntegralOld.save();
		}
		
		//删除新用户注册实名时给老用户的奖励
		cpsPacketService.deletePacketByType(cps.id, 2);
		cpsRateService.deleteRateByType(cps.id, 2);
		cpsIntegralService.deleteIntegralByType(cps.id, 1);
		
		//新用户首投时给老用户奖励(1红包 2 加息卷 3积分)
		if(first_type == 1) {
			double money = 0.00;
			int allotte_day = 0;
			double condition = 0;
			int lock_day = 0;
			String moneys = params.get("firstPacketOldMoney");
			if(moneys != null) {
				money = Double.parseDouble(moneys);
			}
			String allotte_days = params.get("firstPacketOldAllotteDay");
			if(allotte_days != null) {
				allotte_day = Integer.parseInt(allotte_days);
			}
			String lock_days = params.get("firstPacketOldLockDay");
			if(lock_days != null) {
				lock_day = Integer.parseInt(lock_days);
			}
			String conditions = params.get("firstPacketOldCondition");
			if(conditions != null) {
				condition = Double.parseDouble(conditions);
			}
			t_cps_packet firstPacketOld = new t_cps_packet();
			firstPacketOld.t_cps_id = cps.id;
			firstPacketOld.status = 1;
			firstPacketOld.money = money;
			firstPacketOld.allotte_day = allotte_day;
			firstPacketOld.lock_day = lock_day;
			firstPacketOld.condition_money = condition;
			firstPacketOld.type = 2;
			firstPacketOld.time = new Date();
			firstPacketOld.save();
		}else if(first_type == 2){
			double count = 0.00;
			int allotte_day = 0;
			double condition = 0.00;
			int lock_day = 0;
			String counts = params.get("firstRateOldCount");
			if(counts != null) {
				count = Double.parseDouble(counts);
			}
			String allotte_days = params.get("firstRateOldAllotteDay");
			if(allotte_days != null) {
				allotte_day = Integer.parseInt(allotte_days);
			}
			String lock_days = params.get("firstRateOldLockDay");
			if(lock_days != null) {
				lock_day = Integer.parseInt(lock_days);
			}
			String conditions = params.get("firstRateOldCondition");
			if(conditions != null) {
				condition = Double.parseDouble(conditions);
			}
			t_cps_rate firstRateOld = new t_cps_rate();
			firstRateOld.t_cps_id = cps.id;
			firstRateOld.status = 1;
			firstRateOld.count = count;
			firstRateOld.allotte_day = allotte_day;
			firstRateOld.lock_day = lock_day;
			firstRateOld.condition_money = condition;
			firstRateOld.type = 2;
			firstRateOld.time = new Date();
			firstRateOld.save();
		}else if(first_type == 3){
			int integral = 0;
			String integrals = params.get("firstIntegralOldIntegral");
			if(integrals != null) {
				integral = Integer.parseInt(integrals);
			}
			t_cps_integral firstIntegralOld = new t_cps_integral();
			firstIntegralOld.t_cps_id = cps.id;
			firstIntegralOld.type = 1;
			firstIntegralOld.time = new Date();
			firstIntegralOld.integral = integral;
			firstIntegralOld.save();
		}
		
		cpsActivityService.updateIntegral(cps.id, first_type, register_type, integral_ratio, cutoff_time);
		
		flash.success("保存成功！");
		toSetAwardPre(cps.id);
	}
	
	/**
	 * cps活动奖项设置删除红包
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年6月14日
	 * @author liuyang
	 */
	public static void deleteRegisterPacket(long cpsActivityId, long cpsPacketId) {
		
		t_cps_packet packet = cpsPacketService.findByID(cpsPacketId);
		if(packet != null){
			packet.delete();
		}
		
		flash.success("删除成功！");
		toSetAwardPre(cpsActivityId);
	}
	
	/**
	 * cps活动奖项设置老用户删除红包
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年6月14日
	 * @author liuyang
	 */
	public static void deleteRegisterAndInvestPacket(long cpsActivityId, long cpsRegisterAndInvestPacketId) {
		
		t_cps_packet packet = cpsPacketService.findByID(cpsRegisterAndInvestPacketId);
		if(packet != null){
			packet.delete();
		}
		
		flash.success("删除成功！");
		toSetAwardPre(cpsActivityId);
	}
	
	/**
	 * cps活动中奖结算
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年6月20日
	 * @author liuyang
	 */
	public static void awardAccount(long cpsActivityId) {
		
		t_cps_activity cps = cpsActivityService.findByID(cpsActivityId);
		
		if(cps == null) {
			flash.error("获取失败，请重试");
			showCpsActivitysPre(1,5);
		}

		Date nowTime = new Date();
		
		if(nowTime.getTime() <= cps.end_time.getTime() || !(cps.getIs_use().code)) {
			flash.error("结算失败，请重试");
			showCpsActivitysPre(1,5);
		}
		
		//本活动奖项列表
		List<t_cps_award> awards = cpsAwardService.queryAwardsByActivityId(cps.id);
		
		if(awards == null || awards.size()<=0 || awards.size()!=3) {
			flash.error("此活动还未设置奖项，请重试");
			showCpsActivitysPre(1,5);
		}
		
		List<CpsAwardRecord> cpsAwards = cpsService.queryCpsAwardRecords(cps.id);
		
		if(cpsAwards != null && cpsAwards.size()>0) {
			
			for (int i = 0; i < cpsAwards.size(); i++) {
				
				t_cps_award_record records = new t_cps_award_record();
				records.cps_activity_id = cps.id;
				
				if(cpsAwards.get(i).spread_num>=awards.get(0).num) {
					records.cps_award_id = awards.get(0).id;
					records.time = new Date();
					records.user_id = cpsAwards.get(i).id;
					records.save();
				}else if(cpsAwards.get(i).spread_num<awards.get(0).num && cpsAwards.get(i).spread_num>=awards.get(1).num){
					records.cps_award_id = awards.get(1).id;
					records.time = new Date();
					records.user_id = cpsAwards.get(i).id;
					records.save();
				}else if(cpsAwards.get(i).spread_num<awards.get(1).num && cpsAwards.get(i).spread_num>=awards.get(2).num){
					records.cps_award_id = awards.get(2).id;
					records.time = new Date();
					records.user_id = cpsAwards.get(i).id;
					records.save();
				}else{
					continue;
				}
				
			}
		}
		
		cps.account_type = 1;
		cps.save();
		
		flash.error("奖励结算成功！");
        
	    showCpsActivitysPre(1, 5);
	}
	
	/**
	 * cps活动奖项结算设置添加奖品
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年6月15日
	 * @author liuyang
	 */
	public static void addNewAward(long cpsActivityId, String awardName, Integer awardNum, Double awardAmount, String awardPrize) {
		
		t_cps_activity cps = cpsActivityService.findByID(cpsActivityId);
		
		if(cps == null) {
			flash.error("提交失败，请重试");
			showCpsActivitysPre(1,5);
		}
		
		if(awardName != null && awardPrize != null && awardNum != null && awardAmount != null) {
			t_cps_award award = new t_cps_award();
			award.t_cps_id = cps.id;
			award.amount = awardAmount;
			award.name = awardName;
			award.num = awardNum;
			award.prize = awardPrize;
			award.time = new Date();
			award.save();
		}else{
			flash.error("添加失败，请重试");
			toSetAwardPre(cps.id);
		}
		
		flash.success("保存成功！");
		toSetAwardPre(cps.id);
	}
	
	/**
	 * cps活动奖项结算设置修改保存奖品
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年6月15日
	 * @author liuyang
	 */
	public static void saveNewAward(long cpsActivityId, String[] awardName, int[] awardNum, double[] awardAmount, String[] awardPrize) {
		
		t_cps_activity cps = cpsActivityService.findByID(cpsActivityId);
		
		if(cps == null) {
			flash.error("提交失败，请重试");
			showCpsActivitysPre(1,5);
		}
		
		//奖项奖品列表
		List<t_cps_award> awards = cpsAwardService.queryAwardsByActivityId(cps.id);
		if(awards != null && awards.size()>0) {
			for (int i = 0; i < awards.size(); i++) {
				awards.get(i).delete();
			}
		}
		
		int moneyNum = awardName.length;
		int allotte_day_num = awardNum.length;
		int conditionNum = awardAmount.length;
		int lock_day_num = awardPrize.length;
		
		if(moneyNum != allotte_day_num || moneyNum != conditionNum || moneyNum != lock_day_num) {
			flash.error("提交失败，请重试");
			showCpsActivitysPre(1,5);
		}
		
		//循环保存奖项奖品
		if(awardName != null && awardName.length>0) {
			for (int i = 0; i < awardName.length; i++) {
				t_cps_award award = new t_cps_award();
				award.t_cps_id = cps.id;
				award.amount = awardAmount[i];
				award.name = awardName[i];
				award.num = awardNum[i];
				award.prize = awardPrize[i];
				award.time = new Date();
				award.save();
			}
		}
		
		flash.success("保存成功！");
		toSetAwardPre(cps.id);
	}
	
	/**
	 * cps活动奖项结算设置删除奖品
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年6月15日
	 * @author liuyang
	 */
	public static void deleteAward(long cpsActivityId, long cpsAwardId) {
		
		t_cps_award award = cpsAwardService.findByID(cpsAwardId);
		if(award != null){
			award.delete();
		}
		
		flash.success("删除成功！");
		toSetAwardPre(cpsActivityId);
	}
	
	/**
	 * cps活动中奖列表
	 * 
	 * @param cpsActivityId
	 * @return
	 * @createDate 2018年6月21日
	 * @author liuyang
	 */
	public static void toAwardRecordPre(int currPage,int pageSize, long cpsActivityId) {
		
		if(currPage<1){
	        currPage=1;
	    }
		
	    if(pageSize<1){
	        pageSize=10;
	    }
		
		t_cps_activity cps = cpsActivityService.findByID(cpsActivityId);
		
		if(cps == null) {
			flash.error("提交失败，请重试");
			showCpsActivitysPre(1,5);
		}
		
		PageBean<t_cps_award_record> page = cpsAwardRecordService.queryAwardRecordByActivityId(currPage, pageSize, cpsActivityId);
		
		render(page, cpsActivityId);
	}
}
