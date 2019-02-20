package controllers.front;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.common.bean.CurrUser;
import models.common.entity.t_information;
import models.common.entity.t_loan_apply_info;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.entity.t_audit_subject_bid;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_invest;
import models.core.entity.t_product;
import models.core.entity.t_product.RepaymentType;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.control.StaticImportVisitor;

import com.sun.corba.se.spi.protocol.RetryType;

import payment.impl.PaymentProxy;
import play.cache.Cache;
import play.mvc.With;
import services.common.InformationService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.core.AuditSubjectBidService;
import services.core.BidItemUserService;
import services.core.BidService;
import services.core.BillService;
import services.core.InvestService;
import services.core.ProductService;
import yb.enums.ProjectStatus;
import yb.enums.ReturnType;
import yb.enums.ServiceType;
import common.annotation.LoginCheck;
import common.annotation.RealNameCheck;
import common.annotation.SimulatedCheck;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Client;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.NoUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import common.utils.number.NumberFormat;
import controllers.common.FrontBaseController;
import controllers.common.SubmitRepeat;
import controllers.common.interceptor.SimulatedInterceptor;
import controllers.common.interceptor.UserStatusInterceptor;


/**
 * 前台-借款控制器
 *
 * @description
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With({UserStatusInterceptor.class, SubmitRepeat.class,SimulatedInterceptor.class})
public class LoanCtrl extends FrontBaseController {

	/** 注入注入借款service  */
	/*protected static BillService billService = Factory.getService(BillService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static ProductService productService = Factory.getService(ProductService.class);
	
	protected static BidService bidService = Factory.getService(BidService.class);
				  
	protected static AuditSubjectBidService auditSujbectBidService = Factory.getService(AuditSubjectBidService.class);
	
	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	protected static InvestService investservice = Factory.getService(InvestService.class);
	
	protected static BidItemUserService biditemuserservice = Factory.getService(BidItemUserService.class);*/
	
	/**
	 * 借款首页
	 *
	 *
	 * @author yaoyi
	 * @createDate 2016年1月7日
	 */
