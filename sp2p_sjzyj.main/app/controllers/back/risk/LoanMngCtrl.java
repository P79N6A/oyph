package controllers.back.risk;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import models.common.bean.CurrSupervisor;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_guarantee;
import models.common.entity.t_information;
import models.common.entity.t_event_supervisor.Event;
import models.common.entity.t_company_branch;
import models.common.entity.t_loan_apply;
import models.common.entity.t_loan_profession;
import models.common.entity.t_risk_handle_record;
import models.common.entity.t_risk_pic;
import models.common.entity.t_risk_reception;
import models.common.entity.t_risk_report;
import models.common.entity.t_risk_suggest;
import models.common.entity.t_ssq_user;
import models.common.entity.t_template_official_notice;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.common.entity.t_user_live;
import models.common.entity.t_user_profession;
import models.core.bean.BackDebtTransfer;
import models.core.bean.BackRiskBid;
import models.core.bean.BidInvestRecord;
import models.core.bean.BidItemOfSupervisorSubject;
import models.core.bean.BidItemOfUserSubject;
import models.core.entity.t_bid;
import models.core.entity.t_bid_item_supervisor;
import models.core.entity.t_bill;
import models.core.entity.t_debt_transfer;
import models.core.entity.t_bid_item_user.BidItemAuditStatus;
import models.core.entity.t_product;
import models.core.entity.t_product.RepaymentType;
import models.finance.entity.t_industry_sort;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import play.Play;
import play.cache.Cache;
import play.db.jpa.Blob;
import play.db.jpa.JPA;
import play.mvc.With;
import push.AndroidBroadcast;
import push.AndroidNotification;
import push.IOSBroadcast;
import push.PushDemo;
import services.common.BranchService;
import services.common.GuaranteeService;
import services.common.InformationService;
import services.common.LoanApplyService;
import services.common.RiskPicService;
import services.common.RiskReceptionService;
import services.common.RiskRecordService;
import services.common.RiskReportService;
import services.common.RiskSuggestService;
import services.common.TemplateOfficialNoticeService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserLiveService;
import services.common.UserProfessionService;
import services.common.UserService;
import services.common.ssqUserService;
import services.core.AuditSubjectBidService;
import services.core.BidItemSupervisorService;
import services.core.BidItemUserService;
import services.core.BidService;
import services.core.BillService;
import services.core.InvestService;
import services.core.ProductService;
import services.finance.IndustrySortService;
import yb.enums.ProjectStatus;
import yb.enums.ReturnType;
import yb.enums.ServiceType;

import com.shove.Convert;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.BorrowingType;
import common.enums.Client;
import common.utils.CropImage;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.NoUtil;
import common.utils.OSSUploadUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.excel.ExcelUtils;
import common.utils.file.FileUtil;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.back.webOperation.InformationMngCtrl;
import controllers.common.BackBaseController;
import controllers.common.ImagesController;
import controllers.common.SubmitRepeat;
import daos.common.TemplateOfficialNoticeDao;

/**
 * 后台-风控-理财项目管理控制器
 * 
 * @description
 * 
 * @author huangyunsong
 * @createDate 2015年12月18日
 */
@With({ SubmitRepeat.class })
public class LoanMngCtrl extends BackBaseController {
	
	protected static String appkey = null;
	
	protected static String appMasterSecret = null;

	protected static BidService bidservice = Factory.getService(BidService.class);

	protected static ProductService productService = Factory.getService(ProductService.class);

	protected static UserFundService userfundservice = Factory.getService(UserFundService.class);

	protected static UserService userservice = Factory.getService(UserService.class);

	protected static UserInfoService userinfoservice = Factory.getService(UserInfoService.class);

	protected static AuditSubjectBidService auditsubjectbidservice = Factory.getService(AuditSubjectBidService.class);

	protected static BidItemSupervisorService biditemsupervisorservice = Factory.getService(BidItemSupervisorService.class);

	protected static BidItemUserService bidItemUserService = Factory.getService(BidItemUserService.class);

	protected static BillService billService = Factory.getService(BillService.class);

	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected static RiskReceptionService riskReceptionService = Factory.getService(RiskReceptionService.class);
	
	protected static RiskReportService riskReportService = Factory.getService(RiskReportService.class);
	
	protected static RiskPicService riskPicService = Factory.getService(RiskPicService.class);
	
	protected static RiskSuggestService riskSuggestService = Factory.getService(RiskSuggestService.class);
	
	protected static BranchService branchService=Factory.getService(BranchService.class);
	
	protected static RiskRecordService riskRecordService=Factory.getService(RiskRecordService.class);
	
