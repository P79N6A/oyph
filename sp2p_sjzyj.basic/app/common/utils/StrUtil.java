package common.utils;

import groovyjarjarasm.asm.tree.IntInsnNode;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.list.TreeList;
import org.apache.commons.lang.StringUtils;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.shove.Convert;

import common.utils.number.NumberFormat;

/**
 * 字符串相关工具类
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月17日
 */
public final class StrUtil {

	private StrUtil() {}
	
	/**
	 * 用户名是否符合规范（^[\u4E00-\u9FA5A-Za-z0-9_]+$）,默认长度3~10字符
	 * @return
	 */
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
	public static boolean isValidPassword(String password) {
		return isValidPassword(password,6,20);
	}

	/**
	 * 密码是否符合规范（[a-zA-Z\d]{min,max}）
	 * @return
	 */
	public static boolean isValidPassword(String password,Integer min,Integer max){
		if (null == password) {
			return false;
		}
		String reg = "^([^\\s'‘’]{"+min+","+max+"})$";
		return password.matches(reg);
		
	}
	
	/**
	 * 密码是否符合规范（[a-zA-Z\d0-9]{min,max}）
	 * @return
	 */
	public static boolean isValidPasswordes(String password,Integer min,Integer max){
		if (null == password) {
			return false;
		}
		String reg = "^(?![^0-9]+$)(?![^a-zA-Z]+$).+$";
		return password.matches(reg);
		
	}
	
	/**
	 * 是否有效手机号码
	 * @param mobileNum
	 * @return
	 */

	public static boolean isMobileNum(String mobileNum) {
		if (null == mobileNum) {
			return false;
		}
	
		
		return mobileNum.matches("^((13[0-9])|(14[0-9])|(19[0-9])|(16[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))(\\d{8})$");
		//return mobileNum.matches("^((13[0-9])|(14[4,7])|(15[^4,\\D])|(17[6-8])|(18[0-9]))(\\d{8})$");
	}
	
	/**
	 * 是否有效邮箱
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (null == email) {
			return false;
		}
		
		return email.matches("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$");
	}
	
	/**
	 * 是否是QQ邮箱
	 */
	public static boolean isQQEmail(String email){
		if(null == email)
			return false;
		
		return email.matches("^[\\s\\S]*@qq.com$");
	}
	
	
	/**
	 * 是否为16-22位银行账号
	 * @param bankAccount
	 * @return
	 */
	public static boolean isBankAccount(String bankAccount){
	    if (null == bankAccount) {
            return false;
        }
	    
	    return bankAccount.matches("^\\d{16,22}$");
	}
	
