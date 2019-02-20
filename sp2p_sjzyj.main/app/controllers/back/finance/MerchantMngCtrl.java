package controllers.back.finance;

import java.util.Date;
import java.util.Map;

import models.common.entity.t_cost;
import models.common.entity.t_event_supervisor;
import payment.impl.PaymentProxy;
import play.mvc.With;
import services.common.SupervisorService;
import yb.YbConsts;
import yb.enums.ServiceType;

import com.shove.Convert;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.enums.Client;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;

import controllers.common.BackBaseController;
import controllers.common.SubmitRepeat;

/**
 * 后台-财务-商户号管理-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
@With(SubmitRepeat.class)
public class MerchantMngCtrl extends BackBaseController {
	
	protected static SupervisorService supervisorService = Factory.getService(SupervisorService.class);

	/**
	 * 后台-财务-商户号管理-进入商户号管理
	 * @rightID 509001
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void toMerchantPre() {
		
		// 查询代偿账户可用余额
		String businessSeqNo1 = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);
		
		ResultInfo result1 = PaymentProxy.getInstance().queryCompensatoryBalance(Client.PC.code, businessSeqNo1);
		if (result1.code < 1) {
			flash.error("查询代偿账户可用余额异常");
			render();
		}
		
		Map<String, Object> merDetail1 = (Map<String, Object>) result1.obj;
		double merBalance = Convert.strToDouble(merDetail1.get("pBlance").toString(), 0);
		
		// 查询营销账户可用余额
		String businessSeqNo2 = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);
		
		ResultInfo result2 = PaymentProxy.getInstance().queryMarketBalance(Client.PC.code, businessSeqNo2);
		if (result2.code < 1) {
			flash.error("查询营销账户可用余额异常");
			render();
		}
		
		Map<String, Object> merDetail2 = (Map<String, Object>) result2.obj;
		double marBalance = Convert.strToDouble(merDetail2.get("pBlance").toString(), 0);
		
		// 查询费用账户可用余额
		String businessSeqNo3 = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);
		
		ResultInfo result3 = PaymentProxy.getInstance().queryCostBalance(Client.PC.code, businessSeqNo3);
		if (result3.code < 1) {
			flash.error("查询费用账户可用余额异常");
			render();
		}
		
		Map<String, Object> merDetail3 = (Map<String, Object>) result3.obj;
		double cosBalance = Convert.strToDouble(merDetail3.get("pBlance").toString(), 0);
		
		render(merBalance, marBalance, cosBalance);
	}
	
	/**
	 * 后台-财务-商户号管理-充值页面
	 * @rightID 509002
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月20日
	 */
	@SubmitCheck
	public static void toMerchantRechargePre(int flag) {
		render(flag);
	}
	
	/**
	 * 商户充值
	 * @rightID 509002
	 *
	 * @param rechargeAmt 充值金额
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月27日
	 */
	@SubmitOnly
	public static void merchantRecharge (double rechargeAmt, int flag) {
		checkAuthenticity();
		
		if (rechargeAmt <= 0) {
			flash.error("充值金额必须大于0");
			
			toMerchantRechargePre(flag);
		}
		
		boolean addEvent;
		String serviceOrderNo;
		
		//根据标识判断添加的管理员事件
		if(flag == 1) {
			addEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(),  t_event_supervisor.Event.COMPENSATORY_RECHARGE, null);
			serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.COMPENSATORY_RECHARGE);
		}else if(flag == 2) {
			addEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(),  t_event_supervisor.Event.MARKET_RECHARGE, null);
			serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.MARKET_RECHARGE);
		}else {
			addEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(),  t_event_supervisor.Event.COST_RECHARGE, null);
			serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.COST_RECHARGE);
		}
		
		if (!addEvent) {
			flash.error("添加管理员事件失败");
			
			toMerchantRechargePre(flag);
		}				
		
		ResultInfo result = PaymentProxy.getInstance().merchantRecharge(Client.PC.code, serviceOrderNo, rechargeAmt, flag);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			toMerchantRechargePre(flag);
		}
		
		if(flag == 3) {
			// 查询费用账户可用余额
			String businessSeqNo3 = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);
			
			ResultInfo result3 = PaymentProxy.getInstance().queryCostBalance(Client.PC.code, businessSeqNo3);
			if (result3.code < 1) {
				flash.error("查询费用账户可用余额异常");
				toMerchantRechargePre(flag);
			}
			
			Map<String, Object> merDetail3 = (Map<String, Object>) result3.obj;
			double cosBalance = Convert.strToDouble(merDetail3.get("pBlance").toString(), 0);
			
			// 保存费用账户明细
			t_cost cost = new t_cost();
			cost.balance = cosBalance;
			cost.amount = rechargeAmt;
			cost.type = 0;
			cost.sort = 0;
			cost.time = new Date();
			cost.save();
		}
		
		toMerchantRechargePre(flag);
	}
	
	/**
	 * 后台-财务-商户号管理-商户提现页面
	 * @rightID 509003
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月20日
	 */
	@SubmitCheck
	public static void toMerchantWithdrawalPre(int flag) {
		render(flag);
	}
	
	/**
	 * 商户提现
	 * @rightID 509003
	 *
	 * @param withdrawalAmt 提现金额
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月27日
	 */
	@SubmitOnly
	public static void merchantWithdrawal (double withdrawalAmt, int flag) {
		checkAuthenticity();
		
		if (withdrawalAmt <= 0) {
			flash.error("提现金额必须大于0");
			
			toMerchantWithdrawalPre(flag);
		}
		
		boolean addEvent;
		String serviceOrderNo;
		
		//根据标识判断添加的管理员事件
		if(flag == 1) {
			addEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(),  t_event_supervisor.Event.COMPENSATORY_WITHDRAW, null);
			serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.COMPENSATORY_WITHDRAW);
		}else if(flag == 2) {
			addEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(),  t_event_supervisor.Event.MARKET_WITHDRAW, null);
			serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.MARKET_WITHDRAW);
		}else {
			addEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(),  t_event_supervisor.Event.COST_WITHDRAW, null);
			serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.COST_WITHDRAW);
		}
		
		ResultInfo result = PaymentProxy.getInstance().merchantWithdrawal(Client.PC.code, serviceOrderNo, withdrawalAmt, flag);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			toMerchantWithdrawalPre(flag);
		}
		
		if(flag == 3) {
			// 查询费用账户可用余额
			String businessSeqNo3 = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);
			
			ResultInfo result3 = PaymentProxy.getInstance().queryCostBalance(Client.PC.code, businessSeqNo3);
			if (result3.code < 1) {
				flash.error("查询费用账户可用余额异常");
				toMerchantRechargePre(flag);
			}
			
			Map<String, Object> merDetail3 = (Map<String, Object>) result3.obj;
			double cosBalance = Convert.strToDouble(merDetail3.get("pBlance").toString(), 0);
			
			// 保存费用账户明细
			t_cost cost = new t_cost();
			cost.balance = cosBalance;
			cost.amount = withdrawalAmt;
			cost.type = 1;
			cost.sort = 1;
			cost.time = new Date();
			cost.save();
		}
		
		toMerchantWithdrawalPre(flag);
	}

	
}
