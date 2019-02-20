package services.ext.redpacket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.zxing.Result;

import payment.impl.PaymentProxy;
import common.FeeCalculateUtil;
import common.constants.ConfConst;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.common.DealUserDao;
import daos.core.BidDao;
import daos.core.InvestDao;
import daos.ext.redpacket.AddRateActDao;
import daos.ext.redpacket.AddRateTicketDao;
import daos.ext.redpacket.AddRateUserDao;
import models.common.entity.t_deal_platform;
import models.common.entity.t_deal_user;
import models.common.entity.t_user_fund;
import models.core.entity.t_bid;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import models.ext.redpacket.bean.AddRateTicketBean;
import models.ext.redpacket.bean.MaketInvestAddBean;
import models.ext.redpacket.bean.MaketInvestRedBean;
import models.ext.redpacket.entity.t_add_rate_act;
import models.ext.redpacket.entity.t_add_rate_ticket;
import models.ext.redpacket.entity.t_add_rate_user;
import models.ext.redpacket.entity.t_coupon_user;
import net.sf.json.JSONObject;
import play.Logger;
import services.base.BaseService;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.UserFundService;
import sun.security.krb5.internal.Ticket;
import yb.YbConsts;
import yb.YbUtils;
import yb.enums.EntrustFlag;
import yb.enums.ServiceType;

/**
 * 业务类：加息券
 * 
 * @author niu
 * @create 2017.10.27
 */
public class AddRateTicketService extends BaseService<t_add_rate_ticket> {

