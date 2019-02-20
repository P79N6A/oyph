package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

@Entity
public class t_event extends Model{

	
	/**发展历程内容 	*/
	public String event_content;
	
	/**历程时间的年 */
	public Integer time_year;
	
	/**历程时间的月 */
	public Integer time_month;
	
	/**历程时间的日*/
	public Integer time_day;
	
	/**发布时间	 */
	public Date create_time;
	
	/**发布人 */
	public String create_supervisor;
	
	@Transient
	public String time_date;
	
	public String image_url;
	
	public String image_format;

}
