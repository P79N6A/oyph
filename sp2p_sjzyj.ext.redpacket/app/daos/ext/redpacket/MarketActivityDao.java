package daos.ext.redpacket;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_bank_card_user;
import models.ext.redpacket.entity.t_market_activity;


/**
 * 营销活动Dao
 * 
 * @author LiuPengwei
 * @createDate 2018年5月16日15:15:09
 *
 */

public class MarketActivityDao extends BaseDao<t_market_activity>{

	protected MarketActivityDao() {}
	/**
	 * 查询红包活动最新设置的活动时间
	 * 
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年5月16日15:16:29
	 */
	
	public t_market_activity queryRedMarketActivity() {
		
		String sql = "select * from t_market_activity  where activity_type =1 order by id DESC";
		
		Map<String,Object> condition = new HashMap<String, Object>();
		
		return super.findBySQL(sql, condition);
	}
	
	/**
	 * 查询加息券活动最新设置的活动时间
	 * 
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年5月16日15:16:29
	 */
	
	public t_market_activity queryAddMarketActivity() {
		
		String sql = "select * from t_market_activity  where activity_type =2 order by id DESC";
		
		Map<String,Object> condition = new HashMap<String, Object>();
		
		return super.findBySQL(sql, condition);
	}
	
}