/*	@SubmitCheck
	public static void toLoanPre(long productId) {
		
		List<Map<String, Object>> productList = productService.queryProductIsUse();
		if(productList == null || productList.size() == 0){
			render();
		}
		if(productId == 0){
			productId = Long.parseLong(productList.get(0).get("id").toString());
		}else{
			List<String> productIdList = new ArrayList<String>();
			for(Map<String, Object> map:productList){
				productIdList.add(String.valueOf(map.get("id")));
			}
			if(!productIdList.contains(String.valueOf(productId))){
				error404();
			}
		}
		
		t_product tp = productService.findByID(productId);
		
		CurrUser currUser = getCurrUser();
		int baseInfoSchedule = 0;
		if(currUser != null){
			baseInfoSchedule = userInfoService.findUserBasicSchedule(currUser.id);
		}
		
		//读取借款协议的标题
		String loan_agreement = informationService.findLoanPactTitle(); 
		
		//回显数据用到的处理方式
		t_bid bid = (t_bid) Cache.get("bid_"+session.getId());
		if (bid != null) {
			renderArgs.put("bid", bid);
			Cache.delete("bid_"+session.getId());
		}
		
		render(productList, tp, baseInfoSchedule, loan_agreement);
	}
	
	*//**
	 * 创建标的
	 *
	 * @param amount 借款金额
	 * @param apr 借款年利率
	 * @param period 期限
	 * @param repayment_type 还款方式
	 * @param expire_time 到期时间
	 * @param name 标的名称
	 * @param description 标的描述
	 * @param agree_pact 同意借款协议
	 * @param signProductId 标的id签名
	 * @param uuid 防重单uuid
	 *
	 * @author yaoyi
	 * @createDate 2015年12月22日
	 *//*
	@SubmitOnly
	@LoginCheck
	@SimulatedCheck
	@RealNameCheck
	public static void createBid(double amount, double apr, int period, int repayment_type, int invest_period, String name,String projectname, String description, boolean agree_pact, long productId){
		checkAuthenticity();
		
		t_product product = productService.queryProduct(productId);
		if(product == null){
			flash.success("参数错误!");//需求为弱弹窗
			
			toLoanPre(0);
		}
		
		ResultInfo result = checkCreateBidParams(product, amount, apr, period, repayment_type, invest_period, name, description, agree_pact);
		if (result.code < 1) {
			flash.success(result.msg);//需求为弱弹窗
			addCreateBidInfoToFlash(amount, apr, period, repayment_type, invest_period, name, description, agree_pact);
			
			toLoanPre(productId);
		}

		t_user_fund tuf = userFundService.queryUserFundByUserId(getCurrUser().id);
		int client = Client.PC.code;
		
		result = bidService.createBid(amount, apr, period, repayment_type, invest_period, name,projectname, description, product, tuf, client);
		if(result.code < 1){
			LoggerUtil.error(true, result.msg);
			flash.success(result.msg);//需求为弱弹窗
			addCreateBidInfoToFlash(amount, apr, period, repayment_type, invest_period, name, description, agree_pact);
			
			toLoanPre(productId);
		}
		
		t_bid bid = (t_bid)result.obj;
		
		//业务订单号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_PUBLIC);
		
		// 托管，标的登记
		if (ConfConst.IS_TRUST) {
			bid.mer_bid_no = OrderNoUtil.getBidNo();
			
			result = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), businessSeqNo, bid, ServiceType.BID_PUBLIC, ProjectStatus.PUBLIC, billService.findReturnList());
			if (result.code < 1) {
				LoggerUtil.error(true, result.msg);
				flash.error(result.msg);
				addCreateBidInfoToFlash(amount, apr, period, repayment_type, invest_period, name, description, agree_pact);
				
				toLoanPre(productId);
			}
		}
		bid.object_acc_no = result.obj.toString();
		result = bidService.doCreateBid(bid, businessSeqNo);
		if(result.code < 1){
			LoggerUtil.error(true, result.msg);
			flash.error(result.msg);
			addCreateBidInfoToFlash(amount, apr, period, repayment_type, invest_period, name, description, agree_pact);
			
			toLoanPre(productId);
		}
		
		result.code = 1;
		//上传审核资料
		uploadBidItemPre(bid.id);
	}
	
	*//**
	 * 将发标信息保存到flash
	 *
	 * @param amount 借款金额
	 * @param apr 借款年利率
	 * @param period 期限
	 * @param repayment_type 还款方式
	 * @param invest_period 投标期限
	 * @param name 项目名称
	 * @param description 项目描述
	 * @param agree_pact 是否同意借款协议
	 *
	 * @author yaoyi
	 * @createDate 2016年2月25日
	 *//*
	private static void addCreateBidInfoToFlash(double amount, double apr, int period, int repayment_type, int invest_period, 
			String name, String description, boolean agree_pact) {
		
		t_bid bid = new t_bid();
		bid.amount = amount;
		bid.apr = apr;
		bid.period = period;
		bid.setRepayment_type(ReturnType.getEnum(repayment_type));
		bid.invest_period = invest_period;
		bid.title = name;
		bid.description = description;
		flash("agree_pact", agree_pact?"true":"false");
		Cache.set("bid_"+session.getId(), bid, Constants.CACHE_TIME_SECOND_60);
		
	}

	*//**
	 * 前台发标时，参数检查
	 *
	 * @param product 借款产品
	 * @param amount 借款金额
	 * @param apr 借款年利率
	 * @param period 期限
	 * @param repayment_type 还款方式
	 * @param invest_period 投标期限
	 * @param name 标的名称
	 * @param description 标的描述
	 * @param agree_pact 是否同意借款协议
	 * @param productId 产品id
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月25日
	 *//*
	private static ResultInfo checkCreateBidParams(t_product product, double amount, double apr, int period, int repayment_type, int invest_period, 
			String name, String description, boolean agree_pact) {
		ResultInfo result = new ResultInfo();
		
		int baseInfoSchedule = userInfoService.findUserBasicSchedule(getCurrUser().id);
		if (baseInfoSchedule != 3) {
			result.code = -1;
			result.msg = "申请借款需要实名认证并完善资料!";
			
			return result;
		}
		
		if(!agree_pact){//不同意借款协议
			result.code = -1;
			result.msg = "不同意借款协议无法借款!";
			
			return result;
		}
		
		if(amount < product.min_amount || amount > product.max_amount){
			result.code = -1;
			result.msg = "借款金额在["+NumberFormat.format(product.min_amount, Constants.FINANCE_FORMATE_NORMAL)
					+"~"+NumberFormat.format(product.max_amount, Constants.FINANCE_FORMATE_NORMAL)+"]之间!";
			
			return result;
		}
		
		if(apr < product.min_apr || apr > product.max_apr){
			result.code = -1;
			result.msg = "借款年利率在["+product.min_apr+"~"+product.max_apr+"]之间!";
			
			return result;
		}
		
		if(period < product.min_period || period > product.max_period){
			result.code = -1;
			result.msg = "借款期限在["+product.min_period+"~"+product.max_period+"]之间!";
			
			return result;
		}
		
		if(!product.getProductRepaymentTypeList().contains(ReturnType.getEnum(repayment_type))){
			result.code = -1;
			result.msg = "还款方式参数错误!";
			
			return result;
		}
		
		if(invest_period < 1 || invest_period > 10){
			result.code = -1;
			result.msg = "筹款时间在[1~10]天之间!";
			
			return result;
		}
		
		if(StringUtils.isBlank(name) || name.length() < 3 || name.length() > 15){
			result.code = -1;
			result.msg = "项目名称长度在[3~15]位之间!";
			
			return result;
		}
		
		if(StringUtils.isBlank(description) || description.length() < 20 || description.length() > 2000){
			result.code = -1;
			result.msg = "项目描述长度在[20~2000]位之间!";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "";
		return result;
	}

	*//**
	 * 用户上传标的的审核资料
	 *
	 * @author yaoyi
	 * @createDate 2016年1月12日
	 *//*
	@LoginCheck
	public static void uploadBidItemPre(long bidId){
		
		t_bid tb = bidService.findByID(bidId);
		List<t_audit_subject_bid> tasb = auditSujbectBidService.queryAuditSubjectBid(bidId);
		
		render(tb, tasb);
	}
	
	*//**
	 * 用户借款协议
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 *//*
	public static void loanDealPre(){
		t_information loanDeal = informationService.findLoanDeal();
		if(loanDeal == null){
			
			error404();
		}
		
		render(loanDeal);
	}*/
	/**
	 * 
	 * @Title: toLoanPre
	 * 
	 * @description 进入借款页面
	 * @param 
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月18日-下午5:41:41
	 */
	public static void toLoanPre() {
		render();
	}
	/**
	 * 
	 * @Title: addLoanApplyInfo
	 * 
	 * @description 添加一条申请信息
	 * @param  applyInfo
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月18日-下午5:10:34
	 */
	public static void addLoanApplyInfo(t_loan_apply_info applyInfo){
		Date date = new Date();
		applyInfo.appli_time = date;
		applyInfo.update_time = date;
		/** 未受理状态*/
		applyInfo.appli_status = 0;
		applyInfo.save();
		flash.success("申请已提交");
		toLoanPre();
	}
	
	
}
