package service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import models.app.bean.BidInformationBean;
import models.app.bean.BidInvestRecordBean;
import models.app.bean.BidReturnMoneyBean;
import models.app.bean.BidUserInfoBean;
import models.app.bean.InvestBidsBean;
import models.common.entity.t_loan_track;
import models.common.entity.t_risk_pic;
import models.common.entity.t_risk_report;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.entity.t_bid;
import models.core.entity.t_invest.InvestType;
import models.ext.redpacket.bean.AddRateTicketBean;
import models.ext.redpacket.entity.t_red_packet_user;
import models.finance.entity.t_industry_sort;
import models.core.entity.t_product;
import net.sf.json.JSONObject;
import payment.impl.PaymentProxy;
import services.common.IndustrySortService;
import services.common.LoanTrackService;
import services.common.RiskPicService;
import services.common.UserService;
import services.core.InvestService;
import services.ext.redpacket.AddRateTicketService;
import yb.YbUtils;
import yb.enums.EntrustFlag;
import yb.enums.ServiceType;
import common.constants.ConfConst;
import common.enums.Client;
import common.ext.cps.utils.RewardGrantUtils;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import controllers.front.invest.InvestCtrl;
import dao.InvestAppDao;
import dao.RiskDao;
import daos.common.LoanTrackDao;
import daos.core.BidDao;
import daos.ext.redpacket.RedpacketUserDao;

public class InvestAppService extends InvestService{
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	protected static AddRateTicketService addRateTicketService = Factory.getService(AddRateTicketService.class);
	protected static RedpacketUserDao redpacketUserDao = Factory.getDao(RedpacketUserDao.class);
	protected static RiskDao riskDao=Factory.getDao(RiskDao.class);
	private static UserService userService = Factory.getService(UserService.class);
	protected static BidDao bidDao=Factory.getDao(BidDao.class);
	
	protected static LoanTrackService loanTrackService = Factory.getService(LoanTrackService.class);
	
	protected static IndustrySortService industrySortService = Factory.getService(IndustrySortService.class); 
	
	protected static RiskPicService riskPicService = Factory.getService(RiskPicService.class);
	
	private InvestAppDao investAppDao;
	
	private InvestAppService() {
		investAppDao = Factory.getDao(InvestAppDao.class);
	//	super.basedao = investDao;
	}
	
	/***
	 * 理财产品列表
	 *
	 * @param limit 最多条目数
	 * @return
	 * @throws Exception
	 *
	 * @author huangyunsong
	 * @createDate 2016年6月30日
	 */
	public List<InvestBidsBean> listOfInvestBids (int limit) throws Exception{
		
		PageBean<InvestBidsBean> investBidPage = investAppDao.pageOfInvestBids(1, limit);

		return investBidPage.page;
	}
	
