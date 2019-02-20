package services.finance;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.util.HSSFColor;

import com.mchange.v2.log.LogUtils;

import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.FinanceExcelUtils;
import common.utils.LoggerUtil;
import common.utils.MapUtils;
import common.utils.ResultInfo;
import daos.common.SettingDao;
import daos.finance.InternetAgencyDetailStatisticDao;
import models.common.entity.t_setting_platform;
import models.finance.entity.t_internet_agency_detail_statistic;
import models.main.bean.DiscountRate;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import play.Logger;
import play.db.jpa.JPA;
import services.base.BaseService;

/**
 * 网贷机构验收进度明细统计表Service
 * @createDate 2018.10.22
 * */
public class InternetAgencyDetailStatisticService extends BaseService<t_internet_agency_detail_statistic>{

	protected static final SettingDao settingDao = Factory.getDao(SettingDao.class);
	
	protected InternetAgencyDetailStatisticDao internetAgencyDetailStatisticDao = Factory.getDao(InternetAgencyDetailStatisticDao.class);
	
	protected InternetAgencyDetailStatisticService () {
		super.basedao = this.internetAgencyDetailStatisticDao;
	}
	
	/**
	 * 添加数据
	 * @author guoShiJie
	 * @createDate 2018.10.22
	 * */
	public Boolean addInternetAgencyDetailStatistic (Date date , String agencyName, String place,String depositTubeBank, Integer depositTubeStatus ) {
		
		return internetAgencyDetailStatisticDao.addInternetAgencyDetailStatistic(date , agencyName, place, depositTubeBank, depositTubeStatus);
	}
	
	/**
	 * 查询数据 自然人指定月借款余额和借款人数  或者  法人及其他组织指定月借款余额和借款人数
	 * @describe enterpriseName 为true 时，查询法人及其他组织指定月借款余额;enterpriseName 为false 时，查询自然人指定月借款余额
	 * @param date 查询日期
	 * @author guoShiJie
	 * @createDate 2018.10.22
	 * */
	public Map<String,Object> queryLoanBalanceDatas (Date date , Boolean enterpriseName) {
		
		return internetAgencyDetailStatisticDao.queryLoanBalanceDatas(date, enterpriseName);
	}
	
	/**
	 * 查询数据 自然人指定月借款人数  或者  法人及其他组织指定月借款人数
	 * @param enterpriseName true 法人及其他组织; false自然人
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public Map<String,Object> queryLoanPeopleCount (Date date ,Boolean enterpriseName) {
		
		return internetAgencyDetailStatisticDao.queryLoanPeopleCount(date, enterpriseName);
	}
	
	/**
	 * 查询数据 出借人数 自然人 和法人及其他组织
	 * 
	 * @param date 指定月份
	 * @param enterpriseName false 查询出借  自然人人数 ;true 查询法人和其他组织的人数
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public Map<String,Object> queryInvestCountDatas (Date date , Boolean enterpriseName ) {
		
		return internetAgencyDetailStatisticDao.queryInvestCountDatas(date, enterpriseName);
	}
	
	/**
	 * 查询数据 标的信息查询
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public List<Map<String ,Object>> queryBidInfos(Date date ) {
		return internetAgencyDetailStatisticDao.queryBidInfos(date);
	}
	
	/**
	 * 逾期金额查询
	 * @createDate 2018.10.30
	 * */
	public Map<String,Object> queryOverAmounts (Date date) {
		return internetAgencyDetailStatisticDao.queryOverAmounts(date);
	}
	
	/**
	 * 修改数据 指定日期
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public ResultInfo updateInternetAgencyDetailStatistic (Date date) {
		String dateString = DateUtil.dateToString( date , "yyyy-MM");
		ResultInfo result = new ResultInfo();
		t_internet_agency_detail_statistic statistic = internetAgencyDetailStatisticDao.findByColumn(" DATE_FORMAT( time , '%Y-%m' ) = ? ", dateString);
		
		if (statistic == null) {
			result.code = -1;
			result.msg = "没有数据，无法修改";
			return result;
			
		}
		statistic = internetAgencyDetailStatisticDao.findByColumn(" DATE_FORMAT( time , '%Y-%m' ) = ? ", dateString);
		
		/**自然人借款余额*/
		BigDecimal peopleLoanBalance = new BigDecimal("0.00");
		/**自然人借款人数*/
		Integer peopleLoanCount = 0;
		
