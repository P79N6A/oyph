package models.finance.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_return_status extends Model{

	/**报文表名*/
	public String report_name;
	
	/**报送类型(1 天 ，2 月 ， 3季)*/
	public Integer report_type;
	
	/**返回状态(0成功,1失败)*/
	public Integer result_type;
	
	/**时间*/
	public Date time;
	
	/**备注(失败原因说明)*/
	public String remark;
	
	public enum ReportType{
		DAY (1,"天"),
		MONTH (2,"月"),
		SEASON(3,"季");
		
		public Integer code;
		public String value;
		
		private ReportType (Integer code, String value) {
			this.code = code;
			this.value = value;
		}
		
		public static ReportType getEnum (Integer code) {
			ReportType [] type = ReportType.values();
			if (type != null && type.length > 0) {
				for (ReportType reportType : type) {
					if (reportType.code == code) {
						return reportType;
					}
				}
			}
			return null;
		}
	}
}