	protected static LoanApplyService loanApplyService = Factory.getService(LoanApplyService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static UserLiveService userLiveService = Factory.getService(UserLiveService.class);
	
	protected static UserProfessionService userProfessionService = Factory.getService(UserProfessionService.class);
	
	protected static GuaranteeService guaranteeService = Factory.getService(GuaranteeService.class);
	
	protected static ssqUserService ssquserService = Factory.getService(ssqUserService.class);
	
	protected static TemplateOfficialNoticeService templateOfficialNoticeService = Factory.getService(TemplateOfficialNoticeService.class);
	
	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	protected static IndustrySortService industrySortService = Factory.getService(IndustrySortService.class);
	/**
	 * 后台-风控-理财项目-显示借款标
	 * 
	 * @param showType 筛选类型 0-所有;1-初审中;2-借款中;3-满标审核;4-还款中;5-已经结束;6-失败
	 * @param currPage
	 * @param pageSize
	 * @param exports 1:导出 default：不导出
	 * @param numNo 编号
	 * @param projectName 项目名称
	 * @param userName 借款人
	 * 
	 * @author yaoyi
	 * @createDate 2016年1月6日
	 */
	public static void showBidPre(int showType, int currPage, int pageSize) {
		String numNo = params.get("numNo");
		String projectName = params.get("projectName");
		String userName = params.get("userName");

		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);// 0-编号；3-借款金额；4-年利率；5-期限；6-筹款进度；7-发售时间
		if (orderType != 3 && orderType != 4 && orderType != 5 && orderType != 6 && orderType != 7) {
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);

		String orderValueStr = params.get("orderValue");
		int orderValue = Convert.strToInt(orderValueStr, 0);// 0,降序，1,升序
		if (orderValue < 0 || orderValue > 1) {
			orderValue = 0;
		}
		renderArgs.put("orderValue", orderValue);

		int exports = Convert.strToInt(params.get("exports"), 0);
		if (showType < 0 || showType > 10) {
			showType = 0;
		}
		
		List<t_guarantee> guarantees = guaranteeService.findAll();
		renderArgs.put("guarantees", guarantees);
		/**lihuijun 2017-4-21*/
		if(showType==1){
			List<t_company_branch> branchs=branchService.findAll();
			List<t_risk_reception> riskList=riskReceptionService.findAll();
			render(showType,riskList,branchs);
			
		}else if(showType==2 || showType==3 || showType==4 ){
		
			Integer cYear=null;
			Integer cMonth=null;
			Integer cDay=null;
			
			String currYear = params.get("year");
			if(StringUtils.isNotBlank(currYear) &&!currYear.equals("请选择")){
				cYear=Integer.parseInt(currYear);
			}
			String currMonth = params.get("month");
			if(StringUtils.isNotBlank(currMonth) &&!currMonth.equals("请选择")){
				cMonth=Integer.parseInt(currMonth);
			}
			String currDay = params.get("day");
			if(StringUtils.isNotBlank(currDay) &&!currDay.equals("请选择")){
				cDay=Integer.parseInt(currDay);
			}
			
			PageBean<t_risk_report> pageBean=riskReportService.pageOfRiskReport(showType, currPage, pageSize,orderValue,currYear,currMonth,currDay,exports);
			
			//导出
			if (exports == Constants.EXPORT) {
				List<t_risk_report> pageList = pageBean.page;
		
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATE));
				jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
				JSONArray arrList = JSONArray.fromObject(pageList, jsonConfig);
		
				for (Object obj : arrList) {
					JSONObject bid = (JSONObject) obj;
		
					/*if (StringUtils.isNotBlank(bid.getString("period_unit"))) {
						bid.put("periods", bid.getString("period") + t_product.PeriodUnit.valueOf(bid.getString("period_unit")).getShowValue());
					}*/
		
					if (StringUtils.isNotBlank(bid.getString("status"))) {
						bid.put("status", t_risk_report.Status.valueOf(bid.getString("status")).value);
					}
				}
		
				String fileName = "风控报告列表";
				switch (showType) {
				case 2:
					fileName = "待审核风控报告列表";
					break;
				case 3:
					fileName = "审核通过风控报告列表";
					break;
				case 4:
					fileName = "未通过风控报告列表";
					break;
				default:
					fileName = "风控报告列表";
					break;
				}
		
				File file = ExcelUtils.export(fileName, arrList, new String[] { "编号", "申请人", "联系电话", "借款金额", "借款期限", "借款利率", "抵押物类型", "发布时间", "状态" }, new String[] { "id", "app_name", "app_phone", "loan_amount", "loan_time_limit", "annual_rate", "loan_kind", "time", "status" });
		
				renderBinary(file, fileName + ".xls");
			}
			
			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR); 
			int years=year-100;
			int[] arrYear=new int[100];
			int[] arrMonth=new int[]{1,2,3,4,5,6,7,8,9,10,11,12};
			int[] arrDay=new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
			int j=0;
			for(int i=year;i>years;i--){
				arrYear[j]=i;
				j++;
			}
			render(showType,pageBean,arrYear,arrMonth,arrDay,cYear,cMonth,cDay);
		}else{
		/**lihuijun 2017-4-21 end*/
			PageBean<BackRiskBid> pageBean = bidservice.pageOfBidRisk(showType, currPage, pageSize, exports, orderType, orderValue, userName, numNo, projectName);
			
			// 导出
			if (exports == Constants.EXPORT) {
				List<BackRiskBid> list = pageBean.page;
		
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATE));
				jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
				JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
		
				for (Object obj : arrList) {
					JSONObject bid = (JSONObject) obj;
		
					if (StringUtils.isNotBlank(bid.getString("period_unit"))) {
						bid.put("periods", bid.getString("period") + t_product.PeriodUnit.valueOf(bid.getString("period_unit")).getShowValue());
					}
		
					if (StringUtils.isNotBlank(bid.getString("status"))) {
						bid.put("status", t_bid.Status.valueOf(bid.getString("status")).value);
					}
				}
		
				String fileName = "理财项目列表";
				switch (showType) {
				case 5:
					fileName = "初审中理财项目列表";
					break;
				case 6:
					fileName = "借款中理财项目列表";
					break;
				case 7:
					fileName = "满标审核理财项目列表";
					break;
				case 8:
					fileName = "还款中理财项目列表";
					break;
				case 9:
					fileName = "已结束理财项目列表";
					break;
				case 10:
					fileName = "失败理财项目列表";
					break;
				default:
					fileName = "理财项目列表";
					break;
				}
		
				File file = ExcelUtils.export(fileName, arrList, new String[] { "编号", "项目", "借款人", "姓名", "项目金额", "年利率", "期限", "筹款进度", "发售时间", "状态" }, new String[] { "bid_no", "title", "name", "reality_name", "amount", "apr", "periods", "loan_schedule", "pre_release_time", "status" });

		
				renderBinary(file, fileName + ".xls");
			}
		
			double totalAmt = bidservice.findTotalBidAmountRisk(showType, userName, numNo, projectName);
		
			render(pageBean, totalAmt, showType, userName, numNo, projectName);
		}
	}
	
	/**
	 * 后台-风控-理财项目-借款标列表-借款标详情
	 * 
	 * 
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	public static void showBidDetailPre(long bidId) {

		if (bidId < 1) {

			showBidPre(0, 1, 10);
		}

		t_bid tb = bidservice.findByID(bidId);
		if (tb == null) {
			flash.error("没有查询到该借款项目");

			showBidPre(0, 1, 10);
		}

		t_user tu = userservice.findUserById(tb.user_id);
		t_user_info tui = userinfoservice.findUserInfoByUserId(tb.user_id);
		t_user_fund tuf = userfundservice.queryUserFundByUserId(tb.user_id);
		t_risk_report report=null;
		if(tb.risk_id!=null){
			report=riskReportService.findByID(tb.risk_id);
			
		}

		Map<String, Object> item = auditsubjectbidservice.queryBidRefSubject(bidId);
		List<BidItemOfUserSubject> userLoop = (List<BidItemOfUserSubject>) item.get("userItem");
		List<BidItemOfSupervisorSubject> supervisorLoop = (List<BidItemOfSupervisorSubject>) item.get("supervisorItem");

		// 详情页面，资料审核功能关闭
		boolean closeAudit = true;
		renderArgs.put("sysNowTime", new Date().getTime());// 服务器时间传到客户端

		render(tb, tu, tui, tuf, userLoop, supervisorLoop, closeAudit,report);
	}

	/**
	 * 后台-风控-理财项目-借款标列表-进入标记初审页面
	 * 
	 * @param bidId
	 * 
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	@SubmitCheck
	public static void toPreAuditBidPre(long bidId) {

		if (bidId < 1) {

			showBidPre(0, 1, 10);
		}

		t_bid tb = bidservice.findByID(bidId);
		if (tb == null) {
			flash.error("没有查询到该借款项目");

			showBidPre(0, 1, 10);
		}

		t_user tu = userservice.findUserById(tb.user_id);
		t_user_info tui = userinfoservice.findUserInfoByUserId(tb.user_id);
		t_user_fund tuf = userfundservice.queryUserFundByUserId(tb.user_id);
		t_risk_report report=null;
		if(tb.risk_id!=null){
			report=riskReportService.findByID(tb.risk_id);
			
		}
		Map<String, Object> item = auditsubjectbidservice.queryBidRefSubject(bidId);
		List<BidItemOfUserSubject> userLoop = (List<BidItemOfUserSubject>) item.get("userItem");
		List<BidItemOfSupervisorSubject> supervisorLoop = (List<BidItemOfSupervisorSubject>) item.get("supervisorItem");

		long curTime = System.currentTimeMillis();
		if (curTime > 0) {
			tb.invite_code = (new StringBuffer()).append(curTime).substring(2, 10).toString();
		}

		render(tb, tu, tui, tuf, userLoop, supervisorLoop,report);
	}

	/**
	 * 后台-风控-理财项目-借款标列表-进入标记复审页面
	 * 
	 * 
	 * @author DaiZhengmiao
	 * @createDate 2015年12月28日
	 */
	@SubmitCheck
	public static void toAuditBidPre(long bidId) {

		if (bidId < 1) {

			showBidPre(0, 1, 10);
		}
		
		t_bid tb = bidservice.findByID(bidId);
		if (tb == null) {
			flash.error("没有查询到该借款项目");

			showBidPre(0, 1, 10);
		}

		t_user tu = userservice.findUserById(tb.user_id);
		t_user_info tui = userinfoservice.findUserInfoByUserId(tb.user_id);
		t_user_fund tuf = userfundservice.queryUserFundByUserId(tb.user_id);
		t_risk_report report=null;
		if(tb.risk_id!=null){
			report=riskReportService.findByID(tb.risk_id);
			
		}
		Map<String, Object> item = auditsubjectbidservice.queryBidRefSubject(bidId);
		List<BidItemOfUserSubject> userLoop = (List<BidItemOfUserSubject>) item.get("userItem");
		List<BidItemOfSupervisorSubject> supervisorLoop = (List<BidItemOfSupervisorSubject>) item.get("supervisorItem");

		renderArgs.put("sysNowTime", new Date().getTime());// 服务器时间传到客户端

		render(tb, tu, tui, tuf, userLoop, supervisorLoop,report);
	}

	/**
	 * 后台-风控-理财项目-借款标列表-进入发布借款标(理财项目)界面
	 * 
	 * @author yaoyi
	 * @createDate 2015年12月21日
	 */
	public static void createBidPre(long productId) {
		List<t_industry_sort> industryList = industrySortService.findAll();
		List<t_risk_report> riskReportList=riskReportService.findListByColumn("status=?", 1);
		List<Map<String, Object>> tpList = productService.queryProductIsUse();
		List<t_company_branch> branchs = branchService.findAll();
		if (tpList == null || tpList.size() == 0) {
			render();
		}
		if (productId < 1) {
			productId = Long.parseLong(tpList.get(0).get("id").toString());
		}

		// 回显数据用到的处理方式
		t_bid bid = (t_bid) Cache.get(CacheKey.BID_ + session.getId());
		if (bid != null) {
			renderArgs.put("bid", bid);
			Cache.delete(CacheKey.BID_ + session.getId());
		}

		render(tpList, productId,riskReportList,branchs,industryList);
	}

	/**
	 * ajax获取发标时关联用户
	 * 
	 * @param key
	 * 
	 * @author yaoyi
	 * @createDate 2016年1月9日
	 */
	public static void queryRefUser(int currPage, int pageSize, String key) {

		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		flash.put("searchKey", key);

		// 限制最大长度
		if (StringUtils.isNotBlank(key) && key.length() > 16) {
			key = key.substring(0, 16);
		}

		PageBean<Map<String, Object>> pageBean = userservice.queryCreateBidRefUser(currPage, pageSize, key);

		render(pageBean);
	}

	/**
	 * ajax获取标的信息
	 * 
	 * @param productId
	 * 
	 * @author yaoyi
	 * @createDate 2016年1月7日
	 */
	public static void getBidInfo(long productId) {
		ResultInfo res = new ResultInfo();

		if (productId < 1) {
			res.code = -1;
			res.msg = "参数错误";

			renderJSON(res);
		}
		t_product tp = productService.queryProduct(productId);
		Map<String, Object> tpMap = new HashMap<String, Object>();
		tpMap.put("code", 1);
		tpMap.put("min_amount", tp.min_amount);
		tpMap.put("max_amount", tp.max_amount);
		tpMap.put("min_apr", tp.min_apr);
		tpMap.put("max_apr", tp.max_apr);
		tpMap.put("period_unit_code", tp.getPeriod_unit().code);
		tpMap.put("period_unit", tp.getPeriod_unit().value);
		tpMap.put("service_fee_rule", tp.service_fee_rule);
		List<Integer> periodList = new ArrayList<Integer>();
		for (int i = tp.min_period; i <= tp.max_period; i++) {
			periodList.add(i);
		}
		tpMap.put("period", periodList);
		List<Integer> repaymentTypeListCode = new ArrayList<Integer>();
		List<String> repaymentTypeListValue = new ArrayList<String>();
		for (String r : tp.repayment_type.split(",")) {
			repaymentTypeListCode.add(Integer.parseInt(r.trim()));
			repaymentTypeListValue.add(ReturnType.getEnum(Integer.parseInt(r.trim())).key);
		}
		tpMap.put("repayment_type_code", repaymentTypeListCode);
		tpMap.put("repayment_type_value", repaymentTypeListValue); 

		res.code = 1;
		res.msg = "";
		res.obj = tpMap;

		renderJSON(res);
	}

	/**
	 * 后台发标
	 * 
	 * @param productId
	 * @param user_id
	 * @param amount
	 * @param apr
	 * @param period
	 * @param repayment_type
	 * @param invest_period
	 * @param name
	 * @param description
	 * @param serviceCharge 服务费费率
	 * 
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public static void createBid(long productId, long userId, double amount, double apr, int period, int repayment_type, int invest_period,
			String name,String projectname, String description, int loanType, int site, double serviceCharge, long	guaranteeUser, int lock_deadline,String throwArea,Long throwIndustryCode) {
		checkAuthenticity();
		
		String riskId = params.get("riskId");
		
		if(riskId.equals("请选择")){
			riskId=null;
			flash.error("请选择报告!");

			createBidPre(productId);
		}
		
		t_product product = productService.queryProduct(productId);
		if (product == null) {
			flash.error("没有找到借款产品!");

			error404();
		}
		t_user_fund userFund = userfundservice.queryUserFundByUserId(userId);
		if (userFund == null) {
			flash.error("没有找到该借款人");

			error404();
		}
		
		/**个人用户和企业用户限额*/
		t_user_info  userInfo = userInfoService.findByColumn("user_id = ?", userId);
		
		//用户发布标的和用户未还款账单总金额
		double bidAmount = bidservice.findUnfinishedBid(userId);
		
		//企业总限额100W
		if (userInfo.enterprise_credit != null && userInfo.enterprise_bank_no !=null && userInfo.enterprise_name != null &&
				!userInfo.enterprise_credit.equals("") && !userInfo.enterprise_bank_no.equals("") && !userInfo.enterprise_name.equals("")){
			if (bidAmount + amount > 1000000){
				flash.error("企业用户发布借款标的超过限额!");

				createBidPre(productId);
			}
		}else{
			//个人总限额20W
			if (bidAmount + amount > 200000){
				flash.error("个人用户发布借款标的超过限额!");

				createBidPre(productId);
			}
		}
		
		//上上签校验
		t_ssq_user ssqUser = ssquserService.findByUserId(userId);
		if (ssqUser == null || ssqUser.application_status == -1){
			flash.error("电子签章用户校验失败");
			
			createBidPre(productId);
		}

		//ResultInfo result = checkCreateBidParams(product, amount, apr, period, repayment_type, invest_period, name, description, serviceCharge);
		ResultInfo result = checkCreateBidParams(product, amount, apr, period, repayment_type, invest_period, name, description, serviceCharge,throwArea,throwIndustryCode);
		if (result.code < 1) {
			flash.error(result.msg);
			
			//addBidInfoToFlash(userId, userFund.name, amount, apr, period, repayment_type, invest_period, name,projectname, description, lock_deadline);
			addBidInfoToFlash(userId, userFund.name, amount, apr, period, repayment_type, invest_period, name,projectname, description, lock_deadline,throwArea,throwIndustryCode);
			createBidPre(productId);
		}

		int client = Client.PC.code;

		//担保方账号获取
		t_user_info ssqUserInfo = userInfoService.findUserInfoByUserId(guaranteeUser);
		String ssq_guarantee_user = null;
		if (ssqUserInfo != null) {
			ssq_guarantee_user = ssqUserInfo.mobile;
		}
		
		ResultInfo res = bidservice.createBid(amount, apr, period, repayment_type, invest_period, name,projectname, description, 
				product, userFund, client, riskId, serviceCharge, ssq_guarantee_user, lock_deadline,throwArea,throwIndustryCode);
		if (res.code < 1) {
			LoggerUtil.error(true, res.msg);
			flash.error(res.msg);

			//addBidInfoToFlash(userId, userFund.name, amount, apr, period, repayment_type, invest_period, name,projectname, description, lock_deadline);
			addBidInfoToFlash(userId, userFund.name, amount, apr, period, repayment_type, invest_period, name,projectname, description, lock_deadline,throwArea,throwIndustryCode);
			createBidPre(productId);
		}
		
		/** 添加管理员事件 */
		Map<String, String> param = new HashMap<String, String>();
		param.put("supervisor", getCurrentSupervisorName());
		param.put("bid_no", "");// 管理员事件bid_no不做获取
		param.put("bid_name", name);
		supervisorService.addSupervisorEvent(getCurrentSupervisorId(), t_event_supervisor.Event.INVEST_ADD, param);

		t_bid bid = (t_bid) res.obj;
		
		// 业务订单号
		String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_PUBLIC);
		
		// 贷款申请
		t_loan_apply loan = new t_loan_apply();
		loan.service_order_no = serviceOrderNo;
		loan.user_id = userId;
		loan.type = loanType;
		loan.time = new Date();
		loan.amount = amount;
		loan.status = 1;
		loan.site = site;
		if(product.period_units == 1) {
			loan.period = period/30.0;
		}else {
			loan.period = period;
		}
		loan.save();

		// 托管，标的登记
		if (ConfConst.IS_TRUST) {
			bid.mer_bid_no = OrderNoUtil.getBidNo();
			result = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), serviceOrderNo, bid, ServiceType.BID_PUBLIC, ProjectStatus.PUBLIC, billService.findReturnList());
			
			if (result.code < 1) {
				LoggerUtil.error(true, result.msg);
				flash.error(result.msg);
				addBidInfoToFlash(userId, userFund.name, amount, apr, period, repayment_type, invest_period, name,projectname, description, lock_deadline);
				
				createBidPre(productId);
			}
		}

		if(StringUtils.isNotBlank(riskId)){
			t_risk_report report=riskReportService.findByID(Integer.parseInt(riskId));
			if(report!=null){
				report.status=4;
				report.save();
			}
		}
		
		bid.object_acc_no = result.obj.toString();
		res = bidservice.doCreateBid((t_bid) res.obj, serviceOrderNo);
		
		if (res.code < 0) {
			flash.error(res.msg);
			LoggerUtil.error(true, res.msg);

			//addBidInfoToFlash(userId, userFund.name, amount, apr, period, repayment_type, invest_period, name,projectname, description, lock_deadline);
			addBidInfoToFlash(userId, userFund.name, amount, apr, period, repayment_type, invest_period, name,projectname, description, lock_deadline,throwArea,throwIndustryCode);
			createBidPre(productId);
		}
		
		flash.success("项目创建成功!");
		showBidPre(0, 1, 10);
	}

	/**
	 * 添加发标信息到flash
	 * 
	 * @param userId 用户id
	 * @param userName 标的关联用户名
	 * @param amount 借款金额
	 * @param apr 借款年利率
	 * @param period 借款期限
	 * @param repayment_type  还款方式
	 * @param invest_period  投资期限
	 * @param name 标的名称
	 * @param description 标的描述
	 * 
	 * @author yaoyi
	 * @createDate 2016年2月25日
	 */
	private static void addBidInfoToFlash(long userId, String userName, double amount, double apr, int period, int repayment_type, int invest_period, String name,String projectname, String description, int lock_deadline) {

		t_bid bid = new t_bid();
		bid.user_id = userId;
		flash.put("userName", userName);
		bid.amount = amount;
		bid.apr = apr;
		bid.period = period;
		bid.setRepayment_type(ReturnType.getEnum(repayment_type));
		bid.invest_period = invest_period;
		bid.title = name;
		bid.description = description;
		bid.project_name_on_weixin = projectname;
		bid.lock_deadline = lock_deadline;
		Cache.set(CacheKey.BID_ + session.getId(), bid, Constants.CACHE_TIME_SECOND_60);

	}
	
	/**
	 * 添加发标信息到flash
	 * 
	 * @param userId 用户id
	 * @param userName 标的关联用户名
	 * @param amount 借款金额
	 * @param apr 借款年利率
	 * @param period 借款期限
	 * @param repayment_type  还款方式
	 * @param invest_period  投资期限
	 * @param name 标的名称
	 * @param description 标的描述
	 * 
	 * @author guoShiJie
	 * @createDate 2018年10月5日
	 */
	private static void addBidInfoToFlash(long userId, String userName, double amount, double apr, int period, int repayment_type, int invest_period, String name,String projectname, String description, int lock_deadline,String throwArea,Long throwIndustry) {

		t_bid bid = new t_bid();
		bid.user_id = userId;
		flash.put("userName", userName);
		bid.amount = amount;
		bid.apr = apr;
		bid.period = period;
		bid.setRepayment_type(ReturnType.getEnum(repayment_type));
		bid.invest_period = invest_period;
		bid.title = name;
		bid.description = description;
		bid.project_name_on_weixin = projectname;
		bid.lock_deadline = lock_deadline;
		bid.throw_area = throwArea;
		bid.throw_industry = throwIndustry;
		Cache.set(CacheKey.BID_ + session.getId(), bid, Constants.CACHE_TIME_SECOND_60);

	}

	/**
	 * 后台发标时，参数的检查
	 * 
	 * @param product 借款产品对象
	 * @param amount 借款金额
	 * @param apr 借款年利率
	 * @param period 期限
	 * @param repayment_type 还款方式
	 * @param invest_period 投标期限
	 * @param name 标的名称
	 * @param description 标的描述
	 * @return
	 * 
	 * @author yaoyi
	 * @createDate 2016年2月25日
	 */
	private static ResultInfo checkCreateBidParams(t_product product, double amount, double apr, int period,
			int repayment_type, int invest_period, String name, String description, double serviceCharge) {
		ResultInfo result = new ResultInfo();

		if (amount > product.max_amount || amount < product.min_amount) {
			result.code = -1;
			result.msg = "借款项目金额范围在[" + product.min_amount + "~" + product.max_amount + "]元之间!";

			return result;
		}
		if (apr > product.max_apr || apr < product.min_apr) {
			result.code = -1;
			result.msg = "借款项目年利率在[" + product.min_apr + "~" + product.max_apr + "]之间!";

			return result;
		}
		if (period > product.max_period || period < product.min_period) {
			result.code = -1;
			result.msg = "借款项目期限在[" + product.min_period + "~" + product.max_period + "]天之间!";

			return result;
		}
		
		if (serviceCharge > 100 || serviceCharge < 0) {
			result.code = -1;
			result.msg = "服务费费率范围在0-100之间!";

			return result;
		}
		

		List<ReturnType> productRepaymentType = product.getProductRepaymentTypeList();
		if (!productRepaymentType.contains(ReturnType.getEnum(repayment_type))) {
			result.code = -1;
			result.msg = "还款方式错误!";

			return result;
		}

		if (invest_period < 1 || invest_period > 10) {
			result.code = -1;
			result.msg = "筹款时间只能在[1~10]天之间!";

			return result;
		}

		if (StringUtils.isBlank(name) || name.length() < 3 || name.length() > 15) {
			result.code = -1;
			result.msg = "项目名称长度在[3~15]之间!";

			return result;

		}
		if (StringUtils.isBlank(description) || description.length() < 20 || description.length() > 2000) {
			result.code = -1;
			result.msg = "项目描述长度在[20~2000]之间!";

			return result;
		}

		result.code = 1;
		result.msg = "";

		return result;
	}
	
	/**
	 * 后台发标时，参数的检查
	 * 
	 * @param product 借款产品对象
	 * @param amount 借款金额
	 * @param apr 借款年利率
	 * @param period 期限
	 * @param repayment_type 还款方式
	 * @param invest_period 投标期限
	 * @param name 标的名称
	 * @param description 标的描述
	 * @return
	 * 
	 * @author yaoyi
	 * @createDate 2016年2月25日
	 */
	private static ResultInfo checkCreateBidParams(t_product product, double amount, double apr, int period,
			int repayment_type, int invest_period, String name, String description, double serviceCharge,String throwArea,Long throwIndustry) {
		ResultInfo result = new ResultInfo();

		if (amount > product.max_amount || amount < product.min_amount) {
			result.code = -1;
			result.msg = "借款项目金额范围在[" + product.min_amount + "~" + product.max_amount + "]元之间!";

			return result;
		}
		if (apr > product.max_apr || apr < product.min_apr) {
			result.code = -1;
			result.msg = "借款项目年利率在[" + product.min_apr + "~" + product.max_apr + "]之间!";

			return result;
		}
		if (period > product.max_period || period < product.min_period) {
			result.code = -1;
			result.msg = "借款项目期限在[" + product.min_period + "~" + product.max_period + "]天之间!";

			return result;
		}
		
		if (serviceCharge > 100 || serviceCharge < 0) {
			result.code = -1;
			result.msg = "服务费费率范围在0-100之间!";

			return result;
		}
		

		List<ReturnType> productRepaymentType = product.getProductRepaymentTypeList();
		if (!productRepaymentType.contains(ReturnType.getEnum(repayment_type))) {
			result.code = -1;
			result.msg = "还款方式错误!";

			return result;
		}

		if (invest_period < 1 || invest_period > 10) {
			result.code = -1;
			result.msg = "筹款时间只能在[1~10]天之间!";

			return result;
		}

		if (StringUtils.isBlank(name) || name.length() < 3 || name.length() > 15) {
			result.code = -1;
			result.msg = "项目名称长度在[3~15]之间!";

			return result;

		}
		if (StringUtils.isBlank(description) || description.length() < 20 || description.length() > 2000) {
			result.code = -1;
			result.msg = "项目描述长度在[20~2000]之间!";

			return result;
		}
		
		if (StringUtils.isBlank(throwArea)) {
			result.code = -1;
			result.msg = "投向地区为空";
		}
		
		if (throwIndustry == null) {
			result.code = -1;
			result.msg = "投向行业为空";
		}

		result.code = 1;
		result.msg = "";

		return result;
	}

	/**
	 * 标的初审(初审中 -> 借款中)
	 * 
	 * @param bidSign
	 * @param suggest
	 * @param pre_release_time
	 * 
	 * @author yaoyi
	 * @createDate 2015年12月22日
	 */
	@SubmitOnly
	public static void preAuditBidThrough1(String bidSign, String suggest, String pre_release_time, int bid_type, String invite_code) {

		long bidId = decodeSign(bidSign, false);
		final long bidcode=bidId;
		if (bidId < 0) {
			flash.error("标的信息有误!");

			showBidPre(0, 1, 10);
		}
		
		t_bid bid = bidservice.findByID(bidId);
		//贷款申请通过
		t_loan_apply loan = loanApplyService.findByColumn("service_order_no = ?", bid.service_order_no);
		if (loan != null) {
			loan.bid_id = bid.id;
			loan.save();
		}

		if (StringUtils.isBlank(suggest) || suggest.length() < 20) {
			flash.error("风控建议长度位于20~2000位之间!");

			toPreAuditBidPre(bidId);
		}

		if (bid_type == 1 && ("".equals(invite_code) || invite_code == null)) {
			flash.error("定向标邀请码获取失败!");
			
			toPreAuditBidPre(bidId);
		}
		
		long supervisor_id = getCurrentSupervisorId();
		Date pre = DateUtil.strToDate(pre_release_time, Constants.DATE_PLUGIN);
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_MODIFY);
		//托管
		if (ConfConst.IS_TRUST) {
			ResultInfo result = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), businessSeqNo, bid, ServiceType.BID_MODIFY, ProjectStatus.INVESTING, billService.findReturnList());
			
			if (result.code < 0) {
				flash.error(result.msg);
				LoggerUtil.error(true, result.msg);
				
				toPreAuditBidPre(bidId);
			}
		}

		ResultInfo res = bidservice.preAuditBidThrough(bidId, suggest, pre, supervisor_id, bid_type, invite_code);
		if (res.code < 0) {
			flash.error(res.msg);
			LoggerUtil.error(true, res.msg);

			toPreAuditBidPre(bidId);
		}

		/** 添加管理员事件 */
		Map<String, String> param = new HashMap<String, String>();
		param.put("supervisor", getCurrentSupervisorName());
		param.put("bid_no", ((t_bid) res.obj).getBid_no());
		param.put("bid_name", ((t_bid) res.obj).title);
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.INVEST_PREADUIT_PASS, param);

		if (!saveEvent) {
			flash.error("保存管理员事件失败");

			toPreAuditBidPre(bidId);
		}
		//初审通过开始售卖15min后开始执行自动投标
				/*long diffTime=pre.getTime()-System.currentTimeMillis();
				if(diffTime<=0){
					diffTime = 0;
				}
				long doTime=(diffTime)+15*60*1000; 
				Timer timer =new Timer(true);
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						
						 if (JPA.local.get() == null) {
				             EntityManager em = JPA.newEntityManager();
				             final JPA jpa = new JPA();
				             jpa.entityManager = em;
				             JPA.local.set(jpa);
				         }

						 EntityTransaction transa = JPA.em().getTransaction();
						 transa.begin();
				         bidservice.autoInvest(bidcode);	
				         transa.commit();
						
					}
				}, doTime);*/

		flash.success("审核成功,已将项目置为[%s]!", t_bid.Status.FUNDRAISING.value);
		showBidPre(0, 1, 10);
	}

	/**
	 * 标的初审(初审中 -> 初审不通过)
	 * 
	 * @param bidSign
	 * @param suggest
	 * @param pre_release_time
	 * 
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	@SubmitOnly
	public static void preAuditBidNotThrough(String bidSign, String suggest, int bid_type, String invite_code) {
		ResultInfo result = new ResultInfo();

		long bidId = decodeSign(bidSign, false);
		t_bid bid = bidservice.findByID(bidId);
		if (bid == null) {
			flash.error("标的信息有误!");

			showBidPre(0, 1, 10);
		}
		
		//贷款申请不通过
		t_loan_apply loan = loanApplyService.findByColumn("service_order_no = ?", bid.service_order_no);
		if (loan != null) {
			loan.bid_id = bid.id;
			loan.status = 3;
			loan.save();
		}

		if (StringUtils.isBlank(suggest) || suggest.length() < 20 || suggest.length() > 300) {
			flash.error("风控建议长度位于20~300位之间!");

			toPreAuditBidPre(bidId);
		}

		long supervisorId = getCurrentSupervisorId();
		result = bidservice.preAuditBidNotThrough(bid, supervisorId);
		if (result.code < 1) {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);

			toPreAuditBidPre(bidId);
		}

		// 业务流水号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_CANCEL);

		if (ConfConst.IS_TRUST) {
			
			result = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), businessSeqNo, bid, ServiceType.BID_CANCEL, ProjectStatus.CANCEL, billService.findReturnList());
			
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);

				toPreAuditBidPre(bidId);
			}
			
			result = bidservice.doPreAuditBidNotThrough(businessSeqNo, bidId, suggest, supervisorId);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);

				toPreAuditBidPre(bidId);
			}
			flash.success("审核成功,已将项目置为[%s]!", t_bid.Status.PREAUDIT_NOT_THROUGH.value);
			showBidPre(0, 1, 10);
		}

		flash.error("初审不通过，标的状态修改失败！");
		showBidPre(0, 1, 10);
	}

	/**
	 * 标的复审->通过
	 * 
	 * @param bidSign
	 * @param suggest
	 * 
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	@SubmitOnly
	public static void auditBidThrough(String bidSign, String suggest) {
		long bidId = decodeSign(bidSign, false);
		if (bidId < 0) {
			flash.error("标的信息有误!");

			showBidPre(0, 1, 10);
		}
		
		t_bid bid = bidservice.findByID(bidId);
		if (bid == null) {
			flash.error("标的信息有误!");

			showBidPre(0, 1, 10);
		}
		
		if (StringUtils.isBlank(suggest) || suggest.length() < 20 || suggest.length() > 300) {
			flash.error("风控建议长度位于20~300位之间!");

			toAuditBidPre(bidId);
		}

		long supervisorId = getCurrentSupervisorId();
		ResultInfo res = bidservice.auditBidThrough(bidId, suggest, supervisorId);
		if (res.code < 0) {
			flash.error(res.msg);
			LoggerUtil.error(true, res.msg);

			toAuditBidPre(bidId);
		}

		/** 添加管理员事件 */
		Map<String, String> param = new HashMap<String, String>();
		param.put("supervisor", getCurrentSupervisorName());
		param.put("bid_no", ((t_bid) res.obj).getBid_no());
		param.put("bid_name", ((t_bid) res.obj).title);
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.INVEST_ADUIT_PASS, param);
		if (!saveEvent) {
			flash.error("保存管理员事件失败");
			LoggerUtil.error(true, "保存管理员事件失败");

			toAuditBidPre(bidId);
		}

		flash.success("审核成功,已将项目置为[%s]!", t_bid.Status.WAIT_RELEASING.value);
		showBidPre(0, 1, 10);
	}

	/**
	 * 标的复审->不通过
	 * 
	 * @param bidSign
	 * @param suggest
	 * 
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	@SubmitOnly
	public static void auditBidNotThrough(String bidSign, String suggest) {
		ResultInfo result = new ResultInfo();
		long bidId = decodeSign(bidSign, false);

		t_bid bid = bidservice.findByID(bidId);
		if (bid == null) {
			flash.error("标的信息有误!");

			showBidPre(0, 1, 10);
		}
		
		//贷款申请不通过
		t_loan_apply loan = loanApplyService.findByColumn("bid_id = ?", bid.id);
		if (loan != null) {
			loan.status = 3;
			loan.save();
		}

		if (StringUtils.isBlank(suggest) || suggest.length() < 20 || suggest.length() > 300) {
			flash.error("风控建议长度位于20~300位之间!");

			toAuditBidPre(bidId);
		}

		long supervisorId = getCurrentSupervisorId();

		result = bidservice.auditBidNotThrough(bid, supervisorId);
		if (result.code < 1) {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);

			toPreAuditBidPre(bidId);
		}

		// 业务流水号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_CANCEL);

		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), businessSeqNo, bid, ServiceType.BID_CANCEL, ProjectStatus.CANCEL, billService.findReturnList());

			if (result.code < 0) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				
				toAuditBidPre(bidId);
			}
			
			result = bidservice.doAuditBidNotThrough(businessSeqNo, bid.id, suggest, supervisorId);
			if (result.code < 0) {
				flash.error(result.msg);
				LoggerUtil.error(true, result.msg);

				toAuditBidPre(bidId);
			}

			flash.success("审核成功,已将标置为[%s]!", t_bid.Status.AUDIT_NOT_THROUGH.value);
			showBidPre(0, 1, 10);
		}

		flash.error("初审不通过，标的状态修改失败！");
		showBidPre(0, 1, 10);
	}

	/**
	 * 后台编辑标的标题
	 * 
	 * @param sign 标的ID加签为sign
	 * @param newTitle
	 * 
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public static void editBidTitle(String bidSign, String newStr) {
		ResultInfo res = new ResultInfo();

		long bidId = decodeSign(bidSign, true);

		if (StringUtils.isBlank(newStr) || newStr.length() < 3 || newStr.length() > 15) {
			res.code = -1;
			res.msg = "借款标题应该为3~15位";

			renderJSON(res);
		}

		res = bidservice.editBidTitle(bidId, newStr);
		if (res.code < 1) {

			renderJSON(res);
		}

		/** 添加管理员事件 */
		Map<String, String> supervisorParam = new HashMap<String, String>();
		supervisorParam.put("bid_no", "" + bidId);
		supervisorParam.put("bid_name", (String) res.obj);
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.INVEST_EDIT_PROJECTNAME, supervisorParam);
		if (!saveEvent) {
			LoggerUtil.error(true, "保存管理员事件失败");

			res.code = -1;
			res.msg = "保存管理员事件失败!";
			renderJSON(res);
		}

		res.code = 1;
		res.msg = "";
		renderJSON(res);
	}

	/**
	 * 编辑借款说明
	 * 
	 * @param sign
	 * @param newDescription
	 * 
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public static void editBidDescription(String bidSign, String newStr) {
		ResultInfo res = new ResultInfo();

		long bidId = decodeSign(bidSign, true);

		if (StringUtils.isBlank(newStr) || newStr.length() < 20 || newStr.length() > 2000) {
			res.code = -1;
			res.msg = "借款说明应该位于20~2000字之间";

			renderJSON(res);
		}

		res = bidservice.editBidDescription(bidId, newStr);
		if (res.code < 1) {

			renderJSON(res);
		}

		/** 添加管理员事件 */
		Map<String, String> supervisorParam = new HashMap<String, String>();
		supervisorParam.put("bid_no", "" + bidId);
		supervisorParam.put("bid_name", (String) res.obj);
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.INVEST_EDIT_DESCRIPTION, supervisorParam);
		if (!saveEvent) {
			LoggerUtil.error(true, "保存管理员事件失败");

			res.code = -1;
			res.msg = "保存管理员事件失败!";
			renderJSON(res);
		}

		res.code = 1;
		res.msg = "";
		renderJSON(res);
	}

	/**
	 * 获取标的投标记录
	 * 
	 * @param bidId
	 * 
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public static void investRecordPre(int currPage, int pageSize, String bidSign) {
		int exports = Convert.strToInt(params.get("exports"), 0);
		long bidId = decodeSign(bidSign, true);
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 5;
		}
		PageBean<BidInvestRecord> pageBean = investService.pageOfBidInvestDetail(currPage, pageSize, bidId);
		if (exports == Constants.EXPORT) {
			List<BidInvestRecord> list = pageBean.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_TIME_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			
			String fileName="投标记录";
			
			File file = ExcelUtils.export(
						fileName,
						arrList,
						new String[] {
								"投资人", "投资金额", "投资方式", "投标时间"
						},
						new String[] {
								"name","amount", "invest_type","time"
						}
					);
			   
			renderBinary(file, fileName + ".xls");
		}
		

		render(pageBean,bidSign);
	}

	/**
	 * 获取标的还款计划
	 * 
	 * @param bidId
	 * 
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public static void repaymentRecordPre(int currPage, int pageSize, String bidSign) {

		long bidId = decodeSign(bidSign, true);
		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 5;
		}
		PageBean<Map<String, Object>> pageBean = billService.pageOfRepaymentBill(currPage, pageSize, bidId);

		render(pageBean);
	}

	/**
	 * 后台-风控-标的初审，管理员实时上传图片
	 * 
	 * @param imgFile
	 * @param fileName
	 * 
	 * @author yaoyi
	 * @createDate 2016年1月15日
	 */
	public static void imagesUpload(File imgFile, String bidSign, long bidAuditSubjectId, String fileName) {
		response.contentType = "text/html";

		ResultInfo result = new ResultInfo();
		long bidId = decodeSign(bidSign, true);

		if (imgFile == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";

			renderJSON(result);
		}
		if (StringUtils.isBlank(fileName) || fileName.length() > 32) {
			result.code = -1;
			result.msg = "图片名称长度应该位于1~32位之间";

			renderJSON(result);
		}

		result = FileUtil.uploadWaterMarkImgags(imgFile);
		if (result.code < 0) {

			renderJSON(result);
		}

		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		fileInfo.put("imgName", fileName);

		boolean saveflag = biditemsupervisorservice.saveBidItemSupervisor(getCurrentSupervisorId(), bidId, bidAuditSubjectId, fileName, (String) fileInfo.get("staticFileName"), (String) fileInfo.get("fileSuffix"));
		if (!saveflag) {
			result.code = -1;
			result.msg = "图片保存失败";

			renderJSON(result);
		}

		/** 添加管理员事件 */
		Map<String, String> supervisorParam = new HashMap<String, String>();
		supervisorParam.put("bid_no", NoUtil.getBidNo(bidId));
		supervisorParam.put("bid_name", bidservice.findBidNameById(bidId));
		supervisorParam.put("subject", auditsubjectbidservice.findAuditSubjectName(bidAuditSubjectId));
		supervisorParam.put("filename", fileName);
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.INVEST_SUBJECT_FILE_UPLOAD, supervisorParam);
		if (!saveEvent) {
			LoggerUtil.error(true, "保存管理员事件失败");

			result.code = -1;
			result.msg = "保存管理员事件失败!";
			renderJSON(result);
		}

		renderJSON(result);
	}
	
	/**
	 * 后台-风控-标的初审，管理员借用图片
	 * 
	 * @param bidSign
	 * @param bidAuditSubjectId
	 * 
	 * @author liuyang
	 * @createDate 2018年04月25日
	 */
	public static void addImages(String bidSign, long bidAuditSubjectId, String names) {
		response.contentType = "text/html";

		ResultInfo result = new ResultInfo();
		long bidId = decodeSign(bidSign, true);
		
		t_bid bid = bidservice.findByID(bidId);
		
		//确定类型
		Integer type = 1;
		
		if(names.equals("身份证")){
			type = -2;
		}else if(names.equals("机动车登记证")) {
			type = -1;
		}else if(names.equals("房产证明")) {
			type = -1;
		}else if(names.equals("其他证明材料")) {
			type = 1;
		}else if(names.equals("基本车况")) {
			type = 2;
		}else if(names.equals("屋内照片")) {
			type = 2;
		}else if(names.equals("行驶证")) {
			type = 5;
		}else if(names.equals("借款人征信报告")) {
			type = 6;
		}
		
		List<t_risk_pic> risks = riskPicService.findListByColumn("type=? and risk_id=?", type, bid.risk_id);
		
		if(risks.size()>0) {
			for (int i = 0; i <risks.size() ; i++) {
				t_bid_item_supervisor t = new t_bid_item_supervisor();
				t.supervisor_id = getCurrentSupervisorId();
				t.bid_id = bidId;
				t.bid_audit_subject_id = bidAuditSubjectId;
				t.name = i+"";
				t.url = risks.get(i).pic_path;
				t.save();
			}
		}
		
		result.code = 1;
		
		renderJSON(result);
	}

	/**
	 * 后台-标的初审-管理员上传科目资料，刷新div
	 * 
	 * @param bidId
	 * 
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public static void itemSupervisorPre(String bidSign) {

		long bidId = decodeSign(bidSign, true);

		Map<String, Object> item = auditsubjectbidservice.queryBidRefSubject(bidId);
		List<BidItemOfSupervisorSubject> supervisorLoop = (List<BidItemOfSupervisorSubject>) item.get("supervisorItem");

		render(supervisorLoop);
	}

	/**
	 * 管理员删除上传的审核资料
	 * 
	 * @param itemId
	 * 
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public static void delItemSupervisor(long itemId) {

		ResultInfo res = biditemsupervisorservice.delBidItemSupervisor(itemId);
		
		if (res.code < 1) {
			LoggerUtil.error(true, "管理员删除上传的审核科目资料，id为[%s]", itemId);

			renderJSON(res);
		}

		/** 添加管理员事件 */
		Map<String, String> param = (Map<String, String>) res.obj;
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.INVEST_SUBJECT_FILE_REMOVE, param);
		if (!saveEvent) {
			LoggerUtil.error(true, "保存管理员事件失败");

			res.code = -1;
			res.msg = "保存管理员事件失败!";
			renderJSON(res);
		}

		res.code = 1;
		res.msg = "";

		renderJSON(res);
	}

	/**
	 * 管理员审核用户上传的科目
	 * 
	 * @param itemId
	 * 
	 * @author yaoyi
	 * @createDate 2016年1月19日
	 */
	public static void auditBidItemUser(long itemId, int status) {
		ResultInfo result = new ResultInfo();

		if (status != BidItemAuditStatus.NO_PASS.code && status != BidItemAuditStatus.PASS.code) {
			result.code = -1;
			result.msg = "审核状态有误";

			renderJSON(result);
		}

		BidItemAuditStatus statusEnum = BidItemAuditStatus.getEnum(status);
		result = bidItemUserService.auditBidItemUser(itemId, statusEnum);

		if (result.code < 1) {
			LoggerUtil.error(true, "管理员审核用户上传的科目资料，id为[%s]", itemId);

			renderJSON(result);
		}

		/** 添加管理员事件 */
		Map<String, String> param = (Map<String, String>) result.obj;
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), status == BidItemAuditStatus.PASS.code ? Event.INVEST_SUBJECT_AUDIT_PASS : Event.INVEST_SUBJECT_AUDIT_REJECT, param);
		if (!saveEvent) {
			LoggerUtil.error(true, "保存管理员事件失败");

			result.code = -1;
			result.msg = "保存管理员事件失败!";
			renderJSON(result);
		}

		result.code = 1;
		result.msg = "";

		renderJSON(result);
	}

	/**
	 * 解标的的签
	 * 
	 * @param bidSign
	 * @param async  是否来自异步的Ajax请求
	 * @return
	 * 
	 * @author yaoyi
	 * @createDate 2016年2月23日
	 */
	private static long decodeSign(String bidSign, boolean async) {
		ResultInfo result = Security.decodeSign(bidSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			flash.error("签名失败");

			showBidPre(0, 1, 10);
			if (async) {
				renderText("");
			} else {
				error404();
			}
		}

		return Convert.strToLong(result.obj + "", -1);
	}
	
	/**
	 * 保存风控报告
	 * 
	 * @createDate 2017年4月24日
	 * @author lihuijun
	 */
	public static void saveRiskReport(){
		t_risk_report riskReport=null;
		t_risk_handle_record record=new t_risk_handle_record();
		String reportIds = params.get("reportId");
		if(StringUtils.isNotBlank(reportIds)){
			long reportId=Long.parseLong(reportIds);
			riskReport=riskReportService.findByID(reportId);
		}else{
			riskReport=new t_risk_report();
		}
		
		//用户的信息
		t_user user = null;
		t_user_info userInfo = null;
		t_user_live userLive = null;
		t_user_profession userProfession = null;
		String userIds = params.get("userId");
		if(StringUtils.isNotBlank(userIds)) {
			long userId = Long.parseLong(userIds);
			user = userservice.findByID(userId);
			if(user == null) {
				flash.error("系统出错，请稍后重试");
				showBidPre(2,1,10);
			}
			userInfo = userinfoservice.findUserInfoByUserId(userId);
			if(userInfo == null) {
				userInfo = new t_user_info();
				userInfo.user_id = userId;
			}
			userLive = userLiveService.findByColumn("user_id=?", userId);
			if(userLive == null) {
				userLive = new t_user_live();
				userLive.user_id = userId;
			}
			userProfession = userProfessionService.findByColumn("user_id=?", userId);
			if(userProfession == null) {
				userProfession = new t_user_profession();
				userProfession.user_id = userId;
			}
		}
		
		/** 申请人性别 */
		String sex = params.get("appSex");
		if(StringUtils.isNotBlank(sex)) {
			int sexs = Integer.parseInt(sex);
			userInfo.sex = sexs;
		}
		
		/**申请人婚姻状况*/
		String marriages = params.get("appMarriage");
		if(StringUtils.isNotBlank(marriages)){
			int marriagess = Integer.parseInt(marriages);
			userInfo.marital = marriagess;
		}
		
		/** 最高学历 */
		String applicates = params.get("applicate");
		if(StringUtils.isNotBlank(applicates)){
			int applicatess = Integer.parseInt(applicates);
			userInfo.education = applicatess;
		}
		
		/** 最高学位 */
		String degrees = params.get("appDegree");
		if(StringUtils.isNotBlank(degrees)){
			int degreess = Integer.parseInt(degrees);
			userInfo.degree = degreess;
		}
		
		/** 手机号码 */
		String phones = params.get("degrees");
		if(StringUtils.isNotBlank(phones)){
			userInfo.mobile = phones;
		}
		
		/** 住宅电话 */
		String homePhone = params.get("appHomePhone");
		if(StringUtils.isNotBlank(homePhone)){
			userInfo.home_phone = homePhone;
		}
		
		/** 单位电话 */
		String companyTels = params.get("companyTel");
		if(StringUtils.isNotBlank(companyTels)){
			userInfo.work_phone = companyTels;
		}
		
		/** 通讯地址 */
		String communicationAddress = params.get("appCommunicationAddress");
		if(StringUtils.isNotBlank(communicationAddress)){
			userInfo.communication_address = communicationAddress;
		}
		
		/** 通讯地址邮编 */
		String postalCode = params.get("appPostalCode");
		if(StringUtils.isNotBlank(postalCode)){
			userInfo.postal_code = postalCode;
		}
		
		/** 户籍地址 */
		String censusAddresss = params.get("appCensusAddress");
		if(StringUtils.isNotBlank(censusAddresss)) {
			userInfo.census_address = censusAddresss;
		}
		
		/** 电子邮箱 */
		String emails = params.get("appEmail");
		if(StringUtils.isNotBlank(emails)) {
			userInfo.email = emails;
		}
		
		/** 配偶姓名 */
		String pairNames = params.get("pairName");
		if(StringUtils.isNotBlank(pairNames)) {
			userInfo.mate_name = pairNames;
		}
		
		/** 配偶证件号码 */
		String appMateIdNumber = params.get("appMateIdNumber");
		if(StringUtils.isNotBlank(appMateIdNumber)) {
			userInfo.mate_id_number = appMateIdNumber;
		}
		
		/** 配偶工作单位 */
		String mateWorkUnit = params.get("appMateWorkUnit");
		if(StringUtils.isNotBlank(mateWorkUnit)) {
			userInfo.mate_work_unit = mateWorkUnit;
		}
		
		/** 配偶联系电话 */
		String pairContactWays = params.get("pairContactWay");
		if(StringUtils.isNotBlank(pairContactWays)) {
			userInfo.mate_phone = pairContactWays;
		}
		
		/** 第一联系人姓名 */
		String firstName = params.get("appFirstName");
		if(StringUtils.isNotBlank(firstName)) {
			userInfo.emergency_contact_name = firstName;
		}
		
		/** 第一联系人关系 */
		String firstRelation = params.get("appFirstRelation");
		if(StringUtils.isNotBlank(firstRelation)) {
			int firstRelations = Integer.parseInt(firstRelation);
			userInfo.emergency_contact_type = firstRelations;
		}
		
		/** 第一联系人电话 */
		String firstPhone = params.get("appFirstPhone");
		if(StringUtils.isNotBlank(firstPhone)) {
			userInfo.emergency_contact_mobile = firstPhone;
		}
		
		/** 第二联系人姓名 */
		String secondName = params.get("appSecondName");
		if(StringUtils.isNotBlank(secondName)) {
			userInfo.second_name = firstName;
		}
		
		/** 第二联系人关系 */
		String secondRelation = params.get("appSecondRelation");
		if(StringUtils.isNotBlank(secondRelation)) {
			int secondRelations = Integer.parseInt(secondRelation);
			userInfo.second_relation = secondRelations;
		}
		
		/** 第二联系人电话 */
		String secondPhone = params.get("appSecondPhone");
		if(StringUtils.isNotBlank(secondPhone)) {
			userInfo.second_relation_phone = secondPhone;
		}
		
		/** 是否本月开户 */
		String appAcount = params.get("appAccount");
		if(StringUtils.isNotBlank(appAcount)) {
			int appAcounts = Integer.parseInt(appAcount);
			userInfo.is_account = appAcounts;
		}
		
		/** 从事的职业 */
		String guild = params.get("appGuild");
		if(StringUtils.isNotBlank(guild)) {
			userProfession.profession = guild;
		}
		
		/** 单位名称 */
		String company = params.get("appCompany");
		if(StringUtils.isNotBlank(company)) {
			userProfession.company_name = company;
		}
		
		/** 单位所属行业 */
		String companyTrade = params.get("appCompanyTrade");
		if(StringUtils.isNotBlank(companyTrade)) {
			userProfession.company_trade = companyTrade;
		}
		
		/** 单位地址 */
		String companyAddresses = params.get("companyAddress");
		if(StringUtils.isNotBlank(companyAddresses)) {
			userProfession.company_address = companyAddresses;
		}
		
		/** 单位地址邮编 */
		String companyPostalCodes = params.get("companyPostalCode");
		if(StringUtils.isNotBlank(companyPostalCodes)) {
			userProfession.company_postal_code = companyPostalCodes;
		}
		
		/** 单位工作起始年份 */
		String startWorkTime = params.get("startWorkTime");
		if(StringUtils.isNotBlank(startWorkTime)) {
			userProfession.start_work_time = startWorkTime;
		}
		
		/** 职务 */
		String dutys = params.get("duty");
		if(StringUtils.isNotBlank(dutys)) {
			int dutyss = Integer.parseInt(dutys);
			userProfession.duty = dutyss;
		}
		
		/** 职称 */
		String professionals = params.get("professional");
		if(StringUtils.isNotBlank(professionals)) {
			int professionalss = Integer.parseInt(professionals);
			userProfession.professional = professionalss;
		}
		
		/** 年收入 */
		String annualIncomes = params.get("homeAnnualIncome");
		if(StringUtils.isNotBlank(annualIncomes)) {
			double annualIncomess = Double.parseDouble(annualIncomes);
			userProfession.annual_income = annualIncomess;
		}
		
		/** 服务费费率 */
		String serviceCharge = params.get("serviceCharge");
		if(StringUtils.isNotBlank(serviceCharge)) {
			double serviceCharges = Double.parseDouble(serviceCharge);
			riskReport.service_charge = serviceCharges;
		}
		
		/** 居住地址 */
		String residentialAddresses = params.get("residentialAddress");
		if(StringUtils.isNotBlank(residentialAddresses)) {
			userLive.residential_address = residentialAddresses;
		}
		
		/** 居住地址邮编 */
		String residentialPostalCodes = params.get("residentialPostalCode");
		if(StringUtils.isNotBlank(residentialPostalCodes)) {
			userLive.residential_postal_code = residentialPostalCodes;
		}
		
		/** 居住状况 */
		String residentialConditions = params.get("residentialCondition");
		if(StringUtils.isNotBlank(residentialConditions)) {
			int residentialConditionss = Integer.parseInt(residentialConditions);
			userLive.residential_condition = residentialConditionss;
		}
		
		/** 保存数据 */
		user.save();
		userInfo.save();
		userLive.save();
		userProfession.save();
		
		/**面谈时间*/
		String seeTime = params.get("seeTime");
		if(StringUtils.isNotBlank(seeTime)){
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm");
			try {
				Date seeDate=sdf.parse(seeTime);
				riskReport.see_time=seeDate;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		/**面谈地点*/
		String seeAddress = params.get("seeAddress");
		if(StringUtils.isNotBlank(seeAddress)){
			riskReport.see_address=seeAddress;
			flash.put("seeAddress", seeAddress);
		}
		
		/**申请人姓名*/
		String appName = params.get("appName");
		if(StringUtils.isNotBlank(appName)){
			riskReport.app_name=appName;
			flash.put("appName", appName);
		}
		
		/**申请人性别*/
		String appSex = params.get("appSex");
		if(StringUtils.isNotBlank(appSex)){
			int sexs = Integer.parseInt(appSex);
			if(sexs == 1) {
				riskReport.app_sex = "男";
			}else {
				riskReport.app_sex = "女";
			}
			flash.put("appSex", appSex);
		}
		
		/**申请人年龄*/
		String appAge = params.get("appAge");
		if(StringUtils.isNotBlank(appAge)){
			riskReport.app_age=Integer.parseInt(appAge);
			flash.put("appAge", appAge);
		}
		
		
		/**申请人现住址*/
		String appAddress = params.get("appAddress");
		if(StringUtils.isNotBlank(appAddress)){
			riskReport.app_address=appAddress;
			flash.put("appAddress", appAddress);
		}
		
		/**申请人工作单位*/
		String appCompany = params.get("appCompany");
		if(StringUtils.isNotBlank(appCompany)){
			riskReport.app_company=appCompany;
			flash.put("appCompany", appCompany);
		}
		
		/**风控接待人员*/
		String receptionPerson = params.get("receptionPerson");
		if(StringUtils.isNotBlank(receptionPerson)){
			long rePer=Long.parseLong(receptionPerson);
			riskReport.reception_id=rePer;
			flash.put("receptionPerson", receptionPerson);
		}
		
		/**申请人身份证号*/
		String appCreditNo = params.get("appCreditNo");
		if(StringUtils.isNotBlank(appCreditNo)){
			riskReport.app_creditNo=appCreditNo;
			flash.put("appCreditNo", appCreditNo);
		}
		
		/**申请人所从事的职业*/
		String appGuild = params.get("appGuild");
		if(StringUtils.isNotBlank(appGuild)){
			if(appGuild.equals("0")) {
				riskReport.app_guild = "国家机关、企业、事业单位";
			}else if(appGuild.equals("1")) {
				riskReport.app_guild = "专业技术人员";
			}else if(appGuild.equals("3")) {
				riskReport.app_guild = "办事人员和有关人员";
			}else if(appGuild.equals("4")) {
				riskReport.app_guild = "商业、服务业人员";
			}else if(appGuild.equals("5")) {
				riskReport.app_guild = "农、林、牧、渔、水利业";
			}else if(appGuild.equals("6")) {
				riskReport.app_guild = "生产、运输设备操作人员";
			}else if(appGuild.equals("X")) {
				riskReport.app_guild = "军人";
			}else if(appGuild.equals("Y")) {
				riskReport.app_guild = "其他从业人员";
			}else {
				riskReport.app_guild = "未知";
			}
			flash.put("appGuild", appGuild);
		}
		
		/**申请人工作单位地址*/
		String companyAddress = params.get("companyAddress");
		if(StringUtils.isNotBlank(companyAddress)){
			riskReport.company_address=companyAddress;
			flash.put("companyAddress", companyAddress);
		}
		
		/**申请人工作单位电话*/
		String companyTel = params.get("companyTel");
		if(StringUtils.isNotBlank(companyTel)){
			riskReport.company_tel=companyTel;
			flash.put("companyTel", companyTel);
		}
		
		/**申请人家庭住址*/
		String appHomeAddress = params.get("appHomeAddress");
		if(StringUtils.isNotBlank(appHomeAddress)){
			riskReport.app_home_address=appHomeAddress;
			flash.put("appHomeAddress", appHomeAddress);
		}
		
		/**申请人联系电话*/
		String appPhone = params.get("appPhone");
		if(StringUtils.isNotBlank(appPhone)){
			riskReport.app_phone=appPhone;
			flash.put("appPhone", appPhone);
		}
		
		/**申请人婚姻状况*/
		String appMarriage = params.get("appMarriage");
		if(StringUtils.isNotBlank(appMarriage)){
			int marriagess = Integer.parseInt(appMarriage);
			riskReport.app_marriage = marriagess;
		}
		
		/**申请人配偶姓名*/
		String pairName = params.get("pairName");
		if(StringUtils.isNotBlank(pairName)){
			riskReport.pair_name=pairName;
			flash.put("pairName", pairName);
		}
		
		/**申请人配偶 联系方式*/
		String pairContactWay = params.get("pairContactWay");
		if(StringUtils.isNotBlank(pairContactWay)){
			riskReport.pair_contact_way=pairContactWay;
			flash.put("pairContactWay", pairContactWay);
		}
		
		/**申请人月收入*/
		String appMonthlyIncome = params.get("appMonthlyIncome");
		if(StringUtils.isNotBlank(appMonthlyIncome)){
			double appIncome=Double.parseDouble(appMonthlyIncome);
			riskReport.app_monthly_income=appIncome;
			flash.put("appMonthlyIncome", appMonthlyIncome);
		}
		
		/**申请人配偶月收入*/
		String pairMonthlyIncome = params.get("pairMonthlyIncome");
		if(StringUtils.isNotBlank(pairMonthlyIncome)){
			double pairIncome=Double.parseDouble(pairMonthlyIncome);
			riskReport.pair_monthly_income=pairIncome;
			flash.put("pairMonthlyIncome", pairMonthlyIncome);
		}
		
		/**申请人家庭年收入*/
		String homeAnnualIncome = params.get("homeAnnualIncome");
		if(StringUtils.isNotBlank(homeAnnualIncome)){
			double homeIncome=Double.parseDouble(homeAnnualIncome);
			riskReport.home_annual_income=homeIncome;
			flash.put("homeAnnualIncome", homeAnnualIncome);
		}
		
		/**申请人借款用途*/
		String loanPurpose = params.get("loanPurpose");
		if(StringUtils.isNotBlank(loanPurpose)){
			riskReport.loan_purpose=loanPurpose;
			flash.put("loanPurpose", loanPurpose);
		}
		
		/**申请人借款数额*/
		String loanAmount = params.get("loanAmount");
		if(StringUtils.isNotBlank(loanAmount)){
			double loanAmounts=Double.parseDouble(loanAmount);
			riskReport.loan_amount=loanAmounts;
			flash.put("loanAmount", loanAmount);
		}
		
		/**申请人借款品种*/
		String loanKind = params.get("loanKind");
		if(StringUtils.isNotBlank(loanKind)){	
			riskReport.loan_kind=loanKind;
			flash.put("loanKind", loanKind);
		}
		
		/**申请人借款品种*/
		String loanClearKind = params.get("loanClearKind");
		if(StringUtils.isNotBlank(loanClearKind)){	
			riskReport.loan_clear_kind=loanClearKind;
			flash.put("loanClearKind", loanClearKind);
		}
		
		/**申请人劳动力报酬*/
		String laborReward = params.get("laborReward");
		if(StringUtils.isNotBlank(laborReward)){	
			double laborRewards=Double.parseDouble(laborReward);
			riskReport.labor_reward=laborRewards;
			flash.put("laborReward", laborReward);
		}
		
		/**筹款时间（天）*/
		String loanTimeStr = params.get("loanTime");
		if(StringUtils.isNotBlank(loanTimeStr)){
			int loanTime=Integer.parseInt(loanTimeStr);
			riskReport.loan_time=loanTime;
			flash.put("loanTime", loanTime);
		}
		
		/**申请人收入来源有几处*/
		String incomeKinds = params.get("incomeKinds");
		if(StringUtils.isNotBlank(incomeKinds)){	
			int incomeKind=Integer.parseInt(incomeKinds);
			riskReport.income_kinds=incomeKind;
			flash.put("incomeKinds", incomeKinds);
		}
		
		/**申请人还款来源*/
		String backResource = params.get("backResource");
		if(StringUtils.isNotBlank(backResource)){	
			riskReport.back_resource=backResource;
			flash.put("backResource", backResource);
		}
		
		/**申请人房产情况*/
		String houseCondition = params.get("houseCondition");
		if(StringUtils.isNotBlank(houseCondition)){	
			riskReport.house_condition=houseCondition;
			flash.put("houseCondition", houseCondition);
		}
		
		/**申请人车产情况*/
		String vehicleCondition = params.get("vehicleCondition");
		if(StringUtils.isNotBlank(vehicleCondition)){	
			riskReport.vehicle_condition=vehicleCondition;
			flash.put("vehicleCondition", vehicleCondition);
		}
		
		/**申请人其他固定资产*/
		String otherfixedCondition = params.get("otherfixedCondition");
		if(StringUtils.isNotBlank(otherfixedCondition)){	
			riskReport.otherfixed_condition=otherfixedCondition;
			flash.put("otherfixedCondition", otherfixedCondition);
		}
		
		/**申请人担保方式*/
		String guarantyKind = params.get("guarantyKind");
		if(StringUtils.isNotBlank(guarantyKind)){	
			riskReport.guaranty_kind=guarantyKind;
			flash.put("guarantyKind", guarantyKind);
		}
		
		/**保证人姓名*/
		String guarantorName = params.get("guarantorName");
		if(StringUtils.isNotBlank(guarantorName)){	
			riskReport.guarantor_name=guarantorName;
			flash.put("guarantorName", guarantorName);
		}
		
		/**担保金额*/
		String guaranteeAmount = params.get("guaranteeAmount");
		if(StringUtils.isNotBlank(guaranteeAmount)){
			double guaranteeAmounts=Double.parseDouble(guaranteeAmount);
			riskReport.guarantee_amount=guaranteeAmounts;
			flash.put("guaranteeAmount", guaranteeAmount);
		}
		
		/**抵押物种类*/
		String pledgeKind = params.get("pledgeKind");
		if(StringUtils.isNotBlank(pledgeKind)){
			riskReport.pledge_kind=pledgeKind;
			flash.put("pledgeKind", pledgeKind);
		}
		
		/**抵押物权属*/
		String pledgeOwnership = params.get("pledgeOwnership");
		if(StringUtils.isNotBlank(pledgeOwnership)){
			byte pledgeOwnerships=Byte.parseByte(pledgeOwnership);
			if(pledgeOwnerships==0){
				riskReport.pledge_ownership=false;
				flash.put("pledgeOwnership", pledgeOwnership);
			}else{
				riskReport.pledge_ownership=true;
				flash.put("pledgeOwnership", pledgeOwnership);
			}
		}
		
		/**抵押物评估价格*/
		String evaluatePrice = params.get("evaluatePrice");
		if(StringUtils.isNotBlank(evaluatePrice)){	
			double evaluatePrices=Double.parseDouble(evaluatePrice);
			riskReport.evaluate_price=evaluatePrices;
			flash.put("evaluatePrice", evaluatePrice);
		}
		
		/**贷款期限*/
		String loanTimeLimit = params.get("loanTimeLimit");
		if(StringUtils.isNotBlank(loanTimeLimit)){
			riskReport.loan_time_limit=loanTimeLimit;
			flash.put("appAge", loanTimeLimit);
		}
		
		/**贷款年利率*/
		String annualRate = params.get("annualRate");
		if(StringUtils.isNotBlank(annualRate)){
			riskReport.annual_rate=annualRate;
			flash.put("annualRate", annualRate);
		}
		
		
		/**申请人贷款历史*/
		String isLoanPass = params.get("isLoanPass");
		if(StringUtils.isNotBlank(isLoanPass)){
			byte isLoanPasses=Byte.parseByte(isLoanPass);
			if(isLoanPasses==0){
				riskReport.is_loan_pass=false;
				flash.put("isLoanPass", isLoanPass);
			}else{
				riskReport.is_loan_pass=true;
				flash.put("isLoanPass", isLoanPass);
			}
		}
		
		/**申请人履行还款情况（有贷款历史的申请人）*/
		String backloanCondition = params.get("backloanCondition");
		if(StringUtils.isNotBlank(backloanCondition)){
			riskReport.backloan_condition=backloanCondition;
			flash.put("backloanCondition", backloanCondition);
		}
		
		/**该单所属分公司*/
		String branchCompany = params.get("branchCompany");
		if(StringUtils.isNotBlank(branchCompany)){
			long branchId=Long.parseLong(branchCompany);
			riskReport.branch_id=branchId;
			flash.put("branchCompany", branchCompany);
		}
		
		/**申请人学历*/
		String applicate = params.get("applicate");
		if(StringUtils.isNotBlank(applicate)){
			if(applicate.equals("10")) {
				riskReport.applicate = "研究生";
			}else if(applicate.equals("20")) {
				riskReport.applicate = "本科";
			}else if(applicate.equals("30")) {
				riskReport.applicate = "大专";
			}else if(applicate.equals("40")) {
				riskReport.applicate = "中专";
			}else if(applicate.equals("50")) {
				riskReport.applicate = "技校";
			}else if(applicate.equals("60")) {
				riskReport.applicate = "高中";
			}else if(applicate.equals("70")) {
				riskReport.applicate = "初中";
			}else if(applicate.equals("80")) {
				riskReport.applicate = "小学";
			}else if(applicate.equals("90")) {
				riskReport.applicate = "文盲";
			}else {
				riskReport.applicate = "未知";
			}
			flash.put("applicate", applicate);
		}
		
		/**申请人信用卡总额度*/
		String creditCardLimit = params.get("creditCardLimit");
		if(StringUtils.isNotBlank(creditCardLimit)){
			Double limit=Double.parseDouble(creditCardLimit);
			riskReport.credit_card_limit=limit;
			flash.put("creditCardLimit", creditCardLimit);
		}
		
		/**申请人银行负债情况*/
		String bankDebt = params.get("bankDebt");
		if(StringUtils.isNotBlank(bankDebt)){
			riskReport.bank_debt=bankDebt;
			flash.put("bankDebt", bankDebt);
		}
		
		/**申请人民间负债情况*/
		String folkDebt = params.get("folkDebt");
		if(StringUtils.isNotBlank(folkDebt)){
			riskReport.folk_debt=folkDebt;
			flash.put("folkDebt", folkDebt);
		}
		
		/**申请人贷款记录*/
		String debtRecord = params.get("debtRecord");
		if(StringUtils.isNotBlank(debtRecord)){
			riskReport.debt_record=debtRecord;
			flash.put("debtRecord", debtRecord);
		}
		
		/**申请人征信情况*/
		String creditCondition[] = params.getAll("creditCondition");
		if(creditCondition!=null && creditCondition.length>0){
			riskReport.credit_condition=creditCondition[0];
			flash.put("creditCondition", creditCondition);
		}
		
		/**借款说明*/
		String loanState=params.get("loanState");
		if(StringUtils.isNotBlank(loanState)){
			riskReport.loan_state=loanState;
			flash.put("loanState", loanState);
		}
		
		/**勘察师意见*/
		String techOpinion=params.get("techOpinion");
		if(StringUtils.isNotBlank(techOpinion)){
			riskReport.tech_opinion=techOpinion;
			flash.put("techOpinion", techOpinion);
		}
		
		/** 居住年限 */
		String residentYears = params.get("residentYear");
		if(StringUtils.isNotBlank(residentYears)) {
			riskReport.resident_year = residentYears;
			flash.put("residentYears", residentYears);
		}
		
		/** 用户id */
		riskReport.user_id = Long.parseLong(userIds);
		
		/** 担保人用户Id */
		long guarantee_user = Long.parseLong(params.get("guarantee_user"));
		/*if(guarantee_user!=0) {
			riskReport.guarantee_user = guarantee_user;
			flash.put("guarantee_user", guarantee_user);
		}*/
		riskReport.guarantee_user = guarantee_user;
		flash.put("guarantee_user", guarantee_user);
		
		/**判断是保存还是提交*/
		String flag=params.get("flag");
		if(StringUtils.isNotBlank(flag)){
			riskReport.status = -1;
			record.handle_content="保存了风控报告";
			if(StringUtils.isNotBlank(reportIds)){
				record.handle_content="修改并保存了风控报告";
			}
			
		}else{
			riskReport.status = 0;
			record.handle_content="提交了风控报告";
			if(StringUtils.isNotBlank(reportIds)){
				record.handle_content="修改并提交了风控报告";
			}
		}
		t_risk_report report = null;
		if(reportIds==null){
			riskReport.time=new Date();
		/**保存状态*/
			riskReport.save();
		/**得到刚刚保存的风控报告*/
			report=riskReportService.getLastReport();
		}else{
			riskReport.save();
			report=riskReport;
		}
		CurrSupervisor cursuper=getCurrSupervisor();
		record.superName=cursuper.name;
		record.risk_handler=cursuper.reality_name;
		record.risk_id=report.id;
		record.handle_time=new Date();
		record.save();
		/**保存图片路径*/
		if(report!=null){
			riskPicService.deletePicsByRiskId(report.id);
			String array = params.get("filename");
			  if (array != null && !array.equals("") && !array.equals("0")) {
		            String arr[] = array.split("\\+");

		            for (int i = 0; i < arr.length; i++) {
		               t_risk_pic riskpic = new t_risk_pic();
		                riskpic.pic_path = arr[i];
		                riskpic.risk_id=report.id;
		                riskpic.type=1;
		                riskpic.time=new Date();
		                riskpic.save();
		            }
		        }
			  String arrays = params.get("filenames");
			  if (arrays != null && !arrays.equals("") && !arrays.equals("0")) {
		            String arrs[] = arrays.split("\\+");

		            for (int i = 0; i < arrs.length; i++) {
		               t_risk_pic riskpic = new t_risk_pic();
		                riskpic.pic_path = arrs[i];
		                riskpic.risk_id=report.id;
		                riskpic.type=2;
		                riskpic.time=new Date();
		                riskpic.save();
		            }
		        }
			  String sarray = params.get("sfilename");
			  if (sarray != null && !sarray.equals("") && !sarray.equals("0")) {
		            String sarr[] = sarray.split("\\+");

		            for (int i = 0; i < sarr.length; i++) {
		               t_risk_pic riskpic = new t_risk_pic();
		                riskpic.pic_path = sarr[i];
		                riskpic.risk_id=report.id;
		                riskpic.type=3;
		                riskpic.time=new Date();
		                riskpic.save();
		            }
		        }
			  String sarrays = params.get("sfilenames");
			  if (sarrays != null && !sarrays.equals("") && !sarrays.equals("0")) {
		            String sarr[] = sarrays.split("\\+");

		            for (int i = 0; i < sarr.length; i++) {
		               t_risk_pic riskpic = new t_risk_pic();
		                riskpic.pic_path = sarr[i];
		                riskpic.risk_id=report.id;
		                riskpic.type=4;
		                riskpic.time=new Date();
		                riskpic.save();
		            }
		        }
			  
			  //房本或车本图片添加
			  String permitImages = params.get("permitImages");
			  if (permitImages != null && !permitImages.equals("") && !permitImages.equals("0")) {
		            String permitSarr[] = permitImages.split("\\+");

		            for (int i = 0; i < permitSarr.length; i++) {
		               t_risk_pic riskpic = new t_risk_pic();
		                riskpic.pic_path = permitSarr[i];
		                riskpic.risk_id=report.id;
		                riskpic.type=-1;
		                riskpic.time=new Date();
		                riskpic.save();
		            }
		        }
			  
			  //身份证图片添加
			  String cardImages = params.get("cardImages");
			  if (cardImages != null && !cardImages.equals("") && !cardImages.equals("0")) {
		            String cardSarr[] = cardImages.split("\\+");

		            for (int i = 0; i < cardSarr.length; i++) {
		               t_risk_pic riskpic = new t_risk_pic();
		                riskpic.pic_path = cardSarr[i];
		                riskpic.risk_id=report.id;
		                riskpic.type=-2;
		                riskpic.time=new Date();
		                riskpic.save();
		            }
		        }
			  
			  //行驶证图片添加
			  String driveImages = params.get("driveImages");
			  if (driveImages != null && !driveImages.equals("") && !driveImages.equals("0")) {
		            String driveSarr[] = driveImages.split("\\+");

		            for (int i = 0; i < driveSarr.length; i++) {
		               t_risk_pic riskpic = new t_risk_pic();
		                riskpic.pic_path = driveSarr[i];
		                riskpic.risk_id=report.id;
		                riskpic.type=5;
		                riskpic.time=new Date();
		                riskpic.save();
		            }
		        }
			  
			  /** 借款人征信报告图片添加 */
			  String creditImages = params.get("creditImages");
			  if (creditImages != null && !creditImages.equals("") && !creditImages.equals("0")) {
		            String creditSarr[] = creditImages.split("\\+");

		            for (int i = 0; i < creditSarr.length; i++) {
		               t_risk_pic riskpic = new t_risk_pic();
		                riskpic.pic_path = creditSarr[i];
		                riskpic.risk_id=report.id;
		                riskpic.type=6;
		                riskpic.time=new Date();
		                riskpic.save();
		            }
		        }
			  
			  String reportIdss = params.get("reportId");
			  if(StringUtils.isBlank(reportIdss) || StringUtils.isBlank(flag)) {
				  /**保存风控意见*/
					String riskOpinion = params.get("riskOpinion");
					if(StringUtils.isNotBlank(riskOpinion)){
						t_risk_suggest suggest=new t_risk_suggest();
						suggest.risk_id=report.id;
						suggest.suggest=riskOpinion;
						suggest.time=new Date();
						suggest.type=1;
						suggest.supervisorId=getCurrentSupervisorId();
						suggest.save();
					}
			  }
		}
		if(StringUtils.isNotBlank(flag)){
			flash.success("风控报告保存成功！");
		}else{
			flash.success("风控报告提交成功！");
		}
		showBidPre(2,1,10);
	}
	
	/**
	 * 到审核界面
	 * 
	 * @createDate 2017年4月26日
	 * @author lihuijun
	 */
	public static void toPreAuditRiskReportPre(long reportId){
		
		List<t_risk_pic> riskPicList=riskPicService.getRiskPicByRiskId(reportId,null);
		t_risk_report report=riskReportService.findByID(reportId);
		List<t_risk_suggest> suggestList=riskSuggestService.findListByColumn("risk_id=? and type=?", reportId,1);
		List<t_risk_suggest> suggests=riskSuggestService.findListByColumn("risk_id=? and type=?", reportId,2);
		List<t_risk_handle_record> recordList=riskRecordService.findListByColumn("risk_id=?", reportId);
	
		render(report,riskPicList,suggestList,suggests,recordList);
		
	}
	
	/**
	 * 查询类型是type的报告图片
	 * @param rsikId
	 * @createDate 2017年4月28日
	 * @author lihuijun
	 */
	public static void showPictureCarouselPre(long riskId,int type) {
    	List<t_risk_pic> riskPics = riskPicService.getRiskPicByRiskId(riskId,type);
    	int riskCount = 0;
    	if(riskPics != null){
    		riskCount = riskPics.size();
    	}
    	render(riskPics, riskCount);
    }
	
	/**
	 * 审核风控报告
	 * 
	 * @createDate 2017年4月28日
	 * @author lihuijun
	 */
	public static void preAuditReport(){
		
		String loanIdStr = params.get("loanId");
		if(StringUtils.isNotBlank(loanIdStr)){
			long loanId = Integer.parseInt(loanIdStr);
			t_risk_report report=riskReportService.findByID(loanId);
			if(report!=null){	
				String riskStatusStr = params.get("riskStatus");
				if(StringUtils.isNotBlank(riskStatusStr)){
					long superId=getCurrentSupervisorId();
					String suggestStr = params.get("suggest");
					if(StringUtils.isNotBlank(suggestStr)){
						t_risk_suggest suggest=new t_risk_suggest();
						suggest.risk_id=report.id;
						suggest.suggest=suggestStr;
						suggest.time=new Date();
						suggest.type=2;
						suggest.supervisorId=superId;
						suggest.save();
					}
					t_risk_handle_record record=new t_risk_handle_record();
					CurrSupervisor cursuper=getCurrSupervisor();
					record.risk_id=report.id;
					record.superName=cursuper.name;
					record.risk_handler=cursuper.reality_name;
					record.handle_time=new Date();
					switch (riskStatusStr) {
						case "1":
							report.status=1;
							report.save();
							record.handle_content="审核通过";
							record.save();
							flash.success("审核通过！");
							showBidPre(3, 1, 10);
							break;
							
						case "2":
							report.status=2;
							report.save();
							record.handle_content="审核未通过";
							record.save();
							flash.success("审核未通过！");
							showBidPre(4, 1, 10);
							break;
						case "3":
							String loanAmountStr = params.get("loanAmount");
							if(StringUtils.isNotBlank(loanAmountStr)){
								Double loanAmount=Double.parseDouble(loanAmountStr);
								report.loan_amount=loanAmount;
							}
							report.status=3;
							report.save();
							record.handle_content="打回了报告";
							record.save();
							flash.success("打回，等待处理！");
							showBidPre(2, 1, 10);
							break;	
						default:
							report.status=0;
							showBidPre(2, 1, 10);
							break;
					}
				
				}	
			}
		}
		showBidPre(2, 1, 10);
	}
	
	/**
	 * 编辑
	 * @param reportId
	 * @createDate 2017年5月2日
	 * @author lihuijun
	 */
	public static void toEditRiskReportPre(long reportId){
		t_risk_report report=null;
		List<t_risk_pic> picList=null;
		List<t_risk_pic> picLists=null;
		List<t_risk_pic> spicList=null;
		List<t_risk_pic> spicLists = null;
		List<t_risk_pic> cardImageList=null;
		List<t_risk_pic> permitImageList=null;
		List<t_risk_pic> driveImageList=null;
		/** 借款人征信List */
		List<t_risk_pic>  creditImageList = null;
		t_user users = null;
		t_user_info userInfo = null;
		t_user_profession userProfession = null;
		t_user_live userLive = null;
		StringBuffer bimgUrl=null;
		int picCount=0;
		StringBuffer bimgUrls=null;
		int picCounts=0;
		StringBuffer sbimgUrl=null;
		int spicCount=0;
		StringBuffer sbimgUrls=null;
		int spicCounts=0;
		StringBuffer cardImageUrl=null;
		int cardImageCount=0;
		StringBuffer permitImageUrl=null;
		int permitImageCount=0;
		StringBuffer driveImageUrl=null;
		int driveImageCount=0;
		/** 借款人征信图片地址 */
		StringBuffer creditImageUrl=null;
		/** 借款人征信图片数量 */
		int creditImageCount=0;
		
		int showType=1;
		if(reportId>0){
			report=riskReportService.findByID(reportId);
			picList=riskPicService.findListByColumn("risk_id=? and type=?", reportId,1);
			     picCount = picList.size();
		         bimgUrl = new StringBuffer("");
		        for (int i = 0; i < picList.size(); i++) {
		            if (i == (picList.size() - 1)) {
		                bimgUrl.append(picList.get(i).pic_path);
		            } else {
		                bimgUrl.append(picList.get(i).pic_path + "+");
		            }
		        }
			picLists=riskPicService.findListByColumn("risk_id=? and type=?", reportId,2);
				picCounts = picLists.size();
		         bimgUrls = new StringBuffer("");
		        for (int i = 0; i < picLists.size(); i++) {
		            if (i == (picLists.size() - 1)) {
		                bimgUrls.append(picLists.get(i).pic_path);
		            } else {
		                bimgUrls.append(picLists.get(i).pic_path + "+");
		            }
		        }
	        spicList=riskPicService.findListByColumn("risk_id=? and type=?", reportId,3);
			spicCount = spicList.size();
	         sbimgUrl = new StringBuffer("");
	        for (int i = 0; i < spicList.size(); i++) {
	            if (i == (spicList.size() - 1)) {
	                sbimgUrl.append(spicList.get(i).pic_path);
	            } else {
	                sbimgUrl.append(spicList.get(i).pic_path + "+");
	            }
	        } 
	        spicLists=riskPicService.findListByColumn("risk_id=? and type=?", reportId,4);
	        spicCounts = spicLists.size();
	         sbimgUrls = new StringBuffer("");
	        for (int i = 0; i < spicLists.size(); i++) {
	            if (i == (spicLists.size() - 1)) {
	                sbimgUrls.append(spicLists.get(i).pic_path);
	            } else {
	                sbimgUrls.append(spicLists.get(i).pic_path + "+");
	            }
	        } 
	        
	        cardImageList=riskPicService.findListByColumn("risk_id=? and type=?", reportId,-2);
	        cardImageCount = cardImageList.size();
			cardImageUrl = new StringBuffer("");
	        for (int i = 0; i < cardImageList.size(); i++) {
	            if (i == (cardImageList.size() - 1)) {
	            	cardImageUrl.append(cardImageList.get(i).pic_path);
	            } else {
	            	cardImageUrl.append(cardImageList.get(i).pic_path + "+");
	            }
	        }
	        
	        permitImageList=riskPicService.findListByColumn("risk_id=? and type=?", reportId,-1);
	        permitImageCount = permitImageList.size();
			permitImageUrl = new StringBuffer("");
	        for (int i = 0; i < permitImageList.size(); i++) {
	            if (i == (permitImageList.size() - 1)) {
	            	permitImageUrl.append(permitImageList.get(i).pic_path);
	            } else {
	            	permitImageUrl.append(permitImageList.get(i).pic_path + "+");
	            }
	        }
	        
	        driveImageList=riskPicService.findListByColumn("risk_id=? and type=?", reportId,5);
	        driveImageCount = driveImageList.size();
			driveImageUrl = new StringBuffer("");
	        for (int i = 0; i < driveImageList.size(); i++) {
	            if (i == (driveImageList.size() - 1)) {
	            	driveImageUrl.append(driveImageList.get(i).pic_path);
	            } else {
	            	driveImageUrl.append(driveImageList.get(i).pic_path + "+");
	            }
	        }
	        
	        /** 借款人征信报告图片 */
	        creditImageList=riskPicService.findListByColumn("risk_id=? and type=?", reportId,6);
	        creditImageCount = creditImageList.size();
	        creditImageUrl = new StringBuffer("");
	        for (int i = 0; i < creditImageList.size(); i++) {
	            if (i == (creditImageList.size() - 1)) {
	            	creditImageUrl.append(creditImageList.get(i).pic_path);
	            } else {
	            	creditImageUrl.append(creditImageList.get(i).pic_path + "+");
	            }
	        }
	        
	        
	        
	        users = userservice.findByID(report.user_id);
	        userInfo = userinfoservice.findByColumn("user_id=?", report.user_id);
	        userLive = userLiveService.findByColumn("user_id=?", report.user_id);
	        userProfession = userProfessionService.findByColumn("user_id=?", report.user_id);
		}
		List<t_risk_reception> riskList=riskReceptionService.findAll();
		List<t_company_branch> branchs=branchService.findAll();
		//所有担保人
		List<t_guarantee> guarantees = guaranteeService.findAll();
		
		render(showType,riskList,report,picList,picLists,spicList,spicLists,bimgUrl,
				picCount,bimgUrls,picCounts,sbimgUrl,spicCount,spicCounts,sbimgUrls,
				branchs,users,userInfo,userLive,userProfession,guarantees,cardImageList,
				permitImageList,driveImageList,creditImageList,cardImageCount,permitImageCount,driveImageCount,
				creditImageCount,cardImageUrl,permitImageUrl,driveImageUrl,creditImageUrl);
	}
	
	/**
	 * 发标时选择风控报告
	 * @param reportId
	 * @createDate 2017年5月2日
	 * @author lihuijun
	 */
	public static void selectReport(long reportId){
		ResultInfo result=new ResultInfo();
		Map<String,String> msgs=new HashMap();
		if(reportId>0){
			t_risk_report report=riskReportService.findByID(reportId);
			if(report!=null){
				msgs.put("annualRate", report.annual_rate);	
				if(report.loan_amount!=null){
					double loanAmount=report.loan_amount;	
					msgs.put("loanAmount", (int)loanAmount+"");
				}
				msgs.put("timeLimit", report.loan_time_limit);
				msgs.put("loanPurpose",report.loan_purpose);
				msgs.put("loanTime", report.loan_time+""); 
				msgs.put("loanState", report.loan_state);
				msgs.put("selectReport", report.service_charge +"");
				msgs.put("guaranteeUser", report.guarantee_user +"");//担保方上上签用户id
				
				msgs.put("repayment_type",report.repayment_type +"");  //还款方式	
				msgs.put("bor_type", report.bor_type +"");   //借款类型
				msgs.put("throw_industry", report.throw_industry +"");     //投向行业
				msgs.put("site", report.site+"");   //借款地点
				msgs.put("business_type", report.business_type+"");		//业务类型
			}
			result.msgs=msgs;
			renderJSON(result);
		}else{
			renderJSON(result);
		}
	}
	
	
	/**
	 * 标的台账信息查询
	 * 
	 * @author niu
	 * @create 2017.10.09
	 */
	public static void bidAccount(long bidId) {
		
		
		
		
		
		
		
		
		
		
	}
	
	/**
	 * 后台-风控-理财项目-消息推送
	 * 
	 * @param sign 标加密id
	 * @author guoshijie
	 * @createdate 2017.12.12
	 * */
	public static void projectPush(String sign) {
		ResultInfo result = new ResultInfo();
		
		/**解密*/
		long bidId = decodeSign(sign, true);
		t_bid tb = null;
		if (bidId > 1) {
			tb = bidservice.findByID(bidId);
		}
		
		/**加密*/
		String bidIdSign = Security.addSign(bidId, Constants.BID_ID_SIGN, ConfConst.ENCRYPTION_APP_KEY_DES);
		String text = "讴业普惠   "+tb.bid_no+" "+tb.title+"标的上线，数量有限，速抢！>>";
		if (tb != null) {
			
			/**推送*/
			int androidResult = PushDemo.androidBroadcastPush("58b3c1368f4a9d01ae001623", "93g9etyokqjwhlgonwkfxmevrjk2wxb3", "新标上线", text, "id", tb.bid_no.substring(1,tb.bid_no.length()));
			int iosResult = PushDemo.iosBroadcastPush("58c752381061d264c4001ad2", "gehsqjgokosr48rvxkrkzjycpq9axofs", tb.title, text, "bidIdSign", bidIdSign);
			
			if (androidResult == 1 && iosResult == 1) {
				result.msg = "发送成功！";
			} else if (androidResult != 1 && iosResult != 1) {
				
				result.msg = "发送失败！";
			} else {
				if(androidResult != 1) {
					
					result.msg = "安卓发送失败！";
				} else {
					if(iosResult != 1) {
						
						result.msg = "IOS发送失败！";
					}
				}
			}
		}
		renderJSON(result);
		
	}
	
	/**
	 * 获取官方公告模板
	 * @param bidNo 标的编号
	 * @param headTitle 项目标题
	 * @param saleTime 发售时间
	 * @param amount 项目金额(借款金额)
	 * @param period 项目期限
	 * @param annualConvert 年化利率
	 * @param repaymentMethod 还款方式
	 * 
	 * @author guoShiJie
	 * @createDate 2018.04.28
	 * */
	public static long getOfficialInformationModel(String bidNo,String headTitle, String saleTime, Double amount, String period, String annualConvert, String repaymentMethod) {
		t_template_official_notice template = new t_template_official_notice();
		
		/**1 项目名称 */
		StringBuffer title = new StringBuffer();
		if (bidNo != null && !bidNo.equals("")) {
			title.append("【"+getBorrowingType(headTitle)+"】");
			title.append(bidNo+"号");
		}
		template.projectNameKey = "项目名称";
		template.projectNameValue = title.toString();
		/**2 发标时间 */
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date d = null;
		try {
			d = sdf1.parse(saleTime);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String saleBidTime = null;
		if (saleTime != null  && !saleTime.isEmpty() ) {
			SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
			saleBidTime = sdf3.format(d);
			template.saleBidTimeKey = "发标时间";
			template.saleBidTimeValue = saleBidTime;
		}
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日");
		String dtime = sdf2.format(d);
		template.date = dtime;//发标时间"yyyy年MM月dd日"
		/**3 发标金额 */
		String bidAmount = null;
		if (amount != null) {
			Double dd = amount/10000.00;
			bidAmount = dd.toString()+"万";
		}
		template.saleBidAmountKey = "发标金额";
		template.saleBidAmountValue = bidAmount;
		/**4 项目期限 */
		String periodTime = null;
		if (period != null) {
			periodTime = period;
		}
		template.projectTimeLimitKey = "项目期限";
		template.projectTimeLimitValue = periodTime;
		/**5 年化利率 */
		template.annualConvertKey = "年化利率";
		template.annualConvertValue = annualConvert;
		/**6 月还利息 */
		String money = null;
		Double monthInterest = 0.0;
		Double annual = 0.0;
		if (annualConvert != null) {
			String[] convert = annualConvert.split("%");
			String a = convert[0];
			annual = Double.parseDouble(a);
		}
		if (annualConvert != null) {
			monthInterest = amount*annual/100.0/12;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		money = df.format(monthInterest);
		template.monthInterestKey = "月还利息";
		template.monthInterestValue = monthInterest==null?null:money;
		/**7 借款类型 */
		String type = null;
		if (headTitle != null) {
			type = getBorrowingType(headTitle).toString();
			template.borrowingTypeKey = "借款类型";
			template.borrowingTypeValue = type;
		}
		
		/**8 还款方式 */
		String method = null;
		if (repaymentMethod != null) {
			method = ReturnType.getEnum(repaymentMethod).key.equals("先息后本") ? "先息后本,按月还息" : ReturnType.getEnum(repaymentMethod).key;
		}
		template.repaymentMethodKey = "还款方式";
		template.repaymentMethodValue = method;
		
		/** 栏目 */
		template.column_key = "info_bulletin";
		template.column_name = "官方公告";
		
		/** 标题 */
		if (bidNo != null && !bidNo.equals("")) {
			StringBuffer bigTitle = new StringBuffer();
			bigTitle.append(bidNo+"号");
			bigTitle.append(getBorrowingType(headTitle));
			bigTitle.append("发标公告");
			template.title = bigTitle.toString();
		}
		
		/**借款描述*/
		long bidId = 0;
		if (bidNo != null && !bidNo.equals("")) {
			String[] bidArray = bidNo.split("J");
			String bidNum = bidArray[1];
			bidId = Long.parseLong(bidNum);
		}
		String description = bidservice.findByID(bidId).description;
		template.loanDescription = description;
		
		/**上午；下午*/
		if (saleBidTime != null) {
			String day = null;
			String[] t = saleBidTime.split(":");
			String hour = t[0];
			Integer hours = Integer.parseInt(hour);
			if (hours >= 12) {
				day = "下午";
			} else {
				day = "上午";
			}
			template.amOrPm = day;
		}
		
		if (getBorrowingType(headTitle).toString().equals("惠房贷")) {
			template.fixedContent1 = "经核查，借款人征信良好，无逾期记录，无民间借贷，提供资料真实有效，具有按期偿付本息的能力。";
			template.fixedContent2 = "经讴业普惠风控人员现场调查核实，借款人个人征信以及资产状况良好，经营收入稳定，具有按月付息，到期还本的能力，符合讴业普惠借款审核标准。";
		}else if (getBorrowingType(headTitle).toString().equals("惠车贷")) {
			template.fixedContent1 = "借款人收入稳定，提供信息真实有效，征信良好，无不良记录，具有按期还款能力.";
		}
		template.fixedContent3 = "为满足广大投资用户的投资需求，平台将不断推出优质新标，请广大投资用户随时关注平台发标情况，谢谢！";
		template.fixedContent4 = "详情请咨询客服热线： 400-901-8889";
		template.fixedContent5 = "讴业普惠 运营中心";
		template.fixedContent6 = "尊敬的讴业普惠用户:";
		
		/** 图片 */
		File fileImg = Play.getFile("public"+File.separator+"back"+File.separator+"images"+File.separator+"officialNotice.jpg");
		ResultInfo result = FileUtil.uploadImgags(fileImg);
		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		String imgUrl = (String)fileInfo.get("staticFileName");
		/**图片地址*/
		template.image_url = imgUrl;
		/**图片分辨率*/
		String resolution = (String)fileInfo.get("imageResolution");
		template.image_resolution = resolution;
		/**图片大小*/
		String size = (Double)fileInfo.get("size")+"kb";
		template.image_size = size;
		/**图片格式*/
		String format = (String)fileInfo.get("fileSuffix");
		template.image_format = format;
		
		Date date = new Date();
		
		template.sort_time = date;
		template.release_time = date;
		template.time =  date;
		/** 模板来源 */
		template.source_from = "讴业普惠客服";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		Date ddd = new Date();
		String dttime = sdf.format(ddd);
		template.currentDate = dttime;
		template.save();
		return templateOfficialNoticeService.selectLastTemplateOfficialNotice();
	}
	
	/**
	 * 生成官方公告
	 * 
	 * @param bidNo 标的编号
	 * @param img 图片路径 
	 * @param imgURL 项目图片路径
	 * @param headTitle 项目标题
	 * 
	 * @author guoShiJie
	 * @createDate 2018.4.28
	 * */
	public static t_information createOfficialNotice(long templateId) {
		
		t_information info = new t_information();
		info.template_official_notice_id = templateId;
		t_template_official_notice template = templateOfficialNoticeService.findByID(templateId);
		if (template != null) {
			info.column_key = template.column_key;
			info.column_name = template.column_name;
			/**标题*/
			info.title = template.title;
			/**内容*/
			StringBuffer con = new StringBuffer();
			if (template.fixedContent6 != null) {
				con.append("<p>"+template.fixedContent6+"</p>");
			}
			if (template.date != null && template.saleBidTimeValue != null && template.amOrPm != null) {
				con.append("<p>您好!"+template.date+template.amOrPm+template.saleBidTimeValue+"项目安排如下:</p>");
			}
			con.append("<p>");
			con.append("<table>");
			for(int i = 0 ;i < 8 ;i++){
				String key = "";
				String value = "";
				if (i == 0) {
					if (template.projectNameValue != null) {
						value = template.projectNameValue;
					}
					key = template.projectNameKey;
					con.append("<tr>");
					con.append("<td class = td1 style = color:white >"+key+"</td>");
					con.append("<td class = td1 style = color:white >"+value+"</td>");
					con.append("</tr>");
				} else if (i == 1) {
					if (template.saleBidTimeValue != null) {
						value = template.saleBidTimeValue;
					}
					key = template.saleBidTimeKey;
					con.append("<tr>");
					con.append("<td class = td2>"+key+"</td>");
					con.append("<td class = td2>"+value+"</td>");
					con.append("</tr>");
				} else if (i == 2) {
					if (template.saleBidAmountValue != null) {
						value = template.saleBidAmountValue+"元";
					}
					key = template.saleBidAmountKey;
					con.append("<tr>");
					con.append("<td class = td3>"+key+"</td>");
					con.append("<td class = td3>"+value+"</td>");
					con.append("</tr>");
				} else if (i == 3) {
					if (template.projectTimeLimitValue != null) {
						value = template.projectTimeLimitValue;
					}
					key = template.projectTimeLimitKey;
					con.append("<tr>");
					con.append("<td class = td2>"+key+"</td>");
					con.append("<td class = td2>"+value+"</td>");
					con.append("</tr>");
				} else if (i == 4) {
					if (template.annualConvertValue != null) {
						value = template.annualConvertValue;
					}
					key = template.annualConvertKey;
					con.append("<tr>");
					con.append("<td class = td3>"+key+"</td>");
					con.append("<td class = td3>"+value+"</td>");
					con.append("</tr>");
				} else if (i == 5) {
					if (template.monthInterestValue != null) {
						value = template.monthInterestValue+"元";
					}
					key = template.monthInterestKey;
					con.append("<tr>");
					con.append("<td class = td2>"+key+"</td>");
					con.append("<td class = td2>"+value+"</td>");
					con.append("</tr>");
				} else if (i == 6) {
					if (template.borrowingTypeValue != null) {
						value = template.borrowingTypeValue;
					}
					key = template.borrowingTypeKey;
					con.append("<tr>");
					con.append("<td class = td3>"+key+"</td>");
					con.append("<td class = td3>"+value+"</td>");
					con.append("</tr>");
				} else if (i == 7) {
					if (template.repaymentMethodValue != null) {
						value = template.repaymentMethodValue;
					}
					key = template.repaymentMethodKey;
					con.append("<tr>");
					con.append("<td class = td2>"+key+"</td>");
					con.append("<td class = td2>"+value+"</td>");
					con.append("</tr>");
				}
				
			}
			con.append("</table>");
			con.append("</p>");
			if (template.loanDescription != null && template.fixedContent1 != null) {
				con.append("<p>"+template.loanDescription+template.fixedContent1+"</p>");
			}
			if (template.fixedContent2 != null) {
				con.append("<p>"+template.fixedContent2+"</p>");
			}
			if (template.fixedContent3 != null) {
				con.append("<p>"+template.fixedContent3+"</p>");
			}
			con.append("<p>&nbsp;</p>");
			if (template.fixedContent4 != null) {
				con.append("<p>"+template.fixedContent4+"</p>");
			}
			if (template.fixedContent5 != null) {
				con.append("<p>"+template.fixedContent5+"</p>");
			}
			con.append("<style type = text/css>");
			con.append("table {margin: 20px 50px}");
			con.append("td {border: 2px solid white;font-weight:bold;}");
			con.append("tr {text-align:center;}");
			con.append(".td1 {width:340px;height:100px;font-size:25px;background-color:#4e80bb;}");
			con.append(".td2 {width:340px;height:74px;font-size:25px;background-color:#d2d8e8;}");
			con.append(".td3 {width:340px;height:84px;font-size:25px;background-color:#ebeef5;}");
			con.append("</style>");
			if (template.currentDate != null) {
				con.append("<p>"+template.currentDate+"</p>");
			}
			info.content = con.toString();
			info.image_url = template.image_url;
			info.image_resolution = template.image_resolution;
			info.image_size = template.image_size;
			info.image_format = template.image_format;
			info.order_time = template.sort_time;
			info.show_time = template.release_time;
			info.time = new Date();
			info.source_from = template.source_from;
		}
		return info;
	}
	
	/**
	 * 获取借款类型
	 * @author guoShiJie
	 * @createDate 2018.05.02
	 * */
	
	public static StringBuffer getBorrowingType(String headTitle) {
		StringBuffer title = new StringBuffer();
		
		if (headTitle.indexOf("房") != -1) {
			title.append(BorrowingType.getEnum(2).value);
		}
		if (headTitle.indexOf("车") != -1) {
			title.append(BorrowingType.getEnum(1).value);
		}
		return title;
	}
	
	/**
	 * 标的初审(初审中 -> 借款中)
	 * 
	 * @param bidSign
	 * @param suggest
	 * @param pre_release_time
	 * 
	 * @author guoShiJie
	 * @createDate 2018年5月4日
	 */
	@SubmitOnly
	public static void preAuditBidThrough(String bidSign, String suggest, String pre_release_time, int bid_type, String invite_code,String bidNo,String headTitle,String saleTime,Double amount,String period,String annualConvert,String repaymentMethod) {

		long bidId = decodeSign(bidSign, false);
		final long bidcode=bidId;
		if (bidId < 0) {
			flash.error("标的信息有误!");

			showBidPre(0, 1, 10);
		}
		
		t_bid bid = bidservice.findByID(bidId);
		//贷款申请通过
		t_loan_apply loan = loanApplyService.findByColumn("service_order_no = ?", bid.service_order_no);
		if (loan != null) {
			loan.bid_id = bid.id;
			loan.save();
		}

		if (StringUtils.isBlank(suggest) || suggest.length() < 20) {
			flash.error("风控建议长度位于20~2000位之间!");

			toPreAuditBidPre(bidId);
		}

		if (bid_type == 1 && ("".equals(invite_code) || invite_code == null)) {
			flash.error("定向标邀请码获取失败!");
			
			toPreAuditBidPre(bidId);
		}
		
		long supervisor_id = getCurrentSupervisorId();
		Date pre = DateUtil.strToDate(pre_release_time, Constants.DATE_PLUGIN);
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_MODIFY);
		//托管
		if (ConfConst.IS_TRUST) {
			ResultInfo result = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), businessSeqNo, bid, ServiceType.BID_MODIFY, ProjectStatus.INVESTING, billService.findReturnList());
			
			if (result.code < 0) {
				flash.error(result.msg);
				LoggerUtil.error(true, result.msg);
				
				toPreAuditBidPre(bidId);
			}
		}

		ResultInfo res = bidservice.preAuditBidThrough(bidId, suggest, pre, supervisor_id, bid_type, invite_code);
		if (res.code < 0) {
			flash.error(res.msg);
			LoggerUtil.error(true, res.msg);

			toPreAuditBidPre(bidId);
		}

		/** 添加管理员事件 */
		Map<String, String> param = new HashMap<String, String>();
		param.put("supervisor", getCurrentSupervisorName());
		param.put("bid_no", ((t_bid) res.obj).getBid_no());
		param.put("bid_name", ((t_bid) res.obj).title);
		boolean saveEvent = supervisorService.addSupervisorEvent(getCurrentSupervisorId(), Event.INVEST_PREADUIT_PASS, param);

		if (!saveEvent) {
			flash.error("保存管理员事件失败");

			toPreAuditBidPre(bidId);
		}
		//初审通过开始售卖15min后开始执行自动投标
				/*long diffTime=pre.getTime()-System.currentTimeMillis();
				if(diffTime<=0){
					diffTime = 0;
				}
				long doTime=(diffTime)+15*60*1000; 
				Timer timer =new Timer(true);
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						
						 if (JPA.local.get() == null) {
				             EntityManager em = JPA.newEntityManager();
				             final JPA jpa = new JPA();
				             jpa.entityManager = em;
				             JPA.local.set(jpa);
				         }

						 EntityTransaction transa = JPA.em().getTransaction();
						 transa.begin();
				         bidservice.autoInvest(bidcode);	
				         transa.commit();
						
					}
				}, doTime);*/

		flash.success("审核成功,已将项目置为[%s]!", t_bid.Status.FUNDRAISING.value);
		if (bidNo != null && headTitle != null && saleTime != null && amount != null && period != null && annualConvert != null && repaymentMethod != null) {
			/**获取官方公告模板信息*/
			t_information info = null;
			long id  = getOfficialInformationModel(bidNo,headTitle,saleTime,amount,period,annualConvert,repaymentMethod );
			if (id >0) {
				info = createOfficialNotice(id);
			}
			/**生成官方公告*/
			informationService.addInformation(info);
		}
		
		showBidPre(0, 1, 10);
	}
	
	/**
	 * 风控报告作废
	 * @param reportId 风控报告id
	 * @author guoShiJie
	 * @createDate 2018.9.12
	 * */
	public static void invalid (Long reportId) {
		ResultInfo result = new ResultInfo();
		
		if (reportId == null) {
			result.code = 0;
			result.msg = "操作无效";
			renderJSON(JSONObject.fromObject(result));
		}
		
		t_risk_report report = t_risk_report.findById(reportId);
		report.status = 5;
		t_risk_report risk = report.save();
		
		if (risk == null) {
			result.code = -1;
			result.msg = "作废失败";
			renderJSON(JSONObject.fromObject(result));
		}
		
		t_risk_handle_record record = new t_risk_handle_record();
		CurrSupervisor cursuper=getCurrSupervisor();
		record.risk_id=report.id;
		record.superName=cursuper.name;
		record.risk_handler=cursuper.reality_name;
		record.handle_time=new Date();
		record.handle_content = "风控报告作废了";
		record.save();

		result.code = 1;
		result.msg = "作废成功";
		
		renderJSON(JSONObject.fromObject(result));
	}
}
