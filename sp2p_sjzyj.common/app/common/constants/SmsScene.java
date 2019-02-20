package common.constants;

/**
 * 短信验证码发送场景
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年6月14日
 */
public final class SmsScene {

	private SmsScene() {
	}
	
	
	
	/** pc-用户注册,发送短信验证码到注册手机 */
	public static final String PC_REGISTER = "PC_REGISTER";
	
	/** pc-忘记密码,发送短信验证码到用户手机 */
	public static final String PC_FORGET_PWD = "PC_FORGET_PWD";
	
	/** pc-修改绑定手机,发送短信验证码到旧手机 */
	public static final String PC_UPDATE_MOBILE_OLD = "PC_UPDATE_MOBILE_OLD";
	
	/** pc-修改绑定手机,发送短信验证码到新手机 */
	public static final String PC_UPDATE_MOBILE_NEW = "PC_UPDATE_MOBILE_NEW";
	
	
	/** app-用户注册,发送短信验证码到注册手机 */
	public static final String APP_REGISTER = "register";  //与app接口同步，不能随意改动
	
	/** app-忘记密码,发送短信验证码到用户手机 */
	public static final String APP_FORGET_PWD = "forgetPwd";  //与app接口同步，不能随意改动
	
	
	/** wechat-用户注册,发送短信验证码到注册手机 */
	public static final String WX_REGISTER = "WX_REGISTER";  
	
	/** wechat-忘记密码,发送短信验证码到用户手机 */
	public static final String WX_FORGET_PWD = "WX_FORGET_PWD";  
	
	
	
}
