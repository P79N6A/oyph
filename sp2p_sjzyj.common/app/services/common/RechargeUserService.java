package services.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_recharge_user;
import services.base.BaseService;
import common.enums.Client;
import common.utils.Factory;
import common.utils.PageBean;
import daos.common.RechargeUserDao;

public class RechargeUserService extends BaseService<t_recharge_user> {

	protected RechargeUserDao rechargeUserDao = Factory.getDao(RechargeUserDao.class);
	
	protected RechargeUserService() {
		super.basedao = this.rechargeUserDao;
		
	}
	
	/**
	 * 添加充值记录
	 * @param userId 用户Id
	 * @param serviceOrderNo 业务订单号
	 * @param rechargeAmt 提现金额
	 * @param summary 备注
	 * @param client 登录端
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月19日
	 *
	 */
	public boolean addUserRecharge(long userId, String serviceOrderNo,
			double rechargeAmt, String summary, Client client) {
		t_recharge_user rechargeUser = new t_recharge_user();
		
		rechargeUser.time = new Date();
		rechargeUser.order_no = serviceOrderNo;
		rechargeUser.user_id = userId;
		rechargeUser.amount = rechargeAmt;
		rechargeUser.summary = summary;
		rechargeUser.setStatus(t_recharge_user.Status.FAILED);  //先失败后成功
		rechargeUser.setClient(client);
		
		return rechargeUserDao.save(rechargeUser);
	}
	
	/**
	 * 添加代扣充值记录
	 * @param userId 用户Id
	 * @param serviceOrderNo 业务订单号
	 * @param rechargeAmt 提现金额
	 * @param summary 备注
	 * @param client 登录端
	 * @param 银行名称
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年11月06日
	 *
	 */
	public boolean addUserRechargeS(long userId, String serviceOrderNo,
			double rechargeAmt, String summary, Client client, String bankName, String bankAccount) {
		t_recharge_user rechargeUser = new t_recharge_user();
		
		rechargeUser.time = new Date();
		rechargeUser.order_no = serviceOrderNo;
		rechargeUser.user_id = userId;
		rechargeUser.amount = rechargeAmt;
		rechargeUser.summary = summary;
		rechargeUser.setStatus(t_recharge_user.Status.FAILED);  //先失败后成功
		rechargeUser.setClient(client);
		rechargeUser.setBankName(bankName);
		rechargeUser.setBankAccount(bankAccount);
		
		return rechargeUserDao.save(rechargeUser);
	}
	
	
	
	/**
	 * 充值记录
	 * @param currPage
	 * @param pageSize
	 * @param userId
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月16日
	 *
	 */
	public PageBean<t_recharge_user> pageOfDealUser(int currPage,int pageSize, long userId) {

		return rechargeUserDao.pageOfRecharge(currPage, pageSize, userId);
	}

	/**
	 * 统计用户充值次数
	 *
	 * @param userid
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月17日
	 */
	public int countDealOfUser(long userid,t_recharge_user.Status status){
		int count = rechargeUserDao.countByColumn(" user_id=? and status=?", userid,status.code);
		
		return count;
	}
	
	/**
	 * 充值成功，更新相关数据
	 * 
	 * @param serviceOrderNo
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月19日
	 *
	 */
	public int updateRechargeToSuccess(String serviceOrderNo) {

		return rechargeUserDao.updateRechargeToSuccess(serviceOrderNo);
	}
	
	/**
	 * 网银充值成功，更新相关数据
	 * 
	 * @param serviceOrderNo
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年10月26日
	 *
	 */
	public int updateRechargeToSuccesses(String serviceOrderNo) {

		return rechargeUserDao.updateRechargeToSuccesses(serviceOrderNo);
	}
	
	/**
	 * 获取充值金额数据
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月23日
	 *
	 */
	public double findTotalRechargeByDate(String startTime, String endTime,
			int type) {

		return rechargeUserDao.findTotalRechargeByDate(startTime, endTime, type);
	}
	
	
	/**
	 * 指定充值时间内，某用户充值成功的金额
	 * @param startTime
	 * @param endTime
	 * @param userid
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年7月26日
	 */
	public double findUseridTotalRechargeByDate(String startTime, String endTime,long userId) {

		return rechargeUserDao.findUseridTotalRechargeByDate(startTime, endTime, userId);
	}
	
	/**
	 * 指定充值时间内，某用户某银行卡充值成功的金额
	 * @param startTime
	 * @param endTime
	 * @param userid
	 * @param bankName
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年11月06日
	 */
	public double findUseridBankNameTotalRechargeByDate(String startTime, String endTime,long userId, String bankName, String bankAccount) {

		return rechargeUserDao.findUseridBankNameTotalRechargeByDate(startTime, endTime, userId, bankName, bankAccount);
	}
	
	
	
	/**
	 * 更新充值备注
	 * 
	 * @author LiuPengwei
	 * @createDate  2017年9月29日
	 *
	 */
	public int updateSummary(String orderNo, String summary) {

		return rechargeUserDao.updateSummary(orderNo, summary);
	}
	
	
	/**
	 * 更新充值备注
	 * 
	 * @author LiuPengwei
	 * @createDate  2017年9月29日
	 *
	 */
	public int updateSummaryS(String orderNo, String summary) {

		return rechargeUserDao.updateSummaryS(orderNo, summary);
	}
	
	
}
