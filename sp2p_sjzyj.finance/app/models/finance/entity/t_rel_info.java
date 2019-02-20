package models.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 
 *
 * @ClassName: t_rel_info
 *
 * @description 机构关联方信息表
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
@Entity
public class t_rel_info extends Model {

	/** 数据日期 */
	public Date data_date;
	/** 机构代码 */
	public String org_num;

	/** 关联方代码 */
	public String relation_id;

	/** 关联方类型 */
	public String relation_type;

	/** 关联方名称 */
	public String relation_name;

	/** 关联方证件类型 */
	public String relation_id_type;

	/** 关联方证件号码 */
	public String relation_id_no;

	/** 持股比例 */
	public BigDecimal shr_ratio;

	/** 关联方联系电话 */
	public String cust_telephone_no;

	/** 关联方通讯地址 */
	public String cust_adde_desc;

	/** 添加时间 */
	public Date time;

}
