package daos.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import models.common.entity.t_template_notice;
import models.common.entity.t_transfer_notice;
import models.core.bean.BackDebtTransfer;
import models.core.bean.DebtInvest;
import models.core.bean.DebtTransferDetail;
import models.core.entity.t_debt_transfer;

import org.apache.commons.lang3.StringUtils;

import play.db.jpa.JPA;
import common.constants.Constants;
import common.utils.PageBean;
import daos.base.BaseDao;

/**
 * 债权dao
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年3月30日
 */
public class DebtTransferDao extends BaseDao<t_debt_transfer> {

	protected DebtTransferDao() {

	}
	
	/**
	 * 债权审核
	 * 
	 * @param start_time 审核通过时间(竞拍开始时间)
	 * @param end_time 竞拍结束时间
	 * @param status 审核状态
	 * @param audit_supervisor_id 审核管理员ID
	 * @param id 债权转让id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月25日
	 *
	 */
	public int updateDebtTransferPass(Date start_time,Date end_time,int status,long audit_supervisor_id,long id){
		String sql = "UPDATE t_debt_transfer SET start_time=:start_time ,end_time =:end_time,status=:status,audit_supervisor_id =:audit_supervisor_id WHERE id=:id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("start_time", start_time);
		condition.put("end_time", end_time);
		condition.put("status", status);
		condition.put("audit_supervisor_id", audit_supervisor_id);
		condition.put("id", id);
		
		return super.updateBySQL(sql, condition);
	}
	
	/**
	 * 根据id，更新转让项目的转让状态
	 * 
	 * @param id 转让项目的id
	 * @param status 转让项目的状态(枚举类型)
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月25日
	 *
	 */
	public int updateDebtTransfer(long id,t_debt_transfer.Status status){
		String sql = "UPDATE t_debt_transfer SET status=:status WHERE id=:id";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("status", status.code);
		condition.put("id", id);
		
		return super.updateBySQL(sql, condition);
	}

	/**
	 * 根据债权id查询债权的详细信息
	 *
	 * @param debtId 债权id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public DebtTransferDetail findDebtDetailById(long debtId) {
		
		/*
		 SELECT 
				dt.id as id,
				dt.time as time,
				dt.start_time as start_time,
				dt.end_time as end_time,
				dt.invest_id as invest_id,
				b.id as bid_id,
				dt.user_id as user_id,
				b.user_id as loan_user_id,
				u2.name as user_name,
				dt.title as title,
				dt.debt_amount as debt_amount,
				dt.debt_principal as debt_principal,
				dt.transfer_price as transfer_price,
				dt.transfer_period as period,
				dt.status as status,
				(SELECT bi.repayment_time FROM t_bill bi WHERE bi.bid_id=i.bid_id AND bi.status in (0,1) ORDER BY bi.repayment_time LIMIT 1) AS receive_time,
				b.time AS bid_time
			FROM 
				t_debt_transfer dt
				INNER JOIN t_invest i ON dt.invest_id = i.id
				INNER JOIN t_bid b ON b.id = i.bid_id
				INNER JOIN t_user u ON b.user_id = u.id
				INNER JOIN t_user u2 ON u2.id = dt.user_id
			WHERE dt.id=:debutID
		 */
		StringBuffer querySQL = new StringBuffer(" SELECT  dt.id as id, dt.time as time,dt.start_time as start_time,dt.end_time as end_time, dt.invest_id as invest_id, b.id as bid_id, dt.user_id as user_id, b.user_id as loan_user_id, u2.name as user_name, dt.title as title, dt.debt_amount as debt_amount, dt.debt_principal as debt_principal, dt.transfer_price as transfer_price, dt.transfer_period as period, dt.status as status, (SELECT bi.repayment_time FROM t_bill bi WHERE bi.bid_id=i.bid_id AND bi.status in (0,1) ORDER BY bi.repayment_time LIMIT 1) AS receive_time,  b.time AS bid_time FROM t_debt_transfer dt INNER JOIN t_invest i ON dt.invest_id = i.id INNER JOIN t_bid b ON b.id = i.bid_id INNER JOIN t_user u ON b.user_id = u.id INNER JOIN t_user u2 ON u2.id = dt.user_id WHERE  dt.id=:debutID ");
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("debutID", debtId);
		
