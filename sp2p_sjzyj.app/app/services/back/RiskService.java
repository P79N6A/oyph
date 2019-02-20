package services.back;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;










import org.apache.commons.lang.StringUtils;

import com.shove.Convert;

import net.sf.json.JSONObject;
import common.utils.Factory;
import dao.RiskDao;
import models.common.bean.CurrSupervisor;
import models.common.entity.t_company_branch;
import models.common.entity.t_risk_handle_record;
import models.common.entity.t_risk_pic;
import models.common.entity.t_risk_reception;
import models.common.entity.t_risk_report;
import models.common.entity.t_risk_suggest;
import models.common.entity.t_supervisor;
import services.base.BaseService;
import services.common.BranchService;
import services.common.RiskPicService;
import services.common.RiskReceptionService;
import services.common.RiskSuggestService;
import services.common.SupervisorService;

public class RiskService extends BaseService<t_risk_report>{
	
	protected static RiskDao riskDao=Factory.getDao(RiskDao.class);
	
	protected static BranchService branchService =  Factory.getService(BranchService.class);
	
	protected static RiskReceptionService riskReceptionService =  Factory.getService(RiskReceptionService.class);
	
	protected static RiskPicService riskPicService = Factory.getService(RiskPicService.class);
	
	protected static RiskSuggestService riskSuggestService = Factory.getService(RiskSuggestService.class);
	
	protected static SupervisorService supervisorService = Factory.getService(SupervisorService.class);
	
	protected RiskService(){
		super.basedao = riskDao;
	}
	/**
	 * 保存风控报告的图片与风控意见
	 * @param parameters
	 * @param report
	 * @createDate 2017年5月11日
	 * @author lihuijun
	 */
	public void saveRiskAuxi(Map<String, String> parameters,t_risk_report report){
		
		String riskOpinion = parameters.get("riskOpinion");
		if(StringUtils.isNotBlank(riskOpinion)){
			t_risk_suggest suggest=new t_risk_suggest();
			suggest.risk_id=report.id;
			suggest.suggest=riskOpinion;
			suggest.time=new Date();
			suggest.type=1;
			suggest.supervisorId=Convert.strToLong(parameters.get("superId"), 0);//登录才能
			suggest.save();
		}
		
		riskPicService.deletePicsByRiskId(report.id);
		String array = parameters.get("filename");
		  if (array != null && !array.equals("") && !array.equals("0")) {
	            String arr[] = array.split("\\*");
	            for (int i = 0; i < arr.length; i++) {
	               t_risk_pic riskpic = new t_risk_pic();
	                riskpic.pic_path = arr[i];
	                riskpic.risk_id=report.id;
	                riskpic.type=1;
	                riskpic.time=new Date();
	                riskpic.save();
	            }
	        }
		  String arrays = parameters.get("filenames");
		  if (arrays != null && !arrays.equals("") && !arrays.equals("0")) {
	            String arrs[] = arrays.split("\\*");
	            for (int i = 0; i < arrs.length; i++) {
	               t_risk_pic riskpic = new t_risk_pic();
	                riskpic.pic_path = arrs[i];
	                riskpic.risk_id=report.id;
	                riskpic.type=2;
	                riskpic.time=new Date();
	                riskpic.save();
	            }
	        }
		  String sarray = parameters.get("sfilename");
		  if (sarray != null && !sarray.equals("") && !sarray.equals("0")) {
	            String sarr[] = sarray.split("\\*");
	            for (int i = 0; i < sarr.length; i++) {
	               t_risk_pic riskpic = new t_risk_pic();
	                riskpic.pic_path = sarr[i];
	                riskpic.risk_id=report.id;
	                riskpic.type=3;
	                riskpic.time=new Date();
	                riskpic.save();
	            }
	        }
		  String reportIds = parameters.get("reportId");
		  String flag=parameters.get("flag");
		  t_risk_handle_record record=new t_risk_handle_record();
		  t_supervisor cursuper=supervisorService.findByID(Convert.strToLong(parameters.get("superId"), 0));
		  if(cursuper!=null){
			  record.risk_handler=cursuper.reality_name;
			  record.superName=cursuper.name;
			  record.handle_time=new Date();
		  }
		  
		  if(StringUtils.isBlank(reportIds) && StringUtils.isNotBlank(flag)){
			  record.handle_content="保存了风控报告";
		  }
		  
		  if(StringUtils.isBlank(reportIds) && StringUtils.isBlank(flag)){
			  record.handle_content="提交了风控报告";
		  }
		  
		  if(StringUtils.isNotBlank(reportIds) && StringUtils.isBlank(flag)){
			  record.handle_content="修改并提交了风控报告";
		  }
		  
		  if(StringUtils.isNotBlank(reportIds) && StringUtils.isNotBlank(flag)){
			  record.handle_content="修改并保存了风控报告";
		  }
		  
		  record.save();
	}
	
