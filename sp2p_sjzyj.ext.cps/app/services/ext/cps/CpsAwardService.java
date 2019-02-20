package services.ext.cps;

import java.util.List;

import common.utils.Factory;
import daos.ext.cps.CpsAwardDao;
import models.ext.cps.entity.t_cps_award;
import services.base.BaseService;

/**
 * CpsAwardService
 *
 * @description cps奖励设置service实现
 *
 * @author liuyang
 * @createDate 2018年6月12日
 */
public class CpsAwardService extends BaseService<t_cps_award> {

	protected static CpsAwardDao cpsAwardDao = null;
	
	protected CpsAwardService() {
		cpsAwardDao = Factory.getDao(CpsAwardDao.class);
		super.basedao = this.cpsAwardDao;
	}
	
	/**
	 * 查询指定活动的奖项列表
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月20日
	 *
	 */
	public List<t_cps_award> queryAwardsByActivityId(long cpsActivityId) {
		
		return cpsAwardDao.queryAwardsByActivityId(cpsActivityId);
	}
}
