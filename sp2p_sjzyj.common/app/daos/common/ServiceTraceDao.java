package daos.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_service_trace;

public class ServiceTraceDao extends BaseDao<t_service_trace> {

	protected ServiceTraceDao() {}
	
	/**
	 *  分页查询，会员列表  
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param userId 用户id
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年07月12日
	 */
	public PageBean<t_service_trace> pageOfUserTraceList(long userId, int currPage, int pageSize) {
		StringBuffer querySQL = new StringBuffer("SELECT * from t_service_trace s where s.user_id=:userId");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(s.id) FROM t_service_trace s where s.user_id=:userId");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		querySQL.append(" order by s.time desc ");
		
		return this.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), t_service_trace.class, condition);
	}
}
