package common.utils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.activation.FileDataSource;

public class EnumUtil {
	
	/**
	 * 解析枚举</br>
	 * 枚举的属性必须为code和value
	 *
	 * @param ems
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年4月6日
	 */
	public static List<Map<String, Object>> parseEnum(Enum[] ems) throws Exception{
		List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();
		
		Class clazz = null;
		Field field = null;
		for (Enum em:ems) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			clazz = em.getDeclaringClass();
			
			field = clazz.getField("code");
			map.put("code", field.get(em));
			field = clazz.getField("value");
			map.put("value", field.get(em));
			
			list.add(map);
		}
		
		return list;
	}
	
}
