package controllers.back.finance;

import java.io.File;
import java.util.Date;
import java.util.List;

import models.core.bean.BillInvest;
import models.core.entity.t_bill_invest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import services.core.BillInvestService;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;

/**
 * 后台-财务-理财账单-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
public class InvestBillMngCtrl extends BackBaseController {
	
	/** 注入investBillservice */
	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);

	/**
	 * 后台-财务-理财账单-账单列表
	 * @rightID 503001
	 * 
	 * @param showType default:所有  1.待还  2.逾期待还  3.已还  4.已转让
	 * @param exports 1:导出  default：不导出
	 * @param loanName 借款人昵称
	 * @param projectName 项目名称
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void showInvestBillsPre(int showType,int currPage,int pageSize) {
		int exports = Convert.strToInt(params.get("exports"), 0);
		String loanName = params.get("loanName");
		String projectName = params.get("projectName");
		
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);//0,按编号;3,账单金额;5,逾期时长;6,到期时间;7,回款时间
		if(orderType != 0 && orderType != 3 && orderType != 5 && orderType != 6 && orderType != 7){
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);
		
		String orderValueStr = params.get("orderValue");
		int orderValue = Convert.strToInt(orderValueStr, 0);//0,降序，1,升序
		if(orderValue<0 || orderValue>1){
			orderValue = 0;
		}
		renderArgs.put("orderValue", orderValue);
		
		/** 查询理财账单 */
		PageBean<BillInvest> page =  billInvestService.pageOfBillInvestBack(showType,currPage,pageSize,orderType,orderValue,exports,loanName,projectName);
		
		//导出
		if(exports == Constants.EXPORT){
			List<BillInvest> list = page.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject bill = (JSONObject)obj;	
				
				bill.put("period_no", bill.get("period")+"|"+bill.get("totalPeriod"));
	
				if(StringUtils.isNotBlank(bill.getString("status"))){
					bill.put("status", t_bill_invest.Status.valueOf(bill.get("status").toString()).value);
				}
				
				if(StringUtils.isNotBlank(bill.getString("overdue_days"))){
					bill.put("overdue_days", bill.get("overdue_days")+"天");
				}
			}
			
			String fileName="理财账单列表";
			switch (showType) {
				case 1:
					fileName = "待还理财账单列表";
					break;
				case 2:
					fileName = "逾期待还理财账单列表";
					break;
				case 3:
					fileName = "已还理财账单列表";
					break;
				case 4:
					fileName = "已转让理财账单列表";
					break;
				default:
					fileName = "理财账单列表";
					break;
			}
			
			File file = ExcelUtils.export(fileName,
			arrList,
			new String[] {
			"编号", "项目标号", "项目", "借款人", "账单金额(元)", "期数", "逾期时长", "到期时间", "还款时间", "状态"},
			new String[] {
			"bill_invest_no","bid_no","title", "name", "corpus_interest", "period_no", "overdue_days", "receive_time", "real_receive_time","status"});
			   
			renderBinary(file, fileName + ".xls");
		}
		
		/** 查询总额 */
		double totalAmt = billInvestService.findBillInvestTotalAmt(showType,loanName,projectName);
		
		render(page,totalAmt,showType,loanName,projectName);

	}
	
}
