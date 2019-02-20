package services.common;

import common.utils.Factory;
import common.utils.PageBean;
import daos.common.VoteActivityDao;
import models.common.entity.t_vote_activity;
import services.base.BaseService;

/**
 * 投票活动service
 * @author Administrator
 *
 */
public class VoteActivityService extends BaseService<t_vote_activity>{

	protected VoteActivityDao voteActivityDao = Factory.getDao(VoteActivityDao.class);
	
	protected VoteActivityService(){
		this.basedao=voteActivityDao;
	}
	
	/**
	 * 分页搜索投票活动
	 * @param currPage
	 * @param pageSize
	 * @param voteTitle
	 * @return
	 * @createDate 2017年5月18日
	 * @author lvweihuan
	 */
	public PageBean<t_vote_activity> queryBySearch(int currPage,int pageSize,String voteTitle){
	    return voteActivityDao.queryBySearch(currPage, pageSize, voteTitle);
	}
	
	/**
	 * 
	 * @param voteId
	 * @param isUse
	 * @return
	 * @createDate 2017年5月18日
	 * @author lvweihuan
	 */
	public boolean updateVoteIsUse(long voteId,boolean isUse){
	    int rows = voteActivityDao.updateVoteIsUse(voteId, isUse);
	    
	    if(rows>0){
	        return true;
	    }
	    return false;
	}
	
	/**
	 * 查找上架的相应活动
	 * @param type
	 * @return
	 */
	public t_vote_activity queryVoteActivityByType(int type){
		
		return voteActivityDao.queryVoteActivityByType(type);
	}
}
