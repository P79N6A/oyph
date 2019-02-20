package jobs;

import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.common.NoticeService;
import services.common.SettingService;

import java.util.Date;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.DateUtil;
import common.utils.Factory;

/**
 * 定时任务:发送短信(3分钟扫描一次)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月23日
 */
public class SmsSend {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	

	public void doJob() {
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------定时发送短信,开始---------------------");
		}
		
		NoticeService noticeService = Factory.getService(NoticeService.class);
		noticeService.sendSMSTask();
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------定时发送短信,开始---------------------");
		}
		
		
	}
	
}
