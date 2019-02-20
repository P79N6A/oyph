package controllers.back.webOperation;

import java.util.List;
import java.util.Map;

import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;
import models.common.entity.t_company_branch;
import models.common.entity.t_risk_reception;
import models.common.entity.t_event_supervisor.Event;
import play.mvc.With;
import service.ext.experiencebid.ExperienceBidSettingService;
import services.common.BankQuotaService;

/**
 * 后台 -银行限额管理
 * BankQuotaCtrl
 * @author LiuPengwei
 * @createDate 2017年10月31日
 */
@With(SubmitRepeat.class)
public class BankQuotaCtrl extends BackBaseController{
	
	
	protected static BankQuotaService bankQuotaService = Factory.getService(BankQuotaService.class);
	
	
	/**
	 * 进入银行充值限额界面
	 *
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年11月4日
	 */
	public static void showBankQuotaCtrlPre(){
		
		List<Map<String, Object>> map = bankQuotaService.queryBankQuotaInfo();
		
		render(map);
	
	}
	
	/**
	 * 修改某个银行充值限额
	 * 
	 * @param single_quota 单笔充值限额
	 * @param day_quota 每日充值限额
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年11月4日
	 */
	public static void saveBankQuotaCtrl(int single_quota, int day_quota, long bankQuotaId){
		ResultInfo result = new ResultInfo();
		
		
		result = bankQuotaService.saveBankQuota(single_quota, day_quota, bankQuotaId);
		
		long supervisor_id = getCurrentSupervisorId();
		
		supervisorService.addSupervisorEvent(supervisor_id, Event.BANKQUOTAS, null);
		
		renderJSON(result);
	}
}
