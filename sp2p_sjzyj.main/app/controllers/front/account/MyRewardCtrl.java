package controllers.front.account;

import play.mvc.With;
import controllers.common.FrontBaseController;
import controllers.common.interceptor.AccountInterceptor;
import controllers.common.interceptor.SimulatedInterceptor;

/**
 * 前台-账户中心-我的奖励控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With(AccountInterceptor.class)
public class MyRewardCtrl extends FrontBaseController {

	
	/**
	 * 奖励首页
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月18日
	 *
	 */
	public static void awardPre(){
		
		render();
	}
	

}
