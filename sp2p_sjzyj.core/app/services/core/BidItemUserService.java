package services.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_sms_jy_count;
import models.common.entity.t_user;
import models.core.entity.t_audit_item_library;
import models.core.entity.t_bid_item_user;
import models.core.entity.t_bid_item_user.BidItemAuditStatus;
import services.base.BaseService;
import services.common.NoticeService;
import services.common.SmsJyCountService;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.JYSMSModel;
import common.enums.NoticeScene;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.NoUtil;
import common.utils.ResultInfo;

import daos.common.UserDao;
import daos.core.BidItemUserDao;

/**
 * 用户上传的审核科目资料Service
 *
 * @description 
 *
 * @author yaoyi
 * @createDate 2016年3月8日
 */
public class BidItemUserService extends BaseService<t_bid_item_user> {

	protected BidItemUserDao biditemuserdao;
	
	protected static UserDao useDao = Factory.getDao(UserDao.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static BidService bidService = Factory.getService(BidService.class);
	
	protected static SmsJyCountService smsJyCountService = Factory.getService(SmsJyCountService.class);
	protected BidItemUserService() {
		biditemuserdao = Factory.getDao(BidItemUserDao.class);
		super.basedao = biditemuserdao;
	}

	/**
	 * 查询标的的该科目下，用户上传的资料
	 *
	 * @param userId 用户id
	 * @param bidId 标的id
	 * @param bidAuditSubjectId 标的关联的审核科目id
	 * @param name 名称
	 * @param url 路径
	 * @param subjectId 审核科目ID
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月16日
	 */
	public boolean saveBidItemUser(long userId, long bidId, long bidAuditSubjectId, String name, String url, long subjectId) {
		
		t_bid_item_user t = new t_bid_item_user();
		t.user_id = userId;
		t.bid_id = bidId;
		t.bid_audit_subject_id = bidAuditSubjectId;
		t.name = name;
		t.url = url;
		t.setStatus(BidItemAuditStatus.NO_SUBMIT);//0-未提交
		//t.audit_subject_id = subjectId;
		t.upload_type = Constants.LOCAL_UPLAOD;//资料审核上传方式：本地上传
		
		return biditemuserdao.save(t);
	}
	
	/**
	 * 用户删除未提交或审核不通过的图片
	 *
	 * @param bid_audit_subject_id 科目资料id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月18日
	 */
	public boolean deleteBidItemUser(long bid_audit_subject_id) {
		
		int delete = biditemuserdao.deleteBidItemUser(bid_audit_subject_id);
		if(delete < 1){
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * 用户提交上传的审核科目资料
	 *
	 * @param bidItemUserId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public int submitBidItemUser(long bidItemUserId) {
		
		return biditemuserdao.updateBidItemUserStatus(bidItemUserId);
	}

	/**
	 * 管理员审核用户上传的资料，修改状态为通过或不通过
	 *
	 * @param itemId 资料id
	 * @param status 状态
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public ResultInfo auditBidItemUser(long itemId, BidItemAuditStatus status) {
		ResultInfo result = new ResultInfo();
		
		if(!BidItemAuditStatus.NO_PASS.equals(status) && !BidItemAuditStatus.PASS.equals(status)){
			result.code=-1;
			result.msg="您上传的资料已经"+status.value;
			
			return result;
		}
		
		int i = biditemuserdao.auditBidItemUser(itemId, status);
		if(i != 1){
			result.code=-1;
			result.msg="资料审核失败";
			
			return result;
		}
		/** 发送通知 */
		t_bid_item_user bidItemUser = biditemuserdao.findByID(itemId);
		t_user user = useDao.findByID(bidItemUser.user_id);
		Map<String, Object> map = biditemuserdao.findItemName(itemId);
		
		String name = (String)map.get("subjectName");
		String nn = name;
		if (name.indexOf("【") != -1) {
			nn = name.replace("【", "");
		}
		if (name.indexOf("】") != -1) {
			nn = name.replace("】", "");
		}
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		param.put("subject_name", nn);
		
		/** 审核成功  */
		if(BidItemAuditStatus.PASS.equals(bidItemUser.getStatus())){
			//科目审核通过
			//创蓝接口
			//noticeService.sendSysNotice(user.id, NoticeScene.SUBJECT_AUTID_PASS, param);
			//焦云接口
			Boolean flag = smsJyCountService.judgeIsSend(user.mobile, JYSMSModel.MODEL_SUBJECT_AUTID_PASS);
			if (flag) {
				noticeService.sendSysNotice(user.id, NoticeScene.SUBJECT_AUTID_PASS, param,JYSMSModel.MODEL_SUBJECT_AUTID_PASS);
			}
		}
		
		if(BidItemAuditStatus.NO_PASS.equals(bidItemUser.getStatus())){
			//科目审核不通过
			//创蓝接口
			noticeService.sendSysNotice(user.id, NoticeScene.SUBJECT_AUTID_REJECT, param);
			//焦云接口
			//noticeService.sendSysNotice(user.id, NoticeScene.SUBJECT_AUTID_REJECT, param,JYSMSModel.MODEL_SUBJECT_AUTID_REJECT);
			
		}

		Map<String, String>reMsg = new HashMap<String, String>();
		reMsg.put("bid_no", NoUtil.getBidNo(bidItemUser.bid_id));
		reMsg.put("bid_name", bidService.findBidNameById(bidItemUser.bid_id));
		reMsg.put("subject", (String)map.get("subjectName"));
		reMsg.put("filename", bidItemUser.name);
		
		result.obj = reMsg;
		result.code=1;
		result.msg="审核成功";
		
		return result;
	}
	
	/**
	 * 查询标的用户上传的资料
	 *
	 * @param bidId 标的id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月30日
	 */
	public List<t_bid_item_user> queryBidItemUser(long bidId) {
		
		return super.findListByColumn("bid_id=?1 ORDER BY bid_audit_subject_id", bidId);
	}
	
	/**
	 * 查询标的某个科目下用户上传审核科目资料列
	 *
	 * @param bid_id 标的id
	 * @param bidAuditSubjectId 审核科目id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月30日
	 */
	public List<t_bid_item_user> queryBidItemUser(long bid_id, long bid_audit_subject_id) {
		
		return biditemuserdao.queryBidItemUser(bid_id, bid_audit_subject_id, t_bid_item_user.BidItemAuditStatus.NO_SUBMIT.code);
	}
	
	/**
	 * 前台-我的财富-查询用户上传的资料
	 *
	 * @param bid_id 标的id
	 * @param bid_audit_subject_id 标的审核科目id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月30日
	 */
	public List<t_bid_item_user> queryBidItemUserFront(long bid_id, long bid_audit_subject_id) {
		
		return biditemuserdao.queryBidItemUser(bid_id, bid_audit_subject_id, null);
	}

	
	/**
	 * 提交审核资料时同步到科目库
	 *
	 * @param bidItemUserId
	 *
	 * @author Songjia
	 * @createDate 2016年3月31日
	 */
	public boolean saveUserItemLibrary(long bidItemUserId) {
		t_bid_item_user bidItem = biditemuserdao.findByID(bidItemUserId);
		//查询该用户科目库已存的记录数
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", bidItem.user_id);
		int libraryCount = biditemuserdao.countBySQL("select count(id) from t_audit_item_library where user_id = :userId", condition);
		if(libraryCount >= ConfConst.SUBJECT_LIBRARY_MAX_SIZE) {
			condition.put("deleteSize", libraryCount - ConfConst.SUBJECT_LIBRARY_MAX_SIZE + 1);
			//删除超过出审核库
			int row = biditemuserdao.deleteBySQL("delete from t_audit_item_library where user_id=:userId order by time limit :deleteSize", condition);
			if(row < 1) {
				return false;
			}
		}
		
		//如果是本地上传，将新上传的资料存入科目库
		if(bidItem.upload_type == Constants.LOCAL_UPLAOD) {
			t_audit_item_library subjectLibrary = new t_audit_item_library();
			subjectLibrary.url = bidItem.url;//图片路径
			subjectLibrary.name = bidItem.name;//图片名称
			subjectLibrary.user_id = bidItem.user_id;
			
			return basedao.save(subjectLibrary);
		}
		return true;
	}

	/**
	 * 保存从科目库上传的审核资料
	 *
	 * @param userId
	 * @param subjectIds  资料库素材ID集合
	 * @param bidId
	 * @param bidAuditSubjectId  标的审核科目ID
	 * @return
	 *
	 * @author Songjia
	 * @createDate 2016年4月1日
	 */
	public ResultInfo saveBidLibraryItemUser(long userId, List<Long> subjectIds, long bidId, long bidAuditSubjectId) {
		ResultInfo result = new ResultInfo();
		
		t_audit_item_library subjectLibrary = null;
		t_bid_item_user itemUser = null;
		boolean isSuccess = true;
		for(long subjectId : subjectIds) {
			subjectLibrary = t_audit_item_library.findById(subjectId);
			
			itemUser = new t_bid_item_user();
			itemUser.user_id = subjectLibrary.user_id;
			itemUser.bid_id = bidId;
			itemUser.bid_audit_subject_id = bidAuditSubjectId;//标的审核科目ID
			itemUser.name = subjectLibrary.name;
			itemUser.url = subjectLibrary.url;
			itemUser.setStatus(BidItemAuditStatus.NO_SUBMIT);//0-未提交
			itemUser.upload_type = Constants.LIBRARY_UPLOAD;//资料上传方式：科目库上传
			
			isSuccess = biditemuserdao.save(itemUser);
			if(!isSuccess) {
				result.code = -1;
				result.msg = "科目库素材上传失败";
				
				return result;
			}
			
		}
		
		result.code = 1;
		result.msg = "科目库素材上传成功";
		return result;
	}
	
}
