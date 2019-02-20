package controllers.back.weChat;

import java.util.Date;
import java.util.HashMap;
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
import models.common.entity.t_company_style;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_information;
import models.entity.t_wechat_text_message;
import service.TextMessageService;

/**
 * 后台-微信-文本回复-控制器
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年05月17日
 */
public class TextMessageCtrl extends BackBaseController {
	
	protected static TextMessageService textMessageService = Factory.getService(TextMessageService.class);

	/**
	 * 进入文本列表界面
	 *
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 *
	 * @author liuyang
	 * @createDate 2018年05月17日
	 */
	public static void showTextListPre(int currPage,int pageSize){
		
		String keywords = params.get("keywords");
		
		PageBean<t_wechat_text_message> page = textMessageService.pageOfTextList(currPage, pageSize, keywords);
		
		render(page, keywords);
	
	}
	
	/**
	 * 进入文本添加界面
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年05月22日
	 */
	public static void toAddTextPre() {
		
		render();
	}
	
	/**
	 * 保存文本回复
	 *
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月22日
	 */
	public static void addText(int matchs) {
		
		t_wechat_text_message text = new t_wechat_text_message();
		
        String keywords = params.get("keywords");
        if(StringUtils.isBlank(keywords)){
        	flash.error("关键词不能为空");
        	toAddTextPre();
        }else {
        	text.keywords = keywords;
        	flash.put("keywords", keywords);
        }
        
        String content = params.get("content");
        if(StringUtils.isBlank(content)){
        	flash.error("内容简介不能为空");
        	toAddTextPre();
        }else {
        	text.content = content;
        	flash.put("content", content);
        }
        
        text.matchs = matchs;
        text.time = new Date();
        text.setIs_use(IsUse.NO_USE);
        text.save();
        
        flash.success("添加成功");
        showTextListPre(1,10);
        
	}
	
	/**
	 * 进入文本修改界面
	 *
	 * @param textId 文本主键
	 *
	 * @author liuyang
	 * @createDate 2018年05月22日
	 */
	public static void toEditTextPre(long textId) {
		
		t_wechat_text_message text = textMessageService.findByID(textId);
		
		if(text == null) {
			flash.error("系统错误，请重试");
			showTextListPre(1,10);
		}
		
		render(text);
	}
	
	/**
	 * 保存修改的文本
	 *
	 * @param textId 
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年5月22日
	 */
	public static void editText(long textId, int matchs) {

		t_wechat_text_message text = textMessageService.findByID(textId);
		
		if(text == null) {
			flash.error("编辑失败，请重新操作");
			showTextListPre(1,10);
		}
		
		String keywords = params.get("keywords");
        if(StringUtils.isBlank(keywords)){
        	flash.error("关键词不能为空");
        	toEditTextPre(text.id);
        }else {
        	text.keywords = keywords;
        	flash.put("keywords", keywords);
        }
        
        String content = params.get("content");
        if(StringUtils.isBlank(content)){
        	flash.error("内容简介不能为空");
        	toEditTextPre(text.id);
        }else {
        	text.content = content;
        	flash.put("content", content);
        }
        
        text.matchs = matchs;
        text.time = new Date();
        text.save();
        
        flash.success("修改成功");
        showTextListPre(1,10);
	}
	
	/**
	 *  
	 * @Description: 后台-微信-文本回复上下架
	 * @param 
	 * @param flag
	 * 
	 * @author liuyang
	 * 2018年5月22日
	 */
	public static void changeStatus(String sign,String flag) {

		ResultInfo result = Security.decodeSign(sign, Constants.INFORMATION_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		
		long textId = Long.parseLong((String) result.obj);
		
		t_wechat_text_message text = textMessageService.findByID(textId);
		
		if(text==null){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
		}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
		boolean isUseFlag = textMessageService.isUseText(textId, !text.getIs_use().code);
		if(!isUseFlag){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
	    }else{
	    	//添加管理员事件
	    	long supervisorId = getCurrentSupervisorId();
			Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
			supervisorEventParam.put("information_id", text.id.toString());  
			supervisorEventParam.put("information_name", "微信文本回复");
			
			//下架事件
			if(text.getIs_use().code){
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_DISABLED, supervisorEventParam);
			}else{
			//上架事件
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_ENABLE, supervisorEventParam);				
			}
	    }
		
		result.code=1;
		result.msg="上下架操作成功";
		result.obj=text.getIs_use().code;
			
		renderJSON(result);
	}
}
