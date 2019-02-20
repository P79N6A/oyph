package controllers.common;

import net.sf.json.JSONObject;
import play.libs.Images;
import play.mvc.Controller;

import common.utils.ResultInfo;
import common.utils.captcha.CaptchaUtil;

/** 
 * 验证码相关操作类
 * 
 * @description 
 * 
 * @author ZhouYuanZeng 
 * @createDate 2016年4月6日 下午4:01:30  
 */
public class CaptchaController extends Controller{

	/**
	 * 根据random生成验证码图片
	 *
	 * @param uuid 随机的UUID，该id会作为取出最终结果的唯一凭证
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static void createCaptcha(String uuid) {
		Images.Captcha captcha = CaptchaUtil.CaptchaImage(uuid);
		
		renderBinary(captcha);
    }
	
	/**
	 * 刷新验证码
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static void flushCaptcha() {
		String randomID = CaptchaUtil.setCaptcha();
		JSONObject json = new JSONObject();
		json.put("randomID", randomID);
		
		renderJSON(json.toString());
	}
	
	/**
	 * 校验验证码(render一个JSON)
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static void checkCaptcha(){
		ResultInfo result = new ResultInfo();
		
		String code = params.get("code");
		String randomID = params.get("randomID");
		
		String RandCode = CaptchaUtil.getCode(randomID);
		//CaptchaUtil.clearCaptcha(randomID); 异步校验完成后，再实际操作(登录)过程中仍旧会进行再次校验，此时验证码不能清除，在二次校验验证码是必须删除缓存中的验证码
		
		if (RandCode == null) {
			result.code = -1;
			result.msg = "验证码已经失效";
			
			renderJSON(result);
		}
		
		if (!code.equalsIgnoreCase(RandCode)) {
			result.code = -2;
			result.msg = "验证失败";
			
			renderJSON(result);
		} 
		
		result.code = 1;
		result.msg = "验证成功";
		
		renderJSON(result);
	}
	

	/**
	 * 校验验证码(render一个true或者false)
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static void checkCaptcha2(){

		String code = params.get("code");
		String randomID = params.get("randomID");
		
		String RandCode = CaptchaUtil.getCode(randomID);
		//CaptchaUtil.clearCaptcha(randomID); 异步校验完成后，再实际操作(登录)过程中仍旧会进行再次校验，此时验证码不能清除，在二次校验验证码是必须删除缓存中的验证码
		
		if (RandCode == null) {
			
			renderJSON(false);
		}
		
		if (!code.equalsIgnoreCase(RandCode)) {
		
			renderJSON(false);
		} 
		
		renderJSON(true);
	}
	/**
	 * 生成自定义颜色的二维码图片
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public static void createCustomColorCaptcha(String uuid,String background,String text) {
		Images.Captcha captcha = CaptchaUtil.CaptchaImage(uuid,"#"+background,"#"+text);
		
		renderBinary(captcha);
    }

}
