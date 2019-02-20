package daos.common;

import models.common.entity.t_template_pact;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;

/**
 * 合同模板dao的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年1月18日
 */
public class TemplatePactDao extends BaseDao<t_template_pact> {

	protected TemplatePactDao() {
	}

	public t_template_pact findTempByType(int type) {
		String sql = "select * from t_template_pact where type = :type";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		return findBySQL(sql, condition);
	}
}
