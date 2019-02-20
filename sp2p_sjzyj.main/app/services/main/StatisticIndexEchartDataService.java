package services.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.main.entity.t_statistic_index_echart_data;
import services.base.BaseService;
import services.common.RechargeUserService;
import services.common.UserInfoService;
import services.common.UserService;
import services.core.InvestService;

import common.constants.Constants;
import common.utils.ECharts;
import common.utils.Factory;

import dao.main.StatisticIndexEchartDataDao;

public class StatisticIndexEchartDataService extends BaseService<t_statistic_index_echart_data> {
			
	protected StatisticIndexEchartDataDao statisticIndexEchartDataDao = Factory.getDao(StatisticIndexEchartDataDao.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static RechargeUserService rechargeUserService = Factory.getService(RechargeUserService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected StatisticIndexEchartDataService(){
		super.basedao = this.statisticIndexEchartDataDao;
	} 
	
	/**
	 * 获取相关会员数目Echarts
	 * @param type 查询类型 1、昨日 2、最近7天 3、最近30天
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	public ECharts findMembersCount(int type) {
		/* 新增理财会员 */
		List<Object> financiaCount = statisticIndexEchartDataDao.findFinanciaCount(type);
									 
		/* 新增注册会员 */
		List<Object> RegisterCount = statisticIndexEchartDataDao.findRegisterCount(type);
		
		/* 昨日时间点 */
		String[] axis = getAxisDate(type, "MM月dd日");
		String[] legend = { "financia", "register" };
		Object[] financiaObj = financiaCount.toArray();
		Object[] registerObj = RegisterCount.toArray();
		String[] financiaArray = new String[financiaObj.length];
		String[] registerArray = new String[registerObj.length];
		for (int i = 0; i < financiaObj.length; i++) {
			financiaArray[i] = financiaObj[i].toString();
			registerArray[i] = registerObj[i].toString();
		}

		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("financia", financiaArray);
		map.put("register", registerArray);

		ECharts charts = new ECharts(axis, legend, map);
		return charts;
	}

	/**
	 * 获取相关金额数目Echarts
	 * @param type 查询类型 1、昨日 2、最近7天 3、最近30天
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	public ECharts findMoneyNumber(int type) {
		/* 投资金额 */
		List<Object> investMoney = statisticIndexEchartDataDao.findInvestMoney(type); 
		
		/* 充值金额 */
		List<Object> rechargeMoney = statisticIndexEchartDataDao.findRechargeMoney(type);
		/* 昨日时间点 */
		String[] axis = getAxisDate(type, "MM月dd日");
		String[] legend = { "invest", "recharger" };
		Object[] investObj = investMoney.toArray();
		Object[] rechargeObj = rechargeMoney.toArray();
		String[] investArray = new String[investObj.length];
		String[] rechargeArray = new String[rechargeObj.length];
		for (int i = 0; i < investObj.length; i++) {
			investArray[i] = investObj[i].toString();
			rechargeArray[i] = rechargeObj[i].toString();
		}

		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("invest", investArray);
		map.put("recharger", rechargeArray);

		ECharts charts = new ECharts(axis, legend, map);
		return charts;
	}
	
	/**
	 * 获取相关金额数目Echarts
	 * @param type 查询类型 1、昨日 2、最近7天 3、最近30天
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	public ECharts findDatas(int type) {
		
		/* 前台访问量 */
		List<Object> datas = statisticIndexEchartDataDao.findDatas(type);
		
		/* 昨日时间点 */
		String[] axis = getAxisDate(type, "MM月dd日");
		String[] legend = { "invest" };
		Object[] datasObj = datas.toArray();
		String[] datasArray = new String[datasObj.length];
		for (int i = 0; i < datasObj.length; i++) {
			datasArray[i] = datasObj[i].toString();
		}

		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("invest", datasArray);

