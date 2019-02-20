package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import common.utils.Factory;
import daos.common.SettingDao;
import daos.core.BidDao;
import daos.core.InvestDao;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import play.db.jpa.JPA;
import models.bean.Caculate;
import models.common.entity.t_setting_platform;
import models.common.entity.t_supervisor;
import models.common.entity.t_user;
import models.core.entity.t_bid;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import models.entity.t_team_statistics;
import models.entity.t_teams;
import models.main.bean.DiscountRate;
import models.main.bean.TeamRule;

public class TpService {

	protected static final SettingDao settingDao = Factory.getDao(SettingDao.class);

	protected static final InvestDao investDao = Factory.getDao(InvestDao.class);

	/**
	 * author：lihuijun 计算年化折算
	 * 
	 * @param id
	 * @param month
	 * @return
	 */
	public static Caculate findOneMonthById(long id, int month) {
		String sql = "select i.* from t_bid b INNER JOIN t_invest i on b.id=i.bid_id inner join t_user u on u.id=i.user_id LEFT JOIN t_debt_transfer t on  i.debt_id=t.id "
				+ " where b.status>3 and PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( b.release_time, '%Y%m' ) ) =? "
				+ " and u.supervisor_id=? " + " and i.transfer_status<>-1 "
				+ " and ((i.debt_id=0) or (i.debt_id>0 and PERIOD_DIFF( date_format( b.release_time , '%Y%m' ) , date_format( t.end_time, '%Y%m' ) )>0))";
		Query qry = JPA.em().createNativeQuery(sql, t_invest.class);
		qry.setParameter(1, month);
		qry.setParameter(2, id);
		List<t_invest> tdus = qry.getResultList();
		Caculate ca = new Caculate();
		if (tdus.size() > 0) {
			double total = 0;
			double totalMoney = 0;
			double sumMoney = 0;
			for (int i = 0; i < tdus.size(); i++) {
				String sql1 = "select * from t_bid t where t.id=?";
				Query qry1 = JPA.em().createNativeQuery(sql1, t_bid.class);
				qry1.setParameter(1, tdus.get(i).bid_id);
				List<t_bid> tbs = qry1.getResultList();
				if (tbs.size() > 0) {
					List<DiscountRate> lists = new ArrayList<DiscountRate>();
					t_setting_platform setting = settingDao.findByColumn("_key=?", "annual_discount_rate");
					JSONArray jsonArray = JSONArray.fromObject(setting._value);
					lists = (List) JSONArray.toList(jsonArray, new DiscountRate(), new JsonConfig());
					if (tbs.get(0).getPeriod_unit().code == 1) {
						double rate = lists.get(0).discountRate;
						total += tdus.get(i).amount * rate;
						totalMoney += tdus.get(i).amount;
					}
					if (tbs.get(0).getPeriod_unit().code == 2) {
						totalMoney += tdus.get(i).amount * tbs.get(0).period;
						for (DiscountRate rt : lists) {
							if (rt.timeLimit == tbs.get(0).period) {
								total += tdus.get(i).amount * rt.discountRate;
							}
						}
					}
					sumMoney += tdus.get(i).amount;
				}
			}

			ca.convert = total / 100.00;
			ca.totalMoney = totalMoney;
			ca.sumMoney = sumMoney;
			return ca;
		} else {
			ca.convert = 0.00;
			ca.totalMoney = 0.00;
			ca.sumMoney = 0.00;
			return ca;
		}

	}

	/**
	 * author：lihuijun 计算提成
	 * 
	 * @param total
	 * @return
	 */
	public static double caculateDeduct(long id, int month) {
		Caculate ca = findOneMonthById(id, month);
		List<TeamRule> lists = new ArrayList<TeamRule>();
		t_setting_platform setting = settingDao.findByColumn("_key=?", "personal_commission");
		JSONArray jsonArray = JSONArray.fromObject(setting._value);
		lists = (List) JSONArray.toList(jsonArray, new TeamRule(), new JsonConfig());
		double deduct = 0.0;

		for (TeamRule ls : lists) {
			double min = (double) ls.minAmount * 10000.00;
			double max = (double) ls.maxAmount * 10000.00;
			if (ca.convert >= min && ca.convert < max) {
				deduct = ca.totalMoney * ls.amount / 10000.00;
				break;
			}
		}
		return deduct;
	}

