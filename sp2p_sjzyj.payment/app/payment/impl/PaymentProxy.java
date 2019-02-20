package payment.impl;

import java.util.List;
import java.util.Map;

import common.constants.ConfConst;
import common.constants.PaymentConst;
import common.utils.ResultInfo;
import models.common.entity.t_conversion_user;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import payment.IPayment;
import play.Logger;
import yb.YbConsts;
import yb.YbPayment;
import yb.enums.EntrustFlag;
import yb.enums.ProjectStatus;
import yb.enums.ServiceType;

/**
 * 接口代理
 * 
 * @author niu
 * @create 2017.08.23
 */
public class PaymentProxy implements IPayment{
	
	private IPayment payment = null;
	private static PaymentProxy proxy = new PaymentProxy();
	
	/**
	 * 私有构造方法
	 */
	private PaymentProxy() {
	}
	
	/**
	 * 生成代理对象
	 */
	public static PaymentProxy getInstance() {
		return proxy;
	}
	
	/**
	 * 托管网关初始化。项目启动时，需要调用此方法
	 * 
	 * @author niu
	 * @create 2017.08.23
	 */
	public void init() {
		
		if (!ConfConst.IS_TRUST) {  //普通网关
			return;
		}
		
		Logger.info("#### 支付网关初始化 #### 开始 ####");
		
		if (ConfConst.CURRENT_TRUST_TYPE.equals(PaymentConst.TRUST_TYPE)) {
			Logger.info("#### 宜宾 #### 支付网关 #### 加载中 ####");
			
			payment = new YbPayment();
			YbConsts.initSupport();
		}
		
		Logger.info("#### 支付网关初始化 #### 完毕 ####");
	}
	
	
	
	

	//个人开户
	@Override
	public ResultInfo personCustomerRegist(int client, String businessSeqNo, long userId, String username, String certNo,
			String startTime, String endTime,String jobType,String job, String national,
			String postcode, String address, Object... objects) {
		
		return payment.personCustomerRegist(client, businessSeqNo, userId,username, certNo,
					startTime, endTime, jobType, job, national, postcode, address, objects);
	}
	
	//企业开户 
	@Override
	public ResultInfo enterpriseCustomerRegist(int client, String businessSeqNo, long userId, String companyName,
			String uniSocCreCode, String entType, String establish, String companyId,
			String licenseAddress, String bankNo, String bankName, String userName, String idCard, Object... objects) {
		
		return payment.enterpriseCustomerRegist(client, businessSeqNo, userId,companyName,uniSocCreCode,
				entType, establish, companyId, licenseAddress, bankNo, bankName,userName,idCard, objects);
	}
		
	//充值校验密码
	public ResultInfo rechargeCheckPassword(int client, String businessSeqNo, long userId, double rechargeAmt,String bankNo, Object... objects){
		
		return payment.rechargeCheckPassword(client, businessSeqNo, userId,rechargeAmt, bankNo, objects);
	}
	
	//充值
	@Override
	public ResultInfo recharge(int client, String businessSeqNo, long userId, double rechargeAmt,String bankNo, Object... obj) {
		
		return payment.recharge(client, businessSeqNo, userId, rechargeAmt,bankNo, obj);
	}

	//提现校验密码
	public ResultInfo withdrawalCheckPassword(int client, String businessSeqNo, long userId, double rechargeAmt,String bankAccount,
			ServiceType serviceType, double withdrawalFee, Object... objects){	
		return payment.withdrawalCheckPassword(client, businessSeqNo, userId,rechargeAmt, bankAccount, serviceType,withdrawalFee, objects);
	}
	
	//提现
	@Override
	public ResultInfo withdrawal(int client, String businessSeqNo, long userId, double withdrawalAmt, String bankAccount,double withdrawalFee, Object... obj) {
		
		return payment.withdrawal(client, businessSeqNo, userId, withdrawalAmt, bankAccount, withdrawalFee, obj);
	}
	
	
	//企业提现
	@Override
	public ResultInfo enterpriseWithdrawal(int client, String businessSeqNo, long userId, double withdrawalAmt, String bankAccount,double withdrawalFee, Object... obj) {
		
		return payment.enterpriseWithdrawal(client, businessSeqNo, userId, withdrawalAmt, bankAccount, withdrawalFee, obj);
	}
	//正常放款
	@Override
	public ResultInfo bidSuditSucc(int client, String serviceOrderNo, long releaseSupervisorId, t_bid bid, Object... obj) {
		
		return payment.bidSuditSucc(client, serviceOrderNo, releaseSupervisorId, bid, obj);
	}
	
