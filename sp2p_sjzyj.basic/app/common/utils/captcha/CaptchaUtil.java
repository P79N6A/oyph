package common.utils.captcha;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import play.libs.Codec;
import play.libs.Images.Captcha;

import common.utils.captcha.CaptchaImages.CaptchaExt;

public class CaptchaUtil {

	public static final String CACHE_TIME = "10min";
	
   /**
    * 生成验证码
    */
	public static String setCaptcha(){
		
		
		String randomID = Codec.UUID();
	    return randomID;
	}

	/**
	 * 生成验证码图片
	 * @param id
	 * @return
	 */
   public static Captcha CaptchaImage(String id){
	   CaptchaExt captcha = CaptchaImages.captcha();
	   
	    if(StringUtils.isBlank(id)){
	    	return captcha;
	    }

		String cacheCode = (String) Cache.get(id);
		if(StringUtils.isBlank(cacheCode)){  //一个id对应一个图文验证码，防止重复生成。
			String code = captcha.setBackground("#DEF1F8", "#DEF1F8").getText("#0056A0", 4); 
			Cache.safeSet(id, code, CACHE_TIME);
		}

		return captcha;
   }
 
    /**
     * 根据randomid取得对应的验证码值
     *
     * @param randomid
     * @return
     *
     * @author DaiZhengmiao
     * @createDate 2015年12月4日
     */
	public static String getCode(String randomid) {
		if(StringUtils.isBlank(randomid)){ 
			return null; 
			} 
		
		String code = (String) Cache.get(randomid);
		return code;
	}

	/**
	 * 删除randomid所对应的验证码值
	 *
	 * @param uuid
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static void clearCaptcha(String uuid) {
		Cache.safeDelete(uuid);
	}
   
	/**
	 * 生成UUID,放Cache中
	 * 
	 * @return UUID
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		Cache.set(uuid, uuid, CACHE_TIME);

		return uuid;
	}

	
	/**
	 * check Cache UUID
	 */
	public static boolean checkUUID(String key) {
		if(StringUtils.isBlank(key))
			return false;
			
		Object obj = Cache.get(key);
		
		try {
			Cache.delete(key);
		} catch (Exception e) {
			return false;
		}

		if (null == obj) {
			return false;
		}
			
		return true;
	}
	
	/**
	 * 生成指定颜色的验证码图片
	 * 
	 * */
	public static Captcha CaptchaImage(String id,String background,String text){
		CaptchaExt captcha = CaptchaImages.captcha();
		   
		if(StringUtils.isBlank(id)){
			return captcha;
		}

		String cacheCode = (String) Cache.get(id);
		if(StringUtils.isBlank(cacheCode)){  //一个id对应一个图文验证码，防止重复生成。
			String code = captcha.setBackground(background, background).getText(text, 4); 
			Cache.safeSet(id, code, CACHE_TIME);
		}

		return captcha;
	}
}
