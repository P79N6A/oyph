package daos.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.core.bean.BackFinanceBid;
import models.core.bean.BackRiskBid;
import models.core.bean.FrontMyLoanBid;
import models.core.bean.PactBid;
import models.core.entity.t_bid;
import models.core.entity.t_bid.Status;
import models.core.entity.t_bill;

import org.apache.commons.lang.StringUtils;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;

/**
 * 标的表的DAO
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月7日
 */
public class BidDao extends BaseDao<t_bid>{
	
	/**
	 * 初审更新标的类型
	 * 保存邀请码
	 * @param bidId
	 * @param bid_type
	 * @param invite_code
	 * @return
	 * 
	 * @author niu
	 * @createDate 2016年12月26日
	 */
	public int preAuditBidCode(long bidId, int bid_type, String invite_code) {
		
		String updSql = "UPDATE t_bid SET bid_type=:bid_type, invite_code=:invite_code WHERE id=:id";
		Map<String, Object>params = new HashMap<String, Object>();
		params.put("bid_type", bid_type);
		params.put("invite_code", invite_code);
		params.put("id", bidId);	
		
		return super.updateBySQL(updSql, params);
	}
	
	/**
	 * 初审更新标的状态
	 *
	 * @param bidId 标的id
	 * @param status 新状态
	 * @param pre_release_time 预发布时间
	 * @param invest_expire_time 满标时间
	 * @param preauditor_supervisor_id 初审审核人
	 * @param preaudit_time 初审时间
	 * @param preaudit_suggest 审核意见
	 * @param nowstatus 原状态
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月22日
	 */
	public int preAuditBidStatus(long bidId, int status, int trust_status, Date pre_release_time, Date invest_expire_time,  
			long preauditor_supervisor_id, Date preaudit_time, String preaudit_suggest, List<Integer>nowstatus) {
		
		String updSql = "UPDATE t_bid SET status=:status, trust_status=:trust_status, pre_release_time=:pre_release_time, invest_expire_time=:invest_expire_time, preauditor_supervisor_id=:preauditor_supervisor_id, preaudit_time=:preaudit_time, preaudit_suggest=:preaudit_suggest WHERE id=:id AND status in(:nowstatus)";
		Map<String, Object>params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("trust_status", trust_status);
		params.put("pre_release_time", pre_release_time);
		params.put("invest_expire_time", invest_expire_time);
		params.put("preauditor_supervisor_id", preauditor_supervisor_id);
		params.put("preaudit_time", preaudit_time);
		params.put("preaudit_suggest", preaudit_suggest);
		params.put("id", bidId);
		params.put("nowstatus", nowstatus);
		
		return super.updateBySQL(updSql, params);
	}