	/***
	 * 
	 * 理财产品列表接口（OPT=311）
	 * @param currPage
	 * @param pageSize
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public String pageOfInvestBids (int currPage,int pageSize) throws Exception{
		
		PageBean<InvestBidsBean> investBidPage = investAppDao.pageOfInvestBids(currPage, pageSize);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("records", investBidPage.page);
		
		return JSONObject.fromObject(map).toString();
	}

	/**
	 * 投标
	 *
	 * @param userId 用户id
	 * @param bidId 标的id
	 * @param investAmt 份数或者金额
	 * @param client 设备
	 * @param 
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月7日
	 */
	public ResultInfo appInvest(long userId, long bidId, String investAmt, Client client, String businessSeqNo,long redPacketId, String couponIdStr) {
		
		ResultInfo resultInfo = new ResultInfo();
		double redPackeAmt = 0;
		t_bid bid = bidService.findByID(bidId);
		if (bid == null) {
			resultInfo.code = -1;
			resultInfo.msg = "糟糕，没有找到该标的";
			
			return resultInfo;
		}
			 
		InvestType investType = client.code == Client.ANDROID.code ? InvestType.ANDROID : InvestType.IOS;
		
		double amt = Double.parseDouble(investAmt);
		//按份数购买时，投资金额=投资份数*每份的金额
		if (t_product.BuyType.PURCHASE_BY_COPY.equals(bid.getBuy_type())) {
			amt = new Double(investAmt).intValue() * bid.min_invest_amount;
		}
		
		long couponId = 0;
		//加息券使用
		if (couponIdStr != null) {
			/** 使用加息券  */
			couponId = Long.parseLong(couponIdStr) ;

			/**使用加息券投资准备*/
			if(couponId > 0){
				resultInfo = addRateTicketService.checkAddRateTicket(userId, couponId, amt,bid.period, bid.getPeriod_units());
				if(resultInfo.code <= 0 ){
					LoggerUtil.info(false, resultInfo.msg);
					
				}				
			}
		}
		
		
		resultInfo = super.invest(userId, bid, amt,0);
		if (resultInfo.code < 1) {
			resultInfo.code = -1;
			resultInfo.msg = resultInfo.msg;
			
			return resultInfo;
		}
		
		
		String businessSeqNos = OrderNoUtil.getNormalOrderNo(ServiceType.MARKET);
		if (redPacketId > 0){
		
			t_red_packet_user redPacket = redpacketUserDao.findByID(redPacketId);
			redPacket.old_biz_no = businessSeqNos;
			redPacket.save();
			
			resultInfo = investService.investRedPacket(userId, redPacketId, amt, bid.period, bid.getPeriod_units()) ;
				if(resultInfo.code <= 0 ){
					resultInfo.code = -1;
					resultInfo.msg = resultInfo.msg;
				}
			t_red_packet_user redPacketUser = (t_red_packet_user) resultInfo.obj ;
			redPackeAmt =  redPacketUser.amount ;
			
			resultInfo = InvestCtrl.redPacketInvest(client.code, businessSeqNos, userId, redPacketId, bid);
			
		}
		
		
		//业务流水号
		String clientSn = "oyph_" + UUID.randomUUID().toString();
		t_user_info user_info = userInfoService.findUserInfoByUserId(userId);
		
		Map<String, Object> accMap = new HashMap<String, Object>();
		accMap.put("oderNo", "0");
		accMap.put("oldbusinessSeqNo", "");
		accMap.put("oldOderNo", "");
		accMap.put("debitAccountNo", userId);
		accMap.put("cebitAccountNo", bid.object_acc_no);
		accMap.put("currency", "CNY");
		accMap.put("amount", YbUtils.formatAmount(amt));
		accMap.put("summaryCode", "T01");
		
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
			
			resultInfo = PaymentProxy.getInstance().fundTrade(clientSn, userId, businessSeqNo, bid, ServiceType.TENDER, EntrustFlag.UNENTRUST, accountList, contractList);
			if (resultInfo.code < 1) {
				
				if (redPacketId > 0){
					resultInfo = redpacketService.redPacketCorrect(bid, redPacketId, "0", businessSeqNos, userId);
				}
				
				LoggerUtil.info(true, resultInfo.msg);
				return resultInfo;
			}
		}
		
		if(redPacketId > 0){
			
			resultInfo = investService.doInvestRed(userId, bidId, amt, businessSeqNo, "", Client.PC.code, InvestType.PC.code, redPacketId);	
			
			if (resultInfo.code < 1) {
				resultInfo.code = -1;
				
				return resultInfo;
				
			}
			resultInfo.code = 1;
			resultInfo.msg = "投标成功";
			
			return resultInfo;
		}
		
		resultInfo = super.doInvest(userId, bidId, amt, businessSeqNo, "", client.code,investType.code, couponId);
		if (resultInfo.code < 1) {
			resultInfo.code = -1;
			
			return resultInfo;
		}
		
		/** 满足投资发放规则发放 */
		boolean investRedPacket = InvestCtrl.investRedPacket(userId,amt,bid.product_id);
		