		Map<String,Object> peopleLoan = this.queryLoanBalanceDatas(date, false);
		Boolean peopleLoanFlag = MapUtils.isNotNull(peopleLoan);
		if (peopleLoanFlag) {
			/**自然人借款余额*/
			peopleLoanBalance = peopleLoan.get("loanBalance") == null ? new BigDecimal("0.0") : new BigDecimal(peopleLoan.get("loanBalance").toString());
			statistic.personal_loan_balance = peopleLoanBalance;
			
			/**自然人借款人数*/
			peopleLoanCount = peopleLoan.get("number") == null ? 0 : Integer.parseInt(peopleLoan.get("number")+"");
			statistic.personal_loan_count = peopleLoanCount;
		}
		
		/**法人及其他组织借款余额*/
		BigDecimal organizationLoanBalance = new BigDecimal("0.00");
		/**法人及其他组织借款人数*/
		Integer organizationLoanCount = 0;
		Map<String,Object> organizationLoan = this.queryLoanBalanceDatas(date, true);
		Boolean organizationLoanFlag = MapUtils.isNotNull(organizationLoan);
		if (organizationLoanFlag) {
			/**法人及其他组织借款余额*/
			organizationLoanBalance = organizationLoan.get("loanBalance") == null ? new BigDecimal("0.0") : new BigDecimal(organizationLoan.get("loanBalance").toString());
			statistic.organization_loan_balance = organizationLoanBalance;
			
			/**法人及其他组织借款人数*/
			organizationLoanCount = organizationLoan.get("number") == null ? 0 : Integer.parseInt(organizationLoan.get("number")+"");
			statistic.organization_loan_count = organizationLoanCount;
		}
		
		/**借款余额合计*/
		statistic.loan_balance_total = peopleLoanBalance.add(organizationLoanBalance);
		
		/**借款人数合计*/
		statistic.loan_count_total = peopleLoanCount+organizationLoanCount;
		
		
		/**自然人出借人数*/
		Integer peopleInvestCount = 0;
		/**法人及其他组织出借人数*/
		Integer organizationInvestCount = 0;
		Map<String,Object> personInvest = this.queryInvestCountDatas(date, false);
		Boolean personInvestFlag = MapUtils.isNotNull(personInvest);
		if (personInvestFlag) {
			/**自然人出借人数*/
			peopleInvestCount = personInvest.get("number") == null ? 0 : Integer.parseInt(personInvest.get("number")+"");
			statistic.personal_invest_count = peopleInvestCount;
		}
		
		Map<String,Object> organizationInvest = this.queryInvestCountDatas(date, true);
		Boolean organizationInvestFlag = MapUtils.isNotNull(organizationInvest);
		if (organizationInvestFlag) {
			/**法人及其他组织出借人数*/
			organizationInvestCount = organizationInvest.get("number") == null ? 0 : Integer.parseInt(organizationInvest.get("number")+"");
			statistic.organization_invest_count = organizationInvestCount;
		}
		/**出借人数合计*/
		statistic.invest_count = peopleInvestCount+organizationInvestCount;
		
		/**自然人平均借款额度*/
		if (peopleLoanCount != 0 ) {
			statistic.average_loan_quota_personal = peopleLoanBalance.divide(new BigDecimal(peopleLoanCount),2,BigDecimal.ROUND_HALF_UP);
		}else{
			statistic.average_loan_quota_personal = new BigDecimal("0.0");
		}
		/**法人及其他组织平均借款额度*/
		if (organizationLoanCount != 0) {
			statistic.average_loan_quota_organization = organizationLoanBalance.divide(new BigDecimal(organizationLoanCount),2,BigDecimal.ROUND_HALF_UP);
		}else{
			statistic.average_loan_quota_organization = new BigDecimal("0.0");
		}
		/**平均借款利率*/
		List<Map<String,Object>> bidInfos = this.queryBidInfos(date);
		Double total = 0.0;
		if ( bidInfos == null && bidInfos.size() > 0 ) {
			for(int i = 0; i < bidInfos.size() ; i++ ) {
				
				List<DiscountRate> lists = new ArrayList<DiscountRate>();
				t_setting_platform setting = settingDao.findByColumn("_key=?", "annual_discount_rate");
				JSONArray jsonArray = JSONArray.fromObject(setting._value);
				lists = (List) JSONArray.toList(jsonArray, new DiscountRate(), new JsonConfig());
				Integer unit = (Integer)bidInfos.get(i).get("unit");
				if (unit == 1) {
					double rate = lists.get(0).discountRate;
					total += (double)bidInfos.get(i).get("amount") * rate;
				}
				
				if (unit == 2) {
					for (DiscountRate rt : lists) {
						if (rt.timeLimit == (Integer)bidInfos.get(i).get("period")) {
							total += (double)bidInfos.get(i).get("amount") * rt.discountRate;
						}
					}
				}
			}
		}
		
