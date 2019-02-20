package controllers.back.finance;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.shove.Convert;

import models.common.bean.WithdrawalRecord;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import services.common.DealUserService;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;

/**
 * 后台-财务-会员提现-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
public class UserWithdrawMngCtrl extends BackBaseController {

	/** 注入用户交易信息service */
	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);
	
	/**
	 * 后台-财务-会员提现-提现记录列表
	 * @rightID 504001
	 * 
	 * @param currPage 
	 * @param pageSize
	 * @param exports 1:导出   default：不导出
	 * @param name 会员
	 * 
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void showWithdrawLogsPre(int currPage,int pageSize) {
		int exports = Convert.strToInt(params.get("exports"), 0);
		String name = params.get("name");
		
		/** 会员提现 */
		PageBean<WithdrawalRecord> page =  dealUserService.pageOfWithdrawalRecordBack(currPage,pageSize,exports,name);
		
		//导出
		if(exports == Constants.EXPORT){
			List<WithdrawalRecord> list = page.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
						
			File file = ExcelUtils.export("会员提现列表",
			arrList,
			new String[] {
			"编号", "流水号", "会员", "提现金额", "时间"},
			new String[] {
			"id","order_no", "name", "amount", "time"});
			   
			renderBinary(file, "会员提现列表.xls");
		}
		
		/** 提现总额 */
		double totalAmt = dealUserService.findWithdrawalTotalAmt(name);
		
		render(page,totalAmt,name);

	}
	
}
