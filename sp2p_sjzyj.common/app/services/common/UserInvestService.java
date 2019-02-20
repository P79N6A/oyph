package services.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.DateUtil;
import common.utils.Factory;
import daos.common.UserInvestDao;
import models.common.entity.t_user_invest;
import services.base.BaseService;

public class UserInvestService extends BaseService<t_user_invest>{

	protected final UserInvestDao userInvestDao = Factory.getDao(UserInvestDao.class);
	
	protected UserInvestService () {
		super.basedao = this.userInvestDao;
	}
	
	/**
	 * 添加待收数据
	 * @param date 待收月份
	 * @author guoShiJie
	 * @createDate 2018.11.5
	 * */
	public t_user_invest addCurrInvestByUserAndDate (Long user_id , Date date ) {
		t_user_invest userInvest = userInvestDao.queryUserInvestByDate(user_id, date);
		
		Double currInvestAmounts = userInvestDao.queryCurrInvestByUserAndDate(date, user_id);
		if (userInvest == null) {
			userInvest = userInvestDao.addCurrInvestByUserAndDate(user_id, date, currInvestAmounts);
		}
		return userInvest;
	}
	
	/**
	 * 通过月份查询用户待收
	 * @param date 指定日期
	 * @param month 
	 * @param user_id 用户id
	 * 
	 * @author guoShiJie
	 * @createDate 2018.11.5
	 * */
	public List<t_user_invest> queryUserInvestListByDate (Date date, Integer month,Long user_id) {
		List<t_user_invest> userInvestList = new ArrayList<t_user_invest>();
		for (int i = month - 1 ; i >= 0 ; i--) {
			Date time = DateUtil.addMonth(date, -i);
			t_user_invest userInvest = userInvestDao.queryUserInvestByDate(user_id, time);
			
			if (userInvest != null) {
				userInvestList.add(userInvest);
			} else {
				Double amount = userInvestDao.queryCurrInvestByUserAndDate(time, user_id);
				t_user_invest ui = new t_user_invest();
				ui.curr_total_invest_amounts = new BigDecimal(amount);
				ui.user_id = user_id;
				ui.invest_time = DateUtil.dateToString(time, "yyyy-MM");
				ui.time = new Date();
				t_user_invest res = userInvestDao.saveT(ui);
				if (res != null) {
					userInvestList.add(res);
				}
			}
		}
		
		return userInvestList;
	}
}
