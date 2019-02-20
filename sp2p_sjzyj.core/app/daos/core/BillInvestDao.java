package daos.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import models.core.bean.BillInvest;
import models.core.bean.InvestReceive;
import models.core.entity.t_bill_invest;
import models.core.entity.t_product;
import common.constants.Constants;
import common.enums.IsOverdue;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.base.BaseDao;

public class BillInvestDao extends BaseDao<t_bill_invest> {

	protected BillInvestDao() {
	}

	
	/**
	 * 保存投资账单(纠偏业务在这里)
	 *
	 * @param bid_id
	 * @param repayment_type
	 * @param res
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月25日
	 */
	public ResultInfo saveBillInvest(long bid_id, int repayment_type, ResultInfo res) {
		
		/** 1.生成投资账单 */
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO t_bill_invest(user_id, invest_id, bid_id, period, title, receive_time, receive_corpus, ")
		.append("receive_interest, is_overdue, overdue_fine, overdue_days, status) ")
		.append("SELECT a.user_id, a.id, a.bid_id, b.period, b.title, b.repayment_time, ")
		.append("truncate(((a.amount * b.repayment_corpus)/ c.amount),2), truncate(((a.amount * b.repayment_interest)/ c.amount),2), ")
		.append("0, 0.00, 0, 0 ")
		.append("FROM t_bill AS b LEFT JOIN t_invest AS a ON a.bid_id=b.bid_id LEFT JOIN t_bid AS c ON a.bid_id=c.id AND b.bid_id=c.id ")
		.append("WHERE b.bid_id IS NOT NULL AND b.status=0 AND b.bid_id=:bid_id ");
	
		Map<String, Object> params_tbi = new HashMap<String, Object>();
		params_tbi.put("bid_id", bid_id);
		int row_tbi = super.executSQL(sql.toString(), params_tbi);
		if(row_tbi <= 0){
			res.code = -1;
			res.msg = "添加投资账单失败!";
			
			return res;
		}
		
		/** 2.初始化纠偏数据(投资记录表):查询出的理财账单的收取本金总和和利息总和赋值给t_invest标的纠偏字段 */
		StringBuffer correctStartSql = new StringBuffer();
		correctStartSql.append("UPDATE t_invest t1, (SELECT a.invest_id, a.bid_id, a.user_id, SUM(a.receive_corpus) receive_corpus, SUM(a.receive_interest)")
		.append(" receive_interest FROM t_bill_invest a WHERE a.bid_id=:bid_id GROUP BY a.invest_id) t2 SET t1.correct_amount = t2.receive_corpus ,")
		.append(" t1.correct_interest = t2.receive_interest WHERE t1.bid_id = t2.bid_id and t1.id = t2.invest_id");
		Map<String, Object> correctStart = new HashMap<String, Object>();
		correctStart.put("bid_id", bid_id);
		int correctStart_row = super.updateBySQL(correctStartSql.toString(), correctStart);
		if(correctStart_row < 0){
			res.code = -1;
			res.msg = "纠偏数据初始化失败!";
			
			return res;
		}
		
		/** 3.纠偏本金利息给第一个投资人(投资记录表)：
		 * t_bill表要还款的本金总金额和利息总金额分别减去t_invest纠偏本金总和和纠偏利息总金额得到的差值再分别加到第一个投资人纠偏本金和纠偏利息上 */
		StringBuffer correctCorIntSql = new StringBuffer();
		correctCorIntSql.append("UPDATE t_invest t1, (SELECT t3.min_id, (t4.repayment_corpus - t3.collect_amount) check_amount, ")
		.append("(t4.repayment_interest - t3.collect_interest) check_interest from (select min(a.id) min_id, a.bid_id, sum(a.correct_amount) ")
		.append("collect_amount, SUM(a.correct_interest) collect_interest FROM t_invest a WHERE a.bid_id =:bid_id1 GROUP BY a.bid_id) t3 LEFT JOIN ")
		.append("(SELECT b.bid_id, SUM(b.repayment_corpus) repayment_corpus, SUM(b.repayment_interest) repayment_interest FROM  t_bill b WHERE ")
		.append("b.bid_id =:bid_id2 GROUP BY b.bid_id) t4 ON t3.bid_id = t4.bid_id) t2 SET t1.correct_amount = t1.correct_amount + t2.check_amount, ")
		.append("t1.correct_interest = t1.correct_interest + t2.check_interest WHERE t1.id = t2.min_id");		
		Map<String, Object> correctCorInt = new HashMap<String, Object>();
		correctCorInt.put("bid_id1", bid_id);
		correctCorInt.put("bid_id2", bid_id);
		int correctCorInt_row = super.updateBySQL(correctCorIntSql.toString(), correctCorInt);
		if(correctCorInt_row < 0){
			res.code = -1;
			res.msg = "纠偏本金利息失败!";
			
			return res;
		}
		
		/** 4.核对纠偏本金(投资记录表) */
		StringBuffer checkCorrectSql = new StringBuffer();
		checkCorrectSql.append("UPDATE t_invest t1,(SELECT a.id, a.user_id, a.bid_id, (a.amount - a.correct_amount) check_corpus from t_invest a ")
		.append("WHERE a.bid_id =:bid_id) t2 SET t1.correct_amount = t1.correct_amount + t2.check_corpus ")
		.append("WHERE t1.bid_id = t2.bid_id and t1.user_id = t2.user_id and t2.id = t1.id");
		Map<String, Object> checkCorrect = new HashMap<String, Object>();
		checkCorrect.put("bid_id", bid_id);
		int checkCorrect_row = super.updateBySQL(checkCorrectSql.toString(), checkCorrect);
		if(checkCorrect_row < 0){
			res.code = -1;
			res.msg = "纠偏本金失败!";
			
			return res;
		}
		
		/** 5.纠偏利息给第一个投资人(投资记录表)：t_bill里的 和t_invest表的所有本金和本金，利息和利息相减得到的差值给第一个投资人 */
		StringBuffer correctIntSql = new StringBuffer();
		correctIntSql.append("UPDATE t_invest t1, (SELECT t3.min_id, (t4.repayment_corpus - t3.collect_amount) check_amount, ")
		.append("(t4.repayment_interest - t3.collect_interest) check_interest FROM (select MIN(a.id) min_id, a.bid_id, SUM(a.correct_amount) ")
		.append("collect_amount, SUM(a.correct_interest) collect_interest FROM t_invest a WHERE a.bid_id =:bid_id1 GROUP BY a.bid_id) t3 LEFT JOIN ")
		.append("(SELECT b.bid_id, SUM(b.repayment_corpus) repayment_corpus, SUM(b.repayment_interest) repayment_interest FROM  t_bill b WHERE ")
		.append("b.bid_id =:bid_id2 GROUP BY b.bid_id) t4 ON t3.bid_id = t4.bid_id) t2 SET t1.correct_amount = t1.correct_amount + t2.check_amount, ")
		.append("t1.correct_interest = t1.correct_interest + t2.check_interest WHERE t1.id = t2.min_id");
		Map<String, Object> correctInt = new HashMap<String, Object>();
		correctInt.put("bid_id1", bid_id);
		correctInt.put("bid_id2", bid_id);
		int correctInt_row = super.updateBySQL(correctIntSql.toString(), correctInt);
		if(correctInt_row < 0){
			res.code = -1;
			res.msg = "纠偏利息失败!";
			
			return res;
		}
		
		/** 6.纠偏投资应收款明细资金(理财账单表)：t_bill和t_bill_invest根据算出每一期每个人的本金利息差值都给到第一个投资人的本金利息上 */
		StringBuffer updateSql = new StringBuffer();
		updateSql.append("update t_bill_invest t1,(SELECT c.minId, (a.repayment_corpus-b.recivedPrincipal) check_corpus,")
		.append("(a.repayment_interest-b.recivedInterest) check_interest FROM (SELECT id, bid_id, period,")
		.append("repayment_corpus,repayment_interest FROM t_bill WHERE bid_id =:bid_id1) a LEFT JOIN (SELECT a.id ,a.bid_id, a.period, ")
		.append("SUM(a.receive_corpus) recivedPrincipal, SUM(a.receive_interest) recivedInterest FROM t_bill_invest a WHERE a.bid_id=:bid_id2 ")
		.append("GROUP BY a.period) b ON a.bid_id = b.bid_id AND a.period = b.period  LEFT JOIN (SELECT MIN(a.id) minId,a.bid_id,")
		.append("a.period FROM t_bill_invest a WHERE a.bid_id =:bid_id3 GROUP BY a.period) c ON b.bid_id = c.bid_id AND a.period = c.period) ")
		.append("t2 SET t1.receive_corpus = t1.receive_corpus + t2.check_corpus, t1.receive_interest = t1.receive_interest + ")
		.append("t2.check_interest WHERE t1.id = t2.minId");
		Map<String, Object> update = new HashMap<String, Object>();
		update.put("bid_id1", bid_id);
		update.put("bid_id2", bid_id);
		update.put("bid_id3", bid_id);
		int update_row = super.updateBySQL(updateSql.toString(), update);
		if(update_row < 0){
			res.code = -1;
			res.msg = "纠偏投资应收款明细资金失败!";
			
			return res;
		}
			
		/** 7.纠偏待收本金和利息(理财账单表)：t_invest和t_bill_invest把本金和本金，利息和利息的差值都给到第一个投资人那里 */
		StringBuffer updateCorIntSql = new StringBuffer();
		updateCorIntSql.append("UPDATE t_bill_invest t1,(SELECT t3.id, t3.min_id, t3.user_id, t3.bid_id, (t4.amount - t3.receive_corpus) check_corpus, ")
		.append("(t4.correct_interest - t3.receive_interest) check_interest FROM (SELECT a.id, min(a.id) AS min_id, a.invest_id, a.bid_id, a.user_id, SUM(a.receive_corpus) ")
		.append("receive_corpus, SUM(a.receive_interest) receive_interest FROM t_bill_invest a WHERE a.bid_id =:bid_id1 GROUP BY a.invest_id) ")
		.append("t3 LEFT JOIN (SELECT b.user_id, b.id, b.bid_id, b.amount, b.correct_interest FROM t_invest b WHERE b.bid_id =:bid_id2 GROUP BY")
		.append(" b.id) t4 ON t3.bid_id = t4.bid_id AND t3.invest_id = t4.id) t2 SET t1.receive_corpus = t1.receive_corpus + ")
		.append("t2.check_corpus, t1.receive_interest = t1.receive_interest + t2.check_interest WHERE t1.id = t2.min_id");
		Map<String, Object> updateCorInt = new HashMap<String, Object>();
		updateCorInt.put("bid_id1", bid_id);
		updateCorInt.put("bid_id2", bid_id);
		int updateCorInt_row = super.updateBySQL(updateCorIntSql.toString(), updateCorInt);
		if(updateCorInt_row < 0){
			res.code = -1;
			res.msg = "纠偏待收本金和利息失败!";
			
			return res;
		}
		
		if(repayment_type == t_product.RepaymentType.AFTER_THE_REST.code){
			/** 先息后本账单 */
			StringBuffer oprateSql = new StringBuffer();
			oprateSql.append("UPDATE t_bill_invest t1,(SELECT a.receive_corpus, a.id FROM t_bill_invest a LEFT JOIN t_bid b ON a.bid_id = ")
			.append("b.id WHERE a.period < b.period and a.bid_id=:bid_id and b.period_unit <> :period_unit group by a.id) t2 SET t1.receive_corpus = 0.00, t1.receive_interest = ")
			.append("t1.receive_interest + t2.receive_corpus WHERE t1.id = t2.id");
			Map<String, Object> oprate = new HashMap<String, Object>();
			oprate.put("bid_id", bid_id);
			oprate.put("period_unit", t_product.PeriodUnit.DAY.code);
			int oprate_row = super.updateBySQL(oprateSql.toString(), oprate);
			if(oprate_row < 0){
				res.code = -1;
				res.msg = "纠偏待收本金和利息失败!";
				
				return res;
			}
		}
		res.code = 1;
		
		return res;
	}


	public boolean copyToBillInvest(long originalInvestId, long newUserId,long newInvestId) {
		
		String excSQL = "INSERT INTO t_bill_invest ( time, user_id, invest_id, bid_id, period, title, receive_time, receive_corpus, receive_interest, is_overdue, overdue_fine, overdue_days, status, real_receive_time ) SELECT :nowTime, :newUserId, :newInvestId, bii.bid_id, period, title, receive_time, receive_corpus, receive_interest, is_overdue, overdue_fine, overdue_days,status, real_receive_time FROM t_bill_invest bii WHERE bii.invest_id = :originalInvestId AND bii.status = :stat ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nowTime", new Date());
		params.put("newUserId", newUserId);
		params.put("newInvestId", newInvestId);
		params.put("originalInvestId", originalInvestId);
		params.put("stat", models.core.entity.t_bill_invest.Status.NO_RECEIVE.code);
		
		return updateBySQL(excSQL, params) >= 1;
	}
	
