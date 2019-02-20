
package common.constants;
/**
 * 系统运行时缓存key
 *
 * @description 主要在控制器中添加修改和销毁(如用户登录后系统保存的信息)，不涉及到内存表之类的缓存(如系统参数等)
 *
 * @author DaiZhengmiao
 * @createDate 2016年1月23日
 */
public final class CacheKey {

	private CacheKey() {
	}
	
	/** 当前登录用户凭证缓存key前缀（key = front_ + sessionID） */
	public static final String FRONT_ = "front_";
	
	/** 当前用户是否模拟登陆状态（key = simulated_ + sessionID） */
	public static final String SIMULATED_ = "simulated_";
	
	/** 尝试登陆次数 */
	public static final String LOGINCOUNT_ = "loginCount_";
	
	/** 用户信息缓存Key前缀（key = user_ + 用户ID） */
	public static final String USER_ = "user_";
	
	/** 用户资金签名时使用的锁缓存key前缀（key = user_fund_lock_ + 用户ID）  */
	public static final String USER_FUND_LOCK_ = "user_fund_lock_";
	
	/** 用户登入手机号码 */
	public static final String LOGIN_USER_MOBILE = "login_user_mobile_";
	
	/** 用户对象 */
	public static final String LOGIN_USER_USERID = "userid_";
	
	/** 当前登录:后台管理员ID(存入的t_supervisor对象的CurrSupervisor) */
	public static final String LOGIN_SUPERVISOR = "login_supservisor_";
	
	/** 当前登录:后台管理员ID(存入的t_supervisor对象的id) */
	public static final String LOGIN_SUPERVISOR_ID = "login_supservisor_id_";
	
	/** 标的信息缓存前缀（key = bid_ + sessionID） */
	public static final String BID_ = "bid_";
	
	/** 产品信息缓存前缀（key = product_ + sessionID） */
	public static final String PRODUCT_ = "product_";
	
	/** 帮助信息缓存前缀（key = help_ + sessionID） */
	public static final String HELP_ = "help_";
	
	/** 内容信息缓存前缀（key = information_ + sessionID） */
	public static final String INFORMATION_ = "information_";
}
