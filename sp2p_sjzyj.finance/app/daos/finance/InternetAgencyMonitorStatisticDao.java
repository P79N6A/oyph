package daos.finance;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import common.utils.DateUtil;
import daos.base.BaseDao;
import jobs.IndexStatisticsJob;
import models.finance.entity.t_internet_agency_monitor_statistic;
/**
 * 重点机构流动行缺口监测统计表Dao
 * @createDate 2018.10.22
 * */
public class InternetAgencyMonitorStatisticDao extends BaseDao<t_internet_agency_monitor_statistic> {

	public InternetAgencyMonitorStatisticDao () {}
	
	/**
	 * 添加数据 
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public Boolean addInternetAgencyMonitorStatistic (Date date) {
		t_internet_agency_monitor_statistic statistic = new t_internet_agency_monitor_statistic();
		
		statistic.category = "P2P";
		statistic.agency_name = "讴业普惠";
		statistic.source = "重点对象";
		statistic.risk_prejudgement = "低";
		
		statistic.time = date;
		
		return this.save(statistic);
	}
	
	/**
	 * 数据查询 指定月份 存量 资产段（应收回金额，亿元）预计
	 * 
	 * @param date 指定月份
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public Double queryRecoverAmount (Date date) {
		
		StringBuffer sql = new StringBuffer(" select sum(tbl.repayment_corpus+tbl.repayment_interest)/100000000 as amount from t_bill tbl where DATE_FORMAT( tbl.repayment_time , '%Y-%m') = DATE_FORMAT( :date , '%Y-%m' ) ");
		
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("date", date);
		
		return findSingleDoubleBySQL(sql.toString(), 0.00, condition);
	}
	
	/**
	 * 数据查询 指定月份 存量 资产段 （应兑付金额，亿元）
	 * @author guoShiJie
	 * @createDate 2018.10.30
	 * */
	public Double queryInvestAmount (Date date) {
		StringBuffer sql = new StringBuffer(" select sum(tbl.receive_corpus+tbl.receive_interest)/100000000 as amount from t_bill_invest tbl where DATE_FORMAT( tbl.receive_time , '%Y-%m') = DATE_FORMAT( :date , '%Y-%m' ) ");
		
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("date", date);
		
		return findSingleDoubleBySQL(sql.toString(), 0.00, condition);
	}
}
