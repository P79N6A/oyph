package service.ext.experiencebid;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_conversion_user.ConversionType;
import models.common.entity.t_user;
import models.ext.experience.entity.t_experience_bid_account;
import services.base.BaseService;
import services.common.ConversionService;
import services.common.NoticeService;
import services.common.UserFundService;

import com.shove.Convert;
import common.constants.ConfConst;
import common.enums.NoticeScene;
import common.utils.Factory;
import common.utils.ResultInfo;

import daos.common.UserDao;
import daos.ext.experiencebid.ExperienceBidAccountDao;

public class ExperienceBidAccountService extends BaseService<t_experience_bid_account>{
	
	protected static UserDao userDao = Factory.getDao(UserDao.class);
	
	protected static ExperienceBidSettingService experienceBidSettingService = Factory.getService(ExperienceBidSettingService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static ConversionService conversionService = Factory.getService(ConversionService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	public ExperienceBidAccountDao experienceBidAccountDao;
	
	public ExperienceBidAccountService(){
		experienceBidAccountDao = Factory.getDao(ExperienceBidAccountDao.class);
		basedao = experienceBidAccountDao;
	}
	
	/**
	 * 用户注册成功，产生体验标账户
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	public ResultInfo createExperienceAccount(long userId){
		ResultInfo result = new ResultInfo();
		
		t_experience_bid_account experienceBidAccount = new t_experience_bid_account();
		
		Map<String, Object> map = experienceBidSettingService.queryExperienceBidInfo();
		if(map==null){
			result.code = 0;
			result.msg = "后台还没有设置体验标参数!";
			
			return result;
		}
		
		experienceBidAccount.time = new Date();
		experienceBidAccount.user_id = userId;
		experienceBidAccount.count = Convert.strToInt(String.valueOf(map.get("opportunityCount")), 0);
		experienceBidAccount.every_grant = Convert.strToDouble(String.valueOf(map.get("everyGrant")), 0);
		if(!experienceBidAccountDao.save(experienceBidAccount)){
			result.code = -1;
			result.msg = "体验标账户发放失败!";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "体验标账户发放成功!";
		
		return result;
	}
	
	/**
	 * 检查体验金账户当日是否可以领取
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	public ResultInfo checkCanGetGold(long userId) {
		ResultInfo result = new ResultInfo();
		
		t_experience_bid_account experienceBidAccount = experienceBidAccountDao.findUserExperienceAccount(userId);
		
		if(experienceBidAccount.count < 1 ){
			result.code = -1;
			result.msg = "没有还未领取的!";
			
			return result;
		}
		
		if(experienceBidAccount.last_receive_time!=null){
			Date today = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			
			if(df.format(today).equals(df.format(experienceBidAccount.last_receive_time))){
				result.code = -1;
				result.msg = "今天已经领取过啦!";
				
				return result;	
			}
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	/**
	 * 增加体验账户的体验金收益
	 *
	 * @param userId
	 * @param income
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public int addExperienceAccountIncome(long userId, double income) {

		return experienceBidAccountDao.addExperienceAccountIncome(userId, income);
	}

	/**
	 * 领取体验金
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	public ResultInfo getExperienceGold(long userId) {
		ResultInfo result = new ResultInfo();

		int row = experienceBidAccountDao.getExperienceGold(userId);
		if(row < 1){
			result.code = -1;
			result.msg = "领取体验金失败!";
			
			return result;
		}
		
		t_user user = userDao.findByID(userId);
		t_experience_bid_account eba = experienceBidAccountDao.findByColumn("user_id = ?", userId);
		
		/** 获得体验金 发送通知 */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		param.put("amount", eba.every_grant);
		noticeService.sendSysNotice(user.id, NoticeScene.GET_EXPER, param);
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}
	
	/**
	 * 根据用户id查询用户体验账户信息
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	public t_experience_bid_account findUserExperienceAccount(long userId){
		
		return experienceBidAccountDao.findUserExperienceAccount(userId);
	}
	
	/**
	 * 查询用户体验金余额
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月18日
	 */
	public double findUserExperienceGold(long userId) {
		return experienceBidAccountDao.findUserExperienceGold(userId);
	}
	
	/**
	 * 冻结用户体验账户的投标金额
	 *
	 * @param investAmount
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public ResultInfo FreezeUserExperienceGold(double investAmount, long userId){
		ResultInfo result = new ResultInfo();
		
		int row = experienceBidAccountDao.FreezeUserExperienceGold(investAmount, userId);
		if(row < 1){
			result.code = -1;
			result.msg = "冻结体验账户资金失败!";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "";
		
		return result;
	}

	/**
	 * 扣除体验账户的冻结体验金
	 *
	 * @param userId
	 * @param amount
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public boolean experienceUserFundMinusFreezeAmt(long userId, double amount) {
		
		int row = experienceBidAccountDao.experienceUserFundMinusFreezeAmt(userId, amount);
		if(row < 1){
			return false;
		}
		
		return true;
	}

	/**
	 * 体验金收益兑换
	 *
	 * @param id
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月23日
	 */
	public ResultInfo applayConversion(long userId) {
		ResultInfo result = new ResultInfo();
		
		t_experience_bid_account account = findUserExperienceAccount(userId);
		if (account == null) {
			result.code = -1;
			result.msg = "查询体验金账户失败";
			
			return result;
		}
		
		double conversionAmt = account.balance;

		if (conversionAmt <= 0) {
			result.code = -1;
			result.msg = "体验金收益可兑换余额不足";
			
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

		//收益金额减少
		int row = experienceBidAccountDao.minusExperienceAccountIncome(userId, conversionAmt);
		if (row < 1) {
			result.code = -1;
			result.msg = "扣除用户体验金收益金额失败";

			return result;
		}
		
		boolean flag = conversionService.creatConversion(userId, conversionAmt, ConversionType.EXPERIENCE);
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
		
		//用户账户签名校验
		result = userFundService.userFundSignUpdate(userId);
		if (result.code < 1) {
			
			return result;
		}
		
		result.code = 1;
		result.msg = "兑换申请成功，平台正在处理";
		return result;
	}
	
}