	/**
	 * 复审
	 *
	 * @param bidId 标的id
	 * @param status 新状态
	 * @param auditor_supervisor_id 复审审核人
	 * @param audit_time 复审时间
	 * @param audit_suggest 复审意见
	 * @param nowstatus 原状态
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public int auditBidStatus(long bidId, int status, long auditor_supervisor_id, Date audit_time, String audit_suggest, List<Integer> nowstatus) {
		
		String updSql = "UPDATE t_bid SET status=:status, auditor_supervisor_id=:auditor_supervisor_id, audit_time=:audit_time, audit_suggest=:audit_suggest WHERE id=:id AND status in(:nowstatus)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("auditor_supervisor_id", auditor_supervisor_id);
		params.put("audit_time", audit_time);
		params.put("audit_suggest", audit_suggest);
		params.put("id", bidId);
		params.put("nowstatus", nowstatus);
		
		return super.updateBySQL(updSql, params);
	}

	/**
	 * 更新标的标题
	 *
	 * @param bidId
	 * @param newTitle
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public int updateBidTitle(long bidId, String newTitle) {
		
		String sql = "UPDATE t_bid SET title=:title WHERE id=:bid_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", newTitle);
		params.put("bid_id", bidId);
		
		return super.updateBySQL(sql, params);
	}
	
	/**
	 * 更新标的描述
	 *
	 * @param bidId
	 * @param newDescription
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public int updateBidDescription(long bidId, String newDescription){
		
		String sql = "UPDATE t_bid SET description=:description WHERE id=:bid_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("description", newDescription);
		params.put("bid_id", bidId);
		
		return super.updateBySQL(sql, params);
	}
	
	/**
	 * 放款
	 *
	 * @param release_supervisor_id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public int updateBidRelease(long release_supervisor_id, long bidId){
		
		String sql = "UPDATE t_bid SET status=:wait_releasing, trust_status=:trust_status, release_supervisor_id=:release_supervisor_id, release_time=:release_time WHERE id=:bid_id and status=:repaying";
		Map<String, Object>params = new HashMap<String, Object>();
		params.put("wait_releasing", t_bid.Status.REPAYING.code);
		params.put("trust_status", 3);
		params.put("release_supervisor_id", release_supervisor_id);
		params.put("release_time", new Date());
		params.put("bid_id", bidId);
		params.put("repaying", t_bid.Status.WAIT_RELEASING.code);
		
		return super.updateBySQL(sql, params);
	}
	
	/**
	 *  更新投标进度、已投金额、加入人次
	 *
	 * @param bidId 标的id
	 * @param schedule 投标进度
	 * @param investAmt 投资金额
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public synchronized int updateBidschedule(long bidId, double investAmt, double schedule) {
		String sql="UPDATE t_bid SET loan_schedule = :schedule , has_invested_amount = has_invested_amount + :investAmt, invest_count = invest_count + 1 WHERE id= :bidId AND amount >= has_invested_amount + :investAmt";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("schedule", schedule);
		condition.put("investAmt", investAmt);
		condition.put("bidId", bidId);					
		return  this.updateBySQL(sql,condition);
	}

	/**
	 * 满标，更新标的信息
	 *
	 * @param bidId 标的ID
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public int updateBidEnd(long bidId) {
		String sql="UPDATE t_bid SET last_repay_time = :endTime, status = :status WHERE id = :bidId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("endTime", new Date());
		condition.put("status", t_bid.Status.REPAYED.code);
		condition.put("bidId", bidId);
		return this.updateBySQL(sql,condition);
	}
	
	/**
	 * 满标，更新标的信息
	 *
	 * @param bidId 标的ID
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public int updateBidExpire(long bidId) {
		String sql="UPDATE t_bid SET real_invest_expire_time = :expireTime, status = :status WHERE id = :bidId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("expireTime", new Date());
		condition.put("status", t_bid.Status.AUDITING.code);
		condition.put("bidId", bidId);
		return this.updateBySQL(sql,condition);
	}

	/**
	 * 更新托管标的状态
	 * 
	 * @author niu
	 * @create 2017.09.28
	 */
	public int updateTrustBidStatus(long bidId, int startStatus, int endStatus) {
		String sql = "UPDATE t_bid SET trust_status = :endStatus WHERE id = :bidId AND trust_status = :startStatus";
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("bidId", bidId);
		condition.put("startStatus", startStatus);
		condition.put("endStatus", endStatus);
		
		return this.updateBySQL(sql,condition);
	}
	

	/**
	 * 更新标的状态(借款中->流标)
	 *
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月30日
	 */
	public int updateBidStatusFlow(long bidId) {
		
		String sql = "UPDATE t_bid SET status=:status1, trust_status=:trust_status WHERE id=:bidId AND status=:status2";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bidId", bidId);
		params.put("status1", t_bid.Status.FLOW.code);
		params.put("trust_status", 04);
		params.put("status2", t_bid.Status.FUNDRAISING.code);
		
		return super.updateBySQL(sql, params);
	}

	/**
	 * 更新借款服务费
	 *
	 * @param bidId
	 * @param loanServiceFee
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月12日
	 */
	public int updateLoanServiceFee(Long bidId, double loanServiceFee) {
		String sql = "UPDATE t_bid SET loan_fee = :loanFee WHERE id = :bidId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loanFee", loanServiceFee);
		params.put("bidId", bidId);
		
