package services.ext.redpacket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import models.activity.shake.entity.t_shake_record;
import models.activity.shake.entity.t_shake_set;
import models.common.entity.t_conversion_user.ConversionType;
import models.core.entity.t_bid;
import models.ext.redpacket.bean.MaketInvestRedBean;
import models.ext.redpacket.entity.t_red_packet;
import models.ext.redpacket.entity.t_red_packet_account;
import models.ext.redpacket.entity.t_red_packet_user;
import models.ext.redpacket.entity.t_red_packet_user.RedpacketStatus;
import net.sf.json.JSONObject;
import payment.impl.PaymentProxy;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import services.activity.shake.ShakeRecordService;
import services.activity.shake.ShakeSetService;
import services.base.BaseService;
import services.common.ConversionService;
import services.common.UserFundService;
import yb.YbUtils;
import yb.enums.EntrustFlag;
import yb.enums.ServiceType;

import com.shove.security.Encrypt;

import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.interfaces.ICacheable;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ReentrantLockUtil;
import common.utils.ResultInfo;
import daos.ext.redpacket.RedpacketAccountDao;
import daos.ext.redpacket.RedpacketDao;
import daos.ext.redpacket.RedpacketUserDao;

/**
 * 红包service
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月16日
 */
public class RedpacketService extends BaseService<t_red_packet> implements ICacheable {

	protected RedpacketDao redPacketDao	 = null;
	
	protected RedpacketUserDao redpacketUserDao = Factory.getDao(RedpacketUserDao.class);
	
	protected RedpacketAccountDao redpacketAccountDao = Factory.getDao(RedpacketAccountDao.class);
	
	protected UserFundService userFundService = Factory.getService(UserFundService.class);

	protected ConversionService conversionService = Factory.getService(ConversionService.class);
	
	protected static  ShakeSetService shakeSetService = Factory.getService(ShakeSetService.class);
	
	protected RedpacketService(){
		this.redPacketDao =  Factory.getDao(RedpacketDao.class);
		super.basedao = this.redPacketDao;
	}
	