	/**
	 * 更新理财账单状态和收款时间
	 *
	 * @param billInvestId 理财账单id
	 * @param status 新状态
	 * @param realReceiveTime 实际收款时间
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月29日
	 */
	public int updateStatusAndRealReceiveTime(long billInvestId, int status, Date realReceiveTime) {
		String sql = "UPDATE t_bill_invest SET status = :status, real_receive_time = :time WHERE id = :id AND status != :status";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("time", realReceiveTime);
		params.put("id", billInvestId);
		
		return updateBySQL(sql, params);
	}
	
	/**
	 * 更新理财账单状态和收款时间
	 *
	 * @param billInvestId 理财账单id
	 * @param status 新状态
	 * @param realReceiveTime 实际收款时间
	 * @return
	 *
	 * @author guoShiJie
	 * @createDate 2018年10月5日
	 */
	public int updateStatusAndRealReceiveTime(long billInvestId, int status, Date realReceiveTime,String businessSeqNo) {
		String sql = "UPDATE t_bill_invest SET status = :status, real_receive_time = :time ,busniess_no = :businessSeqNo WHERE id = :id AND status != :status";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("time", realReceiveTime);
		params.put("businessSeqNo", businessSeqNo);
		params.put("id", billInvestId);
		
		return updateBySQL(sql, params);
	}
	
