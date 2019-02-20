package controllers.app.wealth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.ext.experience.bean.experienceUserInvestRecord;
import models.ext.experience.entity.t_experience_bid_account;
import net.sf.json.JSONObject;
import service.ext.experiencebid.ExperienceBidAccountService;
import service.ext.experiencebid.ExperienceBidInvestService;

import com.shove.Convert;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;

public class MyExpBidAction {

	public static ExperienceBidInvestService experienceBidInvestService = Factory.getService(ExperienceBidInvestService.class);
	
	public static ExperienceBidAccountService experienceBidAccountService = Factory.getService(ExperienceBidAccountService.class);

	/**
	 * 我的奖励-我的体验金账户信息
	 *
	 * @author huangyunsong
	 * @createDate 2016年6月30日
	 */
	public static String myExpBidAccount(Map<String, String> parameters) throws Exception{
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("code", 1);
		result.put("msg", "成功");
		
		String signUsersId = parameters.get("userId");
		ResultInfo signResult = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (signResult.code < 1) {
			result.put("code", ResultInfo.LOGIN_TIME_OUT);
			result.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			return JSONObject.fromObject(result).toString();
		}
		long userId = Long.parseLong(signResult.obj.toString());

		t_experience_bid_account expBidAccount = experienceBidAccountService.findUserExperienceAccount(userId);
		if (expBidAccount == null) {
			result.put("code", -1);
			result.put("msg", "体验金账户不存在");
			 
			return JSONObject.fromObject(result).toString();
		}
		
		result.put("count", expBidAccount.count);
		result.put("amount", expBidAccount.amount);
		result.put("balance", expBidAccount.balance);
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 我的奖励-体验标投标记录
	 *
	 * @param currPage
	 * @param pageSize
	 *
	 * @author huangyunsong
	 * @createDate 2016年6月30日
	 */
	public static String pageOfMyExpBidInvest(Map<String, String> parameters) throws Exception{
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("code", 1);
		result.put("msg", "成功");
		
		String signUsersId = parameters.get("userId");
		ResultInfo signResult = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (signResult.code < 1) {
			result.put("code", ResultInfo.LOGIN_TIME_OUT);
			result.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			return JSONObject.fromObject(result).toString();
		}
		long userId = Long.parseLong(signResult.obj.toString());
		
		int currPage = Convert.strToInt(parameters.get("currPage"), 1);
		int pageSize = Convert.strToInt(parameters.get("pageSize"), 5);
		
		PageBean<experienceUserInvestRecord> pageBean = experienceBidInvestService.pageOfExperienceUserInvestRecord(userId, currPage, pageSize);
		if (pageBean.page == null) {
			result.put("investRecord", null);
		} else {
			List<Map<String, Object>> investList = new ArrayList<Map<String,Object>>();
			for (experienceUserInvestRecord invest : pageBean.page) {
				Map<String, Object> investMap = new HashMap<String, Object>();
				investMap.put("bidNo", invest.bidNo);
				investMap.put("title", invest.title);
				investMap.put("invest_amount", invest.invest_amount);
				investMap.put("income", invest.income);
				investMap.put("invest_time", invest.invest_time.getTime());
				investMap.put("repayment_time", invest.repayment_time==null?null:invest.repayment_time.getTime());
				investMap.put("status", invest.getStatus().value);
				
				investList.add(investMap);
			}
			
			result.put("investRecord", investList);
		}
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 我的奖励-体验金-体验金领取
	 *
	 * @author huangyunsong
	 * @createDate 2016年6月30日
	 */
	public static String getExperienceGold(Map<String, String> parameters) throws Exception{
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("code", 1);
		result.put("msg", "成功");
		
		String signUsersId = parameters.get("userId");
		ResultInfo signResult = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (signResult.code < 1) {
			result.put("code", ResultInfo.LOGIN_TIME_OUT);
			result.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			return JSONObject.fromObject(result).toString();
		}
		long userId = Long.parseLong(signResult.obj.toString());
		
		ResultInfo checkResult = experienceBidAccountService.checkCanGetGold(userId);
		
		if(checkResult.code < 1){
			result.put("code", -1);
			result.put("msg", checkResult.msg);
			 
			return JSONObject.fromObject(result).toString();
		}
		
	    ResultInfo getResult = experienceBidAccountService.getExperienceGold(userId);
	    
	    if(getResult.code < 1){
			LoggerUtil.error(true, "体验金领取失败:%s", getResult.msg);
			
			result.put("code", -1);
			result.put("msg", "体验金领取失败:%s"+getResult.msg);
			 
			return JSONObject.fromObject(result).toString();
		}
	    
	    t_experience_bid_account expBidAccount = experienceBidAccountService.findUserExperienceAccount(userId);
	    
	    result.put("count", expBidAccount.count);
		result.put("amount", expBidAccount.amount);
		result.put("balance", expBidAccount.balance);
		result.put("every_grant", expBidAccount.every_grant);
	    
	    return JSONObject.fromObject(result).toString();
	}
	
	
	/**
	 * 我的奖励-体验金-兑换
	 *
	 * @author huangyunsong
	 * @createDate 2016年6月30日
	 */
	public static String applayConversion(Map<String, String> parameters) throws Exception{
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("code", 1);
		result.put("msg", "成功");
		
		String signUsersId = parameters.get("userId");
		ResultInfo signResult = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (signResult.code < 1) {
			result.put("code", ResultInfo.LOGIN_TIME_OUT);
			result.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			return JSONObject.fromObject(result).toString();
		}
		long userId = Long.parseLong(signResult.obj.toString());
		
		
		ResultInfo conResult = experienceBidAccountService.applayConversion(userId);
		if (conResult.code < 1) {
			LoggerUtil.info(true, conResult.msg);
			
			result.put("code", -1);
			result.put("msg", conResult.msg);
			 
			return JSONObject.fromObject(result).toString();
		}
		
		t_experience_bid_account expBidAccount = experienceBidAccountService.findUserExperienceAccount(userId);
	    
		result.put("count", expBidAccount.count);
		result.put("amount", expBidAccount.amount);
		result.put("balance", expBidAccount.balance);
		
		return JSONObject.fromObject(result).toString();
	}


}
