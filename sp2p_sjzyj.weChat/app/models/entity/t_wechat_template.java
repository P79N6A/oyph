package models.entity;

import java.util.Map;

import javax.persistence.Entity;

import play.db.jpa.Model;
/**
 * 
 *
 * @ClassName: t_wechat_template
 *
 * @description 
 *
 * @author HanMeng
 * @createDate 2018年12月25日-上午10:49:55
 */
@Entity
public class t_wechat_template extends Model{
		/** 接收者openid */ 
	    private String touser; 
	    /**  模板ID*/
	    private String template_id;
	    /** 模板跳转链接 */
	    private String url;
	 
	   // private Map<String, t_wechat_template_data> data;
	 
	    public String getTouser() {
	        return touser;
	    }
	 
	    public void setTouser(String touser) {
	        this.touser = touser;
	    }
	 
	    public String getTemplate_id() {
	        return template_id;
	    }
	 
	    public void setTemplate_id(String template_id) {
	        this.template_id = template_id;
	    }
	 
	    public String getUrl() {
	        return url;
	    }
	 
	    public void setUrl(String url) {
	        this.url = url;
	    }
	 
//	    public Map<String, t_wechat_template_data> getData() {
//	        return data;
//	    }
//	 
//	    public void setData(Map<String, t_wechat_template_data> data) {
//	        this.data = data;
//	    }
	
	
}
