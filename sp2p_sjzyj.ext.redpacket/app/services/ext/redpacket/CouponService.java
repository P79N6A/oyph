package services.ext.redpacket;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_conversion_user.ConversionType;
import models.ext.redpacket.entity.t_coupon;
import models.ext.redpacket.entity.t_coupon_user;
import models.ext.redpacket.entity.t_coupon_user.CouponStatus;
import models.ext.redpacket.entity.t_red_packet;
import models.ext.redpacket.entity.t_red_packet_account;
import models.ext.redpacket.entity.t_red_packet_user;
import models.ext.redpacket.entity.t_red_packet_user.RedpacketStatus;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import services.base.BaseService;
import services.common.ConversionService;
import services.common.UserFundService;

import com.shove.security.Encrypt;

import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.interfaces.ICacheable;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ReentrantLockUtil;
import common.utils.ResultInfo;
import daos.ext.redpacket.CouponUserDao;
import daos.ext.redpacket.RedpacketAccountDao;
import daos.ext.redpacket.RedpacketDao;
import daos.ext.redpacket.RedpacketUserDao;

/**
 * 加息券service
 *
 * @description 
 *
 * @author LiuPengwei
 * @createDate 2017年7月24日
 */
public class CouponService extends BaseService<t_coupon> implements ICacheable {
	protected RedpacketDao redPacketDao	 = null;
	
	protected RedpacketUserDao redpacketUserDao = Factory.getDao(RedpacketUserDao.class);
		
	
	protected daos.ext.redpacket.CouponDao CouponDao= null;
	
	protected static CouponUserDao couponUserDao = Factory.getDao(CouponUserDao.class);
	
	protected RedpacketAccountDao redpacketAccountDao = Factory.getDao(RedpacketAccountDao.class);
	
	protected UserFundService userFundService = Factory.getService(UserFundService.class);

	protected ConversionService conversionService = Factory.getService(ConversionService.class);
	
	protected CouponService(){
		this.CouponDao =  Factory.getDao(daos.ext.redpacket.CouponDao.class);
		super.basedao = this.CouponDao;
	}
	
	/**
	 * 创建加息券相关的数据
	 *
	 * @param userId 用户的id
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年7月24日
	 */
	public ResultInfo creatCoupon(long userId,String _key) {
		ResultInfo result = new ResultInfo();
		
		t_coupon coupon = findCouponKey(_key);
		if(coupon == null ){
			result.code = 0;
			result.msg = "没有加息券";
			
			return result;
		}
		
		t_coupon_user couponUser = couponUserDao.findByColumn("user_id = ? and _key = ?", userId,_key);
		
		if(couponUser==null){
			Date now = new Date();
			t_coupon_user coupon_user = new t_coupon_user();
			coupon_user.time = now;
			coupon_user._key = coupon._key;
			coupon_user.coupon_name = coupon.name;
			coupon_user.coupon = coupon.coupon;
			coupon_user.user_id = userId;
			coupon_user.amount = coupon.amount;
			coupon_user.use_rule = coupon.use_rule;
			coupon_user.limit_time =coupon.limit_time;
			coupon_user.setStatus(t_coupon_user.CouponStatus.UNRECEIVED);
					
			if(!couponUserDao.save(coupon_user)){
				result.code = -1;
				result.msg = "发送加息券失败!";
						
				return result;
			}
		}
			
		result.code = 1;
		result.msg = "成功";
		
		return result;
	}
	
	
	
