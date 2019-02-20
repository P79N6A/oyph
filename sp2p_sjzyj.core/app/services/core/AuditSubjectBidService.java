package services.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.core.bean.BidItemOfSupervisorSubject;
import models.core.bean.BidItemOfUserSubject;
import models.core.entity.t_audit_subject_bid;
import models.core.entity.t_audit_item_library;
import services.base.BaseService;
import common.utils.Factory;
import common.utils.PageBean;
import daos.base.BaseDao;
import daos.core.AuditSubjectBidDao;

/**
 * 标的审核科目的Service
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月8日
 */
public class AuditSubjectBidService extends BaseService<t_audit_subject_bid>{
	
	public static BidItemSupervisorService bidItemSupervisorService = Factory.getService(BidItemSupervisorService.class);
	
	public static BidItemUserService bidItemUserService = Factory.getService(BidItemUserService.class);

	private AuditSubjectBidDao auditsubjectbiddao;
	
	protected AuditSubjectBidService() {
		auditsubjectbiddao  = Factory.getDao(AuditSubjectBidDao.class);
		super.basedao = auditsubjectbiddao;
	}
	
	/**
	 * 保存标的关联的审核科目
	 *
	 * @param tasb 标的管理审核科目对象
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public boolean save(t_audit_subject_bid tasb){
		
		return auditsubjectbiddao.save(tasb);
	}
	
	/**
	 * 通过id获取审核科目名称
	 *
	 * @param auditSubjectId 审核科目id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月2日
	 */
	public String findAuditSubjectName(long auditSubjectId){
		
		return auditsubjectbiddao.findAuditSubjectName(auditSubjectId);
	}
	
	/**
	 * 根据bidId获取标的审核科目列
	 *
	 * @param bidId 标的id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月26日
	 */
	public List<t_audit_subject_bid> queryAuditSubjectBid(long bidId) {
		
		return auditsubjectbiddao.findListByColumn("bid_id=?1", bidId);
	}

	/**
	 * 后台-风控-初审、复审、详情-获取标的关联的 用户上传资料和管理员上传资料
	 *
	 * @param bid_id 标的id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月16日
	 */
	public Map<String, Object> queryBidRefSubject(long bid_id) {
		
		List<BidItemOfUserSubject> userItem= new ArrayList<BidItemOfUserSubject>();
		List<BidItemOfSupervisorSubject> supervisorItem= new ArrayList<BidItemOfSupervisorSubject>();
		
		List<t_audit_subject_bid> subjectIdList = auditsubjectbiddao.queryAuditSubjectBid(bid_id);
		
		if(subjectIdList == null){
			
			return new HashMap<String, Object>();
		}
		BidItemOfUserSubject bidItemOfUserSubject = null;
		BidItemOfSupervisorSubject bidItemOfSupervisorSubject = null;
		
		for(int i=0; i<subjectIdList.size(); i++){
			bidItemOfUserSubject = new BidItemOfUserSubject();
			bidItemOfUserSubject.bid_id = bid_id;
			bidItemOfUserSubject.bid_audit_subject = subjectIdList.get(i);
			bidItemOfUserSubject.bid_item_user = bidItemUserService.queryBidItemUser(bid_id, bidItemOfUserSubject.bid_audit_subject.id);
			userItem.add(bidItemOfUserSubject);
			
			bidItemOfSupervisorSubject = new BidItemOfSupervisorSubject();
			bidItemOfSupervisorSubject.bid_id = bid_id;
			bidItemOfSupervisorSubject.bid_audit_subject = subjectIdList.get(i);
			bidItemOfSupervisorSubject.bid_item_supervisor = bidItemSupervisorService.queryBidItemSupervisor(bid_id, bidItemOfSupervisorSubject.bid_audit_subject.id);
			supervisorItem.add(bidItemOfSupervisorSubject);
		}
		
		Map<String, Object> result= new HashMap<String, Object>();
		result.put("userItem", userItem);
		result.put("supervisorItem", supervisorItem);
		
		return result;
	}
	
	/**
	 * 前台-我的财富-投资管理-审核科目   查询用户自己上传的审核科目资料
	 *
	 * @param bid_id 标的id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public List<BidItemOfUserSubject> queryBidRefSubjectFront(long bid_id) {
		
		List<BidItemOfUserSubject> userItem= new ArrayList<BidItemOfUserSubject>();
		List<t_audit_subject_bid> subjectIdList = auditsubjectbiddao.queryAuditSubjectBid(bid_id);
		
		if(subjectIdList == null){
			
			return userItem;
		}
		
		BidItemOfUserSubject bidItemOfUserSubject = null;
		
		for(int i=0; i<subjectIdList.size(); i++){
			bidItemOfUserSubject = new BidItemOfUserSubject();
			bidItemOfUserSubject.bid_id = bid_id;
			bidItemOfUserSubject.bid_audit_subject = subjectIdList.get(i);
			bidItemOfUserSubject.bid_item_user = bidItemUserService.queryBidItemUserFront(bid_id, bidItemOfUserSubject.bid_audit_subject.id);
			
			userItem.add(bidItemOfUserSubject);
		}
		
		return userItem;
	}
	
	/**
	 * 前台-理财-标的详情页面的查看管理员上传的审核科目资料
	 *
	 * @param bid_id 标的id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月22日
	 */
	public List<BidItemOfSupervisorSubject> queryBidRefSubjectSupervisor(long bid_id) {
		
		List<BidItemOfSupervisorSubject> supervisorItem= new ArrayList<BidItemOfSupervisorSubject>();
		List<t_audit_subject_bid> subjectIdList = auditsubjectbiddao.queryAuditSubjectBid(bid_id);
		
		if(subjectIdList == null){
			
			return supervisorItem;
		}
		BidItemOfSupervisorSubject bidItemOfSupervisorSubject = null;
		
		for(int i=0; i<subjectIdList.size(); i++){
			bidItemOfSupervisorSubject = new BidItemOfSupervisorSubject();
			bidItemOfSupervisorSubject.bid_id = bid_id;
			bidItemOfSupervisorSubject.bid_audit_subject = subjectIdList.get(i);
			bidItemOfSupervisorSubject.bid_item_supervisor = bidItemSupervisorService.queryBidItemSupervisor(bid_id, bidItemOfSupervisorSubject.bid_audit_subject.id);
			supervisorItem.add(bidItemOfSupervisorSubject);
		}
		
		return supervisorItem;
	}

	/**
	 * 前台-投资管理-审核科目-查看用户科目库
	 *
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年4月1日
	 */
	public PageBean<t_audit_item_library> queryUserLibraryList(int currPage, int pageSize, long userId) {
		
		StringBuffer querySQL = new StringBuffer("select * from t_audit_item_library where user_id = :userId order by id desc");
		StringBuffer countSQL = new StringBuffer("select count(1) from t_audit_item_library  where user_id = :userId ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		return basedao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), t_audit_item_library.class, condition);
		
	}

}
