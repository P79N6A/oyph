package models.core.bean;

import java.io.Serializable;
import java.util.List;

import models.core.entity.t_audit_subject;
import models.core.entity.t_audit_subject_bid;
import models.core.entity.t_bid_item_supervisor;
import models.core.entity.t_bid_item_user;

public class BidItemOfUserSubject implements Serializable{
	
	/** 关联标的id */
	public long bid_id;
	
	/** 对应的审核科目 */
	public t_audit_subject_bid bid_audit_subject;
	
	
	/** 用户上传的审核科目列 */
	public List<t_bid_item_user> bid_item_user;
	
}
