package jobs;

import java.math.BigDecimal;
import java.util.Date;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.ResultInfo;
import controllers.finance.AcctInvestController;
import controllers.finance.TradeInfoCtrl;
import models.finance.entity.t_trade_info.TradeType;
import play.Logger;
import services.common.SettingService;
import services.core.BillService;
import services.finance.ALoanReceiptService;
import services.finance.AcctInvestService;
import services.finance.AgreGuaranteeService;
import services.finance.CustInfoService;
import services.finance.CustOrgService;
import services.finance.OrgInfoService;
import services.finance.RelInfoService;
import services.finance.TradeInfoService;

public class DailyPaper {

	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);

	public void doJob() {
		if (Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------日报，开始----------");
		}

		AcctInvestService acctInvestService = Factory.getService(AcctInvestService.class);
		ALoanReceiptService aLoanReceiptService = Factory.getService(ALoanReceiptService.class);
		AgreGuaranteeService agreGuaranteeService = Factory.getService(AgreGuaranteeService.class);
		OrgInfoService orgInfoService = Factory.getService(OrgInfoService.class);
		CustInfoService custInfoService = Factory.getService(CustInfoService.class);
		CustOrgService custOrgService = Factory.getService(CustOrgService.class);
		TradeInfoService tradeInfoService = Factory.getService(TradeInfoService.class);
		RelInfoService relInfoService = Factory.getService(RelInfoService.class);
		// 机构信息表 生成XML 上报XML
//		orgInfoService.createXmlbByOrgInfo();
		// 关联机构表 生成XML 上报XML
//		relInfoService.createXmlbByRelInfo();
		// 客户信息表 插入数据 生成XML 上报XML
		custInfoService.saveCustInfo();
		custInfoService.createXmlByCustInfo();
		// 客户关联信息表 插入数据 生成XML 上报XML
		custOrgService.saveCustOrg();
		custOrgService.createXmlByCustOrg();
		// 交易信息表 插入数据 生成XML 上报XML
		
		tradeInfoService.createXmlByTradeInfo();
		// 借据信息表 插入数据 生成XML 上报XML
		aLoanReceiptService.createIOUByALoanReceipt();
		aLoanReceiptService.createXmlByALoanReceipt();
		// 产品信息表 插入数据 生成XML 上报XML
		agreGuaranteeService.createProductInformationByAgreGuarantee();
		agreGuaranteeService.createXmlByAgreGuarantee();
		// 产品投向表 插入数据 生成XML 上报XML
		acctInvestService.createProductByAcctInvest();
		acctInvestService.createXmlbByAcctInvest();
		
		
		
		if (Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {

			Logger.info("-----------日报，结束----------");
		}

	}
}
