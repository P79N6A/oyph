package controllers.back.risk;

import java.io.File;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import models.common.entity.t_loan_apply;
import models.common.entity.t_loan_contract;
import models.common.entity.t_loan_profession;
import models.common.entity.t_user_info;
import models.common.entity.t_user_live;
import models.common.entity.t_user_profession;
import services.common.LoanApplyService;
import services.common.LoanContractService;
import services.common.LoanProfessionService;
import services.common.UserInfoService;
import services.common.UserLiveService;
import services.common.UserProfessionService;
import common.constants.Constants;
import common.utils.Factory;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;

/**
 * 后台-风控-征信报文控制器
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2017年7月21日
 */
public class MessageMngCtrl extends BackBaseController {

	protected static LoanApplyService loanApplyService = Factory.getService(LoanApplyService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static UserLiveService userLiveService = Factory.getService(UserLiveService.class);
	
	protected static UserProfessionService userProfessionService = Factory.getService(UserProfessionService.class);
	
	protected static LoanProfessionService loanProfessionService = Factory.getService(LoanProfessionService.class);
	
	protected static LoanContractService loanContractService = Factory.getService(LoanContractService.class);
	
	/**
	 * 征信报文导出页面
	 */
	public static void showMessagePre() {
		
		render();
	}
	
	/**
	 * 导出身份信息
	 */
	public static void identityInformationPre() {
		
		List<t_user_info> userInfo = userInfoService.queryUserInfo();
		
		if(userInfo != null) {
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATES));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(userInfo, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject userInfos = (JSONObject)obj;	
				
				//判断性别
				if(userInfos.getString("sex") == "UNKNOWN") {
					userInfos.put("sex", 0);
				}else if(userInfos.getString("sex") == "MALE") {
					userInfos.put("sex", 1);
				}else {
					userInfos.put("sex", 2);
				}
				
				//判断婚姻状况
				if(userInfos.getString("marital") == "UNMARRRIED") {
					userInfos.put("marital", 10);
				}else if(userInfos.getString("marital") == "MARRIED") {
					userInfos.put("marital", 20);
				}else if(userInfos.getString("marital") == "WIDOWED") {
					userInfos.put("marital", 30);
				}else if(userInfos.getString("marital") == "DIVORCE") {
					userInfos.put("marital", 40);
				}else {
					userInfos.put("marital", 90);
				}
				
				//判断学历
				if(userInfos.getString("education") == "GRADUATE_STUDENT") {
					userInfos.put("education", 10);
				}else if(userInfos.getString("education") == "UNDERGRADUATE") {
					userInfos.put("education", 20);
				}else if(userInfos.getString("education") == "COLLEGE") {
					userInfos.put("education", 30);
				}else if(userInfos.getString("education") == "SECONDARY") {
					userInfos.put("education", 40);
				}else if(userInfos.getString("education") == "SKILL_SCHOOL") {
					userInfos.put("education", 50);
				}else if(userInfos.getString("education") == "HIGH_SCHOOL") {
					userInfos.put("education", 60);
				}else if(userInfos.getString("education") == "MIDDLE_SCHOOL") {
					userInfos.put("education", 70);
				}else if(userInfos.getString("education") == "PRIMARY_SCHOOL") {
					userInfos.put("education", 80);
				}else if(userInfos.getString("education") == "ILLITERACY") {
					userInfos.put("education", 90);
				}else {
					userInfos.put("education", 99);
				}
				
				//判断学位
				if(userInfos.getString("degree") == "REST") {
					userInfos.put("degree", 0);
				}else if(userInfos.getString("degree") == "HONORARY_DOCTOR") {
					userInfos.put("degree", 1);
				}else if(userInfos.getString("degree") == "DOCTOR") {
					userInfos.put("degree", 2);
				}else if(userInfos.getString("degree") == "MASTER") {
					userInfos.put("degree", 3);
				}else if(userInfos.getString("degree") == "BACHELOR") {
					userInfos.put("degree", 4);
				}else {
					userInfos.put("degree", 9);
				}
				
				//判断第一联系人关系
				if(userInfos.getString("emergency_contact_type") == "FATHER") {
					userInfos.put("emergency_contact_type", 0);
				}else if(userInfos.getString("emergency_contact_type") == "MOTHER") {
					userInfos.put("emergency_contact_type", 1);
				}else if(userInfos.getString("emergency_contact_type") == "SPOUSE") {
					userInfos.put("emergency_contact_type", 2);
				}else if(userInfos.getString("emergency_contact_type") == "CHILDREN") {
					userInfos.put("emergency_contact_type", 3);
				}else if(userInfos.getString("emergency_contact_type") == "RELATIVE") {
					userInfos.put("emergency_contact_type", 4);
				}else if(userInfos.getString("emergency_contact_type") == "FRIEND") {
					userInfos.put("emergency_contact_type", 5);
				}else if(userInfos.getString("emergency_contact_type") == "COLLEAGUE") {
					userInfos.put("emergency_contact_type", 6);
				}else if(userInfos.getString("emergency_contact_type") == "SISTERS") {
					userInfos.put("emergency_contact_type", 7);
				}else {
					userInfos.put("emergency_contact_type", 8);
				}
				
				//判断第二联系人关系
				if(userInfos.getString("second_relation") == "FATHER") {
					userInfos.put("second_relation", 0);
				}else if(userInfos.getString("second_relation") == "MOTHER") {
					userInfos.put("second_relation", 1);
				}else if(userInfos.getString("second_relation") == "SPOUSE") {
					userInfos.put("second_relation", 2);
				}else if(userInfos.getString("second_relation") == "CHILDREN") {
					userInfos.put("second_relation", 3);
				}else if(userInfos.getString("second_relation") == "RELATIVE") {
					userInfos.put("second_relation", 4);
				}else if(userInfos.getString("second_relation") == "FRIEND") {
					userInfos.put("second_relation", 5);
				}else if(userInfos.getString("second_relation") == "COLLEAGUE") {
					userInfos.put("second_relation", 6);
				}else if(userInfos.getString("second_relation") == "SISTERS") {
					userInfos.put("second_relation", 7);
				}else {
					userInfos.put("second_relation", 8);
				}
			}	
			
			String fileName="身份信息表";
			
			File file = ExcelUtils.export(fileName,arrList,
					new String[] {
						"P2P机构代码", "姓名", "证件类型", "证件号码", "性别", "出生日期", "婚姻状况", "最高学历", "最高学位", "住宅电话", "手机号码", "单位电话", "电子邮箱", "通讯地址", "通讯地址邮政编码", "户籍地址", "配偶姓名", "配偶证件类型", "配偶证件号码", "配偶工作单位", "配偶联系电话", "第一联系人姓名", "第一联系人关系", "第一关系人联系电话", "第二联系人姓名", "第二联系人关系", "第二关系人联系电话"
					},
					new String[] {
					"","reality_name", "papers_type", "id_number", "sex", "birthday", "marital", "education", "degree", "home_phone", "mobile", "work_phone", "email", "communication_address", "postal_code", "census_address", "mate_name", "mate_papers_type", "mate_id_number", "mate_work_unit", "mate_phone", "emergency_contact_name", "emergency_contact_type", "emergency_contact_mobile", "second_name", "second_relation", "second_relation_phone"
					}
				);
			   
			renderBinary(file, fileName+".xls");
			
		}
		
