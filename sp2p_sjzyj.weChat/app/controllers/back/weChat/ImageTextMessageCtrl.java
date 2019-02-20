package controllers.back.weChat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.IsUse;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.common.BackBaseController;
import models.common.entity.t_event_supervisor;
import models.entity.t_wechat_image_message;
import models.entity.t_wechat_image_text_message;
import models.entity.t_wechat_type;
import service.ImageTextMessageService;
import service.WechatTypeService;

/**
 * 后台-微信-图文回复-控制器
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年05月17日
 */
public class ImageTextMessageCtrl extends BackBaseController {
	
	protected static ImageTextMessageService imageTextMessageService = Factory.getService(ImageTextMessageService.class);
	
	protected static WechatTypeService wechatTypeService = Factory.getService(WechatTypeService.class);

	/**
	 * 进入图文列表界面
	 *
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年05月24日
	 */
	public static void showImageTextListPre(int currPage,int pageSize){

		String keywords = params.get("keywords");
		
		PageBean<t_wechat_image_text_message> page = imageTextMessageService.pageOfTextImageList(currPage, pageSize, keywords);
		
		render(page, keywords);
	
	}
	
	/**
	 * 进入图文添加界面
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年05月24日
	 */
	public static void toAddImageTextPre() {
		
		List<t_wechat_type> wechatT = wechatTypeService.findAll();
		
		render(wechatT);
	}
	
	/**
	 * 进入图文编辑界面
	 *
	 * @param Ids 图文主键
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年05月24日
	 */
	public static void toEditImageTextPre(long Ids) {
		
		t_wechat_image_text_message teletext = imageTextMessageService.findByID(Ids);
		
		if(teletext == null) {
			flash.error("系统出错，请重试");
			showImageTextListPre(1, 10);
		}
		
		List<t_wechat_type> wechatT = wechatTypeService.findAll();
		
		render(teletext, wechatT);
		
	}
	
	/**
	 * 保存图文回复
	 *
	 * @param matchs 匹配
	 * @param is_likenum 点赞
	 * @param is_pageview 阅读
	 * @param is_show 显示封面
	 * @param is_attention 关注、查看全文
	 * @param typeId 分类id
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月24日
	 */
	public static void addImageText(int matchs, int isLikenum, int isPageview, int isShow, int isAttention, int typeId) {
		
		t_wechat_image_text_message teletext = new t_wechat_image_text_message();
		
		String keywords = params.get("keywords");
        if(StringUtils.isBlank(keywords)){
        	flash.error("关键词不能为空");
        	toAddImageTextPre();
        }else {
        	teletext.keywords = keywords;
        	flash.put("keywords", keywords);
        }
        
        String title = params.get("title");
        if(StringUtils.isBlank(title)){
        	flash.error("标题不能为空");
        	toAddImageTextPre();
        }else {
        	teletext.title = title;
        	flash.put("title", title);
        }
        
        teletext.author = params.get("author");
        
        String showContent = params.get("showContent");
        if(StringUtils.isBlank(showContent)){
        	flash.error("显示内容不能为空");
        	toAddImageTextPre();
        }else {
        	teletext.show_content = showContent;
        	flash.put("showContent", showContent);
        }
        
        /** 图片 */
        String filenames = params.get("image_url");
        String fileType = params.get("imageFormat");
        if(StringUtils.isBlank(filenames)){
        	flash.error("图片不能为空");
        	toAddImageTextPre();
        }else {
        	teletext.cover_img = filenames;
        	teletext.cover_format = fileType;
        	flash.put("image_url", filenames);
        	flash.put("imageFormat", fileType);
        }
        
        String content = params.get("content");
        if(StringUtils.isBlank(content)){
        	flash.error("图文详情页内容不能为空");
        	toAddImageTextPre();
        }else {
        	teletext.content = content;
        	flash.put("content", content);
        }
        
        teletext.matchs = matchs;
        teletext.is_attention = isAttention;
        teletext.is_likenum = isLikenum;
        teletext.is_pageview = isPageview;
        teletext.is_show = isShow;
        teletext.setIs_use(IsUse.NO_USE);
        teletext.time = new Date();
        
        if(content == null) {
        	teletext.outreach_url = params.get("outreachUrl");
        }
        
        teletext.type_id = typeId;
        teletext.save();
        
        flash.success("添加成功");
		showImageTextListPre(1,10);
	}
	
	/**
	 * 保存修改的图文
	 *
	 * @param imageId
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月24日
	 */
	public static void editImageText(long Ids, int matchs, int isLikenum, int isPageview, int isShow, int isAttention, int typeId) {
		
		t_wechat_image_text_message teletext = imageTextMessageService.findByID(Ids);
		
		String keywords = params.get("keywords");
        if(StringUtils.isBlank(keywords)){
        	flash.error("关键词不能为空");
        	toAddImageTextPre();
        }else {
        	teletext.keywords = keywords;
        	flash.put("keywords", keywords);
        }
        
        String title = params.get("title");
        if(StringUtils.isBlank(title)){
        	flash.error("标题不能为空");
        	toAddImageTextPre();
        }else {
        	teletext.title = title;
        	flash.put("title", title);
        }
        
        teletext.author = params.get("author");
        
        String showContent = params.get("showContent");
        if(StringUtils.isBlank(showContent)){
        	flash.error("显示内容不能为空");
        	toAddImageTextPre();
        }else {
        	teletext.show_content = showContent;
        	flash.put("showContent", showContent);
        }
        
        /** 图片 */
        String filenames = params.get("image_url");
        String fileType = params.get("imageFormat");
        if(StringUtils.isBlank(filenames)){
        	flash.error("图片不能为空");
        	toAddImageTextPre();
        }else {
        	teletext.cover_img = filenames;
        	teletext.cover_format = fileType;
        	flash.put("image_url", filenames);
        	flash.put("imageFormat", fileType);
        }
        
        String content = params.get("content");
        if(StringUtils.isBlank(content)){
        	flash.error("图文详情页内容不能为空");
        	toAddImageTextPre();
        }else {
        	teletext.content = content;
        	flash.put("content", content);
        }
        
        teletext.matchs = matchs;
        teletext.is_attention = isAttention;
        teletext.is_likenum = isLikenum;
        teletext.is_pageview = isPageview;
        teletext.is_show = isShow;
        teletext.setIs_use(IsUse.NO_USE);
        teletext.time = new Date();
        teletext.outreach_url = params.get("outreachUrl");
        teletext.type_id = typeId;
        teletext.save();
        
        flash.success("修改成功");
		showImageTextListPre(1,10);
	}
	
	/**
	 *  
	 * @Description: 后台-微信-图文回复上下架
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
 		
		long Ids = Long.parseLong((String) result.obj);
		
		t_wechat_image_text_message teletext = imageTextMessageService.findByID(Ids);
		
		if(teletext==null){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
		} 
		boolean isUseFlag = imageTextMessageService.isUseImage(Ids, !teletext.getIs_use().code);
		if(!isUseFlag){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
	    }else{
	    	//添加管理员事件
	    	long supervisorId = getCurrentSupervisorId();
			Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
			supervisorEventParam.put("information_id", teletext.id.toString());  
			supervisorEventParam.put("information_name", "图片文本回复");
			
			//下架事件
			if(teletext.getIs_use().code){
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_DISABLED, supervisorEventParam);
			}else{
			//上架事件
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_ENABLE, supervisorEventParam);				
			}
	    }
		
		result.code=1;
		result.msg="上下架操作成功";
		result.obj=teletext.getIs_use().code;
			
		renderJSON(result);
	}
}
