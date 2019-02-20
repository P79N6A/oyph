package dao.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.app.bean.MyInvestRecordBean;
import models.core.entity.t_bill_invest;
import models.main.bean.LoanContract;

public class UserLoanContractDao extends BaseDao<LoanContract> {
	
	public List<LoanContract> findListLoanContract(Long bidId) {
		String sql = "SELECT P.id,P.pid AS bidId,P.time,p.user_id,p.contract_id,u.`name`,(SELECT SUM(amount) FROM t_invest WHERE bid_id = P.pid AND debt_id = 0 AND user_id = p.user_id GROUP BY user_id)  AS amount,(SELECT t_bid.`status` FROM t_bid WHERE t_bid.id = p.pid) AS `status`,(SELECT t_bid.contract_id FROM t_bid WHERE t_bid.id = pid) AS contract FROM t_pact AS P JOIN t_user AS U ON P.user_id = U.id WHERE P.type = 0 AND P.pid = :bidId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		return findListBySQL(sql, condition);
	}

	public List<LoanContract> findLoanContract(Long bidId) {
		String sql = "SELECT t_pact.id,t_pact.pid AS bidId,t_pact.time,t_pact.user_id,t_pact.contract_id,(SELECT t_bid.amount FROM t_bid WHERE t_bid.id = t_pact.pid) AS amount,(SELECT t_bid.`status` FROM t_bid WHERE t_bid.id = t_pact.pid) AS `status`,'全体出借人' AS name,(SELECT t_bid.contract_id FROM t_bid WHERE t_bid.id = pid) AS contract FROM t_pact WHERE type = 0 AND pid = :bidId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		return findListBySQL(sql, condition);
	}

	public PageBean<LoanContract> PageFindLoanContract(int currPage, int pageSize, long bidId) {
		String sql = "SELECT t_pact.id,t_pact.pid AS bidId,t_pact.time,t_pact.user_id,t_pact.contract_id,(SELECT t_bid.amount FROM t_bid WHERE t_bid.id = t_pact.pid) AS amount,(SELECT t_bid.`status` FROM t_bid WHERE t_bid.id = t_pact.pid) AS `status`,'全体出借人' AS name,(SELECT t_bid.contract_id FROM t_bid WHERE t_bid.id = pid) AS contract FROM t_pact WHERE type = 0 AND pid = :bidId";
		String sqlCount="SELECT id FROM t_pact WHERE type = 0 AND pid = :bidId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		return pageOfBeanBySQL(currPage, pageSize, sqlCount, sql, LoanContract.class, condition);
	}


	public PageBean<LoanContract> PageFindListLoanContract(int currPage, int pageSize, long bidId) {
		String sql = "SELECT P.id,P.pid AS bidId,P.time,p.user_id,p.contract_id,u.`name`,(SELECT SUM(amount) FROM t_invest WHERE bid_id = P.pid AND debt_id = 0 AND user_id = p.user_id GROUP BY user_id)  AS amount,(SELECT t_bid.`status` FROM t_bid WHERE t_bid.id = p.pid) AS `status`,(SELECT t_bid.contract_id FROM t_bid WHERE t_bid.id = pid) AS contract FROM t_pact AS P JOIN t_user AS U ON P.user_id = U.id WHERE P.type = 0 AND P.pid = :bidId";
		String sqlCount="SELECT COUNT(P.id) FROM t_pact AS P JOIN t_user AS U ON P.user_id = U.id WHERE P.type = 0 AND P.pid = :bidId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		return pageOfBeanBySQL(currPage, pageSize, sqlCount, sql, LoanContract.class, condition);
	}
}
