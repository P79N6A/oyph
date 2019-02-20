package controllers.back.finance;

import java.util.List;

import common.utils.Factory;
import common.utils.PageBean;
import controllers.common.BackBaseController;
import models.common.bean.ShowUserInfo;
import models.common.entity.t_credit;
import models.common.entity.t_quota;
import models.common.entity.t_user_info;
import services.common.CreditService;
import services.common.QuotaService;
import services.common.UserInfoService;

/**
 * 后台-财务-投资人限额管理-控制器
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年01月23日
 */
public class QuotaCtrl extends BackBaseController {

	protected static QuotaService quotaService = Factory.getService(QuotaService.class);
	
	protected static CreditService creditService = Factory.getService(CreditService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	//投资人限额管理列表页
	public static void showQuotaPre(Integer showType) {
		
		if (showType < 0 || showType > 6) {
			showType = 0;
		}
		
		List<t_quota> quotaes = quotaService.queryQuotaesByType(showType);
		
		render(quotaes, showType);
	}
	
	//投资人限额管理编辑页面
	public static void toEditQuotaPre(long quotaId) {
		
		t_quota quo = quotaService.findByID(quotaId);
		
		render(quo);
	}
	
	//保存投资限额
	public static void editQuota(long quoId, double sumInvest, int type) {
		
		t_quota quo = quotaService.findByID(quoId);
		
		t_credit credit = creditService.findByColumn("credit_class=?", type);
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(quo.user_id);
		
		if(userInfo == null) {
			flash.error("查询用户信息失败，请重试");
			
			showQuotaPre(0);
		}
		
		if(quo == null || credit == null) {
			flash.error("系统出错，请重试");
			
			showQuotaPre(0);
		}
		
		if (credit.residue_amount < sumInvest) {
			flash.error("投资限额要大于累计投资金额");
			
			toEditQuotaPre(quoId);
		}
		
		userInfo.credit_id = credit.credit_class;
		userInfo.save();
		
		quo.amount = credit.residue_amount;
		quo.type = 1;
		quo.save();
		
		flash.success("保存成功");
		showQuotaPre(0);
	}
}
