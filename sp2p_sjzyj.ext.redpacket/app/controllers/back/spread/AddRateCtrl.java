package controllers.back.spread;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import common.enums.NoticeScene;
import common.enums.NoticeType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import controllers.common.BackBaseController;
import controllers.thread.AddRateUserAllThread;
import daos.common.UserDao;
import models.common.entity.t_event_supervisor.Event;
import models.common.entity.t_user;
import models.core.entity.t_product;
import models.ext.redpacket.bean.MaketInvestAddBean;
import models.ext.redpacket.bean.MaketInvestRedBean;
import models.ext.redpacket.bean.MaketInvestRedBean.RedpacketStatus;
import models.ext.redpacket.entity.t_add_rate_act;
import models.ext.redpacket.entity.t_add_rate_ticket;
import models.ext.redpacket.entity.t_add_rate_user;
import models.ext.redpacket.entity.t_market_activity;
import models.ext.redpacket.entity.t_market_invest;
import models.ext.redpacket.entity.t_red_packet_user;
import services.common.NoticeService;
import services.core.ProductService;
import services.ext.redpacket.AddRateActService;
import services.ext.redpacket.AddRateTicketService;
import services.ext.redpacket.MarketActivityService;
import services.ext.redpacket.MarketInvestService;
import services.ext.redpacket.RedpacketService;

/**
 * 后台-推广-加息券
 * 
 * @author niu
 * @create 2017.10.26
 */
public class AddRateCtrl extends BackBaseController {
	
	protected static AddRateActService addRateActService = Factory.getService(AddRateActService.class);
	
	protected static AddRateTicketService addRateTicketService = Factory.getService(AddRateTicketService.class);
	
	protected static UserDao usersDao = Factory.getDao(UserDao.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static MarketActivityService marketActivityService = Factory.getService(MarketActivityService.class);
	
	protected static ProductService productservice = Factory.getService(ProductService.class);
	
	protected static MarketInvestService marketInvestService = Factory.getService(MarketInvestService.class);
	
	protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);
	/**
	 * 后台-推广-加息券活动主页
	 * 
	 * @author niu
	 * @create 2017.10.26
	 */
	public static void addRateActPre(int currPage,int pageSize) {
		
		PageBean<t_add_rate_act> page = addRateActService.listOfAct(currPage, pageSize);
		

		render(page);
	}
	
