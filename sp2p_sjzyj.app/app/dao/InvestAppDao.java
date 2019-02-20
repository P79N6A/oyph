package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import services.core.BidService;
import common.utils.Factory;
import common.utils.PageBean;
import models.app.bean.BidInformationBean;
import models.app.bean.BidInvestRecordBean;
import models.app.bean.BidReturnMoneyBean;
import models.app.bean.BidUserInfoBean;
import models.app.bean.InvestBidsBean;
import models.common.entity.t_risk_report;
import models.core.entity.t_bid;
import models.core.entity.t_invest;
import models.core.entity.t_bid.Status;
import daos.base.BaseDao;
import daos.core.BidDao;

/**
 * 理财到数据库的DAO
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年4月5日
 */
public class InvestAppDao extends BaseDao<t_invest>{

	protected static RiskDao riskDao=Factory.getDao(RiskDao.class);
	
	protected static BidDao bidDao=Factory.getDao(BidDao.class);
	/***
	 * 
	 * 理财产品列表接口（OPT=311）
	 * @param currPage
	 * @param pageSize
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public PageBean<InvestBidsBean> pageOfInvestBids(int currPage, int pageSize) throws Exception{
		String sql = " SELECT id AS id, title AS title, apr AS apr, period AS period, period_unit AS periodUnit, amount AS amount, has_invested_amount AS hasInvestedAmount, min_invest_amount AS minInvestAmount, status AS status, loan_schedule AS loanSchedule, pre_release_time AS preRelease, lock_deadline AS lockDeadline FROM t_bid WHERE status IN(:statusList) ORDER BY CASE WHEN status > 1 THEN 2 ELSE 1 END, pre_release_time DESC ";
		String sqlCount = " SELECT COUNT(1) FROM t_bid WHERE  status IN(:statusList)   ";
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("statusList", Status.PROCESS);
		
		return pageOfBeanBySQL(currPage, pageSize, sqlCount, sql, InvestBidsBean.class, conditionArgs);
	}
	
	/***
	 * 
	 * 查询标的详情
	 * @param bidId
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-13
	 */
	public BidInformationBean findInvestBidInformation(long bidId){
		String sql ="SELECT id AS id, title AS title, apr AS apr, period_unit AS periodUnit, period AS period, amount AS amount, has_invested_amount AS hasInvestedAmount, min_invest_amount AS minInvestAmount, loan_schedule AS loanSchedule, buy_type AS buyType, repayment_type AS repaymentType, status AS status, pre_release_time AS preRelease, bid_type AS bidType, invite_code AS inviteCode, lock_deadline AS lockDeadline ,invest_expire_time FROM t_bid WHERE id =:bidId";
		
		Map<String, Object>conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("bidId", bidId);
		
		return findBeanBySQL(sql, BidInformationBean.class, conditionArgs);
	}
	
	/***
	 * 借款人详情接口（OPT=322）
	 *
	 * @param bidId
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public BidUserInfoBean findInvestBidsUserInfo(long bidId) throws Exception{
		
		String sql = "SELECT  u.enterprise_name as enterpriseName,u.reality_name AS realityName,u.id_number AS idNumber,u.credit_level_id AS creditLevelId,u.sex AS sex ,u.education AS education, u.marital AS marital  ,u.annual_income AS annualIncome ,u.net_asset AS netAsset,u.work_experience AS workExperience ,u.house AS house ,u.car AS car ,b.preaudit_suggest AS preauditSuggest,b.description AS description  FROM  t_bid b LEFT JOIN t_user_info u  ON b.user_id=u.user_id  WHERE b.id=:bidId";
		
		t_bid bid=bidDao.findByID(bidId);
		if(bid!=null){
			t_risk_report report = riskDao.findByID(bid.risk_id);
			if(report!=null){
				sql = "SELECT  u.enterprise_name as enterpriseName,u.reality_name AS realityName,u.id_number AS idNumber,u.credit_level_id AS creditLevelId,u.sex AS sex ,u.education AS education, u.marital AS marital  ,u.annual_income AS annualIncome ,u.net_asset AS netAsset,u.work_experience AS workExperience ,u.house AS house ,u.car AS car ,b.preaudit_suggest AS preauditSuggest,r.loan_state AS description  FROM  t_bid b LEFT JOIN t_user_info u  ON b.user_id=u.user_id inner join t_risk_report r on b.risk_id=r.id WHERE b.id=:bidId";
			}
		}
		Map<String, Object>conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("bidId", bidId);
		
		return findBeanBySQL(sql, BidUserInfoBean.class, conditionArgs);
		
	}
	
	/***
	 * 借款人详情接口（OPT=322）
	 * 查询管理员上传资料
	 * @param bidId
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public List<Map<String, Object>> listOfInvestBidItems(long bidId){
		String sql = " SELECT id AS auditId, name AS name FROM t_audit_subject_bid WHERE bid_id =:bidId  ";
		Map<String, Object>conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("bidId", bidId);
		
		return 	findListMapBySQL(sql, conditionArgs);	
	}

	/***
	 * 	
	 * 投标记录接口（OPT=324）
	 * @param currPage
	 * @param pageSize
	 * @param bidId
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public PageBean<BidInvestRecordBean> pageOfInvestBidsRecord(int currPage, int pageSize,long bidId) throws Exception{
		String sql = " SELECT  i.id AS id,u.name AS name ,i.amount AS amount, client AS client,i.time FROM t_invest i LEFT JOIN t_user u ON i.user_id=u.id WHERE i.bid_id=:bidId AND i.debt_id=0   ORDER BY  i.id DESC";
		String sqlCount = " SELECT COUNT(1) FROM t_invest WHERE bid_id=:bidId  ";
	
		Map<String, Object>conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("bidId", bidId);
			
		return pageOfBeanBySQL(currPage, pageSize, sqlCount, sql, BidInvestRecordBean.class, conditionArgs);
	}
	
	/****
	 * 回款计划接口（OPT=323）
	 *
	 * @param bidId
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public List<BidReturnMoneyBean> listOfRepaymentBill(long bidId) {
		String sql = "SELECT tb.id AS id,tb.period AS period, ( tb.repayment_corpus + tb.repayment_interest ) AS principalInterest, tb.repayment_time AS repaymentTime, tb.status AS status, ( SELECT COUNT(id) FROM t_bill WHERE bid_id =:bidId ) AS totalPeriod FROM t_bill tb WHERE tb.bid_id =:bidId ";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		
		return findListBeanBySQL(sql, BidReturnMoneyBean.class, condition);
	}
	
}
