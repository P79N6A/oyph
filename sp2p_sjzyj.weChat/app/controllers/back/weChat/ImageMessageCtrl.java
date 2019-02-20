package controllers.back.weChat;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.IsUse;
import common.utils.AccessTokenThread;
import common.utils.Constant;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.WxUploadImage;
import common.utils.file.FileUtil;
import common.utils.wx.WeixinUtil;
import controllers.common.BackBaseController;
import models.common.entity.t_event_supervisor;
import models.entity.t_wechat_access_token;
import models.entity.t_wechat_image_message;
import models.entity.t_wechat_text_message;
import service.AccessTokenService;
import service.ImageMessageService;

/**
 * 后台-微信-图片回复-控制器
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年05月17日
 */
public class ImageMessageCtrl extends BackBaseController {
	
	protected static ImageMessageService imageMessageService = Factory.getService(ImageMessageService.class);
	
	protected static AccessTokenService accessTokenService = Factory.getService(AccessTokenService.class);

	/**
	 * 进入图片列表界面
	 *
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年05月17日
	 */
	public static void showImageListPre(int currPage,int pageSize){
		
		String keywords = params.get("keywords");
		
		PageBean<t_wechat_image_message> page = imageMessageService.pageOfImageList(currPage, pageSize, keywords);
		
		render(page, keywords);
	
	}
	
	/**
	 * 进入图片添加界面
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年05月23日
	 */
	public static void toAddImagePre() {
		
		render();
	}
	
	/**
	 * 进入图片编辑界面
	 *
	 * @param imageId 图片主键
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年05月23日
	 */
	public static void toEditImagePre(long imageId) {
		
		t_wechat_image_message images = imageMessageService.findByID(imageId);
		
		if(images == null) {
			flash.error("系统出错，请重试");
			showImageListPre(1, 10);
		}
		
		render(images);
	}
	
	/**
	 * 保存图片回复
	 *
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月23日
	 */
	public static void addImage(int matchs) {
		
		t_wechat_image_message images = new t_wechat_image_message();
		
		String keywords = params.get("keywords");
        if(StringUtils.isBlank(keywords)){
        	flash.error("关键词不能为空");
        	toAddImagePre();
        }else {
        	images.keywords = keywords;
        	flash.put("keywords", keywords);
        }
        
        /** 图片 */
        String filenames = params.get("image_url");
        String fileType = params.get("imageFormat");
        if(StringUtils.isBlank(filenames)){
        	flash.error("图片不能为空");
        	toAddImagePre();
        }else {
        	images.img_url = filenames;
        	images.img_format = fileType;
        	flash.put("image_url", filenames);
        	flash.put("imageFormat", fileType);
        }
		
        images.matchs = matchs;
        images.time = new Date();
        images.setIs_use(IsUse.NO_USE);
        images.save();
        
        flash.success("添加成功");
        showImageListPre(1,10);
	}
	
	/**
	 * 保存修改的图片
	 *
	 * @param imageId
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月23日
	 */
	public static void editImage(long imageId, int matchs) {
		
		t_wechat_image_message images = imageMessageService.findByID(imageId);
		
		if(images == null) {
			flash.error("编辑失败，请重新操作");
			showImageListPre(1,10);
		}
		
		String keywords = params.get("keywords");
        if(StringUtils.isBlank(keywords)){
        	flash.error("关键词不能为空");
        	toAddImagePre();
        }else {
        	images.keywords = keywords;
        	flash.put("keywords", keywords);
        }
        
        /** 图片 */
        String filenames = params.get("image_url");
        String fileType = params.get("imageFormat");
        if(StringUtils.isBlank(filenames)){
        	flash.error("图片不能为空");
        	toAddImagePre();
        }else {
        	images.img_url = filenames;
        	images.img_format = fileType;
        	flash.put("image_url", filenames);
        	flash.put("imageFormat", fileType);
        }
		
        images.matchs = matchs;
        images.time = new Date();
        images.save();
        
        flash.success("修改成功");
		showImageListPre(1, 10);
	}
	
	/**
	 *  
	 * @Description: 后台-微信-图片回复上下架
	 * @param 
	 * @param flag
	 * 
	 * @author liuyang
	 * 2018年5月23日
	 */
	public static void changeStatus(String sign,String flag) {

		ResultInfo result = Security.decodeSign(sign, Constants.INFORMATION_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
 		
		long imageId = Long.parseLong((String) result.obj);
		
		t_wechat_image_message images = imageMessageService.findByID(imageId);
		
		if(images==null){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
		} 
		boolean isUseFlag = imageMessageService.isUseImage(imageId, !images.getIs_use().code);
		if(!isUseFlag){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
	    }else{
	    	//添加管理员事件
	    	long supervisorId = getCurrentSupervisorId();
			Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
			supervisorEventParam.put("information_id", images.id.toString());  
			supervisorEventParam.put("information_name", "图片文本回复");
			
			//下架事件
			if(images.getIs_use().code){
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_DISABLED, supervisorEventParam);
			}else{
			//上架事件
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_ENABLE, supervisorEventParam);				
			}
	    }
		
		result.code=1;
		result.msg="上下架操作成功";
		result.obj=images.getIs_use().code;
			
		renderJSON(result);
	}
	
	/**
	 * 上传图片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author liuyang
	 * @createDate 2018年5月23日
	 */
	public static void uploadImage(File imgFile, String fileName){
		
		//t_wechat_access_token accessToken = WeixinUtil.getAccessToken(Constant.APPID, Constant.APPSECRET);
		t_wechat_access_token accessToken = WeixinUtil.getAccessToken(Constant.appid, Constant.appsecret);
		String accessTokens = accessToken.getToken();
		
		String url = "https://api.weixin.qq.com/cgi-bin/material/add_material?type=image&access_token=" + accessTokens; 
		
		String media_id = null;
		try {
			media_id = WxUploadImage.postFile(url, imgFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	 * 删除旧图片，上传新图片
	 * 
	 * @param imgFile
	 * @param fileName
	 * @param oldUrl
	 * @param oldFileType
	 *
	 * @author liuyang
	 * @createDate 2018年5月23日
	 */
	public static void uploadReplaceImage(File imgFile, String fileName, String oldUrl, String oldFileType){
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
		
		result = FileUtil.uploadReplaceImgags(imgFile, oldUrl, oldFileType);
		if (result.code < 0) {

			renderJSON(result);
		}
		
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		fileInfo.put("imgName", fileName);
		
		renderJSON(result);
	}
	
}
