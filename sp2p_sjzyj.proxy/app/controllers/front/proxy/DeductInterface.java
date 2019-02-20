package controllers.front.proxy;

import java.io.UnsupportedEncodingException;



import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import com.shove.security.Encrypt;

import common.utils.Factory;
import controllers.common.BaseController;

import models.proxy.bean.SalesManBean;
import models.proxy.entity.t_proxy_profit;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import play.db.jpa.JPA;
import services.proxy.ProfitService;
import services.proxy.SalesManService;

public class DeductInterface extends BaseController{
	
	protected static ProfitService profitService = Factory.getService(ProfitService.class);
	
	protected static SalesManService salesManService = Factory.getService(SalesManService.class);
	

	
	
	public static String scanSales(){
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		String sql="select id,sale_name,sale_mobile from t_proxy_salesman";
		
		
		List<SalesManBean> salesMan= JPA.em().createNativeQuery(sql, SalesManBean.class).getResultList();
		
		map.put("salesMan", salesMan);
		
		String jobStr = Encrypt.encrypt3DES(JSONObject.fromObject(map).toString(), "fbetb4rJnLUUPVSx");
		
		return jobStr;
	}
	/**
	 * @throws Exception 
	 * 
	 * @Title: deductAllInfo   
	 * @Description: TODO(查看所有业务员的提成) 
	 * @param @return    
	 * @return: String 
	 * @author lihuijun 
	 * @date 2018年9月4日 下午5:15:06    
	 * @throws
	 */
	public static String deductAllInfo(String timeStr, long salesId) throws Exception{
		JSONObject bjson =new JSONObject();
		if (params == null ) {
        	bjson.put("message", "查询失败,请传params参数");
			bjson.put("success", false);
			bjson.put("result",null);
			return bjson.toString();
        }
		Map<String,Object> map =new HashMap<String,Object>();
		List<t_proxy_profit> profitList=null;
		if(salesId==0){
			 profitList = profitService.findListByColumn("profit_time=?", timeStr);
			
		}else{
			 profitList = profitService.findListByColumn("profit_time=? and sale_id=?", timeStr,salesId);
			
		}
		map.put("profitList", profitList);
		
		return JSONObject.fromObject(map).toString();
	}
	
}
