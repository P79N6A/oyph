package daos.finance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.finance.entity.t_internal_control_chart;

/**
 * 
 *
 * @ClassName: InternalControlChartDao
 *
 * @description 机构内控情况Dao接口
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
public class InternalControlChartDao extends BaseDao<t_internal_control_chart> {

	protected InternalControlChartDao () {
		
	}
	/**
	 * 
	 * @Title: reportInternal
	 * 
	 * @description p2p   P13机构内控表
	 * @return List<t_internal_control_chart>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月15日-下午4:55:44
	 */
	public List<t_internal_control_chart> reportInternal(){
		String sql = "SELECT * FROM t_internal_control_chart";
		
		Map<String,Object> condition = new HashMap<String,Object>();
		
		return findListBySQL(sql,condition);
	}
}
