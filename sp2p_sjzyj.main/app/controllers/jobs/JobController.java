package controllers.jobs;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.shove.Convert;
import com.shove.security.Encrypt;

import common.constants.ConfConst;
import common.constants.JobConstants;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import controllers.common.BaseController;
import jobs.AddRateTicket;
import jobs.BillRemind;
import jobs.BillRepanymentTime;
import jobs.CheckBidIsFlow;
import jobs.CheckDebtIsFlow;
import jobs.DailyPaper;
import jobs.DisclosureJob;
import jobs.EmailSend;
import jobs.EmailSendingClear;
import jobs.ExperienceBidAutoRepayment;
import jobs.IndexStatisticsJob;
import jobs.InsertMonthData;
import jobs.InsertMonthEight;
import jobs.InsertMonthNine;
import jobs.InsertMonthSeven;
import jobs.InsertMonthSix;
import jobs.MarkOverdue;
import jobs.MassEmailSend;
import jobs.MassSmsSend;
import jobs.QuarterReport;
import jobs.RedPacket;
import jobs.ServiceMonthJob;
import jobs.ServiceStatisticsJob;
import jobs.ShareNum;
import jobs.SmsSend;
import jobs.SmsSendingClear;
import jobs.TeamStatistics;
import jobs.TimeCalculate;
import jobs.UpdateUserVIP;
import jobs.batchDelectPDF;
import jobs.rewardGrant;
import net.sf.json.JSONObject;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Scope.Params;
import proxy.jobs.HistoryDeduct;
import proxy.jobs.HistoryProfit;
import proxy.jobs.MonthProfit;
import proxy.jobs.ProxyDeduct;

public class JobController extends BaseController {

