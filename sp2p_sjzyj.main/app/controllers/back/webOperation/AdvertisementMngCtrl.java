package controllers.back.webOperation;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import models.common.entity.t_advertisement;
import models.common.entity.t_advertisement.Location;
import models.common.entity.t_event_supervisor;
import models.core.entity.t_bid_item_supervisor;

import org.apache.commons.lang.StringUtils;

import play.data.Upload;
import play.libs.Files;
import play.mvc.With;
import services.common.AdvertisementService;
import services.core.BidItemSupervisorService;

import com.shove.Convert;

import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Target;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.OSSUploadUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.file.FileBase64ConvertUitl;
import common.utils.file.FileUtil;
import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;

/**
 * 后台-运维-广告图片控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With(SubmitRepeat.class)
public class AdvertisementMngCtrl extends BackBaseController {

	/** 注入广告图片管理services  */
	protected static AdvertisementService advertisementService = Factory.getService(AdvertisementService.class);
	
	protected static BidItemSupervisorService bidItemSupervisorService = Factory.getService(BidItemSupervisorService.class);

	/**
	 * 广告图片列表查询
	 * @rightID 203001
	 *
	 * @param location 位置
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @return
	
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void showAdsPre(int location, int currPage,int pageSize){		
		PageBean<t_advertisement> page = advertisementService.pageOfAdvertisementBack(currPage, pageSize, t_advertisement.Location.getEnum(location));
				
		render(page,location);
	}
	
	/**
	 * 进入添加广告图片页
	 * @rightID 203002
	 *
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2016年1月8日
	 */
	@SubmitCheck
	public static void toAddAdsPre(){
		
		render();
	}
	
	/**
	 * 添加广告图片
	 * @rightID 203002
	 *
	 * @param ads 广告图片 
	 * @param order_time 排序时间
	 * @param location 位置
	 * @param target 打开方式
	 * 
	 * @return
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	@SubmitOnly
	public static void addAds(t_advertisement ads,String order_time,int location,int target){
		checkAuthenticity();
		
		/* 回显数据 */
		flash.put("location", location);
		flash.put("name", ads.name);
		flash.put("image_url", ads.image_url);
		flash.put("image_resolution", ads.image_resolution);
		flash.put("image_format", ads.image_format);
		flash.put("image_size", ads.image_size);
		flash.put("url", ads.url);
		flash.put("target", target);
		flash.put("orderTime", order_time);
		
		if(StringUtils.isBlank(ads.name)){
			flash.error("名称不能为空");
			toAddAdsPre();
		}
		if(ads.name.length()<1 || ads.name.length()>10){
			flash.error("名称长度1~10个字符");
			toAddAdsPre();
		}
		if(StringUtils.isBlank(order_time)){
			flash.error("排序时间不能为空");
			toAddAdsPre();
		}
		if(Location.getEnum(location) == null){
			flash.error("位置不能为空");
			toAddAdsPre();
		}
		if(StringUtils.isBlank(ads.image_url) || StringUtils.isBlank(ads.image_format) || StringUtils.isBlank(ads.image_size) || StringUtils.isBlank(ads.image_resolution)){
			flash.error("上传图片不能为空");
			toAddAdsPre();
		}
		if(StringUtils.isNotBlank(ads.url)){
			if(ads.url.length()<1 || ads.url.length()>100){
				flash.error("链接长度为0到100个字符");
				toAddAdsPre();	
			}
			if(Target.getEnum(target) == null){
				flash.error("链接打开方式输入不正确");
				toAddAdsPre();
			}
			ads.setTarget(Target.getEnum(target));
		}
		
		//位置
		ads.setLocation(Location.getEnum(location));
		ads.order_time = DateUtil.strToDate(order_time,  Constants.DATE_PLUGIN);
		
		boolean addFlag = advertisementService.addAdvertisement(ads);
		if(!addFlag){
			flash.error("添加失败！");
			toAddAdsPre();
		}
		
		//添加管理员事件
		long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("image_id", ads.id.toString());  
		supervisorEventParam.put("image_name", ads.name);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.IMG_ADD, supervisorEventParam);
		
		flash.success("添加广告图片成功！");
		showAdsPre(0, 1, 10);
	}
	
	/**
	 * 进入广告图片编辑 页
	 * @rightID 203003
	 *
	 * @param id 广告图片id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void toEditAdsPre(long id){
		t_advertisement ads = advertisementService.findByID(id);
		if(ads==null){
			error404();
		}
		render(ads);
	}
	
	
	/**
	 * 编辑广告图片
	 * @rightID 203003
	 *
	 * @param sign 图片加密id
	 * @param name 名称
	 * @param orderTime 排序时间
	 * @param location 所在位置 
	 * @param imageUrl 图片路径
	 * @param imageResolution 图片分辨率
	 * @param imageSize 图片大小 
	 * @param imageFormat 图片格式
	 * @param url 广告链接
	 * @param target 链接打开方式 1-_self  2-_blank
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void editAds(String sign,String name, String orderTime, int location, String imageUrl, String imageResolution, String imageSize,
			String imageFormat, String url, int target){
		
		ResultInfo result = Security.decodeSign(sign, Constants.ADS_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error404();
		}
		long adsId = Long.parseLong((String) result.obj);
		
		if(StringUtils.isBlank(name)){
			flash.error("名称不能为空");
			toEditAdsPre(adsId);
		}
		if(name.length()<1 || name.length()>10){
			flash.error("名称长度1~10个字符");
			toEditAdsPre(adsId);
		}
		if(StringUtils.isBlank(orderTime)){
			flash.error("排序时间不能为空");
			toEditAdsPre(adsId);
		}
		if(t_advertisement.Location.getEnum(location) == null){
			flash.error("位置不能为空");
			toEditAdsPre(adsId);
		}
		if(StringUtils.isBlank(imageUrl) || StringUtils.isBlank(imageResolution) || StringUtils.isBlank(imageSize) || StringUtils.isBlank(imageFormat)){
			flash.error("上传图片不能为空");
			toEditAdsPre(adsId);
		}
		
		if(StringUtils.isNotBlank(url)){
			if(url.length()<1 || url.length()>100){
				flash.error("链接长度为0到100个字符");
				toEditAdsPre(adsId);
				
			}
			if(Target.getEnum(target) == null){
				flash.error("链接打开方式输入不正确");
				toEditAdsPre(adsId);
			}
		}
		
		boolean editFlag = advertisementService.updateAdvertisement(adsId, name,
				DateUtil.strToDate(orderTime, Constants.DATE_PLUGIN),
				t_advertisement.Location.getEnum(location), imageUrl,
				imageResolution, imageSize, imageFormat, url, target);
		if(!editFlag){
			flash.error("编辑失败！");
			toEditAdsPre(adsId);
		}
		
		//添加管理员事件
		long supervisorId = getCurrentSupervisorId();
		t_advertisement advertisement = advertisementService.findByID(adsId);
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("image_id", advertisement.id.toString());  
		supervisorEventParam.put("image_name", advertisement.name);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.IMG_EDIT, supervisorEventParam);

		flash.success("编辑广告图片成功！");
		showAdsPre(0, 1, 10);
	}
	
	/**
	 * 广告图片 上下架
	 * @rightID 203004
	 *
	 * @param sign 广告图片加密id
	 * @return
	
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void isUseAds(String sign){
		ResultInfo result = Security.decodeSign(sign, Constants.ADS_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);             
		}
		long adsId = Long.parseLong((String) result.obj);
		t_advertisement ads = advertisementService.findByID(adsId);
		if(ads==null){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
		}
		
		boolean isUseFlag = advertisementService.updateAdvertisementIsUse(adsId, !ads.getIs_use().code);
		if(!isUseFlag){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
		}
		else{
			//添加管理员事件
			long supervisorId = getCurrentSupervisorId();
			Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
			supervisorEventParam.put("image_id", ads.id.toString());  
			supervisorEventParam.put("image_name", ads.name);
			if(ads.getIs_use().code){
				//下架
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.IMG_DISABLED, supervisorEventParam);
			}else{
				//上架
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.IMG_ENABLE, supervisorEventParam);			
			}
		}
		
		result.code=1;
		result.msg="上下架操作成功";
		result.obj=ads.getIs_use().code;
		
		renderJSON(result);
	}
	
	/**
	 * 删除广告图片
	 * @rightID 203005
	 *
	 * @param id 广告图片加密id 
	 * @return
	 * 	
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void delAdvertisement(String sign){
		ResultInfo result = Security.decodeSign(sign, Constants.ADS_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		long adsId = Long.parseLong((String) result.obj);
		t_advertisement advertisement = advertisementService.findByID(adsId);
		
		boolean delFlag = advertisementService.delAdvertisement(adsId);
		boolean delOSS = OSSUploadUtil.deleteFile(advertisement.image_url, advertisement.image_format);
		if(!delFlag || (!delOSS)){
			result.code=-1;
			result.msg="删除失败";
			
			renderJSON(result);
		}
		else{
			//添加管理员事件
			Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
			supervisorEventParam.put("image_id", advertisement.id.toString());  
			supervisorEventParam.put("image_name", advertisement.name);
			supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.IMG_REMOVE, supervisorEventParam);
		}
		
		result.code=1;
		result.msg="删除成功";
		
		renderJSON(result);
	}
	
	/**
	 * 上传广告图片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月5日
	 */
	public static void uploadAdsImage(File imgFile, String fileName){
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
	 * 上传马赛克图片
	 * 
	 * @param imgFile
	 * @param fileName
	 * @param itemId
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月5日
	 */
	public static void uploadMaSaiKeAdsImage(String base64Code, String fileName, long itemId){
		response.contentType="text/html";
		ResultInfo result = new ResultInfo();
		
		t_bid_item_supervisor bidItem = bidItemSupervisorService.findByID(itemId);
		
		if(bidItem == null) {
			result.code = -1;
			result.msg = "请选择要修改图片的标识";
			
			renderJSON(result);
		}
		
		if (base64Code == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";
			
			renderJSON(result);
		}
		if(StringUtils.isBlank(fileName) || fileName.length() > 32){
			result.code = -1;
			result.msg = "图片名称长度应该位于1~32位之间";
			
			renderJSON(result);
		}
		
		File imgFile = null;
		try {
			imgFile = FileBase64ConvertUitl.toFile(base64Code, fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result = FileUtil.uploadMaSaiKeImgags(imgFile);
		if (result.code < 0) {

			renderJSON(result);
		}
		
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		
		String staticFileName = (String) fileInfo.get("staticFileName");
		
		bidItem.url = staticFileName;
		bidItem.image_format = (String) fileInfo.get("fileSuffix");
		bidItem.save();
		
		renderJSON(result);
	}
	
	public static void convertImage(String imageUrl) {
		ResultInfo result = new ResultInfo();
		
		String base64Url = null;
		
		try {
			base64Url = FileBase64ConvertUitl.getURLImage(imageUrl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.code = -1;
			result.msg = "图片转化失败，请重试";
			
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = base64Url;
		
		renderJSON(result);
		
	}
		
	/**
	 * 多图片上传
	 * 
	 * @createDate 2017年6月2日
	 * @author lihuijun
	 */
	  public static synchronized void uploadAdsImages() {
	        // 保存图片
	        List<Upload> files = (List<Upload>) request.args.get("__UPLOADS");
	        ResultInfo result = new ResultInfo();
	        FileUtil.setResult();
	        for (Upload upload : files) {
	            if (upload.getSize() > 0) {
	                File f = upload.asFile();
	                String fileName = f.getName();
	                
	        		
	        		if (f == null) {
	        			result.code = -1;
	        			result.msg = "请选择要上传的图片";
	        			
	        			renderJSON(result);
	        		}
	        		if(StringUtils.isBlank(fileName) || fileName.length() > 32){
	        			result.code = -1;
	        			result.msg = "图片名称长度应该位于1~32位之间";
	        			
	        			renderJSON(result);
	        		}
	        		
	        		result = FileUtil.uploadImgagses(f);
	        		if (result.code < 0) {

	        			renderJSON(result);
	        		}
	        		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
	        		fileInfo.put("imgName", fileName);
	        		
	                
	               // File storeFile = new File("./public/images/" + UUID.randomUUID().toString());
	                //Files.copy(f, storeFile);
	            }
	        }
	        renderJSON(result);
	    }
	  
	  /**
	   * 
	   * @Title: upWatermarkImages
	   *
	   * @description 上传带水印图片
	   *
	   * @param  
	   * 
	   * @return void    
	   *
	   * @author yaozijun
	   *
	   * @createDate 2018年12月14日
	   */
	  public static synchronized void upWatermarkImages() {
	        // 保存图片
	        List<Upload> files = (List<Upload>) request.args.get("__UPLOADS");
	        ResultInfo result = new ResultInfo();
	        FileUtil.setResult();
	        for (Upload upload : files) {
	            if (upload.getSize() > 0) {
	                File f = upload.asFile();
	                String fileName = f.getName();
	                
	        		
	        		if (f == null) {
	        			result.code = -1;
	        			result.msg = "请选择要上传的图片";
	        			
	        			renderJSON(result);
	        		}
	        		if(StringUtils.isBlank(fileName) || fileName.length() > 32){
	        			result.code = -1;
	        			result.msg = "图片名称长度应该位于1~32位之间";
	        			
	        			renderJSON(result);
	        		}
	        		
	        		result = FileUtil.uploadMaSaiKeImgags(f);
	        		if (result.code < 0) {

	        			renderJSON(result);
	        		}
	        		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
	        		fileInfo.put("imgName", fileName);
	                
	               // File storeFile = new File("./public/images/" + UUID.randomUUID().toString());
	                //Files.copy(f, storeFile);
	            }
	        }
	        renderJSON(result);
	    }

}