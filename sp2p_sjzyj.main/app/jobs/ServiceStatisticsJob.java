package jobs;

import java.util.Date;
import java.util.List;

import common.utils.Factory;
import models.common.entity.t_service_month;
import models.common.entity.t_service_person;
import models.common.entity.t_service_user_relevance;
import play.jobs.Every;
import play.jobs.Job;
import services.common.ServiceMonthService;
import services.common.ServicePersonService;
import services.common.ServiceUserRelevanceService;
import services.common.UserInfoService;

/**
 * 每月最后一天执行客服月末统计
 * 
 * @author liuyang
 * @create 2018-07-15
 */
public class ServiceStatisticsJob {
	
	protected static ServiceUserRelevanceService serviceUserRelevanceService = Factory.getSimpleService(ServiceUserRelevanceService.class);
	
	protected static ServicePersonService servicePersonService = Factory.getService(ServicePersonService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static ServiceMonthService serviceMonthService = Factory.getService(ServiceMonthService.class);

	public void doJob(){
		List<t_service_person> persons = servicePersonService.findAll();
		
		Date date = new Date();
		
		if(persons.size()>0 && persons != null) {
			for (int i = 0; i < persons.size(); i++) {
				t_service_month mon = new t_service_month();
				mon.service_id = persons.get(i).id;
				
				//计算客服所有的服务人数
				int allUser = serviceUserRelevanceService.queryCountByUserId(persons.get(i).id, 0);
				mon.service_num = allUser;
				
				//计算客服服务的开户人数
				int openUser = serviceUserRelevanceService.queryCountByUserId(persons.get(i).id, 1);
				mon.open_num = openUser;
				
				//计算本月待收金额
				double collectMoney = 0.00;
				List<t_service_user_relevance> ser = serviceUserRelevanceService.findListByColumn("service_id=?", persons.get(i).id);
				if(ser.size()>0 && ser != null) {
					for (int j = 0; j < ser.size(); j++) {
						double zhi = userInfoService.findCollectByUser(ser.get(j).user_id);
						collectMoney = collectMoney + zhi;
					}
				}
				mon.collect_money = collectMoney;
				
				//获取月份
				int month = date.getMonth();
				if(month == 0) {
					month = 12;
				}
				
				//计算本月增量金额
				double addMoney = 0.00;
				t_service_month ser1 = serviceMonthService.queryByTime(date.getYear()+1900, month);
				if(ser1 != null) {
					addMoney = collectMoney - ser1.collect_money;
				}
				mon.add_money = addMoney;
				
				//获取年、月、时间
				mon.year = date.getYear()+1900;
				mon.month = date.getMonth()+1;
				mon.time = date;
				
				mon.save();
			}
		}
	}
}
