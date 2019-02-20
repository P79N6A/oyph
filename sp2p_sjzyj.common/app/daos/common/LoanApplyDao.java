package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_loan_apply;
import daos.base.BaseDao;

/**
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月27日
 */
public class LoanApplyDao extends BaseDao<t_loan_apply> {

	protected LoanApplyDao() {}
	
	/**
	 * 查询申请信息
	 * @return
	 */
	public List<t_loan_apply> queryByTime() {
		
		Date dateTime = new Date();
		
		dateTime.setHours(0);
		dateTime.setMinutes(0);
		dateTime.setDate(1);
		dateTime.setSeconds(0);

		String sql="SELECT * FROM t_loan_apply p WHERE p.time >= :time";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("time", dateTime);
		
		return this.findListBySQL(sql, condition);
	}
}
