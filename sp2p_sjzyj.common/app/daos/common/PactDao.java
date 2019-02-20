package daos.common;

import models.common.bean.CostBean;
import models.common.entity.t_pact;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;

/**
 * 合同dao的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年1月18日
 */
public class PactDao extends BaseDao<t_pact> {

	protected PactDao() {
	}

	public List<t_pact> findListByType(int i) {
		String sql = " SELECT *  FROM t_pact AS t_p WHERE t_p.type= :type GROUP BY pid";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", i);
		return this.findListBySQL(sql, condition);
	}

	public t_pact findByCondition(Long bidId, Long userId) {
		String sql = " SELECT * FROM t_pact WHERE pid=:bidId AND user_id=:userId AND type = 0";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		condition.put("userId", userId);
		return findBySQL(sql, condition);
	}
	
}
