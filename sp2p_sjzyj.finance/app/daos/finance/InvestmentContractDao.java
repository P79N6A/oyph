package daos.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import common.utils.DateUtil;
import daos.base.BaseDao;
import models.core.entity.t_invest;

import models.finance.entity.t_investment_contract;
import play.db.jpa.JPA;

/**
 * 金融办-投资合同表 Dao
 * @author guoShiJie
 * 
 * @createDate 2018.10.5
 * */
public class InvestmentContractDao extends BaseDao<t_investment_contract> {
	
	protected InvestmentContractDao () {}

	/**
	 * 粘贴从其他表中查询得到投资合同表的数据
	 * 上个月的投资合同
	 * @author guoShiJie
	 * @createDate 2018.10.6
	 * */
	public int updateInvestmentDatas () {
		
		StringBuffer sql = new StringBuffer("REPLACE INTO t_investment_contract (id ,p_id , user_id , amount , time , apr , unpaid_principal , unpaid_interest , manage_cost , receive_time ,receive_corpus , receive_interest , real_receive_time ) ");
		sql.append("( select distinct tbi.id ,tp.id as p_id , tb.user_id as user_id , tb.amount as amount, tbi.time as time , tb.apr/100 as apr , 0 as unpaid_principal, 0 as unpaid_interest , 0 as manage_cost , tbi.receive_time , tbi.receive_corpus , tbi.receive_interest , tbi.real_receive_time  FROM t_bill_invest tbi INNER JOIN t_invest ti on tbi.invest_id = ti.id  INNER JOIN t_bid tb ON ti.bid_id = tb.id INNER JOIN t_pact tp ON tp.pid = tb.id WHERE tp.type = 0 AND tb.release_time IS NOT NULL AND PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format(tbi.time, '%Y%m' ) ) =1  order by tbi.time asc )");
		Map<String,Object> condition = new HashMap<String,Object>();
		
		return updateBySQL(sql.toString(), condition);
	}
	/**
	 * 
	 * @Title: getInvestmentDatas
	 * 
	 * @description 查找当月的数据
	 * @return List<t_investment_contract>    
	 *
	 * @author HanMeng
	 * @createDate 2018年12月14日-下午4:28:51
	 */
	public List<t_investment_contract> getInvestmentDatas(){
		String sql = " SELECT * FROM t_investment_contract WHERE PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format(time, '%Y%m' ) ) =1";
		Map<String, Object> condition = new HashMap<String, Object>();
		return this.findListBySQL(sql, condition);
		
	}
}