	/**
	 * 创建红包相关的数据(包括用户红包，以及用户的红包账户)
	 *
	 * @param userId 用户的id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public ResultInfo creatRedpacket(long userId) {
		ResultInfo result = new ResultInfo();
		
		List<t_red_packet> redpList =  getCache();
		if(redpList == null || redpList.size() == 0){
			result.code = 0;
			result.msg = "没有红包";
			
			return result;
		}
		Date now = new Date();
		for(t_red_packet red_packet : redpList){
			
			if (red_packet.amount > 0.0) {
				t_red_packet_user redpacket_user = new t_red_packet_user();
				redpacket_user.time = now;
				redpacket_user._key = red_packet._key;
				redpacket_user.red_packet_name = red_packet.name;
				redpacket_user.user_id = userId;
				redpacket_user.amount = red_packet.amount;
				redpacket_user.use_rule = red_packet.use_rule;
				redpacket_user.limit_day = red_packet.limit_day;
				redpacket_user.invest_id = 0 ;
				redpacket_user.setStatus(t_red_packet_user.RedpacketStatus.INACTIVATED);
				
				if(!redpacketUserDao.save(redpacket_user)){
					result.code = -1;
					result.msg = "发送红包失败!";
					
					return result;
				}
			}
		}
		
		t_red_packet_account red_packet_account = new t_red_packet_account();
		red_packet_account.time = now;
		red_packet_account.user_id = userId;
		red_packet_account.balance = 0;
	
		if(!redpacketAccountDao.save(red_packet_account)){
			result.code = -2;
			result.msg = "添加红包账户失败!";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "成功";
		
		return result;
	}
	
	
	/**
	 * 给老用户推广红包
	 *
	 * @param userId 用户的id
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年7月6日
	 */
	public ResultInfo creatRedpackets(long userId) {
		ResultInfo result = new ResultInfo();
		t_red_packet redPacket = redPacketDao.findByColumn("id=?", 7L);
		if(redPacket == null ){
			result.code = 0;
			result.msg = "没有红包";
			
			return result;
		}
		Date now = new Date();
		
		if (redPacket.amount > 0.0) {
			t_red_packet_user redpacket_user = new t_red_packet_user();
			redpacket_user.time = now;
			redpacket_user._key = redPacket._key+now.getTime();
			redpacket_user.red_packet_name = redPacket.name;
			redpacket_user.user_id = userId;
			redpacket_user.amount = redPacket.amount;
			redpacket_user.use_rule = redPacket.use_rule;
			redpacket_user.limit_day = redPacket.limit_day;
			redpacket_user.limit_time = DateUtil.addDay(new Date(), redpacket_user.limit_day);
			redpacket_user.invest_id = 0 ;
			redpacket_user.setStatus(t_red_packet_user.RedpacketStatus.UNRECEIVED);
			
			if(!redpacketUserDao.save(redpacket_user)){
				result.code = -1;
				result.msg = "发送红包失败!";
				
				return result;
			}
		}
		
		t_red_packet_account red_packet_account = new t_red_packet_account();
		red_packet_account.time = now;
		red_packet_account.user_id = userId;
		red_packet_account.balance = 0;
	
		if(!redpacketAccountDao.save(red_packet_account)){
			result.code = -2;
			result.msg = "添加红包账户失败!";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "成功";
		
		return result;
	}
	
	
	
	
	/**
	 * 激活某个用户的某个红包
	 *
	 * @param _key 该红包的唯一标识(具体的参见RedpacketKey)
	 * @param userid 用户的id
	 * @return code-2:时表示该用户没有该红包，红包已经激活或者领取<br>
	 *  code-1:红包激活成功<br>
	 *  code--1:红包激活失败(需要进行数据回滚)<br>
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public ResultInfo activatRedpacket(String _key,long userid){
		ResultInfo result = new ResultInfo();
		t_red_packet_user red_packet_user = redpacketUserDao.findByColumn(" _key=? and user_id=?", _key,userid);
		if (red_packet_user == null) {
			result.code = 2;
			result.msg = "该用户没有该类型的红包";
			
			return result;
		}
		
		if (!RedpacketStatus.INACTIVATED.equals(red_packet_user.getStatus())) {
			result.code = 2;
			result.msg = "红包已经被激活或者领取";
			
			return result;
		}
		
		red_packet_user.setStatus(RedpacketStatus.UNRECEIVED);
		
		/** 激活成功开始有效时间计时  */
		red_packet_user.limit_time = DateUtil.addDay(new Date(), red_packet_user.limit_day);
		if(!redpacketUserDao.save(red_packet_user)){
			result.code = -1;
			result.msg = "红包激活失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "红包激活成功!";
		
		return result;
	}
	
	/**
	 * 领取红包(用户资金变动完成)
	 * 
	 * @description 1.资金验签(用户账户)<br>
	 * 				2.更新红包状态，<br>
	 * 				3.资金变动(红包账户增加资金，用户账户虚拟资产增加资金)<br>
	 * 				3.资金验签更新(用户账户)<br>
	 *
	 * @param _key 该红包的唯一标识(具体的参见RedpacketKey)
	 * @param userId 用户id
	 * @return code-0:时表示该用户没有该红包，红包已经未激活或者已领取
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public ResultInfo receiveRedpacket(String _key, long userId) {
		ResultInfo result = new ResultInfo();
		
		//判断资金是否异常(资金验签)
		result = userFundService.userFundSignCheck(userId);
		if (result.code < 1) {
			
			return result;
		}
		
		//更改红包状态为已领取
		t_red_packet_user red_packet_user = redpacketUserDao.findByColumn(" _key=? and user_id=?", _key,userId);
		if (red_packet_user == null) {
			result.code = 0;
			result.msg = "该用户没有该类型的红包";
			
			return result;
		}
		if (!RedpacketStatus.UNRECEIVED.equals(red_packet_user.getStatus())) {
			result.code = 0;
			result.msg = "红包未激活或者已领取";
			
			return result;
		}
		red_packet_user.setStatus(RedpacketStatus.RECEIVED);	
		if(!redpacketUserDao.save(red_packet_user)){
			result.code = -1;
			result.msg = "红包领取失败";
			
			return result;
		}
		
		/*//更新红包账户金额
		int row = redpacketAccountDao.accountFundAdd(userId, red_packet_user.amount);
		if (row < 1) {
			LoggerUtil.info(false, "增加用户红包可用余额失败，【userId：%s，amount：%s】", userId, red_packet_user.amount);

			result.code = -1;
			result.msg = "红包领取失败";
			
			return result;
		}
		
		//更新用户账户虚拟资产
		boolean flag = userFundService.userVisualFundAdd(userId, red_packet_user.amount);
		if (!flag){
			result.code = -3;
			result.msg = "更新用户账户虚拟资产失败";

			return result;
		}
		
		//更新用户账户签名字段
		result = userFundService.userFundSignUpdate(userId);
		if(result.code < 1){
			
			return result;
		}*/
		
		result.code = 1;
		result.msg = "红包领取成功";
		result.obj = red_packet_user;
		return result;
	}
	
