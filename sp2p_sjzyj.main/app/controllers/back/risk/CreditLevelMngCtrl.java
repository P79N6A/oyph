package controllers.back.risk;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import models.common.entity.t_credit_level;
import models.common.entity.t_event_supervisor.Event;
import net.sf.json.JSONObject;
import services.common.CreditLevelService;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.file.FileUtil;
import controllers.common.BackBaseController;

/**
 * 后台-风控-信用等级管理控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月18日
 */
public class CreditLevelMngCtrl extends BackBaseController {
	
	protected static CreditLevelService creditLevelService = Factory.getService(CreditLevelService.class);
	
	/**
	 * 后台-风控-信用等级-信用等级列表
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月29日
	 *
	 */
	public static void showCreditLevelsPre() {
		List<t_credit_level> list = creditLevelService.queryCreditLevels();
		
		render(list);
	}
	
	/**
	 * 后台-风控-信用等级-信用等级列表-编辑信用等级
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void toEditCreditLevelPre(String creditLevelSign) {
		
		ResultInfo result = new ResultInfo();
		result = Security.decodeSign(creditLevelSign, Constants.CREDITLEVEL_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error404();
		}
		long creditLevelId = Long.parseLong((String) result.obj);;
		
		t_credit_level creditLevel = creditLevelService.findCreditLevel(creditLevelId);
		
		render(creditLevel);
	}
	
	/**
	 * 后台-风控-信用等级编辑
	 * @description 
	 * 
	 * @param imgFile
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月25日
	 */
	public static void updateCreditLevel(String creditLevelSign, String levelName, String imgurl, 
			String imageResolution, String imageSize, String imageFormat){
		ResultInfo result = new ResultInfo();

		result = Security.decodeSign(creditLevelSign, Constants.CREDITLEVEL_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error404();
		}
		long levelId = Long.parseLong((String) result.obj);;
		
		result = creditLevelService.updateCreditLevel(levelId, levelName, imgurl, 
					imageResolution, imageSize, imageFormat);
		if (result.code < -1) {
			
			renderJSON(result);
		}
		
		/* 添加管理员事件 */
		Long supervisor_id = getCurrentSupervisorId();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("level_id", levelId+"");
		map.put("level_name", levelName);
		
		supervisorService.addSupervisorEvent(supervisor_id, Event.CREDITLEVEL_EDIT, map);
		
		flash.put("msg", result.msg);
		
		showCreditLevelsPre();
	}
	
	/**
	 * 上传信用等级图片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月5日
	 */
	public static void uploadCreditLevelImage(File imgFile, String fileName){
		response.contentType="text/html";
		ResultInfo result = new ResultInfo();
		if (imgFile == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";
			
			renderJSON(result);
		}
		if(StringUtils.isBlank(fileName) || fileName.length() > 32){
			result.code = -1;
			result.msg = "图片名称长度应该位于1~32位之间";
			
			renderJSON(result);
		}
		
		result = FileUtil.uploadImgags(imgFile);
		if (result.code < 0) {

			renderJSON(result);
		}
		
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		fileInfo.put("imgName", fileName);
		
		renderJSON(result);
	}
}
