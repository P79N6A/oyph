package common.constants;

/**
 *  常量值
 *
 * @description 
 *
 * @author  yaoyi
 * @createDate 2015年12月18日
 */
public final class Constants {
	
	private Constants() {

	}
	
	/*======= 不同托管第三方差异性配置  =======*/
	/** 借款服务费费率：默认上限50% */
	public static double LOAN_SERVICE_FEE_MAXRATE = 50.0;
	
	/** 理财服务费费率：默认上限100% */
	public static double INVEST_SERVICE_FEE_MAXRATE = 100.0;
	
	/** 债权转让费费率：默认上限10% */
	public static double  DEBT_TRANSFER_MAXRATE = 10.0;
	
	/** 提现手续费费率：默认上限1% */
	public static double  WITHDRAW_MAXRATE = 1;
	
		
	/*======= 缓存时间  =======*/
	/** 缓存时间:12h */
	public static final String CACHE_TIME_HOURS_12 = "12h";
	
	/** 缓存时间:24h */
	public static final String CACHE_TIME_HOURS_24 = "24h";
	
	/** 缓存时间:30min */
	public static final String CACHE_TIME_MINUS_30 = "30mn";
	
	/** 缓存时间:60s */
	public static final String CACHE_TIME_SECOND_60 = "60s";
	
	/** 缓存时间:2min */
	public static final String CACHE_TIME_MINUS_2 = "2mn";
	
	/*======= 安全云盾相关常量  =======*/
	/** 云盾校验用户 */
	public static final String CLOUD_SHIELD_NOUSER = "-1";
	
	/** 云盾校验密码 */
	public static final String CLOUD_SHIELD_PASSWORD_ERROR = "-2";
	
	/** 云盾校验签名 */
	public static final String CLOUD_SHIELD_SIGN_FAULT = "-3";
	
	/** 云盾校验服务器时间 */
	public static final String CLOUD_SHIELD_SERVICE_TIME = "-4";
	
	
	/*======= 加密串相关常量  =======*/
	/** 加密串有效时间(s) */
	public static final int VALID_TIME = 3600;
	
	/** 加密action标识:标ID */
	public static final String BID_ID_SIGN = "b";
	
	/** 加密action标识:债权转让项目ID */
	public static final String DEBT_TRANSFER_ID_SIGN = "debt";
	
	/** 加密action标识:等级ID */
	public static final String CREDITLEVEL_ID_SIGN = "cl";
	
	/** 加密action标识:账单ID */
	public static final String BILL_ID_SIGN = "bill";
	

	/** 加密action标识:投资ID */
	public static final String INVEST_ID_SIGN = "invest";
	

	/** 加密action标识:产品ID */
	public static final String PRODUCT_ID_SIGN = "p";
	
	/** 加密action标识:用户ID */
	public static final String USER_ID_SIGN = "user";
	
	/** 加密action标识:管理员ID */
	public static final String SUPERVISOR_ID_SIGN = "supervisor_id";
	
	/** 加密action标识:资料ID */
	public static final String ITEM_ID_SIGN = "i";
	
	/** 加密action标识:用户资料ID */
	public static final String USER_ITEM_ID_SIGN = "ui";
	
	/** 加密action标识:用户站内信 */
	public static final String MSG_ID_SIGN = "mi";
	
	/** 加密action标识:用户邮箱 */
	public static final String MSG_EMAIL_SIGN = "email";
	
	/** 合同模板加密标志 */
	public static final String MSG_PACTTEMP_SIGN = "pactTemp";
	
	/** 合同模板加密标志 */
	public static final String MSG_PACT_SIGN = "pact";
	
	/** 加密action标识:资讯ID */
	public static final String INFORMATION_ID_SIGN="infor";
	
	/** 加密action标识:广告图片ID  */
	public static final String ADS_ID_SIGN="ads";
	
	/** 加密action标识:理财顾问ID  */
	public static final String CONSULTANT_ID_SIGN="consultant";
	
	/** 加密action标识:合作伙伴ID  */
	public static final String PARTNER_ID_SIGN="partner";
	
	/** 加密action标识:帮助中心ID  */
	public static final String HELPCENTER_ID_SIGN="help";
	
	/** 加密action标识:兑换记录  */
	public static final String CONV_ID_SIGN="conv";
	
	/** 加密action标识:主题  */
	public static final String THEME_ID_SIGN="theme";
	
	/** 一些弹框的sign加密value */
	public static final String SHOW_BOX = "show_box";
	
	/** 加密action标识:通知模板  */
	public static final String NOTEMP_ID_SIGN="notemp";
	
	/** 加密action标识:cps推广记录  */
	public static final String CPS_ID_SIGN="cps";
	
	/** 加密action标识:财富圈推广记录  */
	public static final String WEALTHCIRCLE_ID_SIGN="wealCir";
	
	/*======= 服务费规则中的参数对应的key =======*/
	/** JSON对象-借款服务费的key：借款金额百分比 */
	public static final String LOAN_AMOUNT_RATE = "loan_amount_rate";
	
