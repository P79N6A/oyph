package controllers.app.wealth;

import java.util.Map;

import service.AccountAppService;
import net.sf.json.JSONObject;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;

/**
 * 交易记录
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
public class MyDealAction {

	private static AccountAppService accountAppService = Factory.getService(AccountAppService.class);
	
	/**
	 * 
	 * 交易记录接口（OPT=241）
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-6
	 */
	public static String pageOfUserDealRecords(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		String signId = parameters.get("userId");
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}
		
		long userId = Long.parseLong(result.obj.toString());

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
		
		return accountAppService.pageOfUserDealRecords(userId, currPage, pageSize);	
	}

	
}
