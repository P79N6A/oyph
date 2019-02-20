package controllers.back.spread;

import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Join;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import models.activity.shake.entity.t_shake_record;
import models.activity.shake.entity.t_shake_set;
import models.common.entity.t_event_supervisor.Event;
import models.core.entity.t_product;
import models.common.entity.t_user;
import models.ext.redpacket.bean.MaketInvestRedBean;
import models.ext.redpacket.bean.MaketInvestRedBean.RedpacketStatus;
import models.ext.redpacket.entity.t_market_activity;
import models.ext.redpacket.entity.t_market_invest;
import models.ext.redpacket.entity.t_red_packet;
import models.ext.redpacket.entity.t_red_packet_account;
import models.ext.redpacket.entity.t_red_packet_user;
import services.common.NoticeService;
import services.common.UserService;
import services.core.ProductService;
import services.ext.redpacket.MarketActivityService;
import services.ext.redpacket.MarketInvestService;
import services.ext.redpacket.RedpacketService;

import common.constants.ext.RedpacketKey;
import common.enums.NoticeScene;
import common.enums.NoticeType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.StrUtil;

import controllers.common.BackBaseController;
import controllers.thread.RedPacketUserAllThread;
import daos.base.BaseDao;
import daos.common.UserDao;
import daos.ext.redpacket.RedpacketAccountDao;
import daos.ext.redpacket.RedpacketUserDao;

/**
 * 后台-推广-红包
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月15日
 */
public class RedpacketCtrl extends BackBaseController {

	protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);
	
	protected static RedpacketUserDao redpacketUserDao = Factory.getDao(RedpacketUserDao.class);
	
	protected static RedpacketAccountDao redpacketAccountDao = Factory.getDao(RedpacketAccountDao.class);
	
	protected static UserDao usersDao = Factory.getDao(UserDao.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static MarketActivityService marketActivityService = Factory.getService(MarketActivityService.class);
	
	protected static ProductService productservice = Factory.getService(ProductService.class);
	
	protected static MarketInvestService marketInvestService = Factory.getService(MarketInvestService.class);
	
	protected static UserService userservice = Factory.getService(UserService.class);
	/**
	 * 后台-推广-红包-红包规则
	 *
	 * @rightID 701001
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public static void showRedpacketsPre() {
		
		List<t_red_packet> redPackets = redpacketService.findAll();
		if (redPackets == null || redPackets.size() == 0) {

			error404();
		}
		
		Map<String, t_red_packet> redPacketMap = new HashMap<String, t_red_packet>();
		for (t_red_packet red_packet : redPackets) {

			redPacketMap.put(red_packet._key, red_packet);
		}
		
		render(redPackets,redPacketMap);
	}
	
	/**
	 * 后台-推广-红包-红包规则-设置红包规则
	 *
	 * @rightID 701002
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public static void editRedpackets(){
		checkAuthenticity();

		String account = params.get(RedpacketKey.ACCOUNT);
		String realname = params.get(RedpacketKey.REALNAME);
		String bind_email = params.get(RedpacketKey.BIND_EMAIL);
		String bind_card = params.get(RedpacketKey.BIND_CARD);
		String first_recharge = params.get(RedpacketKey.FIRST_RECHARGE);
		String first_invest = params.get(RedpacketKey.FIRST_INVEST);
		
		String use_rule_account = params.get("use_rule_"+RedpacketKey.ACCOUNT) ;
		String use_rule_realname = params.get("use_rule_"+RedpacketKey.REALNAME) ;
		String use_rule_bind_email = params.get("use_rule_"+RedpacketKey.BIND_EMAIL) ;
		String use_rule_bind_card = params.get("use_rule_"+RedpacketKey.BIND_CARD) ;
		String use_rule_first_recharge = params.get("use_rule_"+RedpacketKey.FIRST_RECHARGE) ;
		String use_rule_first_invest = params.get("use_rule_"+RedpacketKey.FIRST_INVEST) ;
		
		String limit_day_account = params.get("limit_day_"+RedpacketKey.ACCOUNT) ;
		String limit_day_realname = params.get("limit_day_"+RedpacketKey.REALNAME) ;
		String limit_day_bind_email = params.get("limit_day_"+RedpacketKey.BIND_EMAIL) ;
		String limit_day_bind_card = params.get("limit_day_"+RedpacketKey.BIND_CARD) ;
		String limit_day_first_recharge = params.get("limit_day_"+RedpacketKey.FIRST_RECHARGE) ;
		String limit_day_first_invest = params.get("limit_day_"+RedpacketKey.FIRST_INVEST) ;
		
		if ((!StrUtil.isOneDouble(account)) || StrUtil.isNumLess(account, 0) || StrUtil.isNumMore(account, 100)) {
		
			flash.error("资金托管开户红包金额不正确");
			showRedpacketsPre();
		}
		if ((!StrUtil.isOneDouble(realname)) || StrUtil.isNumLess(realname, 0) || StrUtil.isNumMore(realname, 100)) {
			
			flash.error("实名认证红包金额不正确");
			showRedpacketsPre();
		}
		if ((!StrUtil.isOneDouble(bind_email)) || StrUtil.isNumLess(bind_email, 0) || StrUtil.isNumMore(bind_email, 100)) {
			
			flash.error("绑定邮箱红包金额不正确");
			showRedpacketsPre();
		}
		if ((!StrUtil.isOneDouble(bind_card)) || StrUtil.isNumLess(bind_card, 0) || StrUtil.isNumMore(bind_card, 100)) {
			
			flash.error("绑定银行卡红包金额不正确");
			showRedpacketsPre();
		}
		if ((!StrUtil.isOneDouble(first_recharge)) || StrUtil.isNumLess(first_recharge, 0) || StrUtil.isNumMore(first_recharge, 100)) {
			
			flash.error("首次充值红包金额不正确");
			showRedpacketsPre();
		}
		if ((!StrUtil.isOneDouble(first_invest)) || StrUtil.isNumLess(first_invest, 0) || StrUtil.isNumMore(first_invest, 100)) {
			
			flash.error("首次理财红包金额不正确");
			showRedpacketsPre();
		}
		
		if ( !StrUtil.isOneDouble(use_rule_account) ) {
			
			flash.error("资金托管开户红包使用条件金额不正确");
			showRedpacketsPre();
		}
		if (!StrUtil.isOneDouble(use_rule_realname) ) {
			
			flash.error("实名认证红包使用条件金额不正确");
			showRedpacketsPre();
		}
		if ( !StrUtil.isOneDouble(use_rule_bind_email)) {
			
			flash.error("绑定邮箱红包使用条件金额不正确");
			showRedpacketsPre();
		}
		if ( !StrUtil.isOneDouble(use_rule_bind_card) ) {
			
			flash.error("绑定银行卡红包使用条件金额不正确");
			showRedpacketsPre();
		}
		if ( !StrUtil.isOneDouble(use_rule_first_recharge) ) {
			
			flash.error("首次充值红包使用条件金额不正确");
			showRedpacketsPre();
		}
		if ( !StrUtil.isOneDouble(use_rule_first_invest) ) {
			
			flash.error("首次理财红包使用条件金额不正确");
			showRedpacketsPre();
		}
		
		if ((!StrUtil.isOneDouble(limit_day_account)) || StrUtil.isNumLess(limit_day_account, 0) || StrUtil.isNumMore(limit_day_account, 1000)) {
			
			flash.error("资金托管开户红包有效期不正确");
			showRedpacketsPre();
		}
		if ((!StrUtil.isOneDouble(limit_day_realname)) || StrUtil.isNumLess(limit_day_realname, 0) || StrUtil.isNumMore(limit_day_realname, 1000)) {
			
			flash.error("实名认证红包有效期不正确");
			showRedpacketsPre();
		}
		if ((!StrUtil.isOneDouble(limit_day_bind_email)) || StrUtil.isNumLess(limit_day_bind_email, 0) || StrUtil.isNumMore(limit_day_bind_email, 1000)) {
			
			flash.error("绑定邮箱红包有效期不正确");
			showRedpacketsPre();
		}
		if ((!StrUtil.isOneDouble(limit_day_bind_card)) || StrUtil.isNumLess(limit_day_bind_card, 0) || StrUtil.isNumMore(limit_day_bind_card, 1000)) {
			
			flash.error("绑定银行卡红包有效期不正确");
			showRedpacketsPre();
		}
		if ((!StrUtil.isOneDouble(limit_day_first_recharge)) || StrUtil.isNumLess(limit_day_first_recharge, 0) || StrUtil.isNumMore(limit_day_first_recharge, 1000)) {
			
			flash.error("首次充值红包有效期不正确");
			showRedpacketsPre();
		}
		if ((!StrUtil.isOneDouble(limit_day_first_invest)) || StrUtil.isNumLess(limit_day_first_invest, 0) || StrUtil.isNumMore(limit_day_first_invest, 1000)) {
			
			flash.error("首次理财红包有效期不正确");
			showRedpacketsPre();
		}
		
		
		Map<String, Double> newRedpacket = new HashMap<String, Double>();
		newRedpacket.put(RedpacketKey.ACCOUNT, Double.valueOf(account));
		newRedpacket.put(RedpacketKey.REALNAME, Double.valueOf(realname));
		newRedpacket.put(RedpacketKey.BIND_EMAIL, Double.valueOf(bind_email));
		newRedpacket.put(RedpacketKey.BIND_CARD, Double.valueOf(bind_card));
		newRedpacket.put(RedpacketKey.FIRST_RECHARGE, Double.valueOf(first_recharge));
		newRedpacket.put(RedpacketKey.FIRST_INVEST, Double.valueOf(first_invest));
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("use_rule_"+RedpacketKey.ACCOUNT, use_rule_account );
		map.put("use_rule_"+RedpacketKey.REALNAME, use_rule_realname );
		map.put("use_rule_"+RedpacketKey.BIND_EMAIL, use_rule_bind_email );
		map.put("use_rule_"+RedpacketKey.BIND_CARD, use_rule_bind_card );
		map.put("use_rule_"+RedpacketKey.FIRST_RECHARGE, use_rule_first_recharge );
		map.put("use_rule_"+RedpacketKey.FIRST_INVEST, use_rule_first_invest );
		
		map.put("limit_day_"+RedpacketKey.ACCOUNT, limit_day_account );
		map.put("limit_day_"+RedpacketKey.REALNAME, limit_day_realname );
		map.put("limit_day_"+RedpacketKey.BIND_EMAIL, limit_day_bind_email );
		map.put("limit_day_"+RedpacketKey.BIND_CARD, limit_day_bind_card );
		map.put("limit_day_"+RedpacketKey.FIRST_RECHARGE, limit_day_first_recharge );
		map.put("limit_day_"+RedpacketKey.FIRST_INVEST, limit_day_first_invest );
		
		ResultInfo result = redpacketService.updateRedpackets(newRedpacket,map);
		if (result.code < 1) {
			
			LoggerUtil.error(true, "更新红包规则出错,数据回滚");
			flash.error("更新红包规则失败");
			showRedpacketsPre();
		} 
		
		Long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.SPREAD_EDIT_REDPACKEDT, null);
		
		flash.success("更新红包规则成功");
		showRedpacketsPre();
	}
	
	/**
	 * 红包批量发放页面
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年4月12日13:52:36
	 */
	public static void batchSendRedPacketPre(){
		
		render();
	}
	
	/**
	 * 跳转红包投资发放页面
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年4月12日13:52:36
	 */
	public static void showInvestRedPacketPre (){
		
		/**红包投资活动最新时间*/
		t_market_activity queryMarketActivity = marketActivityService.queryRedMarketActivity();
		
		/** 上架理财产品*/
		List<t_product> productAll = productservice.findListByColumn("is_use = ?", true);
	
		/** 上架投资营销规则 */
		List<t_market_invest> marketInvest = marketInvestService.findListByColumn("is_use = ? and type = 1", true);
		
		render(queryMarketActivity,productAll,marketInvest);
	}
	
	/**
	 * 后台-推广-红包-投资发放-编辑
	 *
	 * @rightID 708001
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月22日14:38:53
	 */
	public static void editInvestRedPackets(){
		
		checkAuthenticity();
		List<t_market_invest> marketInvestList = marketInvestService.findListByColumn("is_use = ? and type = 1", true);
		
		//修改
		for (t_market_invest marketInvest : marketInvestList) {
			
			String productIdStr = params.get("productId_"+marketInvest.id); //产品id
			String gainRuleStr = params.get("gainRule_"+marketInvest.id);	//投资金额满
			String amountStr = params.get("amount_"+marketInvest.id);		//红包金额
			String useRuleStr = params.get("useRule_"+marketInvest.id);		//最低投资金额
			String bidPeriodStr = params.get("bidPeriod_"+marketInvest.id);	//投标期限
			String limit_dayStr = params.get("limit_day_"+marketInvest.id);	//有效期限（天）

			if(StringUtils.isNotBlank(productIdStr) && StringUtils.isNotBlank(gainRuleStr) && StringUtils.isNotBlank(amountStr) &&
					StringUtils.isNotBlank(useRuleStr) && StringUtils.isNotBlank(bidPeriodStr) && StringUtils.isNotBlank(limit_dayStr)){
				if ((!StrUtil.isOneDouble(amountStr)) || StrUtil.isNumLess(amountStr, 0) || StrUtil.isNumMore(amountStr, 1000)) {
					
					flash.error("红包金额不正确");
					showInvestRedPacketPre();
				}
				
				if ((!StrUtil.isOneDouble(gainRuleStr)) || StrUtil.isNumLess(gainRuleStr, 0) || StrUtil.isNumMore(gainRuleStr, 999999)) {
					
					flash.error("投资满金额不正确");
					showInvestRedPacketPre();
				}
				
				if ((!StrUtil.isOneDouble(bidPeriodStr)) || StrUtil.isNumLess(bidPeriodStr, 0) || StrUtil.isNumMore(bidPeriodStr, 12)) {
					
					flash.error("投标期限不正确");
					showInvestRedPacketPre();
				}
				
				if ( !StrUtil.isOneDouble(useRuleStr) || StrUtil.isNumLess(useRuleStr, 0) || StrUtil.isNumMore(useRuleStr, 999999)) {
					
					flash.error("红包使用条件金额不正确");
					showInvestRedPacketPre();
				}
			
				if ((!StrUtil.isOneDouble(limit_dayStr)) || StrUtil.isNumLess(limit_dayStr, 0) || StrUtil.isNumMore(limit_dayStr, 365)) {
					
					flash.error("红包有效期不正确");
					showInvestRedPacketPre();
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
				marketInvest.type = 1;
				
				marketInvest.save();
			}
		}
		
		//添加
		String productIdStrs = params.get("productId"); 		//产品id
		String gainRuleStrs = params.get("gainRule");			//投资金额满
		String amountStrs = params.get("amount");				//红包金额
		String useRuleStrs = params.get("useRule");				//最低投资金额
		String bidPeriodStrs = params.get("bidPeriod");			//投标期限
		String limit_dayStrs = params.get("endTime");			//有效期限（天）
		
		if(StringUtils.isNotBlank(productIdStrs) && StringUtils.isNotBlank(gainRuleStrs) && StringUtils.isNotBlank(amountStrs) &&
				StringUtils.isNotBlank(useRuleStrs) && StringUtils.isNotBlank(bidPeriodStrs) && StringUtils.isNotBlank(limit_dayStrs)){
			
			if ((!StrUtil.isOneDouble(amountStrs)) || StrUtil.isNumLess(amountStrs, 0) || StrUtil.isNumMore(amountStrs, 1000)) {
				
				flash.error("红包金额不正确");
				showInvestRedPacketPre();
			}
			
			if ((!StrUtil.isOneDouble(gainRuleStrs)) || StrUtil.isNumLess(gainRuleStrs, 0) || StrUtil.isNumMore(gainRuleStrs, 999999)) {
				
				flash.error("投资满金额不正确");
				showInvestRedPacketPre();
			}
			
			if ((!StrUtil.isOneDouble(bidPeriodStrs)) || StrUtil.isNumLess(bidPeriodStrs, 0) || StrUtil.isNumMore(bidPeriodStrs, 12)) {
				
				flash.error("投标期限不正确");
				showInvestRedPacketPre();
			}
			
			if ( !StrUtil.isOneDouble(useRuleStrs) || StrUtil.isNumLess(useRuleStrs, 0) || StrUtil.isNumMore(useRuleStrs, 999999)) {
				
				flash.error("红包使用条件金额不正确");
				showInvestRedPacketPre();
			}
		
			if ((!StrUtil.isOneDouble(limit_dayStrs)) || StrUtil.isNumLess(limit_dayStrs, 0) || StrUtil.isNumMore(limit_dayStrs, 365)) {
				
				flash.error("红包有效期不正确");
				showInvestRedPacketPre();
			}
			
			long productIds = Long.parseLong(productIdStrs);
			int gainRules =Integer.parseInt(gainRuleStrs);
			int red_packet_amounts = Integer.parseInt(amountStrs);
			int useRules = Integer.parseInt(useRuleStrs);
			int bidPeriods = Integer.parseInt(bidPeriodStrs);
			int limit_days = Integer.parseInt(limit_dayStrs);

			t_market_invest marketInvestAdd = new t_market_invest();
			
			marketInvestAdd.invest_total = gainRules;
			marketInvestAdd.time = new Date();
			marketInvestAdd.invest_product = productIds;
			marketInvestAdd.invest_total = gainRules;
			marketInvestAdd.amount_apr = red_packet_amounts;
			marketInvestAdd.use_rule = useRules;
			marketInvestAdd.use_bid_limit = bidPeriods;
			marketInvestAdd.limit_day = limit_days;
			marketInvestAdd.is_use = true;
			marketInvestAdd.type = 1;
			
			marketInvestAdd.save();
			
		
		}
		
		//管理员记录
		Long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.SPREAD_EDIT_REDPACKEDT, null);
		
		flash.success("更新投资红包规则成功");
		showInvestRedPacketPre();
	}
		
	/**
	 * 后台批量发放红包
	 * 
	 * @param users 			用户名
	 * @param useRule			使用金额限制
	 * @param endTime			有限期限（天）
	 * @param amount			红包金额
	 * @param channel			发放渠道
	 * @param bidPeriod			标的期限
	 * @param sendType			1：自选用户；0：所有用户
	 * @param sms     			勾选短信
	 * @param letter			勾选站内信
	 * 
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年5月15日10:05:04
	 */
	public static void batchSendRedPacket (String users, double useRule, int endTime,
			int bidPeriod, double amount, int sendType, boolean sms, boolean letter){
		
		//sendType ==1 为自选用户  sendType ==0 为所有用户
		if (sendType == 1){
			
			//String 转List
			String[] strs=users.split(",");
		
			for(int i=0,len=strs.length;i<len;i++){
				
				t_user user = usersDao.findByColumn("name = ?",strs[i].toString());
				
				boolean flag = creatOfficialRedPacket(user.id, useRule, endTime, amount,"批量发放", bidPeriod, "batch_give");
			
				if (!flag){
					
					LoggerUtil.error(true,"批量红包发放出错，数据回滚");
					flash.error("批量红包发放失败!");
					batchSendRedPacketPre();
					
				}		
				
				//勾选短信通知
				if (sms) {
					
					Map<String, Object> sceneParame = new HashMap<String, Object>();
					sceneParame.put("user_name", user.name);
					sceneParame.put("amount", amount);
					noticeService.sendUserNotice(user.id, NoticeScene.INVEST_RED_PACKET, sceneParame,NoticeType.SMS);
				}
				
				//勾选站内信通知
				if (letter) {
					
					Map<String, Object> sceneParame = new HashMap<String, Object>();
					sceneParame.put("user_name", user.name);
					sceneParame.put("amount", amount);
					noticeService.sendUserNotice(user.id, NoticeScene.INVEST_RED_PACKET, sceneParame,NoticeType.MSG);
				}
				
			}
			
		}else if (sendType == 0){
			
			List<t_user> user = usersDao.findAll();
			
			if (user != null && user.size() > 0){
				
				for (t_user usern : user) {
					
					Thread thread = new RedPacketUserAllThread(endTime, usern.id, amount, useRule);
					
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
			batchSendRedPacketPre();
		}
		
		flash.success("批量红包发放成功!");
		batchSendRedPacketPre();
		
	}
	
	/**
	 * 添加后台发放用户红包
	 * 
	 * @param userId  			用户id
	 * @param useRule			使用金额限制
	 * @param limitDay			有限期限（天）
	 * @param redpacketAm		红包金额
	 * @param channel			发放渠道
	 * @param bidPeriod			标的期限
	 * @param _key				红包key
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月24日
	 */
	public static boolean creatOfficialRedPacket(long userId, double useRule,int limitDay,
			double redpacketAm,String channel, int bidPeriod ,String _key){
		
		boolean flag = false;

		Date now = new Date();
		
		/**使用期限*/
		Calendar calendar = new GregorianCalendar(); 
		calendar.setTime(now); 
		calendar.add(calendar.DATE, limitDay);
		Date limit_time=calendar.getTime();
		
		t_red_packet_user redpacket_user = new t_red_packet_user();
		redpacket_user.time = now;
		redpacket_user._key = _key;
		redpacket_user.red_packet_name = "理财红包";
		redpacket_user.user_id = userId;
		redpacket_user.amount = redpacketAm;
		redpacket_user.use_rule = useRule;
		redpacket_user.limit_day = limitDay;
		redpacket_user.limit_time = limit_time;
		redpacket_user.invest_id = 0;
		redpacket_user.setStatus(t_red_packet_user.RedpacketStatus.RECEIVED);
		redpacket_user.channel = channel ;
		redpacket_user.bid_period = bidPeriod;
		
		if(!redpacketUserDao.save(redpacket_user)){		
			
			return flag;
		}
		
		t_red_packet_account userRedPacket = redpacketAccountDao.findByColumn("user_id = ?", userId);
		
		if (userRedPacket == null){
			t_red_packet_account red_packet_account = new t_red_packet_account();
			red_packet_account.time = now;
			red_packet_account.user_id = userId;
			red_packet_account.balance = 0;
			
			if(!redpacketAccountDao.save(red_packet_account)){
				
				return flag;	
			}
		}
		
		return true;	
	}
	
	/**
	 * 跳转红包发放记录
	 * 
	 * @param showType 筛选类型  0-所有;1-未使用;2-已使用;3-过期
	 * @param currPage
	 * @param pageSize
	 * 
	 * @author GengJinCang
	 * @createDate 2018年5月11日15:04:20
	 */
	public static void showRedpacketSrecordPre (int showType, int currPage, int pageSize){
		
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

		PageBean<MaketInvestRedBean> pageBean = redpacketService.pageOfMaketRedPacket(currPage, pageSize,
				showType, orderType, orderValue, userName, startTime, endTime);
		
		
		//查询所有红包
		List<t_red_packet_user> RedpacketsAll = redpacketService.queryRedpacketsAll();
		
		//合计红包金额
		double redPacketAm = 0;
		for (t_red_packet_user redPacket : RedpacketsAll) {
			
			redPacketAm = redPacketAm + redPacket.amount ; 
		}
		
		//合计发放红包数量
		int redPacketNum = RedpacketsAll.size();
		
		//查询使用红包
		List<t_red_packet_user> RedpacketsUseAll = redpacketService.queryRedpacketsStatus(RedpacketStatus.ALREADY_USED.code);
		//使用红包金额
		double redPacketUseAm = 0;
		for (t_red_packet_user RedpacketsUse : RedpacketsUseAll) {
			
			redPacketUseAm = redPacketUseAm + RedpacketsUse.amount ; 
		}
		
		//使用发放红包数量
		int redPacketUseNum = RedpacketsUseAll.size();
		
		render(pageBean, redPacketAm, redPacketNum, redPacketUseNum, redPacketUseAm);
	}

	/**
	 * 编辑理财红包活动时间
	 * 
	 * @param start_time  开始时间
	 * @param end_time 	      结束时间
	 * @author LiuPengwei
	 * @throws ParseException 
	 * @createDate 2018年5月17日14:10:24
	 */
	public static void editInvestRedpacketsTime (Date start_time, Date end_time) throws ParseException{

		t_market_activity queryMarketActivity = new t_market_activity();
		queryMarketActivity.time = new Date();
		queryMarketActivity.end_time = end_time;
		queryMarketActivity.start_time = start_time;
		queryMarketActivity.activity_type = 1;
		queryMarketActivity.save();
		
		flash.success("活动时间修改成功");
		showInvestRedPacketPre();
	}
	
	/**
	 * 下架投资发放红包规则
	 * 
	 * @param start_time  开始时间
	 * @param end_time 	      结束时间
	 * @author LiuPengwei
	 * @createDate 2018年5月17日14:10:24
	 */
	
	public static void  soldOutInvestMarket (long marketId){
		
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
	
	/**
	 * 后台发红包，分页查询所有用户
	 * 
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年5月21日14:44:47
	 */
	public static void queryUser(int currPage, int pageSize, String key) {
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		
		flash.put("searchKey", key);

		// 限制最大长度
		if (StringUtils.isNotBlank(key) && key.length() > 16) {
			key = key.substring(0, 16);
		}

		PageBean<Map<String, Object>> pageBean = userservice.queryredPacketUser(currPage, pageSize ,key);

		render(pageBean);
	}
	
}
