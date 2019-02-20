package jobs;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import com.shove.Convert;
import com.sun.mail.imap.protocol.FLAGS;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;
import com.sun.org.apache.xpath.internal.operations.Bool;

import common.constants.SettingKey;
import common.enums.JYSMSModel;
import common.enums.NoticeScene;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.JYSMSUtil;
import common.utils.TimeUtil;
import models.common.entity.t_transfer_notice;
import models.core.bean.Bill;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.On;
import services.common.NoticeService;
import services.common.SettingService;
import services.common.SmsJyCountService;
import services.common.TransferNoticeService;
import services.core.BillService;

public class BillRepanymentTime extends Job{
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	protected static BillService billService = Factory.getService(BillService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);

	protected static TransferNoticeService transferNoticeService = Factory.getService(TransferNoticeService.class);
	
	protected static SmsJyCountService smsJyCountService = Factory.getService(SmsJyCountService.class);

	public void doJob(){
		if (Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------开始执行定时任务：查询借款账单还款日数据开始----------");
		}
		
		Boolean flag = billService.sendMesToFinance();
		if (flag) {
			Logger.info("------还款通知发送成功(财务和风控)-------");
		}else{
			Logger.info("------还款通知发送失败(财务和风控)-------");
		}
		
		if (Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------执行结束：查询借款账单还款日数据结束----------");
		}
		
	}
}
