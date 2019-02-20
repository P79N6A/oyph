package service.ext.experiencebid;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_user;
import models.ext.experience.entity.t_experience_bid;
import models.ext.experience.entity.t_experience_bid_account;
import models.ext.experience.entity.t_experience_bid_invest;
import services.base.BaseService;
import services.common.NoticeService;
import services.common.UserFundService;
import common.constants.Constants;
import common.enums.Client;
import common.enums.NoticeScene;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import common.utils.number.NumberFormat;
import daos.common.UserDao;
import daos.ext.experiencebid.ExperienceBidDao;

public class ExperienceBidService extends BaseService<t_experience_bid>{
	
	public static ExperienceBidSettingService experienceBidSettingService = Factory.getService(ExperienceBidSettingService.class);
	
	public static ExperienceBidAccountService experienceBidAccountService = Factory.getService(ExperienceBidAccountService.class);
	
	public static ExperienceBidInvestService experienceBidInvestService = Factory.getService(ExperienceBidInvestService.class);
	
	public static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	public static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	public static UserDao userDao = Factory.getDao(UserDao.class);
	
	public ExperienceBidDao experienceBidDao = null;
	
	public ExperienceBidService(){
		experienceBidDao =  Factory.getDao(ExperienceBidDao.class);
		basedao = experienceBidDao;
	}
	
	/**
	 * 创建体验标
	 *
	 * @param title 体验标标题
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月17日
	 */
	public ResultInfo createExperienceBid(String title){
		ResultInfo result = new ResultInfo();
		
		t_experience_bid experienceBid = new t_experience_bid();
		experienceBid.time = new Date();
		experienceBid.title = title;
		
		Map<String, Object> bidSetting = experienceBidSettingService.queryExperienceBidInfo();
		if(bidSetting == null || bidSetting.size() < 5){
			result.code = -1;
			result.msg = "请完善体验标设置";
			
			return result;
		}
		experienceBid.apr = Double.valueOf(bidSetting.get("apr").toString());
		experienceBid.period = Integer.valueOf(bidSetting.get("period").toString());
		experienceBid.invest_period = Integer.valueOf(bidSetting.get("raiseTime").toString());//筹款时间
		experienceBid.min_invest_amount = 1000;//固定1000
		experienceBid.repayment_type = "一次性还款";//固定一次性还款
		experienceBid.status = t_experience_bid.Status.FUNDRAISING.code;
		experienceBid.invest_count = 0;
		experienceBidDao.save(experienceBid);
		
		result.code = 1;
		result.msg = "";
		result.obj = experienceBid;
		
		return result;
	}
	
