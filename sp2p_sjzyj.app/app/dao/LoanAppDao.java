package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.app.bean.BillInfo;
import models.app.bean.BillListBean;
import models.core.entity.t_bid;
import daos.base.BaseDao;

/**
 * 借款到数据库的DAO
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年4月5日
 */
public class LoanAppDao extends BaseDao<t_bid>{
	public LoanAppDao(){}
	
	/***
	 * 
	 * 借款账单
	 * @param userId 用户id
	 * @param bidId  标id
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-11
	 */
	public List<BillListBean> listOfLoanBill(long userId, long bidId) {
		
		String querySQL = "SELECT id AS billId,bid_id AS bidId, time, repayment_corpus AS repaymentCorpus, repayment_interest AS repaymentInterest,  repayment_time AS repaymentTime FROM t_bill WHERE user_id=:userId AND bid_id=:bidId";
		Map<String, Object>conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("userId", userId);
		conditionArgs.put("bidId", bidId);
		
		return super.findListBeanBySQL(querySQL, BillListBean.class, conditionArgs);
	}
	
	/***
	 * 
	 * 借款账单详情
	 * @param userId 用户id
	 * @param billId 借款账单id
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-5-11
	 */
	public BillInfo findLoanBill(long userId, long billId) {
		
		String querySQL = "SELECT id AS billId, time, repayment_corpus AS repaymentCorpus, repayment_interest AS repaymentInterest, repayment_time AS repaymentTime, real_repayment_time AS realRepaymentTime, period, status FROM t_bill WHERE user_id=:userId AND id=:billId";
		Map<String, Object>conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("userId", userId);
		conditionArgs.put("billId", billId);
		
		return super.findBeanBySQL(querySQL, BillInfo.class, conditionArgs);
	}

}
