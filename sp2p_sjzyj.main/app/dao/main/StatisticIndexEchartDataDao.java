package dao.main;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.constants.Constants;
import models.common.entity.t_user_info;
import models.main.entity.t_statistic_index_echart_data;
import daos.base.BaseDao;

public class StatisticIndexEchartDataDao extends BaseDao<t_statistic_index_echart_data>{

	public StatisticIndexEchartDataDao() {
	}
	
	/** 
	 * 查询新增理财会员
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	public List<Object> findFinanciaCount(int type) {
		String sql = "SELECT new_financial_members_count FROM t_statistic_index_echart_data WHERE type =:type ORDER BY id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		
		return findListSingleBySQL(sql, condition);
	}

	/**
	 * 查询新增注册会员
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	public List<Object> findRegisterCount(int type) {
		String sql = "SELECT new_register_members_count FROM t_statistic_index_echart_data WHERE type =:type ORDER BY id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		
		return findListSingleBySQL(sql, condition);
	}

	/**
	 * 查询投资金额
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	public List findInvestMoney(int type) {
		String sql = "SELECT invest_money FROM t_statistic_index_echart_data WHERE type =:type ORDER BY id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		
		return findListSingleBySQL(sql, condition);
	}

	/**
	 * 查询充值金额
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	public List findRechargeMoney(int type) {
		String sql = "SELECT recharge_money FROM t_statistic_index_echart_data WHERE type =:type ORDER BY id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		
		return findListSingleBySQL(sql, condition);
	}
	
	/**
	 * 查询前台访问量
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月22日
	 *
	 */
	public List findDatas(int type) {
		String sql = "SELECT datas FROM t_statistic_index_echart_data WHERE type =:type ORDER BY id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		
		return findListSingleBySQL(sql, condition);
	}
	
	/**
	 * 修改数据库中列的值
	 * @param type
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年7月27日
	 *
	 */
	public int updateDatas(int time_type) {
		String sql = "UPDATE t_statistic_index_echart_data SET datas = datas+1 WHERE time_type =:time_type";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("time_type", time_type);
		return updateBySQL(sql, condition);
	}
	
	/**
	 * 获取前台访问量数据
	 * 
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年11月30日
	 *
	 */
	public int findTotalVisitByDate(String startTime, String endTime, int type) {
		String sql = null;
		String hour = null;
		
		Map<String, Object> condition = new HashMap<String, Object>();
		if (type == Constants.YESTERDAY) {
			sql = "SELECT IFNULL(SUM(datas),0) FROM t_statistic_index_echart_data tui WHERE TO_DAYS(:nowTime) - TO_DAYS(tui.time) = 1 AND HOUR(tui.time) <:hour";
			if (endTime.contains(":")) {
				hour = endTime.substring(0, endTime.indexOf(":"));
				if("00".equals(hour)){
					hour = "24";
				}
			}
			
			condition.put("nowTime", new Date());
			condition.put("hour", hour);
		}else{ 
			sql="SELECT IFNULL(SUM(datas),0) FROM t_statistic_index_echart_data tui WHERE tui.time BETWEEN :startTime AND :endTime ";
			condition.put("startTime", startTime);
			condition.put("endTime", endTime);
		}
		
		return findSingleIntBySQL(sql, 0, condition);
	}

	
}
