package daos.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.finance.entity.t_borrower_repayment;
/**
 * 
 *
 * @ClassName: BorrowerRepaymentDao
 *
 * @description 借款人还款明细表dao实现
 *
 * @author HanMeng
 * @createDate 2018年10月5日-下午5:15:43
 */
public class BorrowerRepaymentDao extends BaseDao<t_borrower_repayment> {
	protected BorrowerRepaymentDao(){}
/**
 * 
 *
 * @Title: saveBorrowerRepayment

 * @description: 向借款人还款明细表中     插入当月数据
 *
 * @param id
 * @param service_order_no 流水号
 * @param real_repayment_time  还款日期
 * @param p_id  贷款合同号
 * @param user_id 借款人客户号
 * @param repayment_corpus 还款本金
 * @param repayment_interest 还款利息
 * @return 
   
 * @return int   
 *
 * @author HanMeng
 * @return 
 * @createDate 2018年10月6日-下午4:55:22
 */
	public int saveBorrowerRepayment( ){

		String sql = "REPLACE INTO t_borrower_repayment(id,service_order_no,real_repayment_time,p_id,user_id,repayment_corpus,repayment_interest)"
				+ "(SELECT t.id, t.busniess_no,t.real_repayment_time, p.id,t.user_id,0,0 FROM t_bill t INNER JOIN t_pact p ON t.bid_id=p.pid  WHERE PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format(real_repayment_time, '%Y%m' ) ) =1)";
		Map<String, Object> condition = new HashMap<String, Object>();																																	
		
		
		return updateBySQL(sql, condition);
	
	}
	/**
	 * 
	 *
	 * @Title: getBorrowerRepayment
	
	 * @description: 查询借款人还款明细表数据
	 *
	 * @return 
	   
	 * @return List<t_borrower_repayment>   
	 *
	 * @author HanMeng
	 * @createDate 2018年10月9日-下午3:32:00
	 */
	public List<t_borrower_repayment> getBorrowerRepayment(){
		String sql = " SELECT * FROM t_borrower_repayment WHERE PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format(real_repayment_time, '%Y%m' ) ) =1";
		Map<String, Object> condition = new HashMap<String, Object>();
		return this.findListBySQL(sql, condition);
		
	}
}
