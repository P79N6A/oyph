package controllers.back.webOperation;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.file.FileUtil;
import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;
import models.activity.shake.entity.t_shake_activity;
import models.activity.shake.entity.t_shake_set;
import models.common.entity.t_consultant;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_select_photos;
import models.common.entity.t_select_theme;
import play.mvc.With;
import services.common.ConsultantService;
import services.common.SelectPhotosService;
import services.common.SelectThemeService;

/**
 * 后台 -优选照片上传分享
 * 
 * PhotoSharingCtrl
 * @author LiuPengwei
 * @createDate 2018年1月3日
 */


public class PhotoSharingCtrl extends BackBaseController {

	/** 注入优选照片services  */
	protected static SelectThemeService selectThemeService = Factory.getService(SelectThemeService.class);
	
	protected static SelectPhotosService selectPhotosService = Factory.getService(SelectPhotosService.class);
	
	
	/**
	 * 查询优选照片主题列表
	 * @rightID 214001
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	
	 * @author LiuPengwei
	 * @createDate 2018年1月3日
	 */
	public static void showSelectPhotosPre(int currPage, int pageSize){	
		
		PageBean<t_select_theme> page = selectThemeService.pageOfConsultantBack(currPage,pageSize);

		render(page);
	}
	
	/**
	 * 进入添加优选照片主题页面
	 * @rightID 214002
	 *
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月3日
	 */
	public static void toAddSelectPhotosPre(){
		
		render();
	}
	
	/**
	 * 添加优选主题
	 * @rightID 214002
	 *
	 * @param consultant 优选照片
	 * @param orderTime 排序时间
	 * 
	 * @return
	 * @author LiuPengwei
	 * @createDate 2018年1月3日
	 */
	public static void addSelectPhotos(t_select_theme consultant,String orderTime){
		checkAuthenticity();
				
		/* 回显数据 */
		flash.put("name", consultant.name);
		flash.put("image_url", consultant.image_url);
		flash.put("image_resolution", consultant.image_resolution);
		flash.put("image_format", consultant.image_format);
		flash.put("image_size", consultant.image_size);
		flash.put("orderTime", orderTime);
		flash.put("code_url", consultant.code_url);
		flash.put("code_resolution", consultant.code_resolution);
		flash.put("code_size", consultant.code_size);
		flash.put("code_format", consultant.code_format);
		
		if(StringUtils.isBlank(consultant.name)){
			flash.error("照片名称不能为空");
			toAddSelectPhotosPre();
		}
		if(StringUtils.isBlank(consultant.image_url) || StringUtils.isBlank(consultant.image_resolution) || StringUtils.isBlank(consultant.image_size)){
			flash.error("照片不能为空");
			toAddSelectPhotosPre();
		}
		if(StringUtils.isBlank(orderTime)){
			flash.error("排序时间不能为空");
			toAddSelectPhotosPre();
		}
		
		if(StringUtils.isBlank(consultant.code_url) || StringUtils.isBlank(consultant.code_resolution) || StringUtils.isBlank(consultant.code_size)){
			flash.error("横向标题图不能为空");
			toAddSelectPhotosPre();
		}
		
		consultant.order_time = DateUtil.strToDate(orderTime,  Constants.DATE_PLUGIN);
		
		boolean addFlag = selectThemeService.addSelectPhotos(consultant);

		if(!addFlag){
			flash.error("添加失败");
			toAddSelectPhotosPre();
		}
		
		flash.success("添加主题成功！");
		showSelectPhotosPre(1, 10);
	}
	
	/**
	 * 上传主题照片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author LiuPengwei
	 * @createDate 2018年1月3日
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

	
	
	/**
	 * 主题分享图片上传
	 * 
	 * @param themeId 主题id
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月3日
	 */
	
