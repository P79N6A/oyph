package controllers.back.finance;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.common.bean.ConversionUser;
import models.common.entity.t_conversion_user;
import models.common.entity.t_conversion_user.ConversionStatus;
import models.common.entity.t_conversion_user.ConversionType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import services.common.ConversionService;
import services.common.UserFundService;
import yb.enums.ServiceType;

import com.shove.Convert;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Client;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;

import controllers.common.BackBaseController;

/**
 * 后台-财务-奖励兑换-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
public class ConversionMngCtrl extends BackBaseController {

	protected static ConversionService conversionService = Factory.getService(ConversionService.class);	
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	/**
	 * 后台-财务-奖励兑换-查看兑换记录
	 * @rightID 506001
	 *
	 * @param showType 对应status 0-兑换和没有兑换，1-待兑换，2-已经兑换的，其他所有
	 * @param convType 对应兑换类型。从ConversionType中处理
	 * @param exports 1:导出  default：其它
	 * @param
	 * 
	 * @author DaiZhengmiao
	 * @createDate 2016年2月17日
	 */
	public static void showConversionsPre(int showType, int currPage, int pageSize, int convType) {
		int exports = Convert.strToInt(params.get("exports"), 0);
		String userName = params.get("userName");
		if(showType == -1){
			showType = 0;
			convType = 0;
			userName = "";
		}
		
		ConversionStatus status = null;
		if(showType==1){
			status = ConversionStatus.APPLYING;
		}
		if(showType==2){
			status = ConversionStatus.RECEIVED;
		}
		
		ConversionType type = ConversionType.getEnum(convType);
		PageBean<ConversionUser> page = conversionService.pageOfByStatusAType(currPage, pageSize, status, type,userName,exports);
		
		//导出
		if(exports == Constants.EXPORT){
			List<ConversionUser> list = page.page;
					 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
					
			for (Object obj : arrList) {
				JSONObject conversion = (JSONObject)obj;	
						
				if(StringUtils.isNotBlank(conversion.getString("conversion_type"))){
					conversion.put("conversion_type", t_conversion_user.ConversionType.valueOf(conversion.getString("conversion_type")).value);
				}
						
				if(StringUtils.isNotBlank(conversion.getString("status"))){
					conversion.put("status", t_conversion_user.ConversionStatus.valueOf(conversion.getString("status")).value);
				}
			}
					
			String fileName="奖励兑换列表";
			switch (showType) {
				case 1:
					fileName = "奖励兑换待兑换列表";
					break;
				case 2:
					fileName = "奖励兑换已兑换列表";
					break;
				default:
					fileName = "奖励兑换列表";
					break;
				}
					
			File file = ExcelUtils.export(fileName,
					arrList,
					new String[] {
						"编号", "类型", "会员", "兑换金额", "申请时间", "状态"
					},
					new String[] {
						"id","conversion_type", "userName", "amount", "time", "status"
					}
				);
					   
			renderBinary(file, fileName + ".xls");
		}
		
		
		double sumAmt = conversionService.sumAmtByStatusAType(status, type,userName);
		
		render(page,showType,convType,userName,sumAmt);
	}
	
	/**
	 * 兑换
	 * @rightID 506002
	 * 
	 * @param convSign
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月17日
	 */
	public static void convert(String convSign){
		ResultInfo result = Security.decodeSign(convSign, Constants.CONV_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error404();
		}
		
		long convId = Long.parseLong((String) result.obj);
		t_conversion_user conversion = conversionService.findByID(convId);
		if (conversion == null) {
			flash.error("申请记录不存在");
			
			showConversionsPre(0,0,0,0);
		}
		
		result = conversionService.conversion(conversion, getCurrentSupervisorId());
		if (result.code < 1) {
			flash.error(result.msg);
			
			showConversionsPre(0,0,0,0);
		}
		
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_FAILED);
		
		if (ConfConst.IS_TRUST) {
			//商户可用余额查询，
			result = PaymentProxy.getInstance().queryMerchantBalance(Client.PC.code);
			if (result.code < 1) {
				flash.error("查询商户可用余额异常");
				
				showConversionsPre(0,0,0,0);
			}
			
			Map<String, Object> merDetail = (Map<String, Object>) result.obj;
			double merBalance = Convert.strToDouble(merDetail.get("balance").toString(), 0);
			
			if (conversion.amount > merBalance) {
				flash.error("商户余额不足，请充值");
				
				showConversionsPre(0,0,0,0);
			}
			
			//转账
			result = PaymentProxy.getInstance().conversion(Client.PC.code, serviceOrderNo, conversion);
			if (result.code < 1 && result.code != ResultInfo.ALREADY_RUN) {
				flash.error(result.msg);
				LoggerUtil.info(true, result.msg);
			} else {
				flash.success("兑换成功");
			}

			showConversionsPre(0,0,0,0);
		}

		result = conversionService.doConversion(serviceOrderNo, convId);
		if(result.code<1){
			LoggerUtil.info(true, result.msg);
			
			flash.error("兑换失败");
			showConversionsPre(0,0,0,0);
		}
		
		flash.success("兑换成功");
		showConversionsPre(0,0,0,0);
	}
	
}
