package daos.ext.redpacket;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import common.enums.IsUse;
import common.utils.PageBean;
import models.app.bean.InformationBean;
import models.common.entity.t_user_info;
import models.ext.redpacket.bean.AddRateTicketBean;
import models.ext.redpacket.bean.MaketInvestRedBean;
import models.ext.redpacket.entity.t_red_packet_user;
import net.sf.json.JSONObject;
import daos.base.BaseDao;

/**
 * 用户红包dao
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月7日
 */
public class RedpacketUserDao extends BaseDao<t_red_packet_user> {

	protected RedpacketUserDao() {}
	
	/**
	 * 修改用户红包状态
	 */
	public int updateRedPacketStatus( long redPacketId , int status , int endStatus){

		String sql = " update t_red_packet_user set status = :endStatus where id = :redPacketId and status = :status " ;
		Map<String,Object> map = new HashMap<String, Object>() ;
		map.put("endStatus",endStatus ) ;
		map.put("redPacketId",redPacketId ) ;
		map.put("status",status ) ;
		
		return super.updateBySQL(sql, map) ;
	}
	
	/**
	 * 锁定红包
	 * @param redPacketId
	 * @param status
	 * @param endStatus
	 * @return
	 */
	public int updateRedPacketLockTime(long redPacketId , int status , int endStatus){
		String sql = " update t_red_packet_user set status = :endStatus , lock_time = now() where id = :redPacketId and status = :status " ;
		Map<String,Object> map = new HashMap<String, Object>() ;
		map.put("endStatus",endStatus ) ;
		map.put("redPacketId",redPacketId ) ;
		map.put("status",status ) ;
		
		return super.updateBySQL(sql, map) ;
	}
	
	/**
	 * 将有效期的红包标记为已过期
	 * @param status
	 * @return
	 */
	public int updateRePacketExpiredLimitTime(int status){
		String sql = " update t_red_packet_user set status = :status where limit_time <= NOW() and status != :stu ";
		Map<String,Object> map = new HashMap<String, Object>() ;
		map.put("status",status ) ;
		map.put("stu", t_red_packet_user.RedpacketStatus.ALREADY_USED.code) ;
		
		return super.updateBySQL(sql, map) ;
	}
	
	/**
	 * 将锁定过了24小时的红包进行解锁
	 * @param status
	 * @return
	 */
	public int updateRePacketLockStatus( int status , int endStatus ){
		String sql = " update t_red_packet_user set status = :endStatus , lock_time = null where status = :status and ADDDATE(NOW(),INTERVAL -1 DAY) >= lock_time ; " ;
		Map<String,Object> map = new HashMap<String, Object>() ;
		map.put("status",status ) ;
		map.put("endStatus",endStatus ) ;
		
		return super.updateBySQL(sql, map) ;
	}
	
	
	/**
	 * 查询用户投标可用红包
	 * @param userId
	 * @param InvestAmt
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2017年12月25日
	 */
	 
	public List<t_red_packet_user> queryInvestRedpacketsByUserid(long userId, double InvestAmt){
		String sql="SELECT * FROM t_red_packet_user WHERE user_id = :userId and use_rule <= :InvestAmt and status=:status";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		condition.put("InvestAmt",InvestAmt);
		condition.put("status", t_red_packet_user.RedpacketStatus.RECEIVED.code);
		return findListBeanBySQL(sql, t_red_packet_user.class, condition);
	}
	
	/**
	 * APP分页查询 用户红包
	 * 
	 * @param userId 用户ID
	 * @param status 红包状态
	 * @param currPage 当前页码
	 * @param pageSize 显示条数
	 * @return
	 * 
	 * @author LiuPengwei
	 * @create 2018年1月5日
	 */
	public PageBean<t_red_packet_user> pageOfAddRateTicket(long userId, int status, int currPage, int pageSize) {
		
		StringBuffer querySQL = new StringBuffer("SELECT * FROM t_red_packet_user  where user_id =:userId and status = :status ORDER BY time asc ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(id) FROM t_red_packet_user  where user_id =:userId and status = :status ORDER BY time asc ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		condition.put("status", status);
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), t_red_packet_user.class, condition);
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
	public PageBean<MaketInvestRedBean> pageOfMaketRedPacket(int currPage, int pageSize, int showType, int orderType,int orderValue,
			String userName, String startTime, String endTime) {
	
		/**
		SELECT 
			ru.id AS id,
			ru.channel AS channel,
			ru.time AS time,
			ru.amount AS red_packet_amount,
			ru.use_rule AS use_rule,
			ru.limit_time AS limit_time ,
			ru.status AS status,
			u.name AS name,
			ru.amount AS red_packet_amount,
			ru._key AS _key ,
			ru.create_invest AS create_invest
		FROM 
			t_red_packet_user ru 
		INNER JOIN 
			t_user u on ru.user_id = u.id
		WHERE
			u.name = :name
		ORDER BY 
			ru.id 
		DESC
	 */
	
		StringBuffer querySQL = new StringBuffer("SELECT ru.id AS id,ru.channel AS channel,ru.time AS time,ru.amount AS red_packet_amount,ru.use_rule AS use_rule,ru.limit_time AS limit_time , ru.status AS status,u.name AS name,ru.amount AS red_packet_amount,ru._key AS _key, ru.create_invest AS create_invest FROM t_red_packet_user ru INNER JOIN t_user u on ru.user_id = u.id ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_red_packet_user ru INNER JOIN t_user u on ru.user_id = u.id ");
		Map<String, Object> condition = new HashMap<String, Object>();
		
		//查询分类
		switch (showType) {
		case 1:
			//已领取
			querySQL.append(" where ru.status =2 ");			
			countSQL.append(" where ru.status =2 ");
			
			break;
		case 2:
			//已使用
			querySQL.append(" where ru.status =3 ");
			countSQL.append(" where ru.status =3 ");

			break;
		case 3:
			//已过期
			querySQL.append(" where ru.status =-1 ");
			countSQL.append(" where ru.status =-1 ");
			
			break;
		default:
			//所有

			break;
			
		}
		
		if (StringUtils.isNotBlank(userName)) {
			/* 按用户名搜索 */
			countSQL.append(" AND u.name like :name");
			querySQL.append(" AND u.name like :name");
			condition.put("name", "%"+userName+"%");
		}
		
		if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
			/* 按活动时间搜索 */
			countSQL.append(" AND ru.time>= :startTime AND ru.time <= :endTime");
			querySQL.append(" AND ru.time>= :startTime AND ru.time <= :endTime");
			condition.put("startTime", startTime);
			condition.put("endTime", endTime);                          
		}
		
		//排序分类
		switch (orderType) {
		case 1:
			querySQL.append (" ORDER BY ru.id ");
			countSQL.append (" ORDER BY ru.id ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
				countSQL.append(" DESC ");
			}
			break;
		
		case 4:
			querySQL.append (" ORDER BY ru.time ");
			countSQL.append (" ORDER BY ru.time ");
			if(orderValue == 0){
				querySQL.append(" DESC ");
				countSQL.append(" DESC ");
			}
			break;
		default:
			//所有
			querySQL.append (" ORDER BY ru.id DESC ");
			countSQL.append (" ORDER BY ru.id DESC ");
				
			break;

		}
	
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), MaketInvestRedBean.class, condition);
	}
	
}
