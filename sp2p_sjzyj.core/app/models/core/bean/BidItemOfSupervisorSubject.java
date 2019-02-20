package models.core.bean;

import java.io.Serializable;
import java.util.List;

import models.core.entity.t_audit_subject_bid;
import models.core.entity.t_bid_item_supervisor;

public class BidItemOfSupervisorSubject implements Serializable{
	
	/** 关联标的id */
	public long bid_id;
	
	/** 对应的审核科目id */
	public t_audit_subject_bid bid_audit_subject;
	
	/** 管理员上传的审核科目列 */
	public List<t_bid_item_supervisor> bid_item_supervisor;
}
