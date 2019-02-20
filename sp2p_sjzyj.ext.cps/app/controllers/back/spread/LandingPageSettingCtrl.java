package controllers.back.spread;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.utils.Factory;
import controllers.common.BackBaseController;
import models.common.entity.t_event_supervisor;
import models.ext.cps.entity.t_three_elements_setting;
import services.ext.cps.ThreeElementsService;

/**
 * 落地页设置
 * 
 * @author guoShiJie
 * @createDate 2018.6.27
 * */
public class LandingPageSettingCtrl extends BackBaseController {
	
	protected static ThreeElementsService threeElementsService = Factory.getService(ThreeElementsService.class);

	/**
	 * 落地页设置页面
	 * 
	 * @author guoShiJie
	 * @createDate 2018.6.27
	 * */
	public static void toLandingPagePre () {
		t_three_elements_setting setting = threeElementsService.findElementsByKey("landing_page_key");
		
		render(setting);
	}
	
	/**
	 * 编辑落地页
	 * @author guoShiJie
	 * @createDate 2018.6.27
	 * */
	public static void editLandingPage () {
		
		String titleStr = params.get("title");
		String keywordStr = params.get("keywords");
		String descriptionStr = params.get("description");
		String idStr = params.get("id");
		String keyStr = params.get("_key");
		Long id = StringUtils.isNotBlank(idStr) ? Long.parseLong(idStr) : null;
		t_three_elements_setting setting = new t_three_elements_setting();
		setting.title = titleStr;
		setting.keyword = keywordStr;
		setting.describe1 = descriptionStr;
		setting._key = keyStr;
		setting.id = id;
		
		int res = threeElementsService.updateElements(setting);
		if (res > 0) {
			long supervisorId = getCurrentSupervisorId();
			supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.EDIT_LANDPAGE, null);
			flash.error("修改成功");
			toLandingPagePre ();
		}
		flash.error("修改失败");
		toLandingPagePre ();
	}
}
