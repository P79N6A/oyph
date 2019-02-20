package common.utils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.io.binary.Token.Value;

import net.sf.json.JSONObject;

/**
 * 
 * 通用 工具类  
 * 
 * 
 * @author niu
 * @create 2017.04.21
 * 
 */

public class BrokerUtils {

	/**
	 * 
	 * 参数  错误  处理  
	 * 
	 * 
	 * @author niu
	 * @create 2017.04.21
	 * 
	 */
	public static String paramErrorHandle() {
		
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		result.put("code", -1);
		result.put("msg" , "app 请求：参数不正确");
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 
	 * 集合  列表  处理  
	 * 
	 * 
	 * @author niu
	 * @create 2017.04.25
	 * 
	 */
	public static <B> List<B> listHandle(List<B> list) { return (list != null && list.size() > 0) ? list : null; }
	
	/**
	 * 
	 * 请求  结果  处理  
	 * 
	 * 
	 * @author niu
	 * @create 2017.05.05
	 * 
	 */
	public static String actionResultHandle(Object obj) {
		
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		if (obj == null) {
			result.put("code", -1);
			result.put("msg" , "失败");
		}
		
		if (obj != null) {
			result.put("code", 1);
			result.put("msg" , "成功");
		}
		
		return  JSONObject.fromObject(result).toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * map 转化  Bean  
	 * 
	 * 
	 * @author niu
	 * @create 2017.04.21
	 * 
	 */
	public static Object mapToEntity(Map<String, String> map, Class<?> clazz) {
		Object obj = null;
		try {
			obj = clazz.newInstance();
			
			if (map != null && map.size() > 0) {
				for (Map.Entry<String, String> entry : map.entrySet()) {
					String propertyName  = entry.getKey();
					String propertyValue = entry.getValue();
					
					String setMethodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
					Field field = getClassField(clazz, propertyName);
					if (field == null) {
						continue;
					}
					
					Class<?> fieldTypeClass = field.getType();
					Object value = convertValType(propertyValue, fieldTypeClass);
					  
		            clazz.getMethod(setMethodName, field.getType()).invoke(obj, value);   
		                
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return obj;
	}
	
	public static Field getClassField(Class<?> clazz, String fieldName) {
		if (Object.class.getName().equals(clazz.getName())) {
			return null;
		}
		
		Field[] declaredFields = clazz.getDeclaredFields();
		for (Field field : declaredFields) {
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}
		
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			return getClassField(superClass, fieldName);
		}
		
		return null;
	}
	
	private static Object convertValType(String value, Class<?> fieldTypeClass) throws Exception {    
        Object retVal = null;    
        if(Long.class.getName().equals(fieldTypeClass.getName()) || long.class.getName().equals(fieldTypeClass.getName())) {    
            retVal = Long.parseLong(value.toString());    
        } else if(Integer.class.getName().equals(fieldTypeClass.getName()) || int.class.getName().equals(fieldTypeClass.getName())) {    
            retVal = Integer.parseInt(value.toString());    
        } else if(Float.class.getName().equals(fieldTypeClass.getName()) || float.class.getName().equals(fieldTypeClass.getName())) {    
            retVal = Float.parseFloat(value.toString());    
        } else if(Double.class.getName().equals(fieldTypeClass.getName()) || double.class.getName().equals(fieldTypeClass.getName())) {    
            retVal = Double.parseDouble(value.toString());    
        } else if(Date.class.getName().equals(fieldTypeClass.getName())) {    
            retVal = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss").parse(value);
        } else if(Boolean.class.getName().equals(fieldTypeClass.getName())|| boolean.class.getName().equals(fieldTypeClass.getName())){
        	retVal = value.equals("1") ? true : false;
        }else {    
            retVal = value;    
        }    
        return retVal;    
    }    
	
	
	
}


























