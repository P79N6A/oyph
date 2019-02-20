package services.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.core.entity.t_bid_item_supervisor;
import models.core.entity.t_bid_item_user;
import net.sf.json.JSONObject;
import services.base.BaseService;
import common.utils.Factory;
import common.utils.NoUtil;
import common.utils.OSSUploadUtil;
import common.utils.ResultInfo;
import daos.core.BidItemSupervisorDao;
import daos.core.BidItemUserDao;

/**
 * 管理员上传审核科目资料的Service
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月8日
 */
public class BidItemSupervisorService extends BaseService<t_bid_item_supervisor> {

	public BidItemUserDao biditemuserdao = Factory.getDao(BidItemUserDao.class);
	
	public BidService bidService = Factory.getService(BidService.class);
	
	public AuditSubjectBidService auditSubjectBidService = Factory.getService(AuditSubjectBidService.class);
	
	private BidItemSupervisorDao bidItemSupervisorDao;
	
	protected BidItemSupervisorService() {
		bidItemSupervisorDao = Factory.getDao(BidItemSupervisorDao.class);
		super.basedao = bidItemSupervisorDao;
	}

	/**
	 * 保存管理员上传的审核科目资料
	 *
	 * @param supervisorId 管理员id
	 * @param bidId 标的id
	 * @param bidAuditSubjectId 标的审核科目资料id
	 * @param name 资料名称
	 * @param url 资料路径
	 * @param image_format 图片格式
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月16日
	 */
	public boolean saveBidItemSupervisor(long supervisorId, long bidId, long bidAuditSubjectId, String name, String url, String image_format) {
		
		t_bid_item_supervisor t = new t_bid_item_supervisor();
		t.supervisor_id = supervisorId;
		t.bid_id = bidId;
		t.bid_audit_subject_id = bidAuditSubjectId;
		t.name = name;
		t.url = url;
		t.image_format = image_format;
		
		return bidItemSupervisorDao.save(t);
	}
	
	/**
	 * 管理员删除已经上传的审核科目资料（项目初审的时候）
	 *
	 * @param itemId 资料id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public ResultInfo delBidItemSupervisor(long itemId){
		ResultInfo result = new ResultInfo();
		
		t_bid_item_supervisor bidItemSupervisor = bidItemSupervisorDao.findByID(itemId);
		
		int i = bidItemSupervisorDao.delBidItemSupervisor(itemId);
		
		OSSUploadUtil.deleteFile(bidItemSupervisor.url, bidItemSupervisor.image_format);
		
		if(i < 1){
			result.code=-1;
			result.msg = "删除失败!";
			
			return result;
		}
		
		String subject = auditSubjectBidService.findAuditSubjectName(bidItemSupervisor.bid_audit_subject_id);
		
		Map<String, String>reMsg = new HashMap<String, String>();
		reMsg.put("bid_no", NoUtil.getBidNo(bidItemSupervisor.bid_id));
		reMsg.put("bid_name", bidService.findBidNameById(bidItemSupervisor.bid_id));
		reMsg.put("subject", subject);
		reMsg.put("filename", bidItemSupervisor.name);
		
		result.obj = reMsg;
		result.code = 1;
		result.msg = "";
		
		return result;
	} 
	
	/**
	 * 管理员上传审核资料
	 *
	 * @param bidId 标的id
	 * @param bidAuditSubjectId 标的关联的审核科目id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月30日
	 */
	public List<t_bid_item_supervisor> queryBidItemSupervisor(long bidId, long bid_audit_subject_id) {
		
		return super.findListByColumn("bid_id=?1 AND bid_audit_subject_id=?2", bidId, bid_audit_subject_id);
	}
	
	/**
	 * 根据标的id查询管理员上传的审核科目资料
	 *
	 * @param bidId 标的id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月16日
	 */
	public List<t_bid_item_supervisor> queryBidItemSupervisor(long bidId) {
		
		return super.findListByColumn("bid_id=?1 ORDER BY bid_audit_subject_id", bidId);
	}
	
	/***
	 * 查询借款标审核详情图片（OPT=335）
	 * @param bidId 标id
	 * @param auditId 审核详情的id
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author liuyang
	 * @createDate 2017-12-1
	 */
	public String listOfAuditItems(long bidId, long auditId) throws Exception{
		List<Map<String, Object>> listMaps = bidItemSupervisorDao.listOfAuditItems(bidId, auditId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("auditImgs", listMaps);
		return JSONObject.fromObject(map).toString();
	}
}
