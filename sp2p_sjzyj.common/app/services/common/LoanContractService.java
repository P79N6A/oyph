package services.common;

import java.util.List;

import common.utils.Factory;
import daos.common.LoanContractDao;
import models.common.entity.t_loan_contract;
import services.base.BaseService;

public class LoanContractService extends BaseService<t_loan_contract> {

	protected LoanContractDao loanContractDao = Factory.getDao(LoanContractDao.class);
	
	protected LoanContractService() {
		this.basedao = loanContractDao;
	}
	
	/**
	 * 查询合同信息
	 * @return
	 */
	public List<t_loan_contract> queryByTime() {
		
		return loanContractDao.queryByTime();
	}
}
