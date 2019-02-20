package daos.common;

import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import models.common.entity.t_vote_activity;
import daos.base.BaseDao;

/**
 * 投票活动dao
 * @author Administrator
 *
 */
public class VoteActivityDao extends BaseDao<t_vote_activity>{

    public PageBean<t_vote_activity> queryBySearch(int currPage,int pageSize,String voteTitle){
        
        StringBuffer querySQL = new StringBuffer("select * from t_vote_activity v where 1=1 ");
        StringBuffer countSQL = new StringBuffer("select count(v.id) from t_vote_activity v where 1=1 ");
        
        Map<String, Object> condition = new HashMap<String, Object>();
        
        if(voteTitle!=null && !voteTitle.equals("")){
            querySQL.append("and v.title like :voteTitle ");
            countSQL.append("and v.title like :voteTitle ");
            condition.put("voteTitle", "%"+voteTitle+"%");
        }
        
        querySQL.append("order by v.create_time desc");
        return this.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), condition);
    }
    
    public int updateVoteIsUse(long voteId,boolean isUse){
        String sql="UPDATE t_vote_activity SET is_use=:isUse WHERE id=:id";
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("isUse", isUse);
        condition.put("id", voteId);
        
        return this.updateBySQL(sql, condition);
    }
    
    /**
	 * 查找上架的相应活动
	 * @param type
	 * @return
	 */
	public t_vote_activity queryVoteActivityByType(int type){
		String sql="SELECT * FROM t_vote_activity v WHERE v.is_use =:type";
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("type", type);
		
		return this.findBySQL(sql, condition);
	}
}
