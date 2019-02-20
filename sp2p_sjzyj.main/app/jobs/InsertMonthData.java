package jobs;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import daos.finance.InvestmentContractDao;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.common.SettingService;
import services.core.BillService;
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
 * @ClassName: InsertMonthData
 *
 * @description 定时向金融办月报插入数据(报表P04客户信息表至报表P05最大十家客户信息表)
 *
 * @author HanMeng
 * @createDate 2018年11月16日-上午9:55:37
 */

public class InsertMonthData {
		
	protected UserInformationService userInformationService = Factory.getService(UserInformationService.class);

	protected MaxTenUserInformationService maxTenUserInformationService = Factory.getService(MaxTenUserInformationService.class);
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);

	public void doJob() {

		if (Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------开始执行定时任务：月报报表(报表4--报表5)向数据库插入数据开始----------");
		}
		long start,end;
		start = System.currentTimeMillis();
		
		/** P04客户信息表定时插入  */
		userInformationService.insertUserInformation();
		/** P05最大十家客户信息表定时插入 */
		maxTenUserInformationService.insertMaxTenUserInformation();
		
		end = System.currentTimeMillis();  
		System.out.println("start time:" + start+ "; end time:" + end+ "; Run Time:" + (end - start)/(1000*60) + "(min)");
		if (Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------执行结束：月报报表(报表4--报表5)向数据库插入数据结束----------");
		}

	}
}
