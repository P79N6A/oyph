package controllers.back.finance;

import org.apache.commons.lang.StringUtils;

import common.utils.Factory;
import common.utils.PageBean;
import controllers.common.BackBaseController;
import models.common.entity.t_cost;
import services.common.CostService;

/**
 * 后台-财务-借款账单-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
public class CostMngCtrl extends BackBaseController {
	
	protected static CostService costService = Factory.getService(CostService.class);
	
	public static void showCostListPre() {
		
		String currPageStr = params.get("currPage");
		String pageSizeStr = params.get("pageSize");
		int currPage = 0;
		int pageSize = 0;
		
		if (StringUtils.isNotBlank(currPageStr)) {
			currPage = Integer.parseInt(currPageStr);
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}
		
		PageBean<t_cost> page = costService.pageOfCosts(currPage, pageSize);
		
		render(page);
	}
	
}
