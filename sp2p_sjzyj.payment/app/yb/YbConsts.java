package yb;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.shove.Convert;

import common.constants.Constants;
import common.utils.LoggerUtil;
import play.Logger;
import play.Play;
import yb.enums.ServiceType;

/**
 * 宜宾银行托管常量
 * 
 * @author niu
 * @create 2017.08.23
 */
public class YbConsts {
	
	/** 宜宾银行常见配置属性 */
	private static Properties ybConf = null;
	
	/** 请求必须参数、ChkValue、宜宾响应ChkValue key集合 */
	private static Map<String, String[]> maps = null;
	
	/** 宜宾银行主配置文件 */
	private static final String path = Play.getFile("conf" + File.separator + "yb" + File.separator + "yibinbank.properties").getAbsolutePath();
	
	/**
	 * 加载主配置文件，并初始化相关信息
	 */
	static {
		Logger.info("宜宾银行主配置文件路径：%s", path);
		
		if (ybConf == null) {
			loadProperties();
		}
		
		if (maps == null) {
			initMaps();
		}
		
		initSupport();
	}
	
	/**
	 * 商户私钥文件地址
	 */
	public static final String MER_PRI_KEY_PATH = Play.getFile("conf" + File.separator + "yb" + File.separator + ybConf.getProperty("yibinbank_PrivateKeyFile")).getAbsolutePath();

	/**
	 * 商户公钥文件地址
	 */
	public static final String MER_PUB_KEY_PATH = Play.getFile("conf" + File.separator + "yb" + File.separator + ybConf.getProperty("yibinbank_PublicKeyFile")).getAbsolutePath();

	/**
	 * 宜宾银行接口提交地址
	 */
	public static final String YIBIN_URL = ybConf.getProperty("yibinbank_url");
	public static final String URL_ENCODED = "UTF-8";
	
	public static final String keyStr = ybConf.getProperty("rsa_key");
	public static final String ivStr  = ybConf.getProperty("rsa_iv");
	
	/**
	 * 物流接口提交地址
	 */
	public static final String LOGISTICS_URL = ybConf.getProperty("logistics_url");
	
	
	/**
	 * 加载宜宾银行主配置文件
	 */
	private static void loadProperties() {
		
		Logger.info("读取yibinbank.properties配置文件");
		ybConf = new Properties();
		
		try {
			ybConf.load(new FileInputStream(new File(path)));
		} catch (Exception e) {
			LoggerUtil.error(false, e, "读取yibinbank.properties配置文件异常");
		}
	}
	
	
	
	
	/**
     * KEY 和 IV 是对称加密生成加密串要求的两个变量
     */

    public static final String KEY = ybConf.getProperty("rsa_key");             //key就是自定义加密key，自己定义的简单串

    public static final String IV  = ybConf.getProperty("rsa_iv");    //iv是initialization vector的意思，就是加密的初始话矢量，初始化加密函数的变量
	
	
	
	
	
	
	/**
	 * 不同支付平台差异性融合
	 */
	public static void initSupport() {
		Constants.LOAN_SERVICE_FEE_MAXRATE = Convert.strToDouble(ybConf.getProperty("borrowManageMaxrate"), 50.0); 
		Constants.DEBT_TRANSFER_MAXRATE = Convert.strToDouble(ybConf.getProperty("debtTransferMaxrate"), 100.0);
		Constants.WITHDRAW_MAXRATE = Convert.strToDouble(ybConf.getProperty("maxWithdrawRate"), 1.0);
	}
	
