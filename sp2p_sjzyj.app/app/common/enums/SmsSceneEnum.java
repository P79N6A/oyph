package common.enums;

import org.apache.commons.lang.StringUtils;

/***
 * 手机场景枚举
 *
 * @author luzhiwei
 * @createDate 2016-4-1
 */
public enum SmsSceneEnum {

    /**注册场景**/
	REGISTER("register"),

    /**找回密码场景**/
	FORGET_PWD("forgetPwd");


	/** 描述 */
	public String scene;
	
	private SmsSceneEnum(String scene) {
		this.scene = scene;
	}
	
	
	public static SmsSceneEnum getEnum(String scene){
		if(StringUtils.isBlank(scene)){
			return null;
		}
		SmsSceneEnum[] clients = SmsSceneEnum.values();
		for (SmsSceneEnum cli : clients) {
			if (cli.scene .equals(scene)) {
				return cli;
			}
		}
		return null;
	} 

}
