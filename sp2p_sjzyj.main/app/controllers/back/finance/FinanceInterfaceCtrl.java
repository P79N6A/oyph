package controllers.back.finance;


import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import common.enums.SuperviseReportType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.JRBHttpsUtils;
import common.utils.JRBUtils;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;
import models.finance.entity.t_cash_flow;
import models.finance.entity.t_current_assets;
import models.finance.entity.t_profit;
import models.finance.entity.t_return_status;
import models.finance.entity.t_return_status.ReportType;
import services.finance.ALoanReceiptService;
import services.finance.AcctInvestService;
import services.finance.AgreGuaranteeService;
import services.finance.BorrowerRepaymentService;
import services.finance.CashFlowService;
import services.finance.CurrentAssetsService;
import services.finance.CustInfoService;
import services.finance.CustOrgService;
import services.finance.ExecutiveInformationService;
import services.finance.InternalControlChartService;
import services.finance.InternetAgencyDetailStatisticService;
import services.finance.InternetAgencyMonitorStatisticService;
import services.finance.InternetChangeMonitorService;
import services.finance.InternetClearSummaryService;
import services.finance.InvestmentContractService;
import services.finance.LenderCollectionService;
import services.finance.LoanPactService;
import services.finance.MaxTenUserInformationService;
import services.finance.OrgInfoService;
import services.finance.OrganizationGeneralizeService;
import services.finance.ProfitService;
import services.finance.RelInfoService;
import services.finance.ReturnStatusService;
import services.finance.ShareholderInformationService;
import services.finance.TradeInfoService;
import services.finance.UserInformationService;
/**
 * 
 *
 * @ClassName: FinanceInterfaceCtrl
 *
 * @description 后台-财务- 财务信息-controller
 *
 * @author LiuHangjing
 * @createDate 2018年10月6日-下午3:42:15
 */
public class FinanceInterfaceCtrl extends BackBaseController {
	
	protected static CurrentAssetsService currentAssetsService = Factory.getService(CurrentAssetsService.class);
	
	protected static ProfitService profitService = Factory.getService(ProfitService.class);
	
	protected static CashFlowService cashFlowService = Factory.getService(CashFlowService.class);
	
	protected static ReturnStatusService returnStatusService = Factory.getService(ReturnStatusService.class);
	
	protected static AcctInvestService acctInvestService = Factory.getService(AcctInvestService.class);
	
	protected static ALoanReceiptService aLoanReceiptService = Factory.getService(ALoanReceiptService.class);
	
	protected static AgreGuaranteeService agreGuaranteeService = Factory.getService(AgreGuaranteeService.class);
	
	protected static OrgInfoService orgInfoService = Factory.getService(OrgInfoService.class);
	
	protected static CustInfoService custInfoService = Factory.getService(CustInfoService.class);
	
	protected static CustOrgService custOrgService = Factory.getService(CustOrgService.class);
	
	protected static TradeInfoService tradeInfoService = Factory.getService(TradeInfoService.class);
	
	protected static ExecutiveInformationService executiveInformationService = Factory.getService(ExecutiveInformationService.class);
	
	protected static LoanPactService loanPactService = Factory.getService(LoanPactService.class);
	
	protected static InvestmentContractService investmentContractService = Factory.getService(InvestmentContractService.class);
	
	protected static UserInformationService userInformationService = Factory.getService(UserInformationService.class);
	
	protected static BorrowerRepaymentService borrowerRepaymentService = Factory.getService(BorrowerRepaymentService.class);
	
	protected static LenderCollectionService lenderCollectionService = Factory.getService(LenderCollectionService.class);
	
	protected static MaxTenUserInformationService maxTenUserInformationService = Factory.getService(MaxTenUserInformationService.class);
	
	protected static ShareholderInformationService shareholderInformationService = Factory.getService(ShareholderInformationService.class);
	
	protected static OrganizationGeneralizeService organizationGeneralizeService = Factory.getService(OrganizationGeneralizeService.class);
	
	protected static InternalControlChartService internalControlChartService = Factory.getService(InternalControlChartService.class);
	
