package jobs;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.DateUtil;
import common.utils.Factory;
import models.common.entity.t_disclosure;
import models.common.entity.t_disclosure_invest;
import models.common.entity.t_disclosure_month;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import services.common.DisclosureService;
import services.common.SettingService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.InvestService;

public class DisclosureJob {
	
	BidService bidService = Factory.getService(BidService.class);
	
	BillInvestService billInvestService = Factory.getService(BillInvestService.class);
	
	InvestService investService = Factory.getService(InvestService.class);
	
	DisclosureService disclosureService = Factory.getService(DisclosureService.class);
	
	BillService billService = Factory.getService(BillService.class);
	
	/** 注入系统设置service */
	SettingService settingService = Factory.getService(SettingService.class);
	
	
	DecimalFormat df = new DecimalFormat("#,###.00");
	
	DecimalFormat dfs = new DecimalFormat("#,###");
	
	DecimalFormat dfes = new DecimalFormat( "###############0.00 ");//   16位整数位，两小数位 

	public void doJob(){
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------定时统计信息披露,开始---------------------");
		}
		
		
		//创建信息披露表
		t_disclosure disclosure = new t_disclosure();
		
		/* 放款项目总额 */
		double totalBorrowAmounts = bidService.queryTotalBorrowAmount();
		disclosure.total_amount = df.format(totalBorrowAmounts+162994005)+"";
		
		/* 给用户来带的收益 */
		double incomes = billInvestService.findAllInterest();
		disclosure.total_earnings = df.format(incomes + 8243967.5)+"";
		
		/* 累计成交人次 */
		long totalVolumes = investService.findCountOfInvests();
		disclosure.total_volume = dfs.format(totalVolumes+8268)+"";
		
		/* 累计借款笔数 */
		long totalCounts = bidService.findCountOfBids();
		disclosure.total_count = dfs.format(totalCounts+876)+"";
		
		/* 累计借款总余额 */
		double totalBalances = bidService.findTotalBalance();
		disclosure.total_balance = df.format(totalBalances)+"";
		
		/* 借贷笔数 */
		int totalBalanceCount = bidService.findNowCountOfBids();
		disclosure.total_balance_count = totalBalanceCount + "";
		
		/* 累计待还借款人数量 */
		long borrowerCounts = billService.findBorrowerCount();
		disclosure.borrower_count = dfs.format(borrowerCounts)+"";
		
		/* 累计待还出借人数量 */
		long lenderCounts = billInvestService.findLenderCount();
		disclosure.lender_count = dfs.format(lenderCounts)+"";
		
		/* 累计借款人总数量 */
		long borrowerTotalCounts = billService.findTotalBorrowerCount();
		disclosure.borrower_total_count = dfs.format(borrowerTotalCounts+374)+"";
		
		/* 累计出借人总数量 */
		long lenderTotalCounts = billInvestService.findTotalLenderCount();
		disclosure.lender_total_count = dfs.format(lenderTotalCounts+876)+"";
		
		/* 代偿金额*/
		double compensateMoneys = billService.findCompensateMoney();
		disclosure.compensate_money = df.format(compensateMoneys)+"";
		
		/* 代偿笔数 */
		long compensateCounts = billService.findCompensateCount();
		disclosure.compensate_count = dfs.format(compensateCounts)+"";
		
		/* 前十大借款人 */
		List<Map<String, Object>> tenList = billService.queryMouthInvestLists(10);
		double tenlist = 0.00;
		for (int i = 0; i < tenList.size(); i++) {
			tenlist = tenlist + Double.parseDouble(tenList.get(i).get("amount").toString());
		}
		disclosure.ten_proportion = tenlist/totalBalances*100;
		
		/* 最大单一借款人 */
		List<Map<String, Object>> oneList = billService.queryMouthInvestLists(1);
		double onelist = 0.00;
		for (int i = 0; i < oneList.size(); i++) {
			onelist = onelist + Double.parseDouble(oneList.get(i).get("amount").toString());
		}
		disclosure.one_proportion = onelist/totalBalances*100;
		
		Date dates = new Date();
		Date ti = DateUtil.minusDay(dates, 1);
		String yearMonthTime = DateUtil.getYear(ti)+"年"+DateUtil.getMonth(ti)+"月";
		
		disclosure.year_time = yearMonthTime;
		disclosure.time = dates;
		
		disclosure.save();
		
		t_disclosure dis = disclosureService.findByTime();
		
		Date endTimes = new Date();
		Date beginTimes = DateUtil.minusMonth(endTimes, 1);
		
		String endTime = DateUtil.dateToString(endTimes, "yyyy-MM-dd HH:mm:ss");
		String beginTime = DateUtil.dateToString(beginTimes, "yyyy-MM-dd HH:mm:ss");
		
		//创建信息月数据统计
		t_disclosure_month disMon = new t_disclosure_month();
		
		disMon.disclosure_id = dis.id;
		
		/* 月交易金额 */
		double monAmounts = bidService.findTotalBorrowAmountByMonth(beginTime, endTime);
		
		String temp = dfes.format(monAmounts);
		double zhi = Double.parseDouble(temp);
		disMon.mon_amount = temp;
		
		/* 月交易笔数 */
		int monCounts = investService.findMonCountByMonth(beginTime, endTime);
		disMon.mon_count = monCounts+"";
		
		/* 月出借人数 */
		int monLenders = bidService.findLenderCountByMonth(beginTime, endTime);
		disMon.mon_lender = monLenders+"";
		
		/* 月借款人数 */
		int monBorrowers = investService.findBorrowerCountByMonth(beginTime, endTime);
		disMon.mon_borrower = monBorrowers+"";
		
		/* 月十大投资人 */
		List<Map<String, Object>> mouthInvest = investService.queryMouthInvestListByMonth(beginTime, endTime);
		double monthInvests = 0;
		for (int i = 0; i < mouthInvest.size(); i++) {
			monthInvests = monthInvests + Double.parseDouble(mouthInvest.get(i).get("amount").toString());
		}
		
		disMon.top_ten_amount = monthInvests+"";
		
		String yearTime = DateUtil.getYear(ti)+"."+DateUtil.getMonth(ti);
		String monthTime = DateUtil.getMonth(ti)+"月";
		
		disMon.year_time = yearTime;
		disMon.month_time = monthTime;
		disMon.time = dates;
		disMon.save();
		
		//创建信息披露投资
		for (int i = 0; i < mouthInvest.size(); i++) {
			t_disclosure_invest disInv = new t_disclosure_invest();
			disInv.disclosure_id = dis.id;
			disInv.name = mouthInvest.get(i).get("name").toString();
			disInv.invest_amount = mouthInvest.get(i).get("amount").toString();
			
			double proportions = 0.00;
			proportions = (Double.parseDouble(mouthInvest.get(i).get("amount").toString()))/(zhi)*100;
			disInv.proportion = proportions;
			
			disInv.time = new Date();
			
			disInv.save();
			
		}
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------定时统计信息披露,结束---------------------");
		}
		
	}
	
}
