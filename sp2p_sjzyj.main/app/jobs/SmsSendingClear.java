package jobs;

import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import play.jobs.OnApplicationStart;
import services.common.NoticeService;
import services.common.SendCodeRecordsService;
import services.common.SettingService;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;

/**
 * 定时任务:1、清理短信临时发送表已经发送的数据
 *        2、清理短信限制记录表中的所有数据
 *
 * @description 每天凌晨4点半中执行
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月23日
 */
public class SmsSendingClear {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	

	public void doJob() {
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----定时清理短信临时发送表和短信限制记录表，开始---");
		}
		
		
		/*清理短信临时发送表*/
		NoticeService noticeService = Factory.getService(NoticeService.class);
		noticeService.deleteSmsSending();
		
		/*清理短信限制记录表*/
		SendCodeRecordsService sendCodeRecordsService = Factory.getService(SendCodeRecordsService.class);
		sendCodeRecordsService.delSendRecords();
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----定时清理短信临时发送表和短信限制记录表，开始---");
		}
		
	}
	
}
