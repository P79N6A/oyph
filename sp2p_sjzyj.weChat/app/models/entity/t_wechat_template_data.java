package models.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 
 *
 * @ClassName: t_wechat_template_data
 *
 * @description 
 *
 * @author HanMeng
 * @createDate 2018年12月25日-上午10:49:36
 */
@Entity
public class t_wechat_template_data extends Model{
	
	    private String value;
	    private String color;
	    public String getValue() {
	        return value;
	    }
	 
	    public void setValue(String value) {
	        this.value = value;
	    }
	 
	    public String getColor() {
	        return color;
	    }
	 
	    public void setColor(String color) {
	        this.color = color;
	    }
	
}
