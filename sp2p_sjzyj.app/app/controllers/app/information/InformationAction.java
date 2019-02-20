package controllers.app.information;

import java.util.Map;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import net.sf.json.JSONObject;
import service.InformationAppService;

/**
 * 资讯模块[OPT:4XX]
 * 
 * @author guoshijie
 * @createdate 2017.12.18
 * */
public class InformationAction {

	private static InformationAppService informationAppService = Factory.getService(InformationAppService.class);
	
	/**
	 * 资讯列表[OPT:424]
	 * 
	 * @param parameters
	 * @author guoshijie
	 * @createdate 2017.12.18
	 * */
	public static String listOfInformations(Map<String , String> parameters){
		JSONObject json = new JSONObject();
		
		String currentPageStr = parameters.get("currPage");
		String pageNumStr = parameters.get("pageSize");
		String informationType = parameters.get("informationType");

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
		
		return informationAppService.pageOfInformations(informationType, currPage, pageSize);
	}
	
	/**
	 * 资讯详情[OPT:425]
	 * 
	 * @param parameters
	 * @author guoshijie
	 * @createdate 2017.12.18
	 * */
	public static String informationDetials(Map<String , String> parameters) {
		JSONObject json = new JSONObject();
		
		String informationIdSign = parameters.get("informationIdSign");

		ResultInfo result = Security.decodeSign(informationIdSign, Constants.INFORMATION_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		
		if (result.code < 1) {
			 json.put("code",-1);
			 json.put("msg", result.msg);
			 return json.toString();
		}
		long id = Long.parseLong(result.obj.toString());
		
		return informationAppService.findDetailById(id);
	}
}
