package daos.activity.shake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.activity.shake.entity.t_big_wheel;
import models.app.bean.ActivityListBean;

public class BigWheelDao extends BaseDao<t_big_wheel>{
	/**
	 * 
	 * @Title: findBigWheel
	 * @description: 查询进行中的大转盘活动 
	 *				1、未开始 2、进行中 3、已结束
	 * @return    
	 * @return List<t_big_wheel>   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月12日-上午10:00:21
	 */
	public List<t_big_wheel> findBigWheel(){
		String sql = "select * from t_big_wheel ";
		return findListBySQL(sql, null);
	}
	/**
	 * 
	 * @Title: findListOfActivityBean
	 * 
	 * @description pc端显示活动
	 * @param @return
	 * @return List<ActivityListBean>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月13日-上午9:56:21
	 */
	public List<ActivityListBean> findListOfActivityBean(){
		String sql = "(select b.id,b.name as name,b.start_time as start_time,b.status as status,b.type as type,b.maxNum AS maxNum from t_big_wheel b) union (SELECT s.id,s.name as name,s.stime as start_time,s.status as status,s.type as type,s.winrate AS maxNum from t_shake_activity s)";
		return findListBeanBySQL(sql, ActivityListBean.class, null);
	}
	/**
	 * 
	 * @Title: findListOfActivityAppBean
	 * 
	 * @description app端显示活动列表
	 * @param @return
	 * @return List<ActivityListBean>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月13日-上午9:56:41
	 */
	
	public List<ActivityListBean> findListOfActivityAppBean(){
		String sql = "(select b.id,b.name as name,b.start_time as start_time,b.status as status,b.type as type,b.maxNum AS maxNum from t_big_wheel b where status=3) union (SELECT s.id,s.name as name,s.stime as start_time,s.status as status,s.type as type,s.winrate AS maxNum from t_shake_activity s where status=3)";
		return findListBeanBySQL(sql, ActivityListBean.class, null);
	}
	
	/**
	 * 
	 * @Title: pageOfByBigWheel
	 * @description: 大转盘活动分页设置列表显示
	 *
	 * @param currPage
	 * @param pageSize
	 * @return    
	 * @return PageBean<t_big_wheel>   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月12日-下午5:21:24
	 */
	public PageBean<t_big_wheel> pageOfByBigWheel(int currPage, int pageSize) {
		String querySql = "select * from t_big_wheel order by start_time desc";
		String countSql = "select count(*) from t_big_wheel order by start_time desc";
		
		return pageOfBeanBySQL(currPage, pageSize, countSql, querySql,t_big_wheel.class , null);
	}
	/**
	 * 
	 * @Title: findById
	 * @description: 通过id查询该条
	 *
	 * @param id
	 * @return    
	 * @return t_big_wheel   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月13日-上午9:48:15
	 */
	public t_big_wheel findById(long id) {
		String sql = "select * from t_big_wheel where id = :id";
		Map<String, Object> condition = new HashMap<String,Object>();
		condition.put("id", id);
		return findBySQL(sql, condition);
	}
}
