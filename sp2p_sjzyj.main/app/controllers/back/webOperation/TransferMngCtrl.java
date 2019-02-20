package controllers.back.webOperation;

import java.util.List;

import models.common.entity.t_transfer_notice;
import common.utils.Factory;
import play.mvc.With;
import services.common.TransferNoticeService;
import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;
import daos.common.TransferNoticeDao;

/**
 * 后台-(转让申请/还款通知)管理通知
 * @author lvweihuan
 *
 */
@With(SubmitRepeat.class)
public class TransferMngCtrl extends BackBaseController{
	
	/***/
	protected static TransferNoticeService transferNoticeService=Factory.getService(TransferNoticeService.class);
	protected static TransferNoticeDao traNoticeDao=Factory.getDao(TransferNoticeDao.class);
	
	/**显示转让通知发送的人员列表*/
	public static void showNoticeSuperPre(){
		List<t_transfer_notice> trList=traNoticeDao.findAll();
		render(trList);
	}
	
	/**增加通知人到数据库*/
	public static void addNoticeSuper(String noticeName,String noticeMobile,Integer noticeType,Integer notice_temp){
		t_transfer_notice trNotice=new t_transfer_notice();
		
		trNotice.notice_name=noticeName;
		trNotice.notice_mobile=noticeMobile;
		trNotice.notice_type=noticeType;
		trNotice.notice_temp = notice_temp;
		trNotice.save();
		
		showNoticeSuperPre();	
	}
	
	/**去修改通知人信息*/
	public static void toEditNoticeSuperPre(Long noticeId){
		t_transfer_notice trNotice=traNoticeDao.findByID(noticeId);
		render(trNotice);		
	}
	
	/**修改通知人信息*/
	public static void editNoticeSuper(Long noticeId,String noticeName,String noticeMobile,Integer notice_temp){
		t_transfer_notice trNotice=traNoticeDao.findByID(noticeId);
		trNotice.notice_name = noticeName;
		trNotice.notice_mobile = noticeMobile;
		trNotice.notice_temp = notice_temp;
		trNotice.save();
		
		showNoticeSuperPre();
	}
	
	/**删除这个通知人*/
	public static void deleteNoticeSuperPre(Long noticeId){

		traNoticeDao.delete(noticeId);
		
		showNoticeSuperPre();
	}
	
}
