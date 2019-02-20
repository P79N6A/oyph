package jobs;

import java.io.File;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.shove.Convert;

import common.constants.SettingKey;
import common.enums.SuperviseReportType;
import common.utils.Factory;
import common.utils.JRBHttpsUtils;
import common.utils.JRBUtils;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import models.finance.entity.t_return_status;
import models.finance.entity.t_return_status.ReportType;
import play.Logger;
import play.Play;
import services.common.SettingService;
import services.core.BillService;
import services.finance.CashFlowService;
import services.finance.InternalControlChartService;
import services.finance.ReturnStatusService;
/**
 * 
 *
 * @ClassName: QuarterReport
 *
 * @description 金融办季度报表定时器
 *
 * @author LiuHangjing
 * @createDate 2018年10月11日-下午4:32:14
 */
public class QuarterReport {
	
	protected static CashFlowService cashFlowService = Factory.getService(CashFlowService.class);
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	protected static ReturnStatusService returnStatusService = Factory.getService(ReturnStatusService.class);
	
	protected static InternalControlChartService internalControlChartService = Factory.getService(InternalControlChartService.class);
	
	public void doJob(){
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("-----------金融办季报，开始----------");
		}
		
		cashFlowService.createCashFlowXml();
		
		File file = internalControlChartService.createInternalXML(Play.applicationPath.toString().replace("\\", "/")+"/data/season"+File.separator+JRBUtils.getXMLFileName(null,SuperviseReportType.INTERNAL_CONTROL_SITUATION_TABLE , "oyph"));
		if(file != null){
			
			LoggerUtil.info(false, "金融办发送结果: %s", JRBHttpsUtils.post(file,ReportType.SEASON));
		}
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			
			Logger.info("-----------金融办季报，结束----------");
		}
		
	}
}
