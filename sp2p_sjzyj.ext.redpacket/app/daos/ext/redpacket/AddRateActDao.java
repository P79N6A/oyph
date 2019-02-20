package daos.ext.redpacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.Page;
import common.utils.PageBean;
import daos.base.BaseDao;
import models.ext.redpacket.entity.t_add_rate_act;

/**
 * 加息券活动 Dao
 * 
 * @author niu
 * @create 2017.10.27
 */
public class AddRateActDao extends BaseDao<t_add_rate_act> {

	protected AddRateActDao() {
		
	}
	
	/**
	 * 降序查询所有 加息券活动
	 * 
	 * @author niu
	 * @create 2017.10.27
	 */
	public PageBean<t_add_rate_act> listOfAct(int currPage, int pageSize) {
		
		String querySQL = "SELECT * FROM t_add_rate_act t ORDER BY t.id DESC";
		String countSQL = "SELECT COUNT(*) FROM t_add_rate_act t ORDER BY t.id DESC";
		
		return pageOfBySQL(currPage, pageSize, countSQL, querySQL, null);	
	}
	
	/**
	 * 降序查询所有 加息券活动(未开始的)
	 * 
	 * @author niu
	 * @create 2017.10.31
	 */
	public List<t_add_rate_act> listOfActStart() {
		
		String sql = "SELECT * FROM t_add_rate_act t WHERE t.status = 2 ORDER BY t.id DESC";
		
		return findListBySQL(sql, null);
	}
	
	/**
	 * 查询 - 加息券活动 （进行中）
	 * 
	 * @author niu
	 * @create 2017.11.02
	 */
	public t_add_rate_act getAct() {
		String sql = "SELECT * FROM t_add_rate_act t WHERE t.status = 2 ORDER BY t.id DESC";
		
		return findBySQL(sql, null);
	}
	
	
	
	
	
}


























