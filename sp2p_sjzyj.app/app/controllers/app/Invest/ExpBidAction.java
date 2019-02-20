package controllers.app.Invest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import models.ext.experience.bean.experienceBidInvestRecord;
import models.ext.experience.entity.t_experience_bid;
import models.ext.experience.entity.t_experience_bid_setting;
import net.sf.json.JSONObject;
import service.ext.experiencebid.ExperienceBidInvestService;
import service.ext.experiencebid.ExperienceBidService;
import service.ext.experiencebid.ExperienceBidSettingService;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Client;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;

/**
 * 体验标
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年6月30日
 */
public class ExpBidAction {
	
	protected static ExperienceBidService experienceBidService = Factory.getService(ExperienceBidService.class);
	
	protected static ExperienceBidSettingService experienceBidSettingService = Factory.getService(ExperienceBidSettingService.class);

	protected static ExperienceBidInvestService experienceBidInvestService = Factory.getService(ExperienceBidInvestService.class);
	
	/**
	 * 体验标-标的信息
	 *
	 * @return
	 * @throws Exception
	 *
	 * @author huangyunsong
	 * @createDate 2016年6月30日
	 */
	public static String experienceBid(Map<String, String> parameters) throws Exception{
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("code", 1);
		result.put("msg", "成功");
		
		long expBidId = Convert.strToLong(parameters.get("expBidId"), -1);

		t_experience_bid expBid = experienceBidService.findByID(expBidId);
		if (expBid == null) {
			result.put("code", -1);
			result.put("msg", "标的信息不存在！");
			
			return JSONObject.fromObject(result).toString();
		}
		
		result.put("expBidId", expBid.id);
		result.put("title", expBid.title);
		result.put("apr", expBid.apr);
		result.put("period", expBid.period);
		result.put("min_invest_amount", expBid.min_invest_amount);
		result.put("repayment_type", expBid.repayment_type);
		result.put("is_overdue", expBid.is_overdue);
	
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 体验标-借款详情
	 *
	 * @author huangyunsong
	 * @createDate 2016年6月30日
	 */
	public static String experienceBidDetail(Map<String, String> parameters){
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("code", 1);
		result.put("msg", "成功");
		String expBidIdStr = parameters.get("expBidId");
		
		if(StringUtils.isBlank(expBidIdStr)){
			result.put("code", -1);
			result.put("msg", "体验标传入有误");
			return JSONObject.fromObject(result).toString();
		}
		long expBidId = Convert.strToLong(expBidIdStr, 0);
		
			if(expBidId<=0){
				result.put("code", -1);
				result.put("msg", "体验标传入有误");
				return JSONObject.fromObject(result).toString();
			}
		
		

		t_experience_bid_setting experienceBidSetting = experienceBidSettingService.findByID(expBidId);
		String bidDetail = experienceBidSetting==null?"":experienceBidSetting._value;
		result.put("bidDetail", bidDetail);
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 体验标-投标记录
	 *
	 * @param experienceBidId
	 * @param currPage
	 * @param pageSize
	 *
	 * @author huangyunsong
	 * @createDate 2016年6月30日
	 */
	public static String expBidInvestRecord(Map<String, String> parameters) throws Exception{
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("code", 1);
		result.put("msg", "成功");
		
		int currPage = Convert.strToInt(parameters.get("currPage"), 1);
		int pageSize = Convert.strToInt(parameters.get("pageSize"), 5);
		long expBidId = Convert.strToLong(parameters.get("expBidId"), -1);
		
		PageBean<experienceBidInvestRecord> pageBean = experienceBidInvestService.pageOfExperienceBidInvestRecord(expBidId, currPage, pageSize);
		if (pageBean.page == null) {
			result.put("investRecord", null);
		} else {
			List<Map<String, Object>> investList = new ArrayList<Map<String,Object>>();
			for (experienceBidInvestRecord invest : pageBean.page) {
				Map<String, Object> investMap = new HashMap<String, Object>();
				investMap.put("name", invest.user_name);
				investMap.put("time", invest.invest_time.getTime());
				investMap.put("amount", invest.invest_amount);
				investMap.put("client", invest.getClient()==null?"":invest.getClient().value);
				
				investList.add(investMap);
			}
			result.put("investRecord", investList);
		}
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 体验标-购买
	 *
	 * @param investAmt
	 * @param experienceBidId
	 *
	 * @author huangyunsong
	 * @createDate 2016年6月30日
	 */
	public static String investExpBid(Map<String, String> parameters) throws Exception{
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.put("code", 1);
		result.put("msg", "成功");
		
		String signUsersId = parameters.get("userId");
		long expBidId = Convert.strToLong(parameters.get("expBidId"), -1);
		double investAmt = Convert.strToDouble(parameters.get("investAmt"), -1);
		int client = Convert.strToInt(parameters.get("deviceType"), 1);
		
		ResultInfo signResult = Security.decodeSign(signUsersId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (signResult.code < 1) {
			result.put("code", ResultInfo.LOGIN_TIME_OUT);
			result.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			return JSONObject.fromObject(result).toString();
		}
		long userId = Long.parseLong(signResult.obj.toString());

		if(investAmt<=0 || investAmt%1000 != 0){
			result.put("code", -1);
			result.put("msg", "体验标投标金额只可为1000的正整数倍!");
			 
			return JSONObject.fromObject(result).toString();
		}
		
		ResultInfo investResult = experienceBidService.investExperienceBid(investAmt, expBidId, userId, Client.getEnum(client));
		if(investResult.code < 1){
			LoggerUtil.error(true, "体验账户投体验标的失败：%s", investResult.msg);
			result.put("code", -1);
			result.put("msg", investResult.msg);
			 
			return JSONObject.fromObject(result).toString();
		}
		
		result.put("result", investResult.obj);
		
		return JSONObject.fromObject(result).toString();
	}

}
