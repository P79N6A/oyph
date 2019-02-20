package controllers.back.finance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import models.bean.PaymentRequestLogs;
import models.bean.UserFundCheck;
import models.entity.t_payment_call_back;
import models.entity.t_payment_request;
import services.PaymentService;
import yb.enums.ServiceType;
import common.utils.Factory;
import common.utils.HttpUtil;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;

/**
 * 后台-财务-资金校对-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年1月16日
 */
public class FundCheckCtrl extends BackBaseController {
	
	protected static PaymentService paymentService = Factory.getSimpleService(PaymentService.class);

	/**
	 * 用户资金信息列表
	 * @rightID 511001
	 *
	 * @param userName 
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月10日
	 */
	public static void showFundInfoPre(int showType, String userName, int currPage, int pageSize){
		if (currPage < 1) {
			currPage = 1;
		}

		if (pageSize < 1) {
			pageSize = 5;
		}
		
		if(showType == 1) {
			List<UserFundCheck> page = paymentService.UserFundCheck(userName, currPage, pageSize);
			render(userName,page,showType);
		}
		
		PageBean<UserFundCheck> page = paymentService.pageOfUserFundCheck(userName, currPage, pageSize);
		
		render(page,userName,showType);	
	}
}