	public static void uploadPicturesPre(long themeId) {
		
		if (themeId <= 0) {
			showSelectPhotosPre(1, 10);
		}
		
		//上传图片
		List<t_select_photos> selectThemeList = selectPhotosService.findListByColumn("theme_id = ?", themeId);
		
		
		render(themeId, selectThemeList);
	}
	
	
	/**
	 * 添加优选照片
	 * @rightID 214002
	 *
	 * @param consultant 优选照片
	 * @param orderTime 排序时间
	 * 
	 * @return
	 * @author LiuPengwei
	 * @createDate 2018年1月3日
	 */
	public static void addSelectPhotoss(t_select_photos consultant, long themeId, String order){
		checkAuthenticity();
		
		/* 回显数据 */
		flash.put("order", order);
		flash.put("image_url", consultant.image_url);
		flash.put("image_resolution", consultant.image_resolution);
		flash.put("image_format", consultant.image_format);
		flash.put("image_size", consultant.image_size);
		flash.put("orderTime", consultant.order_times);
		
		int orders = Integer.parseInt(order) ;
		if( orders < 0){
			flash.error("排序序号不能为空");
			uploadPicturesPre(themeId);
		}
		if(StringUtils.isBlank(consultant.image_url) || StringUtils.isBlank(consultant.image_resolution) || StringUtils.isBlank(consultant.image_size)){
			flash.error("照片不能为空");
			uploadPicturesPre(themeId);
		}
		if(StringUtils.isBlank(consultant.order_times)){
			flash.error("排序时间轴不能为空");
			uploadPicturesPre(themeId);
		}
		
		consultant.orders = orders;
		consultant.theme_id = themeId;
		
		
		
		boolean addFlag = selectPhotosService.addSelectPhotoss(consultant);

		
		if(!addFlag){
			flash.error("添加失败");
			uploadPicturesPre(themeId);
		}
		
		flash.success("添加优选照片成功！");
		
		uploadPicturesPre(themeId);
	}
	
	/**
	 * 优选照片删除
	 * @rightID 214003
	 *
	 * @param photoId 优选图片id
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月4日
	 */
	public static void delPhoto(long photoId,long themeId){
		
		if (photoId < 0 ){
			
			flash.error("设置删除失败！");
			uploadPicturesPre(themeId);
		}	
			
		boolean delFlag = selectPhotosService.delPhotos(photoId);
		if(!delFlag){
			
			flash.error("删除失败！");
			uploadPicturesPre(themeId);
			
		}
		flash.success("删除成功");
		uploadPicturesPre(themeId);
		
	}
	
	
	/**
	 * 进入 主题编辑 页面
	 * @rightID 214002
	 *
	 * @param id 主题id
	 * @return
	
	 * @author LiuPengwei
	 * @createDate 2018年1月5日
	 */
	public static void toEditPhotoPre(long themeId){
		t_select_theme consultant = selectThemeService.findByID(themeId);	
		if(consultant == null){
			error404();
		}
		
		render(consultant);
	}
	
	/**
	 * 编辑 主题竖照和横照
	 * @rightID 214002
	 *
	 * @param id 主题id
	 * @param orderTime 主题时间 
	 * @param name 主题标题 
	 * @param imageUrl 竖照路径
	 * @param imageResolution 竖照分辨率 
	 * @param imageSize 竖照大小
	 * @param imageFormat 竖照格式 
	 * @param codeUrl 横照路径 
	 * @param codeResolution 横照分辨率
	 * @param codeSize 横照大小
	 * @param codeFormat 横照格式
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月5日
	 */
	public static void editConsultant(long themeId,String orderTime, String name,
			String imageUrl, String imageResolution, String imageSize,
			String imageFormat, String codeUrl, String codeResolution,
			String codeSize, String codeFormat){
		
		if ( themeId < 0) {
			
			error404();
		}

		if(StringUtils.isBlank(name)){
			flash.error("姓名不能为空");
			toEditPhotoPre(themeId);
		}
		if(StringUtils.isBlank(imageUrl) || StringUtils.isBlank(imageResolution) || StringUtils.isBlank(imageSize)){
			flash.error("图像不能为空");
			toEditPhotoPre(themeId);
		}
		if(StringUtils.isBlank(codeUrl) || StringUtils.isBlank(codeResolution) || StringUtils.isBlank(codeSize)){
			flash.error("二维码图像不能为空");
			toEditPhotoPre(themeId);
		}
		if(StringUtils.isBlank(orderTime)){
			flash.error("排序时间不能为空");
			toEditPhotoPre(themeId);
		}
			
		boolean editFlag = selectThemeService.updateConsultant(themeId, DateUtil.strToDate(orderTime, Constants.DATE_PLUGIN),
				name, imageUrl, imageResolution, imageSize, imageFormat,codeUrl, codeResolution, codeSize, codeFormat);
		if(!editFlag){
			flash.error("编辑失败");
			toEditPhotoPre(themeId);
		}
	
		flash.success("编辑理财顾问成功！");
		showSelectPhotosPre(1, 10);
	}
}
