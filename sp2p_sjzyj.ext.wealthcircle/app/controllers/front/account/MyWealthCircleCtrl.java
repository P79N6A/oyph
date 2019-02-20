package controllers.front.account;

import com.sun.jndi.url.corbaname.corbanameURLContextFactory;

import models.common.bean.CurrUser;
import models.ext.wealthcircle.bean.MyWealthCircleUser;
import models.ext.wealthcircle.entity.t_wealthcircle_account;
import play.mvc.With;
import service.ext.wealthcircle.WealthCircleService;
import services.core.InvestService;
import common.annotation.LoginCheck;
import common.annotation.PaymentAccountCheck;
import common.annotation.SimulatedCheck;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.ext.WealthCircleSettingKey;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.number.Arith;
import controllers.common.FrontBaseController;
import controllers.common.interceptor.AccountInterceptor;
import controllers.common.interceptor.SimulatedInterceptor;

/**
 * 前台-我的财富-我的奖励-财富圈控制器
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年4月7日
 */
@With({AccountInterceptor.class,SimulatedInterceptor.class})
public class MyWealthCircleCtrl extends FrontBaseController  {

	protected static WealthCircleService wealthCircleService = Factory.getService(WealthCircleService.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	/**
	 * 前台-我的财富-我的奖励-我的邀请码
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月7日
	 */
	public static void showMyWealthCirclePre(int currPage) {
		ResultInfo result = sendWcCode();
		if (result.code == 1) {
			
			renderArgs.put("more_invested", result.obj);
		}
		String initial_amount = wealthCircleService.findSettingValueByKey(WealthCircleSettingKey.INITIAL_AMOUNT);
		String rebate_invest = wealthCircleService.findSettingValueByKey(WealthCircleSettingKey.REBATE_INVEST);
		String discount_invest = wealthCircleService.findSettingValueByKey(WealthCircleSettingKey.DISCOUNT_INVEST);
		
		renderArgs.put("initial_amount", initial_amount);
		renderArgs.put("rebate_invest", rebate_invest);
		renderArgs.put("discount_invest", discount_invest);
		
		if (currPage <= 0) {
			currPage = 1;
		}
		
		CurrUser currUser = getCurrUser();
		PageBean<MyWealthCircleUser> page = wealthCircleService.pageOfMyUser(currPage, 5, currUser.id);
		
		t_wealthcircle_account account = wealthCircleService.findAccountByUser(currUser.id);
		render(page,account);
	}
	
	/** 判断是否达到可赠送邀请码的程度，如果到了赠送邀请码 */
	private static ResultInfo sendWcCode(){
		ResultInfo result = new ResultInfo();
		
		double totalInvested = investService.findTotalInvest(getCurrUser().id);
		t_wealthcircle_account wealthcircleAccount = wealthCircleService.findAccountByUser(getCurrUser().id);
		totalInvested = totalInvested-wealthcircleAccount.has_convert_amount;
		String initial_amount = wealthCircleService.findSettingValueByKey(WealthCircleSettingKey.INITIAL_AMOUNT);
		int initialAmount = (Integer.parseInt(initial_amount));
		int num = 0;
		if (totalInvested > 0 && initialAmount > 0) {
			num = (int)Arith.divDown(totalInvested, initialAmount, 0);
		}
		double hasInvested = initialAmount - (totalInvested - initialAmount * num);
		if (num > 0) {
			result = wealthCircleService.sendWealthCircleCode(getCurrUser().id,num);
			if (result.code < 1) {
				LoggerUtil.error(true, "赠送财富圈邀请码时出错:【%s】", result.msg);
			
				return result;
			}
		}
		
		result.code = 1;
		result.msg = "成功";
		result.obj = hasInvested;
		return result;
	}
	
	/**
	 * 前台-我的财富-我的奖励-我的邀请码-领取财富圈返佣
	 *
	 * @param wcSign
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月7日
	 */
	@LoginCheck
	@SimulatedCheck
	public static void reciveWealthCircle(String wcSign) {
		ResultInfo result = Security.decodeSign(wcSign, Constants.WEALTHCIRCLE_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			result.code = -1;
			
			renderJSON(result);
		}
		CurrUser currUser = getCurrUser();
		long wcUserId = Long.parseLong((String) result.obj);
		
		result = wealthCircleService.reciveWealthCircle(wcUserId,currUser.id);
		if (result.code < 1) {
			LoggerUtil.error(true, "返佣领取失败:【%s】", result.msg);
			
			result.msg = "返佣领取失败";
		
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "返佣领取成功";
		renderJSON(result);
	}
	
	/**
	 * 前台-我的财富-我的奖励-我的邀请码-财富圈奖励兑换
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月7日
	 */
	@LoginCheck
	@SimulatedCheck
	@PaymentAccountCheck
	public static void wealthCircleExchange() {

		ResultInfo result = wealthCircleService.applayConversion(getCurrUser().id);
		if (result.code < 1) {
			
			LoggerUtil.error(true, "财富圈申请兑换时出错,数据回滚:【%s】",result.msg);
			
			result.msg = "财富圈奖励申请兑换失败";
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "财富圈奖励申请兑换成功";
		renderJSON(result);
	}
	
}
