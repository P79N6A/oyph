package daos.ext.redpacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.ext.redpacket.bean.AddRateTicketBean;
import models.ext.redpacket.bean.MaketInvestAddBean;
import models.ext.redpacket.bean.MaketInvestRedBean;
import models.ext.redpacket.entity.t_add_rate_user;
import models.ext.redpacket.entity.t_red_packet_user;

/**
 * 用户加息券记录表
 * 
 * @author niu
 * @createDate 2017.11.03
 */
public class AddRateUserDao extends BaseDao<t_add_rate_user> {

	protected AddRateUserDao() {
		
	}
	
	/**
	 * 更新加息券状态
	 * 
	 * @author niu
	 * @createDate 2017.11.03
	 */
	public int updateAddRateTicketStatus(long ticketId, int status) {
		
		String sql = "UPDATE t_add_rate_user SET status = :status WHERE id = :ticketId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("ticketId", ticketId);
		condition.put("status", status);
		
		return updateBySQL(sql, condition);
	}
	
	/**
	 * 更新加息券状态（根据投标Id和用户Id）
	 * 
	 * @author niu
	 * @createDate 2017.11.03
	 */
	public int updateStatusByInvestIdAndUserId(long investId, long userId, int status) {
		
		String sql = "UPDATE t_add_rate_user SET status = :status WHERE user_id = :userId AND invest_id = :investId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("investId", investId);
		condition.put("userId", userId);
		condition.put("status", status);
		
		return updateBySQL(sql, condition);
	}
	
	/**
	 * 查询是否已有加息券
	 * 
	 * @author niu
	 * @createDate 2017.11.06
	 */
	public t_add_rate_user getRateUserByUserIdAndTicketId(long userId, long ticketId) {
		
		String sql = "SELECT * FROM t_add_rate_user ru WHERE ru.user_id = :userId AND ru.ticket_id = :ticketId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		condition.put("ticketId", ticketId);
		
		return this.findBySQL(sql, condition);
	}
	
	/**
	 * 把过期的加息券状态修改为过期
	 * @return
	 */
	public int updateStatusByTime() {
		String sql = "UPDATE t_add_rate_user SET status = 4 WHERE etime < NOW() AND status = 1";
		
		return updateBySQL(sql, null);
	}
	
	/**
	 * APP分页查询 用户加息券
	 * 
	 * @param userId 用户ID
	 * @param status 加息券状态
	 * @param currPage 当前页码
	 * @param pageSize 显示条数
	 * @return
	 * 
	 * @author niu
	 * @create 2017-12-20
	 */
	public PageBean<AddRateTicketBean> pageOfAddRateTicket(long userId, int status, int currPage, int pageSize) {
		
		StringBuffer querySQL = new StringBuffer("SELECT ru.id, rt.apr AS apr, to_days(ru.etime) - to_days(now()) AS day, rt.amount AS amount, ru.status AS status, DATE_FORMAT(ru.etime, '%Y-%c-%d') AS todate FROM t_add_rate_user ru INNER JOIN t_add_rate_ticket rt ON ru.ticket_id = rt.id WHERE ru.user_id = :userId ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(ru.id) FROM t_add_rate_user ru INNER JOIN t_add_rate_ticket rt ON ru.ticket_id = rt.id WHERE ru.user_id = :userId ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		
		if (status == 2) {
			countSQL.append(" AND (ru.status = 2 OR ru.status = 3) ");
			querySQL.append(" AND (ru.status = 2 OR ru.status = 3) ");
					
		} else {
			countSQL.append(" AND ru.status = :status ");
			querySQL.append(" AND ru.status = :status ");
			
			condition.put("status", status);
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), AddRateTicketBean.class, condition);
	}
	
	/**
	 * 查询已使用的加息券
	 * 
	 * @param investId
	 * @param userId
	 * @param status
	 * @return
	 */
	public AddRateTicketBean findTicketByUserId(long investId, long userId, int status) {
		
		String sql = "SELECT ru.id, rt.apr AS apr, to_days(ru.etime) - to_days(now()) AS day, rt.amount AS amount, ru.status AS status, DATE_FORMAT(ru.etime, '%Y-%c-%d') AS todate FROM t_add_rate_user ru INNER JOIN t_add_rate_ticket rt ON ru.ticket_id = rt.id WHERE ru.user_id = :userId AND ru.invest_id = :investId AND ru.status = :status";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		condition.put("investId", investId);
		condition.put("status", status);
		
		return findBeanBySQL(sql, AddRateTicketBean.class, condition);
	}
	
	
	
