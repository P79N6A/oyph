package models.core.entity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 标的审核科目（用户上传）
 *
 * @author yaoyi
 * @createDate 2015年12月22日
 */
@Entity
public class t_bid_item_user extends Model{
	
	/** 创建时间 */
	public Date time = new Date();
	
	/** 借款人ID */
	public long user_id;
	
	/** 标的ID */
	public long bid_id;
	
	/** 标的审核科目ID */
	public long bid_audit_subject_id;
	
	/** 图片名称 */
	public String name;
	
	/** 图片路径 */
	public String url;
	
	/** 资料审核人 */
	public Long audit_supervisor_id;
	
	/** 审核时间 */
	public Date audit_time;
	
	/** 审核结果:
	 * -1：未通过
	 * 0：未提交
	 * 1：待审核
	 * 2：已通过 */
	private int status;
	
	/**
	 * 上传方式：
	 * 本地上传：0
	 * 科目库上传：1
	 */
	public int upload_type;

	public t_bid_item_user() {}
	
	public void setStatus(BidItemAuditStatus sta){
		this.status = sta.code;
	}
	public BidItemAuditStatus getStatus(){
		return BidItemAuditStatus.getEnum(this.status);
	}
	
	public enum BidItemAuditStatus{
		
		/** 未通过 */
		NO_PASS(-1, "未通过"),
		
		/** 未提交 */
		NO_SUBMIT(0, "未提交"),
		
		/** 待审核 */
		WAIT_AUDIT(1, "待审核"),
		
		/** 已通过 */
		PASS(2, "已通过");
		
		public int code;
		public String value;
		
		private BidItemAuditStatus(int code, String value){
			this.code = code;
			this.value = value;
		}
		
		public static final List<Integer> CAN_DELETE_BY_USER = Arrays.asList(NO_PASS.code, NO_SUBMIT.code);
		
		public static BidItemAuditStatus getEnum(int code){
			BidItemAuditStatus[] status = BidItemAuditStatus.values();
			for(BidItemAuditStatus income:status){
				if(income.code == code){
					
					return income;
				}
			}
			
			return null;
		}
	}
}
