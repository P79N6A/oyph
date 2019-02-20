package services.core;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.common.entity.t_pact;
import models.common.entity.t_sms_jy_count;
import models.common.entity.t_template_notice;
import models.common.entity.t_template_pact;
import models.common.entity.t_transfer_notice;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.bean.BackDebtTransfer;
import models.core.bean.DebtInvest;
import models.core.bean.DebtTransfer;
import models.core.bean.DebtTransferDetail;
import models.core.bean.InvestReceive;
import models.core.bean.PactBid;
import models.core.bean.UserDebt;
import models.core.entity.t_bid;
import models.core.entity.t_debt_transfer;
import models.core.entity.t_invest;
import models.core.entity.t_product;
import models.ext.redpacket.entity.t_add_rate_ticket;
import models.ext.redpacket.entity.t_add_rate_user;
import models.ext.redpacket.entity.t_red_packet_user;
import services.base.BaseService;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.NoticeService;
import services.common.PactService;
import services.common.SettingService;
import services.common.SmsJyCountService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.ext.redpacket.AddRateTicketService;
import services.ext.redpacket.RedpacketUserService;
import common.constants.Constants;
import common.constants.SettingKey;
import common.enums.JYSMSModel;
import common.enums.NoticeScene;
import common.enums.PactType;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.JPAUtil;
import common.utils.LoggerUtil;
import common.utils.NoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import common.utils.TimeUtil;
import common.utils.number.Arith;
import daos.common.TransferNoticeDao;
import daos.core.DebtTransferDao;

public class DebtService  extends BaseService<t_debt_transfer>{

	protected DebtTransferDao debtTransferDao = null;
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static BidService bidService = Factory.getService(BidService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);

