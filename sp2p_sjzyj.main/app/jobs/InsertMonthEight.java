package jobs;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;
import play.Logger;
import services.common.SettingService;
import services.finance.BorrowerRepaymentService;
/**
 * 
 *
 * @ClassName: InsertMonthEight
 *
 * @description 金融办P08借款人还款明细表定时插入
 *
 * @author LiuHangjing
 * @createDate 2018年12月13日-上午9:59:05
 */
public class InsertMonthEight {
	
	protected BorrowerRepaymentService borrowerRepaymentService = Factory.getService(BorrowerRepaymentService.class);
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	public void doJob(){
		if (Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------开始执行定时任务：月报报表(报表8)向数据库插入数据开始----------");
		}
		long start,end;
		start = System.currentTimeMillis();
		
		/** P08借款人还款明细表定时插入*/
		borrowerRepaymentService.saveBorrowerRepayment();
		
		end = System.currentTimeMillis();  
		System.out.println("start time:" + start+ "; end time:" + end+ "; Run Time:" + (end - start)/(1000*60) + "(min)");
		
		if (Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------执行结束：月报报表(报表8)向数据库插入数据结束----------");
		}
		
	}
}