		showMessagePre();
	}
	
	/**
	 * 导出职业信息
	 */
	public static void occupationalInformationPre() {
		
		List<t_user_profession> userProfession = userProfessionService.queryByAccount();
		
		if(userProfession != null) {
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATES));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(userProfession, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject userProfessions = (JSONObject)obj;	
				
				//判断职务
				if(userProfessions.getString("duty") == "TOP_LEADER") {
					userProfessions.put("duty", 1);
				}else if(userProfessions.getString("duty") == "MIDDLE_LEADER") {
					userProfessions.put("duty", 2);
				}else if(userProfessions.getString("duty") == "EMPLOYEES") {
					userProfessions.put("duty", 3);
				}else if(userProfessions.getString("duty") == "REST") {
					userProfessions.put("duty", 4);
				}else {
					userProfessions.put("duty", 9);
				}
				
				//判断职称
				if(userProfessions.getString("professional") == "NO") {
					userProfessions.put("professional", 0);
				}else if(userProfessions.getString("professional") == "TOP") {
					userProfessions.put("professional", 1);
				}else if(userProfessions.getString("professional") == "MIDDLE") {
					userProfessions.put("professional", 2);
				}else if(userProfessions.getString("professional") == "PRIMARY") {
					userProfessions.put("professional", 3);
				}else {
					userProfessions.put("professional", 9);
				}
			}	
			
			String fileName="职业信息表";
			
			File file = ExcelUtils.export(fileName,arrList,
					new String[] {
						"P2P机构代码", "姓名", "证件类型", "证件号码", "职业", "单位名称", "单位所属行业", "单位地址", "单位地址邮政编码", "本单位工作起始年份", "职务", "职称", "年收入"
					},
					new String[] {
					"","realityName", "papersType", "idNumber", "profession", "company_name", "company_trade", "company_address", "company_postal_code", "start_work_time", "duty", "professional", "annual_income"
					}
				);
			   
			renderBinary(file, fileName+".xls");
			
		}
			
		showMessagePre();
	}
	
	/**
	 * 导出居住信息
	 */
	public static void residenceInformationPre() {
		
		List<t_user_live> userLive = userLiveService.queryByAccount();
		
		if(userLive != null) {
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATES));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(userLive, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject userLives = (JSONObject)obj;	
				
				//判断职务
				if(userLives.getString("residential_condition") == "OWNED") {
					userLives.put("residential_condition", 1);
				}else if(userLives.getString("residential_condition") == "INSTALLMENT") {
					userLives.put("residential_condition", 2);
				}else if(userLives.getString("residential_condition") == "RELATIVE_BUILDING") {
					userLives.put("residential_condition", 3);
				}else if(userLives.getString("residential_condition") == "DORMITORY") {
					userLives.put("residential_condition", 4);
				}else if(userLives.getString("residential_condition") == "TENT") {
					userLives.put("residential_condition", 5);
				}else if(userLives.getString("residential_condition") == "PUBLIC_BUILDING") {
					userLives.put("residential_condition", 6);
				}else if(userLives.getString("residential_condition") == "REST") {
					userLives.put("residential_condition", 7);
				}else {
					userLives.put("residential_condition", 9);
				}
			}	
			
			String fileName="居住信息表";
			
			File file = ExcelUtils.export(fileName,arrList,
					new String[] {
						"P2P机构代码", "姓名", "证件类型", "证件号码", "居住地址", "居住地址邮政编码", "居住状况"
					},
					new String[] {
					"","realityName", "papersType", "idNumber", "residential_address", "residential_postal_code", "residential_condition"
					}
				);
			   
			renderBinary(file, fileName+".xls");
			
		}
		
		showMessagePre();
	}
	
	/**
	 * 导出贷款申请信息
	 */
	public static void loanApplicationPre() {
		
		List<t_loan_apply> loanApply = loanApplyService.queryByTime();
		
		if(loanApply != null) {
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATES));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(loanApply, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject loanApplys = (JSONObject)obj;	
				
				//判断贷款申请类型
				if(loanApplys.getString("type") == "HOUSING_LOANS") {
					loanApplys.put("type", 11);
				}else if(loanApplys.getString("type") == "COMMERCIAL_LOANS") {
					loanApplys.put("type", 12);
				}else if(loanApplys.getString("type") == "HOUSING_FUND") {
					loanApplys.put("type", 13);
				}else if(loanApplys.getString("type") == "CAR_CONSUME") {
					loanApplys.put("type", 21);
				}else if(loanApplys.getString("type") == "STUDENT_LOANS") {
					loanApplys.put("type", 31);
				}else if(loanApplys.getString("type") == "BUSINESS_LOANS") {
					loanApplys.put("type", 41);
				}else if(loanApplys.getString("type") == "FARM_LOANS") {
					loanApplys.put("type", 51);
				}else if(loanApplys.getString("type") == "CONSUME_LOANS") {
					loanApplys.put("type", 91);
				}else {
					loanApplys.put("type", 99);
				}
			}	
			
			String fileName="贷款申请信息表";
			
			File file = ExcelUtils.export(fileName,arrList,
					new String[] {
						"P2P机构代码", "贷款申请号", "姓名", "证件类型", "证件号码", "贷款申请类型", "贷款申请金额", "贷款申请月数", "贷款申请时间", "贷款申请状态"
					},
					new String[] {
					"", "service_order_no", "realityName", "papersType", "idNumber", "type", "amount", "period", "time", "status"
					}
				);
			   
			renderBinary(file, fileName+".xls");
			
		}
		
		showMessagePre();
	}
	
	/**
	 * 导出贷款业务信息
	 */
	public static void loanTransactionPre() {
		
		List<t_loan_profession> loanProfession = loanProfessionService.queryByTime();
		
		if(loanProfession != null) {
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATES));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(loanProfession, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject loanProfessions = (JSONObject)obj;	
				
				//判断贷款申请类型
				if(loanProfessions.getString("loan_type") == "HOUSING_LOANS") {
					loanProfessions.put("loan_type", 11);
				}else if(loanProfessions.getString("loan_type") == "COMMERCIAL_LOANS") {
					loanProfessions.put("loan_type", 12);
				}else if(loanProfessions.getString("loan_type") == "HOUSING_FUND") {
					loanProfessions.put("loan_type", 13);
				}else if(loanProfessions.getString("loan_type") == "CAR_CONSUME") {
					loanProfessions.put("loan_type", 21);
				}else if(loanProfessions.getString("loan_type") == "STUDENT_LOANS") {
					loanProfessions.put("loan_type", 31);
				}else if(loanProfessions.getString("loan_type") == "BUSINESS_LOANS") {
					loanProfessions.put("loan_type", 41);
				}else if(loanProfessions.getString("loan_type") == "FARM_LOANS") {
					loanProfessions.put("loan_type", 51);
				}else if(loanProfessions.getString("loan_type") == "CONSUME_LOANS") {
					loanProfessions.put("loan_type", 91);
				}else {
					loanProfessions.put("loan_type", 99);
				}
				
				//判断贷款担保方式
				if(loanProfessions.getString("guaranty_style") == "PLEDGE") {
					loanProfessions.put("guaranty_style", 1);
				}else if(loanProfessions.getString("guaranty_style") == "GUARANTY") {
					loanProfessions.put("guaranty_style", 2);
				}else if(loanProfessions.getString("guaranty_style") == "NATURAL_GUARANTEE") {
					loanProfessions.put("guaranty_style", 3);
				}else if(loanProfessions.getString("guaranty_style") == "CREDIT") {
					loanProfessions.put("guaranty_style", 4);
				}else if(loanProfessions.getString("guaranty_style") == "GROUP_HAVE") {
					loanProfessions.put("guaranty_style", 5);
				}else if(loanProfessions.getString("guaranty_style") == "GROUP_NOHAVE") {
					loanProfessions.put("guaranty_style", 6);
				}else if(loanProfessions.getString("guaranty_style") == "FARM_GUARANTEE") {
					loanProfessions.put("guaranty_style", 7);
				}else {
					loanProfessions.put("guaranty_style", 9);
				}
				
			}	
			
			String fileName="贷款业务信息表";
			
			File file = ExcelUtils.export(fileName,arrList,
					new String[] {
						"P2P机构代码", "贷款类型", "贷款合同号码", "业务号", "发生地点", "开户日期", "到期日期", "币种", "授信额度", "共享授信额度", "最大负债额", "担保方式", "还款频率", "还款月数", "剩余还款月数", "协定还款期数", "协定期还款额", "结算/应还款日期", "最近一次实际还款日期", "本月应还款金额", "本月实际还款金额", "余额", "当前逾期期数", "当前逾期总额", "逾期31-60天未归还贷款本金", "逾期61-90天未归还贷款本金", "逾期91-180天未归还贷款本金", "逾期180天未归还贷款本金", "累计逾期期数", "最高逾期期数", "五级分类状态", "账户状态", "24月（账户）还款状态", "账户拥有者信息提示", "姓名", "证件类型", "证件号码"
					},
					new String[] {
					"", "loan_type", "mer_bid_no", "service_order_no", "site", "account_time", "expire_time", "currency", "credit_line", "share_credit_line", "max_liskills", "guaranty_style", "repayment_frequency", "repayment_months", "resudue_months", "agreement_repayment_period", "agreement_repayment_amount", "repayment_date", "last_repayment_date", "current_amount", "actual_amount", "balance", "current_overdue_periods", "current_overdue_amount", "overdue_one", "overdue_two", "overdue_three", "overdue_four", "total_overdue_periods", "max_overdue_periods", "classify_status", "account_status", "repayment_status", "owner_message_hint", "realityName", "papersType", "idNumber"
					}
				);
			   
			renderBinary(file, fileName+".xls");
		}
		
		showMessagePre();
	}
	
	/**
	 * 导出贷款合同信息
	 */
	public static void loanContractPre() {
		
		List<t_loan_contract> loanContract = loanContractService.queryByTime();
		
		if(loanContract != null) {
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATES));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(loanContract, jsonConfig);
			
			String fileName="贷款业务信息表";
			
			File file = ExcelUtils.export(fileName,arrList,
					new String[] {
						"P2P机构代码", "贷款合同号码", "贷款合同生效日期", "贷款合同终止日期", "币种", "贷款合同金额", "合同有效状态"
					},
					new String[] {
					"", "mer_bid_no", "begin_time", "end_time", "currency_type", "contract_amount", "status"
					}
				);
			   
			renderBinary(file, fileName+".xls");
		}
		
		showMessagePre();
	}
	
}
