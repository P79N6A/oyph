package daos.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.core.entity.t_bid_item_user;
import models.core.entity.t_bid_item_user.BidItemAuditStatus;
import daos.base.BaseDao;

/**
 * 用户上传审核科目资料DAO
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月7日
 */
public class BidItemUserDao extends BaseDao<t_bid_item_user> {

	protected BidItemUserDao(){}


	/**
	 * 用户删除自己上传的审核科目资料
	 *
	 * @param bid_audit_subject_id 用户上传的审核科目资料id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public int deleteBidItemUser(long bid_audit_subject_id) {
		
		String sql = "DELETE FROM t_bid_item_user WHERE id=:id AND status in (:status)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", bid_audit_subject_id);
		params.put("status", t_bid_item_user.BidItemAuditStatus.CAN_DELETE_BY_USER);
		
		return super.deleteBySQL(sql, params);
	}
	
	/**
	 * 前台-我的财富-资产管理-我的借款-审核科目资料展开列，提交动作
	 *
	 * @param bidItemUserId 用户上传的审核科目资料id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月30日
	 */
	public int updateBidItemUserStatus(long bidItemUserId) {
		
		String sql = "UPDATE t_bid_item_user SET status=1 WHERE id=:id AND status=:status";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", bidItemUserId);
		params.put("status", t_bid_item_user.BidItemAuditStatus.NO_SUBMIT.code);
		
		return super.updateBySQL(sql, params);
	}
	
	/**
	 * 用户删除未提交或审核不通过的图片
	 *
	 * @param bid_audit_subject_id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月18日
	 */
	public int auditBidItemUser(long bidItemUserId, BidItemAuditStatus status) {
		
		String sql = "UPDATE t_bid_item_user SET status=:status WHERE id=:id AND status=:oldstatus";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status.code);
		params.put("id", bidItemUserId);
		params.put("oldstatus", t_bid_item_user.BidItemAuditStatus.WAIT_AUDIT.code);

		return super.updateBySQL(sql, params);
	}
	
	/**
	 * 通过资料的id 获取所上传资料的名称，科目名称
	 *
	 * @param id 标的审核资料表的id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年2月27日
	 */
	public Map<String, Object> findItemName(long id){
		String sql="SELECT t.name AS subjectName FROM t_bid_item_user tb, t_audit_subject_bid t WHERE t.id = tb.bid_audit_subject_id AND tb.id =:id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		
		return this.findMapBySQL(sql, condition);
		
	}
	
	/**
	 * 查询用户上传的审核科目资料列
	 *
	 * @param bid_id 标的id
	 * @param bidItemUserId 用户上传的审核科目资料id
	 * @param status 审核通过或者不通过的状态
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public List<t_bid_item_user> queryBidItemUser(long bid_id, long bid_audit_subject_id, Integer status){
		
		StringBuffer sql = new StringBuffer("SELECT * FROM t_bid_item_user WHERE bid_id=:bid_id AND bid_audit_subject_id=:bid_audit_subject_id ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bid_id", bid_id);
		params.put("bid_audit_subject_id", bid_audit_subject_id);
		
		if(status != null){
			sql.append("AND status!=:status");
			params.put("status", status);
		}
		
		
		return super.findListBySQL(sql.toString(), params);
	}

}
