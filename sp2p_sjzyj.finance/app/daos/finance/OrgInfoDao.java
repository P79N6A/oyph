package daos.finance;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.finance.entity.t_org_info;

/**
 * 
 *
 * @ClassName: OrgInfoDao
 *
 * @description 机构基本信息Dao接口
 *
 * @author liuyang
 * @createDate 2018年10月5日
 */
public class OrgInfoDao extends BaseDao<t_org_info> {

	protected OrgInfoDao () {
		
	}

	public t_org_info getById(Long id) {
		String sql = " select * from t_org_info where id = :id";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("id", id);
		return this.findBySQL(sql, condition);
	}
}
