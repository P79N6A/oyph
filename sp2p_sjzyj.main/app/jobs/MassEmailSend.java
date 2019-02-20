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
 * 定时任务:群发邮件 
 * 
 *
 * @author liudong
 * @createDate 2016年4月5日
 *
 */
public class MassEmailSend {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	public void doJob() {
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------群发邮件,开始---------------------");
		}
		
		NoticeService noticeService = Factory.getService(NoticeService.class);
		noticeService.sendMassEmailTask();
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------群发邮件,结束---------------------");
		}
		
	}
}
