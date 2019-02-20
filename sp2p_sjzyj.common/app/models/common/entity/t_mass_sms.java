package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 群发短信发送表
 * 
 * @author liudong
 * @createDate 2016年4月1日
 *
 */
@Entity
public class t_mass_sms extends Model {
	
	/** 创建时间 */
	public Date time;
	
	/** 管理员id */
	public Long supervisor_id;
	
	/** 会员类型 */
	private int member_type;
	public t_user_info.MemberType getMember_type() {
		t_user_info.MemberType memberType = t_user_info.MemberType.getEnum(this.member_type);
		return memberType;
	}

	public void setMember_type(t_user_info.MemberType memberType) {
		this.member_type = memberType.code;
	}
	
	/** 发送条数 */
	public int num;
	
	/** 短信内容 */
	public String content;

}
