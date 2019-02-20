package daos.ext.cps;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.ext.cps.entity.t_cps_rate;

/**
 * CPS加息卷设置具体实现dao
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年6月12日
 */
public class CpsRateDao extends BaseDao<t_cps_rate> {

	protected CpsRateDao() {}
	
	/**
	 * 查询指定活动的老用户奖励加息卷
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月13日
	 *
	 */
	public t_cps_rate queryRateByType(long cpsActivityId, int type) {
		String sql = "SELECT * FROM t_cps_rate c where c.t_cps_id =:cpsActivityId and c.type =:type and c.status = 1";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpsActivityId", cpsActivityId);
		params.put("type", type);
		
		return this.findBySQL(sql, params);
	}
	
	/**
	 * 根据类型和活动id删除相应加息卷
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月13日
	 *
	 */
	public void deleteRateByType(long cpsActivityId, int type) {
		
		String sql = "delete from t_cps_rate where t_cps_id =:cpsActivityId and type =:type and status = 1";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpsActivityId", cpsActivityId);
		params.put("type", type);
		
		this.deleteBySQL(sql, params);
		
	}
}
