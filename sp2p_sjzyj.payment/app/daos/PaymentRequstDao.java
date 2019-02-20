package daos;

import models.common.entity.t_user_fund;
import models.entity.t_payment_request;

import java.util.HashMap;
import java.util.Map;

import com.shove.Convert;

import daos.base.BaseDao;

/**
 * 托管系统请求记录
 * 
 * @author niu
 * @create 2017.08.23
 */
public class PaymentRequstDao extends BaseDao<t_payment_request> {

	/**
	 * 请求处理成功，更新请求状态，并防止重复回调
	 *
	 * @param requestMark
	 *
	 * @author LiuPengwei
	 * @createDate 2017年9月11日
	 */
	public int updateReqStatusToSuccess(String requestMark) {
		String sql = "UPDATE t_payment_request SET status = :status WHERE service_order_no = :requestMark AND status <> :status AND service_type<>19";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_payment_request.Status.SUCCESS.code);
		params.put("requestMark", requestMark);
		
		return updateBySQL(sql, params);
		
	}
	
	/**
	 * 查询业务类型，除校验密码
	 *
	 * @param requestMark
	 *
	 * @author LiuPengwei
	 * @createDate 2017年9月11日
	 */
	public t_payment_request findType(String requestMark) {
		String sql = "SELECT * FROM t_payment_request WHERE service_order_no = :requestMark and service_type <>19";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("requestMark", requestMark);
		
		return this.findBySQL(sql, condition);
		
	}
	
	/**
	 * 请求处理成功，更新请求状态，并防止重复回调
	 *
	 * @param businessSeqNo
	 *
	 * @author liuyang
	 * @createDate 2017年9月23日
	 */
	public int updateReqStatusToSuccessByBusinessSeqNo(String businessSeqNo) {
		String sql = "UPDATE t_payment_request SET status = :status WHERE service_order_no = :businessSeqNo AND status <> :status AND service_type <>19";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_payment_request.Status.SUCCESS.code);
		params.put("businessSeqNo", businessSeqNo);
		
		return updateBySQL(sql, params);
		
	}
	
	/**
	 * 根据客户端流水号 -> 查询请求记录
	 * 
	 * @author niu
	 * @create 2017.10.09
	 */
	public t_payment_request findRequestRecord(String clientSn) {
		
		//sql语句
		String sql = "SELECT * FROM t_payment_request t WHERE t.mark = :clientSn";
		
		//查询条件
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("clientSn", clientSn);
		
		//返回查询结果
		return findBySQL(sql, condition);
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
		String sql="SELECT * FROM t_payment_request WHERE service_order_no =:service_order_no order by time desc";
		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("service_order_no", requestMark);
		
		return findBySQL(sql, condition);
	}
	
	/**
	 * 请求处理成功，更新请求状态，并防止重复回调
	 *
	 * @param requestMark
	 *
	 * @author LiuPengwei
	 * @createDate 2017年9月11日
	 */
	public int updateReqStatus(String requestMark) {
		String sql = "UPDATE t_payment_request SET status = :status WHERE mark = :requestMark AND status <> :status";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_payment_request.Status.SUCCESS.code);
		params.put("requestMark", requestMark);
		
		return updateBySQL(sql, params);
		
	}
	
}



























