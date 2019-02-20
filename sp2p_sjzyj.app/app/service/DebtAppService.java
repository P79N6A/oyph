package service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import models.app.bean.DebtInvestBean;
import models.app.bean.DebtReturnMoneyBean;
import models.app.bean.DebtTransferBean;
import models.app.bean.DebtTransferDetailBean;
import models.app.bean.InOutDebt;
import models.core.bean.DebtTransfer;
import models.core.bean.UserDebt;
import models.core.entity.t_bill_invest;
import models.core.entity.t_debt_transfer;
import services.core.DebtService;
import common.utils.Factory;
import common.utils.PageBean;
import dao.DebtTransferAppDao;

/**
 * 债权转让service
 *
 * @description
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
public class DebtAppService extends DebtService {

	protected DebtTransferAppDao debtTransferAppDao = null;

	protected DebtAppService() {
		debtTransferAppDao = Factory.getDao(DebtTransferAppDao.class);
	}

	/**
	 * 根据投资id查询该笔投资欲转让
	 *
	 * @param investId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月30日
	 */
	public DebtInvestBean findDebtInvestByInvestId(long investId) {

		return debtTransferAppDao.findDebtInvestByInvestId(investId);
	}

	/**
	 * 根据债权id查询债权详情
	 *
	 * @param debtid
	 *            债权id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月30日
	 */
	public DebtTransferDetailBean findDetailById(long debtid) {
		return debtTransferAppDao.findDetailById(debtid);
	}

	/**
	 * 查询某个投资的债权回款计划
	 *
	 * @param investId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年7月1日
	 */
	public List<DebtReturnMoneyBean> queryRepaymentBill(long investId) {

		return debtTransferAppDao.queryRepaymentBill(investId);
	}

	/**
	 * 分页查询债权列表
	 *
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月30日
	 */
	public PageBean<DebtTransferBean> pageOfDebts(int currPage, int pageSize) {
		StringBuffer countSQL = new StringBuffer(
				" SELECT count(id) FROM t_debt_transfer dt WHERE dt.status in (:status) ");
		StringBuffer querySQL = new StringBuffer(
				" SELECT dt.id AS id, dt.end_time as end_time, dt.time AS time, dt.invest_id AS invest_id, dt.user_id AS user_id, dt.title AS title, dt.debt_amount  debt_amount, dt.debt_principal AS debt_principal, dt.transfer_price AS transfer_price, dt.transfer_period AS period, dt.status AS status FROM t_debt_transfer dt WHERE dt.status in (:status) ORDER BY dt.status, dt.id DESC");

		Map<String, Object> condition = new HashMap<String, Object>();
		List<Integer> status = new LinkedList<Integer>();
		status.add(t_debt_transfer.Status.AUCTING.code);
		status.add(t_debt_transfer.Status.SUCC.code);
		condition.put("status", status);

		return debtTransferDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(),
				DebtTransferBean.class, condition);
	}

	/**
	 * 分页查询某个用户的债权
	 *
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 *            转让人id
	 * @param transactionUserId
	 *            受让人Id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月30日
	 */
	public PageBean<InOutDebt> pageOfAppDebtByUser(int currPage, int pageSize, Long userId, Long transactionUserId) {
		StringBuffer querySQL = new StringBuffer(
				" SELECT dt.id AS id, dt.invest_id AS invest_id, dt.user_id AS user_id,dt.transaction_user_id as transaction_user_id, dt.title AS title, dt.debt_amount AS debt_amount, dt.debt_principal AS debt_principal, dt.transfer_price AS transfer_price, dt.transfer_period AS transfer_period, dt.status AS status, dt.transaction_time AS transaction_time,p.contract_id AS contract_id FROM t_debt_transfer dt LEFT JOIN t_pact p ON p.pid = dt.id ");
		StringBuffer countSQL = new StringBuffer(" SELECT count(1) FROM t_debt_transfer dt LEFT JOIN t_pact p ON p.pid = dt.id ");

		Map<String, Object> condition = null;

		if (userId != null || transactionUserId != null) {
			querySQL.append(" WHERE  ");
			countSQL.append(" WHERE ");

			condition = new HashMap<String, Object>();

			if (userId != null) {
				querySQL.append("  dt.user_id= :userId ");
				countSQL.append("  dt.user_id= :userId ");
				condition.put("userId", userId);
			}

			if (transactionUserId != null) {
				querySQL.append("  dt.transaction_user_id= :transactionUserId ");
				countSQL.append("  dt.transaction_user_id= :transactionUserId ");
				condition.put("transactionUserId", transactionUserId);
			}
		}
		querySQL.append(" ORDER BY dt.id DESC ");

		return debtTransferDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(),
				InOutDebt.class, condition);
	}

	/**
	 * 
	 * @Title: pageOfAppDebtByUserNewEnd
	 *
	 * @description 我的受让(标已结束t_bid.status=5)-OPT=2390
	 *
	 * @param @param
	 *            currPage
	 * @param @param
	 *            pageSize
	 * @param @param
	 *            userId
	 * @param @return
	 * 
	 * @return PageBean<InOutDebt>
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年1月3日
	 */
	public PageBean<InOutDebt> pageOfAppDebtByUserNewEnd(int currPage, int pageSize, Long transactionUserId) {
		
		String querySQL = "SELECT dt.id AS id, dt.invest_id AS invest_id, dt.user_id AS user_id,dt.transaction_user_id as transaction_user_id, dt.title AS title, dt.debt_amount AS debt_amount, dt.debt_principal AS debt_principal, dt.transfer_price AS transfer_price, dt.transfer_period AS transfer_period, dt.status AS status, dt.transaction_time AS transaction_time,p.contract_id AS contract_id FROM t_debt_transfer dt INNER JOIN t_invest ti ON dt.invest_id = ti.id INNER JOIN t_bid tb ON ti.bid_id = tb.id LEFT JOIN t_pact p ON p.pid = dt.id WHERE tb.status=5 AND dt.transaction_user_id =:transactionUserId ORDER BY dt.id DESC";
		
		String countSQL = "SELECT count(1) FROM t_debt_transfer dt INNER JOIN t_invest ti ON dt.invest_id = ti.id INNER JOIN t_bid tb ON ti.bid_id = tb.id LEFT JOIN t_pact p ON p.pid = dt.id WHERE tb.status=5 AND dt.transaction_user_id= :transactionUserId  ";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("transactionUserId", transactionUserId);

		return debtTransferDao.pageOfBeanBySQL(currPage, pageSize, countSQL,querySQL,InOutDebt.class, condition);
		
	}

	/**
	 * 
	 * @Title: pageOfAppDebtByUserNew
	 *
	 * @description 我的受让(标未结束t_bid.status<>5)-OPT=2390
	 *
	 * @param @param
	 *            currPage
	 * @param @param
	 *            pageSize
	 * @param @param
	 *            userId
	 * @param @return
	 * 
	 * @return PageBean<InOutDebt>
	 *
	 * @author yaozijun
	 *
	 * @createDate 2019年1月3日
	 */
	public PageBean<InOutDebt> pageOfAppDebtByUserNewUnfinished(int currPage, int pageSize, Long transactionUserId) {
		
		String querySQL = "SELECT dt.id AS id, dt.invest_id AS invest_id, dt.user_id AS user_id,dt.transaction_user_id as transaction_user_id, dt.title AS title, dt.debt_amount AS debt_amount, dt.debt_principal AS debt_principal, dt.transfer_price AS transfer_price, dt.transfer_period AS transfer_period, dt.status AS status, dt.transaction_time AS transaction_time,p.contract_id AS contract_id FROM t_debt_transfer dt INNER JOIN t_invest ti ON dt.invest_id = ti.id INNER JOIN t_bid tb ON ti.bid_id = tb.id LEFT JOIN t_pact p ON p.pid = dt.id WHERE tb.status<>5 AND dt.transaction_user_id =:transactionUserId ORDER BY dt.id DESC";
        
		String countSQL = "SELECT count(1) FROM t_debt_transfer dt INNER JOIN t_invest ti ON dt.invest_id = ti.id INNER JOIN t_bid tb ON ti.bid_id = tb.id LEFT JOIN t_pact p ON p.pid = dt.id WHERE tb.status<>5 AND dt.transaction_user_id= :transactionUserId ";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("transactionUserId", transactionUserId);
		
		return debtTransferDao.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, InOutDebt.class, condition);

	}
}
