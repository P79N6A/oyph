package controllers.back.finance;

import java.io.File;
import java.util.Date;
import java.util.List;

import models.common.bean.RechargeRecord;
import models.common.entity.t_deal_user;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import services.common.DealUserService;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;

/**
 * 后台-财务-会员充值-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
public class UserRechargeMngCtrl extends BackBaseController {

	/** 注入用户交易信息service */
	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);
	
	/**
	 * 后台-财务-会员充值-会员充值记录列表
	 * @rightID 505001
	 *
	 * @param currPage
	 * @param pageSize
	 * @param exports 1：导出  default:不导出
	 * @param name 会员昵称
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void showRechargeLogsPre(int currPage, int pageSize) {
		int exports = Convert.strToInt(params.get("exports"), 0);
		String name = params.get("name");

		/** 会员充值记录 */
		PageBean<RechargeRecord> page = dealUserService.pageOfRechargeRecord(currPage, pageSize, exports, name);
			
		//导出
		if(exports == Constants.EXPORT){
			List<RechargeRecord> list = page.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject recharge = (JSONObject)obj;	
				
				if(StringUtils.isNotBlank(recharge.getString("operation_type"))){
					recharge.put("operation_type", t_deal_user.OperationType.valueOf(recharge.get("operation_type").toString()).description);
				}
			}
			
			File file = ExcelUtils.export("会员充值列表",
			arrList,
			new String[] {
			"编号", "流水号", "会员", "充值金额", "充值方式", "时间"},
			new String[] {
			"id","order_no", "name", "amount", "operation_type", "time"});
			   
			renderBinary(file, "会员充值列表.xls");
		}
		
		/** 充值总额 */
		double totalAmt = dealUserService.findRechargeTotalAmt(name);
			
		render(page,totalAmt,name);

	}
	
}
