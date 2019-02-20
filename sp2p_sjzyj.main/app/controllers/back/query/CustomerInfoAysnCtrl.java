package controllers.back.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bouncycastle.jce.provider.JDKDSASigner.stdDSA;
import org.junit.runner.Result;

import com.shove.Convert;

import common.constants.ConfConst;
import common.enums.Client;
import common.utils.EncryptUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;
import controllers.common.BaseController;
import controllers.front.invest.InvestCtrl;
import models.common.bean.CurrUser;
import models.common.entity.t_bank_card_user;
import models.common.entity.t_user_info;
import models.core.entity.t_bid;
import models.ext.redpacket.entity.t_red_packet_user;
import payment.impl.PaymentProxy;
import services.common.BankCardUserService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.core.BidService;
import services.ext.redpacket.RedpacketService;
import sun.net.www.content.text.plain;
import yb.YbConsts;
import yb.YbPaymentCallBackService;
import yb.YbUtils;
import yb.enums.EntrustFlag;
import yb.enums.ServiceType;

/**
 * 后台-查询-客户信息同步
 * 
 * 
 * @author niu
 * @create 2017.10.14
 */
public class CustomerInfoAysnCtrl extends BackBaseController {
	
	private static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	private static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);
	private static YbPaymentCallBackService callBackService = Factory.getSimpleService(YbPaymentCallBackService.class);
	protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);
	protected static BidService bidService = Factory.getService(BidService.class);
	/**
	 * 客户信息同步-准备
	 * 
	 * @author niu
	 * @create 2017.10.14
	 */
	public static void custInfoSyncPre() {
		
		render();
	}
	
	/**
	 * 客户信息同步-操作
	 * 
	 * @author niu
	 * @create 2017.10.14
	 */
	public static void custInfoSync(String customerId, String busiTradeType, String ctype, String crole, String companyName, String username, 
			String certType, String certNo, String certFImage, String certBImage, String certInfo, String idvalidDate, String idexpiryDate, 
			String jobType, String postcode, String address, String national, String completeFlag, String phoneNo, String uniSocCreCode, 
			String uniSocCreDir, String bizLicDomicile, String entType, String dateOfEst, String corpacc, String corpAccBankNo, String corpAccBankNm, 
			String bindFlag, String bindType, String acctype, String oldbankAccountNo, String bankAccountNo, String bankAccountName, 
			String bankAccountTelNo, String note, String job, String debitNo, String creditNo, String amount, String bidId, String oldbusinessSeqNo) {
		
		ServiceType serviceType = ServiceType.getEnumByKey(busiTradeType);
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(serviceType);
		
		
		Map<String, String> inBody = new HashMap<String, String>();
		
		inBody.put("customerId", customerId);
		inBody.put("businessSeqNo", businessSeqNo);
		inBody.put("busiTradeType", busiTradeType);
		inBody.put("ctype", ctype);
		inBody.put("crole", crole);
		inBody.put("companyName", companyName);
		inBody.put("username", username);
		inBody.put("certType", certType);
		inBody.put("certNo", certNo);
		inBody.put("certFImage", certFImage);
		inBody.put("certBImage", certBImage);
		inBody.put("certInfo", certInfo);
		inBody.put("idvalidDate", idvalidDate);
		inBody.put("idexpiryDate", idexpiryDate);
		inBody.put("job", job);
		inBody.put("jobType", jobType);
		inBody.put("postcode", postcode);
		inBody.put("address", address);
		inBody.put("national", national);
		inBody.put("completeFlag", completeFlag);
		inBody.put("phoneNo", phoneNo);
		inBody.put("uniSocCreCode", uniSocCreCode);
		inBody.put("uniSocCreDir", uniSocCreDir);
		inBody.put("bizLicDomicile", bizLicDomicile);
		inBody.put("entType", entType);
		inBody.put("dateOfEst", dateOfEst);
		inBody.put("corpacc", corpacc);
		inBody.put("corpAccBankNo", corpAccBankNo);
		inBody.put("corpAccBankNm", corpAccBankNm);
		inBody.put("bindFlag", bindFlag);
		inBody.put("bindType", bindType);
		inBody.put("acctype", acctype);
		inBody.put("oldbankAccountNo", oldbankAccountNo);
		inBody.put("bankAccountNo", bankAccountNo);
		inBody.put("bankAccountName", bankAccountName);
		inBody.put("bankAccountTelNo", bankAccountTelNo);
		inBody.put("note", note);
		
		if (serviceType.code == 6) {
			//PaymentProxy.getInstance().checkTradePass(Client.PC.code, businessSeqNo, Convert.strToLong(customerId, 0), BaseController.getBaseURL() + "supervisor/query/customerInfo/unbinding", ServiceType.CUSTOMER_UNBINDING, inBody);
			long userId = Convert.strToLong(customerId, 0);
			t_user_info userInfo = userInfoService.findByColumn("user_id=?", userId);
			if (userInfo == null) {
				flash.success("客户信息查询失败！");
				custInfoSyncPre();
			}
			
			PaymentProxy.getInstance().checkTradePass(Client.PC.code, businessSeqNo, userId, BaseController.getBaseURL() + "supervisor/query/customerInfo/unbinding", userInfo.reality_name, ServiceType.CUSTOMER_UNBINDING, 0.00, inBody, ServiceType.UNBINDING_CHECK_TRADE_PASS, 1, bankAccountNo, "", "");
			//unbindCheckPass(186, businessSeqNo, inBody);
		}
		
		ResultInfo result = PaymentProxy.getInstance().custInfoAysn(inBody, OrderNoUtil.getClientSn(), serviceType);
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(Convert.strToLong(customerId, 0));
		userInfo.reality_name = username;
		userInfo.id_number = certNo;
		
		if (result.code > 0) {
			
			switch (serviceType.code) {
			case 1:
				result = userFundService.doCreateAccount(Convert.strToLong(customerId, 0), result.msgs.get("accNo"), username, certNo);
				if (result.code > 0) {
					flash.success("个人开户成功！");
					custInfoSyncPre();
				}
		        
				break;
			case 2:
				result = userInfoService.updateUserInfo(userInfo);
				if (result.code > 0) {
					flash.success("修改成功！");
					custInfoSyncPre();
				}
				break;
			case 3:
				result = userFundService.custCancel(Convert.strToLong(customerId, 0));
				if (result.code > 0) {
					flash.success("注销成功！");
					custInfoSyncPre();
				}
				break;
				
			case 4:
				result = userFundService.doCreateAccount(Convert.strToLong(customerId, 0), result.msgs.get("accNo"), username ,companyName, uniSocCreCode, corpAccBankNo, certNo);
				if (result.code > 0) {
					flash.success("企业开户成功！");
					custInfoSyncPre();
				}
				break;
			}
	
		}
		
		flash.error(result.msg);
		custInfoSyncPre();
	}
	
	/**
	 * 客户解绑验密
	 * 
	 * @author niu
	 * @create 2017.10.17
	 */
	/*public static void unbindCheckPass1(long userId, String businessSeqNo, Map<String, String> inBody) {
		
		//验密
		//PaymentProxy.getInstance().checkTradePass(Client.PC.code, businessSeqNo, userId, BaseController.getBaseURL() + "check/yibincallback/unbinding", ServiceType.CUSTOMER_UNBINDING, inBody);		
	}*/
	
	/**
	 * 客户解绑验密
	 * 
	 * @author niu
	 * @create 2017.10.17
	 */
	public static void unBindAccCheckPass() {
		
		ResultInfo result = callBackService.checkTradePassCB(params, ServiceType.CUSTOMER_UNBINDING);
		
		if (result.code < 0) {
			flash.error(result.msg);
			
			custInfoSyncPre();
		}
		
		Map<String, String> reqParams = result.msgs;

		result = PaymentProxy.getInstance().custInfoAysn(reqParams, OrderNoUtil.getClientSn(), ServiceType.CUSTOMER_UNBINDING);
		if (result.code > 0) {
			t_bank_card_user bankUser = bankCardUserService.findByColumn("account = ?", EncryptUtil.decrypt(reqParams.get("bankAccountNo")));
			t_bank_card_user bank = bankUser.delete();
			if (bank != null) {
				flash.success("客户解绑成功！");
				custInfoSyncPre();
			}
		}
	
		flash.error(result.msg);
		custInfoSyncPre();
	}
		
		
	
	/**
	 * 标的信息同步-营销冲正界面
	 * 
	 * @author LiuPengwei
	 * @create 2018年1月12日
	 */
	public static void bidInfoSyncPre (){
		
		render();
	}
	
	/**
	 * 标的信息同步-营销冲正
	 * 
	 * @param userId 用户id
	 * @param amount 红包冲正金额
	 * @param oldbusinessSeqNo 营销流水号
	 * @param bidId 使用红包的 标的Id
	 * @param phoneNo 用户手机号
	 * 
	 * @author LiuPengwei
	 * @create 2018年1月12日
	 */
	
	public static void marketingInfoSync (String userId, String amount, String oldbusinessSeqNo, String bidId, String phoneNo){
		ResultInfo results = new ResultInfo();
		
		if (userId.equals("") || amount.equals("") || bidId.equals("") || oldbusinessSeqNo.equals("") || phoneNo.equals("")){
			flash.error("请输入所有必填信息！");
			bidInfoSyncPre();						
		}
		
		double amounts = Convert.strToDouble(amount, 0);
		long bidIds = Convert.strToLong(bidId, 0);
		long userIds = Convert.strToLong(userId, 0);
		
		/**验证用户信息是否一致*/
		t_user_info userInfo = userInfoService.findByColumn("user_id = ?", userIds);
		if(!(userInfo.mobile.equals(phoneNo))|| userInfo ==null){
			
			flash.error("用户编号和电话号不正确！");
			bidInfoSyncPre();	
		}
		
		t_bid bid = bidService.findByID(bidIds);
		results = redpacketService.redPacketCorrects(bid, userIds, amounts, "0" , oldbusinessSeqNo);
		
		if (results.code < 1) {
			flash.error(results.msg);
			bidInfoSyncPre();
		}	
		flash.success("营销冲正成功！");
		bidInfoSyncPre();
	}
	
		
	/**
	 * 标的信息同步-取消投标
	 * 
	 * @param userId
	 * @param amount
	 * @param oldbusinessSeqNo
	 * @param bidId
	 * @param phoneNo
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月16日
	 */
	public static void bidInfoSync (String userId, String amount, String oldbusinessSeqNo, String bidId, String phoneNo){
		ResultInfo results = new ResultInfo();
		
		if (userId.equals("") || amount.equals("") || bidId.equals("") || oldbusinessSeqNo.equals("") || phoneNo.equals("")){
			flash.error("请输入所有必填信息！");
			bidInfoSyncPre();						
		}
		
		double amounts = Convert.strToDouble(amount, 0);
		long bidIds = Convert.strToLong(bidId, 0);
		long userIds = Convert.strToLong(userId, 0);
		
		/**验证用户信息是否一致*/
		t_user_info userInfo = userInfoService.findByColumn("user_id = ?", userIds);
		if(!(userInfo.mobile.equals(phoneNo))|| userInfo ==null){
			
			flash.error("用户编号和电话号不正确！");
			bidInfoSyncPre();	
		}
		
				
		t_bid bid = bidService.findByID(bidIds);
		results = bidService.cancelBidCorrects(bid, userIds, amounts, "0" , oldbusinessSeqNo);
		if (results.code < 1) {
			flash.error(results.msg);
			bidInfoSyncPre();	
		}
		flash.success("取消标的成功！");
		bidInfoSyncPre();
	}
	
	
	/**营销活动维护
	 * 
	 * @param userId    用户id
	 * @param amount	营销资金
	 * @param bidId     标的id
	 * @param phoneNo   电话号
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月3日17:17:45
	 */

	public static void markInvest(long userId, double markeAmt, long bidId, String phoneNo) {
		
		t_bid bid = bidService.findByID(bidId);
		
		/**验证用户信息是否一致*/
		t_user_info userInfo = userInfoService.findByColumn("user_id = ?", userId);
		
		if(!(userInfo.mobile.equals(phoneNo))|| userInfo ==null){
			
			flash.error("用户编号和电话号不正确！");
			bidInfoSyncPre();	
		}
		
		String clientSn = "oyph_" + UUID.randomUUID().toString();
		String businessSeqNos = OrderNoUtil.getNormalOrderNo(ServiceType.MARKET);
		
		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		accountMap.put("oderNo", "0");//序号
		accountMap.put("oldbusinessSeqNo","");//原业务流水号
		accountMap.put("oldOderNo","");//原序列号
		accountMap.put("debitAccountNo",YbConsts.MARKETNO);//借方台账号（理财方台账账号）
		accountMap.put("cebitAccountNo",userId);//贷方台账号（用户的台账账号）
		accountMap.put("currency",YbConsts.CURRENCY );//币种
		accountMap.put("amount",YbUtils.formatAmount(markeAmt)+"");//用户实际花费金额
		accountMap.put("summaryCode",ServiceType.MARKET.key);//摘要码
		
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);
		
		
		Map<String, Object> conMap = new HashMap<String, Object>();
		conMap.put("oderNo", "1");
		conMap.put("contractType", "");
		conMap.put("contractRole", "");
		conMap.put("contractFileNm", "");
		conMap.put("debitUserid", "");
		conMap.put("cebitUserid", "");
		
		List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();
		contractList.add(conMap);

		ResultInfo result = PaymentProxy.getInstance().fundTrade(clientSn, userId, businessSeqNos, bid, ServiceType.MARKET, EntrustFlag.UNENTRUST, accountList, contractList);
		
		if (result.code < 1) {
			
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
		}
		
		bidInfoSyncPre();
	}
	
}
