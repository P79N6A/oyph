package service.ext.experiencebid;

import java.util.List;

import models.ext.experience.bean.experienceBidInvestRecord;
import models.ext.experience.bean.experienceUserInvestRecord;
import models.ext.experience.entity.t_experience_bid_invest;
import services.base.BaseService;
import common.utils.Factory;
import common.utils.PageBean;
import daos.ext.experiencebid.ExperienceBidInvestDao;

public class ExperienceBidInvestService extends BaseService<t_experience_bid_invest>{
	
	public ExperienceBidInvestDao experienceBidInvestDao = null;
	
	public ExperienceBidInvestService(){
		experienceBidInvestDao = Factory.getDao(ExperienceBidInvestDao.class);
		basedao = experienceBidInvestDao;
	}
	
	/**
	 * 保存体验账户投标记录
	 *
	 * @param experienceBidInvest
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public boolean save(t_experience_bid_invest experienceBidInvest){
		
		return experienceBidInvestDao.save(experienceBidInvest);
	}
	
	/**
	 * 获取体验标的所有投标记录
	 *
	 * @param experienceBidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public List<t_experience_bid_invest> queryExperienceBidInvest(long experienceBidId) {
		
		return experienceBidInvestDao.queryExperienceBidInvest(experienceBidId);
	}
	
	/**
	 * 前台-体验标-投标记录
	 *
	 * @param experienceBidId
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public PageBean<experienceBidInvestRecord> pageOfExperienceBidInvestRecord(long experienceBidId, int currPage, int pageSize){
		
		return experienceBidInvestDao.pageOfExperienceBidInvestRecord(experienceBidId, currPage, pageSize);
	}

	/**
	 * 用户投资体验标的记录
	 *
	 * @param userId
	 * @param currPage
	 * @param pageSize
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月19日
	 */
	public PageBean<experienceUserInvestRecord> pageOfExperienceUserInvestRecord(long userId, int currPage, int pageSize) {
		
		return experienceBidInvestDao.pageOfExperienceUserInvestRecord(userId, currPage, pageSize);
	}
	
	
}



