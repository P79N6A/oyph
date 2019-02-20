package daos.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.DateUtil;
import daos.base.BaseDao;
import models.finance.entity.t_internet_agency_detail_statistic;

/**
 * 网贷机构验收进度明细统计表Dao
 * @createDate 2018.10.22
 * */
public class InternetAgencyDetailStatisticDao extends BaseDao<t_internet_agency_detail_statistic> {

	protected InternetAgencyDetailStatisticDao () {}
	
	/**
	 * 添加数据
	 * @author guoShiJie
	 * @createDate 2018.10.22
	 * */
	public Boolean addInternetAgencyDetailStatistic (Date date,String agencyName, String place,String depositTubeBank, Integer depositTubeStatus ) {
		
		t_internet_agency_detail_statistic statistic = new t_internet_agency_detail_statistic();
		
		statistic.agency_name = agencyName;
		statistic.place = place;
		statistic.deposit_tube_bank = depositTubeBank;
		statistic.deposit_tube_status = depositTubeStatus;
		statistic.time = date;
		
		return this.save(statistic);
	}
	
	
	/**
	 * 查询数据 自然人指定月借款余额和借款人数  或者  法人及其他组织指定月借款余额和借款人数
	 * @describe enterpriseName 为true 时，查询法人及其他组织指定月借款余额;enterpriseName 为false 时，查询自然人指定月借款余额
	 * @param date 查询日期
	 * @author guoShiJie
	 * @createDate 2018.10.22
	 * */
	public Map<String,Object> queryLoanBalanceDatas (Date date , Boolean enterpriseName) {
		
		StringBuffer sql = new StringBuffer(" select FORMAT(sum(tbd.amount)/10000,3) as loanBalance , count( distinct tur.id ) as number from t_bid tbd inner join t_user tur on tur.id = tbd.user_id inner join t_user_info tui on tui.user_id = tur.id where tbd.release_time IS NOT NULL AND DATE_FORMAT( tbd.release_time , '%Y-%m' ) = DATE_FORMAT( :date , '%Y-%m' ) ");
		if (enterpriseName) {
			sql.append(" AND tui.enterprise_name IS NOT NULL ");
		}else {
			sql.append(" AND tui.enterprise_name IS NULL ");
		}
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("date", date);
		Map<String ,Object> result = this.findMapBySQL(sql.toString(), condition);
		
		return result;
	}
	
	/**
	 * 查询数据 自然人指定月借款人数  或者  法人及其他组织指定月借款人数
	 * @param enterpriseName true 法人及其他组织; false自然人
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public Map<String,Object> queryLoanPeopleCount (Date date ,Boolean enterpriseName) {
		StringBuffer sql = new StringBuffer(" select count( distinct tur.id ) as loanBalance from t_bid tbd inner join t_user tur on tur.id = tbd.user_id inner join t_user_info tui on tui.user_id = tur.id where tbd.release_time IS NOT NULL AND DATE_FORMAT( tbd.release_time , '%Y-%m' ) = DATE_FORMAT( :date , '%Y-%m' ) ");
		if (enterpriseName) {
			sql.append(" AND tui.enterprise_name IS NOT NULL ");
		}else {
			sql.append(" AND tui.enterprise_name IS NULL ");
		}
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("date", date);
		Map<String ,Object> result = this.findMapBySQL(sql.toString(), condition);
		
		return result;
	}
	
	
	/**
	 * 查询数据 出借人数 自然人 和法人及其他组织
	 * 
	 * @param date 指定月份
	 * @param enterpriseName false 查询出借  自然人人数 ;true 查询法人和其他组织的人数
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public Map<String,Object> queryInvestCountDatas (Date date , Boolean enterpriseName ) {
		
		StringBuffer sql = new StringBuffer(" select count( distinct tur.id) as number from t_bill_invest tbi inner join t_bid tbd on tbd.id = tbi.bid_id inner join t_user tur on tur.id = tbi.user_id inner join t_user_info tui on tui.user_id = tur.id where tbd.release_time IS NOT NULL AND DATE_FORMAT( tbd.release_time , '%Y-%m' ) = DATE_FORMAT( :date , '%Y-%m' )  ") ;
		if (enterpriseName) {
			sql.append(" AND tui.enterprise_name IS NOT NULL ");
		} else {
			sql.append(" AND tui.enterprise_name IS NULL ");
		}
		
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("date", date);
		Map<String ,Object> result = this.findMapBySQL(sql.toString(), condition);
		
		return result;
	}
	
	/**
	 * 查询数据 标的信息查询
	 * @author guoShiJie
	 * @createDate 2018.10.23
	 * */
	public List<Map<String ,Object>> queryBidInfos(Date date ) {
		StringBuffer sql = new StringBuffer(" select tbd.amount as amount, tbd.period_unit as unit , tbd.period as period from t_bid tbd inner join t_user tur on tur.id = tbd.user_id inner join t_user_info tui on tui.user_id = tur.id where tbd.release_time IS NOT NULL AND DATE_FORMAT( tbd.release_time , '%Y-%m' ) = DATE_FORMAT( :date , '%Y-%m' ) ");
		
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("date", date);
		List<Map<String ,Object>> result = this.findListMapBySQL(sql.toString(), condition);
		
		return result;
	}
	/**
	 * 逾期金额查询
	 * @createDate 2018.10.30
	 * */
	public Map<String,Object> queryOverAmounts (Date date) {
		StringBuffer sql = new StringBuffer("select sum(repayment_interest+repayment_corpus)/10000 as overAmount from t_bill where is_overdue = 1 AND DATE_FORMAT( time , '%Y-%m' ) = DATE_FORMAT( :date , '%Y-%m') ");
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("date", date);
		Map<String,Object> result = this.findMapBySQL(sql.toString(), condition);
		
		return result;
	}
	
	
}
