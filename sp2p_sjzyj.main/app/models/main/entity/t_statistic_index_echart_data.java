package models.main.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 后台运营数据-Echarts数据
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2016年1月22日
 */
@Entity
public class t_statistic_index_echart_data extends Model{

	/** 创建时间 */
	public Date time;
	
	/** 时间数据所属类型 */
	public int time_type;
	
	/** 新增理财会员 */
	public int new_financial_members_count;
	
	/** 新增注册会员 */
	public int new_register_members_count;
	
	/** 投资金额 */
	public double invest_money;
	
	/** 充值金额 */
	public double recharge_money;
	
	/** 数据类型 */
	public int type;
	
	/** 前台数据访问量 */
	public int datas;
	
}
