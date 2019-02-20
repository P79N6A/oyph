package services.finance;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import common.enums.FiveLevelTextType;
import common.enums.SuperviseReportType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.JRBUtils;
import common.utils.JRBXMLUtils;
import common.utils.TimeUtil;
import daos.finance.LoanPactDao;
import models.common.entity.t_pact;
import models.core.entity.t_bill;
import models.finance.entity.t_loan_pact;
import play.Logger;
import play.Play;
import services.base.BaseService;
import services.common.PactService;
import services.core.BillService;

/**
 * 金融办-P06贷款合同表
 * 
 * @createDate 2018.10.9
 * */
public class LoanPactService extends BaseService<t_loan_pact>{

	protected LoanPactDao loanpactDao = Factory.getDao(LoanPactDao.class);
	
	protected BillService billService = Factory.getService(BillService.class);
	
	protected PactService pactService = Factory.getService(PactService.class);
	
	protected LoanPactService(){
		super.basedao = loanpactDao;
	}
	
	/**
	 * 查询当天日期的合同
	 * */
	public int updateLoanPacts ( ) {
		return loanpactDao.updateLoanPacts();
		
	}
	
	/**
	 * 计算表中的数据
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.7
	 * */
	public void jisuan () {
		Date date = new Date();
		List<t_loan_pact> loanList = loanpactDao.findLoanPactUp();
//		List<t_loan_pact> loanList = loanpactDao.findAll();
		
		if (loanList != null && loanList.size() > 0) {
			for (t_loan_pact loanPact : loanList) {
				/** 贷款期限 */
				if (loanPact.period_unit == 1) {
					loanPact.period = 1;
				}
				
				/** 主要抵押物 */
				
				String goods = null;
				if (loanPact.limits.indexOf("房") != -1){
					goods = "房";
				}else if (loanPact.limits.indexOf("车") != -1) {
					goods = "车";
				}else {
					goods = loanPact.limits;
				}
				loanPact.limits = goods;
				
				/**合同约定到期日期*/
				t_pact pact = pactService.findByColumn(" id = ? AND type = 0 ", loanPact.p_id );
				
				if (pact != null) {
					t_bill bill = billService.findByColumn(" period = ? and bid_id = ? order by period desc limit 1  ", loanPact.period , pact.pid);
					
					if (bill != null) {
						loanPact.expiry_date = bill.repayment_time;
					}
				}
				
				/**平台服务费,佣金费*/
				BigDecimal playformAmount = null;
				playformAmount = loanPact.platform_commission.multiply(new BigDecimal(loanPact.period));
				loanPact.platform_commission = playformAmount;
				
				/** 利息总额 */
				BigDecimal  loan_balance = new BigDecimal(loanPact.amount+"");
				BigDecimal  annual_rate = new BigDecimal(loanPact.annual_rate+"");
				BigDecimal  period = new BigDecimal(loanPact.period);
				BigDecimal  nian = new BigDecimal("12.00");
				BigDecimal billperiod = new BigDecimal(loanPact.bill_period);
				
				
				/** 贷款期限 */
				if (loanPact.expiry_date != null) {
					loanPact.loan_period = DateUtil.getDaysBetween(loanPact.interest_date, loanPact.expiry_date);
				}
				
				/**判断是否逾期*/
				if (loanPact.real_repayment_time == null) {
					if (DateUtil.isDateBefore(loanPact.repayment_time, date)) {
						/**逾期*/
						loanPact.is_overdue = 1;
						/**利息逾期金额*/
						loanPact.overdue_interest_amount = loanPact.real_interest_amount;
						/**本金逾期金额*/
						loanPact.overdue_principal_amount = loanPact.real_principal_amount;
						/**五级分类*/
						loanPact.type = FiveLevelTextType.LOSS.value;
						/**逾期天数*/
						loanPact.overdue = DateUtil.getDaysBetween(loanPact.repayment_time, date);
						
						/**应付利息*/
						billperiod = billperiod.subtract(new BigDecimal("1.00"));
						
						if (loan_balance != null && annual_rate != null && period != null && nian != null && billperiod != null) {
							BigDecimal aprAmounts = loan_balance.multiply(annual_rate).multiply(period).divide(nian,2,RoundingMode.HALF_UP);
							loanPact.gross_interest = aprAmounts;
							/** 利息余额 */
							BigDecimal realityAprAmount = loan_balance.multiply(annual_rate).multiply(billperiod).divide(nian,2,BigDecimal.ROUND_HALF_DOWN);
							
							loanPact.payable_interest = aprAmounts.subtract(realityAprAmount);
						}
						
						/**贷款余额*/
						loanPact.loan_balance = loanPact.amount;
						
					} else {
						/**未逾期*/
						loanPact.is_overdue = 0;
						loanPact.overdue_interest_amount = new BigDecimal("0.00");
						loanPact.overdue_principal_amount = new BigDecimal("0.00");
						loanPact.type = FiveLevelTextType.NORMAL.value;
						loanPact.overdue = 0;
						
						/**应付利息*/
						billperiod = billperiod.subtract(new BigDecimal("1.00"));
						
						if (loan_balance != null && annual_rate != null && period != null && nian != null && billperiod != null) {
							BigDecimal aprAmounts = loan_balance.multiply(annual_rate).multiply(period).divide(nian,2,RoundingMode.HALF_UP);
							loanPact.gross_interest = aprAmounts;
							/** 利息余额 */
							BigDecimal realityAprAmount = loan_balance.multiply(annual_rate).multiply(billperiod).divide(nian,2,BigDecimal.ROUND_HALF_DOWN);
							
							loanPact.payable_interest = aprAmounts.subtract(realityAprAmount);
						}
						
						/**贷款余额*/
						loanPact.loan_balance = loanPact.amount;
					}
					
					if (loanPact.real_interest_amount.compareTo(new BigDecimal("0.00"))==0 && loanPact.real_principal_amount.compareTo(new BigDecimal("0.00"))==0) {
						/**未逾期*/
						loanPact.is_overdue = 0;
						loanPact.overdue_interest_amount = new BigDecimal("0.00");
						loanPact.overdue_principal_amount = new BigDecimal("0.00");
						loanPact.type = FiveLevelTextType.NORMAL.value;
						loanPact.overdue = 0;
						loanPact.loan_balance = new BigDecimal("0.00");
						loanPact.payable_interest = new BigDecimal("0.00");
					}
					
				}else {
					if (DateUtil.isDateBefore(loanPact.repayment_time, loanPact.real_repayment_time)) {
						/**逾期*/
						loanPact.is_overdue = 1;
						/**利息逾期金额*/
						loanPact.overdue_interest_amount = loanPact.real_interest_amount;
						/**本金逾期金额*/
						loanPact.overdue_principal_amount = loanPact.real_principal_amount;
						/**五级分类*/
						loanPact.type = FiveLevelTextType.LOSS.value;
						/**逾期天数*/
						loanPact.overdue = DateUtil.getDaysBetween(loanPact.repayment_time, loanPact.real_repayment_time);
						
						/**贷款余额*/
						loanPact.loan_balance = loanPact.amount;
						
						/**应付利息*/
						billperiod = billperiod.subtract(new BigDecimal("1.00"));
						if (loan_balance != null && annual_rate != null && period != null && nian != null && billperiod != null) {
							BigDecimal aprAmounts = loan_balance.multiply(annual_rate).multiply(period).divide(nian,2,RoundingMode.HALF_UP);
							loanPact.gross_interest = aprAmounts;
							/** 利息余额 */
							BigDecimal realityAprAmount = loan_balance.multiply(annual_rate).multiply(billperiod).divide(nian,2,BigDecimal.ROUND_HALF_DOWN);
							
							loanPact.payable_interest = aprAmounts.subtract(realityAprAmount);
						}
						
						
					}else {
						/**未逾期*/
						loanPact.is_overdue = 0;
						loanPact.overdue_interest_amount = new BigDecimal("0.00");
						loanPact.overdue_principal_amount = new BigDecimal("0.00");
						loanPact.type = FiveLevelTextType.NORMAL.value;
						loanPact.overdue = 0;
						
						/**应付利息*/
						if (loan_balance != null && annual_rate != null && period != null && nian != null && billperiod != null) {
							BigDecimal aprAmounts = loan_balance.multiply(annual_rate).multiply(period).divide(nian,2,RoundingMode.HALF_UP);
							loanPact.gross_interest = aprAmounts;
							/** 利息余额 */
							BigDecimal realityAprAmount = loan_balance.multiply(annual_rate).multiply(billperiod).divide(nian,2,BigDecimal.ROUND_HALF_DOWN);
							
							loanPact.payable_interest = aprAmounts.subtract(realityAprAmount);
						}
						
						/**贷款余额*/
						if (loanPact.real_principal_amount.compareTo(new BigDecimal("0.00")) == 0 ) {
							loanPact.loan_balance = loanPact.amount;
						}else if (loanPact.real_principal_amount.compareTo(loanPact.amount) == 0 ) {
							loanPact.payable_interest = new BigDecimal("0.00");
							loanPact.loan_balance = new BigDecimal("0.00");
						}
					}
				}
				
				loanPact.save();
			}
			Logger.info("P06贷款合同表更改数据完成!!!");
		}
	}
	
