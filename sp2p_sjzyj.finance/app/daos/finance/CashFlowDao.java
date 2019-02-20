package daos.finance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.finance.entity.t_cash_flow;
/**
 * 
 *
 * @ClassName: CashFlowDao
 *
 * @description 财务-财务信息-现金流量表Dao
 *
 * @author LiuHangjing
 * @createDate 2018年10月6日-下午6:09:35
 */
public class CashFlowDao extends BaseDao<t_cash_flow>{

	public CashFlowDao() {
		
	}
	/**
	 * 
	 * @Title: pageOfCashFlow
	 * 
	 * @description 现金流量列表分页显示
	 * @param  currPage 当前页
	 * @param  pageSize 每页条数
	 * @param @return
	 * @return PageBean<t_cash_flow>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月7日-上午9:43:43
	 */
	public PageBean<t_cash_flow> pageOfCashFlow(int currPage, int pageSize) {
		StringBuffer sql = new StringBuffer("SELECT * FROM t_cash_flow ORDER BY create_time");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(id) FROM t_cash_flow");
		
		Map<String,Object> condition  = new HashMap<String, Object>();
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(), sql.toString(), condition);
	}
	/**
	 * 
	 * @Title: onIssue
	 * 
	 * @description 通过时间查询上季的数据,并返回到页面上显示
	 * @return t_cash_flow    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月8日-上午10:13:25
	 */
	public t_cash_flow onIssue(String createTime){
		
		String querySql = "SELECT * FROM t_cash_flow WHERE QUARTER(create_time) = QUARTER(DATE_SUB( :createTime,interval 1 QUARTER))";
		
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("createTime", createTime);
		return findBySQL(querySql, condition);
		
	}
	/**
	 * 
	 * @Title: reportCashFlow
	 * 
	 * @description 获取到上季数据
	 * @return t_cash_flow    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月11日-下午2:23:10
	 */
	public t_cash_flow reportCashFlow(){
		String querySql = "SELECT * FROM t_cash_flow WHERE QUARTER(create_time) = QUARTER(DATE_SUB(now(),interval 1 QUARTER))";
		
		Map<String,Object> condition = new HashMap<String,Object>();
		return findBySQL(querySql,condition);
	}
	
	/**
	 * 
	 * @Title: reportCashFlowLast
	 * 
	 * @description 获取到上上季数据
	 * @return t_cash_flow    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月11日-下午2:23:37
	 */
	public t_cash_flow reportCashFlowLast(){
		String querySql = "SELECT * FROM t_cash_flow WHERE QUARTER(create_time) = QUARTER(DATE_SUB(now(),interval 2 QUARTER))";
		
		Map<String,Object> condition = new HashMap<String,Object>();
		return findBySQL(querySql,condition);
	}
}
