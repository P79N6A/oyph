package daos.ext.cps;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.ext.cps.entity.t_cps_activity;
import models.ext.cps.entity.t_cps_integral;

/**
 * CPS积分设置具体实现dao
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年6月12日
 */
public class CpsIntegralDao extends BaseDao<t_cps_integral> {

	protected CpsIntegralDao() {}
	
	/**
	 * 查询指定活动的老用户奖励积分
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月13日
	 *
	 */
	public t_cps_integral queryIntegralByType(long cpsActivityId, int type){
		
		String sql = "SELECT * FROM t_cps_integral c where c.t_cps_id =:cpsActivityId and c.type =:type";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpsActivityId", cpsActivityId);
		params.put("type", type);
		
		return this.findBySQL(sql, params);
		
	}
	
	/**
	 * 根据类型和活动id删除相应积分
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月13日
	 *
	 */
	public void deleteIntegralByType(long cpsActivityId, int type) {
		
		String sql = "delete from t_cps_integral where t_cps_id =:cpsActivityId and type =:type";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpsActivityId", cpsActivityId);
		params.put("type", type);
		
		this.deleteBySQL(sql, params);
		
	}
}