		ECharts charts = new ECharts(axis, legend, map);
		return charts;
	}

	/**
	 * 获取相应时间数组
	 * @param type
	 * @param formatType
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	private static String[] getAxisDate(int type, String formatType) {
		String[] axis = null;
		SimpleDateFormat format = new SimpleDateFormat(formatType);
		switch (type) {
		case Constants.YESTERDAY:
			axis = new String[] { "2:00", "4:00", "6:00", "8:00", "10:00",
					"12:00", "14:00", "16:00", "18:00", "20:00", "22:00",
					"00:00" };
			break;
		case Constants.LAST_7_DAYS:
			List<String> listA = new ArrayList<String>();
			axis = new String[7];
			for (int i = 0, j = -1; i < 7; i++, j--) {
				Calendar c = Calendar.getInstance();
				String historyDate = format.format(dateFactory(c, j).getTime());
				listA.add(historyDate);
			}
			Collections.reverse(listA);
			for (int i = 0; i < listA.size(); i++) {
				axis[i] = listA.get(i);
			}
			break;
		case Constants.LAST_30_DAYS:
			List<String> listB = new ArrayList<String>();
			axis = new String[10];
			int j = -1;
			for (int i = 0; i < 10; i++) {
				Calendar c = Calendar.getInstance();
				String historyDate = format.format(dateFactory(c, j).getTime());
				listB.add(historyDate);
				j = j - 3;
			}
			Collections.reverse(listB);
			for (int i = 0; i < listB.size(); i++) {
				axis[i] = listB.get(i);
			}
			break;
		default:
			break;
		}
		return axis;
	}
	
	/**
	 * 获取相应条件的时间数组
	 * @param calendar
	 * @param day
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	private static Calendar dateFactory(Calendar calendar, int day) {
		calendar.add(Calendar.DATE, day);
		return calendar;
	}

	/**
	 * 更新Echart数据<数据构建>
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月23日
	 *
	 */
	public int saveOrUpdataEchartsData() {
		/* 新增注册会员数目 */
		int rechargeCount = 0;
		/* 新增理财会员数目 */
		int financialCount = 0;
		/* 充值金额 */
		double RechargeMoney = 0;
		/* 理财金额 */
		double investMoney = 0;
		/* 前台访问量 */
		int visitNum = 0;
		for (int i = 1; i < 4; i++) {
			/* 时间点数组 */
			String[] date = getAxisDate(i, "MM月dd日");
			if (i == Constants.YESTERDAY) {
				for (int j = 0, timeType = 2; j < date.length; j++, timeType += 2) {
					rechargeCount = userService.findUserCount(null, date[j], i);
					financialCount = userInfoService.findFinancialUserCount(null, date[j], i);
					RechargeMoney = rechargeUserService.findTotalRechargeByDate(null, date[j], i);
					investMoney = investService.findTotalInvestByDate(null, date[j], i);
					visitNum = statisticIndexEchartDataDao.findTotalVisitByDate(null, date[j], i);
					updateEchartDate(rechargeCount, financialCount, RechargeMoney, investMoney, visitNum, i, timeType);
				}
			} else {
				int timeType = 0;
				if (i == Constants.LAST_7_DAYS) {
					timeType = 71;
					String[] timeA = getAxisDate(i, "yyyy-MM-dd");
					for (int j = 0; j < timeA.length; j++) {
						rechargeCount = userService.findUserCount(timeA[0] + Constants.STARTTIME, timeA[j] + Constants.ENDTIME, i);
						financialCount = userInfoService.findFinancialUserCount(timeA[0] + Constants.STARTTIME, timeA[j] + Constants.ENDTIME, i);
						RechargeMoney = rechargeUserService.findTotalRechargeByDate(timeA[0] + Constants.STARTTIME, timeA[j] + Constants.ENDTIME, i);
						investMoney = investService.findTotalInvestByDate(timeA[0] + Constants.STARTTIME, timeA[j] + Constants.ENDTIME, i);
						visitNum = statisticIndexEchartDataDao.findTotalVisitByDate(timeA[0] + Constants.STARTTIME, timeA[j] + Constants.ENDTIME, i);

						updateEchartDate(rechargeCount, financialCount, RechargeMoney, investMoney, visitNum, i, timeType);
						timeType += 1;
					}
				} else if (i == Constants.LAST_30_DAYS) {
					timeType = 301;
					String[] timeB = getAxisDate(i, "yyyy-MM-dd");
					for (int j = 0; j < timeB.length; j++) {
						rechargeCount = userService.findUserCount( timeB[0] + Constants.STARTTIME, timeB[j] + Constants.ENDTIME, i);
						financialCount = userInfoService.findFinancialUserCount( timeB[0] + Constants.STARTTIME, timeB[j] + Constants.ENDTIME, i);
						RechargeMoney = rechargeUserService.findTotalRechargeByDate( timeB[0] + Constants.STARTTIME, timeB[j] + Constants.ENDTIME, i);
						investMoney = investService.findTotalInvestByDate( timeB[0] + Constants.STARTTIME, timeB[j] + Constants.ENDTIME, i);
						visitNum = statisticIndexEchartDataDao.findTotalVisitByDate( timeB[0] + Constants.STARTTIME, timeB[j] + Constants.ENDTIME, i);
						
						updateEchartDate(rechargeCount,financialCount, RechargeMoney, investMoney, visitNum, i, timeType);
						timeType += 1;
					}
				}
			}
		}
		return 0;
	}


	/**
	 * 更新Echart数据<执行>
	 * @param echartData
	 * @param type
	 * @param timeType
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月23日
	 *
	 */
	public boolean updateEchartDate(int rechargeCount, int financialCount,
			double RechargeMoney, double investMoney, int visitNum, int type, int timeType) {
		/* 获取数据库原数据 */
		t_statistic_index_echart_data echartIndexData = statisticIndexEchartDataDao.findByColumn("time_type = ?", timeType);
		if (echartIndexData == null) {
			echartIndexData = new t_statistic_index_echart_data();
		}
		echartIndexData.time = new Date();
		echartIndexData.new_financial_members_count = financialCount;
		echartIndexData.new_register_members_count = rechargeCount;
		echartIndexData.invest_money = investMoney;
		echartIndexData.recharge_money = RechargeMoney;
		echartIndexData.time_type = timeType;
		echartIndexData.type = type;
		echartIndexData.datas = visitNum;
		return statisticIndexEchartDataDao.save(echartIndexData);
	}
	
	/**
	 * 修改数据库中列的值
	 * @param type
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年7月27日
	 *
	 */
	public int updateDatas(int time_type) {
		
		return statisticIndexEchartDataDao.updateDatas(time_type);
	}
	
}
