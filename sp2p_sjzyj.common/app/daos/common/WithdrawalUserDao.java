package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_deal_user;
import models.common.entity.t_recharge_user;
import models.common.entity.t_withdrawal_user;
import common.utils.PageBean;
import daos.base.BaseDao;

/**
 * 提现记录dao实现
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月21日
 */
public class WithdrawalUserDao extends BaseDao<t_withdrawal_user> {
	
	protected WithdrawalUserDao() {}

	/**
	 * 提现记录表
	 * 
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月21日
	 */
	public PageBean<t_withdrawal_user> pageOfWithdrawal(int currPage, int pageSize,long userId) {
		
		return super.pageOfByColumn(currPage, pageSize, "user_id = ? ORDER BY id DESC", userId);
	}

	/**
	 * 提现成功，更新相关数据
	 *
	 * @param orderNo
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月11日
	 */
	public int updateWithdrawalToSuccess(String orderNo) {
		String sql = "UPDATE t_withdrawal_user SET status = :status, completed_time = :time, summary=:summary WHERE order_no = :orderNo AND status <> :status";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_recharge_user.Status.SUCCESS.code);
		params.put("time", new Date());
		params.put("orderNo", orderNo);
		params.put("summary", t_deal_user.OperationType.WITHDRAW.value);
		
 		return updateBySQL(sql, params);
	}
	
	/**
	 * 提现失败，更新相关数据
	 *
	 * @param orderNo  业务流水号
	 * @param summary  备注
	 * @return
	 *
	 * @author Liupengwei
	 * @createDate 2017年10月9日
	 */
	public int updateSummary(String orderNo, String summary) {
		String sql = "UPDATE t_withdrawal_user SET status=:status, summary=:summary WHERE order_no = :orderNo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_recharge_user.Status.FAILED.code);
		params.put("orderNo", orderNo);
		params.put("summary", summary);
		
 		return updateBySQL(sql, params);
	}
	
	/**
	 * 提现处理中，更新相关数据
	 *
	 * @param orderNo  业务流水号
	 * @param summary  备注
	 * @return
	 *
	 * @author Liupengwei
	 * @createDate 2017年10月9日
	 */
	public int updateSummaryS(String orderNo, String summary) {
		String sql = "UPDATE t_withdrawal_user SET status=:status, summary=:summary WHERE order_no = :orderNo";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", t_recharge_user.Status.HANDING.code);
		params.put("orderNo", orderNo);
		params.put("summary", summary);
		
 		return updateBySQL(sql, params);
	}
}
