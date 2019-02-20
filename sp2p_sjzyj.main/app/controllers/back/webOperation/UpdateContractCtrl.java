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
import models.common.entity.t_template_pact;
import models.common.entity.t_event_supervisor.Event;
import play.mvc.With;
import service.ext.experiencebid.ExperienceBidSettingService;
import services.common.TemplatePactService;

/**
 * 后台 -借款合同管理
 * UpdateContractCtrl
 * @author liuyang
 * @createDate 2017年11月14日
 */
@With(SubmitRepeat.class)
public class UpdateContractCtrl extends BackBaseController{
	
	protected static TemplatePactService templatePactService = Factory.getService(TemplatePactService.class);
	
	
	/**
	 * 进入借款合同界面
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年11月14日
	 */
	public static void showContractCtrlPre(){
		
		t_template_pact pacts = templatePactService.findByColumn("type=?", 0);
		
		render(pacts);
	
	}
	
	/**
	 * 修改借款合同
	 * 
	 * @param single_quota 单笔充值限额
	 * @param day_quota 每日充值限额
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年11月14日
	 */
	public static void editContract(t_template_pact pacts){
		ResultInfo result = new ResultInfo();
		
		t_template_pact pact = templatePactService.findByColumn("type=?", 0);
		
		pact.content = pacts.content;
		pact.name = pacts.name;
		pact.save();
		
		long supervisor_id = getCurrentSupervisorId();
		
		supervisorService.addSupervisorEvent(supervisor_id, Event.CONTRACTS, null);
		
		ColumnMngCtrl.showColumnsPre();
		
		renderJSON(result);
	}
}
