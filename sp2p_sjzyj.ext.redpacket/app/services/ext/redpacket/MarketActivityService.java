package services.ext.redpacket;


import java.util.HashMap;
import java.util.Map;

import common.utils.Factory;
import daos.ext.redpacket.AddRateActDao;
import daos.ext.redpacket.MarketActivityDao;
import daos.ext.redpacket.RedpacketUserDao;
import models.ext.redpacket.entity.t_market_activity;
import services.base.BaseService;

/**
 * 营销活动Service
 * 
 * @author LiuPengwei
 * @createDate 2018年5月16日15:15:09
 *
 */

public class MarketActivityService extends BaseService<t_market_activity> {

	
	protected MarketActivityDao marketActivityDao = null;
	
	protected MarketActivityService() {
		this.marketActivityDao = Factory.getDao(MarketActivityDao.class);
		super.basedao = marketActivityDao;
	}
	
	
	/**
	 * 查询红包活动最新设置的活动时间
	 * 
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年5月16日15:16:29
	 */
	
	public t_market_activity queryRedMarketActivity() {
		
		return marketActivityDao.queryRedMarketActivity();
		
		
	}
	
	/**
	 * 查询加息券活动最新设置的活动时间
	 * 
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年5月29日11:17:41
	 */
	
	public t_market_activity queryAddMarketActivity() {
		
		return marketActivityDao.queryAddMarketActivity();
		
		
	}
	
}
