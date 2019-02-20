package daos.activity.shake;

import java.util.List;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.activity.shake.entity.t_shake_activity;

/**
 * 摇一摇活动DAO
 * 
 * @author niu
 * @create 2017-12-08
 */
public class ShakeActivityDao extends BaseDao<t_shake_activity> {
	
	
	/**
	 * 保存摇一摇活动
	 * 
	 * @param name  活动名称
	 * @param ctime 活动时间
	 * @return 添加成功返回true,添加失败返回flase.
	 * 
	 * @author niu
	 * @create 2017-12-08
	 */
	public boolean saveShakeActivity(String name, int ctime) {
		
		t_shake_activity activity = new t_shake_activity();
		
		activity.name = name;
		activity.ctime = ctime;
		activity.status = 1;
		
		return activity.save() == null ? false : true;
	}
	
	/**
	 * 分页查询摇一摇活动
	 * 
	 * @param currPage 当前页码
	 * @param pageSize 显示条数
	 * @return 
	 * 
	 * @author niu
	 * @create 2017-12-08
	 */
	public PageBean<t_shake_activity> listOfShakeActivity(int currPage, int pageSize) {
		
		String querySQL = "SELECT * FROM t_shake_activity t ORDER BY t.id DESC";
		String countSQL = "SELECT COUNT(t.id) FROM t_shake_activity t ORDER BY t.id DESC";
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, t_shake_activity.class, null);
	}
	
	/**
	 * 查询已开始的活动
	 * 
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-12
	 */
	public t_shake_activity getOngoingActivity() {
		String sql = "SELECT * FROM t_shake_activity t WHERE t.status = 3";
		
		return findBySQL(sql, null);
	}
	

	
	
	
	
	
	
	
}
