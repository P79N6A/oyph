package daos.ext.cps;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ext.cps.bean.CpsSpreadRecord;
import models.ext.cps.entity.t_cps_user;

import org.apache.commons.lang.StringUtils;

import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;

/**
 * CPS用户具体实现的dao
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月15日
 */
public class CpsUserDao extends BaseDao<t_cps_user> {

	protected CpsUserDao() {

	}
	
	/**
	 * 将某个推广记录的可领取返减少0
	 *
	 * @param cpsId 推广记录id
	 * @param recivable_rebate 减少的金额
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月16日
	 */
	public boolean rebateMinus(long cpsId,double recivable_rebate) {
		
		String sql = "UPDATE t_cps_user SET recivable_rebate=recivable_rebate-:amt WHERE id=:cpsId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("amt", recivable_rebate);
		condition.put("cpsId", cpsId);
		
		return (updateBySQL(sql,condition) == 1);
	}
	
	/**
	 * 投标时，更新cps_ser
	 *
	 * @param userId 用户user_id
	 * @param investAmt 投标金额 
	 * @param investRebate 返佣金额
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月16日
	 */
	public int updateCpsUserRecord(long userId, double investAmt, double investRebate) {
		
		String sql = "UPDATE t_cps_user SET total_invest=total_invest+:investAmt,total_rebate = total_rebate + :investRebate,recivable_rebate=recivable_rebate + :investRebate WHERE user_id= :userId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("investAmt", investAmt);
		condition.put("investRebate", investRebate);
		condition.put("userId", userId);
		
		return updateBySQL(sql,condition);
	}
	
	/**
	 * 查询累计投资总额 累计推广总额
	 *
	 * @param userName 会员昵称
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月16日
	 */
	public Map<String, Object> findTotalCpsAmount(String userName) {
		StringBuffer sql=new StringBuffer("SELECT IFNULL(SUM(tcu.total_invest),0) AS total_invest, IFNULL(SUM(tcu.total_rebate),0) AS total_rebate FROM t_cps_user tcu,t_user tu WHERE tcu.user_id = tu.id ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(userName)) {
			sql.append(" AND tu.name LIKE :userName ");
			condition.put("userName", "%" + userName + "%");
		}
		
		return  super.findMapBySQL(sql.toString(), condition);
	}
	
	/**
	 * 
	 *
	 * @param cpsActivityId cps活动id
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月21日
	 */
	public List<t_cps_user> queryUsersByActivityId(long cpsActivityId) {
		
		String sql = "SELECT * FROM t_cps_user c where c.activity_id =:cpsActivityId and c.total_invest>=2000";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpsActivityId", cpsActivityId);
		
		return this.findListBySQL(sql, params);
	}

	/**
	 * 通过userId查询t_cps_user
	 * @param userId 注册id
	 * @author guoShiJie
	 * @createDate 2018.6.13
	 * */
	public t_cps_user findByUserId(long userId) {
		StringBuffer sql = new StringBuffer("select * from t_cps_user where user_id = :userId");
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("userId", userId);
		return findBySQL(sql.toString(),condition);
	}
	
	/**
	 * 查询推广记录
	 * currPage 当前页
	 * pageSize 页数
	 * spreader_id 推广人id
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public PageBean<CpsSpreadRecord> queryCpsRecord(int currPage , int pageSize,long spreader_id) {
		StringBuffer querySql = new StringBuffer(" select cu.id as id ,a.title as activityName,u.name as user_name, cu.total_invest as total_invest, cu.time as time , cu.total_rebate as total_rebate , u.id as user_id, uu.name as spreader_name "
				+ " from t_cps_user cu "
				+ " left join t_user u on cu.user_id = u.id "
				+ " left join t_user uu on cu.spreader_id = uu.id "
				+ " inner join t_cps_activity a on cu.activity_id = a.id "
				+ " where cu.spreader_id = :spreaderId and a.is_use = 1 ");
		StringBuffer countSql = new StringBuffer(" select count(cu.id) "
				+ " from t_cps_user cu "
				+ " left join t_user u on cu.user_id = u.id "
				+ " left join t_user uu on cu.spreader_id = uu.id "
				+ " inner join t_cps_activity a on cu.activity_id = a.id"
				+ " where cu.spreader_id = :spreaderId and a.is_use = 1 ");
		querySql.append(" order by cu.id desc ");
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("spreaderId", spreader_id);
		return this.pageOfBeanBySQL(currPage, pageSize, countSql.toString(), querySql.toString(), CpsSpreadRecord.class, condition);
		
	}
}
