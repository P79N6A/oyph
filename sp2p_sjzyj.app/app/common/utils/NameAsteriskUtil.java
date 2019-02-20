package common.utils;

import com.shove.Convert;

public class NameAsteriskUtil {

	/***
	 * 
	 * 格式化用户名
	 * @param str
	 * @param start
	 * @param count
	 * @param end
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-14
	 */
	public static String asterisk(String str, int start, int count, int end) {

		int length = str.length();
		if (length == 0) {

			return str;
		}

		StringBuffer result = new StringBuffer();

		if (start > 0) {
			if (start <= length) {
				result.append(str.substring(0, start));
			} else {
				result.append(str.substring(0, length));
			}
		}

		for (int i = 0; i < count; i++) {
			result.append("*");
		}

		if (end > 0) {
			if (end <= length) {
				result.append(str.substring(length - end, length));
			} else {
				result.append(str.substring(0, length));
			}
		}
		
		
		return result.toString();
	}
}
