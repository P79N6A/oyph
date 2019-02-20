package common.utils;

import org.apache.commons.lang.StringUtils;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import play.Logger;


/**
 * 正则相关验证
 * @author Administrator
 *
 */
@Deprecated
public class RegexUtils {
	
	/**
	 * 用户名是否符合规范（^[\u4E00-\u9FA5A-Za-z0-9_]+$）,默认长度3~10字符
	 * @return
	 */
	@Deprecated
	public static boolean isValidUsername(String username) {
		return isValidUsername(username,3,10);
	}
	
	
	/**
	 * 用户名是否符合规范（^[\u4E00-\u9FA5A-Za-z0-9_]+$）
	 * @param username
	 * @param min
	 * @param max
	 * @return
	 */
	@Deprecated
	public static boolean isValidUsername(String username,Integer min,Integer max){
		
		if (StringUtils.isBlank(username)) {
			return false;
		}
		String reg = "^[\u4E00-\u9FA5A-Za-z0-9_]{"+min+","+max+"}$";
		return username.matches(reg);
	}
	
	/**
	 * 密码是否符合规范（[a-zA-Z\d]{6,20}）
	 * @return
	 */
	@Deprecated
	public static boolean isValidPassword(String password) {
		return isValidPassword(password,6,20);
	}

	/**
	 * 密码是否符合规范（[a-zA-Z\d]{min,max}）
	 * @return
	 */
	@Deprecated
	public static boolean isValidPassword(String password,Integer min,Integer max){
		if (null == password) {
			return false;
		}
		String reg = "^([^\\s'‘’]{"+min+","+max+"})$";
		return password.matches(reg);
		
	}
	
	
	/**
	 * 是否有效手机号码
	 * @param mobileNum
	 * @return
	
*/
	@Deprecated
	public static boolean isMobileNum(String mobileNum) {
		if (null == mobileNum) {
			return false;
		}
	
		//return mobileNum.matches("^((13[0-9])|(14[4,7])|(15[^4,\\D])|(17[6-8])|(18[0-9]))(\\d{8})$");
		
		return mobileNum.matches("^((13[0-9])|(14[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))(\\d{8})$");
		
	}
	
	/**
	 * 是否有效邮箱
	 * @param email
	 * @return
	 */
	@Deprecated
	public static boolean isEmail(String email) {
		if (null == email) {
			return false;
		}
		
		return email.matches("^([a-zA-Z0-9])+([a-zA-Z0-9_.-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$");
	}
	
	/**
	 * 是否是QQ邮箱
	 */
	@Deprecated
	public static boolean isQQEmail(String email){
		if(null == email)
			return false;
		
		return email.matches("^[\\s\\S]*@qq.com$");
	}
	
	/**
	 * 是否数字(小数||整数)
	 * @param number
	 * @return
	 */
	@Deprecated
	public static boolean isNumber(String number) {
		if (null == number) {
			return false;
		}
		
		return number.matches("^[+-]?(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d)+)?$");
	}
	
	/**
	 * 是否整数
	 * @param number
	 * @return
	 */
	@Deprecated
	public static boolean isInt(String number) {
		if (null == number) {
			return false;
		}
		
		return number.matches("^[+-]?(([1-9]{1}\\d*)|([0]{1}))$");
	}
	
	/**
	 * 是否正整数
	 * @param number
	 * @return
	 */
	@Deprecated
	public static boolean isPositiveInt(String number) {
		if (null == number) {
			return false;
		}
		
		return number.matches("^[+-]?(([1-9]{1}\\d*)|([0]{1}))$");
	}
	
	/**
	 * 是否日期yyyy-mm-dd(yyyy/mm/dd)
	 * @param date
	 * @return
	 */
	@Deprecated
	public static boolean isDate(String date) {
		if (null == date) {
			return false;
		}
		return date.matches("^([1-2]\\d{3})[\\/|\\-](0?[1-9]|10|11|12)[\\/|\\-]([1-2]?[0-9]|0[1-9]|30|31)$");
	}
	
	
	/**
	 * 是否为16-22位银行账号
	 * @param bankAccount
	 * @return
	 */
	@Deprecated
	public static boolean isBankAccount(String bankAccount){
	    if (null == bankAccount) {
            return false;
        }
	    
	    return bankAccount.matches("^\\d{16,22}$");
	}
	
	
	public static void main(String[] args) {
		Logger.info("123@1qq.com".matches("^[\\s\\S]*@qq.com$")+"");
	}
}
