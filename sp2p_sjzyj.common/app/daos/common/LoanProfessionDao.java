package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_loan_profession;
import daos.base.BaseDao;

/**
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月27日
 */
public class LoanProfessionDao extends BaseDao<t_loan_profession> {

	protected LoanProfessionDao() {}
	
	/**
	 * 根据标的id查询实体
	 * @param bidId
	 * @return
	 */
	public t_loan_profession queryLoanByBidId(long bidId) {
		String sql = " select * from t_loan_profession p where p.bid_id =:bidId ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		
		return super.findBySQL(sql, condition);
	}
	
	/**
	 * 查询业务信息
	 * @return
	 */
	public List<t_loan_profession> queryByTime() {
		
		Date dateTime = new Date();
		
		dateTime.setHours(0);
		dateTime.setMinutes(0);
		dateTime.setDate(1);
		dateTime.setSeconds(0);

		String sql="SELECT * FROM t_loan_profession p WHERE p.time >= :time";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("time", dateTime);
		
		return this.findListBySQL(sql, condition);
	}
}
