package daos.finance;

import java.util.Date;

import daos.base.BaseDao;
import models.finance.entity.t_internet_change_monitor;
/**
 * 重点对象（整改类）整改落实进展监测明细表Dao
 * @createDate 2018.10.22
 * */
public class InternetChangeMonitorDao extends BaseDao<t_internet_change_monitor> {

	public InternetChangeMonitorDao () {}
	
	/**
	 * 添加数据 
	 * 
	 * @param category 所属领域
	 * @param agencyName 机构名称
	 * @param name D平台网址（或者APP名称）
	 * @param date 存量不合规业务计划退出时间
	 * @param riskDegree 风险程度
	 * @param riskExpress 风险主要表现
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.24
	 * */
	public Boolean addInternetChangeMonitor (Date createDate , String category , String agencyName ,String name , String date ,Integer manageStatus , Integer changeStatus,String source,String workPress) {
		
		t_internet_change_monitor monitor = new t_internet_change_monitor();
		
		monitor.category = category;
		monitor.agency_name = agencyName;
		monitor.name = name;
		monitor.out_time = date;
		monitor.source = source;
		monitor.work_progress = workPress;
		monitor.manage_status = manageStatus;
		monitor.change_status = changeStatus;
		monitor.time = createDate;
		
		return this.save(monitor);
	}
	
}
