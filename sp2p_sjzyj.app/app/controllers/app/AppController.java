package controllers.app;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import play.Logger;
import service.IntegralMallService;

import com.shove.gateway.GeneralRestGateway;
import com.shove.gateway.GeneralRestGatewayInterface;

import common.constants.AppConstants;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.app.Invest.DebtAction;
import controllers.app.Invest.ExpBidAction;
import controllers.app.Invest.InvestAction;
import controllers.app.aboutUs.AboutUsAction;
import controllers.app.account.AccountAction;
import controllers.app.activity.addRate.AddRateTicketAction;
import controllers.app.activity.cps.CpsAction;
import controllers.app.activity.redPacket.RedPacketAction;
import controllers.app.activity.shake.ShakeActivityAction;
import controllers.app.back.LoginAction;
import controllers.app.back.RiskAction;
import controllers.app.information.InformationAction;
import controllers.app.integralMall.IntegralMallAction;
import controllers.app.onlineVisitor.OnlineVisitorAction;
import controllers.app.proxy.ProxyAction;
import controllers.app.wealth.MyDealAction;
import controllers.app.wealth.MyExpBidAction;
import controllers.app.wealth.MyFundAction;
import controllers.app.wealth.MyInfoAction;
import controllers.app.wealth.MyQuestionAction;
import controllers.app.wealth.MyReceiveBillAction;
import controllers.app.wealth.MySecurityAction;
import controllers.app.wealth.RechargeAWithdrawalAction;
import controllers.common.BaseController;
import controllers.front.account.MyFundCtrl;
import controllers.front.activity.shake.MyBigWheelActivityCtrl;
import groovy.util.Proxy;

public class AppController extends BaseController implements GeneralRestGatewayInterface{

	/**
	 * app端请求服务器入口
	 *
	 * @throws IOException
	 *
	 * @author yaoyi
	 * @createDate 2016年3月30日
	 */
	public static void index() throws IOException{
		StringBuilder errorDescription = new StringBuilder();
		AppController app = new AppController();
		
		int code = GeneralRestGateway.handle(ConfConst.ENCRYPTION_APP_KEY_MD5, 3000, app, errorDescription);
    	
    	if(code < 0) {
    		Logger.error("%s", errorDescription);
    	}
	}
	
	/**
	 * 扫描二维码下载
	 *
	 * @author yaoyi
	 * @createDate 2016年3月30日
	 */
	public static void download(){
		render();
	}
	public static void downloads(){
		render();
	}
	
