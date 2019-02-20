package daos.proxy;

import java.util.HashMap;
import java.util.Map;

import daos.base.BaseDao;
import models.proxy.entity.t_proxy_salesman_profit_rule;

/**
 * 业务员提成规则表
 * @author Niu Dongfeng
 */
public class RuleDao extends BaseDao<t_proxy_salesman_profit_rule> {
	
	protected RuleDao() {}
	
	/**
	 * 修改业务员收益规则
	 * 
	 * @param key	业务员收益规则键
	 * @param value	业务员收益规则值
	 * @param desc	业务员收益规则描述
	 * 
	 * @author GuoShijie
	 * @createDate 2018.01.19
	 * */
	public int updateRule(String key , String value ,long proxyId) {
		String excuSQL = null;
		Map<String , Object> condition = new HashMap<String , Object>();
		if (value != null && !"[]".equals(value)) {
			excuSQL = "update t_proxy_salesman_profit_rule set _value = :value where _key = :key AND proxy_id = :proxyId";
			condition.put("key", key);
			condition.put("value", value);
			condition.put("proxyId", proxyId);
			return updateBySQL(excuSQL, condition);
		}else {
			excuSQL = "delete from t_proxy_salesman_profit_rule where _key = :key AND proxy_id = :proxyId";
			condition.put("key", key);
			condition.put("proxyId", proxyId);
			return deleteBySQL(excuSQL, condition);
		}
		
	}
}