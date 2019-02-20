package jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.shove.Convert;

import common.constants.SettingKey;
import common.enums.SuperviseReportType;
import common.utils.Factory;
import common.utils.JRBHttpsUtils;
import common.utils.JRBUtils;
import common.utils.LoggerUtil;
import controllers.finance.BorrowerRepaymentCtrl;
import daos.finance.UserInformationDao;
import models.ext.redpacket.entity.t_red_packet_user;
import models.finance.entity.t_borrower_repayment;
import models.finance.entity.t_return_status.ReportType;
import play.Logger;
import play.Play;
import play.jobs.Every;
import play.jobs.Job;
import services.common.SettingService;
import services.finance.BorrowerRepaymentService;
import services.finance.CurrentAssetsService;
import services.finance.ExecutiveInformationService;
import services.finance.InvestmentContractService;
import services.finance.LenderCollectionService;
import services.finance.LoanPactService;
import services.finance.MaxTenUserInformationService;
import services.finance.OrganizationGeneralizeService;
import services.finance.ProfitService;
import services.finance.ShareholderInformationService;
import services.finance.UserInformationService;

/**
 * 
 *
 * @ClassName: ServiceMonthJob
 *
 * @description 金融办月报
 *
 * @author HanMeng
 * @createDate 2018年10月11日-下午4:11:20
 */

public class ServiceMonthJob  {

	protected ExecutiveInformationService executiveInformationService = Factory.getService(ExecutiveInformationService.class);
	protected LoanPactService loanPactService = Factory.getService(LoanPactService.class);
	protected InvestmentContractService investmentContractService = Factory.getService(InvestmentContractService.class);
	protected UserInformationService userInformationService = Factory.getService(UserInformationService.class);
	protected BorrowerRepaymentService borrowerRepaymentService = Factory.getService(BorrowerRepaymentService.class);
	protected LenderCollectionService lenderCollectionService = Factory.getService(LenderCollectionService.class);
	protected MaxTenUserInformationService maxTenUserInformationService = Factory.getService(MaxTenUserInformationService.class);
	protected CurrentAssetsService currentAssetsService = Factory.getService(CurrentAssetsService.class);
	protected ProfitService profitService = Factory.getService(ProfitService.class);
	protected ShareholderInformationService shareholderInformationService = Factory.getService(ShareholderInformationService.class);
	protected  OrganizationGeneralizeService organizationGeneralizeService = Factory.getService(OrganizationGeneralizeService.class);
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);

	public void doJob() {

		if (Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------月报,开始---------------------");
		}
		long start,end;
		start = System.currentTimeMillis();
		
		
		/** P01 机构概况表 */
//		organizationGeneralizeService.createOrganizationGeneralizeXml();
		/** P02 股东信息表 */
		shareholderInformationService.createShareholderInformationXml();
		/** P03 高管信息表 */
		File executiveFile = executiveInformationService.createexEcutiveInformationXML(Play.applicationPath.toString().replace("\\", "/")+"/data/month"	+ File.separator + JRBUtils.getXMLFileName(null, SuperviseReportType.EXECUTIVE_INFO_TABLE, "oyph"));
		if (executiveFile != null) {
			LoggerUtil.info(false, "金融办发送结果: %s", JRBHttpsUtils.post(executiveFile, ReportType.MONTH));
		}
		/** P04客户信息表 */
		userInformationService.createUserInfomationXml();
		/** P05最大十家客户信息表 */
		maxTenUserInformationService.createMaxTenUserInformationXml();
		/** P06金融办---贷款合同表XML */
		File loanFile = loanPactService.createLoanXML(Play.applicationPath.toString().replace("\\", "/")+"/data/month" + File.separator+ JRBUtils.getXMLFileName(null, SuperviseReportType.DEBT_PACT_TABLE, "oyph"));
		if (loanFile != null) {
			LoggerUtil.info(false, "金融办发送结果: %s", JRBHttpsUtils.post(loanFile, ReportType.MONTH));
		}
		/** P07金融办---投资合同表XML */
		File investFile = investmentContractService.createInvestXML(Play.applicationPath.toString().replace("\\", "/")+"/data/month" + File.separator+ JRBUtils.getXMLFileName(null, SuperviseReportType.INVEST_PACT_TABLE, "oyph"));
		if (investFile != null) {
			LoggerUtil.info(false, "金融办发送结果: %s", JRBHttpsUtils.post(investFile, ReportType.MONTH));
		}
		/** P08借款人还款明细表  */
		borrowerRepaymentService.createBorrowerRepaymentXml();
		/** P09投资人收款明细表  */
		lenderCollectionService.createLenderCollectionXml();
		
//		/** P10P10资产负债表  */
//		currentAssetsService.createCurrentAssetsXml();
//		/** P11利润表  */
//		profitService.createProfitXml();
		end = System.currentTimeMillis();  
		System.out.println("start time:" + start+ "; end time:" + end+ "; Run Time:" + (end - start)/1000 + "(s)");
		if (Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------月报,结束---------------------");
		}

	}

}
