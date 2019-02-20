package services.ext.cps;

import java.util.List;

import common.utils.Factory;
import daos.ext.cps.CpsPacketDao;
import models.ext.cps.entity.t_cps_packet;
import services.base.BaseService;

/**
 * CpsPacketService
 *
 * @description cps红包设置service实现
 *
 * @author liuyang
 * @createDate 2018年6月12日
 */
public class CpsPacketService extends BaseService<t_cps_packet> {

	protected static CpsPacketDao cpsPacketDao = null;
	
	protected CpsPacketService() {
		cpsPacketDao = Factory.getDao(CpsPacketDao.class);
		super.basedao = this.cpsPacketDao;
	}
	
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
		
		return cpsPacketDao.queryPacketListByNew(cpsActivityId, type);
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
		
		return cpsPacketDao.queryPacketListByType(cpsActivityId, type);
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
		
		return cpsPacketDao.queryPacketByType(cpsActivityId, type);
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
		
		cpsPacketDao.deletePacketByType(cpsActivityId, type);
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
		return cpsPacketDao.queryCpsPacketByCpsIdAndType(cpsId, type, status);
	}
}
