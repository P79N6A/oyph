package services.ext.cps;

import java.util.List;

import common.utils.Factory;
import common.utils.PageBean;
import daos.ext.cps.CpsAwardRecordDao;
import models.ext.cps.entity.t_cps_award_record;
import services.base.BaseService;

/**
 * CpsAwardRecordServcie
 *
 * @description cps活动中奖记录service实现
 *
 * @author liuyang
 * @createDate 2018年6月12日
 */
public class CpsAwardRecordServcie extends BaseService<t_cps_award_record> {

	protected CpsAwardRecordDao cpsAwardRecordDao = null;
	
	protected CpsAwardRecordServcie() {
		this.cpsAwardRecordDao = Factory.getDao(CpsAwardRecordDao.class);
		super.basedao = this.cpsAwardRecordDao;
	}
	
	/**
	 * 查询指定活动的中奖记录列表
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年6月21日
	 *
	 */
	public PageBean<t_cps_award_record> queryAwardRecordByActivityId(int currPage,int pageSize, long cpsActivityId) {
		
		return cpsAwardRecordDao.queryAwardRecordByActivityId(currPage, pageSize, cpsActivityId);
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
		
		return cpsAwardRecordDao.queryAwardByActivityId(userId, cpsActivityId);
	}
}