	/**
	 * lvweihuan 计算业绩
	 */
	public static double caculateCommission(long id, int month, List<DiscountRate> dreList) {

		String sql = "select i.* from t_bid b INNER JOIN t_invest i on b.id=i.bid_id inner join t_user u on u.id=i.user_id LEFT JOIN t_debt_transfer t on  i.debt_id=t.id "
				+ " where b.status>=3 and PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( b.release_time, '%Y%m' ) ) =? "
				+ " and u.supervisor_id=? " + " and i.transfer_status<>-1 "
				+ " and ((i.debt_id=0) or (i.debt_id>0 and PERIOD_DIFF( date_format(b.release_time , '%Y%m' ) , date_format( t.end_time, '%Y%m' ) )>0))";
		Query qry = JPA.em().createNativeQuery(sql, t_invest.class);
		qry.setParameter(1, month);
		qry.setParameter(2, id);
		List<t_invest> tdus = qry.getResultList();

		double sum = 0;
		BidDao bidDao = new BidDao();
		for (t_invest myInvest : tdus) {
			t_bid myBid = bidDao.findByID(myInvest.bid_id);
			if (myBid.getPeriod_unit().code == 1) {
				for (DiscountRate dreRate : dreList) {
					if (dreRate.timeLimit == 1) {
						sum += myInvest.amount * dreRate.discountRate / 10000;
						break;
					}
				}
			} else {
				for (DiscountRate dreRate : dreList) {
					if (dreRate.timeLimit == myBid.period) {
						sum += myInvest.amount * dreRate.discountRate * myBid.period / 10000;
						break;
					}
				}
			}
		}
		return sum;
	}

	/**
	 * author:guoshijie 普通规则 当月实际发放提成(还息日发放)
	 * 
	 * @param
	 * @return
	 */
	public static double caculateActualMonthCommission(long supervisorId, int month) {

		String billInvestSql = "select tbi.* from t_bill_invest tbi INNER JOIN t_bid tb on tbi.bid_id = tb.id INNER JOIN t_invest ti on tbi.invest_id = ti.id "
				+ " where tbi.status = 1 AND tbi.real_receive_time is NOT NULL AND PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( tbi.real_receive_time, '%Y%m' ) ) =? AND"
				+ " tbi.user_id in (select u.id from t_user u where u.supervisor_id = ? )";

		Query billInvestQry = JPA.em().createNativeQuery(billInvestSql, t_bill_invest.class);
		billInvestQry.setParameter(1, month);
		billInvestQry.setParameter(2, supervisorId);
		
		List<t_bill_invest> billInvest = billInvestQry.getResultList();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
	
			try {
				date = sdf.parse("2018-1-1");
			} catch (ParseException e) {
				
				e.printStackTrace();
			}

		/** 当月实际发放提成 */
		double actualMonthCommission = 0.00;

		/** 当月实际发放的提成(佣金) */
		if (billInvest != null && billInvest.size() > 0) {

			BidDao bidDao = new BidDao();
			for (t_bill_invest bi : billInvest) {

				t_bid bid = bidDao.findByID(bi.bid_id);

				Calendar ca2 = Calendar.getInstance();
				ca2.setTime(bid.release_time);
				int year = ca2.get(Calendar.YEAR);
				int m = ca2.get(Calendar.MONTH) + 1;

				/** 年化 */
				double convert = -1.00;

				List<t_team_statistics> teams = null;
				if (bid.release_time.after(date)) {

					String getDateSql = "select * from t_team_statistics ts where ts.month = ?  AND ts.year = ? "
							+ "AND ts.supervisor_id = ? AND ts.type in (0, 4)";
					Query getDateSqlQry = JPA.em().createNativeQuery(getDateSql, t_team_statistics.class);
					getDateSqlQry.setParameter(1, m);
					getDateSqlQry.setParameter(2, year);
					getDateSqlQry.setParameter(3, supervisorId);
					teams = getDateSqlQry.getResultList();

					if (teams != null && teams.size() > 0) {

						/** 放款当月年化 */
						convert = teams.get(0).year_convert;

					} else {
						Calendar c1 = Calendar.getInstance();
					    Calendar c2 = Calendar.getInstance();
					    c1.setTime(new Date());
					    c2.setTime(bid.release_time);
					    if(c1.getTimeInMillis() >= c2.getTimeInMillis()) {
					    	int year1 = c1.get(Calendar.YEAR);
						    int year2 = c2.get(Calendar.YEAR);
						    int month1 = c1.get(Calendar.MONTH);
						    int month2 = c2.get(Calendar.MONTH);
						    int day1 = c1.get(Calendar.DAY_OF_MONTH);
						    int day2 = c2.get(Calendar.DAY_OF_MONTH);
						    // 获取年的差值 假设 d1 = 2015-8-16 d2 = 2011-9-30
						    int yearInterval = year1 - year2;
						    // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
						    if(month1 < month2 || month1 == month2 && day1 < day2) yearInterval --;
						    // 获取月数差值
						    int monthInterval = (month1 + 12) - month2 ;
						    if(day1 < day2) monthInterval --;
						    monthInterval %= 12;
						    int selm = yearInterval * 12 + monthInterval;
						    convert = findOneMonthById(supervisorId, selm).convert;
					    }
					}
				}

				/** 普通提成规则 */
				List<TeamRule> lists = new ArrayList<TeamRule>();
				t_setting_platform setting = settingDao.findByColumn("_key=?", "personal_commission");
				JSONArray jsonArray = JSONArray.fromObject(setting._value);
				lists = (List) JSONArray.toList(jsonArray, new TeamRule(), new JsonConfig());

				for (TeamRule rule : lists) {

					double min = (double) rule.minAmount * 10000.00;
					double max = (double) rule.maxAmount * 10000.00;

					if (convert >= min && convert < max) {
						t_invest i = investDao.findByID(bi.invest_id);
						/**
						 * 计算规则根据 业务员从一个用户身上一个月获得的提成 = 该用户的投资金额 * amount / 10000
						 */
						if (i != null) {
							
							actualMonthCommission += i.amount * rule.amount / 10000.00;
						}
						
						break;
					}
				}

			}
		}

		return actualMonthCommission;
	}

