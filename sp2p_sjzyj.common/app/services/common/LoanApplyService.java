package services.common;

import java.util.List;

import common.utils.Factory;
import daos.common.LoanApplyDao;
import models.common.entity.t_loan_apply;
import services.base.BaseService;

public class LoanApplyService extends BaseService<t_loan_apply> {

	protected LoanApplyDao loanApplyDao = Factory.getDao(LoanApplyDao.class);
	
	protected LoanApplyService() {
		this.basedao = loanApplyDao;
	}
	
	/**
	 * 查询申请信息
	 * @return
	 */
	public List<t_loan_apply> queryByTime() {
		
		return loanApplyDao.queryByTime();
	}
}
