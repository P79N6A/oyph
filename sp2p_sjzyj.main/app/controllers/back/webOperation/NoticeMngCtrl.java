package controllers.back.webOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_event_supervisor.Event;
import models.common.entity.t_template_notice;

import org.apache.commons.lang.StringUtils;

import services.common.NoticeService;

import com.shove.Convert;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.NoticeScene;
import common.enums.NoticeType;
import common.utils.Factory;
import common.utils.ResultInfo;
import common.utils.Security;

import controllers.common.BackBaseController;

/**
 * 后台-运维-通知模板控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
public class NoticeMngCtrl extends BackBaseController {

	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	/**
	 * 后台-运维-通知模板
	 *
	 * @rightID 207001
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月22日
	 */
	public static void showNoticeScenesPre() {

		List<NoticeScene> scenes = NoticeScene.getScenesWithTemplate();
		
		render(scenes);
	}
	
	/**
	 * 后台-运维-通知模板-进入通知模板管理界面-进入模板编辑界面
	 *
	 * @rightID 207002
	 * 
	 * @param sceneCode 场景的code(NoticeScene枚举的code)
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月22日
	 */
	public static void toEditNoticeScenePre(String sceneCode) {
		
		NoticeScene scene = NoticeScene.getEnum(Convert.strToInt(sceneCode, -1));
		if (scene == null) {
			flash.error("该类型通知模板不存在");
			
			showNoticeScenesPre();
		}
		t_template_notice sms = null;
		t_template_notice msg = null;
		t_template_notice email = null;
		
		List<t_template_notice> templates = noticeService.queryTemplatesByScene(scene);

		for (t_template_notice template : templates) {
			if (template.getType().equals(NoticeType.SMS)) {
				sms = template;
			} else if (template.getType().equals(NoticeType.MSG)) {
				msg = template;
			} else if (template.getType().equals(NoticeType.EMAIL)) {
				email = template;
			}
		}
		
		render(sms,msg,email,scene);
	}

	/**
	 * 后台-运维-通知模板-进入通知模板管理界面-更新消息模板模板内容
	 *
	 * @param 207002
	 *
	 * @param noticeSign 消息模板id的加密串
	 * @param content 更新后的模板内容
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月23日
	 */
	public static void editTemplateNotice(String noticeSign, String content) {
		ResultInfo result = new ResultInfo();
		result = Security.decodeSign(noticeSign, Constants.NOTEMP_ID_SIGN, Constants.VALID_TIME,ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			renderJSON(result);
		}
		
		if (StringUtils.isBlank(content)) {
			result.code = -1;
			result.msg = "内容不为空";

			renderJSON(result);
		}
		long template_id = Long.parseLong((String) result.obj);
		t_template_notice template = noticeService.findByTemplateId(template_id);
		NoticeType type = template.getType();
		if (NoticeType.SMS.equals(type) || NoticeType.MSG.equals(type)) {
			if (content.length() > 240) {
				result.code = -1;
				result.msg = "字数超出限制！";

				renderJSON(result);
			}

		}
		
		result = noticeService.updateTemplateNotice(template_id, content);
		if (result.code < 0) {
			result.code = -1;
			result.msg = "通知模板更新失败！";

			renderJSON(result);
		}
		
		long supervisor_id = getCurrentSupervisorId();
		Map<String, String> map = new HashMap<String, String>();
		map.put("template_id", template.id.toString());
		map.put("template_name", template.title);
		
		Event event = null;
		if (type.equals(NoticeType.SMS)) {
			event = Event.SMS_EDIT;
		} else if (type.equals(NoticeType.MSG)) {
			event = Event.MSG_EDIT;
		} else {
			event = Event.EMAIL_EDIT;
		}
		supervisorService.addSupervisorEvent(supervisor_id, event, map);
		
		renderJSON(result);
	}
	
}