	/**
	 * author: guoshijie 特殊规则
	 * 
	 * @param
	 * @return
	 */
	public static double caculateActualMonthCommissionSpecial(long supervisorId, int month , List<DiscountRate> dreList) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse("2018-1-1");
		} catch (ParseException e) {

			e.printStackTrace();
		}

		/** 当月实际发放的特殊规则提成(提成) */
		double actualMonthCommissionSpecial = 0.00;
		
		String billInvestSql = "select tbi.* from t_bill_invest tbi INNER JOIN t_bid tb on tbi.bid_id = tb.id INNER JOIN t_invest ti on tbi.invest_id = ti.id "
				+ " where tbi.status = 1 AND tbi.real_receive_time is NOT NULL AND PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( tbi.real_receive_time, '%Y%m' ) ) =? AND"
				+ " tbi.user_id in (select u.id from t_user u where u.supervisor_id = ? )";

		Query billInvestQry = JPA.em().createNativeQuery(billInvestSql, t_bill_invest.class);
		billInvestQry.setParameter(1, month);
		billInvestQry.setParameter(2, supervisorId);
		List<t_bill_invest> billInvest = billInvestQry.getResultList();

		/** 当月实际发放特殊规则提成(佣金) */
		if (billInvest != null && billInvest.size() > 0) {
			BidDao bidDao = new BidDao();
			for (t_bill_invest bi : billInvest) {

				t_bid bid = bidDao.findByID(bi.bid_id);

				if (bid.release_time.after(date)) {

					t_invest i = investDao.findByID(bi.invest_id);
					
					if (i != null) {
						
						if (bid.getPeriod_unit().code == 1) {

							for (DiscountRate dreRate : dreList) {
								if (dreRate.timeLimit == 1) {
									actualMonthCommissionSpecial += i.amount * dreRate.discountRate / 10000;
									break;
								}
							}

						} else {

							for (DiscountRate dreRate : dreList) {
								if (dreRate.timeLimit == bid.period) {
									actualMonthCommissionSpecial += i.amount * dreRate.discountRate / 10000;
									break;

								}
							}

						}
						
					}
					

				}

			}

		}

		return actualMonthCommissionSpecial;
	}

}