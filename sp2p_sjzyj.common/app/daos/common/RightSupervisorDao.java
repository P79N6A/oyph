package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_right_supervisor;
import daos.base.BaseDao;

/**
 * 管理员权限表
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月23日
 */
public class RightSupervisorDao extends BaseDao<t_right_supervisor> {

	protected RightSupervisorDao() {
	}
	
	/**
	 * 查询管理员的所有的权限(权限操作，而不是权限)
	 *
	 * @param supervisorId 管理员id
	 * @return Key-value:权限id()，权限的action
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月26日
	 */
	public Map<Long, String> queryActionssOfUser(Long supervisorId) {
		
		String querySQL = " SELECT rc.id as rightId,rc.action as rightAction from t_right_action rc WHERE  EXISTS (select rs.id from t_right_supervisor rs  where rs.supervisor_id=:supervisor_id and rs.right_id=rc.right_id) ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("supervisor_id", supervisorId);
		
		List<Map<String, Object>> lists = findListMapBySQL(querySQL, condition);
		if(lists == null || lists.size() == 0){
			
			return null;
		}
		Map<Long, String> map = new HashMap<Long, String>();
		for(Map<String, Object> oMap :lists){

			Long rightId = Long.valueOf(oMap.get("rightId").toString());

			String rightAction = (String) oMap.get("rightAction");
			map.put(rightId, rightAction);
		}
		
		return map;
	}

}
