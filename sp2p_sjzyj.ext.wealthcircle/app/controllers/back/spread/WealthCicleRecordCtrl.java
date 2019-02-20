package controllers.back.spread;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.ext.wealthcircle.bean.WealthCircleRecord;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import service.ext.wealthcircle.WealthCircleService;

import com.shove.Convert;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;

import controllers.common.BackBaseController;

/**
 * 后台-推广-财富圈-推广记录
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年4月1日
 */
public class WealthCicleRecordCtrl extends BackBaseController {

	protected static WealthCircleService wealthCircleService = Factory.getService(WealthCircleService.class);
	
	/**
	 * 后台-推广-财富圈-财富圈邀请记录
	 * 
	 * @rightID 707001
	 *
	 * @param currPage 当前第几页
	 * @param pageSize 每页显示记录数
	 * @param userName 用户名关键字(被邀请人)
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月8日
	 */
	public static void showRecordsPre(int currPage,int pageSize,String userName) {
		if (currPage <= 0) {
			currPage = 1;
		}
		if (pageSize < 5) {
			pageSize = 10;
		}
		
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);//0,按编号(会员id);3,累计理财;4,累计返佣
		if (orderType != 0 && orderType != 3 && orderType != 4) {
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);
		
		String orderValueStr = params.get("orderValue");
		int orderValue = Convert.strToInt(orderValueStr, 0);//0,降序，1,升序
		if (orderValue < 0 || orderValue > 1) {
			orderValue = 0;
		}
		renderArgs.put("orderValue", orderValue);
		
		int exports = Convert.strToInt(params.get("exports"), 0);
		if (exports != Constants.EXPORT) {
			exports = 0;
		}
		
		renderArgs.put("userName", userName);
		
		PageBean<WealthCircleRecord> page = wealthCircleService.pageOfWcRecords(currPage, pageSize, orderType, orderValue, exports, userName);
		
		//导出
		if (exports == Constants.EXPORT) {
			List<WealthCircleRecord> list = page.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			String fileName = "财富圈邀请记录";			
			File file = ExcelUtils.export(
				fileName,
				arrList,
				new String[] {
						"编号", "会员", "邀请人", "累计理财", "累计返佣","邀请时间"
				},
				new String[] {
						"user_id","user_name","spreader_name","total_invest","total_rebate","active_time"
				}
			);
			   
			renderBinary(file, fileName+".xls");
		}
		
		//累计投资总额，累计返佣总额
		Map<String, Object> totalAmt = wealthCircleService.findTotalWcAmount(userName);
		
		render(page,totalAmt);
	}
	
}
