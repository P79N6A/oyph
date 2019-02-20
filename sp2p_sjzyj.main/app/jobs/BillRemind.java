package jobs;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.ResultInfo;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import play.jobs.OnApplicationStart;
import services.common.SettingService;
import services.core.BillService;

/**
 * 未还账单提醒(到期前一个星期提醒一次，到期当天提醒一次。  未还账单只提醒这2次)
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月1日
 */
public class BillRemind {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	
	public void doJob(){
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------账单到期提醒，开始----------");
		}
		
		
		BillService billService = Factory.getService(BillService.class);
		
		ResultInfo result = billService.billRemind();
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info(result.msg);
			
			Logger.info("-----------=账单到期提醒，结束----------");
		}
		
	}
	
}