	/**
	 *获取XML数据
	 *
	 *@author guoShiJie
	 *@createDate 2018.10.9
	 * */
	public List<JSONObject>  getLoanReportList ( ) {
		List<JSONObject> reportsList = new ArrayList<JSONObject>();
		/**截止到上个月的贷款合同*/
		List<t_loan_pact> loanList = loanpactDao.findAll();
		if (loanList != null && loanList.size() > 0) {
			for (t_loan_pact pact : loanList) {
				JSONObject reportMap = new JSONObject(true);
				/**合同编号*/
				reportMap.put("P060001", pact.p_id);
				/**借款人客户号*/
				reportMap.put("P060002", pact.user_id);
				/**客户名称*/
				reportMap.put("P060003", pact.name);
				/**放款金额*/
				reportMap.put("P060004", pact.amount);
				/**贷款余额*/
				reportMap.put("P060005", pact.loan_balance);
				/**利息总额*/
				reportMap.put("P060006", pact.gross_interest);
				/**应付利息*/
				reportMap.put("P060007", pact.payable_interest);
				/**平台佣金费*/
				reportMap.put("P060008", pact.platform_commission);
				/**合同募集日*/
				reportMap.put("P060009", pact.contract_date == null ? "" : DateUtil.dateToString(pact.contract_date, "yyyy-MM-dd"));
				/**合同起息日*/
				reportMap.put("P060010", pact.interest_date == null ? "" : DateUtil.dateToString(pact.interest_date, "yyyy-MM-dd"));
				/**合同约定到期日期*/
				reportMap.put("P060011", pact.expiry_date == null ? "" : DateUtil.dateToString(pact.expiry_date, "yyyy-MM-dd"));
				/**实际结清日期*/
				reportMap.put("P060012", pact.closing_date == null ? "" : DateUtil.dateToString(pact.closing_date, "yyyy-MM-dd"));
				/**利率*/
				reportMap.put("P060013", pact.annual_rate);
				/**合同担保方式*/
				reportMap.put("P060014", pact.ensure_type);
				/**主要抵押质物*/
				reportMap.put("P060015", pact.limits);
				/**贷款期限*/
				reportMap.put("P060016", pact.loan_period);
				/**贷款用途*/
				reportMap.put("P060017", pact.loan_use);
				/**投向地区*/
				reportMap.put("P060018", pact.throw_area);
				/**投向行业*/
				reportMap.put("P060019", pact.throw_industry);
				/**逾期天数*/
				reportMap.put("P060020", pact.overdue);
				/**本金逾期金额*/
				reportMap.put("P060021", pact.overdue_principal_amount);
				/**利息逾期金额*/
				reportMap.put("P060022", pact.overdue_interest_amount);
				/**五级分类*/
				reportMap.put("P060023",  pact.type);
				
				reportsList.add(reportMap);
			}
		}
		
		return reportsList;
	}
	
