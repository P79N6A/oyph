package models.finance.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 
 *
 * @ClassName: t_org_info
 *
 * @description 机构基本信息表
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
@Entity
public class t_org_info extends Model {

	/** 机构名称 */
	public String org_name;

	/** 机构代码 */
	public String org_num;

	/** 数据日期 */
	public Date data_date;

	/** 注册地址 */
	public String reg_addr;

	/** 办公地址 */
	public String run_addr;

	/** 区域代码 */
	public String region_cd;

	/** 资金存管银行名称 */
	public String third_bank;

	/** 营业执照到期日 */
	public Date exp_date;

	/** 营业执照是否有效 */
	public String sts_flg;

	/** 添加时间 */
	public Date time;

}
