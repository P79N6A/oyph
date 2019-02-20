package daos.finance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.DateUtil;
import daos.base.BaseDao;
import models.finance.entity.t_loan_pact;

/**
 * 金融办-贷款合同表
 * 
 * @createDate 2018.10.9
 * */
public class LoanPactDao extends BaseDao<t_loan_pact>{
	protected LoanPactDao(){}
	
	/**
	 * 获取数据并插入到t_loan_pact 表中
	 * 上个月的贷款合同
	 * @author guoShiJie
	 * @createDate 2018.10.9
	 * */
	public int updateLoanPacts ( ) {
		
		StringBuffer sql = new StringBuffer("REPLACE INTO t_loan_pact ( id ,p_id , user_id, name, amount , gross_interest , payable_interest , platform_commission ,contract_date , interest_date ,closing_date , annual_rate , ensure_type , limits , loan_period , loan_use , throw_area, throw_industry, overdue,  bill_period , period_unit , period , is_overdue , real_principal_amount , real_interest_amount , repayment_time , real_repayment_time ) ");
		sql.append(" ( select tbl.id ,tp.id as p_id , tbl.user_id , tui.reality_name as name , tbd.amount , 0 as gross_interest  , 0 as payable_interest , tbd.amount * tbd.service_charge/100/12 as platform_commission , tbd.pre_release_time as contract_date , tbd.release_time as interest_date  , tbl.real_repayment_time as closing_date , tbd.apr/100 as annual_rate ,trr.guaranty_kind as ensure_type , trr.pledge_kind as limits , 0 as loan_period , trr.loan_purpose as loan_use , tbd.throw_area , tis.code , tbl.overdue_days  , tbl.period as bill_period , tbd.period_unit , tbd.period , tbl.is_overdue , tbl.repayment_corpus as real_principal_amount , tbl.repayment_interest as real_interest_amount , tbl.repayment_time , tbl.real_repayment_time from t_bill tbl inner join t_bid tbd on tbl.bid_id = tbd.id inner join t_risk_report trr on tbd.risk_id = trr.id inner join t_pact tp on tp.pid = tbd.id inner join t_user tu on tu.id = tbd.user_id inner join t_user_info tui on tui.user_id = tu.id left join t_industry_sort tis on tis.id = tbd.throw_industry where  tp.type = 0 AND tbd.release_time IS NOT NULL AND PERIOD_DIFF(date_format(now(), '%Y%m'),date_format(tbd.pre_release_time, '%Y%m')) = 1 order by tbd.pre_release_time asc ) ");
		Map<String,Object> condition = new HashMap<String,Object>();
		
		
		return updateBySQL(sql.toString(),condition);
		
	}
	/**
	 * 
	 * @Title: findLoanPactUp
	 * 
	 * @description 查询上月的数据
	 * @param @return
	 * @return List<t_loan_pact>    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月11日-下午3:26:43
	 */
	public List<t_loan_pact> findLoanPactUp(){
		String sql = "SELECT * FROM t_loan_pact where PERIOD_DIFF(date_format(now(), '%Y%m'),date_format(contract_date,'%Y%m')) = 1";
		return findListBySQL(sql, null);
	}
	
}
