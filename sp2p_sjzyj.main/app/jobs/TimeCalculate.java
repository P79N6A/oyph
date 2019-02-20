package jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import common.utils.Factory;
import models.entity.t_team_statistics;
import models.entity.t_teams;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import services.TeamsService;
import services.TpService;
import services.common.UserInfoService;

public class TimeCalculate {
	
	protected static final TeamsService teamsService = Factory.getService(TeamsService.class);
	
	protected static final UserInfoService userInfoService = Factory.getService(UserInfoService.class);

	public void doJob(){
		
		Logger.info("--------------团队长提成,开始---------------------");
		List<t_teams> teams = teamsService.findAll();
		
		for (int i = 0; i < teams.size(); i++) {
			t_team_statistics team = new t_team_statistics();
			t_teams tea = teams.get(i);
			Calendar a=Calendar.getInstance();
			int year = 0;
			int month = 0;
			if((a.get(Calendar.MONTH)+1) == 1) {
				month = 12;
				year = a.get(Calendar.YEAR) - 1;
			}else{
				month = a.get(Calendar.MONTH);
				year = a.get(Calendar.YEAR);
			}
			if(tea.type == 0) {
				team.time = new Date();
				team.supervisor_id = tea.supervisor_id; 
				team.year = year;
				team.month = month;
				team.month_money = TpService.findOneMonthById(tea.supervisor_id,1).sumMoney;
				team.year_convert = TpService.findOneMonthById(tea.supervisor_id,1).convert;
				team.month_commission = TpService.caculateDeduct(tea.supervisor_id,1);
				team.type = 0;
				team.total_money = tea.total_money;
				team.actual_month_commission = TpService.caculateActualMonthCommission(tea.supervisor_id, 1);
				team.save();
			}
			if(tea.type == 4) {
				team.time = new Date();
				team.supervisor_id = tea.supervisor_id;
				team.year = year;
				team.month = month;
				team.month_money = TpService.findOneMonthById(tea.supervisor_id,1).sumMoney;
				team.year_convert = TpService.findOneMonthById(tea.supervisor_id,1).convert;
				team.month_commission = TpService.caculateDeduct(tea.supervisor_id,1);
				team.type = 4;
				team.total_money = tea.total_money;
				team.actual_month_commission = TpService.caculateActualMonthCommission(tea.supervisor_id, 1);
				team.save();
			}
			if(tea.type == 1) {
				t_teams teas = new t_teams();
				//teas = teamsService.getTeamTotal(tea, 1);
				teas = teamsService.getTeamActualMonthTotal(tea, 1);
				team.time = new Date();
				team.supervisor_id = tea.supervisor_id;
				team.year = year;
				team.month = month;
				team.month_money = teas.total_month_invest;
				team.year_convert = teas.total_month_money;
				team.month_commission = teas.total_month_commission;
				team.type = 1;
				team.total_money = teas.total_money;
				team.actual_month_commission = teas.total_actual_month_commission;
				team.save();
			}
			
		}
		Logger.info("--------------团队长提成,结束---------------------");
	}
	
	public void doJobs() {
		Logger.info("--------------修改用户状态,开始---------------------");
		userInfoService.updateByAccount();
		Logger.info("--------------修改用户状态,结束---------------------");
	}
}
