package daos.ext.redpacket;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ext.redpacket.entity.t_coupon_user;
import models.ext.redpacket.entity.t_red_packet_user;
import daos.base.BaseDao;

/**
 * 用户加息券dao
 *
 * @description 
 *
 * @author LiuPengwei
 * @createDate 2017年7月24日
 */
public class CouponUserDao extends BaseDao<t_coupon_user> {

	protected CouponUserDao() {}
	
	/**
	 * 修改用户加息券状态
	 */
	public int updateCouponStatus( long couponId , int status , int endStatus){
		String sql = " update t_coupon_user set status = :endStatus where id = :couponId and status = :status " ;
		Map<String,Object> map = new HashMap<String, Object>() ;
		map.put("endStatus",endStatus ) ;
		map.put("couponId",couponId ) ;
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
	 * 将过来有效期的红包标记为已过期
	 * @param status
	 * @return
	 */
	public int updateRePacketExpiredLimitTime(int status){
		String sql = " update t_red_packet_user set status = :status where limit_time <= NOW() ; " ;
		Map<String,Object> map = new HashMap<String, Object>() ;
		map.put("status",status ) ;
		
		return super.updateBySQL(sql, map) ;
	}
	
	/**
	 * 加息券 4-6个月标的状态
	 * @param status
	 * @return
	 */
	public List<t_coupon_user> findCouponKey(long userId,int status){
		String sql = "select * from t_coupon_user where user_id = :userId and status =:status and _key not like 'one'";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId",userId);
		params.put("status", status);
		
		return super.findListBySQL(sql, params);
	}
}
