package controllers.app.wealth;

import java.util.HashMap;
import java.util.Map;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.Md5Utils;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import models.app.bean.QuestionListBean;
import models.common.entity.t_question;
import net.sf.json.JSONObject;
import services.common.QuesitonService;

/**
 * 
 * 问题反馈
 * 
 * @author niu
 * @create 2017-11-14
 * 
 */
public class MyQuestionAction {
	
	private static QuesitonService quesitonService = Factory.getService(QuesitonService.class); 
	
	/**
	 * 保存提问的问题（ OPT=264 ）
	 * 
	 * @param parameters 问题参数
	 * @return 
	 * 
	 * @author niu
	 * @create 2017-11-14
	 */
	public static String saveQuestion(Map<String, String> parameters) {
		//定义返回结果
		Map<String, Object> result = new HashMap<String, Object>();
		
		//问题参数
		String questionType = parameters.get("questionType");
		String question = parameters.get("question");
		String questionImages = Md5Utils.JM(parameters.get("questionImages"));
		
		String userId = parameters.get("userId");
		
		//参数验证	
		ResultInfo result1 = Security.decodeSign(userId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result1.code < 1) {
			result.put("code", ResultInfo.LOGIN_TIME_OUT);
			result.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			return JSONObject.fromObject(result).toString();
		}
		if (questionType == null || questionType.equals("") || Convert.strToInt(questionType, 0) == 0) {
			result.put("code", -1);
			result.put( "msg", "请选择问题类型");
			
			return JSONObject.fromObject(result).toString();
		}
		if (question == null || question.equals("")) {
			result.put("code", -1);
			result.put( "msg", "请选填写问题详情");
			
			return JSONObject.fromObject(result).toString();
		}
		if (questionImages == null) {
			result.put("code", -1);
			result.put( "msg", "获取图片文件路径失败");
			
			return JSONObject.fromObject(result).toString();
		}
		
		//保存提问问题
		Boolean saveSuccess = quesitonService.saveQuestion(Convert.strToInt(questionType, 0), question, questionImages, Long.parseLong(result1.obj.toString()));
		
		if (!saveSuccess) {
			result.put("code", -1);
			result.put( "msg", "问题提问失败");
			
			return JSONObject.fromObject(result).toString();
		}
		
		result.put("code", 1);
		result.put( "msg", "问题提问成功");
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 分页查询问题列表（ OPT=265 ）
	 * 
	 * @param parameters 问题参数
	 * @return 
	 * 
	 * @author niu
	 * @create 2017-11-14
	 */
	public static String pageOfQuestion(Map<String, String> parameters) {

		//定义返回结果
		Map<String, Object> result = new HashMap<String, Object>();
		
		//获取参数
		String currPage = parameters.get("currPage");
		String pageSize = parameters.get("pageSize");
		String status   = parameters.get("status");
		String userId = parameters.get("userId");
		
		//参数验证	
		ResultInfo result1 = Security.decodeSign(userId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result1.code < 1) {
			result.put("code", ResultInfo.LOGIN_TIME_OUT);
			result.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 
			return JSONObject.fromObject(result).toString();
		}
		
		//
		int curr = Convert.strToInt(currPage, 0);
		int page = Convert.strToInt(pageSize, 0);
		int stat = Convert.strToInt(status, 0);
		
		PageBean<QuestionListBean> pageQuestion = quesitonService.pageOfQuesiton(curr, page, stat, Long.parseLong(result1.obj.toString()));
		
		result.put("code", 1);
		result.put( "msg", "查询成功!");
		result.put("questionList", pageQuestion.page);
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 查询问题详情（ OPT=266 ）
	 * 
	 * @param parameters 问题参数
	 * @return 
	 * 
	 * @author niu
	 * @create 2017-11-14
	 */
	public static String questionDetails(Map<String, String> parameters) {
		
		//定义返回结果
		Map<String, Object> result = new HashMap<String, Object>();
		
		//获取参数
		String questionId = parameters.get("questionId");
		
		if (questionId == null || questionId.trim().equals("")) {
			result.put("code", -1);
			result.put( "msg", "问题Id获取失败");
			
			return JSONObject.fromObject(result).toString();
		}
		
		//
		long quizId = Convert.strToLong(questionId, 0);
		
		if (quizId <= 0) {
			result.put("code", -1);
			result.put( "msg", "问题Id不存在");
			
			return JSONObject.fromObject(result).toString();
		}
		
		result.put("code", 1);
		result.put( "msg", "查询问题详情成功");
		result.put("questionDetails", quesitonService.getQuestionDetails(quizId));
		
		
		
		return JSONObject.fromObject(result).toString();
	}
	
	
	
	
	
	
	
	
	
	
	
}
