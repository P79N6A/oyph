package common.constants;

/**
 * 平台系统参数设定
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月16日
 */
public final class SettingKey {

	private SettingKey() {}
	
	/** (基本信息)平台LOGO路径:platform_logo_filename */
	public static final String PLATFORM_LOGO_FILENAME = "platform_logo_filename";
	
	/** (基本信息)平台ICON路径:platform_icon_Filename */
	public static final String PLATFORM_ICON_FILENAME = "platform_icon_Filename";
	
	/** (基本信息)平台名称:platform_name */
	public static final String PLATFORM_NAME = "platform_name";
	
	/** (基本信息)公司名称:company_name */
	public static final String COMPANY_NAME = "company_name";
	
	/** (基本信息)公司电话:company_tel */
	public static final String COMPANY_TEL = "company_tel";
	
	/** (基本信息)公司邮箱:company_email */
	public static final String COMPANY_EMAIL = "company_email";
	
	/** (基本信息)公司详细地址:company_address */
	public static final String COMPANY_ADDRESS = "company_address";
	
	/** (基本信息)公司联系QQ1:company_qq1 */
	public static final String COMPANY_QQ1 = "company_qq1";
	
	/** (基本信息)公司联系QQ2:company_qq2 */
	public static final String COMPANY_QQ2 = "company_qq2";
	
	/** (基本信息)公司联系QQ3:company_qq3 */
	public static final String COMPANY_QQ3 = "company_qq3";
	
	/** (基本信息)备案号:site_icp_number */
	public static final String SITE_ICP_NUMBER = "site_icp_number";
	
	
	/** (SEO规则)百度流量统计代码:baidu_code */
	public static final String BAIDU_CODE = "baidu_code";
	
	/** (SEO规则)SEO标题:seo_title */
	public static final String SEO_TITLE = "seo_title";
	
	/** (SEO规则)SEO描述:seo_description */
	public static final String SEO_DESCRIPTION = "seo_description";
	
	/** (SEO规则)SEO关键词:seo_keywords */
	public static final String SEO_KEYWORDS = "seo_keywords";
	
	

	/** (安全参数)敏感词汇屏蔽:sensitive_words */
	public static final String SENSITIVE_WORDS = "sensitive_words";
	
	/** (安全参数)密码连续错误次数时锁定用户:security_lock_times  */
	public static final String SECURITY_LOCK_TIMES = "security_lock_times";
	
	/** (安全参数)密码连续错误次数时锁定用户时长:security_lock_time */
	public static final String SECURITY_LOCK_TIME = "security_lock_time";
	
	
	
	/** (正版授权)注册码:register_code */
	public static final String REGISTER_CODE = "register_code";
	
	/** (自动投标设置):is_auto_invest_show*/
	public static final String IS_AUTO_INVEST_SHOW = "is_auto_invest_show";
	
	/** (短信/邮件通道)短信通道(服务提供商):service_sms_provider */
	public static final String SERVICE_SMS_PROVIDER = "service_sms_provider";
	
	/** (短信/邮件通道)短信通道账号:service_sms_account */
	public static final String SERVICE_SMS_ACCOUNT = "service_sms_account";
	
	/** (短信/邮件通道)短信通道密码:service_sms_password */
	public static final String SERVICE_SMS_PASSWORD = "service_sms_password";
	

	
	/** (短信/邮件通道)邮件通道账号:service_mail_account */
	public static final String SERVICE_MAIL_ACCOUNT = "service_mail_account";
	
	/** (短信/邮件通道)邮件通道密码:service_mail_password */
	public static final String SERVICE_MAIL_PASSWORD = "service_mail_password";
	
	/** (短信/邮件通道)邮箱登录网址:email_website */
	public static final String EMAIL_WEBSITE = "email_website";
	
	/** (短信/邮件通道)POP3服务器:POP3_server */
	public static final String POP3_SERVER = "POP3_server";
	
	/** (短信/邮件通道)STMP服务器:STMP_server */
	public static final String STMP_SERVER = "STMP_server";
	
	
	
	
	/** (理财设置-提现设置)提现手续费起点:withdraw_fee_point */
	public static final String WITHDRAW_FEE_POINT = "withdraw_fee_point";
	
	/** (理财设置-提现设置)提现手续费率:withdraw_fee_rate */
	public static final String WITHDRAW_FEE_RATE = "withdraw_fee_rate";
	
