package daos.finance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.finance.entity.t_agre_guarantee;

public class AgreGuaranteeDao extends BaseDao<t_agre_guarantee>{

	public void truncateTable() {
		String sql = " truncate table t_agre_guarantee";
		this.deleteBySQL(sql, null);
	}

	public List<t_agre_guarantee> listAll() {
		String sql = " select * from t_agre_guarantee";
		Map<String, Object> condition = new HashMap<String, Object>();
		return this.findListBySQL(sql, condition);
	}

}
