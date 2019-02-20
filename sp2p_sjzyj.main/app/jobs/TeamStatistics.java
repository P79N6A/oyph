package jobs;

import java.util.List;

import com.shove.Convert;

import common.constants.SettingKey;
import common.utils.Factory;
import daos.common.SettingDao;
import models.bean.Teams;
import models.common.entity.t_setting_platform;
import models.core.entity.t_invest;
import models.entity.t_teams;
import models.main.bean.TeamRule;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import services.TeamsService;
import services.common.SettingService;
import services.core.InvestService;

public class TeamStatistics {
	
	/** 注入系统设置service */
	protected static SettingService settingService = Factory.getService(SettingService.class);
	
	
	public void doJob() {
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------团队长统计,开始---------------------");
		}
		
		//统计当月和总金额,每天一次
		TeamsService teamsService = Factory.getService(TeamsService.class);
		SettingService settingService = Factory.getService(SettingService.class);
		InvestService investService = Factory.getService(InvestService.class);
		
		//查询所有业务经理的集合
		List<t_teams>  list = teamsService.listOfServiceManager(0);
		t_setting_platform setting = settingService.findByColumn("_key=?", "director_commission");
		t_setting_platform setting1= settingService.findByColumn("_key=?", "director_manager_commission");
		//查询所有的客户经理
		if (list != null && list.size()>0) {
			for (t_teams team : list) {
				Double sumAmount =new Double(0);
				Double  monthAmount =new Double(0);
				Double  sumCommission =new Double(0);
				Double  commission =new Double(0);
				
				sumAmount = investService.queryAmountByUserId(team.supervisor_id);
				
				if(sumAmount>=0){
					teamsService.updateTotalMoney(team.supervisor_id, sumAmount);
					
					sumCommission = TeamRule.queryAmount(sumAmount);
					
					if(sumCommission >=0){
						teamsService.updateTotalCommission(team.supervisor_id, sumCommission);
					}
				}
				
				monthAmount = investService.queryMonthAmount(team.supervisor_id);
				
				if(monthAmount>=0){
					teamsService.updateMonthMoney(team.supervisor_id, monthAmount);
					
					commission = TeamRule.queryAmount(monthAmount);
					
					if(commission >=0){
						teamsService.updateMonthCommission(team.supervisor_id, commission);
					}
				}
			}
		}
		
		
		//统计主任的统计信息
		List<t_teams>  lists = teamsService.findListByColumn("type=?", 1);
		double standard_bid = 0;
		String string = settingService.findSettingValueByKey(SettingKey.STANDARD_BID);
		if (string != null) {
			standard_bid = Double.parseDouble(string); //拆标比例
		}
		
		String string2 = settingService.findSettingValueByKey(SettingKey.DIRECTOR_QUOTA);
		
		int director_quota =  0; //业务部主任团队任务额度
		if (string2 != null) {
			director_quota = Integer.parseInt(string2);
		}
		
		String string3 = settingService.findSettingValueByKey(SettingKey.DIRECTOR_MANAGER_QUOTA);
		int director_manager_quota = 0;//业务部经理团队任务额度
		if (string3 != null) {
			director_manager_quota = Integer.parseInt(string3);
		}
		if (lists != null && lists.size()>0) {
			for (t_teams t_teams : lists) {
				double amount = 0;
				double sumAmounts = 0;
				double direCommission =0;
				double direCommission1 =0;
				
				List<t_teams>  teamList = teamsService.findListByColumn("parent_id=?",t_teams.supervisor_id);
				
				for (t_teams t_teams2 : teamList) {
					amount = t_teams2.month_money+amount;
					sumAmounts = t_teams2.total_money+sumAmounts;
				}
				
				if(sumAmounts>=0){
					teamsService.updateTotalMoney(t_teams.supervisor_id, sumAmounts);
				}
				
				
				//计算业绩
				if(sumAmounts/10000 >=director_quota){
					if(standard_bid>0){
						direCommission = TeamRule.queryAmount(sumAmounts*standard_bid/100,setting._value);					
					}else{
						direCommission = TeamRule.queryAmount(sumAmounts,setting1._value);
					}
				}
				
				if(amount>=0){
					teamsService.updateMonthMoney(t_teams.supervisor_id, amount);
				}
				
				if(amount/10000 >=director_quota){
					if(standard_bid>0){
						direCommission1 = TeamRule.queryAmount(amount*standard_bid/100,setting._value);					
					}else{
						direCommission1 = TeamRule.queryAmount(amount,setting._value);
					}
				}
				
				if(direCommission>=0){
					teamsService.updateTotalCommission(t_teams.supervisor_id, direCommission);
				}
				if(direCommission1>=0){
					teamsService.updateMonthCommission(t_teams.supervisor_id, direCommission1);
				}
				
			}
		}
		
		//统计主任经理的统计信息
		List<t_teams>  direLists = teamsService.findListByColumn("type=?", 2);
		
		if (direLists != null && direLists.size() > 0) {
			for (t_teams t_teams : direLists) {
				double amount = 0;
				double sumAmounts = 0;
				double commission1 =0;
				double commission2 =0;
				
				List<t_teams>  teamList = teamsService.findListByColumn("parent_id=?",t_teams.supervisor_id);
				
				for (t_teams t_teams2 : teamList) {
					amount = t_teams2.month_money+amount;
					sumAmounts = t_teams2.total_money+sumAmounts;
				}
				if(sumAmounts>=0){
					teamsService.updateTotalMoney(t_teams.supervisor_id, sumAmounts);
				}
				if(amount>=0){
					teamsService.updateMonthMoney(t_teams.supervisor_id, amount);
				}
				
				if(sumAmounts/10000 >=director_manager_quota){
					if(standard_bid>0){
						commission1 = TeamRule.queryAmount(sumAmounts*standard_bid/100,setting1._value);					
					}else{
						commission1 = TeamRule.queryAmount(sumAmounts,setting1._value);
					}
				}
				
				if(amount/10000 >=director_manager_quota){
					if(standard_bid>0){
						commission2 = TeamRule.queryAmount(amount*standard_bid/100,setting1._value);					
					}else{
						commission2 = TeamRule.queryAmount(amount,setting1._value);
					}
				}
				
				if(commission1>=0){
					teamsService.updateTotalCommission(t_teams.supervisor_id, commission1);
				}
				
				if(commission2>=0){
					teamsService.updateMonthCommission(t_teams.supervisor_id, commission2);
				}
			}
		}	
		
		if(Convert.strToBoolean(settingService.findSettingValueByKey(SettingKey.IS_INFO_SHOW), true)) {
			Logger.info("--------------团队长统计,结束---------------------");
		}
		
	}
}
