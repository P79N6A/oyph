package common.utils;

import java.util.Map;

/**
 * Map集合工具类
 * @author guoShiJie
 * @createDate 2018.10.23
 * */
public class MapUtils {

	/**
	 * map集合不为空
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public static Boolean isNotNull (Map<String,Object> map) {
		Boolean flag = false;
		if (map != null && map.size() > 0) {
			flag = true;
		}
		return flag;
	}
	
	/**
	 * map集合为空
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public static Boolean isNull (Map<String,Object> map) {
		Boolean flag = false;
		if (map == null) {
			flag = true;
		}
		return flag;
	}
}
