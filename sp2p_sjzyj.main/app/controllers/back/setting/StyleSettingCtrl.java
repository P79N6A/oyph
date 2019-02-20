package controllers.back.setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_event_supervisor.Event;
import models.common.entity.t_theme;
import services.common.ThemeService;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;

import controllers.common.BackBaseController;

/**
 * 后台-设置-风格设置-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月21日
 */
public class StyleSettingCtrl extends BackBaseController {

	protected static ThemeService themeService = Factory.getService(ThemeService.class);
	
	/**
	 * 后台-设置-风格设置
	 *
	 * @rightID 805001
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void toStyleSettingPre() {
		String theme_name = settingService.findSettingValueByKey(SettingKey.THEME_NAME);
		
		t_theme theme = themeService.findThemeByFolder(theme_name);
		
		render(theme);
	}
	
	/**
	 * 后台-设置-风格设置-风格自定义
	 * 
	 * @rightID 805002
	 *
	 * @param mainColor 主色系
	 * @param auxColor 辅色系
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月10日
	 */
	public static void changeColor(String mainColor,String auxColor) {
		if (!StrUtil.isColor(mainColor)) {

			flash.error("主色系色值不符合要求");
			toStyleSettingPre();
		}
		if (!StrUtil.isColor(auxColor)) {

			flash.error("辅色系色值不符合要求");
			toStyleSettingPre();
		}
		
		Map<String, String> themeMap = new HashMap<String, String>();

		themeMap.put(SettingKey.THEME_CUSTOMIZE, "1");
		themeMap.put(SettingKey.THEME_MAIN_COLOR, mainColor);
		themeMap.put(SettingKey.THEME_AUX_COLOR, auxColor);
		
		boolean flag = settingService.updateSettings(themeMap);
		if (!flag) {
			LoggerUtil.info(true, "自定义风格时出错");

			flash.error("自定义风格失败");
			toStyleSettingPre();
		}
		
		Long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.STYLE_CUSTOMIZE, null);
		
		flash.success("自定义风格设置成功，请切换到系统前台进行查看!");
		
		toStyleSettingPre();
	}
	
	/**
	 * 设置-风格设置-一键换肤-(进入皮肤选择界面)
	 *
	 * @rightID 805003
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void showSkinsPre() {
		List<t_theme> themes = themeService.findAll();
	
		render(themes);
	}
	
	/**
	 * 后台-设置-风格设置-一键换肤-(一键换肤)
	 * 
	 * @rightID 805003
	 *
	 * @param themeSign 主题ID的加密串
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月18日
	 */
	public static void changeTheme(String themeSign){
		ResultInfo result = Security.decodeSign(themeSign, Constants.THEME_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error("更新失败，请刷新页面后重试");
			
			showSkinsPre();
		}
		
		long themeId = Long.parseLong((String) result.obj);
		t_theme theme = themeService.findByID(themeId);
		if (theme == null) {
			flash.error("选择的主题不存在!");

			showSkinsPre();
		}
		
		String theme_name = settingService.findSettingValueByKey(SettingKey.THEME_NAME);
		
		Map<String, String> themeMap = new HashMap<String, String>();
		themeMap.put(SettingKey.THEME_NAME, theme.folder);
		if (theme.isWidget) {
			themeMap.put(SettingKey.THEME_WIDGET, theme.widget);
		}
		themeMap.put(SettingKey.THEME_CUSTOMIZE, "0");
		themeMap.put(SettingKey.THEME_MAIN_COLOR, theme.main_color);
		themeMap.put(SettingKey.THEME_AUX_COLOR, theme.aux_color);
		
		boolean flag = settingService.updateSettings(themeMap);
		if (!flag) {
			LoggerUtil.info(true, "切换主题时出错");
			
			flash.error("主题替换失败");
			showSkinsPre();
		}
		
		Long supervisorId = getCurrentSupervisorId();
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("skin_id", themeId+"");
		supervisorEventParam.put("skin_name", theme.name);
		supervisorService.addSupervisorEvent(supervisorId, Event.STYLE_CHANGE_SKIN, supervisorEventParam);
		
		if (theme.folder.equals(theme_name)) {

			flash.success("主题已经还原，请切换到系统前台进行查看!");
		} else {
			
			flash.success("主题已经更换成功，请切换到系统前台进行查看!");
		}
		
		showSkinsPre();
	}
	
}
