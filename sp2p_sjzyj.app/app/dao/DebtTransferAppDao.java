package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import models.app.bean.DebtInvestBean;
import models.app.bean.DebtReturnMoneyBean;
import models.app.bean.DebtTransferDetailBean;
import daos.core.DebtTransferDao;

/**
 * 债权转让Appdao
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
public class DebtTransferAppDao extends DebtTransferDao {

	protected DebtTransferAppDao() {
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
		
		String querySQL = "SELECT i.id as id,i.bid_id AS bid_id,i.user_id AS user_id,b.title AS title, (SELECT IFNULL(SUM(bi.receive_interest+bi.receive_corpus),0) FROM t_bill_invest bi WHERE bi.invest_id=i.id AND bi.status = 0) AS debt_amount, (SELECT IFNULL(SUM(bi.receive_corpus),0) FROM t_bill_invest bi WHERE bi.invest_id=i.id AND bi.status = 0) AS debt_principal, (SELECT IFNULL(SUM(bi.receive_interest),0) FROM t_bill_invest bi WHERE bi.invest_id=i.id AND bi.status = 0) as debt_interest,(SELECT IFNULL(COUNT(id),0) FROM t_bill_invest bi WHERE bi.invest_id=i.id AND bi.status = 0) AS period FROM t_invest i INNER JOIN t_bid b ON i.bid_id = b.id where i.id=:investId";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("investId", investId);

		return findBeanBySQL(querySQL, DebtInvestBean.class, condition);
	}

	/**
	 * 根据债权id查询债权详情
	 *
	 * @param debtid 债权id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年6月30日
	 */
	public DebtTransferDetailBean findDetailById(long debtid) {
		/*
		 SELECT 
				dt.id AS id,
				dt.time AS time,
				dt.end_time AS end_time,
				dt.invest_id AS invest_id,
				b.id AS bid_id,
				dt.user_id AS user_id,
				dt.title AS title,
				dt.debt_amount AS debt_amount,
				dt.debt_principal AS debt_principal,
				dt.transfer_price AS transfer_price,
				dt.transfer_period AS period,
				dt.status AS status,
				(SELECT bi.repayment_time FROM t_bill bi WHERE bi.bid_id=i.bid_id AND bi.status IN (0,1) ORDER BY bi.repayment_time LIMIT 1) AS receive_time
			FROM 
				t_debt_transfer dt
				INNER JOIN t_invest i ON dt.invest_id = i.id
				INNER JOIN t_bid b ON b.id = i.bid_id
			WHERE dt.id= :debtId
		 */
		String querySQL = " SELECT  dt.id AS id, dt.time AS time, dt.end_time AS end_time, dt.invest_id AS invest_id, b.id AS bid_id, dt.user_id AS user_id, dt.title AS title, dt.debt_amount AS debt_amount, dt.debt_principal AS debt_principal, dt.transfer_price AS transfer_price, dt.transfer_period AS period, dt.status AS status, (SELECT bi.repayment_time FROM t_bill bi WHERE bi.bid_id=i.bid_id AND bi.status IN (0,1) ORDER BY bi.repayment_time LIMIT 1) AS receive_time FROM  t_debt_transfer dt INNER JOIN t_invest i ON dt.invest_id = i.id INNER JOIN t_bid b ON b.id = i.bid_id WHERE dt.id= :debtId ";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("debtId", debtid);

		return findBeanBySQL(querySQL, DebtTransferDetailBean.class, condition);
	}
	
	/**
	 * 查询某个投资的债权回款计划
	 *
	 * @param investId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年7月5日
	 */
	public List<DebtReturnMoneyBean> queryRepaymentBill(long investId) {
		/*
		 SELECT
				tbi.id AS id,
				(tbi.receive_corpus + tbi.receive_interest) AS principalInterest,
				tbi.period AS period,
				tbi.receive_time AS repaymentTime,
				tbi.status AS status,
				(SELECT COUNT(id)FROM t_bill WHERE bid_id = tbi.bid_id) AS totalPeriod
			FROM
				t_bill_invest tbi,
				t_bid tb
			WHERE
				tbi.bid_id = tb.id
			AND invest_id = :investId
		 */
		String querySQL = " SELECT tbi.id AS id, (tbi.receive_corpus + tbi.receive_interest) AS principalInterest, tbi.period AS period, tbi.receive_time AS repaymentTime, tbi.status AS status, (SELECT COUNT(id)FROM t_bill WHERE bid_id = tbi.bid_id) AS totalPeriod FROM t_bill_invest tbi, t_bid tb WHERE tbi.bid_id = tb.id AND invest_id = :investId ";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("investId", investId);

		return findListBeanBySQL(querySQL, DebtReturnMoneyBean.class, condition);
	}

}