		return findBeanBySQL(querySQL.toString(), DebtTransferDetail.class, condition);
	}
	
	/**
	 * 查询债权总额
	 * 
	 * @param showType 0-所有   1-待审核  2-转让中 3-成功   4-失败(审核不通过,过期,失效)
	 * @param transferName 转让人姓名
	 * @param numNo 编号
	 * @param projectName 项目
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月28日
	 *
	 */
	public double findTotalDebtAmount(int showType, String transferName, String numNo, String projectName) {
		StringBuffer sql = new StringBuffer("SELECT IFNULL(SUM(tdt.debt_amount),0) FROM t_debt_transfer tdt, t_user tu WHERE tdt.user_id = tu.id ");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		switch (showType) {
			case 1:
				sql.append(" AND status=:status ");
				condition.put("status", t_debt_transfer.Status.PREAUDITING.code);
				break;
			
			case 2:
				sql.append(" AND status=:status ");
				condition.put("status", t_debt_transfer.Status.AUCTING.code);
				break;
				
			case 3:
				sql.append(" AND status=:status ");
				condition.put("status", t_debt_transfer.Status.SUCC.code);
				break;
			
			case 4:
				sql.append(" AND (status=:status1 OR status=:status2 OR status=:status3) ");
				condition.put("status1", t_debt_transfer.Status.AUDIT_NOT_THROUGH.code);
				condition.put("status2", t_debt_transfer.Status.FAILED.code);
				condition.put("status3", t_debt_transfer.Status.INVALID.code);
				break;
		}
		
		/* 按转让人 模糊查询 */
		if(StringUtils.isNotBlank(transferName)){
			sql.append(" AND tu.name LIKE :name");
			condition.put("name", "%"+transferName+"%");
		}
		
		/* 按编号  模糊查询 */
		if(StringUtils.isNotBlank(numNo)){
			sql.append(" AND tdt.id LIKE :id");
			condition.put("id", "%"+numNo+"%");
		}
		
		/* 按项目名称 模糊查询 */
		if(StringUtils.isNotBlank(projectName)){
			sql.append(" AND tdt.title LIKE :title");
			condition.put("title", "%"+projectName+"%");
		}
		
		return super.findSingleDoubleBySQL(sql.toString(), 0.00, condition);
	}
	
	/**
	 * 根据investid查找债权相关信息
	 *
	 * @param investId 投资id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public DebtInvest findDebtByInvestid(Long investId) {
		/*
		 	select 
				i.id as id,
				i.bid_id as bid_id,
				i.user_id as user_id,
				b.title as title,
				(select IFNULL(sum(bi.receive_interest+bi.receive_corpus),0) from t_bill_invest bi where bi.invest_id=i.id and bi.status = 0) as debt_amount,
				(select IFNULL(sum(bi.receive_corpus),0) from t_bill_invest bi where bi.invest_id=i.id and bi.status = 0) as debt_principal,
				(select IFNULL(sum(bi.receive_interest),0) from t_bill_invest bi where bi.invest_id=i.id and bi.status = 0) as debt_interest,
				(select IFNULL(count(id),0) from t_bill_invest bi where bi.invest_id=i.id and bi.status = 0) as period
			from t_invest i
				INNER JOIN t_bid b ON i.bid_id = b.id
		 */
		String querySQL = "SELECT i.id as id,i.bid_id AS bid_id,i.user_id AS user_id,b.title AS title, (SELECT IFNULL(SUM(bi.receive_interest+bi.receive_corpus),0) FROM t_bill_invest bi WHERE bi.invest_id=i.id AND bi.status = 0) AS debt_amount, (SELECT IFNULL(SUM(bi.receive_corpus),0) FROM t_bill_invest bi WHERE bi.invest_id=i.id AND bi.status = 0) AS debt_principal, (SELECT IFNULL(SUM(bi.receive_interest),0) FROM t_bill_invest bi WHERE bi.invest_id=i.id AND bi.status = 0) as debt_interest,(SELECT IFNULL(COUNT(id),0) FROM t_bill_invest bi WHERE bi.invest_id=i.id AND bi.status = 0) AS period FROM t_invest i INNER JOIN t_bid b ON i.bid_id = b.id where i.id=:investId";
		
		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("investId", investId);
		
		return findBeanBySQL(querySQL, DebtInvest.class, condition);
	}
	
	/**
	 * 根据投资id查询转让流程未走完的 债权转让项目
	 * 
	 * @param investId 投资id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月25日
	 *
	 */
	public t_debt_transfer findDebtTransferNo(long investId){
		String sql="SELECT * FROM t_debt_transfer WHERE invest_id = :investId AND status in (:status)";
		
		Map<String, Object> condtion = new HashMap<String, Object>();
		condtion.put("investId", investId);
		condtion.put("status", t_debt_transfer.Status.NO_TRANSFER_FINISH);
		
		return super.findBySQL(sql, condtion);
	}
	

	/**
	 * 查询到期后，债权转让未走完的转让项目
	 * 
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月28日
	 *
	 */
	public List<t_debt_transfer> queryDebtTransfer(){
		String sql="SELECT * FROM t_debt_transfer WHERE status in (:status) AND end_time < :nowTime ";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("status", t_debt_transfer.Status.NO_TRANSFER_FINISH);
		condition.put("nowTime", new Date());
		
		return super.findListBySQL(sql, condition);
	}
	
	
	/**
	 * 查询债权转让项目
	 * 
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param showType 0-所有   1-待审核  2-转让中 3-成功   4-失败(审核不通过,过期,失效)
	 * @param transferName 转让人姓名
	 * @param orderType 排序栏目  0：编号 2：债权总额   3：转让期数  4：竞拍底价
	 * @param orderValue 排序规则 0,降序，1,升序
	 * @param exports 1：导出   default：不导出
	 * @param numNo 编号
	 * @param projectName 项目
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月25日
	 *
	 */
	public PageBean<BackDebtTransfer> pageOfDebtTransferBack(int showType, int currPage, int pageSize,String transferName,int orderType,int orderValue,int exports,String numNo,String projectName){
		/**
		 SELECT
			tdt.id AS id,
			tdt.title AS title,
			tu.NAME AS transfer_name,
			tdt.debt_amount AS debt_amount,
			tdt.transfer_period AS transfer_period,
			tdt.transfer_price AS transfer_price,
		  	tdt.transaction_time AS transaction_time,
			t.name AS transaction_name,
			tdt.status AS status
		FROM
			t_debt_transfer tdt
		INNER JOIN t_user tu
		ON tdt.user_id = tu.id
		LEFT JOIN t_user t ON t.id = tdt.transaction_user_id
		WHERE 1=1 
		 */
		
//		StringBuffer querySQL =new StringBuffer("SELECT tdt.id AS id, tdt.title AS title, tu.NAME AS transfer_name, tdt.debt_amount AS debt_amount, tdt.transfer_period AS transfer_period, tdt.transfer_price AS transfer_price, tdt.transaction_time AS transaction_time, t.name AS transaction_name, tdt.status AS status FROM t_debt_transfer tdt INNER JOIN t_user tu ON tdt.user_id = tu.id LEFT JOIN t_user t ON t.id = tdt.transaction_user_id WHERE 1=1 ");
		StringBuffer querySQL =new StringBuffer("SELECT tdt.id AS id, tdt.title AS title, tu.NAME AS transfer_name, tdt.debt_amount AS debt_amount, tdt.transfer_period AS transfer_period, tdt.transfer_price AS transfer_price, tdt.transaction_time AS transaction_time, t.name AS transaction_name, tdt.status AS status,tp.contract_id AS contract_id FROM t_debt_transfer tdt INNER JOIN t_user tu ON tdt.user_id = tu.id LEFT JOIN t_user t ON t.id = tdt.transaction_user_id LEFT JOIN t_pact tp ON tp.pid = tdt.id WHERE 1=1 ");
		StringBuffer countSQL =new StringBuffer("SELECT COUNT(1) FROM t_debt_transfer tdt INNER JOIN t_user tu ON tdt.user_id = tu.id  WHERE 1=1");
		
		Map<String, Object> condition = new HashMap<String, Object>();	
		//类型查询
		switch (showType) {
			case 1://待审核 
				querySQL.append(" AND status=:status ");
				countSQL.append(" AND status=:status ");
				condition.put("status", t_debt_transfer.Status.PREAUDITING.code);
				break;
			
			case 2://转让中
				querySQL.append(" AND status=:status ");
				countSQL.append(" AND status=:status ");
				condition.put("status", t_debt_transfer.Status.AUCTING.code);
				break;
				
			case 3://成功  
				querySQL.append(" AND status=:status ");
				countSQL.append(" AND status=:status ");
				condition.put("status", t_debt_transfer.Status.SUCC.code);
				break;
			
			case 4://失败(审核不通过,过期,失效)
				querySQL.append(" AND (status=:status1 OR status=:status2 OR status=:status3) ");
				countSQL.append(" AND (status=:status1 OR status=:status2 OR status=:status3) ");
				condition.put("status1", t_debt_transfer.Status.AUDIT_NOT_THROUGH.code);
				condition.put("status2", t_debt_transfer.Status.FAILED.code);
				condition.put("status3", t_debt_transfer.Status.INVALID.code);
				break;
		}
		
		/* 按编号模糊查询*/
		if (StringUtils.isNotBlank(numNo)) {
			querySQL.append(" AND tdt.id LIKE :id");
			countSQL.append(" AND tdt.id LIKE :id");
			condition.put("id", "%" + numNo + "%");
		}
		
		/* 按项目名称模糊查询*/
		if (StringUtils.isNotBlank(projectName)) {
			querySQL.append(" AND tdt.title LIKE :title");
			countSQL.append(" AND tdt.title LIKE :title");
			condition.put("title", "%" + projectName + "%");
		}
	
		/* 按转让人模糊查询*/
		if (StringUtils.isNotBlank(transferName)) {
			querySQL.append(" AND tu.name LIKE :name");
			countSQL.append(" AND tu.name LIKE :name");
			condition.put("name", "%" + transferName + "%");
		}
		
		//排序规则
		switch (orderType) {
			case 2: //债权总额 
				querySQL.append(" ORDER BY debt_amount ");
				if (orderValue == 0) {
					querySQL.append(" DESC ");
				}
				break;
	
			case 3: //转让期数
				querySQL.append(" ORDER BY transfer_period ");
				if (orderValue == 0) {
					querySQL.append(" DESC ");
				}
				break;
	
			case 4: //转让价格 
				querySQL.append(" ORDER BY transfer_price ");
				if (orderValue == 0) {
					querySQL.append(" DESC ");
				}
				break;
	
			default: //编号
				querySQL.append(" ORDER BY id ");
				if (orderValue == 0) {
					querySQL.append(" DESC ");
				}
				break;
		}
		
		//导出
		if (exports == Constants.EXPORT) {
			PageBean<BackDebtTransfer> page = new PageBean<BackDebtTransfer>();
			page.page = super.findListBeanBySQL(querySQL.toString(), BackDebtTransfer.class, condition);
			return page;
		}
			
		return super.pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), BackDebtTransfer.class, condition);
	}
	
	/**
	 * 后台-首页-待办事项的统计
	 *
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年5月13日
	 */
	public int backCountDebtInfo(){
		String countSQL = "SELECT COUNT(1) FROM t_debt_transfer WHERE status=:status";
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		conditionArgs.put("status", t_debt_transfer.Status.PREAUDITING.code);
		
		return findSingleIntBySQL(countSQL, 0, conditionArgs);
	}
}
