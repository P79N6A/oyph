package services.ext.cps;

import java.util.List;

import common.utils.Factory;
import daos.ext.cps.CpsIntegralDao;
import models.ext.cps.entity.t_cps_integral;
import services.base.BaseService;

/**
 * CpsIntegralService
 *
 * @description cps积分设置service实现
 *
 * @author liuyang
 * @createDate 2018年6月12日
 */
public class CpsIntegralService extends BaseService<t_cps_integral> {

	protected static CpsIntegralDao cpsIntegralDao = null;
	
	protected CpsIntegralService() {
		cpsIntegralDao = Factory.getDao(CpsIntegralDao.class);
		super.basedao = this.cpsIntegralDao;
	}
	
	/**
	 * 查询指定活动的老用户奖励积分
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月13日
	 *
	 */
	public t_cps_integral queryIntegralByType(long cpsActivityId, int type){
		
		return cpsIntegralDao.queryIntegralByType(cpsActivityId, type);
	}
	
	/**
	 * 根据类型和活动id删除相应积分
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月13日
	 *
	 */
	public void deleteIntegralByType(long cpsActivityId, int type) {
		
		cpsIntegralDao.deleteIntegralByType(cpsActivityId, type);
	}
	
	/**
	 * cps积分查询
	 * @author guoShiJie
	 * @param cpsId cps活动表id
	 * @createDate 2018.06.13
	 * */
	public t_cps_integral queryIntegralByCpsId (long cpsId)  {
		return cpsIntegralDao.findByColumn("t_cps_id = ? ", cpsId);
	}
	
	/**
	 * cps积分查询
	 * @author guoShiJie
	 * @param */
	public List<t_cps_integral> queryIntegralByConditions (long cpsId,int type ) {
		return cpsIntegralDao.findListByColumn("t_cps_id = ? and type = ?",cpsId,type);
	}
}
