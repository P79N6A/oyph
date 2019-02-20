package models.entity;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import play.db.jpa.Model;

@Entity
public class t_payment_call_back extends Model {
	
	/** 回调时间 */
	public Date time;
	
	/** 托管请求唯一标识 */
	public String request_mark;
	
	/** 回调参数 */
	public String cb_params;
	
	@Transient
	public Map<String, String> cb_params_map;
	
	public Map<String, String> getCb_params_map () {
		if (StringUtils.isBlank(this.cb_params)) {
			
			return new LinkedHashMap<String, String>();
		} 
		
		Map<String, String> CBParams = new Gson().fromJson(this.cb_params,
				new TypeToken<LinkedHashMap<String, String>>(){}.getType());

		return CBParams;
	}
	
	
	
	
	
	
	 
}
