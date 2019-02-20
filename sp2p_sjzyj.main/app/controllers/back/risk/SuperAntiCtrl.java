package controllers.back.risk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpRequest;
import org.eclipse.jdt.internal.compiler.flow.LoopingFlowContext;
import org.apache.commons.lang.StringUtils;

import com.softkey.DesUtil;
import com.softkey.JsonUtil;
import com.softkey.jsyunew3;
import common.enums.AnnualIncome;
import common.utils.Factory;
import common.utils.LoggerUtil;
import controllers.common.BackBaseController;
import controllers.common.BaseController;
import models.common.bean.CurrSupervisor;
import models.common.entity.t_right_supervisor;
import models.common.entity.t_risk_handle_record;
import models.common.entity.t_risk_pic;
import models.common.entity.t_risk_report;
import models.common.entity.t_risk_suggest;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import models.common.entity.t_user_live;
import models.common.entity.t_user_profession;
import models.core.entity.t_bid;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.processors.JsDateJsonBeanProcessor;
import play.data.validation.Phone;
import services.common.RiskRecordService;
import services.common.RiskReportService;
import services.common.RiskSuggestService;
import services.common.UserInfoService;
import services.common.UserLiveService;
import services.common.UserProfessionService;
import services.common.UserService;
import yb.enums.ReturnType;

/**
 * 
 *
 * @ClassName: SuperAntiCtrl
 *
 * @description 风控项目上标接口
 *
 * @author LiuHangjing
 * @createDate 2018年11月26日-下午4:05:33
 */
public class SuperAntiCtrl extends BaseController {

	protected static RiskReportService riskReportService = Factory.getService(RiskReportService.class);

	protected static UserService userService = Factory.getService(UserService.class);

	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static UserProfessionService userProfessionService = Factory.getService(UserProfessionService.class);
	
	protected static UserLiveService userLiveService = Factory.getService(UserLiveService.class);
	
	protected static RiskSuggestService riskSuggestService = Factory.getService(RiskSuggestService.class);
	
	protected static RiskRecordService riskRecordService = Factory.getService(RiskRecordService.class);

