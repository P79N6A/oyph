package jobs;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;
import play.Logger;
import services.common.SettingService;
import services.finance.LenderCollectionService;
/**
 * 
 *
 * @ClassName: InsertMonthNine
 *
 * @description 金融办P09投资人收款明细表定时插入
 *
 * @author LiuHangjing
 * @createDate 2018年12月13日-上午9:59:28
 */
public class InsertMonthNine {
	
	protected LenderCollectionService lenderCollectionService = Factory.getService(LenderCollectionService.class);
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	public void doJob(){
		if (Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------开始执行定时任务：月报报表(报表9)向数据库插入数据开始----------");
		}
		long start,end;
		start = System.currentTimeMillis();
		
		/** P09投资人收款明细表定时插入*/
		lenderCollectionService.savaLenderCollection();
		
		end = System.currentTimeMillis();  
		System.out.println("start time:" + start+ "; end time:" + end+ "; Run Time:" + (end - start)/(1000*60) + "(min)");
		
		if (Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------执行结束：月报报表(报表9)向数据库插入数据结束----------");
		}
		
	}
}
