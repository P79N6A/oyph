package controllers.app.Invest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.app.bean.DebtReturnMoneyBean;
import models.app.bean.DebtTransferBean;
import models.app.bean.DebtTransferDetailBean;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import payment.impl.PaymentProxy;
import play.Logger;
import service.DebtAppService;
import yb.YbPaymentCallBackService;
import yb.YbUtils;
import yb.enums.ServiceType;
import yb.enums.TradeType;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.RemarkPramKey;
import common.enums.Client;
import common.enums.DeviceType;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;

/**
 * 债权转让(OPT=33X)
 *
 * @description
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月30日
 */
public class DebtAction {

	private static DebtAppService debtAppService = Factory.getService(DebtAppService.class);
	private static YbPaymentCallBackService ybPaymentCallBackService = Factory.getSimpleService(YbPaymentCallBackService.class);
	/**
	 * 分页查询债权转让列表(OPT=331)
	 *
	 * @param parameters
	 * @return
	 * @throws Exception
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年7月5日
	 */
	public static String pageOfDebts(Map<String, String> parameters) throws Exception {
		JSONObject json = new JSONObject();

		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");

		if (!StrUtil.isNumeric(currentPageStr) || !StrUtil.isNumeric(pageNumStr)) {
			json.put("code", -1);
			json.put("msg", "分页参数不正确");

			return json.toString();
		}

		int currPage = Convert.strToInt(currentPageStr, 1);
		int pageSize = Convert.strToInt(pageNumStr, 10);

		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}

		PageBean<DebtTransferBean> page = debtAppService.pageOfDebts(currPage,pageSize);

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(// 只要设置这个数组，指定过滤哪些字段。
				new String[] {"id"}
						
		);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功");
		map.put("records", page.page);
		return JSONObject.fromObject(map, jsonConfig).toString();

	}

	/**
	 * 债权转让详情(OPT=332)
	 *
	 * @param parameters
	 * @return
	 * @throws Exception
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年7月5日
	 */
	public static String debtDetail(Map<String, String> parameters)
			throws Exception {
		JSONObject json = new JSONObject();
		String signId = parameters.get("debtId");

		ResultInfo result = Security.decodeSign(signId, Constants.DEBT_TRANSFER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		
		long debtId = Long.valueOf(result.obj+"");
		DebtTransferDetailBean bean = debtAppService.findDetailById(debtId);
		if (bean == null) {
			json.put("code", -1);
			json.put("msg", "查询债权信息失败");
			return json.toString();
		}
		
		json.put("code", 1);
		json.put("msg", "债权信息查询成功");
		
		json.put("debtId", bean.getDebtId());
		json.put("time", bean.getDebt_transfer_no());
		json.put("debt_transfer_no", bean.getDebtId());
		json.put("invest_id", bean.invest_id);
		json.put("bid_id", bean.bid_id);
		json.put("user_id", bean.user_id);
		json.put("debt_amount", bean.debt_amount);
		json.put("debt_principal", bean.debt_principal);
		json.put("transfer_price", bean.transfer_price);
		json.put("period", bean.period);
		json.put("status", bean.status);
		json.put("end_time", bean.getEnd_time());
		json.put("receive_time", bean.getReceive_time());
		json.put("statusStr", bean.getStatusStr());
		
		return json.toString();
	}
	
	/**
	 * 债权回款计划(OPT=333)
	 *
	 * @param parameters
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年7月1日
	 */
	public static String paymentsOfDebt(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signId = parameters.get("investId");

		ResultInfo result = Security.decodeSign(signId, Constants.INVEST_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		long investId = Long.valueOf(result.obj+"");
		
		List<DebtReturnMoneyBean> beans = debtAppService.queryRepaymentBill(investId);
		if (beans == null || beans.size()==0) {
			json.put("code", -2);
			json.put("msg", "查询债权回款计划失败");
			return json.toString();
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(new String[] {// 只要设置这个数组，指定过滤哪些字段。
				"id", 
		});
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "债权回款计划查询成功");
		map.put("records", beans);
		
		return JSONObject.fromObject(map,jsonConfig).toString();
	}

	/**
	 * 购买债权(OPT=334)
	 *
	 * @param parameters
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年7月1日
	 */
	public static String buyDebt(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		String signDebtId = parameters.get("debtId");
		String signUserId = parameters.get("userId");
		String deviceType = parameters.get("deviceType");//设备类型

		ResultInfo result = Security.decodeSign(signDebtId, Constants.DEBT_TRANSFER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		long debtId = Long.parseLong((String)result.obj);
		result = Security.decodeSign(signUserId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		
		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		int client = Integer.parseInt(deviceType);
		
		long userId = Long.parseLong((String)result.obj);
		
		//准备
		result = debtAppService.debtTransfer(debtId, userId);
		if(result.code < 1){
			json.put("code", -2);
			json.put("msg", result.msg);
			return json.toString();
		}
			
		//业务订单号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.FARM_IN);
			
		if (ConfConst.IS_TRUST) {
			//资金托管
			result = PaymentProxy.getInstance().debtTransfer(client, businessSeqNo, debtId, userId);		
			
			TreeMap<String, String> responseMap = YbUtils.getRespHeader(result.msg, ServiceType.FARM_IN, null);
			
			//打印、保存接口回调参数
			ybPaymentCallBackService.printCallBackData("客户转受让", responseMap, ServiceType.FARM_IN , TradeType.FUND_TRADE);
			
			
			if (responseMap== null || !responseMap.get("respCode").equals("P2P0000")){
				json.put("code",-1);
				json.put("msg",responseMap.get("respMsg")) ;
				return json.toString();
			}			
			
			//备注参数调用
			Map<String, String> remarkParams = ybPaymentCallBackService.queryRequestParamss(businessSeqNo);
			double fee = Double.parseDouble(remarkParams.get(RemarkPramKey.BOND_FEE));//转让方手续费
			result = ybPaymentCallBackService.debtTransfer(businessSeqNo, debtId, userId, fee);
			
			if (result.code > 0 || result.code == ResultInfo.ALREADY_RUN) {
				json.put("code",1);
				json.put("msg",result.msg) ;
				
			} else {
				json.put("code",-1);
				json.put("msg",result.msg) ;
			}
		}
		return json.toString();
	}
}