	/**
	 * 初始化宜宾银行必须字段、ChkValue字段、响应ChkValue字段 key集合
	 */
	private static void initMaps() {
		maps = new HashMap<String, String[]>();
		
		//个人客户开户
		String[] person_customer_regist_notNecessary = {"companyName", "certFImage", "certBImage", "certInfo", "uniSocCreCode",
				"uniSocCreDir", "bizLicDomicile", "entType", "dateOfEst", "corpacc", "corpAccBankNo", "corpAccBankNm" , "bindType",
				"acctype", "oldbankAccountNo", "bankAccountNo", "bankAccountName", "bankAccountTelNo", "note"};
		String[] person_customer_regist_encrypt = {"customerId", "certNo", "phoneNo", "username", "address"};
		String[] person_customer_regist_resp_encrypt = {"accNo", "secBankaccNo"};
		String[] person_customer_regist_resp = {"accNo", "secBankaccNo"};
		
		
		maps.put(ServiceType.PERSON_CUSTOMER_REGIST.key + "_notNecessary", person_customer_regist_notNecessary);
		maps.put(ServiceType.PERSON_CUSTOMER_REGIST.key + "_encrypt", person_customer_regist_encrypt);
		maps.put(ServiceType.PERSON_CUSTOMER_REGIST.key + "_resp_encrypt", person_customer_regist_resp_encrypt);
		maps.put(ServiceType.PERSON_CUSTOMER_REGIST.key + "_resp", person_customer_regist_resp);
		
		//企业客户开户
		String[] enterprise_customer_regist_notNecessary = {"certType","certFImage", "certBImage", 
				"certInfo", "idvalidDate", "idexpiryDate", "jobType", "job", "postcode", "address", "national", 
				"completeFlag", "uniSocCreDir", "bindType", "acctype", "oldbankAccountNo", "bankAccountNo", 
				"bankAccountName", "bankAccountTelNo", "note"};
		String[] enterprise_customer_regist_encrypt = {"username","customerId","phoneNo", "uniSocCreCode", "bizLicDomicile", "corpacc","certNo"};
		String[] enterprise_customer_regist_resp_encrypt = {"accNo", "secBankaccNo"};
		String[] enterprise_customer_regist_resp = {"accNo", "secBankaccNo"};
		

		maps.put(ServiceType.ENTERPRISE_CUSTOMER_REGIST.key + "_notNecessary", enterprise_customer_regist_notNecessary);
		maps.put(ServiceType.ENTERPRISE_CUSTOMER_REGIST.key + "_encrypt", enterprise_customer_regist_encrypt);
		maps.put(ServiceType.ENTERPRISE_CUSTOMER_REGIST.key + "_resp_encrypt", enterprise_customer_regist_resp_encrypt);
		maps.put(ServiceType.ENTERPRISE_CUSTOMER_REGIST.key + "_resp", enterprise_customer_regist_resp);
		
		//客户充值
		String[] customer_recharge_notNecessary = {};
		String[] customer_recharge_list_encrypt ={"debitAccountNo","cebitAccountNo"};
		String[] customer_recharge_encrypt = {"bankAccountNo"};
		String[] customer_recharge_resp_encrypt = {};

		maps.put(ServiceType.CUSTOMER_RECHARGE.key + "_notNecessary", customer_recharge_notNecessary);
		maps.put(ServiceType.CUSTOMER_RECHARGE.key + "_encrypt", customer_recharge_encrypt);
		maps.put(ServiceType.CUSTOMER_RECHARGE.key + "_resp_encrypt", customer_recharge_resp_encrypt);
		maps.put(ServiceType.CUSTOMER_RECHARGE.key + "_list_encrypt", customer_recharge_list_encrypt);
		
		
		//客户提现
		String[] customer_withdraw_list_encrypt ={"debitAccountNo","cebitAccountNo"};
		String[] customer_withdraw_encrypt = {"bankAccountNo", "deductType"};
		String[] customer_withdraw_resp_encrypt = {};

		maps.put(ServiceType.CUSTOMER_WITHDRAW.key + "_encrypt", customer_withdraw_encrypt);
		maps.put(ServiceType.CUSTOMER_WITHDRAW.key + "_resp_encrypt", customer_withdraw_resp_encrypt);
		maps.put(ServiceType.CUSTOMER_WITHDRAW.key + "_list_encrypt", customer_withdraw_list_encrypt);	
	
		//企业提现
		String[] enterprise_withdraw_list_encrypt ={"debitAccountNo","cebitAccountNo"};
		String[] enterprise_withdraw_encrypt = {"bankAccountNo", "deductType"};
		String[] enterprise_withdraw_resp_encrypt = {};

		maps.put(ServiceType.ENTERPRISE_WITHDRAW.key + "_encrypt", enterprise_withdraw_encrypt);
		maps.put(ServiceType.ENTERPRISE_WITHDRAW.key + "_resp_encrypt", enterprise_withdraw_resp_encrypt);
		maps.put(ServiceType.ENTERPRISE_WITHDRAW.key + "_list_encrypt", enterprise_withdraw_list_encrypt);	
		
		//正常放款
		String[] LOAN_list_encrypt ={"debitAccountNo","cebitAccountNo"};
		String[] LOAN_encrypt = {"objectId"};

		maps.put(ServiceType.LOAN.key + "_encrypt", LOAN_encrypt);
		maps.put(ServiceType.LOAN.key + "_list_encrypt", LOAN_list_encrypt);
		
		//正常放款
		String[] farm_in_list_encrypt ={"debitAccountNo","cebitAccountNo"};
		String[] farm_in_list2_encrypt ={"debitUserid","cebitUserid"};
		String[] farm_in_encrypt = {"objectId"};

		maps.put(ServiceType.FARM_IN.key + "_encrypt", farm_in_encrypt);
		maps.put(ServiceType.FARM_IN.key + "_list_encrypt", farm_in_list_encrypt);
		maps.put(ServiceType.FARM_IN.key + "_list2_encrypt", farm_in_list2_encrypt);
		
		//投标
		String[] market_encrypt = {"objectId"};
		String[] market_list_encrypt = {"debitAccountNo", "cebitAccountNo"};
		
		maps.put(ServiceType.MARKET.key + "_list_encrypt", market_list_encrypt);
		maps.put(ServiceType.MARKET.key + "_encrypt", market_encrypt);
		
		
		//客户信息修改
		String[] customer_info_modify_encrypt = {"customerId", "certNo", "phoneNo", "username", "address"};
		String[] customer_info_modify_resp_encrypt = {"accNo", "secBankaccNo"};
		String[] customer_info_modify_resp = {"accNo", "secBankaccNo"};
		
		
		maps.put(ServiceType.CUSTOMER_INFO_MODIFY.key + "_encrypt", customer_info_modify_encrypt);
		maps.put(ServiceType.CUSTOMER_INFO_MODIFY.key + "_resp_encrypt", customer_info_modify_resp_encrypt);
		maps.put(ServiceType.CUSTOMER_INFO_MODIFY.key + "_resp", customer_info_modify_resp);
		
		//客户注销
		String[] customer_cancel_notNecessary = {"ctype", "crole", "companyName", "username", "certType", "certNo", "certFImage", "certBImage",
				"certInfo","idvalidDate", "idexpiryDate","job","jobType","postcode","address","national", "completeFlag","phoneNo", "uniSocCreCode",
				"uniSocCreDir", "bizLicDomicile", "entType", "dateOfEst","corpacc","corpAccBankNo","corpAccBankNm","bindFlag", "bindType",
				"acctype","oldbankAccountNo", "bankAccountNo", "bankAccountName", "bankAccountTelNo","note"};
		String[] customer_cancel_encrypt = {"customerId"};
		String[] customer_cancel_resp_encrypt = {"accNo", "secBankaccNo"};
		String[] customer_cancel_resp = {"accNo", "secBankaccNo"};
		
		maps.put(ServiceType.CUSTOMER_CANCEL.key + "_notNecessary", customer_cancel_notNecessary);
		maps.put(ServiceType.CUSTOMER_CANCEL.key + "_encrypt", customer_cancel_encrypt);
		maps.put(ServiceType.CUSTOMER_CANCEL.key + "_resp_encrypt", customer_cancel_resp_encrypt);
		maps.put(ServiceType.CUSTOMER_CANCEL.key + "_resp", customer_cancel_resp);
		
		//客户解绑
		String[] customer_unbinding_encrypt = {"customerId", "certNo", "phoneNo", "username", "address", "bankAccountNo", "bankAccountName", "bankAccountTelNo"};
		String[] customer_unbinding_resp_encrypt = {"accNo", "secBankaccNo"};
		String[] customer_unbinding_resp = {"accNo", "secBankaccNo"};
		
		
		maps.put(ServiceType.CUSTOMER_UNBINDING.key + "_encrypt", customer_unbinding_encrypt);
		maps.put(ServiceType.CUSTOMER_UNBINDING.key + "_resp_encrypt", customer_unbinding_resp_encrypt);
		maps.put(ServiceType.CUSTOMER_UNBINDING.key + "_resp", customer_unbinding_resp);
		
		
		//标的发布
		String[] bid_public_encrypt = {"objectId", "objectName", "customerId"};
		String[] bid_public_resp_encrypt = {"objectaccNo"};
		String[] bid_public_resp = {"objectaccNo"};
		
		maps.put(ServiceType.BID_PUBLIC.key + "_encrypt", bid_public_encrypt);
		maps.put(ServiceType.BID_PUBLIC.key + "_resp_encrypt", bid_public_resp_encrypt);
		maps.put(ServiceType.BID_PUBLIC.key + "_resp", bid_public_resp);
		
		//标的流标
		String[] bid_failed_encrypt = {"objectId", "objectName", "customerId"};
		String[] bid_failed_resp_encrypt = {"objectaccNo"};
		String[] bid_failed_resp = {"objectaccNo"};
		
		maps.put(ServiceType.BID_FAILED.key + "_encrypt", bid_failed_encrypt);
		maps.put(ServiceType.BID_FAILED.key + "_resp_encrypt", bid_failed_resp_encrypt);
		maps.put(ServiceType.BID_FAILED.key + "_resp", bid_failed_resp);
		
		//标的撤标
		String[] bid_cancel_encrypt = {"objectId", "objectName", "customerId"};
		String[] bid_cancel_resp_encrypt = {"objectaccNo"};
		String[] bid_cancel_resp = {"objectaccNo"};
		
		maps.put(ServiceType.BID_CANCEL.key + "_encrypt", bid_cancel_encrypt);
		maps.put(ServiceType.BID_CANCEL.key + "_resp_encrypt", bid_cancel_resp_encrypt);
		maps.put(ServiceType.BID_CANCEL.key + "_resp", bid_cancel_resp);
		
		//标的修改
		String[] bid_modify_encrypt = {"objectId", "objectName", "customerId"};
		String[] bid_modify_resp_encrypt = {"objectaccNo"};
		String[] bid_modify_resp = {"objectaccNo"};
		
		maps.put(ServiceType.BID_MODIFY.key + "_encrypt", bid_modify_encrypt); 
		maps.put(ServiceType.BID_MODIFY.key + "_resp_encrypt", bid_modify_resp_encrypt);
		maps.put(ServiceType.BID_MODIFY.key + "_resp", bid_modify_resp);
		
		//投标
		String[] tender_encrypt = {"objectId"};
		String[] tender_list_encrypt = {"debitAccountNo", "cebitAccountNo"};
		
		maps.put(ServiceType.TENDER.key + "_list_encrypt", tender_list_encrypt);
		maps.put(ServiceType.TENDER.key + "_encrypt", tender_encrypt);
		
		//冲正
		String[] correct_encrypt = {"objectId"};
		String[] correct_list_encrypt = {"debitAccountNo", "cebitAccountNo"};
		
		maps.put(ServiceType.WASHED.key + "_list_encrypt", correct_list_encrypt);
		maps.put(ServiceType.WASHED.key + "_encrypt", correct_encrypt);
		
		//取消投标
		String[] cancel_tender_encrypt = {"objectId"};
		String[] cancel_tender_list_encrypt = {"debitAccountNo", "cebitAccountNo"};
		
		maps.put(ServiceType.CANCEL_TENDER.key + "_list_encrypt", cancel_tender_list_encrypt);
		maps.put(ServiceType.CANCEL_TENDER.key + "_encrypt", cancel_tender_encrypt);
		
		
		//还款
		String[] repayment_encrypt = {"objectId"};
		String[] repayment_list_encrypt = {"debitAccountNo", "cebitAccountNo"};
		
		maps.put(ServiceType.REPAYMENT.key + "_list_encrypt", repayment_list_encrypt);
		maps.put(ServiceType.REPAYMENT.key + "_encrypt", repayment_encrypt);
		
		//出款
		String[] payment_encrypt = {"objectId"};
		String[] payment_list_encrypt = {"debitAccountNo", "cebitAccountNo"};
		
		maps.put(ServiceType.PAYMENT.key + "_list_encrypt", payment_list_encrypt);
		maps.put(ServiceType.PAYMENT.key + "_encrypt", payment_encrypt);
		
		//台账信息查询
		String[] account_info_query_encrypt = {"accountNo"};
		String[] account_info_query_resp = {"totalPageNo", "totalChkNo", "customerId", "phoneNo", "identityCheckStatus", "extantStatus", "accountNo", "accountStatus", "availableamount", "transitamount", "withdrawalamount", "assetamount", "secBackaccNo", "secBankaccStatus", "cardList", "serviceDate", "note"};
		String[] account_info_query_resp_encrypt = {"customerId", "phoneNo", "accountNo", "secBankaccNo"};

		maps.put(ServiceType.ACCOUNT_INFO_QUERY.key + "_encrypt", account_info_query_encrypt);
		maps.put(ServiceType.ACCOUNT_INFO_QUERY.key + "_resp", account_info_query_resp);
		maps.put(ServiceType.ACCOUNT_INFO_QUERY.key + "_resp_encrypt", account_info_query_resp_encrypt);
	
		//用户信息查询
		String[] customer_info_query_encrypt = {"accountNo", "customerId"};
		String[] customer_info_query_resp = {"totalPageNo", "totalChkNo", "customerId", "phoneNo", "identityCheckStatus", "extantStatus", "accountNo", "accountStatus", "availableamount", "transitamount", "withdrawalamount", "assetamount", "secBankaccNo", "secBankaccStatus", "cardList", "serviceDate", "note"};
		String[] customer_info_query_resp_encrypt = {"customerId", "phoneNo", "accountNo", "secBankaccNo"};
		String[] customer_info_query_resp_list = {"tiedAccStatus"};
		String[] customer_info_query_resp_list_encrypt = {"tiedAccno", "tiedAcctelno"};
		
		maps.put(ServiceType.CUSTOMER_INFO_QUERY.key + "_encrypt", customer_info_query_encrypt);
		maps.put(ServiceType.CUSTOMER_INFO_QUERY.key + "_resp", customer_info_query_resp);
		maps.put(ServiceType.CUSTOMER_INFO_QUERY.key + "_resp_encrypt", customer_info_query_resp_encrypt);
		maps.put(ServiceType.CUSTOMER_INFO_QUERY.key + "_resp_list", customer_info_query_resp_list);
		maps.put(ServiceType.CUSTOMER_INFO_QUERY.key + "_resp_list_encrypt", customer_info_query_resp_list_encrypt);
		
		//交易状态查询
		String[] trade_status_query_resp = {"oldbusinessSeqNo", "dealStatus", "respCode", "respMsg", "accountNo", "secBankaccNo", "objectaccNo"};
		String[] trade_status_query_resp_encrypt = {"accountNo", "secBankaccNo", "objectaccNo"};
		
		maps.put(ServiceType.TRADE_STATUS_QUERY.key + "_resp", trade_status_query_resp);
		maps.put(ServiceType.TRADE_STATUS_QUERY.key + "_resp_encrypt", trade_status_query_resp_encrypt);
				
		//用户银行卡查询
		String[] binded_bank_notNecessary = { "beginDate", "endDate", "beginPage", "endPage", "note"};
		String[] binded_bank_encrypt = {"customerId","accountNo"};
		String[] binded_bank_regist_resp_list = {"tiedAccStatus"};
		String[] binded_bank_regist_resp_encrypt = {"customerId","phoneNo","accountNo","secBankaccNo"};
		String[] binded_bank_regist_resp_list_encrypt = {"tiedAccno", "tiedAcctelno"};
		String[] binded_bank_regist_resp = {"totalPageNo", "totalChkNo", "customerId", "phoneNo", "identityCheckStatus", "extantStatus", "accountNo", "accountStatus", "availableamount", "transitamount", "withdrawalamount", "assetamount", "secBankaccNo", "secBankaccStatus", "cardList", "serviceDate", "note"};
		 
		maps.put(ServiceType.QUERY_BINDED_BANK.key + "_notNecessary", binded_bank_notNecessary);
		maps.put(ServiceType.QUERY_BINDED_BANK.key + "_encrypt", binded_bank_encrypt);
		maps.put(ServiceType.QUERY_BINDED_BANK.key + "_resp_encrypt", binded_bank_regist_resp_encrypt);	
		maps.put(ServiceType.QUERY_BINDED_BANK.key + "_resp_list_encrypt", binded_bank_regist_resp_list_encrypt);
		maps.put(ServiceType.QUERY_BINDED_BANK.key + "_resp_list", binded_bank_regist_resp_list);	
		maps.put(ServiceType.QUERY_BINDED_BANK.key + "_resp", binded_bank_regist_resp);
		
		//台账信息查询
		String[] ledger_message_notNecessary = {"customerId","beginDate", "endDate", "beginPage", "endPage", "note"};
		String[] ledger_message_encrypt = {"accountNo"};	
		String[] ledger_message_regist_resp_list = {"tiedAccno", "tiedAccStatus", "tiedAcctelno"};
		String[] ledger_message_regist_resp_encrypt = {"customerId","phoneNo","accountNo","secBankaccNo"};
		String[] ledger_message_regist_resp_list_encrypt = {"tiedAccno", "tiedAcctelno"};                                        
		String[] ledger_message_regist_resp = {"totalPageNo", "totalChkNo", "customerId", "phoneNo", "identityCheckStatus", "extantStatus", "accountNo", "accountStatus", "availableamount", "transitamount", "withdrawalamount", "assetamount", "secBankaccNo", "secBankaccStatus", "cardList", "serviceDate", "note"};
	
		maps.put(ServiceType.QUERY_MESSAGE.key + "_notNecessary", ledger_message_notNecessary);
		maps.put(ServiceType.QUERY_MESSAGE.key + "_encrypt", ledger_message_encrypt);
		maps.put(ServiceType.QUERY_MESSAGE.key + "_resp_encrypt", ledger_message_regist_resp_encrypt);	
		maps.put(ServiceType.QUERY_MESSAGE.key + "_resp_list_encrypt", ledger_message_regist_resp_list_encrypt);
		maps.put(ServiceType.QUERY_MESSAGE.key + "_resp_list", ledger_message_regist_resp_list);
		maps.put(ServiceType.QUERY_MESSAGE.key + "_resp", ledger_message_regist_resp);
		
		//代偿充值
		String[] compensatory_recharge_notNecessary = {};
		String[] compensatory_recharge_list_encrypt ={"debitAccountNo","cebitAccountNo"};
		String[] compensatory_recharge_encrypt = {"bankAccountNo"};
		String[] compensatory_recharge_resp_encrypt = {};

		maps.put(ServiceType.COMPENSATORY_RECHARGE.key + "_notNecessary", compensatory_recharge_notNecessary);
		maps.put(ServiceType.COMPENSATORY_RECHARGE.key + "_encrypt", compensatory_recharge_encrypt);
		maps.put(ServiceType.COMPENSATORY_RECHARGE.key + "_resp_encrypt", compensatory_recharge_resp_encrypt);
		maps.put(ServiceType.COMPENSATORY_RECHARGE.key + "_list_encrypt", compensatory_recharge_list_encrypt);
		
		//营销充值
		String[] market_recharge_notNecessary = {};
		String[] market_recharge_list_encrypt ={"debitAccountNo","cebitAccountNo"};
		String[] market_recharge_encrypt = {"bankAccountNo"};
		String[] market_recharge_resp_encrypt = {};

		maps.put(ServiceType.MARKET_RECHARGE.key + "_notNecessary", market_recharge_notNecessary);
		maps.put(ServiceType.MARKET_RECHARGE.key + "_encrypt", market_recharge_encrypt);
		maps.put(ServiceType.MARKET_RECHARGE.key + "_resp_encrypt", market_recharge_resp_encrypt);
		maps.put(ServiceType.MARKET_RECHARGE.key + "_list_encrypt", market_recharge_list_encrypt);
		
		//费用充值
		String[] cost_recharge_notNecessary = {};
		String[] cost_recharge_list_encrypt ={"debitAccountNo","cebitAccountNo"};
		String[] cost_recharge_encrypt = {"bankAccountNo"};
		String[] cost_recharge_resp_encrypt = {};

		maps.put(ServiceType.COST_RECHARGE.key + "_notNecessary", cost_recharge_notNecessary);
		maps.put(ServiceType.COST_RECHARGE.key + "_encrypt", cost_recharge_encrypt);
		maps.put(ServiceType.COST_RECHARGE.key + "_resp_encrypt", cost_recharge_resp_encrypt);
		maps.put(ServiceType.COST_RECHARGE.key + "_list_encrypt", cost_recharge_list_encrypt);
		
		//代偿提现
		String[] compensatory_withdraw_list_encrypt ={"debitAccountNo","cebitAccountNo"};
		String[] compensatory_withdraw_encrypt = {"bankAccountNo", "deductType"};
		String[] compensatory_withdraw_resp_encrypt = {};

		maps.put(ServiceType.COMPENSATORY_WITHDRAW.key + "_encrypt", compensatory_withdraw_encrypt);
		maps.put(ServiceType.COMPENSATORY_WITHDRAW.key + "_resp_encrypt", compensatory_withdraw_resp_encrypt);
		maps.put(ServiceType.COMPENSATORY_WITHDRAW.key + "_list_encrypt", compensatory_withdraw_list_encrypt);
		
		//营销提现
		String[] market_withdraw_list_encrypt ={"debitAccountNo","cebitAccountNo"};
		String[] market_withdraw_encrypt = {"bankAccountNo", "deductType"};
		String[] market_withdraw_resp_encrypt = {};

		maps.put(ServiceType.MARKET_WITHDRAW.key + "_encrypt", market_withdraw_encrypt);
		maps.put(ServiceType.MARKET_WITHDRAW.key + "_resp_encrypt", market_withdraw_resp_encrypt);
		maps.put(ServiceType.MARKET_WITHDRAW.key + "_list_encrypt", market_withdraw_list_encrypt);
		
		//费用提现
		String[] cost_withdraw_list_encrypt ={"debitAccountNo","cebitAccountNo"};
		String[] cost_withdraw_encrypt = {"bankAccountNo", "deductType"};
		String[] cost_withdraw_resp_encrypt = {};

		maps.put(ServiceType.COST_WITHDRAW.key + "_encrypt", cost_withdraw_encrypt);
		maps.put(ServiceType.COST_WITHDRAW.key + "_resp_encrypt", cost_withdraw_resp_encrypt);
		maps.put(ServiceType.COST_WITHDRAW.key + "_list_encrypt", cost_withdraw_list_encrypt);
			
		// 异步回调
		String[] aysn_call_back_resp_encrypt = {"accNo", "secBankaccNo"};
		String[] aysn_call_back_resp = {"accNo", "businessSeqNo", "dealStatus", "note", "oldTxnType", "oldbusinessSeqNo", "respCode", "respMsg", "secBankaccNo"};
		
		maps.put(ServiceType.ASYN_CALL_BACK.key + "_resp_encrypt", aysn_call_back_resp_encrypt);
		maps.put(ServiceType.ASYN_CALL_BACK.key + "_resp", aysn_call_back_resp);
		
		//委托投标开启/解除
		String[] entrust_protocol_agree_encrypt={"protocolNo", "customerId"};
		String[] entrust_protocol_cancel_encrypt={"protocolNo", "customerId"};
		
		maps.put(ServiceType.ENTRUST_PROTOCOL_AGREE.key + "_encrypt",entrust_protocol_agree_encrypt);
		maps.put(ServiceType.ENTRUST_PROTOCOL_CANCEL.key + "_encrypt",entrust_protocol_cancel_encrypt);
		
		//代偿线下收款
		String[] compensatory_encrypt = {"objectId"};
		String[] compensatory_list_encrypt = {"debitAccountNo", "cebitAccountNo"};
		
		maps.put(ServiceType.COMPENSATORY.key + "_list_encrypt", compensatory_list_encrypt);
		maps.put(ServiceType.COMPENSATORY.key + "_encrypt", compensatory_encrypt);
		
		//代偿回款
		String[] compensatorys_encrypt = {"objectId"};
		String[] compensatorys_list_encrypt = {"debitAccountNo", "cebitAccountNo"};
		
		maps.put(ServiceType.COMPENSATORY_PAYMENT.key + "_list_encrypt", compensatorys_list_encrypt);
		maps.put(ServiceType.COMPENSATORY_PAYMENT.key + "_encrypt", compensatorys_encrypt);
		
		//客户网银充值
		String[] customer_internetbank_recharge_notNecessary = {};
		String[] customer_internetbank_recharge_list_encrypt ={"debitAccountNo","cebitAccountNo"};
		String[] customer_internetbank_recharge_encrypt = {};
		String[] customer_internetbank_recharge_resp_encrypt = {};

		maps.put(ServiceType.CUSTOMER_INTERNATBANK_RECHARGE.key + "_notNecessary", customer_internetbank_recharge_notNecessary);
		maps.put(ServiceType.CUSTOMER_INTERNATBANK_RECHARGE.key + "_encrypt", customer_internetbank_recharge_encrypt);
		maps.put(ServiceType.CUSTOMER_INTERNATBANK_RECHARGE.key + "_resp_encrypt", customer_internetbank_recharge_resp_encrypt);
		maps.put(ServiceType.CUSTOMER_INTERNATBANK_RECHARGE.key + "_list_encrypt", customer_internetbank_recharge_list_encrypt);
		
		//企业网银充值
		String[] enterprise_internetbank_recharge_notNecessary = {};
		String[] enterprise_internetbank_recharge_list_encrypt ={"debitAccountNo","cebitAccountNo"};
		String[] enterprise_internetbank_recharge_encrypt = {};
		String[] enterprise_internetbank_recharge_resp_encrypt = {};

		maps.put(ServiceType.ENTERPRISE_INTERNATBANK_RECHARGE.key + "_notNecessary", enterprise_internetbank_recharge_notNecessary);
		maps.put(ServiceType.ENTERPRISE_INTERNATBANK_RECHARGE.key + "_encrypt", enterprise_internetbank_recharge_encrypt);
		maps.put(ServiceType.ENTERPRISE_INTERNATBANK_RECHARGE.key + "_resp_encrypt", enterprise_internetbank_recharge_resp_encrypt);
		maps.put(ServiceType.ENTERPRISE_INTERNATBANK_RECHARGE.key + "_list_encrypt", enterprise_internetbank_recharge_list_encrypt);
		
	}
	
