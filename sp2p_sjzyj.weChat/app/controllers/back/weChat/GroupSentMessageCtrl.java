package controllers.back.weChat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import controllers.common.BackBaseController;
import models.common.entity.t_event_supervisor;
import models.entity.t_wechat_group_sent;
import models.entity.t_wechat_image_text_message;
import service.GroupClassifyService;
import service.GroupSentService;

/**
 * 后台-微信-群发消息-控制器
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年05月17日
 */
public class GroupSentMessageCtrl extends BackBaseController {
	
	protected static GroupSentService groupSentService = Factory.getService(GroupSentService.class);

	/**
	 * 进入群发消息列表界面
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年05月17日
	 */
	public static void showGroupSentListPre(int currPage,int pageSize){
		
		String title = params.get("title");
		
		PageBean<t_wechat_group_sent> page = groupSentService.pageOfGroupList(currPage, pageSize, title);
		
		render(page, title);
	
	}
	
	/**
	 * 进入群发消息添加界面
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年05月28日
	 */
	public static void toAddGroupSentPre(){
			
		render();
	
	}
	
	/**
	 * 保存群发消息
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年05月29日
	 */
	public static void addGroupSent() {
		
		t_wechat_group_sent groupSent = new t_wechat_group_sent();
		
		String title = params.get("title");
        if(StringUtils.isBlank(title)){
        	flash.error("群发标题不能为空");
        	toAddGroupSentPre();
        }else {
        	groupSent.title = title;
        	flash.put("title", title);
        }
		
		showGroupSentListPre(1, 10);
	}
	
	/**
	 * 进入群发消息修改界面
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年5月28日
	 */
	public static void toEditGroupSentPre(long groupId){
		
		render();
	
	}
	
	/**
	 *  
	 * @Description: 后台-微信-群发回复上下架
	 * @param 
	 * @param flag
	 * 
	 * @author liuyang
	 * 2018年5月29日
	 */
	public static void changeStatus(String sign,String flag) {

		ResultInfo result = Security.decodeSign(sign, Constants.INFORMATION_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
 		
		long Ids = Long.parseLong((String) result.obj);
		
		t_wechat_group_sent groupSent = groupSentService.findByID(Ids);
		
		if(groupSent==null){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
		} 
		boolean isUseFlag = groupSentService.isUseGroup(Ids, !groupSent.getIs_use().code);
		if(!isUseFlag){
			result.code = -1;
			result.msg = "上下架操作失败";
			
			renderJSON(result);
	    }else{
	    	//添加管理员事件
	    	long supervisorId = getCurrentSupervisorId();
			Map<String, String> supervisorEventParam = new HashMap<String, String>(); 
			supervisorEventParam.put("information_id", groupSent.id.toString());  
			supervisorEventParam.put("information_name", "群发回复");
			
			//下架事件
			if(groupSent.getIs_use().code){
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_DISABLED, supervisorEventParam);
			}else{
			//上架事件
				supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.INFO_ENABLE, supervisorEventParam);				
			}
	    }
		
		result.code=1;
		result.msg="上下架操作成功";
		result.obj=groupSent.getIs_use().code;
			
		renderJSON(result);
	}
}