	/** (理财设置-提现设置)最低提现手续费:withdraw_fee_min */
	public static final String WITHDRAW_FEE_MIN = "withdraw_fee_min";
	
	/** (理财设置-转让设置)转让服务费率:transfer_fee_rate */
	public static final String TRANSFER_FEE_RATE = "transfer_fee_rate";
	
	/** (理财设置-充值设置)最低充值金额:recharge_amount_min */
	public static final String RECHARGE_AMOUNT_MIN = "recharge_amount_min";
	
	/** (理财设置-充值设置)最高充值金额:recharge_amount_max */
	public static final String RECHARGE_AMOUNT_MAX = "recharge_amount_max";
	
	/** (理财设置-提现设置)最低提现金额:recharge_amount_min */
	public static final String WITHDRAW_AMOUNT_MIN = "withdraw_amount_min";
	
	/** (理财设置-提现设置)最高提现金额:recharge_amount_max */
	public static final String WITHDRAW_AMOUNT_MAX = "withdraw_amount_max";
	
	
	/** (理财设置-催收短信设置)账单到期提醒天数:bill_expires */
	public static final String BILL_EXPIRES = "bill_expires";
	
	
	
	
	/** (其他)平台短讯:project_releases_trailer */
	public static final String PROJECT_RELEASES_TRAILER = "project_releases_trailer";
	
	/** (其他)前台是否显示统计数据:is_statistics_show */
	public static final String IS_STATISTICS_SHOW = "is_statistics_show";
	
	
	
	
	/** (主题)主题名称[保存的是主题的folder,默认default]:theme_name */
	public static final String THEME_NAME = "theme_name";
	
	/** (主题)首页小部件:theme_widget */
	public static final String THEME_WIDGET = "theme_widget";
	
	/** (主题)是否自定义色系:theme_customize(0,1) */
	public static final String THEME_CUSTOMIZE = "theme_customize";
	
	/** (主题)自定义主色系【当自定义色系为1时启用】:theme_main_color */
	public static final String THEME_MAIN_COLOR = "theme_main_color";
	
	/** (主题)自定义辅色系【当自定义色系为1时启用】:theme_aux_color */
	public static final String THEME_AUX_COLOR = "theme_aux_color";
	
	
	
	
	/** ios最新版本号 */
	public static final String IOS_NEW_VERSION = "ios_new_version";
	
	/** ios升级方式 */
	public static final String IOS_PROMOTION_TYPE = "ios_promotion_type";
	
	/** android最新版本号 */
	public static final String ANDROID_NEW_VERSION = "android_new_version";
	
	/** android升级方式 */
	public static final String ANDROID_PROMOTION_TYPE = "android_promotion_type";
	
	/** android(代理商)最新版本号 */
	public static final String ANDROID_NEW_VERSION_DL = "android_new_version_dl";
	
	/** android(代理商)升级方式 */
	public static final String ANDROID_PROMOTION_TYPE_DL = "android_promotion_type_dl";
	
	//推广会员提成
	public static final String CUSTOMER_COMMISSION = "customer_commission";
	
	public static final String FINANCIAL_FEE = "financial_fee";
	//业务部主任团队任务额度
	public static final String DIRECTOR_QUOTA = "director_quota";
	//业务部经理团队任务额度
	public static final String DIRECTOR_MANAGER_QUOTA = "director_manager_quota";
	//拆标比例
	public static final String STANDARD_BID = "standard_bid";
	//业务室主任团队提成设置
	public static final String DIRECTOR_COMMISSION = "director_commission";
	//业务室主任团队提成设置
	public static final String DIRECTOR_MANAGER_COMMISSION = "director_manager_commission";
	//业务部经理提成
	public static final String BUSINESS_MANAGER_COMMISSION = "business_manager_commission";
	//个人推广提成
	public static final String PERSONAL_COMMISSION = "personal_commission";
	
	//快递密钥KEY
	public static final String EXPRESS_KEY = "express_key";
	
	/**短息提供商 （焦云）*/
	public static final String JY_SMS_PROVIDER = "yj_sms_provider";
	/**app Id*/
	public static final String JY_SMS_APP_ID = "jy_sms_app_id";
	/** app Key */
	public static final String JY_SMS_APP_KEY = "jy_sms_app_key";
	
	public static final String IS_INFO_SHOW = "is_info_show";
}