	/**
	 * 资源文件中文乱码解决
	 *
	 * @param properties
	 * @param key
	 * @param encoding
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月15日
	 */
	public static String getProperty(Properties properties, String key, String encoding){
		String value = properties.getProperty(key);
		
		if (value == null) {
			
			return "";
		}

		try {
			value = new String(value.getBytes("ISO8859-1"), encoding);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		return value;
	}
	
	/**
	 * 获取宜宾银行非必须加签参数key
	 */
	public static String[] getNotNecessaryParams(String key) {
		return maps.get(key + "_notNecessary");
	}
	
	/**
	 * 获取宜宾银行敏感信息加密字段Key
	 */
	public static String[] getEncryptKeys(String key) {
		return maps.get(key + "_encrypt");
	}
	
	public static String[] getListEncryptKeys(String key) {
		return maps.get(key + "_list_encrypt");
	}
	
	public static String[] getList2EncryptKeys(String key) {
		return maps.get(key + "_list2_encrypt");
	}
	
	public static String[] getRespEncryptKeys(String key) {
		return maps.get(key + "_resp_encrypt");
	}
	
	public static String[] getRespListEncryKeys(String key) {
		return maps.get(key + "_resp_list_encrypt");
	}
	public static String[] getRespListKeys(String key) {
		return maps.get(key + "_resp_list");
	}
	public static String[] getRespKeys(String key) {
		return maps.get(key + "_resp");
	}
	
	
	/** 接入系统号标志 */
	public static final String MERCHANT_CODE = "oyph";
	
	public static final String MER_CODE = "oyph";

	public static final String VERSION = "1.0"; //版本号
	
	public static final String ACCTYPE = "00";//卡帐标识（银行卡）
	
	public static final String CURRENCY = "CNY";//人民币
	
	public static final String RESP_SUC_CODE = "P2P0000";
	
	/**
	 * 宜宾银行h5提交地址
	 */
	public static final String YIBIN_H5URL = ybConf.getProperty("yibinbankh5_url");
	public static final String YIBIN_H5_STANDARD_URL = ybConf.getProperty("yibinbankh5standard_url");
	
	/**
	 * 银行卡列表
	 */
	public static final String BANK_LIST = getProperty(ybConf, "bank_list", System.getProperty("file.encoding"));
	
	/**
	 * 平台账户
	 */
	public static final String BANKACCOUNTNO =  ybConf.getProperty("bank_account_no");		//自有资金账户
	
	public static final String COMPENSATORYNO =  ybConf.getProperty("compensatory_no");		//代偿账户
	
	public static final String MARKETNO =  ybConf.getProperty("market_no");					//营销账户
	
	public static final String COSTNO =  ybConf.getProperty("cost_no");						//费用账户
	
	public static final String B_ACCT_NO =  ybConf.getProperty("b_acct_no");				//对公存管户账号
	
	public static final String B_ACCT_NAME =  ybConf.getProperty("b_acct_name");			//对公存管户名称
	
	public static final String B_KEY = ybConf.getProperty("b_key");							//网银充值时的密钥字符串
	
	public static final String RECHARGERBYCPCN = ybConf.getProperty("recharger_by_cpcn");   //网关支付页面
	
	public static final String FRONT_URL = ybConf.getProperty("front_url");					//网银充值后跳转回的页面
}
