		return updateBySQL(sql, params);
	}

	/**
	 * 查询放款项目数量
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月8日
	 *
	 */
	public int findReleasedBidsNum() {
		String sql = "SELECT COUNT(1) FROM t_bid tb WHERE tb.status IN(:status)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_bid.Status.LOAN);
		
		return findSingleIntBySQL(sql, 0, params);
	}

	/**
	 * 查询放款项目总额
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月8日
	 *
	 */
	public double findTotalBorrowAmount() {
		
		String sql = "SELECT SUM(bid.amount) FROM t_bid bid WHERE bid.status IN (:status)";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_bid.Status.LOAN);
		
		return findSingleDoubleBySQL(sql, 0.00, params);
	}
	
	/**
	 * 查找合同相关bid信息
	 *
	 * @param bidId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月22日
	 */
	public PactBid findPactBidById(long bidId) {
		String sql = "SELECT b.id as id,b.title as title,b.description as description,u.name as name ,ui.reality_name as reality_name,ui.id_number as id_number,b.amount as amount,b.apr as apr,b.period_unit as period_unit,b.period as period,b.release_time as release_time,b.repayment_type as repayment_type, b.service_fee_rule as service_fee_rule, ui.enterprise_name as enterprise_name, b.ssq_guarantee_user as guarantee_user FROM t_bid b INNER JOIN t_user u ON b.user_id = u.id INNER JOIN t_user_info ui on u.id = ui.user_id WHERE b.id = :bidId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bidId", bidId);
		
		PactBid pactBid = this.findBeanBySQL(sql, PactBid.class, params);
		
		return pactBid;
	}

	/**
	 * 统计标的借款总额
	 * 
	 * @param showType 筛选类型  0-所有;1-初审中;2-借款中;3-满标审核;4-还款中;5-已经结束;6-失败
	 * @param loanName 借款人昵称
	 * @param numNo 编号
	 * @param projectName 项目名称
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月9日
	 */
	public double findTotalBidAmount(List<Integer> statusList, String loanName, String numNo, String projectName) {
		StringBuffer sql = new StringBuffer("SELECT SUM(tb.amount) FROM t_bid tb LEFT JOIN t_user tu ON tb.user_id = tu.id WHERE 1=1 ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		if (statusList != null && statusList.size() != 0) {
			sql.append(" AND tb.status IN(:status) ");
			condition.put("status", statusList);
		}
		
		/* 按借款人昵称 模糊查询 */
		if(StringUtils.isNotBlank(loanName)){
			sql.append(" AND tu.name LIKE :loanName ");
			condition.put("loanName", "%"+loanName+"%");
		}
		
		/* 按编号 模糊查询 */
		if(StringUtils.isNotBlank(numNo)){
			sql.append(" AND tb.id LIKE :id ");
			condition.put("id", "%"+numNo+"%");
		}
		
		/* 按项目名称 模糊查询 */
		if(StringUtils.isNotBlank(projectName)){
			sql.append(" AND tb.title LIKE :title ");
			condition.put("title", "%"+projectName+"%");
		}
		
		return findSingleDoubleBySQL(sql.toString(), 0.00, condition);
	}

	
	/**
	 * 通过标的Id返回标的名称
	 *
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月2日
	 */
	public String findBidNameById(long bidId){
		
		String sql = "SELECT title FROM t_bid WHERE id=:id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", bidId);
		
		return super.findSingleStringBySQL(sql, "", params);
	}
	
	/**
	 * 通过标的编号查找标的ID
	 *
	 * @param merBidNo
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年3月3日
	 */
	public long findIdByMerBidNo (String merBidNo, long defaultVal) {
		String sql = "SELECT id FROM t_bid WHERE mer_bid_no = :merBidNo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("merBidNo", merBidNo);
		
		return super.findSingleLongBySQL(sql, defaultVal, params);
	}
	
	/**
	 * 前台-理财
	 *
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public PageBean<t_bid> pageOfBidInvest(int currPage, int pageSize) {
		
		String querySQL = "SELECT * FROM t_bid WHERE status IN (:statusList) ORDER BY CASE WHEN status > 1 THEN 2 ELSE 1 END, pre_release_time DESC";
		String countSQL = "SELECT COUNT(id) FROM t_bid WHERE status IN (:statusList)";
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("statusList", Status.PROCESS);
		
		return super.pageOfBySQL(currPage, pageSize, countSQL, querySQL, conditionArgs);
	}


	/**
	 * 我的借款查询
	 *
	 * @param userId
	 * @param pageSize
	 * @param currPage
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月25日
	 */
	public PageBean<t_bid> pageOfMyLoan(long userId, int pageSize, int currPage) {
		
		String countSQL = "SELECT COUNT(id) FROM t_bid WHERE user_id=:userId";
		String querySQL = "SELECT * FROM t_bid WHERE user_id=:userId";
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("userId", userId);
		
		return super.pageOfBySQL(currPage, pageSize, countSQL, querySQL, conditionArgs);
	}
	/**
	 * 后台-风控页面-标的列表
	 *
	 * @param showType 筛选类型  0-所有;1-初审中;2-借款中;3-满标审核;4-还款中;5-已经结束;6-失败
	 * @param currPage
	 * @param pageSize
	 * @param exports 1:导出  default：不导出
	 * @param numNo 编号
	 * @param projectName 项目名称
	 * @param userName 借款人
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月4日
	 */
	public PageBean<BackRiskBid> pageOfBidRisk(int showType, int currPage, int pageSize, int exports,int orderType,int orderValue,String userName,String numNo,String projectName) {
		
		/**
		SELECT
			bid.id AS id,
			bid.title AS title,
			user.name AS name,
			bid.amount AS amount,
			bid.apr AS apr,
			bid.period_unit AS period_unit,
			bid.period AS period,
			bid.loan_schedule AS loan_schedule,
			bid.pre_release_time AS pre_release_time,
			bid.status AS status
		FROM
			t_bid bid
		LEFT JOIN t_user user ON bid.user_id = user .id
		WHERE
			1 = 1
		 */
		/*StringBuffer querySQL = new StringBuffer("SELECT bid.id AS id, bid.title AS title, user.name AS name, user.reality_name AS reality_name, bid.amount AS amount, bid.apr AS apr, bid.period_unit AS period_unit, bid.period AS period, bid.loan_schedule AS loan_schedule, bid.pre_release_time AS pre_release_time, bid.status AS status FROM t_bid bid LEFT JOIN t_user user ON bid.user_id=user.id WHERE 1=1 ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(bid.id) FROM t_bid bid LEFT JOIN t_user user ON bid.user_id=user.id WHERE 1=1 ");
		*/
		StringBuffer querySQL = new StringBuffer("SELECT bid.id AS id, userinfo.reality_name AS reality_name, bid.title AS title, user.name AS name, bid.amount AS amount, bid.apr AS apr, bid.period_unit AS period_unit, bid.period AS period, bid.loan_schedule AS loan_schedule, bid.pre_release_time AS pre_release_time, bid.status AS status FROM t_bid bid LEFT JOIN t_user user ON bid.user_id=user.id LEFT JOIN t_user_info userinfo ON bid.user_id=userinfo.user_id WHERE 1=1 ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(bid.id) FROM t_bid bid LEFT JOIN t_user user ON bid.user_id=user.id LEFT JOIN t_user_info userinfo ON bid.user_id=userinfo.user_id WHERE 1=1 ");
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		switch (showType) {
			case 0://所有
				break;
			case 5:{//初审中
				querySQL.append(" AND bid.status=:status ");
				countSQL.append(" AND bid.status=:status ");
				conditionArgs.put("status", Status.PREAUDITING.code);
				break;
			}
			case 6:{//借款中
				querySQL.append(" AND bid.status=:status ");
				countSQL.append(" AND bid.status=:status");;
				conditionArgs.put("status", Status.FUNDRAISING.code);
				break;
			}
			case 7:{//满标审核
				querySQL.append(" AND bid.status=:status");
				countSQL.append(" AND bid.status=:status");
				conditionArgs.put("status", Status.AUDITING.code);
				break;
			}
			case 8:{//还款中
				querySQL.append(" AND bid.status=:status");
				countSQL.append(" AND bid.status=:status");
				conditionArgs.put("status", Status.REPAYING.code);
				break;
			}
			case 9:{//已经结束
				querySQL.append(" AND bid.status=:status");
				countSQL.append(" AND bid.status=:status");
				conditionArgs.put("status", Status.REPAYED.code);
				break;
			}
			case 10:{//失败
				querySQL.append(" AND bid.status IN (:status)");
				countSQL.append(" AND bid.status IN (:status)");
				conditionArgs.put("status", t_bid.Status.FAIL);
				break;
			}
			default:
				break;
		}
		
		/** 按编号搜索  */
		if(StringUtils.isNotBlank(numNo)){
			querySQL.append(" AND bid.id LIKE :bidId");
			countSQL.append(" AND bid.id LIKE :bidId");
			conditionArgs.put("bidId", "%"+numNo+"%");
		}
		
		/** 按项目名称搜索  */
		if(StringUtils.isNotBlank(projectName)){
			querySQL.append(" AND bid.title LIKE :title");
			countSQL.append(" AND bid.title LIKE :title");
			conditionArgs.put("title", "%"+projectName+"%");
		}
		
		/** 按借款人搜索  */
		if(StringUtils.isNotBlank(userName)){
			querySQL.append(" AND user.name LIKE :userName");
			countSQL.append(" AND user.name LIKE :userName");
			conditionArgs.put("userName", "%"+userName+"%");
		}
		
		switch (orderType) {
			case 3:{
				querySQL.append(" ORDER BY bid.amount ");
				if(orderValue == 0){
					querySQL.append(" DESC ");
				}
				break;
			}
			case 4:{
				querySQL.append(" ORDER BY bid.apr ");
				if(orderValue == 0){
					querySQL.append(" DESC ");
				}
				break;
			}
			case 5:{
				if(orderValue == 0){
					querySQL.append(" ORDER BY bid.period_unit , bid.period ");
				}else{
					querySQL.append(" ORDER BY bid.period_unit DESC, bid.period DESC ");
				}
				break;
			}
			case 6:{
				querySQL.append(" ORDER BY bid.loan_schedule ");
				if(orderValue == 0){
					querySQL.append(" DESC ");
				}
				break;
			}
			case 7:{
				querySQL.append(" ORDER BY bid.pre_release_time ");
				if(orderValue == 0){
					querySQL.append(" DESC ");
				}
				break;
			}
			default:{
				querySQL.append(" ORDER BY bid.id ");
				if(orderValue == 0){
					querySQL.append(" DESC ");
				}
				break;
			}
		}
		
		if(exports == Constants.EXPORT){
			PageBean<BackRiskBid> page = new PageBean<BackRiskBid>();
			page.page = this.findListBeanBySQL(querySQL.toString(), BackRiskBid.class, conditionArgs);
			return page;
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), BackRiskBid.class, conditionArgs);
	}
	
	/**
	 * 后台-财务页面-标的列表
	 *
	 * @param showType 筛选类型    0-所有;1-待放款;2-已放款(还款中,已还款)
	 * @param currPage
	 * @param pageSize
	 * @param exports 1：导出  default:不导出
	 * @param loanName 借款人
	 * @param orderType 排序栏目  0：编号   2：借款金额  7：放款时间
	 * @param orderValue 排序规则 0,降序，1,升序
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月13日
	 */
	public PageBean<BackFinanceBid> pageOfBidFinance(int showType, int currPage, int pageSize, int exports, String loanName, int orderType, int orderValue) {
		
		/**
		SELECT
			bid.id AS id,
			bid.title AS title,
			user.name AS name,
			bid.amount AS amount,
			bid.status AS status,
			bid.release_time AS release_time,
			bid.contract_id AS contract_id,
			supervisor1.reality_name AS pre_audit_supervisor,
			supervisor2.reality_name AS audit_supervisor,
			supervisor3.reality_name AS release_supervisor
		FROM
			(
				(
					(
						t_bid bid
						LEFT JOIN t_user user ON bid.user_id = user.id
					)
					LEFT JOIN t_supervisor supervisor1 ON bid.preauditor_supervisor_id = supervisor1.id
				)
				LEFT JOIN t_supervisor supervisor2 ON bid.auditor_supervisor_id = supervisor2.id
			)
		LEFT JOIN t_supervisor supervisor3 ON bid.release_supervisor_id = supervisor3.id
		WHERE
			1 = 1
		 */
		StringBuffer querySQL = new StringBuffer("SELECT bid.id AS id, bid.title AS title, user.name AS name, bid.amount AS amount, bid.status AS status, bid.release_time AS release_time, bid.contract_id AS contract_id, supervisor1.reality_name AS pre_audit_supervisor, supervisor2.reality_name AS audit_supervisor, supervisor3.reality_name AS release_supervisor FROM (((t_bid bid LEFT JOIN t_user user ON bid.user_id=user.id) LEFT JOIN t_supervisor supervisor1 ON bid.preauditor_supervisor_id=supervisor1.id) LEFT JOIN t_supervisor supervisor2 ON bid.auditor_supervisor_id=supervisor2.id) LEFT JOIN t_supervisor supervisor3 ON bid.release_supervisor_id=supervisor3.id WHERE ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_bid bid LEFT JOIN t_user user ON bid.user_id = user.id WHERE  ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		switch (showType) {
			case 0:{//所有
				querySQL.append("  (bid.status=:status1 OR bid.status=:status2 OR bid.status=:status3) ");
				countSQL.append("  (bid.status=:status1 OR bid.status=:status2 OR bid.status=:status3) ");
				condition.put("status1", t_bid.Status.WAIT_RELEASING.code);
				condition.put("status2", t_bid.Status.REPAYING.code);
				condition.put("status3", t_bid.Status.REPAYED.code);
				break;
			}
			case 1:{//待放款
				querySQL.append("  bid.status=:status ");
				countSQL.append("  bid.status=:status ");
				condition.put("status", t_bid.Status.WAIT_RELEASING.code);
				break;
			}
			case 2:{//已放款
				querySQL.append("  (bid.status=:status1 OR bid.status=:status2) ");
				countSQL.append("  (bid.status=:status1 OR bid.status=:status2) ");
				condition.put("status1", t_bid.Status.REPAYING.code);
				condition.put("status2", t_bid.Status.REPAYED.code);
				break;
			}
		}
		
		//按借款人姓名模糊查询
		if(StringUtils.isNotBlank(loanName)){
			querySQL.append(" AND user.name LIKE :loanName ");
			countSQL.append(" AND user.name LIKE :loanName ");
			condition.put("loanName", "%"+loanName+"%");
		}
		
		if(orderType == 2){ //借款金额
			querySQL.append(" ORDER BY amount ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
		} 
		else if(orderType == 7){ //放款时间
			querySQL.append(" ORDER BY release_time ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
		}
		else { //编号
			querySQL.append(" ORDER BY id ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
			}
		}
		
		
		if(exports == Constants.EXPORT){
			PageBean<BackFinanceBid> page = new PageBean<BackFinanceBid>();
			page.page = findListBeanBySQL(querySQL.toString(), BackFinanceBid.class, condition);
			return page;
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), BackFinanceBid.class, condition);
	}
	
	/**
	 * 前台-我的财富-我的借款
	 *
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月13日
	 */
	public PageBean<FrontMyLoanBid> pageOfBidFront(int currPage, int pageSize, long userId) {
		
		/**
		SELECT 
			bid.id AS id, 
			bid.title AS title, 
			bid.amount AS amount, 
			bid.apr AS apr, 
			bid.period_unit AS period_unit, 
			bid.period AS period, 
			bid.repayment_type AS repayment_type, 
			bid.release_time AS release_time, 
			bid.status AS status, 
			(SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id AND status IN ("+t_bill.Status.NORMAL_REPAYMENT.code+","+t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT.code+","+t_bill.Status.OUT_LINE_RECEIVE.code+","+t_bill.Status.OUT_LINE_PRINCIIPAL_RECEIVE.code+")) AS has_repayment_bill, 
			(SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id) AS total_repayment_bill, 
			(SELECT COUNT(DISTINCT(bid_audit_subject_id)) FROM t_bid_item_user WHERE bid_id=bid.id) AS has_upload_item, 
			(SELECT COUNT(1) FROM t_audit_subject_bid WHERE bid_id=bid.id) AS total_upload_item 
		FROM t_bid bid 
		WHERE bid.user_id=:user_id ORDER BY id DESC
		 */
		String querySQL = "SELECT  bid.id AS id, bid.title AS title,  bid.amount AS amount,  bid.apr AS apr,  bid.period_unit AS period_unit, bid.period AS period, bid.repayment_type AS repayment_type,  bid.release_time AS release_time,  bid.status AS status, bid.contract_id AS contract_id,  (SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id AND status IN ("+t_bill.Status.NORMAL_REPAYMENT.code+","+t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT.code+","+t_bill.Status.OUT_LINE_RECEIVE.code+","+t_bill.Status.OUT_LINE_PRINCIIPAL_RECEIVE.code+","+t_bill.Status.ADVANCE_lINE_RECEIVE.code+","+t_bill.Status.ADVANCE_lINE_RECEIVE_YET.code+")) AS has_repayment_bill,  (SELECT COUNT(1) FROM t_bill WHERE bid_id=bid.id) AS total_repayment_bill,  (SELECT COUNT(DISTINCT(bid_audit_subject_id)) FROM t_bid_item_user WHERE bid_id=bid.id) AS has_upload_item,  (SELECT COUNT(1) FROM t_audit_subject_bid WHERE bid_id=bid.id) AS total_upload_item  FROM t_bid bid WHERE bid.user_id=:user_id ORDER BY id DESC ";
		String countSQL = "SELECT COUNT(1) FROM t_bid WHERE user_id=:user_id";
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("user_id", userId);
		
		return super.pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, FrontMyLoanBid.class, conditionArgs);
	}

	/**
	 * 后台-首页-待办事项的统计
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月14日
	 */
	public Map<String, Object> backCountBidInfo(){
		/**
	     SELECT
			(SELECT COUNT(1) FROM t_bid WHERE status =0) AS preAuditing,
			(SELECT COUNT(1) FROM t_bid WHERE status =2) AS auditing,
			(SELECT COUNT(1) FROM t_bid WHERE status =3) AS waitReleasing
	     FROM DUAL
		 */
		String countSQL = "SELECT IFNULL((SELECT COUNT(1) FROM t_bid WHERE status=:preAuditing), 0) AS preAuditing, IFNULL((SELECT COUNT(1) FROM t_bid WHERE status=:auditing), 0) AS auditing, IFNULL((SELECT COUNT(1) FROM t_bid WHERE status=:waitReleasing), 0) AS waitReleasing FROM DUAL";
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("preAuditing", Status.PREAUDITING.code);
		conditionArgs.put("auditing", Status.AUDITING.code);
		conditionArgs.put("waitReleasing", Status.WAIT_RELEASING.code);
		
		return super.findMapBySQL(countSQL, conditionArgs);
	}
	
	/**
	 * 查询流标的Bid(条件：筹款中；过了投标期限；没有满标)
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月30日
	 */
	public t_bid queryBidIsFlow() {
		
		String sql = "SELECT * FROM t_bid WHERE status=:status AND invest_expire_time < :nowTime AND loan_schedule < 100 AND trust_status = 2";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_bid.Status.FUNDRAISING.code);
		params.put("nowTime", new Date());
		
		return super.findBySQL(sql, params);
	}
	
	/**
	 * 查询标的进度
	 *
	 * @param bidId
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public double findBidSchedule(long bidId) {
		String sql = "select loan_schedule from t_bid where id =:bid_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bid_id", bidId);
		
		return super.findSingleDoubleBySQL(sql, 0.00, params);
	}
	
	/**
	 * 查询待奖励标的
	 * @param rewardGrantType
	 * @return
	 */
	public List<Map<String,Object>> selectRewardBidId(int rewardGrantType){
		String sql = "select IFNULL( id , 0 ) as bid_id from t_bid where reward_grant_type = :type and status in(:sta);" ;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", rewardGrantType);
		params.put("sta", t_bid.Status.LOAN);
		
		return super.findListMapBySQL(sql, params);
		
	}
	
	/**
	 * 修改当前标的奖励状态
	 * @param bidId
	 * @param type
	 * @param endType
	 * @return
	 */
	public int updateBidRewardGrantType(long bidId , int type ,int endType){
		String sql = " update t_bid set reward_grant_type = :endType where id= :bidId and reward_grant_type = :type " ;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		params.put("endType", endType);
		params.put("bidId", bidId );
		
		return super.updateBySQL(sql, params);
	}
	
	/**
	 * 查询最新保存的标
	 * @return
	 */
	public t_bid queryLastBid() {
		String sql = " select * from t_bid b order by b.id desc";
				
		return (t_bid) super.findSingleBySQL(sql, null);
	}
	
	/**
	 * 查询累计借款笔数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年11月28日
	 *
	 */
	public int findNowCountOfBids() {
		String sql = "SELECT COUNT(1) FROM t_bid where status==4";
		
		return findSingleIntBySQL(sql, 0, null);
	}
	
	/**
	 * 查询累计借款笔数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月20日
	 *
	 */
	public int findCountOfBids() {
		String sql = "SELECT COUNT(1) FROM t_bid where status>=4";
		
		return findSingleIntBySQL(sql, 0, null);
	}
	
	/**
	 * 查询上月放款项目总额
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月20日
	 *
	 */
	public double findTotalBorrowAmountByMonth(String beginTime, String endTime) {
		
		String sql = "SELECT SUM(bid.amount) FROM t_bid bid WHERE bid.status=4 and bid.release_time BETWEEN :beginTime AND :endTime";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		
		return findSingleDoubleBySQL(sql, 0.00, params);
	}
	
	/**
	 * 查询月出借人数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月20日
	 *
	 */
	public int findLenderCountByMonth(String beginTime, String endTime) {
		String sql = "SELECT COUNT(distinct user_id) FROM t_bid where time >=:beginTime AND time <=:endTime";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		
		return findSingleIntBySQL(sql, 0, params);
	}
	
	/**
	 * 查询借款总余额
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月25日
	 *
	 */
	public double findTotalBalance() {
		
		String sql = "SELECT SUM(b.repayment_corpus+b.repayment_interest) FROM t_bill b WHERE  b.status=0";
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		return findSingleDoubleBySQL(sql, 0.00, params);
	}
	
	/**
	 * 查询借款总余额
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2018年2月22日
	 *
	 */
	public double findUnfinishedBid(long userId) {
		String sql = "select SUM(bid.amount) FROM t_bid bid WHERE user_id =:userId  and bid.status >= 0 and bid.status != 5 ";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		
		return findSingleDoubleBySQL(sql, 0.00, params);
	}
	
	/**
	 * 查询还款来源
	 * @return
	 * 
	 * @author guoShiJie
	 * @createDate 2018年5月14日
	 * 
	 */
	public String findRepaymentSourceByRiskId (long bidId) {
		String sql = "select back_resource from t_risk_report where id = (select risk_id from t_bid where id = :bidId) ";
		Map<String ,Object> params = new HashMap<String,Object>();
		params.put("bidId", bidId);
		
		return findSingleStringBySQL(sql, null, params);
	}
	
	/**
	 * 
	 * @Title: findByBidId
	 *
	 * @description 在借款信息中添加起息日，有的话写放款时间，没有的话写暂无（年月日）
	 *
	 * @param @param bidId
	 * @param @return 
	 * 
	 * @return t_bid    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年12月10日
	 */
	public t_bid findByBidId (long bidId) {
		String sql = " SELECT * from t_bid t WHERE t.risk_id=:bidId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		
		return this.findBySQL(sql, condition);
	}
	
}
