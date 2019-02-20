package daos.ext.cps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.ext.cps.entity.t_cps_activity;
import models.ext.cps.entity.t_cps_award;

/**
 * CPS奖励设置具体实现dao
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年6月12日
 */
public class CpsAwardDao extends BaseDao<t_cps_award> {

	protected CpsAwardDao() {}
	
	/**
	 * 查询指定活动的奖项列表
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月20日
	 *
	 */
	public List<t_cps_award> queryAwardsByActivityId(long cpsActivityId) {
		
		String sql = "SELECT * FROM t_cps_award c where c.t_cps_id =:cpsActivityId order by c.num desc";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpsActivityId", cpsActivityId);
		
		return this.findListBySQL(sql, params);
	}
}
