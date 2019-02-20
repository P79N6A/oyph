package daos.core;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.JPA;

import com.google.zxing.Result;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import models.app.bean.Bills;
import models.core.bean.Bill;
import models.core.entity.t_bill;
import models.core.entity.t_bill.Status;
import models.core.entity.t_bill_invest;

import net.sf.json.JSONObject;
import common.constants.Constants;
import common.enums.IsOverdue;
import common.utils.DateUtil;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import daos.base.BaseDao;

public class BillDao extends BaseDao<t_bill> {

	protected BillDao() {}


	/**
	 * 查询还款计划列表
	 * 
	 * @author niu
	 * @create 2017.09.09
	 */
	public List<Map<String, Object>> getReturnInfoList(long bidId) {
		
		String sql = "SELECT t.period AS returnNo, DATE_FORMAT(t.repayment_time,'%Y%m%d') AS returnDate FROM t_bill t WHERE t.bid_id = :bidId";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bidId", bidId);
		
		return findListMapBySQL(sql, params);
	}
	
	/**
	 * 查询还款计划列表
	 * 
	 * @author niu
	 * @create 2017.09.09
	 */
	public List<Map<String, Object>> findReturnList(long bidId) {
		
		String sql = "SELECT t.id AS oderNo, t.period AS returnNo, DATE_FORMAT(t.repayment_time,'%Y%m%d') AS returnDate FROM t_bill t WHERE t.bid_id = :bidId";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bidId", bidId);
		
		return findListMapBySQL(sql, params);
	}
	
	
	
	
	
	/**
	 * 查询用户待还金额
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public int updateStatus(long billId, int status) {
		String sql = "UPDATE t_bill SET status = :status WHERE id = :id AND status != :status";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("id", billId);
		
		return updateBySQL(sql, params);
	}
	
	/**
	 * 更新借款账单状态
	 * 
	 * @param billId 借款账单Id
	 * @param status 新状态
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月29日
	 */
	public int updateStatusAndRealRepaymentTime(long billId, int status, Date realRepaymentTime) {
		String sql = "UPDATE t_bill SET status = :status, real_repayment_time = :time WHERE id = :id AND status != :status";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("time", realRepaymentTime);
		params.put("id", billId);
		
		return updateBySQL(sql, params);
	}
	
	/**
	 * 更新借款账单状态
	 * 
	 * @param billId 借款账单Id
	 * @param status 新状态
	 * @return
	 *
	 * @author guoShiJie
	 * @createDate 2018年10月5日
	 */
	public int updateStatusAndRealRepaymentTime(long billId, int status, Date realRepaymentTime,String busniessNo) {
		String sql = "UPDATE t_bill SET status = :status, real_repayment_time = :time , busniess_no = :busniessNo WHERE id = :id AND status != :status ";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("time", realRepaymentTime);
		params.put("busniessNo", busniessNo);
		params.put("id", billId);
		
		return updateBySQL(sql, params);
	}

	/**
	 * 待还总额
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月26日
	 *
	 */
	public double findTotalNoRepaymentAmount() {
		String sql = "SELECT SUM(repayment_corpus+repayment_interest) FROM t_bill WHERE status IN (:status)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_bill.Status.NO_REPAYMENT);
		
		return findSingleDoubleBySQL(sql, 0.00, params);
	}
	
	/**
	 * 到期还款率   
	 * 到期还款率=（理财账单总数-逾期待还理财账单数）/理财账单总数
	 *
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月11日
	 */
	public double queryExpireRepaymentRate() {
		String sql1 = "SELECT COUNT(1) FROM t_bill_invest";
		String sql2 = "SELECT  COUNT(1) FROM t_bill_invest WHERE is_overdue=:overdue AND status=:status";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("status", t_bill_invest.Status.NO_RECEIVE.code);
		condition.put("overdue", IsOverdue.OVERDUE.code);
		
		/* 理财账单总数 */
		long total = findSingleLongBySQL(sql1, 0, null);
		
		/* 逾期待还理财账单数 */
		long part = findSingleLongBySQL(sql2, 0,condition);
		if(total==0){
			total= 1;
		}
		return Arith.div((total-part), total, 4);
	}
	
