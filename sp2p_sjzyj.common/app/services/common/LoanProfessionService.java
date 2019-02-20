package services.common;

import java.util.List;

import common.utils.Factory;
import daos.common.LoanProfessionDao;
import models.common.entity.t_loan_profession;
import services.base.BaseService;

public class LoanProfessionService extends BaseService<t_loan_profession> {

	LoanProfessionDao loanProfessionDao = Factory.getDao(LoanProfessionDao.class);
	
	protected LoanProfessionService() {
		this.basedao = loanProfessionDao;
	}
	
	/**
	 * 根据标的id查询实体
	 * @param bidId
	 * @return
	 */
	public t_loan_profession queryLoanByBidId(long bidId) {
		
		return loanProfessionDao.queryLoanByBidId(bidId);
	}
	
	/**
	 * 查询业务信息
	 * @return
	 */
	public List<t_loan_profession> queryByTime() {
		
		return loanProfessionDao.queryByTime();
	}
}