	/**
	 * 提前还款，更新理财账单回款数据
	 * @param billInvestId
	 * @param status
	 * @param realReceiveTime
	 * @return
	 * @createDate 2017年5月15日
	 * @author lihuijun
	 */
	/*public int updateAdvanceReceiveTime(long billInvestId, int status, Date realReceiveTime) {
		String sql = "UPDATE t_bill_invest SET status = :status, real_receive_time = :time, receive_corpus = :corpus WHERE id = :id AND status != :status";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("time", realReceiveTime);
		params.put("corpus", 1000);
		params.put("id", billInvestId);
		
		return updateBySQL(sql, params);
	}*/
	
	

	public boolean updateBillToTransfered(long invested) {
		String excSQL = " UPDATE t_bill_invest SET status = :status WHERE invest_id = :invested AND status = :sta ";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", models.core.entity.t_bill_invest.Status.TRANSFERED.code);
		params.put("invested", invested);
		params.put("sta", models.core.entity.t_bill_invest.Status.NO_RECEIVE.code);
		
		return updateBySQL(excSQL, params)>=1;
	}
	
	/**
	 * 查询用户待收/已收账单
	 *
	 * @param userId
	 * @param code 待收/已收枚举值，0-待收；1-已收
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public double findUserReceive(long userId, int code) {
		
		String sql = "SELECT IFNULL(SUM(receive_corpus + receive_interest + IFNULL(overdue_fine, 0.00)), 0) AS totalreceive FROM t_bill_invest WHERE user_id=:user_id AND status=:status";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", userId);
		params.put("status", code);
		
		return super.findSingleDoubleBySQL(sql, -1, params);
	}

	/**
	 * 查询某个投资已还本金
	 *
	 * @param investId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public double findHasReceivePrincipal(long investId) {
		
		StringBuffer querySQL = new StringBuffer(" SELECT IFNULL(SUM(bi.receive_corpus),0) FROM t_bill_invest bi WHERE bi.invest_id=:investId AND bi.status= :status ");
			
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("investId", investId);
		condition.put("status", t_bill_invest.Status.RECEIVED.code);
		
		return findSingleDoubleBySQL(querySQL.toString(),0.00,condition);
	}
	
	
	/**
	 * 查询投资总额  
	 *
	 * @param showType default:所有  1.待还  2.逾期待还  3.已还 4.已转让
	 * @param loanName 借款人昵称
	 * @param projectName 项目名称
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月19日
	 */
	public double findBillInvestTotalAmt(int showType,String loanName,String projectName) {
		StringBuffer sql= new StringBuffer("SELECT IFNULL(SUM(tbi.receive_corpus + tbi.receive_interest),0) AS totalAmt FROM t_bill_invest tbi,t_user tu WHERE tbi.user_id = tu.id ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		//1.待还  
		if(showType == 1){
			sql.append(" AND tbi.status =:status ");
			condition.put("status", t_bill_invest.Status.NO_RECEIVE.code);
		}
		
		//2.逾期待还  
		else if(showType == 2){
			sql.append(" AND tbi.status =:status AND tbi.is_overdue =true ");
			condition.put("status", t_bill_invest.Status.NO_RECEIVE.code);
		}
		
		//3.已还
		else if(showType == 3){
			sql.append(" AND tbi.status =:status ");
			condition.put("status", t_bill_invest.Status.RECEIVED.code);
		}
		
		//4.已转让
		else if(showType == 4){
			sql.append(" AND tbi.status =:status ");
			condition.put("status", t_bill_invest.Status.TRANSFERED.code);
		}
		
		//按借款人昵称 模糊查询
		if(StringUtils.isNotBlank(loanName)){
			sql.append(" AND tu.name LIKE :loanName ");
			condition.put("loanName", "%"+loanName+"%");
		}
		
		//按名称模糊查询
		if(StringUtils.isNotBlank(projectName)){
			sql.append(" AND tbi.title LIKE :title ");
			condition.put("title", "%"+projectName+"%");
		}

		return this.findSingleDoubleBySQL(sql.toString(), 0.00, condition);
	}
	
	/**
	 * 查询所有的应收利息(也即给用户创造的收益，已还未还都算,剔除已经转让的账单)
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月23日
	 */
	public double findAllInterest() {
		StringBuffer sql= new StringBuffer("SELECT IFNULL(SUM(tbi.receive_interest),0) AS totalAmt FROM t_bill_invest tbi where tbi.status <> :status ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("status", models.core.entity.t_bill_invest.Status.TRANSFERED.code);
		
		return this.findSingleDoubleBySQL(sql.toString(), 0.00, condition);
	}

	/**
	 * 分页查询 我的理财账单 
	 * 
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 *
	 * @authorliudong
	 * @createDate 2015年12月16日
	 */
	public PageBean<t_bill_invest> pageOfMyBillInvest(int currPage,
			int pageSize, long userId) {

		String sqlCount = " SELECT COUNT(id) FROM t_bill_invest WHERE user_id=:userId";
		String sql = "SELECT * FROM t_bill_invest WHERE user_id=:userId";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", "userId");

		return pageOfBySQL(currPage, pageSize, sqlCount, sql, condition);
	}

	/**
	 * 分页查询 理财账单 
	 * 
	 * @param showType default:所有  1.待还  2.逾期待还  3.已还   4.已转让
	 * @param currPage 当前页
	 * @param pageSize 条数
	 * @param orderType 参与排序的栏目 0,按编号;3,账单金额;5,逾期时长;6,到期时间;7,回款时间
	 * @param orderValue 排序的类型:0,降序;1,升序
	 * @param exports 1:导出  default：不导出
	 * @param loanName 借款人昵称
	 * @param projectName 项目
	 * @return
	 *
	 * @authorliudong
	 * @createDate 2015年12月16日
	 */
	public PageBean<BillInvest> pageOfBillInvestBack(int showType,int currPage, int pageSize,int orderType,int orderValue,int exports,String loanName,String projectName) {
		/**
			 SELECT	
				tbi.id AS id,
				tbi.time AS time,
				tb.title AS title,
				tu.name AS name,
				tbi.bid_id AS bid_id,
				tbi.receive_corpus AS receive_corpus,
				tbi.receive_interest AS receive_interest,
				(tbi.receive_corpus + tbi.receive_interest) AS corpus_interest,
				tbi.is_overdue AS is_overdue,
				tbi.invest_id AS invest_id,
				tbi.period AS period,
				tbi.overdue_days AS overdue_days,
				tbi.receive_time AS receive_time,
				tbi.real_receive_time AS real_receive_time,
				tbi.status AS status,
				(SELECT COUNT(1) FROM t_bill WHERE bid_id = tbi.bid_id ) AS totalPeriod
			FROM
				t_bill_invest tbi,
				t_user tu,
				t_bid tb
			WHERE
				tbi.user_id = tu.id
			AND tbi.bid_id = tb.id
		 */
		StringBuffer sql = new StringBuffer("SELECT tbi.id AS id,tbi.time AS time,tb.title AS title,tu.name AS name,tbi.bid_id AS bid_id,tbi.receive_corpus AS receive_corpus,tbi.receive_interest AS receive_interest,(tbi.receive_corpus + tbi.receive_interest) AS corpus_interest,tbi.is_overdue AS is_overdue,tbi.invest_id AS invest_id,tbi.period AS period,tbi.overdue_days AS overdue_days,tbi.receive_time AS receive_time,tbi.real_receive_time AS real_receive_time,tbi.status AS status,(SELECT COUNT(1) FROM t_bill WHERE bid_id = tbi.bid_id ) AS totalPeriod FROM t_bill_invest tbi, t_user tu, t_bid tb WHERE tbi.user_id = tu.id AND tbi.bid_id = tb.id ");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(1) FROM t_bill_invest tbi,t_user tu,t_bid tb WHERE tbi.user_id = tu.id AND tbi.bid_id = tb.id ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		// 1.待还
		if(showType == 1){
			sql.append(" AND tbi.status =:status ");
			sqlCount.append(" AND tbi.status =:status ");
			condition.put("status", t_bill_invest.Status.NO_RECEIVE.code);
		}
		
		//2.逾期待还  
		else if(showType == 2){
			sql.append(" AND tbi.status =:status AND tbi.is_overdue =:is_overdue ");
			sqlCount.append(" AND tbi.status =:status AND tbi.is_overdue =:is_overdue ");
			condition.put("status", t_bill_invest.Status.NO_RECEIVE.code);
			condition.put("is_overdue", IsOverdue.OVERDUE.code);
		}
		
		//3.已还
		else if(showType == 3){
			sql.append(" AND tbi.status =:status ");
			sqlCount.append(" AND tbi.status =:status ");
			condition.put("status", t_bill_invest.Status.RECEIVED.code);
		}
		
		//4.已转让
		else if(showType == 4){
			sql.append(" AND tbi.status =:status ");
			sqlCount.append(" AND tbi.status =:status ");
			condition.put("status", t_bill_invest.Status.TRANSFERED.code);
		}
		
		//按项目名称 模糊查询
		if(StringUtils.isNotBlank(projectName)){
			sql.append(" AND tb.title LIKE :title ");
			sqlCount.append(" AND tb.title LIKE :title ");
			condition.put("title", "%"+projectName+"%");
		}
		
		//按借款人昵称 模糊查询
		if(StringUtils.isNotBlank(loanName)){
			sql.append(" AND tu.name LIKE :loanName ");
			sqlCount.append(" AND tu.name LIKE :loanName ");
			condition.put("loanName", "%"+loanName+"%");
		}
		
		if(orderType == 3){
			sql.append(" ORDER BY corpus_interest ");
		} else if(orderType == 5){
			sql.append(" ORDER BY tbi.overdue_days ");
		} else if(orderType == 6){
			sql.append(" ORDER BY tbi.receive_time ");
		} else if(orderType == 7){
			sql.append(" ORDER BY tbi.real_receive_time  ");
		} else {
			sql.append(" ORDER BY tbi.id ");
		}
		
		if(orderValue == 0){//升序ASC不管
			sql.append(" DESC ");
		}
		
		if(exports == Constants.EXPORT){
			PageBean<BillInvest> pageBean = new PageBean<BillInvest>();
			pageBean.page = this.findListBeanBySQL(sql.toString(), BillInvest.class, condition);
			return pageBean;
		}
		
		return pageOfBeanBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), BillInvest.class, condition);
	}