	protected static RelInfoService relInfoService = Factory.getService(RelInfoService.class);
	
	protected static InternetAgencyDetailStatisticService internetAgencyDetailStatisticService = Factory.getService(InternetAgencyDetailStatisticService.class);
	
	protected static InternetAgencyMonitorStatisticService interAgencyMonitorStatisticService = Factory.getService(InternetAgencyMonitorStatisticService.class);
	
	protected static InternetChangeMonitorService internetChangeMonitorService = Factory.getService(InternetChangeMonitorService.class);
	
	protected static InternetClearSummaryService internetClearSummaryService = Factory.getService(InternetClearSummaryService.class);
	/**
	 * 
	 * @Title: balanceSheetPre
	 * 
	 * @description 财务信息-资产负债表列表显示页面
	 * @param  
	 *
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月6日-下午2:58:14
	 */
	public static void balanceSheetPre(int currPage,int pageSize) {

		PageBean<t_current_assets> page = currentAssetsService.pageOfCurrentAssets(currPage, pageSize);
		
		render(page);
	}
	
	/**
	 * 
	 * @Title: toAddBalanceSheetPre
	 * 
	 * @description 到资产负债添加页面
	 * @param  
	 *
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月6日-下午3:00:22
	 */
	public static void toAddBalanceSheetPre() {
		
		render();
	}
	
	/**
	 * 
	 * @Title: addBalanceSheet
	 * 
	 * @description 保存一条资产负债信息
	 * @param  
	 *
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @throws ParseException 
	 * @createDate 2018年10月6日-下午3:00:48
	 */
	public static void addBalanceSheet(t_current_assets assets,String create_time) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		assets.create_time=format.parse(create_time);
		assets.save();
		
