package controllers.back.finance;

import java.io.File;
import java.util.Date;
import java.util.List;

import models.common.bean.DealPlatform;
import models.common.entity.t_deal_platform;
import models.common.entity.t_deal_platform.DealOption;
import models.common.entity.t_deal_platform.DealType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import services.common.DealPlatformService;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;

/**
 * 后台-财务-平台收支-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
public class PlateformDealMngCtrl extends BackBaseController {

	protected static DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
	
	/**
	 * 后台-财务-平台收支-平台收支记录列表
	 * @rightID 507001
	 *	
	 * @param dealOption 交易类型
	 * @param showType 收支类型  1：收入 2：支出  default：所有
	 * @param exports 1：导出  default:不导出
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void showPlateformDealsPre(int dealOption,int showType,int currPage, int pageSize) {
		int exports = Convert.strToInt(params.get("exports"), 0);
		DealType dealType = DealType.getEnum(showType);
		String userName = params.get("userName");
		if(showType == -1){
			dealOption=0;
			showType=0;
			userName = "";
		}
		
		/** 平台收支记录 */
		PageBean<DealPlatform> page = dealPlatformService.pageOfDealsByOption(currPage, pageSize, DealOption.getEnum(dealOption), dealType,userName,exports);
		
		//导出
		if(exports == Constants.EXPORT){
			List<DealPlatform> list = page.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject dealPlatform = (JSONObject)obj;
				
				if(StringUtils.isNotBlank(dealPlatform.getString("operation"))){
					dealPlatform.put("operation", t_deal_platform.DealOption.valueOf(dealPlatform.getString("operation")).value);
				}
				
				if(StringUtils.isNotBlank(dealPlatform.getString("type"))){
					dealPlatform.put("type", t_deal_platform.DealType.valueOf(dealPlatform.getString("type")).value);
				}
			}
			
			String fileName = "平台收支列表";;
			if(DealType.INCOME.equals(dealType)){
				fileName = "平台收支收入列表";
			} else if(DealType.EXPENSES.equals(dealType)){
				fileName = "平台收支支出列表";
			}
			
			File file = ExcelUtils.export(
				fileName,
				arrList,
				new String[] {
						"编号", "交易类型", "收支类型", "金额", "会员", "备注", "时间"
				},
				new String[] {
						"id","operation","type","amount","name","remark", "time"
				}
			);
			   
			renderBinary(file, fileName+".xls");
		}
		
		/** 合计总额  */
		double totalAmt = dealPlatformService.findDealPlatformTotalAmt(DealOption.getEnum(dealOption), DealType.getEnum(showType),userName);
		
		
		render(page,totalAmt,dealOption,showType,userName);
	}
	
}
