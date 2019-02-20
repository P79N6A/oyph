package common.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import common.enums.FundingRiskReportType;
import common.enums.SuperviseReportType;

/**
 * 金融办报文获取工具类
 * 
 * @author guoShiJie
 * @createDate 2018.10.9
 * */
public class JRBUtils {

	/**
	 * 报文头HashMap
	 * 
	 * @param SenderID 报文发起方
	 * @param ReceiverID 报文接收方
	 * @param ReportSender 报文发起人
	 * @param ReportReceiver 报文接收人
	 * @param ReportSendDate 报文发起时间
	 * @param ReportSendTime 报文发起时间
	 * @param ReportType 报文类型
	 * @param MesgID 通信级标识号
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.9
	 * */
	public static JSONObject getHeaderInfoMap (String senderID ,String receiverID , String reportSender , String reportReceiver , String reportSendDate , String reportSendTime , String reportType , String mesgID ) {
		
		JSONObject headerInfo = new JSONObject (true);
		headerInfo.put("SenderID", senderID );
		headerInfo.put("ReceiverID", receiverID );
		headerInfo.put("ReportSender", reportSender );
		headerInfo.put("ReportReceiver", reportReceiver );
		headerInfo.put("ReportSendDate", reportSendDate );
		headerInfo.put("ReportSendTime", reportSendTime );
		headerInfo.put("ReportType", reportType );
		headerInfo.put("MesgID", mesgID );
		
		return headerInfo;
	}
	
	/**
	 * 报文基本信息HashMap
	 * @param FillingAgency 填报机构
	 * @param ReportDate 报表日期
	 * @param Draftsman 填表人
	 * @param Reviewer 复核人
	 * @param LeadingOfficial 负责人
	 * */
	public static JSONObject getReportBasicInfoMap (String fillingAgency ,String reportDate , String draftsman ,String reviewer , String leadingOfficial) {
		JSONObject reportBasicInfo = new JSONObject (true);
		
		reportBasicInfo.put("FillingAgency", fillingAgency);
		reportBasicInfo.put("ReportDate", reportDate);
		reportBasicInfo.put("Draftsman", draftsman);
		reportBasicInfo.put("Reviewer", reviewer);
		reportBasicInfo.put("LeadingOfficial", leadingOfficial);
		
		return reportBasicInfo;
	}
	
	/**
	 * XML文件名命名
	 * 
	 * @param FundingRiskReportType
	 * @param SuperviseReportType
	 * @param prefix 前缀
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.9
	 * */
	public static String getXMLFileName ( FundingRiskReportType funding , SuperviseReportType supervise , String prefix ) {
		//String date = TimeUtil.dateToStr_yyyyMMdd(new Date());
		String fileName = null;
		String name = null;
		if ( funding != null ) {
			name = funding.description;
		}
		if (supervise != null) {
			name = supervise.description;
		}
		fileName = prefix+"_"+name+".xml";
		
		return fileName;
	}
	
	/**
	 * 接口请求返回解析
	 * */
	/*public static String parse(JSONObject result) {
		String resultValue = null;
		
		Object resultString = result.get("result");
		
		if (resultString.equals("0000")) {
			resultValue  = "成功";
		}else if (resultString.equals("0002")) {
			resultValue = "报文头错误";
		}else if (resultString.equals("0003")) {
			resultValue = "请求错误";
		}else if (resultString.equals("0004")) {
			resultValue = "系统错误";
		}else if (resultString.equals("0005")) {
			resultValue = "发送异常";
		}
		
		return resultValue;
	}*/
	
}
