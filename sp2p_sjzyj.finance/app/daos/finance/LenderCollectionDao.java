package daos.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.finance.entity.t_investment_contract;
import models.finance.entity.t_lender_collection;
/**
 * 
 *
 * @ClassName: LenderCollectionDao
 *
 * @description 出借人收款明细dao实现
 *
 * @author HanMeng
 * @createDate 2018年10月6日-上午10:02:24
 */
public class LenderCollectionDao extends BaseDao<t_lender_collection> {
	protected LenderCollectionDao(){}
	/**
	 * 
	 *
	 * @Title: savaLenderCollection
	
	 * @description: 向出借人收款明细表中插入数据
	 * @param id 
	 * @param service_order_no 流水号
	 * @param p_id  信贷合同号
	 * @param user_id 出借人客户号
	 * @param real_receive_time 收款日期
	 * @param receive_corpus 实收本金
	 * @param receive_interest 实收利息
	 * @return 
	   
	 * @return int   
	 *
	 * @author HanMeng
	 * @createDate 2018年10月6日-上午11:04:50
	 */
	public int savaLenderCollection(){

		String sql = "REPLACE INTO t_lender_collection (id,service_order_no,p_id,user_id,real_receive_time,receive_corpus,receive_interest)"
				+" (SELECT t.id,t.busniess_no, p.id,t.user_id,t.real_receive_time,t.receive_corpus,t.receive_interest FROM t_bill_invest t INNER JOIN t_pact p ON t.bid_id=p.pid WHERE  t.busniess_no is not null  and  PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format(t.real_receive_time, '%Y%m' ) ) =1)";
		Map<String, Object> condition = new HashMap<String, Object>();
		return updateBySQL(sql, condition);		
	}
	
	
	/**
	 * 
	 *
	 * @Title: getInvestmentContract
	
	 * @description: 查询出借人收款明细表数据
	 *
	 * @return 
	   
	 * @return List<t_investment_contract>   
	 *
	 * @author HanMeng
	 * @createDate 2018年10月9日-下午3:34:31
	 */
	public List<t_lender_collection> getLenderCollection(){
		String sql = " SELECT * FROM t_lender_collection real_receive_time WHERE PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format(real_receive_time, '%Y%m' ) ) =1";
		Map<String, Object> condition = new HashMap<String, Object>();
		return this.findListBySQL(sql, condition);
	}
}
