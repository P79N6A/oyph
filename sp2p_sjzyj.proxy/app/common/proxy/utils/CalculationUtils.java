package common.proxy.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import common.utils.Factory;
import daos.base.BaseDao;
import daos.common.UserDao;
import daos.core.BidDao;
import daos.core.InvestDao;
import daos.proxy.ProfitDao;
import daos.proxy.ProxyDao;
import daos.proxy.RuleDao;
import daos.proxy.SalesManDao;
import daos.proxy.SalesManUserDao;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import models.core.entity.t_bid;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import models.proxy.bean.AnnualRate;
import models.proxy.bean.Income;
import models.proxy.bean.ProfitRule;
import models.proxy.entity.t_proxy;
import models.proxy.entity.t_proxy_profit;
import models.proxy.entity.t_proxy_salesman;
import models.proxy.entity.t_proxy_salesman_profit_rule;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import play.db.jpa.JPA;
import proxy.jobs.HistoryDeduct;

/**
 * 计算规则
 * 
 * @author GuoShijie
 * @createDate 2018.01.22
 * */
public class CalculationUtils extends BaseDao<t_user>{
	
	protected CalculationUtils(){}
	
	protected static final InvestDao investDao = Factory.getDao(InvestDao.class);
	
	protected static final BidDao bidDao = Factory.getDao(BidDao.class);
	
	protected static final RuleDao ruleDao = Factory.getDao(RuleDao.class);
	
	protected static final SalesManDao salesManDao = Factory.getDao(SalesManDao.class);
	
	protected static final ProfitDao profitDao = Factory.getDao(ProfitDao.class);
	
	protected static final ProxyDao proxyDao =  Factory.getDao(ProxyDao.class);
	
	protected static final UserDao userDao = Factory.getDao(UserDao.class);
	
	protected static final SalesManUserDao salesManUserDao = Factory.getDao(SalesManUserDao.class); 
	
	/**
	 * 业务员推广理财总金额
	 * @param proxySalesManId 业务员id
	 * @author GuoShijie
	 * @createDate 2018.01.22
	 * */
	public double totalMoney(long proxySalesManId) {
		
		Double amount = new Double(0);
		String sql = "select sum(amount) from t_invest where transfer_status=0 and   user_id in (select id from t_user where proxy_salesMan_id=:proxy_salesMan)";
		Map<String , Object> condition = new HashMap<String , Object>();
		condition.put("proxy_salesMan", proxySalesManId);
		
		amount = (Double) investDao.findSingleByHQL(sql, condition);
		
		return amount == null ? 0 : amount;
	}
	
	/**
	 * 业务员当月推广理财总金额
	 * @param proxySalesManId 业务员id
	 * @author GuoShijie
	 * @createDate 2018.01.22
	 * */
	public double monthTotalMoney(long proxySalesManId,int selmonth) {
		
		Double amount = new Double(0);
		List<t_user> user = userDao.findListByColumn("proxy_salesMan_id = ?", proxySalesManId);
		if (user != null && user.size() > 0) {
			for (t_user u : user) {
				t_user.getT().setMonths(selmonth);
				amount += u.monthAmount;
			}
		}
		
		return amount;
	}
	
