package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.JPA;

import com.shove.Convert;

import common.utils.BrokerUtils;
import common.utils.PageBean;
import models.common.entity.t_risk_report;
import daos.base.BaseDao;
/**
 * 风控报告列表
 * RiskDao
 * @author lihuijun
 * @createDate 2017年5月9日
 */
public class RiskDao extends BaseDao<t_risk_report>{

	public List<t_risk_report> getRiskReportListByPage(Map<String, String> parameters){
		
		StringBuffer querySQL = new StringBuffer("SELECT r.* from t_risk_report r where 1=1 ");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(r.id) FROM t_risk_report r where 1=1 ");
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		
		int showType=Convert.strToInt(parameters.get("showType"), 0);
		
		switch (showType) {
		case 2:
			querySQL.append(" AND (r.status=:status or r.status=:statu or r.status=:stat)");
			countSQL.append(" AND (r.status=:status or r.status=:statu or r.status=:stat)");
			conditionArgs.put("status", 0);
			conditionArgs.put("statu", 3);
			conditionArgs.put("stat", -1);
			break;
		case 3:
			querySQL.append(" AND (r.status=:status or r.status=:statu)");
			countSQL.append(" AND (r.status=:status or r.status=:statu)");
			conditionArgs.put("status", 1);
			conditionArgs.put("statu", 4);
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
		String year=parameters.get("year");
		if(StringUtils.isNotBlank(year) && !year.equals("请选择")){
			int yearInt=Integer.parseInt(year)+1;
			String y=year+"-01-01 00:00:00.0";
			String yy=yearInt+"-01-01 00:00:00.0";
			String yyy=year;
			querySQL.append(" AND r.time >= :year and r.time < :years");
			countSQL.append(" AND r.time >= :year and r.time < :years");
			conditionArgs.put("year", y);
			conditionArgs.put("years", yy);
			String month=parameters.get("month");
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
				String day=parameters.get("day");
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
		
		int orderValue=Convert.strToInt(parameters.get("orderValue"), 0);
		if(orderValue == 0){
			querySQL.append(" DESC ");
		}
		PageBean<t_risk_report> page=pageOfBeanBySQL(Convert.strToInt(parameters.get("currPage"), 1), Convert.strToInt(parameters.get("pageSize"), 13), countSQL.toString(), querySQL.toString(), t_risk_report.class, conditionArgs);
		return page != null ? BrokerUtils.listHandle(page.page) : null;
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
}
