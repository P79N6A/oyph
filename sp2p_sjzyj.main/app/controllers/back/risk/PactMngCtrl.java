package controllers.back.risk;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_event_supervisor;
import models.common.entity.t_template_pact;

import org.apache.commons.lang.StringUtils;

import services.common.PactService;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.file.FileUtil;
import controllers.common.BackBaseController;

/**
 * 后台-风控-合同模板管理控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月18日
 */
public class PactMngCtrl extends BackBaseController {

	protected static PactService pactService = Factory.getService(PactService.class);
	
	/**
	 * 后台-风控-合同模板-合同模板列表
	 *
	 * @rightID 406001
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void showPactsPre() {

		List<t_template_pact> pacts = pactService.queryAllTemps();

		render(pacts);
	}
	
	/**
	 * 后台-风控-合同模板-合同模板列表-进入编辑合同模板界面
	 * 
	 * @rightID 406002
	 *
	 * @param pactTempSign 合同模板加密串
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void toEditPactPre(String pactTempSign) {
		ResultInfo result = Security.decodeSign(pactTempSign, Constants.MSG_PACTTEMP_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			flash.error(result.msg);
			showPactsPre();
		}
		
		long tempid = Long.parseLong((String) result.obj);
		t_template_pact template = pactService.findByTempId(tempid);
		if (template == null) {

			error404();
		}
		
		render(template);
	}
	
	/**
	 * 后台-风控-合同模板-合同模板列表-修改合同模板
	 *
	 * @rightID 406002
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月18日
	 */
	public static void editPactTemp() {
		
		String pactTempSign = params.get("pactTempSign");
		String name = params.get("name");
		String content = params.get("content");
		String image_url = params.get("image_url");
		String imageResolution = params.get("imageResolution");
		String imageSize = params.get("imageSize");
		String imageFormat = params.get("imageFormat");
		
		ResultInfo result = Security.decodeSign(pactTempSign, Constants.MSG_PACTTEMP_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error(result.msg);

			showPactsPre();
		}
		
		if (!valida_editPactTemp(name, content)) {

			toEditPactPre(pactTempSign);
		}
		
		long tempid = Long.parseLong((String) result.obj);
		
		boolean flag = pactService.updatePactTemp(tempid, name, content, image_url, imageResolution, imageSize, imageFormat);
		if (!flag) {
			flash.error("更新失败");

			toEditPactPre(pactTempSign);
		}
		
		flash.success("合同模板更新成功");
		
		long supervisorId = getCurrentSupervisorId();
		t_template_pact temp = pactService.findByTempId(tempid);
		Map<String, String> param = new HashMap<String, String>();
		param.put("pact_id", tempid+"");
		param.put("pact_name", temp.name);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.PACT_EDIT, param);
		
		showPactsPre();
	}
	
	/** editPactTemp()方法的数据校验方法 */
	private static boolean valida_editPactTemp(String name,String content){
		if (StringUtils.isBlank(name) || name.length() > 20) {
			flash.error("标题长度超出限制");

			return false;
		}
		if (StringUtils.isBlank(content)) {
			flash.error("请输入内容");

			return false;
		}
		
		return true;
	}
	
	/**
	 * 上传合同模板水印图片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月5日
	 */
	public static void uploadPactTempImage(File imgFile, String fileName){
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
