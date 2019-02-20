package daos.finance;

import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.base.BaseDao;
import groovy.time.BaseDuration;
import models.finance.entity.t_current_assets;
/**
 * 
 *
 * @ClassName: CurrentAssetsDao
 *
 * @description 后台-财务- 财务信息-资产负债表 Dao
 *
 * @author LiuHangjing
 * @createDate 2018年10月6日-下午5:36:13
 */
public class CurrentAssetsDao extends BaseDao<t_current_assets>{
	
	protected CurrentAssetsDao() {
		
	}
	/**
	 * 
	 * @Title: pageOfCurrentAssets
	 * 
	 * @description 资产负债表列表分页显示
	 * @param currPage 当前页
	 * @param pageSize 每页条数 
	 *
	 * @return PageBean<T>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月6日-下午5:28:53
	 */
	
	public PageBean<t_current_assets> pageOfCurrentAssets(int currPage, int pageSize){
		StringBuffer sql = new StringBuffer("SELECT * FROM t_current_assets ORDER BY create_time");
		StringBuffer sqlCount = new StringBuffer("SELECT COUNT(id) FROM t_current_assets");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		return this.pageOfBySQL(currPage, pageSize, sqlCount.toString(),sql.toString(), condition);
	}
	/**
	 * 
	 * @Title: reportInfo
	 * 
	 * @description 通过时间查询信息报送，当月报送的是上个月的
	 * @return t_current_assets    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年10月11日-上午11:44:36
	 */
	public t_current_assets reportInfo(){
		String sql = "SELECT * FROM t_current_assets WHERE PERIOD_DIFF(date_format(now(), '%Y%m'),DATE_FORMAT(create_time,'%Y%m'))=1";
		
		Map<String,Object> condition = new HashMap<String,Object>();
		
		return findBySQL(sql, condition);
	}
}
