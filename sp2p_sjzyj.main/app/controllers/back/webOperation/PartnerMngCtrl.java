package controllers.back.webOperation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_event_supervisor;
import models.common.entity.t_partner;

import org.apache.commons.lang.StringUtils;

import play.mvc.With;
import services.common.PartnerService;
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
 * 后台-运维-合作伙伴控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With(SubmitRepeat.class)
public class PartnerMngCtrl extends BackBaseController {

	/** 注入合作伙伴services  */
	protected static PartnerService partnerService = Factory.getService(PartnerService.class);
	
	/**
	 * 查询合作伙伴列表
	 * @rightID 205001
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void showPartnersPre(int currPage,int pageSize){
		
		PageBean<t_partner> page = partnerService.pageOfPartnerBack(currPage, pageSize);
		
		render(page);
	}
	
	/**
	 * 删除合作伙伴
	 * @rightID 205004
	 *
	 * @param sign 合作伙伴加密id
	 * @return
	
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public static void delPartner(String sign){
		ResultInfo result = Security.decodeSign(sign, Constants.PARTNER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		long partnerId = Long.parseLong((String) result.obj);
		t_partner partner = partnerService.findByID(partnerId);
		
		boolean delUrl = OSSUploadUtil.deleteFile(partner.image_url, partner.image_format);
		boolean delFlag = partnerService.delPartner(partnerId);
		if(!delFlag || !delUrl){
			result.code=-1;
			result.msg="删除失败";
			
			renderJSON(result);
		}
		
		//添加管理员事件
		long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("partner_id", partner.id.toString());  
		supervisorEventParam.put("partner_name", partner.name);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.PARTNER_REMOVE, supervisorEventParam);
	
		
		result.code=1;
		result.msg="删除成功";
		
		renderJSON(result);
	}
	
	/**
	 * 进入  添加合作伙伴页面
	 * @rightID 205002
	 *
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	@SubmitCheck
	public static void toAddPartnerPre(){
		
		render();
	}
	
	/**
	 * 添加伙伴确认页面
	 * @rightID 205002
	 *
	 * @param partner 合作伙伴 
	 * @param orderTime 排序时间
	 * 
	 * @return
	 * @author liudong
	 * @createDate 2016年1月4日
	 */
	@SubmitOnly
	public static void addPartner(t_partner partner, String orderTime){
		checkAuthenticity();
		
		/* 回显数据 */
		flash.put("name", partner.name);
		flash.put("image_url", partner.image_url);
		flash.put("image_resolution", partner.image_resolution);
		flash.put("image_format", partner.image_format);
		flash.put("image_size", partner.image_size);
		flash.put("url", partner.url);
		flash.put("orderTime", orderTime);
		
		if(StringUtils.isBlank(partner.name)){
			flash.error("名称不能为空");
			toAddPartnerPre();
		}
		if(StringUtils.isBlank(partner.image_url) || StringUtils.isBlank(partner.image_format) || StringUtils.isBlank(partner.image_size) || StringUtils.isBlank(partner.image_resolution)){
			flash.error("上传图片不能为空");
			toAddPartnerPre();
		}
		if(StringUtils.isNotBlank(partner.url)){
			if(partner.url.length()<1 || partner.url.length()>100){
				flash.error("链接长度为0到100个字符");
				toAddPartnerPre();	
			}
		}
		
		if(StringUtils.isBlank(orderTime)){
			flash.error("排序时间不能为空");
			toAddPartnerPre();
		}
		partner.order_time = DateUtil.strToDate(orderTime,  Constants.DATE_PLUGIN);
		
		boolean addFlag = partnerService.addPartner(partner);
		if(!addFlag){
			flash.error("添加失败");
			toAddPartnerPre();
		}
		
		//添加管理员事件
		long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("partner_id", partner.id.toString());  
		supervisorEventParam.put("partner_name", partner.name);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.PARTNER_ADD, supervisorEventParam);

		flash.success("添加合作伙伴成功！");
		showPartnersPre(1, 10);
	}
	
	/**
	 * 后台-运维-合作伙伴-编辑合作伙伴
	 * @rightID 205003
	 * 
	 * 进入 编辑页面
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void toEditPartnerPre(long id) {
		t_partner partner = partnerService.findByID(id);
		if (partner == null) {
			error404();
		}
		render(partner);
	}

	/**
	 * 编辑确认页面 
	 * @rightID 205003
	 *
	 * @param sign 合作伙伴加密id
	 * @param name  名称
	 * @param orderTime 排序时间
	 * @param imageUrl 图片路径
	 * @param imageResolution 图片分辨率
	 * @param imageSize 文件大小 
	 * @param imageFormat 文件格式
	 * @param url  合作伙伴链接
	 * @param target 链接打开方式  true-_self, false-_blank
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2016年1月5日
	 */
	public static void editPartner(String sign,String name, String orderTime, String imageUrl,
			String imageResolution, String imageSize, String imageFormat,
			String url, int target) {
		ResultInfo result = Security.decodeSign(sign, Constants.PARTNER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error404();
		}
		long partnerId = Long.parseLong((String) result.obj);
		t_partner partner = partnerService.findByID(partnerId);
		
		boolean editFlag = partnerService.upadtePartner(partnerId, name, 
				DateUtil.strToDate(orderTime, Constants.DATE_PLUGIN), imageUrl, imageResolution, 
				imageSize, imageFormat, url, target);
		if(!editFlag){
			
			flash.error("编辑失败");
			toEditPartnerPre(partnerId);
		}
		
		//添加管理员事件
		long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
		supervisorEventParam.put("partner_id", partner.id.toString());  
		supervisorEventParam.put("partner_name", partner.name);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.PARTNER_EDIT, supervisorEventParam);
		
		flash.success("编辑合作伙伴成功！");
		showPartnersPre(1,10);
	}
	
	/**
	 * 上传合作伙伴图片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月5日
	 */
	public static void uploadPartnerImage(File imgFile, String fileName){
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
