package controllers.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.db.jpa.Blob;
import play.mvc.Controller;
import play.mvc.Http;
import common.utils.Md5Utils;
import common.utils.ResultInfo;
import common.utils.file.FileUtil;

/** 
 * 图片相关操作(上传和查看)
 * 
 * @description 
 * 
 * @author ZhouYuanZeng 
 * @createDate 2016年4月6日 下午3:59:36  
 */
public class ImagesController extends Controller {
	
	/**
	 * KindEditor编辑专用的文件上传<br><br>
	 * error:0表示成功<br>
	 * message:信息<br>
	 * url:图片路径<br>
	 * 
	 *
	 * @param imgFile
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月5日
	 */
	public static void keImagesUpload(File imgFile){
		ResultInfo result = new ResultInfo();
		if (imgFile == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";
			
			if (result.code < 0) {
				JSONObject json = new JSONObject();
				json.put("message", "请选择要上传的图片");
				json.put("error", -1);

				renderText(json.toString());
			}
		}
		
		result = FileUtil.uploadImgags(imgFile);
		if (result.code < 0) {
			JSONObject json = new JSONObject();
			json.put("message", result.msg);
			json.put("error", -1);
			
			renderText(json.toString());
		}
		
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		
		String filename = (String) fileInfo.get("staticFileName");
				
		JSONObject json = new JSONObject();
		json.put("url", Play.configuration.getProperty("http.path")+filename);
		json.put("error", 0);
		
		renderText(json.toString());
	}
	
	/**
	 * 图片查看
	 *
	 * @description 这里有个坑，比如说在basic中这个方法配置的路由为/imags,但是实际上暴露给用户的是/xx(前缀)/images，例如main中加载这个module的路由为:  *       /                       module:com.shovesoft.sp2p.basic<br>
	 *				那么完整的路径为 :/basic/imags。直接使用@{xxxAction.xxMethod()}的方式没有问题，但是手工编写路由时，请<b style="color:red;">小心</b>
	 *
	 * @param uuid 图片的UUID
	 * @throws FileNotFoundException
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static void images(String uuid) throws FileNotFoundException {
		Blob blob = new Blob();
    	InputStream is = null;
    	
    	uuid = compatibleUuid(uuid);
    	
		try {
			is = new FileInputStream(new File(blob.getStore(), uuid.split("\\.")[0]));
		} catch (FileNotFoundException e) {
			is = new FileInputStream(Play.getFile("/public/common/default.jpg"));
		}
		
    	renderBinary(is);
	}
	
	/**
	 * 下载图片
	 *
	 * @param uuid
	 * @param imageName
	 *
	 * @author yaoyi
	 * @createDate 2016年2月23日
	 */
	public static void downLoadImages(String uuid, String imageName){
		Blob blob = new Blob();
		Http.Response.current().headers.put("Pragma", new Http.Header("Pragma", "public"));
		Http.Response.current().headers.put("Cache-Control", new Http.Header("Cache-Control", "max-age=0"));
		
		if (StringUtils.isBlank(imageName)) {
			imageName = "default.png";
		}
    	
		uuid = compatibleUuid(uuid);
    	
    	File file = null;
    	try {
    		file = new File(blob.getStore(), uuid.split("\\.")[0]);
    		if(!file.canRead()){
    			file = Play.getFile("/public/common/default.jpg");
        	}
    	} catch (Exception e) {
    		file = Play.getFile("/public/common/default.jpg");
    	}
    	
		renderBinary(file, imageName);
	}
	
	/**
	 * 兼容带相对路径的图片
	 *
	 * @param uuid
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月23日
	 */
	public static String compatibleUuid(String uuid){
    	String name = Play.configuration.getProperty("attachments.path", "attachments");
    	
    	if(StringUtils.isNotBlank(uuid) && uuid.contains(name)){
    		
    		uuid = uuid.substring(uuid.indexOf(name)+name.length()+1);
    	}
    	
    	return uuid;
	}
	
	
	
	/**
	 * app图片上传
	 *
	 * @param imgFile
	 *
	 * @author luzhiwei 
	 * @createDate 2016-4-25
	 */
	public static void appImagesUpload(File imgFile) {
		
		response.contentType="text/html";
		response.encoding = "UTF-8";
		ResultInfo result = new ResultInfo();
		JSONObject  json = new JSONObject();
		if (imgFile == null) {
			json.put("code", -1);
			json.put("msg", "请选择要上传的图片");
			renderJSON(json);
		}
		
		result = FileUtil.uploadImgags(imgFile);
		if (result.code < 0) {
			json.put("code", -1);
			json.put("msg", "上传失败");
			renderJSON(json);
		} 
		
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		
		System.out.println("加密前路径"+fileInfo.get("staticFileName").toString());
		
		json.put("code", 1);
		json.put("msg",  "上传成功");
		json.put("fileName", Md5Utils.KL(fileInfo.get("staticFileName").toString()));
	
		renderText(json);
	}
	
}
