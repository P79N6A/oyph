package daos.common;

import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_service_month;
import models.common.entity.t_service_trace;

public class ServiceMonthDao extends BaseDao<t_service_month> {
	
	protected ServiceMonthDao() {}

	
	/**
	 *  分页查询，客服月统计列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param userId 用户id
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年07月13日
	 */
	public PageBean<t_service_month> pageOfServiceMonthList(int currPage, int pageSize, int year, int month) {
		StringBuffer querySQL = new StringBuffer("SELECT * from t_service_month s where s.month=:month and s.year=:year");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(s.id) FROM t_service_month s where s.month=:month and s.year=:year");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("month", month);
		condition.put("year", year);
		
		querySQL.append(" order by s.time desc ");
		
		return this.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), t_service_month.class, condition);
	}
	
	/**
	 *  根据年月查询实体类
	 *
	 * @param year 年
	 * @param month 月
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年07月15日
	 */
	public t_service_month queryByTime(int year, int month) {
		
		String sql = " select * from t_service_month s where s.year =:year and s.month =:month ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("month", month);
		condition.put("year", year);
		
		return this.findBySQL(sql, condition);
	}
}
