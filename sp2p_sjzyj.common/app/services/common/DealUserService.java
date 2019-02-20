package services.common;

import java.util.Date;
import java.util.Map;

import models.common.bean.RechargeRecord;
import models.common.bean.WithdrawalRecord;
import models.common.entity.t_deal_user;
import services.base.BaseService;

import common.utils.Factory;
import common.utils.PageBean;

import daos.common.DealUserDao;

/**
 * 用户交易记录service接口实现
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月21日
 */
public class DealUserService extends BaseService<t_deal_user> {

	protected DealUserDao dealUserDao = Factory.getDao(DealUserDao.class);;
	
	protected DealUserService() {
		super.basedao = this.dealUserDao;
	}
	
	/**
	 * 添加一条用户交易信息
	 *
	 * @param orderNo 订单号
	 * @param userId 用户ID
	 * @param amount 交易金额
	 * @param balance 可用余额
	 * @param freeze 冻结金额
	 * @param dealType 交易类型
	 * @param operation 操作类型枚举
	 * @param summaryParam 备注参数
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public boolean addDealUserInfo(String orderNo, long userId, double amount,
			double balance, double freeze, t_deal_user.OperationType operationType,
			Map<String, String> summaryParam) {
		t_deal_user dealUser = new t_deal_user();
		dealUser.time = new Date();
		dealUser.user_id = userId;
		dealUser.order_no = orderNo;
		dealUser.amount = amount;
		dealUser.balance = balance;
		dealUser.freeze = freeze;
		dealUser.setOperation_type(operationType, summaryParam);

		return dealUserDao.save(dealUser);
	}

	/**
	 * 查询交易记录
	 *
	 * @param pageSize  页码
	 * @param currPage 当前页
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月21日
	 */
	public PageBean<t_deal_user> pageOfDealUser(int currPage, int pageSize, long userId) {
		
		return dealUserDao.pageOfDealUser(currPage, pageSize, userId);
	}

	/**
	 * 充值记录（后台,交易记录表中获取）
	 *
	 * @param pageSize
	 * @param currPage
	 * @param exports 1：导出  default:不导出
	 * @param name 会员昵称
	 * @return
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 */
	public PageBean<RechargeRecord> pageOfRechargeRecord(int currPage ,int pageSize, int exports, String name) {
		
		return dealUserDao.pageOfRechargeRecord(currPage, pageSize,exports,name);
	}

	/**
	 * 提现记录（后台,交易记录表中获取）
	 * 
	 * @param pageSize
	 * @param currPage
	 * @param exports 1:导出  default：不导出
	 * @param name 会员
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 */
	public PageBean<WithdrawalRecord> pageOfWithdrawalRecordBack(int currPage,int pageSize,int exports,String name) {
		
		return dealUserDao.pageOfWithdrawalRecordBack(currPage, pageSize, t_deal_user.OperationType.WITHDRAW,exports,name);
	}
	
	/**
	 * 查询提现总额
	 *
	 * @param name 会员昵称
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月19日
	 */
	public double findWithdrawalTotalAmt(String name) {
		
		return dealUserDao.findTransactionlTotalAmt(t_deal_user.OperationType.WITHDRAW, name);
	}

	/**
	 * 查询充值总额
	 *
	 * @param name 会员昵称
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月19日
	 */
	public double findRechargeTotalAmt(String name) {
		//账户直充
		double amt1 = dealUserDao.findTransactionlTotalAmt(t_deal_user.OperationType.RECHARGE,name);
		//闪电快充
		double amt2 = dealUserDao.findTransactionlTotalAmt(t_deal_user.OperationType.RECHARGE_QUICK,name);
		
		return amt1+amt2;
	}
	
	/**
	 * 查询某一时间段的充值总额
	 * 
	 * @author niu
	 * @createDate 2017.11.04
	 */
	public double findRechargeTotalAmtByTime(long userId, Date stime) {
		
		return dealUserDao.findRechargeTotalAmtByTime(userId, stime);
	}
	
	
	
}