	/**
	 * 更新借款账单状态和还款时间
	 * 
	 * @param billId 借款账单Id
	 * @param status 新状态
	 * @param realRepaymentTime 时间还款时间
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月29日
	 */
	public List<t_bill> queryNoRepaymentBillList(long bidId) {
		String sql = "SELECT * FROM t_bill WHERE bid_id = :bidId  AND status IN (:status) ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bidId", bidId);
		params.put("status", t_bill.Status.NO_REPAYMENT);
		
		return findListBySQL(sql, params);
	}
	
	/**
	 * 标的还款计划(根据标的Id查询借款账单)
	 *
	 * @param bidId 标的id
	 * @param status 账单状态
	 * @param isOverDue 是否逾期
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2016年12月30日
	 */
	public Map<String, Object> queryBillCount(long bidId,int status,boolean isOverDue) {
		StringBuffer sql = new StringBuffer("SELECT COUNT(id) AS period FROM t_bill WHERE bid_id=:bidId ");
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		switch (status)
		{
		    case Constants.NO_PAY:
		    	sql.append(" AND status in(:sta)");
		    	condition.put("sta", Status.NO_REPAYMENT);
		        break;
		   case Constants.ALEADY_PAY:
			   sql.append(" AND status in(:sta)");
			   condition.put("sta", Status.REPAYED);
		       break;   
		}
		
		if(isOverDue){
			sql.append(" AND is_overdue=:isOverDue");
			condition.put("isOverDue", isOverDue);
		}
		
		return findMapBySQL(sql.toString(), condition);
	}
	
	/**
	 * 查询今天到期或者一个星期后到期，未还借款账单（正常未还，本息垫付未还）
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月2日
	 */
	public List<Map<String, Object>> queryListOfWillExpireBill() {
		
		String sql = "SELECT user.id userId, user.name, bill.id billId, bill.time, bill.repayment_time, (bill.repayment_corpus+bill.repayment_interest) AS repayment_amount"
				+ " FROM t_bill bill LEFT JOIN t_user user ON bill.user_id=user.id "
				+ " WHERE status IN (:status) AND (DATEDIFF(:nowTime, repayment_time)=7 OR DATEDIFF(:nowTime, repayment_time)=0)";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_bill.Status.NO_REPAYMENT);
		params.put("nowTime", new Date());
		
		return super.findListMapBySQL(sql, params);
	}
	
	/**
	 * 
	 * 查询某期借款账单
	 * 
	 * @param userId 用户userId 
	 * @param statusList 借款账单  账单状态
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2015年12月26日
	 */
	public double findUserPay(long userId, List<Integer> statusList) {
		
		String sql = "SELECT IFNULL(SUM(IFNULL(repayment_corpus, 0.00) + IFNULL(repayment_interest, 0.00) + IFNULL(overdue_fine, 0.00)), 0) FROM t_bill WHERE user_id=:user_id AND status in (:status)";
		Map<String, Object>params = new HashMap<String, Object>();
		params.put("user_id", userId);
		params.put("status", statusList);
			
		return super.findSingleDoubleBySQL(sql, -1, params);
	}
	
	/**
	 * 回款计划
	 * 
	 * @param bid_id 标的Id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2015年12月18日
	 */
	public List<t_bill> findBillByBidId(long bidId) {
		
		String sql = "SELECT * FROM t_bill WHERE bid_id=:bidId ORDER BY id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId",bidId);
		
		List<t_bill> list = findListBySQL(sql, condition); 
		
		return list;
	}
	
	/**
	 * 统计借款账单 应还本金利息合计
	 *
	 * @param showType default:所有        1:待还(正常待还+逾期待还+本息垫付待还) 2:逾期待还(待还+逾期) 3:已还(正常还款、本息垫付还款 、线下收款、本息垫付后线下收款 )
	 * @param loanName 借款人昵称
	 * @param projectName 项目
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月19日
	 */
	public double findTotalBillAmount(int showType, String loanName, String projectName) {
		StringBuffer sql= new StringBuffer("SELECT IFNULL(SUM(tb.repayment_corpus + tb.repayment_interest),0) FROM t_bill tb,t_user tu WHERE tb.user_id = tu.id ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		// 1:待还
		if(showType == 1){
			sql.append(" AND tb.status IN (:sta)");
	    	condition.put("sta", Status.NO_REPAYMENT);
		}
		
		//2:逾期待还
		else if(showType == 2){
			sql.append(" AND tb.status IN (:sta) AND  tb.is_overdue=true");
	    	condition.put("sta", Status.NO_REPAYMENT);
		}
		
		//3:已还
		else if(showType == 3){
			sql.append(" AND tb.status IN (:sta)");
	    	condition.put("sta", Status.REPAYED);
		}
		
		//按借款人昵称 模糊查询
		if(StringUtils.isNotBlank(loanName)){
			sql.append(" AND tu.name LIKE :loanName ");
			condition.put("loanName", "%"+loanName+"%");
		}
		
		//按项目 模糊查询
		if(StringUtils.isNotBlank(projectName)){
			sql.append(" AND tb.title LIKE :title ");
			condition.put("title", "%"+projectName+"%");
		}
		
		return  findSingleDoubleBySQL(sql.toString(), 0.00, condition);
	}
	
	/**
	 * 分页查询  借款账单
	 * 
	 * @param showType default:所有    1:待还(正常待还+本息垫付待还) 2:逾期待还(待还+逾期) 3:已还(正常还款、本息垫付还款 、线下收款、本息垫付后线下收款 )
	 * @param exports 1：导出  default：不导出
	 * @param loanName 借款人昵称
	 * @param orderType 排序栏目  0：编号   3：账单金额    5：逾期时长   6：到期时间    7：还款时间
	 * @param orderValue 排序规则 0,降序，1,升序
	 * @param projectName 项目
	 * @return
	 *
	 * @author liudong
	 * @createDate 2015年12月18日
	 */
	public PageBean<Bill> pageOfBillBack(int showType,int currPage,int pageSize, int exports, String loanName, int orderType, int orderValue, String projectName) {
		/**
		 SELECT
			tb.id AS id,
			tb.time AS time,
			tb.user_id AS user_id,
			tb.bid_id AS bid_id,
			tb.title AS title,
			tb.period AS period,
			tb.status AS status,
			tb.repayment_time AS repayment_time,
			tb.real_repayment_time AS real_repayment_time,
			tb.repayment_corpus AS repayment_corpus,
			tb.repayment_interest AS repayment_interest,
			tb.is_overdue AS is_overdue,
			tb.overdue_fine AS overdue_fine,
			tb.overdue_days AS overdue_days,
			tu.name AS name,
			(tb.repayment_corpus + tb.repayment_interest) AS corpus_interest,
			(SELECT COUNT(1) AS period FROM t_bill WHERE bid_id = tb.bid_id) AS totalperiod
		FROM
			t_bill tb,
			t_user tu
		WHERE
			tb.user_id = tu.id;
		 */
		
	
		StringBuffer sql = new StringBuffer("SELECT tb.id AS id, tb.is_payment AS is_payment, tb.time AS time, tb.user_id AS user_id, tb.bid_id AS bid_id, tb.title AS title, tb.period AS period, tb.status AS status, tb.repayment_time AS repayment_time, tb.real_repayment_time AS real_repayment_time, tb.repayment_corpus AS repayment_corpus, tb.repayment_interest AS repayment_interest, tb.is_overdue AS is_overdue, tb.overdue_fine AS overdue_fine, tb.overdue_days AS overdue_days, tu.name AS name, (tb.repayment_corpus + tb.repayment_interest) AS corpus_interest, (SELECT COUNT(1) AS period FROM t_bill WHERE bid_id=tb.bid_id) AS totalperiod FROM t_bill tb, t_user tu WHERE tb.user_id = tu.id ");		
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(1) FROM t_bill tb,t_user tu WHERE tb.user_id = tu.id ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		//筛选类型
		switch (showType) {
			case 1://1:待还
				sql.append(" AND tb.status IN (:sta)");
		    	sqlCount.append(" AND tb.status IN (:sta)");
		    	condition.put("sta", Status.NO_REPAYMENT);
				break;
			
			case 2://2:逾期待还
				sql.append(" AND tb.status IN (:sta) AND  tb.is_overdue=:is_overdue ");
		    	sqlCount.append(" AND tb.status IN (:sta) AND tb.is_overdue=:is_overdue ");
		    	condition.put("sta", Status.NO_REPAYMENT);
		    	condition.put("is_overdue", IsOverdue.OVERDUE.code);
				break;
				
			case 3:////3:已还
				sql.append(" AND tb.status IN (:sta)");
		    	sqlCount.append(" AND tb.status IN (:sta)");
		    	condition.put("sta", Status.REPAYED);
				break;
		}
		
		//按项目名称 模糊查询
		if(StringUtils.isNotBlank(projectName)){
			sql.append(" AND tb.bid_id LIKE :title ");
			sqlCount.append(" AND tb.bid_id LIKE :title ");
			condition.put("title", "%"+projectName+"%");
		}
		
		//按借款人昵称 模糊查询
		if(StringUtils.isNotBlank(loanName)){
			sql.append(" AND tu.name LIKE :loanName ");
			sqlCount.append(" AND tu.name LIKE :loanName ");
			condition.put("loanName", "%"+loanName+"%");
		}
		
		//排序规则
		switch (orderType) {
			case 3: //3：账单金额   
				sql.append(" ORDER BY corpus_interest ");
				if(orderValue == 0){
					sql.append(" DESC ");
				}
				break;
				
			case 5:  //5：逾期时长 
				sql.append(" ORDER BY overdue_days ");
				if(orderValue == 0){
					sql.append(" DESC ");
				}
				break;
				
			case 6:  //6：到期时间  
				sql.append(" ORDER BY repayment_time ");
				if(orderValue == 0){
					sql.append(" DESC ");
				}
				break;
				
			case 7:  //7：还款时间
				sql.append(" ORDER BY real_repayment_time ");
				if(orderValue == 0){
					sql.append(" DESC ");
				}
				break;
	
			default: //0：编号  
				sql.append(" ORDER BY id ");
				if(orderValue == 0){
					sql.append(" DESC ");
				}
				break;
		}
		
		//导出
		if(exports == Constants.EXPORT){
			PageBean<Bill> page = new PageBean<Bill>();
			page.page = findListBeanBySQL(sql.toString(), Bill.class, condition);
			return page;
		}
		
		return pageOfBeanBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), Bill.class, condition);
	}
	
	/**
	 * 
	 * @param bidId
	 * @return
	 * @createDate 2017年5月12日
	 * @author lihuijun
	 */
	public List<Bill> ListOfBillByBidId(long bidId){
		
		String sql="SELECT tb.id AS id, tb.time AS time, tb.user_id AS user_id, tb.bid_id AS bid_id, tb.title AS title, "
				+ "tb.period AS period, tb.status AS status, tb.repayment_time AS repayment_time, "
				+ "tb.real_repayment_time AS real_repayment_time, tb.repayment_corpus AS repayment_corpus, "
				+ "tb.repayment_interest AS repayment_interest, tb.is_overdue AS is_overdue, "
				+ "tb.overdue_fine AS overdue_fine, tb.overdue_days AS overdue_days, tu.name AS name, "
				+ "(tb.repayment_corpus + tb.repayment_interest) AS corpus_interest, "
				+ "(SELECT COUNT(1) AS period FROM t_bill WHERE bid_id=tb.bid_id) AS totalperiod FROM t_bill tb, "
				+ "t_user tu WHERE tb.user_id = tu.id and tb.bid_id=? and tb.status <= 1";
		
		return JPA.em().createNativeQuery(sql, Bill.class).setParameter(1, bidId).getResultList();
		
	}

	/**
	 * 根据标的ID查询所有的未还借款账单
	 *
	 * @param bidId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月29日
	 */
	public PageBean<Map<String, Object>> pageOfRepaymentBill(int currPage, int pageSize, long bidId) {
		String sql="SELECT tb.period AS period, (tb.repayment_corpus+tb.repayment_interest) AS principalInterest,tb.repayment_time AS repayment_time,tb.status AS status,tb.bid_id AS bid_id,(SELECT COUNT(id) FROM t_bill WHERE bid_id=:bidId) AS totalPeriod FROM t_bill tb WHERE tb.bid_id=:bidId ORDER BY tb.id";
		
		String sqlCount="SELECT COUNT(tb.id) FROM t_bill tb WHERE tb.bid_id=:bidId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		
		return pageOfMapBySQL(currPage, pageSize, sqlCount, sql, condition);
	}

	/**
	 * 后台-首页-账单信息的统计字段
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月15日
	 */
	public Map<String, Object> backCountBillInfo() {
		/**
		SELECT
			IFNULL((SELECT COUNT(1)	FROM t_bill	WHERE repayment_time BETWEEN :startTime 
			    AND :endTime), 0) AS expirationMonth,
			IFNULL((SELECT COUNT(1) FROM t_bill WHERE is_overdue=:is_overdue AND status IN(:status)), 0) AS overdue
		FROM DUAL
		 */
		String countSQL = "SELECT IFNULL((SELECT COUNT(1) FROM t_bill WHERE repayment_time BETWEEN :startTime AND :endTime), 0) AS expirationMonth, IFNULL((SELECT COUNT(1) FROM t_bill WHERE is_overdue=:is_overdue AND status IN(:status)), 0) AS overdue FROM DUAL";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("startTime", DateUtil.getMonthFirstDay());
		condition.put("endTime", DateUtil.getMonthLastDay());
		condition.put("status", t_bill.Status.NO_REPAYMENT);
		condition.put("is_overdue", IsOverdue.OVERDUE.code);
		
		return super.findMapBySQL(countSQL, condition);
	}
	
	/**
	 * 系统定时任务，执行逾期的操作
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月16日
	 */
	public ResultInfo autoMarkOverdue(){
		ResultInfo result = new ResultInfo();
		
		//1、标记逾期
		String updBillStatus = "UPDATE t_bill SET mark_overdue_time=:nowTime, is_overdue=1,overdue_days=DATEDIFF(:nowTime,repayment_time) WHERE status IN (:no_repayment) AND DATEDIFF(:nowTime, repayment_time)>0 AND is_overdue=0";
		Map<String, Object> updBillStatusParams = new HashMap<String, Object>();
		updBillStatusParams.put("nowTime", new Date());
		updBillStatusParams.put("no_repayment", t_bill.Status.NO_REPAYMENT);
		int upd1 = super.updateBySQL(updBillStatus, updBillStatusParams);
		if (upd1 < 0) {
			result.code = -1;
			result.msg = "标记逾期失败!";
			
			return result;
		}
		
		//2、查询逾期的借款账单
		String selOverdueBill = "SELECT id, bid_id, period FROM t_bill WHERE is_overdue=1 AND status IN (:no_repayment)";
		Map<String, Object> updOverdueBillParams = new HashMap<String, Object>();
		updOverdueBillParams.put("no_repayment", t_bill.Status.NO_REPAYMENT);
		List<Map<String, Object>> overdueBidIdList = super.findListMapBySQL(selOverdueBill, updOverdueBillParams);
		if (overdueBidIdList==null || overdueBidIdList.size()==0) {
			result.code = 1;
			result.msg = "没有逾期的账单!";
			
			return result;
		}
		//3、循环每一笔借款账单，处理罚息
		String selOverdueRate = "SELECT service_fee_rule FROM t_bid WHERE id=:bidId";
		String updInvestBillFine1 = "UPDATE t_bill_invest t1, "
				+ "(SELECT bid_id, period, DATEDIFF(:nowTime,repayment_time) overdue_days FROM t_bill WHERE status IN(0,1) AND is_overdue=1 AND id=:billId) t2 "
				+ "SET t1.overdue_fine=(t1.receive_corpus+t1.receive_interest)*";
		String updInvestBillFine2 = "*t2.overdue_days, t1.overdue_days=IF(t1.status=0,t2.overdue_days,t1.overdue_days), t1.is_overdue=1 WHERE t1.bid_id=t2.bid_id AND t1.period=t2.period";
		Map<String, Object> selOverdueRateParams = new HashMap<String, Object>();
		for (Map<String, Object> map:overdueBidIdList) {
			
			long billId = Long.parseLong(map.get("id").toString());
			long bidId = Long.parseLong(map.get("bid_id").toString());
			int period = Integer.parseInt(map.get("period").toString());
			selOverdueRateParams.put("bidId", bidId);
			String serviceFeeRule = super.findSingleStringBySQL(selOverdueRate, "", selOverdueRateParams);
			
			Map<String, Object> updOverdueBillMap = new HashMap<String, Object>();
			updOverdueBillMap.put("billId", billId);
			updOverdueBillMap.put("nowTime", new Date());
			double overdueRate = JSONObject.fromObject(serviceFeeRule).getDouble(Constants.OVERDUE_AMOUNT_RATE);
			overdueRate = Arith.div(overdueRate, 100, 10);
			String updOverdueBillFine = updInvestBillFine1 + overdueRate + updInvestBillFine2;
			int upd2 = super.updateBySQL(updOverdueBillFine, updOverdueBillMap);
			if (upd2 < 0) {
				result.code = -1;
				result.msg = "更新理财账单逾期罚息数据失败！";
				
				return result;
			}
			
			if (Constants.IS_STINT_OF ) {
		        
				String sql = "UPDATE t_bill_invest SET overdue_fine=TRUNCATE(receive_interest*2.5, 2) WHERE bid_id=:bidId AND period=:period"
						+ " AND TRUNCATE(receive_interest*2.5, 2)<overdue_fine";
				Map<String, Object>selOverdueInterestMap = new HashMap<String, Object>();
				selOverdueInterestMap.put("bidId", bidId);
				selOverdueInterestMap.put("period", period);
				int upd3 = super.updateBySQL(sql, selOverdueInterestMap);
				if (upd3 < 0) {
					result.code = -1;
					result.msg = "扣除理财账单中多出的罚息部分失败！";
					
					return result;
				}
				
			}
			//针对这期借款账单，计算罚息(通过理财账单求和得到)
			String updBillOverdue = "UPDATE t_bill t1, (SELECT SUM(overdue_fine) AS overdue_fine FROM t_bill_invest WHERE bid_id=:bidId AND period=:period) t2 "
					+ "SET t1.overdue_fine=t2.overdue_fine, overdue_days=DATEDIFF(:nowTime,t1.repayment_time) WHERE t1.bid_id=:bidId2 AND t1.period=:period2";
			Map<String, Object> updBillOverdueMap = new HashMap<String, Object>();
			updBillOverdueMap.put("bidId", bidId);
			updBillOverdueMap.put("period", period);
			updBillOverdueMap.put("nowTime", new Date());
			updBillOverdueMap.put("bidId2", bidId);
			updBillOverdueMap.put("period2", period);
			
			int overdueBill = super.updateBySQL(updBillOverdue, updBillOverdueMap);
			if (overdueBill < 1) {
				LoggerUtil.error(true, "更新借款账单罚息数据失败!借款账单id：%s", billId);
				result.code = -1;
				result.msg = "更新借款账单罚息数据失败";
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	public void updateThisBill(long billId,double surplusCorpus,double surplusInterest,double surplusFine){
		String sql = "UPDATE t_bill SET repayment_corpus =:surplusCorpus, repayment_interest =:surplusInterest,overdue_fine =:surplusFine WHERE id = :id ";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("surplusCorpus", surplusCorpus);
		params.put("surplusInterest", surplusInterest);
		params.put("surplusFine", surplusFine);
		params.put("id", billId);	
		updateBySQL(sql, params);
	}
	
	public void updateBill(long billId){
		String sql = "UPDATE t_bill SET repayment_corpus =0, repayment_interest =0,overdue_fine =0,status=7 WHERE id = :id ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", billId);	
		updateBySQL(sql, params);
	}
	
	/**
	 * 修改借款账单是否出款
	 *
	 * @author niu
	 * @create 2017.09.28
	 */
	public int updateBillIsPayment(long billId) {
		String sql = "UPDATE t_bill SET is_payment = 1 WHERE id = :billId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("billId", billId);
		
		return this.updateBySQL(sql, condition);
	}
	
	/**
	 * 查询待还总借款人数量
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月25日
	 *
	 */
	public int findBorrowerCount() {
		String sql = "SELECT COUNT(distinct user_id) FROM t_bill where status=0 ";

		Map<String, Object> params = new HashMap<String, Object>();
		
		return findSingleIntBySQL(sql, 0, params);
	}
	
	/**
	 * 查询总借款人数量
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月25日
	 *
	 */
	public int findTotalBorrowerCount() {
		String sql = "SELECT COUNT(distinct user_id) FROM t_bill ";

		Map<String, Object> params = new HashMap<String, Object>();
		
		return findSingleIntBySQL(sql, 0, params);
	}
	
	/**
	 * 查询总代偿笔数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月25日
	 *
	 */
	public int findCompensateCount() {
		String sql = "SELECT COUNT(distinct bid_id) FROM t_bill where status=5 ";

		Map<String, Object> params = new HashMap<String, Object>();
		
		return findSingleIntBySQL(sql, 0, params);
	}
	
	/**
	 * 查询总代偿金额
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月25日
	 *
	 */
	public double findCompensateMoney() {
		String sql = "SELECT SUM(repayment_corpus+repayment_interest) FROM t_bill where status=5 ";

		Map<String, Object> params = new HashMap<String, Object>();
		
		return findSingleDoubleBySQL(sql, 0, params);
	}
	
	/**
	 * 借款人待还金额
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月27日
	 */
	public List<Map<String, Object>> queryMouthInvestLists(long limit) {
		Map<String, Object> condition = new HashMap<String, Object>();

		String sql = "SELECT sum(ti.repayment_corpus) AS amount FROM t_bill ti WHERE ti.status=0 GROUP BY ti.user_id ORDER BY amount DESC LIMIT :limit";
		
		condition.put("limit", limit);
		
		return this.findListMapBySQL(sql, condition);
	}

	/**
	 * 
	 * @Title: getBillByBid
	 * @description 根据bid_id查询
	 * @param bid_id
	 * @return
	 * List<t_bill>
	 * @author likai
	 * @createDate 2018年12月18日 上午9:18:11
	 */
	public List<t_bill> getBillByBid(Long bid_id) {
		Map<String, Object> condition = new HashMap<String, Object>();
		String sql = "SELECT * FROM `t_bill` WHERE bid_id = :bid_id";
		condition.put("bid_id", bid_id);
		return this.findListBySQL(sql, condition);
	}
	/**
	 * 贷后追踪（贷后管理）
	 * HanMeng
	 * @createDate 2018 11 28
	 * */
	public List<Bills> pageOfBill () {
		String sql= " select tbl.id as id , tbd.id as bidId, tbl.user_id as userId , tui.mobile , tui.reality_name as realName , trr.entry_number , tbl.repayment_time , tbl.period as period ,tbd.period as total_period,FORMAT((tbd.amount * tbd.service_charge/100/12 + tbl.repayment_interest + tbl.repayment_corpus ),2) as amount , tbl.is_overdue from t_bill tbl inner join t_bid tbd on tbl.bid_id=tbd.id inner join t_risk_report trr on tbd.risk_id=trr.id inner join t_user_info tui on tbd.user_id=tui.user_id where trr.entry_number IS NOT NULL  order by tbl.id desc ";
		//String countSQL = "SELECT COUNT(*) FROM t_bill tbl INNER JOIN t_bid tbd ON tbl.bid_id = tbd.id INNER JOIN t_risk_report trr ON tbd.risk_id = trr.id INNER JOIN t_user_info tui ON tbd.user_id = tui.user_id WHERE trr.entry_number IS NOT NULL ";
		Map<String,Object> condition = new HashMap<String,Object>();
				
		return this.findListBeanBySQL(sql, Bills.class, condition);
	}
	/**
	 * 
	 * @Title: getBymobile
	 * @description: 贷后管理按电话搜索(模糊查询)
	 *
	 * @param mobile
	 * @return    
	 * @return List<Bills>   
	 *
	 * @author HanMeng
	 * @createDate 2018年12月17日-下午5:05:36
	 */
	public List<Bills> getBymobile (String mobile) {
		String sql= " select tbl.id as id , tbd.id as bidId, tbl.user_id as userId , tui.mobile , tui.reality_name as realName , trr.entry_number , tbl.repayment_time , tbl.period as period ,tbd.period as total_period,FORMAT((tbd.amount * tbd.service_charge/100/12 + tbl.repayment_interest + tbl.repayment_corpus ),2) as amount , tbl.is_overdue from t_bill tbl inner join t_bid tbd on tbl.bid_id=tbd.id inner join t_risk_report trr on tbd.risk_id=trr.id inner join t_user_info tui on tbd.user_id=tui.user_id where trr.entry_number IS NOT NULL and  tui.mobile LIKE:mobile order by tbl.id desc ";
		Map<String,Object> condition = new HashMap<String,Object>();
		
		condition.put("mobile", "%"+mobile+"%");
		return this.findListBeanBySQL(sql, Bills.class, condition);
	
	}
	/**
	 * 
	 * @Title: getByoverdue
	 * @description: 贷后管理按是否逾期搜索（0未逾期 1 逾期）
	 *
	 * @param overdue
	 * @return    
	 * @return List<Bills>   
	 *
	 * @author HanMeng
	 * @createDate 2018年12月18日-上午10:15:37
	 */
	public List<Bills> getByoverdue (Integer is_overdue) {
		String sql= " select tbl.id as id , tbd.id as bidId, tbl.user_id as userId , tui.mobile , tui.reality_name as realName , trr.entry_number , tbl.repayment_time , tbl.period as period ,tbd.period as total_period,FORMAT((tbd.amount * tbd.service_charge/100/12 + tbl.repayment_interest + tbl.repayment_corpus ),2) as amount , tbl.is_overdue from t_bill tbl inner join t_bid tbd on tbl.bid_id=tbd.id inner join t_risk_report trr on tbd.risk_id=trr.id inner join t_user_info tui on tbd.user_id=tui.user_id where trr.entry_number IS NOT NULL and  tbl.is_overdue =:is_overdue order by tbl.id desc ";
		Map<String,Object> condition = new HashMap<String,Object>();
		
		condition.put("is_overdue", is_overdue);
		return this.findListBeanBySQL(sql, Bills.class, condition);
	
	}

	/**
	 * 
	 * @Title: findBillByStatus
	 * 
	 * @description 定时查询正常待还用户的还款日(根据时间倒叙)
	 * @return List<Bill>    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月30日-下午5:01:43
	 */
	public List<Bill> findBillByStatus() {
		String sql = "SELECT tb.id AS id, tb.is_payment AS is_payment, tb.time AS time, tb.user_id AS user_id, tb.bid_id AS bid_id, tb.title AS title, tb.period AS period, tb.status AS status, tb.repayment_time AS repayment_time, tb.real_repayment_time AS real_repayment_time, tb.repayment_corpus AS repayment_corpus, tb.repayment_interest AS repayment_interest, tb.is_overdue AS is_overdue, tb.overdue_fine AS overdue_fine, tb.overdue_days AS overdue_days, tu.name AS name, (tb.repayment_corpus + tb.repayment_interest) AS corpus_interest, (SELECT COUNT(1) AS period FROM t_bill WHERE bid_id=tb.bid_id) AS totalperiod FROM t_bill tb, t_user tu WHERE tb.user_id = tu.id AND tb.status = 0 ORDER BY tb.repayment_time DESC";
		
		return findListBeanBySQL(sql, Bill.class, null);
	}
}