	/**
	 * 加息券活动-添加
	 * 
	 * @author niu
	 * @create 2017.10.26
	 */
	public static void insertAddRateAct(String name, Date stime, Date etime) {
		
		//判断参数
		if (name == null || "".equals(name)) {
			flash.error("请输入活动名称");
			addRateActPre(0, 12);
		}
		
		if (name.length() > 32) {
			flash.error("活动名称太长");
			addRateActPre(0, 12);
		}
		
		if (stime == null) {
			flash.error("请输入活动开始时间");
			addRateActPre(0, 12);
		}
		
		if (etime == null) {
			flash.error("请输入活动结束时间");
			addRateActPre(0, 12);
		}
		
		if (stime.compareTo(etime) > 0) {
			flash.error("结束时间要大于开始时间");
			addRateActPre(0, 12);
		}
		
		etime = DateUtil.add(etime, Calendar.DAY_OF_MONTH, 1);
		//判断活动状态
		
		int status = 0;	
		Date nowTime = null;
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(); 
		String nowDate=sdf.format(date);
		
		try {
			nowTime = sdf.parse(nowDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		
		//未开始
		if (stime.compareTo(nowTime) > 0) {
			status = 1;
		}
		
		//进行中
		if (stime.compareTo(nowTime) <= 0 && etime.compareTo(nowTime) >= 0) {
			status = 2;
		}
		
		//已结束
		if (etime.compareTo(nowTime) < 0) {
			status = 3;
		}
		
		t_add_rate_act act = new t_add_rate_act();
		
		act.stime = stime;
		act.etime = etime;
		act.name = name;
		act.status = status;
		
		ResultInfo result = addRateActService.insertAct(act);
		
		if (result.code > 0) {
			flash.success(result.msg);
			addRateActPre(0, 12);
		}

		flash.error("加息券活动添加失败");
		addRateActPre(0, 12);
	}
	
	/**
	 * 后台-推广-加息券活动规则
	 * 
	 * @author niu
	 * @create 2017.10.26
	 */
	public static void addRateRulePre(int currPage, int pageSize) {
		
		List<t_add_rate_act> actList = addRateActService.listOfActStart();
		PageBean<t_add_rate_act> page = addRateActService.listOfAct(currPage, pageSize);
		List<t_add_rate_ticket> tickets = addRateTicketService.listOfAddRateTicket();
		
		render(actList, page, tickets);
	}
	
	/**
	 * 后台-推广-加息券添加
	 * 
	 * @author niu
	 * @create 2017.10.26
	 */
	public static void addRateTicket(double apr, int day, double large, double small, double amount, long actId) {
		
		if (actId == 0) {
			flash.error("请选择加息券活动");
			addRateRulePre(1, 10);
		}
		
		if (apr == 0.0) {
			flash.error("加息券利率输入不正确");
			addRateRulePre(1, 10);
		}
		
		if (day == 0) {
			flash.error("加息券有效期输入不正确");
			addRateRulePre(1, 10);
		}
		
		if (large == 0.0 || small == 0.0) {
			flash.error("加息券获取条件：投资金额输入不正确");
			addRateRulePre(1, 10);
		}
		
		if (amount == 0.0) {
			flash.error("加息券使用条件：投资金额输入不正确");
			addRateRulePre(1, 10);
		}
		
		t_add_rate_ticket ticket = addRateTicketService.addRateTicket(apr, day, large, small, amount, actId, 0);
	
		if (ticket != null) {
			flash.success("加息券添加成功");
		} else {
			flash.error("加息券添加失败");
		}
		
		addRateRulePre(1, 10);
	}
	/**
	 * 加息券批量发放页面
	 * 
	 * @author GengJinCang
	 * @createDate 2018年5月10日14:22:50
	 */
	public static void batchSendratePre(){
		render();
	}
	
	/**
	 * 后台批量发放加息券
	 * 
	 * @param users 			用户名
	 * @param useRule			最低投资
	 * @param endTime			有限期限（天）
	 * @param rate				加息券利息
	 * @param channel			发放渠道
	 * @param bidPeriod			标的期限
	 * @param sendType			1：自选用户；0：所有用户
	 * @param sms     			勾选短信
	 * @param letter			勾选站内信
	 * 
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年5月24日16:24:51
	 */
	public static void batchSendAddRate (String users, double useRule, int endTime,
			int bidPeriod, double rate, int sendType, boolean sms, boolean letter){
		
		//sendType ==1 为自选用户  sendType ==0 为所有用户
		if (sendType == 1){
			
			//String 转List
			String[] strs=users.split(",");
		
			for(int i=0,len=strs.length;i<len;i++){
				
				t_user user = usersDao.findByColumn("name = ?",strs[i].toString());
				boolean flag = getAddRateTicketShakeActivity(rate,useRule, user.id, endTime, bidPeriod,"批量发放");
				
				if (!flag){
					
					LoggerUtil.error(true,"批量红包发放出错，数据回滚");
					flash.error("批量红包发放失败!");
					
					batchSendratePre();
					
				}		
				
				//勾选短信通知
				if (sms) {
					
					Map<String, Object> sceneParame = new HashMap<String, Object>();
					sceneParame.put("user_name", user.name);
					sceneParame.put("rate", rate);
					noticeService.sendUserNotice(user.id, NoticeScene.INVEST_ADD_RATE, sceneParame,NoticeType.SMS);
				}
				
				//勾选站内信通知
				if (letter) {
					
					Map<String, Object> sceneParame = new HashMap<String, Object>();
					sceneParame.put("user_name", user.name);
					sceneParame.put("rate", rate);
					noticeService.sendUserNotice(user.id, NoticeScene.INVEST_ADD_RATE, sceneParame,NoticeType.MSG);
				}
				
			}
			
		}else if (sendType == 0){
			
			List<t_user> user = usersDao.findAll();
			
			if (user != null && user.size() > 0){
				
				for (t_user usern : user) {
					
					Thread thread = new AddRateUserAllThread(rate, endTime, usern.id, useRule, bidPeriod);
					
					thread.start();
					
					if(Thread.activeCount()>=50){
						
						try {
							
							thread.join();
							
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}
					}
				}
			}
			
		}else {
			
			flash.error("发放类型不存在！");
			batchSendratePre();
		}
		
		flash.success("批量加息券发放成功!");
		batchSendratePre();
		
	}
	
	
	
	/**
	 * 获得加息卷
	 * @param apr		利息
	 * @param useRule	最低投资
	 * @param userId	用户id
	 * @param day 		有限期限（天）
	 * @param bidPeriod  标的期限
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月24日
	 */
	
	public static boolean getAddRateTicketShakeActivity(double apr, double useRule, long userId, int day, int bidPeriod, String channel) {
		
		t_add_rate_ticket ticket = addRateTicket(apr, day, 0, 0, useRule, 2);
		if (ticket == null) {
			return false;
		}
		
		Date nowDate = new Date();
		
		Date etime = DateUtil.add(nowDate, Calendar.DAY_OF_YEAR, ticket.day);
		
		t_add_rate_user rateUser = new t_add_rate_user();
		
		rateUser.stime = nowDate;
		rateUser.etime = etime;
		rateUser.ticket_id = ticket.id;
		rateUser.user_id = userId;
		rateUser.status = 1;
		rateUser.channel = channel;
		rateUser.bid_period = bidPeriod;
		
		t_add_rate_user rate_user = rateUser.save();
		
		return rate_user != null ? true : false;
	}
	
	/**
	 *  后台-推广-加息券添加
	 *  
	 * @param apr
	 * @param day
	 * @param large
	 * @param small
	 * @param amount
	 * @param type
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月24日
	 */
	
	public static t_add_rate_ticket addRateTicket(double apr, int day, double large, double small, double amount, int type) {
		
		t_add_rate_ticket ticket = new t_add_rate_ticket();
		
		ticket.apr = apr;
		ticket.day = day;
		ticket.large = large;
		ticket.small = small;
		ticket.amount = amount;
		ticket.act_id = 0;
		ticket.type = type;
		
		return ticket.save();
	}
	
	/**
	 * 编辑理财加息券活动时间
	 * 
	 * @param start_time  开始时间
	 * @param end_time 	      结束时间
	 * @author LiuPengwei
	 * @throws ParseException 
	 * @createDate 2018年5月25日16:19:55
	 */
	
	public static void editInvestSendRateTime (Date start_time, Date end_time) throws ParseException{
		
		t_market_activity queryMarketActivity = new t_market_activity();
		queryMarketActivity.time = new Date();
		queryMarketActivity.end_time = end_time;
		queryMarketActivity.start_time = start_time;
		queryMarketActivity.activity_type = 2;
		queryMarketActivity.save();
		
		flash.success("活动时间修改成功");
		
		showInvsetsendratePre();
	}
	
	/**
	 * 跳转投资发放界面
	 * 
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月28日
	 */
	public static void showInvsetsendratePre (){
		
		/**红包投资活动最新时间*/
		t_market_activity queryMarketActivity = marketActivityService.queryAddMarketActivity();
		
		/** 上架理财产品*/
		List<t_product> productAll = productservice.findListByColumn("is_use = ?", true);
	
		/** 上架投资营销规则 */
		List<t_market_invest> marketInvest = marketInvestService.findListByColumn("is_use = ? and type = 2", true);
		
		render(queryMarketActivity,productAll,marketInvest);
	}
	
	/**
	 * 后台-推广-加息券-投资发放-编辑
	 *
	 * @rightID 708001
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月28日17:12:14
	 */
	public static void editInvestSendRate(){
		
		checkAuthenticity();
		List<t_market_invest> marketInvestList = marketInvestService.findListByColumn("is_use = ? and type = 2", true);
		
		//修改
		for (t_market_invest marketInvest : marketInvestList) {
			
			String productIdStr = params.get("productId_"+marketInvest.id); //产品id
			String gainRuleStr = params.get("gainRule_"+marketInvest.id);	//投资金额满
			String amountStr = params.get("rate_"+marketInvest.id);			//加息券利息
			String useRuleStr = params.get("useRule_"+marketInvest.id);		//最低投资金额
			String bidPeriodStr = params.get("bidPeriod_"+marketInvest.id);	//投标期限
			String limit_dayStr = params.get("limit_day_"+marketInvest.id);	//有效期限（天）

			if(StringUtils.isNotBlank(productIdStr) && StringUtils.isNotBlank(gainRuleStr) && StringUtils.isNotBlank(amountStr) &&
					StringUtils.isNotBlank(useRuleStr) && StringUtils.isNotBlank(bidPeriodStr) && StringUtils.isNotBlank(limit_dayStr)){
				if ((!StrUtil.isOneDouble(amountStr)) || StrUtil.isNumLess(amountStr, 0) || StrUtil.isNumMore(amountStr, 1000)) {
					
					flash.error("红包金额不正确");
					showInvsetsendratePre();
				}
				
				if ((!StrUtil.isOneDouble(gainRuleStr)) || StrUtil.isNumLess(gainRuleStr, 0) || StrUtil.isNumMore(gainRuleStr, 999999)) {
					
					flash.error("投资满金额不正确");
					showInvsetsendratePre();
				}
				
				if ((!StrUtil.isOneDouble(bidPeriodStr)) || StrUtil.isNumLess(bidPeriodStr, 0) || StrUtil.isNumMore(bidPeriodStr, 12)) {
					
					flash.error("投标期限不正确");
					showInvsetsendratePre();
				}
				
				if ( !StrUtil.isOneDouble(useRuleStr) || StrUtil.isNumLess(useRuleStr, 0) || StrUtil.isNumMore(useRuleStr, 999999)) {
					
					flash.error("红包使用条件金额不正确");
					showInvsetsendratePre();
				}
			
				if ((!StrUtil.isOneDouble(limit_dayStr)) || StrUtil.isNumLess(limit_dayStr, 0) || StrUtil.isNumMore(limit_dayStr, 365)) {
					
					flash.error("红包有效期不正确");
					showInvsetsendratePre();
				}
				
				long productId = Long.parseLong(productIdStr);
				int gainRule =Integer.parseInt(gainRuleStr);
				int red_packet_amount = Integer.parseInt(amountStr);
				int useRule = Integer.parseInt(useRuleStr);
				int bidPeriod = Integer.parseInt(bidPeriodStr);
				int limit_day = Integer.parseInt(limit_dayStr);
		
				marketInvest.invest_product = productId;
				marketInvest.invest_total = gainRule;
				marketInvest.amount_apr = red_packet_amount;
				marketInvest.use_rule = useRule;
				marketInvest.use_bid_limit = bidPeriod;
				marketInvest.limit_day = limit_day;
				marketInvest.type = 2;
				
				marketInvest.save();
			}
		}
		
		//添加
		String productIdStrs = params.get("productId"); 		//产品id
		String gainRuleStrs = params.get("gainRule");			//投资金额满
		String amountStrs = params.get("rate");					//加息券利息
		String useRuleStrs = params.get("useRule");				//最低投资金额
		String bidPeriodStrs = params.get("bidPeriod");			//投标期限
		String limit_dayStrs = params.get("endTime");			//有效期限（天）

		if(StringUtils.isNotBlank(productIdStrs) && StringUtils.isNotBlank(gainRuleStrs) && StringUtils.isNotBlank(amountStrs) &&
				StringUtils.isNotBlank(useRuleStrs) && StringUtils.isNotBlank(bidPeriodStrs) && StringUtils.isNotBlank(limit_dayStrs)){
			
			if ((!StrUtil.isOneDouble(amountStrs)) || StrUtil.isNumLess(amountStrs, 0) || StrUtil.isNumMore(amountStrs, 1000)) {
				
				flash.error("红包金额不正确");
				showInvsetsendratePre();
			}
			
			if ((!StrUtil.isOneDouble(gainRuleStrs)) || StrUtil.isNumLess(gainRuleStrs, 0) || StrUtil.isNumMore(gainRuleStrs, 999999)) {
				
				flash.error("投资满金额不正确");
				showInvsetsendratePre();
			}
			
			if ((!StrUtil.isOneDouble(bidPeriodStrs)) || StrUtil.isNumLess(bidPeriodStrs, 0) || StrUtil.isNumMore(bidPeriodStrs, 12)) {
				
				flash.error("投标期限不正确");
				showInvsetsendratePre();
			}
			
			if ( !StrUtil.isOneDouble(useRuleStrs) || StrUtil.isNumLess(useRuleStrs, 0) || StrUtil.isNumMore(useRuleStrs, 999999)) {
				
				flash.error("红包使用条件金额不正确");
				showInvsetsendratePre();
			}
		
			if ((!StrUtil.isOneDouble(limit_dayStrs)) || StrUtil.isNumLess(limit_dayStrs, 0) || StrUtil.isNumMore(limit_dayStrs, 365)) {
				
				flash.error("红包有效期不正确");
				showInvsetsendratePre();
			}
			
			long productIds = Long.parseLong(productIdStrs);
			int gainRules =Integer.parseInt(gainRuleStrs);
			double amounts =Double.parseDouble(amountStrs);
			int useRules = Integer.parseInt(useRuleStrs);
			int bidPeriods = Integer.parseInt(bidPeriodStrs);
			int limit_days = Integer.parseInt(limit_dayStrs);

			t_market_invest marketInvestAdd = new t_market_invest();
			
			marketInvestAdd.invest_total = gainRules;
			marketInvestAdd.time = new Date();
			marketInvestAdd.invest_product = productIds;
			marketInvestAdd.invest_total = gainRules;
			marketInvestAdd.amount_apr = amounts;
			marketInvestAdd.use_rule = useRules;
			marketInvestAdd.use_bid_limit = bidPeriods;
			marketInvestAdd.limit_day = limit_days;
			marketInvestAdd.is_use = true;
			marketInvestAdd.type = 2;
			
			marketInvestAdd.save();
			
		
		}
		
		//管理员记录
		Long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.SPREAD_EDIT_REDPACKEDT, null);
		
		flash.success("更新投资红包规则成功");
		showInvsetsendratePre();
	}
	
	/**
	 * 跳转加息券发放记录
	 * 
	 * @param showType 筛选类型  0-所有;1-未使用;3-已使用;4-过期
	 * @param currPage
	 * @param pageSize
	 * 
	 * @author 刘鹏伟
	 * @createDate 2018年5月28日17:49:34
	 */
	public static void showRateRecordPre (int showType, int currPage, int pageSize){
		
		/* 搜索条件 */
		String userName = params.get("userName"); //用户名
		String startTime = params.get("startTime");//开始时间
		String endTime = params.get("endTime");//结束时间
		
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
		
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		
		PageBean<MaketInvestAddBean> pageBean = addRateTicketService.pageOfMaketAddRate(currPage, pageSize,
				showType, orderType, orderValue, userName, startTime, endTime);
		
		//已使用数量
		int addRateUseAll = addRateTicketService.findRateUserStatus(3);
		List<t_add_rate_ticket> addRateTicketList = addRateTicketService.findAll();
		//全部加息券数量
		int addRateAll = addRateTicketList.size();
				
		render(pageBean,addRateUseAll,addRateAll);
	}

	
	/**
	 * 下架投资发放红包规则
	 * 
	 * @param start_time  开始时间
	 * @param end_time 	      结束时间
	 * @author LiuPengwei
	 * @createDate 2018年5月29日17:28:55
	 */
	
	public static void  changeIsUseStatus (long marketId){
		
		ResultInfo result = new ResultInfo();
		
		t_market_invest marketInvest = marketInvestService.findByID(marketId);		
		marketInvest.is_use =false;

		boolean flag = marketInvestService.soldOutInvestMarket(marketInvest);
		
		if (!flag){
			result.code = -1;
			result.msg = "投资红包发放规则删除失败";
		}
		
		result.code = 1;
		
		renderJSON(result);
	}
	
}





