	@Override
	public String getInterfaceDes(int code) {	
		
		return payment.getInterfaceDes(code);
	}

	@Override
	public int getInterfaceType(Enum interfaceType) {
		
		return payment.getInterfaceType(interfaceType);
	}



	

	@Override
	public ResultInfo userBindCard(int client, String serviceOrderNo, long userId, String name, String idNumber, String mobile, Object... obj) {
		
		return payment.userBindCard(client, serviceOrderNo, userId, name, idNumber, mobile, obj);
	}

	@Override
	public ResultInfo queryBindedBankCard(int client, long userId, String serviceOrderNo,int serviceType, Object... obj) {
		
		return payment.queryBindedBankCard(client, userId, serviceOrderNo, serviceType,obj);
	}

	@Override
	public ResultInfo regist(int client, String serviceOrderNo, long userId, Object... obj) {
		
		return payment.regist(client, serviceOrderNo, userId, obj);
	}

	@Override
	public ResultInfo invest(int client, int investType, String serviceOrderNo, long userId, t_bid bid, double investAmt, Object... obj) {
		
		return payment.invest(client, investType, serviceOrderNo, userId, bid, investAmt, obj);
	}

	@Override
	public ResultInfo repayment(int client, String serviceOrderNo, t_bill bill, List<Map<String, Double>> billInvestFeeList, Object... obj) {
		
		return payment.repayment(client, serviceOrderNo, bill, billInvestFeeList, obj);
	}

	@Override
	public ResultInfo advanceRepayment(int client, String serviceOrderNo, t_bill bill, Object... obj) {
		return payment.advanceRepayment(client, serviceOrderNo, bill, obj);
	}

	@Override
	public ResultInfo repaymentAll(int client, String serviceOrderNo, t_bill bill, List<Map<String, Double>> billInvestFeeList, Object... obj) {		
		return payment.repaymentAll(client, serviceOrderNo, bill, billInvestFeeList, obj);
	}

	@Override
	public ResultInfo debtTransfer(int clint, String serviceOrderNo, Long debtId, Long userId) {		
		return payment.debtTransfer(clint, serviceOrderNo, debtId, userId);
	}

	@Override
	public ResultInfo login(int client, long userId, Object... obj) {
		
		return payment.login(client, userId, obj);
	}

	@Override
	public ResultInfo autoInvest(int client, int investType, String serviceOrderNo, long userId, t_bid bid, double investAmt, Object... obj) {
		
		return payment.autoInvest(client, investType, serviceOrderNo, userId, bid, investAmt, obj);
	}


	@Override
	public ResultInfo bidCreate(int client, String serviceOrderNo, t_bid bid, int bidCreateFrom, Object... obj) {
		
		return payment.bidCreate(client, serviceOrderNo, bid, bidCreateFrom, obj);
	}

	


	@Override
	public ResultInfo queryMerchantBalance(int client, Object... obj) {
		
		return payment.queryMerchantBalance(client, obj);
	}

	@Override
	public ResultInfo conversion(int client, String serviceOrderNo, t_conversion_user conversion, Object... obj) {
		
		return payment.conversion(client, serviceOrderNo, conversion, obj);
	}

	@Override
	public ResultInfo advance(int client, String serviceOrderNo, t_bill bill, List<Map<String, Double>> billInvestFeeList, Object... obj) {
		
		return payment.advance(client, serviceOrderNo, bill, billInvestFeeList, obj);
	}

	@Override
	public ResultInfo advanceReceive(int client, String serviceOrderNo, t_bill bill, List<Map<String, Double>> billInvestFeeList, Object... obj) {
		
		return payment.advanceReceive(client, serviceOrderNo, bill, billInvestFeeList, obj);
	}

	/**
	 * 设置交易密码
	 * 
	 * @author liu
	 * @create 2017.09.07
	 */
	@Override
	public ResultInfo setUserPassword(int client, String businessSeqNo, long userId, Object... objects){
		
		return payment.setUserPassword(client, businessSeqNo, userId, objects);
	}
	
	/**
	 * 修改交易密码
	 * 
	 * @author liu
	 * @create 2017.09.08
	 */
	@Override
	public ResultInfo amendUserPassword(int client, String businessSeqNo, long userId, Object... objects){
		
		return payment.amendUserPassword(client, businessSeqNo, userId, objects);
	}
	
	/**
	 * 重置交易密码
	 * 
	 * @author liu
	 * @create 2017.09.08
	 */
	@Override
	public ResultInfo resetUserPassword(int client, String businessSeqNo, long userId, Object... objects){
		
		return payment.resetUserPassword(client, businessSeqNo, userId, objects);
	}
	
