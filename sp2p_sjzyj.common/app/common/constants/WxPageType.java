package common.constants;

public final class WxPageType {

	private WxPageType() {
	}
	/** 错误页面，不带引导框 */
	public static final int PAGE_FAIL = -1;
	
	/** 公用错误，带引导框(首页或者账户中心)  */
	public static final int PAGE_COMMUNAL_FAIL = -2;
	
	/** 错误页面，开户引导 */
	public static final int PAGE_FAIL_PAYMENT = -3;
	
	/** 错误页面，绑卡引导 */
	public static final int PAGE_FAIL_BINDCAED = -4;
	
	/** 正确，不带引导框 */
	public static final int PAGE_SUCC = 1;
	
	/** 正确，带开户引导页(开户或者首页) */
	public static final int PAGE_SUCC_PAYMENT = 2;
	
	/** 公用正确 ，带引导框(首页或者账户中心)  */
	public static final int PAGE_COMMUNAL_SUCC = 3;
	
	/** 正确 ，密码修改成功((返回主页或登录))  */
	public static final int PAGE_UPDATE_PASSWORD_SUCC = 4;
	
}
