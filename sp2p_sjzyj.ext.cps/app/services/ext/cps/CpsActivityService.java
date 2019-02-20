package services.ext.cps;

import java.util.Date;

import common.utils.Factory;
import common.utils.PageBean;
import daos.ext.cps.CpsActivityDao;
import models.ext.cps.entity.t_cps_activity;
import services.base.BaseService;

/**
 * CpsActivityService
 *
 * @description cps活动service实现
 *
 * @author liuyang
 * @createDate 2018年6月12日
 */
public class CpsActivityService extends BaseService<t_cps_activity> {

	protected static CpsActivityDao cpsActivityDao = null;
	
	protected CpsActivityService() {
		cpsActivityDao = Factory.getDao(CpsActivityDao.class);
		super.basedao = this.cpsActivityDao;
	}
	
	/**
	 * 查找所有的cps活动
	 * @param type
	 * @return
	 * @createDate 2018年6月12日
	 * @author liuyang
	 */
	public PageBean<t_cps_activity> queryCpsActivity(int currPage,int pageSize,String title) {
		
		return cpsActivityDao.queryCpsActivity(currPage, pageSize, title);
	}
	
	/**
	 * 
	 * @param cpsId
	 * @param isUse
	 * @return
	 * @createDate 2018年6月12日
	 * @author liuyang
	 */
	public boolean updateVoteIsUse(long cpsId,boolean isUse){
	    int rows = cpsActivityDao.updateCpsActivityIsUse(cpsId, isUse);
	    
	    if(rows>0){
	        return true;
	    }
	    return false;
	}
	
	/**
	 * 修改积分、时间
	 * @param type
	 * @return
	 */
	public void updateIntegral(long cpsId, int first_type, int register_type, double integral_ratio, int cutoff_time) {
		
		cpsActivityDao.updateIntegral(cpsId, first_type, register_type, integral_ratio, cutoff_time);
	}
	/**
	 * 查询正在进行的活动
	 * @author guoShiJie
	 * @createDate 2018.6.13
	 * */
	public t_cps_activity queryGoingActivity() {
		return cpsActivityDao.queryGoingActivity();
	}
	
	/**
	 * 通过id查询已上架的活动
	 * @author guoShiJie
	 * @createDate 2018.6.19
	 * */
	public t_cps_activity queryCpsActivity(long cpsId,int isUse) {
		return cpsActivityDao.queryCpsActivity(cpsId, isUse);
	}
	
	/**通过id查询正在进行中的活动
	 * @author guoShiJie
	 * @createDate 2018.6.19
	 * */
	public t_cps_activity queryGoingCpsActivityById (long cpsId,int isUse) {
		return cpsActivityDao.queryGoingCpsActivity(cpsId,isUse);
	}
	
	/**
	 * 更新t_cps_activity表
	 * @author guoShiJie
	 * @createDate 2018.6.20
	 * */
	public int updateCpsCutoffLimitTime (Date cutoffLimitTime,long cpsId,int isUse) {
		return cpsActivityDao.updateCpsCutoffLimitTime(cutoffLimitTime, cpsId, isUse);
	}
	
	/**
	 * 通过id查询上架活动
	 * @author guoShiJie
	 * @createDate 2018.6.19
	 * */
	public t_cps_activity queryCpsActivity(long cpsId) {
		return cpsActivityDao.queryCpsActivity(cpsId);
	}
	
	/**
	 * 查询活动是否开始
	 * @author liuyang
	 * @createDate 2018.6.23
	 * */
	public t_cps_activity queryActivity() {
		
		return cpsActivityDao.queryActivity();
	}
	
	/**
	 * 查询结束活动
	 * @author liuyang
	 * @createDate 2018.6.23
	 * */
	public t_cps_activity queryEndActivity() {
		
		return cpsActivityDao.queryEndActivity();
	}
	
	/**
	 * 查询活动都下架
	 * @author liuyang
	 * @createDate 2018.6.23
	 * */
	public t_cps_activity queryActivityIsUse() {
		
		return cpsActivityDao.queryActivityIsUse();
	}
}