		flash.success("添加成功");
		balanceSheetPre(1,10);
	}
	
	/**
	 * 
	 * @Title: toAddBalanceSheetPre
	 * 
	 * @description 到资产负债修改页面
	 * @param  id
	 *
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月6日-下午3:00:22
	 */
	public static void toEditBalanceSheetPre(long id) {
		//通过id查询,进入修改页面
		t_current_assets assets = currentAssetsService.findByID(id);
		
		if(assets==null){
			error404();
		}
		render(assets);
	}
	
	/**
	 * 
	 * @Title: editBalanceSheet
	 * 
	 * @description 保存修改好的一条资产负债信息
	 * @param ids
	 *
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @throws ParseException 
	 * @createDate 2018年10月6日-下午3:00:48
	 */
	public static void editBalanceSheet(t_current_assets assets,String create_time) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		assets.create_time=format.parse(create_time);
		
		assets.save();
		
		flash.success("编辑成功");
		balanceSheetPre(1,10);
	}
	
	/**
	 * 
	 * @Title: profitStatementPre
	 * 
	 * @description  财务信息-利润表列表显示
	 * @param 
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月6日-下午5:51:04
	 */
	public static void profitStatementPre(int currPage, int pageSize){
		
		PageBean<t_profit> page = profitService.pageOfProfit(currPage, pageSize);
		
		render(page);
	}
	/**
	 * 
	 * @Title: toAddProfitStatementPre
	 * 
	 * @description  财务信息-到利润表添加页面
	 * @param profit 
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月6日-下午5:53:05
	 */
	public static void toAddProfitStatementPre(){
		render();
	}
	/**
	 * 
	 * @Title: addProfitStatement
	 * 
	 * @description  保存一条利润表信息
	 * @param  profit
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @throws ParseException 
	 * @createDate 2018年10月6日-下午5:56:17
	 */
	public static void addProfitStatement(t_profit profit,String create_time) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		profit.create_time=format.parse(create_time);
		profit.save();
		flash.success("添加成功");
		profitStatementPre(1,10);
	}
	/**
	 * 
	 * @Title: toEditProfitStatementPre
	 * 
	 * @description 到修改利润表信息页面
	 * @param id
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月6日-下午5:59:34
	 */
	public static void toEditProfitStatementPre(long id){
		t_profit profit = profitService.findByID(id);
		
		if(profit == null){
			error404();
		}
		render(profit);
	}
	/**
	 * 
	 * @Title: editProfitStatement
	 * 
	 * @description 保存修改的利润表信息
	 * @param ids
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @throws ParseException 
	 * @createDate 2018年10月6日-下午6:01:14
	 */
	public static void editProfitStatement(t_profit profit,String create_time) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		profit.create_time=format.parse(create_time);
		
		profit.save();
		flash.success("编辑成功");
		profitStatementPre(1,10);
	}
	/**
	 * 
	 * @Title: cashFlowSheetPre
	 * 
	 * @description 财务信息-现金流量表列表分页显示
	 * @param 
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月6日-下午6:19:46
	 */
	public static void cashFlowSheetPre(int currPage, int pageSize){
		PageBean<t_cash_flow> page = cashFlowService.pageOfCashFlow(currPage, pageSize);
		
		render(page);
	}
	/**
	 * 
	 * @Title: toAddCashFlowSheetPre
	 * 
	 * @description 财务信息-到现金流量表添加页面
	 * @param 
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月6日-下午6:20:17
	 */
	public static void toAddCashFlowSheetPre(){
		
		render();
	}
	/**
	 * 
	 * @Title: addCashFlowSheet
	 * 
	 * @description 保存一条现金流量信息
	 * @param  profit
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @throws ParseException 
	 * @createDate 2018年10月6日-下午6:35:39
	 */
	public static void addCashFlowSheet(t_cash_flow cashFlow,String create_time) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		cashFlow.create_time=format.parse(create_time);
		
		cashFlow.save();
		flash.success("添加成功");
		cashFlowSheetPre(1,10);
	}
	/**
	 * 
	 * @Title: toEditCashFlowSheetPre
	 * 
	 * @description 到修改现金流量表信息页面
	 * @param id
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月6日-下午6:36:21
	 */
	public static void toEditCashFlowSheetPre(long id){
		t_cash_flow cashFlow = cashFlowService.findByID(id);
		if(cashFlow == null){
			error404();
		}
		Date create_time = cashFlow.create_time;
		String createTime = common.utils.TimeUtil.dateToStr(create_time);
		
		//通过id查询到该条信息,然后通过该条信息的时间,查询上期
		t_cash_flow cashFlowUp = cashFlowService.onIssue(createTime);
		render(cashFlow,cashFlowUp);

	}
	/**
	 * 
	 * @Title: editCashFlowSheet
	 * 
	 * @description 保存修改的现金流量表信息 
	 * @param  ids
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @throws ParseException 
	 * @createDate 2018年10月6日-下午6:22:23
	 */
	public static void editCashFlowSheet(t_cash_flow cashFlow,String create_time) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		cashFlow.create_time=format.parse(create_time);
		cashFlow.save();
		flash.success("编辑成功");
		cashFlowSheetPre(1,10);
	}
	/**
	 * 
	 * @Title: financialOfficePre
	 * 
	 * @description 财务信息-金融办列表分页显示
	 * @param currPage
	 * @param pageSize
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月17日-上午11:29:35
	 */
	public static void financialOfficePre(int currPage, int pageSize){
		PageBean<t_return_status> page = returnStatusService.pageOfReturnStatus(currPage, pageSize);
		
		render(page);
	}
	/**
	 * 
	 * @Title: replayReportPre
	 * 
	 * @description 手动重新发送控制器 
	 * @param id
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月18日-上午10:31:28
	 */
	public static void replayReportPre(Long id){
	
		t_return_status status = returnStatusService.findByID(id);
		
		if(status.report_name.indexOf("FP01") != -1){
			/** FP.FP01.001 机构信息表 生成XML 上报XML*/
			orgInfoService.createXmlbByOrgInfo();
		}else if(status.report_name.indexOf("FP02") != -1){
			relInfoService.createXmlbByRelInfo();
			
		}else if(status.report_name.indexOf("FP03") != -1){
			/** FP.FP03.001 客户信息表 插入数据 生成XML 上报XML*/
			custInfoService.saveCustInfo();
			custInfoService.createXmlByCustInfo();
			
		}else if(status.report_name.indexOf("FP04") != -1){
			/** FP.FP04.001 客户关联信息表 插入数据 生成XML 上报XML*/
			custOrgService.saveCustOrg();
			custOrgService.createXmlByCustOrg();
			
		}else if(status.report_name.indexOf("FP05") != -1){
			/** FP.FP05.001 交易信息表 插入数据 生成XML 上报XML*/
			
			tradeInfoService.createXmlByTradeInfo();
			
		}else if(status.report_name.indexOf("FP06") != -1){
			
			/** FP.FP06.001 借据信息表 插入数据 生成XML 上报XML*/
			aLoanReceiptService.createIOUByALoanReceipt();
			aLoanReceiptService.createXmlByALoanReceipt();
		}else if(status.report_name.indexOf("FP07") != -1){
			/** FP.FP07.001 产品信息表 插入数据 生成XML 上报XML*/
			agreGuaranteeService.createProductInformationByAgreGuarantee();
			agreGuaranteeService.createXmlByAgreGuarantee();
			
		}else if(status.report_name.indexOf("FP08") != -1){
			/** FP.FP08.001 产品投向表 插入数据 生成XML 上报XML*/
			acctInvestService.createProductByAcctInvest();
			acctInvestService.createXmlbByAcctInvest();
			
		}else if(status.report_name.indexOf("P01") != -1){
			/** P01 机构概况表*/
			organizationGeneralizeService.createOrganizationGeneralizeXml();
			
		}else if(status.report_name.indexOf("P02") != -1){
			/** P02 股东信息表*/
			shareholderInformationService.createShareholderInformationXml();
			
		}else if(status.report_name.indexOf("P03") != -1){
			
			/** P03 高管信息表*/
			File executiveFile = executiveInformationService.createexEcutiveInformationXML("data" + File.separator + "month"
					+ File.separator + JRBUtils.getXMLFileName(null, SuperviseReportType.EXECUTIVE_INFO_TABLE, "oyph"));
			if (executiveFile != null) {
				LoggerUtil.info(false, "金融办发送结果: %s", JRBHttpsUtils.post(executiveFile, ReportType.MONTH));
			}
		}else if(status.report_name.indexOf("P04") != -1){
			/** P04客户信息表定时插入  */
			userInformationService.insertUserInformation();
			/**P04客户信息表*/
			userInformationService.createUserInfomationXml();
			
		}else if(status.report_name.indexOf("P05") != -1){
			/** P05最大十家客户信息表定时插入 */
			maxTenUserInformationService.insertMaxTenUserInformation();
			/** P05最大十家客户信息表*/
			maxTenUserInformationService.createMaxTenUserInformationXml();
		}else if(status.report_name.indexOf("P06") != -1){
			/** P06金融办---贷款合同表XML */
			File loanFile = loanPactService.createLoanXML("data" + File.separator + "month" + File.separator
					+ JRBUtils.getXMLFileName(null, SuperviseReportType.DEBT_PACT_TABLE, "oyph"));
			if (loanFile != null) {
				LoggerUtil.info(false, "金融办发送结果: %s", JRBHttpsUtils.post(loanFile, ReportType.MONTH));
			}
			
		}else if(status.report_name.indexOf("P07") != -1){
			
			/** P07金融办---投资合同表XML */
			File investFile = investmentContractService.createInvestXML("data" + File.separator + "month" + File.separator
					+ JRBUtils.getXMLFileName(null, SuperviseReportType.INVEST_PACT_TABLE, "oyph"));
			if (investFile != null) {
				LoggerUtil.info(false, "金融办发送结果: %s", JRBHttpsUtils.post(investFile, ReportType.MONTH));
			}
		}else if(status.report_name.indexOf("P08") != -1){
			/** P08借款人还款明细表定时插入*/
			borrowerRepaymentService.saveBorrowerRepayment();
			/** P08借款人还款明细表  */
			borrowerRepaymentService.createBorrowerRepaymentXml();
		}else if(status.report_name.indexOf("P09") != -1){
			/** P09投资人收款明细表定时插入*/
			lenderCollectionService.savaLenderCollection();
			/** P09投资人收款明细表  */
			lenderCollectionService.createLenderCollectionXml();
		}else if(status.report_name.indexOf("P10") != -1){
			/** P10 机构资产负债表  */
			currentAssetsService.createCurrentAssetsXml();
			
		}else if(status.report_name.indexOf("P11") != -1){
			/** P11 机构利润表  */
			profitService.createProfitXml();
			
		}else if(status.report_name.indexOf("P12") != -1){
			/** P12 机构现金流量表*/
			cashFlowService.createCashFlowXml();
			
		}else if(status.report_name.indexOf("P13") != -1){
			/** P13 机构内控表*/
			File file = internalControlChartService.createInternalXML("data"+File.separator+"season"+File.separator+JRBUtils.getXMLFileName(null,SuperviseReportType.INTERNAL_CONTROL_SITUATION_TABLE , "oyph"));
			if(file != null){
				
				LoggerUtil.info(false, "金融办发送结果: %s", JRBHttpsUtils.post(file,ReportType.SEASON));
			}
			
		}
		
		financialOfficePre(1,10);
		
	}
	
	/**
	 * 文件发送
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.19
	 * */
	public static void sendReportPre (Integer num ) {
		
		if (num == null) {
			flash.error("未选中文件,发送失败");
			financialOfficePre(1,10);
		}
		
		switch (num) {
		case 1:
			/** FP.FP01.001 机构信息表 生成XML 上报XML*/
			orgInfoService.createXmlbByOrgInfo();
			break;
		case 2:
			relInfoService.createXmlbByRelInfo();
			break;
		case 3:
			/** FP.FP03.001 客户信息表 插入数据 生成XML 上报XML*/
			custInfoService.saveCustInfo();
			custInfoService.createXmlByCustInfo();
			break;
		case 4:
			/** FP.FP04.001 客户关联信息表 插入数据 生成XML 上报XML*/
			custOrgService.saveCustOrg();
			custOrgService.createXmlByCustOrg();
			break;
		case 5:
			/** FP.FP05.001 交易信息表 插入数据 生成XML 上报XML*/
			tradeInfoService.createXmlByTradeInfo();
			break;
		case 6:
			/** FP.FP06.001 借据信息表 插入数据 生成XML 上报XML*/
			aLoanReceiptService.createIOUByALoanReceipt();
			aLoanReceiptService.createXmlByALoanReceipt();
			break;
		case 7:
			/** FP.FP07.001 产品信息表 插入数据 生成XML 上报XML*/
			agreGuaranteeService.createProductInformationByAgreGuarantee();
			agreGuaranteeService.createXmlByAgreGuarantee();
			break;
		case 8:
			/** FP.FP08.001 产品投向表 插入数据 生成XML 上报XML*/
			acctInvestService.createProductByAcctInvest();
			acctInvestService.createXmlbByAcctInvest();
			break;
		case 9:
			/** P01 机构概况表*/
			organizationGeneralizeService.createOrganizationGeneralizeXml();
			break;
		case 10:
			/** P02 股东信息表*/
			shareholderInformationService.createShareholderInformationXml();
			break;
		case 11:
			/** P03 高管信息表*/
			File executiveFile = executiveInformationService.createexEcutiveInformationXML("data" + File.separator + "month"
					+ File.separator + JRBUtils.getXMLFileName(null, SuperviseReportType.EXECUTIVE_INFO_TABLE, "oyph"));
			if (executiveFile != null) {
				LoggerUtil.info(false, "金融办发送结果: %s", JRBHttpsUtils.post(executiveFile, ReportType.MONTH));
			}
			break;
		case 12:
			/** P04客户信息表定时插入  */
			userInformationService.insertUserInformation();
			/**P04客户信息表*/
			userInformationService.createUserInfomationXml();
			break;
		case 13:
			/** P05最大十家客户信息表定时插入 */
			maxTenUserInformationService.insertMaxTenUserInformation();
			/** P05最大十家客户信息表*/
			maxTenUserInformationService.createMaxTenUserInformationXml();
			break;
		case 14:
			/** P06金融办---贷款合同表XML */
			File loanFile = loanPactService.createLoanXML("data" + File.separator + "month" + File.separator
					+ JRBUtils.getXMLFileName(null, SuperviseReportType.DEBT_PACT_TABLE, "oyph"));
			if (loanFile != null) {
				LoggerUtil.info(false, "金融办发送结果: %s", JRBHttpsUtils.post(loanFile, ReportType.MONTH));
			}
			break;
		case 15:
			/** P07金融办---投资合同表XML */
			File investFile = investmentContractService.createInvestXML("data" + File.separator + "month" + File.separator
					+ JRBUtils.getXMLFileName(null, SuperviseReportType.INVEST_PACT_TABLE, "oyph"));
			if (investFile != null) {
				LoggerUtil.info(false, "金融办发送结果: %s", JRBHttpsUtils.post(investFile, ReportType.MONTH));
			}
			break;
		case 16:
			/** P08借款人还款明细表定时插入*/
			borrowerRepaymentService.saveBorrowerRepayment();
			/** P08借款人还款明细表  */
			borrowerRepaymentService.createBorrowerRepaymentXml();
			break;
		case 17:
			/** P09投资人收款明细表定时插入*/
			lenderCollectionService.savaLenderCollection();
			/** P09投资人收款明细表  */
			lenderCollectionService.createLenderCollectionXml();
			break;
		case 18:
			/** P10 机构资产负债表  */
			currentAssetsService.createCurrentAssetsXml();
			break;
		case 19:
			/** P11 机构利润表  */
			profitService.createProfitXml();
			break;
		case 20:
			/** P12 机构现金流量表*/
			cashFlowService.createCashFlowXml();
			break;
		case 21:
			/** P13 机构内控表*/
			File file = internalControlChartService.createInternalXML("data"+File.separator+"season"+File.separator+JRBUtils.getXMLFileName(null,SuperviseReportType.INTERNAL_CONTROL_SITUATION_TABLE , "oyph"));
			if(file != null){
				
				LoggerUtil.info(false, "金融办发送结果: %s", JRBHttpsUtils.post(file,ReportType.SEASON));
			}
			break;
		default:
			break;
		}
		
		financialOfficePre(1,10);
		
	}
	
	/**
	 * 财务报表页面
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.29
	 * */
	public static void financialStatementsPre () {
		render();
	}
	
	/**
	 * 导出生成Excel文件
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.29
	 * */
	public static void exportFinancialExcel (String export) {
		Date date = DateUtil.addMonth(new Date (), 0) ;
		String fileName = null;
		Integer exportType = Integer.parseInt(export);
		File file = null;
		switch (exportType) {
		case 0:
			flash.error("未选择报文，不能导出");
			financialStatementsPre ();
			break;
		case 1:
			/**清理整顿阶段工作进展情况汇总表*/
			fileName = "清理整顿阶段工作进展情况汇总表";
			file = internetClearSummaryService.createInternetClearSummaryExcel(date);
			break;
		case 2:
			/**重点对象（整改类）整改落实进展监测明细表*/
			fileName = "重点对象（整改类）整改落实进展监测明细表";
			file = internetChangeMonitorService.createInternetChangeMonitorExcel(date);
			break;
		case 3:
			/**重点机构流动性缺口监测统计表*/
			fileName = "重点机构流动性缺口监测统计表";
			file = interAgencyMonitorStatisticService.createInternetAgencyMonitorStatisticExcel(date);
			break;
		case 4:
			/**网贷机构验收进度明细统计表*/
			fileName = "网贷机构验收进度明细统计表";
			file = internetAgencyDetailStatisticService.createInternetAgencyDetailStatistic(date);
			break;
		default:
			break;
		}
		if (file != null) {
			renderBinary(file, fileName+".xls");
		}
		flash.error("未生成excel格式文件,不能下载");
		financialStatementsPre ();
	}
}


