package daos.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.common.entity.t_guarantee;

/**
 * 担保人dao的具体实现
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年1月5日
 */
public class GuaranteeDao extends BaseDao<t_guarantee> {

	protected GuaranteeDao() {}
	
	/**
	 * 后台代偿，获取关联担保人
	 *
	 * @param key
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年1月8日
	 */
	public PageBean<t_guarantee> queryGuaranteesByKey(int currPage, int pageSize, String key) {
		
		String querySQL = "SELECT * FROM t_guarantee tu ";
		String countSQL = "SELECT COUNT(tu.id) FROM t_guarantee tu";
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(key)){
			querySQL += " WHERE tu.name LIKE :name ";
			countSQL += " where tu.name LIKE :name ";
			conditionArgs.put("name", "%"+key+"%");
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL, querySQL, t_guarantee.class, conditionArgs);
	}
	
}
