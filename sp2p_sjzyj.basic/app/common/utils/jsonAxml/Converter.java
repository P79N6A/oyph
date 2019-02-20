package common.utils.jsonAxml;

import org.apache.commons.lang.StringUtils;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class Converter {
	
	/**
	 * xml字符串转json字符串
	 * @param xml
	 * @return
	 */
	public static String xmlToJson(String xml){  
        return new XMLSerializer().read(xml).toString();  
    }
	
	/**
	 * json字符串转xml字符串
	 * @param json
	 * @return
	 */
    public static String jsonToXml(String json){  
        XMLSerializer xmlSerializer = new XMLSerializer();  
        xmlSerializer.setTypeHintsEnabled(false);      
        
        return xmlSerializer.write(JSONSerializer.toJSON(json));  
    }
    
	/**
	 * json字符串转xml字符串
	 * @param json
	 * @param rootName
	 * @param elementName
	 * @param objectName
	 * @param arrayName
	 * @return
	 */
    public static String jsonToXml(String json, String rootName, String elementName, String objectName, String arrayName){  
        XMLSerializer xmlSerializer = new XMLSerializer();  
        xmlSerializer.setTypeHintsEnabled(false);
        
        if (StringUtils.isNotBlank(rootName)) {
        	xmlSerializer.setRootName(rootName);
		}
        
        if (StringUtils.isNotBlank(elementName)) {
        	xmlSerializer.setElementName(elementName);
		}
        
        if (StringUtils.isNotBlank(objectName)) {
        	xmlSerializer.setObjectName(objectName);
        }
        
        if (StringUtils.isNotBlank(arrayName)) {
        	xmlSerializer.setArrayName(arrayName);
        }
        
        return xmlSerializer.write(JSONSerializer.toJSON(json));  
    }  
    
	/**
	 * xml字符串转json对象/数组
	 * @param xml
	 * @return
	 */
	public static JSON xmlToObj(String xml) {
		return new XMLSerializer().read(xml);  
	}
}
