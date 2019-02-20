package controllers.front.spread;

import java.util.Date;
import java.util.Map;

import models.ext.experience.bean.experienceBidInvestRecord;
import models.ext.experience.bean.experienceUserInvestRecord;
import models.ext.experience.entity.t_experience_bid;
import models.ext.experience.entity.t_experience_bid_account;
import play.mvc.With;
import service.ext.experiencebid.ExperienceBidAccountService;
import service.ext.experiencebid.ExperienceBidInvestService;
import service.ext.experiencebid.ExperienceBidService;

import common.annotation.LoginCheck;
import common.annotation.PaymentAccountCheck;
import common.annotation.SimulatedCheck;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;

import controllers.common.FrontBaseController;
import controllers.common.SubmitRepeat;
import controllers.common.interceptor.SimulatedInterceptor;
import controllers.common.interceptor.UserStatusInterceptor;

@With({UserStatusInterceptor.class,SimulatedInterceptor.class, SubmitRepeat.class})
public class ExperienceBidFrontCtrl extends FrontBaseController{

	public static ExperienceBidService experienceBidService = Factory.getService(ExperienceBidService.class);
	
	public static ExperienceBidAccountService experienceBidAccountService = Factory.getService(ExperienceBidAccountService.class);
	
	public static ExperienceBidInvestService experienceBidInvestService = Factory.getService(ExperienceBidInvestService.class);
	
	/**
	 * 理财-体验标详情
	 *
	 * @param experienceBidId
	 *
	 * @author yaoyi
	 * @createDate 2016年2月17日
	 */
	@SubmitCheck
	public static void experienceBidDetailPre(long experienceBidId){
		
		t_experience_bid experienceBid = experienceBidService.findByID(experienceBidId);
		if(experienceBid == null){
			
			error404();
		}
		
		double	visualBalance = 0.00;
		if(getCurrUser()!=null){
			visualBalance = experienceBidAccountService.findUserExperienceGold(getCurrUser().id);
		}
		
		renderArgs.put("sysNowTime", new Date().getTime());//服务器时间传到客户端
		
		render(experienceBid, visualBalance);
	}
	
	/**
	 * 理财-投体验标
	 *
	 * @param investInput
	 * @param bidId
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	@LoginCheck
	@SimulatedCheck
	@SubmitOnly
	public static void investExperienceBid(double investInput, long experienceBidId){
		checkAuthenticity();
		if(investInput<=0 || investInput%1000 != 0){
			flash.error("体验标投标金额只可为1000的正整数倍!");
			
			experienceBidDetailPre(experienceBidId);
		}
		
		ResultInfo result = experienceBidService.investExperienceBid(investInput, experienceBidId, getCurrUser().id);
		if(result.code < 1){
			flash.error(result.msg);
			LoggerUtil.error(true, "体验账户投体验标的失败：%s", result.msg);
			
			experienceBidDetailPre(experienceBidId);
		}
		
		Map<String, Object> resp = (Map<String, Object>) result.obj;
		flash.put("investExperienceBidSuccess", true);
		flash.put("income", resp.get("income"));
		flash.put("period", resp.get("period"));
		flash.put("investAmount", resp.get("investAmount"));
		flash.put("amount", resp.get("amount"));
		
		experienceBidDetailPre(experienceBidId);
	}
	
	/**
	 * 理财-详情-体验标的投资记录
	 *
	 * @param experienceBidId
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	@LoginCheck
	public static void doInvestExperienceBidRecordPre(long experienceBidId, int currPage, int pageSize){
		
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 5;
		}
		
		PageBean<experienceBidInvestRecord> pageBean = experienceBidInvestService.pageOfExperienceBidInvestRecord(experienceBidId, currPage, pageSize);
		
		render(pageBean);
	}
	
	/**
	 * 我的财富-我的奖励-体验金申请兑换
	 * 
	 * @author huangyunsong
	 * @createDate 2016年2月23日
	 */
	@LoginCheck
	@SimulatedCheck
	@PaymentAccountCheck
	public static void applayConversion() {
		ResultInfo result = new ResultInfo();
		result = experienceBidAccountService.applayConversion(getCurrUser().id);
		if (result.code < 1) {

			LoggerUtil.info(true, result.msg);
			result.msg = "体验金申请兑换失败";
			renderJSON(result);
		}
		
		result.code = 1;
		
		renderJSON(result);
	}
	
	
	/**
	 * 我的财富-我的奖励-我的体验金账户信息
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	@LoginCheck
	public static void showMyExperienceGoldPre(int currPage, int pageSize){
		
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 5;
		}
		
		PageBean<experienceUserInvestRecord> pageBean = experienceBidInvestService.pageOfExperienceUserInvestRecord(getCurrUser().id, currPage, pageSize);
		
		t_experience_bid_account experienceBidAccount = experienceBidAccountService.findUserExperienceAccount(getCurrUser().id);
		
		render(pageBean, experienceBidAccount);
	}
	
	/**
	 * 我的财富-我的奖励-用户领取体验金
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	@LoginCheck
	@SimulatedCheck
	public static void getExperienceGold(){
		ResultInfo result = new ResultInfo();
		
		result = experienceBidAccountService.checkCanGetGold(getCurrUser().id);
		if(result.code < 1){
			
			renderJSON(result);
		}
		
	    result = experienceBidAccountService.getExperienceGold(getCurrUser().id);
	    if(result.code < 1){
			LoggerUtil.error(true, "体验金领取失败:%s", result.msg);
			
			renderJSON(result);
		}
	    
		renderJSON(result);
	}
}