	/**
	 * 业务员当月年化折算
	 * @param id 业务员id
	 * @param month 与当月日期（年月）之差
	 * @author GuoShijie
	 * @createDate 2018.01.22
	 * */
	public Income annualConversion(long id , int month) {
		t_proxy_salesman sales = salesManDao.findByID(id);
		
		String sql = "select i.* from t_bid b "
				+ "INNER JOIN t_invest i on b.id=i.bid_id "
				+ "inner join t_user u on u.id=i.user_id "
				+ "LEFT JOIN t_debt_transfer t on  i.debt_id=t.id "
				+ " where b.status>3 and PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( b.release_time, '%Y%m' ) ) =? "
				+ " and u.proxy_salesMan_id=? " 
				+ " and i.transfer_status<>-1 "
				+ " and ((i.debt_id=0) or (i.debt_id>0 and PERIOD_DIFF( date_format( b.release_time , '%Y%m' ) , date_format( t.end_time, '%Y%m' ) )= 0))";
		
		Query qry = JPA.em().createNativeQuery(sql, t_invest.class);
		qry.setParameter(1, month);
		qry.setParameter(2, id);
		List<t_invest> tis = qry.getResultList();
		Income ar = new Income();
		double total = 0.00;
		double totalMoney = 0.00;
		double sumMoney = 0.00;
		if (tis != null && tis.size() > 0) {
			
			for (int i = 0 ; i < tis.size() ; i++) {
				t_bid bid = bidDao.findByID(tis.get(i).bid_id);
				if (bid != null) {
					List<AnnualRate> lists = new ArrayList<AnnualRate>();
					List<t_proxy_salesman_profit_rule> annual = ruleDao.findListByColumn("_key=? AND proxy_id=?" , "annual_discount_rate" ,sales.proxy_id);
					if (annual != null && annual.size() > 0) {
						if (annual.get(0)._value != null && !"".equals(annual.get(0)._value)) {
							JSONArray jsonArray = JSONArray.fromObject(annual.get(0)._value);
							lists = (List) JSONArray.toList(jsonArray , new AnnualRate() , new JsonConfig());
						}
					}
					
					if (lists != null && lists.size() > 0) {
						if (bid.getPeriod_unit().code == 1) {
							double rate = lists.get(0).discountRate;
							total += tis.get(i).amount * rate;
							totalMoney += tis.get(i).amount;
						}
						if (bid.getPeriod_unit().code == 2) {
							totalMoney += tis.get(i).amount * bid.period;
							for (AnnualRate rt : lists) {
								if (rt.timeLimit == bid.period) {
									total += tis.get(i).amount * rt.discountRate;
								}
							}
						}
					}
					
					sumMoney += tis.get(i).amount;
				}
			}
			ar.convert = total / 100.00;
			ar.totalMoney = totalMoney;
			ar.sumMoney = sumMoney;
		} else {
			ar.convert = 0.00;
			ar.totalMoney = 0.00;
			ar.sumMoney = 0.00;
		}
		return ar;
	}
	