	// url="http://192.168.1.101:8001/supervisor/risk/SuperAntiCtrl/receiveInfo?params="{'message':'查询失败','success':false}"";
	public static String receiveInfo(String params){
		
			JSONObject json = new JSONObject();
			
			/** 接收的参数转json对象 解密*/
			String desParams = DesUtil.decode("yunquekj", params);
			System.out.println("解密后："+desParams);
			JSONObject dataJsonObject = JSONObject.fromObject(desParams);
			
			
			/** 判断传过来的参数是否为空*/
			if (desParams == null) {
				json.put("message", "发送失败,参数为空");
				json.put("code", -200);
				json.put("result", null);
				return json.toString();
			}
			/** 判断是否是json格式数据 */
			boolean isJson = isJson(desParams);
			if (!isJson) {
	
				json.put("message", "发送失败,数据格式错误");
				json.put("code", -200);
				json.put("result", null);
				return json.toString();
			}
	
			JSONArray jArray = (JSONArray) dataJsonObject.get("data");
			if (jArray.size()<=0) {
				json.put("message", "data数据为空,请检查");
				json.put("code", -200);
				return json.toString();
			}
			/** 风控操作日志表 */
			t_risk_handle_record record = new t_risk_handle_record();
	
			/** 风控报告表 */
			t_risk_report risk_report = new t_risk_report();
	
			/** 风控*/
			t_risk_suggest suggest = new t_risk_suggest();
	
			/** 用户居住信息表 */
			t_user_live userLive = new t_user_live();
	
			/** 用户职业信息表 */
			t_user_profession userProfession = new t_user_profession();
	
			/** 风控操作者*/
			String managementName;
			if (dataJsonObject.has("managementName")) {
				managementName = dataJsonObject.get("managementName").toString();
			}else{
				json.put("message", "风控操作者不能为空");
				json.put("code", -200);
				return json.toString();
			}
			for (Object object : jArray) {
				JSONObject jsonObject = (JSONObject) object;
				
				/** 72. 电话 */
				String phone = (String) jsonObject.get("phone");
				if (phone == null) {
					json.put("message", "电话不能为空");
					json.put("code", -200);
					return json.toString();
				}
				/** 73.抵押人姓名 */
				String name = (String) jsonObject.get("name");
				if (name == null) {
					json.put("message", "申请人姓名不能为空");
					json.put("code", -200);
					return json.toString();
				}
				/** 74.身份证号码 */
				String id_number = (String) jsonObject.get("id_number");
				if (id_number == null) {
					json.put("message", "申请人身份证号不能为空");
					json.put("code", -200);
					return json.toString();
				}
				/** 根据用户名和身份证号查询该用户是否注册且实名，如果没有注册提示该用户信息不存在，如果存在即直接修改当前用户的id关联的信息 */
				/** 用户表 */
				t_user user = userService.queryByMobile(phone);
				if (user == null) {
					json.put("message", "该用户不存在,请先在讴业普惠平台注册");
					json.put("code", -200);
					return json.toString();
				} else {
					// 修改信息
					/** 用户信息表 */
					t_user_info user_info = userInfoService.getUserInfo(name, id_number);
					if (user_info == null) {
						json.put("message", "该用户尚未开通资金托管,请先在讴业普惠平台开通");
						json.put("code", -200);
						return json.toString();
					} else {
	
						risk_report.user_id = user_info.user_id;
						userLive.user_id = user_info.user_id;
						userProfession.user_id = user_info.user_id;
						
						/** 申请人姓名 */
						risk_report.app_name = name;
	
						/** 申请人电话 */
						risk_report.app_phone = phone;
	
						/** 申请人身份证号码 */
						risk_report.app_creditNo = id_number;
	
						/** 75.业务类型 */
						Integer business_type = (Integer) jsonObject.get("business_type");
						if (business_type==null) {
							json.put("message", "报单编号为空");
							json.put("code", -200);
							return json.toString();
						}
						if(business_type == 0){
							/**惠房贷*/
							risk_report.business_type = 4;
						}else if(business_type == 1){
							/**惠车贷*/
							risk_report.business_type = 3;
						}
						
						/** 1. 报单编号 */
						String entry_number = (String) jsonObject.get("entry_number");
						if (entry_number == null) {
							json.put("message", "报单编号为空");
							json.put("code", -200);
							return json.toString();
						} else {
							risk_report.entry_number = entry_number;
						}
						/** 2. 借款人职业 */
						String occupational = (String) jsonObject.get("occupational");
						if (occupational == null) {
							json.put("message", "从事职业为空");
							json.put("code", -200);
							return json.toString();
						} else {
	
							userProfession.profession = occupational;
							if (occupational.equals("0")) {
								risk_report.app_guild = "国家机关、企业、事业单位";
							} else if (occupational.equals("1")) {
								risk_report.app_guild = "专业技术人员";
							} else if (occupational.equals("3")) {
								risk_report.app_guild = "办事人员和有关人员";
							} else if (occupational.equals("4")) {
								risk_report.app_guild = "商业、服务业人员";
							} else if (occupational.equals("5")) {
								risk_report.app_guild = "农、林、牧、渔、水利业";
							} else if (occupational.equals("6")) {
								risk_report.app_guild = "生产、运输设备操作人员";
							} else if (occupational.equals("X")) {
								risk_report.app_guild = "军人";
							} else if (occupational.equals("Y")) {
								risk_report.app_guild = "其他从业人员";
							} else {
								risk_report.app_guild = "未知";
							}
						}
	
						/** 3. 单位名称 */
						String company_name = (String) jsonObject.get("company_name");
						if (company_name == null) {
							json.put("message", "单位名称不能为空");
							json.put("code", -200);
							return json.toString();
						} else {
							userProfession.company_name = company_name;
							risk_report.app_company = company_name;
						}
	
						/** 4. 单位所属行业 */
						String industry_of_the_company = (String) jsonObject.get("industry_of_the_company");
						if (industry_of_the_company == null) {
							json.put("message", "单位所属行业不能为空");
							json.put("code", -200);
							return json.toString();
						} else {
							userProfession.company_trade = industry_of_the_company;
						}
	
						/** 5. 单位地址 */
						String company_address = (String) jsonObject.get("company_address");
						userProfession.company_address = company_address;
						risk_report.company_address = company_address;
	
						/** 6. 单位地址编码 */
						String company_email = (String) jsonObject.get("company_mail");
						userProfession.company_postal_code = company_email;
	
						/** 7. 单位工作起始年份 */
						String starting_work = (String) jsonObject.get("starting_work");
						userProfession.start_work_time = starting_work;
						
						/** 8. 职务 */
						Integer duty = (Integer) jsonObject.get("duty");
						if (duty == null) {
							json.put("message", "职务不能为空");
							json.put("code", -200);
							return json.toString();
						} else {
							userProfession.duty = duty;
						}
	
						/** 9. 职称 */
						Integer professional_title = (Integer) jsonObject.get("professional_title");
						if (professional_title == null) {
							json.put("message", "职称不能为空");
							json.put("code", -200);
							return json.toString();
						} else {
							userProfession.professional = professional_title;
						}
	
						/** 10. 年收入 */
						Double annual_income = (Double) jsonObject.get("annual_income");
						if (annual_income != null) {
							userProfession.annual_income = annual_income;
						} else {
							json.put("message", "年收入为空");
							json.put("code", -200);
							return json.toString();
						}
	
						/** 11. 服务费费率 */
						Double service_rate = (Double) jsonObject.get("service_rate");
						if (service_rate == null) {
							json.put("message", "服务费费率不能为空");
							json.put("code", -200);
							return json.toString();
						} else {
							risk_report.service_charge = service_rate;
						}
	
						/** 12. 居住状况 */
						Integer residential_condition = (Integer) jsonObject.get("residential_condition");
						if (residential_condition == null) {
							json.put("message", "居住状况不能为空");
							json.put("code", -200);
							return json.toString();
						} else {
							userLive.residential_condition = residential_condition;
						}
	
						/** 13. 居住年限 */
						String period_of_resident = (String) jsonObject.get("period_of_resident");
						if (period_of_resident == null) {
							json.put("message", "居住年限不能为空");
							json.put("code", -200);
							return json.toString();
						} else {
							risk_report.resident_year = period_of_resident;
						}
	
						/** 14. 面谈时间 */
						Long facetime = (Long) jsonObject.get("facetime");
						if (facetime == null) {
							risk_report.see_time = null;
						} else {
							Date date = new Date(facetime);
							risk_report.see_time = date;
						}
	
						/** 15.面谈地点 */
						String interview_place = (String) jsonObject.get("interview_place");
						risk_report.see_address = interview_place;
	
						/** 16.借款品种 */
						String borrowing_species = (String) jsonObject.get("borrowing_species");
						risk_report.loan_kind = borrowing_species;
	
						/** 17.劳动报酬 */
						Double reward = (Double) jsonObject.get("reward");
						risk_report.labor_reward = reward;
	
						/** 18.筹款时间 */
						Integer loan_time = (Integer) jsonObject.get("money_collecting_time");
						risk_report.loan_time = loan_time;
	
						/** 19.收入来源几处 */
						Integer ource_of_revenue = (Integer) jsonObject.get("ource_of_revenue");
						risk_report.income_kinds = ource_of_revenue;
	
						/** 20.还款来源 */
						String repayment = (String) jsonObject.get("repayment");
						risk_report.back_resource = repayment;
	
						/** 21.房产情况 */
						String housing_situation = (String) jsonObject.get("housing_situation");
						risk_report.house_condition = housing_situation;
	
						/** 22.车产情况 */
						String car_situation = (String) jsonObject.get("car_situation");
						risk_report.vehicle_condition = car_situation;
	
						/** 23.担保方式 */
						String guaranty_style = (String) jsonObject.get("guaranty_style");
						risk_report.guaranty_kind = guaranty_style;
	
						/** 24.其它固定资产 */
						String other_assets = (String) jsonObject.get("other_assets");
						risk_report.otherfixed_condition = other_assets;
	
						/** 25.征信情况 */
						String credit = (String) jsonObject.get("credit");
						risk_report.credit_condition = credit;
	
						/** 26.保证人姓名 */
						String name_of_sponsor = (String) jsonObject.get("name_of_sponsor");
						risk_report.guarantor_name = name_of_sponsor;
	
						/** 27.担保金额 */
						Double margin_trade = (Double) jsonObject.get("margin_trade");
						risk_report.guarantee_amount = margin_trade;
	
						/** 28.抵押物方式 */
						String pledge_type = (String) jsonObject.get("pledge_type");
						risk_report.pledge_kind = pledge_type;
	
						/** 29.抵押物权属(是否属于本人)(0 否1 是) */
						boolean belong_to_oneself = (boolean) jsonObject.get("belong_to_oneself");
						risk_report.pledge_ownership = belong_to_oneself;
	
						/** 30.评估价格 */
						Double assess_price = (Double) jsonObject.get("assess_price");
						risk_report.evaluate_price = assess_price;
	
						/** 31.借款期限 */
						String life_of_loan = (String) jsonObject.get("life_of_loan");
						if (life_of_loan == null || "".equals(life_of_loan)) {
							json.put("message", "借款期限不能为空");
							json.put("code", -200);
							return json.toString();
						}else{
							risk_report.loan_time_limit = life_of_loan;
						}
	
						/** 32.风控报告表里的贷款年利率 */
						Double annual_interest_rate = (Double) jsonObject.get("annual_interest_rate");
						risk_report.annual_rate = String.format("%.1f", annual_interest_rate);
	
						/** 33.申请人贷款历史(是否贷过款)：is_loan_pass(0否 1 是) */
						boolean whether_the_loan = (boolean) jsonObject.get("whether_the_loan");
						risk_report.is_loan_pass = whether_the_loan;
	
						/** 34.该单所属分公司 */
						Integer branch_office = (Integer) jsonObject.get("branch_office");
						risk_report.branch_id = branch_office.longValue();
	
						/** 35. 居住地址/现住址 */
						String nowaddress = (String) jsonObject.get("nowaddress");
						risk_report.app_address = nowaddress;
						userLive.residential_address = nowaddress;
	
						/** 36.信用卡额度 */
						Double line_of_credit = (Double) jsonObject.get("line_of_credit");
						risk_report.credit_card_limit = line_of_credit;
	
						/** 37.银行负债情况 */
						String liabilities_of_bank = (String) jsonObject.get("liabilities_of_bank");
						risk_report.bank_debt = liabilities_of_bank;
	
						/** 38.民间负债情况 */
						String liabilities_of_folk = (String) jsonObject.get("liabilities_of_folk");
						risk_report.folk_debt = liabilities_of_folk;
	
						/** 39.贷款记录 */
						String loan_documentation = (String) jsonObject.get("loan_documentation");
						risk_report.debt_record = loan_documentation;
	
						/** 40.家庭住址 */
						String home_address = (String) jsonObject.get("home_address");
						risk_report.app_home_address = home_address;
						risk_report.app_address = home_address;
						/** 通讯地址*/
						user_info.communication_address = home_address;
						
						/** 41.月收入app_monthly_income */
						Double monthly_profit = (Double) jsonObject.get("monthly_profit");
						risk_report.app_monthly_income = monthly_profit;
	
						/** 42.配偶月收入 */
						Double spouse_monthly_profit = (Double) jsonObject.get("spouse_monthly_profit");
						risk_report.pair_monthly_income = spouse_monthly_profit;
	
						/** 43.借款说明 */
						String borrowing_that = (String) jsonObject.get("borrowing_that");
						if (borrowing_that == null) {
							json.put("message", "贷款说明不能为空");
							json.put("code", -200);
							return json.toString();
						} else {
							risk_report.loan_state = borrowing_that;
						}
	
						/** 44.勘察师意见 */
						String survey_and_opinion = (String) jsonObject.get("survey_and_opinion");
						risk_report.tech_opinion = survey_and_opinion;
	
						/** 45.风控意见 */  				
						String risk_control_opinion = (String) jsonObject.get("risk_control_opinion");
						suggest.suggest  = risk_control_opinion;
						
						/** 46 借款期限(单位 月) */
						Object approval_deadline = jsonObject.get("approval_deadline");
						if (approval_deadline==null) {
							json.put("message", "借款期限不能为空");
							json.put("code", -200);
							return json.toString();
						}else{
							risk_report.loan_time_limit = approval_deadline.toString();
						}
	
						/** 47 借款用途 */
						String purpose_of_loan = (String) jsonObject.get("purpose_of_loan");
						if (purpose_of_loan != null) {
							risk_report.loan_purpose = purpose_of_loan;
						} else {
							json.put("message", "借款用途不能为空");
							json.put("code", -200);
							return json.toString();
						}
	
						/** 48 审批额度(单位 万元)*/
						Double approval_limit = (Double) jsonObject.get("approval_limit");
						if (approval_limit != null) {
							risk_report.loan_amount = approval_limit;
						} else {
							json.put("message", "审批额度不能为空");
							json.put("code", -200);
							return json.toString();
						}
						
						/** 49 还款方式  */    				
						Integer repayment_type  =  (Integer) jsonObject.get("payment_type");
						if (repayment_type == null) {
							json.put("message", "还款方式不能为空");
							json.put("code", -200);
							return json.toString();
						}else{
							risk_report.repayment_type = repayment_type;
						}
	
						/** 50 申请人性别 */
						Integer gender = (Integer) jsonObject.get("gender");
						if (gender != null) {
	
							if (gender == 1) {
								risk_report.app_sex = "男";
							} else if (gender == 2) {
								risk_report.app_sex = "女";
							} else {
								risk_report.app_sex = "未知";
							}
						} else {
							json.put("message", "申请人性别不能为空");
							json.put("code", -200);
							return json.toString();
						}
	
						/** 51 电子邮箱 */
						String email = (String) jsonObject.get("email");
						user_info.email = email;
	
						/** 52 配偶姓名 */
						String spouse_identification_name = (String) jsonObject.get("spouse_identification_name");
						user_info.mate_name = spouse_identification_name;
						risk_report.pair_name = spouse_identification_name;
	
						/** 53 配偶证件号码 */
						String spouse_identification_number = (String) jsonObject.get("spouse_identification_number");
						user_info.mate_id_number = spouse_identification_number;
	
						/** 54.配偶工作单位 */
						String spousal_work_unit = (String) jsonObject.get("spousal_work_unit");
						user_info.mate_work_unit = spousal_work_unit;
	
						/** 55. 配偶联系电话 */
						String spouse_telephone = (String) jsonObject.get("spouse_telephone");
						user_info.mate_phone = spouse_telephone;
						risk_report.pair_contact_way = spouse_telephone;
	
						/** 56 亲属联系人姓名 */
						String relative_contact_name = (String) jsonObject.get("relative_contact_name");
						if (relative_contact_name != null) {
							user_info.emergency_contact_name = relative_contact_name;
						} else {
							json.put("message", "亲属联系人姓名不能为空");
							json.put("code", -200);
							return json.toString();
						}
	
						/** 57 亲属联系电话 */
						String relative_contact_number = (String) jsonObject.get("relative_contact_number");
						if (relative_contact_number != null) {
							user_info.emergency_contact_mobile = relative_contact_number;
						} else {
							json.put("message", "亲属联系电话不能为空");
							json.put("code", -200);
							return json.toString();
						}
	
						/** 58 紧急联系人与本人关系 */
						Integer emergency_relation = (Integer) jsonObject.get("emergency_relation");
						if (emergency_relation != null) {
							user_info.emergency_contact_type = emergency_relation;
						} else {
							json.put("message", "紧急联系人与本人关系不能为空");
							json.put("code", -200);
							return json.toString();
						}
	
						/** 59 亲属联系人与本人关系 */
						Integer domestic_relation = (Integer) jsonObject.get("domestic_relation");
						user_info.second_relation = domestic_relation;
	
						/** 60 借款人年龄 */
						Integer age = (Integer) jsonObject.get("age");
						if (age != null) {
							risk_report.app_age = age;
						} else {
							json.put("message", "借款人年龄为空");
							json.put("code", -200);
							return json.toString();
						}
	
						/** 61 婚姻状况 */
						Integer marital_status = (Integer) jsonObject.get("marital_status");
						risk_report.app_marriage = marital_status;
						user_info.marital = marital_status;
	
						/** 62 学历 */
						Integer education = (Integer) jsonObject.get("education");
						user_info.education = education;
	
						if (education.equals("10")) {
							risk_report.applicate = "研究生";
						} else if (education.equals("20")) {
							risk_report.applicate = "本科";
						} else if (education.equals("30")) {
							risk_report.applicate = "大专";
						} else if (education.equals("40")) {
							risk_report.applicate = "中专";
						} else if (education.equals("50")) {
							risk_report.applicate = "技校";
						} else if (education.equals("60")) {
							risk_report.applicate = "高中";
						} else if (education.equals("70")) {
							risk_report.applicate = "初中";
						} else if (education.equals("80")) {
							risk_report.applicate = "小学";
						} else if (education.equals("90")) {
							risk_report.applicate = "文盲";
						} else {
							risk_report.applicate = "未知";
						}
	
						/** 63 学位 */
						Integer diploma = (Integer) jsonObject.get("diploma");
						if (diploma != null) {
							user_info.degree = diploma;
						} else {
							json.put("message", "学位不能为空");
							json.put("code", -200);
							return json.toString();
						}
	
						/** 64 住宅电话号码 */
						String home_phone = (String) jsonObject.get("home_phone");
						user_info.home_phone = home_phone;
	
						/** 65居住地址邮政编码 */
						String mailing_address = (String) jsonObject.get("mailing_address");
						if (mailing_address != null) {
							userLive.residential_postal_code = mailing_address;
							/**通讯地址邮编*/
							user_info.postal_code = mailing_address;
						} else {
							json.put("message", "居住地址邮政编码不能为空");
							json.put("code", -200);
							return json.toString();
						}
						/** 66 单位电话号码 */
						String business_phone_number = (String) jsonObject.get("business_phone_number");
						risk_report.company_tel = business_phone_number;
						user_info.work_phone = business_phone_number;
				
						/** 67 户籍地址 */    				
						String  permanent_residence_address = (String) jsonObject.get("permanent_residence_address");
						user_info.census_address =  permanent_residence_address;
							
						/** 68 申请人借款类型*/
						Integer bor_type = (Integer) jsonObject.get("bor_type");
						if (bor_type == null) {
							json.put("message", "借款类型为空");
							return json.toString();
						}else{
							risk_report.bor_type = bor_type;
						}
						
						/** 69 借款地点(发生地区划代码)*/
						Integer site = (Integer) jsonObject.get("site"); 
						if (site == null) {
							json.put("message", "借款地点为空");
							json.put("code", -200);
							return json.toString();
						}else {
							
							risk_report.site = site;
						}
						
						/** 70 投向行业*/
						Integer throw_industry = (Integer) jsonObject.get("throw_industry");
						if (throw_industry==null) {
							json.put("message", "投向行业为空");
							json.put("code", -200);
							return json.toString();
						}else{
							
							risk_report.throw_industry = throw_industry;
						}
						
						/** 71 家庭年收入*/
						Double home_annual_income = (Double) jsonObject.get("home_annual_income");
						risk_report.home_annual_income = home_annual_income;
						//判断是否保存成功
						boolean flag1 = false; 
						/** 保存用户职业信息*/
						flag1 = userProfessionService.insertUserPro(userProfession);
						if (!flag1) {
							json.put("message", "添加用户职业信息失败");
							json.put("code", -200);
							return json.toString();
						}
						/** 保存用户基本信息*/
						flag1 = userInfoService.insertUserInfo(user_info);
						if (!flag1) {
							json.put("message", "添加用户基本信息失败");
							json.put("code", -200);
							return json.toString();
						}
						/** 保存用户居住信息*/
						flag1 = userLiveService.insertUserLive(userLive);
						if (!flag1) {
							json.put("message", "添加用户居住信息失败");
							json.put("code", -200);
							return json.toString();
						}
						risk_report.status = 1;
						risk_report.time = new Date();
						/** 保存风控报告信息  返回当前插入的信息*/
						t_risk_report flag = riskReportService.insert(risk_report);
						if (flag != null) {
							/** 保存风控项目穿过来的风控意见 */
							suggest.risk_id = flag.id;
							suggest.time = new Date();
							suggest.type = 1;
							flag1 = riskSuggestService.insertSuggest(suggest);
							if (!flag1) {
								json.put("message", "添加风控建议失败");
								json.put("code", -200);
								return json.toString();
							}
							/** 保存风控项目传过来 操作的日志 */
							record.risk_id = flag.id;
							record.risk_handler = managementName;
							record.handle_time = flag.time;
							record.handle_content = "风控项目提交了一条风控报告";
							record.superName = managementName;
							flag1 =riskRecordService.insertRecord(record);
							if (!flag1) {
								json.put("message", "添加风控操作记录失败");
								json.put("code", -200);
								return json.toString();
							}
							
						} else {
							json.put("message", "添加风控报告失败");
							json.put("code", -200);
							return json.toString();
						}
						
					}
				}
			}
			
			JSONArray photo = (JSONArray) dataJsonObject.get("queryImage");
			
			for (Object object : photo) {
				/** 风控图片表 */
				t_risk_pic pic = new t_risk_pic();
				
				JSONObject jsonObj = (JSONObject) object;
				
				String entry_number = (String) jsonObj.get("report_id");
				if (entry_number != null) {
					object = riskReportService.getReportId(entry_number).getEntityId();				
					pic.risk_id = Long.valueOf(String.valueOf(object)).longValue();
				} else {
					json.put("message", "您传送的queryImage为空 ");
					json.put("code", -200);
					return json.toString();
				}		
				/**图片路径*/
				String img_url = (String) jsonObj.get("img_url");
				pic.pic_path = img_url;
				/**业务类型*/
				int bussness_type = (int) jsonObj.get("bussness_type");
				pic.type = bussness_type;
				/** 创建时间*/
				Long create_time = (Long) jsonObj.get("create_time");
				if (create_time != null) {
					Date date = new Date(create_time);			
					pic.time = date;
					pic.save();
				}else{
					json.put("message", "创建时间 为空 ");
					json.put("code", -200);
					return json.toString();
				}
							
			}
	
			// 返回的数据
			json.put("message", "添加风控报告成功");
			json.put("code", 200);
			return json.toString();

	}

	/**
	 * 
	 * @Title: isJson
	 * 
	 * @description 判断传过来的参数是不是json格式的数据
	 * @param content
	 * @return boolean
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月27日-下午1:34:14
	 */
	public static boolean isJson(String content) {

        try {
            JSONObject.fromObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
	}


}
