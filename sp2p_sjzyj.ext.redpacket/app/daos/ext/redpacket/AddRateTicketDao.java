package daos.ext.redpacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.ext.redpacket.bean.AddRateTicketBean;
import models.ext.redpacket.entity.t_add_rate_ticket;

/**
 * 加息券 Dao
 * 
 * @author niu
 * @create 2017.10.27
 */
public class AddRateTicketDao extends BaseDao<t_add_rate_ticket> {

	protected AddRateTicketDao() {
		
	}
	

	/**
	 * 查询所有 加息券
	 * 
	 * @author niu
	 * @create 2017.11.1
	 */
	public List<t_add_rate_ticket> listOfAddRateTicket() {
		
		String sql = "SELECT * FROM t_add_rate_ticket";		
		return findListBySQL(sql, null);
	}
	
	/**
	 * 查询 - 加息券 — 根据活动ID
	 * 
	 * @author niu
	 * @create 2017.11.2
	 */
	public List<t_add_rate_ticket> listOfAddRateTicketByActId(long actId) {
		
		String sql = "SELECT * FROM t_add_rate_ticket t WHERE t.act_id = :actId";	
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("actId", actId);
		
		return findListBySQL(sql, condition);
	}
	
	/**
	 * 查询加息券（根据用户Id）
	 * 
	 * @author niu
	 * @createDate 2017.11.02
	 */
	public List<t_add_rate_ticket> listOfTicketByUserId(long userId) {
		
		String sql = "SELECT * FROM t_add_rate_user t WHERE t.user_id = :userId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return findListBySQL(sql, condition);
	}
	
	/**
	 * 查询加息券（根据用户Id）
	 * 
	 * @author niu
	 * @createDate 2017.11.02
	 */
	public List<AddRateTicketBean> listOfAddRateTicketByUserId(long userId) {
		
		String sql = "SELECT u.id, t.apr AS apr, to_days(u.etime) - to_days(now()) AS day, t.amount AS amount, u.status AS status, DATE_FORMAT(u.etime, '%Y-%c-%d') AS todate FROM t_add_rate_user u INNER JOIN t_add_rate_ticket t ON u.ticket_id = t.id WHERE u.user_id = :userId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		return super.findListBeanBySQL(sql, AddRateTicketBean.class, condition);
	}
	
	/**
	 * 查询加息券（根据用户Id和加息券状态）
	 * 
	 * @author niu
	 * @createDate 2017.11.03
	 */
	public List<AddRateTicketBean> listOfTicketByUserIdAndStatus(long userId, int status) {
		
		String sql = "SELECT u.id, t.apr AS apr, to_days(u.etime) - to_days(now()) AS day, t.amount AS amount, u.status AS status, DATE_FORMAT(u.etime, '%Y-%c-%d') AS todate FROM t_add_rate_user u INNER JOIN t_add_rate_ticket t ON u.ticket_id = t.id WHERE u.user_id = :userId AND u.status = :status";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		condition.put("status", status);
		
		return super.findListBeanBySQL(sql, AddRateTicketBean.class, condition);
	}
	
	/**
	 * 是否使用加息券
	 * 
	 * @author niu
	 * @createDate 2017.11.06
	 */
	public t_add_rate_ticket getTicket(long userId, long investId, int status) {
		
		String sql = "SELECT * FROM t_add_rate_ticket rt WHERE rt.id = (SELECT ru.ticket_id FROM t_add_rate_user ru WHERE ru.user_id = :userId AND ru.invest_id = :investId AND ru.status = :status)";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		condition.put("investId", investId);
		condition.put("status", status);
		
		return this.findBySQL(sql, condition);
	}
	
	
	
	
}




