	/**
	 * 当月业务员提成
	 * @param salesManId 业务员id
	 * @param selmonth 与当月日期（年月）之差
	 * @author GuoShijie
	 * @createDate 2018.01.22
	 * */
	public double salesmanMonthCommission(long salesManId , int selmonth) {
		
		/**当月业务员提成*/
		double salesmanCommission = 0.00;
		
		/**查询所有理财账单*/
		String billInvestSql = "select tbi.* from t_bill_invest tbi "
				+ "INNER JOIN t_bid tb on tbi.bid_id = tb.id "
				+ "INNER JOIN t_invest ti on tbi.invest_id = ti.id "
				+ " where tbi.status = 1 "
				+ "AND tbi.real_receive_time is NOT NULL "
				+ "AND PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( tbi.real_receive_time, '%Y%m' ) ) =? "
				+ "AND tbi.user_id in (select u.id from t_user u where u.proxy_salesMan_id = ? ) " 
				+ "AND tb.release_time > '2018-01-01 00:00:00'";

		Query billInvestQry = JPA.em().createNativeQuery(billInvestSql, t_bill_invest.class);
		billInvestQry.setParameter(1, selmonth);
		billInvestQry.setParameter(2, salesManId);
		
		List<t_bill_invest> billInvest = billInvestQry.getResultList();
		
		double annualConvert = -1.00;
		
		if(selmonth==0){
			t_proxy_salesman sale = salesManDao.findByID(salesManId);
			if(sale!=null){
				annualConvert=sale.cur_year_convert;
			}
		}else{
			
			Calendar ca = Calendar.getInstance();
			ca.setTime(new Date());
			
			int year = 0;
			int month = 0;
			
			if((ca.get(Calendar.MONTH)+1) == 1) {
				
				month = 12;
				year = ca.get(Calendar.YEAR) - 1;
				
			}else{
				
				month = ca.get(Calendar.MONTH);
				year = ca.get(Calendar.YEAR);
			}
			
			String dateStr = month > 9 ? year+"-"+month : year+"-0"+month;
			
			t_proxy_profit pro = profitDao.findByColumn("sale_id = ? and type = 1 and profit_time = ?", salesManId, dateStr);
			
			if(pro != null) {
				annualConvert = pro.year_convert;
			}
			
		}

		if (billInvest != null && billInvest.size() > 0) {

			for (t_bill_invest bill : billInvest) {

				/**业务员提成规则*/
				List<t_proxy_salesman_profit_rule> rule = null;
				
				t_proxy_salesman sales = salesManDao.findByID(salesManId);
				List<ProfitRule> salesManRule = new ArrayList<ProfitRule>();
				if (sales != null) {
					rule = ruleDao.findListByColumn("_key = ? AND proxy_id = ?", "salesman_rule_1" ,sales.proxy_id);
				}
				if (rule != null && rule.size() > 0) {
					if (rule.get(0)._value != null && !"".equals(rule.get(0)._value)) {
						JSONArray jsonArray = JSONArray.fromObject(rule.get(0)._value);
						salesManRule = (List)JSONArray.toList(jsonArray , new ProfitRule() , new JsonConfig());
					}
				}
				if (salesManRule != null && salesManRule.size() > 0) {
					for (ProfitRule profit : salesManRule) {
						double min = (double)profit.minAmount * 10000.00;
						double max = (double)profit.maxAmount * 10000.00;
						
						if (annualConvert >= min && annualConvert < max) {
							t_invest i = investDao.findByID(bill.invest_id);
							/** 计算规则根据 业务员从一个用户身上一个月获得的提成 = 该用户的投资金额 * amount / 10000 */
							if (i != null) {
								salesmanCommission += i.amount * profit.amount / 10000.00;
							}
							
							break;
						}
					}
				}
			}
		}
		
		return salesmanCommission;
	}
	
/*public  double salesmanMonthCommission(long salesManId , int selmonth) {
		
		*//**当月业务员提成*//*
		double salesmanCommission = 0.00;
		*//**查询所有理财账单*//*

		String billInvestSql = "select tbi.* from t_bill_invest tbi INNER JOIN t_bid tb on tbi.bid_id = tb.id INNER JOIN t_invest ti on tbi.invest_id = ti.id "
				+ " where tbi.status = 1 AND tbi.real_receive_time is NOT NULL AND PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( tbi.real_receive_time, '%Y%m' ) ) =? AND"
				+ " tbi.user_id in (select u.id from t_user u where u.proxy_salesMan_id = ? )";
		
		String billInvestSql ="select tbi.*"
									+ " from t_bill_invest tbi"
									+ " inner join t_bid tb on tbi.bid_id = tb.id" 
									+ " inner join t_invest ti on tbi.invest_id = ti.id"
							  +" where tbi.status = 1"
									 + " and tbi.real_receive_time is not null"
									 + " and period_diff(date_format(now(),'%Y%m'), date_format(tbi.real_receive_time,'%Y%m')) = ?"
									 + " and tbi.user_id in (select u.id"
									      + " from t_user u"
									      + " where u.proxy_salesman_id = ?)";

		

		Query billInvestQry = JPA.em().createNativeQuery(billInvestSql, t_bill_invest.class);
		billInvestQry.setParameter(1, selmonth);
		billInvestQry.setParameter(2, salesManId);
		
		List<t_bill_invest> billInvest = billInvestQry.getResultList();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date date = null;
		
		try {
			date = sdf.parse("2018-01-01");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (billInvest != null && billInvest.size() > 0) {
			
			//BidDao bidDao = new BidDao();
			int xx=0;
			for (t_bill_invest bill : billInvest) {
				
				
				t_bid bid = bidDao.findByID(bill.bid_id);
				xx++;
				System.out.println("xx ="+xx);
				
				*//** 年化 *//*
				double annualConvert = -1.00;
				
				if (bid != null) {
					Calendar ca2 = Calendar.getInstance();
					ca2.setTime(bid.release_time);
					int year = ca2.get(Calendar.YEAR);
					int month = ca2.get(Calendar.MONTH)+1;
					
					if (bid.release_time.after(date)) {
						List<t_proxy_profit> profit = profitDao.findListByColumn("sale_id = ? AND profit_time = ? AND type = ? ",salesManId, month > 9 ? year+"-"+month : year+"-0"+month, 1);
						if (profit != null && profit.size() > 0) {
							*//**放款当月年化*//*
							annualConvert = profit.get(0).year_convert;
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
								annualConvert = annualConversion(salesManId , selm).convert;
						    }
						    
						}
					}
				}
				
				*//**业务员提成规则*//*
				List<t_proxy_salesman_profit_rule> rule = null;
				
				t_proxy_salesman sales = salesManDao.findByID(salesManId);
				List<ProfitRule> salesManRule = new ArrayList<ProfitRule>();
				if (sales != null) {
					rule = ruleDao.findListByColumn("_key = ? AND proxy_id = ?", "salesman_rule_1" ,sales.proxy_id);
				}
				if (rule != null && rule.size() > 0) {
					if (rule.get(0)._value != null && !"".equals(rule.get(0)._value)) {
						JSONArray jsonArray = JSONArray.fromObject(rule.get(0)._value);
						salesManRule = (List)JSONArray.toList(jsonArray , new ProfitRule() , new JsonConfig());
					}
				}
				if (salesManRule != null && salesManRule.size() > 0) {
					for (ProfitRule profit : salesManRule) {
						double min = (double)profit.minAmount * 10000.00;
						double max = (double)profit.maxAmount * 10000.00;
						if (annualConvert >= min && annualConvert < max) {
							t_invest i = investDao.findByID(bill.invest_id);
							*//** 计算规则根据 业务员从一个用户身上一个月获得的提成 = 该用户的投资金额 * amount / 10000 *//*
							if (i != null) {
								salesmanCommission += i.amount * profit.amount / 10000.00;
							}
							
							break;
						}
					}
				}
				
			}
			
		}
		return salesmanCommission;
	}*/
	
