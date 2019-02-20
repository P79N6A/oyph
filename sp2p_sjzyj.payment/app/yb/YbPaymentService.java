package yb;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shove.Convert;

import common.constants.RemarkPramKey;
import common.constants.SettingKey;
import common.utils.EncryptUtil;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import controllers.common.BaseController;
import models.bean.RequestHeader;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.entity.t_bid;
import models.entity.t_payment_call_back;
import models.entity.t_payment_request;
import net.sf.json.JSONObject;
import play.Logger;
import services.PaymentService;
import services.common.SettingService;
import services.common.UserFundService;
import services.common.UserInfoService;
import yb.enums.AutoFlag;
import yb.enums.BindFlag;
import yb.enums.BindType;
import yb.enums.CertType;
import yb.enums.CheckType;
import yb.enums.CustomerRole;
import yb.enums.CustomerType;
import yb.enums.EntrustFlag;
import yb.enums.Nature;
import yb.enums.ObjectType;
import yb.enums.OtherAmountType;
import yb.enums.PayType;
import yb.enums.ProjectStatus;
import yb.enums.RechargeType;
import yb.enums.ReturnType;
import yb.enums.ServiceType;
import yb.enums.TradeType;
import yb.enums.WithdrawType;
import yb.enums.YbPayType;

/**
 * 宜宾银行 业务类
 * 
 * @author niu
 * @create 2017.08.23
 */
public class YbPaymentService extends PaymentService {	
	
