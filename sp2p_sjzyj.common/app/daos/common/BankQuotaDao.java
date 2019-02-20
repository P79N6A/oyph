package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_bank_quota;
import models.common.entity.t_credit_level;

public class BankQuotaDao extends BaseDao<t_bank_quota>{
	
	protected BankQuotaDao() {
		
	}
	
	/**
	 * 查询所有银行充值限额信息
	 *
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2016年2月16日
	 */
	public List<Map<String, Object>> queryBankQuotaInfo(){
		
		String sql = "SELECT * FROM t_bank_quota ";
		Map<String, Object> params = new HashMap<String, Object>();
		
		return super.findListMapBySQL(sql, params);
	}
}