	public static void testJob() {

		ResultInfo result = new ResultInfo();
		
		Map<String,String> paramMap = new HashMap<String, String>();
		String reqparams = null;
		try {
			//如果直接使用params.allSimple()则会出现乱码
			reqparams = params.urlEncode();
			
		} catch (Exception e) {
			
			LoggerUtil.error(false, e, "获取资金托管接口参数异常");
		}
		
		if (reqparams == null) {
			
			result.code = -1;
			result.msg  = "请求参数错误";
			String answer = JSONObject.fromObject(result).toString();
			renderJSON(answer);
		}
	
		String param[] = reqparams.split("&");
		for (int i = 0; i < param.length; i++) {
			String content = param[i];
			String key = content.substring(0, content.indexOf("="));
			String value = content.substring(content.indexOf("=") + 1, content.length());
			try {
				paramMap.put(key, URLDecoder.decode(value,"UTF-8"));
			} catch (Exception e) {
				
				result.code = -1;
				result.msg  = "请求参数错误";
				String answer = JSONObject.fromObject(result).toString();
				renderJSON(answer);
			}
		}
		String jobStr = Encrypt.decrypt3DES(paramMap.get("JOB"), ConfConst.ENCRYPTION_KEY_DES);

		int jobId = Convert.strToInt(jobStr, 0);
		
		switch (jobId) {
		case JobConstants.JOB_INTEREST_RATE_COUPONS:
			new AddRateTicket().doJob();
			break;
		case JobConstants.JOB_BILL_REMIND:
			new BillRemind().doJob();
			break;
		case JobConstants.JOB_CHECK_BID_IS_FLOW:
			new CheckBidIsFlow().doJob();
			break;
		case JobConstants.JOB_CHECK_DEBT_IS_FLOW:
			new CheckDebtIsFlow().doJob();
			break;
		case JobConstants.JOB_INFORMATION_DISCLOSURE:
			new DisclosureJob().doJob();
			break;
		case JobConstants.JOB_SEND_EMAIL:
			new EmailSend().doJob();
			break;
		case JobConstants.JOB_CLEAN_SEND_EMAIL:
			new EmailSendingClear().doJob();
			break;
		case JobConstants.JOB_HOME_PAGE_DATA_STATISTICS:
			new IndexStatisticsJob().statistics();
			break;
		case JobConstants.JOB_MARK_OVERDUE:
			new MarkOverdue().doJob();
			break;
		case JobConstants.JOB_MASS_SEND_MAIL:
			new MassEmailSend().doJob();
			break;
		case JobConstants.JOB_MASS_SEND_SMS:
			new MassSmsSend().doJob();
			break;
		case JobConstants.JOB_UPDATE_REDPACKET_STATUS:
			new RedPacket().doJob();
			break;
		case JobConstants.JOB_REWARD_GRANT:
			new rewardGrant().doJob();
			break;
		case JobConstants.JOB_SEND_SMS:
			new SmsSend().doJob();
			break;
		case JobConstants.JOB_CLEAR_SEND_SMS:
			new SmsSendingClear().doJob();
			break;
		case JobConstants.JOB_TEAM_COMPUTE:
			new TeamStatistics().doJob();
			break;
		case JobConstants.JOB_TEAM_COMMISSION:
			new TimeCalculate().doJob();
			new TimeCalculate().doJobs();
			break;
		case JobConstants.JOB_PROXY_MONTH_PROFIT:
			new HistoryProfit().doJob();
			break;
		case JobConstants.JOB_PROXY_CURRENT_PROFIT:
			new MonthProfit().doJob();
			break;
		case JobConstants.JOB_EXP_BID_AUTO_REPAYMENT:
			try {
				new ExperienceBidAutoRepayment().doJob();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case JobConstants.JOB_PROXY__UPDATE_USER:
			new ProxyDeduct().doJobUpdateUser();
			break;
		case JobConstants.JOB_PROXY_UPDATE_SALE_ANNUAL:
			new ProxyDeduct().doJobUpdateSaleAnnual();
			break;
		case JobConstants.JOB_PROXY_UPDATE_SALE_PROFIT:
			new ProxyDeduct().doJobUpdateSaleProfit();
			break;
		case JobConstants.JOB_PROXY_UPDATE_INFO:
			new ProxyDeduct().doJobUpdateProxy();
			break;
		case JobConstants.JOB_PROXY_UPDATE_SALE_HISTORY_ANNUAL:
			new HistoryDeduct().doJobUpdateSaleHistoryAnnual();
			break;
		case JobConstants.JOB_PROXY_UPDATE_SALE_HISTORY_PROFIT:
			new HistoryDeduct().doJobUpdateSaleHistoryProfit();
			break;
		case JobConstants.JOB_PROXY_UPDATE_HISTORY_INFO:
			new HistoryDeduct().doJobUpdateProxyHistoty();
			break;
		case JobConstants.JOB_SERVICE_STATISTICS:	
			new ServiceStatisticsJob().doJob();
			break;
		case JobConstants.JOB_PDF_BATCH_DEL:
			try {
				new batchDelectPDF().doJob();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			//金融云日报插入并报送
		case JobConstants.JOB_DAILY_PAPER:	
			new DailyPaper().doJob();
			break;

			//金融云月报报送
		case JobConstants.JOB_BORROWEREPAYMENT:	
			new ServiceMonthJob().doJob();
			break;
		//金融云季报报送
		case JobConstants.JOB_QUARTER_REPORT:	
			new QuarterReport().doJob();
			break;
		//大转盘分享次数	
		case JobConstants.JOB_SHARE_NUM:	
			new ShareNum().doJob();
			break;
		//更新客户VIP状态
		case JobConstants.JOB_UPDATE_USER_VIP:
			new UpdateUserVIP().doJob();
			break;  
		//金融办月报插入数据	4-5
		case JobConstants.JOB_INSERT_MONTH:
			new InsertMonthData().doJob();
			break;
		//金融办月报插入数据	6
		case JobConstants.JOB_INSERT_SIX:
			new InsertMonthSix().doJob();
			break;
		//金融办月报插入数据	7
		case JobConstants.JOB_INSERT_SEVEN:
			new InsertMonthSeven().doJob();
			break;
		//金融办月报插入数据8
		case JobConstants.JOB_INSERT_EIGHT:
			new InsertMonthEight().doJob();
			break;
		//金融办月报插入数据9
		case JobConstants.JOB_INSERT_NINE:
			new InsertMonthNine().doJob();
			break;
		case JobConstants.JOB_BILL_REPANYMENT_TIME:
			try {
				new BillRepanymentTime().doJob();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			result.code = -1;
			result.msg  = "请求参数错误";
			String answer = JSONObject.fromObject(result).toString();
			renderJSON(answer);
			break;
		}
		
		result.code = 1;
		result.msg  = "定时任务执行成功";
		
		String answer = JSONObject.fromObject(result).toString();
		
		renderJSON(answer);
	}
	
	
}
