package services.common;

import java.util.List;

import common.utils.Factory;
import common.utils.PageBean;
import daos.common.ParticipationDao;
import models.common.entity.t_participation;
import services.base.BaseService;

public class ParticipationService extends BaseService<t_participation> {

	protected static ParticipationDao participationDao = Factory.getDao(ParticipationDao.class);
	
	protected ParticipationService() {
		super.basedao = this.participationDao;
	}
	
	/**
	 * 通过参与id
	 * @param partId
	 * @return
	 * @createDate 2017年5月22日
	 * @author lvweihuan
	 */
	public long queryUserOrder(long partId){
	    long userOrder=0;
	    
	    t_participation participation = this.findByID(partId);
	    
	    if(participation==null){
	        return userOrder;
	    }
	    
	    List<t_participation> participations = participationDao.queryUserOrder(participation.activity_id);
	    
	    if(participation!=null){
	        long i=1;
	        for(t_participation participation2:participations){
	            if(participation2.id==partId){
	                return i;
	            }
	            i++;
	        }
	        
	    }
	    
	    return userOrder;
	}
	
	/**
	 * 查询参加活动的人数
	 * @param activityId
	 * @return
	 */
	public int queryTotalParticipations(long activityId) {
		
		return participationDao.queryTotalParticipations(activityId);
	}
	
	/**
	 * 投票排行榜
	 * @param activityId
	 * @return
	 */
	public List<t_participation> queryVoteRankList(long activityId) {
		
		return participationDao.queryVoteRankList(activityId);
	}
	
	/**
	 * 查询照片列表
	 * 
	 * @param name
	 * @param status
	 * @param currPage
	 * @param pageSize
	 * @param searchTitle
	 * @return
	 */
	public PageBean<t_participation> queryParticipationsByPage(int currPage, int pageSize, int orderType, long activityId, String searchTitle) {
		
		return participationDao.queryParticipationsByPage(currPage, pageSize, orderType, activityId, searchTitle);
	}
	
	/**
	 * 查询需审核的参与表
	 * @param currPage
	 * @param pageSize
	 * @return
	 * @createDate 2017年5月22日
	 * @author lvweihuan
	 */
	public PageBean<t_participation> queryPartCheck(int currPage,int pageSize,long voteId){
	    return participationDao.queryPartCheck(currPage, pageSize,voteId);
	}
	
	/**
	 * 根据条件查询实体对象
	 * @param activityId
	 * @param userId
	 * @return
	 */
	public t_participation queryTotalParticipationsByUserId(long activityId,long userId) {
		
		return participationDao.queryTotalParticipationsByUserId(activityId, userId);
	}
	
	/**
	 * 投票数加1
	 * @param participationId
	 * @return
	 */
	public int updateByIds(long participationId) {
		
		return participationDao.updateByIds(participationId);
	}
}
