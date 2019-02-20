package daos.finance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.finance.entity.t_a_loan_receipt;

public class ALoanReceiptDao extends BaseDao<t_a_loan_receipt>{

	public void truncateTable() {
		String sql = " truncate table t_a_loan_receipt";
		this.deleteBySQL(sql, null);
	}

	public List<t_a_loan_receipt> listAll() {
		String sql = " select * from t_a_loan_receipt";
		Map<String, Object> condition = new HashMap<String, Object>();
		return this.findListBySQL(sql, condition);
	}
	
}