	/**
	 *校验交易密码
	 * 
	 * @author liu
	 * @create 2017.09.13
	 */
	@Override
	public ResultInfo checkPassword(int client, String businessSeqNo, long userId, Object... objects){
		
		return payment.checkPassword(client, businessSeqNo, userId, objects);
	}
	
	/**
	 *托管账户资金信息查询
	 * 
	 * @author liu
	 * @create 2017.09.14
	 */
	@Override
	public ResultInfo queryFundInfo(int client, String account, String businessSeqNo) {
		
		return payment.queryFundInfo(client, account, businessSeqNo);
	}
	
	/**
	 * 代偿账户资金信息查询
	 *
	 * @param client 客户端
	 * @param businessSeqNo 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	@Override
	public ResultInfo queryCompensatoryBalance(int client, String businessSeqNo){
		
		return payment.queryCompensatoryBalance(client, businessSeqNo);
	}
	
	/**
	 * 担保账户资金信息查询
	 *
	 * @param client 客户端
	 * @param businessSeqNo
	 * @param paymentAccount  
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	public ResultInfo queryGuaranteeBalance(int client, String businessSeqNo, long paymentAccount){
		
		return payment.queryGuaranteeBalance(client, businessSeqNo, paymentAccount);
	}
	
	/**
	 * 营销账户资金信息查询
	 *
	 * @param client 客户端
	 * @param businessSeqNo 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	@Override
	public ResultInfo queryMarketBalance(int client, String businessSeqNo) {
		
		return payment.queryMarketBalance(client, businessSeqNo);
	}
	
	/**
	 * 费用账户资金信息查询
	 *
	 * @param client 客户端
	 * @param businessSeqNo 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	@Override
	public ResultInfo queryCostBalance(int client, String businessSeqNo) {
		
		return payment.queryCostBalance(client, businessSeqNo);
	}
	
	/**
	 * 台账平台充值
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 
	 * @param rechargeAmt 
	 * @param flag 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月20日
	 */
	@Override
	public ResultInfo merchantRecharge(int client, String serviceOrderNo, double rechargeAmt, int flag){
		
		return payment.merchantRecharge(client, serviceOrderNo, rechargeAmt, flag);
	}
	
	/**
	 * 台账平台取现
	 *
	 * @param client 客户端
	 * @param serviceOrderNo 
	 * @param rechargeAmt 
	 * @param flag 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年9月21日
	 */
	public ResultInfo merchantWithdrawal(int client, String serviceOrderNo, double rechargeAmt, int flag){
		
		return payment.merchantWithdrawal(client, serviceOrderNo, rechargeAmt, flag);
	}
	
	/**
	 * 后台代偿线下收款
	 * 
	 * @author liu
	 * @create 2017.10.12
	 */
	public ResultInfo offlineReceive(String clientSn, String businessSeqNo, t_bid bid, ServiceType serviceType, EntrustFlag entrustFlag, List<Map<String, Object>> accountList, List<Map<String, Object>> contractList, Object... objects) {
		
		return payment.offlineReceive(clientSn, businessSeqNo, bid, serviceType, entrustFlag, accountList, contractList, objects);
	}
	
	/**
	 * 校验交易密码(网银充值)
	 * 
	 * @author liuyang
	 * @create 2017年10月23日
	 */
	public ResultInfo ebankRechargeCheckPassword(int client, String businessSeqNo, long userId,double rechargeAmt, int flags){
		
		return payment.ebankRechargeCheckPassword(client, businessSeqNo, userId, rechargeAmt, flags);
	}
	
	/**
	 * 校验交易密码(绑定银行卡)
	 * 
	 * @author liuyang
	 * @create 2018年1月3日
	 */
	public ResultInfo bindCardCheckPassword(int client, String businessSeqNo, long userId){
		
		return payment.bindCardCheckPassword(client, businessSeqNo, userId);
	}
	
	/**
	 * 校验交易密码(担保人还款)
	 * 
	 * @author liuyang
	 * @create 2018年1月3日
	 */
	public ResultInfo guaranteeCheckPassword(int client, String businessSeqNo, long userId, String userName, double amount, String backUrl, String billSign){
		
		return payment.guaranteeCheckPassword(client, businessSeqNo, userId, userName, amount, backUrl, billSign);
	}
	
	/**
	 * 客户网银充值
	 * 
	 * @author liuyang
	 * @create 2017年10月23日
	 */
	public ResultInfo ebankRecharge(int client, String businessSeqNo, long userId, double rechargeAmt){
		
		return payment.ebankRecharge(client, businessSeqNo, userId, rechargeAmt);
	}
	
