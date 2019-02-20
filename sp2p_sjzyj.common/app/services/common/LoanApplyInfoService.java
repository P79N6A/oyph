package services.common;

import common.utils.Factory;
import common.utils.PageBean;
import daos.common.CreditDao;
import daos.common.LoanApplyInfoDao;
import models.common.entity.t_loan_apply_info;
import services.base.BaseService;
/**
 * 
 *
 * @ClassName: LoanApplyInfoService
 *
 * @description 前台借款信息Service
 *
 * @author LiuHangjing
 * @createDate 2018年12月18日-下午5:05:51
 */
public class LoanApplyInfoService extends BaseService<t_loan_apply_info>{
	
	protected static LoanApplyInfoDao loanApplyInfoDao = Factory.getDao(LoanApplyInfoDao.class);
	
	protected LoanApplyInfoService() {
		
		super.basedao = this.loanApplyInfoDao;
	}
	/**
	 * 
	 * @Title: findPageOfLoanApply
	 * 
	 * @description 后台-风控-借款申请-分页列表显示
	 * @param  currPage
	 * @param  pageSize

	 * @return PageBean<t_loan_apply_info>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月18日-下午5:50:33
	 */
	public PageBean<t_loan_apply_info> findPageOfLoanApply(int showType,String appli_phone,int currPage,int pageSize){
		
		return loanApplyInfoDao.findPageOfLoanApply(showType,appli_phone,currPage, pageSize);
	}
	
	/**
	 * 
	 * @Title: updateAppliStatus
	 * 
	 * @description 后台-风控-借款申请-修改受理状态
	 * @param @param id
	 * @return Integer    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月19日-下午2:46:26
	 */
	public Integer updateAppliStatus(long id) {
		
		return loanApplyInfoDao.updateAppliStatus(id);
		
		
	}
	
}