	/**
	 * 激活某个用户的某个加息券
	 *
	 * @param _key 该加息券的唯一标识(具体的参见CouponKey)
	 * @param userid 用户的id
	 * @return code-2:时表示该用户没有该加息券，加息券已经激活或者领取<br>
	 *  code-1:加息券激活成功<br>
	 *  code--1:加息券激活失败(需要进行数据回滚)<br>
	 *
	 * @author LiuPengwei
	 * @createDate 2017年7月26日
	 */
	public static ResultInfo activatCoupon(String _key,long userid){
		ResultInfo result = new ResultInfo();
		t_coupon_user coupon_user = couponUserDao.findByColumn(" _key=? and user_id=?", _key,userid);
		if (coupon_user == null) {
			result.code = 2;
			result.msg = "该用户没有该类型的加息券";
			
			return result;
		}
		
		if (!CouponStatus.INACTIVATED.equals(coupon_user.getStatus())) {
			result.code = 2;
			result.msg = "加息券已经被激活或者领取";
			
			return result;
		}
		
		coupon_user.setStatus(CouponStatus.UNRECEIVED);
		
		if(!couponUserDao.save(coupon_user)){
			result.code = -1;
			result.msg = "加息券激活失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "加息券激活成功!";
		
		return result;
	}
	
	/**
	 * 领取加息券(用户资金变动完成)
	 * 
	 * @description 1.资金验签(用户账户)<br>
	 * 				2.更新加息券状态，<br>
	 *
	 * @param _key 该加息券的唯一标识(具体的参见RedpacketKey)
	 * @param userId 用户id
	 * @return code-0:时表示该用户没有该加息券，加息券已经未激活或者已领取
	 *
	 * @author LiuPengwei
	 * @createDate 2017年7月26日
	 */
	public ResultInfo receiveCoupon(String _key, long userId) {
		ResultInfo result = new ResultInfo();
		
		//判断资金是否异常(资金验签)
		result = userFundService.userFundSignCheck(userId);
		if (result.code < 1) {
			
			return result;
		}
		
		//更改加息券状态为已领取
		t_coupon_user coupon_user = couponUserDao.findByColumn(" _key=? and user_id=?", _key,userId);
		if (coupon_user == null) {
			result.code = 0;
			result.msg = "该用户没有该类型的加息券";
			
			return result;
		}
		if (!CouponStatus.UNRECEIVED.equals(coupon_user.getStatus())) {
			result.code = 0;
			result.msg = "加息券未激活或者已领取";
			
			return result;
		}
		coupon_user.setStatus(CouponStatus.RECEIVED);	
		if(!couponUserDao.save(coupon_user)){
			result.code = -1;
			result.msg = "加息券领取失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "加息券领取成功";
		result.obj = coupon_user;
		return result;
	}
	
	
	/**
	 * 更新红包规则
	 *
	 * @param newRedpackets map的key为加息券的唯一规则，value为更新后的值
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public ResultInfo updateRedpackets(Map<String, Double> newRedpackets , Map<String,Object> map) {

		ResultInfo result = new ResultInfo();
		for(String key : newRedpackets.keySet()){
			t_red_packet redpacket = redPacketDao.findByColumn(" _key=? ", key);
			if (redpacket == null) {
				result.code = -2;
				result.msg = "没有找到对应的红包!";
			
				return result;
			}
			redpacket.amount = newRedpackets.get(key);
			redpacket.use_rule = Double.parseDouble( map.get("use_rule_"+key).toString() ) ;
			redpacket.limit_day = Integer.parseInt( map.get("limit_day_"+key).toString() ) ;
			if (!redPacketDao.save(redpacket)) {
				result.code = -3;
				result.msg = "更新失败!";
			
				return result;
			}
		}

		result.code = 1;
		result.msg = "更新成功!";
		
		addAFlushCache();//刷新加息券基础数据
		
		return result;
	}
	
	/**
	 * 查询用户在某个场景下的加息券
	 *
	 * @param userid 用户的ID
	 * @param _key 加息券场景的关键字(值参考RedpacketKey)
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年7月26日
	 */
	public static t_coupon_user findCouponByUser(Long userid,String _key){
		t_coupon_user coupon_user = couponUserDao.findByColumn(" _key=? and user_id=?", _key,userid);
		
		return coupon_user;
	}
	
	/**
	 * 根据红包的关键字查询某个红包
	 *
	 * @param _key 红包关键字(值参考RedpacketKey)
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月7日
	 */
	public t_coupon findByKey(String _key){
		List<t_coupon> coupons = getCache();
		if (coupons == null || coupons.size() == 0) {
			return null;
		}
		
		for(t_coupon coupon : coupons){
			if (coupon._key.equals(_key)) {
				return coupon;
			}
		}
		
		return null;
	}
	
	
	@Override
	public List<t_coupon> findAll() {
		
		return getCache();
	}
	
	/**
	 * 查询某个用户的所有的加息券
	 *
	 * @param userid 用户的ID
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年7月24日
	 */
	public List<t_coupon_user> queryCouponsByUserid(Long userid) {
		
		List<t_coupon_user> couponUsers = couponUserDao.findListByColumn("user_id = ?", userid);
		
		return couponUsers;
	}

	@Override
	public void addAFlushCache() {
		List<t_coupon> packets = CouponDao.findAll();
		
		Cache.safeSet(this.getClass().getName(),packets,Constants.CACHE_TIME_HOURS_24);
	}

	@Override
	public List<t_coupon> getCache() {
		List<t_coupon> packets = (List<t_coupon>) Cache.get(this.getClass().getName());
		if (packets == null) {
			addAFlushCache();
			packets = (List<t_coupon>) Cache.get(this.getClass().getName());
		}
		
		return packets;
	}

	@Override
	public void clearCache() {
		Cache.safeDelete(this.getClass().getName());
	}
	
	
	

	
	/**
	 * 修改指定加息券状态
	 * @param redPacketId
	 * @param status
	 * @param endStatus
	 * @return
	 */
	public int updateCouponStatus(long coupontId , int status , int endStatus ){
		return couponUserDao.updateCouponStatus(coupontId, status, endStatus);
	}
	

	/**
	 * 将过期的红包标记为已过期
	 * @param status
	 * @return
	 */
	public int updateRePacketExpiredLimitTime(int status){
		return redpacketUserDao.updateRePacketExpiredLimitTime(status) ;
	}
	
	/**
	 * 修改红包锁定状态
	 * @param status
	 * @param endStatus
	 * @return
	 */
	public int updateRePacketLockStatus(int status , int endStatus){
		return redpacketUserDao.updateRePacketLockStatus(status, endStatus) ;
	}
	
	
	/**   
	 * 加息券id查询
	 * @param  couponId
	 * @return t_coupon_user
	 * 
	 * @author LiuPengwei
	 * @createDate 2017年7月26日
	 * 
	 * */
	public t_coupon_user findByIds(long couponId){
		return couponUserDao.findByID(couponId) ;
	}
	
	
	/**
	 * 加息券关键字和状态查询
	 * @param userid
	 * @param status
	 * @param _key
	 */
	public List<t_coupon_user> queryCouponsByUserIdStatus(Long userid , int status ,String _key) {
		
		List<t_coupon_user> couponUsers = couponUserDao.findListByColumn(" user_id=? and status = ? and _key = ?", userid , status , _key);
		return couponUsers;
	}
	
	/**
	 * 加息券状态查询
	 * @param userid
	 * @param status
	 * @param _key
	 */
	public List<t_coupon_user> queryCouponsByUserIdStatuss(Long userid , int status ) {
		
		List<t_coupon_user> couponUsers = couponUserDao.findListByColumn(" user_id=? and status = ? ", userid , status );
		return couponUsers;
	}
	
	
	/**
	 * 通过表id和用户id查询
	 * 
	 */
	public t_coupon_user queryByColumn(Long bid_id ,Long user_id ,double money ,int status) {
		
		t_coupon_user couponUsers = couponUserDao.findByColumn("bid_id = ? and user_id = ? and money = ? and status = ?", bid_id , user_id, money ,status);
		return couponUsers;
	}
	
	
	
	/**
	 *用户id查询
	 * 
	 */
	public List<t_coupon_user> queryListUserid(Long user_id) {
		
		List<t_coupon_user> couponUsers = couponUserDao.findListByColumn("user_id = ? ",user_id);
		return couponUsers;
	}
	
	public List<t_coupon_user> findCouponKey(long userId,int status){
		List<t_coupon_user> couponUsers = couponUserDao.findCouponKey(userId,status);
		return couponUsers;
	}
	
	
	public t_coupon findCouponKey(String _key){
		t_coupon coupons = CouponDao.findByColumn("_key = ?", _key);
		return coupons;
	}
	
	
}