	protected static AddRateActDao addRateActDao = Factory.getDao(AddRateActDao.class);
	protected static AddRateUserDao addRateUserDao = Factory.getDao(AddRateUserDao.class);
	protected static DealUserDao dealUserDao = Factory.getDao(DealUserDao.class);
	protected static InvestDao investDao = Factory.getDao(InvestDao.class);
	protected static BidDao bidDao = Factory.getDao(BidDao.class);
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);
	protected static DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
	
	protected AddRateTicketDao addRateTicketDao = null;
	
	protected AddRateTicketService() {
		this.addRateTicketDao = Factory.getDao(AddRateTicketDao.class);
		super.basedao = addRateTicketDao;
	}
	
	/**
	 * 摇一摇活动获得加息卷
	 * 
	 * @param apr
	 * @param useRule
	 * @param userId
	 * @param activityId
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-14
	 */
	public boolean getAddRateTicketShakeActivity(double apr, double useRule, long userId, long activityId) {
		
		t_add_rate_ticket ticket = addRateTicket(apr / 100.0, 30, 0, 0, useRule, activityId, 1);
		if (ticket == null) {
			return false;
		}
		
		Date nowDate = new Date();
		
		Date etime = DateUtil.add(nowDate, Calendar.DAY_OF_YEAR, ticket.day);
		
		t_add_rate_user rateUser = new t_add_rate_user();
		
		rateUser.stime = nowDate;
		rateUser.etime = etime;
		rateUser.ticket_id = ticket.id;
		rateUser.user_id = userId;
		rateUser.status = 1;
		
		t_add_rate_user rate_user = rateUser.save();
		
		return rate_user != null ? true : false;
	}
	
	/**
	 * 后台-推广-加息券添加
	 * 
	 * @author niu
	 * @create 2017.10.26
	 */
	public t_add_rate_ticket addRateTicket(double apr, int day, double large, double small, double amount, long actId, int type) {
		
		t_add_rate_ticket ticket = new t_add_rate_ticket();
		
		ticket.apr = apr;
		ticket.day = day;
		ticket.large = large;
		ticket.small = small;
		ticket.amount = amount;
		ticket.act_id = actId;
		ticket.type = type;
		
		return ticket.save();
	}
	

	/**
	 * 查询所有 加息券
	 * 
	 * @author niu
	 * @create 2017.11.1
	 */
	public List<t_add_rate_ticket> listOfAddRateTicket() {
			
		return addRateTicketDao.listOfAddRateTicket();
	}
	
	/**
	 * 获得加息券
	 * 
	 * @author niu
	 * @create 2017.11.02
	 */
	public ResultInfo getTicket(long userId) {
		
		ResultInfo result = new ResultInfo();
		
		//查询有无加息券活动，有返回活动
		t_add_rate_act act = addRateActDao.getAct();
		
		if (act == null) {
			result.code = -1;
			result.msg  = "暂无加息券活动";
			
			return result;
		}
		
		//查询活动期间充值累计金额
		double totalAmount = dealUserDao.findRechargeTotalAmtByTime(userId, act.stime);
		
		//查询加息券活动的加息券
		List<t_add_rate_ticket> tickets = addRateTicketDao.listOfAddRateTicketByActId(act.id);
		
		if (tickets == null || tickets.size() <= 0) {
			result.code = -1;
			result.msg  = "未查询到加息券";
			
			return result;
		}
		
		Date nowDate = new Date();
		
		//获取加息券
		for (t_add_rate_ticket ticket : tickets) {
			
			t_add_rate_user havedTicket = addRateUserDao.getRateUserByUserIdAndTicketId(userId, ticket.id);
			if (havedTicket == null) {
				Boolean isAdd = false;
				
				if (ticket.large == -1 && totalAmount >= ticket.small) {
					isAdd = true;
				}
				if (ticket.large != -1 && totalAmount >= ticket.small && totalAmount <= ticket.large) {
					isAdd = true;
				}
				if (ticket.large != -1 && totalAmount >= ticket.large) {
					isAdd = true;
				}
				if (isAdd) {
					//加息券有效期
					Date etime = DateUtil.add(nowDate, Calendar.DAY_OF_YEAR, ticket.day);
					
					t_add_rate_user rateUser = new t_add_rate_user();
					
					rateUser.stime = nowDate;
					rateUser.etime = etime;
					rateUser.ticket_id = ticket.id;
					rateUser.user_id = userId;
					rateUser.status = 1;
					
					rateUser.save();
				}
			} 
		}
		
		result.code = 1;
		result.msg  = "";
		
		return result;
	}
	
	/**
	 * 查询 - 加息券 （根据用户Id）
 	 * 
	 * @author niu
	 * @create 2017.11.02
	 */
	public List<AddRateTicketBean> listOfAddRateTicketByUserId(long userId) {
		
		return addRateTicketDao.listOfAddRateTicketByUserId(userId);
	}
	
	/**
	 * 查询加息券（根据用户Id和加息券状态）
	 * 
	 * @author niu
	 * @createDate 2017.11.03
	 */
	public List<AddRateTicketBean> listOfTicketByUserIdAndStatus(long userId, int status) {
		
		/*List<AddRateTicketBean> ticketBeans = addRateTicketDao.listOfTicketByUserIdAndStatus(userId, status);
		
		if (ticketBeans == null || ticketBeans.size() <= 0) {
			return null;
		}
		
		for (AddRateTicketBean ticketBean : ticketBeans) {
			
			if (ticketBean.day <= 0) {
				System.out.println(ticketBean.day);
				t_add_rate_user rateUser = addRateUserDao.findByID(ticketBean.id);
				if (rateUser != null) {
					rateUser.status = 3;
					addRateUserDao.save(rateUser);
				}
				
				ticketBeans.remove(ticketBean);
			}
		}*/
		
		
		

		return addRateTicketDao.listOfTicketByUserIdAndStatus(userId, status);
	}
	
	/**
	 * 使用查询加息券（准备）
	 * 
	 * @author niu
	 * @param period_unit 
	 * @param period 
	 * @createDate 2017.11.03
	 */
	public ResultInfo checkAddRateTicket(long userId, long ticketId, double investAmount, int period, int period_unit) {
		
		ResultInfo result = new ResultInfo();
		
		t_add_rate_user ticket = addRateUserDao.findByID(ticketId);
		if (ticket == null || ticket.user_id != userId) {
			result.code = -1;
			result.msg  = "加息券不存在";
			
			return result;
		}
		
		//期限单位为天
		if (period_unit == 1){
			result.code = -3 ;
			result.msg = "使用加息券标的期限不满足" ;
			
			return result ;
		}
		
		//标的期限大于红包使用期限
		if (ticket.bid_period > period){
			result.code = -3 ;
			result.msg = "使用加息券标的期限不满足"+ticket.bid_period+"个月以上" ;
			
			return result ;
		}
		
		if(DateUtil.isDateAfter(new Date(), ticket.etime)){
			result.code = -3 ;
			result.msg = "投资使用的加息券已过使用期" ;
			
			addRateUserDao.updateAddRateTicketStatus(ticketId, 4);
			
			return result ;
		}
		
		if (ticket.status != 1) {
			result.code = -1;
			result.msg  = "加息券状态错误";
			
			return result;
		}
		
		result.code = 1;
		result.msg  = "加息券检验成功";
		
		return result;
	}
	
	/**
	 * 更新加息券状态
	 * 
	 * @author niu
	 * @createDate 2017.11.03
	 */
	public ResultInfo updateTicketStatus(long ticketId, long investId) {
		
		ResultInfo result = new ResultInfo();
		
		t_add_rate_user rateUser = addRateUserDao.findByID(ticketId);
		
		if (rateUser == null) {
			result.code = -1;
			result.msg  = "加息券不存在";
			
			return result;
		}
		
		rateUser.status = 3;
		rateUser.invest_id = investId;
		
		rateUser.save();
		
		result.code = 1;
		result.msg  = "加息券状态修改成功";
		
		return result;
	}
	
	/**
	 * 更新加息券状态（根据投标Id和用户Id）
	 * 
	 * @author niu
	 * @createDate 2017.11.03
	 */
	public int updateStatusByInvestIdAndUserId(long investId, long userId, int status) {

		return addRateUserDao.updateStatusByInvestIdAndUserId(investId, userId, status);
	}
	
	/**
	 * 定时清除过期的加息券
	 * @return
	 */
	public int updateStatusByTime() {
		return addRateUserDao.updateStatusByTime();
	}
	
	/**
	 * 加息券返息
	 * 
	 * @author niu
	 * @createDate 2017.11.06
	 */
	public ResultInfo returnRateOfAddRate(long userId, long investId, t_bill_invest billInvest) {
		ResultInfo result = new ResultInfo();
		
		//是否使用加息券
		t_add_rate_ticket ticket = addRateTicketDao.getTicket(userId, investId, 3);
		if (ticket == null) {
			result.code = -1;
			result.msg  = "没有使用加息券";
			
			return result;
		}
		
		//查询投标记录
		t_invest invest = investDao.findByID(investId);
		if (invest == null) {
			result.code = -1;
			result.msg  = "查询投标记录失败！";
			
			return result;
		}
		
		//查询标的信息
		t_bid bid = bidDao.findByID(invest.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg  = "查询标的信息失败！";
			
			return result;
		}
		
		//计算返息
		double interest = invest.amount * ticket.apr / 12.00 / 100.00;
		interest = interest - FeeCalculateUtil.getOriginalInvestManagerFee(interest, bid.service_fee_rule);
		
		
		//接口参数
		Map<String, Object> accMap = new HashMap<String, Object>();
		accMap.put("oderNo", "0");
		accMap.put("oldbusinessSeqNo", "");
		accMap.put("oldOderNo", "");
		accMap.put("debitAccountNo", YbConsts.MARKETNO);
		accMap.put("cebitAccountNo", userId);
		accMap.put("currency", "CNY");
		accMap.put("amount", YbUtils.formatAmount(interest));
		accMap.put("summaryCode", "T10");
		
		List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accMap);
		
		Map<String, Object> conMap = new HashMap<String, Object>();
		conMap.put("oderNo", "0");
		conMap.put("contractType", "");
		conMap.put("contractRole", "");
		conMap.put("contractFileNm", "");
		conMap.put("debitUserid", "");
		conMap.put("cebitUserid", "");
		
		List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();
		contractList.add(conMap);
		
		if (ConfConst.IS_TRUST) {
			String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.MARKET);
			
			//接口
			result = PaymentProxy.getInstance().fundTrade(OrderNoUtil.getClientSn(), userId, businessSeqNo, bid, ServiceType.MARKET, EntrustFlag.UNENTRUST, accountList, contractList);
			if (result.code < 0) {
				LoggerUtil.info(false, "userId:" + userId + "investId:" + investId + "bidId:" + bid.id + "ticketId:" + ticket.id);
			}
			
			//用户签名是否通过
			boolean isSignSuccess = true;  
			result = userFundService.userFundSignCheck(userId);
			if (result.code < 1) {
				isSignSuccess = false;
			}
			
			//用户增加金额
			boolean addFund = userFundService.userFundAdd(userId, interest);
			if (!addFund) {
				result.code = -1;
				result.msg = "增加理财人可用余额失败"; 
				
				return result;
			}
			
			//刷新理财人资金信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(userId);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "获取理财资金信息失败";
				
				return result;
			}
			
			//如果投资用户账户资金没有遭到非法改动，那么就更新其篡改标志，否则不更新
			if(isSignSuccess){
				userFundService.userFundSignUpdate(userId);
			}
			
			//添加理财人收款记录
			Map<String, String> summaryParam = new HashMap<String, String>();
			summaryParam.put("billInvestNo", billInvest.bill_invest_no);
			boolean addDeal = dealUserService.addDealUserInfo(businessSeqNo, userId, 0.00 + interest, 0.00, 0.00, t_deal_user.OperationType.RECEIVE_ADD_RATE, summaryParam);//此时不计“理财服务费”和“逾期罚息”
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加理财人收款记录失败";
				
				return result;
			}
			
			//添加平台收支记录
			Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
			dealRemarkParam.put("bill_invest_no", billInvest.bill_invest_no);
			
			boolean addPlatDeal = dealPlatformService.addPlatformDeal(userId, interest, t_deal_platform.DealRemark.PLATFORM_DEAL_ADD_RATE, dealRemarkParam);
			if (!addPlatDeal) {
				result.code = -1;
				result.msg = "添加平台收支记录失败";
				
				return result;
			}

		}
		
		result.code = 1;
		result.msg = "加息券返息成功";
		
		return result;
	}
	
	/**
	 * 查询已使用的加息券
	 * 
	 * @param investId
	 * @param userId
	 * @param status
	 * @return
	 */
	public AddRateTicketBean findTicketByUserId(long investId, long userId, int status) {
		
		return addRateUserDao.findTicketByUserId(investId, userId, status);
	}
	
	/**
	 * 后台红包发放记录分页查询
	 * 
	 * @param showType 
	 * @param currPage 当前页码
	 * @param pageSize 显示条数
	 * @return
	 * 
	 * @author LiuPengwei
	 * @param endTime 
	 * @param startTime 
	 * @param userName 
	 * @create 2018年5月21日16:46:11
	 */
	public PageBean<MaketInvestAddBean> pageOfMaketAddRate(int currPage, int pageSize,int showType,int orderType,int orderValue,
			String userName, String startTime, String endTime) {
		
		PageBean<MaketInvestAddBean> pageBean = addRateUserDao.pageOfMaketAddRate(currPage, pageSize,showType, orderType, orderValue,userName, startTime, endTime);

		return pageBean;
	}
	
	
	
	/** 
	 * 查询某状态的加息券个数
	 * @param status
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月30日
	 */
	public int findRateUserStatus(int status) {
		
		return addRateUserDao.findRateUserStatus(status);
	}
	
}
