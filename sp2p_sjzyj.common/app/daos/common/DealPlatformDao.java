package daos.common;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_deal_platform;
import models.common.entity.t_deal_platform.DealOption;
import models.common.entity.t_deal_platform.DealType;

import org.apache.commons.lang.StringUtils;

import daos.base.BaseDao;


/**
 * 平台交易记录DAO接口
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月22日
 */
public class DealPlatformDao extends BaseDao<t_deal_platform> {

	protected DealPlatformDao() {
	}

	/**
	 * 根据交易类型 查询交易 收支总额 (根据用户名关键字进行模糊查询)
	 *
	 * @param dealOption 1：平台奖励 2：闪电充值 3：充值手续费  4：提现手续费  5：本息垫付 6：逾期罚息  7：线下收款 8:理财服务费  9:借款服务费   10:转让服务费 
	 * @param dealType 1:收入  2:支出
	 * @param userName 会员昵称关键字
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月19日
	 */
	public double findDealPlatformTotalAmt(DealOption dealOption,DealType dealType,String userName) {
		StringBuffer sql=new StringBuffer("SELECT IFNULL(SUM(amount),0) FROM t_deal_platform tdp ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		if(StringUtils.isNotBlank(userName)){
			sql.append(" LEFT JOIN t_user u ON tdp.user_id=u.id ")
			.append(" WHERE tdp.type=:type ")
			.append(" AND u.name LIKE :userName ");
			
			condition.put("type", dealType.code);
			condition.put("userName", "%"+userName+"%");
		}else {
			sql.append(" WHERE tdp.type=:type ");
			condition.put("type", dealType.code);
		}
		
		if(dealOption!= null){
			sql.append(" AND tdp.operation=:operation");
			condition.put("operation", dealOption.code);
		}
		return this.findSingleDoubleBySQL(sql.toString(), 0.00, condition);
	}
	
}
