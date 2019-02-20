package daos.ext.cps;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import common.utils.PageBean;
import daos.base.BaseDao;
import models.ext.cps.entity.t_cps_activity;

/**
 * CPS活动具体实现dao
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年6月12日
 */
public class CpsActivityDao extends BaseDao<t_cps_activity> {

	protected CpsActivityDao() {}
	
	/**
	 * 查找所有的cps活动
	 * @param type
	 * @return
	 */
	public PageBean<t_cps_activity> queryCpsActivity(int currPage,int pageSize,String title) {
		 
        StringBuffer querySQL = new StringBuffer("select * from t_cps_activity v where 1=1 ");
        StringBuffer countSQL = new StringBuffer("select count(v.id) from t_cps_activity v where 1=1 ");
        
        Map<String, Object> condition = new HashMap<String, Object>();
        
        if(title!=null && !title.equals("")){
            querySQL.append("and v.title like :title ");
            countSQL.append("and v.title like :title ");
            condition.put("title", "%"+title+"%");
        }
        
        querySQL.append("order by v.time desc");
        return this.pageOfBySQL(currPage, pageSize, countSQL.toString(), querySQL.toString(), condition);
	}
	
	/**
	 * 上架下架修改
	 * @param type
	 * @return
	 */
	public int updateCpsActivityIsUse(long cpsId,boolean isUse){
        String sql="UPDATE t_cps_activity SET is_use=:isUse WHERE id=:id";
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("isUse", isUse);
        condition.put("id", cpsId);
        
        return this.updateBySQL(sql, condition);
    }
	
	/**
	 * 修改积分、时间
	 * @param type
	 * @return
	 */
	public int updateIntegral(long cpsId, int first_type, int register_type, double integral_ratio, int cutoff_time) {
		String sql="UPDATE t_cps_activity SET first_type=:first_type, register_type=:register_type,integral_ratio =:integral_ratio,cutoff_time=:cutoff_time WHERE id=:id";
        Map<String, Object> condition = new HashMap<String, Object>();
        
        condition.put("id", cpsId);
        condition.put("first_type", first_type);
        condition.put("register_type", register_type);
        condition.put("integral_ratio", integral_ratio);
        condition.put("cutoff_time", cutoff_time);
        
        return this.updateBySQL(sql, condition);
	}
	/**
	 * 查询正在进行的活动
	 * @author guoShiJie
	 * @createDate 2018.6.13
	 * */
	public t_cps_activity queryGoingActivity() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeStr = sdf.format(new Date());
		
		String sql = "select * from t_cps_activity where (  begin_time <= :nowDate1 and end_time >= :nowDate2 ) and is_use = :isUse ";
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("nowDate1", timeStr+".0");
		condition.put("nowDate2", timeStr+".0");
		condition.put("isUse", 1 );
		return this.findBySQL(sql, condition);
		
	}
	
	/**
	 * 通过id查询上架活动
	 * @author guoShiJie
	 * @createDate 2018.6.19
	 * */
	public t_cps_activity queryCpsActivity(long cpsId,int isUse) {
		String sql = "select * from t_cps_activity where id = :cpsId and is_use = :isUse";
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("cpsId", cpsId);
		condition.put("isUse", isUse);
		return this.findBySQL(sql, condition);
	}
	
	/**
	 * 通过id查询活动是否进行中
	 * @author guoShiJie
	 * @createDate 2018.6.19
	 * */
	public t_cps_activity queryGoingCpsActivity(long cpsId,int isUse) {
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeStr = sdf.format(today);
		String sql = "select * from t_cps_activity where :nowDate >= begin_time and :nowDate <= end_time and is_use = :isUse and id = :cpsId " ;
		Map<String,Object> condition  = new HashMap<String,Object>();
		condition.put("nowDate", timeStr+".0");
		condition.put("isUse", isUse );
		condition.put("cpsId", cpsId);
		return this.findBySQL(sql, condition);
	}
	
	/**
	 * 更新t_cps_activity表
	 * @author guoShiJie
	 * @createDate 2018.6.20
	 * */
	public int updateCpsCutoffLimitTime(Date cutoffLimitTime,long cpsId,int isUse) {
		String sql = "update t_cps_activity set cutoff_limit_time = :limit_time where id = :cpsId  and is_use = :isUse";
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("limit_time", cutoffLimitTime );
		condition.put("cpsId", cpsId);
		condition.put("isUse", isUse);
		return this.updateBySQL(sql, condition);
	}
	
	/**
	 * 通过id查询上架活动
	 * @author guoShiJie
	 * @createDate 2018.6.19
	 * */
	public t_cps_activity queryCpsActivity(long cpsId) {
		String sql = "select * from t_cps_activity where id = :cpsId ";
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("cpsId", cpsId);
		return this.findBySQL(sql, condition);
	}
	
	/**
	 * 查询活动是否开始
	 * @author liuyang
	 * @createDate 2018.6.23
	 * */
	public t_cps_activity queryActivity() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeStr = sdf.format(new Date());
		
		String sql = "select * from t_cps_activity where begin_time > :nowDate1 and is_use = :isUse ";
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("nowDate1", timeStr+".0");
		condition.put("isUse", 1 );
		return this.findBySQL(sql, condition);
		
	}
	
	/**
	 * 查询结束活动
	 * @author liuyang
	 * @createDate 2018.6.23
	 * */
	public t_cps_activity queryEndActivity() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeStr = sdf.format(new Date());
		
		String sql = "select * from t_cps_activity where end_time < :nowDate1 and is_use = :isUse ";
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("nowDate1", timeStr+".0");
		condition.put("isUse", 1 );
		return this.findBySQL(sql, condition);
		
	}
	
	/**
	 * 查询活动都下架
	 * @author liuyang
	 * @createDate 2018.6.23
	 * */
	public t_cps_activity queryActivityIsUse() {
		
		String sql = "select * from t_cps_activity where is_use = :isUse ";
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("isUse", 0 );
		return this.findBySQL(sql, condition);
		
	}
}
