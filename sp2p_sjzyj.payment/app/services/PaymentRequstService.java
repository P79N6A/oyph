package services;


import common.utils.Factory;
import daos.PaymentRequstDao;
import models.entity.t_payment_request;
import services.base.BaseService;

public class PaymentRequstService extends BaseService<t_payment_request> {
	
	protected static PaymentRequstDao paymentRequestDao = Factory.getDao(PaymentRequstDao.class);
	
	/**
	 * 请求处理成功，更新请求状态，并防止重复回调
	 *
	 * @param businessSeqNo
	 *
	 * @author liuyang
	 * @createDate 2017年9月23日
	 */
	public int updateReqStatusToSuccessByBusinessSeqNo(String businessSeqNo) {
		
		return paymentRequestDao.updateReqStatusToSuccessByBusinessSeqNo(businessSeqNo);
	}
	
	/**
	 * 根据客户端流水号 -> 查询请求记录
	 *  
	 * @param requestMark 业务流水号
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 *
	 */
	public t_payment_request queryByRequestMark(String requestMark) {
		
		return paymentRequestDao.queryByRequestMark(requestMark);
	}

}
