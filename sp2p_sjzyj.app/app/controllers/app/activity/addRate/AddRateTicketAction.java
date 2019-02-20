package controllers.app.activity.addRate;

import java.util.List;
import java.util.Map;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import controllers.common.BaseController;
import models.app.bean.InvestAddRateBean;
import net.sf.json.JSONObject;
import service.ShakeActivityAppService;

public class AddRateTicketAction extends BaseController {
	
	private static ShakeActivityAppService shakeActivityAppService = Factory.getService(ShakeActivityAppService.class);
	
	/**
	 * 
	 * @param parameters
	 * @return
	 */
	public static String pageOfAddRateTicket(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		String statusStr = parameters.get("status");
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());
		
		int status = Convert.strToInt(statusStr, 0);
		if (status != 1 && status != 2 && status != 4) {
			json.put("code",-1);
			json.put("msg", "加息券查询：状态参数错误");
			 
			 return json.toString();
		}

		if(!StrUtil.isNumeric(currentPageStr)||!StrUtil.isNumeric(pageNumStr)){
			 json.put("code",-1);
			 json.put("msg", "分页参数不正确");
			 
			 return json.toString();
		}
		
		int currPage = Convert.strToInt(currentPageStr, 1);
		int pageSize = Convert.strToInt(pageNumStr, 10);
		
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		
		return shakeActivityAppService.pageOfAddRateTicket(userId, status, currPage, pageSize);
	}
	
	/**
	 * 查询投资可用的加息券
	 * 
	 * @param userId 用户Id
	 * @param amount 投资大于等于多少可用
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-21
	 */
	public static String listOfInvestUseAddRate(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		String userIdSign = parameters.get("userId");
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (userIdSignDecode.code < 1) {
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);

			return json.toString();
		}
		
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		
		String investAmt = parameters.get("investAmt");
		
		if (!StrUtil.isNumericPositiveInt(investAmt)) {
			json.put("code", -1);
			json.put("msg", "请输入正确的投标金额!");
			
			return json.toString();
		}
		
		double amt = Double.parseDouble(investAmt);
		
		json.put("code", 1);
		json.put("msg", "加息券查询成功");

		json.put("tickets", shakeActivityAppService.listOfInvestUseAddRate(userId, amt));
		
		return json.toString();
	}
	

}