	/**
	 * 查询报告列表
	 * @param parameters
	 * @return
	 * @createDate 2017年5月9日
	 * @author lihuijun
	 */
	public String getRiskReportListStr(Map<String, String> parameters,String msg){
		
		Map<String, Object> result = new LinkedHashMap<>();
		
		result.put("code", 1);
		result.put("msg", msg);
		result.put("reportList", riskDao.getRiskReportListByPage(parameters));
		
		return JSONObject.fromObject(result).toString();
		
	}
	
	/**
	 * 查询报告详情
	 * @param riskReportId
	 * @return
	 * @createDate 2017年5月9日
	 * @author lihuijun
	 */
	public String getRiskReportDetailStr(long riskReportId){
		
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("code", 1);
		result.put("msg", "查询成功");
		
		t_risk_report report=super.findByID(riskReportId);
		if(report!=null){
			result.put("reportDetail", report);
			List<t_risk_pic> picList=null;
			List<t_risk_pic> picLists=null;
			List<t_risk_pic> spicList=null;
			StringBuffer bimgUrl=null;
			int picCount=0;
			StringBuffer bimgUrls=null;
			int picCounts=0;
			StringBuffer sbimgUrl=null;
			int spicCount=0;

			picList=riskPicService.findListByColumn("risk_id=? and type=?", report.id,1);
		     picCount = picList.size();
	         bimgUrl = new StringBuffer("");
	        for (int i = 0; i < picList.size(); i++) {
	            if (i == (picList.size() - 1)) {
	                bimgUrl.append(picList.get(i).pic_path);
	            } else {
	                bimgUrl.append(picList.get(i).pic_path + "*");
	            }
	        }
	        result.put("picList", picList);
	        result.put("picCount", picCount);
	        result.put("bimgUrl", bimgUrl.toString());
			picLists=riskPicService.findListByColumn("risk_id=? and type=?", report.id,2);
			picCounts = picLists.size();
	        bimgUrls = new StringBuffer("");
	        for (int i = 0; i < picLists.size(); i++) {
	            if (i == (picLists.size() - 1)) {
	                bimgUrls.append(picLists.get(i).pic_path);
	            } else {
	                bimgUrls.append(picLists.get(i).pic_path + "*");
	            }
	        }
	        result.put("picLists", picLists);
	        result.put("picCounts", picCounts);
	        result.put("bimgUrls", bimgUrls.toString());    
	        spicList=riskPicService.findListByColumn("risk_id=? and type=?", report.id,3);
			spicCount = spicList.size();
	         sbimgUrl = new StringBuffer("");
	        for (int i = 0; i < spicList.size(); i++) {
	            if (i == (spicList.size() - 1)) {
	                sbimgUrl.append(spicList.get(i).pic_path);
	            } else {
	                sbimgUrl.append(spicList.get(i).pic_path + "*");
	            }
	        } 
	        result.put("spicList", spicList);
	        result.put("spicCount", spicCount);
	        result.put("sbimgUrl", sbimgUrl.toString());
	        List<t_risk_suggest> suggestList=riskSuggestService.findListByColumn("risk_id=? and type=?", report.id,1);
			List<t_risk_suggest> suggests=riskSuggestService.findListByColumn("risk_id=? and type=?", report.id,2);
			result.put("suggestList", suggestList);
			result.put("suggests", suggests);
		}else{
			result.put("reportDetail", null);	
		}
		/**如果要是修改*/
		List<t_company_branch> branchs = branchService.findAll();
		if(branchs==null){
			result.put("branchs", null);
		}else{
			result.put("branchs", branchs);
		}
		
		List<t_risk_reception> receptions = riskReceptionService.findAll();
		if(receptions==null){
			result.put("receptions", null);
		}else{
			result.put("receptions", receptions);
		}

		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 风控经理审核报告
	 * @param reportId
	 * @return
	 * @createDate 2017年5月10日
	 * @author lihuijun
	 */
	public String auditReport(Map<String, String> parameters,long superId){
		Map<String, Object> result = new LinkedHashMap<>();
		long  riskReportId = Convert.strToLong(parameters.get("riskReportId"), 0);
		if(riskReportId>0){
			t_risk_report report=super.findByID(riskReportId);
			if(report!=null){
				String riskStatusStr = parameters.get("riskStatus");
				if(StringUtils.isNotBlank(riskStatusStr)){
					String suggestStr = parameters.get("suggest");
					if(StringUtils.isNotBlank(suggestStr)){
						t_risk_suggest suggest=new t_risk_suggest();
						suggest.risk_id=report.id;
						suggest.suggest=suggestStr;
						suggest.time=new Date();
						suggest.type=2;
						suggest.supervisorId = superId; //登录才能得到
						suggest.save();
					}
					t_risk_handle_record record=new t_risk_handle_record();
					t_supervisor cursuper=supervisorService.findByID(superId);
					record.risk_id=report.id;
					record.superName=cursuper.name;
					record.risk_handler=cursuper.reality_name;
					record.handle_time=new Date();
					switch (riskStatusStr) {
						case "1":
							report.status = 1;
							report.save();
							record.handle_content="审核通过";
							record.save();
							result.put("code", 1);
							result.put("msg", "审核通过");
							parameters.put("showType",parameters.get("showType"));				
							break;
							
						case "2":
							report.status = 2;
							report.save();
							record.handle_content="审核未通过";
							record.save();
							result.put("code", 1);
							result.put("msg", "审核未通过");
							parameters.put("showType",parameters.get("showType"));
							break;
						case "3":
							String loanAmountStr = parameters.get("loanAmount");
							if(StringUtils.isNotBlank(loanAmountStr)){
								Double loanAmount=Double.parseDouble(loanAmountStr);
								report.loan_amount=loanAmount;
							}
							report.status = 3;
							report.save();
							record.handle_content="打回了报告";
							record.save();
							result.put("code", 1);
							result.put("msg", "打回，等待处理");
							parameters.put("showType",parameters.get("showType"));
							break;	
						default:
							report.status = 0;
							parameters.put("showType",parameters.get("showType"));
							break;
						}
				}
				
			}else{
				result.put("code", 1);
				result.put("msg", "保存失败");
			}
		}else{
			result.put("code", 1);
			result.put("msg", "保存失败");
		}
		return JSONObject.fromObject(result).toString();
	}
	
	public t_risk_report saveRiskReport(Map<String, String> parameters){
		t_risk_report riskReport=null;
		String reportIds = parameters.get("reportId");
		if(StringUtils.isNotBlank(reportIds)){
			long reportId=Long.parseLong(reportIds);
			riskReport=riskDao.findByID(reportId);
		}else{
			riskReport=new t_risk_report();
		}
		
		/**面谈时间*/
		String seeTime = parameters.get("see_time");
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
		String seeAddress = parameters.get("see_address");
		if(StringUtils.isNotBlank(seeAddress)){
			riskReport.see_address=seeAddress;
		}
		
		/**申请人姓名*/
		String appName = parameters.get("app_name");
		if(StringUtils.isNotBlank(appName)){
			riskReport.app_name=appName;
		}
		
		/**申请人性别*/
		String appSex = parameters.get("app_sex");
		if(StringUtils.isNotBlank(appSex)){
			riskReport.app_sex=appSex;
		}
		
		/**申请人年龄*/
		String appAge = parameters.get("app_age");
		if(StringUtils.isNotBlank(appAge)){
			riskReport.app_age=Integer.parseInt(appAge);
		}
		
		
		/**申请人现住址*/
		String appAddress = parameters.get("app_address");
		if(StringUtils.isNotBlank(appAddress)){
			riskReport.app_address=appAddress;
		}
		
		/**申请人工作单位*/
		String appCompany = parameters.get("app_company");
		if(StringUtils.isNotBlank(appCompany)){
			riskReport.app_company=appCompany;
		}
		
		/**风控接待人员*/
		String receptionPerson = parameters.get("reception_id");
		if(StringUtils.isNotBlank(receptionPerson)){
			long rePer=Long.parseLong(receptionPerson);
			riskReport.reception_id=rePer;
		}
		
		/**申请人身份证号*/
		String appCreditNo = parameters.get("app_creditNo");
		if(StringUtils.isNotBlank(appCreditNo)){
			riskReport.app_creditNo=appCreditNo;
		}
		
		/**申请人所从事的职业*/
		String appGuild = parameters.get("app_guild");
		if(StringUtils.isNotBlank(appGuild)){
			riskReport.app_guild=appGuild;
		}
		
		/**申请人工作单位地址*/
		String companyAddress = parameters.get("company_address");
		if(StringUtils.isNotBlank(companyAddress)){
			riskReport.company_address=companyAddress;
		}
		
		/**申请人工作单位电话*/
		String companyTel = parameters.get("company_tel");
		if(StringUtils.isNotBlank(companyTel)){
			riskReport.company_tel=companyTel;
		}
		
		/**申请人家庭住址*/
		String appHomeAddress = parameters.get("app_home_address");
		if(StringUtils.isNotBlank(appHomeAddress)){
			riskReport.app_home_address=appHomeAddress;
		}
		
		/**申请人联系电话*/
		String appPhone = parameters.get("app_phone");
		if(StringUtils.isNotBlank(appPhone)){
			riskReport.app_phone=appPhone;
		}
		
		/**申请人婚姻状况*/
		String appMarriage = parameters.get("app_marriage");
		if(StringUtils.isNotBlank(appMarriage)){
			int marriagess = Integer.parseInt(appMarriage);
			riskReport.app_marriage = marriagess;
		}
		
		/**申请人配偶姓名*/
		String pairName = parameters.get("pair_name");
		if(StringUtils.isNotBlank(pairName)){
			riskReport.pair_name=pairName;
		}
		
		/**申请人配偶 联系方式*/
		String pairContactWay = parameters.get("pair_contact_way");
		if(StringUtils.isNotBlank(pairContactWay)){
			riskReport.pair_contact_way=pairContactWay;
		}
		
		/**申请人月收入*/
		String appMonthlyIncome = parameters.get("app_monthly_income");
		if(StringUtils.isNotBlank(appMonthlyIncome)){
			double appIncome=Double.parseDouble(appMonthlyIncome);
			riskReport.app_monthly_income=appIncome;
		}
		
		/**申请人配偶月收入*/
		String pairMonthlyIncome = parameters.get("pair_monthly_income");
		if(StringUtils.isNotBlank(pairMonthlyIncome)){
			double pairIncome=Double.parseDouble(pairMonthlyIncome);
			riskReport.pair_monthly_income=pairIncome;
		}
		
		/**申请人家庭年收入*/
		String homeAnnualIncome = parameters.get("home_annual_income");
		if(StringUtils.isNotBlank(homeAnnualIncome)){
			double homeIncome=Double.parseDouble(homeAnnualIncome);
			riskReport.home_annual_income=homeIncome;
		}
		
		/**申请人借款用途*/
		String loanPurpose = parameters.get("loan_purpose");
		if(StringUtils.isNotBlank(loanPurpose)){
			riskReport.loan_purpose=loanPurpose;
		}
		
		/**申请人借款数额*/
		String loanAmount = parameters.get("loan_amount");
		if(StringUtils.isNotBlank(loanAmount)){
			double loanAmounts=Double.parseDouble(loanAmount);
			riskReport.loan_amount=loanAmounts;
		}
		
		/**申请人借款品种*/
		String loanKind = parameters.get("loan_kind");
		if(StringUtils.isNotBlank(loanKind)){	
			riskReport.loan_kind=loanKind;
		}
		
		/**申请人借款品种*/
		String loanClearKind = parameters.get("loan_clear_kind");
		if(StringUtils.isNotBlank(loanClearKind)){	
			riskReport.loan_clear_kind=loanClearKind;
		}
		
		/**申请人劳动力报酬*/
		String laborReward = parameters.get("labor_reward");
		if(StringUtils.isNotBlank(laborReward)){	
			double laborRewards=Double.parseDouble(laborReward);
			riskReport.labor_reward=laborRewards;
		}
		
		/**筹款时间（天）*/
		String loanTimeStr = parameters.get("loan_time");
		if(StringUtils.isNotBlank(loanTimeStr)){
			int loanTime=Integer.parseInt(loanTimeStr);
			riskReport.loan_time=loanTime;
		}
		
		/**申请人收入来源有几处*/
		String incomeKinds = parameters.get("income_kinds");
		if(StringUtils.isNotBlank(incomeKinds)){	
			int incomeKind=Integer.parseInt(incomeKinds);
			riskReport.income_kinds=incomeKind;
		}
		
		/**申请人还款来源*/
		String backResource = parameters.get("back_resource");
		if(StringUtils.isNotBlank(backResource)){	
			riskReport.back_resource=backResource;
		}
		
		/**申请人房产情况*/
		String houseCondition = parameters.get("house_condition");
		if(StringUtils.isNotBlank(houseCondition)){	
			riskReport.house_condition=houseCondition;
		}
		
		/**申请人车产情况*/
		String vehicleCondition = parameters.get("vehicle_condition");
		if(StringUtils.isNotBlank(vehicleCondition)){	
			riskReport.vehicle_condition=vehicleCondition;
		}
		
		/**申请人其他固定资产*/
		String otherfixedCondition = parameters.get("otherfixed_condition");
		if(StringUtils.isNotBlank(otherfixedCondition)){	
			riskReport.otherfixed_condition=otherfixedCondition;
		}
		
		/**申请人担保方式*/
		String guarantyKind = parameters.get("guaranty_kind");
		if(StringUtils.isNotBlank(guarantyKind)){	
			riskReport.guaranty_kind=guarantyKind;
		}
		
		/**保证人姓名*/
		String guarantorName = parameters.get("guarantor_name");
		if(StringUtils.isNotBlank(guarantorName)){	
			riskReport.guarantor_name=guarantorName;
		}
		
		/**担保金额*/
		String guaranteeAmount = parameters.get("guarantee_amount");
		if(StringUtils.isNotBlank(guaranteeAmount)){
			double guaranteeAmounts=Double.parseDouble(guaranteeAmount);
			riskReport.guarantee_amount=guaranteeAmounts;
		}
		
		/**抵押物种类*/
		String pledgeKind = parameters.get("pledge_kind");
		if(StringUtils.isNotBlank(pledgeKind)){
			riskReport.pledge_kind=pledgeKind;
		}
		
		/**抵押物权属*/
		String pledgeOwnership = parameters.get("pledge_ownership");
		if(StringUtils.isNotBlank(pledgeOwnership)){
			byte pledgeOwnerships=Byte.parseByte(pledgeOwnership);
			if(pledgeOwnerships==0){
				riskReport.pledge_ownership=false;
			}else{
				riskReport.pledge_ownership=true;
			}
		}
		
		/**抵押物评估价格*/
		String evaluatePrice = parameters.get("evaluate_price");
		if(StringUtils.isNotBlank(evaluatePrice)){	
			double evaluatePrices=Double.parseDouble(evaluatePrice);
			riskReport.evaluate_price=evaluatePrices;
		}
		
		/**贷款期限*/
		String loanTimeLimit = parameters.get("loan_time_limit");
		if(StringUtils.isNotBlank(loanTimeLimit)){
			riskReport.loan_time_limit=loanTimeLimit;
		}
		
		/**贷款年利率*/
		String annualRate = parameters.get("annual_rate");
		if(StringUtils.isNotBlank(annualRate)){
			riskReport.annual_rate=annualRate;
		}
		
		
		/**申请人贷款历史*/
		String isLoanPass = parameters.get("is_loan_pass");
		if(StringUtils.isNotBlank(isLoanPass)){
			byte isLoanPasses=Byte.parseByte(isLoanPass);
			if(isLoanPasses==0){
				riskReport.is_loan_pass=false;
			}else{
				riskReport.is_loan_pass=true;
			}
		}
		
		/**申请人履行还款情况（有贷款历史的申请人）*/
		String backloanCondition = parameters.get("backloan_condition");
		if(StringUtils.isNotBlank(backloanCondition)){
			riskReport.backloan_condition=backloanCondition;
		}
		
		/**该单所属分公司*/
		String branchCompany = parameters.get("branch_id");
		if(StringUtils.isNotBlank(branchCompany)){
			long branchId=Long.parseLong(branchCompany);
			riskReport.branch_id=branchId;
		}
		
		/**申请人学历*/
		String applicate = parameters.get("applicate");
		if(StringUtils.isNotBlank(applicate)){
			riskReport.applicate=applicate;
		}
		
		/**申请人信用卡总额度*/
		String creditCardLimit = parameters.get("credit_card_limit");
		if(StringUtils.isNotBlank(creditCardLimit)){
			Double limit=Double.parseDouble(creditCardLimit);
			riskReport.credit_card_limit=limit;
		}
		
		/**申请人银行负债情况*/
		String bankDebt = parameters.get("bank_debt");
		if(StringUtils.isNotBlank(bankDebt)){
			riskReport.bank_debt=bankDebt;
		}
		
		/**申请人民间负债情况*/
		String folkDebt = parameters.get("folk_debt");
		if(StringUtils.isNotBlank(folkDebt)){
			riskReport.folk_debt=folkDebt;
		}
		
		/**申请人贷款记录*/
		String debtRecord = parameters.get("debt_record");
		if(StringUtils.isNotBlank(debtRecord)){
			riskReport.debt_record=debtRecord;
		}
		
		/**申请人征信情况*/
		String creditCondition = parameters.get("credit_condition");
		if(StringUtils.isNotBlank(creditCondition) ){
			riskReport.credit_condition=creditCondition;
		}
		/*String creditCondition[] = parameters.getAll("creditCondition");
		if(creditCondition!=null && creditCondition.length>0){
			riskReport.credit_condition=creditCondition[0];
		}*/
		
		
		/**借款说明*/
		String loanState=parameters.get("loan_state");
		if(StringUtils.isNotBlank(loanState)){
			riskReport.loan_state=loanState;
		}
		
		/**勘察师意见*/
		String techOpinion=parameters.get("tech_opinion");
		if(StringUtils.isNotBlank(techOpinion)){
			riskReport.tech_opinion=techOpinion;
		}
		
		/**判断是保存还是提交*/
		String flag=parameters.get("flag");
		if(StringUtils.isNotBlank(flag)){
			riskReport.status = -1;
		}else{
			riskReport.status = 0;
		}
		riskReport.time=new Date();
		t_risk_report report = null;
		if(reportIds==null){	
		/**保存状态*/
			riskReport.save();
		/**得到刚刚保存的风控报告*/
			report=riskDao.getLastReport();
		}else{
			riskReport.save();
			report=riskReport;
		}
		
		return report;
	}


}