		if (statistic.loan_balance_total.compareTo(new BigDecimal("0.0")) != 0) {
			
			statistic.average_loan_apr = new BigDecimal(total).divide(new BigDecimal("10000.0"),2,BigDecimal.ROUND_HALF_UP).divide(statistic.loan_balance_total,2, BigDecimal.ROUND_HALF_UP);
		} else {
			
			statistic.average_loan_apr = new BigDecimal("0.00");
		}
		
		/**逾期金额*/
		Map<String,Object> over = this.queryOverAmounts(date);
		Boolean overFlag = MapUtils.isNotNull(over);
		if (overFlag) {
			statistic.overdue_amount = over.get("overAmount") == null ? new BigDecimal("0.0") : new BigDecimal(over.get("overAmount").toString()).setScale(4,BigDecimal.ROUND_HALF_UP);
		}else{
			statistic.overdue_amount = new BigDecimal("0.0");
		}
		
		Boolean statisticFlag = internetAgencyDetailStatisticDao.save(statistic);
		if (statisticFlag) {
			result.code = 1;
			result.msg = "修改数据成功!!!";
			
			return result;
		}
		
		result.code = -2;
		result.msg = "修改数据失败!!!";
		
		return result;
	}
	
	/**
	 * 生成Excel文件
	 * 
	 * @param date 日期
	 * @param String [] 对string数组的值的解释
	 * @exception
	 * 0---合并单元格 ;格式: "起始行,结束行,起始列,结束列"
	 * 1---单元格内容值;
	 * 2---单元格位置;格式: "行,列"
	 * 3---对齐; l-左对齐;m-居中对齐;r-右对齐;
	 * 4---字号;
	 * 5---行高和列宽; 格式: "行高,列宽"
	 * 6---有无边框; 有边框-border;无边框-noBorder;
	 * 7---文字颜色; short类型值
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.25
	 * */
	public File createInternetAgencyDetailStatistic (Date date) {
		
		t_internet_agency_detail_statistic statistic = internetAgencyDetailStatisticDao.findByColumn(" DATE_FORMAT( time , '%Y-%m' ) = ?", DateUtil.dateToString(date, "yyyy-MM"));
		if (statistic == null) {
			this.addInternetAgencyDetailStatistic(date , "石家庄菲尔德投资咨询有限公司", "新华区", "宜宾市商业银行", 1);
		}
		this.updateInternetAgencyDetailStatistic(date);
		
		statistic = internetAgencyDetailStatisticDao.findByColumn(" DATE_FORMAT( time , '%Y-%m' ) = ? ", DateUtil.dateToString(date, "yyyy-MM"));
		
		List<String []> list = new ArrayList<String []> ();
		
		String [] s1 = {"0,0,0,18","网贷机构验收进度明细统计表","0,0","m","15","600,1200","noBorder",HSSFColor.BLACK.index+""};
		String [] s2 = {"1,1,0,5","填报人:","1,0","l","10","600,1200","noBorder",HSSFColor.BLACK.index+""};
		String [] s3 = {"1,1,6,13","联系方式:","1,6","l","10","600,1200","noBorder",HSSFColor.BLACK.index+""};
		String [] s4 = {"1,1,14,18","填报时间:","1,14","l","10","600,1200","noBorder",HSSFColor.BLACK.index+""};
		String [] s5 = {"2,3,0,0","序号","2,0","m","10","600,1200","border",HSSFColor.BLACK.index+""};
		String [] s6 = {"2,3,1,1","企业名称","2,1","m","10","600,3000","border",HSSFColor.BLACK.index+""};
		String [] s7 = {"2,3,2,2","属地","2,2","m","10","600,1200","border",HSSFColor.BLACK.index+""};
		String [] s8 = {"2,2,3,5","借款余额（万元）","2,3","m","10","600,1500","border",HSSFColor.BLACK.index+""};
		String [] s9 = {"","自然人","3,3","m","10","900,1500","border",HSSFColor.BLACK.index+""};
		String [] s10 = {"","法人及其他组织","3,4","m","10","900,2000","border",HSSFColor.BLACK.index+""};
		String [] s11 = {"","合计","3,5","m","10","900,1500","border",HSSFColor.BLACK.index+""};
		String [] s12 = {"2,2,6,8","借款人数（人）","2,6","m","10","600,1500","border",HSSFColor.BLACK.index+""};
		String [] s13 = {"","自然人","3,6","m","10","900,1500","border",HSSFColor.BLACK.index+""};
		String [] s14 = {"","法人及其他组织","3,7","m","10","900,2000","border",HSSFColor.BLACK.index+""};
		String [] s15 = {"","合计","3,8","m","10","900,1500","border",HSSFColor.BLACK.index+""};
		String [] s16 = {"2,2,9,11","出借人数（人）","2,9","m","10","600,1500","border",HSSFColor.BLACK.index+""};
		String [] s17 = {"","自然人","3,9","m","10","900,1500","border",HSSFColor.BLACK.index+""};
		String [] s18 = {"","法人及其他组织","3,10","m","10","900,2000","border",HSSFColor.BLACK.index+""};
		String [] s19 = {"","合计","3,11","m","10","900,1500","border",HSSFColor.BLACK.index+""};
		String [] s20 = {"2,2,12,13","平均借款额度（万元）","2,12","m","10","600,1500","border",HSSFColor.BLACK.index+""};
		String [] s21 = {"","自然人","3,12","m","10","900,1500","border",HSSFColor.BLACK.index+""};
		String [] s22 = {"","法人及其他组织","3,13","m","10","900,2000","border",HSSFColor.BLACK.index+""};
		String [] s23 = {"2,3,14,14","平均借款利率（%）","2,14","m","10","600,2000","border",HSSFColor.BLACK.index+""};
		String [] s24 = {"2,3,15,15","逾期金额（万元）","2,15","m","10","600,2000","border",HSSFColor.BLACK.index+""};
		String [] s25 = {"2,2,16,17","资金存管情况","2,16","m","10","600,2000","border",HSSFColor.BLACK.index+""};
		String [] s26 = {"","存管银行","3,16","m","10","900,2000","border",HSSFColor.BLACK.index+""};
		String [] s27 = {"","存管状态","3,17","m","10","900,2000","border",HSSFColor.BLACK.index+""};
		String [] s28 = {"","备注","2,18","m","10","600,1000","border",HSSFColor.BLACK.index+""};
	
		list.add(s1);
		list.add(s2);
		list.add(s3);
		list.add(s4);
		list.add(s5);
		list.add(s6);
		list.add(s7);
		list.add(s8);
		list.add(s9);
		list.add(s10);
		list.add(s11);
		list.add(s12);
		list.add(s13);
		list.add(s14);
		list.add(s15);
		list.add(s16);
		list.add(s17);
		list.add(s18);
		list.add(s19);
		list.add(s20);
		list.add(s21);
		list.add(s22);
		list.add(s23);
		list.add(s24);
		list.add(s25);
		list.add(s26);
		list.add(s27);
		list.add(s28);
		
		
		if (statistic != null) {
			String [] s34 = {"","","4,0","m","10","1800,1200","border",HSSFColor.BLACK.index+""};
			String [] s35 = {"",statistic.agency_name == null ? "" : statistic.agency_name,"4,1","m","10","1800,3000","border",HSSFColor.BLACK.index+""};
			String [] s36 = {"",statistic.place == null ? "" : statistic.place,"4,2","m","10","1800,1200","border",HSSFColor.BLACK.index+""};
			String [] s37 = {"",statistic.personal_loan_balance == null ? "" : statistic.personal_loan_balance+"","4,3","m","10","1800,1500","border",HSSFColor.BLACK.index+""};
			String [] s38 = {"",statistic.organization_loan_balance == null ? "" : statistic.organization_loan_balance+"","4,4","m","10","1800,2000","border",HSSFColor.BLACK.index+""};
			String [] s39 = {"",statistic.loan_balance_total == null ? "" : statistic.loan_balance_total+"","4,5","m","10","1800,1500","border",HSSFColor.BLACK.index+""};
			String [] s40 = {"",statistic.personal_loan_count == null ? "" : statistic.personal_loan_count+"","4,6","m","10","1800,1500","border",HSSFColor.BLACK.index+""};
			String [] s41 = {"",statistic.organization_loan_count == null ? "" : statistic.organization_loan_count+"","4,7","m","10","1800,2000","border",HSSFColor.BLACK.index+""};
			String [] s42 = {"",statistic.loan_count_total == null ? "" : statistic.loan_count_total+"","4,8","m","10","1800,1500","border",HSSFColor.BLACK.index+""};
			String [] s43 = {"",statistic.personal_invest_count == null ? "" : statistic.personal_invest_count+"","4,9","m","10","1800,1500","border",HSSFColor.BLACK.index+""};
			String [] s44 = {"",statistic.organization_invest_count == null ? "" : statistic.organization_invest_count+"","4,10","m","10","1800,2000","border",HSSFColor.BLACK.index+""};
			String [] s45 = {"",statistic.invest_count == null ? "" : statistic.invest_count+"","4,11","m","10","1800,1500","border",HSSFColor.BLACK.index+""};
			String [] s46 = {"",statistic.average_loan_quota_personal == null ? "" : statistic.average_loan_quota_personal+"","4,12","m","10","1800,1500","border",HSSFColor.BLACK.index+""};
			String [] s47 = {"",statistic.average_loan_quota_organization == null ? "" : statistic.average_loan_quota_organization+"","4,13","m","10","1800,2000","border",HSSFColor.BLACK.index+""};
			String [] s48 = {"",statistic.average_loan_apr == null ? "" : statistic.average_loan_apr+"%","4,14","m","10","1800,2000","border",HSSFColor.BLACK.index+""};
			String [] s49 = {"",statistic.overdue_amount == null ? "" : statistic.overdue_amount+"","4,15","m","10","1800,2000","border",HSSFColor.BLACK.index+""};
			String [] s50 = {"",statistic.deposit_tube_bank == null ? "" : statistic.deposit_tube_bank,"4,16","m","10","1800,2000","border",HSSFColor.BLACK.index+""};
			String [] s51 = {"",(statistic.deposit_tube_status == 1 ? "已上线" : ""),"4,17","m","10","1800,2000","border",HSSFColor.BLACK.index+""};
			String [] s52 = {"","","4,18","m","10","1800,1000","border",HSSFColor.BLACK.index+""};
			
			list.add(s34);
			list.add(s35);
			list.add(s36);
			list.add(s37);
			list.add(s38);
			list.add(s39);
			list.add(s40);
			list.add(s41);
			list.add(s42);
			list.add(s43);
			list.add(s44);
			list.add(s45);
			list.add(s46);
			list.add(s47);
			list.add(s48);
			list.add(s49);
			list.add(s50);
			list.add(s51);
			list.add(s52);
			
		}
		String [] s53 = {"","","1,1","m","10","600,3000","noBorder",HSSFColor.BLACK.index+""};
		String [] s54 = {"","","1,2","m","10","600,1200","noBorder",HSSFColor.BLACK.index+""};
		String [] s55 = {"","","1,3","m","10","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s56 = {"","","1,4","m","10","600,2000","noBorder",HSSFColor.BLACK.index+""};
		String [] s57 = {"","","1,5","m","10","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s58 = {"","","1,7","m","10","600,2000","noBorder",HSSFColor.BLACK.index+""};
		String [] s59 = {"","","1,8","m","10","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s60 = {"","","1,9","m","10","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s61 = {"","","1,10","m","10","600,2000","noBorder",HSSFColor.BLACK.index+""};
		String [] s62 = {"","","1,11","m","10","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s63 = {"","","1,12","m","10","600,1500","noBorder",HSSFColor.BLACK.index+""};
		String [] s64 = {"","","1,13","m","10","600,2000","noBorder",HSSFColor.BLACK.index+""};
		String [] s65 = {"","","1,15","m","10","600,2000","noBorder",HSSFColor.BLACK.index+""};
		String [] s66 = {"","","1,16","m","10","600,2000","noBorder",HSSFColor.BLACK.index+""};
		String [] s67 = {"","","1,17","m","10","600,2000","noBorder",HSSFColor.BLACK.index+""};
		String [] s68 = {"","","1,18","m","10","600,2000","noBorder",HSSFColor.BLACK.index+""};
		
		list.add(s53);
		list.add(s54);
		list.add(s55);
		list.add(s56);
		list.add(s57);
		list.add(s58);
		list.add(s59);
		list.add(s60);
		list.add(s61);
		list.add(s62);
		list.add(s63);
		list.add(s64);
		list.add(s65);
		list.add(s66);
		list.add(s67);
		list.add(s68);
		
		return FinanceExcelUtils.createExcel("网贷机构验收进度明细统计表", 24, 19, list);
		
	}
	
}