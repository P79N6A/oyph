package daos.common;

import java.util.HashMap;
import java.util.Map;

import models.common.entity.t_conversion_user;
import models.common.entity.t_conversion_user.ConversionStatus;
import models.common.entity.t_conversion_user.ConversionType;

import org.apache.commons.lang.StringUtils;

import daos.base.BaseDao;

/**
 * 兑换记录dao
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月16日
 */
public class ConversionDao extends BaseDao<t_conversion_user> {
	
	protected ConversionDao() {
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
		
		StringBuffer sumSQL = new StringBuffer("SELECT IFNULL(SUM(cu.amount),0) FROM t_conversion_user cu LEFT JOIN t_user u ON cu.user_id= u.id ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		
		if((status != null) || (type != null) || StringUtils.isNotBlank(userName)){
			sumSQL.append(" WHERE 1=1 ");
			
			if(status != null){
				sumSQL.append(" AND cu.status = :status ");
				condition.put("status", status.code);
			}
			if(type != null){
				sumSQL.append(" AND cu.conversion_type = :conversion_type ");
				condition.put("conversion_type", type.code);
			}
			if (StringUtils.isNotBlank(userName)) {
				sumSQL.append(" AND u.name LIKE :userName ");
				condition.put("userName", "%"+userName+"%");
			}
		}
		
		return findSingleDoubleBySQL(sumSQL.toString(),0,condition);
	}

	/**
	 * 根据兑换状态和兑换类型查询兑换的记录数
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月14日
	 */
	public int countConversions() {
		
		String sql = "SELECT COUNT(1) FROM t_conversion_user WHERE status = :status ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", ConversionStatus.APPLYING.code);
		
		return findSingleIntBySQL(sql, 0, params);
	}
	
}
