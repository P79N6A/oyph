package common.utils.jsonAxml;

import java.text.DecimalFormat;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * 属性过滤器   属性格式化
 *
 * @description 
 *
 * @author liudong
 * @createDate 2015年12月11日
 */
public class JsonDoubleValueProcessor implements JsonValueProcessor {

    
    private String format = "##,##0.00";

    
    public JsonDoubleValueProcessor() {
        super();
    }

    
    public JsonDoubleValueProcessor(String format) {
        super();
        this.format = format;
    }

    
    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
        return process(value);
    }

    
    public Object processObjectValue(String key, Object value,
            JsonConfig jsonConfig) {
        return process(value);
    }

    
    private Object process(Object value) {
        try {
            if (value instanceof Double) {
            	DecimalFormat myformat = new DecimalFormat();
        		myformat.applyPattern(format);
        		
                return myformat.format(value);
            }
            
            return value == null ? "" : value.toString();
        } catch (Exception e) {
            return "";
        }

    }

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
