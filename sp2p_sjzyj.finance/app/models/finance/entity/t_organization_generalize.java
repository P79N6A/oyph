package models.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 
 *
 * @ClassName: t_organization_generalize
 *
 * @description 机构概括表
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
@Entity
public class t_organization_generalize extends Model {

	/**  机构名称   */
	public String name;
	
	/**  注册地址   */
	public String register_address;
	
	/**  办公地址   */
	public String work_address;
	
	/**  经营范围   */
	public String business_scope;
	
	/**  社会统一信用代码   */
	public String unified_social_credit_code;
	
	/**  电信业务经营许可证号   */
	public String tel_business_license_number;
	
	/**  成立时间   */
	public Date begin_time;
	
	/**  法定代表人姓名   */
	public String legal_name;
	
	/**  法定代表人身份证号   */
	public String legal_card_num;
	
	/**  联系电话   */
	public String mobile;
	
	/**  注册资本   */
	public BigDecimal register_fund;
	
	/**  实缴资本   */
	public BigDecimal contributed_capital;
	
	/**  资金存管银行名称   */
	public String bank_name;
	
	/**  银行存管上线时间   */
	public Date bank_time;
	
	/**  平台名称   */
	public String platform_name;
	
	/**  平台网址   */
	public String platform_url;
	
	/**  平台上线运营时间   */
	public Date platform_time;
	
	/**  移动APP应用名称   */
	public String app_platform_name;
	
	/**  在职员工人数   */
	public String employee_num;
	
	/**  正式员工人数   */
	public String regular_employee_num;
	
	/**  本科及以上学历员工人数   */
	public String undergraduate_employee_num;
	
	/**  IT科技人员人数   */
	public String science_employee_num;
	
	/**  企业基本户账户名称   */
	public String company_name;
	
	/**  企业基本账户   */
	public String company_account;
	
	/**  企业基本户开立银行网点全称   */
	public String company_bank_account;
	
	/**  添加时间   */
	public Date time;
	
}
