package services.ext.cps;

import java.util.List;

import common.utils.Factory;
import daos.ext.cps.CpsRateDao;
import models.ext.cps.entity.t_cps_rate;
import services.base.BaseService;

/**
 * CpsRateService
 *
 * @description cps加息卷设置service实现
 *
 * @author liuyang
 * @createDate 2018年6月12日
 */
public class CpsRateService extends BaseService<t_cps_rate> {

	protected static CpsRateDao cpsRateDao = null;
	
	protected CpsRateService() {
		cpsRateDao = Factory.getDao(CpsRateDao.class);
		super.basedao = this.cpsRateDao;
	}
	
	/**
	 * 查询指定活动的老用户奖励加息卷
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月13日
	 *
	 */
	public t_cps_rate queryRateByType(long cpsActivityId, int type) {
		
		return cpsRateDao.queryRateByType(cpsActivityId, type);
	}
	
	/**
	 * 根据类型和活动id删除相应加息卷
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月13日
	 *
	 */
	public void deleteRateByType(long cpsActivityId, int type) {
		
		cpsRateDao.deleteRateByType(cpsActivityId, type);
	}
	
	/**
	 * cps加息券活动查询
	 * @author guoShiJie
	 * @createDate 2018.6.13
	 * */
	public t_cps_rate queryCpsRateByCpsId(long cpsId) {
		return cpsRateDao.findByColumn("t_cps_id = ?", cpsId);
	}
	
	/**
	 * 	cps加息券查询
	 * 	@param cpsId cps活动id
	 * 	@param type 类型(0注册 1注册并实名 2首投)
	 * 	@param status 状态(0新客户 1老客户)
	 *	@author guoShiJie
	 *	@createDate 2018.6.13
	 * */
	public List<t_cps_rate> queryCpsRateByCondition (long cpsId,int type,int status) {
		return cpsRateDao.findListByColumn("t_cps_id = ? and type = ? and status = ? ", cpsId,type,status);
	}
}
