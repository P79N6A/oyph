package jobs;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.common.SettingService;
import services.core.DebtService;

public class CheckDebtIsFlow {
	
	/** 注入系统设置service */
	SettingService settingService = Factory.getService(SettingService.class);
	

	public void doJob() {
		
		DebtService debtService = Factory.getService(DebtService.class);
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------定时判断债权过期,开始---------------------");
		}
		
		ResultInfo result = debtService.judgeDebtFlow();
		if(result.code < 1){
			
			LoggerUtil.error(true, "定时债权过期业务失败! %s", result.msg);
		}
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------定时判断债权过期,结束---------------------");
		}
		
	  
	}
}
