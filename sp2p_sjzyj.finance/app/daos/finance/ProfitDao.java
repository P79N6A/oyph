package daos.finance;

import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.finance.entity.t_profit;
/**
 * 
 *
 * @ClassName: ProfitDao
 *
 * @description 财务-财务信息-利润表dao
 *
 * @author LiuHangjing
 * @createDate 2018年10月6日-下午6:10:00
 */
public class ProfitDao extends BaseDao<t_profit>{

	public ProfitDao() {
	
	}
	/**
	 * 
	 * @Title: pageOfProfit
	 * 
	 * @description 利润列表分页显示
	 * @param  currPage 当前页
	 * @param  pageSize 每页条数
	 * @return PageBean<t_profit>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月7日-上午9:25:03
	 */
	public PageBean<t_profit> pageOfProfit(int currPage, int pageSize) {
		StringBuffer sql = new StringBuffer("SELECT * FROM t_profit ORDER BY create_time");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(id) FROM t_profit");
		
		Map<String,Object> condition = new HashMap<String,Object>();
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), condition);
	}
	/**
	 * 
	 * @Title: reportInfoProfit
	 * 
	 * @description 查询上月数据返回实体,本月发送
	 * @return t_profit    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月10日-下午2:06:16
	 */
	public t_profit reportInfoProfit() {
		
		String sql = "SELECT * FROM t_profit WHERE PERIOD_DIFF(date_format(now(), '%Y%m'),DATE_FORMAT(create_time,'%Y%m'))=1";
		
		Map<String,Object> condition = new HashMap<String,Object>();
		
		return findBySQL(sql, condition);
	}
}