	private static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	/**
	 * 个人客户注册，请求报文
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月19日
	 */
	public HashMap<String, JSONObject> personCustomerRegist(String businessSeqNo, String phoneNo, long userId, String clientSn, 
			String certNo, String username,	String startTime, String endTime,String jobType,String job, String national,
			String postcode, String address) {
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("busiTradeType", ServiceType.PERSON_CUSTOMER_REGIST.key);//业务类型
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("customerId", ""+ userId);//会员编号
		inBody.put("username", username);//真实姓名
		inBody.put("phoneNo", phoneNo);//手机号		
		inBody.put("certType", CertType.IDENTITY_CARD.value);//证件类型
		inBody.put("certNo", certNo);//身份证号
		inBody.put("idvalidDate", startTime);//身份证有效起始日期
		inBody.put("idexpiryDate", endTime);//身份证有效结束日期
		inBody.put("jobType", jobType);//职业类型
		inBody.put("job", job);//职业描述
		inBody.put("postcode",postcode);//邮政编码
		inBody.put("address",address);//联系地址
		inBody.put("national",national);//民族
		inBody.put("completeFlag","1");//身份证信息完整
		inBody.put("bindFlag",BindFlag.BIND.value);//绑卡标识
		
		inBody.put("crole", CustomerRole.WHOLE.value);//会员角色
		inBody.put("ctype", CustomerType.PERSON.value);//会员类型
	
		
				
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.PERSON_CUSTOMER_REGIST.key, TradeType.CUSTOMER_INFO_SYNC.key, null, null,null,null);
	}
	
	
	/**
	 *  企业客户注册，请求报文
	 * @param businessSeqNo
	 * @param phoneNo
	 * @param userId
	 * @param clientSn
	 * @param companyName
	 * @param uniSocCreCode
	 * @param entType
	 * @param establish
	 * @param companyId
	 * @param licenseAddress
	 * @param bankNo
	 * @param bankName
	 * @return
	 * @author LiuPengwei
	 * @create 2017年9月19日
	 */
	
	public HashMap<String, JSONObject> companyCustomerRegist(String businessSeqNo, String phoneNo, long userId, String clientSn,
			String companyName, String uniSocCreCode, String entType, String establish, String companyId,
			String licenseAddress, String bankNo, String bankName, String userName, String idCard) {
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("busiTradeType", ServiceType.ENTERPRISE_CUSTOMER_REGIST.key);//业务类型
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("customerId", ""+ userId);//会员编号
		inBody.put("companyName", companyName);//企业名称
		inBody.put("uniSocCreCode", uniSocCreCode);//统一社会信用代码
		inBody.put("bizLicDomicile", licenseAddress);//营业执照住所
		inBody.put("entType", entType);//主体类型
		inBody.put("dateOfEst", establish);//
		inBody.put("corpacc", companyId);//对公户账号
		inBody.put("corpAccBankNo", bankNo);//对公开户行行号
		inBody.put("corpAccBankNm", bankName);//对公开户行名称
		inBody.put("crole", CustomerRole.WHOLE.value);//会员角色
		inBody.put("ctype", CustomerType.ENTERPRISE.value);//会员类型
		inBody.put("phoneNo", phoneNo);
		inBody.put("bindFlag", "00");
		inBody.put("username", userName);
		inBody.put("certNo", idCard);
		

		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.ENTERPRISE_CUSTOMER_REGIST.key, TradeType.CUSTOMER_INFO_SYNC.key, null, null,null,null);
	}
	
	
	/**
	 * 客户充值
	 * @param businessSeqNo
	 * @param clientSn
	 * @param businessOrderNo
	 * @param payType
	 * @param bankAccountNo
	 * @param userId
	 * @param amount
	 * @return
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月13日
	 */
	
	
	public HashMap<String, JSONObject> customerRecharge(String businessSeqNo, String clientSn, String businessOrderNo, int payType, String bankAccountNo, String cebitAccountNo, double amount) {
		

		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		
		accountMap.put("oderNo", "0");//序号
		accountMap.put("debitAccountNo", "");//借方台账账户
		accountMap.put("cebitAccountNo", cebitAccountNo);//贷方台账账户
		accountMap.put("currency", YbConsts.CURRENCY);//货币类型
		accountMap.put("amount", YbUtils.formatAmount(amount));//充值金额
		accountMap.put("otherAmounttype", OtherAmountType.RECHARGE_FEE.value);//其他金额类型
		accountMap.put("otherAmount", 0+"");//其他金额
	
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);		
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("businessOrderNo",businessSeqNo);//订单流水号
		inBody.put("rType", RechargeType.CUSTOMER_WITHHOLD_RECHARGE.value);//充值类型
		inBody.put("entrustflag", EntrustFlag.UNENTRUST.value);//委托标识
		inBody.put("payType", PayType.getEnum(payType).value);//支付方式
		inBody.put("bankAccountNo", bankAccountNo);//银行卡号
		inBody.put("busiBranchNo", "");
		inBody.put("secBankaccNo", "");
		inBody.put("note", "");
		inBody.put("platformAccountNo", "");
		inBody.put("deductType", "");
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.CUSTOMER_RECHARGE.key, TradeType.CUSTOMER_RECHARGE_WITHDRAW.key, accountList, "accountList",null,null);
	}
	

	
	/**
	 * 客户提现	
	 * @param businessSeqNo
	 * @param clientSn
	 * @param businessOrderNo
	 * @param payType
	 * @param bankAccountNo
	 * @param debitAccountNo
	 * @param amount
	 * @return
	 * @author LiuPengwei
	 * @create 2017年9月13日
	 */
	 
	public HashMap<String, JSONObject> customerWithdraw(String businessSeqNo, String clientSn, String businessOrderNo, PayType payType, String bankAccountNo, String debitAccountNo,
			double amount,double withdrawalFee) {
		
		
		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		accountMap.put("oderNo", "1");//序号
		accountMap.put("debitAccountNo", debitAccountNo);//借方台账账户
		accountMap.put("cebitAccountNo", "");//贷方台账账户
		accountMap.put("currency", YbConsts.CURRENCY);//货币类型
		accountMap.put("amount", YbUtils.formatAmount(amount));//提现金额
		accountMap.put("otherAmounttype", OtherAmountType.WITHDRAW_FEE.value);//其他金额类型
		accountMap.put("otherAmount", withdrawalFee+"");//其他金额
		
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);	
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("businessOrderNo", businessSeqNo);//订单流水号
		inBody.put("rType", WithdrawType.CUSTOMER_WITHDRAW.value);//提现类型
		inBody.put("entrustflag", EntrustFlag.UNENTRUST.value);//委托标识
		inBody.put("payType", payType.BANK_PAYMENT.value);//支付方式
		inBody.put("bankAccountNo", bankAccountNo);//银行卡号
		inBody.put("busiBranchNo", "");
		inBody.put("secBankaccNo", "");
		inBody.put("note", "");
		inBody.put("platformAccountNo", "");
		inBody.put("deductType", "01");
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.ENTERPRISE_WITHDRAW.key, TradeType.CUSTOMER_RECHARGE_WITHDRAW.key, accountList, "accountList",null,null);
	}
	
	
	/**
	 * 企业提现	
	 * @param businessSeqNo
	 * @param clientSn
	 * @param businessOrderNo
	 * @param payType
	 * @param bankAccountNo
	 * @param debitAccountNo
	 * @param amount
	 * @return
	 * @author LiuPengwei
	 * @create 2017年10月15日
	 */
	 
	public HashMap<String, JSONObject> enterpriseWithdraw(String businessSeqNo, String clientSn, String businessOrderNo, PayType payType, String bankAccountNo, String debitAccountNo,
			double amount,double withdrawalFee) {
		
		
		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		accountMap.put("oderNo", "1");//序号
		accountMap.put("debitAccountNo", debitAccountNo);//借方台账账户
		accountMap.put("cebitAccountNo", "");//贷方台账账户
		accountMap.put("currency", YbConsts.CURRENCY);//货币类型
		accountMap.put("amount", YbUtils.formatAmount(amount));//提现金额
		accountMap.put("otherAmounttype", OtherAmountType.WITHDRAW_FEE.value);//其他金额类型
		accountMap.put("otherAmount", withdrawalFee+"");//其他金额
		
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);	
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("businessOrderNo", businessSeqNo);//订单流水号
		inBody.put("rType", WithdrawType.ENTERPRISE_ENTRUST_WITHDRAW.value);//提现类型
		inBody.put("entrustflag", EntrustFlag.UNENTRUST.value);//委托标识
		inBody.put("payType", payType.BANK_PAYMENT.value);//支付方式
		inBody.put("bankAccountNo", bankAccountNo);//银行卡号
		inBody.put("busiBranchNo", "");
		inBody.put("secBankaccNo", "");
		inBody.put("note", "");
		inBody.put("platformAccountNo", "");
		inBody.put("deductType", "01");
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.ENTERPRISE_WITHDRAW.key, TradeType.CUSTOMER_RECHARGE_WITHDRAW.key, accountList, "accountList",null,null);
	}
	
	
	/**正常还款
	 * 
	 * @author LiuPengwei
	 * @create 2017年9月13日
	 */
	 
	public HashMap<String, JSONObject> bidAuditSucc(String businessSeqNo, String clientSn, String debitAccountNo,
			String financierAccountNo, String bidNo, double loanServiceFee, double financierFee) {
		
		//融资人金额
		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		accountMap.put("oderNo", "0");//序号
		accountMap.put("oldbusinessSeqNo","");//原业务流水号
		accountMap.put("oldOderNo","");//原序列号
		accountMap.put("debitAccountNo",debitAccountNo);//借方台账号（标的台账账号）
		accountMap.put("cebitAccountNo",financierAccountNo);//贷方台账号（融资方台账账号）
		accountMap.put("currency",YbConsts.CURRENCY );//币种
		accountMap.put("amount",financierFee+"");//交易金额(融资人)
		accountMap.put("summaryCode",ServiceType.LOAN.key);//摘要码
		
		//手续费收取
		LinkedHashMap<String, Object> chargeMap = new LinkedHashMap<String, Object>();
		chargeMap.put("oderNo", "1");//序号
		chargeMap.put("oldbusinessSeqNo","");//原业务流水号
		chargeMap.put("oldOderNo","");//原序列号
		chargeMap.put("debitAccountNo", debitAccountNo);//借方台账号（标的台账账号）
		chargeMap.put("cebitAccountNo", YbConsts.COSTNO);//贷方台账号（平台台账账号）
		chargeMap.put("currency",YbConsts.CURRENCY );//币种
		chargeMap.put("amount",loanServiceFee+"");//交易金额(平台)
		chargeMap.put("summaryCode","T12");//摘要码
		
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);
		accountList.add(chargeMap);
		
		
		LinkedHashMap<String, Object> contractMaps = new LinkedHashMap<String, Object>();
		contractMaps.put("oderNo", "2");//序号
		contractMaps.put("contractType","03");//合同类型
		contractMaps.put("contractRole","03");//角色
		contractMaps.put("contractFileNm",YbConsts.MERCHANT_CODE+"_"+debitAccountNo+"_"+financierAccountNo+"_"+bidNo+"_"+getFormatDate("yyyyMMdd"));//合同文件名
		contractMaps.put("debitUserid","");//借方用户Id
		contractMaps.put("cebitUserid","");//贷方用户Id
		
		
		//合同文件名列表
		List<Map<String, Object>>contractList = new ArrayList<Map<String, Object>>();
		contractList.add(contractMaps);
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("busiTradeType", ServiceType.LOAN.key);//业务操作类型
		inBody.put("entrustflag", EntrustFlag.UNENTRUST.value);//委托标识
		inBody.put("objectId", bidNo);//标的编号
		inBody.put("note", "平台服务费");//备注
		inBody.put("entrustPayFlag", "00");
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.LOAN.key, TradeType.FUND_TRADE.key, accountList, "accountList",contractList,"contractList");
	}
	
	/**
	 * 债权转让数据拼接
	 *
	 * @author LiuPengwei
	 * @createDate 2017年10月13日
	 */
	public HashMap<String, JSONObject> debtTransfer(String businessSeqNo, String clientSn, String debitAccountNo,
			String financierAccountNo, String bidNo, double loanServiceFee, double financierFee) {
		//融资人金额
		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		accountMap.put("oderNo", "0");//序号
		accountMap.put("oldbusinessSeqNo","");//原业务流水号
		accountMap.put("oldOderNo","");//原序列号
		accountMap.put("debitAccountNo",debitAccountNo);//借方台账号（受让方台账账号）
		accountMap.put("cebitAccountNo",financierAccountNo);//贷方台账号（转让方台账账号）
		accountMap.put("currency",YbConsts.CURRENCY );//币种
		accountMap.put("amount",financierFee+"");//交易金额
		accountMap.put("summaryCode",ServiceType.FARM_IN.key);//摘要码
		
		//手续费收取
		LinkedHashMap<String, Object> chargeMap = new LinkedHashMap<String, Object>();
		chargeMap.put("oderNo", "1");//序号
		chargeMap.put("oldbusinessSeqNo","");//原业务流水号
		chargeMap.put("oldOderNo","");//原序列号
		chargeMap.put("debitAccountNo", financierAccountNo);//借方台账号（平台费用的台账账号）
		chargeMap.put("cebitAccountNo", YbConsts.COSTNO);//贷方台账号（转让方台账账号）
		chargeMap.put("currency",YbConsts.CURRENCY );//币种
		chargeMap.put("amount",loanServiceFee+"");//交易金额(平台)
		chargeMap.put("summaryCode","T12");//摘要码
		
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);
		accountList.add(chargeMap);
		
		
		LinkedHashMap<String, Object> contractMaps = new LinkedHashMap<String, Object>();
		contractMaps.put("oderNo", "2");//序号
		contractMaps.put("contractType","04");//合同类型
		contractMaps.put("contractRole","04");//角色
		contractMaps.put("contractFileNm",YbConsts.MERCHANT_CODE+"_"+debitAccountNo+"_"+financierAccountNo+"_"+bidNo+"_"+getFormatDate("yyyyMMdd"));//合同文件名
		contractMaps.put("debitUserid",debitAccountNo+"");//受让方用户Id
		contractMaps.put("cebitUserid",financierAccountNo+"");//转让方用户Id
		
		
		//合同文件名列表
		List<Map<String, Object>>contractList = new ArrayList<Map<String, Object>>();
		contractList.add(contractMaps);
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("busiTradeType", ServiceType.FARM_IN.key);//业务操作类型
		inBody.put("entrustflag", EntrustFlag.UNENTRUST.value);//委托标识
		inBody.put("objectId", bidNo);//标的编号
		inBody.put("note", "平台服务费");//备注
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.FARM_IN.key, TradeType.FUND_TRADE.key, accountList, "accountList",contractList,"contractList");
	}
	
	
	
	/**
	 * 获取指定格式的日期
	 */
	public static String getFormatDate(String format) {
		
		return new SimpleDateFormat(format).format(new Date());
	}
	

	/**
	 * 防重复处理校验
	 *
	 * @param cdParams 回调参数
	 * @param payType 接口类型
	 *
	 * @author LiuPengwei
	 * @createDate 2017年9月19日
	 */
	public static ResultInfo checkSign(String businessSeqNo, YbPayType payType){
		ResultInfo result = new ResultInfo();
		
		if (payType.isAddRequestRecord) {
			//防止重单
			
			t_payment_request pr = paymentRequstDao.findByColumn("service_order_no = ? and service_type<>19", businessSeqNo);
			if (pr == null) {
				result.code = -1;
				result.msg = payType.value + "查询托管请求记录失败";	
				
				return result;
			}
			
			int row = paymentRequstDao.updateReqStatusToSuccess(businessSeqNo);
			if (row > 0) {
				result.code = 1;
				result.msg = payType.value + "更新请求状态成功";	
				
				return result;
			}
			
			if (row == 0) {
				result.code = ResultInfo.ALREADY_RUN;
				result.msg = payType.value + "已成功执行";	
				
				return result;
			}
			
			if (row < 0) {
				result.code = ResultInfo.ERROR_SQL;
				result.msg = payType.value + "更新请求状态时，数据库异常";	
				
				return result;
			}
		}
		
		
		result.code = 1;
		result.msg = payType.value + "更新请求状态成功";	
		
		return result;
	}
		
	
	/**
	 * 用户设置交易密码接口，参数拼装
	 *
	 * @param userId 用户id
	 * @param businessSeqNo 业务流水号
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年9月7日
	 */
	public HashMap<String, String> setUserPassword(long userId, String businessSeqNo) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		//获取时间戳
		String signTime = System.currentTimeMillis() + "";
		
		//签名
		LinkedHashMap<String, String> inBody = new LinkedHashMap<>();
		inBody.put("userId", "" + userId);
		String signature = YbUtils.getSignature(signTime, inBody, null, null, null);
		
		//设置回调路径
		String backURL = BaseController.getBaseURL() + "payment/yibincallback/setUserPasswordSyn";
		
		//验证回调路径
		/*String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
		Boolean flag = Pattern.matches(regex, backURL);
		if(flag == false) {
			return null;
		}*/

		map.put("systemCode", YbConsts.MERCHANT_CODE); // 平台号
		map.put("userId", "" + userId); // 会员编号
		map.put("backURL", backURL); // 回调地址
		map.put("signTime", signTime); // 时间戳
		map.put("signature", signature); // 签名
		map.put("businessSeqNo", businessSeqNo); // 业务流水号
		
		return map;
	}
	
	/**
	 * 用户重置交易密码接口，参数拼装
	 *
	 * @param userId 用户id
	 * @param businessSeqNo 业务流水号
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年9月7日
	 */
	public HashMap<String, String> resetUserPassword(long userId, String businessSeqNo) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		//获取时间戳
		String signTime = System.currentTimeMillis() + "";
		
		//签名
		LinkedHashMap<String, String> inBody = new LinkedHashMap<>();
		inBody.put("userId", "" + userId);
		String signature = YbUtils.getSignature(signTime, inBody, null, null, null);
		
		//设置回调路径
		String backURL = BaseController.getBaseURL() + "payment/yibincallback/resetUserPasswordSyn";

		map.put("systemCode", YbConsts.MERCHANT_CODE); // 平台号
		map.put("userId", "" + userId); // 会员编号
		map.put("backURL", backURL); // 回调地址
		map.put("signTime", signTime); // 时间戳
		map.put("signature", signature); // 签名
		map.put("businessSeqNo", businessSeqNo); // 业务流水号
		
		return map;
	}
	
	/**
	 * 用户修改密码接口，参数拼装
	 *
	 * @param userId 用户id
	 * @param businessSeqNo 业务流水号
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年9月7日
	 */
	public HashMap<String, String> amendUserPassword(long userId, String businessSeqNo) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		//获取时间戳
		String signTime = System.currentTimeMillis() + "";
		
		//签名
		LinkedHashMap<String, String> inBody = new LinkedHashMap<>();
		inBody.put("userId", "" + userId);
		String signature = YbUtils.getSignature(signTime, inBody, null, null, null);
		
		//设置回调路径
		map.put("backURL", BaseController.getBaseURL() + "payment/yibincallback/amendUserPasswordSyn"); // 回调地址

		map.put("systemCode", YbConsts.MERCHANT_CODE); // 平台号
		map.put("userId", "" + userId); // 会员编号
		map.put("signTime", signTime); // 时间戳
		map.put("signature", signature); // 签名
		map.put("businessSeqNo", businessSeqNo); // 业务流水号
		
		return map;
	}
	
	/**
	 * 用户校验交易密码接口，参数拼装
	 *
	 * @param userId 用户id
	 * @param businessSeqNo 业务流水号
	 * @param amount 金额
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年9月13日
	 */
	public HashMap<String, String> checkPassword(long userId, String businessSeqNo,ServiceType serviceType, double amount) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		//获取时间戳
		String signTime = System.currentTimeMillis() + "";
		
		//签名
		LinkedHashMap<String, String> inBody = new LinkedHashMap<>();
		inBody.put("userId", "" + userId);
		String signature = YbUtils.getSignature(signTime, inBody, null, null, null);
		String backURL =null;
		//设置回调路径
		switch (serviceType.code) {
		
		//绑卡
		case 5:
			backURL = BaseController.getBaseURL() + "payment/yibincallback/checkPasswordSyn";
			break;
		//充值
		case 14:
			backURL = BaseController.getBaseURL() + "payment/yibincallback/rechargeCheckPasswordSyn";
			map.put("amount", amount+""); // 金额
			map.put("userName", userInfo.reality_name); // 真实姓名
			map.put("operType", "R01"); // 客户充值标识
			break;
		//客户提现
		case 15:
			backURL = BaseController.getBaseURL() + "payment/yibincallback/withdrawalCheckPasswordSyn";
			map.put("amount", amount+""); // 金额
			map.put("userName", userInfo.reality_name); // 真实姓名
			map.put("operType", "W01"); // 提现标识
			break;
		case 46:
			backURL = BaseController.getBaseURL() + "payment/yibincallback/enterpriseWithdrawalCheckPasswordSyn";
			map.put("amount", amount+""); // 金额
			map.put("userName", userInfo.reality_name); // 真实姓名
			map.put("operType", "W06"); // 提现标识
			break;
		case 4:
			break;
		default:
			break;
		}
		

		map.put("systemCode", YbConsts.MERCHANT_CODE); // 平台号
		map.put("userId", "" + userId); // 会员编号
		map.put("backURL", backURL); // 回调地址
		map.put("signTime", signTime); // 时间戳
		map.put("signature", signature); // 签名
		map.put("businessSeqNo", businessSeqNo); // 业务流水号
		
		return map;
	}
	
	/**
	 * 用户校验交易密码接口，参数拼装
	 *
	 * @param userId 用户id
	 * @param businessSeqNo 业务流水号
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年9月13日
	 */
	public HashMap<String, String> bindCardCheckPassword(long userId, String businessSeqNo, ServiceType serviceType) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		//获取时间戳
		String signTime = System.currentTimeMillis() + "";
		
		//签名
		LinkedHashMap<String, String> inBody = new LinkedHashMap<>();
		inBody.put("userId", "" + userId);
		String signature = YbUtils.getSignature(signTime, inBody, null, null, null);
		String backURL =null;
		
		backURL = BaseController.getBaseURL() + "payment/yibincallback/checkPasswordSyn";
		
		map.put("systemCode", YbConsts.MERCHANT_CODE); // 平台号
		map.put("userId", "" + userId); // 会员编号
		map.put("backURL", backURL); // 回调地址
		map.put("signTime", signTime); // 时间戳
		map.put("signature", signature); // 签名
		map.put("businessSeqNo", businessSeqNo); // 业务流水号
		//map.put("userName", userName); // 客户名称
		//map.put("bankNo", bankNo); // 银行卡号
		map.put("operType", "B01"); // 交易类型
		
		return map;
	}
	
	/**
	 * 用户校验交易密码接口，参数拼装
	 *
	 * @param userId 用户id
	 * @param businessSeqNo 业务流水号
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年9月13日
	 */
	public HashMap<String, String> guaranteeCheckPassword(long userId, String businessSeqNo, ServiceType serviceType, String userName, double amount, String backUrl) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		//获取时间戳
		String signTime = System.currentTimeMillis() + "";
		
		//签名
		LinkedHashMap<String, String> inBody = new LinkedHashMap<>();
		inBody.put("userId", "" + userId);
		String signature = YbUtils.getSignature(signTime, inBody, null, null, null);
		
		map.put("systemCode", YbConsts.MERCHANT_CODE); // 平台号
		map.put("userId", "" + userId); // 会员编号
		map.put("backURL", backUrl); // 回调地址
		map.put("signTime", signTime); // 时间戳
		map.put("signature", signature); // 签名
		map.put("businessSeqNo", businessSeqNo); // 业务流水号
		map.put("userName", userName); // 客户名称
		map.put("amount", amount+""); // 银行卡号
		map.put("operType", "T08"); // 交易类型
		
		return map;
	}
	
	/**
	 * 用户校验交易密码接口，参数拼装(网银充值)
	 *
	 * @param userId 用户id
	 * @param businessSeqNo 业务流水号
	 * @param amount 充值金额 
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年10月23日
	 */
	public HashMap<String, String> ebankRechargeCheckPassword(long userId, String businessSeqNo, double amount, int flags) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId); 
		
		//获取时间戳
		String signTime = System.currentTimeMillis() + "";
		
		//签名
		LinkedHashMap<String, String> inBody = new LinkedHashMap<>();
		inBody.put("userId", "" + userId);
		String signature = YbUtils.getSignature(signTime, inBody, null, null, null);
		
		String backURL = null;
		if(flags>4) {
			backURL = BaseController.getBaseURL() + "payment/yibincallback/ebankRechargeCheckPasswordsSyn";
		}else {
			backURL = BaseController.getBaseURL() + "payment/yibincallback/ebankRechargeCheckPasswordSyn";
		}
		
		map.put("systemCode", YbConsts.MERCHANT_CODE); // 平台号
		map.put("userId", "" + userId); // 会员编号
		map.put("backURL", backURL); // 回调地址
		map.put("signTime", signTime); // 时间戳
		map.put("signature", signature); // 签名
		map.put("businessSeqNo", businessSeqNo); // 业务流水号
		map.put("amount", amount+""); // 充值金额
		map.put("userName", userInfo.reality_name); // 充值金额
		map.put("operType", "R02"); // 客户网银充值标识
		
		return map;
	}
	
	/**
	 * 查询绑定的银行卡接口，参数拼装
	 *
	 * @param userId 用户id
	 * @param clientSn 客户端
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年9月11日
	 */
	public HashMap<String, JSONObject> queryBindedBankCard(long userId, String clientSn) {
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("checkType", CheckType.CUSTOMER_INFO_QUERY.value);//查询类型
		inBody.put("accountNo", "" + userId);//查询类型
		inBody.put("customerId", "" + userId);//会员编号
		inBody.put("showNum", "10");//每页显示条数
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.QUERY_BINDED_BANK.key, TradeType.DATA_INFO_QUERY.key, null, null, null, null);
	}
	
	
	/**
	 * 用户绑卡，参数拼装
	 *
	 * @param userId 用户id
	 * @param businessSeqNo 业务流水号
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年9月13日
	 */
	public HashMap<String, String> userBindCard(long userId, String businessSeqNo, String name, String idNumber, String mobile) {
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		//获取时间戳
		String signTime = System.currentTimeMillis() + "";
		
		//签名
		LinkedHashMap<String, String> inBody = new LinkedHashMap<>();
		inBody.put("userId", "" + userId);
		String signature = YbUtils.getSignature(signTime, inBody, null, null, null);

		//设置回调路径
		map.put("backURL", BaseController.getBaseURL() + "payment/yibincallback/userBindCardSyn"); // 回调地址
		
		map.put("systemCode", YbConsts.MERCHANT_CODE); // 平台号
		map.put("userId", "" + userId); // 会员编号
		map.put("signTime", signTime); // 时间戳
		map.put("signature", signature); // 签名
		map.put("businessSeqNo", businessSeqNo); // 业务流水号
		map.put("idCardNo", idNumber); // 身份证号
		map.put("userName", name); // 持卡人姓名
		map.put("cardPhoneNumber", mobile); // 电话
		map.put("channelname", "讴业普惠"); // 平台名称
		map.put("userNameType", 2+""); // 
		map.put("idCardNoType", 2+""); // 
		map.put("cardNoType", 2+""); // 
		map.put("cardPhoneNumberType", 2+""); // 
		
		return map;
	}
	
	
	
	/**
	 * 查询托管账户资金信息查询，参数拼装
	 *
	 * @param userId 用户id
	 * @param clientSn 客户端
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年9月14日
	 */
	public HashMap<String, JSONObject> queryFundInfo(long userId, String clientSn) {
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("checkType", CheckType.ACCOUNT_INFO_QUERY.value);//查询类型
		inBody.put("accountNo", "" + userId);//台账账号
		inBody.put("showNum", "10");//每页显示条数
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.QUERY_MESSAGE.key, TradeType.DATA_INFO_QUERY.key, null, null, null, null);
	}
	
	/**
	 * 查询代偿账户资金信息查询，参数拼装
	 *
	 * @param clientSn 客户端
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年9月20日
	 */
	public HashMap<String, JSONObject> queryCompensatoryBalance(String clientSn) {
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("checkType", CheckType.ACCOUNT_INFO_QUERY.value);//查询类型
		inBody.put("accountNo", YbConsts.COMPENSATORYNO);//台账账号
		inBody.put("showNum", "10");//每页显示条数
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.QUERY_MESSAGE.key, TradeType.DATA_INFO_QUERY.key, null, null, null, null);
	}
	
	/**
	 * 查询代偿账户资金信息查询，参数拼装
	 *
	 * @param clientSn 客户端
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年9月20日
	 */
	public HashMap<String, JSONObject> queryGuaranteeBalance(String clientSn, long paymentAccount) {
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("checkType", CheckType.ACCOUNT_INFO_QUERY.value);//查询类型
		inBody.put("accountNo", paymentAccount+"");//台账账号
		inBody.put("showNum", "10");//每页显示条数
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.QUERY_MESSAGE.key, TradeType.DATA_INFO_QUERY.key, null, null, null, null);
	}
	
	/**
	 * 查询营销账户资金信息查询，参数拼装
	 *
	 * @param clientSn 客户端
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年9月20日
	 */
	public HashMap<String, JSONObject> queryMarketBalance(String clientSn) {
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("checkType", CheckType.ACCOUNT_INFO_QUERY.value);//查询类型
		inBody.put("accountNo", YbConsts.MARKETNO);//台账账号
		inBody.put("showNum", "10");//每页显示条数
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.QUERY_MESSAGE.key, TradeType.DATA_INFO_QUERY.key, null, null, null, null);
	}
	
	/**
	 * 查询费用账户资金信息查询，参数拼装
	 *
	 * @param clientSn 客户端
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年9月20日
	 */
	public HashMap<String, JSONObject> queryCostBalance(String clientSn) {
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("checkType", CheckType.ACCOUNT_INFO_QUERY.value);//查询类型
		inBody.put("accountNo", YbConsts.COSTNO);//台账账号
		inBody.put("showNum", "10");//每页显示条数
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.QUERY_MESSAGE.key, TradeType.DATA_INFO_QUERY.key, null, null, null, null);
	}
	
	/**
	 * 代偿充值
	 * @param businessSeqNo
	 * @param clientSn
	 * @param businessOrderNo
	 * @param payType
	 * @param amount
	 * @return
	 * 
	 * @author Liuyang
	 * @create 2017年9月20日
	 */
	public HashMap<String, JSONObject> compensatoryRecharge(String businessSeqNo, String clientSn, String businessOrderNo, int payType, double amount) {

		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		
		accountMap.put("oderNo", "1");//序号
		accountMap.put("debitAccountNo", "");//借方台账账户
		accountMap.put("cebitAccountNo", YbConsts.COMPENSATORYNO);//贷方台账账户
		accountMap.put("currency", YbConsts.CURRENCY);//货币类型
		accountMap.put("amount", YbUtils.formatAmount(amount));//充值金额
		accountMap.put("otherAmounttype", "");//其他金额类型
		accountMap.put("otherAmount", "");//其他金额
	
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);		
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("businessOrderNo","");//订单流水号
		inBody.put("rType", RechargeType.COMPENSTATE_RECHARGE.value);//充值类型
		inBody.put("entrustflag", EntrustFlag.UNENTRUST.value);//委托标识
		inBody.put("payType", PayType.getEnum(payType).value);//支付方式
		inBody.put("bankAccountNo", YbConsts.BANKACCOUNTNO);//银行卡号
		inBody.put("busiBranchNo", "");
		inBody.put("secBankaccNo", "");
		inBody.put("note", "");
		inBody.put("platformAccountNo", "");//手续费收取平台台账账号
		inBody.put("deductType", "");//内扣外扣类型
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.COMPENSATORY_RECHARGE.key, TradeType.CUSTOMER_RECHARGE_WITHDRAW.key, accountList, "accountList",null,null);
	}
	
	/**
	 * 代偿提现
	 * @param businessSeqNo
	 * @param clientSn
	 * @param businessOrderNo
	 * @param payType
	 * @param amount
	 * @return
	 * 
	 * @author Liuyang
	 * @create 2017年9月21日
	 */
	public HashMap<String, JSONObject> compensatoryWithdrawal(String businessSeqNo, String clientSn, String businessOrderNo, int payType, double amount) {

		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		
		accountMap.put("oderNo", "1");//序号
		accountMap.put("debitAccountNo", YbConsts.COMPENSATORYNO);//借方台账账户
		accountMap.put("cebitAccountNo", "");//贷方台账账户
		accountMap.put("currency", YbConsts.CURRENCY);//货币类型
		accountMap.put("amount", YbUtils.formatAmount(amount));//提现金额
		accountMap.put("otherAmounttype", "");//其他金额类型
		accountMap.put("otherAmount", "");//其他金额
	
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);		
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("businessOrderNo","");//订单流水号
		inBody.put("rType", WithdrawType.COMPENSTATE_WITHDRAW.value);//提现类型
		inBody.put("entrustflag", EntrustFlag.ENTRUST.value);//委托标识
		inBody.put("payType", PayType.getEnum(payType).value);//支付方式
		inBody.put("bankAccountNo", YbConsts.BANKACCOUNTNO);//银行卡号
		inBody.put("busiBranchNo", "");
		inBody.put("secBankaccNo", "");
		inBody.put("note", "");
		inBody.put("platformAccountNo", "");//手续费收取平台台账账号
		inBody.put("deductType", "");//内扣外扣类型
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.COMPENSATORY_WITHDRAW.key, TradeType.CUSTOMER_RECHARGE_WITHDRAW.key, accountList, "accountList",null,null);
	}
	
	/**
	 * 营销充值
	 * @param businessSeqNo
	 * @param clientSn
	 * @param businessOrderNo
	 * @param payType
	 * @param amount
	 * @return
	 * 
	 * @author Liuyang
	 * @create 2017年9月20日
	 */
	public HashMap<String, JSONObject> marketRecharge(String businessSeqNo, String clientSn, String businessOrderNo, int payType, double amount) {

		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		
		accountMap.put("oderNo", "1");//序号
		accountMap.put("debitAccountNo", "");//借方台账账户
		accountMap.put("cebitAccountNo", YbConsts.MARKETNO);//贷方台账账户
		accountMap.put("currency", YbConsts.CURRENCY);//货币类型
		accountMap.put("amount", YbUtils.formatAmount(amount));//充值金额
		accountMap.put("otherAmounttype", "");//其他金额类型
		accountMap.put("otherAmount", "");//其他金额
	
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);		
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("businessOrderNo","");//订单流水号
		inBody.put("rType", RechargeType.MARKETING_RECHARGE.value);//充值类型
		inBody.put("entrustflag", EntrustFlag.UNENTRUST.value);//委托标识
		inBody.put("payType", PayType.getEnum(payType).value);//支付方式
		inBody.put("bankAccountNo", YbConsts.BANKACCOUNTNO);//银行卡号
		inBody.put("busiBranchNo", "");
		inBody.put("secBankaccNo", "");
		inBody.put("note", "");
		inBody.put("platformAccountNo", "");//手续费收取平台台账账号
		inBody.put("deductType", "");//内扣外扣类型
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.MARKET_RECHARGE.key, TradeType.CUSTOMER_RECHARGE_WITHDRAW.key, accountList, "accountList",null,null);
	}
	
	/**
	 * 营销提现
	 * @param businessSeqNo
	 * @param clientSn
	 * @param businessOrderNo
	 * @param payType
	 * @param amount
	 * @return
	 * 
	 * @author Liuyang
	 * @create 2017年9月20日
	 */
	public HashMap<String, JSONObject> marketWithdrawal(String businessSeqNo, String clientSn, String businessOrderNo, int payType, double amount) {

		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		
		accountMap.put("oderNo", "1");//序号
		accountMap.put("debitAccountNo", YbConsts.MARKETNO);//借方台账账户
		accountMap.put("cebitAccountNo", "");//贷方台账账户
		accountMap.put("currency", YbConsts.CURRENCY);//货币类型
		accountMap.put("amount", YbUtils.formatAmount(amount));//提现金额
		accountMap.put("otherAmounttype", "");//其他金额类型
		accountMap.put("otherAmount", "");//其他金额
		
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);		
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("businessOrderNo","");//订单流水号
		inBody.put("rType", WithdrawType.MARKETING_WITHDRAW.value);//充值类型
		inBody.put("entrustflag", EntrustFlag.UNENTRUST.value);//委托标识
		inBody.put("payType", PayType.getEnum(payType).value);//支付方式
		inBody.put("bankAccountNo", YbConsts.BANKACCOUNTNO);//银行卡号
		inBody.put("busiBranchNo", "");
		inBody.put("secBankaccNo", "");
		inBody.put("note", "");
		inBody.put("platformAccountNo", "");//手续费收取平台台账账号
		inBody.put("deductType", "");//内扣外扣类型
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.MARKET_WITHDRAW.key, TradeType.CUSTOMER_RECHARGE_WITHDRAW.key, accountList, "accountList",null,null);
	}
	
	/**
	 * 费用充值
	 * @param businessSeqNo
	 * @param clientSn
	 * @param businessOrderNo
	 * @param payType
	 * @param amount
	 * @return
	 * 
	 * @author Liuyang
	 * @create 2017年9月20日
	 */
	public HashMap<String, JSONObject> costRecharge(String businessSeqNo, String clientSn, String businessOrderNo, int payType, double amount) {

		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		
		accountMap.put("oderNo", "1");//序号
		accountMap.put("debitAccountNo", "");//借方台账账户
		accountMap.put("cebitAccountNo", YbConsts.COSTNO);//贷方台账账户
		accountMap.put("currency", YbConsts.CURRENCY);//货币类型
		accountMap.put("amount", YbUtils.formatAmount(amount));//充值金额
		accountMap.put("otherAmounttype", "");//其他金额类型
		accountMap.put("otherAmount", 0);//其他金额
	
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);		
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("businessOrderNo","");//订单流水号
		inBody.put("rType", RechargeType.FEE_RECHARGE.value);//充值类型
		inBody.put("entrustflag", EntrustFlag.UNENTRUST.value);//委托标识
		inBody.put("payType", PayType.getEnum(payType).value);//支付方式
		inBody.put("bankAccountNo", YbConsts.BANKACCOUNTNO);//银行卡号
		inBody.put("busiBranchNo", "");
		inBody.put("secBankaccNo", "");
		inBody.put("note", "");
		inBody.put("platformAccountNo", "");//手续费收取平台台账账号
		inBody.put("deductType", "");//内扣外扣类型
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.COST_RECHARGE.key, TradeType.CUSTOMER_RECHARGE_WITHDRAW.key, accountList, "accountList",null,null);
	}
	
	/**
	 * 费用提现
	 * @param businessSeqNo
	 * @param clientSn
	 * @param businessOrderNo
	 * @param payType
	 * @param amount
	 * @return
	 * 
	 * @author Liuyang
	 * @create 2017年9月20日
	 */
	public HashMap<String, JSONObject> costWithdrawal(String businessSeqNo, String clientSn, String businessOrderNo, int payType, double amount) {

		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		
		accountMap.put("oderNo", "1");//序号
		accountMap.put("debitAccountNo", YbConsts.COSTNO);//借方台账账户
		accountMap.put("cebitAccountNo", "");//贷方台账账户
		accountMap.put("currency", YbConsts.CURRENCY);//货币类型
		accountMap.put("amount", YbUtils.formatAmount(amount));//提现金额
		accountMap.put("otherAmounttype", "");//其他金额类型
		accountMap.put("otherAmount", "");//其他金额
	
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);		
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("businessOrderNo","");//订单流水号
		inBody.put("rType", WithdrawType.FEE_WITHDRAW.value);//充值类型
		inBody.put("entrustflag", EntrustFlag.UNENTRUST.value);//委托标识
		inBody.put("payType", PayType.getEnum(payType).value);//支付方式
		inBody.put("bankAccountNo", YbConsts.BANKACCOUNTNO);//银行卡号
		inBody.put("busiBranchNo", "");
		inBody.put("secBankaccNo", "");
		inBody.put("note", "");
		inBody.put("platformAccountNo", "");//手续费收取平台台账账号
		inBody.put("deductType", "");//内扣外扣类型
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.COST_WITHDRAW.key, TradeType.CUSTOMER_RECHARGE_WITHDRAW.key, accountList, "accountList",null,null);
	}
	
	/**
	 *  资金交易-后台线下收款（T00002）
	 * 
	 * @param clientSn
	 * @param businessSeqNo
	 * @param bid
	 * @param serviceType
	 * @param entrustFlag
	 * @param accountList
	 * @param contractList
	 * @return
	 * 
	 * @author Liuyang
	 * @create 2017年10月12日 
	 */
	public HashMap<String, JSONObject> offlineReceive(String clientSn, String businessSeqNo, t_bid bid, ServiceType serviceType, EntrustFlag entrustFlag, List<Map<String, Object>> accountList, List<Map<String, Object>> contractList) {
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("busiTradeType", serviceType.key);//业务交易类型
		inBody.put("entrustflag", entrustFlag.value);//委托标识
		inBody.put("objectId", bid.mer_bid_no);//标的编号
		inBody.put("note", serviceType.note);//备注
		inBody.put("entrustPayFlag", "00");//受托支付标识
		
		return YbUtils.getRequestText(clientSn, inBody, serviceType.key, TradeType.FUND_TRADE.key, accountList, "accountList", contractList, "contractList");
	}
	
	/**
	 * 客户网银充值
	 * @param businessSeqNo
	 * @param clientSn
	 * @param businessOrderNo
	 * @param payType
	 * @param bankAccountNo
	 * @param userId
	 * @param amount
	 * @return
	 * 
	 * @author liuyang
	 * @create 2017年10月20日
	 */
	public HashMap<String, JSONObject> customerInternetbankRecharge(String businessSeqNo, String clientSn, String businessOrderNo, int payType, String cebitAccountNo, double amount) {
		
		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		
		accountMap.put("oderNo", "0");//序号
		accountMap.put("debitAccountNo", "");//借方台账账户
		accountMap.put("cebitAccountNo", cebitAccountNo);//贷方台账账户
		accountMap.put("currency", YbConsts.CURRENCY);//货币类型
		accountMap.put("amount", YbUtils.formatAmount(amount));//充值金额
		accountMap.put("otherAmounttype", OtherAmountType.RECHARGE_FEE.value);//其他金额类型
		accountMap.put("otherAmount", 0+"");//其他金额
	
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);		
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("businessOrderNo",businessSeqNo);//订单流水号
		inBody.put("rType", RechargeType.CUSTOMER_CYBER_BANK_RECHARGE.value);//充值类型
		inBody.put("entrustflag", EntrustFlag.UNENTRUST.value);//委托标识
		inBody.put("payType", PayType.getEnum(payType).value);//支付方式
		inBody.put("bankAccountNo", "");//银行卡号
		inBody.put("busiBranchNo", "");
		inBody.put("secBankaccNo", "");
		inBody.put("note", "");
		inBody.put("platformAccountNo", "");
		inBody.put("deductType", "");
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.CUSTOMER_INTERNATBANK_RECHARGE.key, TradeType.CUSTOMER_RECHARGE_WITHDRAW.key, accountList, "accountList",null,null);
	}
	
	/**
	 * 企业网银充值
	 * @param businessSeqNo
	 * @param clientSn
	 * @param businessOrderNo
	 * @param payType
	 * @param bankAccountNo
	 * @param userId
	 * @param amount
	 * @return
	 * 
	 * @author liuyang
	 * @create 2017年12月18日
	 */
	public HashMap<String, JSONObject> enterpriseInternetbankRecharge(String businessSeqNo, String clientSn, String businessOrderNo, int payType, String cebitAccountNo, double amount) {
		
		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		
		accountMap.put("oderNo", "0");//序号
		accountMap.put("debitAccountNo", "");//借方台账账户
		accountMap.put("cebitAccountNo", cebitAccountNo);//贷方台账账户
		accountMap.put("currency", YbConsts.CURRENCY);//货币类型
		accountMap.put("amount", YbUtils.formatAmount(amount));//充值金额
		accountMap.put("otherAmounttype", OtherAmountType.RECHARGE_FEE.value);//其他金额类型
		accountMap.put("otherAmount", 0+"");//其他金额
	
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);		
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("businessOrderNo",businessSeqNo);//订单流水号
		inBody.put("rType", RechargeType.CUSTOMER_CYBER_BANK_RECHARGE.value);//充值类型
		inBody.put("entrustflag", EntrustFlag.UNENTRUST.value);//委托标识
		inBody.put("payType", PayType.getEnum(payType).value);//支付方式
		inBody.put("bankAccountNo", "");//银行卡号
		inBody.put("busiBranchNo", "");
		inBody.put("secBankaccNo", "");
		inBody.put("note", "");
		inBody.put("platformAccountNo", "");
		inBody.put("deductType", "");
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.ENTERPRISE_INTERNATBANK_RECHARGE.key, TradeType.CUSTOMER_RECHARGE_WITHDRAW.key, accountList, "accountList",null,null);
	}
	
	