	/**
	 * 申请兑换红包
	 *
	 * @description 1.资金验签(红包账户)<br>
	 * 				2.资金变动(红包账户余额减少资金，红包账户冻结金额增加)<br>
	 * 				3.资金验签更新(红包账户)<br>
	 * 				4.产生兑换记录<br>
	 * 
	 * @param userId 用户ID
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月16日
	 */
	public ResultInfo applayConversion(long userId){
		ResultInfo result = new ResultInfo();
		
		t_red_packet_account redPacketAccount = findAccountByUserid(userId);
		if (redPacketAccount == null) {
			result.code = -1;
			result.msg = "查询红包账户失败";
			
			return result;
		}
		
		//兑换金额
		double conversionAmt = redPacketAccount.balance;

		if (conversionAmt <= 0) {
			result.code = -1;
			result.msg = "红包可兑换余额不足";
			
			return result;
		}
		
		//每次兑换上限金额
		if (conversionAmt > ConfConst.MAX_CONVERSION){
			conversionAmt = ConfConst.MAX_CONVERSION;
		}
		
		//用户账户签名校验
		result = userFundService.userFundSignCheck(userId);
		if (result.code < 1) {
			
			return result;
		}

		//红包金额减少
		int row = redpacketAccountDao.accountFundMinus(userId, conversionAmt);
		if (row < 1) {
			result.code = -1;
			result.msg = "扣除用户红包金额失败";

			return result;
		}
		
		boolean flag = conversionService.creatConversion(userId, conversionAmt, ConversionType.REDPACKET);
		if (!flag) {
			result.code = -1;
			result.msg = "生成兑换记录失败";

			return result;
		}
		
		//用户虚拟资产减少
		boolean fundMinus = userFundService.userVisualFundMinus(userId, conversionAmt);
		if (!fundMinus) {
			result.code = -1;
			result.msg = "扣除用户的虚拟资产失败";

			return result;
		}
		
		//用户账户签名更新
		result = userFundService.userFundSignUpdate(userId);
		if (result.code < 1) {
			
			return result;
		}
		
		result.code = 1;
		result.msg = "兑换申请成功，平台正在处理";
		return result;
	}
	
