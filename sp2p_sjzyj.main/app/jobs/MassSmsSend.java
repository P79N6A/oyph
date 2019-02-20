package jobs;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;

import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.common.NoticeService;
import services.common.SettingService;

/**
 * 定时任务:群发短信(10分钟扫描一次)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月23日
 */
public class MassSmsSend {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	public void doJob() {
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------群发短信,开始---------------------");
		}
		
		NoticeService noticeService = Factory.getService(NoticeService.class);
		noticeService.sendMassSMSTask();
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------群发短信,结束---------------------");
		}
		
	}
}