	/**
	 * 代理商推广理财总金额
	 * @param proxyId 代理商id
	 * @author GuoShijie
	 * @createDate 2018.01.23
	 * */
	public double proxyTotalMoney(long proxyId) {
		Double amount = new Double(0);
		
		List<t_proxy_salesman> salesman = salesManDao.findListByColumn("proxy_id = ?", proxyId);
		if (salesman != null && salesman.size() > 0) {
			for (t_proxy_salesman sale : salesman) {
				String sql = "select sum(amount) from t_invest where transfer_status=0 and   user_id in (select id from t_user where proxy_salesMan_id=:salesMan_id )";
				Map<String , Object> condition = new HashMap<String , Object>();
				condition.put("salesMan_id" , sale.id);
				amount += (Double) investDao.findSingleByHQL(sql, condition) == null ? 0 : (Double) investDao.findSingleByHQL(sql, condition);
			}
		}
		
		return amount;
	}
	
	/**
	 * 代理商当月推广理财总金额
	 * @param proxyId 代理商id
	 * @author GuoShijie
	 * @createDate 2018.01.23
	 * */
	public double proxyMonthTotalMoney(long proxyId,int selmonth) {
		Double amount = new Double(0);
		List<t_proxy_salesman> salesman = salesManDao.findListByColumn("proxy_id = ?", proxyId);
		
		if (salesman != null && salesman.size() > 0) {
			for (t_proxy_salesman sale : salesman) {
				amount += monthTotalMoney(sale.id , selmonth);
			}
		}
		
		return amount;
	}
	
