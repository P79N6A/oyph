package controllers.back.webOperation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_consultant;
import models.common.entity.t_event_supervisor;

import org.apache.commons.lang.StringUtils;

import play.mvc.With;
import services.common.ConsultantService;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.OSSUploadUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.file.FileUtil;
import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;

/**
 * 后台-运维-理财顾问
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With(SubmitRepeat.class)
public class ConsultantMngCtrl extends BackBaseController {

	/** 注入理财顾问services  */
	protected static ConsultantService consultantService = Factory.getService(ConsultantService.class);
	
	/**
	 * 查询理财顾问 列表
	 * @rightID 204001
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public static void showConsultantsPre(int currPage, int pageSize){	
		
		PageBean<t_consultant> page = consultantService.pageOfConsultantBack(currPage,pageSize);
		render(page);
	}
	
	/**
	 * 进入添加理财顾问页面
	 * @rightID 204002
	 *
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2016年1月8日
	 */
	@SubmitCheck
	public static void toAddConsultantPre(){
		
		render();
	}
	
	/**
	 * 添加理财顾问 
	 * @rightID 204002
	 *
	 * @param consultant 理财顾问 
	 * @param orderTime 排序时间
	 * 
	 * @return
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	@SubmitOnly
	public static void addConsultant(t_consultant consultant,String orderTime){
		checkAuthenticity();
		
		/* 回显数据 */
		flash.put("name", consultant.name);
		flash.put("image_url", consultant.image_url);
		flash.put("image_resolution", consultant.image_resolution);
		flash.put("image_format", consultant.image_format);
		flash.put("image_size", consultant.image_size);
		flash.put("code_url", consultant.code_url);
		flash.put("code_resolution", consultant.code_resolution);
		flash.put("code_size", consultant.code_size);
		flash.put("code_format", consultant.code_format);
		flash.put("orderTime", orderTime);
		
		if(StringUtils.isBlank(consultant.name)){
			flash.error("姓名不能为空");
			toAddConsultantPre();
		}
		if(StringUtils.isBlank(consultant.image_url) || StringUtils.isBlank(consultant.image_resolution) || StringUtils.isBlank(consultant.image_size)){
			flash.error("图像不能为空");
			toAddConsultantPre();
		}
		if(StringUtils.isBlank(consultant.code_url) || StringUtils.isBlank(consultant.code_resolution) || StringUtils.isBlank(consultant.code_size)){
			flash.error("二维码图像不能为空");
			toAddConsultantPre();
		}
		if(StringUtils.isBlank(orderTime)){
			flash.error("排序时间不能为空");
			toAddConsultantPre();
		}
		
		consultant.order_time = DateUtil.strToDate(orderTime,  Constants.DATE_PLUGIN);
		
		boolean addFlag = consultantService.addConsultant(consultant);

		if(!addFlag){
			flash.error("添加失败");
			toAddConsultantPre();
		}
		
		//添加管理员事件
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("consultant_id", consultant.id.toString());  
		supervisorEventParam.put("consultant_name", consultant.name);
		supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.CONSULTANT_ADD, supervisorEventParam);

		flash.success("添加理财顾问成功！");
		showConsultantsPre(1, 10);
	}
	
	/**
	 * 进入 编辑理财顾问 页面
	 * @rightID 204003
	 *
	 * @param id 理财顾问id
	 * @return
	
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public static void toEditConsultantPre(long id){
		t_consultant consultant = consultantService.findByID(id);	
		if(consultant == null){
			error404();
		}
		
		render(consultant);
	}
	
	/**
	 * 编辑理财顾问
	 * @rightID 204003
	 *
	 * @param sign 理财顾问加密id
	 * @param orderTime 排序时间 
	 * @param name 姓名
	 * @param imageUrl 头像路径
	 * @param imageResolution 头像分辨率 
	 * @param imageSize 头像大小
	 * @param imageFormat 头像格式 
	 * @param codeUrl 二维码路径 
	 * @param codeResolution 二维码分辨率
	 * @param codeSize 二维码大小
	 * @param codeFormat 二维码格式
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月28日
	 */
	public static void editConsultant(String sign,String orderTime, String name,
			String imageUrl, String imageResolution, String imageSize,
			String imageFormat, String codeUrl, String codeResolution,
			String codeSize, String codeFormat){
		ResultInfo result = Security.decodeSign(sign, Constants.CONSULTANT_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error404();
		}
		long consId = Long.parseLong((String) result.obj);
		
		if(StringUtils.isBlank(name)){
			flash.error("姓名不能为空");
			toEditConsultantPre(consId);
		}
		if(StringUtils.isBlank(imageUrl) || StringUtils.isBlank(imageResolution) || StringUtils.isBlank(imageSize)){
			flash.error("图像不能为空");
			toEditConsultantPre(consId);
		}
		if(StringUtils.isBlank(codeUrl) || StringUtils.isBlank(codeResolution) || StringUtils.isBlank(codeSize)){
			flash.error("二维码图像不能为空");
			toEditConsultantPre(consId);
		}
		if(StringUtils.isBlank(orderTime)){
			flash.error("排序时间不能为空");
			toEditConsultantPre(consId);
		}
			
		boolean editFlag = consultantService.updateConsultant(consId, DateUtil.strToDate(orderTime, Constants.DATE_PLUGIN),
				name, imageUrl, imageResolution, imageSize, imageFormat,codeUrl, codeResolution, codeSize, codeFormat);
		if(!editFlag){
			flash.error("编辑失败");
			toEditConsultantPre(consId);
		}
		
		//添加管理员事件
		t_consultant consultant = consultantService.findByID(consId);
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("consultant_id", consultant.id.toString());  
		supervisorEventParam.put("consultant_name", consultant.name);
		supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.CONSULTANT_EDIT, supervisorEventParam);
		
		flash.success("编辑理财顾问成功！");
		showConsultantsPre(1, 10);
	}
	
	/**
	 * 理财顾问删除
	 * @rightID 204004
	 *
	 * @param sign 理财顾问加密id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void delConsultant(String sign){
		ResultInfo result = Security.decodeSign(sign, Constants.CONSULTANT_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		long consId = Long.parseLong((String) result.obj);
		t_consultant consultant = consultantService.findByID(consId);
		boolean delUrl = OSSUploadUtil.deleteFile(consultant.image_url, consultant.image_format);
		boolean delUrls = OSSUploadUtil.deleteFile(consultant.code_url, consultant.code_format);
		boolean delFlag = consultantService.delConsultant(consId);
		if(!delFlag || !delUrl || !delUrls){
			result.code=-1;
			result.msg="删除失败";
			
			renderJSON(result);
		}
		
		//添加管理员事件
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("consultant_id", consultant.id.toString());  
		supervisorEventParam.put("consultant_name", consultant.name);
		supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.CONSULTANT_REMOVE, supervisorEventParam);

		
		result.code=1;
		result.msg="删除成功";
		
		renderJSON(result);
	}
	
	/**
	 * 上传理财顾问图片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月5日
	 */
	public static void uploadConsultantImage(File imgFile, String fileName){
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
