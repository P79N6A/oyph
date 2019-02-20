package common.constants;

/**
 * 定时任务  - 常量
 * 
 * @author niu
 */
public final class JobConstants {

	public static final String JOB_ACCESS_URL = "http://192.168.0.230:8002/job?JOB=";
	
	private JobConstants() {
	}	
																			
	public static final int JOB_BILL_REMIND = 131;							// 131: 账单提醒    
	
	public static final int JOB_CHECK_BID_IS_FLOW = 132;					// 132: 检查流标    
	
	public static final int JOB_CHECK_DEBT_IS_FLOW = 133; 					// 133: 检查债券过期    
	
	public static final int JOB_INFORMATION_DISCLOSURE = 134;				// 134: 信息披露    
	
	public static final int JOB_SEND_EMAIL = 135;							// 135: 发送邮件    
	
	public static final int JOB_CLEAN_SEND_EMAIL = 136;						// 136: 清理邮件    
	
	public static final int JOB_HOME_PAGE_DATA_STATISTICS = 137; 			// 137: 首页数据统计    
	
	public static final int JOB_MARK_OVERDUE = 138;							// 138: 标记逾期    
	
	public static final int JOB_MASS_SEND_MAIL = 139;						// 139: 群发邮件    
	
	public static final int JOB_MASS_SEND_SMS = 140;						// 140: 群发短信    
	
	public static final int JOB_UPDATE_REDPACKET_STATUS = 141;				// 141: 群发短信    
	
	public static final int JOB_REWARD_GRANT = 142;							// 142: 奖励补助    
	
	public static final int JOB_SEND_SMS = 143;								// 143: 发送短信    
	
	public static final int JOB_CLEAR_SEND_SMS = 144;						// 144: 清理短信    
	
	public static final int JOB_TEAM_COMPUTE = 145;							// 145: 团队统计    
	
	public static final int JOB_TEAM_COMMISSION = 146;						// 146: 团队提成    

	public static final int JOB_PROXY_MONTH_PROFIT = 147;					// 147: 代理商月提成 
	
	public static final int JOB_PROXY_CURRENT_PROFIT = 148;					// 148: 代理商当前提成    
	
	public static final int JOB_INTEREST_RATE_COUPONS = 149;				// 149: 加息券过期   
	
	public static final int JOB_EXP_BID_AUTO_REPAYMENT = 150;				// 150: 体验标自动还款
	 
	public static final int JOB_PROXY__UPDATE_USER = 158;                   // 158: 更新用户资金信息  
	
	public static final int JOB_PROXY_UPDATE_SALE_ANNUAL = 152;             // 152: 更新业务员年化
	
	public static final int JOB_PROXY_UPDATE_SALE_PROFIT =153;              // 153: 更新业务员提出
	
	public static final int JOB_PROXY_UPDATE_INFO =154;						// 154: 更新代理商信息
	
	public static final int JOB_PROXY_UPDATE_SALE_HISTORY_ANNUAL = 155;   	// 155: 更新上个月业务员年化
	
	public static final int JOB_PROXY_UPDATE_SALE_HISTORY_PROFIT =156;      // 155: 更新上个月业务员提成
	
	public static final int JOB_PROXY_UPDATE_HISTORY_INFO =157;             // 157: 更新代理商上个月提成
				
	public static final int JOB_PDF_BATCH_DEL =151;							// 151: PDF文件批量删除
	
	public static final int JOB_REFRESH_TOKEN = 159;						// 159: 微信刷新token
	
	public static final int JOB_SERVICE_STATISTICS = 160;					// 160: 客服月末统计
	
	public static final int JOB_DAILY_PAPER = 162;							// 162: 日报
	
	public static final int JOB_BORROWEREPAYMENT = 161;						// 161: 金融办月报 
	
	public static final int JOB_QUARTER_REPORT = 163;						// 163:金融办季报
	
	public static final int JOB_SHARE_NUM = 164;							// 164:大转盘活动分享朋友圈次数定时清0
	
	public static final int JOB_UPDATE_USER_VIP = 165;						// 165: 更改客户VIP等级
	
	public static final int JOB_INSERT_MONTH = 166;							// 166; 金融办月报4--5插入数据库
	
	public static final int JOB_INSERT_SIX = 167;							// 167: 金融办月报6插入数据库
	
	public static final int JOB_INSERT_SEVEN = 168;							// 168: 金融办月报7插入数据库
	
	public static final int JOB_INSERT_EIGHT = 169;							// 169: 金融办月报8插入数据库

	public static final int JOB_INSERT_NINE = 170;							// 170: 金融办月报9插入数据库
	
	public static final int JOB_BILL_REPANYMENT_TIME = 171;					// 171： 定时查询借款标的信息(还款日)
}