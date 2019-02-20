package service.ext.wealthcircle;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import models.common.entity.t_conversion_user.ConversionType;
import models.common.entity.t_user;
import models.ext.wealthcircle.bean.MyWealthCircleUser;
import models.ext.wealthcircle.bean.WealthCircleRecord;
import models.ext.wealthcircle.entity.t_wealthcircle_account;
import models.ext.wealthcircle.entity.t_wealthcircle_setting;
import models.ext.wealthcircle.entity.t_wealthcircle_user;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.cache.Cache;
import services.base.BaseService;
import services.common.ConversionService;
import services.common.NoticeService;
import services.common.UserFundService;
import services.common.UserService;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.ext.WealthCircleSettingKey;
import common.enums.NoticeScene;
import common.interfaces.ICacheable;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.number.Arith;

import dao.ext.wealthcircle.WealthCircleAccountDao;
import dao.ext.wealthcircle.WealthCircleSettingDao;
import dao.ext.wealthcircle.WealthCircleUserDao;

public class WealthCircleService extends BaseService<t_wealthcircle_setting> implements ICacheable{

	protected WealthCircleSettingDao wealthCircleSettingDao = null;
	
	protected WealthCircleAccountDao wealthCircleAccountDao = Factory.getDao(WealthCircleAccountDao.class);
	
