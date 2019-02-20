package controllers.front.account;

import models.common.bean.ConversionUser;
import models.common.entity.t_deal_user;
import models.common.entity.t_recharge_user;
import models.common.entity.t_withdrawal_user;
import play.mvc.With;
import services.common.ConversionService;
import services.common.DealUserService;
import services.common.RechargeUserService;
import services.common.WithdrawalUserService;

import com.shove.Convert;
import common.utils.Factory;
import common.utils.PageBean;

import controllers.common.FrontBaseController;
import controllers.common.interceptor.AccountInterceptor;

/**
 * 前台-账户中心-交易记录控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With(AccountInterceptor.class)
public class MyDealCtrl extends FrontBaseController {

	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);

	protected static RechargeUserService rechargeUserService = Factory.getService(RechargeUserService.class);
	
	protected static WithdrawalUserService withdrawalUserService = Factory.getService(WithdrawalUserService.class);
	
	protected static ConversionService conversionService = Factory.getService(ConversionService.class);
	
	/**
	 * 跳转交易记录页面
	 * 
	 * @author Chenzhipeng
	 * @createDate 2016年1月21日
	 *
	 */
	public static void toDealRecordsPre(){
		
		render();
	}
	
	/**
	 * 交易记录
	 * @param currPage
	 * @param pageSize
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月16日
	 *
	 */
	public static void listOfDealRecordsPre(int currPage, int pageSize){
		if(pageSize<1){
			pageSize=5;
		}
		long userId = getCurrUser().id;
		PageBean<t_deal_user> dealPageBean = dealUserService.pageOfDealUser(currPage, pageSize, userId);
	
		render(dealPageBean);
	}
	
	/**
	 * 充值记录
	 * @param currPage
	 * @param pageSize
	 * 
	 * @author Chenzhipeng
	 * @createDate 2016年1月16日
	 *
	 */
	public static void listOfRechargeRecordsPre(int currPage, int pageSize){
		if(pageSize<1){
			pageSize=5;
		}
		long userId = getCurrUser().id;
		PageBean<t_recharge_user> rechargePageBean = rechargeUserService.pageOfDealUser(currPage, pageSize, userId);
		
		render(rechargePageBean);
	}
	
	/**
	 * 提现记录
	 * @param currPage
	 * @param pageSize
	 * 
	 * @author Chenzhipeng
	 * @createDate 2016年1月16日
	 *
	 */
	public static void listOfWithdrawRecordsPre(int currPage, int pageSize){
		if(pageSize<1){
			pageSize=5;
		}
		long userId = getCurrUser().id;
		PageBean<t_withdrawal_user> withdrawalPageBean = withdrawalUserService.pageOfDealUser(currPage,pageSize, userId);
		
		render(withdrawalPageBean);
	}
	
	/**
	 * 兑换记录
	 * @param currPage
	 * @param pageSize
	 * 
	 * @author Chenzhipeng
	 * @createDate 2016年1月16日
	 *
	 */
	public static void listOfConversionRecordsPre(int currPage, int pageSize){
		if(pageSize<1){
			pageSize=5;
		}
		long userId = getCurrUser().id;
		PageBean<ConversionUser> pageBean = conversionService.pageOfConversionRecord(currPage, pageSize, userId);
		
		render(pageBean);
	}
}
