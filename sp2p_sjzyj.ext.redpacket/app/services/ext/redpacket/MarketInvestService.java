package services.ext.redpacket;

import common.utils.Factory;
import daos.ext.redpacket.MarketActivityDao;
import daos.ext.redpacket.MarketInvestDao;
import models.ext.redpacket.entity.t_market_invest;
import services.base.BaseService;


/**
 * 理财产品营销规则表Service
 * 
 * @author LiuPengwei
 * @createDate 2018年5月16日16:36:37
 *
 */
public class MarketInvestService extends BaseService<t_market_invest>{

	protected MarketInvestDao marketInvestDao = null;
	
	protected MarketInvestService() {
		this.marketInvestDao = Factory.getDao(MarketInvestDao.class);
		super.basedao = marketInvestDao;
	}
	
	
	/**
	 * 投资活动规则下架
	 * 
	 * @param marketInvest
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年5月17日16:15:26
	 */
	public  boolean soldOutInvestMarket (t_market_invest marketInvest){
		
		boolean flag = marketInvestDao.save(marketInvest);
		
		return flag;
	}
	
	
}
