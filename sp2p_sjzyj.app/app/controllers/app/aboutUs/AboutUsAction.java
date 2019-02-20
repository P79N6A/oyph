package controllers.app.aboutUs;

import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import service.AboutUsService;
import services.common.SettingService;

import com.shove.Convert;
import common.constants.SettingKey;
import common.enums.DeviceType;
import common.utils.Factory;

/**
 * 更多(4)模块[OPT:4XX]
 *
 * @description 包含公司介绍、获取icon(2)、联系我们(3)，app版本信息等
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
public class AboutUsAction {
	private static AboutUsService aboutAppService = Factory.getService(AboutUsService.class);
	private static SettingService settingService = Factory.getService(SettingService.class);
	
	/***
	 * 
	 * 公司介绍（opt=411）
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-8
	 */
	public static String aboutUs(Map<String, String> parameters){

		return aboutAppService.findAboutUs();
	}
	
	/***
	 * 
	 * 联系我们（opt=402）
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-8
	 */
	public static String contactUs(Map<String, String> parameters){

		return aboutAppService.findContactUs();
	}
	
	/***
	 * 
	 * 平台注册协议（OPT=112）
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public static String registerProtocol(Map<String, String> parameters){
		
		return aboutAppService.findRegisterProtocol();
	}
	
	/***
	 * 获取APP版本信息
	 *
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-26
	 */
	public static String getPlatformInfo(Map<String, String> parameters) {
        JSONObject json = new JSONObject();
		
		String deviceTypeStr = parameters.get("deviceType");
		
		if (DeviceType.getEnum(Convert.strToInt(deviceTypeStr, -99)) == null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		int deviceType = Integer.parseInt(deviceTypeStr);
		
		/**ios 版本*/
		String iosVersion = settingService.findSettingValueByKey(SettingKey.IOS_NEW_VERSION);
		
		/**ios 升级类型*/
		String iosType = settingService.findSettingValueByKey(SettingKey.IOS_PROMOTION_TYPE);
		boolean iosPromotionType = (StringUtils.isNotBlank(iosType) && iosType.equals("1")) ? true : false;
		
		/**android 版本*/
		String androidVersion = settingService.findSettingValueByKey(SettingKey.ANDROID_NEW_VERSION);
	
		/**android 升级类型*/
		String androidType = settingService.findSettingValueByKey(SettingKey.ANDROID_PROMOTION_TYPE);
		boolean androidPromotionType = (StringUtils.isNotBlank(androidType) && androidType.equals("1")) ? true : false;

		json.put("version", deviceType == DeviceType.DEVICE_ANDROID.code ? androidVersion : iosVersion);
		json.put("promotionType", deviceType == DeviceType.DEVICE_ANDROID.code ? androidPromotionType : iosPromotionType);
		json.put("code", 1);
		json.put("msg", "查询成功");
		return json.toString();
	}
	
	
	

	
}