	/**
	 * 体验标投标
	 *
	 * @param investAmount
	 * @param experienceBidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public ResultInfo investExperienceBid(double investAmount, long experienceBidId, long userId){
		ResultInfo result = new ResultInfo();
		
		t_experience_bid experienceBid = experienceBidDao.findByID(experienceBidId);
		if(experienceBid == null){
			result.code = -1;
			result.msg = "非法参数!";
			
			return result;
		}
		
		//标的状态判断
		if(!t_experience_bid.Status.FUNDRAISING.equals(experienceBid.getStatus())){
			result.code = -1;
			result.msg = "这个体验标的状态已经不是借款中了!无法投标";
			
			return result;
		}
		//投标期限判断
		Date deadLineDate = DateUtil.addDay(experienceBid.time, experienceBid.invest_period);
		if(new Date().compareTo(deadLineDate)==1){
			result.code = -1;
			result.msg = "这个体验标的投标期限已经截止!无法投标";
			
			return result;
		}
		//起投金额判断
		if(investAmount < experienceBid.min_invest_amount){
			result.code = -1;
			result.msg = "这个体验标的最小投标金额为"+experienceBid.min_invest_amount+"!";
			
			return result;
		}
		
		//体验标账户状态判断
		t_experience_bid_account experienceBidAccount = experienceBidAccountService.findUserExperienceAccount(userId);
		if(experienceBidAccount == null){
			result.code = -1;
			result.msg = "体验标账户不存在!";
			
			return result;
		}
		//体验账户可用余额判断
		if(experienceBidAccount.amount < investAmount){
			result.code = -1;
			result.msg = "投标金额超出您的体验金账户余额!";
			
			return result;
		}
		//体验账户收益计算
		double income = getInvestIncome(investAmount, experienceBid.apr, experienceBid.period);
		
		//冻结体验账余额
		result = experienceBidAccountService.FreezeUserExperienceGold(investAmount, userId);
		if(result.code < 1){
			
			return result;
		}
		
		//增加体验账户投标记录
		t_experience_bid_invest experienceBidInvest = new t_experience_bid_invest();
		experienceBidInvest.time = new Date();
		experienceBidInvest.user_id = userId;
		experienceBidInvest.bid_id = experienceBidId;
		experienceBidInvest.amount = investAmount;
		experienceBidInvest.income = income;
		experienceBidInvest.setClient(Client.PC);
		boolean save = experienceBidInvestService.save(experienceBidInvest);
		if(!save){
			result.code = -1;
			result.msg = "保存体验账户投标记录失败!";
			
			return result;
		}
		
		//更新体验标的 “已投金额”和 “加入人次”
		int row = experienceBidDao.updateExperienceBid(investAmount, experienceBidId);
		if(row < 1){
			result.code = -1;
			result.msg = "更新体验标信息失败!";
			
			return result;
		}
		
		t_user user = userDao.findByID(userId);
		/** 体验标投标成功  发送通知 */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		param.put("exper_no", experienceBid.bid_no);
		param.put("exper_name", experienceBid.title);
		param.put("amount", investAmount);
		param.put("period", experienceBid.period);
		param.put("apr", experienceBid.apr);
		noticeService.sendSysNotice(user.id, NoticeScene.BUY_EXPER_BID_SUCC, param);
		
		//返回页面需要的数据
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("income", income);
		resp.put("period", experienceBid.period);
		resp.put("investAmount", investAmount);
		resp.put("amount", experienceBidAccount.amount-investAmount);
		
