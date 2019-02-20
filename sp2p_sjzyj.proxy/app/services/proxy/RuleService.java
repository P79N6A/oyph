package services.proxy;

import java.util.Date;
import java.util.Map;

import common.utils.Factory;
import daos.proxy.ProfitDao;
import daos.proxy.RuleDao;
import models.proxy.entity.t_proxy_salesman_profit_rule;
import services.base.BaseService;

/**
 * 业务员收益规则业务
 * @author Niu Dongfeng
 */
public class RuleService extends BaseService<t_proxy_salesman_profit_rule> {

	protected RuleDao ruleDao = Factory.getDao(RuleDao.class);
	
	protected RuleService() {
		super.basedao = ruleDao;
	}
	
	/**
	 * 修改业务员收益规则
	 * @author GuoShijie
	 * @createDate 2018.01.22
	 * */
	/*public int updateRule(String key , String value , long proxyId) {
		
		return ruleDao.updateRule(key, value , proxyId);
	}*/
	
	/**
	 * 批量修改规则
	 * @author GuoShijie
	 * @param proxyId 代理商id
	 * @createDate 2018.01.26
	 * */
	public boolean updateRules(Map<String , String> maps , long proxyId) {
		if (maps == null || maps.keySet() == null || maps.keySet().size() == 0 ) {
			return false;
		}
		
		for (String _key : maps.keySet()) {
			t_proxy_salesman_profit_rule profitRule = ruleDao.findByColumn("_key=? AND proxy_id=?", _key,proxyId);
			if (profitRule == null) {
				t_proxy_salesman_profit_rule rule = new t_proxy_salesman_profit_rule();
				rule.proxy_id = proxyId;
				rule._key = _key;
				rule._value = maps.get(_key);
				rule.save();
				
			} else {
				ruleDao.updateRule(_key , maps.get(_key) , proxyId);
			}
		}
		return true;
	}
}
