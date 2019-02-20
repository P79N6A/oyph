package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.bean.CostBean;
import models.common.entity.t_cost;

/**
 * 费用账户Dao接口
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年03月14日
 */
public class CostDao extends BaseDao<t_cost> {

	protected CostDao() {
		
	}
	
	/**
	 * 费用账户 列表查询
	 * 
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年03月14日
	 */
	public PageBean<t_cost> pageOfCosts(int currPage, int pageSize) {
		StringBuffer sql = new StringBuffer("SELECT * FROM t_cost WHERE 1=1 ");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(1) FROM t_cost WHERE 1=1 ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		sql.append(" ORDER BY time DESC");
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), condition);
	}
	
	/**
	 * 费用账户服务费列表查询
	 * 
	 * @param beginTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年08月14日
	 */
	public List<CostBean> queryCosts(String beginTime, String endTime) {
		String sql = " SELECT t.id as id,'服务费' as incomeAccount,t.remark as remark,t.time as time, t.amount as money FROM t_cost t WHERE t.time <= :endTime AND t.time >= :beginTime and t.amount !=0 and t.sort = 3 ORDER BY t.time ";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("beginTime", beginTime);
		condition.put("endTime", endTime);
		
		return  this.findListBeanBySQL(sql, CostBean.class, condition);
	}
	
	/**
	 * 费用账户提现手续费总和
	 * 
	 * @param beginTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 * 
	 * @author liuyang
	 * @createDate 2018年08月14日
	 */
	public double countWithdraw(String beginTime, String endTime) {
		String sql = " SELECT sum(t.amount) FROM t_cost t WHERE t.time <= :endTime AND t.time >= :beginTime and t.sort = 2 ORDER BY t.time ";

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("beginTime", beginTime);
		condition.put("endTime", endTime);
		
		return this.findSingleDoubleBySQL(sql, 0.00, condition);
	}
}