	/**
	 * 获取贷款合同表报文头 和贷款合同表报文基本信息
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.9
	 * */
	public Map< String ,JSONObject> getLoanMap() {
		Map< String ,JSONObject> result = new HashMap<String,JSONObject> ();
		
		Date date = new Date();
		 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		 Calendar cale = Calendar.getInstance();
		 cale.set(Calendar.DAY_OF_MONTH, 0);
		 String lastDay = format.format(cale.getTime());
		/**获取贷款合同表报文头*/
		JSONObject headerInfo = JRBUtils.getHeaderInfoMap("911301050799558020", "000000", "石家庄菲尔德投资咨询有限公司", "", DateUtil.dateToString(date,"yyyy-MM-dd") ,DateUtil.dateToString(date,"HH:mm:ss"), SuperviseReportType.DEBT_PACT_TABLE.value, "A1234B1234C1234D1234oplk8976tr1d");
		
		/**获取贷款合同表报文基本信息*/
		JSONObject reportBasicInfo = JRBUtils.getReportBasicInfoMap("911301050799558020", lastDay, "李媛媛", "裴雪松", "冯鑫");

		result.put("headerInfo", headerInfo);
		result.put("reportBasicInfo", reportBasicInfo);
		
		return result;
	}
	
	/**
	 * 获取贷款合同信息，生成XML文件
	 * 
	 * @param 生成XML文件的路径
	 * @author guoShiJie
	 * @createDate 2018.10.11
	 * */
	public File createLoanXML (String fileURL) {
		
		Map<String,JSONObject> loan = getLoanMap();
		List<JSONObject> report = getLoanReportList();
		return JRBXMLUtils.createXML(loan.get("headerInfo"), loan.get("reportBasicInfo"), report, fileURL);
	}
	
}