	protected static DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);
	
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);
	
	protected static BillService billService = Factory.getService(BillService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static PactService pactService = Factory.getService(PactService.class);
	
	protected static TransferNoticeDao traNoticeDao=Factory.getDao(TransferNoticeDao.class);
	
	protected static RedpacketUserService redpacketUserService = Factory.getService(RedpacketUserService.class);
	
	protected static AddRateTicketService addRateTicketService = Factory.getService(AddRateTicketService.class);
	
	protected static SmsJyCountService smsJyCountService = Factory.getService(SmsJyCountService.class);
	
	protected DebtService() {
		debtTransferDao	= Factory.getDao(DebtTransferDao.class);
		super.basedao = this.debtTransferDao;
	}
	
	/**
	 * 
	 *
	 * @param debtId 债权id
	 * @param transferPeriod 债权期数
	 * @param managerfee 转让管理费
	 * @param toUserName 受让人名称
	 * @param debtId 转让人名称
	 * @param newInvestId 新的投资id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月1日
	 */
	public ResultInfo creatDebtPact(Long debtId,int transferPeriod,double managerfee,String toUserName,String fromUserName,long newInvestId){
		ResultInfo result = new ResultInfo();
		
		DecimalFormat myformat = new DecimalFormat();
		myformat.applyPattern("0.00");
		
		DebtTransferDetail detail = findDebtDetailById(debtId);
		t_invest invest = investService.findByID(detail.invest_id);
		PactBid pactBid = bidService.findPactBidById(invest.bid_id);
		t_template_pact temp = pactService.findByType(PactType.DEBT);
		
		Map<String, String> contentParam = new HashMap<String, String>();
		//合同名称:pact_name
		contentParam.put("pact_name", temp.name);
		
		//合同编号:pact_no
		String pact_no = NoUtil.getPactNo(debtId, new Date());
		contentParam.put("pact_no", pact_no);
		
		//合同数据组装
		
		//债权转让人:fromUserName
		contentParam.put("fromUserName", fromUserName);
		
		//债权受让人:toUserName
		contentParam.put("toUserName", toUserName);
		
		//借款编号 bid_no
		contentParam.put("bid_no", pactBid.getBid_no());
		
		//借款人真实姓名loan_real_name
		contentParam.put("loan_real_name", pactBid.reality_name);

		//借款人平台用户名:loan_name
		contentParam.put("loan_name", pactBid.name);
				
		//借款人平台用户名:id_number
		contentParam.put("id_number", pactBid.id_number);
		
		//借款总额:bid_amount
		contentParam.put("bid_amount", myformat.format(pactBid.amount));
		
		//借款年利率:apr
		contentParam.put("apr", myformat.format(pactBid.apr));
		
		//借款期限:period_num
		contentParam.put("period_num", pactBid.period+"");
		
		//放款方式:repayment_type
		contentParam.put("repayment_type", pactBid.getRepayment_type().value);
		
		//借款开始日期:release_date
		contentParam.put("release_date", DateUtil.dateToString(pactBid.release_time, "yyyy年MM月dd日"));
						
		//借款到期日:last_repay_time
		contentParam.put("last_repay_time", DateUtil.dateToString(pactBid.getLast_repay_time(), "yyyy年MM月dd日"));
					
		//剩余借款本金:debt_principal
		contentParam.put("debt_principal", myformat.format(detail.debt_principal));
		
		//转让价格:transfer_price
		contentParam.put("transfer_price", myformat.format(detail.transfer_price));
		
		//转让期限:debt_principal
		contentParam.put("transfer_period", transferPeriod+"");
		
		//转让服务费率:transfer_fee_rate
		double percent = Double.valueOf(settingService.findSettingValueByKey(SettingKey.TRANSFER_FEE_RATE));
		contentParam.put("transfer_fee_rate",  myformat.format(percent));
		
		//转让管理费:manage_fee
		contentParam.put("manage_fee", myformat.format(managerfee));
		
		//转让时间:transaction_time
		contentParam.put("transaction_time", DateUtil.dateToString(new Date(), "yyyy年MM月dd日"));
		
		// 还款明细 last_repay_list
		List<InvestReceive> list = billInvestService.queryMyBillInvest(newInvestId);
		StringBuffer listBuffer = new StringBuffer("<table cellpadding='0' cellspacing='0' border='1' width='100%' style='text-align: center;'><tr><th colspan='5'>剩余还款明细</th></tr>")
				.append("<tr><th>期数</th><th>还款日期</th><th>应还本金（元）</th><th>应还利息（元）</th><th>应还总额（元）</th></tr>");
		for(InvestReceive receive : list){
			listBuffer.append("<tr>")
			.append("<td>").append(receive.period).append("/").append(pactBid.period).append("</td>")
			.append("<td>").append(DateUtil.dateToString(receive.receive_time, "yyyy年MM月dd日")).append("</td>")
			.append("<td>").append(myformat.format(receive.receive_corpus)).append("</td>")
			.append("<td>").append(myformat.format(receive.receive_interest)).append("</td>")
			.append("<td>").append(myformat.format(receive.corpus_interest)).append("</td>")
			.append("</tr>");
		}	
		listBuffer.append("</table>");
		contentParam.put("last_repay_list", listBuffer.toString());
		/** 合同生成时间 */
		Date time = new Date();
		String transfer_time = TimeUtil.dateToStrCurr(time);
		contentParam.put("transfer_time",transfer_time);
		
		temp.content = StrUtil.replaceByMap(temp.content, contentParam);
		
		t_pact pact = new t_pact();
		pact.time = new Date();
		pact.pid = debtId;
		pact.setType(PactType.DEBT);
		pact.content = temp.content;
		pact.image_url = temp.image_url;
		
		result = pactService.createPact(pact);
		return result;
	}

	/**
	 * 申请债权转让
	 *
	 * @param investId 投资id
	 * @param title 债权名称
	 * @param period 转让期限(需要在多久之内转出去)
	 * @param transferPrice 转让底价(50~100%的本金)
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public ResultInfo applyDebtTransfer(Long investId,String title,int period,int transferPrice){
		ResultInfo result = new ResultInfo();
		
		result = isInvestCanbeTransfered(investId);
		if (result.code < 1 ) {
			
			return result;
		}
		
		DebtInvest invest = this.findDebtByInvestid(investId);
		double half_principal = Arith.div(invest.debt_principal*8, 10, 0);//本金的80%
		if (transferPrice > invest.debt_amount || transferPrice < half_principal) {
			result.code = -3;
			result.msg = "债权底价输入有误";

			return result;
		}

		t_debt_transfer debt = new t_debt_transfer();
		debt.time = new Date();
		debt.invest_id = investId;
		debt.user_id = invest.user_id;
		debt.title = title;
		debt.debt_amount = invest.debt_amount;
		debt.debt_principal = invest.debt_principal;
		debt.transfer_price = transferPrice;
		debt.transfer_period = invest.period;
		debt.period = period;
		debt.setStatus(t_debt_transfer.Status.PREAUDITING);
		
		if (!debtTransferDao.save(debt)) {
			result.code = -1;
			result.msg = "添加债权转让记录失败";

			return result;
		}
		
		if (!investService.updateTransferStatus(investId, t_invest.TransferStatus.TRANSFERING)) {
			result.code = -1;
			result.msg = "更新投资记录状态失败";

			return result;
		}
		
		//申请成功 发送通知
		t_user user = userService.findByID(debt.user_id);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		param.put("debt_no", debt.debt_transfer_no);
		param.put("title", debt.title);
		
		//创蓝接口
		//noticeService.sendSysNotice(debt.user_id, NoticeScene.DEBT_APPLY_SUCC, param);
		//焦云接口
		Boolean eflag = smsJyCountService.judgeIsSend(user.mobile, JYSMSModel.MODEL_DEBT_APPLY_SUCC);
		if (eflag) {
			noticeService.sendSysNotice(debt.user_id, NoticeScene.DEBT_APPLY_SUCC, param,JYSMSModel.MODEL_DEBT_APPLY_SUCC);
		}
		
		result.code = 1;
		result.msg = "申请成功";
		
		/**
		 * author:lihuijun
		 * date:2017-2-9
		 */
		//创蓝接口
		/*List<t_template_notice> templates=noticeService.queryTemplatesByScene(NoticeScene.TRANSFER_APPLY_NOTICE);
		Map<String, String> param_format=StrUtil.mapToMap(param);
		if(templates.size()>0){
			for(t_template_notice tem:templates){
				
				String content = StrUtil.replaceByMap(tem.content,param_format);
				List<t_transfer_notice> superList=traNoticeDao.findAssNotice();
				for(t_transfer_notice sup:superList){
					noticeService .sendMsg(sup.notice_mobile, content);
				}
			}				
			
		}*/
		//焦云接口
		Map<String,String> strMap = new HashMap<String,String>();
		strMap.put("user_name", user.name);
		strMap.put("debt_no", debt.debt_transfer_no);
		//查询转让通知人
		List<t_transfer_notice> superList = traNoticeDao.findAssNotice();
		
		if (superList != null && superList.size() > 0) {
			for (t_transfer_notice transfer : superList) {
				
				if (transfer.notice_type == 0) {
					Boolean flag = smsJyCountService.judgeIsSend(transfer.notice_mobile, JYSMSModel.MODEL_TRANSFER_APPLY_NOTICE);
					if (flag) {
						noticeService.sendJYMess(transfer.notice_mobile, strMap, JYSMSModel.MODEL_TRANSFER_APPLY_NOTICE);
					}
				}
			}
		}
		
		return result;
	}
	

	/**
	 * 债权审核操作
	 * 
	 * @param debtTransferId 债权转让id
	 * @param supervisorId 审核管理员id
	 * @param status 审核结果(状态)
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月25日
	 *
	 */
	public ResultInfo auditDebtTransfer(long debtTransferId, long supervisorId, t_debt_transfer.Status status){
		ResultInfo result = new ResultInfo();
		
		t_debt_transfer debtTransfer = debtTransferDao.findByID(debtTransferId);
		//转让项目是否存在
		if (debtTransfer == null) {
			result.code = -1;
			result.msg = "对不起！审核失败！";

			return result;
		}
		
		t_invest invest = investService.findByID(debtTransfer.invest_id);
		if (invest == null) {
			result.code = -1;
			result.msg = "对不起！审核失败！";

			return result;
		}
		
		//非转让中的状态不能进行审核通过
		if ( !t_invest.TransferStatus.TRANSFERING.equals(invest.getTransfer_status()) ) {
			result.code = -1;
			result.msg = "非转让状态不能转让审核";

			return result;
		}
		
		//转让项目是否处于待审核状态
		if ( !t_debt_transfer.Status.PREAUDITING.equals(debtTransfer.getStatus()) ) {
			result.code = -1;
			result.msg = "该状态不能审核";

			return result;
		}

		Date start_time = new Date();
		Date end_time = DateUtil.addDay(new Date(),debtTransfer.period);
		
		t_user user = userService.findByID(debtTransfer.user_id);
		
		//审核通过操作
		if (status.code == t_debt_transfer.Status.AUCTING.code) {
			//审核操作通过，更新数据库表 t_debt_transfer
			int rowDebt = debtTransferDao.updateDebtTransferPass(start_time, end_time, t_debt_transfer.Status.AUCTING.code, supervisorId, debtTransferId);
			if (rowDebt < 1) {
				result.code = -1;
				result.msg = "审核通过操作失败";

				return result;
			}
			
			//更新转让人的投资表的转让状态
			boolean flagTransfer = investService.updateTransferStatus(debtTransfer.invest_id, t_invest.TransferStatus.TRANSFERING);
			if (!flagTransfer) {
				result.code = -1;
				result.msg = "更新转让人投资状态失败";

				return result;
			}
			
			// 审核通过 发送通知
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("user_name", user.name);
			param.put("debt_no", debtTransfer.debt_transfer_no);
			param.put("title", debtTransfer.title);
			noticeService.sendSysNotice(user.id, NoticeScene.DEBT_AUDIT_PASS, param);
			 
			result.code=1;
			result.msg="审核通过操作成功";
			result.obj = debtTransfer;
			
			return result;
		}
		//审核不通过操作
		else{
			//审核操作不通过，更新数据库表 t_debt_transfer
			int rowDebt = debtTransferDao.updateDebtTransferPass(start_time, end_time, t_debt_transfer.Status.AUDIT_NOT_THROUGH.code, supervisorId, debtTransferId);
			if (rowDebt < 1) {
				result.code = -1;
				result.msg = "审核通过操作失败";

				return result;
			}
			
			//更新转让人的投资表的转让状态
			boolean flagTransfer = investService.updateTransferStatus(debtTransfer.invest_id, t_invest.TransferStatus.NORMAL);
			if (!flagTransfer) {
				result.code = -1;
				result.msg = "更新转让人投资状态失败";

				return result;
			}
			
			//审核不通过发送通知
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("user_name", user.name);
			param.put("debt_no", debtTransfer.debt_transfer_no);
			param.put("title", debtTransfer.title);
			noticeService.sendSysNotice(user.id, NoticeScene.DEBT_AUDIT_REJECT, param);

			result.code = 1;
			result.msg = "债权审核不通过操作成功！";
			result.obj = debtTransfer;
			
			return result;
		}
	}
	
	/**
	 * 债权转让(准备)
	 *
	 * @param debtId 债权id
	 * @param currUserId 债权购买者
	 * @return 详细以map的形式当做obj返回
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public ResultInfo debtTransfer(long debtId,long currUserId) {
		ResultInfo result = new ResultInfo();
		
		DebtTransferDetail detail = this.findDebtDetailById(debtId);
		if (detail == null) {
			result.code = 0;
			result.msg = "没有查到相关债权信息";

			return result;
		}
		
		t_bid bid = bidService.findByID(detail.bid_id);
		t_invest invest = investService.findByID(detail.invest_id);
		t_debt_transfer debt = debtTransferDao.findByID(debtId);
		
		if (bid == null || invest == null || debt == null ) {
			result.code = 0;
			result.msg = "没有查到相关债权信息";
			
			return result;
		}
		
		if (bid.user_id == currUserId) {
			result.code = 0;
			result.msg = "原借款人不能购买该债权";
			
			return result;
		}
		
		if (invest.user_id == currUserId) {
			result.code = 0;
			result.msg = "不能购买自己转让的债权";
			
			return result;
		}
		
		if (!t_debt_transfer.Status.AUCTING.equals(debt.getStatus())) {
			result.code = 0;
			result.msg = "债权不处于转让中状态";

			return result;
		}
		
		//受让人资金签名校验
		result = userFundService.userFundSignCheck(currUserId);
		if (result.code < 1) {
			result.code = 0;
			result.msg = "购买者资金异常";

			return result;
		}
		
		t_user_fund toUserFund = userFundService.queryUserFundByUserId(currUserId);
		if (toUserFund.balance < debt.transfer_price) {
			result.code = 0;
			result.msg = "可用余额不足以购买该债权";

			return result;
		}
		
		result.code = 1;
		result.msg = "债权转让完毕";

		return result;
	}


	/**
	 * 债权转让成交业务处理(资金托管债权转让处理回调)
	 *
	 * @param serviceOrderNo 业务订单号
	 * @param debtId 债权id
	 * @param toUserId 受让人id
	 * @param debtFee 债权管理费
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public ResultInfo doDebtTransfer(String serviceOrderNo,Long debtId,Long toUserId,double debtFee){
		ResultInfo result = new ResultInfo();
		
		t_debt_transfer debt = debtTransferDao.findByID(debtId);
		t_invest invest = investService.findByID(debt.invest_id);
		
		//判断债权状态
		if (!models.core.entity.t_debt_transfer.Status.AUCTING.equals(debt.getStatus())) {
			result.msg = "对不起！该债权已经不处于待成交状态！";
			result.code = -1;

			return result;
		}

		//更新债权的状态、成交价、成交时间、成交人
		debt.transaction_price = debt.transfer_price;
		debt.transaction_time = new Date();
		debt.transaction_user_id = toUserId;
		debt.service_order_no = serviceOrderNo;  //成交业务订单号
		debt.setStatus(models.core.entity.t_debt_transfer.Status.SUCC); 
		if (!debtTransferDao.save(debt)) {
			result.msg = "更改债权信息失败";
			result.code = -111;
			
			return result;
		}
		
		//更新原来的投资状态为'已转出'
		if (!investService.updateTransferStatus(invest.id,models.core.entity.t_invest.TransferStatus.TRANSFERED)) {
			result.msg = "更改投资的状态为'已转出'失败";
			result.code = -111;
			
			return result;
		}

		//复制invest
		result = investService.copyToInvest(invest.id, debtId, toUserId, serviceOrderNo);
		if (result.code < 1) {
			result.msg = "复制投资记录失败";
			result.code = -2;

			return result;
		}
		
		//复制账单
		t_invest newInvest = (t_invest) result.obj;
		if (!billInvestService.copyToBillInvest(invest.id,toUserId, newInvest.id)) {
			result.msg = "复制投资账单失败";
			result.code = -2;

			return result;
		}
		
		//更新原来未还投资账单状态为已转让
		if (!billInvestService.updateBillToTransfered(invest.id)) {
			result.msg = "更新原来的投资账单状态失败";
			result.code = -2;

			return result;
		}
		
		//受让人可用余额减少
		if (!userFundService.userFundMinus(toUserId,debt.transfer_price)) {
			result.msg = "扣除债权购买者金额失败";
			result.code = -3;

			return result;
		}
		
		//添加交易记录
		t_user_fund auctionDebtRecordUser = userFundService.queryUserFundByUserId(toUserId);
		Map<String,String> summaryParam = new HashMap<String, String>();
		summaryParam.put("debt_no", debt.debt_transfer_no);
		if ( !dealUserService.addDealUserInfo(
				serviceOrderNo, 
				auctionDebtRecordUser.user_id, 
				debt.transfer_price, 
				auctionDebtRecordUser.balance, 
				auctionDebtRecordUser.freeze, 
				models.common.entity.t_deal_user.OperationType.REPAYMENT_TRANSFER_AMOUNT, 
				summaryParam) ){
			result.msg = "添加用户交易记录失败";
			result.code = -4;

			return result;
		}
		
		//更新受让人的签名
		result = userFundService.userFundSignUpdate(toUserId);
		if (result.code < 1) {
			result.msg = "更新最高竞价者的资金签名失败";
			result.code = -3;

			return result;
		}

		
		//原投资人的资金状况
		boolean ischecked = true;
		result = userFundService.userFundSignCheck(invest.user_id);
		if (result.code < 1) {
			LoggerUtil.info(false, "债权转让人资金异常!");

			ischecked = false;
		}
		
		//债权转让者可用余额增加
		if(!userFundService.userFundAdd(debt.user_id, debt.transfer_price)){
			result.msg = "债权转让人可用余额增加失败";
			result.code = -3;

			return result;
		}
		
		//添加交易记录(债权转让人) 转让人获取竞拍金额[107]
		t_user_fund debtUser = userFundService.queryUserFundByUserId(debt.user_id);
		Map<String,String> summaryParam2 = new HashMap<String, String>();
		summaryParam2.put("debt_no", debt.debt_transfer_no);
		if( !dealUserService.addDealUserInfo(
				serviceOrderNo, 
				debt.user_id, 
				debt.transfer_price, 
				debtUser.balance, 
				debtUser.freeze, 
				models.common.entity.t_deal_user.OperationType.RECEIVE_AUCTION_AMOUNT, 
				summaryParam2) ) {
			result.msg = "添加用户交易记录失败";
			result.code = -4;

			return result;
		}
		
		//扣除债权转让人的转让管理费
		double managerfee = debtFee;
		if (managerfee > 0) {
			if (!userFundService.userFundMinus(debt.user_id, managerfee)) {
			result.msg = "扣除债权转让人转让管理费失败";
			result.code = -3;
			 
			return result;
			}
			}
		
		//添加交易记录(债权转让人) 转让人支付转让服务费[208]
		t_user_fund debtUser3 = userFundService.queryUserFundByUserId(debt.user_id);
		Map<String,String> summaryParam3 = new HashMap<String, String>();
		summaryParam3.put("debt_no", debt.debt_transfer_no);
		if (!dealUserService.addDealUserInfo(
				serviceOrderNo, 
				debt.user_id, 
				managerfee,
				debtUser3.balance, 
				debtUser3.freeze,
				models.common.entity.t_deal_user.OperationType.REPAYMENT_TRANSFER_FEE,
				summaryParam3)) {
			
			result.msg = "添加用户交易记录失败";
			result.code = -4;

			return result;
		}
		
		//更新债权转让人的资金签名,(转让人本来就是异常的，则不更新签名)
		if (ischecked) {
			result = userFundService.userFundSignUpdate(debt.user_id);
			if (result.code < 1) {
				result.msg = "更新最高竞价者的资金签名失败";
				result.code = -3;
				
				return result;
			}
		}
		
		// 添加平台交易记录转让服务费
		Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
		dealRemarkParam.put("transfer_no", debt.debt_transfer_no);
		if (!dealPlatformService.addPlatformDeal(debt.user_id, managerfee, models.common.entity.t_deal_platform.DealRemark.TRANSFER_FEE, dealRemarkParam)) {
			result.msg = "平台交易记录添加失败";
			result.code = -4;

			return result;
		}
		
		// 发送通知 债权转让成功给受让人
		t_user maxUser = userService.findByID(toUserId);
		Map<String, Object> paramAuction = new HashMap<String, Object>();
		paramAuction.put("user_name", maxUser.name);
		paramAuction.put("debt_no", debt.debt_transfer_no);
		paramAuction.put("title", debt.title);
		paramAuction.put("amount", debt.transfer_price);
		noticeService.sendSysNotice(toUserId, NoticeScene.AUCTION_SUCC_AUCTION, paramAuction);
		 
		//发送通知(债权转让者) 购买成功
		t_user invester = userService.findByID(debt.user_id);
		Map<String, Object> paramTransfer = new HashMap<String, Object>();
		paramTransfer.put("user_name", invester.name);
		paramTransfer.put("debt_no", debt.debt_transfer_no);
		paramTransfer.put("title", debt.title);
		paramTransfer.put("amount",  debt.transfer_price);
		paramTransfer.put("fee", managerfee);
		paramTransfer.put("balance", ( debt.transfer_price-managerfee));
		noticeService.sendSysNotice(debt.user_id, NoticeScene.AUCTION_SUCC_TRANSFER, paramTransfer);
		result = creatDebtPact(debtId,debt.transfer_period,managerfee,maxUser.name,invester.name,newInvest.id);
		if (result.code < 1) {
			result.code = -1;
			result.msg = "生成转让协议失败";
			
			return result;
		}
		
		result.msg = "债权转让成功";
		result.code = 1;

		return result;
	}
	
	/**
	 * 未完成的债权转让进行转让失败 (定时任务流拍时,债权为过期状态; 还款时,债权为失效状态)
	 * 
	 * @param debtId 债权转让id
	 * @param status 债权状态
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月25日
	 *
	 */
	public ResultInfo debtTransferFailure(long debtId,t_debt_transfer.Status status){
		ResultInfo result = new ResultInfo();
		
		// 更新债权转让状态 --定时任务流拍时,债权为过期状态; 还款时,债权为失效状态
		int rowDebt = debtTransferDao.updateDebtTransfer(debtId,status);
		if (rowDebt < 1) {
			result.code = -1;
			result.msg = "更新债权失效状态失败";

			return result;
		}
			
		t_debt_transfer debt_transfer = debtTransferDao.findByID(debtId);
			
		//更新投资表状态
		boolean flagInvestStatus = investService.updateTransferStatus(debt_transfer.invest_id, t_invest.TransferStatus.NORMAL);
		if (!flagInvestStatus) {
			result.code = -1;
			result.msg = "更新投资状态失败";

			return result;
		}
		
		t_user user = userService.findByID(debt_transfer.user_id);
		
		//给转让人发送过期人通知
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		param.put("debt_no", debt_transfer.debt_transfer_no);
		param.put("title", debt_transfer.title);
		noticeService.sendSysNotice(user.id, NoticeScene.DEBT_FAIL, param);
		
		
	   result.code = 1;
	   result.msg = "转让失败";
	
	   return result;
	}
	
	/**
	 * 定时任务---到期债权转让过期
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月28日
	 *
	 */
	public ResultInfo judgeDebtFlow(){
		ResultInfo result = new ResultInfo();
		
		List<t_debt_transfer> debtList = debtTransferDao.queryDebtTransfer();
		
		if (debtList == null || debtList.size() == 0) {
			result.code = 1;
			result.msg = "没有未完成的债权转让";

			return result;
		}
		
		//进行转让过期操作
		for (t_debt_transfer debt : debtList) {
			if (debt != null) {
				// 每个转让项目流拍事务开始
				JPAUtil.transactionBegin();
				/**项目转让失败 */
				result = debtTransferFailure(debt.id,t_debt_transfer.Status.FAILED);

				JPAUtil.transactionCommit();

				return result;
			}
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	/**
	 * 统计处于竞拍中的债权数量
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public int countDebtInAuction() {
		
		return debtTransferDao.countByColumn(" status IN (?,?) ", t_debt_transfer.Status.AUCTING.code,t_debt_transfer.Status.SUCC.code);
	}
	
	/**
	 * 某个投资是否可以进行转让
	 *
	 * @param investId 投资id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public ResultInfo isInvestCanbeTransfered(Long investId){
		ResultInfo result = new ResultInfo();
		
		t_invest invest = investService.findByID(investId);
		if (invest == null) {
			result.code = -1;
			result.msg = "该笔投资不存在";

			return result;
		}
		
		if(invest.debt_id > 0){
			result.code = -1;
			result.msg = "不允许二次转让";

			return result;
		}
		
		t_invest.TransferStatus transferStatus = invest.getTransfer_status();
		if (!t_invest.TransferStatus.NORMAL.equals(transferStatus)) {
			result.code = -1;
			result.msg = "该笔投资不能进行转让";

			return result;
		}
		
		t_bid bid = bidService.findByColumn(" id=?", invest.bid_id);
		t_bid.Status bidStatus = bid.getStatus();
		if (!t_bid.Status.REPAYING.equals(bidStatus)) {
			result.code = -1;
			result.msg = "该笔投资不处于还款中，不能进行转让";

			return result;
		} else {
			int countPaying = billInvestService.countPayingBill(investId);
			if(countPaying == 0){
				result.code = -1;
				result.msg = "该笔投资已完成,不能再进行转让";

				return result;
			}
		}
		
		if (!t_product.PeriodUnit.MONTH.equals(bid.getPeriod_unit())) {
			result.code = -1;
			result.msg = "天标不允许转让";

			return result;
		}
		
		if(billInvestService.isInvestOverdue(investId)){
			result.code = -1;
			result.msg = "已经逾期，不能转让";//有逾期记录，不能转让

			return result;
		}
		
		if(invest.red_packet_id != 0) {
			t_red_packet_user packets = redpacketUserService.findByID(invest.red_packet_id);
			if(packets != null) {
				if(packets.lock_time != null && (!packets.lock_time.equals(""))) {
					Date nowTime = new Date();
					if(packets.lock_time.getTime()>nowTime.getTime()) {
						result.code = -1;
						result.msg = "该笔投资在红包锁定期内，不能转让";//有逾期记录，不能转让

						return result;
					}
				}
			}
		}
		
		if(invest.rate_ticket_id != 0) {
			t_add_rate_user users = t_add_rate_user.findById(invest.rate_ticket_id);
			if(users == null){
				result.code = 1;
				result.msg = "可以进行转让";
				return result;
			}
			t_add_rate_ticket tickets = addRateTicketService.findByID(users.ticket_id);
			if(tickets != null) {
				if(tickets.lock_time != null && (!tickets.lock_time.equals(""))) {
					Date nowTime = new Date();
					if(tickets.lock_time.getTime()>nowTime.getTime()) {
						result.code = -1;
						result.msg = "该笔投资在加息卷锁定期内，不能转让";//有逾期记录，不能转让

						return result;
					}
				}
			}
		}
		
		result.code = 1;
		result.msg = "可以进行转让";
		return result;
	}
	
	/**
	 * 根据投资id查询债权
	 *
	 * @param investId 投资id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public t_debt_transfer findDebtTransferByInvestId(Long investId){
		
		return debtTransferDao.findByColumn(" invest_id=? ", investId);
	}

	/**
	 * 根据债权id查询债权的详细信息
	 *
	 * @param debtId 债权Id
	 * @return 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public DebtTransferDetail findDebtDetailById(long debtId){
		
		return debtTransferDao.findDebtDetailById(debtId);
	}
	
	/**
	 * 查询债权转让信息
	 *
	 * @param debtId 债权id
	 * @param currUserId 债权购买者
	 * @return 详细以map的形式当做obj返回
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public ResultInfo findTransferInfo(long debtId,long currUserId) {
		ResultInfo result = new ResultInfo();
		
		DebtTransferDetail detail = this.findDebtDetailById(debtId);
		if (detail == null) {
			result.code = 0;
			result.msg = "没有查到相关债权信息";

			return result;
		}
		
		t_bid bid = bidService.findByID(detail.bid_id);
		t_invest invest = investService.findByID(detail.invest_id);
		t_debt_transfer debt = debtTransferDao.findByID(debtId);
		
		if (bid == null || invest == null || debt == null ) {
			result.code = 0;
			result.msg = "没有查到相关债权信息";
			
			return result;
		}

		//借款人id
		long bidUserId = bid.user_id;  
		
		//投资人id
		long fromUserId = invest.user_id;
		
		//借款标编号
		String bidNo = bid.mer_bid_no;
		
		//投资流水号
		String creMerBillNo = invest.service_order_no;
		
		//债权待收本金
		double corpus = detail.debt_principal;
				
		//债权待收利息
		double interest = detail.debt_amount - detail.debt_principal;//债权金额 = 待收利息 + 待收本金
		
		//已还金额(只算本金)
		double printAmt = billInvestService.findHasReceivePrincipal(detail.invest_id);
		
		//债权成交金额
		double pPayAmt = detail.transfer_price;
		
		//受让人
		long toUserId = currUserId;
		
		//转让管理费
		double managefee = this.debtTransferMaxfee(corpus);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bidUser", bidUserId); //借款人
		map.put("fromUserId", fromUserId); //投资人
		map.put("toUserId", toUserId); //受让人
		map.put("bidNo", bidNo); ///标的编号
		map.put("pCreMerBillNo", creMerBillNo); //投资流水号
		map.put("interest", interest);  //债权待收利息
		map.put("pCretAmt2", corpus);  //转让金额（只算本金）（汇付用）
		map.put("pPayAmt", pPayAmt);  //成交金额
		map.put("managefee", managefee);  //转让管理费，收转让人
		map.put("orderDate", DateUtil.dateToString(invest.time, "yyyyMMdd"));
		map.put("printAmt", printAmt);  //已还金额 (只算本金)（汇付用）
		map.put("bid", bid);
		result.obj = map;
		
		result.code = 1;
		result.msg = "数据组装成功";
		return result;
	}
	
	/**
	 * 根据investid查找债权相关信息
	 *
	 * @param investId 投资id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public DebtInvest findDebtByInvestid(Long investId) {
		
		return debtTransferDao.findDebtByInvestid(investId);
	}
	
	/**
	 * 查询债权总额
	 * 
	 * @param showType 0-所有   1-待审核  2-转让中 3-成功   4-失败(审核不通过,过期,失效)
	 * @param transferName 转让人姓名
	 * @param numNo 编号
	 * @param projectName 项目
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月28日
	 *
	 */
	public double findTotalDebtAmount(int showType, String transferName, String numNo, String projectName) {
		
		return debtTransferDao.findTotalDebtAmount(showType, transferName, numNo, projectName);
	}
	
	/**
	 * 还款时，是否有未完成的债权转让，有的话,债权失效
	 * 
	 * @param
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月25日
	 *
	 */
	public ResultInfo queryDebtTransferNoComplete(long bidId){
		ResultInfo result = new ResultInfo();
		result.code = 1;
		
		//是否有未完成的债权转让
		List<t_invest> investList = investService.queryInvestTransfer(bidId);
		
		if (investList == null || investList.size() == 0) {
			result.code = 1;
			result.msg = "没有未完成的债权转让";

			return result;
		}
		
		//进行债权失效操作
		for (t_invest invest : investList) {
			if (invest != null) {
				t_debt_transfer debtTransfer = debtTransferDao.findDebtTransferNo(invest.id);
				if (debtTransfer != null) {
					result = debtTransferFailure(debtTransfer.id,t_debt_transfer.Status.INVALID);
					return result;
				}
			}
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	/**
	 * 查询债权转让项目
	 * 
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param showType 0-所有   1-待审核  2-转让中  3-成功   4-失败(审核不通过,过期，失效)
	 * @param transferName 转让人姓名
	 * @param orderType 排序栏目  0：编号 2：债权总额   3：转让期数  4：转让价格
	 * @param orderValue 排序规则 0,降序，1,升序
	 * @param exports 1：导出   default：不导出
	 * @param numNo 编号
	 * @param projectName 项目
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月25日
	 *
	 */
	public PageBean<BackDebtTransfer> pageOfDebtTransferBack(int showType, int currPage, int pageSize,String transferName,int orderType,int orderValue,int exports,String numNo,String projectName){

		return debtTransferDao.pageOfDebtTransferBack(showType, currPage, pageSize, transferName, orderType, orderValue, exports, numNo, projectName);
	}
	
	/**
	 * 分页查询处于竞拍和成功状态的债权
	 *
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月28日
	 */
	public PageBean<DebtTransfer> pageOfDebtTransfer(int currPage,int pageSize){
		
		StringBuffer countSQL = new StringBuffer(" SELECT count(id) FROM t_debt_transfer dt WHERE dt.status in (:status) ");
		StringBuffer querySQL = new StringBuffer(" SELECT  dt.id AS id, dt.end_time as end_time, dt.time AS time, dt.invest_id AS invest_id, dt.user_id AS user_id, dt.title AS title, dt.debt_amount  debt_amount, dt.debt_principal AS debt_principal, dt.transfer_price AS transfer_price, dt.transfer_period AS period, dt.status AS status FROM t_debt_transfer dt WHERE dt.status in (:status) ORDER BY dt.status, dt.id DESC");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		List<Integer> status = new LinkedList<Integer>();
		status.add(t_debt_transfer.Status.AUCTING.code);
		status.add(t_debt_transfer.Status.SUCC.code);
		condition.put("status", status);
		
		return debtTransferDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), DebtTransfer.class, condition);
	}
	
	/**
	 * 分页查询某个用户的债权
	 *
	 * @param userId 转让人id
	 * @param transactionUserId 受让人Id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月28日
	 */
	public PageBean<UserDebt> pageOfDebtByUser(int currPage,int pageSize,Long userId,Long transactionUserId ){
		StringBuffer querySQL = new StringBuffer(" SELECT dt.id AS id, dt.time AS time, dt.invest_id AS invest_id, dt.user_id AS user_id,dt.transaction_user_id as transaction_user_id, dt.title AS title, dt.debt_amount AS debt_amount, dt.debt_principal AS debt_principal, dt.transfer_price AS transfer_price, dt.transfer_period AS transfer_period, dt.status AS status, dt.start_time AS start_time, dt.end_time AS end_time, dt.transaction_time AS transaction_time,p.contract_id AS contract_id FROM t_debt_transfer dt LEFT JOIN t_pact p ON p.pid = dt.id ");
		StringBuffer countSQL = new StringBuffer(" SELECT count(1) FROM t_debt_transfer dt LEFT JOIN t_pact p ON p.pid = dt.id")	;
		
		Map<String, Object> condition = null;
		
		if(userId != null || transactionUserId != null){
			querySQL.append(" WHERE  ");
			countSQL.append(" WHERE ");
			
			condition = new HashMap<String, Object>();
			 
			if(userId != null){
				querySQL.append("  dt.user_id= :userId ");
				countSQL.append("  dt.user_id= :userId ");
				condition.put("userId", userId);
			}
			
			if(transactionUserId != null){
				querySQL.append("  dt.transaction_user_id= :transactionUserId ");
				countSQL.append("  dt.transaction_user_id= :transactionUserId ");
				condition.put("transactionUserId", transactionUserId);
			}
		}
		querySQL.append(" ORDER BY dt.id DESC ");
		
		return debtTransferDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), UserDebt.class, condition);
	}
	
	/**
	 * 获取转让手续费
	 *
	 * @param amt 本金
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	private static double debtTransferMaxfee(double amt){
		double percent = Double.valueOf(settingService.findSettingValueByKey(SettingKey.TRANSFER_FEE_RATE));
		double temp = new BigDecimal(percent).compareTo(new BigDecimal(Constants.DEBT_TRANSFER_MAXRATE))>0 ? Constants.DEBT_TRANSFER_MAXRATE : percent;
		return amt*temp/100;
	}
	
	/**
	 * 后台-首页-待办事项的统计
	 *
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年5月13日
	 */
	public int backCountDebtInfo(){
		
		return debtTransferDao.backCountDebtInfo();
	}
}
