package daos.ext.cps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.ext.cps.entity.t_cps_activity;
import models.ext.cps.entity.t_cps_award;
import models.ext.cps.entity.t_cps_award_record;

/**
 * CPS中奖记录具体实现dao
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年6月12日
 */
public class CpsAwardRecordDao extends BaseDao<t_cps_award_record> {

	protected CpsAwardRecordDao() {}
	
	/**
	 * 查找cps活动中奖记录列表
	 * @param type
	 * @return
	 */
	public PageBean<t_cps_award_record> queryAwardRecordByActivityId(int currPage,int pageSize, long cpsActivityId) {
		 
        StringBuffer querySQL = new StringBuffer("select * from t_cps_award_record v where v.cps_activity_id=:cpsActivityId ");
        StringBuffer countSQL = new StringBuffer("select count(v.id) from t_cps_award_record v where v.cps_activity_id=:cpsActivityId ");
        
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("cpsActivityId", cpsActivityId);
        
        return this.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), condition);
	}
	
	/**
	 * 查询cps活动中奖记录
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月23日
	 *
	 */
	public t_cps_award_record queryAwardByActivityId(long userId, long cpsActivityId){
		
		String sql = "SELECT * FROM t_cps_award_record c where c.cps_activity_id =:cpsActivityId and c.user_id =:userId";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpsActivityId", cpsActivityId);
		params.put("userId", userId);
		
		return this.findBySQL(sql, params);
	}
}
