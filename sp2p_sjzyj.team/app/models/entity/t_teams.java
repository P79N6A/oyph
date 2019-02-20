package models.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Query;
import javax.persistence.Transient;

import dao.TeamStatisticsDao;

import models.common.entity.t_deal_user;
import models.common.entity.t_supervisor;
import models.common.entity.t_user;
import models.common.entity.t_user_info;

import play.db.jpa.JPA;
import play.db.jpa.Model;
import services.TpService;

@Entity
public class t_teams extends Model{

	public long supervisor_id;
	
	public Date time;
	
	public long parent_id;
	
	public double month_money;//当月理财金额
	
	public double total_money; //理财总金额
	
	public double month_commission; //当月佣金
	
	public double total_commission; //总佣金
	
	public int type; //类型(0,客户经理,1,业务主任,2业务部经理)
	
	public Long curr_commission;//使用的提成规则，空表示普通提成规则
	
	public double actual_month_commission; //当月实际发放佣金
	@Transient
	public double total_month_commission;//团队月佣金lvweihuan
	@Transient
	public double total_month_money;//团队月折算理财金额lvweihuan
	@Transient
	public double total_month_invest;
	@Transient
	public double total_actual_month_commission; //团队实际发放提成金额guoshijie
	
	@Transient
	public double total_per_commission;//特殊提成规则的提成
	
	@Transient
	public double actual_month_commission_special;//当月实际发放的特殊规则提成
	
	private static t_teams t;
	public static  t_teams getT(){
		if(t==null){
			t=new t_teams();
		}
		return t;
	}
	@Transient
	public double sumMoney;
	public double getSumMoney(){
		return TpService.findOneMonthById(supervisor_id,t.month).sumMoney;
	}
	@Transient
	public int month;

	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	@Transient
	public double yearConvert;
	public Double getYearConvert(){
		
		return TpService.findOneMonthById(supervisor_id,t.month).convert;
	}
	@Transient
	public Double deduct;
	public Double getDeduct(){
		return TpService.caculateDeduct(supervisor_id,t.month);
	}

	@Transient
	public String code;
	public String getCode(){
		t_supervisor supervisor = t_supervisor.findById(supervisor_id);
		if(supervisor==null){
			return "";
		}else{
			return supervisor.extension;
		}
	}
	
	@Transient
	public String count;
	public String getCount(){
		switch (this.type) {
		case 0:
			return t_user.count(" supervisor_id=? ", this.supervisor_id)+"";
		case 1:
			return t_teams.count(" parent_id=? ", this.supervisor_id)+"";
		case 2:
			return t_teams.count(" parent_id=? ", this.supervisor_id)+"";
		case 3:
			 return "0";
		case 4:
			return t_user.count(" supervisor_id=? ", this.supervisor_id)+"";
		default:
			return "0";
		}
	}
	
	@Transient
	public String realName;
	public String getRealName(){
		t_supervisor supervisor = t_supervisor.findById(supervisor_id);
		if(supervisor==null){
			return "";
		}else{
			return supervisor.reality_name;
		}
	}
	
	@Transient
	public String name;
	public String getName(){
		t_supervisor supervisor = t_supervisor.findById(supervisor_id);
		if(supervisor==null){
			return "";
		}else{
			return supervisor.name;
		}
	}
	
	@Transient
	public String mobile;
	public String getMobile(){
		t_supervisor supervisor = t_supervisor.findById(supervisor_id);
		if(supervisor==null){
			return "";
		}else{
			return supervisor.mobile;
		}
	}
	