	/** JSON对象-借款服务费的key：减去的借款期数 */
	public static final String SUB_PERIOD="sub_period";
	
	/** JSON对象-借款服务费的key：减去借款期数后，借款金额的百分比 */
	public static final String SUB_LOAN_AMOUNT_RATE="sub_loan_amount_rate";
	
	/** JSON对象-理财服务费的key：百分比 */
	public static final String INVEST_AMOUNT_RATE="invest_amount_rate";
	
	/** JSON对象-逾期罚息的key：百分比 */
	public static final String OVERDUE_AMOUNT_RATE="overdue_amount_rate";
	
	
	/** 日利息结算常量：360天 */
	public static final int DAY_INTEREST = 360;
	
	
	/*======= 平台栏目展示数据条数常量  =======*/
	/** 前台-首页-理财故事所取条数 1条 */
	public static final int INVEST_STORY_NUM = 6;
	
	/** 前台-首页-媒体报道所取条数  1条 */
	public static final int MEDIA_REPORT_NUM = 6;
	
	/** 前台-首页-资讯管理 (官方公告,媒体报道，理财故事) 所取条数  5条 */
	public static final int INFORMATION_ADS_NUM = 5;
	
	/** 前台-首页-理财顾问 所取条数  20*/
	public static final int INVEST_CONSULTANT_NUM = 20;
	
	/** 前台-首页-合作伙伴 所取条数 24 */
	public static final int PARTNER_NUM = 24;
	
	
	/*======= 时间格式化公用常量  =======*/
	/** 页面显示用日期格式（ 包含时间） yy/MM/dd HH:mm:ss*/
	public static final String DATE_TIME_FORMATE = "yy/MM/dd HH:mm:ss";
	
	/** 页面显示用日期格式（不包含时间） yy/MM/dd */
	public static final String DATE_FORMATE = "yy/MM/dd";
	
	/** 时间控件使用的时间格式 yyyy/MM/dd HH:mm:ss  */
	public static final String DATE_PLUGIN = "yyyy/MM/dd HH:mm:ss";
	
	/** 页面显示用日期格式（不包含时间） yyyyMMdd */
	public static final String DATE_FORMATES = "yyyyMMdd";
	
	/** 页面显示用日期格式（不包含时间） yyyy-MM-dd */
	public static final String DATE_FORMATESS = "yyyy-MM-dd";
	
	/** 页面显示用日期格式(仅包含年月) yyyy-MM */
	public static final String DATE_FORMATEMM = "yyyy-MM";
	
	/** 页面显示用日期格式(仅包含月) MM*/
	public static final String DATE_MM = "MM";

	/** 页面显示用日期格式(仅包含年) yyyy*/
	public static final String DATE_YY = "yyyy";
	
	
	
	
	/*======= Number格式化公用常量  =======*/
	/** 列表中金额格式化显示,加前缀￥  */
	public static final String FINANCE_FORMATE_TAD = "￥0.00";
	
	/** 金额格式化显示，保留两位小数，单位调用处控制 */
	public static final String FINANCE_FORMATE_NORMAL = "0.00";
	
	/** 数字保留一位小数 */
	public static final String NUMBER_FORMATE_NORMAL = "0.0";
	
	//=================================================================================================================
	/** 借款账单 查询所有  */
	public static final int PAY_ALL = 0;

	/** 借款账单 查询未还  */
	public static final int NO_PAY = 1;
	
	/** 借款账单 查询已还 */
	public static final int ALEADY_PAY = 2;
	
	//=================================================================================================================
	/** 图片最大不超过 2M 即2048kb */
	public static final int GREAT_PIC_SIZE = 2048;

	/** 后台运营数据报表显示类型 1：昨天  2：最近7天  3：最近30天 */
	public static final int YESTERDAY = 1;
	public static final int LAST_7_DAYS = 2;
	public static final int LAST_30_DAYS = 3;
	
	/** 一天两端时间 */
	public static final String STARTTIME = " 00:00:00";
	public static final String ENDTIME = " 23:59:59";
	
	/** 标的发布方式，1-前台发标；2-后台发标 */
	public static final int BID_CREATE_FROM_FRONT = 1;
	public static final int BID_CREATE_FROM_BACK = 2;
	public static final int BID_CREATE_FROM_OTHER = 3;
	
	//==================================================================================================================
	/** 导出 */
	public static final int EXPORT = 1; 
	
	/** 是否限制逾期总费 */
	public static final boolean IS_STINT_OF = true;
	/** 限制为多少倍 */
	public static final double OF_AMOUNT = 2.5;
	
	
	//=================================================================================================================
	/** 资料审核：本地上传*/
	public static final int LOCAL_UPLAOD = 0;
	
	/** 资料审核：科目库上传 */
	public static final int LIBRARY_UPLOAD = 1;
	
	/** 群发定时短信任务最多发送次数  */
	public static final int TRY_SMS_TIMES = 1;
	
	/** 群发定时邮件任务最多发送次数  */
	public static final int TRY_EMAIL_TIMES = 1;
	
	
}