	protected WealthCircleUserDao wealthCircleUserDao = Factory.getDao(WealthCircleUserDao.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static ConversionService conversionService = Factory.getService(ConversionService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected WealthCircleService() {
		this.wealthCircleSettingDao = Factory.getDao(WealthCircleSettingDao.class);
		super.basedao = this.wealthCircleSettingDao;
	}
	
	/**
	 * 创建财富圈账号和推广关系
	 *
	 * @param WealthCircleCode 财富圈邀请码
	 * @param userId 注册者用户id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月6日
	 */
	public ResultInfo creatWealthCircle(String WealthCircleCode,Long userId){
		ResultInfo result = new ResultInfo();
		
		if (StringUtils.isNotBlank(WealthCircleCode)) {
			result = isWealthCircleCodeUseful(WealthCircleCode);
			if (result.code < 0) {

				return result;
			}
		}
		
		Date now = new Date();
		
		//创建财富圈账号
		t_wealthcircle_account wealthcircleAccount	= new t_wealthcircle_account();
		wealthcircleAccount.time = now;
		wealthcircleAccount.user_id = userId;
		wealthcircleAccount.balance = 0.0;
		wealthcircleAccount.has_convert_amount = 0.0;
		if (!wealthCircleAccountDao.save(wealthcircleAccount)) {
			
			result.code = -1;
			result.msg = "创建财富圈账号失败";
			
			return result;
		}
		
		//如果有邀请码，生成财富圈关系
		if (StringUtils.isNotBlank(WealthCircleCode)) {
			t_wealthcircle_user wealthcircleUser =  findWealthCircleUserByCode(WealthCircleCode);
			wealthcircleUser.user_id = userId;
			wealthcircleUser.active_time = now;
			wealthcircleUser.status = true;
			wealthcircleUser.status = true;
			
			if (!wealthCircleUserDao.save(wealthcircleUser)) {
				result.code = -1;
				result.msg = "邀请码使用失败";

				return result;
			}
			
			//给邀请人(邀请码持有者)发送通知 财富圈邀请成功
			t_user spreader = userService.findByID(wealthcircleUser.spreader_id);
			t_user user = userService.findByID(userId);
			Map<String, Object> param2 = new HashMap<String, Object>();
			param2.put("spreader_name", spreader.name);
			param2.put("user_name", user.name);
			noticeService.sendSysNotice(spreader.id, NoticeScene.WEALTHCIRCLE_SPREAD_SUCC, param2);
		}
		
		result.code = 1;
		result.msg = "财富圈账号创建成功";
		
		return result;
	}
	
	/**
	 * 发送财富圈邀请码
	 *
	 * @param userId 用户的id
	 * @param codeNum 赠送邀请码的数量
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月7日
	 */
	public ResultInfo sendWealthCircleCode(Long userId,int codeNum){
		ResultInfo result = new ResultInfo();
		
		String initial_amount = findSettingValueByKey(WealthCircleSettingKey.INITIAL_AMOUNT);
		int initialAmount = (Integer.parseInt(initial_amount));
		Date now = new Date();
		for (int i = 0; i < codeNum; i++) {
			t_wealthcircle_user wealthcircleUser = new t_wealthcircle_user();
			wealthcircleUser.time = now;
			wealthcircleUser.wc_code = randWcCode();
			wealthcircleUser.spreader_id = userId;
			wealthcircleUser.status = false;
			wealthcircleUser.total_invest = 0.0;
			wealthcircleUser.total_rebate = 0.0;
			wealthcircleUser.recivable_rebate = 0.0;
			if(!wealthCircleUserDao.save(wealthcircleUser)){
				result.code = -1;
				result.msg = "添加财富圈邀请码时出错";
				
				return result;
			}
			
			// 发送赠送邀请码消息 
			t_user spreader = userService.findByID(wealthcircleUser.spreader_id);
			Map<String, Object> param2 = new HashMap<String, Object>();
			param2.put("user_name", spreader.name);
			noticeService.sendSysNotice(spreader.id, NoticeScene.WEALTHCIRCLE_GET_CODE, param2);
		}
		
		if (!wealthCircleAccountDao.updateConvertAmount(userId, initialAmount * codeNum)) {
			result.code = -1;
			result.msg = "更新已经生成邀请码的理财金额失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "赠送邀请码成功";
		
		return result;
	}
	
	/**
	 * 投标时发送财富圈返佣
	 * 
	 * @param userId 被推广人的user_id
	 * @param amount 投标金额
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月16日
	 */
	public ResultInfo investGiveWcCommission(long userId,double amount) {
		ResultInfo result = new ResultInfo();
		
		//查询推广关系
		t_wealthcircle_user wcUser = wealthCircleUserDao.findByColumn(" user_id=? ", userId);
		
		if (wcUser == null) {
			result.code = 1;
			result.msg = "推广关系不存在";

			return result;
		}
		
		double rebate_invest = Double.valueOf(findSettingValueByKey(WealthCircleSettingKey.REBATE_INVEST));
		//若推广关系存在
		double investRebate  = Arith.divUp(Arith.mul(amount, rebate_invest), 1000, 2);
		
		//更新财富圈返佣
		int row2 = wealthCircleUserDao.updateWcUserRecord(userId, amount, investRebate);
		if (row2 < 1) {
			result.code = -1;
			result.msg = "投标时，返佣失败";

			return result;
		}
			
		//若理财返佣比 大于0 并且 返佣金额大于0
		if (rebate_invest > 0 && investRebate > 0) {
			// 发送通知 获得财富圈返佣
			t_user spreader = userService.findByID(wcUser.spreader_id);
			Map<String, Object> param2 = new HashMap<String, Object>();
			param2.put("spreader_name", spreader.name);
			param2.put("amount", investRebate);
			noticeService.sendSysNotice(spreader.id, NoticeScene.WEALTHCIRCLE_REBATE, param2);
		}
		
		result.code=1;
		result.msg="交易成功";
		
		return result;
	}

	
	/**
	 * 领取财富圈返佣
	 *
	 * @param wcUserId 财富圈关系id
	 * @param spreaderId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月7日
	 */
	public ResultInfo reciveWealthCircle(Long wcUserId, Long spreaderId) {
		ResultInfo result = new ResultInfo();
		
		//判断资金是否异常(资金验签)
		result = userFundService.userFundSignCheck(spreaderId);
		if (result.code < 1) {
			
			return result;
		}
		
		t_wealthcircle_user wcUser = wealthCircleUserDao.findByID(wcUserId);
		if (wcUser == null || wcUser.spreader_id.longValue() != spreaderId.longValue()) {
			result.code = 0;
			result.msg = "邀请记录不存在或者不是该用户的推广会员";
			
			return result;
		}
		
		double recivable_rebate = wcUser.recivable_rebate;

		if (recivable_rebate <= 0) {
			result.code = 0;
			result.msg = "没有可领取的返佣";

			return result;
		}
		
		//可领取返佣金额减少
		boolean flagOf = wealthCircleUserDao.rebateMinus(wcUserId,recivable_rebate);
		if (!flagOf) {
			result.code = -1;
			result.msg = "推广记录可领取金额减少失败!";
			
			return result;
		}
		
		//更新财富圈账户金额
		boolean flag2 = (wealthCircleAccountDao.wcAccountFundAdd(spreaderId, recivable_rebate)>=1);
		if (!flag2) {
			result.code = -1;
			result.msg = "用户财富圈账户资金更新失败!";
			
			return result;
		}
		
		//更新用户账户虚拟资产
		boolean flag = userFundService.userVisualFundAdd(spreaderId, recivable_rebate);
		if (!flag){
			result.code = -3;
			result.msg = "更新用户账户虚拟资产失败";

			return result;
		}
		
		//更新用户账户签名字段
		result = userFundService.userFundSignUpdate(spreaderId);
		if(result.code < 1){
			
			return result;
		}
		
		return result;
	}
	
	/**
	 * 用户财富圈返佣申请兑换
	 *
	 * @param userId 用户id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月7日
	 */
	public ResultInfo applayConversion(long userId) {
		ResultInfo result = new ResultInfo();
		//数据校验
		t_wealthcircle_account wcAccount = findAccountByUser(userId);
		if (wcAccount == null) {
			result.code = -1;
			result.msg = "查询财富圈账户失败";

			return result;
		}
		double balance = wcAccount.balance;
		if (balance <= 0) {
			result.code = -1;
			result.msg = "财富圈可兑换返佣不足";

			return result;
		}
		
		//每次兑换上限金额
		if (balance > ConfConst.MAX_CONVERSION){
			balance = ConfConst.MAX_CONVERSION;
		}
		
		//用户资金验签
		result = userFundService.userFundSignCheck(userId);
		if (result.code < 1) {
			
			return result;
		}
		
		//财富圈账户资金置减少
		if (!wealthCircleAccountDao.wcAccountMinus(userId, balance)) {
			result.code = 0;
			result.msg = "扣除用户财富圈账户余额失败";

			return result;
		}
		
		//添加兑换记录
		if (!conversionService.creatConversion(userId, balance, ConversionType.WEALTHCIRCLE)) {
			result.code = -1;
			result.msg = "生成兑换记录失败";

			return result;
		}
		
		//虚拟资产减少
		if (!userFundService.userVisualFundMinus(userId, balance)){
			result.code = -3;
			result.msg = "更新用户账户虚拟资产失败";

			return result;
		}
		
		//更新签名
		result = userFundService.userFundSignUpdate(userId);
		if (result.code < 1) {
			
			return result;
		}
		
		result.code = 1;
		result.msg = "财富圈返佣申请兑换成功";
		return result;
	}
	
	/**
	 * 更新财富圈设置
	 *
	 * @param settings key:_key,value:_value(更新后的值)
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月16日
	 */
	public boolean updateSettings(Map<String, String> settings) {
		if (settings == null || settings.keySet() == null || settings.keySet().size() == 0 ) {
			
			return false;
		}

		for (String _key : settings.keySet()) {
			t_wealthcircle_setting setting = wealthCircleSettingDao.findByColumn("_key=?", _key);
			setting._value = settings.get(_key);
			wealthCircleSettingDao.save(setting);
		}
		
		addAFlushCache();
		return true;
	}
	
	/**
	 * 判断一个财富圈邀请码是否有用(包括是否存在和是否被使用)
	 *
	 * @param WealthCircleCode
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月6日
	 */
	public ResultInfo isWealthCircleCodeUseful(String WealthCircleCode){
		ResultInfo result = new ResultInfo();
		
		t_wealthcircle_user wealthcircleUser = findWealthCircleUserByCode(WealthCircleCode);
		if (wealthcircleUser == null) {
			result.code = 0;
			result.msg = "该财富圈邀请码不存在";
			
			return result;
		}
		
		if (wealthcircleUser.status) {
			result.code = -1;
			result.msg = "该财富圈邀请码已经被使用";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "邀请码存在，且可以使用";
		
		return result;
	}
	
	/**
	 * 查询某个用户的财富圈账户
	 *
	 * @param spreaderId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月7日
	 */
	public t_wealthcircle_account findAccountByUser(Long spreaderId){
		
		return wealthCircleAccountDao.findByColumn(" user_id=? ", spreaderId);
	}
	
	/**
	 * 查询邀请码信息
	 *
	 * @param WealthCircleCode 邀请码
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月6日
	 */
	public t_wealthcircle_user findWealthCircleUserByCode(String WealthCircleCode){
		t_wealthcircle_user wealthcircleUser = wealthCircleUserDao.findByColumn(" wc_code=? ", WealthCircleCode);
		
		return wealthcircleUser;
	}
	
	
	/**
	 * 根据键名取得系统参数
	 *
	 * @param key 系统参数的键名
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月16日
	 */
	public t_wealthcircle_setting findSettingByKey(String key) {
		Map<String, t_wealthcircle_setting> optionMaps = getCache();
		if ( optionMaps == null ) {
			
			return null;
		}
		
		t_wealthcircle_setting option = optionMaps.get(key);
		
		return option;
	}

	/**
	 * 根据键名取得系统参数的值
	 *
	 * @param key 系统参数的键名
	 * @return 该系统参数的值(String 类型)
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月16日
	 */
	public String findSettingValueByKey(String key) {
		t_wealthcircle_setting option = findSettingByKey(key);
		if (option == null) {

			return null;
		}
		
		return option._value;
	}
	
	/**
	 * 查询累计投资总额 和累计推广总额
	 *
	 * @param userName 根据会员名称模糊检索
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月8日
	 */
	public Map<String, Object> findTotalWcAmount(String userName) {
		
		return wealthCircleUserDao.findTotalWcAmount(userName);
	}
	
	/**
	 * 分页查询某个用户的推广记录
	 *
	 * @param currPage
	 * @param pageSize
	 * @param spreaderId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月7日
	 */
	public PageBean<MyWealthCircleUser> pageOfMyUser(int currPage,int pageSize, Long spreaderId) {
		/*
		 SELECT 
			wu.id AS id,
			wu.time AS time,
			wu.wc_code AS wc_code,
			wu.user_id AS user_id,
			u.name AS user_name,
			wu.spreader_id AS spreader_id,
			wu.active_time AS active_time,
			wu.status AS status,
			wu.total_invest AS total_invest,
			wu.total_rebate AS total_rebate,
			wu.recivable_rebate AS recivable_rebate
		FROM t_wealthcircle_user wu 
		LEFT JOIN t_user u ON wu.user_id=u.id
		WHERE wu.spreader_id = :spreader_id
		 */
		StringBuffer countSQL = new StringBuffer(" SELECT count(wu.id) FROM t_wealthcircle_user wu ");
		StringBuffer querySQL = new StringBuffer(" SELECT wu.id AS id, wu.time AS time, wu.wc_code AS wc_code, wu.user_id AS user_id, u.name AS user_name, wu.spreader_id AS spreader_id, wu.active_time AS active_time, wu.status AS status, wu.total_invest AS total_invest, wu.total_rebate AS total_rebate, wu.recivable_rebate AS recivable_rebate FROM t_wealthcircle_user wu LEFT JOIN t_user u ON wu.user_id=u.id ");
		
		Map<String, Object> conditon = null;
		if (spreaderId != null) {
			conditon = new HashMap<String, Object>();
			countSQL.append(" WHERE wu.spreader_id = :spreader_id   ");
			querySQL.append(" WHERE wu.spreader_id = :spreader_id   ");
			conditon.put("spreader_id", spreaderId);
		}
		
		querySQL.append(" ORDER BY wu.id DESC ");
		
		PageBean<MyWealthCircleUser> page = wealthCircleUserDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), MyWealthCircleUser.class, conditon);
		return page;

	}
	
	/**
	 * 分页查询推广记录(只有财富圈邀请码被使用了才回出现在本列表中)
	 *
	 * @param currPage 当前第几页
	 * @param pageSize 每页显示的记录数
	 * @param orderType 排序类型(0:编号(会员编号);3:累计理财;4:累计返佣)
	 * @param orderValue 0:降序;1:升序
	 * @param exports 0:不导出;1:导出
	 * @param userName 根据用户名模糊查询关键字
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月8日
	 */
	public PageBean<WealthCircleRecord> pageOfWcRecords(int currPage,int pageSize,int orderType,int orderValue,int exports,String userName){
		/*
		SELECT
			wu.id AS id,
			wu.time as time,
			wu.wc_code AS wc_code,
			wu.user_id AS user_id,
			u1.name AS user_name,
			wu.spreader_id AS spreader_id,
			u2.name AS spreader_name,
			wu.active_time AS active_time,
			wu.total_invest AS total_invest,
			wu.total_rebate AS total_rebate,
			wu.recivable_rebate AS recivable_rebate
		FROM t_wealthcircle_user wu
		INNER JOIN t_user u1 ON u1.id = wu.user_id
		INNER JOIN t_user u2 ON u2.id = wu.spreader_id
		WHERE (wu.user_id IS NOT NULL) AND (wu.user_id > 0)
		*/
		
		StringBuffer countSQL = null;
		if (StringUtils.isNotBlank(userName)) {
			countSQL = new StringBuffer(" SELECT count(wu.id) FROM t_wealthcircle_user wu INNER JOIN t_user u1 ON u1.id = wu.user_id where (wu.user_id IS NOT NULL) AND (wu.user_id > 0) ");
		} else {
			//没有会员条件时，不进行连表查询
			countSQL = new StringBuffer(" select count(wu.id) from t_wealthcircle_user wu where (wu.user_id IS NOT NULL) AND (wu.user_id > 0) ");
		}
		StringBuffer querySQL = new StringBuffer(" SELECT wu.id AS id,wu.time as time, wu.wc_code AS wc_code, wu.user_id AS user_id, u1.name AS user_name, wu.spreader_id AS spreader_id, u2.name AS spreader_name, wu.active_time AS active_time, wu.total_invest AS total_invest, wu.total_rebate AS total_rebate, wu.recivable_rebate AS recivable_rebate FROM t_wealthcircle_user wu INNER JOIN t_user u1 ON u1.id = wu.user_id INNER JOIN t_user u2 ON u2.id = wu.spreader_id WHERE (wu.user_id IS NOT NULL) AND (wu.user_id > 0) ");
		
		Map<String, Object> condition = null;
		if (StringUtils.isNotBlank(userName)) {
			condition = new HashMap<String, Object>();
			countSQL.append(" AND u1.name LIKE :userName  ");
			querySQL.append(" AND u1.name LIKE :userName    ");
			condition.put("userName", "%"+userName+"%");
		}
		
		if (orderType == 3) {
			querySQL.append(" ORDER BY wu.total_invest ");// 按累计理财
		} else if (orderType == 4) {
			querySQL.append(" ORDER BY wu.total_rebate ");// 按累计返佣
		} else {
			querySQL.append(" ORDER BY wu.user_id ");// 编号(会员编号)
		}
		
		if (orderValue == 0) {
			querySQL.append(" DESC ");
		}
		
		if (exports == Constants.EXPORT) {
			PageBean<WealthCircleRecord> page = new PageBean<WealthCircleRecord>();
			page.page = wealthCircleUserDao.findListBeanBySQL(querySQL.toString(), WealthCircleRecord.class, condition);
			return page;
		}
		
		PageBean<WealthCircleRecord> page = wealthCircleUserDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), WealthCircleRecord.class, condition);
		return page;
	}
	
