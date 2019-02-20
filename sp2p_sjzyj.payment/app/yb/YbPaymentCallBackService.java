package yb;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;

import com.ning.http.client.Response;
import com.shove.Convert;

import common.constants.RemarkPramKey;
import common.enums.Client;
import common.utils.BankInfoUtil;
import common.utils.Factory;
import common.utils.Http;
import common.utils.HttpUtils;
import common.utils.LoggerUtil;
import common.utils.Md5Utils;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import controllers.common.BaseController;
import controllers.front.account.MyAccountCtrl;
import daos.PaymentCallBackDao;
import daos.PaymentRequstDao;
import models.common.entity.t_bank_card_user;
import models.common.entity.t_cost;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.entity.t_bid;
import models.entity.t_payment_call_back;
import models.entity.t_payment_request;
import net.sf.json.JSONObject;
import payment.impl.PaymentProxy;
import play.Logger;
import play.mvc.Scope.Params;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.core.DebtService;
import yb.YbPaymentService;
import yb.enums.ProjectStatus;
import yb.enums.ServiceType;
import yb.enums.TradeType;
import yb.enums.YbPayType;
public class YbPaymentCallBackService extends YbPaymentService {
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static PaymentCallBackDao paymentCallBackDao = Factory.getDao(PaymentCallBackDao.class);
	
	protected static PaymentRequstDao paymentRequstDao = Factory.getDao(PaymentRequstDao.class);
	
