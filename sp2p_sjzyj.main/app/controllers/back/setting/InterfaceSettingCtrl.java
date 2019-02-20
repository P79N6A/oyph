package controllers.back.setting;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_event_supervisor;

import org.apache.commons.lang.StringUtils;

import common.constants.SettingKey;

import controllers.common.BackBaseController;

/**
 * 后台-设置-接口设置
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月28日
 */
public class InterfaceSettingCtrl extends BackBaseController {
	
	/**
	 * 后台-设置-接口设置
	 * 
	 * @rightID 802001
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void toInterfaceSettingPre() {

		//短信通道
		/*String service_sms_provider = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_PROVIDER);
		String service_sms_account = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_ACCOUNT);
		String service_sms_password = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_PASSWORD);*/
		
		String service_sms_provider = settingService.findSettingValueByKey(SettingKey.JY_SMS_PROVIDER);
		String service_sms_account = settingService.findSettingValueByKey(SettingKey.JY_SMS_APP_ID);
		String service_sms_password = settingService.findSettingValueByKey(SettingKey.JY_SMS_APP_KEY);
		
		//邮件通道
		String service_mail_account = settingService.findSettingValueByKey(SettingKey.SERVICE_MAIL_ACCOUNT);
		String service_mail_password = settingService.findSettingValueByKey(SettingKey.SERVICE_MAIL_PASSWORD);
		String email_website = settingService.findSettingValueByKey(SettingKey.EMAIL_WEBSITE);
		String pop3_server = settingService.findSettingValueByKey(SettingKey.POP3_SERVER);
		String stmp_server = settingService.findSettingValueByKey(SettingKey.STMP_SERVER);

		
		renderArgs.put("service_sms_provider", service_sms_provider);
		renderArgs.put("service_sms_account", service_sms_account);
		renderArgs.put("service_sms_password", service_sms_password);
		
		renderArgs.put("service_mail_account", service_mail_account);
		renderArgs.put("service_mail_password", service_mail_password);
		renderArgs.put("email_website", email_website);
		renderArgs.put("pop3_server", pop3_server);
		renderArgs.put("stmp_server", stmp_server);
		
		render();
	}
	
	/**
	 * 后台-设置-接口设置-修改短信通道
	 *
	 * @rightID 802002
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月9日
	 */
	public static void editSMSProvider() {
		checkAuthenticity();
		
		if (!valid_editSMSProvider()) {

			toInterfaceSettingPre();
		}
		
		Map<String, String> settings = new HashMap<String, String>();
		/*settings.put(SettingKey.SERVICE_SMS_PROVIDER, params.get("service_sms_provider"));
		settings.put(SettingKey.SERVICE_SMS_ACCOUNT, params.get("service_sms_account"));
		settings.put(SettingKey.SERVICE_SMS_PASSWORD, params.get("service_sms_password"));*/
		
		settings.put(SettingKey.JY_SMS_PROVIDER, params.get("service_sms_provider"));
		settings.put(SettingKey.JY_SMS_APP_ID, params.get("service_sms_account"));
		settings.put(SettingKey.JY_SMS_APP_KEY, params.get("service_sms_password"));
		
		settingService.updateSettings(settings);
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.PROVIDER_SMS, null);
		
		flash.success("短信接口设置成功");
		
		toInterfaceSettingPre();
	}
	
	/**
	 * 后台-设置-接口设置-修改邮件通道
	 *
	 * @rightID 802003
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月9日
	 */
	public static void editEmailProvider() {
		checkAuthenticity();
		
		if (!valid_editEmailProvider()) {

			toInterfaceSettingPre();
		}
		
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(SettingKey.SERVICE_MAIL_ACCOUNT, params.get("service_mail_account"));
		settings.put(SettingKey.SERVICE_MAIL_PASSWORD, params.get("service_mail_password"));
		settings.put(SettingKey.EMAIL_WEBSITE, params.get("email_website"));
		settings.put(SettingKey.POP3_SERVER, params.get("pop3_server"));
		settings.put(SettingKey.STMP_SERVER, params.get("stmp_server"));
		
		settingService.updateSettings(settings);
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.PROVIDER_EMAIL, null);
		
		flash.success("邮件接口设置成功");
		toInterfaceSettingPre();
	}
	
	/** editSMSProvider()的数据校验方法 */
	private static boolean valid_editSMSProvider() {
		String service_sms_provider = params.get("service_sms_provider");
		String service_sms_account = params.get("service_sms_account");
		String service_sms_password = params.get("service_sms_password");
		
		if (StringUtils.isBlank(service_sms_provider)) {
			flash.error("请选择短信提供商");

			return false;
		}
		
		if (StringUtils.isBlank(service_sms_account)) {
			flash.error("短信通道账号不能为空");

			return false;
		}
		
		if (StringUtils.isBlank(service_sms_password)) {
			flash.error("短信通道密码不能为空");

			return false;
		}
		
		return true;
	}
	
	/** editEmailProvider()的数据校验方法 */
	private static boolean valid_editEmailProvider() {
		
		String service_mail_account = params.get("service_mail_account");
		String service_mail_password = params.get("service_mail_password");
		String email_website = params.get("email_website");
		String pop3_server = params.get("pop3_server");
		String stmp_server = params.get("stmp_server");

		if (StringUtils.isBlank(service_mail_account)) {
			flash.error("邮件账号不能为空");

			return false;
		}
		
		if (StringUtils.isBlank(service_mail_password)) {
			flash.error("邮箱密码不能为空");

			return false;
		}
		
		if (StringUtils.isBlank(email_website)) {
			flash.error("邮箱登录地址不能为空");

			return false;
		}
		
		if (StringUtils.isBlank(pop3_server)) {
			flash.error("POP3服务器不能为空");

			return false;
		}
		
		if (StringUtils.isBlank(stmp_server)) {
			flash.error("STMP服务器不能为空");

			return false;
		}
		
		return true;
	}
}