	/** 生成随机财富圈邀请码:YQ-xxxx-xxxx */
	private static String randWcCode(){
		StringBuffer code = new StringBuffer("YQ-");
		Random random = new Random();
		for (int i = 1; i <= 8; i++) {
			code.append(random.nextInt(10));
			if (i == 4) {
				code.append("-");
			}
		}
		
		return code.toString();
	}
	
	@Override
	public void addAFlushCache() {
		Map<String, t_wealthcircle_setting> maps = null;
		
		try {
			List<t_wealthcircle_setting> options = wealthCircleSettingDao.findAll();
			if (options != null && options.size() > 0) {
				maps = new HashMap<String, t_wealthcircle_setting>();
				for (t_wealthcircle_setting option : options) {
					maps.put(option._key, option);
				}
			}
		} catch (Exception e) {
			Logger.error(e, "%s.addCache():"+"添加缓存时出错", this.getClass().getName());
		}
		
		Cache.safeSet(this.getClass().getName(),maps,Constants.CACHE_TIME_HOURS_24);
	}

	@Override
	public Map<String, t_wealthcircle_setting> getCache() {
		Map<String, t_wealthcircle_setting> maps = (Map<String, t_wealthcircle_setting>) Cache.get(this.getClass().getName());
		if ( (maps == null) || (maps.keySet() == null) || (maps.keySet().size() == 0) ) {
			addAFlushCache();
			maps = (Map<String, t_wealthcircle_setting>) Cache.get(this.getClass().getName());
		}
		
		return maps;
	}

	@Override
	public void clearCache() {
		Cache.safeDelete(this.getClass().getName());
	}

}