	/**
	 * 当月代理商提成
	 * @param proxyId 代理商id
	 * @param selmonth 与当月日期（年月）之差
	 * @author GuoShijie
	 * @createDate 2018.01.22
	 * */
	public double proxyMonthCommission(long proxyId , int selmonth) {
		
		double result = 0.00;
		double saleCommission = 0.00;
		double proxyCommission = 0.00;
		
		/**代理商提成规则*/
		List<AnnualRate> proxyRule = new ArrayList<AnnualRate>();
		t_proxy proxy = proxyDao.findByID(proxyId);
		if (proxy != null) {
			if (proxy.profit_rule != null && !"".equals(proxy.profit_rule)) {
				JSONArray jsonArray = JSONArray.fromObject(proxy.profit_rule);
				proxyRule = (List)JSONArray.toList(jsonArray, new AnnualRate(), new JsonConfig());
			}
		}
		
		List<t_proxy_salesman> salesman = salesManDao.findListByColumn("proxy_id = ?", proxyId);
		
		if (salesman != null && salesman.size() > 0) {
			for (t_proxy_salesman man : salesman) {
				
				String billInvestSql = "select tbi.* from t_bill_invest tbi "
						+ "INNER JOIN t_bid tb on tbi.bid_id = tb.id "
						+ "INNER JOIN t_invest ti on tbi.invest_id = ti.id "
						+ " where tbi.status = 1 "
						+ "AND tbi.real_receive_time is NOT NULL "
						+ "AND PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( tbi.real_receive_time, '%Y%m' ) ) =? "
						+ "AND tbi.user_id in (select u.id from t_user u where u.proxy_salesMan_id = ? )";
				
				Query billInvestQry = JPA.em().createNativeQuery(billInvestSql, t_bill_invest.class);
				billInvestQry.setParameter(1, selmonth);
				billInvestQry.setParameter(2, man.id);
				List<t_bill_invest> billInvest = billInvestQry.getResultList();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				
				Date date = null;
				
				try {
					date = sdf.parse("2018-01-01");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(billInvest != null && billInvest.size() > 0) {
					
					BidDao bidDao = new BidDao();
					for (t_bill_invest bill : billInvest) {
						
						t_bid bid = bidDao.findByID(bill.bid_id);
						if (bid != null) {
							if (bid.release_time.after(date)) {
								t_invest i = investDao.findByID(bill.invest_id);
								
								if (i != null && proxyRule != null && proxyRule.size() > 0) {
									if (bid.getPeriod_unit().code == 1) {
										for (AnnualRate rule : proxyRule) {
											if (rule.timeLimit == 1) {
												proxyCommission += i.amount * rule.discountRate / 10000;
												break;
											}
										}
									} else { 
										for (AnnualRate rule : proxyRule) {
											if (rule.timeLimit == bid.period) {
												proxyCommission += i.amount * rule.discountRate / 10000;
												break;

											}
										}
									}
								}
							}
						}

					}
					
				}
			}
			
			for (t_proxy_salesman man : salesman) {
				saleCommission += salesmanMonthCommission(man.id , selmonth);
			}
		}
		
		return result = proxyCommission - saleCommission;
	}
	
	/**
	 * 代理商年化折算
	 * @param proxyId 代理商id
	 * @param month 与当月日期（年月）之差
	 * @author GuoShijie
	 * @createDate 2018.01.23
	 * */
	public double proxyAnnualConversion(long proxyId , int month) {
		
		/**代理商年化*/
		double proxyAnnual = 0.00;
		
		List<t_proxy_salesman> salesman = salesManDao.findListByColumn("proxy_id = ?", proxyId);
		
		if (salesman != null && salesman.size() > 0) {
			for (t_proxy_salesman sm : salesman) {
				proxyAnnual += annualConversion(sm.id, month).convert;
			}
		}
		
		return proxyAnnual;
	}
	
	/**
	 * 业务员当月推广会员人数
	 * @param salesManId 业务员id
	 * @author GuoShijie
	 * @createDate 2018.02.01
	 * */
	public int salesManCurUserCount(long salesManId , int selmonth) {
		return (int) t_user.count("proxy_salesMan_id = "+salesManId+" AND PERIOD_DIFF(date_format(now( ) , '%Y%m') , date_format(time , '%Y%m')) ="+selmonth);
	}
	
	/**
	 * 业务员当月理财会员人数
	 * @param salesManId 业务员id
	 * @author GuoShijie
	 * @createDate 2018.02.02
	 * */
	public int salesManMonthAmountCount(long salesManId,int selmonth) {
		int count = 0;
		
		String sql = "select * from t_user where proxy_salesMan_id = :salesManId";
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("salesManId", salesManId);
		List<t_user> user = findListBySQL(sql , condition);
		if (user != null && user.size() > 0) {
			for (t_user u : user) {
				t_user.getT().setMonths(selmonth);
				if (u.monthAmount != 0) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * 代理商理财会员数量
	 * @param proxyId 代理商id
	 * @author GuoShijie
	 * @createDate 2018.02.02
	 * */
	public int proxyAmountUserCount(long proxyId) {
		int count = 0;
		List<t_proxy_salesman> salesman = salesManDao.findListByColumn("proxy_id = ?", proxyId);
		if (salesman != null && salesman.size() > 0) {
			for (t_proxy_salesman sales : salesman) {
				count += (int)t_user_info.count("invest_member_time is not null and user_id in( select id from t_user where proxy_salesMan_id="+sales.id+")");
			}
		}
		return count;
	}
	
	/**
	 * 代理商总提成
	 * @author GuoShijie
	 * @createDate 2018.02.05
	 * */
	public double proxyTotalProfit(long proxyId , int selmonth) {
		double proxyProfit = 0.00;
		List<t_proxy_profit> profit = profitDao.findListByColumn("sale_id=? AND type=2 AND PERIOD_DIFF(date_format(now( ) , '%Y%m') , date_format(str_to_date(profit_time , '%Y-%m') , '%Y%m') )> ?", proxyId , selmonth);
		if (profit != null && profit.size() > 0) {
			for (t_proxy_profit p : profit) {
				proxyProfit += p.profit;
			}
		}
		proxyProfit += proxyMonthCommission(proxyId, selmonth);
		return proxyProfit;
	}
	
	/**
	 * 业务员总提成
	 * @param 2018.02.05
	 * @author GuoShijie
	 * @createDate
	 * */
	public double salesManTotalProfit(long salesManId , int selmonth) {
		double salesManProfit = 0.00;
		List<t_proxy_profit> proxyProfit = profitDao.findListByColumn("sale_id=? AND PERIOD_DIFF(date_format(now( ) , '%Y%m') , date_format(str_to_date(profit_time , '%Y-%m') , '%Y%m') )> ?  AND type=1" , salesManId , selmonth);
		if (proxyProfit != null && proxyProfit.size() > 0) {
			for (t_proxy_profit profit : proxyProfit) { 
				salesManProfit += profit.profit;
			}
		}
		salesManProfit += salesmanMonthCommission(salesManId, selmonth);
		return salesManProfit;
	}
	
	/**
	 * 代理商总推广会员
	 * @author GuoShijie
	 * @createDate 2018.02.22
	 * */
	public int proxyTotalUser(long proxyId) {
		int count = 0;
		List<t_proxy_salesman> salesman = salesManDao.findListByColumn("proxy_id = ?", proxyId);
		if (salesman != null && salesman.size() > 0) {
			for (t_proxy_salesman sales : salesman) {
				count += (int)t_user.count("proxy_salesMan_id=?" , sales.id);
			}
		}
		return count;
	}
	
	/**
	 * 删除代理商下多余会员
	 * @author GuoShijie
	 * @createDate 2018.03.13
	 * */
	public static void deleteMoreProxyUser() {
		String sql = "delete from t_proxy_user where user_id in ( select id from t_user where proxy_salesMan_id = 0 )";
		salesManUserDao.deleteBySQL(sql, null);
	}
	
	/**
	 * 删除多余的收益记录
	 * @author GuoShijie
	 * @createDate 2018.04.02
	 * */
	public static void deleteMoreProxyProfit() {
		String sql = "delete from t_proxy_profit where id not in (select minid from (select min(id) as minid from t_proxy_profit GROUP BY sale_id,profit_time,type) b)";
		profitDao.deleteBySQL(sql, null);
		
	}
	
	//*****************************************
	/**
	 * 
	 * @Title: saleTotalProfit   
	 * @Description: 业务员除本月的收益和 
	 * @param @param salesManId
	 * @param @param selmonth
	 * @param @return    
	 * @return: double 
	 * @author lihuijun 
	 * @date 2018年4月23日 上午11:18:16    
	 * @throws
	 */
	public double saleTotalProfit(long salesManId , int selmonth) {
		double salesManProfit = 0.00;
		List<t_proxy_profit> proxyProfit = profitDao.findListByColumn("sale_id=? AND PERIOD_DIFF(date_format(now( ) , '%Y%m') , date_format(str_to_date(profit_time , '%Y-%m') , '%Y%m') )> ?  AND type=1" , salesManId , selmonth);
		if (proxyProfit != null && proxyProfit.size() > 0) {
			for (t_proxy_profit profit : proxyProfit) { 
				salesManProfit += profit.profit;
			}
		}
		return salesManProfit;
	}
	
	/**
	 * 
	 * @Title: proxyCurrTotalMoney   
	 * @Description: 代理商本月投资额
	 * @param @param proxyId
	 * @param @param selmonth
	 * @param @return    
	 * @return: double 
	 * @author lihuijun 
	 * @date 2018年4月23日 上午11:26:04    
	 * @throws
	 */
	public double proxyCurrTotalMoney(long proxyId,int selmonth) {
		Double amount = new Double(0);
		List<t_proxy_salesman> salesman = salesManDao.findListByColumn("proxy_id = ?", proxyId);
		
		if (salesman != null && salesman.size() > 0) {
			for (t_proxy_salesman sale : salesman) {
				amount += sale.cur_invest_amount;
			}
		}
		
		return amount;
	}
	
	/**
	 * 
	 * @Title: proxyAmountUserCount   
	 * @Description: 代理商理财会员数量
	 * @param @param proxyId
	 * @param @return    
	 * @return: int 
	 * @author lihuijun 
	 * @date 2018年4月23日 上午11:44:08    
	 * @throws
	 */
	public int proxyAmUserCount(long proxyId) {
		int count = 0;
		List<t_proxy_salesman> salesman = salesManDao.findListByColumn("proxy_id = ?", proxyId);
		if (salesman != null && salesman.size() > 0) {
			for (t_proxy_salesman sales : salesman) {
				count += sales.total_invest_user;
			}
		}
		return count;
	}
	
	/**
	 * 
	 * @Title: proxyTotalUser   
	 * @Description: 代理商总推广会员
	 * @param @param proxyId
	 * @param @return    
	 * @return: int 
	 * @author lihuijun 
	 * @date 2018年4月23日 上午11:50:30    
	 * @throws
	 */
	public int proxyTotUser(long proxyId) {
		int count = 0;
		List<t_proxy_salesman> salesman = salesManDao.findListByColumn("proxy_id = ?", proxyId);
		if (salesman != null && salesman.size() > 0) {
			for (t_proxy_salesman sales : salesman) {
				count += sales.total_user;
			}
		}
		return count;
	}
	
	/**
	 * 
	 * @Title: proxyTotMoney   
	 * @Description: 代理商推广理财总额
	 * @param @param proxyId
	 * @param @return    
	 * @return: double 
	 * @author lihuijun 
	 * @date 2018年4月23日 上午11:54:16    
	 * @throws
	 */
	
	public double proxyTotMoney(long proxyId) {
		Double amount = new Double(0);
		
		List<t_proxy_salesman> salesman = salesManDao.findListByColumn("proxy_id = ?", proxyId);
		if (salesman != null && salesman.size() > 0) {
			for (t_proxy_salesman sale : salesman) {

				amount +=sale.total_amount;
				
			}
		}
		return amount;
	}
	
	/**
	 * 
	 * @Title: proxyAnnConversion   
	 * @Description: 代理商本月年化 
	 * @param @param proxyId
	 * @param @param month
	 * @param @return    
	 * @return: double 
	 * @author lihuijun 
	 * @date 2018年4月23日 上午11:59:34    
	 * @throws
	 */
	public double proxyAnnConversion(long proxyId , int month) {
		
		/**代理商年化*/
		double proxyAnnual = 0.00;
		
		List<t_proxy_salesman> salesman = salesManDao.findListByColumn("proxy_id = ?", proxyId);
		
		if (salesman != null && salesman.size() > 0) {
			for (t_proxy_salesman sm : salesman) {
				proxyAnnual += sm.cur_year_convert;
			}
		}
		
		return proxyAnnual;
	}
	
	/**
	 * 
	 * @Title: proxyCurrCommission   
	 * @Description: 代理商本月提成
	 * @param @param proxyId
	 * @param @param selmonth
	 * @param @return    
	 * @return: double 
	 * @author lihuijun 
	 * @date 2018年4月23日 下午3:04:00    
	 * @throws
	 */
	public double proxyCurrCommission(long proxyId , int selmonth) {
			
			double saleCommission = 0.00;
			double proxyCommission = 0.00;
			List<AnnualRate> proxyRule = new ArrayList<AnnualRate>();
			t_proxy proxy = proxyDao.findByID(proxyId);
			
			if(proxy!=null){
				if (proxy.profit_rule != null && !"".equals(proxy.profit_rule)) {
					JSONArray jsonArray = JSONArray.fromObject(proxy.profit_rule);
					proxyRule = (List)JSONArray.toList(jsonArray, new AnnualRate(), new JsonConfig());
				}
			}
			
			List<t_proxy_salesman> salesman = salesManDao.findListByColumn("proxy_id = ?", proxyId);
			
			if(salesman!=null && salesman.size()>0){
				for (t_proxy_salesman man : salesman) {
					
					proxyCommission += saleOfProxyRuleProfit(man.id , 0, proxyRule);
					
					saleCommission  += saleOfRuleProfit(man.id);
				}
				
				//等待添加逻辑
			}
			return proxyCommission-saleCommission;
	}
	
	/**
	 * 
	 * @Title: saleOfProxyRuleProfit   
	 * @Description: 代理商规则下的业务员提成 
	 * @param @param saleId
	 * @param @param selmonth
	 * @param @return    
	 * @return: double 
	 * @author lihuijun 
	 * @date 2018年4月23日 下午3:08:48    
	 * @throws
	 */
	public double saleOfProxyRuleProfit(long saleId , int selmonth,List<AnnualRate> proxyRule){
		
		double proxyCommission = 0.00;
		
		String billInvestSql = "select tbi.* from t_bill_invest tbi "
				+ "INNER JOIN t_bid tb on tbi.bid_id = tb.id "
				+ "INNER JOIN t_invest ti on tbi.invest_id = ti.id "
				+ " where tbi.status = 1 "
				+ "AND tbi.real_receive_time is NOT NULL "
				+ "AND PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( tbi.real_receive_time, '%Y%m' ) ) =? "
				+ "AND tbi.user_id in (select u.id from t_user u where u.proxy_salesMan_id = ? ) "
				+ "AND tb.release_time > '2018-01-01 00:00:00'";
		
		Query billInvestQry = JPA.em().createNativeQuery(billInvestSql, t_bill_invest.class);
		billInvestQry.setParameter(1, selmonth);
		billInvestQry.setParameter(2, saleId);
		
		List<t_bill_invest> billInvest = billInvestQry.getResultList();
		
		if(billInvest != null && billInvest.size() > 0) {
			for (t_bill_invest bill : billInvest) {		
				t_bid bid = bidDao.findByID(bill.bid_id);
				if (bid != null) {
						t_invest i = investDao.findByID(bill.invest_id);
						
						if (i != null && proxyRule != null && proxyRule.size() > 0) {
							if (bid.getPeriod_unit().code == 1) {
								for (AnnualRate rule : proxyRule) {
									if (rule.timeLimit == 1) {
										proxyCommission += i.amount * rule.discountRate / 10000;
										break;
									}
								}
							} else { 
								for (AnnualRate rule : proxyRule) {
									if (rule.timeLimit == bid.period) {
										proxyCommission += i.amount * rule.discountRate / 10000;
										break;

									}
								}
							}
						}
					}
				}
			}
		return proxyCommission;
	}
	
	/**
	 * 
	 * @Title: saleOfRuleProfit   
	 * @Description:  业务员规则下的业务员提成
	 * @param @param salesManId
	 * @param @param selmonth
	 * @param @return    
	 * @return: double 
	 * @author lihuijun 
	 * @date 2018年4月23日 下午4:05:13    
	 * @throws
	 */
	public double saleOfRuleProfit(long salesManId){
		
		double saleCommission = 0.00;
		
		t_proxy_salesman sale = salesManDao.findByID(salesManId);
		
		if(sale!=null)
			saleCommission = sale.cur_profit;
		
		return saleCommission;
	}
	
	/**
	 * 
	 * @Title: proxyTotProfit   
	 * @Description: 代理商除本月的提成和 
	 * @param @param proxyId
	 * @param @param selmonth
	 * @param @return    
	 * @return: double 
	 * @author lihuijun 
	 * @date 2018年4月23日 下午4:23:06    
	 * @throws
	 */
	public double proxyTotProfit(long proxyId , int selmonth) {
		double proxyProfit = 0.00;
		List<t_proxy_profit> profit = profitDao.findListByColumn("sale_id=? AND type=2 AND PERIOD_DIFF(date_format(now( ) , '%Y%m') , date_format(str_to_date(profit_time , '%Y-%m') , '%Y%m') )> ?", proxyId , selmonth);
		if (profit != null && profit.size() > 0) {
			for (t_proxy_profit p : profit) {
				proxyProfit += p.profit;
			}
		}
		return proxyProfit;
	}
	
	/**
	 * 
	 * @Title: proxyHistoryTotalMoney   
	 * @Description: 计算代理商上个月投资额与年化
	 * @param @param proxyId
	 * @param @param selmonth
	 * @param @return    
	 * @return: double 
	 * @author lihuijun 
	 * @date 2018年4月24日 下午5:52:22    
	 * @throws
	 */
	
	public t_proxy_profit proxyHistoryTotalMoney(long proxyId,int selmonth){
		
		double amount = 0.0;
		double annual = 0.0;
		double prof = 0.0;
		
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		
		int year = 0;
		int month = 0;
		
		if((ca.get(Calendar.MONTH)+1) == 1) {
			
			month = 12;
			year = ca.get(Calendar.YEAR) - 1;
			
		}else{
			
			month = ca.get(Calendar.MONTH);
			year = ca.get(Calendar.YEAR);
		}
		
		String dateStr = month > 9 ? year+"-"+month : year+"-0"+month;
		
		
		String sql = "select p.* from t_proxy_profit p where p.type = 1 and p.profit_time =:time and p.sale_id in (select s.id from t_proxy_salesman s where s.proxy_id = :proxyId)";
		
		Map<String,Object> condition = new HashMap<String,Object>();
		
		condition.put("time", dateStr);
		
		condition.put("proxyId", proxyId);
		
		t_proxy_profit profit = new t_proxy_profit();
		
		List<t_proxy_profit> profitList=profitDao.findListBySQL(sql, condition);
		if(profitList!=null && profitList.size()>0){
			for(t_proxy_profit pro:profitList){
				amount+=pro.invest_amount;
				annual+=pro.year_convert;
				prof  +=pro.profit;
			}
		}
		
		profit.invest_amount = amount;
		profit.year_convert = annual;
		profit.profit = prof;
		
		return profit;
	}
	
	/**
	 * 
	 * @Title: proxyHistoryCommission   
	 * @Description: 
	 * @param @param proxyId
	 * @param @param selmonth
	 * @param @return    
	 * @return: double 
	 * @author lihuijun 
	 * @date 2018年4月25日 上午10:23:19    
	 * @throws
	 */
	public double proxyHistoryCommission(long proxyId , int selmonth) {
		
		double saleCommission = 0.00;
		double proxyCommission = 0.00;
		List<AnnualRate> proxyRule = new ArrayList<AnnualRate>();
		t_proxy proxy = proxyDao.findByID(proxyId);
		
		if(proxy!=null){
			if (proxy.profit_rule != null && !"".equals(proxy.profit_rule)) {
				JSONArray jsonArray = JSONArray.fromObject(proxy.profit_rule);
				proxyRule = (List)JSONArray.toList(jsonArray, new AnnualRate(), new JsonConfig());
			}
		}
		
		List<t_proxy_salesman> salesman = salesManDao.findListByColumn("proxy_id = ?", proxyId);
		
		if(salesman!=null && salesman.size()>0){
			for (t_proxy_salesman man : salesman) {
				
				proxyCommission += saleOfProxyRuleProfit(man.id , 1, proxyRule);
				
			}
			saleCommission  = proxyHistoryTotalMoney(proxy.id, 1).profit;
			
			//等待添加逻辑
		}
		return proxyCommission-saleCommission;
}

	
}