	/**
	 * 是否是纯数字，不含空格
	 *
	 * @param str
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月8日
	 */
	public static boolean isNumeric(String str) {
		if (StringUtils.isBlank(str)) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 是否数值类型，整数或小数
	 * @param number
	 * @return
	 */
	public static boolean isNumericalValue(String str) {
		if (null == str) {
			return false;
		}
		
		return str.matches("^[+-]?(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d)+)?$");
	}
	
	/**
	 * 是否整数(^[+-]?(([1-9]{1}\\d*)|([0]{1}))$)
	 *
	 * @param str
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月8日
	 */
	public static boolean isNumericInt(String str) {
		if (str == null) {
			return false;
		}

		return str.matches("(^[+-]?([0-9]|([1-9][0-9]*))$)");
	}
	
	/**
	 * 是否正整数
	 * @param number
	 * @return
	 */
	public static boolean isNumericPositiveInt(String number) {
		if (null == number) {
			return false;
		}
		
		return number.matches("^[+-]?(([1-9]{1}\\d*)|([0]{1}))$");
	}
	
	/**
	 * 判断是否是正整数数或者一位小数
	 *
	 * @param str
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月11日
	 */
	public static boolean isOneDouble(String str) {
		//
		if (str == null) {
			return false;
		}

		return str.matches("^(\\d+\\.\\d{1,1}|\\d+)$");
	}
	
	/**
	 * 判断给定字符串是否小于给定的值(min)
	 *
	 * @param str
	 * @param min
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月11日
	 */
	public static boolean isNumLess(String str,float min) {
		if (str == null) {

			return false;
		}
		if (!isNumeric(str)) {

			return false;
		}

		float val = Float.parseFloat(str);

		return (val < min);
	}
	
	/**
	 * 判断给定的字符串大于说给定的值
	 *
	 * @param str
	 * @param max
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月11日
	 */
	public static boolean isNumMore(String str,float max) {
		if (str == null) {

			return false;
		}
		if (!isNumeric(str)) {

			return false;
		}

		float val = Float.parseFloat(str);

		return (val > max);
	}
	
	/**
	 * 是否小数
	 *
	 * @param str
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月8日
	 */
	public static boolean isNumericDouble(String str) {
		if (str == null) {
			return false;
		}

		return str.matches("^[+-]?(([1-9]\\d*\\.?\\d+)|(0{1}\\.\\d+)|0{1})");
	}
	
	/**
	 * 是否是16进制颜色值
	 *
	 * @param str
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月19日
	 */
	public static boolean isColor(String str){
		if (str == null) {
			return false;
		}
	
		return str.matches("(^([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$)");
	}

	/**
	 * 判断是否是Boolean值
	 *
	 * @param str
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月8日
	 */
	public static boolean isBoolean(String str) {
		if (str == null) {
			return false;
		}

		return str.equals("true") || str.equals("false");
	}
	
	

	/**
	 * 判断是否是日期,格式：yyyy-MM-dd
	 *
	 * @param str
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月8日
	 */
	public static boolean isDate(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		try {
			format.parse(str);
		} catch (ParseException e) {
			return false;
		}

		return true;
	}
	
	
	/**
	 * 使用map中的值替换str的指定内容
	 *
	 * @description 假设:date  supervisor设置SEO规则,date。其中date和supervisor是要被替换的内容，则map中key就为date,supervisor。
	 * 
	 * @param str
	 * @param map
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月17日
	 */
	public static String replaceByMap(String str,Map<String,String> map){
		String result = str;

		if (map == null || map.isEmpty()) {
			return result;
		}
		
		for (String _key : map.keySet()) {
			result = result.replace(_key, map.get(_key));
		}
		
		return result;
	}
	
	
	/**
	 * 计算一个颜色值的的0.7透明度
	 *
	 * @param color 颜色值为ffffff/fff两种形式
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月19日
	 */
	public static String colorByAlpha(String color){
		if(color.length() == 3){
			color = "000"+color;
		}
		String r = color.substring(0, 2);
		String g = color.substring(2, 4);
		String b = color.substring(4);
		String nColor = Integer.toHexString(Integer.parseInt(r, 16)*7/10+255*3/10)+Integer.toHexString(Integer.parseInt(g, 16)*7/10+255*3/10)+Integer.toHexString(Integer.parseInt(b, 16)*7/10+255*3/10);
		return nColor;
	}
	
	
	/**
	 * 将map<String,Object>转换成Map<String,String>并对其中的double和date类型进行格式化
	 *
	 * @param param
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月22日
	 */
	public static Map<String, String> mapToMap(Map<String, Object> param){
		
		Map<String, String> param_format = new HashMap<String, String>();
		if (param != null && param.keySet() != null && param.keySet().size() != 0) {
			for (String _key : param.keySet()) {
				Object obj = param.get(_key);
				if (obj instanceof Double) {
					Double d = (Double) obj;
					param_format.put(_key, NumberFormat.round(d, 2));

				} else if (obj instanceof Date) {
					Date date = (Date) obj;
					param_format.put(_key, DateUtil.dateToString(date, "yyyy-MM-dd HH:mm:ss"));

				} else {
					param_format.put(_key, obj.toString());

				}
			}
		}
		
		return param_format;
	}
	
	/**
	 * 前台分页显示的页面1,2,3,...,6,7,8
	 *
	 * @param totalPages
	 * @param currPage
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月19日
	 */
	public static String[] getPageArr(int totalPages,int currPage){
		int[] pageArr = null;
		if(totalPages <= 6){
			pageArr = new int[totalPages];
			for (int i = 1; i <= totalPages; i++) {
				pageArr[i-1] = i;
			}
		
		} else {
			Set<Integer> pageSet = new TreeSet<Integer>();
			for (int i = 1; i <= 3; i++) {
				pageSet.add(i);
			}
			for (int i = totalPages; i >= totalPages - 2; i--) {
				pageSet.add(i);
			}
			
			for (int i = currPage - 2; i <= currPage + 2; i++) {
				if (i > 0 && i < totalPages) {
					pageSet.add(i);
				}
			}
			Object[] objs = pageSet.toArray();
			pageArr = new int[objs.length];
			for(int i =0, max = objs.length;i<max;i++){
				pageArr[i] = Integer.parseInt(objs[i].toString());
			}
		}

		List<String> list = new ArrayList<String>();
		for(int i=0,max = pageArr.length;i<max;i++){
			list.add(pageArr[i]+"");
			if(i < max-1){
				if(pageArr[i]+1<pageArr[i+1]){
					list.add("...");
				}
			}
			
		}
		
		String[] strs = new String[list.size()];
		list.toArray(strs);
		
		return strs;
	}
	
	public static String[] toStr(int[] a){
		List<String> list = new ArrayList<String>();
		for(int i=0,max = a.length;i<max;i++){
			list.add(a[i]+"");
			if(i < max-1){
				if(a[i]+1<a[i+1]){
					list.add("...");
				}
			}
			
		}
		String[] strs = new String[list.size()];
		list.toArray(strs);
		return strs;
	}
	
	public static String asterisk (String str, int start, int end, int count) {
		
		StringBuffer result = new StringBuffer();
		int length = str.length();
		
		if (start <= length ) {
			result.append(str.substring(0, start));
		} else {
			result.append(str.substring(0, length));
		}
		
		for (int i=0; i<count; i++) {
			result.append("*");
		}
		
		if (end <= length ) {
			result.append(str.substring(length-end, length));
		} else {
			result.append(str.substring(0, length));
		}
		
       return result.toString();
	}

	/**
	 * map集合转成hashMap集合
	 * @author guoShiJie
	 * @createDate 2018.7.24
	 * */
	public static HashMap<String,Object> mapToHashMap (Map<String,Object> params) {
		HashMap<String,Object> hashMap = new HashMap<String,Object>();
		if (params != null && params.keySet() != null && params.keySet().size() != 0) {
			for (String key : params.keySet()) {
				hashMap.put(key, params.get(key));
			}
		}
		return hashMap;
	}
	
}