		result.code = 1;
		result.msg = "体验账户投标成功!";
		result.obj = resp;
		return result;
	}
	
 	/**
 	 * 体验标放款
 	 *
 	 * @param experienceBidId
 	 * @return
 	 *
 	 * @author yaoyi
 	 * @createDate 2016年2月19日
 	 */
	public ResultInfo release(long experienceBidId) {
		ResultInfo result = new ResultInfo();
		
		t_experience_bid experienceBid = experienceBidDao.findByID(experienceBidId);
		if(experienceBid == null){
			result.code = -1;
			result.msg = "参数错误!";
			
			return result;
		}
		
		//标的状态判断
		if(!t_experience_bid.Status.FUNDRAISING.equals(experienceBid.getStatus())){
			result.code = -1;
			result.msg = "标的状态已经不是借款中!";
			
			return result;
		}
		//体验标没有人投，直接将标的结束
		if(experienceBid.invest_count < 1){
			
			int end = experienceBidDao.interruptExperienceBid(experienceBidId);
			if(end < 1) {
				result.code = -1;
				result.msg = "将体验标置为[已结束]失败!";
				
				return result;
			}
			
			result.code = 99;
			result.msg = "结束体验标成功!";
			result.obj = experienceBid;
			
			return result;
		}
		
		Date repayment_time = DateUtil.addDay(new Date(), experienceBid.period);
		//更新体验标表
		int row = experienceBidDao.updateExperienceBidStatus(experienceBidId, repayment_time);
		if(row < 1){
			result.code = -1;
			result.msg = "更新标的状态失败!";
			
			return result;
		}
		
		//体验账户资金操作
		List<t_experience_bid_invest> invests = experienceBidInvestService.queryExperienceBidInvest(experienceBidId);//获得用户对这个标所投的金额
		if (invests == null || invests.size() == 0) {
			result.code = -1;
			result.msg = "获取这个标的投标记录为空!";

			return result;
		}
		//减去投资人的投标冻结金额 
		result = experienceInvestAmountProcess(invests, experienceBid);
		if(result.code < 1){
			
			return result;
		}
		
		result.code = 1;
		result.msg = "";
		result.obj = experienceBid;
		
		return result;
	}
	
	/**
	 * 扣除投资人的冻结金额
	 *
	 * @param invests
	 * @param experienceBid
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	private ResultInfo experienceInvestAmountProcess(List<t_experience_bid_invest> invests, t_experience_bid experienceBid){
		ResultInfo result = new ResultInfo();

		for(t_experience_bid_invest invest:invests){
			boolean minusFund = experienceBidAccountService.experienceUserFundMinusFreezeAmt(invest.user_id, invest.amount);
			if(!minusFund){
				result.code = -1;
				result.msg = "扣除体验账户冻结金额失败!";
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg = "";
		return result;
	}

	/**
	 * 体验标自动还款
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public ResultInfo experienceBidAutoRepayment(t_experience_bid experienceBid) {
		ResultInfo result = new ResultInfo();
		
		//检查标的状态
		if(!t_experience_bid.Status.REPAYING.equals(experienceBid.getStatus())){
			result.code = -1;
			result.msg = "体验标自动还款失败,标的状态不是[还款中]";
			
			return result;
		}
		//获取标的投资记录
		List<t_experience_bid_invest> experienceBidInvests = experienceBidInvestService.queryExperienceBidInvest(experienceBid.id);
		
		if(experienceBidInvests==null || experienceBidInvests.size()==0){
			//没有投标记录，将体验标结束掉
			int row = experienceBidDao.endExperienceBid(experienceBid.id);
			if(row < 1){
				result.code = -1;
				result.msg = "体验标自动还款失败,标的状态更新失败!";
				
				return result;
			}
			result.code = 1;
			result.msg = "";
			
			return result;
		}
		
		boolean isInvestSignCheckSuccess = true;
		//将投资表的预收益加到体验账户的收益中
		for(t_experience_bid_invest bidinvest : experienceBidInvests){

			//真实账户资金验签
	/*		result = userFundService.userFundSignCheck(bidinvest.user_id);
			if (result.code < 1) {
				
				isInvestSignCheckSuccess = false;
			}
			*/
			//增加体验账户的收益
			int add = experienceBidAccountService.addExperienceAccountIncome(bidinvest.user_id, bidinvest.income);
			if(add < 1){
				result.code = -1;
				result.msg = "增加体验账户的收益失败!";
				
				return result;
			}
			//增加真实账户的虚拟资产
			if(bidinvest.income > 0){
				boolean flag = userFundService.userVisualFundAdd(bidinvest.user_id, bidinvest.income);
				if (!flag){
					result.code = -3;
					result.msg = "用户资产更新失败";
	
					return result;
				}
			}
			
			//更新真实账户签名
			if (isInvestSignCheckSuccess) {
				userFundService.userFundSignUpdate(bidinvest.user_id);
			}
			
			t_user user = userDao.findByID(bidinvest.user_id);
			//体验标放款 发送通知
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("user_name", user.name);
			param.put("exper_no", experienceBid.bid_no);
			param.put("exper_name", experienceBid.title);
			param.put("amount", bidinvest.income);
			noticeService.sendSysNotice(user.id, NoticeScene.EXPER_SECTION, param);
		}
		
		//将体验标结束掉
		int row = experienceBidDao.endExperienceBid(experienceBid.id);
		if(row < 1){
			result.code = -1;
			result.msg = "体验标自动还款失败,标的状态更新失败!";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "体验标自动还款成功!";
		
		return result;
	}
	
	/**
	 * 查询体验标的列表
	 *
	 * @param currPage 当前页
	 * @param pageSize 页大小
	 * @param exports 1：导出 default：默认不导出
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月17日
	 */
	public PageBean<t_experience_bid> pageOfexperienceBid(int showType, int currPage, int pageSize, int exports){
		
		return experienceBidDao.pageOfexperienceBid(showType, currPage, pageSize, exports);
	}
	
	/**
	 * 检查今天需要还款的体验标
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public List<t_experience_bid> queryExperienceBidForRepayment() {
		
		return experienceBidDao.queryExperienceBidForRepayment();
	}
	
 	/**
 	 * 体验标收益计算
 	 *
 	 * @param investAmount
 	 * @param apr
 	 * @param period
 	 * @return
 	 *
 	 * @author yaoyi
 	 * @createDate 2016年2月19日
 	 */
 	public double getInvestIncome(double investAmount, double apr, int period){
 		
 		return Arith.round(Arith.mul(Arith.div(Arith.mul(investAmount, Arith.div(apr, 100, 10)), Constants.DAY_INTEREST, 10), period), 2);
 	}
 	
	/**
	 * 首页显示借款中的体验标
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月17日
	 */
	public t_experience_bid findExperienceBidFront(){
		
		return experienceBidDao.findExperienceBidFront();
	}
	
	/**
	 * 查询体验标的已投总额
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月17日
	 */
	public double findTotalGold(int showType) {
		
		return experienceBidDao.findTotalGold(showType);
	}
 	
	
	/**
	 * 体验标投标
	 *
	 * @param investAmount
	 * @param experienceBidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public ResultInfo investExperienceBid(double investAmount, long experienceBidId, long userId, Client client){
		ResultInfo result = new ResultInfo();
		
		t_experience_bid experienceBid = experienceBidDao.findByID(experienceBidId);
		if(experienceBid == null){
			result.code = -1;
			result.msg = "该体验标不存在!";
			
			return result;
		}
		
		//标的状态判断
		if(!t_experience_bid.Status.FUNDRAISING.equals(experienceBid.getStatus())){
			result.code = -1;
			result.msg = "这个体验标的状态已经不是借款中了!无法投标";
			
			return result;
		}
		//投标期限判断
		Date deadLineDate = DateUtil.addDay(experienceBid.time, experienceBid.invest_period);
		if(new Date().compareTo(deadLineDate)==1){
			result.code = -1;
			result.msg = "这个体验标的投标期限已经截止!无法投标";
			
			return result;
		}
		//起投金额判断
		if(investAmount < experienceBid.min_invest_amount){
			result.code = -1;
			result.msg = "这个体验标的最小投标金额为"+experienceBid.min_invest_amount+"!";
			
			return result;
		}
		
		//体验标账户状态判断
		t_experience_bid_account experienceBidAccount = experienceBidAccountService.findUserExperienceAccount(userId);
		if(experienceBidAccount == null){
			result.code = -1;
			result.msg = "体验标账户不存在!";
			
			return result;
		}
		//体验账户可用余额判断
		if(experienceBidAccount.amount < investAmount){
			result.code = -1;
			result.msg = "投标金额超出您的体验金账户余额!";
			
			return result;
		}
		//体验账户收益计算
		double income = getInvestIncome(investAmount, experienceBid.apr, experienceBid.period);
		
		//冻结体验账余额
		result = experienceBidAccountService.FreezeUserExperienceGold(investAmount, userId);
		if(result.code < 1){
			
			return result;
		}
		
		//增加体验账户投标记录
		t_experience_bid_invest experienceBidInvest = new t_experience_bid_invest();
		experienceBidInvest.time = new Date();
		experienceBidInvest.user_id = userId;
		experienceBidInvest.bid_id = experienceBidId;
		experienceBidInvest.amount = investAmount;
		experienceBidInvest.income = income;
		experienceBidInvest.setClient(client);
		boolean save = experienceBidInvestService.save(experienceBidInvest);
		if(!save){
			result.code = -1;
			result.msg = "保存体验账户投标记录失败!";
			
			return result;
		}
		
		//更新体验标的 “已投金额”和 “加入人次”
		int row = experienceBidDao.updateExperienceBid(investAmount, experienceBidId);
		if(row < 1){
			result.code = -1;
			result.msg = "更新体验标信息失败!";
			
			return result;
		}
		
		t_user user = userDao.findByID(userId);
		/** 体验标投标成功  发送通知 */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", user.name);
		param.put("exper_no", experienceBid.bid_no);
		param.put("exper_name", experienceBid.title);
		param.put("amount", investAmount);
		param.put("period", experienceBid.period);
		param.put("apr", experienceBid.apr);
		noticeService.sendSysNotice(user.id, NoticeScene.BUY_EXPER_BID_SUCC, param);
		
		//返回页面需要的数据
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("income", NumberFormat.round(income, 2));
		resp.put("period", experienceBid.period);
		resp.put("investAmount", NumberFormat.round(investAmount, 0));
		resp.put("amount", experienceBidAccount.amount-investAmount);
		
		result.code = 1;
		result.msg = "体验账户投标成功!";
		result.obj = resp;
		return result;
	}
}
