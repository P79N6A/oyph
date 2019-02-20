package controllers.back.webOperation;

import java.util.List;






import models.common.entity.t_company_branch;
import models.common.entity.t_risk_reception;
import common.utils.Factory;
import play.mvc.With;
import services.common.BranchService;
import services.common.RiskReceptionService;
import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;
import daos.common.RiskReceptionDao;

/**
 * 后台 -管理风控专员
 * RiskMngCtrl
 * @author lihuijun
 * @createDate 2017年5月4日
 */
@With(SubmitRepeat.class)
public class RiskMngCtrl extends BackBaseController{
	
	protected static BranchService branchService=Factory.getService(BranchService.class);
	
	protected static RiskReceptionService riskReceptionService=Factory.getService(RiskReceptionService.class);
	
	protected static RiskReceptionDao riskReceptionDao=Factory.getDao(RiskReceptionDao.class);
	
	/**显示风控专员列表*/
	public static void showRiskReceptionPre(){
		List<t_company_branch> branchs=branchService.findAll();
		List<t_risk_reception> trList=riskReceptionService.findAll();
		render(trList,branchs);
	}
	
	/**增加风控专员到数据库*/
	public static void addRiskMember(String riskName,String riskMobile,Long riskBranch,int riskType){
		t_risk_reception riskReception=new t_risk_reception();
		riskReception.reception_name=riskName;
		riskReception.phone=riskMobile;
		riskReception.branch_id=riskBranch;
		riskReception.type=riskType;
		riskReception.save();
		flash.success("添加成功！");
		showRiskReceptionPre();	
	}
	
	/**去修改专员信息*/
	public static void toEditRiskReceptionPre(Long riskReceptionId){
		List<t_company_branch> branchs=branchService.findAll();
		t_risk_reception trNotice=riskReceptionService.findByID(riskReceptionId);
		render(trNotice,branchs);		
	}
	
	/**修改专员信息*/
	public static void editRiskMember(Long riskReceptionId,String riskName,String riskMobile,Long riskBranch,int riskType){
		if(riskReceptionId!=null ){
			t_risk_reception riskReception=riskReceptionService.findByID(riskReceptionId);
			riskReception.reception_name=riskName;
			riskReception.phone=riskMobile;
			riskReception.branch_id=riskBranch;
			riskReception.type=riskType;
			riskReception.save();
			flash.success("修改成功！");
		}
		showRiskReceptionPre();	
	}
	
	
	
	/**删除专员*/
	public static void deleteRiskReceptionPre(Long riskReceptionId){
		riskReceptionDao.delete(riskReceptionId);
		flash.success("删除成功！");
		showRiskReceptionPre();
	}
	
}
