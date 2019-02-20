package jobs;


import java.util.List;
import java.util.Map;

import com.shove.Convert;

import models.core.bean.BidInvestRecord;
import models.core.entity.t_bid;
import models.core.entity.t_invest;
import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.ResultInfo;
import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Every;
import play.jobs.Job;
import service.ext.wealthcircle.WealthCircleService;
import services.common.SettingService;
import services.core.BidService;
import services.core.InvestService;
import services.ext.cps.CpsService;

public class rewardGrant {

	private static BidService bidService = Factory.getService(BidService.class) ;
	private static InvestService investService = Factory.getService(InvestService.class) ;
	private static CpsService cpsService = Factory.getService(CpsService.class) ;
	private static WealthCircleService wealthCircleService = Factory.getService(WealthCircleService.class) ;
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	

	public void doJob()  {
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------定时添加奖励,开始---------------------");
		}
		
		cpsRewardGrant() ;
		wealthcircleRewardGrant() ;
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------定时添加奖励,结束---------------------");
		}
		
	}
	
	/**
	 * 添加cps 奖励
	 */
	public void cpsRewardGrant(){
		
		List<Map<String, Object>> list = bidService.selectRewardBidId(t_bid.RewardGrantType.NOT_REWARD.code) ;
		if(list==null || list.size() == 0){
			return ;
		}
		
		long bidId= 0 ;
		List<t_invest> investList ;
		ResultInfo result = new ResultInfo() ;
		int countBid = 0 ;
		for (Map<String, Object> map : list) {
			bidId = Long.parseLong( map.get("bid_id").toString() ) ;
			investList = investService.queryBidInvest(bidId) ;
			if(investList == null || investList.size() ==0){
				continue ;
			}
			
			for (t_invest invest : investList) {
				result = cpsService.investGiveCpsCommission(invest.user_id, invest.amount);
				
				if(result.code < 0){
					JPA.setRollbackOnly();
					continue ;
				}
			}
			
			countBid = bidService.updateBidRewardGrantType(bidId, t_bid.RewardGrantType.NOT_REWARD.code, t_bid.RewardGrantType.CPS_REWARD.code) ;
			
			if( countBid > 0 ){
				JPAUtil.transactionCommit();
			}else{
				JPA.setRollbackOnly();
			}
		}
	}
	
	/**
	 * 财富圈奖励发放
	 */
	public void wealthcircleRewardGrant(){
		
		List<Map<String, Object>> list = bidService.selectRewardBidId(t_bid.RewardGrantType.CPS_REWARD.code) ;
		if(list==null || list.size() == 0){
			return ;
		}
		
		long bidId= 0 ;
		List<t_invest> investList ;
		ResultInfo result = new ResultInfo() ;
		int countBid = 0 ;
		for (Map<String, Object> map : list) {
			bidId = Long.parseLong( map.get("bid_id").toString() ) ;
			investList = investService.queryBidInvest(bidId) ;
			if(investList == null || investList.size() ==0){
				continue ;
			}
			
			for (t_invest invest : investList) {
				result = wealthCircleService.investGiveWcCommission(invest.user_id, invest.amount);
				
				if(result.code < 0){
					JPA.setRollbackOnly();
					continue ;
				}
			}
			
			countBid = bidService.updateBidRewardGrantType(bidId, t_bid.RewardGrantType.CPS_REWARD.code, t_bid.RewardGrantType.WEALTHCIRCLE_REWARD.code) ;
			
			if( countBid > 0 ){
				JPAUtil.transactionCommit();
			}else{
				JPA.setRollbackOnly();
			}
		}
	}
}
