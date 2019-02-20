package services.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.db.jpa.JPA;
import common.utils.Factory;
import common.utils.PageBean;
import daos.common.DevelopEventDao;
import models.common.entity.t_event;
import services.base.BaseService;

public class DevelopEventService extends BaseService<t_event>{
	
	protected static DevelopEventDao deveDao = Factory.getDao(DevelopEventDao.class);
	
	protected DevelopEventService(){
		super.basedao=deveDao;
	}
	
	public PageBean<t_event> pageOfDevelopEvent(int currPage,int pageSize){
		
		return deveDao.pageOfDevelopEvent(currPage,pageSize);
	};
	public void editEvent(t_event deve){
		String sql="update t_event set image_url=:image_url,event_content=:event_content,create_supervisor=:create_supervisor,create_time=:create_time,time_year=:time_year,time_month=:time_month,time_day=:time_day where id=:id";
		Map<String, Object> args=new HashMap<>();
		args.put("event_content", deve.event_content);
		args.put("create_supervisor", deve.create_supervisor);
		args.put("create_time", deve.create_time);
		args.put("time_year", deve.time_year);
		args.put("time_month", deve.time_month);
		args.put("time_day", deve.time_day);
		args.put("image_url", deve.image_url);
		args.put("id", deve.id);
		deveDao.updateBySQL(sql, args);
	}
	public PageBean<t_event> getDevelopEvent(Integer year){
		return deveDao.getDevelopEvent(year);
	}
	
	public List<Integer> getAllYear(){
		return deveDao.getAllYear();
	}
}
