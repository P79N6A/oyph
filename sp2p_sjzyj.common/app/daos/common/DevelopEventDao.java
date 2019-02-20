package daos.common;

import java.util.List;

import javax.persistence.Query;

import play.db.jpa.JPA;
import common.utils.PageBean;
import models.common.entity.t_event;
import daos.base.BaseDao;

public class DevelopEventDao extends BaseDao<t_event> {

	public PageBean<t_event> pageOfDevelopEvent(int currPage,int pageSize){
		String sql = "SELECT * FROM t_event ORDER BY create_time DESC";
		String sqlCount = "SELECT COUNT(id) FROM t_event";

		return this.pageOfBySQL(currPage, pageSize, sqlCount, sql, null);
	};
	public PageBean<t_event> getDevelopEvent(Integer year){
		String sql="select * from t_event e where e.time_year=? order by e.time_month desc, e.time_day desc";
		Query qry=JPA.em().createNativeQuery(sql, t_event.class);
		qry.setParameter(1, year);
		List<t_event> events=qry.getResultList();
		PageBean<t_event> page=new PageBean<t_event>();
		page.page=events;
		return page;
	}
	public List<Integer> getAllYear(){
		String sql="select distinct e.time_year from t_event e order by e.time_year desc";
		Query qry=JPA.em().createNativeQuery(sql);
		List<Integer> years=qry.getResultList();
		return years;
		
	}
}
