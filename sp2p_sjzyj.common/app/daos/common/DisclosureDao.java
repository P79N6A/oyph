package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_disclosure;

/**
 * 信息披露dao实现
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2017年12月15日
 */
public class DisclosureDao extends BaseDao<t_disclosure> {

	protected DisclosureDao() {
		
	}
	
	/**
	 * 查询最近插入的一条数据
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月20日
	 *
	 */
	public t_disclosure findByTime() {
		String sql = "SELECT * FROM t_disclosure t order by t.time desc ";

		return findBySQL(sql, null);
	}
	
	/**
	 * 查询所有的披露信息
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月29日
	 *
	 */
	public List<t_disclosure> queryListById() {
		String sql = "SELECT * FROM t_disclosure t order by t.id desc ";
		Map<String, Object> params = new HashMap<String, Object>();
		
		return this.findListBySQL(sql, params);
	}
}
