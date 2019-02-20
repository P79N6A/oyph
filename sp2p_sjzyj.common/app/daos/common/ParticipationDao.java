package daos.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.PageBean;

import models.common.entity.t_participation;
import org.apache.commons.lang.StringUtils;
import daos.base.BaseDao;

public class ParticipationDao extends BaseDao<t_participation> {

	protected ParticipationDao() {}
	
	public List<t_participation> queryUserOrder(long voteId){
	    
	    String sql="select * from t_participation p where p.activity_id=:voteId order by p.poll_num desc";
	    
	    Map<String, Object> args=new HashMap<String, Object>();
	    args.put("voteId", voteId);
	    
	    List<t_participation> participations=this.findListBySQL(sql, args);
	    
	    if(participations==null || participations.size()<=0){
	        participations=null;
	    }
	    
	    return participations;
	}
	
	/**
	 * 查询参加活动的人数
	 * @param activityId
	 * @return
	 */
	public int queryTotalParticipations(long activityId) {
		String sql="SELECT COUNT(p.id) FROM t_participation p WHERE p.activity_id =:activityId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("activityId", activityId);
		
		return this.findSingleIntBySQL(sql, 0, condition);
	}
	
	/**
	 * 投票排行榜
	 * @param activityId
	 * @return
	 */
	public List<t_participation> queryVoteRankList(long activityId) {
		Map<String, Object> condition = new HashMap<String, Object>();
		String sql = " select * from t_participation p where p.activity_id =:activityId and p.status=1 order by p.poll_num desc limit 10 ";
		condition.put("activityId", activityId);
		return this.findListBySQL(sql, condition);
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

		StringBuffer querySQL = new StringBuffer("SELECT * FROM t_participation h WHERE 1=1 AND h.status =1");
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(h.id) FROM t_participation h WHERE 1=1 AND h.status =1");
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		
		/** 按活动id搜索  */
		if(activityId!=0){
			querySQL.append(" AND h.activity_id =:activityId");
			countSQL.append(" AND h.activity_id =:activityId");
			conditionArgs.put("activityId", activityId);
		}
		
		/** 按名称搜索 */
		if((!searchTitle.equals("")) && StringUtils.isNotBlank(searchTitle) && (searchTitle!=null) && (!searchTitle.equals("输入名称"))){
			querySQL.append(" AND h.name LIKE :searchTitle");
			countSQL.append(" AND h.name LIKE :searchTitle");
			conditionArgs.put("searchTitle", "%"+searchTitle+"%");
		}
		
		/** 排序  */
		switch (orderType) {
			case 3:{
				querySQL.append(" ORDER BY h.poll_num ");
				break;
			}
			case 4:{
				querySQL.append(" ORDER BY h.poll_num DESC");
				break;
			}
			default:{
				querySQL.append(" ORDER BY h.time DESC");
				break;
			}
		}		
	
		return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), t_participation.class, conditionArgs);
	
	}
	
	/**
	 * 查询需要审核的参与表
	 * @param currPage
	 * @param pageSize
	 * @return
	 * @createDate 2017年5月22日
	 * @author lvweihuan
	 */
	public PageBean<t_participation> queryPartCheck(int currPage,int pageSize,long voteId){
	    
	    StringBuffer querySQl = new StringBuffer("select * from t_participation p where p.status<>1 ");
	    StringBuffer countSQL = new StringBuffer("select count(p.id) from t_participation p where p.status<>1 ");
	    
	    Map<String, Object> args = new HashMap<>();
	    
	    if(voteId>0){
	        querySQl.append("and p.activity_id=:voteId ");
	        countSQL.append("and p.activity_id=:voteId ");
	        args.put("voteId", voteId);
	    }
	    
	    querySQl.append("order by p.id ");
	    
        return pageOfBeanBySQL(currPage, pageSize, countSQL.toString(), querySQl.toString(), t_participation.class, args);
    }
	
	/**
	 * 根据条件查询实体对象
	 * @param activityId
	 * @param userId
	 * @return
	 */
	public t_participation queryTotalParticipationsByUserId(long activityId,long userId) {
		String sql="SELECT * FROM t_participation p WHERE p.activity_id =:activityId and p.user_id =:userId";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("activityId", activityId);
		condition.put("userId", userId);
		
		return this.findBySQL(sql, condition);
	}
	
	/**
	 * 投票数加1
	 * @param participationId
	 * @return
	 */
	public int updateByIds(long participationId) {

		StringBuffer countSQL = new StringBuffer("UPDATE t_participation h SET h.poll_num= h.poll_num+1 where h.id=:participationId ");
		
		Map<String, Object> conditionArgs = new HashMap<String, Object>();
		
		if(participationId != 0) {
			conditionArgs.put("participationId", participationId);
			
			return super.updateBySQL(countSQL.toString(), conditionArgs);
		} else {
			
			return -1;
		}
	}
}
