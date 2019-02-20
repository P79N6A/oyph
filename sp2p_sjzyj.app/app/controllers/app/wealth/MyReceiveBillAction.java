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

public class MyReceiveBillAction {

	private static AccountAppService accountAppService = Factory.getService(AccountAppService.class);
	
	/***
	 * 
	 * 回款计划 （OPT=242）
	 * @param parameters
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-1
	 */
	public static String pageOfReceiveBill (Map<String, String> parameters) throws Exception{
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");
		String currPage = parameters.get("currPage");
		String pageSize = parameters.get("pageSize");
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			 return json.toString();
		}

		if(!StrUtil.isNumeric(currPage)||!StrUtil.isNumeric(pageSize)){
			 json.put("code",-1);
			 json.put("msg", "分页参数不正确");
			 
			 return json.toString();
		}
		
		int curr = Convert.strToInt(currPage, 1);
		int size = Convert.strToInt(pageSize, 10);
		if (curr < 1) {
			curr = 1;
		}
		if (size < 1) {
			size = 10;
		}
		
		long userId = Long.parseLong(result.obj.toString());
		
		return accountAppService.pageOfInvestReceive(curr, size, userId);
	}
	
}
