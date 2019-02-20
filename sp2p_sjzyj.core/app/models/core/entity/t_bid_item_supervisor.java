package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.utils.NoUtil;
import play.db.jpa.Model;

@Entity
public class t_bid_item_supervisor extends Model{
	
	/** 上传时间 */
	public Date time = new Date();
	
	/** 管理员ID */
	public long supervisor_id;
	
	/** 借款标ID */
	public long bid_id;
	
	/** 标的审核科目ID */
	public long bid_audit_subject_id;
	
	/** 图片名称 */
	public String name;
	
	/** 图片路径 */
	public String url;
	
	/** 图片格式 */
	public String image_format;

	public String getImage_format() {
		return this.name.substring(this.name.lastIndexOf(".")+1);
	}
	
	public t_bid_item_supervisor() {}

	public t_bid_item_supervisor(long supervisor_id, long bid_id,
			long bid_audit_subject_id, String name, String url, String image_format) {
		super();
		this.supervisor_id = supervisor_id;
		this.bid_id = bid_id;
		this.bid_audit_subject_id = bid_audit_subject_id;
		this.name = name;
		this.url = url;
		this.image_format = image_format;
	}
	
}