	@Transient
	public String investCount;
	public String getInvestCount(){
		
		switch (this.type) {
		case 0:
			return t_user_info.count(" invest_member_time is not null and user_id in( select id from t_user where supervisor_id="+this.supervisor_id+")")+"";
		case 1:
			 return t_user_info.count(" invest_member_time is not null and user_id in( select id from t_user where supervisor_id in(select supervisor_id from t_teams where parent_id="+this.supervisor_id+" ) ) ")+"";
		case 2:
			long count =0;
			List<t_teams> list = t_teams.find("parent_id=?", this.supervisor_id).fetch();
			for (t_teams t_teams2 : list) {
				count = t_user_info.count(" invest_member_time is not null and user_id in( select id from t_user where supervisor_id in(select supervisor_id from t_teams where parent_id="+t_teams2.supervisor_id+" ) ) ")+count;
			}
			 return count+"";
		case 3:
			return "0";
		case 4:
			return t_user_info.count(" invest_member_time is not null and user_id in( select id from t_user where supervisor_id="+this.supervisor_id+")")+"";
		default:
			return "0";
		}
		
	}
	
	@Transient
	public String userCode;
	public String getUserCode(){
		
		switch (this.type) {
		case 0:
			return t_user_info.count("  user_id in( select id from t_user where supervisor_id="+this.supervisor_id+")")+"";
		case 1:
			 return t_user_info.count(" user_id in( select id from t_user where supervisor_id in(select supervisor_id from t_teams where parent_id="+this.supervisor_id+" ) ) ")+"";
		case 2:
			long count =0;
			List<t_teams> list = t_teams.find("parent_id=?", this.supervisor_id).fetch();
			for (t_teams t_teams2 : list) {
				count = t_user_info.count(" user_id in( select id from t_user where supervisor_id in(select supervisor_id from t_teams where parent_id="+t_teams2.supervisor_id+" ) ) ")+count;
			}
			 return count+"";
		case 3:
			return "0";
		case 4:
			return t_user_info.count("  user_id in( select id from t_user where supervisor_id="+this.supervisor_id+")")+"";
		default:
			return "0";
		}
		
	}
	
	@Transient
	public String position;
	public String getPosition(){
		switch (this.type) {
		case 0:
		return "客户经理";	
		case 1:
			return "业务室主任";	
		case 2:
			return "业务部经理";
		case 4:
			return "外部客户经理";
		default:
			return "普通管理员";	
	}
	}
	//团队本月佣金
	@Transient
	public double teamMonthCommission;
	public Double getTeamMonthCommission(){
		teamMonthCommission =0;
		switch (this.type) {
		case 1:
		case 2:
		List<t_teams> list = t_teams.find("parent_id=?", this.supervisor_id).fetch();
			
			for (t_teams t_teams2 : list) {
				teamMonthCommission = t_teams2.month_commission+teamMonthCommission;
			}
			return teamMonthCommission;
		default:
			return teamMonthCommission;
		}
		
	}
	@Transient
	public double teamCommission;
	public Double getTeamCommission(){
		teamCommission =0;
		switch (this.type) {
		case 1:
		case 2:
			List<t_teams> list = t_teams.find("parent_id=?", this.supervisor_id).fetch();
			
			for (t_teams t_teams2 : list) {
				teamCommission = t_teams2.total_commission+teamCommission;
			}
			return teamCommission;

		default:
			return teamCommission;
		}
	}
	
	@Transient
	public int months;
	
	public int getMonths() {
		return months;
	}
	public void setMonths(int months) {
		this.months = months;
	}
	
	@Transient
	public int year;

	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	@Transient
	public t_team_statistics teamSta;
	public t_team_statistics getTeamSta() {
		
		TeamStatisticsDao teamStatisticsDao = common.utils.Factory.getDao(TeamStatisticsDao.class);
		t_team_statistics teams = teamStatisticsDao.findByColumns(this.supervisor_id, this.type,t.months,t.year);
		
		return teams;
	}
	
	//当月实际发放的提成(佣金)
	/*@Transient
	public double actualMonthCommission;*/
	public double getActual_month_commission() {
		
		return TpService.caculateActualMonthCommission(supervisor_id,t.month);
		
	}
}
