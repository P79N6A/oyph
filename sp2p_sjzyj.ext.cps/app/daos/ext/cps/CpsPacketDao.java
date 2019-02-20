package daos.ext.cps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import daos.base.BaseDao;
import models.ext.cps.entity.t_cps_packet;

/**
 * CPS活动红包设置实现dao
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年6月12日
 */
public class CpsPacketDao extends BaseDao<t_cps_packet> {

	protected CpsPacketDao() {} 
	
	/**
	 * 查询指定活动的新用户注册时的红包奖励列表
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月13日
	 *
	 */
	public List<t_cps_packet> queryPacketListByNew(long cpsActivityId, int type) {
		
		String sql = "SELECT * FROM t_cps_packet c where c.t_cps_id =:cpsActivityId and c.type =:type and c.status = 0";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpsActivityId", cpsActivityId);
		params.put("type", type);
		
		return this.findListBySQL(sql, params);
	}
	
	/**
	 * 查询指定活动的老用户推广新用户注册并投资奖励红包
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年9月26日
	 *
	 */
	public List<t_cps_packet> queryPacketListByType(long cpsActivityId, int type) {
		
		String sql = "SELECT * FROM t_cps_packet c where c.t_cps_id =:cpsActivityId and c.type =:type and c.status = 1";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpsActivityId", cpsActivityId);
		params.put("type", type);
		
		return this.findListBySQL(sql, params);
	}
	
	/**
	 * 查询指定活动的老用户奖励红包
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月13日
	 *
	 */
	public t_cps_packet queryPacketByType(long cpsActivityId, int type) {
		
		String sql = "SELECT * FROM t_cps_packet c where c.t_cps_id =:cpsActivityId and c.type =:type and c.status = 1";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpsActivityId", cpsActivityId);
		params.put("type", type);
		
		return this.findBySQL(sql, params);
	}
	
	/**
	 * 根据类型和活动id删除相应红包
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月13日
	 *
	 */
	public void deletePacketByType(long cpsActivityId, int type) {
		
		String sql = "delete from t_cps_packet where t_cps_id =:cpsActivityId and type =:type and status = 1";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpsActivityId", cpsActivityId);
		params.put("type", type);
		
		this.deleteBySQL(sql, params);
		
	}
	
	/**
	 * 	cps红包查询
	 * 	@param cpsId cps活动id
	 * 	@param type 类型(0注册 1注册并实名 2首投)
	 * 	@param status 状态(0新客户 1老客户)
	 *	@author guoShiJie
	 *	@createDate 2018.6.13
	 * */
	public List<t_cps_packet> queryCpsPacketByCpsIdAndType (long cpsId,int type,int status) {
		return this.findListByColumn("t_cps_id = ? and type = ? and status = ? ", cpsId,type,status);
	}
}
