package daos.common;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import common.utils.DateUtil;
import daos.base.BaseDao;
import models.common.entity.t_user_invest;

public class UserInvestDao extends BaseDao<t_user_invest>{

	protected UserInvestDao () {}
	
	/**
	 * 指定月份的待收查询
	 * @param date 日期
	 * @param user_id 客户id
	 * @author guoShiJie
	 * @createDate 2018.11.5
	 * */
	public Double queryCurrInvestByUserAndDate (Date date , Long user_id) {
		String sql = "select sum(tbi.receive_corpus+tbi.receive_interest) from t_bill_invest tbi inner join t_bid tbd on tbi.bid_id = tbd.id where tbi.user_id = :userId AND DATE_FORMAT( tbd.release_time , '%Y-%m' ) <= DATE_FORMAT( :date , '%Y-%m' ) AND tbi.status = 0 ";
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("userId", user_id);
		condition.put("date", date);
		
		return this.findSingleDoubleBySQL(sql, 0.00, condition);
	}
	
	/**
	 * 添加待收数据
	 * @createDate 2018.11.5
	 * */
	public t_user_invest addCurrInvestByUserAndDate (Long user_id , Date date , Double currInvestAmounts) {
		t_user_invest userInvest = new t_user_invest();
		
		userInvest.user_id = user_id;
		userInvest.curr_total_invest_amounts = new BigDecimal(currInvestAmounts);
		userInvest.invest_time = DateUtil.dateToString(date, "yyyy-MM");
		userInvest.time = new Date();
		
		return this.saveT(userInvest);
	}
	
	/**
	 * 通过月份查询用户的待收
	 * @author guoShiJie
	 * @createDate 2018.11.5
	 * */
	public t_user_invest queryUserInvestByDate (Long user_id,Date date) {
		
		return this.findByColumn("user_id = ? and invest_time = ? ", user_id , DateUtil.dateToString(date, "yyyy-MM"));
	}
}
