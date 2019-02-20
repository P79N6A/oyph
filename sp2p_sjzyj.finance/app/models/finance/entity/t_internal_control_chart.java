package models.finance.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 
 *
 * @ClassName: t_internal_control_chart
 *
 * @description 机构内控情况表
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
@Entity
public class t_internal_control_chart extends Model {

	/**  平台是否有第三方数字认证系统   */
	public String third_party_digital_authentication_system;
	
	/**  平台是否将借贷合同到期保存至5年及以上   */
	public String contract_of_loan;
	
	/**  平台是否有堡垒机产品，记录并留存借贷双方数据   */
	public String fortress_product;
	
	/**  平台是否具有完善的防火墙、入侵检测、数据加密以及灾难恢复等网络安全设施和管理制度   */
	public String firewall;
	
	/**  平台是否制定应急响应预案   */
	public String emergency_response_plan;
	
	/**  是否有清晰的贷款业务审批流程管理   */
	public String approval_process_management;
	
	/**  是否建立全流程（事先、事中、事后）风险管控   */
	public String security_risk_control;
	
	/**  审批风险识别模型以及模型类型_是否使用了担保\贷款审批风险模型   */
	public String risk_model;
	
	/** 是否构建了贷款业务审批风险识别软件系统   */
	public String risk_identification_software_system;
	
	/**  是否构建了贷后风险监测软件系统   */
	public String post_loan_risk_monitoring;
	
	/**  是否建立了贷款客户的统一集中式的信息采集与管理体系   */
	public String customer_information_collection_and_management;
	
	/**  是否在平台有风险提示界面   */
	public String risk_prompt_interface;
	
	/**  是否对客户进行投资风险测评   */
	public String investment_risk_assessment;
	
	/**  是否被监管当局处罚   */
	public String regulatory_penalties;
	
	/**  最近是否有违法违规经营记录   */
	public String records_of_illegal_operations;
	
	/**  平台是否有第三方数字认证系统(情况说明)   */
	public String third_party_digital_authentication_system_explain;
	
	/**  平台是否将借贷合同到期保存至5年及以上(情况说明)   */
	public String contract_of_loan_explain;
	
	/**  平台是否有堡垒机产品，记录并留存借贷双方数据(情况说明)   */
	public String fortress_product_explain;
	
	/**  平台是否具有完善的防火墙、入侵检测、数据加密以及灾难恢复等网络安全设施和管理制度(情况说明)  */
	public String firewall_explain;
	
	/**  平台是否制定应急响应预案(情况说明)   */
	public String emergency_response_plan_explain;
	
	/**  是否有清晰的贷款业务审批流程管理(情况说明)   */
	public String approval_process_management_explain;
	
	/**  是否建立全流程（事先、事中、事后）风险管控(情况说明)   */
	public String security_risk_control_explain;
	
	/**  审批风险识别模型以及模型类型_是否使用了担保\贷款审批风险模型(情况说明)   */
	public String risk_model_explain;
	
	/**  是否构建了贷款业务审批风险识别软件系统(情况说明)   */
	public String risk_identification_software_system_explain;
	
	/**  是否构建了贷后风险监测软件系统(情况说明)   */
	public String post_loan_risk_monitoring_explain;
	
	/**  是否建立了贷款客户的统一集中式的信息采集与管理体系(情况说明)   */
	public String customer_information_collection_and_management_explain;
	
	/**  是否在平台有风险提示界面(情况说明)   */
	public String risk_prompt_interface_explain;
	
	/**  是否对客户进行投资风险测评(情况说明)   */
	public String investment_risk_assessment_explain;
	
	/**  是否被监管当局处罚(情况说明)   */
	public String regulatory_penalties_explain;
	
	/**  最近是否有违法违规经营记录(情况说明)   */
	public String records_of_illegal_operations_explain;
	
	/**  添加时间   */
	public Date time;
	
}

