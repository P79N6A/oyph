package controllers.back.finance;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.bean.CallBackPrint;
import models.bean.PaymentRequestLogs;
import models.entity.t_payment_call_back;
import models.entity.t_payment_request;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import services.PaymentService;
import yb.YbUtils;
import common.constants.Constants;
import common.enums.ServiceType;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;

import controllers.common.BackBaseController;

/**
 * 后台-财务-托管日志-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年1月16日
 */
public class PaymentLogMngCtrl extends BackBaseController {
	
	protected static PaymentService paymentService = Factory.getSimpleService(PaymentService.class);

	/**
	 * 后台-财务-托管日志-资金托管接口请求记录列表
	 * @rightID 510001
	 *
	 * @param showType 筛选切换参数
	 * @param currPage 当前页
	 * @param pageSize 分页大小
	 * @param serviceType 按业务类型筛选
	 * @param userName 按用户名筛选
	 * @param serviceOrderNo 按业务订单号筛选
	 * @param orderNo 按交易订单号筛选
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月23日
	 */
	public static void showRequestLogsPre (int showType, int currPage, int pageSize, Integer serviceType,
			String userName, String serviceOrderNo, String orderNo) {
		if (showType < 0 || showType > 2) {
			showType = 0;  //参数错误，默认显示所有
		}
		
		PageBean<PaymentRequestLogs> page = paymentService.pageOfRequestRecord(showType, currPage, pageSize, serviceType, userName, serviceOrderNo, orderNo);
		
		render(page, showType, serviceType, userName, serviceOrderNo, orderNo);
	}

	/**
	 * 查看请求参数
	 * @rightID 510002
	 *
	 * @param requestId
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月18日
	 */
	public static void showRequestDataPre (String requestMark) {
		
		JSONObject json = paymentService.queryTrustReqParam(requestMark);
		
		if (json == null) {
			flash.error("请求参数为空");
			redirect("back.finance.PaymentLogMngCtrl.showRequestLogsPre");
		}
		
		Object o = json.get("body");
		if (o != null) {
			Map<String, String> body = YbUtils.jsonToMap(o.toString());
			render(body);
		}
		Map<String, String> reqHeader = null;
		if (json.get("reqHeader") != null) {
			reqHeader = YbUtils.jsonToMap(json.get("reqHeader").toString());
		} 
		Map<String, String> inBody = null;
		if (json.get("inBody") != null) {
			inBody = YbUtils.jsonToMap(json.get("inBody").toString());
		}
		
		
		render(reqHeader, inBody);
	}
	
	/**
	 * 查看回调参数列表
	 * @rightID 510003
	 *
	 * @param requestId
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月18日
	 */
	public static void showCBDatasPre (long requestId) {
		
		t_payment_request paymentRequest = paymentService.findPaymentRequestById(requestId);
		
		if (paymentRequest == null) {
			flash.error("请求记录不存在");
			redirect("back.finance.PaymentLogMngCtrl.showRequestLogsPre");
		}

		List<CallBackPrint> CBList = paymentService.queryCallBackList(paymentRequest.mark, paymentRequest.getService_type().code);

		
		render(CBList);
	}
	
	/**
	 * 日志补单
	 * @rightID 510003
	 *
	 * @param callBackId 回调日志id
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月19日
	 */
	public static void repair(long callBackId){
		ResultInfo result = new ResultInfo();
		
		String callBackURL = "";
		if (StringUtils.isBlank(callBackURL)) {
			result.code = -1;
			result.msg = "查询异步回调地址失败";

			renderJSON(result);
		}
		
		Map<String, String> callBackParams = new HashMap<>();
		if (callBackParams == null) {
			result.code = -1;
			result.msg = "查询回调参数失败";

			renderJSON(result);
		}
		
		callBackParams.put("url", callBackURL);
		
		result.code = 1;
		result.obj = callBackParams;
		
		renderJSON(result);
	}
	
}
