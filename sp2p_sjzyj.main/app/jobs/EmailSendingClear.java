package jobs;

import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import services.common.NoticeService;
import services.common.SettingService;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;

/**
 * 定时任务:清理邮件临时发送表中已经发送的数据
 *
 * @description 每天凌晨4点中执行
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月23日
 */

public class EmailSendingClear {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	
	public void doJob() {
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------定时清理邮件临时发送表，开始--------------");
		}
		
		NoticeService noticeService = Factory.getService(NoticeService.class);
		noticeService.deleteEmailSending();
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------定时清理邮件临时发送表，结束--------------");
		}
		
	}
	
}
