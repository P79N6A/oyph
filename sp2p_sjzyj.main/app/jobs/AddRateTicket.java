package jobs;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import services.common.SettingService;
import services.ext.redpacket.AddRateTicketService;

/**
 * 定时清除过期加息券 （每天凌晨2点执行）
 * 
 * @author niu 
 * @create 2017-12-18
 */
public class AddRateTicket {

	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	public void doJob() {
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------定时清理过期加息券，开始--------");
		}
		
		AddRateTicketService addRateTicketService = Factory.getService(AddRateTicketService.class);
		addRateTicketService.updateStatusByTime();	
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------定时清理过期加息券，结束--------");
		}
		
	}
	
}
