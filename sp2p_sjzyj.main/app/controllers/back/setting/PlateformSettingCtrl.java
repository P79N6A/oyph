package controllers.back.setting;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_event_supervisor.Event;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import common.utils.file.FileUtil;
import controllers.common.BackBaseController;

/**
 * 后台-设置-平台设置-控制器
 *
 * @description 包含平台设置、接口设置
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
public class PlateformSettingCtrl extends BackBaseController {

		
	/**
	 * 后台-设置-平台设置
	 * 
	 * @rightID 801001
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void toPlateformSettingPre() {

		//基本信息
		String platform_logo_filename = settingService.findSettingValueByKey(SettingKey.PLATFORM_LOGO_FILENAME);
		String platform_icon_filename = settingService.findSettingValueByKey(SettingKey.PLATFORM_ICON_FILENAME);
		String platform_name = settingService.findSettingValueByKey(SettingKey.PLATFORM_NAME);
		String company_name = settingService.findSettingValueByKey(SettingKey.COMPANY_NAME);
		String company_tel = settingService.findSettingValueByKey(SettingKey.COMPANY_TEL);
		String company_email = settingService.findSettingValueByKey(SettingKey.COMPANY_EMAIL);
		String company_address = settingService.findSettingValueByKey(SettingKey.COMPANY_ADDRESS);
		String company_qq1 = settingService.findSettingValueByKey(SettingKey.COMPANY_QQ1);
		String company_qq2 = settingService.findSettingValueByKey(SettingKey.COMPANY_QQ2);
		String company_qq3 = settingService.findSettingValueByKey(SettingKey.COMPANY_QQ3);
		String site_icp_number = settingService.findSettingValueByKey(SettingKey.SITE_ICP_NUMBER);
		
		renderArgs.put("platform_logo_filename", platform_logo_filename);
		renderArgs.put("platform_icon_filename", platform_icon_filename);
		renderArgs.put("platform_name", platform_name);
		renderArgs.put("company_name", company_name);
		renderArgs.put("company_tel", company_tel);
		renderArgs.put("company_email", company_email);
		renderArgs.put("company_address", company_address);
		renderArgs.put("company_qq1", company_qq1);
		renderArgs.put("company_qq2", company_qq2);
		renderArgs.put("company_qq3", company_qq3);
		renderArgs.put("site_icp_number", site_icp_number);
		
		//SEO
		String baidu_code = settingService.findSettingValueByKey(SettingKey.BAIDU_CODE);
		String seo_title = settingService.findSettingValueByKey(SettingKey.SEO_TITLE);
		String seo_description = settingService.findSettingValueByKey(SettingKey.SEO_DESCRIPTION);
		String seo_keywords = settingService.findSettingValueByKey(SettingKey.SEO_KEYWORDS);
		renderArgs.put("baidu_code", baidu_code);
		renderArgs.put("seo_title", seo_title);
		renderArgs.put("seo_description", seo_description);
		renderArgs.put("seo_keywords", seo_keywords);
		
		//安全设置
		String sensitive_words = settingService.findSettingValueByKey(SettingKey.SENSITIVE_WORDS);
		String security_lock_times = settingService.findSettingValueByKey(SettingKey.SECURITY_LOCK_TIMES);
		String security_lock_time = settingService.findSettingValueByKey(SettingKey.SECURITY_LOCK_TIME);
		renderArgs.put("sensitive_words", sensitive_words);
		renderArgs.put("security_lock_times", security_lock_times);
		renderArgs.put("security_lock_time", security_lock_time);
		
		//正版授权
		String register_code = settingService.findSettingValueByKey(SettingKey.REGISTER_CODE);
		renderArgs.put("register_code", register_code);
		
		//自动投标状态
		int isAutoInvestShow = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.IS_AUTO_INVEST_SHOW), 0);
		renderArgs.put("isAutoInvestShow", isAutoInvestShow);
		
		render();
	}

	/**
	 * 后台-设置-平台设置-修改平台基本信息设置
	 *
	 * @rightID 801002
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月13日
	 */
	public static void editInfo() {
		checkAuthenticity();
		
		if (!valid_editInfo()) {

			toPlateformSettingPre();
		}
		
		Map<String, String> infos = new HashMap<String, String>();
		infos.put(SettingKey.PLATFORM_LOGO_FILENAME, params.get("platform_logo_filename"));
		infos.put(SettingKey.PLATFORM_ICON_FILENAME, params.get("platform_icon_filename"));
		infos.put(SettingKey.PLATFORM_NAME, params.get("platform_name"));
		infos.put(SettingKey.COMPANY_NAME, params.get("company_name"));
		infos.put(SettingKey.COMPANY_TEL, params.get("company_tel"));
		infos.put(SettingKey.COMPANY_EMAIL, params.get("company_email"));
		infos.put(SettingKey.COMPANY_ADDRESS, params.get("company_address"));
		infos.put(SettingKey.COMPANY_QQ1, params.get("company_qq1"));
		infos.put(SettingKey.COMPANY_QQ2, params.get("company_qq2"));
		infos.put(SettingKey.COMPANY_QQ3, params.get("company_qq3"));
		infos.put(SettingKey.SITE_ICP_NUMBER, params.get("site_icp_number"));
		
		boolean flag = settingService.updateSettings(infos);
		if (!flag) {

			flash.error("平台基本信息更新失败，请稍后再试");
			toPlateformSettingPre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.SETTING_BASIC, null);
		
		flash.success("基本信息保存成功");
		
		toPlateformSettingPre();
	}
	
	/**
	 * 后台-设置-平台设置-修改SEO设置
	 *
	 * @rightID 801003
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月15日
	 */
	public static void editSEO() {
		checkAuthenticity();
		
		if (!valid_editSEO()) {

			toPlateformSettingPre();
		}
		Map<String, String> seos = new HashMap<String, String>();
		seos.put(SettingKey.BAIDU_CODE, params.get("baidu_code"));
		seos.put(SettingKey.SEO_TITLE, params.get("seo_title"));
		seos.put(SettingKey.SEO_DESCRIPTION, params.get("seo_description"));
		seos.put(SettingKey.SEO_KEYWORDS, params.get("seo_keywords"));
		
		boolean flag = settingService.updateSettings(seos);
		if (!flag) {

			flash.error("SEO信息更新失败，请稍后再试");
			toPlateformSettingPre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.SETTING_SEO, null);
		
		flash.success("SEO设置成功");
		
		toPlateformSettingPre();
	}

	/**
	 * 后台-设置-平台设置-修改安全设置
	 *
	 * @rightID 801004
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月10日
	 */
	public static void editSecurity(){
		checkAuthenticity();
		
		if (!valid_editSecurity()) {

			toPlateformSettingPre();
		}
		Map<String, String> securitys = new HashMap<String, String>();
		securitys.put(SettingKey.SENSITIVE_WORDS, params.get("sensitive_words"));
		securitys.put(SettingKey.SECURITY_LOCK_TIMES, params.get("security_lock_times"));
		securitys.put(SettingKey.SECURITY_LOCK_TIME, params.get("security_lock_time"));

		boolean flag = settingService.updateSettings(securitys);
		if (!flag) {

			flash.error("安全信息更新失败，请稍后再试");
			toPlateformSettingPre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.SETTING_SECURITY, null);
		
		flash.success("安全设置成功");
		
		toPlateformSettingPre();
		
	}
	
	/**
	 * 后台-设置-平台设置-修改注册码信息设置
	 *
	 * @rightID 801005
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月14日
	 */
	public static void editRegisterCode(String register_code){
		checkAuthenticity();
		
		if (StringUtils.isBlank(register_code) || register_code.length() > 128) {
			flash.error("请正确输入授权码!");

			toPlateformSettingPre();
		}
		
		boolean flag = settingService.updateSetting(SettingKey.REGISTER_CODE, register_code);
		if (!flag) {

			flash.error("注册码更新失败，请稍后再试");
			toPlateformSettingPre();
		}
		
		long supervisorId = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisorId, Event.SETTING_REGISTERCODE, null);
		
		flash.success("正版授权成功");
		
		toPlateformSettingPre();
	}
	
	/**
	 * 更改"自动投标"系统参数
	 *
	 * @param flag ture显示(1),false不显示0
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年3月26日
	 */
	public static void updateIsAutoInvestShow(boolean flag) {
		ResultInfo result = new ResultInfo();
		
		String _value = "0";
		if (flag) {
			_value = "1";
		}
		
		boolean display = settingService.updateSetting(SettingKey.IS_AUTO_INVEST_SHOW, _value);
		if (!display) {
			result.msg = "数据没有更新";
			
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "自动投标更改成功";
		
		long supervisor_id = getCurrentSupervisorId();
		supervisorService.addSupervisorEvent(supervisor_id, Event.SETTING_AUTOINVEST, null);
		
		renderJSON(result);
	}
	
	/** editInfo()校验方法 */
	private static boolean valid_editInfo(){
		String platform_logo_filename = params.get("platform_logo_filename");
		if (StringUtils.isBlank(platform_logo_filename)) {
			flash.error("请上传平台logo");

			return false;
		}
		
		String platform_icon_filename = params.get("platform_icon_filename");
		if (StringUtils.isBlank(platform_icon_filename)) {
			flash.error("请上传ICON图片");

			return false;
		}
		
		String platform_name = params.get("platform_name");
		if (StringUtils.isBlank(platform_name) || platform_name.length() > 20) {
			flash.error("平台名称超出限制");

			return false;
		}
		
		String company_name = params.get("company_name");
		if (StringUtils.isBlank(company_name) || company_name.length() > 20) {
			flash.error("公司名称超出限制");

			return false;
		}
		
		String company_tel = params.get("company_tel");
		if (StringUtils.isBlank(company_tel) ) {
			flash.error("请输入公司联系方式");

			return false;
		}
		
		String company_email = params.get("company_email");
		if (StringUtils.isBlank(company_email) || (!StrUtil.isEmail(company_email)) ) {
			flash.error("请正确输入公司邮件");

			return false;
		}
		
		String company_address = params.get("company_address");
		if (StringUtils.isBlank(company_address) || company_address.length() > 40) {
			flash.error("公司地址超出限制");

			return false;
		}
		
		String company_qq1 = params.get("company_qq1");
		if (StringUtils.isBlank(company_qq1)  || company_qq1.length() > 11) {
			flash.error("客服QQ输入有误");

			return false;
		}
		
		String site_icp_number = params.get("site_icp_number");
		if (StringUtils.isBlank(site_icp_number)  || site_icp_number.length() > 30 ) {
			flash.error("备案号超出限制");

			return false;
		}
		
		return true;
	}
	
	/** editSEO()的数据校验 */
	private static boolean valid_editSEO(){
		
		String baidu_code = params.get("baidu_code");
		if (StringUtils.isBlank(baidu_code) || baidu_code.length() > 1024 ) {
			flash.error("百度统计代码超出限制");

			return false;
		}
		String seo_title = params.get("seo_title");
		if (StringUtils.isBlank(seo_title) || seo_title.length() > 1024 ) {
			flash.error("SEO 标题超出限制");

			return false;
		}
		String seo_description = params.get("seo_description");
		if (StringUtils.isBlank(seo_description) || seo_description.length() > 1024 ) {
			flash.error("SEO描述超出限制");

			return false;
		}
		String seo_keywords = params.get("seo_keywords");
		if (StringUtils.isBlank(seo_keywords) || seo_keywords.length() > 1024 ) {
			flash.error("SEO关键字超出限制");

			return false;
		}
		
		return true;
	}
	
	/** editSecurity()的数据校验方法 */
	private static boolean valid_editSecurity() {
		
		String sensitive_words = params.get("sensitive_words");
		if (StringUtils.isNotBlank(sensitive_words) && sensitive_words.length() > 500 ) {
			flash.error("敏感字符超出限制");

			return false;
		}
		int security_lock_times = com.shove.Convert.strToInt(params.get("security_lock_times"), -1);
		if (security_lock_times < 1 || security_lock_times > 9) {
			flash.error("密码错误次数不正确");

			return false;
		}
		
		int security_lock_time = com.shove.Convert.strToInt(params.get("security_lock_time"), -1);
		if (security_lock_time < 1 || security_lock_time > 60) {
			flash.error("密码锁定时间超出限制");

			return false;
		}
		
		return true;
	}
	
	/**
	 * 上传平台图片
	 * 
	 * @param imgFile
	 * @param fileName
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月5日
	 */
	public static void uploadPlatformImage(File imgFile, String fileName){
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
