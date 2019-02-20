package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import play.db.jpa.JPA;
import services.base.BaseService;
import models.bean.Caculate;
import models.common.entity.t_deal_user;
import models.common.entity.t_setting_platform;
import models.common.entity.t_supervisor;
import models.common.entity.t_user;
import models.core.entity.t_bid;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import models.entity.t_team_statistics;
import models.entity.t_teams;
import models.main.bean.TeamRule;
import common.interfaces.ICacheable;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import dao.TeamStatisticsDao;
import dao.TeamsDao;
import daos.common.SettingDao;
import daos.common.SupervisorDao;
import daos.common.UserDao;
import daos.core.BidDao;
import daos.core.InvestDao;

public class TeamsService extends BaseService<t_teams> implements ICacheable{

	private static TeamsDao teamsDao = null;
	protected static final UserDao userDao = Factory.getDao(UserDao.class);
	protected static final InvestDao investDao = Factory.getDao(InvestDao.class);
	protected static final TeamStatisticsDao teamStatisticsDao = Factory.getDao(TeamStatisticsDao.class);
	protected TeamsService() {
		teamsDao = Factory.getDao(TeamsDao.class);
		super.basedao = this.teamsDao;
	}
	
	public static t_teams findTeamsById(long id){
		
		t_teams teams = teamsDao.findByID(id);
		
		if (teams == null) {
			LoggerUtil.info(false, "查询团队长时有误");
			
			return null;
		}
		return teams;
	}
	//lvweihuan begin
	public static t_teams getTeamTotal(t_teams mine,int selMonth){
		TpService tpmine=new TpService();
		SettingDao settingDao = Factory.getDao(SettingDao.class);
		List<t_teams> myteams=new ArrayList<>();
		myteams=queryListByPrentId(mine.supervisor_id);
		double summ=0;//总折算
		double sumc=0;//总提成
		double sumteam=0;//总理财（乘月份后的）
		double sumi=0;
		List<TeamRule> teamRules=new ArrayList<TeamRule>();
		t_setting_platform setting3 = settingDao.findByColumn("_key=?", "business_manager_commission");
		JSONArray jsonArray3 = JSONArray.fromObject(setting3._value);
		teamRules=(List)JSONArray.toList(jsonArray3, new TeamRule(), new JsonConfig()); 
		Caculate ca=null;
		for(t_teams myteam:myteams){			
			ca=tpmine.findOneMonthById(myteam.supervisor_id, selMonth);
			summ+=ca.convert;
			sumteam+=ca.totalMoney;
			sumi+=ca.sumMoney;
		}
		for(TeamRule trr:teamRules){
			double teammin=trr.minAmount*10000;
			double teammax=trr.maxAmount*10000;
			if(summ>=teammin && summ<teammax){
				sumc=sumteam*trr.amount/10000.0;	
				break;
			}
		}
		mine.total_month_commission=sumc;//月提成
		mine.total_month_money=summ;//月折算
		mine.total_month_invest=sumi;
		return mine;
	}
	//lvweihuan end
	/**
	 * 查询各种类型的列表
	 * @param currPage
	 * 
	 * @param pageSize
	 * @param type
	 * @return
	 */
	public static PageBean<t_teams> findTypeList(int currPage,int pageSize, int type,String name,int exports){
		
		PageBean<t_teams> page = new PageBean<t_teams>();
		Map<String,Object> args = new HashMap<String, Object>();
		String sql ="type=? AND supervisor_id in (SELECT id FROM t_supervisor t WHERE lock_status = 0)";
		if(name!=null&&name!=""){
			sql =" type=? and supervisor_id in (select id from t_supervisor where lock_status = 0 and t_supervisor.reality_name like "+"%"+name+"%"+")";
		}
		if(exports==1){
			String sql1 = " select * from t_teams where type=:type AND supervisor_id in (SELECT id FROM t_supervisor t WHERE lock_status = 0) ";
			args.put("type", type);
			page.page =  teamsDao.findListBySQL(sql1, args);
		    return  page;
		}
		
		return teamsDao.pageOfByColumn(currPage, pageSize, sql, type);
	}
	

//----------------------------------------------------------------------------------
	/**
	 * 业务查询经理
	 * @param currPage
	 * @param pageSize
	 * @param type
	 * @param supervisorId
	 * @return
	 */
	public static PageBean<t_teams> findDirectorList(int currPage,int pageSize, int type,long supervisorId,int exports){
		
		String sql = "(type=? OR type = 4) and parent_id=?";
		PageBean<t_teams> page = new PageBean<t_teams>();
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("type", type);
		args.put("parent_id", supervisorId);
		
		if(exports==1){
			String sql1 = " select * from t_teams where (type=:type OR type = 4) and parent_id=:parent_id ";
			args.put("type", type);
			
			args.put("parent_id", supervisorId);
			
			page.page =  teamsDao.findListBySQL(sql1, args);
		    return  page;
		}
		
		
		return teamsDao.pageOfByColumn(currPage, pageSize, sql, type,supervisorId);
	}
	
	
	/**
	 * 保存团队
	 * @param supervisor_id
	 * @param time
	 * @param parent_id
	 * @param type
	 * @return
	 */
	public static long save(long supervisor_id,long parent_id,int type){
	  t_teams teams = new t_teams();
	  
	  teams.supervisor_id = supervisor_id;
	  teams.time = new Date();
	  teams.parent_id = parent_id;
	  teams.type = type;
	  teams.save();
	  
	  return teams.id;
	}
	
