package daos.finance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.finance.entity.t_acct_invest;

public class AcctInvestDao extends BaseDao<t_acct_invest>{

	public void truncateTable() {
		String sql = " truncate table t_acct_invest";
		this.deleteBySQL(sql, null);
	}

	public List<t_acct_invest> listAll() {
		String sql = " select * from t_acct_invest";
		Map<String, Object> condition = new HashMap<String, Object>();
		return this.findListBySQL(sql, condition);
	}

}
