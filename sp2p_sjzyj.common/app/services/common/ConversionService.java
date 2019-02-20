package services.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.common.bean.ConversionUser;
import models.common.entity.t_conversion_user;
import models.common.entity.t_conversion_user.ConversionStatus;
import models.common.entity.t_conversion_user.ConversionType;
import models.common.entity.t_deal_platform;
import models.common.entity.t_deal_user;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_user_fund;

import org.apache.commons.lang.StringUtils;

import services.base.BaseService;

import common.constants.Constants;
import common.enums.NoticeScene;
import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;

import daos.common.ConversionDao;

/**
 * 兑换service
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月16日
 */
public class ConversionService extends BaseService<t_conversion_user> {

	protected ConversionDao conversionDao;
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class); 
	
	protected static DealUserService dealUserService = Factory.getService(DealUserService.class); 
	
	protected static DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class); 
	
	protected static SupervisorService supervisorService = Factory.getService(SupervisorService.class); 
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected ConversionService() {
		conversionDao = Factory.getDao(ConversionDao.class);
		super.basedao = conversionDao;
	}
	
	/**
	 * 添加兑换记录
	 *
	 * @param userId 申请兑换的用户的id
	 * @param amount 申请兑换的金额
	 * @param conversionType 申请兑换的类型
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月16日
	 */
	public boolean creatConversion(long userId, double amount,ConversionType conversionType) {

		t_conversion_user conversion_user = new t_conversion_user();
		conversion_user.time = new Date();
		conversion_user.user_id = userId;
		conversion_user.amount = amount;
		conversion_user.setConversion_type(conversionType);
		conversion_user.setStatus(ConversionStatus.APPLYING);
		
		return conversionDao.save(conversion_user);
	}
	
	/**
	 * 兑换（准备）
	 *
	 * @param conversion
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月22日
	 */
	public ResultInfo conversion(t_conversion_user conversion, long supervisorId) {
		ResultInfo result = new ResultInfo();
		
		if(!ConversionStatus.APPLYING.equals(conversion.getStatus())){
			result.code = -1;
			result.msg = "状态非法!";
			
			return result;
		}
		
		result = userFundService.userFundSignCheck(conversion.user_id);
		if (result.code < 1) {
			
			return result;
		}
		
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("conversion_id", conversion.id+"");
		boolean addEvent = supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.CONVERSION, supervisorEventParam);
		if (!addEvent) {
			result.code = -1;
			result.msg = "添加管理员事件失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "兑换准备完毕";
		
		return result;
	}
	
	/**
	 * 兑换（执行）
	 *
	 * @param conversionId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月16日
	 */
	public ResultInfo doConversion(String serviceOrderNo, long conversionId) {
		ResultInfo result = new ResultInfo();
		
		t_conversion_user conversion = conversionDao.findByID(conversionId);
		if (conversion == null) {
			result.code = -1;
			result.msg = "没有该兑换记录";
			
			return result;
		}
		if(!ConversionStatus.APPLYING.equals(conversion.getStatus())){
			result.code = -1;
			result.msg = "已经兑换完成!";
			
			return result;
		}
		conversion.audit_time = new Date();
		conversion.setStatus(ConversionStatus.RECEIVED);
		if (!conversionDao.save(conversion)) {
			result.code = -1;
			result.msg = "数据更新失败";
			
			return result;
		}
		
		boolean fundAdd = userFundService.userFundAdd(conversion.user_id, conversion.amount);
		if (!fundAdd) {
			result.code = -1;
			result.msg = "增加用户可用余额失败";
			
			return result;
		}
		
		//签名更新
		result = userFundService.userFundSignUpdate(conversion.user_id);
		if (result.code < 1) {
			
			return result;
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(conversion.user_id);
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("conversion_type", conversion.getConversion_type().value);
		boolean addDeal = dealUserService.addDealUserInfo(serviceOrderNo,
				conversion.user_id,
				conversion.amount, 
				userFund.balance, 
				userFund.freeze, 
				t_deal_user.OperationType.CONVERSION, summaryParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加交易记录失败";
			
			return result;
		}
		
		addDeal = dealPlatformService.addPlatformDeal(conversion.user_id, conversion.amount, 
				t_deal_platform.DealRemark.CONVERSION, null);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加平台收支记录失败";
			
			return result;
		}
		
		/**  平台奖励兑换成功 发送通知 */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", userFund.name);
		param.put("amount", conversion.amount);
		param.put("balance", userFund.balance);
		noticeService.sendSysNotice(userFund.user_id, NoticeScene.EXCHANGE_SUCC, param);
		
		result.code = 1;
		result.msg = "兑换成功";
		
		return result;
	}

	/**
	 * 根据兑换状态和兑换类型查询兑换总金额数(用户名称关键字进行模糊查询)
	 *
	 * @param status 兑换状态
	 * @param type 兑换类型
	 * @param userName 用户名称关键字
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月17日
	 */
	public double sumAmtByStatusAType(ConversionStatus status,ConversionType type,String userName) {
		
		return conversionDao.sumAmtByStatusAType(status, type,userName);
	}
	
	/**
	 * 根据兑换状态和兑换类型查询兑换的记录数
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月14日
	 */
	public int countConversions(){
		return conversionDao.countConversions();
	}
	
	/**
	 * 后台-财务-奖励兑换列表
	 *
	 * @param currPage 当前第几页
	 * @param pageSize 每页显示页数
	 * @param status 兑换状态
	 * @param type 兑换类型
	 * @param userName 用户的名称(模糊查询)
	 * @exports 1:导出  default：其它
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月17日
	 */
	public PageBean<ConversionUser> pageOfByStatusAType(int currPage, int pageSize,ConversionStatus status, ConversionType type,String userName,int exports){
		StringBuffer querySQL = new StringBuffer("SELECT cu.id AS id, cu.time AS time, cu.user_id AS user_id, cu.conversion_type AS conversion_type, cu.amount AS amount, cu.audit_time AS audit_time, cu.status AS status, u.name AS userName FROM t_conversion_user cu LEFT JOIN t_user u ON u.id = cu.user_id ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_conversion_user cu LEFT JOIN t_user u ON u.id = cu.user_id  ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		if((status != null) || (type != null) || StringUtils.isNotBlank(userName)){
			querySQL.append(" WHERE 1=1 ");
			countSQL.append(" WHERE 1=1 ");
			
			if(status != null){
				querySQL.append(" AND cu.status = :status ");
				countSQL.append(" AND cu.status = :status ");
				condition.put("status", status.code);
			}
			if(type != null){
				querySQL.append(" AND cu.conversion_type = :conversion_type ");
				countSQL.append(" AND cu.conversion_type = :conversion_type ");
				condition.put("conversion_type", type.code);
			}
			if(StringUtils.isNotBlank(userName)){
				querySQL.append(" AND u.name LIKE :userName ");
				countSQL.append(" AND u.name LIKE :userName ");
				condition.put("userName", "%"+userName+"%");
			}
		}
		
		querySQL.append(" ORDER BY id DESC");
		
		if(exports == Constants.EXPORT){
			PageBean<ConversionUser> page = new PageBean<ConversionUser>();
			page.page = conversionDao.findListBeanBySQL(querySQL.toString(), ConversionUser.class, condition);
			
			return page ;
		} 
	
		return conversionDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(),ConversionUser.class, condition);
	}
	
	/**
	 * 前台-兑换记录
	 *
	 * @param currPage 当前第几页
	 * @param pageSize 每页显示页数
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月17日
	 */
	public PageBean<ConversionUser> pageOfConversionRecord(int currPage, int pageSize, long userId){
		StringBuffer querySQL = new StringBuffer("SELECT cu.*,u.name as userName FROM t_conversion_user cu LEFT JOIN t_user u ON u.id = cu.user_id WHERE cu.user_id = :userId");
		StringBuffer countSQL = new StringBuffer("SELECT count(cu.id) FROM t_conversion_user cu WHERE cu.user_id = :userId");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		
		querySQL.append(" ORDER BY id DESC");
		
		PageBean<ConversionUser> page = conversionDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(),ConversionUser.class, condition);
		
		return page;
	}

	
	
}