	/**
	 * 修改当前月投资金额
	 * @param id
	 * @param money
	 * @return
	 */
	public static int updateMonthMoney(long id,double money,double total_money){
		
		String sql = "update t_teams set month_money =:month_money,total_money=:total_money where id=:id";
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("month_money", money);
		args.put("total_money", total_money);
		args.put("total_money", total_money);
		
		return  teamsDao.updateBySQL(sql, args);
	}


	@Override
	public void addAFlushCache() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public <K> K getCache() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		
	}
	
	
	public PageBean<t_teams> pageOfaccountManager(int currPage,int pageSize,int type){
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		
		if(currPage <=0){
			currPage =1;
		}
		if(pageSize <=0){
			pageSize=10;
		}
		
		/*String sql ="select * from t_teams where type=:type and parent_id=0";
		String sqlCount ="select count(id) from t_teams where type=:type and parent_id=0";*/
		String sql = "SELECT * FROM t_teams te WHERE (te.type = 0 OR te.type =4) AND te.parent_id = 0";
		String sqlCount = "SELECT COUNT(te.id) FROM t_teams te WHERE (te.type = 0 OR te.type =4) AND te.parent_id = 0";
		
		//conditionArgs.put("type", type);
		
		return teamsDao.pageOfBySQL(currPage, pageSize, sqlCount, sql, null);
		
	}
	
	
	/**
	 * 修改父类的id
	 * @param ids
	 * @param supervisor_id
	 * @return
	 */
	public void updateParentsBySupervisor(String ids,long supervisor_id){
		ResultInfo resultInfo = new ResultInfo();
		
		String[] id =  ids.split(",");
		
		for (String string : id) {
			if(string!=""){
				this.updateParent(Long.parseLong(string), supervisor_id);
			}
		}
	}
	
	/**
	 * 修改父类id
	 * @param id
	 * @param supervisorId
	 */
	public void updateParent(long id ,long supervisorId){
		String sql = "update t_teams set parent_id=:parent_id where supervisor_id=:supervisor_id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parent_id", supervisorId);
		map.put("supervisor_id", id);
		teamsDao.updateBySQL(sql, map);
	}
	
	/**
	 * 修改总金额
	 * @param supervisorId
	 * @param amount
	 */
	public void updateTotalMoney(long supervisorId,double amount){
		String sql = "update t_teams set total_money=:total_money where supervisor_id=:supervisor_id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total_money", amount);
		map.put("supervisor_id", supervisorId);
		teamsDao.updateBySQL(sql, map);
	}
	
	
	/**
	 * 修改当月的金额
	 * @param supervisorId
	 * @param amount
	 */
	public void updateMonthMoney(long supervisorId,double amount){
		String sql = "update t_teams set month_money=:month_money where supervisor_id=:supervisor_id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("month_money", amount);
		map.put("supervisor_id", supervisorId);
		teamsDao.updateBySQL(sql, map);
	}
	
	
	/**
	 * 修改总佣金
	 * @param supervisorId
	 * @param amount
	 */
	public void updateTotalCommission(long supervisorId,double amount){
		String sql = "update t_teams set total_commission=:total_commission where supervisor_id=:supervisor_id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total_commission", amount);
		map.put("supervisor_id", supervisorId);
		teamsDao.updateBySQL(sql, map);
	}
	
	
	/**
	 * 修改当月佣金
	 * @param supervisorId
	 * @param amount
	 */
	public void updateMonthCommission(long supervisorId,double amount){
		String sql = "update t_teams set month_commission=:month_commission where supervisor_id=:supervisor_id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("month_commission", amount);
		map.put("supervisor_id", supervisorId);
		teamsDao.updateBySQL(sql, map);
	}
	
	public ResultInfo delete(long id,int type,ResultInfo resultInfo){
		String sql = "update t_teams set parent_id=0 where id=:id  and type=:type";
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		args.put("id", id);
		args.put("type", type);
		int rows =teamsDao.updateBySQL(sql, args);
		if(rows <=0){
			resultInfo.code =-1;
			resultInfo.msg ="修改失败";
			return resultInfo;
		}
		 
		 resultInfo.code =1;
		 resultInfo.msg ="修改成功";
		 return resultInfo;
		
	}
	
	/**
	 * 查询团队列表
	 * @param parent_id
	 * @return List<T>
	 */
	//liuyang begin 2016-12-30-----------------------------
	public static List<t_teams> queryListByPrentId(long parent_id){
		String sql = " select * from t_teams where parent_id=:parent_id ";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("parent_id", parent_id);
		return teamsDao.findListBySQL(sql, args);
	}
	//liuyang end 2016-12-30-----------------------------
	
	
	/**
	 * 查询所有业务经理的集合
	 * 
	 * @author niu
	 * @create 2017.09.22
	 */
	public static List<t_teams> listOfServiceManager(int type) {
		
		String sql = "SELECT * FROM t_teams t WHERE t.type = :type";
		
		Map<String, Object> args = new HashMap<>();
		args.put("type", type);
		
		return teamsDao.findListBySQL(sql, args);
	}
	
	/**
	 * 团队长实际月发放提成
	 * 
	 * @author guoshijie
	 * @createdate 2017.12.27
	 * */
	public static t_teams getTeamActualMonthTotal(t_teams mine , int selMonth) {
		SettingDao settingDao = Factory.getDao(SettingDao.class);
		double actualSum = 0.00;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse("2018-1-1");
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		/**当月团队长...*/
		t_teams team = getTeamTotal(mine, selMonth);
		
		/**投资账单*/
		List<t_bill_invest> billInvest = new ArrayList<t_bill_invest>();
		
		/**团队业务员列表*/
		List<t_teams> businessteams = queryListByPrentId(team.parent_id);
		
		if(businessteams !=null && businessteams.size() > 0) {
			/**投资账单*/
			for (t_teams bts : businessteams) {
				List<t_bill_invest> b = null;
				String billInvestSql = "select tbi.* from t_bill_invest tbi INNER JOIN t_bid tb on tbi.bid_id = tb.id INNER JOIN t_invest ti on tbi.invest_id = ti.id "
						+ " where tbi.status = 1 AND tbi.real_receive_time is NOT NULL AND PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( tbi.real_receive_time, '%Y%m' ) ) =? AND"
						+ " tbi.user_id in (select u.id from t_user u where u.supervisor_id = ? )";
				Query billInvestQry = JPA.em().createNativeQuery(billInvestSql , t_bill_invest.class);
				billInvestQry.setParameter(1, selMonth);
				billInvestQry.setParameter(2, bts.supervisor_id);
				b = billInvestQry.getResultList();
				
				for (t_bill_invest bill : b) {
					billInvest.add(bill);
				}
			}
		}
		
		if(billInvest != null && billInvest.size() > 0) {
			BidDao bidDao = new BidDao();
			for (t_bill_invest bi : billInvest) {
				
				t_bid bid = bidDao.findByID(bi.bid_id);
				Calendar ca2 = Calendar.getInstance();
				ca2.setTime(bid.release_time);
				int year = ca2.get(Calendar.YEAR);
				int m = ca2.get(Calendar.MONTH)+1;
				
				/**团队年化*/
				double convert = -1.00;
				
				if (bid.release_time.after(date)) {
					
					List<t_team_statistics> ts = teamStatisticsDao.findListByColumn("month = ? AND year = ? AND type = ? AND supervisor_id = ?",m,year,1,mine.supervisor_id);
					if (ts !=null && ts.size() > 0) {
						
						/**放款当月团队年化*/
						convert = ts.get(0).year_convert;
					} else {
						Calendar c1 = Calendar.getInstance();
					    Calendar c2 = Calendar.getInstance();
					    c1.setTime(new Date());
					    c2.setTime(bid.release_time);
					    if(c1.getTimeInMillis() >= c2.getTimeInMillis()) {
					    	int year1 = c1.get(Calendar.YEAR);
						    int year2 = c2.get(Calendar.YEAR);
						    int month1 = c1.get(Calendar.MONTH);
						    int month2 = c2.get(Calendar.MONTH);
						    int day1 = c1.get(Calendar.DAY_OF_MONTH);
						    int day2 = c2.get(Calendar.DAY_OF_MONTH);
						    // 获取年的差值 假设 d1 = 2015-8-16 d2 = 2011-9-30
						    int yearInterval = year1 - year2;
						    // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
						    if(month1 < month2 || month1 == month2 && day1 < day2) yearInterval --;
						    // 获取月数差值
						    int monthInterval = (month1 + 12) - month2 ;
						    if(day1 < day2) monthInterval --;
						    monthInterval %= 12;
						    int selm = yearInterval * 12 + monthInterval;
							convert = getTeamTotal(mine,selm).total_month_money;
					    }
					}
					
				}
				
				/**团队提成规则*/
				List<TeamRule> teamRules=new ArrayList<TeamRule>();
				t_setting_platform setting3 = settingDao.findByColumn("_key=?", "business_manager_commission");
				JSONArray jsonArray3 = JSONArray.fromObject(setting3._value);
				teamRules=(List)JSONArray.toList(jsonArray3, new TeamRule(), new JsonConfig());
				
				for (TeamRule trr : teamRules) {
					double teammin=trr.minAmount*10000;
					double teammax=trr.maxAmount*10000;
					if (convert >= teammin && convert < teammax) {
						t_invest i= investDao.findByID(bi.invest_id);
						if (i != null) {
							actualSum += i.amount * trr.amount / 10000.00;
						}
						
						break;
					}
				}
				
			}
			
		}
		
		/**给team赋值(月团队实际发放提成)*/
		team.total_actual_month_commission = actualSum;
		return team;
	}
}