	protected static DebtService debtService = Factory.getService(DebtService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	/**
	 * 获取设备类型
	 * @author LiuPengwei
	 * @create 2017年9月19日
	 */
	public ServiceType getServiceType(String businessSeqNo) {
		
		t_payment_request pr = paymentRequstDao.findType(businessSeqNo);
		if (pr == null) {
			LoggerUtil.info(false, "查询托管请求记录失败");
			
			return null;
		}
		
		return pr.getService_type();
	}
	
	
	
	/**
	 * 开户回调
	 *
	 * @param 宜宾银行开户号
	 * @param 真实姓名
	 * @param 身份证号
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年9月19日
	 */
	public ResultInfo userRegister(long userId,TreeMap<String, String> cbParams ,String realName, String idNumber,YbPayType ybPayType,String businessSeqNo) {
		
		ResultInfo result = new ResultInfo();
		
		//仿重单处理;
		result = this.checkSign(businessSeqNo, ybPayType.USERREGISTER);
		
		if(result.code < 1){
			return result;
		}
		
		result = userFundService.doCreateAccount(userId, cbParams.get("accNo"), realName, idNumber);
		if (result.code < 1) {
			
			return result;
		}
        
		result.code = 1;
		result.msg = "开户成功";
		
		return result;
	}
	
	
	/**
	 * 企业回调
	 *
	 * @param 宜宾银行开户号
	 * @param 法人真实姓名
	 * @param 身份证号
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年9月19日
	 */
	public ResultInfo enterpriseRegister(long userId, TreeMap<String, String> cbParams ,String realName,
			String companyName, String uniSocCreCode,YbPayType ybPayType, String businessSeqNo, String bankNo, String idCard) {
		
		ResultInfo result = new ResultInfo();
		
		//仿重单处理;
		result = this.checkSign(businessSeqNo, ybPayType.USERREGISTER);
		
		if(result.code < 1){
			return result;
		}
		
		result = userFundService.doCreateAccount(userId, cbParams.get("accNo"), realName ,companyName, uniSocCreCode, bankNo, idCard);
		if (result.code < 1) {
			
			return result;
		}
        
		result.code = 1;
		result.msg = "开户成功";
		
		return result;
	}
	
	/**
	 * 充值回调
	 * @param cbParams 回调参数
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月8日
	 */
	public ResultInfo netSave(Map<String, String> cbParams,Map<String, String> remarkParams) {
		ResultInfo result = new ResultInfo();
	
		//签名，状态码，仿重单处理;
		result = this.checkSign(cbParams.get("oldbusinessSeqNo"), YbPayType.NETSAVE);
		if(result.code < 1){
			
			return result;
		}

		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		double rechargeAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.RECHARGE_AMT));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);

		result = userFundService.doRecharge(userId, rechargeAmt, serviceOrderNo);
			
		if (result.code < 1) {
			
			return result;
		}
		
		result.code = 1;
		result.msg = "充值成功";
		
		return result;
	}
	
	/**
	 * 提现回调业务处理
	 *
	 * @param cbParams
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月11日
	 */
	public ResultInfo withdrawal(Map<String, String> cbParams, Map<String, String> remarkParams) {
		ResultInfo result;
		
		
		//签名，状态码，仿重单处理;
		result = this.checkSign(cbParams.get("oldbusinessSeqNo"), YbPayType.CASH);
		if(result.code < 1){
			
			return result;
		}
		
		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		double withdrawalAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.WITHDRAWAL_AMT));
		double withdrawalFee = Double.parseDouble(remarkParams.get(RemarkPramKey.WITHDRAWAL_FEE));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);
		
		result = userFundService.doWithdrawal(userId, withdrawalAmt, withdrawalFee, serviceOrderNo);
		if (result.code < 1) {
			
			return result;
		}
		
		//用户资金查询
		t_user_fund userFund = userFundService.findByColumn("user_id = ?", userId);
		
		//手续费为0且余额为0,进行注销操作
		if (withdrawalFee == 0 && userFund.balance == 0){
			
			String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.CUSTOMER_CANCEL);
			
			Map<String, String> inBody = new HashMap<String, String>();
			inBody.put("customerId", userId+"");
			inBody.put("businessSeqNo", businessSeqNo);
			inBody.put("busiTradeType", ServiceType.CUSTOMER_CANCEL.key);
			
			result = PaymentProxy.getInstance().custInfoAysn(inBody, OrderNoUtil.getClientSn(), ServiceType.CUSTOMER_CANCEL);
			if (result.code > 0) {
				result = userFundService.custCancel(userId);
				if (result.code > 0) {
					Logger.info("注销成功！");
				}else{
					Logger.info("注销失败！");
				}
			}
		}
		
		// 查询费用账户可用余额
		String businessSeqNo3 = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);
		
		ResultInfo result3 = PaymentProxy.getInstance().queryCostBalance(Client.PC.code, businessSeqNo3);
		if (result3.code < 1) {
			result.msg = "查询费用账户可用余额异常";
			result.code = -1;
			MyAccountCtrl.withdrawPre();
		}
		
		Map<String, Object> merDetail3 = (Map<String, Object>) result3.obj;
		double cosBalance = Convert.strToDouble(merDetail3.get("pBlance").toString(), 0);
		
		// 保存费用账户明细
		t_cost cost = new t_cost();
		cost.balance = cosBalance;
		cost.amount = withdrawalFee;
		cost.type = 0;
		cost.sort = 2;
		cost.time = new Date();
		cost.save();
		
		result.code = 1;
		result.msg = "提现成功";
		
		return result;
	}
	
	
	
	/**
	 * 债权转让(资金托管业务回调)
	 *
	 * @param cbParams
	 * @return 失败会将debtId返回
	 *
	 * @author LiuPengwei
	 * @createDate 2016年3月29日
	 */
	public ResultInfo debtTransfer(String businessSeqNo, Long debtId, Long toUserId, double debtFee) {
		
		ResultInfo result = this.checkSign(businessSeqNo, YbPayType.CREDITASSIGN);
		if (result.code < 1) {
			
			return result;
		}
		
		return debtService.doDebtTransfer(businessSeqNo,debtId,toUserId,debtFee);
	}
	
	
	/**********************************************************************   **********************************************************************/
	
	
	public ResultInfo checkGuaranteeGU(Params params, ServiceType serviceType) {

		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = getRespParams(params);
		
		//打印、保存接口回调参数
		printCheckPassRespParams(serviceType, cbParams);
		
		String businessSeqNo = cbParams.get("businessSeqNo");
		String flag = cbParams.get("flag");
		
		if(flag.equals("2")) {
			result.code = -1;
			result.msg  = cbParams.get("校验交易密码失败");
			
		}else{
			
			result.code = 1;
			result.msg  = businessSeqNo;
		}
		
		return result;
	}
	
	/**
	 * 校验交易密码 - 回调
	 * 
	 * @author niu
	 * @create 2017.09.22
	 */
	public ResultInfo checkTradePassCB(Params params, ServiceType serviceType) {
		
		ResultInfo result = new ResultInfo();
		
		//获取回调参数
		Map<String, String> cbParams = getRespParams(params);
		//打印、保存回调参数
		printCheckPassRespParams(serviceType, cbParams);
		
		//验签
		String userId = cbParams.get("userId");
		String signTime = cbParams.get("signTime");
		String toVerifyData = signTime + "|" + userId;
		
		boolean varifyRes = YbUtils.varifySign(toVerifyData, cbParams.get("signature"));
		
		if (!varifyRes) {
			result.code = -1;
			result.msg  = serviceType.note + "验密验签失败";
			
			return result;
		}
		
		if (!cbParams.get("flag").equals("1")) {
			result.code = -1;
			result.msg  = cbParams.get("校验交易密码失败");
			
			return result;
		}
		
		String businessSeqNo = cbParams.get("businessSeqNo");
		t_payment_request pr = paymentRequstDao.findByColumn("mark = ?", businessSeqNo);
		if (pr == null) {
			result.code = -1;
			result.msg  = "业务请求参数查询失败";
			
			return result;
		}
		
		int row = paymentRequstDao.updateReqStatus(businessSeqNo);
		
		if (row == 0) {
			result.code = ResultInfo.ALREADY_RUN;
			result.msg = serviceType.value + "已成功执行";	
			
			return result;
		}
		
		if (row < 0) {
			result.code = ResultInfo.ERROR_SQL;
			result.msg = serviceType.value + "更新请求状态时，数据库异常";	
			
			return result;
		}
		
		result.code = 1;
		result.msg  = businessSeqNo;
		result.msgs = YbUtils.jsonToMap(pr.req_map);
				
		return result;
	}
	
	/**
	 * 还款 - 回调业务
	 * 
	 * @author niu
	 * @create 2017.09.25
	 */
	public ResultInfo repaymentCBS() {
		
		
		
		ResultInfo result = new ResultInfo();
		return result;
	}
	
	/**
	 * 出款 - 回调业务
	 * 
	 * @author niu
	 * @create 2017.09.25
	 */
	public ResultInfo paymentCBS() {
		
		
		
		ResultInfo result = new ResultInfo();
		return result;
	}
	
	
	/**
	 * 标的信息同步 - 回调业务
	 * 
	 * @author niu
	 * @create 2017.10.09
	 */
	public ResultInfo bidInfoAysnCBS(String respBody, t_bid bid, ProjectStatus projectStatus) {
		
		//验签
		
		
		//更新请求状态
		
		//回调业务处理
		switch (projectStatus.code) {
		case 1://发布
			
			break;
		case 2://投资
			
			break;
		case 3://放款
			
			break;
		case 4://流标
			
			break;
		case 5://撤标
			
			break;
		case 6://还款
			
			break;
		case 7://结束
			
			break;
		}
		
		return null;
	}
	
	
	
	
	
	
	
	
	/**
	 * 防止重单：更新请求状态
	 * 
	 * @author niu
	 * @create 2017.10.09
	 */
	public static ResultInfo updateRequestStatus(String clientSn) {
		
		ResultInfo result = new ResultInfo();
		
		if (clientSn == null || clientSn.isEmpty()) {
			result.code = -1;
			result.msg  = "客户段流水号不能为空";
			
			return result;
		}
		
		//查询托管请求记录
		t_payment_request pr = paymentRequstDao.findRequestRecord(clientSn);
		
		if (pr == null) {
			result.code = -1;
			result.msg  = "查询托管请求记录失败";
			
			return result;
		}
		
		//更新请求状态
		int row = paymentRequstDao.updateReqStatusToSuccess(clientSn);
		
		if (row < 0) {
			result.code = ResultInfo.ERROR_SQL;
			result.msg  = "更新请求状态时，数据库异常！";
			
			return result;
		}
		
		if (row == 0) {
			result.code = ResultInfo.ALREADY_RUN;
			result.msg  = "已成功执行！";
			
			return result;
		}
		
		result.code = 1;
		result.msg  = "更新请求状态成功！";
		
		return result;
	}
	
	/**
	 * 用户跳转网银充值页面，参数拼装
	 *
	 * @param userId 用户id
	 * @param businessSeqNo 业务流水号
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年10月24日
	 */
	public HashMap<String, String> toRechargerByCpcn(long userId, String businessSeqNo, String rechargeAmt) {
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		t_user_info userInfo = userInfoService.findByColumn("user_id=?", userId);

		//获取时间戳
		String signTime = System.currentTimeMillis() + "";
		
		//参数拼接成字符串
		StringBuffer sign = new StringBuffer();
		sign.append("ACCT_NAME="+userInfo.reality_name);
		sign.append("&ACCT_NO="+userFund.payment_account);
		sign.append("&B_ACCT_NAME="+"天风天财(武汉)金融信息服务有限公司");
		sign.append("&B_ACCT_NO="+YbConsts.B_ACCT_NO);
		sign.append("&COMM_NO="+"002777");
		sign.append("&GOODS_NAME="+"网银充值");
		sign.append("&ORDER_NO="+businessSeqNo);
		sign.append("&OUT_ID="+businessSeqNo);
		sign.append("&PAY_AMT="+rechargeAmt);
		sign.append("&SUB_SOURCE_TYPE="+"OYPH");
		sign.append("&TRAN_TIME="+signTime);
		sign.append("&USER_IP="+BaseController.getIp());
		sign.append("&key="+YbConsts.B_KEY);
		
		String signs = (Md5Utils.GetMD5Code(sign.toString())).toLowerCase();
		
		map.put("OUT_ID", businessSeqNo); // 交易流水号
		map.put("TRAN_TIME", signTime); // 交易时间
		map.put("GOODS_NAME", "网银充值"); // 商品名称
		map.put("COMM_NO", "002777"); // 商户编号
		map.put("FRONT_URL", YbConsts.FRONT_URL); // 回调页面
		map.put("ORDER_NO", businessSeqNo); // 订单号
		map.put("USER_IP", BaseController.getIp()); // 用户IP
		map.put("ACCT_NO", userFund.payment_account); // 收款人账户
		map.put("ACCT_NAME", userInfo.reality_name); // 收款人账户名称
		map.put("B_ACCT_NO", YbConsts.B_ACCT_NO); // 对公存管户账号
		map.put("B_ACCT_NAME", "天风天财(武汉)金融信息服务有限公司"); // 对公存管户名称
		map.put("SUB_SOURCE_TYPE", "OYPH"); // 子渠道编号
		map.put("PAY_AMT", rechargeAmt); // 订单金额
		if(userInfo.enterprise_name == null || userInfo.enterprise_name.equals("")) {
			map.put("BC_FLAG", "2"); // 对公对私标志(个人2 企业1)
		}else {
			map.put("BC_FLAG", "1"); // 对公对私标志(个人2 企业1)
		}
		map.put("SIGNATURE", signs); // 签名
		
		return map;
	}
	
	/**
	 * 企业跳转网银充值页面，参数拼装
	 *
	 * @param userId 用户id
	 * @param businessSeqNo 业务流水号
	 * @return
	 *
	 * @author liu
	 * @createDate 2017年12月18日
	 */
	public HashMap<String, String> toEnterpriseRechargerByCpcn(long userId, String businessSeqNo, double rechargeAmt, int flags) {
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		t_user_info userInfo = userInfoService.findByColumn("user_id=?", userId);

		//获取时间戳
		String signTime = System.currentTimeMillis() + "";
		
		//参数拼接成字符串
		StringBuffer sign = new StringBuffer();
		sign.append("ACCT_NAME="+userInfo.reality_name);
		sign.append("&ACCT_NO="+userFund.payment_account);
		sign.append("&B_ACCT_NAME="+"天风天财(武汉)金融信息服务有限公司");
		sign.append("&B_ACCT_NO="+YbConsts.B_ACCT_NO);
		sign.append("&COMM_NO="+"002777");
		sign.append("&GOODS_NAME="+"网银充值");
		sign.append("&ORDER_NO="+businessSeqNo);
		sign.append("&OUT_ID="+businessSeqNo);
		sign.append("&PAY_AMT="+rechargeAmt);
		sign.append("&SUB_SOURCE_TYPE="+"OYPH");
		sign.append("&TRAN_TIME="+signTime);
		sign.append("&USER_IP="+BaseController.getIp());
		sign.append("&key="+YbConsts.B_KEY);
		
		String signs = (Md5Utils.GetMD5Code(sign.toString())).toLowerCase();
		
		map.put("OUT_ID", businessSeqNo); // 交易流水号
		map.put("TRAN_TIME", signTime); // 交易时间
		map.put("GOODS_NAME", "网银充值"); // 商品名称
		map.put("COMM_NO", "002777"); // 商户编号
		map.put("FRONT_URL", YbConsts.FRONT_URL); // 回调页面
		map.put("ORDER_NO", businessSeqNo); // 订单号
		map.put("USER_IP", BaseController.getIp()); // 用户IP
		map.put("ACCT_NO", userFund.payment_account); // 收款人账户
		map.put("ACCT_NAME", userInfo.reality_name); // 收款人账户名称
		map.put("B_ACCT_NO", YbConsts.B_ACCT_NO); // 对公存管户账号
		map.put("B_ACCT_NAME", "天风天财(武汉)金融信息服务有限公司"); // 对公存管户名称
		map.put("SUB_SOURCE_TYPE", "OYPH"); // 子渠道编号
		map.put("PAY_AMT", rechargeAmt+""); // 订单金额
		if(flags == 3 || flags == 5) {
			map.put("BC_FLAG", "2"); // 对公对私标志(个人2 企业1)
		}else {
			map.put("BC_FLAG", "1"); // 对公对私标志(个人2 企业1)
		}
		map.put("SIGNATURE", signs); // 签名
		
		return map;
	}
	
	/**
	 * 网银充值回调
	 * @param cbParams 回调参数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年10月26日
	 */
	public ResultInfo internetbankNetSave(Map<String, String> cbParams,Map<String, String> remarkParams) {
		ResultInfo result = new ResultInfo();
	
		//签名，状态码，仿重单处理;
		result = this.checkSign(cbParams.get("oldbusinessSeqNo"), YbPayType.NETSAVE);
		if(result.code < 1){
			
			return result;
		}

		long userId = Long.parseLong(remarkParams.get(RemarkPramKey.USER_ID));
		double rechargeAmt = Double.parseDouble(remarkParams.get(RemarkPramKey.RECHARGE_AMT));
		String serviceOrderNo = remarkParams.get(RemarkPramKey.SERVICE_ORDER_NO);

		result = userFundService.internetDoRecharge(userId, rechargeAmt, serviceOrderNo);
			
		if (result.code < 1) {
			
			return result;
		}
		
		result.code = 1;
		result.msg = "网银充值成功";
		
		return result;
	}
	
	public ResultInfo updateReqStatus(String clientSn, ServiceType serviceType) {
		
		ResultInfo result = new ResultInfo();
		
		t_payment_request pr = paymentRequstDao.findByColumn("mark = ?", clientSn);
		if (pr == null) {
			result.code = -1;
			result.msg  = "业务请求参数查询失败";
			
			return result;
		}
		
		int row = paymentRequstDao.updateReqStatus(clientSn);
		
		if (row == 0) {
			result.code = ResultInfo.ALREADY_RUN;
			result.msg = serviceType.note + "已成功执行";	
			
			return result;
		}
		
		if (row < 0) {
			result.code = ResultInfo.ERROR_SQL;
			result.msg = serviceType.note + "更新请求状态时，数据库异常";	
			
			return result;
		}
		
		result.code = 1;
		result.msg = "请求状态修改成功";
		
		return result;
	}
	
	/**
	 * 绑定银行卡回调
	 * @param userIds 用户id
	 * @param bankNos 银行卡号
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年01月27日
	 */
	public ResultInfo userBindCard(String userIds, String bankNos) {
		
		ResultInfo result = new ResultInfo();
		
		t_bank_card_user bankC = new t_bank_card_user();
		
		long userId = Long.parseLong(userIds);
		
		String bankNo = null;
		/*String bankName = null;
		bankNo = bankNos.substring(0,8);
		bankName = BankInfoUtil.getNameOfBank(bankNo);
		
		if(bankName.equals("") || StringUtils.isBlank(bankName)) {
			result.code = -1;
			result.msg = "银行卡号有误";
			
		}else{
			String[] banks = BankInfoUtil.getBankSimpleName(bankName);
			
			if(banks.length>0 && banks!=null) {
				bankC.bank_code = banks[1];
				bankC.bank_name = banks[0];
			}
			
			result.code = 1;
			result.msg = "绑卡成功";
		}*/
		String bankCode = null;
		int length = bankNos.length();
		bankNo = bankNos.substring(0, 8);
		length = length - bankNo.length();
		for (int i = 0 ; i < length ; i++ ) {
			bankNo = bankNo + "0";
		}
		String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?cardNo=yourcardNo&cardBinCheck=true";
		url = url.replace("yourcardNo", bankNo);
		String respone = Http.sendGet(url);
		JSONObject json = JSONObject.fromObject(respone);
		bankCode = (String)json.get("bank");
		Logger.info("bankCode:%s",bankCode);
		if (bankCode == null || StringUtils.isBlank(bankCode) || bankCode.equals("")) {
			result.code = -1;
			result.msg = "银行卡号有误或银行卡号不符合要求";
		}else {
			String[] banks = BankInfoUtil.getBankName(bankCode);
			if ( banks != null && banks.length > 0) {
				bankC.bank_code = banks[1];
				bankC.bank_name = banks[0];
			}
			result.code = 1;
			result.msg = "绑卡成功";
		}
		
		bankC.account = bankNos;
		bankC.user_id = userId;
		bankC.time = new Date();
		bankC.save();
		
		return result;
	}
	
	
	

}





















































