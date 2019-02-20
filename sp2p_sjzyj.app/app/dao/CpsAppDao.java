package dao;

import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_user;
import models.ext.cps.bean.CpsSpreadRecord;
import models.ext.cps.entity.t_cps_user;

public class CpsAppDao extends BaseDao<t_cps_user>{

	/**
	 * CPS推广记录
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public PageBean<CpsSpreadRecord> queryCpsRecord(int currPage , int pageSize,long spreader_id) {
		StringBuffer querySql = new StringBuffer(" select cu.id as id ,a.title as activityName,u.name as user_name, cu.total_invest as total_invest, cu.time as time , cu.total_rebate as total_rebate , u.id as user_id, uu.name as spreader_name "
				+ " from t_cps_user cu "
				+ " left join t_user u on cu.user_id = u.id "
				+ " left join t_user uu on cu.spreader_id = uu.id "
				+ " inner join t_cps_activity a on cu.activity_id = a.id "
				+ " where cu.spreader_id = :spreaderId and a.is_use = 1 ");
		StringBuffer countSql = new StringBuffer(" select count(cu.id) "
				+ " from t_cps_user cu "
				+ " left join t_user u on cu.user_id = u.id "
				+ " left join t_user uu on cu.spreader_id = uu.id "
				+ " inner join t_cps_activity a on cu.activity_id = a.id"
				+ " where cu.spreader_id = :spreaderId and a.is_use = 1 ");
		querySql.append(" order by cu.id desc ");
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("spreaderId", spreader_id);
		return this.pageOfBeanBySQL(currPage, pageSize, countSql.toString(), querySql.toString(), CpsSpreadRecord.class, condition);
	}
	
	/**
	 * CPS推广链接
	 * @author guoShiJie
	 * @createDate 2018.6.19
	 * */
	public String queryMobileById(long userId) {
		t_user user = t_user.findById(userId);
		if (user == null) {
			return null;
		}
		return user.mobile;
	}
}
