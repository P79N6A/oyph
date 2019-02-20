package services.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.common.bean.DealPlatform;
import models.common.entity.t_deal_platform;
import models.common.entity.t_deal_platform.DealOption;
import models.common.entity.t_deal_platform.DealType;

import org.apache.commons.lang.StringUtils;

import services.base.BaseService;

import common.constants.Constants;
import common.utils.Factory;
import common.utils.PageBean;

import daos.common.DealPlatformDao;

/**
 * 平台交易记录的service的具体实现
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月22日
 */
public class DealPlatformService extends BaseService<t_deal_platform> {

	protected DealPlatformDao dealPlatformDao = null;
	
	protected DealPlatformService() {
		this.dealPlatformDao = Factory.getDao(DealPlatformDao.class);
		
		super.basedao = this.dealPlatformDao;
	}

	/**
	 * 添加平台交易记录
	 *
	 * @param userId 用户id
	 * @param amount 金额
	 * @param dealRemark 平台收支类型备注(包括收支类型，操作类型和描述信息)
	 * @param dealRemarkParam dealRemark对应的参数
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月23日
	 */
	public boolean addPlatformDeal(long userId, double amount, t_deal_platform.DealRemark dealRemark, Map<String, Object> dealRemarkParam) {
		t_deal_platform deal = new t_deal_platform();
		deal.time = new Date();
		deal.user_id = userId;
		deal.amount = amount;
		deal.setRemark(dealRemark, dealRemarkParam);
		
		return dealPlatformDao.save(deal);
	}

	/**
	 * 根据操作类型分页查找所有的平台交易记录(根据用户名关键字进行模糊查询)
	 *
	 * @param currPage 当前第几页
	 * @param pageSize 每页显示的数据量
	 * @param dealOption 操作类型(当操作类型为null时表示查询所有的)
	 * @param dealType 收支类型
	 * @param userName 用户名关键字
	 * @param exports 1:导出  default：不导出
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月23日
	 */
	public PageBean<DealPlatform> pageOfDealsByOption(int currPage, int pageSize, t_deal_platform.DealOption dealOption, t_deal_platform.DealType dealType,String userName,int exports) {
		
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_deal_platform tdp LEFT JOIN t_user tu ON tdp.user_id = tu.id WHERE 1=1");
		StringBuffer querySQL = new StringBuffer("SELECT tdp.id AS id,  tdp.time AS time ,tdp.operation AS operation,  tdp.user_id AS user_id,  tdp.amount AS amount,  tdp.type AS type, tdp.remark AS remark,tu.name AS name FROM t_deal_platform tdp LEFT JOIN t_user tu ON tdp.user_id = tu.id WHERE 1=1");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		if(dealOption != null){
			countSQL.append(" AND operation=:operation ");
			querySQL.append(" AND operation=:operation ");
			condition.put("operation", dealOption.code);
		}
		if(dealType != null){
			countSQL.append(" AND type=:dealType ");
			querySQL.append(" AND type=:dealType ");
			condition.put("dealType", dealType.code);
		}
		if(StringUtils.isNotBlank(userName)){
			countSQL.append(" AND tu.name LIKE :userName ");
			querySQL.append(" AND tu.name LIKE :userName ");
			condition.put("userName", "%"+userName+"%");
		}
		querySQL.append(" ORDER BY id DESC");
		
		if(exports == Constants.EXPORT){
			PageBean<DealPlatform> page = new PageBean<DealPlatform>();
			page.page = dealPlatformDao.findListBeanBySQL(querySQL.toString(), DealPlatform.class, condition);
			return page;
		}
		
		return dealPlatformDao.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), DealPlatform.class,condition);
		
	}

	/**
	 * 根据交易类型 查询交易 收支总额(根据用户名关键字进行模糊查询)
	 *
	 * @param dealOption 1：平台奖励 2：闪电充值 3：充值手续费  4：提现手续费  5：本息垫付 6：逾期罚息  7：线下收款 8:理财服务费  9:借款服务费   10:转让服务费 
	 * @param dealType 1.收入  2.支出
	 * @param userName 用户名关键字
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月19日
	 */
	public double findDealPlatformTotalAmt(DealOption dealOption,DealType dealType,String userName) {
		double totalAmt=0.00;
		
		//查询收支合计  总计=收入-支出
		if(dealType == null){
			//收入总额 
			double incomeAmt = dealPlatformDao.findDealPlatformTotalAmt(dealOption, DealType.INCOME,userName);
	
			//支出总额
			double expensesAmt = dealPlatformDao.findDealPlatformTotalAmt(dealOption, DealType.EXPENSES,userName);
			
			totalAmt = incomeAmt - expensesAmt;
		}
		else if(DealType.INCOME.equals(dealType)){
			totalAmt = dealPlatformDao.findDealPlatformTotalAmt(dealOption, dealType,userName);
		}
		else if(DealType.EXPENSES.equals(dealType)){
			totalAmt = dealPlatformDao.findDealPlatformTotalAmt(dealOption, dealType,userName)*(-1);
		}
		
		return totalAmt;
	}

}
