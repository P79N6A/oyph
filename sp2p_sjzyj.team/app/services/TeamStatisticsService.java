package services;

import common.utils.Factory;

import dao.TeamStatisticsDao;
import models.entity.t_team_statistics;
import services.base.BaseService;

public class TeamStatisticsService extends BaseService<t_team_statistics> {

	protected TeamStatisticsDao teamStatisticsDao = Factory.getDao(TeamStatisticsDao.class);
	
	protected TeamStatisticsService(){
		super.basedao = teamStatisticsDao;
	}
	
	/**
	 * 通过supervisorId和type查询一条数据
	 * @param supervisorId
	 * @param type
	 * @return
	 */
	public t_team_statistics findByColumns(long supervisorId, int type, int months, int years) {
		
		return teamStatisticsDao.findByColumns(supervisorId, type, months, years);
	}
}
