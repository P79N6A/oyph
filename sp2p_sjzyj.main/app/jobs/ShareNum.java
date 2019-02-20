package jobs;

import javax.persistence.Entity;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;
import common.utils.ResultInfo;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.On;
import services.activity.shake.UserGoldService;
import services.common.SettingService;
import services.core.BillService;


/**
 * 
 *
 * @ClassName: ShareNum
 *
 * @description 每天凌晨12:01将分享朋友圈次数归0
 *
 * @author HanMeng
 * @createDate 2018年11月1日-下午3:59:11
 */
//@Every("1min")
@On("0 01 00 ? * *")
public class ShareNum extends Job {
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	protected static UserGoldService userGoldService = Factory.getService(UserGoldService.class);

	public void doJob(){
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------定时清空分享朋友圈次数，开始----------");
		}
		userGoldService.updateShareNum();
	
		if (Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------定时清空分享朋友圈次数，结束----------");
		}
	}
}
