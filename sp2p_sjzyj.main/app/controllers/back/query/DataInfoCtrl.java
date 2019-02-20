package controllers.back.query;

import java.util.HashMap;
import java.util.Map;

import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import controllers.common.BaseController;
import payment.impl.PaymentProxy;
import yb.enums.ServiceType;
import yb.enums.TradeType;

/**
 * 后台-查询-数据信息查询
 * 
 * @createDate 2018.7.11
 * */
public class DataInfoCtrl extends BaseController{
	/**
	 * 用户信息查询页面
	 * */
	public static void customerInfoPre() {
		
		render();
	}
	
	/**
	 * 台账信息查询页面
	 * */
	public static void accountInfoPre(String result,String respCode,String respMsg,String accountNo,String accountStatus,String availAbleAmount,String transItAmount,String withDrawalAmount) {
		
		render(result,respCode,respMsg,accountNo,accountStatus,availAbleAmount,transItAmount,withDrawalAmount);
	}

	/**
	 * 
	 * */
	public static void accountInfoQuery() {
		
		String checkTypeStr = params.get("checkType");
		String accountNoStr = params.get("accountNo");
		if (checkTypeStr.isEmpty() || accountNoStr.isEmpty()) {
			
			accountInfoPre("","","","","","","","");
		}
		
		ResultInfo result = PaymentProxy.getInstance().dataInfoQuery(OrderNoUtil.getClientSn(), "",accountNoStr, ServiceType.getEnumByKey(checkTypeStr),null);
		
		if (result != null && result.code > 0) {
			String accountStatus = "";
			switch (result.msgs.get("accountStatus")) {
			case "00":
				accountStatus = "正常";
				break;
			case "01":
				accountStatus = "冻结";
				break;
			case "02":
				accountStatus = "注销";
				break;
			case "99":
				accountStatus = "待激活";
				break;
			}
			
			accountInfoPre(result.msg,result.msgs.get("respCode"),result.msgs.get("respMsg"),accountNoStr,accountStatus,result.msgs.get("availableamount"),result.msgs.get("transitamount"),result.msgs.get("withdrawalamount"));
		}
		
		accountInfoPre(accountNoStr+result.msg,"","",accountNoStr,"","","","");
	}
	
}