	/**
	 * ios微信扫码下载提示页面
	 *
	 * @author huangyunsong
	 * @createDate 2016年5月17日
	 */
	public static void iosTip(String path){
		render(path);
	}
	

	
	@Override
	public String delegateHandleRequest(Map<String, String> parameters, StringBuilder errorDescription) throws RuntimeException {
		String result = null;
		long timestamp = new Date().getTime();
		LoggerUtil.info(false, "客户端请求(%s):【%s】",timestamp+"", JSONObject.fromObject(parameters));
		switch(Integer.parseInt(parameters.get("OPT"))){
			case AppConstants.APP_LOGIN:
				try{
					result = AccountAction.logining(parameters);//123
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "用户登录时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			
			case AppConstants.APP_SEND_CODE:
				try{
					result = AccountAction.sendCode(parameters);//111
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "发送短信验证码时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
				
			case AppConstants.APP_REGISTER_PROTOCOL:
				try{
					result = AboutUsAction.registerProtocol(parameters);//112
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "加载注册协议时：%s", e.getMessage());
					result = errorHandling();
				}
				break;			
			case AppConstants.APP_REGISTERING:
				try{
					result = AccountAction.registering(parameters);//113
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "会员注册时：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_PAYACCOUNT_OPEN:
				try{
					result = AccountAction.createAccount(parameters);//114
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "会员开户时：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_REGISTER_PRE:
				try{
					result = AccountAction.registerPre(parameters);//115
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "准备注册时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_USER_BANK_LIST:
				try{
					result = MySecurityAction.listUserBankCard(parameters);//221
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询会员银行卡列表时：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
				
			case AppConstants.APP_BIND_CARD:
				try{
					result = MySecurityAction.bindCard(parameters);//703
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "会员绑卡时：%s", e.getMessage());
					result = errorHandling();
				}
				break;		
			case AppConstants.APP_SET_DEFAULT_BANKCARD:
				try{
					result = MySecurityAction.setDefaultBankCard(parameters);//223
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "设置默认卡时：%s", e.getMessage());
					result = errorHandling();
				}
				break;		
				
			case AppConstants.APP_UPDATE_PWD:
				try{
					result = AccountAction.updateUserPwd(parameters);//122
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "用户更改密码时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_VERIFICATION_CODE:
				try{
					result = AccountAction.verificationCode(parameters);//121
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "验证验证码时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_WITHDRAWAL:
				try{
					result = RechargeAWithdrawalAction.withdrawal(parameters);//214
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "提现时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_WITHDRAWAL_PRE:
				try{
					result = RechargeAWithdrawalAction.withdrawalPre(parameters);//213
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "准备提现时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_WITHDRAWAL_RECORD:
				try{
					result = RechargeAWithdrawalAction.pageOfWithdraw(parameters);//215
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询提现记录时:%s", e.getMessage());
					result = errorHandling();
				}
				break;

			case AppConstants.APP_USER_INFO:
				try{
					result = MyInfoAction.userInfomation(parameters);//251
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询个人基本信息时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_RECHARGE_PRE:
				try{
					result = RechargeAWithdrawalAction.rechargePre(parameters);//216
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "准备充值时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_RECHARGE:
				try{
					result = RechargeAWithdrawalAction.recharge(parameters);//211
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "充值时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_RECHARGE_RECORDS:
				try{
					result = RechargeAWithdrawalAction.pageOfRecharge (parameters);//212
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询充值交易记录时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_MESSAGE:
				try{
					result = MyInfoAction.pageOfUserMessage(parameters);//252
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询消息列表时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_RETUEN_MONEY_PLAN:
				try{
					result = MyReceiveBillAction.pageOfReceiveBill(parameters);//242
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询回款计划时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			/** OPT=231:我的投资接口——我的出借 */	
			case AppConstants.APP_MYINVEST:
				try{
					
					result = MyFundAction.pageOfMyInvestNew(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询我的投资时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_INVEST_BILL:
				try{
					result = MyFundAction.listOfInvestBill(parameters);//232
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询理财账单详情时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_INVEST_BILL_INFO:
				try{
					result = MyFundAction.investBillInfo(parameters);//237
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询理财账单详情时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_LOAN:
				try{
					result = MyFundAction.pageOfMyLoan(parameters);//233
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询我的借款时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_LOAN_BILL:
				try{
					result = MyFundAction.listOfLoanBill(parameters);//234
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询借款账单时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_LOAN_BILL_INFO:
				try{
					result = MyFundAction.findLoanBill(parameters);//238
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询借款账单详情时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_LOAN_REPAYMENT:
				try{
					result = MyFundAction.repaymentCheckPass(parameters);//235
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "还款时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
				/**lihuijun*/
			case AppConstants.APP_LOAN_REPAYMENTALL:
				try{
					result = MyFundAction.repaymentAll(parameters);//235
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "还款时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			
			case AppConstants.APP_BID_PACT:
				try{
					result = MyFundAction.showBidPact(parameters);//236
				
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询理财协议时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_BID_SSQ_PACT:
				try{
					result = MyFundAction.showBidPactSSQ(parameters);//2361
				
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询理财电子签章协议时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_BID_PACT_LEND:
				try{
					result = MyFundAction.showBidPactAll(parameters);//2360
					
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "我的借款合同下拉单时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_DEBT://我的转让
				try{
					result = MyFundAction.pageOfDebt(parameters);//239
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "我的受让:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			/** OPT:2390  我的受让 新!!!(根据标的状态分为未完成和已完成) */
			case AppConstants.APP_DEBT_NEW:
				try{
					result = MyFundAction.pageOfDebtNew(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "我的受让:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_DEBT_PACT://转让协议
				try{
					result = MyFundAction.showDebtPact(parameters);//2312
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "我的受让:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_DEBT_SSQ_PACT://我的转让我的受让电子签章合同
				try{
					result = MyFundAction.showDebtPactSSQ(parameters);//2362
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "我的转让和我的受让电子签章协议时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_APPLAY_DEBT_PRE:
				try{
					result = MyFundAction.applyDebtPre(parameters);//2313
				}catch(Exception e){
					LoggerUtil.error(true, "债权申请准备:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_APPLAY_DEBT:
				try{
					result = MyFundAction.applyDebtTransfer(parameters);//2314
				}catch(Exception e){
					LoggerUtil.error(true, "债权申请:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_USER_DEAL_RECORD:
				try{
					result = MyDealAction.pageOfUserDealRecords(parameters);//241
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询交易记录时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_USER_DETAIL:
				try{
					result = MyInfoAction.toUserInfo(parameters);//2531
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询用户信息时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_USER_INFO_DETAIL:
				try{
					result = MyInfoAction.toUserInfoTwo(parameters);//253
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询用户信息时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_USER_INFO_UPDATE:
				try{
					result = MyInfoAction.updateUserInfo(parameters);//254
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "保存用户信息时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_USER_PHOTO_UPDATE:
				try{
					result = MyInfoAction.updatePhoto(parameters);//255
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "保存用户信息时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_SECURITY:
				try{
					result = MySecurityAction.userSecurity(parameters);//261
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询安全中心时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_UPDATE_PWDBYOLD:
				try{
					result = MySecurityAction.userUpdatePwdbyold(parameters);//262
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "通过原密码更新密码时:%s", e.getMessage());
					result = errorHandling();
				}
				break;			
			case AppConstants.APP_UPDATE_EMAIL:
				try{
					result = MySecurityAction.updateEmail(parameters);//263
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "修改邮箱时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_EXP_BID_MYACCOUNT:
				try{
					result = MyExpBidAction.myExpBidAccount(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询体验金账户信息时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_EXP_BID_MYINVEST:
				try{
					result = MyExpBidAction.pageOfMyExpBidInvest(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询我的体验标投资记录时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_EXP_BID_GOLD_GET:
				try{
					result = MyExpBidAction.getExperienceGold(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "领取体验金时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_EXP_BID_INCOME_CONVERSION:
				try{
					result = MyExpBidAction.applayConversion(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "兑换体验金收益时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_INVEST_BIDS:
				try{
					result = InvestAction.pageOfInvestBids(parameters);//311
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询理财产品列表时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_INVEST_BID_INFORMATION:
				try{
					result = InvestAction.investBidInformation(parameters);//312
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询理财标详情时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_INVEST:
				try{
					result = InvestAction.investCheckPass(parameters);//321
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "投标时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_INVEST_BIDS_DETAILS:
				try{
					result = InvestAction.investBidDeatils(parameters);//322
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询借款标详情时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_INVEST_BIDS_REPAYMENT_PLAN:
				try{
					result = InvestAction.listOfRepaymentBill(parameters);//323
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询标回款计划时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
				
			case AppConstants.APP_INVEST_BIDS_RECORDS:
				try{
					result = InvestAction.investBidsRecord(parameters);//324
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询投标记录时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_DEBTS:
				try{
					result = DebtAction.pageOfDebts(parameters);//331
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "分页查询债权时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_DEBT_DETAIL:
				try{
					result = DebtAction.debtDetail(parameters);//332
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询债权转让信息详情:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_DEBT_BILLS:
				try{
					result = DebtAction.paymentsOfDebt(parameters);//333
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询债权回款计划:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_BUY_DEBT:
				try{
					result = DebtAction.buyDebt(parameters);//334
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "购买债权:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_EXP_BID:
				try{
					result = ExpBidAction.experienceBid(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询体验标信息:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_EXP_BID_DETATIL:
				try{
					result = ExpBidAction.experienceBidDetail(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询体验标借款详情:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_EXP_BID_INVEST_RECORD:
				try{
					result = ExpBidAction.expBidInvestRecord(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询体验标投资记录:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_EXP_BID_INVEST:
				try{
					result = ExpBidAction.investExpBid(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "购买体验标:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_COMPANY_INFO:
				try{
					result = AboutUsAction.aboutUs(parameters);//411
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询公司介绍时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_CONTACT_US:
				try{
					result = AboutUsAction.contactUs(parameters);//421
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询联系我们时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_PLATFORM_ICON:
				try{
					result = AccountAction.findPlatformIconFilename(parameters);//124
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询平台logo时：%s", e.getMessage());
					result = errorHandling();
				}
				break;		
				
			case AppConstants.APP_VERSION:
				try{
					result = AboutUsAction.getPlatformInfo(parameters);//423
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP版本时：%s", e.getMessage());
					result = errorHandling();
				}
				break;				
			case AppConstants.APP_INDEX:
				try{
					result = HomeAction.index();
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP首页时：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
				
			case AppConstants.APP_MALL:
				try{
					result = HomeAction.Mallindex(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询积分商城首页时：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_MALL_DETAILS:
				try{
					result = HomeAction.mallDetails(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询积分产品详情：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_RECEIPT_ADDRESS:
				try{
					result = HomeAction.receiptAddres(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询收获地址：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_EXCHANGE_GOODS:
				try{
					result = HomeAction.exchangeGoods(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "兑换商品：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_UPDATE_ADRESS:
				try{
					result = HomeAction.updateAdress(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "修改添加地址：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_INTEGRAL_RECORD:
				try{
					result = HomeAction.integralRecord(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "积分记录：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_ALREADY_CONVERTIBLE:
				try{
					result = HomeAction.alreadyConvertible(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "已兑换商品：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_HF_LOGIN:
				try{
					result = HomeAction.hfLogin(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "登录汇付：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_SIGN:
				try{
					result = HomeAction.sign(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "商城签到：%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.APP_MALL_TYPE_GOODS:
				try{
					result = IntegralMallAction.findGoodsByTypeId(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询某一类商品：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			/**后台*/
			case AppConstants.APP_BACK_LOGIN:
				try {
					result = LoginAction.backLogin(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "登录时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_RISK_TO_CREATE:
				try {
					result = RiskAction.toCreateRiskReport();
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "查询分公司与风控专员：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_RISK_CREATE:
				try {
					result = RiskAction.CreateRiskReport(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "生成风控报告：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_RISK_REPORT_LIST:
				try {
					result = RiskAction.queryRiskReportList(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "查询风控报告列表：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_RISK_REPORT_DETAIL:
				try {
					result = RiskAction.queryRiskReportDetail(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "查询风控报告详情：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_RISK_REPORT_AUDIT:
				try {
					result = RiskAction.AuditRiskReport(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "审核风控报告：%s", e.getMessage());
					result = errorHandling();
				
				}
				break;
			/**设置交易密码、修改、重置*/
			case AppConstants.SET_USER_PASSWORD:
				try {
					result = MySecurityAction.setUserPassword(parameters);//700
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "设置交易密码时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.AMEND_USER_PASSWORD:
				try {
					result = MySecurityAction.amendUserPassword(parameters);//701
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "修改交易密码时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.RESET_USER_PASSWORD:
				try {
					result = MySecurityAction.resetUserPassword(parameters);//702
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "重置交易密码时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.CHECK_PASSWORD:
				try {
					result = MySecurityAction.bindCardCheckPassword(parameters);//222
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "绑卡前验密时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.TO_CARD_PAGE:
				try {
					result = MySecurityAction.toCardPage(parameters);//704
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "到绑卡页面：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_TO_SET_ENTRUST:
				try {
					result = AccountAction.toSetAutoInvest(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "进入委托投标设置时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_OPEN_AUTO_INVEST:
				try {
					result = AccountAction.openAutoInvest(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "开启自动投标委托时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_CLOSE_AUTO_INVEST:
				try {
					result = AccountAction.closeAutoInvest(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "关闭自动投标委托时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_SEE_ENTRUST_PROTOCOL:
				try {
					result = AccountAction.seeEntrustProtocol(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "查看自动投标委托协议时：%s", e.getMessage());
					result = errorHandling();
				}
				
				break;
			case AppConstants.SAVE_QUESTION:
				try {
					result = MyQuestionAction.saveQuestion(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "提交问题时：%s", e.getMessage());
					result = errorHandling();
				}
				
				break;
			case AppConstants.PAGE_OF_QUESITON:
				try {
					result = MyQuestionAction.pageOfQuestion(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "分页显示问题列表时：%s", e.getMessage());
					result = errorHandling();
				}
				
				break;
			case AppConstants.QUESTION_DETAILS:
				try {
					result = MyQuestionAction.questionDetails(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "查看问题详情时：%s", e.getMessage());
					result = errorHandling();
				}
				
				break;
			case AppConstants.AUDIT_IMG:
				try{
					result = InvestAction.auditImgDeatils(parameters);//335
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询借款标审核详情图片:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.SHAKE_ACTIVITY:
				try{
					result = ShakeActivityAction.listOfShakeActivity(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "摇一摇活动查询时:%s", e.getMessage());
					result = errorHandling();
				}
				break;	
			case AppConstants.SHAKE:
				try{
					result = ShakeActivityAction.shake(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "摇一摇时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.RED_PACKET_ACTIVITY:
				try{
					result = RedPacketAction.showMyRedpacket(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查看红包账户信息时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.ADD_RATE_TICKET_LIST:
				try{
					result = AddRateTicketAction.pageOfAddRateTicket(parameters);
					
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询加息券列表时:%s", e.getMessage());
					
					result = errorHandling();
				}
				break;
			case AppConstants.SHAKE_MY_RECORED:
				try{

					result = ShakeActivityAction.pageOfMyShakeRecord(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "我的奖励-查询年会摇一摇个人中奖信息列表时:%s", e.getMessage());
					
					result = errorHandling();
				}
				break;
			case AppConstants.INVEST_ADD_RATE_TICKET:
				try{
					result = AddRateTicketAction.listOfInvestUseAddRate(parameters);
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "投标查询加息卷列表时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_INFORMATION:
				try {
					result = InformationAction.listOfInformations(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "资讯列表:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_INFORMATION_DETAILS:
				try {
					result = InformationAction.informationDetials(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "资讯详情:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_INVEST_RED_PACKET:
				try {
					result = RedPacketAction.showInvestRedpacket(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "查询投标时可用红包列表时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_RISK_ASSESSMENT:
				try {
					result = AccountAction.riskAssessment(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "风险评估测评时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_PROXY_OR_SALEMAN_LOGIN:
				try {
					result = ProxyAction.proxyOrSaleManLogining(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "代理商或者业务员登录时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_PROXY_HOME:
				try {
					result = ProxyAction.proxyHome(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "代理商主页数据查询时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_PROXY_RULE:
				try {
					result = ProxyAction.proxyRule(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "代理商规则查询时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_SALEMAN_RULE:
				try {
					result = ProxyAction.saleManRule(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "业务员规则查询时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_LIST_OF_SALEMAN:
				try {
					result = ProxyAction.listOfSaleMan(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "业务员列表查询时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_SALEMAN_INFO:
				try {
					result = ProxyAction.saleManInfo(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "业务员列表查询时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_LIST_OF_SALEMAN_USER:
				try {
					result = ProxyAction.saleManUsers(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "业务员名下会员查询时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_ADD_SALEMAN:
				try {
					result = ProxyAction.addSaleMan(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "添加业务员时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_PROXY_UPDATE_PWD:
				try {
					result = ProxyAction.updatePwd(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "更改密码时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_SALEMAN_PROFIT:
				try {
					result = ProxyAction.saleManProfit(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "业务员提成查询时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_SALEMAN_EDIT:
				try {
					result = ProxyAction.saleManEdit(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "业务员编辑时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_SALEMAN_SENDCODE:
				try {
					result = ProxyAction.sendCode(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "忘记密码发送短信时:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case AppConstants.APP_SALEMAN_FORGETPWD:
				try {
					result = ProxyAction.updateUserPwd(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "忘记密码:%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case 426:
				try{
					result = ProxyAction.getPlatformInfo(parameters);//426
				}catch(Exception e){
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP(代理商)版本时：%s", e.getMessage());
					result = errorHandling();
				}
				break;
			case 900:
				try {
					result = CpsAction.pageOfCpsSpreadRecord(parameters);//900
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "查询APP(CPS推广记录): %s", e.getMessage());
				}
				break;
			case 901:
				try {
					result = CpsAction.querySpreadMobileByUserId(parameters);//901
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "获取CPS推广链接加密手机号: %s", e.getMessage());
				}
				break;
			case AppConstants.APP_CPS_AWARD_RECORD:
				try {
					result = CpsAction.queryAwardRecord(parameters);//902
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "获取CPS推广活动中奖记录: %s", e.getMessage());
				}
				break;
			case AppConstants.APP_EXCHANGE_VIRTUAL_GOODS://521
				try {
					result = HomeAction.exchangeVirtualGoods(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(true, "兑换红包加息券: %s", e.getMessage());
				}
				break;
			case AppConstants.APP_ONLINE_VISITOR_CARD://910
				try {
					result = OnlineVisitorAction.visitorChatDataPoint(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(false,"在线访客名片埋点url: %s", e.getMessage());
				}
				break;
				
		    /** app大转盘分享朋友圈 */
			case AppConstants.APP_SHARE_NUM://427 朋友圈分享
				try {
					result = AccountAction.shareNum(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(false,"朋友圈分享: %s", e.getMessage());
				}
				break;
			case AppConstants.APP_URl://903 
				try {
					result = MyBigWheelActivityCtrl.appGetUrl(parameters);
				} catch (Exception e) {
					e.printStackTrace();
					LoggerUtil.error(false,"获取URL: %s", e.getMessage());
				}
				break;
		}
		//LoggerUtil.info(false, "客户端请求(%s):【%s】",timestamp+"", JSONObject.fromObject(parameters));
		LoggerUtil.info(false, "服务器响应(%s):【%s】",timestamp+"", result);
		return result;
	}
	
	public static void index2(){
		/*
		AppConstants.APP_APPLAY_DEBT_PRE债权转让申请准备
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_APPLAY_DEBT_PRE+"");
		String signID = Security.addSign(358L, Constants.INVEST_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("investId", signID);
		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		
		/*
		//AppConstants.APP_APPLAY_DEBT债权转让申请
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_APPLAY_DEBT+"");
		String signID = Security.addSign(358L, Constants.INVEST_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("investId", signID);
		parameters.put("title", "358转让啦");
		parameters.put("period", "2");
		parameters.put("transferPrice", "4500");
		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		/*
		//AppConstants.APP_MYINVEST我的投资列表
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_MYINVEST+"");
		String signID = Security.addSign(189L, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("userId", signID);
		parameters.put("currPage", "1");
		parameters.put("pageSize", "5");
		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		/*
		//AppConstants.APP_LOAN我的投资列表
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_LOAN+"");
		String signID = Security.addSign(188L, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("userId", signID);
		parameters.put("currPage", "1");
		parameters.put("pageSize", "5");
		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		
		/*
		//AppConstants.APP_INDEBT我的受让
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_DEBT+"");
		String signID = Security.addSign(189L, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("userId", signID);
		parameters.put("debtOf", "1");
		parameters.put("currPage", "1");
		parameters.put("pageSize", "5");
		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		/*
		//AppConstants.APP_DEBT_PACT转让协议
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_DEBT_PACT+"");
		String signID = Security.addSign(37L, Constants.DEBT_TRANSFER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("debtId", signID);

		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		
		/*//AppConstants.APP_DEBTS债权转让列表
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_DEBTS+"");
		String signID = Security.addSign(37L, Constants.DEBT_TRANSFER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("currPage", "1");
		parameters.put("pageSize", "5");

		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
	/*	
		//AppConstants.APP_DEBT_DETAIL债权转让详细信息
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_DEBT_DETAIL+"");
		String signID = Security.addSign(37L, Constants.DEBT_TRANSFER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("debtId", signID);

		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		
		*/
		
		/*//AppConstants.APP_DEBT_DETAIL债权回款计划
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_DEBT_BILLS+"");
		String signID = Security.addSign(357L, Constants.INVEST_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		parameters.put("investId", signID);

		
		String result = new AppController().delegateHandleRequest(parameters, null);
		System.out.println(result);
		renderJSON(result);
		*/
		//AppConstants.APP_BUY_DEBT购买债权
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("OPT", AppConstants.APP_BUY_DEBT+"");
		String debtId = Security.addSign(55L, Constants.DEBT_TRANSFER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		String userId = Security.addSign(188L, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);//17700000001
		parameters.put("debtId", debtId);
		parameters.put("userId", userId);
		parameters.put("deviceType", "2");
		
		String result = new AppController().delegateHandleRequest(parameters, null);
		renderJSON(result);
	}
	
	/***
	 * 
	 * 程序异常 信息统一提示
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-25
	 */
	private String errorHandling(){
		JSONObject json = new JSONObject();
		
		json.put("code", ResultInfo.ERROR_500);
		json.put("msg", "系统繁忙，请稍后再试");
		
		return json.toString();
	}
}
