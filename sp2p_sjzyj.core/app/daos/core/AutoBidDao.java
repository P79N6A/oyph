package daos.core;

import java.util.HashMap;
import java.util.Map;

import models.core.entity.t_auto_bid;
import daos.base.BaseDao;

/** 
 * 自动投标接口实现类
 * 
 * @description 
 * 
 * @author ZhouYuanZeng 
 * @createDate 2016年3月25日 上午11:04:39  
 */
public class AutoBidDao extends BaseDao<t_auto_bid>{
	protected AutoBidDao(){}
	
	/**
	 * 根据userId，bidId查询用户自动投标记录
	 *  
	 * @param userId 用户id
	 * @param bidId
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年3月25日
	 *
	 */
	public t_auto_bid findAutoBidByUserIdAndBidId(long userId, long bidId){
		String sql="SELECT * FROM t_auto_bid WHERE user_id =:user_id and bid_id = :bid_id";
		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("user_id", userId);
		condition.put("bid_id", bidId);
		
		return findBySQL(sql, condition);
	}
}
