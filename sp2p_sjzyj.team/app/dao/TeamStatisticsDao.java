package dao;

import java.util.HashMap;
import java.util.Map;

import models.entity.t_team_statistics;
import daos.base.BaseDao;

public class TeamStatisticsDao extends BaseDao<t_team_statistics> {

	protected TeamStatisticsDao(){
		
	}
	
	/**
	 * 通过supervisorId和type查询一条数据
	 * @param supervisorId
	 * @param type
	 * @return
	 */
	public t_team_statistics findByColumns(long supervisorId, int type, int months, int years) {
		
		String sql = " SELECT * FROM t_team_statistics h WHERE h.supervisor_id =:supervisorId and h.type =:type and h.month =:months and h.year =:years";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("supervisorId", supervisorId);
		condition.put("type", type);
		condition.put("months", months);
		condition.put("years", years);
		
		return super.findBySQL(sql, condition);
	}
}
