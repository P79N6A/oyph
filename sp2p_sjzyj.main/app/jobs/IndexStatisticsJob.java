package jobs;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import com.shove.Convert;

import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import play.jobs.OnApplicationStart;
import services.common.SettingService;
import services.common.UserFundService;
import services.common.UserService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.InvestService;
import services.main.StatisticIndexEchartDataService;
import common.constants.SettingKey;
import common.utils.DateUtil;
import common.utils.Factory;

/**
 * 前台、后台首页统计数据展示,执行时间：02：00
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2016年1月25日
 */
@OnApplicationStart
public class IndexStatisticsJob extends Job {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	/** 注册用户数 */
	public static String userCount;
	
	/** 放款项目数量 */
	public static String bidCount;
	
	/** 放款项目总额(累计成交) */
	public static String totalBorrowAmount;
	
	/** 待还理财账单的账单总额 */
	public static String totalBillAmount;
	
	/** 平台浮动金 */
	public static String platformFloatAmount;
	
	/** 数据更新时间 */
	public static Date date;
	
	/** 给用户来带的收益 */
	public static String income;
	
	/** 到期还款率 */
	public static double expireRepaymentRate;
	
	/** 运营时间 天 */
	public static int serviceTime;
	
	/** 运营时间 小时 */
	public static int serviceHour;
	
	/** 累计成交人次  次数 */
	public static String totalVolume;
	
	/** 累计借款笔数  笔数 */
	public static String totalCount;
	
	@Override
	public void doJob() throws Exception {
		statistics();
	}
	
	public static void statistics() {
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------定时统计前后台首页数据，开始--------------");
		}
		
		StatisticIndexEchartDataService echartDataService = Factory.getService(StatisticIndexEchartDataService.class);
		UserService userService = Factory.getService(UserService.class);
		
		UserFundService userFundService = Factory.getService(UserFundService.class);
		
		BidService bidService = Factory.getService(BidService.class);
		
		BillInvestService billInvestService = Factory.getService(BillInvestService.class);
		 
		BillService billService = Factory.getService(BillService.class);
		
		InvestService investService = Factory.getService(InvestService.class);
		
		DecimalFormat df = new DecimalFormat("#,###.00");
		
		DecimalFormat dfs = new DecimalFormat("#,###");
		
		/* 注册用户数 */
		long userCounts = userService.queryTotalRegisterUserCount();
		userCount = dfs.format(userCounts)+"";
		
		/* 放款项目数量 */
		long bidCounts = bidService.queryReleasedBidsNum();
		bidCount = dfs.format(bidCounts);
		
		/* 放款项目总额 */
		double totalBorrowAmounts = bidService.queryTotalBorrowAmount();
		totalBorrowAmount = df.format(totalBorrowAmounts+162994005)+"";
		
		/* 待还总额 */
		double totalBillAmounts = billService.queryTotalNoRepaymentAmount();
		totalBillAmount = df.format(totalBillAmounts);
		
		/* 平台浮动金 */
		double platformFloatAmounts = userFundService.queryPlatformFloatAmount();
		platformFloatAmount = df.format(platformFloatAmounts)+"";
		
		/* 给用户来带的收益 */
		double incomes = billInvestService.findAllInterest();
		income = df.format(incomes + 8243967.5)+"";
		
		/* 到期还款率 */
		expireRepaymentRate = billService.queryExpireRepaymentRate()*100;
		
		/* 更新时间 */
		date = new Date();  
		
		/* 更新Echart报表数据 */
		echartDataService.saveOrUpdataEchartsData();
		
		Date beginTime = DateUtil.strDateToEndDate("2016-8-3");
		Date endTime = new Date();

		serviceTime = DateUtil.getDaysDiffCeil(beginTime,endTime);
		Calendar c = Calendar.getInstance();
		serviceHour = c.get(Calendar.HOUR_OF_DAY);
		
		/* 累计成交人次 */
		long totalVolumes = investService.findCountOfInvests();
		totalVolume = dfs.format(totalVolumes+8268)+"";
		
		/* 累计借款笔数 */
		long totalCounts = bidService.findCountOfBids();
		totalCount = dfs.format(totalCounts+876)+"";
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------定时统计前后台首页数据，结束--------------");
		}
		
	}

}
