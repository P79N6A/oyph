package daos.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import models.common.bean.RechargeRecord;
import models.common.bean.WithdrawalRecord;
import models.common.entity.t_deal_user;
import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;

/**
 * 用户交易记录dao实现
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月21日
 */
public class DealUserDao extends BaseDao<t_deal_user> {

	protected DealUserDao() {}
	
	/**
	 * 查询各种交易类型 总额
	 *
	 * @param operation_type 交易类型：1-充值；2-提现；3-放款；4-还款；5-投资 
	 * @param name 会员昵称
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月19日
	 */
	public double findTransactionlTotalAmt(t_deal_user.OperationType operationtype, String name) {
		StringBuffer sql = new StringBuffer("SELECT IFNULL(SUM(tdu.amount ),0) FROM t_deal_user tdu,t_user tu WHERE tdu.user_id = tu.id AND operation_type=:operation_type ");
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("operation_type", operationtype.code);
		
		//按会员昵称模糊查询
		if(StringUtils.isNotBlank(name)){
			sql.append(" AND tu.name LIKE :name ");
			condition.put("name", "%"+name+"%");
		}
		
		return this.findSingleDoubleBySQL(sql.toString(), 0.00, condition);
	}

	/**
	 * 查询交易记录
	 * 
	 * @param pageSize 页码
	 * @param currPage 当前页
	 * @param userId 用户id
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月21日
	 *
	 */
	public PageBean<t_deal_user> pageOfDealUser(int currPage, int pageSize, long userId) {
		
		return super.pageOfByColumn(currPage, pageSize, "user_id = ? ORDER BY id DESC", userId);
	}

	/**
	 * 查询充值记录
	 * 
	 * @param pageSize
	 * @param currPage
	 * @param exports 1：导出  default:不导出
	 * @param name 会员昵称
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 *
	 */
	public PageBean<RechargeRecord> pageOfRechargeRecord(int currPage, int pageSize,int exports, String name) {
		/**
		  SELECT
			tdu.id AS id,
			tdu.order_no AS order_no,
			tu.name AS name,
			tdu.amount AS amount,
			tdu.operation_type AS operation_type,
			tdu.time AS time
		FROM
			t_deal_user tdu,
			t_user tu
		WHERE
			tdu.user_id = tu.id
		AND tdu.operation_type IN (101, 102)
		ORDER BY
			tdu.id DESC ;
		 */
		
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_deal_user tdu,t_user tu WHERE tdu.user_id = tu.id AND tdu.operation_type IN(:recharge_type) ");	
		StringBuffer querySQL = new StringBuffer("SELECT tdu.id AS id,tdu.order_no AS order_no,tu.name AS name, tdu.amount AS amount,tdu.operation_type AS operation_type,tdu.time AS time FROM t_deal_user tdu,t_user tu WHERE tdu.user_id = tu.id AND tdu.operation_type IN(:recharge_type) ");
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("recharge_type", t_deal_user.OperationType.RECHARGE_TYPE);
		
		//按会员昵称模糊查询
		if(StringUtils.isNotBlank(name)){
			querySQL.append(" AND tu.name like :name ");
			countSQL.append(" AND tu.name like :name ");
			condition.put("name", "%"+name+"%");
		}
		
		querySQL.append(" ORDER BY tdu.id DESC");
		
		//导出
		if(exports == Constants.EXPORT){
			PageBean<RechargeRecord> page = new PageBean<RechargeRecord>();
			page.page = this.findListBeanBySQL(querySQL.toString(), RechargeRecord.class, condition);
			return page;
		}
		
		return this.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), RechargeRecord.class, condition);
	}

	/**
	 * 查询统计 各种交易类型 交易记录
	 *
	 * @param pageSize
	 * @param currPage
	 * @param exports 1：导出  default：不导出
	 * @param name 会员
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 *
	 */
	public PageBean<WithdrawalRecord> pageOfWithdrawalRecordBack(int currPage, int pageSize,t_deal_user.OperationType operationtype,int exports,String name) {
		/**
		  	SELECT
				tdu.id AS id,
				tdu.order_no AS order_no,
				tu.name AS name,
				tdu.time AS time,
				tdu.amount AS amount
			FROM
				t_deal_user tdu,
				t_user tu
			WHERE
				tdu.user_id = tu.id
			AND tdu.operation_type =: operation_type
			ORDER BY
				id DESC 
		 
		 */
		
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) FROM t_deal_user tdu,t_user tu WHERE tdu.user_id = tu.id AND  tdu.operation_type =:operation_type ");		
		StringBuffer querySQL = new StringBuffer("SELECT tdu.id AS id,tdu.order_no AS order_no,tu.name AS name,tdu.time AS time , tdu.amount AS amount FROM t_deal_user tdu,t_user tu WHERE tdu.user_id = tu.id AND tdu.operation_type =:operation_type ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("operation_type", operationtype.code);
		
		//按会员昵称模糊查询
		if(StringUtils.isNotBlank(name)){
			querySQL.append(" AND tu.name like :name ");
			countSQL.append(" AND tu.name like :name ");
			condition.put("name", "%"+name+"%");
		}
		
		querySQL.append("  ORDER BY id DESC ");
		
		//导出
		if(exports == Constants.EXPORT){
			PageBean<WithdrawalRecord> pageBean = new PageBean<WithdrawalRecord>();
			pageBean.page = this.findListBeanBySQL(querySQL.toString(), WithdrawalRecord.class, condition);
			return pageBean;
		}
		
		return this.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), WithdrawalRecord.class, condition);
	}
	
	/**
	 * 查询某一时间段的充值总额
	 * 
	 * @author niu
	 * @createDate 2017.11.04
	 */
	public double findRechargeTotalAmtByTime(long userId, Date stime) {
		
		String sql = "SELECT IFNULL(SUM(tdu.amount ),0) AS amount FROM t_deal_user tdu WHERE tdu.operation_type = :operation_type AND tdu.user_id = :userId AND tdu.time >= :stime AND tdu.time <= NOW()";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("operation_type", t_deal_user.OperationType.RECHARGE.code);
		condition.put("userId", userId);
		condition.put("stime", stime);
		
		return this.findSingleDoubleBySQL(sql, 0.00, condition);
	}
	
	
}