	/**
	 * 后台红包发放记录分页,分类,排序,模糊查询
	 * 
	 * @param showType 
	 * @param currPage 当前页码
	 * @param pageSize 显示条数
	 * @return
	 * 
	 * @author LiuPengwei
	 * @param endTime 
	 * @param startTime 
	 * @param userName 
	 * @create 2018年5月21日11:55:05
	 */
	public PageBean<MaketInvestAddBean> pageOfMaketAddRate(int currPage, int pageSize, int showType, int orderType,int orderValue,
			String userName, String startTime, String endTime) {
	
		/**
		 * SELECT 
		 * 		tart.id AS id,
		 * 		tu.name AS name,
		 * 		taru.channel AS channel,
		 * 		taru.stime AS time,
		 * 		taru.etime AS etime,
		 * 		tart.apr AS apr,
		 * 		tart.amount AS use_rule,
		 * 		taru.create_invest AS create_invest,
		 * 		taru.status AS status 
		 * FROM
		 * 		t_add_rate_ticket tart 
		 * INNER JOIN  
		 * 		t_add_rate_user taru
		 * ON 
		 * 		tart.id = taru.ticket_id 
		 * INNER JOIN  
		 * 		t_user tu 
		 * ON 
		 * 		taru.user_id = tu.id
		 * ORDER BY 
		 * 		tart.id 
		 * DESC
		 */
		
		StringBuffer querySQL = new StringBuffer("SELECT tart.id AS id ,tu.name AS name, taru.channel AS channel ,taru.stime AS time , taru.etime AS etime ,tart.apr AS apr ,tart.amount AS use_rule,taru.create_invest AS create_invest,taru.status AS status FROM t_add_rate_ticket tart INNER JOIN  t_add_rate_user taru ON tart.id = taru.ticket_id INNER JOIN  t_user tu ON taru.user_id = tu.id ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_add_rate_ticket tart INNER JOIN  t_add_rate_user taru ON tart.id = taru.ticket_id INNER JOIN  t_user tu ON taru.user_id = tu.id ");
		Map<String, Object> condition = new HashMap<String, Object>();
		
		//查询分类
		switch (showType) {
		case 1:
			//已领取
			querySQL.append(" where taru.status =1 ");			
			countSQL.append(" where taru.status =1 ");
			
			break;
		case 3:
			//已使用
			querySQL.append(" where taru.status =3 ");
			countSQL.append(" where taru.status =3 ");

			break;
		case 4:
			//已过期
			querySQL.append(" where taru.status =4 ");
			countSQL.append(" where taru.status =4 ");
			
			break;
		default:
			//所有

			break;
			
		}
		
		if (StringUtils.isNotBlank(userName)) {
			/* 按用户名搜索 */
			countSQL.append(" AND tu.name like :name");
			querySQL.append(" AND tu.name like :name");
			condition.put("name", "%"+userName+"%");
		}
		
		if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
			/* 按发放时间搜索 */
			countSQL.append(" AND taru.stime>= :startTime AND taru.stime <= :endTime");
			querySQL.append(" AND taru.stime>= :startTime AND taru.stime <= :endTime");
			condition.put("startTime", startTime);
			condition.put("endTime", endTime);                          
		}
		
		//排序分类
		switch (orderType) {
		case 1:
			querySQL.append (" ORDER BY tart.id ");
			countSQL.append (" ORDER BY tart.id ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
				countSQL.append(" DESC ");
			}
			break;
		
		case 4:
			querySQL.append (" ORDER BY taru.stime ");
			countSQL.append (" ORDER BY taru.stime ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
				countSQL.append(" DESC ");
			}
			break;
		default:
			//所有
			querySQL.append (" ORDER BY tart.id DESC ");
			countSQL.append (" ORDER BY tart.id DESC ");
				
			break;

		}
	
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), MaketInvestAddBean.class, condition);
	}
	
	
	/** 
	 * 查询某状态的加息券个数
	 * @param status
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月30日
	 */
	public int findRateUserStatus(int status) {
		
		String sql = "SELECT count(id) FROM t_add_rate_user where status =:status ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("status", status);
		
		return findSingleIntBySQL(sql, 0, condition);
	}
	
	
}






























