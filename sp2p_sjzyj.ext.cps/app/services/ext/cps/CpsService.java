package services.ext.cps;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_conversion_user.ConversionType;
import models.common.entity.t_user;
import models.ext.cps.bean.CpsAwardRecord;
import models.ext.cps.bean.CpsSpreadRecord;
import models.ext.cps.bean.CpsUser;
import models.ext.cps.entity.t_cps_account;
import models.ext.cps.entity.t_cps_setting;
import models.ext.cps.entity.t_cps_user;

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
import common.constants.ext.CpsSettingKey;
import common.enums.NoticeScene;
import common.interfaces.ICacheable;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import daos.ext.cps.CpsAccountDao;
import daos.ext.cps.CpsSettingDao;
import daos.ext.cps.CpsUserDao;

/**
 * CPSserivce
 *
 * @description 由于cps设置需要做缓存，故使用t_cps_setting
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月15日
 */
public class CpsService extends BaseService<t_cps_setting> implements ICacheable {

	protected CpsSettingDao cpsSettingDao = null;
	
	protected CpsAccountDao cpsAccountDao = Factory.getDao(CpsAccountDao.class);
	
	protected CpsUserDao cpsUserDao = Factory.getDao(CpsUserDao.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static ConversionService conversionService = Factory.getService(ConversionService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected CpsService() {
		cpsSettingDao	= Factory.getDao(CpsSettingDao.class);
		super.basedao = this.cpsSettingDao;
	}
	
	/**
	 * 创建CPS相关的数据
	 * @param mobile 邀请码
	 * @param userId 被推广人userid
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月16日
	 */
	public ResultInfo createCps(String mobile,Long userId) {
		ResultInfo result = new ResultInfo();
		
		Date now = new Date();
		//创建注册者的CPS账号[t_cps_account]
		t_cps_account cpsAccount = new t_cps_account();
		cpsAccount.time = now;
		cpsAccount.user_id = userId;
		cpsAccount.balance = 0;
		if (!cpsAccountDao.save(cpsAccount)) {
			result.code = -1;
			result.msg = "创建CPS账号失败!";
			
			return result;
		}
		
		//创建推广关系
		if (StringUtils.isNotBlank(mobile)) {
			t_user spreaderUser = userService.findByColumn(" mobile=? ", mobile);
			if (spreaderUser == null) {
				result.code = 0;
				result.msg = "推广人手机号不存在!";

				return result;
			}
			
			t_cps_account spreaderAccount = this.findCpsAccountByUserId(spreaderUser.id);
			if (spreaderAccount == null) {
				result.code = 0;
				result.msg = "推广人CPS账号不存在，无法进行推广!";

				return result;
			}
			
			//创建CPS推广关系[t_cps_user]
			double rebate_reg = Double.valueOf(this.findSettingValueByKey(CpsSettingKey.REBATE_REG));//注册返佣
			
			t_cps_user cpsUser = new t_cps_user();
			cpsUser.time = now;
			cpsUser.user_id = userId;
			cpsUser.spreader_id = spreaderUser.id;
			cpsUser.total_invest = 0;
			cpsUser.total_rebate = rebate_reg;
			cpsUser.recivable_rebate = rebate_reg;
			
			if (!cpsUserDao.save(cpsUser)) {
				result.code = -1;
				result.msg = "创建CPS推广关系失败!";
				
				return result;
			}
			
			//被推广人
			t_user user = userService.findByID(userId);
			
			//创建 推广关系   发送通知
			Map<String, Object> param1 = new HashMap<String, Object>();
			param1.put("spreader_name", spreaderUser.name);
			param1.put("user_name", user.name);
			noticeService.sendSysNotice(spreaderUser.id, NoticeScene.CPS_SPREAD_SUCC, param1);
			
			//注册返佣金额大于0时，给推广人返佣 发送通知
			if (rebate_reg > 0) {
				Map<String, Object> param2 = new HashMap<String, Object>();
				param2.put("user_name", spreaderUser.name);
				param2.put("amount", rebate_reg);
				noticeService.sendSysNotice(spreaderUser.id, NoticeScene.CPS_REBATE, param2);
			}
			
		}
		
		result.code = 1;
		result.msg = "CPS相关数据创建成功!";
		return result;
	}
	
	/**
	 * 更新cps设置
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
			t_cps_setting setting = cpsSettingDao.findByColumn("_key=?", _key);
			setting._value = settings.get(_key);
			cpsSettingDao.save(setting);
		}
		
		addAFlushCache();
		return true;
	}
	
	/**
	 * 领取某个CPS推广记录的可领取返佣
	 *
	 * @param cpsUserId 推广记录id
	 * @param spreaderId 推广会员id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月16日
	 */
	public ResultInfo reciveCps(long cpsId, long spreaderId) {
		ResultInfo result = new ResultInfo();
		
		//判断资金是否异常(资金验签)
		result = userFundService.userFundSignCheck(spreaderId);
		if (result.code < 1) {
			
			return result;
		}
		
		t_cps_user cpsUser = cpsUserDao.findByID(cpsId);
		if (cpsUser == null || cpsUser.spreader_id != spreaderId) {
			result.code = 0;
			result.msg = "推广记录不存在或者不是该用户的推广会员";
			
			return result;
		}
		
		double recivable_rebate = cpsUser.recivable_rebate;

		if (recivable_rebate <= 0) {
			result.code = 0;
			result.msg = "没有可领取的返佣";
			
			return result;
		}
		//可领取返佣减少
		boolean flagOf = cpsUserDao.rebateMinus(cpsId,recivable_rebate);
		if (!flagOf) {
			result.code = -1;
			result.msg = "推广记录可领取金额减少失败!";
			
			return result;
		}
		//更新CPS账户金额
		boolean flag2 = (cpsAccountDao.cpsAccountFundAdd(spreaderId,recivable_rebate)>=1);
		if (!flag2) {
			result.code = -1;
			result.msg = "用户CPS账户资金更新失败!";
			
			return result;
		}
		
		//更新用户账户虚拟资产
		boolean flag = userFundService.userVisualFundAdd(spreaderId, recivable_rebate);
		if (!flag) {
			result.code = -3;
			result.msg = "更新用户账户虚拟资产失败";

			return result;
		}
		
		//更新用户账户签名字段
		result = userFundService.userFundSignUpdate(spreaderId);
		if (result.code < 1) {
			
			return result;
		}
		
		return result;
	}
	
	/**
	 * 用户CPS返佣申请兑换
	 *
	 * @param userId 用户id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月16日
	 */
	public ResultInfo applayConversion(long userId) {
		ResultInfo result = new ResultInfo();
		//数据校验
		t_cps_account cpsAccount = findCpsAccountByUserId(userId);
		if (cpsAccount == null) {
			result.code = -1;
			result.msg = "查询CPS账户失败";

			return result;
		}
		double balance = cpsAccount.balance;
		if (balance <= 0) {
			result.code = -1;
			result.msg = "CPS可兑换返佣不足";

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
		
		//cps账户资金减少
		if (!cpsAccountDao.cpsAccountMinus(userId, balance)) {
			result.code = 0;
			result.msg = "扣除用户CPS账户余额失败";

			return result;
		}
		
		//添加兑换记录
		if (!conversionService.creatConversion(userId, balance, ConversionType.CPS)) {
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
		result.msg = "CPS返佣申请兑换成功";
		return result;
	}
	

	/**
	 * 投标时,更新CPS_USER里的资金(总投资金额，返佣总额，可领取)
	 * 
	 * @param userId 被推广人的user_id
	 * @param amount 投标金额
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月16日
	 */
	public ResultInfo investGiveCpsCommission(long userId,double amount) {
		ResultInfo result = new ResultInfo();
		
		//查询推广关系
		t_cps_user cpsUser = cpsUserDao.findByColumn(" user_id=? ", userId);
		
		if (cpsUser == null) {
			result.code = 1;
			result.msg = "推广关系不存在";

			return result;
		}
		
		//理财返佣千分比
		double rebate_invest = Double.valueOf(this.findSettingValueByKey(CpsSettingKey.REBATE_INVEST));
		
		//若推广关系存在，得到返佣金额
		double investRebate  = Arith.divUp(Arith.mul(amount, rebate_invest), 1000, 2);
		
		//更新cps返佣
		int row2 = cpsUserDao.updateCpsUserRecord(userId, amount, investRebate);
		if (row2 < 1) {
			result.code = -1;
			result.msg = "投标时，返佣失败";

			return result;
		}
			
		//若理财返佣比 大于0 并且 返佣金额大于0
		if (rebate_invest > 0 && investRebate > 0) {	
			//推广人
			t_user spreaderUser = userService.findByID(cpsUser.spreader_id);
			
			//给推广人返佣 发送通知
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("user_name", spreaderUser.name);
			param.put("amount", investRebate);
			noticeService.sendSysNotice(spreaderUser.id, NoticeScene.CPS_REBATE, param);
		}
		
		result.code=1;
		result.msg="交易成功";
		
		return result;
	}

	
	/**
	 * 根据用户的id查找该会员的CPS账户
	 *
	 * @param userId 会员id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月16日
	 */
	public t_cps_account findCpsAccountByUserId(Long userId){
		t_cps_account cpsAccount = cpsAccountDao.findByColumn(" user_id=? ", userId);
	
		return cpsAccount;
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
	public t_cps_setting findSettingByKey(String key) {
		Map<String, t_cps_setting> optionMaps = getCache();
		if ( optionMaps == null ) {
			
			return null;
		}
		
		t_cps_setting option = optionMaps.get(key);
		
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
		t_cps_setting option = findSettingByKey(key);
		if (option == null) {

			return null;
		}
		
		return option._value;
	}
	
	/**
	 * 查询累计投资总额 和累计推广总额
	 *
	 * @param userName 会员昵称
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月16日
	 */
	public Map<String, Object> findTotalCpsAmount(String userName) {
		
		return cpsUserDao.findTotalCpsAmount(userName);
	}
	
	
	/**
	 * 分页查询某个会员的所有推广人记录
	 *
	 * @param currPage
	 * @param pageSize
	 * @param spreaderId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月16日
	 */
	public PageBean<CpsUser> pageOfCpsusersBySpreaderId(int currPage,int pageSize,Long spreaderId){
		/**
		 SELECT
			cu.id AS id,
			cu.time AS time,
			cu.user_id AS user_id,
			u.name AS userName,
			cu.spreader_id AS spreader_id,
			cu.total_rebate AS total_rebate,
			cu.recivable_rebate AS recivable_rebate
		 FROM
			t_cps_user cu
		 INNER JOIN t_user u ON cu.user_id = u.id;
		 */
		StringBuffer countSQL = new StringBuffer("SELECT count(1) FROM t_cps_user cu ");
		StringBuffer querySQL = new StringBuffer("SELECT cu.id AS id,cu.time as time,cu.user_id AS user_id, u.name AS userName, cu.spreader_id AS spreader_id, cu.total_rebate AS total_rebate, cu.recivable_rebate AS recivable_rebate FROM t_cps_user cu INNER JOIN t_user u ON cu.user_id = u.id ");
		
		Map<String, Object> conditon = new HashMap<String, Object>();
		if (spreaderId != null) {
			conditon = new HashMap<String, Object>();
			countSQL.append(" WHERE cu.spreader_id=:spreader_id  ");
			querySQL.append(" WHERE cu.spreader_id=:spreader_id  ");
			conditon.put("spreader_id", spreaderId);
		}
		
		querySQL.append(" ORDER BY cu.id DESC ");
		
		PageBean<CpsUser> page = cpsUserDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), CpsUser.class, conditon);
		return page;
	}
	
	/**
	 * 查询cps推广记录
	 *
	 * @param currPage 当前页 
	 * @param pageSize 每页条数
	 * @param userName 会员(被推广人)
	 * @param exports 1：导出  default：不导出
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月16日
	 */
	public PageBean<CpsSpreadRecord> pageOfCpsSpreadRecord(int currPage,int pageSize,String userName, int exports){
			/**
		 	SELECT
				tcu.id AS id,
				tu.id AS user_id,
				tu.name AS user_name,
				tu2.name AS spreader_name,
				tcu.total_invest AS total_invest,
				tcu.total_rebate AS total_rebate,
				tcu.time AS time
			FROM
				t_cps_user tcu
				LEFT JOIN t_user tu ON tcu.user_id = tu.id
				LEFT JOIN t_user tu2 ON tcu.spreader_id = tu2.id
				inner join t_cps_activity c on tcu.activity_id = c.id;
		 */
		
		StringBuffer querySQL = new StringBuffer("SELECT tcu.id AS id,tu.id AS user_id, tu.name AS user_name,tu2.name AS spreader_name,tcu.total_invest AS total_invest,tcu.total_rebate AS total_rebate,tcu.time AS time FROM t_cps_user tcu LEFT JOIN t_user tu ON tcu.user_id = tu.id LEFT JOIN t_user tu2 ON tcu.spreader_id = tu2.id inner join t_cps_activity c on tcu.activity_id = c.id WHERE 1=1 and c.is_use=1 ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_cps_user tcu LEFT JOIN t_user tu ON tcu.user_id = tu.id LEFT JOIN t_user tu2 ON tcu.spreader_id = tu2.id inner join t_cps_activity c on tcu.activity_id = c.id  WHERE 1=1  and c.is_use=1 ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		//会员模糊查询
		if(StringUtils.isNotBlank(userName)){
			querySQL.append(" AND tu.name LIKE :userName ");
			countSQL.append(" AND tu.name LIKE :userName ");
			condition.put("userName", "%"+userName+"%");
		}
		
		querySQL.append(" ORDER BY id DESC ");
		
		// 导出
		if (exports == Constants.EXPORT) {
			PageBean<CpsSpreadRecord> page = new PageBean<CpsSpreadRecord>();
			page.page = cpsUserDao.findListBeanBySQL(querySQL.toString(), CpsSpreadRecord.class, condition);
			return page;
		}
		
		return cpsUserDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), CpsSpreadRecord.class, condition);	
			
	}
	
	@Override
	public void addAFlushCache() {
		Map<String, t_cps_setting> maps = null;
		
		try {
			List<t_cps_setting> options = cpsSettingDao.findAll();
			if(options != null && options.size()>0){
				maps = new HashMap<String, t_cps_setting>();
				for(t_cps_setting option : options){
					maps.put(option._key, option);
				}
			}
		} catch (Exception e) {
			Logger.error(e, "%s.addCache():"+"添加缓存时出错", this.getClass().getName());
		}
		
		Cache.safeSet(this.getClass().getName(),maps,Constants.CACHE_TIME_HOURS_24);
	}

	@Override
	public Map<String, t_cps_setting> getCache() {
		Map<String, t_cps_setting> maps = (Map<String, t_cps_setting>) Cache.get(this.getClass().getName());
		if ( (maps == null) || (maps.keySet() == null) || (maps.keySet().size() == 0) ) {
			addAFlushCache();
			maps = (Map<String, t_cps_setting>) Cache.get(this.getClass().getName());
		}
		
		return maps;
	}

	@Override
	public void clearCache() {
		Cache.safeDelete(this.getClass().getName());
	}
	
	/**
	 * 查询cps推广活动奖项结算
	 *
	 * @param cpsActivityId cps活动id 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月21日
	 */
	public List<CpsAwardRecord> queryCpsAwardRecords(long cpsActivityId){
		
		String querySQL = "SELECT tu2.id AS id,count(*) AS spread_num FROM t_cps_user tcu LEFT JOIN t_user tu2 ON tcu.spreader_id = tu2.id where tcu.total_invest>=2000 and tcu.activity_id=:cpsActivityId group by tu2.id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("cpsActivityId", cpsActivityId);
		
		return cpsUserDao.findListBeanBySQL(querySQL, CpsAwardRecord.class, condition);	
			
	}
}
