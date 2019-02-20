package daos.common;

import models.common.entity.t_supervisor;

import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;

/**
 * 后台管理员dao的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月15日
 */
public class SupervisorDao extends BaseDao<t_supervisor> {

	protected SupervisorDao() {}
	
	public PageBean<t_supervisor> querySupervisor(int currPage, int pageSize, int status, String userName){
		
		StringBuffer querySql=new StringBuffer("select * from t_supervisor e where 1=1 ");
		StringBuffer countSql=new StringBuffer("select count(e.id) from t_supervisor e where 1=1 ");
		
		Map<String, Object> conditionArgs=new HashMap<String, Object>();
		
		if(status == 2){
			querySql.append(" and e.lock_status=:status");
			countSql.append(" and e.lock_status=:status");
			conditionArgs.put("status", status);
		} else {
			querySql.append(" and e.lock_status=:status");
			countSql.append(" and e.lock_status=:status");
			conditionArgs.put("status", 0);
		}
		
		if(userName != null) {
			querySql.append(" and e.reality_name like :userName");
			countSql.append(" and e.reality_name like :userName");
			conditionArgs.put("userName", "%"+userName+"%");
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSql.toString(), querySql.toString(), t_supervisor.class, conditionArgs);
	}
}