/******************************************************************* niu *************************************************************************/
	
	/**
	 * 标的信息同步（O00001）
	 * 
	 * @author niu
	 * @create 2017.09.14
	 */
	public HashMap<String, JSONObject> bidInfoAysn(String clientSn, String businessSeqNo, t_bid bid, ServiceType serviceType, ProjectStatus projectStatus, List<Map<String, Object>> returnInfoList) {
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("busiTradeType", serviceType.key);//业务交易类型
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("objectId", bid.mer_bid_no);//标的编号
		inBody.put("objectName", bid.title);//标的名称
		inBody.put("totalAmount", YbUtils.formatAmount(bid.amount));//标的金额 decimal(17, 2)
		inBody.put("interestRate", bid.apr + "");//标的年化利率
		inBody.put("returnType", bid.getRepayment_type().value);//还款方式列表
		inBody.put("customerId", bid.user_id + "");//会员编号
		inBody.put("projectStatus", projectStatus.value);//标的状态
		inBody.put("nature", Nature.NET_LOAN.value);//标的属性：00：网贷 、01：非网贷（默认网贷）
		inBody.put("note", serviceType.note);//备注
		
		inBody.put("objectType", ObjectType.ORDINARY_OBJECT.value);//特殊标的字段，00-为普通标的  01-为特殊标的，特殊标的需要验密
		inBody.put("autoFlag", AutoFlag.NON_AUTO_INVEST.value);//00：非自动投标标的,01：自动投标标的 
		
		//return YbUtils.getRequest(clientSn, inBody, serviceType.key, TradeType.BID_INFO_SYNC.key, returnInfoList, "returnInfoList");
		return YbUtils.getRequestText(clientSn, inBody, serviceType.key, TradeType.BID_INFO_SYNC.key, returnInfoList, "returnInfoList", null, null);
	}
	
	/**
	 * 客户信息同步（U00001）
	 * 
	 * @author niu
	 * @create 2017.10.15
	 */
	public HashMap<String, JSONObject> custInfoAysn(Map<String, String> inBody, String clientSn, ServiceType serviceType) {
		
		return YbUtils.getRequestText(clientSn, inBody, serviceType.key, TradeType.CUSTOMER_INFO_SYNC.key, null, null, null, null);
	}
	
	/**
	 * 资金交易（T00002）
	 * 
	 * @author niu
	 * @create 2017.09.18
	 */
	public HashMap<String, JSONObject> fundTrade(String clientSn, String businessSeqNo, t_bid bid, ServiceType serviceType, EntrustFlag entrustFlag, List<Map<String, Object>> accountList, List<Map<String, Object>> contractList) {
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("busiTradeType", serviceType.key);//业务交易类型
		inBody.put("entrustflag", entrustFlag.value);//委托标识
		inBody.put("objectId", bid.mer_bid_no);//标的编号
		inBody.put("note", serviceType.note);//备注
		inBody.put("entrustPayFlag", "00");
		
		return YbUtils.getRequestText(clientSn, inBody, serviceType.key, TradeType.FUND_TRADE.key, accountList, "accountList", contractList, "contractList");
	}
	
	/**
	 * 校验交易密码
	 * 
	 * @author niu
	 * @create 2017.09.20
	 */
	public HashMap<String, String> checkTradePass(String businessSeqNo, long userId, String backURL, String userName, ServiceType serviceType, double amount, int type, String cardNo, String authtime, String authority) {
		
		//加签时间戳
		String signTime = System.currentTimeMillis() + "";
		//String backURL = BaseController.getBaseURL() + "payment/yibincallback/checkTradePassCB";
		
		//加签参数
		Map<String, String> signParam = new HashMap<String, String>();
		signParam.put("userId", userId + "");
		
		//加签
		String signature = YbUtils.getSignature(signTime, signParam, null, null, null);
		
		//参数组织
		HashMap<String, String> reqParam = new HashMap<String, String>();
		
		reqParam.put("businessSeqNo", businessSeqNo);
		reqParam.put("systemCode", YbConsts.MER_CODE);
		reqParam.put("userId", userId + "");
		reqParam.put("backURL", backURL);
		reqParam.put("signTime", signTime);
		reqParam.put("signature", signature);
		
		reqParam.put("userName", userName);
		reqParam.put("operType", serviceType.key);
		
		if (type == 1) {
			reqParam.put("cardNo", cardNo);
		}
		
		if (type == 2) {
			reqParam.put("amount", YbUtils.formatAmount(amount));
		}
		
		if (type == 3) {
			reqParam.put("authtime", authtime);
			reqParam.put("authority", authority);
		}
		
		
		return reqParam;
	}
	
	/**
	 * 交易状态查询（C00001）
	 * 
	 * @author niu
	 * @create 2017.09.25
	 */
	public HashMap<String, JSONObject> queryTradeStatus(String clientSn, String businessSeqNo, String oldbusinessSeqNo, String operType) {
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);
		inBody.put("oldbusinessSeqNo", oldbusinessSeqNo);
		inBody.put("operType", operType);
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.TRADE_STATUS_QUERY.key, TradeType.TRADE_STATUS_QUERY.key, null, null, null, null);
	}
	
	/**
	 * 数据信息查询（C00002）
	 * 
	 * @author niu
	 * @create 2017.09.27
	 */
	public HashMap<String, JSONObject> dataInfoQuery(String clientSn, String customerId, String accountNo, ServiceType serviceType) {
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("checkType", serviceType.key);
		inBody.put("customerId", customerId);
		inBody.put("accountNo", accountNo);
		
		inBody.put("beginDate", "");
		inBody.put("endDate", "");
		inBody.put("beginPage", "");
		inBody.put("endPage", "");
		inBody.put("showNum", "10");
		inBody.put("note", "");
		
		return YbUtils.getRequestText(clientSn, inBody, serviceType.key, TradeType.DATA_INFO_QUERY.key, null, null, null, null);
	}
	
	
	/**
	 * 打印、保存验密请求参数
	 * 
	 * @author niu
	 * @create 2017.09.20
	 */
	public static void printCheckPassReqParams(String clientSn, long userId, String businessSeqNo, String businessOrderNo, ServiceType serviceType, TradeType tradeType, String backURL, Map<String, String> checkPassReqParams, Map<String, String> businessReqParams) {
		
		StringBuffer info = new StringBuffer();
		
		info.append("****************** " + serviceType.note + " - 请求开始 ******************");
		info.append("\n");
		info.append("\n" + "                                                   " + "businessSeqNo : " + checkPassReqParams.get("businessSeqNo"));
		info.append("\n" + "                                                   " + "       userId : " + checkPassReqParams.get("userId"));
		info.append("\n" + "                                                   " + "     userName : " + checkPassReqParams.get("userName"));
		info.append("\n" + "                                                   " + "       amount : " + checkPassReqParams.get("amount"));	
		info.append("\n" + "                                                   " + "     operType : " + checkPassReqParams.get("operType"));
		info.append("\n" + "                                                   " + "   systemCode : " + checkPassReqParams.get("systemCode"));
		info.append("\n" + "                                                   " + "     signTime : " + checkPassReqParams.get("signTime"));
		info.append("\n" + "                                                   " + "      backURL : " + checkPassReqParams.get("backURL"));
		info.append("\n" + "                                                   " + "    signature : " + checkPassReqParams.get("signature"));
		info.append("\n");
		info.append("\n" + "                                                   " + "****************** " + serviceType.note + " - 请求结束 ******************");
		info.append("\n");
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info(info.toString());
		}
		//保存
		JPAUtil.transactionBegin();
		
		t_payment_request pr = new t_payment_request();
		
		pr.time = new Date();
		pr.service_order_no = businessSeqNo;
		pr.order_no = "";
		pr.user_id = userId;
		pr.mark = businessSeqNo;
		pr.setService_type(serviceType);
		pr.setPay_type(tradeType);
		pr.order_no = businessOrderNo;
		pr.setStatus(t_payment_request.Status.FAILED);  //先失败，后成功
		pr.ayns_url = backURL;
		pr.req_params = new Gson().toJson(checkPassReqParams);
		pr.req_map = new Gson().toJson(businessReqParams);
				
		paymentRequstDao.save(pr);
		
		JPAUtil.transactionCommit();
		
	}
	
	/**
	 * 打印、保存验密响应参数
	 * 
	 * @author niu
	 * @create 2017.09.20
	 */
	public static void printCheckPassRespParams(ServiceType serviceType, Map<String, String> checkPassRespParams) {
		
		StringBuffer info = new StringBuffer();
		
		info.append("****************** " + serviceType.note + " - 回调开始 ******************");
		info.append("\n");
		info.append("\n" + "                                                   " + "\0       托管业务类型  : " + serviceType.key);
		info.append("\n" + "                                                   " + "businessSeqNo : " + checkPassRespParams.get("businessSeqNo"));
		info.append("\n" + "                                                   " + "         flag : " + checkPassRespParams.get("flag"));
		info.append("\n" + "                                                   " + "     signTime : " + checkPassRespParams.get("signTime"));
		info.append("\n" + "                                                   " + "       userId : " + checkPassRespParams.get("userId"));
		info.append("\n" + "                                                   " + "    signature : " + checkPassRespParams.get("signature"));
		info.append("\n");
		info.append("\n" + "                                                   " + "****************** " + serviceType.note + " - 回调结束 ******************");
		info.append("\n");
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info(info.toString());
		}
		
		//保存
		JPAUtil.transactionBegin();
		
		t_payment_call_back pcb = new t_payment_call_back();
		pcb.time = new Date();
		pcb.request_mark = checkPassRespParams.get("businessSeqNo");
		pcb.cb_params = new Gson().toJson(checkPassRespParams);
		paymentCallBackDao.save(pcb);
		
		JPAUtil.transactionCommit();
	}
	
	
	
	
	/**
	 * 打印、保存异步回调报文参数
	 *
	 * @param des			业务提示
	 * @param cbParams 		异步报文
	 * @param serviceType   托管类型
	 * @param payType		接口类型
	 * @param remarkParams  备注参数
	 *
	 * @author LiuPengwei
	 * @createDate 2017年9月7日
	 */
	public void printOutCallBack(String desc, Map<String, String> cbParams, ServiceType serviceType, TradeType payType, 
			Map<String, String> remarkParams) {
		StringBuffer info = new StringBuffer();
		info.append("\n******************【"+desc+"】开始******************");
		if (serviceType != null) {
			info.append("\n托管业务类型：" + serviceType.value);
		}
		info.append("\n接口类型：" + payType.value);

		for(Entry<String, String> entry : cbParams.entrySet()){		
			try {
				info.append("\n" + entry.getKey() + "--" + java.net.URLDecoder.decode(entry.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LoggerUtil.error(false, e, "回调参数解码失败");
			}
		}
		
		info.append("******************【"+desc+"】结束******************");
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info(info.toString());
		}
		
		//托管业务唯一标识
		String mark = remarkParams.get(RemarkPramKey.MARK);
		
		JPAUtil.transactionBegin();
		
		t_payment_call_back pcb = new t_payment_call_back();
		pcb.time = new Date();
		pcb.request_mark = mark;
		pcb.cb_params = new Gson().toJson(cbParams);
		paymentCallBackDao.save(pcb);
		
		JPAUtil.transactionCommit();
		
		
	}
	
	
	
	/******************************************************************* niu *************************************************************************/
	/**
	 * 打印，保存回调响应参数
	 * 
	 * @author niu
	 * @create 2017.09.05
	 */
	public static void printResponseParams(String desc, String clientSn, String responseString, ServiceType serviceType, TradeType tradeType) {
		
		//解析响应报文
		if (responseString == null || responseString.isEmpty()) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("响应报文打印，保存失败！");
			}
			
			return;
		}
		
		JSONObject respMsg =  JSONObject.fromObject(responseString);
		
		if (respMsg == null) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("响应报文打印，保存失败！");
			}
			return;
		}
		
		Object outBody = respMsg.get("outBody");
		Object respHeader = respMsg.get("respHeader");
		
		Map<String, String> outBodyMap = null;
		Map<String, String> respHeaderMap = null;
		
		if (serviceType.code == 30 || serviceType.code == 32 || serviceType.code == 24 || serviceType.code == 25 || serviceType.code == 8 || serviceType.code == 9
				|| serviceType.code == 31 || serviceType.code == 22) {
			
			if (respHeader == null) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info("响应报文打印，保存失败！");
				}
				return;
			}
			
			respHeaderMap = YbUtils.jsonToMap(respHeader.toString());
			
			if (respHeaderMap == null) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info("响应报文打印，保存失败！");
				}
				return;
			}
		} else {
			
			if (outBody == null || respHeader == null) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info("响应报文打印，保存失败！");
				}
				return;
			}
			
			outBodyMap = YbUtils.jsonToMap(outBody.toString());
			respHeaderMap = YbUtils.jsonToMap(respHeader.toString());
			
			if (outBodyMap == null || respHeaderMap == null) {
				if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
					Logger.info("响应报文打印，保存失败！");
				}
				return;
			}
		}
		
		
		//保存	
		JPAUtil.transactionBegin();
		
		t_payment_call_back cb = new t_payment_call_back();
		
		cb.time = new Date();
		cb.request_mark = clientSn;
		cb.cb_params = responseString;
		
		paymentCallBackDao.save(cb);
		
		JPAUtil.transactionCommit();
	}
	
	/**
	 * 打印、保存接口请求参数
	 * 
	 * @author niu
	 * @create 2017.09.05
	 */
	public static void printRequestParams(String clientSn, long userId, String businessSeqNo, String businessOrderNo, HashMap<String, JSONObject> requestBody, ServiceType serviceType, TradeType tradeType) {
		
		//解析请求报文
		if (requestBody == null || requestBody.isEmpty()) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("响应报文打印，保存失败！");
			}
			return;
		}
		
		JSONObject inBodyJson = requestBody.get("inBody");
		JSONObject reqHeaderJson = requestBody.get("reqHeader");
		
		if (inBodyJson == null || inBodyJson.isEmpty() || reqHeaderJson == null || reqHeaderJson.isEmpty()) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("响应报文打印，保存失败！");
			}
			return;
		}
		
		Map<String, String> inBody = YbUtils.jsonToMap(inBodyJson.toString());
		Map<String, String> reqHeader = YbUtils.jsonToMap(reqHeaderJson.toString());
		
		if (inBody == null || inBody.isEmpty() || reqHeader == null || reqHeader.isEmpty()) {
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("响应报文打印，保存失败！");
			}
			return;
		}
				
		
		//保存	
		JPAUtil.transactionBegin();
		
		t_payment_request request = new t_payment_request();
		
		request.time = new Date();
		request.user_id = userId;
		request.setService_type(serviceType);
		request.service_order_no = businessSeqNo;
		request.order_no = businessOrderNo;
		request.setPay_type(tradeType);
		request.setStatus(t_payment_request.Status.FAILED);
		request.ayns_url = "";
		request.req_params = new Gson().toJson(requestBody);
		request.mark = clientSn;
		request.req_map =  new Gson().toJson(inBody);
		
		paymentRequstDao.save(request);
		
		JPAUtil.transactionCommit();
		
	}
	
	/**
	 * 打印、保存接口回调参数
	 *
	 * @param cbParams
	 * @param serviceType
	 * @param payType
	 *
	 * @author liuyang
	 * @createDate 2017年9月7日
	 */
	public void printCallBackData(String desc, Map<String, String> cbParams, ServiceType serviceType, TradeType payType) {
		StringBuffer info = new StringBuffer();
		info.append("\n******************【"+desc+"】开始******************");
		if (serviceType != null) {
			info.append("\n托管业务类型：" + serviceType.value);
		}
		info.append("\n接口类型：" + payType.value);

		for(Entry<String, String> entry : cbParams.entrySet()){		
			try {
				info.append("\n" + entry.getKey() + "--" + java.net.URLDecoder.decode(entry.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LoggerUtil.error(false, e, "回调参数解码失败");
			}
		}
		
		info.append("******************【"+desc+"】结束******************");
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info(info.toString());
		}
		
		
		JPAUtil.transactionBegin();
		
		t_payment_call_back pcb = new t_payment_call_back();
		pcb.time = new Date();
		pcb.request_mark = "";
		pcb.cb_params = new Gson().toJson(cbParams);
		paymentCallBackDao.save(pcb);
		
		JPAUtil.transactionCommit();
		
		
	}
	
	/**
	 * 打印、保存接口回调参数
	 *
	 * @param cbParams
	 * @param serviceType
	 * @param payType
	 *
	 * @author liuyang
	 * @createDate 2017年9月7日
	 */
	public void printCallBackDataS(String desc, Map<String, String> cbParams, ServiceType serviceType, TradeType payType) {
		StringBuffer info = new StringBuffer();
		info.append("\n******************【"+desc+"】开始******************");
		if (serviceType != null) {
			info.append("\n托管业务类型：" + serviceType.value);
		}
		info.append("\n接口类型：" + payType.value);

		for(Entry<String, String> entry : cbParams.entrySet()){		
			try {
				info.append("\n" + entry.getKey() + "--" + java.net.URLDecoder.decode(entry.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LoggerUtil.error(false, e, "回调参数解码失败");
			}
		}
		
		info.append("******************【"+desc+"】结束******************");
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info(info.toString());
		}
		
		JPAUtil.transactionBegin();
		
		t_payment_call_back pcb = new t_payment_call_back();
		pcb.time = new Date();
		pcb.request_mark = cbParams.get("clientSn");
		pcb.cb_params = new Gson().toJson(cbParams);
		paymentCallBackDao.save(pcb);
		
		JPAUtil.transactionCommit();
		
		
	}
	
	
	/**
	 * 打印、保存接口请求参数
	 *
	 * @param requestMark 托管请求唯一标识
	 * @param userId 关联用户id
	 * @param serviceOrderNo 业务订单号
	 * @param orderNo 交易订单号
	 * @param serviceType 消息类型
	 * @param payType 汇付操作类型
	 * @param params 参数
	 *
	 * @author liuyang
	 * @createDate 2017年9月11日
	 */
	public void printRequestData(String requestMark, long userId, String serviceOrderNo, String orderNo, 
			ServiceType serviceType, TradeType payType, Map<String, String> reqParams, Map<String, String> remarkParams) {
		StringBuffer info = new StringBuffer();
		info.append("\n******************"+serviceType.note+"请求开始******************");
		info.append("\n托管业务类型：" + serviceType.note);
		info.append("\n接口类型：" + payType.value);

		for(Entry<String, String> entry : reqParams.entrySet()){		
			info.append("\n" + entry.getKey() + "--" + entry.getValue());
		}
		
		info.append("\n******************"+serviceType.note+"请求结束******************");
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info(info.toString());
		}
		
		JPAUtil.transactionBegin();
		
		t_payment_request pr = new t_payment_request();
		
		pr.time = new Date();
		pr.user_id = userId;
		pr.service_order_no = serviceOrderNo;
		pr.setService_type(serviceType);
		pr.order_no = orderNo;
		pr.setPay_type(payType);
		pr.setStatus(t_payment_request.Status.FAILED);  //先失败，后成功
		pr.ayns_url = "";
		pr.req_params = new Gson().toJson(reqParams);
		pr.mark = requestMark;
		pr.req_map = new Gson().toJson(remarkParams);
		
		paymentRequstDao.save(pr);
		
		JPAUtil.transactionCommit();
		
	}
	
	/**
	 * 打印、保存接口请求参数
	 *
	 * @param requestMark 托管请求唯一标识
	 * @param userId 关联用户id
	 * @param serviceOrderNo 业务订单号
	 * @param orderNo 交易订单号
	 * @param serviceType 消息类型
	 * @param payType 汇付操作类型
	 * @param params 参数
	 *
	 * @author LiuPengwei
	 * @createDate 2017年9月11日
	 */
	public void printRequestDataS(String requestMark, long userId, String serviceOrderNo, String orderNo, 
			ServiceType serviceType, TradeType payType, HashMap<String, JSONObject> reqParams, Map<String, String> remarkParams) {
		StringBuffer info = new StringBuffer();
		info.append("\n******************"+serviceType.note+"请求开始******************");
		info.append("\n托管业务类型：" + serviceType.note);
		info.append("\n接口类型：" + payType.value);

		for(Entry<String, JSONObject> entry : reqParams.entrySet()){		
			info.append("\n" + entry.getKey() + "--" + entry.getValue());
		}
		
		info.append("\n******************"+serviceType.note+"请求结束******************");
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info(info.toString());
		}
		
		
		JPAUtil.transactionBegin();
		
		t_payment_request pr = new t_payment_request();
		
		pr.time = new Date();
		pr.user_id = userId;
		pr.service_order_no = serviceOrderNo;
		pr.setService_type(serviceType);
		pr.order_no = orderNo;
		pr.setPay_type(payType);
		pr.setStatus(t_payment_request.Status.FAILED);  //先失败，后成功
		pr.ayns_url = "";
		pr.req_params = new Gson().toJson(reqParams);
		pr.mark = requestMark;
		pr.req_map = new Gson().toJson(remarkParams);
		
		paymentRequstDao.save(pr);
		
		JPAUtil.transactionCommit();
		
	}
	
	
	/**
	 * 
	* 
	* @Description: TODO(委托验密回调) 
	* @author lihuijun 
	* @date 2017年9月26日 上午10:41:18 
	*
	*/
	public HashMap<String, String> autoCheckPassword(long userId, String businessSeqNo,ServiceType serviceType, String valid_time, double amount) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		//获取时间戳
		String signTime = System.currentTimeMillis() + "";
		
		//签名
		LinkedHashMap<String, String> inBody = new LinkedHashMap<>();
		inBody.put("userId", "" + userId);
		String signature = YbUtils.getSignature(signTime, inBody, null, null, null);
		String backURL=BaseController.getBaseURL() + "payment/yibincallback/autoCheckPasswordSyn";
		if(serviceType.code == 9){
			backURL=BaseController.getBaseURL() + "payment/yibincallback/closeAutoCheckPasswordSyn";
		}
		map.put("systemCode", YbConsts.MERCHANT_CODE); // 平台号
		map.put("userId", "" + userId); // 会员编号
		map.put("backURL", backURL); // 回调地址
		map.put("signTime", signTime); // 时间戳
		map.put("signature", signature); // 签名
		map.put("businessSeqNo", businessSeqNo); // 业务流水号
		
		
		map.put("userName", userInfo.reality_name);
		map.put("operType", "B04"); // 委托协议同意标识
		if(serviceType.code==9){
			map.put("operType", "B05"); // 委托协议同意标识
		}
		map.put("authAmount", amount +"");
		map.put("authTime", valid_time);
		return map;
	}
	
	/**
	* 
	* 
	* @Description: TODO(开启自动投标设置) 
	* @author lihuijun
	* @date 2017年9月25日 下午3:44:01 
	*
	*/
	public HashMap<String,JSONObject> autoInvestSignature(String clientSn, String businessSeqNo, long userId,ServiceType serviceType,
			String protocolNo, double invest_amt, String valid_time){

		
		LinkedHashMap<String, String> inBody = new LinkedHashMap<>();
			//放入参数
			inBody.put("customerId", userId+"");
			inBody.put("businessSeqNo", businessSeqNo);//业务流水号
			inBody.put("fundTradetype", "T01");//资金操作类型
			inBody.put("protocolNo", protocolNo);//协议号
			if(serviceType.code==8){
				inBody.put("note", "签署委托投标");
				inBody.put("busiTradeType","B04");//委托协议签署
				
			}else if(serviceType.code==9){
				inBody.put("note", "解除委托投标");
				inBody.put("busiTradeType","B05");//委托协议解除
				
			}
			
			inBody.put("authAmount", invest_amt +"");//投资金额
			inBody.put("authTime", valid_time);//失效时间
			
			
		return YbUtils.getRequestText(clientSn, inBody, serviceType.key, TradeType.CUSTOMER_ENTRUST_PROTOCOL.key, null, null, null, null);
	}
	
	/**
	 * 用户校验交易密码接口，参数拼装(银行卡预留手机号)
	 *
	 * @param userId 用户id
	 * @param businessSeqNo 业务流水号
	 * @return
	 *
	 * @author guoShiJie
	 * @createDate 2018年04月25日
	 */
	public HashMap<String, String> bindCardCheckPassword1(long userId, String businessSeqNo, ServiceType serviceType,String mobilePhone) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		//获取时间戳
		String signTime = System.currentTimeMillis() + "";
		
		//签名
		LinkedHashMap<String, String> inBody = new LinkedHashMap<>();
		inBody.put("userId", "" + userId);
		String signature = YbUtils.getSignature(signTime, inBody, null, null, null);
		String backURL =null;
		
		backURL = BaseController.getBaseURL() + "payment/yibincallback/checkPasswordSyn1?mobilePhone="+mobilePhone;
		
		map.put("systemCode", YbConsts.MERCHANT_CODE); // 平台号
		map.put("userId", "" + userId); // 会员编号
		map.put("backURL", backURL); // 回调地址
		map.put("signTime", signTime); // 时间戳
		map.put("signature", signature); // 签名
		map.put("businessSeqNo", businessSeqNo); // 业务流水号
		//map.put("userName", userName); // 客户名称
		//map.put("bankNo", bankNo); // 银行卡号
		map.put("operType", "B01"); // 交易类型
		map.put("mobilePhone", mobilePhone);//银行卡预留手机号
		return map;
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
	public HashMap<String, JSONObject> reversalTransfer(String businessSeqNo, String clientSn, String debitAccountNo,
			String financierAccountNo, double financierFee, String oldbusinessSeqNo, String bidNo) {
		//融资人金额
		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		accountMap.put("oderNo", "0");//序号
		accountMap.put("oldbusinessSeqNo",oldbusinessSeqNo);//原业务流水号
		accountMap.put("oldOderNo","0");//原序列号
		accountMap.put("debitAccountNo",financierAccountNo);//转让方台账账号
		accountMap.put("cebitAccountNo",debitAccountNo);//受让方台账账号
		accountMap.put("currency",YbConsts.CURRENCY );//币种
		accountMap.put("amount",financierFee+"");//交易金额
		accountMap.put("summaryCode",ServiceType.WASHED.key);//摘要码
		
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);
		
		LinkedHashMap<String, Object> contractMaps = new LinkedHashMap<String, Object>();
		contractMaps.put("oderNo", "2");//序号
		contractMaps.put("contractType","");//合同类型
		contractMaps.put("contractRole","");//角色
		contractMaps.put("contractFileNm","");//合同文件名
		contractMaps.put("debitUserid","");//受让方用户Id
		contractMaps.put("cebitUserid","");//转让方用户Id
		
		
		//合同文件名列表
		List<Map<String, Object>>contractList = new ArrayList<Map<String, Object>>();
		contractList.add(contractMaps);
		
		//请求报文体
		LinkedHashMap<String, String> inBody = new LinkedHashMap<String, String>();
		
		//放入必填参数
		inBody.put("businessSeqNo", businessSeqNo);//业务流水号
		inBody.put("busiTradeType", ServiceType.WASHED.key);//业务操作类型
		inBody.put("entrustflag", EntrustFlag.UNENTRUST.value);//委托标识
		inBody.put("objectId", bidNo);//标的编号
		inBody.put("note", "转让冲正");//备注
		
		return YbUtils.getRequestText(clientSn, inBody, ServiceType.WASHED.key, TradeType.FUND_TRADE.key, accountList, "accountList",contractList,"contractList");
	}
	
}

































