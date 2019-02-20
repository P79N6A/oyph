package controllers.back.risk;

import java.util.Date;

import org.codehaus.groovy.control.StaticImportVisitor;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;
import models.common.entity.t_loan_apply_info;
import services.common.LoanApplyInfoService;
/***
 * 
 *
 * @ClassName: LoanApplyInfoCtrl
 *
 * @description 借款申请后台
 *
 * @author LiuHangjing
 * @createDate 2018年12月18日-下午5:45:52
 */
public class LoanApplyInfoCtrl extends BackBaseController{
	
	protected static LoanApplyInfoService loanApplyInfoService = Factory.getService(LoanApplyInfoService.class);
	/***
	 * 
	 * @Title: loanApplyInfoPre
	 * 
	 * @description 后台-风控-贷款申请分页显示
	 * @param  currPage
	 * @param pageSize
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月18日-下午5:53:59
	 */
	public static void showLoanApplyInfoPre(int showType,int currPage,int pageSize){
		
		String appli_phone = params.get("appli_phone");
		if (showType <0 || showType >2) {
			showType = 0;
		}
		PageBean<t_loan_apply_info> page = loanApplyInfoService.findPageOfLoanApply(showType,appli_phone,currPage, pageSize);
		
		render(page,showType);
	}
	
	/**
	 * 
	 * @Title: updateAppliStatus
	 * 
	 * @description 后台-风控-借款申请-受理状态修改
	 * @param @param id
	 * @return ResultInfo    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月19日-下午2:47:07
	 */
	public static ResultInfo updateAppliStatus(long id){
		ResultInfo result = new ResultInfo();
		Integer num = loanApplyInfoService.updateAppliStatus(id);
		if (num>=1) {
			result.code = 1;
			result.msg = "更改状态为已受理";
			flash.success("更改状态为已受理");
		}else {
			result.code = -1;
			result.msg = "更改状态失败";
			flash.error("受理失败");
		}
		
		return result;
		
	}
	
}