	/**
	 * 分页查询 回款计划
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月18日
	 *
	 */
	public PageBean<InvestReceive> pageOfInvestReceive(int currPage,int pageSize, long userId) {
		String sqlCount = "SELECT COUNT(1) FROM t_bill_invest tbi INNER JOIN t_bid tb ON tb.id = tbi.bid_id WHERE tbi.user_id =:user_id AND tbi.status =:status";
		String sql = " SELECT tbi.id AS id, tbi.time AS time,tbi.user_id as user_id, tbi.receive_corpus AS receive_corpus, tbi.receive_interest AS receive_interest, (tbi.receive_corpus + tbi.receive_interest) AS corpus_interest,tbi.invest_id AS invest_id, tbi.period AS period, tbi.receive_time AS receive_time, tbi.real_receive_time AS real_receive_time, tbi.status AS status,tb.service_fee_rule AS service_fee_rule, (SELECT COUNT(1) FROM t_bill bl WHERE bl.bid_id = tbi.bid_id) AS totalPeriod FROM t_bill_invest tbi INNER JOIN t_bid tb ON tbi.bid_id = tb.id WHERE tbi.user_id =:user_id AND tbi.status =:status  ORDER BY receive_time";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("user_id", userId);
		condition.put("status", t_bill_invest.Status.NO_RECEIVE.code);

		return pageOfBeanBySQL(currPage, pageSize, sqlCount, sql, InvestReceive.class, condition);
	}
	
