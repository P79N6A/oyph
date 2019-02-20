package jobs;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import models.common.entity.t_user;
import play.Logger;
import play.db.jpa.JPA;
import play.jobs.Every;
import play.jobs.Job;
import services.common.SettingService;
import services.common.UserService;
import services.common.UserVIPGradeService;

/**
 * 用户VIP等级修改
 * 
 * @author guoShiJie
 * @createDate 2018.11.6
 * */

public class UpdateUserVIP {

	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserVIPGradeService userVIPGradeService = Factory.getService(UserVIPGradeService.class);
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	public void doJob () {
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------用户VIP等级修改，开始----------");
		}
		
		List<t_user> userList = userService.findAll();
		if (userList != null && userList.size() > 0) {
			for (int i = 0 ; i < userList.size() ; i++) {
				int res = userVIPGradeService.updateUserVipGrade(userList.get(i).id, DateUtil.addMonth(new Date(), 0));
				
				if (res > 0) {
					LoggerUtil.info(false, userList.get(i).id+"客户VIP等级修改成功");
				}else {
					LoggerUtil.info(false, userList.get(i).id+"客户VIP等级未改变");
				}
			}
		}
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------用户VIP等级修改，结束----------");
		}
	}
	
}
