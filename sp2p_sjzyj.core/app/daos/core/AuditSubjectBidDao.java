package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.core.entity.t_audit_subject_bid;
import daos.base.BaseDao;

/**
 * 标的审核科目表DAO
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月7日
 */
public class AuditSubjectBidDao extends BaseDao<t_audit_subject_bid>{
	
	protected AuditSubjectBidDao(){}

	/**
	 * 通过标的审核科目id查询审核科目名称
	 *
	 * @param auditSubjectId 标的审核科目表id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月2日
	 */
	public String findAuditSubjectName(long auditSubjectId) {
		
		String sql = "SELECT name FROM t_audit_subject_bid WHERE id=:id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", auditSubjectId);
		
		return super.findSingleStringBySQL(sql, "", params);
	}
	
	/**
	 * 通过bidId查询审核科目id列
	 *
	 * @param bidId 标的id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月23日
	 */
	public List<Object> querySubjectList(long bidId) {
		
		String sql = "SELECT id FROM t_audit_subject_bid WHERE bid_id=:bid_id ORDER BY id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bid_id", bidId);
		return super.findListSingleBySQL(sql, params);
	}
	
	/**
	 * 通过bidId查询标的关联审核科目的对象列
	 *
	 * @param bid_id 标的id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月23日
	 */
	public List<t_audit_subject_bid> queryAuditSubjectBid(long bid_id){
		
		String sql = "SELECT * FROM t_audit_subject_bid WHERE bid_id=:bid_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bid_id", bid_id);
		
		return super.findListBySQL(sql, params);
	}

}