	/**
	 * 不分页查询 ，用户该笔投资的理财账单
	 *
	 * @param investId 投资Id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月20日
	 */
	public List<InvestReceive> queryMyBillInvestFront(long investId) {
		/**
			 SELECT
				tbi.id AS id,
				tbi.time AS time,
				tbi.user_id as user_id,
				tbi.receive_corpus AS receive_corpus,
				tbi.receive_interest AS receive_interest,
				(tbi.receive_corpus + tbi.receive_interest) AS corpus_interest,
				tbi.invest_id AS invest_id,
				tbi.period AS period,
				tbi.receive_time AS receive_time,
				tbi.real_receive_time AS real_receive_time,
				tbi.status AS status,
				tb.service_fee_rule AS service_fee_rule,
				(SELECT COUNT(id)FROM t_bill WHERE bid_id = tbi.bid_id) AS totalPeriod
			FROM
				t_bill_invest tbi,
				t_bid tb
			WHERE
				tbi.bid_id = tb.id
			AND invest_id = :investId
		 */
		String sql = "SELECT tbi.id AS id, tbi.time AS time,tbi.user_id as user_id, tbi.receive_corpus AS receive_corpus, tbi.receive_interest AS receive_interest, (tbi.receive_corpus + tbi.receive_interest) AS corpus_interest,tbi.invest_id AS invest_id, tbi.period AS period, tbi.receive_time AS receive_time, tbi.real_receive_time AS real_receive_time, tbi.status AS status,tb.service_fee_rule AS service_fee_rule, (SELECT COUNT(id)FROM t_bill WHERE bid_id = tbi.bid_id) AS totalPeriod FROM t_bill_invest tbi, t_bid tb WHERE tbi.bid_id = tb.id AND invest_id =:investId ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("investId", investId);
		
		return this.findListBeanBySQL(sql, InvestReceive.class, condition);
	}
	
