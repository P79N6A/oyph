package daos.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.common.entity.t_user_info;
import models.finance.entity.t_trade_info;
import models.finance.entity.t_trade_info.TradeType;


/**
 * 交易信息Dao接口
 *
 * @ClassName: TradeInfoDao

 * @description 
 *
 * @author lujinpeng
 * @createDate 2018年10月6日-上午9:59:49
 */
public class TradeInfoDao extends BaseDao<t_trade_info> {

	protected TradeInfoDao () {
		
	}
	
	/**
	 * 清空表数据
	 *
	 * @Title: truncateTable
	
	 * @description 
	 *
	 * @param  
	   
	 * @return void    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月8日-上午9:14:38
	 */
	public void truncateTable() {
		String sql = " truncate table t_trade_info";
		this.deleteBySQL(sql, null);
	}
	
	
	
	/**
	 * 查询表数据
	 *
	 * @Title: findTradeInfo
	
	 * @description 
	 *
	 * @param @return 
	   
	 * @return List<t_trade_info>    
	
	 *
	 * @author lujinpeng
	 * @createDate 2018年10月8日-上午9:45:43
	 */
	public List<t_trade_info> findTradeInfo () {
		String sql = "select * from t_trade_info where TO_DAYS( NOW( ) ) - TO_DAYS(trade_date) = 1";
		Map<String,Object> condition = new HashMap<String, Object>();
		
		return this.findListBySQL(sql, condition);
	}
	

	/**
	 * 保存交易信息记录
	 * @author guoShiJie
	 * @createDate 2018.10.18
	 * */
	public Boolean saveTradeInfo (Date dataDate ,Date tradeDate, String angery, String businessNo ,String account ,TradeType serviceType,Long userId,String userName , String currency , BigDecimal amount ,Long bidId) {
		
		t_trade_info trade = new t_trade_info();
		
		trade.data_date = dataDate;
		trade.trade_date = tradeDate;
		trade.trade_organization_id = angery;
		trade.serial_number = businessNo;
		trade.account = account;
		trade.tran_type = Integer.parseInt(serviceType.value);
		trade.customer_id = userId+"";
		trade.customer_name = userName;
		trade.currency = currency;
		trade.amount = amount;
		trade.amount_cny = amount;
		trade.product_code = bidId+"";
		
		return this.save(trade);
	}
	

}
