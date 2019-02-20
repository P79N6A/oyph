package daos.common;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_service_user_relevance;

public class ServiceUserRelevanceDao extends BaseDao<t_service_user_relevance> {

	protected ServiceUserRelevanceDao() {}
	
	/**
	 *  查询客服服务的客服数量
	 *
	 * @param serviceId 客服id
	 * @param type 类型
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年07月15日
	 */
	public int queryCountByUserId(long serviceId, int type) {
		
		StringBuffer sql = new StringBuffer("SELECT count(s.id) from t_service_user_relevance s inner join t_user_info u on s.user_id = u.user_id where 1=1 and s.service_id=:serviceId");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("serviceId", serviceId);
		
		switch (type) {
			case 0:{//所有
				break;
			}
			case 1:{//开户
				sql.append("  AND u.reality_name is not null ");
				break;
			}
		}
		
		return findSingleIntBySQL(sql.toString(), 0, condition);
	}
}