	/**
	 * 
	 * @param billInvestId
	 * @createDate 2017年5月25日
	 * @author lihuijun
	 */
	public void updateTheBillInvest(long billInvestId,double surplusCorpus,double surplusInterest,double surplusFine){
		String sql = "UPDATE t_bill_invest SET receive_corpus =:receive_corpus, receive_interest =:receive_interest,overdue_fine =:overdue_fine WHERE id = :id ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("receive_corpus", surplusCorpus);
		params.put("receive_interest", surplusInterest);
		params.put("overdue_fine", surplusFine);
		params.put("id", billInvestId);
		updateBySQL(sql, params);
	}

	
	/**
	 * 更新理财账单数据
	 * @param billId
	 * @param surplusCorpus
	 * @param surplusInterest
	 * @param surplusFine
	 * @createDate 2017年5月19日
	 * @author lihuijun
	 */
	public void updateThisBillInvest(long billInvestId){
		String sql = "UPDATE t_bill_invest SET receive_corpus =:receive_corpus, receive_interest =:receive_interest,overdue_fine =:overdue_fine,status =:status WHERE id = :id ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("receive_corpus", 0);
		params.put("receive_interest", 0);
		params.put("overdue_fine", 0);
		params.put("status", 1);
		params.put("id", billInvestId);
		updateBySQL(sql, params);
	}
	
	/**
	 * 查询待还出借人数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月25日
	 *
	 */
	public int findLenderCount() {
		String sql = "SELECT COUNT(distinct user_id) FROM t_bill_invest where status=0 ";

		Map<String, Object> params = new HashMap<String, Object>();
		
		return findSingleIntBySQL(sql, 0, params);
	}
	
	/**
	 * 查询总出借人数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月25日
	 *
	 */
	public int findTotalLenderCount() {
		String sql = "SELECT COUNT(distinct user_id) FROM t_bill_invest ";

		Map<String, Object> params = new HashMap<String, Object>();
		
		return findSingleIntBySQL(sql, 0, params);
	}
	
	/**
	 * 查询某个人待还总额
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年01月23日
	 *
	 */
	public double findTotalAmounts(long userId) {
		String sql = "SELECT SUM(b.receive_corpus+b.receive_interest) FROM t_bill_invest b INNER JOIN t_bid bid ON bid.id = b.bid_id WHERE bid.status>0 and bid.status<=4 AND b.user_id=:userId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		
		return super.findSingleDoubleBySQL(sql, 0.00, params);
	}

}
