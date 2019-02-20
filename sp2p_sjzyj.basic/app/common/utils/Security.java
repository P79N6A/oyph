package common.utils;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.shove.security.Encrypt;

/**
 * ID加密、解密类
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月18日
 */
public class Security {
	
	/** 日期格式:yyyy-MM-dd HH:mm:ss */
	public static final String yyyy_MM_ddHH_mm_ss = "yyyy-MM-dd HH:mm:ss";

	/**
	 * Id加密
	 * @description 
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 * @param id 需要加密的Id
	 * @param action 加密action标识
	 * @param encryptionKey 加密Key
	 * @return
	 */
	public static String addSign(long id, String action, String encryptionKey) {
		
		/* 将id、action标示、当前时间利用3des加密 */
		String des = Encrypt.encrypt3DES(id+","+action+","+DateUtil.dateToString(new Date(), yyyy_MM_ddHH_mm_ss), 
				encryptionKey);
		
		/* 将加密得到的des利用MD5再次加密 */
		String md5 = Encrypt.MD5(des + encryptionKey);
		
		/* 将得到的des和MD5密文拼接处理 */
		String sign= des + md5.substring(0, 8);
		
		return sign;
	}
	
	/**
	 * Id解密
	 * @description 
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 * @param sign 密文
	 * @param action 加密action标识
	 * @param validLength 密文有效时长
	 * @param encryptionKey 加密Key
	 * @return
	 */
	public static ResultInfo decodeSign(String sign, String action, int validLength, 
			String encryptionKey ) {
		ResultInfo result = new ResultInfo();
		/* 判断密文是否为空 */
		if(StringUtils.isBlank(sign) || sign.length() < 8) {
			result.code = -1;
			result.msg = "无效请求";
			
			return result;
		}
		
		String des = sign.substring(0, sign.length()-8);
		String key = sign.substring(sign.length()-8);
		String md5 = Encrypt.MD5(des + encryptionKey);
		
		if(!key.equals(md5.substring(0, 8))) {
			result.code = -1;
			result.msg = "无效请求";
			
			return result;
		}
		
		String[] decryArray = Encrypt.decrypt3DES(des,encryptionKey).split(",");
		
		if(decryArray.length != 3) {
			result.code = -1;
			result.msg = "无效请求";
			
			return result;
		}
		
		if(!decryArray[1].equals(action)) {
			result.code = -1;
			result.msg = "无效请求";
			
			return result;
		}
		
		Date validTime = DateUtil.strToDate(decryArray[2], yyyy_MM_ddHH_mm_ss);
		
		if(validTime == null) {
			result.code = -1;
			result.msg = "无效请求";
			
			return result;
		}
		
		/* 判断密文是否过期 */
		if(!DateUtil.isInValidTime(validTime, validLength)) {
			result.code = -1;
			result.msg = "请求时间已过期，请重新请求";
			
			return result;
		}
		
		if(!StrUtil.isNumericInt(decryArray[0])) {
			result.code = -1;
			result.msg = "无效请求";

			return result;
		}
		result.code = 1;
		result.msg = "解密成功";
		result.obj = decryArray[0];
		
		return result;
	}
	
	/**
	 * Email加密
	 * @description 
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 * @param id 需要加密的Id
	 * @param action 加密action标识
	 * @param encryptionKey 加密Key
	 * @return
	 */
	public static String addEmailSign(String email, String action, String encryptionKey) {
		
		/* 将id、action标示、当前时间利用3des加密 */
		String des = Encrypt.encrypt3DES(email+","+action+","+DateUtil.dateToString(new Date(), yyyy_MM_ddHH_mm_ss), 
				encryptionKey);
		
		/* 将加密得到的des利用MD5再次加密 */
		String md5 = Encrypt.MD5(des + encryptionKey);
		
		/* 将得到的des和MD5密文拼接处理 */
		String sign= des + md5.substring(0, 8);
		
		return sign;
	}
	
	/**
	 * 	 * email解密
	 * @param sign 密文
	 * @param action 加密action标识
	 * @param validLength 密文有效时长
	 * @param encryptionKey 加密Key
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月15日
	 *
	 */
	public static ResultInfo decodeEmailSign(String sign, String action, int validLength, 
			String encryptionKey ) {
		ResultInfo result = new ResultInfo();
		/* 判断密文是否为空 */
		if(StringUtils.isBlank(sign) || sign.length() < 8) {
			result.code = -1;
			result.msg = "无效请求";
			
			return result;
		}
		
		String des = sign.substring(0, sign.length()-8);
		String key = sign.substring(sign.length()-8);
		String md5 = Encrypt.MD5(des + encryptionKey);
		
		if(!key.equals(md5.substring(0, 8))) {
			result.code = -1;
			result.msg = "无效请求";
			
			return result;
		}
		
		String[] decryArray = Encrypt.decrypt3DES(des,encryptionKey).split(",");
		
		if(decryArray.length != 3) {
			result.code = -1;
			result.msg = "无效请求";
			
			return result;
		}
		
		if(!decryArray[1].equals(action)) {
			result.code = -1;
			result.msg = "无效请求";
			
			return result;
		}
		
		Date validTime = DateUtil.strToDate(decryArray[2], yyyy_MM_ddHH_mm_ss);
		
		if(validTime == null) {
			result.code = -1;
			result.msg = "无效请求";
			
			return result;
		}
		
		/* 判断密文是否过期 */
		if(!DateUtil.isInValidTime(validTime, validLength)) {
			result.code = -1;
			result.msg = "请求时间已过期，请重新请求";
			
			return result;
		}
		
		if(!StrUtil.isEmail(decryArray[0])) {
			result.code = -1;
			result.msg = "无效请求";

			return result;
		}
		result.code = 1;
		result.msg = "解密成功";
		result.obj = decryArray[0];
		
		return result;
	}

}
