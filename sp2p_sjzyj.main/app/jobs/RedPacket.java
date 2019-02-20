package jobs;

import models.ext.redpacket.entity.t_red_packet_user;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;
import daos.ext.redpacket.RedpacketUserDao;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.common.SettingService;
import services.ext.redpacket.RedpacketService;

public class RedPacket {

	protected RedpacketService redpacketService = Factory.getService(RedpacketService.class);
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	public void doJob() {
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------修改红包过期状态,开始---------------------");
		}
		
		//解锁锁定的红包
		redpacketService.updateRePacketLockStatus(t_red_packet_user.RedpacketStatus.IN_USED.code, t_red_packet_user.RedpacketStatus.RECEIVED.code) ;
		
		//将过期的红包状态修改
		redpacketService.updateRePacketExpiredLimitTime(t_red_packet_user.RedpacketStatus.EXPIRED.code) ;
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------修改红包过期状态,结束---------------------");
		}
		
	}
}
