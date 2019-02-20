package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.JPA;
import common.constants.Constants;
import common.utils.PageBean;
import models.common.entity.t_risk_report;
import models.core.bean.BackRiskBid;
import models.core.entity.t_bid.Status;
import daos.base.BaseDao;

public class RiskReportDao extends BaseDao<t_risk_report>{
	
	protected RiskReportDao() {
	}
	
	/**
	 * 
	 * @param showType
	 * @param currPage
	 * @param pageSize
	 * @return
	 * @createDate 2017年4月24日
	 * @author lihuijun
	 */
	public PageBean<t_risk_report> pageOfRiskReport(int showType, int currPage, int pageSize, int orderValue,String year,String month,String day,int exports){
		StringBuffer querySQL = new StringBuffer("SELECT r.* from t_risk_report r where 1=1 ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(r.id) FROM t_risk_report r where 1=1 ");
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		
		switch (showType) {
		case 2:
			querySQL.append(" AND (r.status=:status or r.status=:statu or r.status=:stat)");
			countSQL.append(" AND (r.status=:status or r.status=:statu or r.status=:stat)");
			conditionArgs.put("status", 0);
			conditionArgs.put("statu", 3);
			conditionArgs.put("stat", -1);
			break;
		case 3:
			querySQL.append(" AND (r.status=:status or r.status=:statu or r.status=:stat)");
			countSQL.append(" AND (r.status=:status or r.status=:statu or r.status=:stat)");
			conditionArgs.put("status", 1);
			conditionArgs.put("statu", 4);
			conditionArgs.put("stat", 5);
			
			break;
		case 4:
			querySQL.append(" AND r.status=:status ");
			countSQL.append(" AND r.status=:status ");
			conditionArgs.put("status", 2);
			break;
		
		default:
			break;
		}
		
		/** 按年份  */
		if(StringUtils.isNotBlank(year) && !year.equals("请选择")){
			int yearInt=Integer.parseInt(year)+1;
			String y=year+"-01-01 00:00:00.0";
			String yy=yearInt+"-01-01 00:00:00.0";
			String yyy=year;
			querySQL.append(" AND r.time >= :year and r.time < :years");
			countSQL.append(" AND r.time >= :year and r.time < :years");
			conditionArgs.put("year", y);
			conditionArgs.put("years", yy);
			if(StringUtils.isNotBlank(month) && !month.equals("请选择")){
				int monthInt=Integer.parseInt(month)+1;
				String m="";
				String mm="";
				String mmm="";
				if(monthInt>10){
				 m=yyy+"-"+month+"-01 00:00:00.0";
				 mm=yyy+"-"+monthInt+"-01 00:00:00.0";
				 mmm=month;
				}else if(monthInt==10){
				 m=yyy+"-09-01 00:00:00.0";
				 mm=yyy+"-10-01 00:00:00.0";
				 mmm="09";
				}else{
				 m=yyy+"-0"+month+"-01 00:00:00.0";
				 mm=yyy+"-0"+monthInt+"-01 00:00:00.0";	
				 mmm="0"+month;
				}
				querySQL.append(" AND r.time >= :month and r.time < :months");
				countSQL.append(" AND r.time >= :month and r.time < :months");
				conditionArgs.put("month", m);
				conditionArgs.put("months", mm);
				if(StringUtils.isNotBlank(day) && !day.equals("请选择")){
					int dayInt=Integer.parseInt(day)+1;
					String d="";
					String dd="";
					if(dayInt>10){
						d=yyy+"-"+mmm+"-"+day+" 00:00:00.0";
						dd=yyy+"-"+mmm+"-"+dayInt+" 00:00:00.0";
					}else if(dayInt==10){
						d=yyy+"-"+mmm+"-09 00:00:00.0";
						dd=yyy+"-"+mmm+"-10 00:00:00.0";
					}else{
						d=yyy+"-"+mmm+"-0"+day+" 00:00:00.0";
						dd=yyy+"-"+mmm+"-0"+dayInt+" 00:00:00.0";
					}
					 
					querySQL.append(" AND r.time >= :day and r.time < :days");
					countSQL.append(" AND r.time >= :day and r.time < :days");
					conditionArgs.put("day", d);
					conditionArgs.put("days", dd);
				}
				
			}
		}
		
		
		querySQL.append(" ORDER BY r.time ");
		if(orderValue == 0){
			querySQL.append(" DESC ");
		}
		
		if(exports == Constants.EXPORT){
			PageBean<t_risk_report> page = new PageBean<t_risk_report>();
			page.page = this.findListBeanBySQL(querySQL.toString(), t_risk_report.class, conditionArgs);
			return page;
		}
		
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), t_risk_report.class, conditionArgs);
	}
	
	/**
	 * 得到刚刚保存的那个风控报告
	 * @return
	 * @createDate 2017年4月26日
	 * @author lihuijun
	 */
	public t_risk_report getLastReport(){
		String sql="select r.* from t_risk_report r order by r.id desc";
		Query query=JPA.em().createNativeQuery(sql,t_risk_report.class);
		List<t_risk_report> riskList= query.getResultList();
		if(riskList!=null && riskList.size()>0){
			t_risk_report risk=riskList.get(0);
			return risk;
		}else{
			return null;
		}
	}
	
	/**
	 * 
	 * @Title: getReportId
	 * @description: 根据报单编号获取风控报告id
	 *
	 * @param entry_number
	 * @return    
	 * @return t_risk_report   
	 *
	 * @author HanMeng
	 * @createDate 2018年12月4日-下午3:30:21
	 */
	public t_risk_report getReportId(String entry_number){
		String sql ="SELECT * FROM t_risk_report r WHERE r.entry_number =:entry_number";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("entry_number", entry_number);
		return this.findBySQL(sql, condition);
	}
	/**
	 * 
	 * @Title: insert
	 * 
	 * @description 保存时,返回刚保存的当前实体对象
	 * @param risk_report
	 * @return t_risk_report    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年12月6日-上午8:51:41
	 */
	public t_risk_report insert(t_risk_report risk_report) {
		
		return this.saveT(risk_report);
	}
	
	/**
	 * 
	 * @Title: findByBidId
	 *
	 * @description 借贷人其他借贷情况在风控报告中添加一列，前台写活
	 *
	 * @param @param bidId
	 * @param @return 
	 * 
	 * @return t_risk_report    
	 *
	 * @author yaozijun
	 *
	 * @createDate 2018年12月11日
	 */
	public t_risk_report findByBidId (Long bidId){
		String sql = " SELECT r.* FROM t_risk_report r INNER JOIN t_bid b ON r.user_id=b.user_id WHERE b.id=:bidId";
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("bidId", bidId);
		
		return this.findBySQL(sql, condition);
	}
}