	/**
	 * 更新红包规则
	 *
	 * @param newRedpackets map的key为红包的唯一规则，value为更新后的值
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
		
		addAFlushCache();//刷新红包基础数据
		
		return result;
	}
	
	/**
	 * 查询用户在某个场景下的红包
	 *
	 * @param userid 用户的ID
	 * @param _key 红包场景的关键字(值参考RedpacketKey)
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月24日
	 */
	public t_red_packet_user findRedpacketByUser(Long userid,String _key){
		t_red_packet_user red_packet_user = redpacketUserDao.findByColumn(" _key=? and user_id=?", _key,userid);
		
		return red_packet_user;
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
	public t_red_packet findByKey(String _key){
		List<t_red_packet> redPackets = getCache();
		if (redPackets == null || redPackets.size() == 0) {
			return null;
		}
		
		for(t_red_packet red_packet : redPackets){
			if (red_packet._key.equals(_key)) {
				return red_packet;
			}
		}
		
		return null;
	}
	
	/**
	 * 查询某个用户的红包账户
	 *
	 * @param userid 用户ID
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public t_red_packet_account findAccountByUserid(long userid) {

		t_red_packet_account account = redpacketAccountDao.findByColumn(" user_id=? ", userid);
		
		return account;
	}
	
	@Override
	public List<t_red_packet> findAll() {
		
		return getCache();
	}
	
	/**
	 * 查询某个用户的所有的红包
	 *
	 * @param userid 用户的ID
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public List<t_red_packet_user> queryRedpacketsByUserid(Long userid) {
		
		List<t_red_packet_user> redpacketUsers = redpacketUserDao.findListByColumn(" user_id=? ", userid);
		
		return redpacketUsers;
	}

	@Override
	public void addAFlushCache() {
		List<t_red_packet> packets = redPacketDao.findAll();
		
		Cache.safeSet(this.getClass().getName(),packets,Constants.CACHE_TIME_HOURS_24);
	}

	@Override
	public List<t_red_packet> getCache() {
		List<t_red_packet> packets = (List<t_red_packet>) Cache.get(this.getClass().getName());
		if (packets == null) {
			addAFlushCache();
			packets = (List<t_red_packet>) Cache.get(this.getClass().getName());
		}
		
		return packets;
	}

	@Override
	public void clearCache() {
		Cache.safeDelete(this.getClass().getName());
	}
	
	/**
	 * 根据状态查询红包
	 * @param userid
	 * @param status
	 * @return
	 */
	public List<t_red_packet_user> queryRedpacketsByUserIdStatus(Long userid , int status) {
		
		List<t_red_packet_user> redpacketUsers = redpacketUserDao.findListByColumn(" user_id=? and status = ? ", userid , status);
		
		return redpacketUsers;
	}
	
	/**
	 * 根据红包ID查询红包
	 * @param redPacketId
	 * @return
	 */
	public t_red_packet_user queryRedPacket(long redPacketId){
		
		return redpacketUserDao.findByID(redPacketId);
	}
	
	/**
	 * 修改指定红包状态
	 * @param redPacketId
	 * @param status
	 * @param endStatus
	 * @return
	 */
	public int updateRedPacketStatus(long redPacketId , int status , int endStatus ){
		return redpacketUserDao.updateRedPacketStatus(redPacketId, status, endStatus) ;
	}
	
	/**
	 * 锁定红包
	 * @param redPacketId
	 * @param status
	 * @param endStatus
	 * @return
	 */
	public int updateRedPacketLockTime(long redPacketId , int status , int endStatus ){
		return redpacketUserDao.updateRedPacketLockTime(redPacketId, status, endStatus) ;
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
		return redpacketUserDao.updateRePacketLockStatus( status, endStatus) ;
	}
	
	/**
	 * 摇一摇活动获得红包
	 * @param userId 获得红包用户id
	 * @param record 用户获奖记录
	 * 
	 * @return boolean  是否获得
	 * @author LiuPengwei
	 * @creatDate 2017年12月14日
	 */
	
	public boolean creatShakeRedPacket (long userId, t_shake_record record){
		
		boolean flag = false;

		Date now = new Date();
		
		t_shake_set shakeSet = shakeSetService.findByColumn("activity_id = ?", record.activity_id);
		
		/**使用期限*/
		Calendar calendar = new GregorianCalendar(); 
		calendar.setTime(now); 
		calendar.add(calendar.DATE, 30);
		Date limit_time=calendar.getTime();
		
		t_red_packet_user redpacket_user = new t_red_packet_user();
		redpacket_user.time = now;
		redpacket_user._key = "shake_red_packet";
		redpacket_user.red_packet_name = "摇一摇活动红包";
		redpacket_user.user_id = userId;
		redpacket_user.amount = record.amount;
		redpacket_user.use_rule = shakeSet.use_rule;
		redpacket_user.limit_day = 30;
		redpacket_user.limit_time = limit_time;
		redpacket_user.invest_id = 0;
		redpacket_user.setStatus(t_red_packet_user.RedpacketStatus.RECEIVED);
		
		if(!redpacketUserDao.save(redpacket_user)){		
			
			return flag;			
		}
		t_red_packet_account userRedPacket = redpacketAccountDao.findByColumn("user_id = ?", userId);
		
		if (userRedPacket == null){
			t_red_packet_account red_packet_account = new t_red_packet_account();
			red_packet_account.time = now;
			red_packet_account.user_id = userId;
			red_packet_account.balance = 0;
			
			if(!redpacketAccountDao.save(red_packet_account)){
				
				return flag;	
			}
		}
		
		
		return true;	
	}
	
	public List<t_red_packet_user> queryInvestRedpacketsByUserid(long userId, double InvestAmt){
		return redpacketUserDao.queryInvestRedpacketsByUserid(userId, InvestAmt) ;
	}
	
	/**
	 * 红包冲正
	 * 
	 * @param bid					标
	 * @param redPacketAmount		红包金额
	 * @param oldOderNo				原序号
	 * @param oldbusinessSeqNo		原业务流水号
	 * @param userId				客户的台账账号
	 * @return
	 * 
	 * @author niu
	 * @create 2018-01-01
	 */
	public ResultInfo redPacketCorrect(t_bid bid, long redPacketId, String oldOderNo, String oldbusinessSeqNo, long userId) {
		
		t_red_packet_user redPacket = redpacketUserDao.findByID(redPacketId);
		
		ResultInfo result = new ResultInfo();
		
		Map<String, Object> accMap = new HashMap<String, Object>();
		accMap.put("oderNo", "0");
		accMap.put("oldbusinessSeqNo", oldbusinessSeqNo);
		accMap.put("oldOderNo", oldOderNo);
		accMap.put("debitAccountNo", userId);
		accMap.put("cebitAccountNo", yb.YbConsts.MARKETNO);
		accMap.put("currency", "CNY");
		accMap.put("amount", YbUtils.formatAmount(redPacket.amount));
		accMap.put("summaryCode", "T11");
		
		List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accMap);
		
		Map<String, Object> conMap = new HashMap<String, Object>();
		conMap.put("oderNo", "0");
		conMap.put("contractType", "");
		conMap.put("contractRole", "");
		conMap.put("contractFileNm", "");
		conMap.put("debitUserid", "");
		conMap.put("cebitUserid", "");
		
		List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();
		contractList.add(conMap);
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.WASHED);
		String clientSn = "oyph_" + UUID.randomUUID().toString();
		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().fundTrade(clientSn, userId, businessSeqNo, bid, ServiceType.WASHED, EntrustFlag.UNENTRUST, accountList, contractList);		
		}
				
		return result;
	}
	
	
	/**
	 * 添加摇一摇红包
	 * 
	 * @param userId
	 * @param record
	 * @return
	 * 
	 * @author LiuPengwei 
	 * @createDate 2018年5月23日10:31:13
	 */
	public boolean creatShareRedPacket(long userId, t_shake_record record){
		
		boolean flag = false;

		Date now = new Date();
		
		t_shake_set shakeSet = shakeSetService.findByColumn("activity_id = ?", record.activity_id);
		
		/**使用期限*/
		Calendar calendar = new GregorianCalendar(); 
		calendar.setTime(now); 
		calendar.add(calendar.DATE, 30);
		Date limit_time=calendar.getTime();
		
		t_red_packet_user redpacket_user = new t_red_packet_user();
		redpacket_user.time = now;
		redpacket_user._key = "share_red_packet";
		redpacket_user.red_packet_name = "用户分享红包";
		redpacket_user.user_id = userId;
		redpacket_user.amount = record.amount;
		redpacket_user.use_rule = shakeSet.use_rule;
		redpacket_user.limit_day = 30;
		redpacket_user.limit_time = limit_time;
		redpacket_user.invest_id = 0;
		redpacket_user.setStatus(t_red_packet_user.RedpacketStatus.RECEIVED);
		
		if(!redpacketUserDao.save(redpacket_user)){		
			
			return flag;			
		}
		t_red_packet_account userRedPacket = redpacketAccountDao.findByColumn("user_id = ?", userId);
		
		if (userRedPacket == null){
			t_red_packet_account red_packet_account = new t_red_packet_account();
			red_packet_account.time = now;
			red_packet_account.user_id = userId;
			red_packet_account.balance = 0;
			
			if(!redpacketAccountDao.save(red_packet_account)){
				
				return flag;	
			}
		}
		
		
		return true;	
	}
	
	
	
	/**
	 * 红包冲正
	 * 
	 * @param bid					标
	 * @param redPacketAmount		红包金额
	 * @param oldOderNo				原序号
	 * @param oldbusinessSeqNo		原业务流水号
	 * @param userId				客户的台账账号
	 * @return
	 * 
	 * @author LiuPengwei
	 * @create 2018年1月12日
	 */
	public ResultInfo redPacketCorrects(t_bid bid,long userId, double redPacketAmount, String oldOderNo, String oldbusinessSeqNo) {
		
		ResultInfo result = new ResultInfo();
		
		Map<String, Object> accMap = new HashMap<String, Object>();
		accMap.put("oderNo", "0");
		accMap.put("oldbusinessSeqNo", oldbusinessSeqNo);
		accMap.put("oldOderNo", oldOderNo);
		accMap.put("debitAccountNo", userId + ""); //台账号
		accMap.put("cebitAccountNo", yb.YbConsts.MARKETNO);
		accMap.put("currency", "CNY");
		accMap.put("amount", YbUtils.formatAmount(redPacketAmount));
		accMap.put("summaryCode", "T11");
		
		List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accMap);
		
		
		Map<String, Object> conMap = new HashMap<String, Object>();
		conMap.put("oderNo", "2");
		conMap.put("contractType", "");
		conMap.put("contractRole", "");
		conMap.put("contractFileNm", "");
		conMap.put("debitUserid", "");
		conMap.put("cebitUserid", "");
		
		List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();
		contractList.add(conMap);
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.WASHED);
		String clientSn = "oyph_" + UUID.randomUUID().toString();
		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().fundTrade(clientSn, userId, businessSeqNo, bid, ServiceType.WASHED, EntrustFlag.UNENTRUST, accountList, contractList);		
		}
				
		return result;
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
	public String pageOfRedPacket(long userId, int status, int currPage, int pageSize) {
		
		PageBean<t_red_packet_user> pageBean = redpacketUserDao.pageOfAddRateTicket(userId, status, currPage, pageSize);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 1);
		map.put("msg", "红包查询成功");
		map.put("shakeRedPacketList", pageBean.page);

		return JSONObject.fromObject(map).toString();
	}
	
	/**
	 * 后台红包发放记录分页查询
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
	 * @create 2018年5月21日16:46:11
	 */
	public PageBean<MaketInvestRedBean> pageOfMaketRedPacket(int currPage, int pageSize,int showType,int orderType,int orderValue,
			String userName, String startTime, String endTime) {
		
		PageBean<MaketInvestRedBean> pageBean = redpacketUserDao.pageOfMaketRedPacket(currPage, pageSize,showType, orderType, orderValue
				,userName, startTime, endTime);

		return pageBean;
	}
	
	
	/**
	 * 查询所有的红包
	 *
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2016年2月15日
	 */
	public List<t_red_packet_user> queryRedpacketsAll() {
		
		List<t_red_packet_user> redpacketUsers = redpacketUserDao.findAll();
		
		return redpacketUsers;
	}
	
	
	
	/**
	 * 根据状态查询红包
	 * 
	 * @param status 状态
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月23日
	 */
	public List<t_red_packet_user> queryRedpacketsStatus(int status) {
		
		List<t_red_packet_user> redpacketUsers = redpacketUserDao.findListByColumn("status = ? ", status);
		
		return redpacketUsers;
	}
	
	
	
}
