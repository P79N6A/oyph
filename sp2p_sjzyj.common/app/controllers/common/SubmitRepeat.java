package controllers.common;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import play.Logger;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;
import services.common.SettingService;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.SettingKey;
import common.utils.Factory;

/**
 * 防止重复提交
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月7日
 */
public class SubmitRepeat extends Controller {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);

	@Before
	static void checkAccess() {
		
		SubmitOnly check = getActionAnnotation(SubmitOnly.class);
		SubmitCheck addCheck = getActionAnnotation(SubmitCheck.class);
		
		if(addCheck != null) {
			String uuid = UUID.randomUUID().toString();
			Cache.set(uuid, uuid, "30min");
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("重复提交生成的校验码:"+uuid);
			}

			 
			 flash.put("submitUuid",uuid);
		}
		
		if(check != null) {
			String uuid = params.get("uuidRepeat");
			
			if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
				Logger.info("重复提交生成的校验码:"+uuid);
			}
			
			if(StringUtils.isBlank(uuid) || Cache.get(uuid) == null) {
				String url = request.headers.get("referer").value();
				flash.error("请勿重复提交");
				redirect(url);
			}
			
			Cache.delete(uuid);
	    }
	}
	
}
