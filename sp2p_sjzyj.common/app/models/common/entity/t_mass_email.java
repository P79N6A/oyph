package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 群发邮件
 * 
 * @author liudong
 * @createDate 2016年4月5日
 */
@Entity
public class t_mass_email extends Model {

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
	
	/** 邮件主题 */
	public String title;
	
	/** 邮件内容 */
	public String content;
}
