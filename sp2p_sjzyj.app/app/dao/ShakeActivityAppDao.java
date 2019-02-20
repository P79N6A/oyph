package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.activity.shake.entity.t_shake_activity;
import models.app.bean.InvestAddRateBean;
import models.app.bean.ShakeActivityListBean;

/**
 * 摇一摇活动DAO
 * 
 * @author niu
 * @create 2017-12-12
 */
public class ShakeActivityAppDao extends BaseDao<t_shake_activity> {
	
	/**
	 * 摇一摇活动列表
	 * 
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-12
	 */
	public List<ShakeActivityListBean> listOfShakeActivity() {
		
		String sql = "SELECT t.id AS activityId, t.name AS title, t.description AS description, t.status AS status, t.winrate AS maxNum FROM t_shake_activity t WHERE t.status = 3";
		
		return findListBeanBySQL(sql, ShakeActivityListBean.class, null);
	}
	
	/**
	 * 查询投资可用的加息券
	 * 
	 * @param userId 用户Id
	 * @param amount 投资大于等于多少可用
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-21
	 */
	public List<InvestAddRateBean> listOfInvestUseAddRate(long userId, double amount) {
		
		String sql = "SELECT ru.id AS id, rt.apr AS apr, rt.amount AS amount FROM t_add_rate_user ru INNER JOIN t_add_rate_ticket rt ON ru.ticket_id = rt.id WHERE ru.status = 1 AND ru.user_id = :userId AND rt.amount <= :amount ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		condition.put("amount", amount);
		
		return findListBeanBySQL(sql, InvestAddRateBean.class, condition);
	}
	
}
