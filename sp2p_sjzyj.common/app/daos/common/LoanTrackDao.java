package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_loan_track;

/**
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2018年01月26日
 */
public class LoanTrackDao extends BaseDao<t_loan_track> {

	protected LoanTrackDao() {}
	
	/**
	 *  分页查询，跟踪列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年01月26日
	 */
	public PageBean<t_loan_track> pageOfLoanTracks(int currPage, int pageSize) {
		StringBuffer sql = new StringBuffer("SELECT * FROM t_loan_track ORDER BY time");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(id) FROM t_loan_track");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), condition);
	}
	
	/**
	 *  分页查询，跟踪列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年01月26日
	 */
	public PageBean<t_loan_track> pageOfLoanTracksByBidId(int currPage, int pageSize, long bidId) {
		StringBuffer sql = new StringBuffer("SELECT * FROM t_loan_track t where t.bid_id=:bidId ORDER BY t.time");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(id) FROM t_loan_track t where t.bid_id=:bidId");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), condition);
	}
	
	public List<t_loan_track> listOfLoanTracks(long bidId) {
		String sql = "select * from t_loan_track l where l.bid_id =:bidId ";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		
		return findListBySQL(sql, condition);
	}
	
}
