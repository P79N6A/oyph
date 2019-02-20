package daos.finance;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.finance.entity.t_rel_info;

/**
 * 
 *
 * @ClassName: RelInfoDao
 *
 * @description 机构关联方信息Dao接口
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
public class RelInfoDao extends BaseDao<t_rel_info> {

	protected RelInfoDao () {
		
	}

	public t_rel_info getById(Long id) {
		String sql = " select * from t_rel_info where id = :id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		return this.findBySQL(sql, condition);
	}
}
