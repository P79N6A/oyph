package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_loan_contract;
import daos.base.BaseDao;

/**
 * 贷款合同Dao
 *  
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月27日
 */

public class LoanContractDao extends BaseDao<t_loan_contract> {

	protected LoanContractDao() {}
	
	/**
	 * 查询合同信息
	 * @return
	 */
	public List<t_loan_contract> queryByTime() {

		Date dateTime = new Date();
		
		dateTime.setHours(0);
		dateTime.setMinutes(0);
		dateTime.setDate(1);
		dateTime.setSeconds(0);

		String sql="SELECT * FROM t_loan_contract p WHERE p.begin_time >= :time";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("time", dateTime);
		
		return this.findListBySQL(sql, condition);
	}
}
