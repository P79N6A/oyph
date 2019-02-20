package services.common;

import java.util.List;

import common.utils.Factory;
import daos.base.BaseDao;
import daos.common.TransferNoticeDao;
import models.common.entity.t_transfer_notice;
import services.base.BaseService;

public class TransferNoticeService extends BaseService<t_transfer_notice>{
	
	protected static TransferNoticeDao trNoticeDao=Factory.getDao(TransferNoticeDao.class);
	/**
	 * 
	 * @Title: findRepayNotice
	 * 
	 * @description 查询还款通知人电话
	 * @return List<t_transfer_notice>    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年2月1日-上午11:16:57
	 */
	public static List<t_transfer_notice> findRepayNotice(){
		 return trNoticeDao.findRepayNotice();
	}
}