		resultInfo.code = 1;
		resultInfo.msg = "投标成功";
		/** app端首投成功后给老用户(推广人)加金币*/
		if (user_info.getMember_type().code == 0) {
			t_user user = userService.findByID(userId);
			RewardGrantUtils.toOldCustomerGold(user);
			//如果新用户投标金额大于等于5000和时间在注册15天内
			Date time  = new Date();			
			int days = DateUtil.getDaysDiffFloor(user.time,time);
			if (amt >= 5000 && days <= 15) {
				//给老用户和新用户京东E卡
				RewardGrantUtils.toOldAndNewECard(user);
			}
		}
		return resultInfo;
	}
		
	
	/***
	 * 借款标详情（opt=312）
	 * @param bidId 标id
	 * @param userId 用户的id 
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public String findInvestBidInformation(long bidId, t_user_fund userFund) throws Exception{
		BidInformationBean bidInfo = investAppDao.findInvestBidInformation(bidId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("bidInfo", JSONObject.fromObject(bidInfo));
		if(userFund != null) {
			map.put("balance", userFund.balance);
		}
		
		return JSONObject.fromObject(map).toString();
	}
		
	/***
	 * 借款人详情接口（OPT=322）
	 * @param bidId 标id
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public String findInvestBidDeatils(long bidId) throws Exception{
		BidUserInfoBean bidInfo = investAppDao.findInvestBidsUserInfo(bidId);
		List<Map<String, Object>> listMaps = investAppDao.listOfInvestBidItems(bidId);
		
		List<t_loan_track> loanTrack = loanTrackService.listOfLoanTracks(bidId);
		
		t_risk_report report=null;
		t_bid bid=bidDao.findByID(bidId);
		if(bid!=null){
			if(bid.risk_id!=null && bid.risk_id>0){
				 report=riskDao.findByID(bid.risk_id);
			}
		}
		
		//起息日
		Date date = null;
		if(bid != null) {
			date = bid.release_time;
		}
		
		//房子和车辆的担保方式
		String pledgeKind = null;
		if(report != null) {
			pledgeKind = report.pledge_kind;
			
			if(pledgeKind.equals("房产一抵") || pledgeKind.equals("房产二抵")) {
				pledgeKind = "房产抵押";
			}
		}
		
		//征信报告图片
		List<t_risk_pic> riskPic = null;
		if(report != null) {
			riskPic = riskPicService.getRiskPicByRiskId(report.id, 6);
		}
		
		/** 将页面借款人信息中职业数据修改为单位所属行业  */
		t_industry_sort industrySort = null;
		
		if(bid.id!=null){
			industrySort= industrySortService.getByCompanyTrade(bid.id);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("bidInfo", JSONObject.fromObject(bidInfo));
		map.put("bidItemSupervisor", listMaps);
		map.put("loanTrack", loanTrack);
		map.put("report", JSONObject.fromObject(report));
		map.put("marriage", bidInfo.getMarital());
		map.put("industrySort", industrySort);
		map.put("pledgeKind", pledgeKind);
		map.put("date", date);
		
		if(riskPic != null && riskPic.size()>0) {
			map.put("riskPicUrl", riskPic.get(0).pic_path);
		}
		
		return JSONObject.fromObject(map).toString();
	}
		
	/***
	 * 投标记录接口（OPT=324）
	 * @param bidId 标id
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public String pageOfInvestBidsRecord(int currPage,int pageSize,long bidId) throws Exception{
		PageBean<BidInvestRecordBean> bidRecord = investAppDao.pageOfInvestBidsRecord(currPage,pageSize,bidId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("records", bidRecord.page);
		
		return JSONObject.fromObject(map).toString();
	}
		
	/***
	 * 回款计划接口（OPT=323）
	 * @param bidId 标id
	 * @return
	 * @throws Exception
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-7
	 */
	public String listOfRepaymentBill(long bidId) throws Exception{
		List<BidReturnMoneyBean> bidRecord = investAppDao.listOfRepaymentBill(bidId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "查询成功!");
		map.put("records", bidRecord);
		
		
		return JSONObject.fromObject(map).toString();
	}
	
	/**
	 * APP投标验密
	 * 
	 * @author niu
	 * @create 2017.10.13
	 */
	/*public ResultInfo investCheckPass11(int client, String businessSeqNo, long userId) {
		
		Map<String, String> serviceRequestParams = new HashMap<String, String>();
		ResultInfo result = PaymentProxy.getInstance().checkTradePass(client, businessSeqNo, userId, "http://a.a.com:9000/payment/yibincallback/checkPassAI", ServiceType.TENDER, serviceRequestParams);
		
		return result;
	}*/
	
	
	
	
	
}