	/**
	 * 企业网银充值
	 * 
	 * @author liuyang
	 * @create 2017年12月18日
	 */
	public ResultInfo enterpriseEbankRecharge(int client, String businessSeqNo, long userId, double rechargeAmt, int flags){
		
		return payment.enterpriseEbankRecharge(client, businessSeqNo, userId, rechargeAmt, flags);
	}
	
	
	public ResultInfo userBindCard(int client, String serviceOrderNo, long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	
	

	

	
	

	@Override
	public ResultInfo bidInfoAysn(String clientSn, String businessSeqNo, t_bid bid, yb.enums.ServiceType serviceType, ProjectStatus projectStatus, List<Map<String, Object>> returnInfoList, Object... objects) {
		return payment.bidInfoAysn(clientSn, businessSeqNo, bid, serviceType, projectStatus, returnInfoList, objects);
	}
	
	@Override
	public ResultInfo fundTrade(String clientSn, long userId, String businessSeqNo, t_bid bid, yb.enums.ServiceType serviceType, EntrustFlag entrustFlag, List<Map<String, Object>> accountList, List<Map<String, Object>> contractList, Object... objects) {
		return payment.fundTrade(clientSn, userId, businessSeqNo, bid, serviceType, entrustFlag, accountList, contractList, objects);
	}

	@Override
	public ResultInfo checkTradePass(int client, String businessSeqNo, long userId, String actionURL, String userName, ServiceType serviceType, double amount, Map<String, String> serviceRequestParams, ServiceType checkPassType, int type, String cardNo, String authtime, String authority, Object... objects) {
		return payment.checkTradePass(client, businessSeqNo, userId, actionURL, userName,serviceType, amount, serviceRequestParams, checkPassType, type, cardNo, authtime, authority, objects);
	}

	@Override
	public ResultInfo queryTradeStatus(String clientSn, String businessSeqNo, String oldbusinessSeqNo, String operType, Object... objects) {
		return payment.queryTradeStatus(clientSn, businessSeqNo, oldbusinessSeqNo, operType, objects);
	}

	@Override
	public ResultInfo dataInfoQuery(String clientSn, String customerId, String accountNo, ServiceType serviceType, Object... objects) {
		return payment.dataInfoQuery(clientSn, customerId, accountNo, serviceType, objects);
	}


	@Override
	public ResultInfo custInfoAysn(Map<String, String> inBody, String clientSn, ServiceType serviceType, Object... objects) {
		return payment.custInfoAysn(inBody, clientSn, serviceType, objects);
	}
	
	
	
	

	@Override
	public ResultInfo autoCheckPassword(int client, String businessSeqNo,
			long userId, ServiceType serviceType,double min_apr, int max_period, double invest_amt, String valid_time,int valid_day, Object... objects) {
		// TODO Auto-generated method stub
		return payment.autoCheckPassword(client, businessSeqNo, userId, serviceType,min_apr,max_period,invest_amt, valid_time, valid_day, objects);
	}

	@Override
	public ResultInfo autoInvestSignature(int client, String serviceOrderNo,
			long userId, ServiceType serviceType, double invest_amt, String valid_time, Object... obj) {
		// TODO Auto-generated method stub
		return payment.autoInvestSignature(client, serviceOrderNo, userId, serviceType, invest_amt, valid_time, obj);
	}

	public ResultInfo queryBindedBankCard(int code, long userId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 绑定银行卡(银行卡预留手机号)
	 * 
	 * @author guoShiJie
	 * @create 2018.04.16
	 */
	@Override
	public ResultInfo bindCardCheckPassword1(int client, String businessSeqNo, long userId,String mobilePhone){
		return payment.bindCardCheckPassword1(client, businessSeqNo, userId, mobilePhone);
	}
	
	/**
	 * 受让冲正数据拼接
	 * 
	 * @param businessSeqNo 业务流水号
	 * @param clientSn 托管请求唯一标识
	 * @param debitAccountNo 受让方台账账号
	 * @param financierAccountNo 转让方台账账号
	 * @param financierFee 交易金额
	 * @param oldbusinessSeqNo 原业务流水号
	 *
	 * @author liuyang
	 * @createDate 2018年8月9日
	 */
	public ResultInfo reversalTransfer(String businessSeqNo, String clientSn, String debitAccountNo,
			String financierAccountNo, double financierFee, String oldbusinessSeqNo, String bidNo){
		return payment.reversalTransfer(businessSeqNo, clientSn, debitAccountNo, financierAccountNo, financierFee, oldbusinessSeqNo, bidNo);
	}
}



























