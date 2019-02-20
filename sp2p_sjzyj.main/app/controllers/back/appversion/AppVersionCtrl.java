package controllers.back.appversion;

import models.common.entity.t_event_supervisor.Event;

import org.apache.commons.lang.StringUtils;

import common.constants.SettingKey;
import common.enums.PromotionType;
import common.utils.LoggerUtil;
import controllers.common.BackBaseController;

/**
 * APP-版本管理
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年4月25日
 */
public class AppVersionCtrl extends BackBaseController{
	
	/**
	 * APP-版本管理（进入页面）
	 *
	 *
	 * @author yaoyi
	 * @createDate 2016年4月25日
	 */
	public static void toAppVersionSettingPre() {
		
		String ios_new_version = settingService.findSettingValueByKey(SettingKey.IOS_NEW_VERSION);
		String ios_promotion_type = settingService.findSettingValueByKey(SettingKey.IOS_PROMOTION_TYPE);
		if (ios_promotion_type == null) {
			ios_promotion_type = "0";
		}
		
		String android_new_version = settingService.findSettingValueByKey(SettingKey.ANDROID_NEW_VERSION);
		String android_promotion_type = settingService.findSettingValueByKey(SettingKey.ANDROID_PROMOTION_TYPE);
		if (android_promotion_type == null) {
			android_promotion_type = "0";
		}
		
		String android_new_version_dl = settingService.findSettingValueByKey(SettingKey.ANDROID_NEW_VERSION_DL);
		String android_promotion_type_dl = settingService.findSettingValueByKey(SettingKey.ANDROID_PROMOTION_TYPE_DL);
		if (android_promotion_type_dl == null) {
			android_promotion_type_dl = "0";
		}
		
		render(ios_new_version, ios_promotion_type, android_new_version, android_promotion_type, android_new_version_dl, android_promotion_type_dl);
	}
	
	/**
	 * 设置ios版本管理
	 *
	 * @param iosNewVersion
	 * @param iosPromotionType
	 *
	 * @author yaoyi
	 * @createDate 2016年4月25日
	 */
	public static void iosAppVersionSetting(String iosNewVersion, int iosPromotionType) {
		if (StringUtils.isBlank(iosNewVersion)) {
			flash.error("请填写ios最新版本!");
			
			toAppVersionSettingPre();
		}
		if (PromotionType.getEnum(iosPromotionType)==null) {
			flash.error("请选择升级方式!");
			
			toAppVersionSettingPre();
		}
		
		boolean flag1 = settingService.updateOrAddSetting(SettingKey.IOS_NEW_VERSION, iosNewVersion);
		boolean flag2 = settingService.updateOrAddSetting(SettingKey.IOS_PROMOTION_TYPE, String.valueOf(iosPromotionType));
		
		if (flag1 && flag2) {
			supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.APP_IOSVERSION_SETTING, null);
			
			flash.success("设置成功!");
		} else {
			LoggerUtil.error(true, "设置ios版本管理失败!");
			flash.error("设置失败!");
		}
		toAppVersionSettingPre();
	}
	
	/**
	 * 设置android版本管理
	 *
	 * @param androidNewVersion
	 * @param androidPromotionType
	 *
	 * @author yaoyi
	 * @createDate 2016年4月25日
	 */
	public static void androidAppVersionSetting(String androidNewVersion, int androidPromotionType) {
		if (StringUtils.isBlank(androidNewVersion)) {
			flash.error("请填写android最新版本!");
			
			toAppVersionSettingPre();
		}
		if (PromotionType.getEnum(androidPromotionType)==null) {
			flash.error("请选择升级方式!");
			
			toAppVersionSettingPre();
		}
		
		boolean flag1 = settingService.updateOrAddSetting(SettingKey.ANDROID_NEW_VERSION, androidNewVersion);
		boolean flag2 = settingService.updateOrAddSetting(SettingKey.ANDROID_PROMOTION_TYPE, String.valueOf(androidPromotionType));
		
		if (flag1 && flag2) {
			supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.APP_ANDROIDVERSION_SETTING, null);
			
			flash.success("设置成功!");
		} else {
			LoggerUtil.error(true, "设置android版本管理失败!");
			flash.error("设置失败!");
		}
		toAppVersionSettingPre();
	}
	
	/**
	 * 设置android(代理商)版本管理
	 *
	 * @param androidNewVersionDl
	 * @param androidPromotionTypeDl
	 *
	 * @author yaoyi
	 * @createDate 2016年4月25日
	 */
	public static void androidAppVersionSettingDl(String androidNewVersionDl, int androidPromotionTypeDl) {
		if (StringUtils.isBlank(androidNewVersionDl)) {
			flash.error("请填写android(代理商)最新版本!");
			
			toAppVersionSettingPre();
		}
		if (PromotionType.getEnum(androidPromotionTypeDl)==null) {
			flash.error("请选择升级方式!");
			
			toAppVersionSettingPre();
		}
		
		boolean flag1 = settingService.updateOrAddSetting(SettingKey.ANDROID_NEW_VERSION_DL, androidNewVersionDl);
		boolean flag2 = settingService.updateOrAddSetting(SettingKey.ANDROID_PROMOTION_TYPE_DL, String.valueOf(androidPromotionTypeDl));
		
		if (flag1 && flag2) {
			supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.APP_ANDROIDVERSION_SETTING_DL, null);
			
			flash.success("设置成功!");
		} else {
			LoggerUtil.error(true, "设置android(代理商)版本管理失败!");
			flash.error("设置失败!");
		}
		toAppVersionSettingPre();
	}
}
